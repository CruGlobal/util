package org.ccci.util.servlet;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * Delegates all calls to the delegate {@link ServletOutputStream} given to the constructor.
 * Useful base class for <tt>ServletOutputStream</tt> decorators.
 * 
 * @author Matt Drees
 *
 */
public class ForwardingServletOutputStream extends ServletOutputStream
{

    private final ServletOutputStream delegate;

    public ForwardingServletOutputStream( ServletOutputStream delegate)
    {
        this.delegate = delegate;
    }

    public void close() throws IOException
    {
        delegate.close();
    }

    public boolean equals(Object obj)
    {
        return delegate.equals(obj);
    }

    public void flush() throws IOException
    {
        delegate.flush();
    }

    public int hashCode()
    {
        return delegate.hashCode();
    }

    public void print(boolean b) throws IOException
    {
        delegate.print(b);
    }

    public void print(char c) throws IOException
    {
        delegate.print(c);
    }

    public void print(double d) throws IOException
    {
        delegate.print(d);
    }

    public void print(float f) throws IOException
    {
        delegate.print(f);
    }

    public void print(int i) throws IOException
    {
        delegate.print(i);
    }

    public void print(long l) throws IOException
    {
        delegate.print(l);
    }

    public void print(String s) throws IOException
    {
        delegate.print(s);
    }

    public void println() throws IOException
    {
        delegate.println();
    }

    public void println(boolean b) throws IOException
    {
        delegate.println(b);
    }

    public void println(char c) throws IOException
    {
        delegate.println(c);
    }

    public void println(double d) throws IOException
    {
        delegate.println(d);
    }

    public void println(float f) throws IOException
    {
        delegate.println(f);
    }

    public void println(int i) throws IOException
    {
        delegate.println(i);
    }

    public void println(long l) throws IOException
    {
        delegate.println(l);
    }

    public void println(String s) throws IOException
    {
        delegate.println(s);
    }

    public String toString()
    {
        return delegate.toString();
    }

    public void write(byte[] b, int off, int len) throws IOException
    {
        delegate.write(b, off, len);
    }

    public void write(byte[] b) throws IOException
    {
        delegate.write(b);
    }

    public void write(int b) throws IOException
    {
        delegate.write(b);
    }
    
    
    
}