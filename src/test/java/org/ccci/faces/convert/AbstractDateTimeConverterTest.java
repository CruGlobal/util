package org.ccci.faces.convert;

import java.util.Locale;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;

import org.ccci.faces.convert.helper.EmptyValueExpression;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class AbstractDateTimeConverterTest
{

    protected MockFacesContext mockFacesContext;

    public AbstractDateTimeConverterTest()
    {
        super();
    }

    @BeforeMethod
    public void setUpFacesContext()
    {
        mockFacesContext = new MockFacesContext();
        mockFacesContext.setCurrent();
        mockFacesContext.setViewRoot(new UIViewRoot() {
            
            /**
             * to avoid having to mock a ViewHandler.  See {@link UIViewRoot#getLocale()}
             */ 
            @Override
            public Locale getLocale()
            {
                return null; 
            }
        });
    }

    @AfterMethod
    public void cleanup()
    {
        mockFacesContext.release();
    }
    

    protected UIComponent dummyComponent(final Class<?> boundType)
    {
        return new UIInput() {
            @Override
            public ValueExpression getValueExpression(String name)
            {
                return new EmptyValueExpression() {
                    private static final long serialVersionUID = 1L;
                    
                    @Override
                    public Class<?> getType(ELContext context)
                    {
                        return boundType;
                    }

                    @Override
                    public Class<?> getExpectedType()
                    {
                        return boundType;
                    }
                };
            }
        }; 
    }

    /**
     * Don't try to do as much as Seam's MockFacesContext does
     * @author Matt Drees
     *
     */
    public static class MockFacesContext extends org.jboss.seam.mock.MockFacesContext
    {

        public MockFacesContext()
        {
            super(null, null);
        }
        
        @Override
        public ELContext getELContext()
        {
            return null;
        }
    }
}
