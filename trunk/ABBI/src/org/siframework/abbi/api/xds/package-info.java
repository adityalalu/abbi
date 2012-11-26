/**
 * This package represents an implementation of an ABBI Back End supporting connection to an XDS 
 * Registry using the Open Health Tools IHE Profiles library to connect to the Registry and Repository
 * 
 * To configure this package, you must specify the location of the registry, repositories, and registry 
 * patient ID OID in the search.api.mapping.properties file found in the WEB-INF folder.
 * 
 * org.siframework.abbi.api.xds.XDSImpl.registry=<i>URL to XDS Registry End-point</i>
 * org.siframework.abbi.api.xds.XDSImpl.source=<i>OID for the registry patient identifier source</i>
 * 
 * Each repository using this registry must be identified (unless the registry implements the XCA
 * gateway protocol).  Configure the XDS ABBI back-end by specifying the repository ID and end-point
 * URLs in the following parameters.  The name component of the property is used to link the ID and URL
 * end-points for the same repository.
 * 
 * org.siframework.abbi.api.xds.XDSImpl.repository.id.<i>name</i>=<i>Repository ID (an OID)</i> 
 * org.siframework.abbi.api.xds.XDSImpl.repository.url.<i>name</i>=<i>Repository end-point URL</i>
 * 
 */
package org.siframework.abbi.api.xds;