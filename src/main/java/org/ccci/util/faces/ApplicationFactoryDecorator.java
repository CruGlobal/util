package org.ccci.util.faces;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;


public class ApplicationFactoryDecorator extends ApplicationFactory
{

    private final ApplicationFactory delegate;
    
    public ApplicationFactoryDecorator(ApplicationFactory delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public Application getApplication()
    {
        return new ApplicationDecorator(delegate.getApplication());
    }

    @Override
    public void setApplication(Application application)
    {
        delegate.setApplication(application);
    }

}
