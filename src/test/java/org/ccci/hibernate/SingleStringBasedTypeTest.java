package org.ccci.hibernate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ccci.util.ValueObject;
import org.hamcrest.core.IsInstanceOf;
import org.hibernate.HibernateException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SingleStringBasedTypeTest
{

    public static class Foo extends ValueObject implements Serializable
    {

        final String foo;
        
        public Foo(String foo)
        {
            this.foo = foo;
        }

        @Override
        protected Object[] getComponents()
        {
            return new Object[]{foo};
        }
        
        @Override
        public String toString()
        {
            return foo;
        }
        
        private static final long serialVersionUID = 1L;
    }
    
    public static class FooType extends SingleStringBasedType<Foo> {}
    
    
    FooType fooType;
    
    @Mock
    ResultSet resultSet;
    
    @Mock
    PreparedStatement preparedStatement;
    
    @BeforeMethod
    public void init()
    {
        MockitoAnnotations.initMocks(this);
        fooType = new FooType();
    }
    
    @Test
    public void testNullSafeGet() throws HibernateException, SQLException
    {
        String columnName = "foo_column";
        when(resultSet.getString("foo_column")).thenReturn("bar");
        Object result = fooType.nullSafeGet(resultSet, new String[]{columnName}, new Object());
        
        assertThat(result, IsInstanceOf.instanceOf(Foo.class));
        assertThat((Foo)result, equalTo(new Foo("bar")));
    }

    @Test
    public void testNullSafeSet() throws HibernateException, SQLException
    {
        Foo testFoo = new Foo("bar");
        
        fooType.nullSafeSet(preparedStatement, testFoo, 42);
        verify(preparedStatement).setString(42, "bar");
    }
    
}
