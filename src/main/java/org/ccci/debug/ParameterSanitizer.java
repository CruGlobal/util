package org.ccci.debug;

import java.util.List;

public interface ParameterSanitizer
{

    /**
     * Sanitize a query string (aka 'GET') parameter
     * @param parameterName
     * @param parameterValues
     * @return
     */
    List<String> sanitizeQueryStringParameter(String parameterName, List<String> parameterValues);

    /**
     * Sanitize a post body parameter
     * @param parameterName
     * @param parameterValues
     * @return
     */
    List<String> sanitizePostBodyParameter(String parameterName, List<String> parameterValues);

    /**
     * For generic param sanitization; could be a query string param or post body param
     * @param parameterName
     * @param parameterValues
     * @return
     */
    List<String> sanitizeParameter(String parameterName, List<String> parameterValues);

}
