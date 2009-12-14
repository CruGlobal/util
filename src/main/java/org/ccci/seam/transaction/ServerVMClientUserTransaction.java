package org.ccci.seam.transaction;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;


/**
 * Heavily inspired ;-) by http://docs.huihoo.com/javadoc/jboss/4.0.2/org/jboss/tm/usertx/client/ServerVMClientUserTransaction.java.html
 * 
 * Jboss' 'web' profile doesn't include whatever it takes to lookup "java:comp/UserTransaction" in jndi.  So, this is
 * a shortcut to go straight to Jboss' transaction implementation.
 * 
 * Copy/pasted, since the old jboss transactions project isn't easily found in maven, near as I can tell, and I'd only use this one class...
 * 
 * @thief Matt Drees
 */
public class ServerVMClientUserTransaction implements UserTransaction
{   // Static --------------------------------------------------------

    /**
     *  Our singleton instance.
     */
    private final static ServerVMClientUserTransaction singleton = new ServerVMClientUserTransaction();


    /**
     *  The <code>TransactionManagerz</code> we delegate to.
     */
    private final TransactionManager tm;


    /**
     *  Return a reference to the singleton instance.
     */
    public static ServerVMClientUserTransaction getSingleton()
    {
       return singleton;
    }

    // Constructors --------------------------------------------------

    /**
     *  Create a new instance.
     */
    private ServerVMClientUserTransaction()
    {
       // Lookup the local TM
       TransactionManager local = null;
       try {
          local = (TransactionManager)new InitialContext().lookup("java:/TransactionManager");

       } catch (NamingException ex) 
       {
          //throw new RuntimeException("TransactionManager not found: " + ex);
       }
       tm = local;
    }
    //public constructor for TESTING ONLY
    public ServerVMClientUserTransaction(final TransactionManager tm)
    {
       this.tm = tm;
    }

    // Public --------------------------------------------------------


    //
    // implements interface UserTransaction
    //

    public void begin()
       throws NotSupportedException, SystemException
    {
       tm.begin();
       
    }

    public void commit()
       throws RollbackException,
              HeuristicMixedException,
              HeuristicRollbackException,
              SecurityException,
              IllegalStateException,
              SystemException
    {
       tm.commit();
    }

    public void rollback()
       throws SecurityException,
              IllegalStateException,
              SystemException
    {
       tm.rollback();
    }

    public void setRollbackOnly()
       throws IllegalStateException,
              SystemException
    {
       tm.setRollbackOnly();
    }

    public int getStatus()
       throws SystemException
    {
       return tm.getStatus();
    }

    public void setTransactionTimeout(int seconds)
       throws SystemException
    {
       tm.setTransactionTimeout(seconds);
    }


}
