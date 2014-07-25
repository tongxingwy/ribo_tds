/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.utils.CacheManager;
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import com.sybase.jdbc3.utils.LogUtil;
/*      */ import com.sybase.jdbc3.utils.SybInputPassword;
/*      */ import com.sybase.jdbcx.Capture;
/*      */ import com.sybase.jdbcx.EedInfo;
/*      */ import com.sybase.jdbcx.SybEventHandler;
/*      */ import com.sybase.jdbcx.SybMessageHandler;
/*      */ import java.io.IOException;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.DriverManager;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Struct;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Properties;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ public class SybConnection
/*      */   implements com.sybase.jdbcx.SybConnection
/*      */ {
/*   74 */   private static Logger LOG = Logger.getLogger(SybConnection.class.getName());
/*      */ 
/*   76 */   private static volatile long _logIdCounter = 0L;
/*   77 */   protected String _logId = null;
/*      */   static final int TRANSACTION_NONE = 0;
/*      */   static final int TRANSACTION_READ_UNCOMMITTED = 1;
/*      */   static final int TRANSACTION_READ_COMMITTED = 2;
/*      */   static final int TRANSACTION_REPEATABLE_READ = 4;
/*      */   static final int TRANSACTION_SERIALIZABLE = 8;
/*  109 */   private MdaManager _mda = null;
/*      */ 
/*  113 */   protected SybDatabaseMetaData _sybDBMD = null;
/*      */ 
/*  116 */   private SQLWarning _warning = null;
/*      */   protected Protocol _protocol;
/*      */   protected ProtocolContext _pc;
/*      */   private ProtocolContext _sharedPc;
/*      */   private CacheManager _sharedCm;
/*      */   protected SybProperty _props;
/*      */   protected boolean _dynamicPrepare;
/*      */   protected String _dbName;
/*      */   protected String _dbProductName;
/*      */   protected String _dbProductVersion;
/*  139 */   protected String _url = null;
/*      */   private int _loginTimeout;
/*      */   private long _loginStartTime;
/*  149 */   private byte _state = 0;
/*      */ 
/*  151 */   private int _dynStmtNum = 100;
/*      */   private Hashtable _cursors;
/*  157 */   private Vector _hostPortList = null;
/*      */ 
/*  159 */   private Vector _secondaryHostPortList = null;
/*      */ 
/*  162 */   private int _haLoginStatus = 0;
/*      */ 
/*  164 */   private int _haState = -1;
/*      */ 
/*  167 */   private boolean _wasConnected = false;
/*      */   private String _currentHostPort;
/*  173 */   private boolean _inClose = false;
/*      */ 
/*  175 */   private boolean _duringConnect = false;
/*      */ 
/*  186 */   boolean _batchSBPIInitialized = false;
/*      */ 
/*  189 */   private Vector _savepoints = null;
/*      */ 
/*  191 */   private int _savepointId = 1;
/*      */ 
/*  194 */   private int _defaultQueryTimeout = 0;
/*      */ 
/*  197 */   private int _internalQueryTimeout = 0;
/*      */ 
/*  201 */   protected Vector _stmtList = null;
/*      */   protected int _rsHoldability;
/*  207 */   private Properties _clientInfoProperties = new Properties();
/*      */ 
/*  213 */   protected Blob _nullBlob = null;
/*      */ 
/*  215 */   protected Clob _nullClob = null;
/*      */ 
/*  220 */   private int _tranIsolationLevel = -1;
/*      */ 
/*  223 */   private Boolean _readOnly = null;
/*      */ 
/*      */   public SybConnection(String paramString1, SybUrlProvider paramSybUrlProvider, String paramString2)
/*      */     throws SQLException
/*      */   {
/*  236 */     this(paramString1, paramSybUrlProvider, paramString2, DriverManager.getLoginTimeout());
/*      */   }
/*      */ 
/*      */   protected SybConnection(String paramString1, SybUrlProvider paramSybUrlProvider, String paramString2, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  267 */     this._logId = (paramString1 + "_Co" + _logIdCounter++);
/*      */ 
/*  270 */     this._url = paramString2;
/*  271 */     this._protocol = paramSybUrlProvider.getProtocol();
/*  272 */     this._props = paramSybUrlProvider.getSybProperty();
/*  273 */     this._hostPortList = paramSybUrlProvider.getHostPortList();
/*  274 */     this._dbName = paramSybUrlProvider.getDatabaseName();
/*  275 */     this._dynamicPrepare = isPropertySet(25);
/*  276 */     this._loginTimeout = paramInt;
/*  277 */     this._haState = -1;
/*  278 */     this._duringConnect = true;
/*  279 */     this._savepoints = new Vector();
/*  280 */     this._defaultQueryTimeout = this._props.getInteger(62);
/*      */ 
/*  282 */     this._internalQueryTimeout = this._props.getInteger(61);
/*      */ 
/*  284 */     this._stmtList = new Vector();
/*  285 */     this._rsHoldability = 1;
/*      */ 
/*  287 */     if (isPropertySet(100))
/*      */     {
/*  289 */       this._props.setProperty(31, "com.sybase.jdbc3.jdbc.SybSSLSocketFactory");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  298 */       if (isPropertySet(34))
/*      */       {
/*  300 */         String str = this._props.getString(45);
/*      */ 
/*  302 */         if (str != null)
/*      */         {
/*  304 */           this._secondaryHostPortList = new Vector();
/*  305 */           this._secondaryHostPortList.addElement(str);
/*      */         }
/*      */         else
/*      */         {
/*  315 */           this._secondaryHostPortList = paramSybUrlProvider.getSecondaryHostPortList();
/*      */         }
/*  317 */         setHALoginStatus(1);
/*  318 */         handleHAFailover();
/*  319 */         if ((((this._secondaryHostPortList == null) || (this._secondaryHostPortList.size() == 0))) && (this._haState != 8))
/*      */         {
/*  323 */           ErrorMessage.raiseError("JZ0F1");
/*      */         }
/*      */ 
/*      */       }
/*  327 */       else if (isPropertySet(72))
/*      */       {
/*  329 */         setHALoginStatus(0);
/*  330 */         handleHAFailover();
/*      */       }
/*      */       else
/*      */       {
/*  334 */         regularConnect();
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  344 */       throw localSQLException;
/*      */     }
/*      */     finally
/*      */     {
/*  348 */       this._duringConnect = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void regularConnect()
/*      */     throws SQLException
/*      */   {
/*  356 */     if ((this._dynamicPrepare) && 
/*  358 */       (isPropertySet(15)))
/*      */     {
/*  360 */       handleSQLE(ErrorMessage.createWarning("010PO"));
/*      */     }
/*      */ 
/*  364 */     tryLogin(this._hostPortList);
/*      */   }
/*      */ 
/*      */   private void tryLogin(Vector paramVector)
/*      */     throws SQLException
/*      */   {
/*  370 */     boolean bool1 = true;
/*      */ 
/*  373 */     int i = 1;
/*      */ 
/*  379 */     boolean bool2 = isPropertySet(26);
/*      */ 
/*  381 */     if (this._haState == 9)
/*      */     {
/*  383 */       i = 0;
/*      */     }
/*      */ 
/*  386 */     Enumeration localEnumeration = paramVector.elements();
/*      */     while (true) { if (!localEnumeration.hasMoreElements())
/*      */         break label438;
/*  389 */       this._loginStartTime = System.currentTimeMillis();
/*      */       try
/*      */       {
/*  392 */         String str1 = (String)localEnumeration.nextElement();
/*  393 */         this._currentHostPort = str1;
/*      */ 
/*  397 */         bool1 = this._props.getBoolean(76);
/*  398 */         if (bool1)
/*      */         {
/*  400 */           this._props.setProperty(76, new Boolean(false));
/*      */         }
/*      */ 
/*  404 */         this._protocol.login(str1, this._props, this, true);
/*  405 */         jsr 296;
/*      */       }
/*      */       catch (SQLWarning localSQLWarning1)
/*      */       {
/*  409 */         chainWarnings(localSQLWarning1);
/*      */       }
/*      */       catch (SQLException localSQLException1)
/*      */       {
/*  414 */         if (this._protocol.getRedirectImmed())
/*      */         {
/*  416 */           this._hostPortList = this._protocol.getRedirectionHostPort();
/*  417 */           localEnumeration = this._hostPortList.elements();
/*  418 */           this._protocol.setRedirectImmed(false);
/*  419 */           this._haLoginStatus &= -9;
/*      */         }
/*      */ 
/*  426 */         if ((bool2) && (localEnumeration.hasMoreElements()))
/*      */         {
/*  430 */           chainWarnings(localSQLException1);
/*      */           try
/*      */           {
/*  435 */             ErrorMessage.raiseWarning("010SQ");
/*      */           }
/*      */           catch (SQLWarning localSQLWarning2)
/*      */           {
/*  441 */             chainWarnings(localSQLWarning2);
/*      */           }
/*      */ 
/*  444 */           jsr 182; return;
/*      */         }
/*  446 */         if (((isPropertySet(34)) && (getHAState() == 10)) || ((isPropertySet(72)) && (getHAState() == 9)))
/*      */         {
/*  452 */           markDead(false);
/*  453 */           handleSQLE(localSQLException1);
/*      */         } else {
/*  455 */           if ((isPropertySet(34)) && ((("JZ006".equals(localSQLException1.getSQLState())) || ("JZ00L".equals(localSQLException1.getSQLState())))) && (getHAState() != 1) && (getHAState() != 6))
/*      */           {
/*  462 */             handleHAFailover();
/*      */ 
/*  468 */             i = 0;
/*  469 */             jsr 53; break label438:
/*      */           }
/*  471 */           if ("JZ0F2".equals(localSQLException1.getSQLState()))
/*      */           {
/*  483 */             handleSQLE(localSQLException1);
/*      */           }
/*      */           else
/*      */           {
/*  489 */             markDead(false);
/*  490 */             handleSQLE(localSQLException1);
/*      */           }
/*      */         }
/*  492 */         jsr 14;
/*      */       }
/*      */       finally {
/*  495 */         jsr 6;
/*      */       } } if (bool1)
/*      */     {
/*  498 */       this._props.setProperty(76, new Boolean(bool1)); } ret;
/*      */ 
/*  502 */     if (i == 0) {
/*      */       label438: return;
/*      */     }
/*  505 */     this._state = 1;
/*  506 */     this._pc = initProtocol();
/*      */ 
/*  517 */     long l = getLoginTimeRemaining();
/*  518 */     if (l < 0L)
/*      */     {
/*  520 */       ErrorMessage.raiseError("JZ00M");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  525 */       if ((this._warning != null) && 
/*  529 */         ("01ZZZ".equals(this._warning.getSQLState())) && (this._warning.getErrorCode() == 4022))
/*      */       {
/*      */         try
/*      */         {
/*  534 */           CallableStatement localCallableStatement = prepareCall("{call sp_password(?,?)}");
/*      */ 
/*  536 */           String str2 = null;
/*  537 */           String str3 = null;
/*  538 */           String str4 = null;
/*  539 */           str2 = this._props.getString(4);
/*  540 */           str3 = this._props.getString(69);
/*      */ 
/*  543 */           if (this._props.getBoolean(70))
/*      */           {
/*  546 */             SybInputPassword localSybInputPassword = new SybInputPassword(str2);
/*      */ 
/*  548 */             str2 = localSybInputPassword.getOldPassword();
/*  549 */             str3 = localSybInputPassword.getNewPassword();
/*  550 */             str4 = localSybInputPassword.getConfirmPassword();
/*      */           }
/*      */           else
/*      */           {
/*  554 */             str3 = this._props.getString(69);
/*      */           }
/*      */ 
/*  558 */           if (str3 != null)
/*      */           {
/*  560 */             localCallableStatement.setString(1, str2);
/*  561 */             localCallableStatement.setString(2, str3);
/*  562 */             localCallableStatement.executeUpdate();
/*  563 */             this._props.setProperty(4, str3);
/*      */ 
/*  565 */             this._props.setProperty(69, null);
/*      */ 
/*  567 */             this._warning = null;
/*      */           }
/*      */           else
/*      */           {
/*  571 */             ErrorMessage.raiseError("01ZZZ", this._warning.getErrorCode());
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (SQLException localSQLException2)
/*      */         {
/*  578 */           handleSQLE(localSQLException2);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  583 */       init();
/*      */     }
/*      */     catch (SQLWarning localSQLWarning3)
/*      */     {
/*  587 */       chainWarnings(localSQLWarning3);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void init()
/*      */     throws SQLException
/*      */   {
/*  598 */     Statement localStatement = null;
/*  599 */     SybProperty localSybProperty = this._props;
/*  600 */     String str = this._dbName;
/*  601 */     int i = 0;
/*      */     Object localObject2;
/*  603 */     if (localSybProperty.getBoolean(6))
/*      */     {
/*      */       try
/*      */       {
/*  608 */         checkMDA(this._pc);
/*      */ 
/*  613 */         determineCharset();
/*  614 */         i = 1;
/*      */ 
/*  616 */         this._protocol.setOption(null, 11, false);
/*      */ 
/*  619 */         if ((str != null) && (str.length() > 0))
/*      */         {
/*      */           try
/*      */           {
/*  625 */             setCatalog(str);
/*      */           }
/*      */           catch (SQLException localSQLException1)
/*      */           {
/*  633 */             checkForHAException(localSQLException1);
/*  634 */             handleSQLE(ErrorMessage.createWarning("010DF", localSQLException1.getMessage()));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*  643 */         checkForHAException(localException);
/*  644 */         checkUnsupportedCharsetException(localException);
/*  645 */         checkForLoginTimeout(localException);
/*      */ 
/*  662 */         if (i == 0)
/*      */         {
/*      */           try
/*      */           {
/*  666 */             determineCharset();
/*      */           }
/*      */           catch (SQLException localSQLException2)
/*      */           {
/*  670 */             checkUnsupportedCharsetException(localSQLException2);
/*  671 */             checkForHAException(localSQLException2);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/*  679 */           ErrorMessage.raiseWarning("010MX", localException.toString());
/*      */         }
/*      */         catch (SQLWarning localSQLWarning1)
/*      */         {
/*  685 */           localObject2 = null;
/*      */           try
/*      */           {
/*  688 */             if ((str != null) && (str.length() > 0))
/*      */             {
/*  690 */               localObject2 = prepareInternalStatement("use ?");
/*  691 */               ((PreparedStatement)localObject2).setString(1, str);
/*  692 */               ((PreparedStatement)localObject2).executeUpdate();
/*      */             }
/*      */           }
/*      */           catch (SQLException localSQLException5)
/*      */           {
/*  697 */             checkForHAException(localSQLException5);
/*  698 */             handleSQLE(ErrorMessage.createWarning("010UF", localSQLException5.getMessage()));
/*      */           }
/*      */           finally
/*      */           {
/*  703 */             if (localObject2 != null)
/*      */             {
/*      */               try
/*      */               {
/*  707 */                 ((PreparedStatement)localObject2).close();
/*  708 */                 localObject2 = null;
/*      */               }
/*      */               catch (SQLException localSQLException7)
/*      */               {
/*  712 */                 localObject2 = null;
/*  713 */                 handleSQLE(localSQLException7);
/*      */               }
/*      */               catch (RuntimeException localRuntimeException1)
/*      */               {
/*  717 */                 localObject2 = null;
/*  718 */                 throw localRuntimeException1;
/*      */               }
/*      */             }
/*  721 */             handleSQLE(localSQLWarning1);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  732 */       determineCharset();
/*  733 */       localObject1 = null;
/*      */       try
/*      */       {
/*  738 */         if ((str != null) && (str.length() > 0))
/*      */         {
/*  740 */           localObject1 = prepareInternalStatement("use ?");
/*  741 */           ((PreparedStatement)localObject1).setString(1, str);
/*  742 */           ((PreparedStatement)localObject1).executeUpdate();
/*      */         }
/*      */       }
/*      */       catch (SQLException localSQLException3)
/*      */       {
/*  747 */         checkForHAException(localSQLException3);
/*  748 */         handleSQLE(ErrorMessage.createWarning("010SK", localSQLException3.getMessage()));
/*      */       }
/*      */       finally
/*      */       {
/*  753 */         if (localObject1 != null)
/*      */         {
/*      */           try
/*      */           {
/*  757 */             ((PreparedStatement)localObject1).close();
/*  758 */             localObject1 = null;
/*      */           }
/*      */           catch (SQLException localSQLException8)
/*      */           {
/*  762 */             localObject1 = null;
/*  763 */             handleSQLE(localSQLException8);
/*      */           }
/*      */           catch (RuntimeException localRuntimeException2)
/*      */           {
/*  767 */             localObject1 = null;
/*  768 */             throw localRuntimeException2;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  778 */     Object localObject1 = localSybProperty.getString(18);
/*  779 */     if ((localObject1 != null) && (((String)localObject1).length() > 0))
/*      */     {
/*      */       try
/*      */       {
/*  783 */         localStatement = createInternalStatement();
/*  784 */         localStatement.executeUpdate((String)localObject1);
/*      */       }
/*      */       catch (SQLException localSQLException4)
/*      */       {
/*  788 */         handleSQLE(localSQLException4);
/*      */       }
/*      */       finally
/*      */       {
/*  792 */         if (localStatement != null)
/*      */         {
/*      */           try
/*      */           {
/*  796 */             localStatement.close();
/*  797 */             localStatement = null;
/*      */           }
/*      */           catch (SQLException localSQLException9)
/*      */           {
/*  801 */             localStatement = null;
/*  802 */             handleSQLE(localSQLException9);
/*      */           }
/*      */           catch (RuntimeException localRuntimeException3)
/*      */           {
/*  806 */             localStatement = null;
/*  807 */             throw localRuntimeException3;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  813 */     this._state = 0;
/*      */ 
/*  818 */     String[] arrayOfString = (String[])this._props.getObject(38);
/*      */ 
/*  820 */     if (arrayOfString != null)
/*      */     {
/*  824 */       localObject2 = this._protocol.getClassLoader();
/*      */ 
/*  826 */       if (localObject2 == null)
/*      */       {
/*  829 */         ErrorMessage.raiseError("JZ0CL");
/*      */       }
/*      */       else
/*      */       {
/*      */         try
/*      */         {
/*  836 */           ((DynamicClassLoader)localObject2).preloadJars(arrayOfString);
/*      */         }
/*      */         catch (SQLException localSQLException6)
/*      */         {
/*  841 */           chainWarnings(localSQLException6);
/*      */           try
/*      */           {
/*  845 */             ErrorMessage.raiseWarning("010PF");
/*      */           }
/*      */           catch (SQLWarning localSQLWarning2)
/*      */           {
/*  851 */             chainWarnings(localSQLWarning2);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  856 */     resetNullLobs();
/*      */   }
/*      */ 
/*      */   private boolean isSurrogateProcessingDisabled()
/*      */     throws SQLException
/*      */   {
/*  864 */     int i = 0;
/*  865 */     Statement localStatement = createInternalStatement();
/*  866 */     ResultSet localResultSet = localStatement.executeQuery("select value2 from master.dbo.syscurconfigs where config = (select config from master.dbo.sysconfigures where name='enable surrogate processing' and parent != 19 and config != 19)");
/*      */ 
/*  870 */     if (localResultSet.next())
/*      */     {
/*  872 */       i = (localResultSet.getInt(1) == 1) ? 1 : 0;
/*      */     }
/*  874 */     localResultSet.close();
/*  875 */     localStatement.close();
/*  876 */     localResultSet = null;
/*  877 */     localStatement = null;
/*  878 */     return i;
/*      */   }
/*      */ 
/*      */   private boolean isSurrogateProcessingDisabledOnServer()
/*      */     throws SQLException
/*      */   {
/*  895 */     if (!this._props.getBoolean(6))
/*      */     {
/*  902 */       return isSurrogateProcessingDisabled();
/*      */     }
/*      */ 
/*  906 */     int i = 0;
/*      */     try
/*      */     {
/*  910 */       PreparedStatement localPreparedStatement = this._mda.getMetaDataAccessor("SURROGATEPROCESS", this._pc);
/*      */ 
/*  912 */       SybResultSet localSybResultSet = (SybResultSet)localPreparedStatement.executeQuery();
/*      */ 
/*  916 */       if (localSybResultSet.next())
/*      */       {
/*  918 */         i = (localSybResultSet.getInt(1) == 0) ? 1 : 0;
/*      */       }
/*  920 */       localSybResultSet.close();
/*  921 */       localPreparedStatement.close();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  935 */       if (localSQLException.getSQLState().equals("JZ0SJ"))
/*      */       {
/*  937 */         return isSurrogateProcessingDisabled();
/*      */       }
/*      */ 
/*  943 */       throw localSQLException;
/*      */     }
/*      */ 
/*  946 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean okToThrowLoginTimeoutException()
/*      */   {
/*  960 */     switch (this._haState)
/*      */     {
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*  968 */       break;
/*      */     default:
/*  973 */       return false;
/*      */     }
/*  975 */     return this._duringConnect;
/*      */   }
/*      */ 
/*      */   public int getLoginTimeout()
/*      */   {
/*  983 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  985 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  987 */       LOG.fine(this._logId + " getLoginTimeout()");
/*      */     }
/*      */ 
/*  991 */     return this._loginTimeout;
/*      */   }
/*      */ 
/*      */   protected long getLoginTimeRemaining()
/*      */   {
/* 1003 */     int i = this._loginTimeout * 1000;
/*      */ 
/* 1009 */     if (i == 0)
/*      */     {
/* 1012 */       return 0L;
/*      */     }
/*      */ 
/* 1015 */     long l = 0L;
/*      */ 
/* 1020 */     switch (this._haState)
/*      */     {
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/* 1027 */       break;
/*      */     default:
/* 1032 */       return 0L;
/*      */     }
/*      */ 
/* 1040 */     l = i - (System.currentTimeMillis() - this._loginStartTime);
/*      */ 
/* 1042 */     if (l == 0L)
/*      */     {
/* 1044 */       l = -1L;
/*      */     }
/* 1046 */     return l;
/*      */   }
/*      */ 
/*      */   private void determineCharset() throws SQLException
/*      */   {
/* 1051 */     String str1 = this._props.getString(8);
/*      */ 
/* 1067 */     if (str1 == null)
/*      */     {
/* 1076 */       str1 = getDefaultServerCharset();
/*      */ 
/* 1093 */       if (str1 == null)
/*      */       {
/* 1103 */         str1 = "ascii_7";
/*      */         try
/*      */         {
/* 1107 */           ErrorMessage.raiseWarning("010TQ");
/*      */         }
/*      */         catch (SQLException localSQLException)
/*      */         {
/* 1112 */           handleSQLE(localSQLException);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1117 */       this._protocol.setOption(null, 6, str1);
/*      */     }
/*      */ 
/* 1120 */     if ((!str1.equalsIgnoreCase("utf8")) || (!this._protocol.isAse()) || (!isSurrogateProcessingDisabledOnServer())) {
/*      */       return;
/*      */     }
/* 1123 */     String str2 = this._props.getString(77);
/*      */ 
/* 1125 */     if (str2 != null) {
/*      */       return;
/*      */     }
/*      */ 
/* 1129 */     this._props.setProperty(77, "x-SybUTF8");
/*      */ 
/* 1132 */     this._protocol.setOption(null, 6, str1);
/*      */   }
/*      */ 
/*      */   public void checkForHAException(Exception paramException)
/*      */     throws SQLException
/*      */   {
/* 1144 */     if (!paramException instanceof SQLException)
/*      */       return;
/* 1146 */     SQLException localSQLException = (SQLException)paramException;
/*      */ 
/* 1149 */     if (!"JZ0F2".equals(localSQLException.getSQLState()))
/*      */       return;
/* 1151 */     throw localSQLException;
/*      */   }
/*      */ 
/*      */   public void checkForLoginTimeout(Exception paramException)
/*      */     throws SQLException
/*      */   {
/* 1162 */     int i = 0;
/* 1163 */     SQLException localSQLException1 = null;
/* 1164 */     if (!paramException instanceof SQLException)
/*      */       return;
/* 1166 */     localSQLException1 = (SQLException)paramException;
/* 1167 */     while (localSQLException1 != null)
/*      */     {
/* 1169 */       if ("JZ00M".equals(localSQLException1.getSQLState()))
/*      */       {
/* 1172 */         i = 1;
/*      */         try
/*      */         {
/* 1181 */           close();
/*      */         }
/*      */         catch (SQLException localSQLException2)
/*      */         {
/*      */         }
/*      */ 
/* 1187 */         break;
/*      */       }
/* 1189 */       localSQLException1 = localSQLException1.getNextException();
/*      */     }
/* 1191 */     if (i == 0) {
/*      */       return;
/*      */     }
/* 1194 */     throw localSQLException1;
/*      */   }
/*      */ 
/*      */   public void checkUnsupportedCharsetException(Exception paramException)
/*      */     throws SQLException
/*      */   {
/* 1212 */     int i = 0;
/* 1213 */     SQLException localSQLException1 = null;
/* 1214 */     if (!paramException instanceof SQLException)
/*      */       return;
/* 1216 */     localSQLException1 = (SQLException)paramException;
/* 1217 */     while (localSQLException1 != null)
/*      */     {
/* 1219 */       if ("JZ0IB".equals(localSQLException1.getSQLState()))
/*      */       {
/* 1222 */         i = 1;
/*      */         try
/*      */         {
/* 1225 */           close();
/*      */         }
/*      */         catch (SQLException localSQLException2)
/*      */         {
/*      */         }
/*      */ 
/* 1231 */         break;
/*      */       }
/* 1233 */       localSQLException1 = localSQLException1.getNextException();
/*      */     }
/* 1235 */     if (i == 0) {
/*      */       return;
/*      */     }
/* 1238 */     throw localSQLException1;
/*      */   }
/*      */ 
/*      */   public Statement createStatement()
/*      */     throws SQLException
/*      */   {
/* 1253 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1255 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1257 */       LOG.fine(this._logId + " createStatement()");
/*      */     }
/*      */ 
/* 1264 */     checkConnection();
/* 1265 */     SybStatement localSybStatement = new SybStatement(this._logId, initProtocol());
/* 1266 */     localSybStatement.setQueryTimeout(this._defaultQueryTimeout);
/* 1267 */     localSybStatement.setResultSetHoldability(this._rsHoldability);
/* 1268 */     return localSybStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1286 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1288 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1290 */         LOG.finer(this._logId + " prepareStatement(String = [" + paramString + "])");
/*      */       }
/* 1292 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1294 */         LOG.fine(this._logId + " prepareStatement(String)");
/*      */       }
/*      */     }
/*      */ 
/* 1298 */     return prepareStatement(paramString, 2);
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1304 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1306 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1308 */         LOG.finer(this._logId + " prepareStatement(String = [" + paramString + "] , int = [" + paramInt + "])");
/*      */       }
/* 1311 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1313 */         LOG.fine(this._logId + " prepareStatement(String, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1320 */       return prepareStatement(paramString, this._dynamicPrepare, paramInt);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1324 */       if (this._dynamicPrepare)
/*      */       {
/* 1327 */         return prepareStatement(paramString, false, paramInt);
/*      */       }
/*      */ 
/* 1330 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 1337 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1339 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1341 */         LOG.finest(LogUtil.logMethod(false, this._logId, " prepareStatement", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/* 1344 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1346 */         LOG.finer(LogUtil.logMethod(true, this._logId, " prepareStatement", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/* 1349 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1351 */         LOG.fine(this._logId + " prepareStatement(String, int[])");
/*      */       }
/*      */     }
/*      */ 
/* 1355 */     if ((paramArrayOfInt == null) || (paramArrayOfInt.length != 1))
/*      */     {
/* 1357 */       ErrorMessage.raiseError("JZ0GK", "columnIndexes");
/*      */     }
/*      */ 
/* 1360 */     return prepareStatement(paramString, 1);
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, String[] paramArrayOfString)
/*      */     throws SQLException
/*      */   {
/* 1366 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1368 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1370 */         LOG.finest(LogUtil.logMethod(false, this._logId, " prepareStatement", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/*      */ 
/* 1373 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1375 */         LOG.finer(LogUtil.logMethod(true, this._logId, " prepareStatement", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/* 1378 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1380 */         LOG.fine(this._logId + " prepareStatement(String, String[])");
/*      */       }
/*      */     }
/*      */ 
/* 1384 */     if ((paramArrayOfString == null) || (paramArrayOfString.length != 1))
/*      */     {
/* 1386 */       ErrorMessage.raiseError("JZ0GK", "columnNames");
/*      */     }
/*      */ 
/* 1389 */     return prepareStatement(paramString, 1);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public PreparedStatement prepareStatement(String paramString, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 1407 */     return prepareStatement(paramString, paramBoolean, 2);
/*      */   }
/*      */ 
/*      */   private PreparedStatement prepareStatement(String paramString, boolean paramBoolean, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1416 */     checkConnection();
/*      */     SybPreparedStatement localSybPreparedStatement;
/* 1418 */     if (paramBoolean)
/*      */     {
/* 1421 */       localSybPreparedStatement = new SybPreparedStatement(this._logId, initProtocol(), paramString, this._dynStmtNum++, paramInt);
/*      */     }
/*      */     else
/*      */     {
/* 1426 */       localSybPreparedStatement = new SybPreparedStatement(this._logId, initProtocol(), paramString, paramInt);
/*      */     }
/* 1428 */     localSybPreparedStatement.setQueryTimeout(this._defaultQueryTimeout);
/* 1429 */     localSybPreparedStatement.setResultSetHoldability(this._rsHoldability);
/* 1430 */     return localSybPreparedStatement;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1448 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1450 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1452 */         LOG.finer(this._logId + " prepareCall(String = [" + paramString + "] )");
/*      */       }
/* 1454 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1456 */         LOG.fine(this._logId + " prepareCall(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1461 */     checkConnection();
/* 1462 */     SybCallableStatement localSybCallableStatement = new SybCallableStatement(this._logId, initProtocol(), paramString);
/* 1463 */     localSybCallableStatement.setQueryTimeout(this._defaultQueryTimeout);
/* 1464 */     localSybCallableStatement.setResultSetHoldability(this._rsHoldability);
/* 1465 */     return localSybCallableStatement;
/*      */   }
/*      */ 
/*      */   public String nativeSQL(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1476 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1478 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1480 */         LOG.finer(this._logId + "nativeSQL(String = [" + paramString + "] )");
/*      */       }
/* 1482 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1484 */         LOG.fine(this._logId + " nativeSQL(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1489 */     checkConnection();
/* 1490 */     SybStatement localSybStatement = (SybStatement)createStatement();
/* 1491 */     return localSybStatement.processEscapes(paramString);
/*      */   }
/*      */ 
/*      */   public void setAutoCommit(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 1503 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1505 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1507 */         LOG.finer(this._logId + "setAutoCommit(boolean = [" + paramBoolean + "] )");
/*      */       }
/* 1510 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1512 */         LOG.fine(this._logId + " setAutoCommit(boolean)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1517 */     checkConnection();
/*      */     try
/*      */     {
/* 1520 */       this._protocol.setOption(null, 1, paramBoolean);
/*      */ 
/* 1524 */       if (isPropertySet(79))
/*      */       {
/* 1526 */         this._protocol.setOption(null, 13, !paramBoolean);
/*      */       }
/*      */ 
/* 1529 */       resetNullLobs();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1533 */       handleSQLE(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void resetNullLobs()
/*      */     throws SQLException
/*      */   {
/* 1548 */     if (!canUseLocators())
/*      */     {
/* 1550 */       this._nullClob = new SybCharClientLob(this._logId, this._pc, new StringBuffer(), 1);
/* 1551 */       this._nullBlob = new SybBinaryClientLob(this._logId, this._pc, new byte[0]);
/*      */     }
/*      */     else
/*      */     {
/* 1555 */       PreparedStatement localPreparedStatement = null;
/* 1556 */       SybResultSet localSybResultSet = null;
/*      */       try
/*      */       {
/* 1559 */         localPreparedStatement = this._mda.getMetaDataAccessor("INIT_NULL_LOBS", this._pc);
/* 1560 */         localSybResultSet = (SybResultSet)localPreparedStatement.executeQuery();
/*      */ 
/* 1562 */         if (localSybResultSet.next())
/*      */         {
/* 1564 */           this._nullBlob = localSybResultSet.getInitializedBlob(1);
/* 1565 */           this._nullClob = localSybResultSet.getInitializedClob(2);
/*      */         }
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 1571 */         if (localSybResultSet != null)
/*      */         {
/* 1573 */           localSybResultSet.close();
/* 1574 */           localSybResultSet = null;
/*      */         }
/* 1576 */         if (localPreparedStatement != null)
/*      */         {
/* 1578 */           localPreparedStatement.close();
/* 1579 */           localPreparedStatement = null;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getAutoCommit()
/*      */     throws SQLException
/*      */   {
/* 1597 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1599 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1601 */       LOG.fine(this._logId + " getAutoCommit()");
/*      */     }
/*      */ 
/* 1606 */     checkConnection();
/* 1607 */     boolean bool = false;
/*      */     try
/*      */     {
/* 1610 */       bool = this._protocol.getBoolOption(null, 1);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1614 */       handleSQLE(localSQLException);
/*      */     }
/*      */ 
/* 1618 */     return bool;
/*      */   }
/*      */ 
/*      */   public void commit()
/*      */     throws SQLException
/*      */   {
/* 1629 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1631 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1633 */       LOG.fine(this._logId + " commit()");
/*      */     }
/*      */ 
/* 1638 */     checkConnection();
/*      */     Enumeration localEnumeration;
/*      */     try
/*      */     {
/* 1642 */       this._protocol.endTransaction(true);
/* 1643 */       if (!this._stmtList.isEmpty())
/*      */       {
/* 1645 */         for (localEnumeration = this._stmtList.elements(); localEnumeration.hasMoreElements(); )
/*      */         {
/* 1647 */           ((SybStatement)localEnumeration.nextElement()).cancel();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1653 */       handleSQLE(localSQLException);
/*      */     }
/*      */ 
/* 1656 */     resetNullLobs();
/*      */   }
/*      */ 
/*      */   public void rollback()
/*      */     throws SQLException
/*      */   {
/* 1668 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1670 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1672 */       LOG.fine(this._logId + " rollback()");
/*      */     }
/*      */ 
/* 1677 */     checkConnection();
/*      */     try
/*      */     {
/* 1682 */       this._protocol.endTransaction(false);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1686 */       handleSQLE(localSQLException);
/*      */     }
/* 1688 */     resetNullLobs();
/*      */   }
/*      */ 
/*      */   public void rollback(Savepoint paramSavepoint)
/*      */     throws SQLException
/*      */   {
/* 1696 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1698 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1700 */         LOG.finer(LogUtil.logMethod(true, this._logId, " rollback", new Object[] { paramSavepoint }));
/*      */       }
/* 1703 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1705 */         LOG.fine(this._logId + " rollback(Savepoint)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1710 */     checkConnection();
/*      */     int i;
/* 1712 */     if ((i = getSavepointIdx(paramSavepoint)) == -1)
/*      */     {
/* 1714 */       ErrorMessage.raiseError("JZ017");
/*      */     }
/*      */ 
/* 1718 */     PreparedStatement localPreparedStatement = this._mda.getMetaDataAccessor("ROLL_TO_SAVEPOINT", " " + ((SybSavepoint)paramSavepoint)._name, this._pc);
/*      */ 
/* 1721 */     localPreparedStatement.executeUpdate();
/* 1722 */     localPreparedStatement.close();
/* 1723 */     this._savepoints.subList(i + 1, this._savepoints.size()).clear();
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/* 1731 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1733 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1735 */       LOG.fine(this._logId + " setSavepoint()");
/*      */     }
/*      */ 
/* 1742 */     SybSavepoint localSybSavepoint = (SybSavepoint)setSavepoint("jConn_autogen_svpt" + this._savepointId);
/*      */ 
/* 1744 */     localSybSavepoint._id = this._savepointId;
/*      */ 
/* 1746 */     localSybSavepoint._isNamed = false;
/* 1747 */     this._savepointId += 1;
/* 1748 */     return localSybSavepoint;
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1756 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1758 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1760 */         LOG.finer(this._logId + " setSavepoint(String = [" + paramString + "])");
/*      */       }
/* 1762 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1764 */         LOG.fine(this._logId + " setSavepoint(String)");
/*      */       }
/*      */     }
/*      */ 
/* 1768 */     checkConnection();
/* 1769 */     PreparedStatement localPreparedStatement = this._mda.getMetaDataAccessor("SAVEPOINT", " " + paramString, this._pc);
/*      */ 
/* 1771 */     localPreparedStatement.executeUpdate();
/* 1772 */     localPreparedStatement.close();
/* 1773 */     SybSavepoint localSybSavepoint = new SybSavepoint(this._logId, paramString);
/* 1774 */     localSybSavepoint._isNamed = true;
/*      */     int i;
/* 1778 */     if ((i = getSavepointIdx(localSybSavepoint)) != -1)
/*      */     {
/* 1780 */       this._savepoints.removeElementAt(i);
/*      */     }
/* 1782 */     this._savepoints.addElement(localSybSavepoint);
/* 1783 */     return localSybSavepoint;
/*      */   }
/*      */ 
/*      */   public void releaseSavepoint(Savepoint paramSavepoint)
/*      */     throws SQLException
/*      */   {
/* 1791 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1793 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1795 */         LOG.finer(LogUtil.logMethod(true, this._logId, " releaseSavepoint", new Object[] { paramSavepoint }));
/*      */       }
/* 1798 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1800 */         LOG.fine(this._logId + " releaseSavepoint(Savepoint)");
/*      */       }
/*      */     }
/*      */     int i;
/* 1805 */     if ((i = getSavepointIdx(paramSavepoint)) == -1)
/*      */     {
/* 1807 */       ErrorMessage.raiseError("JZ017");
/*      */     }
/* 1809 */     this._savepoints.removeElementAt(i);
/*      */   }
/*      */ 
/*      */   private int getSavepointIdx(Savepoint paramSavepoint) throws SQLException
/*      */   {
/* 1814 */     int i = -1;
/* 1815 */     for (int j = 0; j < this._savepoints.size(); ++j)
/*      */     {
/* 1817 */       SybSavepoint localSybSavepoint = (SybSavepoint)this._savepoints.get(j);
/* 1818 */       if (!localSybSavepoint._name.equals(((SybSavepoint)paramSavepoint)._name))
/*      */         continue;
/* 1820 */       i = j;
/*      */ 
/* 1822 */       break;
/*      */     }
/*      */ 
/* 1825 */     return i;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/* 1835 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1837 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1839 */       LOG.fine(this._logId + " close()");
/*      */     }
/*      */ 
/* 1850 */     if (this._state == 2)
/*      */     {
/* 1852 */       return;
/*      */     }
/*      */ 
/* 1858 */     this._inClose = true;
/*      */     Enumeration localEnumeration;
/* 1860 */     if (this._cursors != null)
/*      */     {
/* 1862 */       localEnumeration = this._cursors.keys();
/*      */     }
/*      */     while (true)
/*      */       try
/*      */       {
/* 1867 */         String str = (String)localEnumeration.nextElement();
/* 1868 */         this._cursors.remove(str);
/*      */       }
/*      */       catch (NoSuchElementException localNoSuchElementException)
/*      */       {
/* 1875 */         markDead(false);
/*      */         try
/*      */         {
/* 1878 */           this._protocol.logout();
/*      */         }
/*      */         catch (SQLException localSQLException)
/*      */         {
/* 1882 */           handleSQLE(localSQLException);
/*      */         }
/*      */         finally
/*      */         {
/* 1888 */           this._pc.drop();
/* 1889 */           if (this._sharedPc != null)
/*      */           {
/* 1891 */             this._sharedPc.drop();
/*      */           }
/*      */ 
/* 1894 */           this._inClose = false;
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */     throws SQLException
/*      */   {
/* 1908 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1910 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1912 */       LOG.fine(this._logId + " isClosed()");
/*      */     }
/*      */ 
/* 1921 */     String str = this._props.getString(36);
/*      */ 
/* 1928 */     if ((this._state != 2) && (!"INTERNAL".equals(str)))
/*      */     {
/* 1931 */       CallableStatement localCallableStatement = null;
/* 1932 */       Statement localStatement = null;
/*      */       try
/*      */       {
/* 1936 */         if (str == null)
/*      */         {
/* 1938 */           localCallableStatement = prepareInternalCall("{call sp_mda(?,?)}");
/* 1939 */           localCallableStatement.setInt(1, 0);
/* 1940 */           localCallableStatement.setInt(2, 9);
/* 1941 */           localCallableStatement.executeQuery();
/*      */         }
/*      */         else
/*      */         {
/* 1948 */           localStatement = createInternalStatement();
/* 1949 */           localStatement.executeQuery(str);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException localSQLException1)
/*      */       {
/* 1966 */         if ("JZ0F2".equals(localSQLException1.getSQLState()))
/*      */         {
/* 1975 */           throw localSQLException1;
/*      */         }
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*      */         try
/*      */         {
/* 1987 */           if (localCallableStatement != null)
/*      */           {
/* 1989 */             localCallableStatement.close();
/* 1990 */             localCallableStatement = null;
/*      */           }
/* 1992 */           if (localStatement != null)
/*      */           {
/* 1994 */             localStatement.close();
/* 1995 */             localStatement = null;
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (SQLException localSQLException2)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2008 */     return this._state == 2;
/*      */   }
/*      */ 
/*      */   public DatabaseMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 2023 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2025 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2027 */       LOG.fine(this._logId + " getMetaData()");
/*      */     }
/*      */ 
/* 2032 */     checkConnection();
/* 2033 */     checkDBMD();
/* 2034 */     return this._sybDBMD;
/*      */   }
/*      */ 
/*      */   protected String getDatabaseProductName()
/*      */     throws SQLException
/*      */   {
/* 2042 */     return this._dbProductName;
/*      */   }
/*      */ 
/*      */   protected synchronized void setDatabaseProductName(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2048 */     this._dbProductName = paramString;
/*      */   }
/*      */ 
/*      */   protected String getDatabaseProductVersion()
/*      */     throws SQLException
/*      */   {
/* 2056 */     return this._dbProductVersion;
/*      */   }
/*      */ 
/*      */   protected synchronized void setDatabaseProductVersion(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2062 */     this._dbProductVersion = paramString;
/*      */   }
/*      */ 
/*      */   public void setReadOnly(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 2074 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2076 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2078 */         LOG.finer(this._logId + " setReadOnly(boolean = [" + paramBoolean + "])");
/*      */       }
/* 2081 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2083 */         LOG.fine(this._logId + " setReadOnly(boolean)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2088 */     checkConnection();
/*      */     try
/*      */     {
/* 2091 */       this._protocol.setOption(null, 3, paramBoolean);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2095 */       handleSQLE(localSQLException);
/*      */     }
/* 2097 */     this._readOnly = Boolean.valueOf(paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/* 2111 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2113 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2115 */       LOG.fine(this._logId + " isReadOnly()");
/*      */     }
/*      */ 
/* 2119 */     if (this._readOnly == null)
/*      */     {
/* 2122 */       checkConnection();
/*      */       try
/*      */       {
/* 2125 */         boolean bool = this._protocol.getBoolOption(null, 3);
/* 2126 */         this._readOnly = Boolean.valueOf(bool);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 2130 */         handleSQLE(localSQLException);
/*      */       }
/*      */     }
/* 2133 */     return this._readOnly.booleanValue();
/*      */   }
/*      */ 
/*      */   public void setCatalog(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2141 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2143 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2145 */         LOG.finer(this._logId + " setCatalog(String = [" + paramString + "])");
/*      */       }
/* 2147 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2149 */         LOG.fine(this._logId + " setCatalog(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2154 */     checkConnection();
/*      */     try
/*      */     {
/* 2157 */       this._protocol.setOption(null, 9, paramString);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2161 */       handleSQLE(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getCatalog()
/*      */     throws SQLException
/*      */   {
/* 2175 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2177 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2179 */       LOG.fine(this._logId + " getCatalog()");
/*      */     }
/*      */ 
/* 2184 */     checkConnection();
/*      */ 
/* 2187 */     String str = null;
/*      */     try
/*      */     {
/* 2190 */       str = this._protocol.getStringOption(null, 9, null);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2195 */       handleSQLE(localSQLException);
/*      */     }
/*      */ 
/* 2198 */     return str;
/*      */   }
/*      */ 
/*      */   public void setTransactionIsolation(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2212 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2214 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2216 */         LOG.finer(this._logId + " setTransactionIsolation(int = [" + paramInt + "])");
/*      */       }
/* 2219 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2221 */         LOG.fine(this._logId + " setTransactionIsolation(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2226 */     checkConnection();
/*      */     try
/*      */     {
/* 2230 */       this._protocol.setOption(null, 2, paramInt);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2235 */       handleSQLE(localSQLException);
/*      */     }
/*      */ 
/* 2238 */     this._tranIsolationLevel = paramInt;
/*      */   }
/*      */ 
/*      */   public int getTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/* 2253 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2255 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2257 */       LOG.fine(this._logId + " getTransactionIsolation()");
/*      */     }
/*      */ 
/* 2261 */     if (this._tranIsolationLevel == -1)
/*      */     {
/* 2264 */       checkConnection();
/*      */       try
/*      */       {
/* 2267 */         this._tranIsolationLevel = this._protocol.getIntOption(null, 2);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 2272 */         handleSQLE(localSQLException);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2278 */     return this._tranIsolationLevel;
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 2291 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2293 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2295 */       LOG.fine(this._logId + " getWarnings()");
/*      */     }
/*      */ 
/* 2304 */     return this._warning;
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarningsNoCheckConnection()
/*      */     throws SQLException
/*      */   {
/* 2321 */     return this._warning;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/* 2333 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2335 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2337 */       LOG.fine(this._logId + " clearWarnings()");
/*      */     }
/*      */ 
/* 2342 */     checkConnection();
/*      */ 
/* 2344 */     this._warning = null;
/*      */   }
/*      */ 
/*      */   public int getHoldability()
/*      */     throws SQLException
/*      */   {
/* 2352 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2354 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2356 */       LOG.fine(this._logId + " getHoldability()");
/*      */     }
/*      */ 
/* 2360 */     return this._rsHoldability;
/*      */   }
/*      */ 
/*      */   public void setHoldability(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2368 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2370 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2372 */         LOG.finer(this._logId + " setHoldability(int = [" + paramInt + "])");
/*      */       }
/* 2375 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2377 */         LOG.fine(this._logId + " setHoldability(int)");
/*      */       }
/*      */     }
/*      */ 
/* 2381 */     if ((paramInt != 1) && (paramInt != 2))
/*      */     {
/* 2384 */       ErrorMessage.raiseError("JZ0SW", String.valueOf(paramInt));
/*      */     }
/*      */ 
/* 2388 */     this._rsHoldability = paramInt;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public String getSessionID()
/*      */     throws SQLException
/*      */   {
/* 2402 */     return this._protocol.getStringOption(null, 5, null);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void regWatch(String paramString, SybEventHandler paramSybEventHandler, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2430 */     checkConnection();
/*      */     try
/*      */     {
/* 2433 */       this._protocol.makeEventContext(paramString, paramSybEventHandler, paramInt);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2438 */       handleSQLE(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void regNoWatch(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2459 */     checkConnection();
/*      */     try
/*      */     {
/* 2462 */       this._protocol.killEventContext(paramString);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2467 */       handleSQLE(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Capture createCapture()
/*      */     throws SQLException
/*      */   {
/* 2482 */     return (Capture)this._protocol.getObjectOption(this._pc, 8);
/*      */   }
/*      */ 
/*      */   public Object getEndpoint()
/*      */     throws SQLException
/*      */   {
/* 2490 */     return this._protocol.getObjectOption(this._pc, 7);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void cancel()
/*      */     throws SQLException
/*      */   {
/* 2506 */     this._protocol.cancel(this._pc, true);
/*      */   }
/*      */ 
/*      */   public void markDead()
/*      */   {
/*      */     try
/*      */     {
/* 2517 */       markDead(true);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void markDead(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 2537 */     this._state = 2;
/* 2538 */     if (!paramBoolean)
/*      */       return;
/* 2540 */     checkConnection();
/*      */   }
/*      */ 
/*      */   public void markDeadTryHA()
/*      */     throws IOException
/*      */   {
/* 2554 */     int i = 0;
/*      */     try
/*      */     {
/* 2557 */       i = ((isPropertySet(34)) || (isPropertySet(72))) ? 1 : 0;
/*      */     }
/*      */     catch (SQLException localSQLException1)
/*      */     {
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 2584 */       markDead(true);
/*      */     }
/*      */     catch (SQLException localSQLException2)
/*      */     {
/* 2594 */       if ("JZ0F2".equals(localSQLException2.getSQLState()))
/*      */       {
/* 2596 */         ErrorMessage.raiseHAException("JZ0F2");
/*      */       }
/* 2598 */       else if ("JZ0F3".equals(localSQLException2.getSQLState()))
/*      */       {
/* 2600 */         ErrorMessage.raiseHAException("JZ0F3");
/*      */       }
/*      */       else
/*      */       {
/* 2620 */         IOException localIOException = new IOException(localSQLException2.getMessage());
/* 2621 */         ErrorMessage.raiseSybConnectionDeadException(localIOException);
/*      */       }
/*      */     }
/* 2624 */     if (i == 0)
/*      */     {
/*      */       return;
/*      */     }
/*      */ 
/* 2629 */     ErrorMessage.raiseHAException("JZ0F2");
/*      */   }
/*      */ 
/*      */   public String getDefaultServerCharset()
/*      */     throws SQLException
/*      */   {
/* 2642 */     checkConnection();
/* 2643 */     String str = null;
/*      */     try
/*      */     {
/* 2646 */       str = this._protocol.getStringOption(null, 6, null);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2651 */       handleSQLE(localSQLException);
/*      */     }
/*      */ 
/* 2655 */     return str;
/*      */   }
/*      */ 
/*      */   protected void handleSQLE(SQLException paramSQLException)
/*      */     throws SQLException
/*      */   {
/* 2668 */     boolean bool = thisChainHasAnException(paramSQLException);
/* 2669 */     SQLWarning localSQLWarning = null;
/* 2670 */     if (!bool)
/*      */     {
/* 2672 */       localSQLWarning = (SQLWarning)paramSQLException;
/*      */     }
/* 2676 */     else if (this._props.getBoolean(76))
/*      */     {
/* 2679 */       paramSQLException = getAllExceptions(paramSQLException);
/*      */     }
/*      */     else
/*      */     {
/* 2684 */       localSQLWarning = getAllTheWarnings(paramSQLException);
/*      */     }
/*      */ 
/* 2687 */     if (localSQLWarning != null)
/*      */     {
/* 2690 */       if (this._warning == null)
/*      */       {
/* 2692 */         this._warning = localSQLWarning;
/*      */       }
/*      */       else
/*      */       {
/* 2696 */         this._warning.setNextWarning(localSQLWarning);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2704 */     if ((!bool) || 
/* 2706 */       (this._props == null)) {
/*      */       return;
/*      */     }
/* 2709 */     throw paramSQLException;
/*      */   }
/*      */ 
/*      */   public int getHALoginStatus()
/*      */   {
/* 2724 */     return this._haLoginStatus;
/*      */   }
/*      */ 
/*      */   protected int getHAState()
/*      */   {
/* 2732 */     return this._haState;
/*      */   }
/*      */ 
/*      */   public void setHAState(int paramInt)
/*      */   {
/* 2740 */     this._haState = paramInt;
/*      */   }
/*      */ 
/*      */   protected void setHALoginStatus(int paramInt)
/*      */   {
/* 2751 */     this._haLoginStatus = paramInt;
/*      */     try
/*      */     {
/* 2754 */       if (isPropertySet(71))
/*      */       {
/* 2756 */         this._haLoginStatus |= 8;
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isDead()
/*      */   {
/* 2772 */     return this._state == 2;
/*      */   }
/*      */ 
/*      */   public static SQLWarning getAllTheWarnings(SQLException paramSQLException)
/*      */   {
/* 2778 */     SQLWarning localSQLWarning = null;
/* 2779 */     for (SQLException localSQLException = paramSQLException; localSQLException != null; localSQLException = localSQLException.getNextException())
/*      */     {
/* 2781 */       if (!localSQLException instanceof SQLWarning)
/*      */         continue;
/* 2783 */       if (localSQLWarning == null)
/*      */       {
/* 2785 */         localSQLWarning = new SQLWarning(localSQLException.getMessage(), localSQLException.getSQLState(), localSQLException.getErrorCode());
/*      */       }
/*      */       else
/*      */       {
/* 2790 */         localSQLWarning.setNextWarning(new SQLWarning(localSQLException.getMessage(), localSQLException.getSQLState(), localSQLException.getErrorCode()));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2795 */     paramSQLException = null;
/* 2796 */     return localSQLWarning;
/*      */   }
/*      */ 
/*      */   public static SQLException getAllExceptions(SQLException paramSQLException)
/*      */   {
/* 2810 */     Object localObject = null;
/* 2811 */     for (SQLException localSQLException = paramSQLException; localSQLException != null; localSQLException = localSQLException.getNextException())
/*      */     {
/* 2813 */       if (localSQLException instanceof SQLWarning)
/*      */         continue;
/* 2815 */       if (localObject == null)
/*      */       {
/* 2817 */         if (localSQLException instanceof EedInfo)
/*      */         {
/* 2819 */           localObject = new SybSQLException(localSQLException.getMessage(), localSQLException.getSQLState(), localSQLException.getErrorCode(), ((EedInfo)localSQLException).getState(), ((EedInfo)localSQLException).getSeverity(), ((EedInfo)localSQLException).getServerName(), ((EedInfo)localSQLException).getProcedureName(), ((EedInfo)localSQLException).getLineNumber(), ((EedInfo)localSQLException).getEedParams(), ((EedInfo)localSQLException).getTranState(), ((EedInfo)localSQLException).getState());
/*      */         }
/*      */         else
/*      */         {
/* 2833 */           localObject = new SQLException(localSQLException.getMessage(), localSQLException.getSQLState(), localSQLException.getErrorCode());
/*      */ 
/* 2835 */           if ((((SQLException)localObject).getCause() == null) && (paramSQLException.getCause() != null))
/*      */           {
/* 2837 */             ((SQLException)localObject).initCause(paramSQLException.getCause());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/* 2843 */       else if (localSQLException instanceof EedInfo)
/*      */       {
/* 2845 */         ((SQLException)localObject).setNextException(new SybSQLException(localSQLException.getMessage(), localSQLException.getSQLState(), localSQLException.getErrorCode(), ((EedInfo)localSQLException).getState(), ((EedInfo)localSQLException).getSeverity(), ((EedInfo)localSQLException).getServerName(), ((EedInfo)localSQLException).getProcedureName(), ((EedInfo)localSQLException).getLineNumber(), ((EedInfo)localSQLException).getEedParams(), ((EedInfo)localSQLException).getTranState(), ((EedInfo)localSQLException).getState()));
/*      */       }
/*      */       else
/*      */       {
/* 2859 */         ((SQLException)localObject).setNextException(new SQLException(localSQLException.getMessage(), localSQLException.getSQLState(), localSQLException.getErrorCode()));
/*      */ 
/* 2861 */         if ((((SQLException)localObject).getCause() == null) && (paramSQLException.getCause() != null))
/*      */         {
/* 2863 */           ((SQLException)localObject).initCause(paramSQLException.getCause());
/*      */         }
/*      */       }
/*      */ 
/* 2867 */       ((SQLException)localObject).setStackTrace(localSQLException.getStackTrace());
/*      */     }
/*      */ 
/* 2870 */     paramSQLException = null;
/* 2871 */     return (SQLException)localObject;
/*      */   }
/*      */ 
/*      */   public static SQLWarning convertToWarnings(SQLException paramSQLException)
/*      */   {
/* 2877 */     Object localObject = null;
/* 2878 */     for (SQLException localSQLException = paramSQLException; localSQLException != null; localSQLException = localSQLException.getNextException())
/*      */     {
/* 2880 */       SQLWarning localSQLWarning = new SQLWarning(localSQLException.getMessage(), localSQLException.getSQLState(), localSQLException.getErrorCode());
/*      */ 
/* 2882 */       if (localObject == null)
/*      */       {
/* 2884 */         localObject = localSQLWarning;
/*      */       }
/*      */       else
/*      */       {
/* 2888 */         localObject.setNextWarning(localSQLWarning);
/*      */       }
/*      */     }
/* 2891 */     return localObject;
/*      */   }
/*      */ 
/*      */   public void chainWarnings(SQLException paramSQLException)
/*      */   {
/* 2896 */     SQLWarning localSQLWarning = convertToWarnings(paramSQLException);
/*      */ 
/* 2898 */     if (this._warning == null)
/*      */     {
/* 2900 */       this._warning = localSQLWarning;
/*      */     }
/*      */     else
/*      */     {
/* 2904 */       this._warning.setNextWarning(localSQLWarning);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static boolean thisChainHasAnException(SQLException paramSQLException)
/*      */   {
/* 2913 */     int i = 0;
/* 2914 */     for (SQLException localSQLException = paramSQLException; localSQLException != null; localSQLException = localSQLException.getNextException())
/*      */     {
/* 2916 */       if (localSQLException instanceof SQLWarning)
/*      */         continue;
/* 2918 */       i = 1;
/* 2919 */       break;
/*      */     }
/*      */ 
/* 2922 */     return i;
/*      */   }
/*      */ 
/*      */   public void setSybMessageHandler(SybMessageHandler paramSybMessageHandler)
/*      */   {
/* 2939 */     this._pc.setMessageHandler(paramSybMessageHandler);
/*      */   }
/*      */ 
/*      */   public SybMessageHandler getSybMessageHandler()
/*      */   {
/* 2953 */     if (this._pc != null)
/*      */     {
/* 2955 */       return this._pc.getMessageHandler();
/*      */     }
/* 2957 */     return null;
/*      */   }
/*      */ 
/*      */   public Statement createStatement(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 2963 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2965 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2967 */         LOG.finer(this._logId + " createStatement(int = [" + paramInt1 + "] , int = [" + paramInt2 + "])");
/*      */       }
/* 2970 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2972 */         LOG.fine(this._logId + " createStatement(int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2977 */     SybStatement localSybStatement = (SybStatement)createStatement();
/* 2978 */     localSybStatement.setResultSetParams(paramInt1, paramInt2);
/*      */ 
/* 2980 */     return localSybStatement;
/*      */   }
/*      */ 
/*      */   public Statement createStatement(int paramInt1, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/* 2986 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2988 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2990 */         LOG.finer(this._logId + " createStatement(int = [" + paramInt1 + "] , int = [" + paramInt2 + "] , int = [" + paramInt3 + "])");
/*      */       }
/* 2994 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2996 */         LOG.fine(this._logId + " createStatement(int, int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3001 */     SybStatement localSybStatement = (SybStatement)createStatement(paramInt1, paramInt2);
/*      */ 
/* 3003 */     localSybStatement.setResultSetHoldability(paramInt3);
/*      */ 
/* 3005 */     return localSybStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 3011 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3013 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3015 */         LOG.finer(this._logId + " prepareStatement(String = [" + paramString + "] , int = [" + paramInt1 + "] , int = [" + paramInt2 + "])");
/*      */       }
/* 3019 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3021 */         LOG.fine(this._logId + " prepareStatement(String, int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3026 */     SybPreparedStatement localSybPreparedStatement = (SybPreparedStatement)prepareStatement(paramString);
/* 3027 */     localSybPreparedStatement.setResultSetParams(paramInt1, paramInt2);
/*      */ 
/* 3029 */     return localSybPreparedStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/* 3035 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3037 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3039 */         LOG.finer(this._logId + " prepareStatement(String = [" + paramString + "] , int = [" + paramInt1 + "] , int = [" + paramInt2 + "] , int = [" + paramInt3 + "])");
/*      */       }
/* 3044 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3046 */         LOG.fine(this._logId + " prepareStatement(String, int, int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3051 */     SybPreparedStatement localSybPreparedStatement = (SybPreparedStatement)prepareStatement(paramString, paramInt1, paramInt2);
/*      */ 
/* 3053 */     localSybPreparedStatement.setResultSetHoldability(paramInt3);
/*      */ 
/* 3055 */     return localSybPreparedStatement;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 3061 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3063 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3065 */         LOG.finer(this._logId + " prepareCall(String = [" + paramString + "] , int = [" + paramInt1 + "] , int = [" + paramInt2 + "])");
/*      */       }
/* 3069 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3071 */         LOG.fine(this._logId + " prepareCall(String, int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3076 */     SybCallableStatement localSybCallableStatement = (SybCallableStatement)prepareCall(paramString);
/* 3077 */     localSybCallableStatement.setResultSetParams(paramInt1, paramInt2);
/*      */ 
/* 3079 */     return localSybCallableStatement;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/* 3085 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3087 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3089 */         LOG.finer(this._logId + " prepareCall(String = [" + paramString + "] , int = [" + paramInt1 + "] , int = [" + paramInt2 + "] , int = [" + paramInt3 + "])");
/*      */       }
/* 3094 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3096 */         LOG.fine(this._logId + " prepareCall(String, int, int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3101 */     SybCallableStatement localSybCallableStatement = (SybCallableStatement)prepareCall(paramString, paramInt1, paramInt2);
/*      */ 
/* 3103 */     localSybCallableStatement.setResultSetHoldability(paramInt3);
/*      */ 
/* 3105 */     return localSybCallableStatement;
/*      */   }
/*      */ 
/*      */   public Map getTypeMap() throws SQLException
/*      */   {
/* 3110 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3112 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3114 */       LOG.fine(this._logId + " getTypeMap()");
/*      */     }
/*      */ 
/* 3122 */     return null;
/*      */   }
/*      */ 
/*      */   public void setTypeMap(Map paramMap) throws SQLException
/*      */   {
/* 3127 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3129 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3131 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setTypeMap", new Object[] { paramMap }));
/*      */       }
/* 3134 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3136 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setTypeMap", new Object[] { paramMap }));
/*      */       }
/* 3139 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3141 */         LOG.fine(this._logId + " setTypeMap(Java.util.Map)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3146 */     Debug.notImplemented(this, "setTypeMap(java.util.Map)");
/*      */   }
/*      */ 
/*      */   protected ProtocolContext initProtocol()
/*      */     throws SQLException
/*      */   {
/* 3156 */     ProtocolContext localProtocolContext = this._protocol.getProtocolContext(this._props);
/* 3157 */     localProtocolContext._protocol = this._protocol;
/* 3158 */     localProtocolContext._conn = this;
/* 3159 */     if (this._pc != null)
/*      */     {
/* 3161 */       localProtocolContext._msgHandler = this._pc._msgHandler;
/*      */     }
/* 3163 */     return localProtocolContext;
/*      */   }
/*      */ 
/*      */   public MdaManager getMDA(ProtocolContext paramProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 3169 */     checkMDA(paramProtocolContext);
/* 3170 */     return this._mda;
/*      */   }
/*      */ 
/*      */   private void handleHADRFailOver()
/*      */     throws SQLException
/*      */   {
/* 3176 */     Vector localVector = new Vector();
/*      */ 
/* 3179 */     localVector.add(this._protocol.getHadrLatestPrimaryHostPort());
/*      */ 
/* 3184 */     setHAState(-1);
/*      */ 
/* 3186 */     tryLogin(localVector);
/* 3187 */     this._wasConnected = true;
/*      */ 
/* 3189 */     this._protocol.setReceivedHadrFailover(false);
/*      */ 
/* 3194 */     ErrorMessage.raiseError("JZ0F3");
/*      */   }
/*      */ 
/*      */   protected void handleHAFailover()
/*      */     throws SQLException
/*      */   {
/* 3203 */     nextHAState();
/* 3204 */     switch (getHAState())
/*      */     {
/*      */     case 0:
/* 3209 */       if (isPropertySet(34))
/*      */       {
/* 3211 */         setHALoginStatus(1);
/*      */       }
/* 3213 */       tryLogin(this._hostPortList);
/*      */ 
/* 3215 */       this._wasConnected = true;
/*      */ 
/* 3219 */       if (this._haState == 7)
/*      */       {
/* 3221 */         setHAState(8); return;
/*      */       }
/*      */ 
/* 3225 */       setHAState(2);
/*      */ 
/* 3227 */       break;
/*      */     case 4:
/* 3235 */       setHALoginStatus(3);
/* 3236 */       tryLogin(this._hostPortList);
/*      */ 
/* 3238 */       this._wasConnected = true;
/* 3239 */       setHAState(2);
/* 3240 */       break;
/*      */     case 5:
/* 3255 */       setHALoginStatus(3);
/* 3256 */       tryLogin(this._hostPortList);
/*      */ 
/* 3258 */       this._wasConnected = true;
/* 3259 */       setHAState(2);
/* 3260 */       break;
/*      */     case 1:
/* 3264 */       if ((this._secondaryHostPortList == null) || (this._secondaryHostPortList.size() == 0))
/*      */       {
/* 3267 */         ErrorMessage.raiseError("JZ0F1");
/*      */       }
/* 3269 */       setHALoginStatus(5);
/* 3270 */       tryLogin(this._secondaryHostPortList);
/*      */ 
/* 3272 */       this._wasConnected = true;
/* 3273 */       setHAState(3);
/* 3274 */       break;
/*      */     case 6:
/* 3278 */       setHALoginStatus(7);
/* 3279 */       tryLogin(this._secondaryHostPortList);
/*      */ 
/* 3281 */       this._wasConnected = true;
/* 3282 */       setHAState(3);
/* 3283 */       break;
/*      */     case 7:
/* 3287 */       setHALoginStatus(1);
/* 3288 */       tryLogin(this._hostPortList);
/*      */ 
/* 3290 */       this._wasConnected = true;
/* 3291 */       setHAState(8);
/* 3292 */       break;
/*      */     case 10:
/* 3297 */       setHALoginStatus(3);
/* 3298 */       if (this._protocol.getRedirectionHostPort() != null)
/*      */       {
/* 3300 */         this._hostPortList = this._protocol.getRedirectionHostPort();
/*      */       }
/* 3302 */       tryLogin(this._hostPortList);
/*      */ 
/* 3304 */       this._wasConnected = true;
/* 3305 */       setHAState(8);
/* 3306 */       break;
/*      */     case 9:
/* 3310 */       if (isPropertySet(34))
/*      */       {
/* 3312 */         setHALoginStatus(17);
/*      */       }
/*      */       else
/*      */       {
/* 3316 */         setHALoginStatus(16);
/*      */       }
/* 3318 */       tryLogin(this._protocol.getRedirectionHostPort());
/*      */ 
/* 3320 */       this._wasConnected = true;
/* 3321 */       setHAState(8);
/*      */     case 2:
/*      */     case 3:
/*      */     case 8:
/*      */     }
/*      */   }
/*      */ 
/*      */   private void nextHAState()
/*      */   {
/* 3335 */     if (!this._wasConnected);
/* 3337 */     switch (this._haState)
/*      */     {
/*      */     case -1:
/* 3342 */       this._haState = 0;
/* 3343 */       break;
/*      */     case 0:
/* 3347 */       this._haState = 1;
/* 3348 */       break;
/*      */     case 7:
/* 3352 */       break;
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     default:
/* 3365 */       return;
/*      */ 
/* 3370 */       switch (this._haState)
/*      */       {
/*      */       case 2:
/* 3377 */         this._haState = 4;
/* 3378 */         break;
/*      */       case 4:
/*      */       case 5:
/* 3383 */         this._haState = 6;
/* 3384 */         break;
/*      */       case 3:
/* 3390 */         this._haState = 5;
/* 3391 */         break;
/*      */       case 8:
/* 3396 */         this._haState = 10;
/*      */       case 9:
/*      */       case 6:
/*      */       case 7:
/*      */       }
/*      */ 
/* 3397 */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkConnection()
/*      */     throws SQLException
/*      */   {
/* 3425 */     if (this._state != 2)
/*      */       return;
/* 3427 */     String str = this._props.getString(96);
/* 3428 */     if ((str != null) && (str.compareToIgnoreCase("HADR_RECONNECT") == 0) && (this._protocol.isReceivedHadrFailover()))
/*      */     {
/* 3432 */       this._state = 0;
/* 3433 */       handleHADRFailOver();
/*      */     }
/* 3445 */     else if ((isPropertySet(34)) && (!this._inClose) && (getHAState() != 1) && (getHAState() != 6))
/*      */     {
/* 3449 */       this._state = 0;
/* 3450 */       handleHAFailover();
/*      */     }
/* 3452 */     else if ((isPropertySet(72)) && (!this._inClose) && (getHAState() == 9))
/*      */     {
/* 3455 */       this._state = 0;
/* 3456 */       handleHAFailover();
/*      */     }
/*      */     else
/*      */     {
/* 3463 */       this._protocol.abort();
/* 3464 */       ErrorMessage.raiseError("JZ0C0");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected ResultSet getCursorResultSet(String paramString)
/*      */   {
/* 3471 */     if (this._cursors == null)
/*      */     {
/* 3473 */       return null;
/*      */     }
/* 3475 */     return (ResultSet)this._cursors.get(paramString);
/*      */   }
/*      */ 
/*      */   protected void addCursorResultSet(String paramString, ResultSet paramResultSet)
/*      */   {
/* 3480 */     if (this._cursors == null)
/*      */     {
/* 3482 */       this._cursors = new Hashtable();
/*      */     }
/* 3484 */     this._cursors.put(paramString, paramResultSet);
/*      */   }
/*      */ 
/*      */   public void removeCursorResultSet(String paramString)
/*      */   {
/* 3489 */     if (this._cursors == null)
/*      */     {
/* 3491 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 3495 */       this._cursors.remove(paramString);
/*      */     }
/*      */     catch (NoSuchElementException localNoSuchElementException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public com.sybase.jdbcx.SybPreparedStatement copyPreparedStatement(com.sybase.jdbcx.SybPreparedStatement paramSybPreparedStatement)
/*      */     throws SQLException
/*      */   {
/* 3513 */     SybPreparedStatementCopy localSybPreparedStatementCopy = new SybPreparedStatementCopy(getSharedProtocolContext(), (SybPreparedStatement)paramSybPreparedStatement);
/*      */ 
/* 3519 */     localSybPreparedStatementCopy.setMaxRows(0);
/*      */ 
/* 3521 */     return localSybPreparedStatementCopy;
/*      */   }
/*      */ 
/*      */   public com.sybase.jdbcx.SybCallableStatement copyCallableStatement(com.sybase.jdbcx.SybCallableStatement paramSybCallableStatement)
/*      */     throws SQLException
/*      */   {
/* 3535 */     SybCallableStatementCopy localSybCallableStatementCopy = new SybCallableStatementCopy(getSharedProtocolContext(), (SybCallableStatement)paramSybCallableStatement);
/*      */ 
/* 3541 */     localSybCallableStatementCopy.setMaxRows(0);
/*      */ 
/* 3543 */     return localSybCallableStatementCopy;
/*      */   }
/*      */ 
/*      */   protected synchronized ProtocolContext getSharedProtocolContext()
/*      */     throws SQLException
/*      */   {
/* 3553 */     if (this._sharedPc == null)
/*      */     {
/* 3555 */       this._sharedPc = initProtocol();
/*      */     }
/* 3557 */     return this._sharedPc;
/*      */   }
/*      */ 
/*      */   protected synchronized CacheManager getSharedCacheManager()
/*      */   {
/* 3566 */     return this._sharedCm;
/*      */   }
/*      */ 
/*      */   protected synchronized void setSharedCacheManager(CacheManager paramCacheManager)
/*      */   {
/* 3575 */     this._sharedCm = paramCacheManager;
/*      */   }
/*      */ 
/*      */   protected int getDynStmtNum()
/*      */   {
/* 3580 */     return this._dynStmtNum;
/*      */   }
/*      */ 
/*      */   protected void setDynStmtNum(int paramInt)
/*      */   {
/* 3585 */     this._dynStmtNum = paramInt;
/*      */   }
/*      */ 
/*      */   private void checkMDA(ProtocolContext paramProtocolContext) throws SQLException
/*      */   {
/* 3590 */     if (this._mda != null)
/*      */       return;
/*      */     try
/*      */     {
/* 3594 */       if (paramProtocolContext == null)
/*      */       {
/* 3596 */         synchronized (this)
/*      */         {
/* 3598 */           this._mda = new MdaManager(this._logId, this, this._pc);
/*      */         }
/*      */ 
/*      */       }
/*      */       else {
/* 3603 */         this._mda = new MdaManager(this._logId, this, paramProtocolContext);
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 3608 */       handleSQLE(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkDBMD()
/*      */     throws SQLException
/*      */   {
/* 3615 */     checkMDA(null);
/* 3616 */     if (this._sybDBMD != null)
/*      */       return;
/* 3618 */     this._sybDBMD = new SybDatabaseMetaData(this._logId, this);
/*      */   }
/*      */ 
/*      */   public Statement createInternalStatement()
/*      */     throws SQLException
/*      */   {
/* 3631 */     Statement localStatement = createStatement();
/* 3632 */     localStatement.setQueryTimeout(this._internalQueryTimeout);
/* 3633 */     return localStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareInternalStatement(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3642 */     PreparedStatement localPreparedStatement = prepareStatement(paramString);
/* 3643 */     localPreparedStatement.setQueryTimeout(this._internalQueryTimeout);
/* 3644 */     return localPreparedStatement;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareInternalStatement(String paramString, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 3653 */     PreparedStatement localPreparedStatement = prepareStatement(paramString, paramBoolean);
/* 3654 */     localPreparedStatement.setQueryTimeout(this._internalQueryTimeout);
/* 3655 */     return localPreparedStatement;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareInternalCall(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3664 */     CallableStatement localCallableStatement = prepareCall(paramString);
/* 3665 */     localCallableStatement.setQueryTimeout(this._internalQueryTimeout);
/* 3666 */     return localCallableStatement;
/*      */   }
/*      */ 
/*      */   public boolean isValid(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3676 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3678 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3680 */         LOG.finer(this._logId + " isValid(int = [" + paramInt + "])");
/*      */       }
/* 3682 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3684 */         LOG.fine(this._logId + " isValid(int)");
/*      */       }
/*      */     }
/*      */ 
/* 3688 */     Statement localStatement = null;
/* 3689 */     ResultSet localResultSet = null;
/* 3690 */     if (paramInt < 0)
/*      */     {
/* 3692 */       ErrorMessage.raiseError("JZ0SS");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 3697 */       localStatement = createStatement();
/* 3698 */       localStatement.setQueryTimeout(paramInt);
/* 3699 */       localResultSet = localStatement.executeQuery("select 1");
/* 3700 */       return localResultSet.next();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*      */     }
/*      */     finally
/*      */     {
/* 3709 */       if (localResultSet != null)
/*      */       {
/* 3711 */         localResultSet.close();
/* 3712 */         localResultSet = null;
/*      */       }
/*      */ 
/* 3715 */       if (localStatement != null)
/*      */       {
/* 3717 */         localStatement.close();
/* 3718 */         localStatement = null;
/*      */       }
/*      */     }
/*      */ 
/* 3722 */     return false;
/*      */   }
/*      */ 
/*      */   public Array createArrayOf(String paramString, Object[] paramArrayOfObject) throws SQLException
/*      */   {
/* 3727 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3729 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3731 */         LOG.finest(LogUtil.logMethod(false, this._logId, " createArrayOf", new Object[] { paramString, paramArrayOfObject }));
/*      */       }
/* 3734 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3736 */         LOG.finer(LogUtil.logMethod(true, this._logId, " createArrayOf", new Object[] { paramString, paramArrayOfObject }));
/*      */       }
/* 3739 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3741 */         LOG.fine(this._logId + " createArrayOf(String, Object[])");
/*      */       }
/*      */     }
/*      */ 
/* 3745 */     return null;
/*      */   }
/*      */ 
/*      */   public Blob createBlob()
/*      */     throws SQLException
/*      */   {
/* 3753 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3755 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3757 */       LOG.fine(this._logId + " createBlob()");
/*      */     }
/*      */ 
/* 3761 */     if (!canUseLocators())
/*      */     {
/* 3763 */       return new SybBinaryClientLob(this._logId, this._pc, new byte[0]);
/*      */     }
/*      */ 
/* 3766 */     if (this._batchSBPIInitialized)
/*      */     {
/* 3768 */       ErrorMessage.raiseError("JZ044");
/*      */     }
/*      */ 
/* 3772 */     PreparedStatement localPreparedStatement = this._mda.getMetaDataAccessor("INIT_IMAGELOCATOR", this._pc);
/*      */ 
/* 3774 */     SybResultSet localSybResultSet = (SybResultSet)localPreparedStatement.executeQuery();
/* 3775 */     Blob localBlob = null;
/* 3776 */     if (localSybResultSet.next())
/*      */     {
/* 3778 */       localBlob = localSybResultSet.getInitializedBlob(1);
/*      */     }
/*      */ 
/* 3781 */     return localBlob;
/*      */   }
/*      */ 
/*      */   public Clob createClob()
/*      */     throws SQLException
/*      */   {
/* 3789 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3791 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3793 */       LOG.fine(this._logId + " createClob()");
/*      */     }
/*      */ 
/* 3797 */     if (!canUseLocators())
/*      */     {
/* 3799 */       return new SybCharClientLob(this._logId, this._pc, new StringBuffer(), 1);
/*      */     }
/*      */ 
/* 3802 */     if (this._batchSBPIInitialized)
/*      */     {
/* 3804 */       ErrorMessage.raiseError("JZ044");
/*      */     }
/*      */ 
/* 3808 */     PreparedStatement localPreparedStatement = this._mda.getMetaDataAccessor("INIT_TEXTLOCATOR", this._pc);
/*      */ 
/* 3810 */     SybResultSet localSybResultSet = (SybResultSet)localPreparedStatement.executeQuery();
/* 3811 */     Clob localClob = null;
/* 3812 */     if (localSybResultSet.next())
/*      */     {
/* 3814 */       localClob = localSybResultSet.getInitializedClob(1);
/*      */     }
/*      */ 
/* 3817 */     return localClob;
/*      */   }
/*      */ 
/*      */   public Struct createStruct(String paramString, Object[] paramArrayOfObject)
/*      */     throws SQLException
/*      */   {
/* 3869 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3871 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3873 */         LOG.finest(LogUtil.logMethod(false, this._logId, " createStruct", new Object[] { paramString, paramArrayOfObject }));
/*      */       }
/* 3876 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3878 */         LOG.finer(LogUtil.logMethod(true, this._logId, " createStruct", new Object[] { paramString, paramArrayOfObject }));
/*      */       }
/* 3881 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3883 */         LOG.fine(this._logId + " createStruct(String, Object[])");
/*      */       }
/*      */     }
/*      */ 
/* 3887 */     Debug.notImplemented(this, "public Struct createStruct(String arg0, Object[] arg1)");
/*      */ 
/* 3889 */     return null;
/*      */   }
/*      */ 
/*      */   public Properties getClientInfo() throws SQLException
/*      */   {
/* 3894 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3896 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3898 */       LOG.fine(this._logId + " getClientInfo()");
/*      */     }
/*      */ 
/* 3904 */     if (this._clientInfoProperties.size() == 0)
/*      */     {
/* 3910 */       if (this._state == 2)
/*      */       {
/* 3912 */         this._clientInfoProperties.put("ApplicationName", "");
/* 3913 */         this._clientInfoProperties.put("ClientUser", "");
/* 3914 */         this._clientInfoProperties.put("ClientHostname", "");
/*      */       }
/*      */       else
/*      */       {
/* 3918 */         checkConnection();
/*      */ 
/* 3920 */         PreparedStatement localPreparedStatement = this._mda.getMetaDataAccessor("GET_CLIENT_INFO", this._pc);
/* 3921 */         localPreparedStatement.execute();
/* 3922 */         ResultSet localResultSet = localPreparedStatement.getResultSet();
/* 3923 */         if (localResultSet.next())
/*      */         {
/* 3925 */           this._clientInfoProperties.put("ApplicationName", localResultSet.getString(1));
/* 3926 */           this._clientInfoProperties.put("ClientUser", localResultSet.getString(2));
/* 3927 */           this._clientInfoProperties.put("ClientHostname", localResultSet.getString(3));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3932 */     this._clientInfoProperties.put("HADR_LISTMAP", this._protocol.getHADRListMap());
/*      */ 
/* 3934 */     return this._clientInfoProperties;
/*      */   }
/*      */ 
/*      */   public String getClientInfo(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3943 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3945 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3947 */         LOG.finer(this._logId + " getClientInfo(String = [" + paramString + "])");
/*      */       }
/* 3949 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3951 */         LOG.fine(this._logId + " getClientInfo(String)");
/*      */       }
/*      */     }
/*      */ 
/* 3955 */     checkConnection();
/* 3956 */     getClientInfo();
/* 3957 */     Object localObject1 = this._clientInfoProperties.get(paramString);
/* 3958 */     if (localObject1 != null)
/*      */     {
/* 3960 */       return localObject1.toString();
/*      */     }
/* 3962 */     if (paramString.equalsIgnoreCase("ENABLE_FUNCTIONALITY_GROUP"))
/*      */     {
/* 3964 */       return null;
/*      */     }
/* 3966 */     Object localObject2 = this._props.getConnProperty(paramString);
/* 3967 */     if ((localObject2 == null) || (localObject2.toString().equalsIgnoreCase("null")))
/*      */     {
/* 3969 */       return null;
/*      */     }
/*      */ 
/* 3973 */     return localObject2.toString();
/*      */   }
/*      */ 
/*      */   public void setClientInfo(Properties paramProperties)
/*      */     throws SQLException
/*      */   {
/* 3979 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3981 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3983 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setClientInfo", new Object[] { paramProperties }));
/*      */       }
/* 3986 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3988 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setClientInfo", new Object[] { paramProperties }));
/*      */       }
/* 3991 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3993 */         LOG.fine(this._logId + " setClientInfo(Properties)");
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 3999 */       checkConnection();
/*      */     }
/*      */     catch (SQLException localSQLException1)
/*      */     {
/* 4003 */       throw new SQLException();
/*      */     }
/* 4005 */     String str = null;
/* 4006 */     Object localObject1 = null;
/* 4007 */     String[] arrayOfString = { "clientapplname", "clienthostname", "clientname" };
/* 4008 */     for (int i = 0; i < paramProperties.size(); ++i)
/*      */     {
/* 4010 */       str = paramProperties.keySet().toArray()[i].toString();
/* 4011 */       if (str.equalsIgnoreCase("HADR_LISTMAP")) {
/*      */         continue;
/*      */       }
/*      */ 
/* 4015 */       Object localObject2 = paramProperties.get(str);
/*      */       try
/*      */       {
/*      */         PreparedStatement localPreparedStatement;
/* 4018 */         if (str.equals("ApplicationName"))
/*      */         {
/* 4020 */           localPreparedStatement = this._mda.getMetaDataAccessor("SET_CLIENT_INFO", this._pc);
/* 4021 */           localPreparedStatement.setString(1, arrayOfString[0]);
/* 4022 */           localPreparedStatement.setString(2, localObject2.toString());
/* 4023 */           localPreparedStatement.executeUpdate();
/* 4024 */           this._clientInfoProperties.put(str, localObject2.toString());
/* 4025 */           break label468:
/*      */         }
/* 4027 */         if (str.equals("ClientHostname"))
/*      */         {
/* 4029 */           localPreparedStatement = this._mda.getMetaDataAccessor("SET_CLIENT_INFO", this._pc);
/* 4030 */           localPreparedStatement.setString(1, arrayOfString[1]);
/* 4031 */           localPreparedStatement.setString(2, localObject2.toString());
/* 4032 */           localPreparedStatement.executeUpdate();
/* 4033 */           this._clientInfoProperties.put(str, localObject2.toString());
/* 4034 */           break label468:
/*      */         }
/* 4036 */         if (str.equals("ClientUser"))
/*      */         {
/* 4038 */           localPreparedStatement = this._mda.getMetaDataAccessor("SET_CLIENT_INFO", this._pc);
/* 4039 */           localPreparedStatement.setString(1, arrayOfString[2]);
/* 4040 */           localPreparedStatement.setString(2, localObject2.toString());
/* 4041 */           localPreparedStatement.executeUpdate();
/* 4042 */           this._clientInfoProperties.put(str, localObject2.toString());
/* 4043 */           break label468:
/*      */         }
/*      */ 
/* 4047 */         label468: throw new SQLException();
/*      */       }
/*      */       catch (SQLException localSQLException2)
/*      */       {
/* 4052 */         if (localObject1 == null)
/*      */         {
/* 4054 */           localObject1 = localSQLException2;
/*      */         }
/*      */         else
/*      */         {
/* 4058 */           localObject1.setNextException(localSQLException2);
/*      */         }
/*      */       }
/*      */     }
/* 4062 */     if (localObject1 == null)
/*      */       return;
/* 4064 */     throw localObject1;
/*      */   }
/*      */ 
/*      */   public void setClientInfo(String paramString1, String paramString2)
/*      */     throws SQLException
/*      */   {
/* 4071 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4073 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4075 */         LOG.finer(this._logId + " setClientInfo(String = [" + paramString1 + "] , String = [" + paramString2 + "])");
/*      */       }
/* 4078 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4080 */         LOG.fine(this._logId + " setClientInfo(String, String)");
/*      */       }
/*      */     }
/*      */     PreparedStatement localPreparedStatement;
/*      */     try
/*      */     {
/* 4086 */       checkConnection();
/* 4087 */       localPreparedStatement = this._mda.getMetaDataAccessor("SET_CLIENT_INFO", this._pc);
/*      */     }
/*      */     catch (SQLException localSQLException1)
/*      */     {
/* 4091 */       throw new SQLException();
/*      */     }
/* 4093 */     String str = paramString1;
/* 4094 */     Object localObject = null;
/* 4095 */     String[] arrayOfString = { "clientapplname", "clienthostname", "clientname" };
/*      */     try
/*      */     {
/* 4098 */       if (str.equals("ApplicationName"))
/*      */       {
/* 4100 */         localPreparedStatement = this._mda.getMetaDataAccessor("SET_CLIENT_INFO", this._pc);
/* 4101 */         localPreparedStatement.setString(1, arrayOfString[0]);
/* 4102 */         localPreparedStatement.setString(2, paramString2);
/* 4103 */         localPreparedStatement.executeUpdate();
/* 4104 */         this._clientInfoProperties.put(str, paramString2);
/* 4105 */         return;
/*      */       }
/* 4107 */       if (str.equals("ClientHostname"))
/*      */       {
/* 4109 */         localPreparedStatement = this._mda.getMetaDataAccessor("SET_CLIENT_INFO", this._pc);
/* 4110 */         localPreparedStatement.setString(1, arrayOfString[1]);
/* 4111 */         localPreparedStatement.setString(2, paramString2);
/* 4112 */         localPreparedStatement.executeUpdate();
/* 4113 */         this._clientInfoProperties.put(str, paramString2);
/* 4114 */         return;
/*      */       }
/* 4116 */       if (str.equals("ClientUser"))
/*      */       {
/* 4118 */         localPreparedStatement = this._mda.getMetaDataAccessor("SET_CLIENT_INFO", this._pc);
/* 4119 */         localPreparedStatement.setString(1, arrayOfString[2]);
/* 4120 */         localPreparedStatement.setString(2, paramString2);
/* 4121 */         localPreparedStatement.executeUpdate();
/* 4122 */         this._clientInfoProperties.put(str, paramString2);
/* 4123 */         return;
/*      */       }
/* 4125 */       int i = this._props.getKey(str);
/*      */ 
/* 4127 */       if (i == -1)
/*      */       {
/* 4129 */         throw new SQLException();
/*      */       }
/*      */ 
/* 4136 */       if ((paramString2 == null) || (paramString2.equalsIgnoreCase("")) || (paramString2.equalsIgnoreCase("null")))
/*      */       {
/* 4139 */         this._props.setConnProperty(str, null, true, false);
/*      */       }
/*      */       else
/*      */       {
/* 4143 */         this._props.setConnProperty(str, paramString2, true, false);
/*      */       }
/*      */ 
/* 4147 */       if (str.equalsIgnoreCase("DYNAMIC_PREPARE"))
/*      */       {
/* 4149 */         this._dynamicPrepare = isPropertySet(i);
/*      */       }
/* 4151 */       if (str.equalsIgnoreCase("DEFAULT_QUERY_TIMEOUT"))
/*      */       {
/* 4153 */         this._defaultQueryTimeout = this._props.getInteger(62);
/*      */       }
/*      */ 
/* 4156 */       if (str.equalsIgnoreCase("INTERNAL_QUERY_TIMEOUT"))
/*      */       {
/* 4158 */         this._internalQueryTimeout = this._props.getInteger(61);
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException2)
/*      */     {
/* 4163 */       if (localObject == null)
/*      */       {
/* 4165 */         localObject = localSQLException2;
/*      */       }
/*      */       else
/*      */       {
/* 4169 */         localObject.setNextException(localSQLException2);
/*      */       }
/*      */     }
/* 4172 */     if (localObject == null)
/*      */       return;
/* 4174 */     throw localObject;
/*      */   }
/*      */ 
/*      */   protected boolean isLobLocatorSupported()
/*      */     throws SQLException
/*      */   {
/* 4196 */     return this._protocol.isLocatorSupported();
/*      */   }
/*      */ 
/*      */   public boolean canUseLocators()
/*      */     throws SQLException
/*      */   {
/* 4207 */     return (isLobLocatorSupported()) && (isLOBLocatorEnabled()) && (!getAutoCommit());
/*      */   }
/*      */ 
/*      */   public boolean isPropertySet(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 4213 */     return this._props.getBoolean(paramInt);
/*      */   }
/*      */ 
/*      */   public boolean isLOBLocatorEnabled()
/*      */     throws SQLException
/*      */   {
/* 4225 */     return isPropertySet(79);
/*      */   }
/*      */ 
/*      */   public boolean isWrapperFor(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 4233 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4235 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4237 */         LOG.finest(LogUtil.logMethod(false, this._logId, " isWrapperFor", new Object[] { paramClass }));
/*      */       }
/* 4240 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4242 */         LOG.finer(LogUtil.logMethod(true, this._logId, " isWrapperFor", new Object[] { paramClass }));
/*      */       }
/* 4245 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4247 */         LOG.fine(this._logId + " isWrapperFor(Class<?>)");
/*      */       }
/*      */     }
/*      */ 
/* 4251 */     return paramClass.isInstance(this);
/*      */   }
/*      */ 
/*      */   public Object unwrap(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 4259 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4261 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4263 */         LOG.finest(LogUtil.logMethod(false, this._logId, " unwrap", new Object[] { paramClass }));
/*      */       }
/* 4266 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4268 */         LOG.finer(LogUtil.logMethod(true, this._logId, " unwrap", new Object[] { paramClass }));
/*      */       }
/* 4271 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4273 */         LOG.fine(this._logId + " unwrap(Class<T>)");
/*      */       }
/*      */     }
/* 4276 */     SybConnection localSybConnection = null;
/*      */     try
/*      */     {
/* 4279 */       localSybConnection = this;
/*      */     }
/*      */     catch (ClassCastException localClassCastException)
/*      */     {
/* 4283 */       ErrorMessage.raiseError("JZ031", paramClass.getName());
/*      */     }
/*      */ 
/* 4286 */     return localSybConnection;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybConnection
 * JD-Core Version:    0.5.4
 */