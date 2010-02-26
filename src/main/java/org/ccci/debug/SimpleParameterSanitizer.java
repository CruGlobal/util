package org.ccci.debug;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Name("parameterSanitizer")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class SimpleParameterSanitizer implements ParameterSanitizer
{
    
    /**
     * A list of regular expressions that specify which request parameters (both query string & post body) 
     * are sensitive and should therefore be sanitized.
     */
    private List<String> sensitiveRequestParamPatterns;

    @Override
    public List<String> sanitizePostBodyParameter(String parameterName, List<String> parameterValues)
    {
        return sanitizeParameter(parameterName, parameterValues);
    }

    @Override
    public List<String> sanitizeQueryStringParameter(String parameterName, List<String> parameterValues)
    {
        return sanitizeParameter(parameterName, parameterValues);
    }

    @Override
    public List<String> sanitizeParameter(String parameterName, List<String> parameterValues)
    {
        boolean isSensitive = false;
        for (String sensitiveRequestParamPattern : sensitiveRequestParamPatterns)
        {
            if (parameterName.matches(sensitiveRequestParamPattern))
                isSensitive = true;
        }
        if (isSensitive)
        {
            List<String> sanitizedValues = Lists.newArrayList();
            for (int i = 0; i < parameterValues.size(); i++)
            {
                sanitizedValues.add("**********");
            }
            return sanitizedValues;
        }
        else
        {
            return parameterValues;
        }
    }
    

    public List<String> getSensitiveRequestParamPatterns()
    {
        return sensitiveRequestParamPatterns;
    }

    public void setSensitiveRequestParamPatterns(List<String> sensitiveRequestParams)
    {
        Preconditions.checkNotNull(sensitiveRequestParams, "sensitiveRequestParams is null");
        this.sensitiveRequestParamPatterns = sensitiveRequestParams;
    }


}
