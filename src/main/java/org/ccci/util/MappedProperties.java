package org.ccci.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.jexl2.JexlEngine;

import org.ccci.util.strings.Strings;

@SuppressWarnings("serial")
public class MappedProperties<T> extends Properties
{
	private T t;

	private JexlEngine jexlEngine = new JexlEngine();

	private Map<String,String> map;

	public MappedProperties(Map<String,String> map, T t)
	{
		this(map, t, true);
	}

	public MappedProperties(Map<String,String> map, T t, boolean lenient)
	{
		this(map, t, lenient, false);
	}

	public MappedProperties(Map<String,String> map, T t, boolean lenient, boolean silent)
	{
		this.map = map;

		this.t = t;

		setLenient(lenient);

		setSilent(silent);
	}

	@Override
	public String getProperty(String key)
	{
		return (String) jexlEngine.getProperty(t, map.get(key));
	}

	@Override
	public String setProperty(String key, String value)
	{
		jexlEngine.setProperty(t, map.get(key), value);

		return value;
	}

	@Override
	public String getProperty(String key, String defaultValue)
	{
		return Strings.isEmpty(getProperty(key)) ? defaultValue : getProperty(key);
	}

	@Override
	public void list(PrintStream out)
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public void list(PrintWriter out)
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public synchronized void load(InputStream inStream) throws IOException
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public synchronized void load(Reader reader) throws IOException
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public synchronized void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public Enumeration<?> propertyNames()
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public synchronized void save(OutputStream out, String comments)
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public void store(OutputStream out, String comments) throws IOException
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public void store(Writer writer, String comments) throws IOException
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public synchronized void storeToXML(OutputStream os, String comment, String encoding) throws IOException
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public synchronized void storeToXML(OutputStream os, String comment) throws IOException
	{
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public Set<String> stringPropertyNames()
	{
		throw new RuntimeException("Not implemented.");
	}

	public boolean isLenient()
	{
		return jexlEngine.isLenient();
	}

	public boolean isSilent()
	{
		return jexlEngine.isSilent();
	}

	public void setLenient(boolean flag)
	{
		jexlEngine.setLenient(flag);
	}

	public void setSilent(boolean flag)
	{
		jexlEngine.setSilent(flag);
	}
}
