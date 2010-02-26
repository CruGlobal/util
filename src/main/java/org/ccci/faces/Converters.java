package org.ccci.faces;

import org.ccci.faces.convert.ComponentlessValueObjectConverter;
import org.ccci.util.mail.EmailAddress;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

public class Converters
{
    
    @Name("emailAddressConverter")
    @BypassInterceptors
    @AutoCreate
    @Converter(forClass = EmailAddress.class)
    public static class ApplicationPasswordConverter extends ComponentlessValueObjectConverter<EmailAddress>{}

}
