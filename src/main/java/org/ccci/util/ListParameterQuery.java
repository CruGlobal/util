package org.ccci.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * JPA queries can't have list parameters.  So, you can't do something like:
 * "select apple from Apple apple where apple.color in (:colors)"
 * ...
 * query.setParameter("colors", Lists.newArrayList("red", "green"))
 * 
 * Hibernate supports this by default, but TopLink doesn't.
 * 
 * So, this is a helper object to allow this sort of functionality.
 * 
 * Usage:
 * new ListParameterQuery(entityManger,
 *   "select apple from Apple where apple.color in (:colors)")
 *   .setParameter(Lists.newArrayList("red", "green")
 *   .getResultList(); 
 * 
 * This results in a query like 
 * "select apple from Apple where apple.color in (:colors0, :colors1)"
 * being generated, 
 * and then each parameter will be set individually.
 * 
 * Requirements:
 * The the parameters intended to be used as lists must be enclosed in parentheses, with the keyword "in" before it,
 * like so: "... color in (:colors)".  
 * The parameter name should be alphanumeric (I think jpa allows slightly more charcters, but I didn't bother to match JPA's requirements).
 * 
 * Doesn't support indexed parameters e.g. "... color in (?)"
 * 
 * @author Matt Drees
 *
 */
public class ListParameterQuery implements Query
{

    //should match e.g. "in (:green)", and remember "green" as a group.
    //more restrictive than jpa parameter name rules (see class comment)
    final static Pattern listParameterPattern = Pattern.compile("in \\(:([a-zA-Z0-9]*)\\)");
    
    private final EntityManager entityManager;
    private final String rawQueryString; 
    private final Map<String, List<?>> listParameters = Maps.newHashMap();
    private final Set<String> listParamNames = Sets.newHashSet();
    
    private Integer startPosition;
    private FlushModeType flushMode;
    private final Map<String, Object> hints = Maps.newLinkedHashMap();
    private Integer maxResult;
    private final Map<String, Object> namedParameters = Maps.newLinkedHashMap();
    private final Map<Integer, Object> indexedParameters = Maps.newLinkedHashMap();
    private final Map<String, TemporalType> namedTemporalTypes = Maps.newHashMap();
    private final Map<Integer, TemporalType> indexedTemporalTypes = Maps.newHashMap();

    public ListParameterQuery(EntityManager entityManager, String queryString)
    {
        Preconditions.checkNotNull(entityManager);
        Preconditions.checkNotNull(queryString);
        this.entityManager = entityManager;
        this.rawQueryString = queryString;
        
        Matcher matcher = listParameterPattern.matcher(queryString);
        while (matcher.find())
        {
            String paramName = matcher.group(1);
            assert paramName != null;
            Preconditions.checkArgument(!listParamNames.contains(paramName), "duplicated parameter name: %s", paramName);
            listParamNames.add(paramName);
        }
    }

    public int executeUpdate()
    {
        return createQuery().executeUpdate();
    }

    public List<?> getResultList()
    {
        return createQuery().getResultList();
    }

    public Object getSingleResult()
    {
        return createQuery().getSingleResult();
    }

    private Query createQuery()
    {
        Preconditions.checkState(listParamNames.equals(listParameters.keySet()), "not all parameters have been set: %s",
            CollectionsExtension.difference(listParamNames, listParameters.keySet()));

        Query query = entityManager.createQuery(getTransformedQueryString());
        if (startPosition != null)
        {
            query = query.setFirstResult(startPosition);
        }
        if (flushMode != null)
        {
            query = query.setFlushMode(flushMode);
        }
        for (Map.Entry<String, Object> hint : hints.entrySet())
        {
            query = query.setHint(hint.getKey(), hint.getValue());
        }
        if (maxResult != null)
        {
            query = query.setMaxResults(maxResult);
        }
        for (Map.Entry<String, Object> namedParameter : namedParameters.entrySet())
        {
            String paramName = namedParameter.getKey();
            Object paramValue = namedParameter.getValue();
            if (namedTemporalTypes.containsKey(paramName))
            {
                if (paramValue instanceof Date)
                {
                    query = query.setParameter(paramName, (Date) paramValue, namedTemporalTypes.get(paramName));
                } else if (paramValue instanceof Calendar)
                {
                    query = query.setParameter(paramName, (Calendar) paramValue, namedTemporalTypes.get(paramName));
                } else {
                    throw new AssertionError();
                }
            } 
            else
            {
                query = query.setParameter(paramName, paramValue);
            }
        }
        for (Map.Entry<Integer, Object> indexedParameter : indexedParameters.entrySet())
        {
            int paramIndex = indexedParameter.getKey();
            Object paramValue = indexedParameter.getValue();

            if (indexedTemporalTypes.containsKey(paramIndex))
            {
                if (paramValue instanceof Date)
                {
                    query = query.setParameter(paramIndex, (Date) paramValue, indexedTemporalTypes.get(paramIndex));
                } else if (paramValue instanceof Calendar)
                {
                    query = query.setParameter(paramIndex, (Calendar) paramValue, indexedTemporalTypes.get(paramIndex));
                } else {
                    throw new AssertionError();
                }
            }
            else
            {
                query = query.setParameter(paramIndex, paramValue);
            }
        }
        for (String listParameterName : listParameters.keySet())
        {
            int i = 0;
            for (Object value : listParameters.get(listParameterName))
            {
                query = query.setParameter(generateParamName(listParameterName, i), value);
                i++;
            }
        }
        
        return query;
    }

    private String getTransformedQueryString()
    {
        StringBuffer transformedQueryStringBuffer = new StringBuffer();
        Matcher matcher = listParameterPattern.matcher(rawQueryString);
        while (matcher.find())
        {
            String paramName = matcher.group(1);
            assert paramName != null;
            List<String> transformedParamNames = Lists.newArrayList();
            for (int i = 0; i < listParameters.get(paramName).size(); i++)
            {
                transformedParamNames.add(":" + generateParamName(paramName, i));
            }
            matcher.appendReplacement(transformedQueryStringBuffer, "in (" + Joiner.on(", ").join(transformedParamNames) + ")");
        }
        matcher.appendTail(transformedQueryStringBuffer);
        return transformedQueryStringBuffer.toString();
    }

    private String generateParamName(String paramName, int i)
    {
        return paramName + i;
    }

    public ListParameterQuery setFirstResult(int startPosition)
    {
        this.startPosition = startPosition;
        return this;
    }

    public ListParameterQuery setFlushMode(FlushModeType flushMode)
    {
        this.flushMode = flushMode;
        return this;
    }

    public ListParameterQuery setHint(String hintName, Object value)
    {
        hints.put(hintName, value);
        return this;
    }

    public ListParameterQuery setMaxResults(int maxResult)
    {
        this.maxResult = maxResult;
        return this;
    }

    
    public ListParameterQuery setParameter(String name, Object value)
    {
        if (listParamNames.contains(name))
        {
            Preconditions.checkArgument(value instanceof List<?>,
                "parameter %s looks like a list parameter, but value is a %s", name, value.getClass());
            List<?> oldValue = listParameters.put(name, (List<?>) value);
            Preconditions.checkArgument(oldValue == null, "parameter %s was already set", name);
        }
        else
        {
            Object oldValue = namedParameters.put(name, value);
            Preconditions.checkArgument(oldValue == null, "parameter %s was already set", name);
        }
        return this;
    }
    
    public ListParameterQuery setParameter(int position, Object value)
    {
        Object oldValue = indexedParameters.put(position, value);
        Preconditions.checkArgument(oldValue == null, "position %s already has a parameter", position);
        return this;
    }

    
    public ListParameterQuery setParameter(String name, Date value, TemporalType temporalType)
    {
        setParameter(name, value);
        namedTemporalTypes.put(name, temporalType);
        return this;
    }

    public ListParameterQuery setParameter(String name, Calendar value, TemporalType temporalType)
    {
        setParameter(name, value);
        namedTemporalTypes.put(name, temporalType);
        return this;
    }

    public ListParameterQuery setParameter(int position, Date value, TemporalType temporalType)
    {
        setParameter(position, value);
        indexedTemporalTypes.put(position, temporalType);
        return this;
    }

    public ListParameterQuery setParameter(int position, Calendar value, TemporalType temporalType)
    {
        setParameter(position, value);
        indexedTemporalTypes.put(position, temporalType);
        return this;
    }

}
