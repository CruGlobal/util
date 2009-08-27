package org.ccci.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Assert;

import org.ccci.testutils.MockEntityManager;
import org.ccci.testutils.MockQuery;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class ListParameterQueryTest
{

    
    
    @Test
    public void testParameterQueryPattern()
    {
        Pattern listsParameterPattern = ListParameterQuery.listParameterPattern;
        Matcher matcher = listsParameterPattern.matcher("apple.color in (:colors) and");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("colors", matcher.group(1));
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void testParameterQuery()
    {
        String queryString = "select apple from Apple apple where apple.color in (:colors) and apple.weight > :weight";
        List<String> colors = Lists.newArrayList("red", "green");
        
        EntityManager entityManager = new MockEntityManager(){
            @Override
            public Query createQuery(String query)
            {
                String expected = "select apple from Apple apple where apple.color in (:colors0, :colors1) and apple.weight > :weight";
                Assert.assertEquals(expected , query);
                return new MockQuery()
                {
                    @Override
                    public Query setParameter(String name, Object value)
                    {
                        CollectionsExtension.assertContains(ImmutableSet.of("colors0", "colors1", "weight"), name);
                        return super.setParameter(name, value);
                    }
                    
                    @Override
                    public List<?> getResultList()
                    {
                        return Lists.newArrayList();
                    }
                };
            }
            
        };
        
        
        ListParameterQuery parameterQuery = new ListParameterQuery(entityManager , queryString);
        parameterQuery
            .setParameter("weight", 4)
            .setParameter("colors", colors);
        List<?> apples = parameterQuery.getResultList();
        Assert.assertNotNull(apples);
        Assert.assertTrue(apples.isEmpty());

    }
}
