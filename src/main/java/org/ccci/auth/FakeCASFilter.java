package org.ccci.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ccci.model.Designation;
import org.ccci.model.EmployeeId;
import org.ccci.model.SsoGuid;
import org.ccci.model.SsoUsername;
import org.ccci.util.strings.Strings;

import edu.yale.its.tp.cas.client.CASReceipt;
import edu.yale.its.tp.cas.client.filter.CASFilter;

/**
 * For use when real CAS isn't available, and one needs to pretend like it is.
 * 
 * @author Matt Drees
 */
public class FakeCASFilter implements Filter
{

    private SsoUsername username;
    private EmployeeId employeeId;
    private Designation designation;
    private SsoGuid ssoGuid;
    
    @Override
    public void destroy()
    {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession();
        CASReceipt reciept = (CASReceipt) session.getAttribute(CASFilter.CAS_FILTER_RECEIPT);
        if (reciept == null)
        {
            reciept = new CASReceipt();
            reciept.setUserName(Strings.safeToString(username));
            reciept.getAttributes().put(CasAuthenticator.DESIGNATION_ATTRIBUTE, Strings.safeToString(designation));
            reciept.getAttributes().put(CasAuthenticator.SSO_GUID_ATTRIBUTE, Strings.safeToString(ssoGuid));
            reciept.getAttributes().put(CasAuthenticator.EMPLID_ATTRIBUTE, Strings.safeToString(employeeId));
            session.setAttribute(CASFilter.CAS_FILTER_RECEIPT, reciept);
        }
        chain.doFilter(httpRequest, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        username = SsoUsername.nullableValueOf(filterConfig.getInitParameter("username"));
        employeeId = EmployeeId.nullableValueOf(filterConfig.getInitParameter("employeeId"));
        designation = Designation.nullableValueOf(filterConfig.getInitParameter("designation"));
        ssoGuid = SsoGuid.nullableValueOf(filterConfig.getInitParameter("ssoGuid"));
    }

}
