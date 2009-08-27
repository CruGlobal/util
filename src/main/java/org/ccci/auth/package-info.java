/**
 * it appears to get the ability to have MockAuthenticator have the same component name
 * as CasAuthenticator, it needs to be referenced in components.xml via the namespace mechanism,
 * not the standard one (or else I get "Two components with the same name and precedence" problems)
 */
@Namespace(value="urn:java:org.ccci.auth")
@AutoCreate
package org.ccci.auth;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Namespace;
