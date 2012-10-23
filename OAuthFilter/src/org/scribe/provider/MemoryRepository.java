package org.scribe.provider;

import org.scribe.provider.model.Application;
import org.scribe.provider.model.Repository;
import org.scribe.provider.model.Token;
import org.scribe.provider.model.User;
import org.scribe.provider.model.Token.TokenType;
import org.scribe.utils.Preconditions;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements a token repository suitable for testing, but not deployment,
 * as there is no backing store for tokens.  It can be used as the base class for an 
 * implementation that provides a backing store.
 * 
 * @author Keith W. Boone
 *
 */
public class MemoryRepository implements Repository, Runnable 
{
	/**
	 * A map allowing one to obtain a token by its key. Note, even a persistent
	 * repository could still use this map, as the the REQUEST_TOKEN and TEMP_TOKEN
	 * need not be persisted for more than a few minutes, and so need not be retained
	 * in a database.
	 */
	private HashMap<String, Token> tokens = new HashMap<String, Token>();
	
	/**
	 * This first set stores nonces that have been used in the current aging period.
	 * When nonces are saved, they are stored in this set.
	 */
	private HashSet<String> nonces = new HashSet<String>();
	/**
	 * This second set is only used to check for nonces, as these nonces were generated
	 * in the prior aging period.  When a new aging period is started, these nonces are
	 * thrown away.
	 */
	private HashSet<String> oldNonces = new HashSet<String>();
	
	/**
	 * This is a simple hash table for users, indexed by id.
	 */
	private HashMap<String, User> users = new HashMap<String, User>();
	/**
	 * This is a simple hash table for applications, indexed by id.
	 */
	private HashMap<String, Application> apps = new HashMap<String, Application>();
	
	/** 
	 * This thread ensures than the nonce HashSet is periodically cleaned out.
	 * Every aging period, it 
	 * 1) discards oldNonces.
	 * 2) moves nonces to oldNonces
	 * 3) creates a new set to track new nonces
	 */
	private Thread cleanupThread;
	
	/**
	 * The constructor creates and starts a cleanup thread that activates every MAXNONCEAGE period.
	 * @see cleanupThread
	 * @see run
	 */
	public MemoryRepository() {
		cleanupThread = new Thread(this);
		cleanupThread.setName("NonceCleanup");
		cleanupThread.setDaemon(true);
		cleanupThread.start();
	}

	/**
	 * To implement a backed store, override this method to retrieve tokens
	 * from the database, and if not found there, call this method via super.getToken().
	 */
	@Override
	public Token getToken(String key) {
		Preconditions.checkNotNull(key, "Key cannot be null");
		Token t = tokens.get(key);
		return t;
	}

	/**
	 * To implement a backed store, override this method to remove tokens from the
	 * database first, and if not found there, call this method via super.remove();
	 */
	@Override
	public void remove(Token token) {
		Preconditions.checkNotNull(token, "Token cannot be null");
		tokens.remove(token.getToken());
	}
	
	@Override
	/**
	 * This implementation of nonce management can be used by applications that are 
	 * not federated across more than one server.  It simply checks agains a set of nonces
	 * in memory that have been previously used.
	 */
	public boolean isNonceUsed(String nonce) 
	{
		Preconditions.checkNotNull(nonce, "Nonce cannot be null");
		// Check for nonces that are certainly less than five minutes in age in nonces
		// and for nonces that could be more than five minutes in age in oldNonces
		return nonces.contains(nonce) || oldNonces.contains(nonce);
	}

	@Override
	/**
	 * This implementation of nonce management can be used by applications that are 
	 * not federated across more than one server.  It simply stores the nonce in a 
	 * set.
	 */
	public void saveNonce(String nonce) 
	{
		Preconditions.checkNotNull(nonce, "Nonce cannot be null");
		nonces.add(nonce);
	}

	@Override
	/**
	 * This implements the cleanup Thread that discards any old nonces after 2 * MAXNONCEAGE seconds.
	 * It is thread safe, as are methods isNonceUsed() and saveNonce().
	 */
	public void run() {
		while (true)
		{	try {
				Thread.sleep(MAX_NONCE_AGE * 1000);
				// After waiting MAXNONCE Age Seconds
				// Discard any old Nonces (they are guaranteed to be at least MAXNONCEAGE old)
				// Save any new nonces (these are less than MAXNONCEAGE old)
				oldNonces = nonces;				
				// create a place to store any new nonces
				nonces = new HashSet<String>();
			} catch (InterruptedException e) 
			{	
				// Do nothing.
			}
		}
	}

	/**
	 * This interface is used to execute type specific token setup in createInternal
	 * @author Keith W. Boone
	 */
	private static interface SetupToken {
		/**
		 * Perform type specific token initialization
		 * @param t The token to initialize
		 */
		public void setup(Token t);
	}
	
	/**
	 * Create a new token of a specified type.
	 * @param type	The type of token to create.
	 * @param data	The data to associate with the token.
	 * @param tokenSetup	Token specific setup that needs to be performed before storage.
	 * @return	The newly created token.
	 */
	protected Token createInternal(TokenType type, String data, SetupToken tokenSetup)
	{
		String parts[] = null;
		Token t = null;
		synchronized (tokens)
		{	do 
			{	parts = Token.generateKeys(TOKEN_LENGTH, SECRET_LENGTH);
				// Ensures that there are no key collisions 
			} 	while (tokens.get(parts[0]) != null);
		
			t = new Token(type, parts[0], parts[1], data);
			tokenSetup.setup(t);
			store(t);
		}
		return t;
	}

	@Override
	public Token createClientToken(final Application app) {
		return createInternal(TokenType.CLIENT_KEY, null, 
				new SetupToken() {
					public void setup(Token t) {
						t.setApplication(app);
						t.setExpirationDate(new Date(System.currentTimeMillis() + 1000 * MAX_CLIENT_TOKEN_AGE));
					}
			
				}
		);
	}

	@Override
	public Token createRequestToken(final Application app, String callback) {
		return createInternal(TokenType.REQUEST_TOKEN, callback, 
				new SetupToken() {
					public void setup(Token t) {
						t.setApplication(app);
						t.setExpirationDate(new Date(System.currentTimeMillis() + 1000 * MAX_REQUEST_TOKEN_AGE));
					}
				}
		);
	}

	@Override
	public void createTemporaryToken(final Token requestToken, final User user, final Collection<String> roles) 
	{
		String verifier = String.format("%08d", (long)(Math.random() * 100000000l));
		requestToken.setVerifier(verifier);
		requestToken.setUser(user);
		requestToken.setExpirationDate(new Date(System.currentTimeMillis() + 1000 * MAX_TEMPORARY_TOKEN_AGE));
		Set<String> tRoles = requestToken.getRoles();
		tRoles.clear();
		tRoles.addAll(roles);
	}

	@Override
	public Token createAccessToken(final Application app, final User user, final Collection<String> roles) {
		return createInternal(TokenType.ACCESS_TOKEN, null, 
				new SetupToken() {
					public void setup(Token t) {
						t.setApplication(app);
						t.setUser(user);
						t.setExpirationDate(new Date(System.currentTimeMillis() + 1000 * MAX_ACCESS_TOKEN_AGE));
						Set<String> tRoles = t.getRoles();
						tRoles.clear();
						tRoles.addAll(roles);
					}
			
				}
		);
	}

	@Override
	public User getUser(String userId) {
		synchronized (users) {
			return users.get(userId);
		}
	}

	@Override
	public Application getApplication(String appId) {
		synchronized (apps) {
			return apps.get(appId);
		}
	}

	/**
	 * This class implements a simple user that only has a userId.
	 * @author keith W. Boone
	 */
	public class MemoryUser implements User {
		final String userId;
		Set<Application> apps = new HashSet<Application>();
		Set<Token> authorizations = new HashSet<Token>();
		
		MemoryUser(String userId)
		{
			this.userId = userId;
		}
		@Override
		public String getName() {
			return userId;
		}
		@Override
		public Set<Application> getOwnedApplications() {
			return apps;
		}
		@Override
		public Set<Token> getAuthorization() {
			return authorizations;
		}
		
		public Token getAuthorization(Application app)
		{
			for (Token t: authorizations)
				if (t.getApplication().getName().equals(app.getName()))
					return t;
			return null;
		}
	}
	
	/**
	 * This class implements a simple application that only has a appId.
	 * @author keith W. Boone
	 */
	public class MemoryApplication implements Application {
		final private Token clientToken;
		final private String appId;
		final private User owner;
		MemoryApplication(String appId, User owner)
		{
			this.appId = appId;
			this.owner = owner;
			clientToken = createClientToken(this);
			owner.getOwnedApplications().add(this);
		}
		@Override
		public String getName() {
			return appId;
		}
		@Override
		public Token getClientToken() {
			return clientToken;
		}
		@Override
		public User getOwner() {
			return owner;
		}
	}
	
	/**
	 * Create a new application named name, owned by owner
	 * @param appId	The id of the application to create.
	 * @param owner	The user who is the owner of this application
	 * @return The newly created application object, or null if an application with that
	 * id already exists.
	 */
	public Application createApplication(String appId, User owner) {
		Application a;
		synchronized (apps) {
			if (apps.get(appId) != null)
				return null;
			a = new MemoryApplication(appId, owner);
			store(a);
		}
		return a;
	}

	/**
	 * Create a new user with the specified name.
	 * @param userId The userId of the user to create.
	 * @return	The newly created user or null if a user with that id already exists.
	 */
	public User createUser(String userId) {
		User u;
		synchronized (users) {
			if (users.get(userId) != null)
				return null;
			u = new MemoryUser(userId);
			store(u);
		}
		return u;
	}
	
	/**
	 * Override this method in derived classes to store tokens.
	 * @param t	The token to store.
	 */
	protected void store(Token t)
	{
		tokens.put(t.getToken(), t);
	}
	
	/**
	 * Override this method in derived classes to store users.
	 * @param u	The user to store.
	 */
	protected void store(User u)
	{
		users.put(u.getName(), u);
	}
	
	/**
	 * Override this method in derived classes to store applications.
	 * @param a	The application to store.
	 */
	protected void store(Application a)
	{
		apps.put(a.getName(), a);
	}

}
