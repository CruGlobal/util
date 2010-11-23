package org.ccci.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

public class MiniConnectionPoolManager
{
  private ConnectionPoolDataSource dataSource;
  private int maxConnections;
  private int timeout;
  private PrintWriter logWriter;
  private Semaphore semaphore;
  private Queue<PooledConnection> recycledConnections;
  private int activeConnections;
  private PoolConnectionEventListener poolConnectionEventListener;
  private boolean isDisposed;

  public MiniConnectionPoolManager(ConnectionPoolDataSource dataSource, int maxConnections)
  {
    this(dataSource, maxConnections, 60);
  }

  public MiniConnectionPoolManager(ConnectionPoolDataSource dataSource, int maxConnections, int timeout)
  {
    this.dataSource = dataSource;
    this.maxConnections = maxConnections;
    this.timeout = timeout;
    try
    {
      this.logWriter = dataSource.getLogWriter();
    }
    catch (SQLException localSQLException)
    {
    }
    if (maxConnections < 1) throw new IllegalArgumentException("Invalid maxConnections value.");
    this.semaphore = new Semaphore(maxConnections, true);
    this.recycledConnections = new LinkedList<PooledConnection>();
    this.poolConnectionEventListener = new PoolConnectionEventListener();
  }

  public synchronized void dispose()
    throws SQLException
  {
    if (this.isDisposed) return;
    this.isDisposed = true;
    SQLException e = null;
    while (!(this.recycledConnections.isEmpty()))
    {
      PooledConnection pconn = (PooledConnection)this.recycledConnections.remove();
      try
      {
        pconn.close();
      }
      catch (SQLException e2)
      {
        if (e == null) e = e2;
      }
    }
    if (e == null) return; throw e;
  }

  public Connection getConnection()
    throws SQLException
  {
    synchronized (this)
    {
      if (this.isDisposed) throw new IllegalStateException("Connection pool has been disposed.");
    }
    try
    {
      if (!(this.semaphore.tryAcquire(this.timeout, TimeUnit.SECONDS))) throw new TimeoutException();
    }
    catch (InterruptedException e)
    {
      throw new RuntimeException("Interrupted while waiting for a database connection.", e);
    }
    boolean ok = false;
    try
    {
      Connection conn = getConnection2();
      ok = true;
      return conn;
    }
    finally
    {
      if (!(ok)) this.semaphore.release();
    }
  }

  private synchronized Connection getConnection2() throws SQLException
  {
    if (this.isDisposed) throw new IllegalStateException("Connection pool has been disposed.");
    PooledConnection pconn;
    if (!(this.recycledConnections.isEmpty()))
    {
      pconn = (PooledConnection)this.recycledConnections.remove();
    }
    else
    {
      pconn = this.dataSource.getPooledConnection();
    }
    Connection conn = pconn.getConnection();
    this.activeConnections += 1;
    pconn.addConnectionEventListener(this.poolConnectionEventListener);
    assertInnerState();
    return conn;
  }

  private synchronized void recycleConnection(PooledConnection pconn)
  {
    if (this.isDisposed)
    {
      disposeConnection(pconn);
      return;
    }
    if (this.activeConnections <= 0) throw new AssertionError();
    this.activeConnections -= 1;
    this.semaphore.release();
    this.recycledConnections.add(pconn);
    assertInnerState();
  }

  private synchronized void disposeConnection(PooledConnection pconn)
  {
    if (this.activeConnections <= 0) throw new AssertionError();
    this.activeConnections -= 1;
    this.semaphore.release();
    closeConnectionNoEx(pconn);
    assertInnerState();
  }

  private void closeConnectionNoEx(PooledConnection pconn)
  {
    try
    {
      pconn.close();
    }
    catch (SQLException e)
    {
      log("Error while closing database connection: " + e.toString());
    }
  }

  private void log(String msg)
  {
    String s = "MiniConnectionPoolManager: " + msg;
    try
    {
      if (this.logWriter == null) {
        System.err.println(s); return;
      }
      this.logWriter.println(s);
    }
    catch (Exception localException)
    {
    }
  }

  private void assertInnerState()
  {
    if (this.activeConnections < 0) throw new AssertionError();
    if (this.activeConnections + this.recycledConnections.size() > this.maxConnections) throw new AssertionError();
    if (this.activeConnections + this.semaphore.availablePermits() <= this.maxConnections) return; throw new AssertionError();
  }

  public synchronized int getActiveConnections()
  {
    return this.activeConnections;
  }

  private class PoolConnectionEventListener
    implements ConnectionEventListener
  {
    public void connectionClosed(ConnectionEvent event)
    {
      PooledConnection pconn = (PooledConnection)event.getSource();
      pconn.removeConnectionEventListener(this);
      MiniConnectionPoolManager.this.recycleConnection(pconn);
    }

    public void connectionErrorOccurred(ConnectionEvent event)
    {
      PooledConnection pconn = (PooledConnection)event.getSource();
      pconn.removeConnectionEventListener(this);
      MiniConnectionPoolManager.this.disposeConnection(pconn);
    }
  }

  public static class TimeoutException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public TimeoutException()
    {
      super("Timeout while waiting for a free database connection.");
    }
  }
}