package org.ccci.debug;

import static org.jboss.seam.ScopeType.STATELESS;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.hibernate.JDBCException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.ccci.util.seam.Components;

import com.google.common.collect.Lists;

@Name("additionalExceptionDetailsPrinter")
@Scope(STATELESS)
@BypassInterceptors
public class AdditionalExceptionDetailsPrinter
{
    
    public List<String> getAdditionalDetails(Throwable rootThrowable)
    {
        List<String> details = Lists.newArrayList();
        for (Throwable throwable : ExceptionUtil.eachThrowableInChain(rootThrowable)) {
            for (ExceptionDetailPrinter printer : getExceptionDetailPrinters())
            {
                if (printer.detailsAvailableForThrowable(throwable))
                {
                    details.addAll(printer.getDetails(throwable));
                }
            }
        }
        return details;
    }

    public List<ExceptionDetailPrinter> getExceptionDetailPrinters()
    {
        return Lists.newArrayList(
            new InvalidStateExceptionPrinter(),
            new SQLExceptionPrinter(),
            new BatchUpdateExceptionPrinter(),
            new JDBCExceptionPrinter(),
            new ConstraintViolationExceptionPrinter());
    }
    
    public interface ExceptionDetailPrinter
    {
        boolean detailsAvailableForThrowable(Throwable throwable);
        List<String> getDetails(Throwable throwable);
    }
    
    public static class InvalidStateExceptionPrinter implements ExceptionDetailPrinter
    {

        public boolean detailsAvailableForThrowable(Throwable throwable)
        {
            return throwable instanceof InvalidStateException;
        }

        public List<String> getDetails(Throwable throwable)
        {
            InvalidStateException ise = (InvalidStateException) throwable;
            List<String> details = Lists.newArrayList();
            details.add("InvalidStateException:  Invalid values follow:");
            int i = 1;
            for (InvalidValue invalidValue : ise.getInvalidValues()) {
                details.add("  " + i + ": "
                        + invalidValue.getBeanClass().getSimpleName() + "."
                        + invalidValue.getPropertyPath() + ": "
                        + invalidValue.getMessage());
                i++;
            }
            return details;
        }
        
    }
    
    public static class SQLExceptionPrinter implements ExceptionDetailPrinter
    {
        
        public boolean detailsAvailableForThrowable(Throwable throwable)
        {
            return throwable instanceof SQLException;
        }
        
        public List<String> getDetails(Throwable throwable)
        {
            SQLException sqle = (SQLException) throwable;
            List<String> details = Lists.newArrayList();
            details.add("SQLException:");
            //note: for a list of sql states: http://publib.boulder.ibm.com/infocenter/db2luw/v8/index.jsp?topic=/com.ibm.db2.udb.doc/core/r0sttmsg.htm
            details.add("  SQL State: " + sqle.getSQLState());
            details.add("  Error Code: " + sqle.getErrorCode());
            return details;
        }
        
    }
    
    public static class BatchUpdateExceptionPrinter implements ExceptionDetailPrinter
    {
        
        public boolean detailsAvailableForThrowable(Throwable throwable)
        {
            return throwable instanceof BatchUpdateException;
        }
        
        public List<String> getDetails(Throwable throwable)
        {
            BatchUpdateException jdbcException = (BatchUpdateException) throwable;
            List<String> details = Lists.newArrayList();
            details.add("BatchUpdateException:");
            details.add("  Update Counts: " + toFriendlyUpdateCountString(jdbcException.getUpdateCounts()));
            return details;
        }

        private String toFriendlyUpdateCountString(int[] updateCounts)
        {
            StringBuilder builder = new StringBuilder(updateCounts.length * 3);
            builder.append("[");
            boolean first = true;
            for (int updateCount : updateCounts)
            {
                if (!first) builder.append(", ");
                first = false;
                builder.append(translateUpdateCount(updateCount));
            }
            builder.append("]");
            return builder.toString();
        }

        private String translateUpdateCount(int updateCount)
        {
            switch (updateCount) 
            {
                case Statement.EXECUTE_FAILED: return "EXECUTE_FAILED";
                case Statement.SUCCESS_NO_INFO: return "SUCCESS_NO_INFO";
                default: return String.valueOf(updateCount); 
            }
        }
        
    }
    
    public static class JDBCExceptionPrinter implements ExceptionDetailPrinter
    {
        
        public boolean detailsAvailableForThrowable(Throwable throwable)
        {
            return throwable instanceof JDBCException;
        }
        
        public List<String> getDetails(Throwable throwable)
        {
            JDBCException jdbcException = (JDBCException) throwable;
            List<String> details = Lists.newArrayList();
            details.add("JDBCException:");
            details.add("  SQL: " + jdbcException.getSQL());
            return details;
        }
        
    }
    
    public static class ConstraintViolationExceptionPrinter implements ExceptionDetailPrinter
    {
        
        public boolean detailsAvailableForThrowable(Throwable throwable)
        {
            return throwable instanceof ConstraintViolationException;
        }
        
        public List<String> getDetails(Throwable throwable)
        {
            ConstraintViolationException jdbcException = (ConstraintViolationException) throwable;
            List<String> details = Lists.newArrayList();
            details.add("ConstraintViolationException:");
            details.add("  Constraint name: " + jdbcException.getConstraintName());
            return details;
        }
        
    }

    public static AdditionalExceptionDetailsPrinter instance()
    {
        return Components.getStatelessComponent(AdditionalExceptionDetailsPrinter.class);
    }
    
    
}
