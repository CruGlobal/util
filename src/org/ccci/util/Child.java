package org.ccci.util;

public interface Child<P>
{
    void setParent(P parent);

    P getParent();
}
