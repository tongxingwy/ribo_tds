/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import com.sybase.jdbcx.Capture;
/*      */ import com.sybase.jdbcx.SybCallableStatement;
/*      */ import com.sybase.jdbcx.SybConnection;
/*      */ import com.sybase.jdbcx.SybEventHandler;
/*      */ import com.sybase.jdbcx.SybMessageHandler;
/*      */ import com.sybase.jdbcx.SybPreparedStatement;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Struct;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.WeakHashMap;
/*      */ import javax.sql.XAConnection;
/*      */ 
/*      */ public class SybConnectionProxy
/*      */   implements SybConnection
/*      */ {
/*      */   protected final SybPooledConnection _realConn;
/*      */   private final boolean _realConnIsXA;
/*   95 */   private WeakHashMap _openStatements = new WeakHashMap();
/*      */ 
/*  106 */   private boolean _wasClosed = false;
/*      */ 
/*      */   public SybConnectionProxy(SybPooledConnection paramSybPooledConnection)
/*      */   {
/*  117 */     this._realConn = paramSybPooledConnection;
/*      */ 
/*  119 */     this._realConnIsXA = this._realConn instanceof XAConnection;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  143 */     checkIfClosed();
/*      */ 
/*  145 */     this._wasClosed = true;
/*      */ 
/*  149 */     closeRememberedStatements();
/*      */ 
/*  151 */     this._realConn.notifyListeners(null);
/*      */   }
/*      */ 
/*      */   public void commit()
/*      */     throws SQLException
/*      */   {
/*  170 */     checkIfClosed();
/*      */ 
/*  172 */     if ((this._realConnIsXA) && 
/*  174 */       (!checkLocalTransaction("commit()")))
/*      */     {
/*  176 */       return;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  181 */       this._realConn.commit();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  187 */       testConnection(localSQLException);
/*  188 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rollback()
/*      */     throws SQLException
/*      */   {
/*  201 */     checkIfClosed();
/*      */ 
/*  203 */     if ((this._realConnIsXA) && 
/*  205 */       (!checkLocalTransaction("rollback()")))
/*      */     {
/*  207 */       return;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  212 */       this._realConn.rollback();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  218 */       testConnection(localSQLException);
/*  219 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rollback(Savepoint paramSavepoint)
/*      */     throws SQLException
/*      */   {
/*  227 */     checkIfClosed();
/*      */ 
/*  229 */     if ((this._realConnIsXA) && 
/*  231 */       (!checkLocalTransaction("rollback(Savepoint)")))
/*      */     {
/*  233 */       return;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  238 */       this._realConn.rollback(paramSavepoint);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  244 */       testConnection(localSQLException);
/*  245 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/*  256 */     checkIfClosed();
/*      */ 
/*  258 */     if ((this._realConnIsXA) && 
/*  260 */       (!checkLocalTransaction("setSavepoint()")))
/*      */     {
/*  262 */       return null;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  267 */       return this._realConn.setSavepoint();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  273 */       testConnection(localSQLException);
/*  274 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint(String paramString)
/*      */     throws SQLException
/*      */   {
/*  285 */     checkIfClosed();
/*      */ 
/*  287 */     if ((this._realConnIsXA) && 
/*  289 */       (!checkLocalTransaction("setSavepoint(String)")))
/*      */     {
/*  291 */       return null;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  296 */       return this._realConn.setSavepoint(paramString);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  302 */       testConnection(localSQLException);
/*  303 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void releaseSavepoint(Savepoint paramSavepoint)
/*      */     throws SQLException
/*      */   {
/*  312 */     checkIfClosed();
/*      */ 
/*  314 */     if ((this._realConnIsXA) && 
/*  316 */       (!checkLocalTransaction("releaseSavepoint(Savepoint)")))
/*      */     {
/*  318 */       return;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  323 */       this._realConn.releaseSavepoint(paramSavepoint);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  329 */       testConnection(localSQLException);
/*  330 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getHoldability()
/*      */     throws SQLException
/*      */   {
/*  341 */     checkIfClosed();
/*      */     try
/*      */     {
/*  345 */       return this._realConn.getHoldability();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  351 */       testConnection(localSQLException);
/*  352 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setHoldability(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  363 */     checkIfClosed();
/*      */     try
/*      */     {
/*  367 */       this._realConn.setHoldability(paramInt);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  373 */       testConnection(localSQLException);
/*  374 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAutoCommit(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  387 */     checkIfClosed();
/*      */ 
/*  389 */     if ((this._realConnIsXA) && 
/*  391 */       (paramBoolean) && 
/*  393 */       (this._realConnIsXA) && 
/*  395 */       (!checkLocalTransaction("setAutoCommit()")))
/*      */     {
/*  397 */       return;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  407 */       this._realConn.setAutoCommit(paramBoolean);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  413 */       testConnection(localSQLException);
/*  414 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  426 */     checkIfClosed();
/*      */     try
/*      */     {
/*  429 */       this._realConn.clearWarnings();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  435 */       testConnection(localSQLException);
/*  436 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Statement createStatement()
/*      */     throws SQLException
/*      */   {
/*  443 */     checkIfClosed();
/*  444 */     Statement localStatement = null;
/*      */     try
/*      */     {
/*  449 */       localStatement = this._realConn.createStatement();
/*  450 */       rememberStatement(localStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  456 */       testConnection(localSQLException);
/*  457 */       throw localSQLException;
/*      */     }
/*  459 */     return localStatement;
/*      */   }
/*      */ 
/*      */   public Statement createStatement(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  465 */     checkIfClosed();
/*  466 */     Statement localStatement = null;
/*      */     try
/*      */     {
/*  470 */       localStatement = this._realConn.createStatement(paramInt1, paramInt2);
/*  471 */       rememberStatement(localStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  477 */       testConnection(localSQLException);
/*  478 */       throw localSQLException;
/*      */     }
/*  480 */     return localStatement;
/*      */   }
/*      */ 
/*      */   public Statement createStatement(int paramInt1, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*  487 */     checkIfClosed();
/*  488 */     Statement localStatement = null;
/*      */     try
/*      */     {
/*  492 */       localStatement = this._realConn.createStatement(paramInt1, paramInt2, paramInt3);
/*      */ 
/*  494 */       rememberStatement(localStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  500 */       testConnection(localSQLException);
/*  501 */       throw localSQLException;
/*      */     }
/*  503 */     return localStatement;
/*      */   }
/*      */ 
/*      */   public boolean getAutoCommit()
/*      */     throws SQLException
/*      */   {
/*  510 */     checkIfClosed();
/*      */     try
/*      */     {
/*  514 */       return this._realConn.getAutoCommit();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  520 */       testConnection(localSQLException);
/*  521 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getCatalog()
/*      */     throws SQLException
/*      */   {
/*  528 */     checkIfClosed();
/*      */     try
/*      */     {
/*  531 */       return this._realConn.getCatalog();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  537 */       testConnection(localSQLException);
/*  538 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public DatabaseMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/*  545 */     checkIfClosed();
/*      */     try
/*      */     {
/*  548 */       return this._realConn.getMetaData();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  554 */       testConnection(localSQLException);
/*  555 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/*  562 */     checkIfClosed();
/*      */     try
/*      */     {
/*  565 */       return this._realConn.getTransactionIsolation();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  571 */       testConnection(localSQLException);
/*  572 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Map getTypeMap()
/*      */     throws SQLException
/*      */   {
/*  579 */     checkIfClosed();
/*      */     try
/*      */     {
/*  582 */       return this._realConn.getTypeMap();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  588 */       testConnection(localSQLException);
/*  589 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/*  596 */     checkIfClosed();
/*      */     try
/*      */     {
/*  599 */       return this._realConn.getWarnings();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  605 */       testConnection(localSQLException);
/*  606 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */     throws SQLException
/*      */   {
/*  613 */     if (this._wasClosed)
/*      */     {
/*  615 */       return true;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  620 */       return this._realConn.isClosed();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  626 */       testConnection(localSQLException);
/*  627 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/*  634 */     checkIfClosed();
/*      */     try
/*      */     {
/*  637 */       return this._realConn.isReadOnly();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  643 */       testConnection(localSQLException);
/*  644 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String nativeSQL(String paramString)
/*      */     throws SQLException
/*      */   {
/*  651 */     checkIfClosed();
/*      */     try
/*      */     {
/*  654 */       return this._realConn.nativeSQL(paramString);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  660 */       testConnection(localSQLException);
/*  661 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String paramString)
/*      */     throws SQLException
/*      */   {
/*  668 */     checkIfClosed();
/*  669 */     CallableStatement localCallableStatement = null;
/*      */     try
/*      */     {
/*  672 */       localCallableStatement = this._realConn.prepareCall(paramString);
/*  673 */       rememberStatement(localCallableStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  679 */       testConnection(localSQLException);
/*  680 */       throw localSQLException;
/*      */     }
/*  682 */     return localCallableStatement;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  688 */     checkIfClosed();
/*  689 */     CallableStatement localCallableStatement = null;
/*      */     try
/*      */     {
/*  692 */       localCallableStatement = this._realConn.prepareCall(paramString, paramInt1, paramInt2);
/*  693 */       rememberStatement(localCallableStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  699 */       testConnection(localSQLException);
/*  700 */       throw localSQLException;
/*      */     }
/*  702 */     return localCallableStatement;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*  709 */     checkIfClosed();
/*  710 */     CallableStatement localCallableStatement = null;
/*      */     try
/*      */     {
/*  713 */       localCallableStatement = this._realConn.prepareCall(paramString, paramInt1, paramInt2, paramInt3);
/*      */ 
/*  715 */       rememberStatement(localCallableStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  721 */       testConnection(localSQLException);
/*  722 */       throw localSQLException;
/*      */     }
/*  724 */     return localCallableStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString)
/*      */     throws SQLException
/*      */   {
/*  731 */     checkIfClosed();
/*  732 */     PreparedStatement localPreparedStatement = null;
/*      */     try
/*      */     {
/*  735 */       localPreparedStatement = this._realConn.prepareStatement(paramString);
/*  736 */       rememberStatement(localPreparedStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  742 */       testConnection(localSQLException);
/*  743 */       throw localSQLException;
/*      */     }
/*  745 */     return localPreparedStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  751 */     checkIfClosed();
/*  752 */     PreparedStatement localPreparedStatement = null;
/*      */     try
/*      */     {
/*  755 */       localPreparedStatement = this._realConn.prepareStatement(paramString, paramInt1, paramInt2);
/*  756 */       rememberStatement(localPreparedStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  762 */       testConnection(localSQLException);
/*  763 */       throw localSQLException;
/*      */     }
/*  765 */     return localPreparedStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  771 */     checkIfClosed();
/*  772 */     PreparedStatement localPreparedStatement = null;
/*      */     try
/*      */     {
/*  775 */       localPreparedStatement = this._realConn.prepareStatement(paramString, paramInt);
/*  776 */       rememberStatement(localPreparedStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  782 */       testConnection(localSQLException);
/*  783 */       throw localSQLException;
/*      */     }
/*  785 */     return localPreparedStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/*  791 */     checkIfClosed();
/*  792 */     PreparedStatement localPreparedStatement = null;
/*      */     try
/*      */     {
/*  795 */       localPreparedStatement = this._realConn.prepareStatement(paramString, paramArrayOfInt);
/*  796 */       rememberStatement(localPreparedStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  802 */       testConnection(localSQLException);
/*  803 */       throw localSQLException;
/*      */     }
/*  805 */     return localPreparedStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, String[] paramArrayOfString)
/*      */     throws SQLException
/*      */   {
/*  811 */     checkIfClosed();
/*  812 */     PreparedStatement localPreparedStatement = null;
/*      */     try
/*      */     {
/*  815 */       localPreparedStatement = this._realConn.prepareStatement(paramString, paramArrayOfString);
/*  816 */       rememberStatement(localPreparedStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  822 */       testConnection(localSQLException);
/*  823 */       throw localSQLException;
/*      */     }
/*  825 */     return localPreparedStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*  834 */     checkIfClosed();
/*  835 */     PreparedStatement localPreparedStatement = null;
/*      */     try
/*      */     {
/*  838 */       localPreparedStatement = this._realConn.prepareStatement(paramString, paramInt1, paramInt2, paramInt3);
/*      */ 
/*  840 */       rememberStatement(localPreparedStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  846 */       testConnection(localSQLException);
/*  847 */       throw localSQLException;
/*      */     }
/*  849 */     return localPreparedStatement;
/*      */   }
/*      */ 
/*      */   public void setCatalog(String paramString)
/*      */     throws SQLException
/*      */   {
/*  855 */     checkIfClosed();
/*      */     try
/*      */     {
/*  858 */       this._realConn.setCatalog(paramString);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  864 */       testConnection(localSQLException);
/*  865 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setReadOnly(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  872 */     checkIfClosed();
/*      */     try
/*      */     {
/*  875 */       this._realConn.setReadOnly(paramBoolean);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  881 */       testConnection(localSQLException);
/*  882 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTransactionIsolation(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  889 */     checkIfClosed();
/*      */     try
/*      */     {
/*  892 */       this._realConn.setTransactionIsolation(paramInt);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  898 */       testConnection(localSQLException);
/*  899 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTypeMap(Map paramMap)
/*      */     throws SQLException
/*      */   {
/*  906 */     checkIfClosed();
/*      */     try
/*      */     {
/*  909 */       this._realConn.setTypeMap(paramMap);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  915 */       testConnection(localSQLException);
/*  916 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void cancel()
/*      */     throws SQLException
/*      */   {
/*  929 */     checkIfClosed();
/*      */     try
/*      */     {
/*  932 */       this._realConn.cancel();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  938 */       testConnection(localSQLException);
/*  939 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Capture createCapture()
/*      */     throws SQLException
/*      */   {
/*  946 */     checkIfClosed();
/*      */     try
/*      */     {
/*  949 */       return this._realConn.createCapture();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  955 */       testConnection(localSQLException);
/*  956 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getSessionID()
/*      */     throws SQLException
/*      */   {
/*  963 */     checkIfClosed();
/*      */     try
/*      */     {
/*  966 */       return this._realConn.getSessionID();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  972 */       testConnection(localSQLException);
/*  973 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  981 */     checkIfClosed();
/*      */     PreparedStatement localPreparedStatement;
/*      */     try
/*      */     {
/*  986 */       localPreparedStatement = this._realConn.prepareStatement(paramString, paramBoolean);
/*  987 */       rememberStatement(localPreparedStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  993 */       testConnection(localSQLException);
/*  994 */       throw localSQLException;
/*      */     }
/*  996 */     return localPreparedStatement;
/*      */   }
/*      */ 
/*      */   public void regNoWatch(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1002 */     checkIfClosed();
/*      */     try
/*      */     {
/* 1005 */       this._realConn.regNoWatch(paramString);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1011 */       testConnection(localSQLException);
/* 1012 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void regWatch(String paramString, SybEventHandler paramSybEventHandler, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1020 */     checkIfClosed();
/*      */     try
/*      */     {
/* 1023 */       this._realConn.regWatch(paramString, paramSybEventHandler, paramInt);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1029 */       testConnection(localSQLException);
/* 1030 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setSybMessageHandler(SybMessageHandler paramSybMessageHandler)
/*      */   {
/* 1037 */     this._realConn.setSybMessageHandler(paramSybMessageHandler);
/*      */   }
/*      */ 
/*      */   public SybMessageHandler getSybMessageHandler()
/*      */   {
/* 1043 */     return this._realConn.getSybMessageHandler();
/*      */   }
/*      */ 
/*      */   protected boolean wasClosed()
/*      */   {
/* 1048 */     return this._wasClosed;
/*      */   }
/*      */ 
/*      */   protected void rememberStatement(Statement paramStatement)
/*      */   {
/* 1057 */     this._openStatements.put(paramStatement, null);
/*      */   }
/*      */ 
/*      */   protected void closeRememberedStatements()
/*      */   {
/* 1067 */     Iterator localIterator = this._openStatements.keySet().iterator();
/*      */ 
/* 1069 */     while (localIterator.hasNext())
/*      */     {
/* 1073 */       SybStatement localSybStatement = (SybStatement)localIterator.next();
/*      */       try
/*      */       {
/* 1083 */         if (!localSybStatement.isClosed())
/*      */         {
/* 1085 */           localSybStatement.close();
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1107 */     this._openStatements.clear();
/*      */   }
/*      */ 
/*      */   public SybPreparedStatement copyPreparedStatement(SybPreparedStatement paramSybPreparedStatement)
/*      */     throws SQLException
/*      */   {
/* 1119 */     checkIfClosed();
/*      */     SybPreparedStatement localSybPreparedStatement;
/*      */     try
/*      */     {
/* 1124 */       localSybPreparedStatement = this._realConn.copyPreparedStatement(paramSybPreparedStatement);
/* 1125 */       rememberStatement(localSybPreparedStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1131 */       testConnection(localSQLException);
/* 1132 */       throw localSQLException;
/*      */     }
/* 1134 */     return localSybPreparedStatement;
/*      */   }
/*      */ 
/*      */   public SybCallableStatement copyCallableStatement(SybCallableStatement paramSybCallableStatement)
/*      */     throws SQLException
/*      */   {
/* 1141 */     checkIfClosed();
/*      */     SybCallableStatement localSybCallableStatement;
/*      */     try
/*      */     {
/* 1146 */       localSybCallableStatement = this._realConn.copyCallableStatement(paramSybCallableStatement);
/* 1147 */       rememberStatement(localSybCallableStatement);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1153 */       testConnection(localSQLException);
/* 1154 */       throw localSQLException;
/*      */     }
/* 1156 */     return localSybCallableStatement;
/*      */   }
/*      */ 
/*      */   private void checkIfClosed()
/*      */     throws SQLException
/*      */   {
/* 1168 */     if (!this._wasClosed)
/*      */       return;
/* 1170 */     ErrorMessage.raiseError("JZ0C0");
/*      */   }
/*      */ 
/*      */   private void testConnection(SQLException paramSQLException)
/*      */   {
/* 1196 */     boolean bool = true;
/*      */     try
/*      */     {
/* 1203 */       bool = this._realConn.isClosed();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*      */     }
/*      */ 
/* 1215 */     if (!bool)
/*      */       return;
/* 1217 */     this._realConn.notifyListeners(paramSQLException);
/*      */   }
/*      */ 
/*      */   private boolean checkLocalTransaction(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1225 */     SybXAResource localSybXAResource = (SybXAResource)((SybXAConnection)this._realConn).getXAResource();
/*      */ 
/* 1227 */     if (!localSybXAResource.isLocalTransactionOK())
/*      */     {
/*      */       try
/*      */       {
/* 1231 */         ErrorMessage.raiseWarning((localSybXAResource instanceof SybXAResource11) ? "01S10" : "01S09", paramString);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 1239 */         this._realConn.chainWarnings(localSQLException);
/*      */       }
/* 1241 */       return false;
/*      */     }
/*      */ 
/* 1246 */     return true;
/*      */   }
/*      */ 
/*      */   public Array createArrayOf(String paramString, Object[] paramArrayOfObject)
/*      */     throws SQLException
/*      */   {
/* 1254 */     Debug.notImplemented(this, "public Array createArrayOf(String typeName, Object[] elements)");
/* 1255 */     return null;
/*      */   }
/*      */ 
/*      */   public Blob createBlob()
/*      */     throws SQLException
/*      */   {
/* 1261 */     return this._realConn.createBlob();
/*      */   }
/*      */ 
/*      */   public Clob createClob()
/*      */     throws SQLException
/*      */   {
/* 1267 */     return this._realConn.createClob();
/*      */   }
/*      */ 
/*      */   public Struct createStruct(String paramString, Object[] paramArrayOfObject)
/*      */     throws SQLException
/*      */   {
/* 1289 */     Debug.notImplemented(this, "public Struct createStruct(String typeName, Object[] attributes)");
/* 1290 */     return null;
/*      */   }
/*      */ 
/*      */   public Properties getClientInfo()
/*      */     throws SQLException
/*      */   {
/* 1296 */     return this._realConn.getClientInfo();
/*      */   }
/*      */ 
/*      */   public String getClientInfo(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1302 */     return this._realConn.getClientInfo(paramString);
/*      */   }
/*      */ 
/*      */   public boolean isValid(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1308 */     return this._realConn.isValid(paramInt);
/*      */   }
/*      */ 
/*      */   public boolean isWrapperFor(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 1330 */     return paramClass.isInstance(this);
/*      */   }
/*      */ 
/*      */   public Object unwrap(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 1338 */     SybConnectionProxy localSybConnectionProxy = null;
/*      */     try
/*      */     {
/* 1341 */       localSybConnectionProxy = this;
/*      */     }
/*      */     catch (ClassCastException localClassCastException)
/*      */     {
/* 1345 */       ErrorMessage.raiseError("JZ031", paramClass.getName());
/*      */     }
/*      */ 
/* 1348 */     return localSybConnectionProxy;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybConnectionProxy
 * JD-Core Version:    0.5.4
 */