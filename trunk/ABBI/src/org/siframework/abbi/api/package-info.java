/**
 * This package contains classes used to define the API implemented by an ABBI Back-End.
 * Back-ends are pluggable, allowing the ABBI application to use different back ends to support different
 * data sources.
 * 
 * To configure the application to use a different back end, update the search.api.mapping.properties
 * file in the WEB-INF folder.  Change the property named org.siframework.abbi.api to reference the
 * name of the class implementing the API class.
 *
 * e.g.:
 * org.siframework.abbi.api=org.siframework.abbi.api.xds.XDSImpl
 * 
 * Additional properties used to configure your back-end implementation class can be included 
 * in this file. By convention, these properties should be start with the class name that they apply to
 * in order to avoid property name collisions.
 * 
 * e.g.,
 * org.siframework.abbi.api.xds.XDSImpl.registry=http://ihexds.nist.gov:12080/tf6/services/xdsregistryb
 * org.siframework.abbi.api.xds.XDSImpl.source=1.3.6.1.4.1.21367.2009.1.2.300
 * 
 * @see org.siframework.abbi.api.xds.XDSImpl
 */
package org.siframework.abbi.api;