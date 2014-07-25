/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.charset.CheckPureConverter;
/*      */ import com.sybase.jdbc3.charset.NioConverter;
/*      */ import com.sybase.jdbc3.jdbc.Cursor;
/*      */ import com.sybase.jdbc3.jdbc.DateObject;
/*      */ import com.sybase.jdbc3.jdbc.DynamicClassLoader;
/*      */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*      */ import com.sybase.jdbc3.jdbc.MdaManager;
/*      */ import com.sybase.jdbc3.jdbc.Param;
/*      */ import com.sybase.jdbc3.jdbc.ParamManager;
/*      */ import com.sybase.jdbc3.jdbc.Protocol;
/*      */ import com.sybase.jdbc3.jdbc.ProtocolContext;
/*      */ import com.sybase.jdbc3.jdbc.ProtocolManager;
/*      */ import com.sybase.jdbc3.jdbc.ProtocolResultSet;
/*      */ import com.sybase.jdbc3.jdbc.SybBCP;
/*      */ import com.sybase.jdbc3.jdbc.SybConnection;
/*      */ import com.sybase.jdbc3.jdbc.SybHAException;
/*      */ import com.sybase.jdbc3.jdbc.SybProperty;
/*      */ import com.sybase.jdbc3.jdbc.SybResultSet;
/*      */ import com.sybase.jdbc3.jdbc.SybSQLException;
/*      */ import com.sybase.jdbc3.jdbc.SybSQLWarning;
/*      */ import com.sybase.jdbc3.jdbc.TextPointer;
/*      */ import com.sybase.jdbc3.timedio.InStreamMgr;
/*      */ import com.sybase.jdbc3.timedio.OutStreamMgr;
/*      */ import com.sybase.jdbc3.utils.CacheManager;
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import com.sybase.jdbc3.utils.HexConverts;
/*      */ import com.sybase.jdbc3.utils.SyncObj;
/*      */ import com.sybase.jdbcx.CharsetConverter;
/*      */ import com.sybase.jdbcx.SybEventHandler;
/*      */ import com.sybase.jdbcx.SybMessageHandler;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.DriverManager;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import java.util.WeakHashMap;
/*      */ 
/*      */ public class Tds
/*      */   implements TdsConst, Protocol
/*      */ {
/*   97 */   protected InStreamMgr _inStreamMgr = null;
/*      */ 
/*  100 */   protected OutStreamMgr _outStreamMgr = null;
/*      */ 
/*  111 */   private PduOutputFormatter _outFormat = null;
/*      */ 
/*  124 */   protected boolean _sendLiterals = false;
/*      */ 
/*  127 */   protected boolean _sendLongAnyway = false;
/*      */ 
/*  130 */   protected boolean _stripExec = false;
/*      */ 
/*  133 */   protected TdsDataOutputStream _out = null;
/*      */ 
/*  136 */   private SybProperty _info = null;
/*      */ 
/*  142 */   private Hashtable _typeSearchableList = null;
/*      */ 
/*  149 */   private HashMap _typeNameList = new HashMap();
/*      */ 
/*  152 */   private Map _storeTPC = Collections.synchronizedMap(new WeakHashMap());
/*      */ 
/*  155 */   private TdsEventContext _eventCtx = null;
/*      */ 
/*  161 */   private int _maxRows = 0;
/*      */ 
/*  165 */   private boolean _adjustingMaxRows = false;
/*      */ 
/*  168 */   private int _packetSize = 512;
/*      */ 
/*  171 */   protected String _charsetName = null;
/*  172 */   private String _serverDefaultCharsetName = null;
/*      */ 
/*  177 */   protected CharsetConverter _charsetConverter = null;
/*      */ 
/*  182 */   private boolean _inLogin = false;
/*      */ 
/*  185 */   private boolean _cancelSent = false;
/*      */ 
/*  190 */   protected boolean _gotCancelAck = false;
/*      */   protected SybConnection _conn;
/*  196 */   protected CapabilityToken _capT = null;
/*      */   private Vector _contexts;
/*  206 */   boolean _bigEndian = true;
/*      */   protected static boolean _unicodeBigUnmarkedOK;
/*      */   protected static boolean _isUnicodeBigEndian;
/*  237 */   protected boolean _usingCheckingConverter = false;
/*      */ 
/*  239 */   protected ReusableLanguageToken _commitLangToken = null;
/*  240 */   protected ReusableLanguageToken _rollbackLangToken = null;
/*      */ 
/*  243 */   private boolean _isAse = false;
/*      */ 
/*  246 */   private boolean _inBulkBatch = false;
/*  247 */   private boolean _startBulk = false;
/*  248 */   private boolean _enableBulkRawInterface = false;
/*      */ 
/*  251 */   private boolean _recievedHadrFailover = false;
/*  252 */   private int _hadrTransactionState = -1;
/*  253 */   private String _hadrLatestPrimaryHostPort = null;
/*      */ 
/*  290 */   boolean _ignoreDIP = false;
/*  291 */   boolean _crc = false;
/*      */ 
/*  297 */   HASessionContext _haContext = null;
/*      */ 
/*  302 */   boolean _isHAConn = false;
/*      */ 
/*  306 */   KerberosSessionContext _kerberosContext = null;
/*      */ 
/*  310 */   boolean _isKerberosConn = false;
/*      */ 
/*  316 */   protected Hashtable _cursors = new Hashtable();
/*      */ 
/*  327 */   private DynamicClassLoader _classLoader = null;
/*      */   private static final int[] INFO_MSGNO;
/*      */   private static final int WARNING_SEVERITY = 10;
/*      */   private static final String COMMIT_QUERY = "commit";
/*      */   private static final String ROLLBACK_QUERY = "rollback";
/*  346 */   private boolean _useChained = true;
/*      */ 
/*  351 */   private boolean _inTransaction = false;
/*      */ 
/*  356 */   private boolean _autoCommit = true;
/*      */ 
/*  358 */   private boolean _redirectImmed = false;
/*  359 */   protected boolean _redirectBitOn = false;
/*  360 */   private Vector _redirectHostPort = null;
/*  361 */   private BCPToken _bcpT = null;
/*  362 */   private BCPRawInterface _bcpRaw = null;
/*  363 */   private boolean _isUnicharEnabled = false;
/*  364 */   private boolean _serverAcceptsColumnStatusByte = false;
/*  365 */   private boolean _serverAcceptsDateData = false;
/*  366 */   private int[] _storeStaticValues = new int[10];
/*      */   private static final int ISUNICHARENABLED = 0;
/*      */   private static final int SERVERACCEPTSCOLUMNSTATUSBYTE = 1;
/*      */   private static final int SERVERACCEPTSDATEDATA = 2;
/*      */   private static final int SERVERACCEPTSBIGDATETIMEDATA = 3;
/*  371 */   private boolean _serverAcceptsBigDateTimeData = false;
/*  372 */   private boolean _isUtf8OrServerCharset = false;
/*  373 */   private LinkedHashMap _hadrListMap = new LinkedHashMap();
/*      */ 
/*  380 */   private String _catalog = null;
/*      */ 
/*      */   public Protocol getProtocol()
/*      */   {
/*  397 */     return new Tds();
/*      */   }
/*      */ 
/*      */   public void login(String paramString, SybProperty paramSybProperty, SybConnection paramSybConnection, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  418 */     this._classLoader = ((DynamicClassLoader)paramSybProperty.getObject(37));
/*      */ 
/*  421 */     Object localObject = null;
/*  422 */     this._conn = paramSybConnection;
/*  423 */     this._info = paramSybProperty;
/*  424 */     this._bigEndian = (!paramSybProperty.getBoolean(30));
/*  425 */     this._ignoreDIP = paramSybProperty.getBoolean(32);
/*  426 */     this._crc = paramSybProperty.getBoolean(63);
/*  427 */     this._useChained = this._info.getBoolean(53);
/*      */ 
/*  429 */     if ((paramBoolean) || (this._capT == null))
/*      */     {
/*  431 */       if (this._haContext == null)
/*      */       {
/*  435 */         this._haContext = new HASessionContext(paramSybProperty, this);
/*      */       }
/*  437 */       if (this._haContext.wasHARequested())
/*      */       {
/*  439 */         this._haContext.getSessionID();
/*  440 */         this._haContext.setHALogin(this._conn.getHALoginStatus());
/*  441 */         this._isHAConn = true;
/*      */       }
/*  443 */       this._capT = new CapabilityToken();
/*      */     }
/*      */ 
/*  446 */     this._capT.setCapabilities(this._info);
/*  447 */     if (this._info.getBoolean(35))
/*      */     {
/*  450 */       this._capT.setCapabilities(this._info);
/*  451 */       paramBoolean = false;
/*      */     }
/*      */ 
/*  455 */     if (this._info.getBoolean(48))
/*      */     {
/*  457 */       this._kerberosContext = new KerberosSessionContext(this);
/*  458 */       this._isKerberosConn = true;
/*      */     }
/*      */ 
/*  462 */     int i = (paramSybProperty.getString(20) == null) ? 1 : 0;
/*      */ 
/*  464 */     String str1 = "";
/*  465 */     int j = 0;
/*      */ 
/*  467 */     if (i != 0)
/*      */     {
/*  472 */       int k = paramString.lastIndexOf(':');
/*  473 */       if (k == -1)
/*      */       {
/*  475 */         ErrorMessage.raiseError("JZ003", paramString);
/*      */       }
/*      */ 
/*  478 */       str1 = paramString.substring(0, k);
/*  479 */       String str2 = paramString.substring(k + 1);
/*      */ 
/*  486 */       if ((str1 == null) || (str2 == null) || (str1.equals("")) || (str2.equals("")))
/*      */       {
/*  489 */         ErrorMessage.raiseError("JZ003", paramString);
/*      */       }
/*      */ 
/*  493 */       if (this._kerberosContext != null)
/*      */       {
/*  495 */         this._kerberosContext.setHostName(str1);
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  500 */         j = new Integer(str2).intValue();
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException)
/*      */       {
/*  504 */         ErrorMessage.raiseError("JZ0NE", paramString, localNumberFormatException.toString());
/*      */       }
/*      */ 
/*  515 */       if ((j < 0) || (j > 65535))
/*      */       {
/*  517 */         ErrorMessage.raiseError("JZ0PN", "" + j);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  525 */     setCharConvert(paramSybProperty);
/*      */     try
/*      */     {
/*  530 */       this._inStreamMgr = new InStreamMgr(str1, j, paramSybProperty, this._conn.getLoginTimeout());
/*      */ 
/*  533 */       localObject = this._inStreamMgr.getCaptureWarnings();
/*  534 */       OutputStream localOutputStream = this._inStreamMgr.getOutputStream();
/*  535 */       this._outFormat = new PduOutputFormatter(localOutputStream, this._packetSize, this._conn);
/*  536 */       this._out = new TdsDataOutputStream(this, this._outFormat);
/*  537 */       this._out.setBigEndian(this._bigEndian);
/*  538 */       this._outStreamMgr = new OutStreamMgr(this._inStreamMgr);
/*      */ 
/*  549 */       this._eventCtx = new TdsEventContext(this, this._inStreamMgr, this._outStreamMgr);
/*  550 */       this._eventCtx._conn = paramSybConnection;
/*      */ 
/*  553 */       new TdsMigrateContext(this, this._inStreamMgr, this._outStreamMgr);
/*      */ 
/*  555 */       if ((this._haContext.wasHARequested()) && (!this._haContext.isMigrating()))
/*      */       {
/*  557 */         this._haContext.setHALogin(this._conn.getHALoginStatus());
/*      */ 
/*  559 */         refreshTPC();
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  564 */       handleIOE(localIOException);
/*      */     }
/*      */ 
/*  567 */     this._sendLiterals = paramSybProperty.getBoolean(15);
/*  568 */     this._sendLongAnyway = paramSybProperty.getBoolean(28);
/*      */ 
/*  571 */     if (paramSybProperty.getString(20) == null)
/*      */     {
/*      */       try
/*      */       {
/*  575 */         doLogin();
/*      */       }
/*      */       catch (SQLWarning localSQLWarning)
/*      */       {
/*  579 */         if (localSQLWarning.getSQLState().equals("010SM"))
/*      */         {
/*      */           try
/*      */           {
/*  585 */             logout();
/*      */           }
/*      */           catch (SQLException localSQLException2)
/*      */           {
/*      */           }
/*      */ 
/*  592 */           login(paramString, paramSybProperty, paramSybConnection, false);
/*      */         }
/*  596 */         else if (localObject == null)
/*      */         {
/*  598 */           localObject = localSQLWarning;
/*      */         }
/*      */         else
/*      */         {
/*  602 */           ((SQLWarning)localObject).setNextWarning(localSQLWarning);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException localSQLException1)
/*      */       {
/*  608 */         if ((this._info.getBoolean(55)) && (this._info.getBoolean(67)) && ("JZ00L".equals(localSQLException1.getSQLState())))
/*      */         {
/*  612 */           SybProperty localSybProperty = (SybProperty)this._info.clone();
/*      */ 
/*  614 */           localSybProperty.setProperty(67, "false");
/*  615 */           localSybProperty.setProperty(55, "false");
/*      */           try
/*      */           {
/*  618 */             logout();
/*      */           }
/*      */           catch (SQLException localSQLException3)
/*      */           {
/*      */           }
/*      */ 
/*  625 */           login(paramString, localSybProperty, paramSybConnection, false);
/*      */         }
/*      */         else
/*      */         {
/*  629 */           throw localSQLException1;
/*      */         }
/*      */       }
/*      */     }
/*  633 */     if (localObject == null)
/*      */       return;
/*  635 */     throw ((Throwable)localObject);
/*      */   }
/*      */ 
/*      */   private void doLogin()
/*      */     throws SQLException
/*      */   {
/*  645 */     this._inLogin = true;
/*      */ 
/*  650 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)getProtocolContext(this._info);
/*      */ 
/*  652 */     int i = 0;
/*  653 */     i = (this._conn.getLoginTimeout() > 0) ? this._conn.getLoginTimeout() : DriverManager.getLoginTimeout();
/*      */ 
/*  655 */     localTdsProtocolContext._timeout = (i * 1000);
/*  656 */     this._outStreamMgr.getSendLock(localTdsProtocolContext);
/*  657 */     this._outStreamMgr.beginRequest(localTdsProtocolContext);
/*      */     try
/*      */     {
/*  661 */       this._outFormat.setPDUHeader(2, 0);
/*  662 */       LoginToken localLoginToken = new LoginToken(this._info, localTdsProtocolContext, this._haContext);
/*      */ 
/*  664 */       localLoginToken.send(this._out);
/*  665 */       this._capT.send(this._out);
/*  666 */       if (this._isKerberosConn)
/*      */       {
/*  668 */         this._kerberosContext.beginHandshake(localTdsProtocolContext);
/*      */       }
/*      */       else
/*      */       {
/*  672 */         this._out.flush();
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException1)
/*      */     {
/*  678 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/*  679 */       handleIOE(localIOException1);
/*      */     }
/*      */     finally
/*      */     {
/*  683 */       this._outFormat.setPDUHeader(15, 0);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  689 */       int j = 0;
/*      */ 
/*  692 */       if (this._isKerberosConn)
/*      */       {
/*  695 */         j = processLoginAckToken(localTdsProtocolContext);
/*      */         try
/*      */         {
/*  698 */           this._outFormat.setPDUHeader(15, 1);
/*  699 */           while (j != 133)
/*      */           {
/*  702 */             this._kerberosContext.exchangeOpaqueTokens(localTdsProtocolContext);
/*  703 */             j = processLoginAckToken(localTdsProtocolContext);
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (IOException localIOException2)
/*      */         {
/*  710 */           handleIOE(localIOException2);
/*      */         }
/*      */         finally
/*      */         {
/*  714 */           this._outFormat.setPDUHeader(15, 0);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  721 */         j = processLoginAckToken(localTdsProtocolContext);
/*      */       }
/*      */ 
/*  725 */       Vector localVector = new Vector();
/*  726 */       while (j == 7)
/*      */       {
/*  730 */         int k = nextResult(localTdsProtocolContext);
/*      */         try
/*      */         {
/*  739 */           MsgToken localMsgToken = new MsgToken(localTdsProtocolContext._in);
/*  740 */           int l = localMsgToken.getMessageID();
/*      */ 
/*  745 */           localVector.addElement(new Integer(l));
/*      */ 
/*  747 */           switch (l)
/*      */           {
/*      */           case 1:
/*      */           case 14:
/*      */           case 30:
/*  761 */             this._info.setProperty(67, "false");
/*      */ 
/*  763 */             SecLoginContext localSecLoginContext = new SecLoginContext(this, l);
/*  764 */             localSecLoginContext.getKey(localTdsProtocolContext);
/*      */ 
/*  766 */             localTdsProtocolContext.setState(2);
/*  767 */             localTdsProtocolContext._haveDone = false;
/*      */ 
/*  771 */             this._outStreamMgr.queueRequest(localTdsProtocolContext);
/*  772 */             localSecLoginContext.sendEncPwd(localTdsProtocolContext, this._info.getString(4));
/*      */ 
/*  776 */             j = processLoginAckToken(localTdsProtocolContext);
/*      */ 
/*  781 */             if ((j != 5) && (j != 7))
/*      */             {
/*  784 */               ErrorMessage.raiseError("JZ00L"); } break;
/*      */           case 12:
/*  798 */             this._haContext.readSessionID(localTdsProtocolContext, localMsgToken);
/*      */ 
/*  805 */             localTdsProtocolContext.setState(2);
/*  806 */             localTdsProtocolContext._haveDone = false;
/*      */ 
/*  810 */             this._outStreamMgr.queueRequest(localTdsProtocolContext);
/*  811 */             this._haContext.acknowledgeSessionID(this._out, localTdsProtocolContext);
/*      */ 
/*  814 */             j = processLoginAckToken(localTdsProtocolContext);
/*      */ 
/*  819 */             if ((j != 5) && (j != 7))
/*      */             {
/*  822 */               this._conn.chainWarnings(ErrorMessage.createWarning("010HA"));
/*      */ 
/*  824 */               ErrorMessage.raiseError("JZ00L"); } break;
/*      */           default:
/*  830 */             ErrorMessage.raiseError("JZ00L");
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (IOException localIOException3)
/*      */         {
/*  843 */           this._outStreamMgr.abortRequest(localTdsProtocolContext);
/*  844 */           handleIOE(localIOException3);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  853 */       if (this._packetSize != 512)
/*      */       {
/*  856 */         this._outFormat.setNetBufSize(this._packetSize);
/*  857 */         this._inStreamMgr.setNetBufSize(this._packetSize);
/*      */       }
/*      */ 
/*  869 */       if ((this._haContext.wasHARequested()) && (((localVector.isEmpty()) || (!localVector.contains(new Integer(12))))) && (!this._haContext.isMigrating()) && (this._info.getBoolean(34)))
/*      */       {
/*  877 */         this._conn.chainWarnings(ErrorMessage.createWarning("010HD"));
/*      */ 
/*  880 */         this._outStreamMgr.endRequest(localTdsProtocolContext);
/*  881 */         logout();
/*  882 */         ErrorMessage.raiseError("JZ00L");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*      */     }
/*      */     finally
/*      */     {
/*  893 */       this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */ 
/*  895 */       this._inLogin = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void logout()
/*      */     throws SQLException
/*      */   {
/*  906 */     checkBcpWithMixStmt();
/*      */ 
/*  911 */     LogoutToken localLogoutToken = new LogoutToken();
/*  912 */     this._inStreamMgr.closing();
/*      */ 
/*  927 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)getProtocolContext(this._info);
/*      */ 
/*  929 */     localTdsProtocolContext._timeout = 1000;
/*  930 */     this._outStreamMgr.getSendLock(localTdsProtocolContext);
/*  931 */     this._outStreamMgr.beginRequest(localTdsProtocolContext);
/*      */     try
/*      */     {
/*  934 */       localLogoutToken.send(this._out);
/*  935 */       this._out.flush();
/*  936 */       this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */ 
/*  938 */       int i = 1;
/*  939 */       while (i != 0)
/*      */       {
/*  941 */         int j = nextResult(localTdsProtocolContext);
/*  942 */         switch (j)
/*      */         {
/*      */         case 0:
/*  948 */           i = 0;
/*  949 */           break;
/*      */         }
/*  951 */         ErrorMessage.raiseError("JZ0P1");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  963 */       if (!"JZ006".equals(localSQLException.getSQLState()))
/*      */       {
/*  966 */         throw localSQLException;
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  974 */       this._inStreamMgr.close();
/*      */ 
/*  977 */       this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void abort()
/*      */   {
/*  990 */     if (this._inStreamMgr == null)
/*      */       return;
/*  992 */     this._inStreamMgr.close();
/*      */   }
/*      */ 
/*      */   public void language(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 1002 */     checkBcpWithMixStmt();
/* 1003 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/* 1004 */     LanguageToken localLanguageToken = null;
/*      */ 
/* 1010 */     String str = null;
/* 1011 */     if (paramParamManager != null)
/*      */     {
/* 1013 */       paramParamManager.checkParams(this, false, false, -1);
/* 1014 */       str = paramParamManager.processParamMarkers(paramString);
/* 1015 */       if (str != null)
/*      */       {
/* 1017 */         paramString = str;
/*      */       }
/*      */     }
/*      */ 
/* 1021 */     localLanguageToken = new LanguageToken(paramString, str != null, false);
/*      */ 
/* 1026 */     boolean bool = this._outStreamMgr.getSendLock(localTdsProtocolContext);
/*      */     try
/*      */     {
/* 1030 */       adjustMaxRows(localTdsProtocolContext);
/* 1031 */       this._outStreamMgr.beginRequest(localTdsProtocolContext);
/*      */       try
/*      */       {
/* 1034 */         localLanguageToken.send(this._out);
/* 1035 */         sendParamStream(paramParamManager, this._out);
/* 1036 */         this._out.flush();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1040 */         if (localIOException instanceof TdsInputStreamIOException)
/*      */         {
/* 1042 */           cancel(localTdsProtocolContext, false);
/*      */         }
/* 1044 */         handleIOE(localIOException);
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1049 */       bool = true;
/*      */ 
/* 1051 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 1052 */       throw localSQLException;
/*      */     }
/* 1054 */     if (!bool)
/*      */       return;
/* 1056 */     this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   public void language(ProtocolContext paramProtocolContext, LanguageToken paramLanguageToken)
/*      */     throws SQLException
/*      */   {
/* 1068 */     checkBcpWithMixStmt();
/* 1069 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/*      */ 
/* 1079 */     boolean bool = this._outStreamMgr.getSendLock(localTdsProtocolContext);
/*      */     try
/*      */     {
/* 1082 */       this._outStreamMgr.beginRequest(localTdsProtocolContext);
/*      */       try
/*      */       {
/* 1085 */         paramLanguageToken.send(this._out);
/* 1086 */         this._out.flush();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1090 */         handleIOE(localIOException);
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1095 */       bool = true;
/*      */ 
/* 1097 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 1098 */       throw localSQLException;
/*      */     }
/* 1100 */     if (!bool)
/*      */       return;
/* 1102 */     this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   public void rpc(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 1112 */     checkBcpWithMixStmt();
/* 1113 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/*      */ 
/* 1118 */     paramParamManager.checkParams(this, true, false, -1);
/* 1119 */     DbrpcToken localDbrpcToken = new DbrpcToken(paramString, paramParamManager);
/*      */ 
/* 1122 */     boolean bool = this._outStreamMgr.getSendLock(localTdsProtocolContext);
/*      */     try
/*      */     {
/* 1126 */       adjustMaxRows(localTdsProtocolContext);
/* 1127 */       this._outStreamMgr.beginRequest(localTdsProtocolContext);
/*      */       try
/*      */       {
/* 1130 */         localDbrpcToken.send(this._out);
/* 1131 */         sendParamStream(paramParamManager, this._out);
/* 1132 */         this._out.flush();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1136 */         if (localIOException instanceof TdsInputStreamIOException)
/*      */         {
/* 1138 */           cancel(localTdsProtocolContext, false);
/*      */         }
/* 1140 */         handleIOE(localIOException);
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1145 */       bool = true;
/*      */ 
/* 1147 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 1148 */       throw localSQLException;
/*      */     }
/* 1150 */     if (!bool)
/*      */       return;
/* 1152 */     this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   public void dynamicPrepare(ProtocolContext paramProtocolContext, String paramString1, String paramString2, ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 1162 */     checkBcpWithMixStmt();
/* 1163 */     paramParamManager.initParamTypesArray();
/*      */ 
/* 1165 */     Object localObject = null;
/* 1166 */     CapabilitySet localCapabilitySet = this._capT._reqCaps;
/* 1167 */     if (!localCapabilitySet.get(7))
/*      */     {
/* 1172 */       ErrorMessage.raiseError("JZ0PB");
/*      */     }
/*      */     String str;
/* 1174 */     if (localCapabilitySet.get(48))
/*      */     {
/* 1177 */       str = "create proc " + paramString1 + " as " + paramString2;
/*      */     }
/*      */     else
/*      */     {
/* 1181 */       str = paramString2;
/*      */     }
/*      */ 
/* 1187 */     int i = 5 + paramString1.length() + str.length();
/* 1188 */     if (i > 32767)
/*      */     {
/* 1192 */       if (isWidetableEnabled())
/*      */       {
/* 1196 */         localObject = new Dynamic2Token(1, paramString1, str, false, false, false, false);
/*      */       }
/*      */       else
/*      */       {
/* 1205 */         ErrorMessage.raiseError("JZ0PD");
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1211 */       localObject = new DynamicToken(1, paramString1, str, false, false, false, false);
/*      */     }
/*      */ 
/* 1215 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/*      */ 
/* 1218 */     localTdsProtocolContext._dynamicFmts = null;
/*      */ 
/* 1220 */     this._outStreamMgr.getSendLock(localTdsProtocolContext);
/*      */     try
/*      */     {
/* 1223 */       this._outStreamMgr.beginRequest(localTdsProtocolContext);
/*      */       try
/*      */       {
/* 1226 */         ((DynamicToken)localObject).send(this._out);
/* 1227 */         this._out.flush();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1231 */         handleIOE(localIOException);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1237 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 1238 */       throw localSQLException;
/*      */     }
/* 1240 */     this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */     while (true)
/*      */     {
/* 1244 */       switch (nextResult(localTdsProtocolContext))
/*      */       {
/*      */       case 1:
/* 1249 */         localTdsProtocolContext._lastResult = 0;
/*      */ 
/* 1252 */         localTdsProtocolContext._dynamicFmts = localTdsProtocolContext._paramFmts;
/* 1253 */         localTdsProtocolContext._dynamicFmts.setPc(localTdsProtocolContext);
/* 1254 */         paramParamManager.setParamMd(localTdsProtocolContext._paramFmts, false);
/* 1255 */         break;
/*      */       case 3:
/* 1259 */         localTdsProtocolContext._lastResult = 0;
/*      */ 
/* 1261 */         localTdsProtocolContext._paramFmts.setPc(localTdsProtocolContext);
/* 1262 */         paramParamManager.setParamMd(localTdsProtocolContext._paramFmts, true);
/* 1263 */         break;
/*      */       case 0:
/* 1266 */         return;
/*      */       case 2:
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void languageBatch(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager, ArrayList paramArrayList, int paramInt, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 1277 */     boolean bool = initCommandExecSession(paramProtocolContext);
/* 1278 */     sendLanguageParams(paramProtocolContext, paramString, paramParamManager, paramArrayList, paramBoolean, paramInt, true);
/* 1279 */     finishCommandExecSession(paramProtocolContext, bool);
/*      */   }
/*      */ 
/*      */   public void sendLanguageParams(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager, ArrayList paramArrayList, boolean paramBoolean1, int paramInt, boolean paramBoolean2)
/*      */     throws SQLException
/*      */   {
/* 1289 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/*      */     try
/*      */     {
/* 1293 */       LanguageToken localLanguageToken = null;
/* 1294 */       if (paramInt != 0)
/*      */       {
/* 1296 */         startAsyncThreadIfLargeBatch(paramInt, localTdsProtocolContext);
/* 1297 */         boolean bool = (paramBoolean1) && (this._capT._reqCaps.get(99));
/* 1298 */         if ((((!bool) || (paramBoolean2))) && 
/* 1300 */           (paramArrayList != null))
/*      */         {
/* 1302 */           localObject1 = paramParamManager.processParamMarkers(paramString);
/* 1303 */           if (localObject1 != null)
/*      */           {
/* 1305 */             paramString = (String)localObject1;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1310 */         Object localObject1 = paramParamManager.getParams();
/* 1311 */         int i = localObject1.length;
/* 1312 */         localLanguageToken = new LanguageToken(paramString, i != 0, bool);
/*      */ 
/* 1314 */         ParamsToken localParamsToken = new ParamsToken();
/* 1315 */         int j = 0;
/* 1316 */         for (int k = 0; ; ++k) { if (k >= paramInt)
/*      */             break label347;
/* 1318 */           if (i == 0)
/*      */           {
/* 1320 */             localLanguageToken.send(this._out);
/*      */           }
/*      */           else
/*      */           {
/*      */             Object localObject3;
/* 1324 */             for (int l = 0; l < i; ++l)
/*      */             {
/* 1326 */               localObject3 = (Param)paramArrayList.get(j++);
/* 1327 */               paramParamManager.doSetParam(l + 1, ((Param)localObject3)._colId, ((Param)localObject3)._sqlType, ((Param)localObject3)._inValue, ((Param)localObject3)._scale);
/*      */             }
/*      */ 
/* 1330 */             if (!paramParamManager.checkParams(this, false, bool, k))
/*      */               continue;
/* 1332 */             if ((!bool) || (paramBoolean2))
/*      */             {
/* 1334 */               localLanguageToken.send(this._out);
/* 1335 */               Object localObject2 = localObject1[0];
/* 1336 */               localObject3 = null;
/* 1337 */               if (localObject2 instanceof TdsParam2)
/*      */               {
/* 1339 */                 localObject3 = new ParamFormat2Token(paramParamManager, paramBoolean1);
/*      */               }
/*      */               else
/*      */               {
/* 1343 */                 localObject3 = new ParamFormatToken(paramParamManager, paramBoolean1);
/*      */               }
/* 1345 */               ((ParamFormatToken)localObject3).send(this._out);
/* 1346 */               paramBoolean2 = false;
/*      */             }
/*      */ 
/* 1349 */             localParamsToken.send(this._out);
/*      */ 
/* 1351 */             sendParamStream(localObject1, 0, localObject1.length - 1, false);
/*      */           }
/*      */  }
/*      */ 
/*      */ 
/*      */       }
/*      */ 
/* 1358 */       localLanguageToken = new LanguageToken(paramString, true, false);
/* 1359 */       label347: localLanguageToken.send(this._out);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1364 */       if (localIOException instanceof TdsInputStreamIOException)
/*      */       {
/* 1366 */         cancel(localTdsProtocolContext, false);
/*      */       }
/* 1368 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 1369 */       handleIOE(localIOException);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1376 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 1377 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void dynamicExecute(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager, int paramInt, Object[] paramArrayOfObject, int[] paramArrayOfInt1, Calendar[] paramArrayOfCalendar, int[] paramArrayOfInt2, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws SQLException
/*      */   {
/* 1395 */     boolean bool = initCommandExecSession(paramProtocolContext);
/* 1396 */     sendDynamicExecuteParams(paramProtocolContext, paramString, paramParamManager, paramInt, paramArrayOfObject, paramArrayOfInt1, paramArrayOfCalendar, paramArrayOfInt2, paramBoolean1, true, paramBoolean2);
/*      */ 
/* 1398 */     finishCommandExecSession(paramProtocolContext, bool);
/*      */   }
/*      */ 
/*      */   public void sendDynamicExecuteParams(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager, int paramInt, Object[] paramArrayOfObject, int[] paramArrayOfInt1, Calendar[] paramArrayOfCalendar, int[] paramArrayOfInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */     throws SQLException
/*      */   {
/* 1415 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/*      */     try
/*      */     {
/* 1419 */       DynamicToken localDynamicToken = null;
/* 1420 */       int i = 0;
/* 1421 */       int j = 0;
/* 1422 */       int k = paramParamManager._params.length;
/*      */ 
/* 1424 */       Object localObject1 = null;
/* 1425 */       Param localParam = null;
/* 1426 */       ParamsToken localParamsToken = new ParamsToken();
/*      */ 
/* 1428 */       Calendar[] arrayOfCalendar = new Calendar[k];
/*      */ 
/* 1433 */       boolean bool = (paramBoolean1) && (this._capT._reqCaps.get(98));
/* 1434 */       if (k == 0)
/*      */       {
/* 1436 */         localDynamicToken = new DynamicToken(2, paramString, null, false, false, isSuppressRowFormatSupportedAndSet(localTdsProtocolContext), false);
/*      */ 
/* 1438 */         for (int l = 0; ; ++l) { if (l >= paramInt)
/*      */             break label581;
/* 1440 */           localDynamicToken.send(this._out); }
/*      */ 
/*      */ 
/*      */       }
/*      */ 
/* 1445 */       startAsyncThreadIfLargeBatch(paramInt, localTdsProtocolContext);
/* 1446 */       for (int i1 = 0; i1 < paramInt; ++i1)
/*      */       {
/* 1449 */         Param[] arrayOfParam = new Param[k];
/* 1450 */         for (int i2 = 0; i2 < k; ++i2)
/*      */         {
/* 1453 */           if ((paramParamManager._params[i2]._colId != -999) && (paramParamManager._params[i2]._colId != i2))
/* 1454 */             j = paramParamManager._params[i2]._colId + i;
/*      */           else {
/* 1456 */             j = i2 + i;
/*      */           }
/* 1458 */           Object localObject2 = paramArrayOfObject[j];
/* 1459 */           if (localObject2 != null)
/*      */           {
/* 1461 */             switch (paramArrayOfInt1[j])
/*      */             {
/*      */             case 91:
/*      */             case 92:
/*      */             case 93:
/* 1468 */               if (!localObject2 instanceof DateObject)
/*      */               {
/* 1470 */                 Calendar localCalendar = paramArrayOfCalendar[j];
/* 1471 */                 if (localCalendar != null)
/*      */                 {
/* 1473 */                   arrayOfCalendar[i2] = localCalendar;
/*      */                 }
/* 1475 */                 else if (arrayOfCalendar[i2] == null)
/*      */                 {
/* 1477 */                   arrayOfCalendar[i2] = Calendar.getInstance();
/*      */                 }
/* 1479 */                 localObject2 = new DateObject(localObject2, arrayOfCalendar[i2], paramArrayOfInt1[j], true);
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1485 */           paramParamManager.doSetParam(i2 + 1, paramParamManager._params[i2]._colId, paramArrayOfInt1[j], localObject2, paramArrayOfInt2[j]);
/*      */ 
/* 1487 */           localParam = paramParamManager._params[i2];
/* 1488 */           localParam._isUnicodeType = paramParamManager.getParamMD(i2)._isUnicodeType;
/* 1489 */           arrayOfParam[i2] = localParam;
/*      */         }
/*      */ 
/* 1492 */         i += k;
/* 1493 */         if (!paramParamManager.checkParams(this, true, bool, i1))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/* 1498 */         if ((!bool) || (paramBoolean2))
/*      */         {
/* 1500 */           i2 = ((isSuppressParamFormatSupportedAndSet()) && (!paramBoolean3) && (paramParamManager.hasParamsNotChanged())) ? 1 : 0;
/*      */ 
/* 1505 */           if (i2 == 0)
/*      */           {
/* 1507 */             paramParamManager.copyCurIntoPrevParamTypes();
/*      */           }
/* 1509 */           localDynamicToken = new DynamicToken(2, paramString, null, true, bool, isSuppressRowFormatSupportedAndSet(localTdsProtocolContext), i2);
/*      */ 
/* 1513 */           localDynamicToken.send(this._out);
/* 1514 */           if (i2 == 0)
/*      */           {
/* 1516 */             if (localParam instanceof TdsParam2)
/*      */             {
/* 1518 */               localObject1 = new ParamFormat2Token(paramParamManager, arrayOfParam, this, 0, bool);
/*      */             }
/*      */             else
/*      */             {
/* 1523 */               localObject1 = new ParamFormatToken(paramParamManager, arrayOfParam, this, 0, bool);
/*      */             }
/*      */ 
/* 1526 */             ((ParamFormatToken)localObject1).sendAddBatch(arrayOfParam, this._out);
/*      */           }
/* 1528 */           paramBoolean2 = false;
/*      */         }
/* 1530 */         localParamsToken.send(this._out);
/*      */ 
/* 1533 */         label581: sendParamStream(arrayOfParam, 0, arrayOfParam.length - 1, true);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1542 */       if (localIOException instanceof TdsInputStreamIOException)
/*      */       {
/* 1544 */         cancel(localTdsProtocolContext, false);
/*      */       }
/* 1546 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 1547 */       handleIOE(localIOException);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1554 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 1555 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void startAsyncThreadIfLargeBatch(int paramInt, TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 1566 */     int i = this._info.getInteger(85);
/*      */ 
/* 1568 */     if ((i <= 0) || (paramInt <= i) || (this._inStreamMgr.asyncStarted())) {
/*      */       return;
/*      */     }
/* 1571 */     this._inStreamMgr.startAsync();
/* 1572 */     paramTdsProtocolContext._batchReadAhead = true;
/*      */   }
/*      */ 
/*      */   public ResultSetMetaData dynamicMetaData(ProtocolContext paramProtocolContext)
/*      */   {
/* 1581 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/* 1582 */     return localTdsProtocolContext._dynamicFmts;
/*      */   }
/*      */ 
/*      */   public void dynamicDeallocate(ProtocolContext paramProtocolContext, String paramString)
/*      */     throws SQLException
/*      */   {
/* 1591 */     checkBcpWithMixStmt();
/*      */ 
/* 1595 */     DynamicToken localDynamicToken = new DynamicToken(4, paramString, null, false, false, false, false);
/*      */ 
/* 1598 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/* 1599 */     this._outStreamMgr.getSendLock(localTdsProtocolContext);
/*      */     try
/*      */     {
/* 1602 */       this._outStreamMgr.beginRequest(localTdsProtocolContext);
/*      */       try
/*      */       {
/* 1605 */         localDynamicToken.send(this._out);
/* 1606 */         this._out.flush();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1610 */         handleIOE(localIOException);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1616 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 1617 */       throw localSQLException;
/*      */     }
/* 1619 */     this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */     while (true)
/*      */     {
/* 1623 */       switch (nextResult(localTdsProtocolContext))
/*      */       {
/*      */       case 0:
/* 1627 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getSendLock(ProtocolContext paramProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 1640 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/* 1641 */     return this._outStreamMgr.getSendLock(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   public void freeSendLock(ProtocolContext paramProtocolContext)
/*      */   {
/* 1649 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/* 1650 */     this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   public void setOption(ProtocolContext paramProtocolContext, int paramInt, String paramString)
/*      */     throws SQLException
/*      */   {
/* 1670 */     switch (paramInt)
/*      */     {
/*      */     case 6:
/* 1673 */       setCharConvert(paramString, false);
/* 1674 */       break;
/*      */     case 9:
/* 1676 */       PreparedStatement localPreparedStatement = null;
/* 1677 */       MdaManager localMdaManager = this._conn.getMDA(paramProtocolContext);
/* 1678 */       localPreparedStatement = localMdaManager.getMetaDataAccessor("SET_CATALOG", paramProtocolContext);
/*      */ 
/* 1681 */       boolean bool = this._sendLiterals;
/* 1682 */       this._sendLiterals = false;
/*      */       try
/*      */       {
/* 1685 */         localPreparedStatement.setString(1, paramString);
/* 1686 */         localPreparedStatement.executeUpdate();
/* 1687 */         localPreparedStatement.close();
/*      */       }
/*      */       finally
/*      */       {
/* 1691 */         this._sendLiterals = bool;
/*      */       }
/*      */     default:
/* 1696 */       ErrorMessage.raiseError("JZ0BD");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setOption(ProtocolContext paramProtocolContext, int paramInt, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 1710 */     PreparedStatement localPreparedStatement = null;
/* 1711 */     MdaManager localMdaManager = this._conn.getMDA(paramProtocolContext);
/* 1712 */     switch (paramInt)
/*      */     {
/*      */     case 14:
/* 1715 */       localPreparedStatement = localMdaManager.getMetaDataAccessor("SET_LOGBULKCOPY_ON", paramProtocolContext);
/* 1716 */       break;
/*      */     case 1:
/* 1720 */       if ((paramBoolean) && (this._inTransaction))
/*      */       {
/* 1722 */         endTransaction(true);
/*      */       }
/* 1724 */       if (!this._info.getString(57).equalsIgnoreCase("OSW"))
/*      */       {
/* 1726 */         if (this._useChained)
/*      */         {
/* 1728 */           if (paramBoolean)
/*      */           {
/* 1730 */             localPreparedStatement = localMdaManager.getMetaDataAccessor("SET_AUTOCOMMIT_ON", paramProtocolContext); break label494:
/*      */           }
/*      */ 
/* 1735 */           localPreparedStatement = localMdaManager.getMetaDataAccessor("SET_AUTOCOMMIT_OFF", paramProtocolContext);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */         try
/*      */         {
/* 1746 */           OptionCmdToken localOptionCmdToken1 = new OptionCmdToken();
/* 1747 */           if (paramBoolean)
/*      */           {
/* 1749 */             localOptionCmdToken1.setOption(25, 0);
/*      */           }
/*      */           else
/*      */           {
/* 1753 */             localOptionCmdToken1.setOption(25, 1);
/*      */           }
/* 1755 */           processOptionCmdToken(localOptionCmdToken1);
/*      */         }
/*      */         catch (IOException localIOException1)
/*      */         {
/* 1760 */           handleIOE(localIOException1);
/*      */ 
/* 1767 */           this._autoCommit = paramBoolean;
/* 1768 */           if (paramBoolean)
/*      */           {
/* 1770 */             if (this._inTransaction)
/*      */             {
/* 1772 */               endTransaction(true);
/* 1773 */               this._inTransaction = false;
/* 1774 */               ErrorMessage.raiseWarning("010CP");
/*      */             }
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 1780 */             if (this._inTransaction)
/*      */             {
/* 1785 */               return;
/*      */             }
/* 1787 */             localPreparedStatement = localMdaManager.getMetaDataAccessor("BEGIN_TRAN", paramProtocolContext);
/*      */           }
/*      */         }
/*      */       }
/* 1789 */       break;
/*      */     case 3:
/* 1791 */       if (paramBoolean)
/*      */       {
/* 1793 */         localPreparedStatement = localMdaManager.getMetaDataAccessor("SET_READONLY_TRUE", paramProtocolContext);
/*      */       }
/*      */       else
/*      */       {
/* 1798 */         localPreparedStatement = localMdaManager.getMetaDataAccessor("SET_READONLY_FALSE", paramProtocolContext);
/*      */       }
/*      */ 
/* 1801 */       break;
/*      */     case 13:
/* 1809 */       if (this._capT._reqCaps.get(101))
/*      */       {
/*      */         try
/*      */         {
/* 1813 */           OptionCmdToken localOptionCmdToken2 = new OptionCmdToken();
/* 1814 */           if (paramBoolean)
/*      */           {
/* 1816 */             localOptionCmdToken2.setOption(49, 1);
/*      */           }
/*      */           else
/*      */           {
/* 1820 */             localOptionCmdToken2.setOption(49, 0);
/*      */           }
/* 1822 */           processOptionCmdToken(localOptionCmdToken2);
/*      */         }
/*      */         catch (IOException localIOException2)
/*      */         {
/* 1826 */           handleIOE(localIOException2);
/*      */         }
/* 1827 */       }break;
/*      */     case 11:
/* 1831 */       localPreparedStatement = localMdaManager.getMetaDataAccessor("CONNECTCONFIG", paramProtocolContext);
/* 1832 */       if (this._info.getString(57).equalsIgnoreCase("OSW"))
/*      */       {
/* 1840 */         localPreparedStatement = null; } case 2:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 12:
/*      */     default:
/*      */       try { OptionCmdToken localOptionCmdToken3 = new OptionCmdToken();
/*      */ 
/* 1847 */         if (this._info.getInteger(56) != 0)
/*      */         {
/* 1849 */           localOptionCmdToken3.setOption(2, this._info.getInteger(56));
/*      */ 
/* 1851 */           processOptionCmdToken(localOptionCmdToken3);
/*      */         }
/* 1853 */         localOptionCmdToken3 = new OptionCmdToken();
/* 1854 */         localOptionCmdToken3.setOption(35, 1);
/* 1855 */         processOptionCmdToken(localOptionCmdToken3); }
/*      */       catch (IOException localIOException3)
/*      */       {
/* 1860 */         handleIOE(localIOException3);
/*      */ 
/* 1864 */         ErrorMessage.raiseError("JZ0BD");
/* 1865 */         return;
/*      */       }
/*      */     }
/* 1867 */     if (localPreparedStatement != null)
/*      */     {
/* 1869 */       if ((!paramBoolean) && (this._inTransaction))
/*      */       {
/* 1874 */         label494: return;
/*      */       }
/* 1876 */       localPreparedStatement.executeUpdate();
/* 1877 */       localPreparedStatement.close();
/*      */     }
/*      */ 
/* 1880 */     if (paramInt != 1)
/*      */       return;
/* 1882 */     this._inTransaction = (!paramBoolean);
/*      */   }
/*      */ 
/*      */   public void setOption(ProtocolContext paramProtocolContext, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1895 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/* 1896 */     switch (paramInt1)
/*      */     {
/*      */     case 2:
/* 1899 */       int i = -1;
/*      */       Object localObject;
/* 1900 */       if (!this._info.getString(57).equalsIgnoreCase("OSW"))
/*      */       {
/* 1902 */         localObject = null;
/* 1903 */         MdaManager localMdaManager = this._conn.getMDA(paramProtocolContext);
/* 1904 */         switch (paramInt2)
/*      */         {
/*      */         case 1:
/* 1907 */           i = 0;
/* 1908 */           break;
/*      */         case 2:
/* 1910 */           i = 1;
/* 1911 */           break;
/*      */         case 4:
/* 1913 */           i = 2;
/* 1914 */           break;
/*      */         case 8:
/* 1916 */           i = 3;
/* 1917 */           break;
/*      */         case 0:
/* 1919 */           ErrorMessage.raiseError("JZ014");
/* 1920 */           break;
/*      */         case 3:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         default:
/* 1922 */           ErrorMessage.raiseError("JZ0I3", "" + paramInt2, "setTransactionIsolation");
/*      */         }
/*      */ 
/* 1925 */         localObject = localMdaManager.getMetaDataAccessor("SET_ISOLATION", " " + i, paramProtocolContext);
/*      */ 
/* 1931 */         ((PreparedStatement)localObject).executeUpdate();
/* 1932 */         ((PreparedStatement)localObject).close();
/* 1933 */         return;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1939 */         localObject = new OptionCmdToken();
/* 1940 */         switch (paramInt2)
/*      */         {
/*      */         case 1:
/* 1943 */           i = 0;
/* 1944 */           break;
/*      */         case 2:
/* 1946 */           i = 1;
/* 1947 */           break;
/*      */         case 4:
/* 1949 */           i = 2;
/* 1950 */           break;
/*      */         case 8:
/* 1952 */           i = 3;
/* 1953 */           break;
/*      */         case 0:
/* 1955 */           ErrorMessage.raiseError("JZ014");
/* 1956 */           break;
/*      */         case 3:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         default:
/* 1958 */           ErrorMessage.raiseError("JZ0I3", "" + paramInt2, "setTransactionIsolation");
/*      */         }
/*      */ 
/* 1961 */         ((OptionCmdToken)localObject).setOption(8, i);
/* 1962 */         processOptionCmdToken((OptionCmdToken)localObject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1966 */         handleIOE(localIOException);
/*      */       }
/* 1968 */       break;
/*      */     case 4:
/* 1975 */       localTdsProtocolContext._maxRows = paramInt2;
/* 1976 */       break;
/*      */     default:
/* 1978 */       ErrorMessage.raiseError("JZ0BD");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void processOptionCmdToken(OptionCmdToken paramOptionCmdToken)
/*      */     throws SQLException
/*      */   {
/* 1989 */     checkBcpWithMixStmt();
/* 1990 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)getProtocolContext(this._info);
/*      */     try
/*      */     {
/* 1993 */       this._outStreamMgr.getSendLock(localTdsProtocolContext);
/* 1994 */       this._outStreamMgr.beginRequest(localTdsProtocolContext);
/*      */       try
/*      */       {
/* 1997 */         paramOptionCmdToken.send(this._out);
/* 1998 */         this._out.flush();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 2002 */         handleIOE(localIOException);
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2007 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 2008 */       throw localSQLException;
/*      */     }
/* 2010 */     this._outStreamMgr.endRequest(localTdsProtocolContext);
/* 2011 */     getDoneResult(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   public boolean getBoolOption(ProtocolContext paramProtocolContext, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2023 */     switch (paramInt)
/*      */     {
/*      */     case 12:
/* 2026 */       return this._stripExec;
/*      */     case 1:
/* 2029 */       return !this._inTransaction;
/*      */     case 3:
/* 2032 */       boolean bool = false;
/* 2033 */       MdaManager localMdaManager = this._conn.getMDA(paramProtocolContext);
/* 2034 */       PreparedStatement localPreparedStatement = localMdaManager.getMetaDataAccessor("ISREADONLY", paramProtocolContext);
/* 2035 */       ResultSet localResultSet = localPreparedStatement.executeQuery();
/* 2036 */       localResultSet.next();
/* 2037 */       bool = localResultSet.getBoolean(1);
/* 2038 */       localPreparedStatement.close();
/* 2039 */       return bool;
/*      */     }
/*      */ 
/* 2042 */     ErrorMessage.raiseError("JZ0BD");
/*      */ 
/* 2045 */     return false;
/*      */   }
/*      */ 
/*      */   public int getIntOption(ProtocolContext paramProtocolContext, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2056 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/*      */ 
/* 2058 */     switch (paramInt)
/*      */     {
/*      */     case 2:
/* 2061 */       PreparedStatement localPreparedStatement = null;
/* 2062 */       MdaManager localMdaManager = this._conn.getMDA(paramProtocolContext);
/* 2063 */       ResultSet localResultSet = null;
/* 2064 */       localPreparedStatement = localMdaManager.getMetaDataAccessor("GET_ISOLATION", paramProtocolContext);
/* 2065 */       localResultSet = localPreparedStatement.executeQuery();
/* 2066 */       localResultSet.next();
/* 2067 */       int i = localResultSet.getInt(1);
/* 2068 */       localPreparedStatement.close();
/* 2069 */       switch (i)
/*      */       {
/*      */       case 0:
/* 2072 */         return 1;
/*      */       case 1:
/* 2074 */         return 2;
/*      */       case 2:
/* 2076 */         return 4;
/*      */       case 3:
/* 2078 */         return 8;
/*      */       }
/* 2080 */       ErrorMessage.raiseError("JZ0P4");
/*      */ 
/* 2083 */       return -1;
/*      */     case 4:
/* 2085 */       return localTdsProtocolContext._maxRows;
/*      */     }
/* 2087 */     ErrorMessage.raiseError("JZ0BD");
/*      */ 
/* 2090 */     return 0;
/*      */   }
/*      */ 
/*      */   public String getStringOption(ProtocolContext paramProtocolContext, int paramInt, String paramString)
/*      */     throws SQLException
/*      */   {
/* 2101 */     String str = null;
/* 2102 */     PreparedStatement localPreparedStatement = null;
/* 2103 */     ResultSet localResultSet = null;
/* 2104 */     MdaManager localMdaManager = null;
/* 2105 */     switch (paramInt)
/*      */     {
/*      */     case 5:
/* 2109 */       str = this._inStreamMgr.getSessionID();
/* 2110 */       break;
/*      */     case 10:
/* 2112 */       localMdaManager = this._conn.getMDA(paramProtocolContext);
/* 2113 */       str = localMdaManager.getFunctionMap(paramString, paramProtocolContext);
/* 2114 */       break;
/*      */     case 9:
/* 2116 */       if (this._catalog == null)
/*      */       {
/* 2118 */         localMdaManager = this._conn.getMDA(paramProtocolContext);
/* 2119 */         localPreparedStatement = localMdaManager.getMetaDataAccessor("GET_CATALOG", paramProtocolContext);
/* 2120 */         localResultSet = localPreparedStatement.executeQuery();
/* 2121 */         localResultSet.next();
/* 2122 */         str = localResultSet.getString(1);
/* 2123 */         localPreparedStatement.close(); break label272:
/*      */       }
/*      */ 
/* 2127 */       str = this._catalog;
/*      */ 
/* 2129 */       break;
/*      */     case 6:
/* 2131 */       if (this._serverDefaultCharsetName == null)
/*      */       {
/* 2133 */         int i = 0;
/*      */         try
/*      */         {
/* 2142 */           localMdaManager = this._conn.getMDA(paramProtocolContext);
/* 2143 */           localPreparedStatement = localMdaManager.getMetaDataAccessor("DEFAULT_CHARSET", paramProtocolContext);
/*      */ 
/* 2145 */           i = 1;
/*      */         }
/*      */         catch (SQLException localSQLException)
/*      */         {
/* 2149 */           if ("JZ0F2".equals(localSQLException.getSQLState()))
/*      */           {
/* 2154 */             throw localSQLException;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2161 */         if (i != 0)
/*      */         {
/* 2163 */           localResultSet = localPreparedStatement.executeQuery();
/* 2164 */           localResultSet.next();
/* 2165 */           this._serverDefaultCharsetName = localResultSet.getString(1);
/* 2166 */           localPreparedStatement.close();
/*      */         }
/*      */       }
/* 2169 */       str = this._serverDefaultCharsetName;
/* 2170 */       break;
/*      */     case 7:
/*      */     case 8:
/*      */     default:
/* 2172 */       ErrorMessage.raiseError("JZ0BD");
/*      */     }
/*      */ 
/* 2175 */     label272: return str;
/*      */   }
/*      */ 
/*      */   public Object getObjectOption(ProtocolContext paramProtocolContext, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2186 */     Object localObject = null;
/* 2187 */     switch (paramInt)
/*      */     {
/*      */     case 7:
/* 2190 */       localObject = this._inStreamMgr;
/* 2191 */       break;
/*      */     case 8:
/* 2193 */       localObject = this._inStreamMgr.getCapture();
/* 2194 */       break;
/*      */     default:
/* 2196 */       ErrorMessage.raiseError("JZ0BD");
/*      */     }
/*      */ 
/* 2199 */     return localObject;
/*      */   }
/*      */ 
/*      */   public void cancel(ProtocolContext paramProtocolContext, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 2211 */     cancel(paramProtocolContext, paramBoolean, true);
/*      */   }
/*      */ 
/*      */   public void cancel(ProtocolContext paramProtocolContext, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws SQLException
/*      */   {
/* 2224 */     TdsProtocolContext localTdsProtocolContext1 = (TdsProtocolContext)paramProtocolContext;
/*      */ 
/* 2227 */     if (paramBoolean1)
/*      */     {
/* 2229 */       this._inBulkBatch = false;
/*      */     }
/*      */ 
/* 2232 */     if (this._cancelSent)
/*      */     {
/* 2234 */       return;
/*      */     }
/*      */ 
/* 2238 */     if ((!localTdsProtocolContext1.isCancelNeeded()) && 
/* 2240 */       (!paramBoolean1))
/*      */     {
/* 2243 */       return;
/*      */     }
/*      */ 
/* 2248 */     if (!paramBoolean1)
/*      */     {
/* 2250 */       paramBoolean1 = this._info.getBoolean(23);
/*      */     }
/*      */ 
/* 2254 */     TdsProtocolContext localTdsProtocolContext2 = makeCancel(localTdsProtocolContext1, paramBoolean1);
/* 2255 */     if (localTdsProtocolContext2 == null)
/*      */     {
/* 2258 */       return;
/*      */     }
/*      */ 
/* 2261 */     int i = 0;
/*      */     try
/*      */     {
/* 2264 */       sendCancel(localTdsProtocolContext2);
/* 2265 */       getCancel(localTdsProtocolContext2);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*      */       try
/*      */       {
/* 2275 */         this._conn.markDeadTryHA();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 2284 */         i = 1;
/*      */ 
/* 2287 */         handleIOE(localIOException);
/*      */       }
/* 2289 */       if (!paramBoolean2)
/*      */       {
/* 2291 */         throw localSQLException;
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 2299 */       if (i == 0)
/*      */       {
/* 2301 */         endCancel(localTdsProtocolContext2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private TdsProtocolContext makeCancel(TdsProtocolContext paramTdsProtocolContext, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 2313 */     TdsProtocolContext localTdsProtocolContext = new TdsProtocolContext(null, this, this._inStreamMgr, this._outStreamMgr);
/*      */ 
/* 2315 */     localTdsProtocolContext._conn = this._conn;
/* 2316 */     localTdsProtocolContext._timeout = ((20000 > paramTdsProtocolContext._timeout) ? 20000 : paramTdsProtocolContext._timeout);
/*      */ 
/* 2318 */     localTdsProtocolContext.setSponsor(paramTdsProtocolContext);
/*      */ 
/* 2321 */     this._outStreamMgr.getSendLock(localTdsProtocolContext);
/*      */ 
/* 2323 */     if (!this._outStreamMgr.doCancelRequest(paramTdsProtocolContext, localTdsProtocolContext, paramBoolean))
/*      */     {
/* 2327 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 2328 */       return null;
/*      */     }
/* 2330 */     return localTdsProtocolContext;
/*      */   }
/*      */ 
/*      */   private void sendCancel(TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2340 */       this._outFormat.setPDUHeader(6, 0);
/*      */ 
/* 2342 */       this._outFormat.flush();
/*      */ 
/* 2344 */       this._inStreamMgr.cancelling(true);
/* 2345 */       this._cancelSent = true;
/*      */ 
/* 2347 */       this._outFormat.setPDUHeader(15, 0);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 2352 */       this._outStreamMgr.abortRequest(paramTdsProtocolContext);
/* 2353 */       handleIOE(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void getCancel(TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/*      */     while (true)
/*      */     {
/* 2366 */       int i = nextResult(paramTdsProtocolContext);
/* 2367 */       switch (i)
/*      */       {
/*      */       case 0:
/*      */       case 5:
/* 2378 */         paramTdsProtocolContext._haveDone = false;
/* 2379 */         if ((((DoneToken)paramTdsProtocolContext._tdsToken)._status & 0x20) != 0)
/*      */         {
/* 2384 */           return;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2394 */       paramTdsProtocolContext.close(false);
/*      */ 
/* 2396 */       paramTdsProtocolContext._chainedSqe = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void endCancel(TdsProtocolContext paramTdsProtocolContext)
/*      */   {
/* 2405 */     this._inStreamMgr.cancelling(false);
/* 2406 */     this._cancelSent = false;
/*      */ 
/* 2408 */     this._outStreamMgr.endRequest(paramTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   public void resync(ProtocolContext paramProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 2418 */     Debug.notImplemented(this, "resync()");
/*      */   }
/*      */ 
/*      */   public void endTransaction(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 2428 */     if (paramBoolean)
/*      */     {
/* 2430 */       if (this._commitLangToken == null)
/*      */       {
/* 2432 */         this._commitLangToken = new ReusableLanguageToken("commit");
/*      */       }
/*      */ 
/* 2435 */       doCommand(this._commitLangToken);
/*      */     }
/*      */     else
/*      */     {
/* 2439 */       if (this._rollbackLangToken == null)
/*      */       {
/* 2441 */         this._rollbackLangToken = new ReusableLanguageToken("rollback");
/*      */       }
/*      */ 
/* 2444 */       doCommand(this._rollbackLangToken);
/*      */     }
/* 2446 */     if ((this._useChained) || (this._autoCommit))
/*      */       return;
/* 2448 */     this._inTransaction = false;
/* 2449 */     setOption(null, 1, false);
/*      */   }
/*      */ 
/*      */   public void bulkWrite(TextPointer paramTextPointer, InputStream paramInputStream, int paramInt1, int paramInt2, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2466 */       if (paramInt2 + paramInt1 > paramInputStream.available())
/*      */       {
/* 2468 */         ErrorMessage.raiseError("JZ0J0");
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException1)
/*      */     {
/* 2473 */       handleIOE(localIOException1);
/*      */     }
/*      */ 
/* 2476 */     byte[] arrayOfByte1 = paramTextPointer._textPtr;
/* 2477 */     byte[] arrayOfByte2 = paramTextPointer._timeStamp;
/* 2478 */     String str1 = paramTextPointer._tableName;
/* 2479 */     String str2 = paramTextPointer._columnName;
/* 2480 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)getProtocolContext(this._info);
/* 2481 */     String str3 = HexConverts.hexConvert(arrayOfByte1, 16);
/* 2482 */     String str4 = HexConverts.hexConvert(arrayOfByte2, 8);
/* 2483 */     String str5 = "writetext bulk " + str1 + "." + str2 + " 0x" + str3 + " timestamp = 0x" + str4;
/*      */ 
/* 2485 */     if (paramBoolean)
/*      */     {
/* 2489 */       str5 = str5 + " with log";
/*      */     }
/*      */ 
/* 2492 */     this._outStreamMgr.getSendLock(localTdsProtocolContext);
/* 2493 */     localTdsProtocolContext.setSponsor(localTdsProtocolContext);
/*      */ 
/* 2496 */     language(localTdsProtocolContext, str5, null);
/*      */     try
/*      */     {
/* 2499 */       getDoneResult(localTdsProtocolContext);
/*      */     }
/*      */     catch (SQLException localSQLException1)
/*      */     {
/* 2503 */       localTdsProtocolContext.setSponsor(null);
/* 2504 */       this._outStreamMgr.endRequest(localTdsProtocolContext);
/* 2505 */       cancel(localTdsProtocolContext, false);
/* 2506 */       throw localSQLException1;
/*      */     }
/*      */ 
/* 2511 */     int i = this._packetSize - 8;
/*      */ 
/* 2513 */     byte[] arrayOfByte3 = new byte[i];
/* 2514 */     this._outStreamMgr.beginRequest(localTdsProtocolContext);
/*      */     try
/*      */     {
/* 2518 */       this._outFormat.setPDUHeader(7, 0);
/*      */ 
/* 2520 */       int j = 0;
/* 2521 */       if (paramInt2 == 0)
/*      */       {
/* 2525 */         paramInt2 = paramInputStream.available();
/*      */       }
/* 2529 */       else if (paramInt1 > 0)
/*      */       {
/* 2532 */         paramInputStream.skip(paramInt1);
/*      */       }
/*      */ 
/* 2536 */       this._out.writeInt(paramInt2);
/*      */ 
/* 2538 */       while ((j = paramInputStream.read(arrayOfByte3, 0, Math.min(paramInt2, arrayOfByte3.length))) > 0)
/*      */       {
/* 2540 */         this._outFormat.write(arrayOfByte3, j);
/* 2541 */         paramInt2 -= j;
/*      */       }
/* 2543 */       this._outFormat.flush();
/*      */     }
/*      */     catch (IOException localIOException2)
/*      */     {
/* 2547 */       handleIOE(localIOException2);
/*      */     }
/*      */     finally
/*      */     {
/* 2551 */       this._outFormat.setPDUHeader(15, 0);
/* 2552 */       localTdsProtocolContext.setSponsor(null);
/* 2553 */       this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*      */       while (true)
/*      */       {
/* 2565 */         int k = nextResult(localTdsProtocolContext);
/* 2566 */         switch (k)
/*      */         {
/*      */         case 0:
/* 2569 */           break;
/*      */         case 3:
/* 2578 */           if (localTdsProtocolContext._lastTds == 172)
/*      */           {
/* 2580 */             localTdsProtocolContext._in.readUnsignedShort();
/*      */ 
/* 2583 */             TdsJdbcInputStream localTdsJdbcInputStream1 = new TdsJdbcInputStream(null, localTdsProtocolContext, this);
/*      */ 
/* 2585 */             localTdsJdbcInputStream1._dataFmt = new DataFormat(localTdsProtocolContext._in, false);
/*      */ 
/* 2587 */             arrayOfByte2 = localTdsJdbcInputStream1.getBytes();
/*      */ 
/* 2590 */             System.arraycopy(arrayOfByte2, 0, paramTextPointer._timeStamp, 0, arrayOfByte2.length);
/*      */ 
/* 2593 */             localTdsProtocolContext._lastResult = -1;
/*      */           }
/*      */ 
/* 2596 */           if ((localTdsProtocolContext._lastTds == 236) || (localTdsProtocolContext._lastTds == 32))
/*      */           {
/* 2602 */             int l = localTdsProtocolContext._in.readUnsignedByte();
/*      */ 
/* 2607 */             if (l != 215)
/*      */             {
/* 2610 */               ErrorMessage.raiseError("JZ0P4");
/*      */             }
/*      */ 
/* 2614 */             TdsJdbcInputStream localTdsJdbcInputStream2 = new TdsJdbcInputStream(null, localTdsProtocolContext, this);
/*      */ 
/* 2616 */             localTdsJdbcInputStream2._dataFmt = localTdsProtocolContext._paramFmts.getDataFormat(0);
/*      */ 
/* 2618 */             arrayOfByte2 = localTdsJdbcInputStream2.getBytes();
/*      */ 
/* 2621 */             System.arraycopy(arrayOfByte2, 0, paramTextPointer._timeStamp, 0, arrayOfByte2.length);
/*      */ 
/* 2624 */             localTdsProtocolContext._lastResult = -1;
/* 2625 */           }break;
/*      */         }
/*      */ 
/* 2628 */         ErrorMessage.raiseError("JZ0P1");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException2)
/*      */     {
/*      */     }
/*      */     catch (IOException localIOException3)
/*      */     {
/* 2639 */       handleIOE(localIOException3);
/*      */     }
/*      */     finally
/*      */     {
/* 2646 */       localTdsProtocolContext.drop();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writePage(TextPointer paramTextPointer, byte[] paramArrayOfByte, String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2661 */     String str = "dbcc dbrepair (" + paramString + ", writepage, " + paramInt + ")";
/*      */ 
/* 2663 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)getProtocolContext(this._info);
/*      */ 
/* 2665 */     this._outStreamMgr.getSendLock(localTdsProtocolContext);
/* 2666 */     localTdsProtocolContext.setSponsor(localTdsProtocolContext);
/*      */ 
/* 2669 */     language(localTdsProtocolContext, str, null);
/*      */     try
/*      */     {
/* 2672 */       getDoneResult(localTdsProtocolContext);
/*      */     }
/*      */     catch (SQLException localSQLException1)
/*      */     {
/* 2676 */       localTdsProtocolContext.setSponsor(null);
/* 2677 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 2678 */       cancel(localTdsProtocolContext, false);
/* 2679 */       throw localSQLException1;
/*      */     }
/*      */ 
/* 2682 */     this._outStreamMgr.beginRequest(localTdsProtocolContext);
/*      */     try
/*      */     {
/* 2686 */       this._outFormat.setPDUHeader(7, 0);
/*      */ 
/* 2690 */       int i = 4 + paramArrayOfByte.length;
/*      */ 
/* 2693 */       this._out.writeInt(i);
/*      */ 
/* 2695 */       this._out.writeInt(0);
/* 2696 */       this._out.write(paramArrayOfByte);
/* 2697 */       this._outFormat.flush();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 2701 */       handleIOE(localIOException);
/*      */     }
/*      */     finally
/*      */     {
/* 2705 */       this._outFormat.setPDUHeader(15, 0);
/* 2706 */       localTdsProtocolContext.setSponsor(null);
/* 2707 */       this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 2712 */       getDoneResult(localTdsProtocolContext);
/*      */     }
/*      */     catch (SQLException localSQLException2)
/*      */     {
/*      */     }
/*      */     finally
/*      */     {
/* 2723 */       localTdsProtocolContext.drop();
/*      */     }
/*      */   }
/*      */ 
/*      */   public int nextResult(ProtocolContext paramProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 2736 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/*      */ 
/* 2740 */     if (localTdsProtocolContext._ungotResult != -1)
/*      */     {
/* 2744 */       localTdsProtocolContext._lastResult = localTdsProtocolContext._ungotResult;
/*      */ 
/* 2749 */       if (localTdsProtocolContext._ungotResult == 6)
/*      */       {
/* 2751 */         localTdsProtocolContext._ungotResult = 211;
/*      */       }
/*      */       else
/*      */       {
/* 2755 */         localTdsProtocolContext._ungotResult = -1;
/*      */       }
/* 2757 */       return localTdsProtocolContext._lastResult;
/*      */     }
/*      */ 
/* 2762 */     switch (localTdsProtocolContext._lastResult)
/*      */     {
/*      */     case 3:
/* 2765 */       slurpParams(localTdsProtocolContext);
/* 2766 */       if (!this._info.getBoolean(43))
/*      */       {
/* 2769 */         localTdsProtocolContext.chainException(ErrorMessage.createWarning("010P4")); } break;
/*      */     case 1:
/* 2774 */       resultSet(paramProtocolContext);
/*      */     case 209:
/* 2776 */       localTdsProtocolContext._trs.close(false);
/* 2777 */       localTdsProtocolContext.chainException(ErrorMessage.createWarning("010P6"));
/*      */     }
/*      */ 
/* 2784 */     if (localTdsProtocolContext._haveDone)
/*      */     {
/* 2786 */       localTdsProtocolContext._haveDone = false;
/*      */ 
/* 2790 */       localTdsProtocolContext._lastResult = 0;
/* 2791 */       return localTdsProtocolContext._lastResult;
/*      */     }
/*      */ 
/* 2794 */     int i = -1;
/*      */     while (true)
/*      */     {
/*      */       label959: Object localObject;
/*      */       try
/*      */       {
/*      */         while (true)
/*      */         {
/*      */           try
/*      */           {
/* 2804 */             i = localTdsProtocolContext._lastTds;
/* 2805 */             localTdsProtocolContext._lastTds = localTdsProtocolContext._in.readUnsignedByte();
/*      */           }
/*      */           catch (IOException localIOException1)
/*      */           {
/* 2811 */             if ((this._cancelSent) && 
/* 2818 */               (localIOException1 instanceof EOFException) && 
/* 2823 */               (localTdsProtocolContext._tdsToken != null) && ((((DoneToken)localTdsProtocolContext._tdsToken)._status & 0x20) != 0))
/*      */             {
/* 2829 */               localTdsProtocolContext._lastResult = 0;
/* 2830 */               break label1976:
/*      */             }
/*      */ 
/* 2841 */             if (localTdsProtocolContext._chainedSqe != null)
/*      */             {
/* 2843 */               handleIOE(localIOException1, localTdsProtocolContext._chainedSqe);
/*      */             }
/*      */             else
/*      */             {
/* 2847 */               handleIOE(localIOException1);
/*      */             }
/*      */           }
/* 2850 */           switch (localTdsProtocolContext._lastTds)
/*      */           {
/*      */           case 253:
/*      */           case 254:
/*      */           case 255:
/* 2857 */             DoneToken localDoneToken = new DoneToken(localTdsProtocolContext._in);
/* 2858 */             localTdsProtocolContext._tdsToken = localDoneToken;
/* 2859 */             if ((localDoneToken._status & 0x10) != 0)
/*      */             {
/* 2863 */               if ((((TdsProtocolContext)paramProtocolContext).isSelectSql()) && (!paramProtocolContext._batch) && (localTdsProtocolContext._dynamicFmts != null) && (((i == 231) || (i == 98))))
/*      */               {
/* 2869 */                 ungetResult(localTdsProtocolContext, 253);
/* 2870 */                 localTdsProtocolContext._lastResult = 1;
/* 2871 */                 break label1976:
/*      */               }
/*      */ 
/* 2875 */               localTdsProtocolContext._lastResult = 5;
/* 2876 */               if ((localTdsProtocolContext._lastTds == 255) && ((localDoneToken._status & 0x1) != 0))
/*      */               {
/* 2878 */                 setCount(localDoneToken, localTdsProtocolContext);
/* 2879 */                 if ((localTdsProtocolContext._batch) || (this._ignoreDIP))
/*      */                 {
/* 2881 */                   setDoneCount(localDoneToken, localTdsProtocolContext);
/*      */                 }
/*      */ 
/* 2884 */                 localTdsProtocolContext._lastResult = 5;
/*      */               }
/* 2886 */               else if ((localDoneToken._status & 0x8) == 8)
/*      */               {
/* 2888 */                 setDoneCount(localDoneToken, localTdsProtocolContext);
/* 2889 */                 localTdsProtocolContext._haveDone = true;
/* 2890 */                 localTdsProtocolContext._crcCount = 0;
/*      */               }
/* 2892 */               else if ((localDoneToken._status & 0x1) == 0)
/*      */               {
/* 2895 */                 localTdsProtocolContext._lastResult = 5;
/* 2896 */                 setCount(localDoneToken, localTdsProtocolContext);
/* 2897 */                 localTdsProtocolContext._haveDone = true;
/*      */               } else {
/* 2899 */                 if ((localTdsProtocolContext._lastTds == 253) && ((localDoneToken._status & 0x1) != 0))
/*      */                 {
/* 2901 */                   if (this._crc)
/*      */                   {
/* 2903 */                     localTdsProtocolContext._crcCount += localDoneToken._count;
/* 2904 */                     localDoneToken._count = localTdsProtocolContext._crcCount;
/* 2905 */                     localTdsProtocolContext._crcCount = 0;
/*      */                   }
/*      */                   else
/*      */                   {
/* 2909 */                     localTdsProtocolContext._previousCount = localDoneToken._count;
/*      */                   }
/* 2911 */                   break label1976:
/*      */                 }
/*      */ 
/* 2914 */                 if ((localDoneToken._status & 0x40) == 0)
/*      */                 {
/* 2916 */                   localTdsProtocolContext._lastResult = 5;
/*      */                 }
/*      */                 else {
/* 2919 */                   if (this._ignoreDIP) continue; if (localTdsProtocolContext._batch)
/*      */                   {
/*      */                     continue;
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 2928 */               setDoneCount(localDoneToken, localTdsProtocolContext);
/* 2929 */               break label1976:
/*      */             }
/* 2931 */             if ((localDoneToken._status & 0x1) != 0)
/*      */             {
/* 2934 */               if ((localDoneToken._status & 0x8) != 8)
/*      */                 break label959;
/* 2936 */               if (!localTdsProtocolContext._batch)
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/* 2942 */               localTdsProtocolContext._lastResult = 5;
/* 2943 */               setDoneCount(localDoneToken, localTdsProtocolContext);
/* 2944 */               localTdsProtocolContext._crcCount = 0;
/* 2945 */               break label1976:
/*      */             }
/*      */ 
/* 2949 */             if ((localDoneToken._status & 0x8) == 8)
/*      */             {
/* 2951 */               if ((this._ignoreDIP) || (localTdsProtocolContext._batch))
/*      */               {
/* 2953 */                 localTdsProtocolContext._lastResult = 5;
/* 2954 */                 setDoneCount(localDoneToken, localTdsProtocolContext);
/*      */               }
/*      */               else
/*      */               {
/* 2958 */                 localTdsProtocolContext._lastResult = 0;
/*      */               }
/* 2960 */               localTdsProtocolContext._crcCount = 0;
/* 2961 */               localTdsProtocolContext._haveDone = true;
/* 2962 */               break label1976:
/*      */             }
/*      */ 
/* 2965 */             if ((localDoneToken._status & 0x1) == 0)
/*      */             {
/* 2971 */               localTdsProtocolContext._lastResult = 0;
/* 2972 */               setDoneCount(localDoneToken, localTdsProtocolContext);
/* 2973 */               localTdsProtocolContext._haveDone = true;
/* 2974 */               localTdsProtocolContext._crcCount = 0;
/* 2975 */               break label1976:
/*      */             }
/* 2977 */             if (localTdsProtocolContext._eed)
/*      */             {
/* 2979 */               localTdsProtocolContext._lastResult = 229;
/*      */ 
/* 2982 */               break label1976:
/*      */             }
/*      */ 
/* 2991 */             if ((!localTdsProtocolContext._batch) || (
/* 2993 */               ((localDoneToken._status & 0x8) != 8) && (((localTdsProtocolContext._lastResult != 229) || ((localDoneToken._status & 0x1) == 0)))))
/*      */             {
/*      */               continue;
/*      */             }
/*      */ 
/* 3001 */             localTdsProtocolContext._lastResult = 5;
/* 3002 */             break;
/*      */           case 238:
/* 3009 */             if (this._info.getBoolean(58))
/*      */             {
/* 3011 */               localTdsProtocolContext.newRowFmt(localTdsProtocolContext._in, 238);
/*      */             }
/*      */             else
/*      */             {
/* 3015 */               localTdsProtocolContext._paramFmts = new RowFormatToken(localTdsProtocolContext._in);
/*      */             }
/* 3017 */             localTdsProtocolContext._lastResult = 1;
/* 3018 */             break;
/*      */           case 97:
/* 3022 */             if (this._info.getBoolean(58))
/*      */             {
/* 3024 */               localTdsProtocolContext.newRowFmt(localTdsProtocolContext._in, 97);
/*      */             }
/*      */             else
/*      */             {
/* 3028 */               localTdsProtocolContext._paramFmts = new RowFormat2Token(localTdsProtocolContext._in);
/*      */             }
/* 3030 */             localTdsProtocolContext._lastResult = 1;
/* 3031 */             break;
/*      */           case 121:
/* 3036 */             if (localTdsProtocolContext._batch)
/*      */             {
/* 3038 */               slurpParams(localTdsProtocolContext);
/*      */             }
/*      */ 
/* 3041 */             localTdsProtocolContext._lastResult = 3;
/* 3042 */             break;
/*      */           case 172:
/* 3047 */             localTdsProtocolContext._lastResult = 3;
/* 3048 */             break;
/*      */           case 226:
/* 3052 */             this._capT = new CapabilityToken(localTdsProtocolContext._in);
/* 3053 */             if (!serverAcceptsTimeData())
/*      */             {
/* 3055 */               this._info.setProperty(52, "false");
/*      */             }
/* 3057 */             if (!serverPacketSize())
/*      */             {
/* 3059 */               this._info.setProperty(54, "false");
/*      */             }
/* 3061 */             if (this._capT._reqCaps.get(89))
/*      */             {
/* 3063 */               this._conn.setHAState(7);
/* 3064 */               if (!this._capT._reqCaps.get(90))
/*      */               {
/* 3066 */                 this._inStreamMgr.setSerialize();
/*      */               }
/*      */             }
/* 3069 */             localTdsProtocolContext._lastResult = -1;
/* 3070 */             break;
/*      */           case 231:
/* 3074 */             new DynamicToken(localTdsProtocolContext._in);
/* 3075 */             localTdsProtocolContext._lastResult = -1;
/* 3076 */             break;
/*      */           case 98:
/* 3080 */             new Dynamic2Token(localTdsProtocolContext._in);
/* 3081 */             localTdsProtocolContext._lastResult = -1;
/* 3082 */             break;
/*      */           case 131:
/* 3086 */             new CurInfoToken(localTdsProtocolContext);
/*      */ 
/* 3088 */             break;
/*      */           case 136:
/* 3092 */             new CurInfo3Token(localTdsProtocolContext);
/*      */ 
/* 3094 */             break;
/*      */           case 229:
/* 3098 */             localObject = new EedToken(localTdsProtocolContext._in);
/* 3099 */             localTdsProtocolContext._lastResult = 229;
/* 3100 */             if (!processEed(localTdsProtocolContext, (EedToken)localObject))
/*      */               continue;
/* 3102 */             break;
/*      */           case 202:
/* 3108 */             new KeyToken(localTdsProtocolContext._in);
/* 3109 */             break;
/*      */           case 209:
/* 3121 */             if ((localTdsProtocolContext._lastResult == 211) || ((!paramProtocolContext._batch) && (localTdsProtocolContext._dynamicFmts != null) && (((i == 231) || (i == 98)))))
/*      */             {
/* 3124 */               ungetResult(localTdsProtocolContext, 209);
/* 3125 */               localTdsProtocolContext._lastResult = 1;
/*      */             }
/*      */             else
/*      */             {
/* 3129 */               localTdsProtocolContext._lastResult = 209;
/*      */             }
/*      */ 
/* 3132 */             break;
/*      */           case 236:
/* 3136 */             if (localTdsProtocolContext._eed)
/*      */             {
/* 3138 */               localTdsProtocolContext._paramFmtsForEed = new RowFormatToken(localTdsProtocolContext._in);
/*      */             }
/* 3140 */             else if (localTdsProtocolContext._event)
/*      */             {
/* 3142 */               localTdsProtocolContext._paramFmtsForEvent = new RowFormatToken(localTdsProtocolContext._in);
/*      */             }
/*      */             else
/*      */             {
/* 3147 */               localTdsProtocolContext._paramFmts = new RowFormatToken(localTdsProtocolContext._in);
/*      */             }
/* 3149 */             localTdsProtocolContext._lastResult = 3;
/* 3150 */             break;
/*      */           case 32:
/* 3154 */             if (localTdsProtocolContext._eed)
/*      */             {
/* 3156 */               localTdsProtocolContext._paramFmtsForEed = new ParamFormat2Token(localTdsProtocolContext._in);
/*      */             }
/* 3158 */             else if (localTdsProtocolContext._event)
/*      */             {
/* 3160 */               localTdsProtocolContext._paramFmtsForEvent = new ParamFormat2Token(localTdsProtocolContext._in);
/*      */             }
/*      */             else
/*      */             {
/* 3165 */               localTdsProtocolContext._paramFmts = new ParamFormat2Token(localTdsProtocolContext._in);
/*      */             }
/* 3167 */             localTdsProtocolContext._lastResult = 3;
/* 3168 */             break;
/*      */           case 215:
/* 3173 */             localTdsProtocolContext._lastResult = 3;
/* 3174 */             break;
/*      */           case 227:
/* 3177 */             EnvChangeToken localEnvChangeToken = new EnvChangeToken(localTdsProtocolContext._in);
/* 3178 */             int k = localEnvChangeToken.getEnvType();
/*      */ 
/* 3180 */             switch (k)
/*      */             {
/*      */             case 1:
/* 3183 */               this._catalog = localEnvChangeToken._newValue;
/* 3184 */               break;
/*      */             case 4:
/* 3187 */               if (localEnvChangeToken._newValue != null)
/*      */               {
/* 3191 */                 this._packetSize = Integer.valueOf(localEnvChangeToken._newValue).intValue(); } break;
/*      */             case 3:
/* 3197 */               if (localEnvChangeToken._newValue != null)
/*      */               {
/* 3199 */                 String str1 = getCharsetMapping(localEnvChangeToken._newValue);
/* 3200 */                 if (str1 != null)
/*      */                 {
/* 3204 */                   this._serverDefaultCharsetName = localEnvChangeToken._newValue;
/*      */                 }
/*      */ 
/* 3207 */                 String str2 = this._charsetName;
/* 3208 */                 if (localEnvChangeToken._oldValue == null)
/*      */                 {
/* 3210 */                   this._isUtf8OrServerCharset = ((str2 == null) || (str2.equalsIgnoreCase("utf8")) || (str2.equalsIgnoreCase("x-SybUTF8")));
/*      */                 }
/*      */                 else
/*      */                 {
/* 3216 */                   this._isUtf8OrServerCharset = ((str2 == null) || (str2.equalsIgnoreCase("utf8")) || (str2.equalsIgnoreCase("x-SybUTF8")) || (str2.equalsIgnoreCase(getCharsetMapping(localEnvChangeToken._oldValue))));
/*      */                 }
/*      */ 
/* 3221 */                 if ((str1 != null) && (!str1.equals(str2)))
/*      */                 {
/* 3224 */                   setCharConvert(localEnvChangeToken._newValue, false);
/*      */ 
/* 3229 */                   if (str2 != null)
/*      */                   {
/*      */                     try
/*      */                     {
/* 3233 */                       ErrorMessage.raiseWarning("010TP", str2, str1);
/*      */                     }
/*      */                     catch (SQLException localSQLException)
/*      */                     {
/* 3239 */                       localTdsProtocolContext.chainException(localSQLException);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */ 
/*      */             case 2:
/*      */             }
/*      */ 
/* 3246 */             localTdsProtocolContext._lastResult = -1;
/* 3247 */             break;
/*      */           case 162:
/* 3252 */             localTdsProtocolContext._lastResult = 3;
/* 3253 */             break;
/*      */           case 168:
/* 3257 */             localTdsProtocolContext.addAltFmtToken(localTdsProtocolContext._in);
/*      */           case 211:
/*      */           case 173:
/*      */           case 101:
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3262 */         localTdsProtocolContext.setCurrentAltRow(localTdsProtocolContext._in);
/*      */ 
/* 3268 */         if (localTdsProtocolContext._lastResult == 211)
/*      */         {
/* 3270 */           ungetResult(localTdsProtocolContext, 211);
/*      */         }
/* 3272 */         localTdsProtocolContext._lastResult = 6;
/* 3273 */         break label1976:
/*      */ 
/* 3277 */         localTdsProtocolContext._lastResult = 173;
/* 3278 */         break label1976:
/*      */ 
/* 3282 */         localTdsProtocolContext._lastResult = 101;
/* 3283 */         break label1976:
/*      */ 
/* 3291 */         label1976: new Slurp(localTdsProtocolContext._in, localTdsProtocolContext._lastTds);
/*      */       }
/*      */       catch (IOException j)
/*      */       {
/* 3303 */         if (localTdsProtocolContext._chainedSqe != null)
/*      */         {
/* 3305 */           handleIOE(localIOException2, localTdsProtocolContext._chainedSqe);
/*      */         }
/*      */         else
/*      */         {
/* 3309 */           handleIOE(localIOException2);
/*      */         }
/*      */ 
/* 3315 */         if ((localTdsProtocolContext._chainedSqe != null) && (!localTdsProtocolContext._eed))
/*      */         {
/* 3319 */           int j = localTdsProtocolContext._lastTds;
/*      */ 
/* 3322 */           if (j != 229)
/*      */           {
/* 3326 */             ungetResult(localTdsProtocolContext, localTdsProtocolContext._lastResult);
/*      */           }
/* 3328 */           localObject = localTdsProtocolContext._chainedSqe;
/* 3329 */           localTdsProtocolContext._chainedSqe = null;
/* 3330 */           throw ((Throwable)localObject);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3335 */     return localTdsProtocolContext._lastResult;
/*      */   }
/*      */ 
/*      */   private void setCount(DoneToken paramDoneToken, TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 3345 */     if (this._crc)
/*      */     {
/* 3347 */       paramTdsProtocolContext._previousCount = (paramTdsProtocolContext._crcCount += paramDoneToken._count);
/*      */     }
/*      */     else
/*      */     {
/* 3351 */       paramTdsProtocolContext._previousCount = paramDoneToken._count;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setDoneCount(DoneToken paramDoneToken, TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 3359 */     if (this._crc)
/*      */     {
/* 3361 */       paramDoneToken._count = paramTdsProtocolContext._crcCount;
/* 3362 */       if (paramTdsProtocolContext._lastTds != 253)
/*      */         return;
/* 3364 */       paramTdsProtocolContext._crcCount = 0;
/*      */     }
/*      */     else
/*      */     {
/* 3369 */       paramDoneToken._count = paramTdsProtocolContext._previousCount;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ungetResult(ProtocolContext paramProtocolContext, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3379 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/*      */ 
/* 3382 */     localTdsProtocolContext._ungotResult = paramInt;
/*      */   }
/*      */ 
/*      */   public int count(ProtocolContext paramProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 3391 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/*      */ 
/* 3397 */     localTdsProtocolContext._lastResult = -1;
/*      */ 
/* 3399 */     return ((DoneToken)localTdsProtocolContext._tdsToken)._count;
/*      */   }
/*      */ 
/*      */   public void param(ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 3409 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramParamManager.getContext();
/*      */ 
/* 3412 */     int i = paramParamManager.getNext();
/* 3413 */     Param[] arrayOfParam = paramParamManager.getParams();
/*      */ 
/* 3416 */     localTdsProtocolContext._lastResult = -1;
/* 3417 */     TdsJdbcInputStream localTdsJdbcInputStream = null;
/*      */     try
/*      */     {
/* 3420 */       switch (localTdsProtocolContext._lastTds)
/*      */       {
/*      */       case 121:
/* 3423 */         localTdsJdbcInputStream = new TdsJdbcInputStream(null, localTdsProtocolContext, this);
/* 3424 */         TdsParam localTdsParam = null;
/* 3425 */         if ((arrayOfParam.length == 0) || (arrayOfParam[0]._sqlType != -998))
/*      */         {
/* 3429 */           localTdsParam = new TdsParam(this._out);
/* 3430 */           localTdsParam._sqlType = -998;
/*      */         }
/*      */         else
/*      */         {
/* 3434 */           localTdsParam = (TdsParam)arrayOfParam[0];
/*      */         }
/* 3436 */         localTdsParam._outValue = localTdsJdbcInputStream;
/* 3437 */         localTdsJdbcInputStream._dataFmt = new DataFormat(localTdsParam, this._out, 4);
/* 3438 */         paramParamManager.registerStatus(localTdsJdbcInputStream);
/* 3439 */         break;
/*      */       case 172:
/* 3442 */         localTdsProtocolContext._in.readUnsignedShort();
/* 3443 */         localTdsJdbcInputStream = new TdsJdbcInputStream(null, localTdsProtocolContext, this);
/* 3444 */         localTdsJdbcInputStream._dataFmt = new DataFormat(localTdsProtocolContext._in, false);
/* 3445 */         if ((localTdsJdbcInputStream._dataFmt._status & 0x1) == 1)
/*      */         {
/* 3448 */           if (paramParamManager.getParamSetType() == 1)
/*      */           {
/* 3450 */             i = paramParamManager.nextOutParam(localTdsJdbcInputStream._dataFmt.getName());
/* 3451 */             if (i < 0)
/*      */             {
/* 3453 */               ErrorMessage.raiseError("JZ0P4");
/*      */             }
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 3459 */             i = paramParamManager.nextOutParam();
/*      */           }
/* 3461 */           arrayOfParam[i]._outValue = localTdsJdbcInputStream;
/*      */         }
/* 3463 */         paramParamManager.registerParam(localTdsJdbcInputStream);
/* 3464 */         break;
/*      */       case 32:
/*      */       case 236:
/* 3469 */         break;
/*      */       case 215:
/* 3473 */         int j = 0;
/*      */         while (true) { if (j >= localTdsProtocolContext._paramFmts._numColumns)
/*      */             break label421;
/* 3475 */           localTdsJdbcInputStream = new TdsJdbcInputStream(null, localTdsProtocolContext, this);
/* 3476 */           localTdsJdbcInputStream._dataFmt = localTdsProtocolContext._paramFmts.getDataFormat(j++);
/* 3477 */           if ((localTdsJdbcInputStream._dataFmt._status & 0x1) == 1)
/*      */           {
/* 3480 */             if (paramParamManager.getParamSetType() == 1)
/*      */             {
/* 3482 */               i = paramParamManager.nextOutParam(localTdsJdbcInputStream._dataFmt.getName());
/* 3483 */               if (i < 0)
/*      */               {
/* 3485 */                 ErrorMessage.raiseError("JZ0P4");
/*      */               }
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 3491 */               i = paramParamManager.nextOutParam();
/*      */             }
/* 3493 */             if (i < arrayOfParam.length)
/*      */             {
/* 3495 */               arrayOfParam[i]._outValue = localTdsJdbcInputStream;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 3503 */           paramParamManager.registerParam(localTdsJdbcInputStream); }
/*      */ 
/*      */       default:
/* 3507 */         ErrorMessage.raiseError("JZ0P4");
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 3512 */       label421: handleIOE(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ProtocolResultSet resultSet(ProtocolContext paramProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 3523 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/* 3524 */     if (this._info.getBoolean(58))
/*      */     {
/* 3526 */       return localTdsProtocolContext.newResultSet();
/*      */     }
/*      */ 
/* 3533 */     if (localTdsProtocolContext._cursor != null)
/*      */     {
/* 3535 */       if ((localTdsProtocolContext._cursor._type & 0x120) != 0)
/*      */       {
/* 3537 */         localTdsProtocolContext._trs = new TdsScrollResultSet(localTdsProtocolContext);
/*      */       }
/*      */       else
/*      */       {
/* 3541 */         localTdsProtocolContext._trs = new TdsResultSet(localTdsProtocolContext);
/*      */       }
/*      */ 
/*      */     }
/*      */     else {
/* 3546 */       localTdsProtocolContext._trs = new TdsResultSet(localTdsProtocolContext);
/*      */     }
/* 3548 */     localTdsProtocolContext._lastResult = -1;
/* 3549 */     return localTdsProtocolContext._trs;
/*      */   }
/*      */ 
/*      */   public Param[] paramArray(ProtocolContext paramProtocolContext, int paramInt)
/*      */   {
/* 3559 */     Object localObject = null;
/* 3560 */     int i = 1;
/* 3561 */     if ((paramInt * 136 > 65535) && 
/* 3567 */       (isWidetableEnabled()))
/*      */     {
/* 3574 */       localObject = new TdsParam2[paramInt];
/* 3575 */       for (int j = 0; j < paramInt; ++j)
/*      */       {
/* 3577 */         localObject[j] = new TdsParam2(this._out);
/*      */       }
/* 3579 */       i = 0;
/*      */     }
/*      */ 
/* 3582 */     if (i != 0)
/*      */     {
/* 3596 */       localObject = new TdsParam[paramInt];
/* 3597 */       for (int k = 0; k < paramInt; ++k)
/*      */       {
/* 3599 */         localObject[k] = new TdsParam(this._out);
/*      */       }
/*      */     }
/* 3602 */     return (Param)localObject;
/*      */   }
/*      */ 
/*      */   public Param[] paramArray(int paramInt, CacheManager paramCacheManager)
/*      */   {
/* 3611 */     TdsUpdateParam[] arrayOfTdsUpdateParam = new TdsUpdateParam[paramInt];
/* 3612 */     for (int i = 0; i < paramInt; ++i)
/*      */     {
/* 3614 */       arrayOfTdsUpdateParam[i] = new TdsUpdateParam(this._out, paramCacheManager);
/*      */     }
/* 3616 */     return arrayOfTdsUpdateParam;
/*      */   }
/*      */ 
/*      */   public Param getParam()
/*      */   {
/* 3621 */     return new TdsParam();
/*      */   }
/*      */ 
/*      */   public ProtocolContext getProtocolContext(SybProperty paramSybProperty)
/*      */   {
/* 3632 */     TdsProtocolContext localTdsProtocolContext = new TdsProtocolContext(null, this, this._inStreamMgr, this._outStreamMgr);
/*      */ 
/* 3634 */     localTdsProtocolContext._conn = this._conn;
/*      */     try
/*      */     {
/* 3638 */       localTdsProtocolContext._rereadable = paramSybProperty.getBoolean(14);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 3643 */       localTdsProtocolContext._rereadable = true;
/*      */     }
/* 3645 */     if (localTdsProtocolContext._rereadable)
/*      */     {
/* 3647 */       localTdsProtocolContext.setRereadable();
/*      */     }
/*      */     else
/*      */     {
/* 3651 */       localTdsProtocolContext._cm = null;
/*      */     }
/*      */ 
/* 3658 */     if (this._haContext.wasHARequested())
/*      */     {
/* 3662 */       this._storeTPC.put(localTdsProtocolContext, localTdsProtocolContext.toString());
/*      */     }
/* 3664 */     return localTdsProtocolContext;
/*      */   }
/*      */ 
/*      */   private synchronized void refreshTPC()
/*      */   {
/* 3669 */     Iterator localIterator = this._storeTPC.keySet().iterator();
/* 3670 */     while (localIterator.hasNext())
/*      */     {
/* 3673 */       TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)localIterator.next();
/*      */ 
/* 3675 */       localTdsProtocolContext.refreshYourself(this, this._inStreamMgr, this._outStreamMgr);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeProtocolContext(ProtocolContext paramProtocolContext)
/*      */   {
/* 3690 */     if (!this._isHAConn)
/*      */     {
/* 3692 */       return;
/*      */     }
/* 3694 */     Object localObject = this._storeTPC.remove(paramProtocolContext);
/*      */   }
/*      */ 
/*      */   public void dump(SyncObj paramSyncObj1, SyncObj paramSyncObj2)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void makeEventContext(String paramString, SybEventHandler paramSybEventHandler, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3745 */     if ((paramString == null) || (!paramSybEventHandler instanceof SybEventHandler))
/*      */     {
/* 3747 */       ErrorMessage.raiseError("JZ0H0", paramString);
/*      */     }
/*      */ 
/* 3750 */     this._inStreamMgr.startAsync();
/*      */ 
/* 3752 */     this._eventCtx.addHandler(paramString, paramSybEventHandler, paramInt);
/*      */   }
/*      */ 
/*      */   public void killEventContext(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3761 */     this._eventCtx.dropHandler(paramString);
/*      */   }
/*      */ 
/*      */   public Cursor getCursor(ProtocolContext paramProtocolContext, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 3772 */     paramBoolean = this._info.getBoolean(27);
/* 3773 */     if (!paramBoolean)
/*      */     {
/* 3775 */       paramBoolean = !this._capT._reqCaps.get(6);
/*      */     }
/* 3777 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/* 3778 */     if (!localTdsProtocolContext._rereadable)
/*      */     {
/* 3780 */       localTdsProtocolContext._rereadable = true;
/* 3781 */       localTdsProtocolContext.setRereadable();
/*      */     }
/* 3783 */     return new TdsCursor(this, localTdsProtocolContext, paramBoolean, getProtocolContext(this._info));
/*      */   }
/*      */ 
/*      */   protected int getDoneResult(TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 3792 */     int i = 0;
/*      */     while (true)
/*      */     {
/* 3795 */       switch (nextResult(paramTdsProtocolContext))
/*      */       {
/*      */       case 5:
/* 3799 */         i = ((DoneToken)paramTdsProtocolContext._tdsToken)._count;
/*      */ 
/* 3803 */         noop();
/*      */       case 0:
/* 3805 */         if (paramTdsProtocolContext._haveDone == true);
/* 3807 */         return i;
/*      */       }
/*      */ 
/* 3811 */       ErrorMessage.raiseError("JZ0P1");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected SybProperty getSybProperty()
/*      */   {
/* 3823 */     return this._info;
/*      */   }
/*      */ 
/*      */   protected int noop()
/*      */   {
/* 3830 */     return 1;
/*      */   }
/*      */ 
/*      */   public int getHadrTransactionState()
/*      */   {
/* 3835 */     return this._hadrTransactionState;
/*      */   }
/*      */ 
/*      */   public void setHadrTransactionState(int paramInt)
/*      */   {
/* 3840 */     this._hadrTransactionState = paramInt;
/*      */   }
/*      */ 
/*      */   public boolean isReceivedHadrFailover()
/*      */   {
/* 3845 */     return this._recievedHadrFailover;
/*      */   }
/*      */ 
/*      */   public void setReceivedHadrFailover(boolean paramBoolean)
/*      */   {
/* 3850 */     this._recievedHadrFailover = paramBoolean;
/*      */   }
/*      */ 
/*      */   public String getHadrLatestPrimaryHostPort()
/*      */   {
/* 3855 */     return this._hadrLatestPrimaryHostPort;
/*      */   }
/*      */ 
/*      */   protected boolean getResultSetResult(TdsProtocolContext paramTdsProtocolContext, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 3866 */     int i = 0;
/*      */     while (true)
/*      */     {
/* 3870 */       switch (nextResult(paramTdsProtocolContext))
/*      */       {
/*      */       case 1:
/* 3873 */         i = 1;
/* 3874 */         if (!paramBoolean) {
/*      */           break;
/*      */         }
/* 3877 */         paramTdsProtocolContext._lastResult = -1;
/* 3878 */         break;
/*      */       case 0:
/*      */       case 5:
/* 3883 */         break;
/*      */       default:
/* 3887 */         ErrorMessage.raiseError("JZ0P1");
/*      */       }
/*      */     }
/*      */ 
/* 3891 */     return i;
/*      */   }
/*      */ 
/*      */   private boolean processEed(TdsProtocolContext paramTdsProtocolContext, EedToken paramEedToken)
/*      */     throws SQLException
/*      */   {
/* 3902 */     Object localObject = null;
/* 3903 */     for (int i = 0; i < INFO_MSGNO.length; ++i)
/*      */     {
/* 3905 */       if (paramEedToken._msgNumber == INFO_MSGNO[i])
/*      */       {
/* 3909 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3914 */     if (((this._haContext.getLogin() & 0x8) == 0) && (paramEedToken._msgNumber == 1))
/*      */     {
/* 3917 */       return false;
/*      */     }
/*      */ 
/* 3920 */     paramTdsProtocolContext._eed = true;
/* 3921 */     if (paramEedToken._sqlState != null)
/*      */     {
/* 3923 */       i = paramEedToken._sqlState.length();
/* 3924 */       if (i > 5)
/*      */       {
/* 3927 */         paramEedToken._sqlState = paramEedToken._sqlState.substring(0, 5);
/*      */       }
/* 3929 */       else if (i < 5)
/*      */       {
/* 3932 */         paramEedToken._sqlState += "     ".substring(0, 5 - i);
/*      */       }
/*      */     }
/*      */ 
/* 3936 */     i = ((paramEedToken._status & 0x1) == 1) ? 1 : 0;
/*      */ 
/* 3938 */     if (i != 0)
/*      */     {
/* 3940 */       paramEedToken._params = paramTdsProtocolContext.getParams();
/*      */     }
/*      */     else
/*      */     {
/* 3944 */       paramEedToken._params = paramTdsProtocolContext.makeEmptyParams();
/*      */     }
/*      */ 
/* 3947 */     if (paramEedToken._msgNumber == 2376)
/*      */     {
/* 3949 */       setReceivedHadrFailover(true);
/*      */     }
/*      */ 
/* 3952 */     if (paramEedToken._msgNumber == 2379)
/*      */     {
/* 3954 */       setHadrTransactionState(paramEedToken._tranState);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 3959 */       if (paramEedToken._msgNumber == 1)
/*      */       {
/* 3963 */         extractHostPortList(paramEedToken._params);
/*      */ 
/* 3965 */         this._redirectImmed = ((paramEedToken._state & 0x1) == 1);
/*      */ 
/* 3967 */         this._redirectBitOn = ((paramEedToken._state & 0x2) == 2);
/*      */ 
/* 3969 */         if (this._inLogin)
/*      */         {
/* 3971 */           this._conn.setHAState(7);
/*      */         }
/* 3973 */         paramEedToken._params.close();
/* 3974 */         paramTdsProtocolContext._eed = false;
/* 3975 */         if ((this._redirectImmed) && (!this._inLogin))
/*      */         {
/* 3977 */           this._conn.markDeadTryHA();
/*      */         }
/* 3979 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException1)
/*      */     {
/* 3985 */       ErrorMessage.raiseError("JZ0P4");
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 3989 */       handleIOE(localIOException);
/*      */     }
/*      */ 
/* 3996 */     if ((paramEedToken._class == 10) || (paramEedToken._msgNumber == 0) || (paramEedToken._msgNumber == 16511) || (this._inLogin))
/*      */     {
/* 4001 */       if ((paramEedToken._sqlState != null) && 
/* 4003 */         (!paramEedToken._sqlState.regionMatches(false, 0, "01", 0, 2)))
/*      */       {
/* 4005 */         paramEedToken._sqlState = ("01" + paramEedToken._sqlState.substring(2, 5));
/*      */       }
/*      */ 
/* 4008 */       localObject = new SybSQLWarning(paramEedToken._msg, paramEedToken._sqlState, paramEedToken._msgNumber, paramEedToken._state, paramEedToken._class, paramEedToken._serverName, paramEedToken._procName, paramEedToken._lineNum, paramEedToken._params, paramEedToken._tranState, paramEedToken._status);
/*      */     }
/*      */     else
/*      */     {
/* 4014 */       localObject = new SybSQLException(paramEedToken._msg, paramEedToken._sqlState, paramEedToken._msgNumber, paramEedToken._state, paramEedToken._class, paramEedToken._serverName, paramEedToken._procName, paramEedToken._lineNum, paramEedToken._params, paramEedToken._tranState, paramEedToken._status);
/*      */     }
/*      */ 
/* 4019 */     SybMessageHandler localSybMessageHandler = paramTdsProtocolContext.getMessageHandler();
/* 4020 */     if (localSybMessageHandler != null)
/*      */     {
/* 4022 */       localObject = localSybMessageHandler.messageHandler((SQLException)localObject);
/*      */     }
/*      */ 
/* 4025 */     if (i != 0)
/*      */     {
/*      */       try
/*      */       {
/* 4029 */         paramEedToken._params.close();
/*      */       }
/*      */       catch (SQLException localSQLException2)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4038 */     if (localObject != null)
/*      */     {
/* 4040 */       paramTdsProtocolContext.chainException((SQLException)localObject);
/*      */     }
/*      */ 
/* 4047 */     paramTdsProtocolContext._eed = false;
/*      */ 
/* 4051 */     if (paramTdsProtocolContext instanceof TdsMigrateContext)
/*      */     {
/* 4056 */       paramTdsProtocolContext.close(true);
/*      */ 
/* 4059 */       return true;
/*      */     }
/*      */ 
/* 4062 */     return paramTdsProtocolContext._lastResult == 0;
/*      */   }
/*      */ 
/*      */   protected void extractHostPortList(SybResultSet paramSybResultSet)
/*      */     throws SQLException
/*      */   {
/* 4073 */     this._redirectHostPort = new Vector();
/* 4074 */     ResultSetMetaData localResultSetMetaData = paramSybResultSet.getMetaData();
/* 4075 */     for (int i = 1; i <= localResultSetMetaData.getColumnCount(); ++i)
/*      */     {
/* 4077 */       String str = paramSybResultSet.getString(i);
/*      */ 
/* 4079 */       String[] arrayOfString = str.split(" ");
/*      */ 
/* 4081 */       if ((arrayOfString.length != 3) || (arrayOfString[0].compareTo("tcp") != 0))
/*      */         continue;
/* 4083 */       this._redirectHostPort.addElement(arrayOfString[1] + ":" + arrayOfString[2]);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void extractHADRMAP(SybResultSet paramSybResultSet)
/*      */     throws SQLException
/*      */   {
/* 4094 */     this._hadrListMap.clear();
/*      */ 
/* 4096 */     int i = 1;
/* 4097 */     int j = 0;
/*      */ 
/* 4101 */     ResultSetMetaData localResultSetMetaData = paramSybResultSet.getMetaData();
/* 4102 */     int k = localResultSetMetaData.getColumnCount();
/*      */ 
/* 4106 */     this._hadrListMap.put("GroupName", paramSybResultSet.getString(i));
/* 4107 */     ++i;
/*      */ 
/* 4110 */     this._hadrListMap.put("GenerationNumber", paramSybResultSet.getString(i));
/* 4111 */     ++i;
/*      */ 
/* 4115 */     String str1 = localResultSetMetaData.getColumnName(i);
/*      */ 
/* 4117 */     while ((i <= k) && (str1.compareToIgnoreCase("Data Source Name") == 0))
/*      */     {
/* 4122 */       Properties localProperties = new Properties();
/*      */ 
/* 4124 */       LinkedList localLinkedList1 = new LinkedList();
/* 4125 */       LinkedList localLinkedList2 = new LinkedList();
/* 4126 */       int l = 0;
/*      */ 
/* 4129 */       String str2 = paramSybResultSet.getString(i);
/* 4130 */       ++i;
/*      */ 
/* 4134 */       str1 = localResultSetMetaData.getColumnName(i);
/*      */ 
/* 4137 */       while ((i <= k) && (str1.compareToIgnoreCase("Data Source Address") == 0))
/*      */       {
/* 4140 */         String str3 = paramSybResultSet.getString(i);
/* 4141 */         localLinkedList1.add(str3);
/*      */ 
/* 4144 */         if ((i == 4) && (j == 0))
/*      */         {
/* 4146 */           String[] arrayOfString = str3.split(" ");
/*      */ 
/* 4150 */           this._hadrLatestPrimaryHostPort = (arrayOfString[1].trim() + ":" + arrayOfString[2].trim());
/*      */         }
/*      */ 
/* 4154 */         ++i;
/* 4155 */         if (i > k) {
/*      */           break;
/*      */         }
/*      */ 
/* 4159 */         str1 = localResultSetMetaData.getColumnName(i);
/*      */       }
/*      */ 
/* 4163 */       while ((i <= k) && (str1.compareToIgnoreCase("HA Failover") == 0))
/*      */       {
/* 4166 */         localLinkedList2.add(paramSybResultSet.getString(i));
/* 4167 */         ++i;
/* 4168 */         if (i > k) {
/*      */           break;
/*      */         }
/*      */ 
/* 4172 */         str1 = localResultSetMetaData.getColumnName(i);
/*      */       }
/*      */ 
/* 4175 */       if ((i <= k) && (str1.compareToIgnoreCase("flag") == 0))
/*      */       {
/* 4178 */         l = paramSybResultSet.getInt(i);
/* 4179 */         ++i;
/* 4180 */         if (i < k)
/*      */         {
/* 4182 */           str1 = localResultSetMetaData.getColumnName(i);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4188 */       localProperties.put("DataSourceName", str2);
/* 4189 */       localProperties.put("AddressList", localLinkedList1);
/* 4190 */       localProperties.put("HAFailoverList", localLinkedList2);
/* 4191 */       localProperties.put("Flag", new Integer(l));
/*      */ 
/* 4194 */       if (j == 0)
/*      */       {
/* 4196 */         this._hadrListMap.put("Primary", localProperties);
/*      */       }
/*      */       else
/*      */       {
/* 4200 */         this._hadrListMap.put("Standby_" + j, localProperties);
/*      */       }
/* 4202 */       ++j;
/*      */     }
/*      */   }
/*      */ 
/*      */   public LinkedHashMap getHADRListMap()
/*      */   {
/* 4208 */     return this._hadrListMap;
/*      */   }
/*      */ 
/*      */   protected TdsProtocolContext sendMigrateMsg(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 4219 */     checkBcpWithMixStmt();
/*      */ 
/* 4222 */     TdsProtocolContext localTdsProtocolContext1 = (TdsProtocolContext)getProtocolContext(this._info);
/*      */ 
/* 4225 */     TdsProtocolContext localTdsProtocolContext2 = (TdsProtocolContext)this._outStreamMgr.currentContext();
/*      */ 
/* 4228 */     if (localTdsProtocolContext2 != null)
/*      */     {
/* 4230 */       localTdsProtocolContext1.setSponsor(localTdsProtocolContext2);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 4235 */       this._outStreamMgr.getSendLock(localTdsProtocolContext1);
/* 4236 */       this._outStreamMgr.beginRequestForMigration(localTdsProtocolContext1);
/* 4237 */       MsgToken localMsgToken = new MsgToken(0, (short)paramInt);
/* 4238 */       this._outFormat.setPDUHeader(17, 1);
/* 4239 */       localMsgToken.send(this._out);
/* 4240 */       this._out.flush();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 4244 */       handleIOE(localIOException);
/*      */     }
/*      */     finally
/*      */     {
/* 4250 */       this._outStreamMgr.abortRequest(localTdsProtocolContext1);
/* 4251 */       localTdsProtocolContext1.close(true);
/* 4252 */       this._outFormat.setPDUHeader(15, 0);
/*      */     }
/* 4254 */     return localTdsProtocolContext2;
/*      */   }
/*      */ 
/*      */   protected void migrate()
/*      */   {
/* 4260 */     InStreamMgr localInStreamMgr = this._inStreamMgr;
/* 4261 */     PduOutputFormatter localPduOutputFormatter = this._outFormat;
/* 4262 */     TdsDataOutputStream localTdsDataOutputStream = this._out;
/* 4263 */     OutStreamMgr localOutStreamMgr = this._outStreamMgr;
/*      */ 
/* 4265 */     this._conn.setHAState(9);
/*      */     try
/*      */     {
/* 4269 */       this._conn.markDeadTryHA();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 4273 */       if (localIOException instanceof SybHAException)
/*      */       {
/* 4275 */         localInStreamMgr.migrateDbio(this._inStreamMgr);
/* 4276 */         localPduOutputFormatter.changeOutput(localInStreamMgr.getOutputStream());
/*      */       }
/* 4278 */       this._inStreamMgr = localInStreamMgr;
/* 4279 */       this._outFormat = localPduOutputFormatter;
/* 4280 */       this._out = localTdsDataOutputStream;
/* 4281 */       this._outStreamMgr = localOutStreamMgr;
/* 4282 */       this._inStreamMgr._migrating = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void doCommand(String paramString)
/*      */     throws SQLException
/*      */   {
/* 4295 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)getProtocolContext(this._info);
/*      */ 
/* 4297 */     language(localTdsProtocolContext, paramString, null);
/* 4298 */     readCommandResults(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   protected void doCommand(LanguageToken paramLanguageToken)
/*      */     throws SQLException
/*      */   {
/* 4311 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)getProtocolContext(this._info);
/*      */ 
/* 4313 */     language(localTdsProtocolContext, paramLanguageToken);
/* 4314 */     readCommandResults(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   private void readCommandResults(TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 4324 */     int i = -1;
/*      */     do
/*      */     {
/*      */       try
/*      */       {
/* 4329 */         i = nextResult(paramTdsProtocolContext);
/* 4330 */         switch (i)
/*      */         {
/*      */         case 3:
/* 4333 */           break;
/*      */         case 1:
/*      */         case 209:
/* 4339 */           cancel(paramTdsProtocolContext, false);
/* 4340 */           ErrorMessage.raiseError("JZ0P1");
/*      */ 
/* 4342 */           return;
/*      */         }
/*      */       }
/*      */       catch (SQLWarning localSQLWarning)
/*      */       {
/*      */       }
/*      */     }
/*      */ 
/* 4350 */     while (i != 0);
/*      */ 
/* 4358 */     paramTdsProtocolContext.drop();
/*      */   }
/*      */ 
/*      */   protected void sendParamStream(ParamManager paramParamManager, TdsDataOutputStream paramTdsDataOutputStream)
/*      */     throws IOException, SQLException
/*      */   {
/* 4373 */     checkBcpWithMixStmt();
/*      */ 
/* 4375 */     if (paramParamManager == null) return;
/* 4376 */     if (paramParamManager.hasInParams())
/*      */     {
/* 4381 */       Object localObject = null;
/* 4382 */       Param[] arrayOfParam = paramParamManager.getParams();
/*      */ 
/* 4385 */       if (arrayOfParam[0] instanceof TdsParam2)
/*      */       {
/* 4395 */         localObject = new ParamFormat2Token(paramParamManager, false);
/*      */       }
/*      */       else
/*      */       {
/* 4399 */         localObject = new ParamFormatToken(paramParamManager, false);
/*      */       }
/*      */ 
/* 4406 */       ((ParamFormatToken)localObject).send(paramTdsDataOutputStream);
/* 4407 */       ParamsToken localParamsToken = new ParamsToken();
/* 4408 */       localParamsToken.send(paramTdsDataOutputStream);
/*      */     }
/*      */ 
/* 4411 */     paramParamManager.send(paramTdsDataOutputStream);
/*      */   }
/*      */ 
/*      */   private void adjustMaxRows(TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 4424 */     if (this._adjustingMaxRows)
/*      */     {
/* 4426 */       return;
/*      */     }
/* 4428 */     this._adjustingMaxRows = true;
/*      */ 
/* 4430 */     PreparedStatement localPreparedStatement = null;
/*      */     try
/*      */     {
/* 4434 */       if (this._maxRows != paramTdsProtocolContext._maxRows)
/*      */       {
/* 4436 */         if ((this._info.getString(57).equalsIgnoreCase("ASE")) || (this._capT._reqCaps.get(102)))
/*      */         {
/* 4442 */           int i = paramTdsProtocolContext._maxRows;
/* 4443 */           if (this._capT._reqCaps.get(102))
/*      */           {
/* 4445 */             i = 0 - i;
/*      */           }
/* 4447 */           OptionCmdToken localOptionCmdToken = new OptionCmdToken();
/* 4448 */           localOptionCmdToken.setOption(5, i);
/* 4449 */           localOptionCmdToken.send(this._out);
/*      */         }
/*      */         else
/*      */         {
/* 4453 */           MdaManager localMdaManager = this._conn.getMDA(paramTdsProtocolContext);
/* 4454 */           localPreparedStatement = localMdaManager.getMetaDataAccessor("SET_ROWCOUNT", paramTdsProtocolContext);
/* 4455 */           localPreparedStatement.setInt(1, paramTdsProtocolContext._maxRows);
/* 4456 */           localPreparedStatement.executeUpdate();
/* 4457 */           localPreparedStatement.close();
/*      */         }
/*      */ 
/* 4461 */         this._maxRows = paramTdsProtocolContext._maxRows;
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 4466 */       handleIOE(localIOException);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 4472 */       throw localSQLException;
/*      */     }
/*      */     finally
/*      */     {
/* 4476 */       this._adjustingMaxRows = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharConvert(SybProperty paramSybProperty)
/*      */     throws SQLException
/*      */   {
/* 4486 */     String str = paramSybProperty.getString(8);
/* 4487 */     setCharConvert(str, true);
/*      */   }
/*      */ 
/*      */   public void setCharConvert(String paramString, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 4493 */     if (this._charsetConverter == null)
/*      */     {
/* 4496 */       String str = this._info.getString(21);
/*      */       try
/*      */       {
/* 4500 */         Class localClass = Class.forName(str);
/*      */ 
/* 4503 */         this._charsetConverter = ((CharsetConverter)localClass.newInstance());
/* 4504 */         this._usingCheckingConverter = ((this._charsetConverter instanceof NioConverter) || (this._charsetConverter instanceof CheckPureConverter));
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/* 4510 */         ErrorMessage.raiseError("JZ0I6", localException.getMessage());
/*      */       }
/*      */     }
/*      */ 
/* 4514 */     if (paramString != null)
/*      */     {
/* 4516 */       this._charsetName = getCharsetMapping(paramString);
/*      */     }
/*      */     else
/*      */     {
/* 4528 */       this._charsetName = null;
/*      */     }
/* 4530 */     if (this._charsetName != null)
/*      */     {
/*      */       try
/*      */       {
/* 4534 */         this._charsetConverter.setEncoding(this._charsetName);
/*      */       }
/*      */       catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*      */       {
/* 4538 */         if (paramBoolean)
/*      */         {
/* 4540 */           ErrorMessage.raiseError("JZ0I5", paramString + "\nThe client charset is set to : " + this._charsetName + "\n" + localUnsupportedEncodingException.getLocalizedMessage());
/*      */         }
/*      */         else
/*      */         {
/* 4545 */           ErrorMessage.raiseError("JZ0IB", paramString + "\nThe client charset is set to : " + this._charsetName + "\n" + localUnsupportedEncodingException.getLocalizedMessage());
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException)
/*      */       {
/* 4564 */         if (paramBoolean)
/*      */         {
/* 4566 */           ErrorMessage.raiseError("JZ0I5", paramString);
/*      */         }
/*      */         else
/*      */         {
/* 4571 */           ErrorMessage.raiseError("JZ0IB", paramString);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4578 */     this._commitLangToken = new ReusableLanguageToken("commit");
/* 4579 */     this._rollbackLangToken = new ReusableLanguageToken("rollback");
/*      */   }
/*      */ 
/*      */   public boolean isWidetableEnabled()
/*      */   {
/* 4590 */     return this._capT._reqCaps.get(59) & !this._capT._respCaps.get(45);
/*      */   }
/*      */ 
/*      */   public boolean isUnicharEnabled()
/*      */   {
/* 4604 */     boolean bool = false;
/* 4605 */     if (this._storeStaticValues[0] == 1)
/*      */     {
/* 4607 */       return this._isUnicharEnabled;
/*      */     }
/*      */     try
/*      */     {
/* 4611 */       this._isUnicharEnabled = (bool = this._capT._reqCaps.get(66) & !this._info.getBoolean(44));
/*      */ 
/* 4613 */       this._storeStaticValues[0] = 1;
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*      */     }
/*      */ 
/* 4620 */     return bool;
/*      */   }
/*      */ 
/*      */   public boolean serverAcceptsColumnStatusByte()
/*      */   {
/* 4630 */     if (this._inLogin)
/*      */     {
/* 4632 */       return false;
/*      */     }
/*      */ 
/* 4636 */     if (this._storeStaticValues[1] == 1)
/*      */     {
/* 4638 */       return this._serverAcceptsColumnStatusByte;
/*      */     }
/* 4640 */     this._serverAcceptsColumnStatusByte = this._capT._reqCaps.get(58);
/* 4641 */     this._storeStaticValues[1] = 1;
/* 4642 */     return this._serverAcceptsColumnStatusByte;
/*      */   }
/*      */ 
/*      */   public boolean serverAcceptsTimeData()
/*      */   {
/* 4651 */     return this._capT._reqCaps.get(72);
/*      */   }
/*      */ 
/*      */   public boolean isLocatorSupported()
/*      */   {
/* 4659 */     return this._capT._reqCaps.get(101);
/*      */   }
/*      */ 
/*      */   public boolean serverPacketSize()
/*      */   {
/* 4667 */     return this._capT._reqCaps.get(79);
/*      */   }
/*      */ 
/*      */   public boolean serverAcceptsBigDateTimeData()
/*      */   {
/* 4675 */     if (this._storeStaticValues[3] == 1)
/*      */     {
/* 4677 */       return this._serverAcceptsBigDateTimeData;
/*      */     }
/* 4679 */     this._serverAcceptsBigDateTimeData = ((this._capT._reqCaps.get(93)) && (this._capT._reqCaps.get(94)));
/* 4680 */     this._storeStaticValues[3] = 1;
/* 4681 */     return this._serverAcceptsBigDateTimeData;
/*      */   }
/*      */ 
/*      */   public boolean serverAcceptsDateData()
/*      */   {
/* 4689 */     if (this._storeStaticValues[2] == 1)
/*      */     {
/* 4691 */       return this._serverAcceptsDateData;
/*      */     }
/* 4693 */     this._serverAcceptsDateData = this._capT._reqCaps.get(71);
/* 4694 */     this._storeStaticValues[2] = 1;
/* 4695 */     return this._serverAcceptsDateData;
/*      */   }
/*      */ 
/*      */   public boolean sendCurDeclare3()
/*      */   {
/* 4703 */     return this._capT._reqCaps.get(74);
/*      */   }
/*      */ 
/*      */   public boolean shouldReleaseLockOnCursorClose() throws SQLException
/*      */   {
/* 4708 */     return (this._info.getBoolean(82)) && (sendCurDeclare3());
/*      */   }
/*      */ 
/*      */   public void setRedirectImmed(boolean paramBoolean)
/*      */   {
/* 4714 */     this._redirectImmed = paramBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getRedirectImmed()
/*      */   {
/* 4719 */     return this._redirectImmed;
/*      */   }
/*      */ 
/*      */   public Vector getRedirectionHostPort()
/*      */   {
/* 4724 */     return this._redirectHostPort;
/*      */   }
/*      */ 
/*      */   public boolean useInsensitiveScrollableCursor()
/*      */   {
/* 4735 */     return (this._capT._reqCaps.get(74) & this._capT._reqCaps.get(76));
/*      */   }
/*      */ 
/*      */   public boolean isAse()
/*      */   {
/* 4753 */     return this._isAse;
/*      */   }
/*      */ 
/*      */   public boolean isSuppressParamFormatSupported()
/*      */   {
/* 4758 */     return this._capT._reqCaps.get(104);
/*      */   }
/*      */ 
/*      */   public boolean isDynamicBatchSupported()
/*      */   {
/* 4763 */     return this._capT._reqCaps.get(98);
/*      */   }
/*      */ 
/*      */   public boolean isLanguageBatchSupported()
/*      */   {
/* 4768 */     return this._capT._reqCaps.get(99);
/*      */   }
/*      */ 
/*      */   protected boolean isTypeSearchable(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 4784 */     if (this._typeSearchableList == null)
/*      */     {
/* 4788 */       localObject1 = null;
/*      */       try
/*      */       {
/* 4792 */         this._typeSearchableList = new Hashtable();
/* 4793 */         localObject1 = this._conn.getMetaData().getTypeInfo();
/* 4794 */         while (((ResultSet)localObject1).next())
/*      */         {
/* 4797 */           Integer localInteger = new Integer(((ResultSet)localObject1).getInt(2));
/*      */ 
/* 4802 */           if (this._typeSearchableList.get(localInteger) != null)
/*      */           {
/*      */             continue;
/*      */           }
/*      */ 
/* 4809 */           this._typeSearchableList.put(localInteger, new Boolean(((ResultSet)localObject1).getInt(9) != 0));
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 4817 */         this._typeSearchableList = null;
/* 4818 */         throw localSQLException;
/*      */       }
/*      */       finally
/*      */       {
/* 4822 */         if (localObject1 != null)
/*      */         {
/* 4824 */           ((ResultSet)localObject1).close();
/* 4825 */           localObject1 = null;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4831 */     Object localObject1 = (Boolean)this._typeSearchableList.get(new Integer(paramInt));
/*      */ 
/* 4833 */     if (localObject1 == null)
/*      */     {
/* 4843 */       ErrorMessage.raiseError("JZ0P8");
/* 4844 */       return false;
/*      */     }
/*      */ 
/* 4849 */     return ((Boolean)localObject1).booleanValue();
/*      */   }
/*      */ 
/*      */   protected String getColumnTypeName(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 4866 */     return getColumnTypeName(paramInt1, paramInt2, null);
/*      */   }
/*      */ 
/*      */   protected String getColumnTypeName(int paramInt1, int paramInt2, String paramString)
/*      */     throws SQLException
/*      */   {
/* 4875 */     String str1 = null;
/* 4876 */     ResultSet localResultSet = null;
/* 4877 */     PreparedStatement localPreparedStatement = null;
/* 4878 */     String str2 = paramInt1 + "." + paramInt2;
/* 4879 */     synchronized (this._typeNameList)
/*      */     {
/* 4881 */       str1 = (String)this._typeNameList.get(str2);
/*      */     }
/* 4883 */     if (str1 == null)
/*      */     {
/*      */       try
/*      */       {
/* 4889 */         if (paramString == null)
/*      */         {
/* 4891 */           localPreparedStatement = this._conn.getMDA(null).getMetaDataAccessor("COLUMNTYPENAME", null);
/*      */         }
/*      */         else
/*      */         {
/* 4896 */           localPreparedStatement = this._conn.getMDA(null).getMetaDataAccessor("COLUMNTYPENAME", "", paramString, null);
/*      */         }
/*      */ 
/* 4899 */         localPreparedStatement.setInt(1, paramInt1);
/* 4900 */         localPreparedStatement.setInt(2, paramInt2);
/* 4901 */         localResultSet = localPreparedStatement.executeQuery();
/*      */ 
/* 4905 */         if (localResultSet.next())
/*      */         {
/* 4910 */           str1 = localResultSet.getString(1);
/*      */ 
/* 4914 */           synchronized (this._typeNameList)
/*      */           {
/* 4916 */             this._typeNameList.put(str2, str1);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 4925 */           ErrorMessage.raiseError("JZ0P8");
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 4931 */         throw localSQLException;
/*      */       }
/*      */       finally
/*      */       {
/* 4935 */         if (localResultSet != null)
/*      */         {
/* 4937 */           localResultSet.close();
/* 4938 */           localResultSet = null;
/*      */         }
/* 4940 */         if (localPreparedStatement != null)
/*      */         {
/* 4942 */           localPreparedStatement.close();
/* 4943 */           localPreparedStatement = null;
/*      */         }
/*      */       }
/*      */     }
/* 4947 */     return str1;
/*      */   }
/*      */ 
/*      */   public DynamicClassLoader getClassLoader()
/*      */   {
/* 4957 */     return this._classLoader;
/*      */   }
/*      */ 
/*      */   private void slurpParams(TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 4964 */     if (paramTdsProtocolContext._lastResult == 3);
/* 4969 */     paramTdsProtocolContext._lastResult = -1;
/*      */     try
/*      */     {
/* 4972 */       TdsJdbcInputStream localTdsJdbcInputStream = new TdsJdbcInputStream(null, paramTdsProtocolContext, this);
/*      */ 
/* 4974 */       switch (paramTdsProtocolContext._lastTds)
/*      */       {
/*      */       case 121:
/* 4977 */         TdsParam localTdsParam = new TdsParam(this._out);
/* 4978 */         localTdsParam._sqlType = -998;
/* 4979 */         localTdsJdbcInputStream._dataFmt = new DataFormat(localTdsParam, this._out, 4);
/* 4980 */         localTdsJdbcInputStream.open(false);
/* 4981 */         localTdsJdbcInputStream.resetInputStream(paramTdsProtocolContext._in);
/* 4982 */         localTdsJdbcInputStream.clear();
/* 4983 */         break;
/*      */       case 172:
/* 4986 */         paramTdsProtocolContext._in.readUnsignedShort();
/* 4987 */         localTdsJdbcInputStream._dataFmt = new DataFormat(paramTdsProtocolContext._in, false);
/* 4988 */         localTdsJdbcInputStream.open(false);
/* 4989 */         localTdsJdbcInputStream.resetInputStream(paramTdsProtocolContext._in);
/* 4990 */         localTdsJdbcInputStream.clear();
/* 4991 */         break;
/*      */       case 32:
/*      */       case 236:
/*      */         try
/*      */         {
/* 4998 */           paramTdsProtocolContext._lastTds = paramTdsProtocolContext._in.readUnsignedByte();
/*      */ 
/* 5004 */           if (paramTdsProtocolContext._lastTds != 215)
/*      */           {
/* 5007 */             ErrorMessage.raiseError("JZ0P4");
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (IOException localIOException2)
/*      */         {
/* 5013 */           handleIOE(localIOException2);
/*      */         }
/*      */       default:
/* 5018 */         for (int i = 0; i < paramTdsProtocolContext._paramFmts._numColumns; ++i)
/*      */         {
/* 5020 */           localTdsJdbcInputStream._dataFmt = paramTdsProtocolContext._paramFmts.getDataFormat(i);
/* 5021 */           localTdsJdbcInputStream.open(false);
/* 5022 */           localTdsJdbcInputStream.resetInputStream(paramTdsProtocolContext._in);
/*      */ 
/* 5025 */           localTdsJdbcInputStream.clear();
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException1)
/*      */     {
/* 5032 */       handleIOE(localIOException1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void handleIOE(IOException paramIOException)
/*      */     throws SQLException
/*      */   {
/* 5039 */     handleIOE(paramIOException, null);
/*      */   }
/*      */ 
/*      */   private void handleIOE(IOException paramIOException, SQLException paramSQLException)
/*      */     throws SQLException
/*      */   {
/* 5059 */     if (paramIOException instanceof SybHAException)
/*      */     {
/* 5061 */       ErrorMessage.raiseError(((SybHAException)paramIOException).getSqlState(), paramSQLException);
/*      */     }
/* 5063 */     else if (paramIOException instanceof InterruptedIOException)
/*      */     {
/*      */       Object localObject;
/* 5065 */       if (this._conn.okToThrowLoginTimeoutException())
/*      */       {
/* 5067 */         localObject = ErrorMessage.createIOEKilledConnEx(paramIOException);
/*      */ 
/* 5069 */         ((SQLException)localObject).setNextException(paramSQLException);
/* 5070 */         ErrorMessage.raiseError("JZ00M", (SQLException)localObject);
/*      */       }
/* 5073 */       else if (this._info.getBoolean(51))
/*      */       {
/* 5083 */         localObject = (TdsProtocolContext)getProtocolContext(this._info);
/*      */ 
/* 5085 */         cancel((ProtocolContext)localObject, true);
/*      */       }
/*      */     }
/* 5088 */     if (paramSQLException != null)
/*      */     {
/* 5090 */       ErrorMessage.raiseErrorCheckDead(paramIOException, paramSQLException);
/*      */     }
/*      */     else
/*      */     {
/* 5094 */       ErrorMessage.raiseErrorCheckDead(paramIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int processLoginAckToken(TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 5114 */     LoginAckToken localLoginAckToken = null;
/* 5115 */     int i = -1;
/*      */     while (true)
/*      */     {
/*      */       try
/*      */       {
/* 5122 */         i = nextResult(paramTdsProtocolContext);
/*      */       }
/*      */       catch (SQLException localSQLException2)
/*      */       {
/* 5128 */         this._conn.chainWarnings(localSQLException1);
/* 5129 */         String str2 = localSQLException1.getSQLState();
/*      */ 
/* 5131 */         if (str2 != null)
/*      */         {
/*      */           SQLException localSQLException2;
/* 5133 */           if (str2.equals("JZ006"))
/*      */           {
/* 5153 */             if (this._info.getBoolean(34))
/*      */             {
/* 5155 */               throw localSQLException1;
/*      */             }
/*      */ 
/* 5163 */             SQLException localSQLException3 = localSQLException1;
/* 5164 */             while (localSQLException1 != null)
/*      */             {
/* 5166 */               str2 = localSQLException1.getSQLState();
/* 5167 */               if (str2.equals("JZ0TO"))
/*      */               {
/* 5169 */                 ErrorMessage.raiseError("JZ00M", localSQLException3);
/*      */               }
/* 5171 */               localSQLException2 = localSQLException1.getNextException();
/*      */             }
/* 5173 */             break label237:
/*      */           }
/*      */ 
/* 5176 */           if (str2.equals("010SM"))
/*      */           {
/* 5178 */             throw localSQLException2;
/*      */           }
/*      */ 
/* 5185 */           if (str2.equals("JZ0F2"))
/*      */           {
/* 5187 */             throw localSQLException2;
/*      */           }
/*      */         }
/*      */       }
/* 5190 */       continue;
/*      */ 
/* 5192 */       switch (i)
/*      */       {
/*      */       case 0:
/* 5195 */         break;
/*      */       case 173:
/*      */         try
/*      */         {
/* 5200 */           localLoginAckToken = new LoginAckToken(paramTdsProtocolContext._in);
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/* 5205 */           this._conn.chainWarnings(ErrorMessage.createWarning("JZ006", localIOException.toString()));
/*      */ 
/* 5207 */           break label237:
/*      */         }
/*      */ 
/* 5213 */         if (localLoginAckToken != null) { if (localLoginAckToken.getLoginStatus() == 7) break; if (localLoginAckToken.getLoginStatus() == 135)
/*      */           {
/*      */             break;
/*      */           }
/*      */  }
/*      */ 
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5226 */     if (localLoginAckToken == null)
/*      */     {
/* 5228 */       label237: ErrorMessage.raiseError("JZ00L");
/*      */     }
/*      */ 
/* 5233 */     if (!localLoginAckToken.tdsVersionOK())
/*      */     {
/* 5235 */       this._conn.chainWarnings(ErrorMessage.createWarning("0100V", localLoginAckToken.getTdsVersionString()));
/*      */     }
/*      */ 
/* 5241 */     if ((this._haContext.wasHARequested()) && (!localLoginAckToken.loginOK()))
/*      */     {
/* 5245 */       this._conn.chainWarnings(ErrorMessage.createWarning("010HA"));
/*      */     }
/*      */ 
/* 5251 */     if ((this._isKerberosConn) && (!localLoginAckToken.loginOK()))
/*      */     {
/* 5255 */       this._conn.chainWarnings(ErrorMessage.createWarning("010KF"));
/*      */     }
/*      */ 
/* 5262 */     if (!localLoginAckToken.loginOK())
/*      */     {
/* 5264 */       ErrorMessage.raiseError("JZ00L");
/*      */     }
/*      */ 
/* 5270 */     if (localLoginAckToken._progName != null)
/*      */     {
/* 5272 */       if (localLoginAckToken._progName.equals("OpenServer"))
/*      */       {
/* 5278 */         this._inStreamMgr.setSerialize();
/*      */       }
/* 5280 */       else if ((localLoginAckToken._progName.equals("sql server")) || (localLoginAckToken._progName.equals("ASE")))
/*      */       {
/* 5289 */         this._stripExec = true;
/* 5290 */         this._isAse = true;
/*      */       }
/* 5292 */       if (!this._isAse)
/*      */       {
/* 5294 */         String str1 = this._info.getString(68);
/* 5295 */         if ((str1 != null) && (!str1.equalsIgnoreCase("NONE")) && (!str1.equalsIgnoreCase("FALSE")))
/*      */         {
/* 5298 */           this._info.setProperty(68, "NONE");
/* 5299 */           this._conn.chainWarnings(ErrorMessage.createWarning("010UP", "ENABLE_BULK_LOAD"));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5307 */     return localLoginAckToken.getLoginStatus();
/*      */   }
/*      */ 
/*      */   protected int getMaxLongvarcharLength() throws SQLException
/*      */   {
/* 5312 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)getProtocolContext(this._info);
/*      */ 
/* 5314 */     return this._conn.getMDA(null).getMaxLongvarcharLength(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   protected int getMaxLongvarbinaryLength() throws SQLException {
/* 5318 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)getProtocolContext(this._info);
/*      */ 
/* 5320 */     return this._conn.getMDA(null).getMaxLongvarbinaryLength(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   protected boolean isUnicodeBigEndian() {
/* 5324 */     return _isUnicodeBigEndian;
/*      */   }
/*      */ 
/*      */   protected boolean isUnicodeBigUnmarkedSupported() {
/* 5328 */     return _unicodeBigUnmarkedOK;
/*      */   }
/*      */ 
/*      */   public int flushBCP(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 5337 */     int i = 0;
/* 5338 */     if (this._startBulk)
/*      */     {
/* 5340 */       TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)getProtocolContext(this._info);
/*      */       try
/*      */       {
/* 5343 */         this._outStreamMgr.getSendLock(localTdsProtocolContext);
/* 5344 */         this._outStreamMgr.beginRequest(localTdsProtocolContext);
/* 5345 */         this._outFormat.setPDUHeader(7, 1);
/* 5346 */         this._startBulk = false;
/* 5347 */         this._outFormat.flush();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 5351 */         handleIOE(localIOException);
/*      */       }
/*      */       catch (SQLException localSQLException1)
/*      */       {
/* 5355 */         this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 5356 */         this._inBulkBatch = false;
/* 5357 */         this._startBulk = false;
/* 5358 */         throw localSQLException1;
/*      */       }
/*      */       finally
/*      */       {
/* 5362 */         this._outFormat.setPDUHeader(15, 0);
/* 5363 */         this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 5368 */         if (paramBoolean)
/*      */         {
/* 5370 */           i = getDoneResult(localTdsProtocolContext);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException localSQLException2)
/*      */       {
/* 5377 */         throw localSQLException2;
/*      */       }
/*      */       finally
/*      */       {
/* 5381 */         this._inBulkBatch = false;
/* 5382 */         this._startBulk = false;
/*      */       }
/*      */     }
/* 5385 */     return i;
/*      */   }
/*      */ 
/*      */   public void initBCP(SybBCP paramSybBCP, String paramString, int paramInt) throws SQLException, IOException
/*      */   {
/* 5390 */     language(getProtocolContext(this._info), paramString, null);
/* 5391 */     this._enableBulkRawInterface = this._info.getBoolean(78);
/* 5392 */     if (paramInt >= 2)
/*      */     {
/* 5394 */       this._inBulkBatch = true;
/*      */     }
/* 5396 */     this._usingCheckingConverter = true;
/* 5397 */     if (this._enableBulkRawInterface)
/*      */     {
/* 5399 */       this._bcpRaw = new BCPRawInterface(paramSybBCP, this._out);
/*      */     }
/*      */     else
/*      */     {
/* 5403 */       this._bcpT = new BCPToken(paramSybBCP, this._out);
/*      */     }
/* 5405 */     this._outFormat.setPDUHeader(7, 0);
/* 5406 */     this._startBulk = true;
/*      */   }
/*      */ 
/*      */   public int sendBulkData(Object[] paramArrayOfObject, int[] paramArrayOfInt1, int[] paramArrayOfInt2, Calendar[] paramArrayOfCalendar)
/*      */     throws SQLException, IOException
/*      */   {
/* 5414 */     if (this._enableBulkRawInterface)
/*      */     {
/* 5416 */       this._bcpRaw.writeBulkData(paramArrayOfObject, paramArrayOfInt1, paramArrayOfInt2, paramArrayOfCalendar);
/*      */     }
/*      */     else
/*      */     {
/* 5420 */       this._bcpT.writeBulkData(paramArrayOfObject, paramArrayOfInt1, paramArrayOfInt2, paramArrayOfCalendar);
/*      */     }
/* 5422 */     return 1;
/*      */   }
/*      */ 
/*      */   public int sendBulkData(LinkedList paramLinkedList1, LinkedList paramLinkedList2, LinkedList paramLinkedList3, LinkedList paramLinkedList4)
/*      */     throws SQLException, IOException
/*      */   {
/* 5431 */     if (this._enableBulkRawInterface)
/*      */     {
/* 5434 */       for (i = paramLinkedList1.size(); ; --i) { if (i <= 0)
/*      */           break label113;
/* 5436 */         this._bcpRaw.writeBulkData((Object[])paramLinkedList1.removeFirst(), (int[])paramLinkedList2.removeFirst(), (int[])paramLinkedList3.removeFirst(), (Calendar[])paramLinkedList4.removeFirst()); }
/*      */ 
/*      */ 
/*      */     }
/*      */ 
/* 5466 */     for (int i = paramLinkedList1.size(); i > 0; --i)
/*      */     {
/* 5468 */       this._bcpT.writeBulkData((Object[])paramLinkedList1.removeFirst(), (int[])paramLinkedList2.removeFirst(), (int[])paramLinkedList3.removeFirst(), (Calendar[])paramLinkedList4.removeFirst());
/*      */     }
/*      */ 
/* 5473 */     label113: return flushBCP(true);
/*      */   }
/*      */ 
/*      */   public boolean getInTransaction()
/*      */   {
/* 5478 */     return this._inTransaction;
/*      */   }
/*      */ 
/*      */   public void checkBcpWithMixStmt()
/*      */     throws SQLException
/*      */   {
/* 5486 */     if (!this._inBulkBatch)
/*      */       return;
/* 5488 */     ErrorMessage.raiseError("JZBK4");
/*      */   }
/*      */ 
/*      */   protected void sendParamStream(Param[] paramArrayOfParam, int paramInt1, int paramInt2, boolean paramBoolean)
/*      */     throws IOException, SQLException
/*      */   {
/* 5506 */     if (paramArrayOfParam == null)
/*      */     {
/* 5508 */       return;
/*      */     }
/* 5510 */     if (paramArrayOfParam.length <= 0) {
/*      */       return;
/*      */     }
/* 5513 */     TdsParam localTdsParam = null;
/* 5514 */     for (int i = paramInt1; i <= paramInt2; ++i)
/*      */     {
/* 5516 */       localTdsParam = (TdsParam)paramArrayOfParam[i];
/* 5517 */       localTdsParam.send(this._out, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getCharsetMapping(String paramString)
/*      */     throws SQLException
/*      */   {
/* 5530 */     String str = this._info.getString(77);
/*      */ 
/* 5532 */     if (str != null)
/*      */     {
/* 5534 */       return str;
/*      */     }
/* 5536 */     return Iana.lookupIana(paramString);
/*      */   }
/*      */ 
/*      */   public boolean isLOBSupportedAsParameterToSproc()
/*      */   {
/* 5541 */     return this._capT._reqCaps.get(95);
/*      */   }
/*      */ 
/*      */   public boolean initCommandExecSession(ProtocolContext paramProtocolContext) throws SQLException
/*      */   {
/* 5546 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/* 5547 */     boolean bool = this._outStreamMgr.getSendLock(localTdsProtocolContext);
/*      */ 
/* 5549 */     adjustMaxRows(localTdsProtocolContext);
/* 5550 */     this._outStreamMgr.beginRequest(localTdsProtocolContext);
/* 5551 */     return bool;
/*      */   }
/*      */ 
/*      */   public void finishCommandExecSession(ProtocolContext paramProtocolContext, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 5557 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)paramProtocolContext;
/*      */     try
/*      */     {
/* 5560 */       this._out.flush();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 5564 */       if (localIOException instanceof TdsInputStreamIOException)
/*      */       {
/* 5566 */         cancel(localTdsProtocolContext, false);
/*      */       }
/* 5568 */       this._outStreamMgr.abortRequest(localTdsProtocolContext);
/* 5569 */       handleIOE(localIOException);
/*      */     }
/*      */ 
/* 5577 */     if ((!paramBoolean) || (localTdsProtocolContext._batchReadAhead))
/*      */       return;
/* 5579 */     this._outStreamMgr.endRequest(localTdsProtocolContext);
/*      */   }
/*      */ 
/*      */   public void abortCommandSession(ProtocolContext paramProtocolContext)
/*      */   {
/* 5585 */     this._outStreamMgr.abortRequest((TdsProtocolContext)paramProtocolContext);
/*      */   }
/*      */ 
/*      */   public boolean isUTF8OrServerCharset()
/*      */   {
/* 5590 */     return this._isUtf8OrServerCharset;
/*      */   }
/*      */ 
/*      */   public boolean isSuppressRowFormatSupportedAndSet(TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/* 5596 */     return (paramTdsProtocolContext.isSelectSql()) && (this._capT._respCaps.get(62)) && (this._info.getBoolean(89));
/*      */   }
/*      */ 
/*      */   public boolean isSuppressParamFormatSupportedAndSet()
/*      */     throws SQLException
/*      */   {
/* 5602 */     return (this._capT._reqCaps.get(104)) && (this._info.getBoolean(90));
/*      */   }
/*      */ 
/*      */   public boolean isDynamicHomogenousBatchSupportedAndSet()
/*      */     throws SQLException
/*      */   {
/* 5608 */     return (this._capT._reqCaps.get(98)) && (this._info.getBoolean(84)) && (!this._info.getBoolean(15));
/*      */   }
/*      */ 
/*      */   public boolean isLanguageHomogenousBatchSupportedAndSet()
/*      */     throws SQLException
/*      */   {
/* 5616 */     return (this._capT._reqCaps.get(99)) && (this._info.getBoolean(84)) && (!this._info.getBoolean(15));
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   91 */     ProtocolManager.registerProtocol(new Tds());
/*      */ 
/*  214 */     _unicodeBigUnmarkedOK = true;
/*      */ 
/*  229 */     _isUnicodeBigEndian = false;
/*      */ 
/*  258 */     String str = "x";
/*  259 */     byte[] arrayOfByte = null;
/*      */     try
/*      */     {
/*  263 */       arrayOfByte = str.getBytes("UnicodeBigUnmarked");
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException1)
/*      */     {
/*  267 */       _unicodeBigUnmarkedOK = false;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  272 */       arrayOfByte = str.getBytes("Unicode");
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException2)
/*      */     {
/*      */     }
/*      */ 
/*  281 */     if (((arrayOfByte[0] == -2) && (arrayOfByte[1] == -1)) || (arrayOfByte[0] == 0))
/*      */     {
/*  283 */       _isUnicodeBigEndian = true;
/*      */     }
/*      */ 
/*  330 */     INFO_MSGNO = new int[] { 5701, 5703, 5704, 7326 };
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.Tds
 * JD-Core Version:    0.5.4
 */