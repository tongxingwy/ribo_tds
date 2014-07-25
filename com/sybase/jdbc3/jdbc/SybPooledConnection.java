/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import javax.sql.ConnectionEvent;
/*     */ import javax.sql.ConnectionEventListener;
/*     */ import javax.sql.PooledConnection;
/*     */ 
/*     */ public class SybPooledConnection extends SybConnection
/*     */   implements PooledConnection
/*     */ {
/*  63 */   private Vector _listeners = null;
/*     */ 
/*  72 */   private SybConnectionProxy _currentProxy = null;
/*     */ 
/*     */   protected SybPooledConnection(SybUrlProvider paramSybUrlProvider, int paramInt)
/*     */     throws SQLException
/*     */   {
/*  83 */     super(null, paramSybUrlProvider, null, paramInt);
/*     */ 
/*  92 */     this._listeners = new Vector(5);
/*     */   }
/*     */ 
/*     */   public Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 117 */     reAssignProxy(this);
/* 118 */     return this._currentProxy;
/*     */   }
/*     */ 
/*     */   public void addConnectionEventListener(ConnectionEventListener paramConnectionEventListener)
/*     */   {
/* 139 */     this._listeners.add(paramConnectionEventListener);
/*     */   }
/*     */ 
/*     */   public void removeConnectionEventListener(ConnectionEventListener paramConnectionEventListener)
/*     */   {
/* 161 */     this._listeners.remove(paramConnectionEventListener);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 175 */     if ((this._currentProxy != null) && (!this._currentProxy.wasClosed()))
/*     */     {
/* 177 */       this._currentProxy.close();
/*     */     }
/*     */ 
/* 182 */     this._listeners.clear();
/*     */ 
/* 185 */     super.close();
/*     */   }
/*     */ 
/*     */   protected void notifyListeners(SQLException paramSQLException)
/*     */   {
/*     */     ConnectionEvent localConnectionEvent;
/* 208 */     if (paramSQLException == null)
/*     */     {
/* 212 */       localConnectionEvent = new ConnectionEvent(this);
/*     */     }
/*     */     else
/*     */     {
/* 218 */       localConnectionEvent = new ConnectionEvent(this, paramSQLException);
/*     */     }
/*     */ 
/* 226 */     Enumeration localEnumeration = this._listeners.elements();
/* 227 */     while (localEnumeration.hasMoreElements())
/*     */     {
/* 229 */       ConnectionEventListener localConnectionEventListener = (ConnectionEventListener)localEnumeration.nextElement();
/* 230 */       if (paramSQLException != null)
/*     */       {
/* 232 */         localConnectionEventListener.connectionErrorOccurred(localConnectionEvent);
/*     */       }
/*     */ 
/* 236 */       localConnectionEventListener.connectionClosed(localConnectionEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void reAssignProxy(SybPooledConnection paramSybPooledConnection)
/*     */   {
/* 252 */     if ((this._currentProxy != null) && (!this._currentProxy.wasClosed()))
/*     */     {
/*     */       try
/*     */       {
/* 258 */         this._currentProxy.close();
/*     */       }
/*     */       catch (SQLException localSQLException)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 275 */     this._currentProxy = new SybConnectionProxy(paramSybPooledConnection);
/*     */   }
/*     */ 
/*     */   protected Connection getConnectionProxy()
/*     */   {
/* 282 */     return this._currentProxy;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybPooledConnection
 * JD-Core Version:    0.5.4
 */