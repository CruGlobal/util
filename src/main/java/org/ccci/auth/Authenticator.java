package org.ccci.auth;

import java.io.IOException;

public interface Authenticator
{
    boolean authenticate();
    
    void casLogout() throws IOException;
}
