package org.scribe.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.scribe.provider.Token.TokenType;
import org.scribe.provider.model.Token;
import org.scribe.utils.Preconditions;

public class DatabaseTokenRepositoryImpl extends MemoryRepository {

	private static class DBToken extends Token
	{
		long expires;
		
		public DBToken(TokenType type, String token, String secret, String data)
		{	super(type, token, secret, data);
		}
		
		private DBToken(Token oldToken, String token, String secret)
		{
			super(oldToken, token, secret);
		}
		private DBToken(Token oldToken, String token, String secret, String data)
		{
			super(oldToken, token, secret, data);
		}
		private DBToken(TokenType type, String token, String secret, int appId, int userId)
		{
			super(null, token, secret);
		}
		public void setExpires(long expires) {
			this.expires = expires;
		}
		
		public long getExpires() { return expires; }
	};
	
	private Connection con = null;
	private String dbName = "OAUTH";
	public static final String TOKEN_TABLE = "TOKENS";
	public static final String TOKEN_COLS[] = {
		"token CHAR(20) NOT NULL UNIQUE PRIMARY KEY",
		"secret CHAR(12) NOT NULL",
		"expires BIGINT NOT NULL",
		"type TINYINT NOT NULL",
		"appId INT NOT NULL",
		"userId INT NOT NULL DEFAULT 0",
		"data VARCHAR(256) NULL"
	};
	
	DatabaseTokenRepositoryImpl()
	{
	}
	
	public void setDatabase(String dbName)
	{
		this.dbName = dbName;
	}
	
	public void setConnection(Connection con)
	{
		this.con = con;
	}
	
	private void createTable(String tableName, String tableDef[]) throws SQLException
	{
		StringBuffer create = new StringBuffer();
		create.append("CREATE TABLE IF NOT EXISTS ").append(dbName).append(".").append(tableName).append(" ");
		for (String col: tableDef)
		{
			create.append(col).append(", ");
		}
		// strip the last , 
		create.setLength(create.length()-2);
		
		Statement st = con.createStatement();
		st.executeUpdate(create.toString());
	}
	
	public void checkTable() throws SQLException
	{
		createTable(TOKEN_TABLE, TOKEN_COLS);
	}
	
	@Override
	public void put(Token token) throws SQLException 
	{
		int retries = 3;
		
		DBToken t = (DBToken)token;
		
		String insert = "INSERT INTO " + dbName + "." + TOKEN_TABLE + 
		" (token, secret, expires, type, appId, userId, data)" +
		" VALUES (?, ?, ?, ?, ?, ?, ?)";

		while (--retries != 0)
		{
			PreparedStatement st = null;
			try {
				st = con.prepareStatement(insert);
				st.setString(1, t.getToken());
				st.setString(2, t.getSecret());
				st.setLong(3, t.getExpires());
				/* How do we get Application and user Id.
				st.setInt(4, t.getAppId());
				st.setInt(5, t.getUserId());
				*/
				st.setString(6, t.getRawResponse());
				st.execute();
			} 
			catch (SQLException e) 
			{	if (retries == 0)
					throw e;
			}
			finally
			{
				st.close();
			}
		}
		
	}
	
	public Token get(String key) 
	{
		Preconditions.checkNotNull(key, "Key cannot be null");
		String q = "SELECT * FROM "+dbName+"."+TOKEN_TABLE+" WHERE token = ? AND expires < ?";
		try {
			PreparedStatement st = con.prepareStatement(q);
			st.setString(1, key);
			st.setLong(2, System.currentTimeMillis()/1000);
			ResultSet rs = st.executeQuery();
			if (rs.next()) 
			{	String secret = rs.getString("secret");
				int type = rs.getInt("type");
				long expires = rs.getLong("expires");
				int appId = rs.getInt("appId");
				int userId = rs.getInt("userId");
				String data = rs.getString("data");
				Token.TokenType tk = Token.TokenType.values()[type];
				
				DBToken dbToken = new DBToken(tk, key, secret, data);
				dbToken.setExpires(expires);
				if (!rs.next())
					return dbToken;
				
				// Whoops, we matched two keys, which should really never happen.
				// Safest thing to do from a security perspective would be to act as 
				// if no match had occured?
			}
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}
	public long getExpiration(TokenType type)
	{
		long expires = System.currentTimeMillis() / 1000;
		switch (type) {
		case CLIENT_KEY:
			expires += TokenRepository.MAXCLIENTAGE;
			break;
		case ACCESS_TOKEN:
			expires += TokenRepository.MAXACCESSTOKENAGE;
			break;
		case REQUEST_TOKEN:
		case TEMP_TOKEN:
			expires += TokenRepository.MAXTEMPTOKENAGE;
			break;
		}
		return expires;
	}
	
	public Token createInternal(Token oldToken, String key, String secret, String data)
	{
		DBToken t = new DBToken(oldToken, key, secret, data);
		t.setExpires(getExpiration(t.getType()));
		return t;
	}
}
