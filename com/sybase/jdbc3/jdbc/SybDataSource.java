/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.utils.ASAUDPUtil;
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import com.sybase.jdbc3.utils.EncryptedValue;
/*      */ import com.sybase.jdbc3.utils.LogUtil;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.lang.reflect.Method;
/*      */ import java.sql.Connection;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Properties;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import javax.naming.BinaryRefAddr;
/*      */ import javax.naming.NamingException;
/*      */ import javax.naming.Reference;
/*      */ import javax.naming.StringRefAddr;
/*      */ 
/*      */ public class SybDataSource extends SybDriver
/*      */   implements com.sybase.jdbcx.SybDataSource
/*      */ {
/*  106 */   private static Logger LOG = Logger.getLogger(SybDataSource.class.getName());
/*  107 */   private static volatile long _logIdCounter = 0L;
/*      */   static final long serialVersionUID = -1964411659637713576L;
/*      */   static final String ADDRESS_LIST = "addressList";
/*      */   static final String SYB_PROPERTY = "sybProperty";
/*      */   static final String RM_NAME = "resourceManagerName";
/*      */   static final String RM_TYPE = "resourceManagerType";
/*  119 */   static final String OBJECT_FACTORY_NAME = SybObjectFactory.class.getName();
/*      */ 
/*  125 */   private String _databaseName = null;
/*      */ 
/*  132 */   private String _dataSourceName = null;
/*      */ 
/*  138 */   private String _description = null;
/*      */ 
/*  142 */   private String _networkProtocol = "Tds";
/*      */ 
/*  148 */   private int _portNumber = -1;
/*      */ 
/*  154 */   private String _serverName = null;
/*      */ 
/*  168 */   private SybProperty _sybProperty = null;
/*      */ 
/*  176 */   private String _resourceManagerName = null;
/*      */ 
/*  182 */   private boolean _rmNameSetByMethod = false;
/*      */ 
/*  190 */   private int _resourceManagerType = 0;
/*      */ 
/*  196 */   private String _addressListStr = null;
/*      */ 
/*  199 */   private Vector _addressList = null;
/*      */ 
/*  202 */   private transient PrintWriter _logWriter = null;
/*      */ 
/*  205 */   private int _loginTimeout = 0;
/*      */ 
/*  208 */   private String _defaultUser = null;
/*      */ 
/*  211 */   private char[] _defaultPassword = null;
/*      */ 
/*  213 */   private String _logId = null;
/*      */ 
/*      */   public SybDataSource()
/*      */   {
/*  224 */     this._logId = ("Ds" + _logIdCounter++);
/*      */ 
/*  230 */     this._sybProperty = new SybProperty(this._version);
/*      */   }
/*      */ 
/*      */   public Connection getConnection()
/*      */     throws SQLException
/*      */   {
/*  260 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  262 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  264 */       LOG.fine(this._logId + "getConnection()");
/*      */     }
/*      */ 
/*  274 */     return connect(createSybUrlProvider(), getLoginTimeout());
/*      */   }
/*      */ 
/*      */   public Connection getConnection(String paramString1, String paramString2)
/*      */     throws SQLException
/*      */   {
/*  292 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  294 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  296 */         LOG.finer(this._logId + "getConnection(String = [" + paramString1 + "], String = [" + paramString2 + "])");
/*      */       }
/*  299 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  301 */         LOG.fine(this._logId + "getConnection(String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  310 */     SybUrlProvider localSybUrlProvider = createSybUrlProvider();
/*  311 */     SybProperty localSybProperty = localSybUrlProvider.getSybProperty();
/*  312 */     localSybProperty.setProperty(3, paramString1, true);
/*  313 */     localSybProperty.setProperty(4, paramString2, true);
/*      */ 
/*  319 */     Connection localConnection = connect(localSybUrlProvider, getLoginTimeout());
/*  320 */     return localConnection;
/*      */   }
/*      */ 
/*      */   public Reference getReference()
/*      */     throws NamingException
/*      */   {
/*  352 */     Reference localReference = new Reference(super.getClass().getName(), OBJECT_FACTORY_NAME, null);
/*      */ 
/*  358 */     localReference.add(new StringRefAddr("databaseName", getDatabaseName()));
/*  359 */     localReference.add(new StringRefAddr("dataSourceName", getDataSourceName()));
/*  360 */     localReference.add(new StringRefAddr("description", getDescription()));
/*  361 */     localReference.add(new StringRefAddr("networkProtocol", getNetworkProtocol()));
/*  362 */     localReference.add(new StringRefAddr("portNumber", String.valueOf(getPortNumber())));
/*      */ 
/*  364 */     localReference.add(new StringRefAddr("serverName", getServerName()));
/*  365 */     localReference.add(new StringRefAddr("user", getUser()));
/*  366 */     localReference.add(new StringRefAddr("password", getPassword()));
/*      */ 
/*  370 */     localReference.add(new StringRefAddr("resourceManagerName", getResourceManagerName()));
/*  371 */     localReference.add(new StringRefAddr("resourceManagerType", String.valueOf(getResourceManagerType())));
/*      */ 
/*  373 */     localReference.add(new StringRefAddr("addressList", getAddressList()));
/*      */ 
/*  379 */     String str1 = null;
/*  380 */     String str2 = null;
/*  381 */     Method localMethod = null;
/*  382 */     for (int i = 0; i < 103; ++i)
/*      */     {
/*  384 */       str1 = SybProperty.PROPNAME[i].toUpperCase();
/*  385 */       if (str1.equals("SEND_LONG_PARAMS_REGARDLESS_OF_CAPABILITIES"))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/*  390 */       str2 = "get" + str1;
/*      */       try
/*      */       {
/*  393 */         localMethod = SybDataSource.class.getDeclaredMethod(str2, null);
/*      */       }
/*      */       catch (Exception localException1)
/*      */       {
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  406 */         localReference.add(new StringRefAddr(str1, (String)localMethod.invoke(this, null)));
/*      */       }
/*      */       catch (Exception localException2)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  421 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*  422 */       new ObjectOutputStream(localByteArrayOutputStream).writeObject(getSybProperty());
/*      */ 
/*  424 */       localReference.add(new BinaryRefAddr("sybProperty", localByteArrayOutputStream.toByteArray()));
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */ 
/*  432 */     return localReference;
/*      */   }
/*      */ 
/*      */   public PrintWriter getLogWriter()
/*      */     throws SQLException
/*      */   {
/*  441 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  443 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  445 */       LOG.fine(this._logId + "getLogWriter()");
/*      */     }
/*      */ 
/*  450 */     return this._logWriter;
/*      */   }
/*      */ 
/*      */   public void setLogWriter(PrintWriter paramPrintWriter) throws SQLException
/*      */   {
/*  455 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  457 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  459 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setLogWrietr", new Object[] { paramPrintWriter }));
/*      */       }
/*  462 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  464 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setLogWriter", new Object[] { paramPrintWriter }));
/*      */       }
/*  467 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  469 */         LOG.fine(this._logId + " setLogWriter(PrintWriter)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  474 */     this._logWriter = paramPrintWriter;
/*  475 */     Debug.setOutputWriter(paramPrintWriter);
/*      */   }
/*      */ 
/*      */   public int getLoginTimeout() throws SQLException
/*      */   {
/*  480 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  482 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  484 */       LOG.fine(this._logId + "getLoginTimeout()");
/*      */     }
/*      */ 
/*  489 */     return this._loginTimeout;
/*      */   }
/*      */ 
/*      */   public void setLoginTimeout(int paramInt) throws SQLException
/*      */   {
/*  494 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  496 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  498 */         LOG.finer(this._logId + "setLoginTimeout(int = [" + paramInt + "])");
/*      */       }
/*  501 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  503 */         LOG.fine(this._logId + "setLoginTimeout(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  508 */     this._loginTimeout = paramInt;
/*      */   }
/*      */ 
/*      */   public String getServerName()
/*      */   {
/*  522 */     return this._serverName;
/*      */   }
/*      */ 
/*      */   public void setServerName(String paramString)
/*      */   {
/*  529 */     this._serverName = paramString;
/*      */   }
/*      */ 
/*      */   public String getDatabaseName()
/*      */   {
/*  538 */     return this._databaseName;
/*      */   }
/*      */ 
/*      */   public void setDatabaseName(String paramString)
/*      */   {
/*  546 */     this._databaseName = paramString;
/*      */   }
/*      */ 
/*      */   public String getDataSourceName()
/*      */   {
/*  555 */     return this._dataSourceName;
/*      */   }
/*      */ 
/*      */   public void setDataSourceName(String paramString)
/*      */   {
/*  563 */     this._dataSourceName = paramString;
/*      */   }
/*      */ 
/*      */   public String getDescription()
/*      */   {
/*  572 */     return this._description;
/*      */   }
/*      */ 
/*      */   public void setDescription(String paramString)
/*      */   {
/*  580 */     this._description = paramString;
/*      */   }
/*      */ 
/*      */   public String getUser()
/*      */   {
/*  589 */     return this._defaultUser;
/*      */   }
/*      */ 
/*      */   public void setUser(String paramString)
/*      */   {
/*  597 */     this._defaultUser = paramString;
/*      */ 
/*  602 */     this._sybProperty.setProperty(3, paramString, true);
/*      */   }
/*      */ 
/*      */   public String getPassword()
/*      */   {
/*  611 */     return (this._defaultPassword == null) ? null : String.valueOf(this._defaultPassword);
/*      */   }
/*      */ 
/*      */   public void setPassword(String paramString)
/*      */   {
/*  619 */     this._defaultPassword = ((paramString == null) ? null : paramString.toCharArray());
/*      */ 
/*  624 */     this._sybProperty.setProperty(4, paramString, true);
/*      */   }
/*      */ 
/*      */   public String getNetworkProtocol()
/*      */   {
/*  633 */     return this._networkProtocol;
/*      */   }
/*      */ 
/*      */   public void setNetworkProtocol(String paramString)
/*      */   {
/*  646 */     if (paramString.equals("shm"))
/*      */     {
/*  648 */       this._sybProperty.setProperty(31, "com.sybase.shmem.ShmemSocketFactory", true);
/*      */ 
/*  651 */       this._networkProtocol = "Tds";
/*      */     }
/*      */     else
/*      */     {
/*  655 */       this._networkProtocol = paramString;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getPortNumber()
/*      */   {
/*  665 */     return this._portNumber;
/*      */   }
/*      */ 
/*      */   public void setPortNumber(int paramInt)
/*      */   {
/*  672 */     this._portNumber = paramInt;
/*      */   }
/*      */ 
/*      */   public Object getConnectionProperty(String paramString)
/*      */   {
/*  677 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  679 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  681 */         LOG.finer(this._logId + "getConnection(String = [" + paramString + "])");
/*      */       }
/*  683 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  685 */         LOG.fine(this._logId + "getConnection(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  692 */     return this._sybProperty.getConnProperty(paramString);
/*      */   }
/*      */ 
/*      */   public void setConnectionProperties(Properties paramProperties)
/*      */     throws SQLException
/*      */   {
/*  713 */     Enumeration localEnumeration = paramProperties.keys();
/*  714 */     if (localEnumeration.hasMoreElements())
/*      */     {
/*  716 */       label5: str = (String)localEnumeration.nextElement();
/*  717 */       for (int i = 0; ; ++i) { if (i < 103);
/*  719 */         if (!str.equalsIgnoreCase(SybProperty.PROPNAME[i]))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  729 */         this._sybProperty.setProperty(i, paramProperties.getProperty(str), true);
/*      */ 
/*  731 */         break label5: }
/*      */ 
/*      */ 
/*      */     }
/*      */ 
/*  739 */     String str = paramProperties.getProperty(SybProperty.PROPNAME[42]);
/*      */ 
/*  741 */     if (str == null)
/*      */       return;
/*  743 */     this._resourceManagerName = str;
/*      */   }
/*      */ 
/*      */   public void setResourceManagerName(String paramString)
/*      */   {
/*  770 */     if (paramString == null)
/*      */       return;
/*  772 */     this._resourceManagerName = paramString;
/*  773 */     this._rmNameSetByMethod = true;
/*      */   }
/*      */ 
/*      */   public String getResourceManagerName()
/*      */   {
/*  784 */     return this._resourceManagerName;
/*      */   }
/*      */ 
/*      */   public void setResourceManagerType(int paramInt)
/*      */   {
/*  798 */     switch (paramInt)
/*      */     {
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     }
/*      */ 
/*  808 */     this._resourceManagerType = paramInt;
/*      */   }
/*      */ 
/*      */   public int getResourceManagerType()
/*      */   {
/*  818 */     return this._resourceManagerType;
/*      */   }
/*      */ 
/*      */   public void setAddressList(String paramString)
/*      */   {
/*  830 */     this._addressListStr = paramString;
/*  831 */     if ((this._addressListStr == null) || (this._addressListStr.trim().length() == 0))
/*      */       return;
/*  833 */     String[] arrayOfString = this._addressListStr.split(",");
/*      */ 
/*  835 */     Vector localVector = new Vector(arrayOfString.length);
/*  836 */     for (int i = 0; i < arrayOfString.length; ++i)
/*      */     {
/*  838 */       localVector.addElement(arrayOfString[i]);
/*      */     }
/*  840 */     setAddressList(localVector);
/*      */   }
/*      */ 
/*      */   public String getAddressList()
/*      */   {
/*  849 */     return this._addressListStr;
/*      */   }
/*      */ 
/*      */   public void setSERVICENAME(String paramString)
/*      */   {
/*  857 */     setPropValue(0, paramString);
/*      */   }
/*      */ 
/*      */   public String getSERVICENAME()
/*      */   {
/*  865 */     return getPropValue(0);
/*      */   }
/*      */ 
/*      */   public void setHOSTNAME(String paramString)
/*      */   {
/*  873 */     setPropValue(1, paramString);
/*      */   }
/*      */ 
/*      */   public String getHOSTNAME()
/*      */   {
/*  881 */     return getPropValue(1);
/*      */   }
/*      */ 
/*      */   public void setHOSTPROC(String paramString)
/*      */   {
/*  889 */     setPropValue(2, paramString);
/*      */   }
/*      */ 
/*      */   public String getHOSTPROC()
/*      */   {
/*  897 */     return getPropValue(2);
/*      */   }
/*      */ 
/*      */   public void setUSER(String paramString)
/*      */   {
/*  905 */     this._defaultUser = paramString;
/*  906 */     setPropValue(3, paramString);
/*      */   }
/*      */ 
/*      */   public String getUSER()
/*      */   {
/*  914 */     return getPropValue(3);
/*      */   }
/*      */ 
/*      */   public void setPASSWORD(String paramString)
/*      */   {
/*  922 */     this._defaultPassword = ((paramString == null) ? null : paramString.toCharArray());
/*  923 */     setPropValue(4, paramString);
/*      */   }
/*      */ 
/*      */   public String getPASSWORD()
/*      */   {
/*  931 */     return getPropValue(4);
/*      */   }
/*      */ 
/*      */   public void setAPPLICATIONNAME(String paramString)
/*      */   {
/*  939 */     setPropValue(5, paramString);
/*      */   }
/*      */ 
/*      */   public String getAPPLICATIONNAME()
/*      */   {
/*  947 */     return getPropValue(5);
/*      */   }
/*      */ 
/*      */   public void setUSE_METADATA(String paramString)
/*      */   {
/*  955 */     setPropValue(6, paramString);
/*      */   }
/*      */ 
/*      */   public String getUSE_METADATA()
/*      */   {
/*  963 */     return getPropValue(6);
/*      */   }
/*      */ 
/*      */   public void setLANGUAGE(String paramString)
/*      */   {
/*  971 */     setPropValue(7, paramString);
/*      */   }
/*      */ 
/*      */   public String getLANGUAGE()
/*      */   {
/*  979 */     return getPropValue(7);
/*      */   }
/*      */ 
/*      */   public void setCHARSET(String paramString)
/*      */   {
/*  987 */     setPropValue(8, paramString);
/*      */   }
/*      */ 
/*      */   public String getCHARSET()
/*      */   {
/*  995 */     return getPropValue(8);
/*      */   }
/*      */ 
/*      */   public void setJAVA_CHARSET_MAPPING(String paramString)
/*      */   {
/* 1003 */     setPropValue(77, paramString);
/*      */   }
/*      */ 
/*      */   public String getJAVA_CHARSET_MAPPING()
/*      */   {
/* 1011 */     return getPropValue(77);
/*      */   }
/*      */ 
/*      */   public void setREMOTEPWD(String paramString)
/*      */   {
/* 1019 */     setPropValue(9, new EncryptedValue(paramString));
/*      */   }
/*      */ 
/*      */   public String getREMOTEPWD()
/*      */   {
/* 1027 */     return getPropValue(9);
/*      */   }
/*      */ 
/*      */   public void setVERSIONSTRING(String paramString)
/*      */   {
/* 1035 */     setPropValue(10, paramString);
/*      */   }
/*      */ 
/*      */   public String getVERSIONSTRING()
/*      */   {
/* 1043 */     return getPropValue(10);
/*      */   }
/*      */ 
/*      */   public void setEXPIRESTRING(String paramString)
/*      */   {
/* 1051 */     setPropValue(11, paramString);
/*      */   }
/*      */ 
/*      */   public String getEXPIRESTRING()
/*      */   {
/* 1059 */     return getPropValue(11);
/*      */   }
/*      */ 
/*      */   public void setPACKETSIZE(String paramString)
/*      */   {
/* 1067 */     setPropValue(12, paramString);
/*      */   }
/*      */ 
/*      */   public String getPACKETSIZE()
/*      */   {
/* 1075 */     return getPropValue(12);
/*      */   }
/*      */ 
/*      */   public void setSTREAM_CACHE_SIZE(String paramString)
/*      */   {
/* 1083 */     setPropValue(13, paramString);
/*      */   }
/*      */ 
/*      */   public String getSTREAM_CACHE_SIZE()
/*      */   {
/* 1091 */     return getPropValue(13);
/*      */   }
/*      */ 
/*      */   public void setREPEAT_READ(String paramString)
/*      */   {
/* 1099 */     setPropValue(14, paramString);
/*      */   }
/*      */ 
/*      */   public String getREPEAT_READ()
/*      */   {
/* 1107 */     return getPropValue(14);
/*      */   }
/*      */ 
/*      */   public void setLITERAL_PARAMS(String paramString)
/*      */   {
/* 1115 */     setPropValue(15, paramString);
/*      */   }
/*      */ 
/*      */   public String getLITERAL_PARAMS()
/*      */   {
/* 1123 */     return getPropValue(15);
/*      */   }
/*      */ 
/*      */   public void setCURSOR_ROWS(String paramString)
/*      */   {
/* 1131 */     setPropValue(16, paramString);
/*      */   }
/*      */ 
/*      */   public String getCURSOR_ROWS()
/*      */   {
/* 1139 */     return getPropValue(16);
/*      */   }
/*      */ 
/*      */   public void setPROXY(String paramString)
/*      */   {
/* 1147 */     setPropValue(17, paramString);
/*      */   }
/*      */ 
/*      */   public String getPROXY()
/*      */   {
/* 1155 */     return getPropValue(17);
/*      */   }
/*      */ 
/*      */   public void setSQLINITSTRING(String paramString)
/*      */   {
/* 1163 */     setPropValue(18, paramString);
/*      */   }
/*      */ 
/*      */   public String getSQLINITSTRING()
/*      */   {
/* 1171 */     return getPropValue(18);
/*      */   }
/*      */ 
/*      */   public void setSESSION_TIMEOUT(String paramString)
/*      */   {
/* 1179 */     setPropValue(19, paramString);
/*      */   }
/*      */ 
/*      */   public String getSESSION_TIMEOUT()
/*      */   {
/* 1187 */     return getPropValue(19);
/*      */   }
/*      */ 
/*      */   public void setSESSION_ID(String paramString)
/*      */   {
/* 1195 */     setPropValue(20, paramString);
/*      */   }
/*      */ 
/*      */   public String getSESSION_ID()
/*      */   {
/* 1203 */     return getPropValue(20);
/*      */   }
/*      */ 
/*      */   public void setCHARSET_CONVERTER_CLASS(String paramString)
/*      */   {
/* 1211 */     setPropValue(21, paramString);
/*      */   }
/*      */ 
/*      */   public String getCHARSET_CONVERTER_CLASS()
/*      */   {
/* 1219 */     return getPropValue(21);
/*      */   }
/*      */ 
/*      */   public void setJCONNECT_VERSION(String paramString)
/*      */   {
/* 1227 */     setPropValue(22, paramString);
/*      */   }
/*      */ 
/*      */   public String getJCONNECT_VERSION()
/*      */   {
/* 1235 */     return getPropValue(22);
/*      */   }
/*      */ 
/*      */   public void setCANCEL_ALL(String paramString)
/*      */   {
/* 1243 */     setPropValue(23, paramString);
/*      */   }
/*      */ 
/*      */   public String getCANCEL_ALL()
/*      */   {
/* 1251 */     return getPropValue(23);
/*      */   }
/*      */ 
/*      */   public void setPROTOCOL_CAPTURE(String paramString)
/*      */   {
/* 1259 */     setPropValue(24, paramString);
/*      */   }
/*      */ 
/*      */   public String getPROTOCOL_CAPTURE()
/*      */   {
/* 1267 */     return getPropValue(24);
/*      */   }
/*      */ 
/*      */   public void setDYNAMIC_PREPARE(String paramString)
/*      */   {
/* 1275 */     setPropValue(25, paramString);
/*      */   }
/*      */ 
/*      */   public String getDYNAMIC_PREPARE()
/*      */   {
/* 1283 */     return getPropValue(25);
/*      */   }
/*      */ 
/*      */   public void setCONNECTION_FAILOVER(String paramString)
/*      */   {
/* 1291 */     setPropValue(26, paramString);
/*      */   }
/*      */ 
/*      */   public String getCONNECTION_FAILOVER()
/*      */   {
/* 1299 */     return getPropValue(26);
/*      */   }
/*      */ 
/*      */   public void setLANGUAGE_CURSOR(String paramString)
/*      */   {
/* 1307 */     setPropValue(27, paramString);
/*      */   }
/*      */ 
/*      */   public String getLANGUAGE_CURSOR()
/*      */   {
/* 1315 */     return getPropValue(27);
/*      */   }
/*      */ 
/*      */   public void setSERIALIZE_REQUESTS(String paramString)
/*      */   {
/* 1323 */     setPropValue(29, paramString);
/*      */   }
/*      */ 
/*      */   public String getSERIALIZE_REQUESTS()
/*      */   {
/* 1331 */     return getPropValue(29);
/*      */   }
/*      */ 
/*      */   public void setLSB_BYTE_ORDER(String paramString)
/*      */   {
/* 1339 */     setPropValue(30, paramString);
/*      */   }
/*      */ 
/*      */   public String getLSB_BYTE_ORDER()
/*      */   {
/* 1347 */     return getPropValue(30);
/*      */   }
/*      */ 
/*      */   public void setSYBSOCKET_FACTORY(String paramString)
/*      */   {
/* 1355 */     setPropValue(31, paramString);
/*      */   }
/*      */ 
/*      */   public String getSYBSOCKET_FACTORY()
/*      */   {
/* 1363 */     return getPropValue(31);
/*      */   }
/*      */ 
/*      */   public void setIGNORE_DONE_IN_PROC(String paramString)
/*      */   {
/* 1371 */     setPropValue(32, paramString);
/*      */   }
/*      */ 
/*      */   public String getIGNORE_DONE_IN_PROC()
/*      */   {
/* 1379 */     return getPropValue(32);
/*      */   }
/*      */ 
/*      */   public void setSELECT_OPENS_CURSOR(String paramString)
/*      */   {
/* 1387 */     setPropValue(33, paramString);
/*      */   }
/*      */ 
/*      */   public String getSELECT_OPENS_CURSOR()
/*      */   {
/* 1395 */     return getPropValue(33);
/*      */   }
/*      */ 
/*      */   public void setREQUEST_HA_SESSION(String paramString)
/*      */   {
/* 1403 */     setPropValue(34, paramString);
/*      */   }
/*      */ 
/*      */   public String getREQUEST_HA_SESSION()
/*      */   {
/* 1411 */     return getPropValue(34);
/*      */   }
/*      */ 
/*      */   public void setELIMINATE_010SM(String paramString)
/*      */   {
/* 1419 */     setPropValue(35, paramString);
/*      */   }
/*      */ 
/*      */   public String getELIMINATE_010SM()
/*      */   {
/* 1427 */     return getPropValue(35);
/*      */   }
/*      */ 
/*      */   public void setIS_CLOSED_TEST(String paramString)
/*      */   {
/* 1435 */     setPropValue(36, paramString);
/*      */   }
/*      */ 
/*      */   public String getIS_CLOSED_TEST()
/*      */   {
/* 1443 */     return getPropValue(36);
/*      */   }
/*      */ 
/*      */   public void setCLASS_LOADER(String paramString)
/*      */   {
/*      */   }
/*      */ 
/*      */   public String getCLASS_LOADER()
/*      */   {
/* 1467 */     return getPropValue(37);
/*      */   }
/*      */ 
/*      */   public void setPRELOAD_JARS(String paramString)
/*      */   {
/* 1475 */     setPropValue(38, paramString);
/*      */   }
/*      */ 
/*      */   public String getPRELOAD_JARS()
/*      */   {
/* 1483 */     return getPropValue(38);
/*      */   }
/*      */ 
/*      */   public void setFAKE_METADATA(String paramString)
/*      */   {
/* 1491 */     setPropValue(39, paramString);
/*      */   }
/*      */ 
/*      */   public String getFAKE_METADATA()
/*      */   {
/* 1499 */     return getPropValue(39);
/*      */   }
/*      */ 
/*      */   public void setGET_BY_NAME_USES_COLUMN_LABEL(String paramString)
/*      */   {
/* 1507 */     setPropValue(40, paramString);
/*      */   }
/*      */ 
/*      */   public String getGET_BY_NAME_USES_COLUMN_LABEL()
/*      */   {
/* 1515 */     return getPropValue(40);
/*      */   }
/*      */ 
/*      */   public void setBE_AS_JDBC_COMPLIANT_AS_POSSIBLE(String paramString)
/*      */   {
/* 1523 */     setPropValue(41, paramString);
/*      */   }
/*      */ 
/*      */   public String getBE_AS_JDBC_COMPLIANT_AS_POSSIBLE()
/*      */   {
/* 1531 */     return getPropValue(41);
/*      */   }
/*      */ 
/*      */   public void setGET_COLUMN_LABEL_FOR_NAME(String paramString)
/*      */   {
/* 1539 */     setPropValue(65, paramString);
/*      */   }
/*      */ 
/*      */   public String getGET_COLUMN_LABEL_FOR_NAME()
/*      */   {
/* 1547 */     return getPropValue(65);
/*      */   }
/*      */ 
/*      */   public void setRMNAME(String paramString)
/*      */   {
/* 1555 */     if (paramString == null)
/*      */       return;
/* 1557 */     this._resourceManagerName = paramString;
/* 1558 */     this._rmNameSetByMethod = true;
/* 1559 */     setPropValue(42, paramString);
/*      */   }
/*      */ 
/*      */   public String getRMNAME()
/*      */   {
/* 1568 */     return getPropValue(42);
/*      */   }
/*      */ 
/*      */   public void setDISABLE_UNPROCESSED_PARAM_WARNINGS(String paramString)
/*      */   {
/* 1576 */     setPropValue(43, paramString);
/*      */   }
/*      */ 
/*      */   public String getDISABLE_UNPROCESSED_PARAM_WARNINGS()
/*      */   {
/* 1584 */     return getPropValue(43);
/*      */   }
/*      */ 
/*      */   public void setDISABLE_UNICHAR_SENDING(String paramString)
/*      */   {
/* 1592 */     setPropValue(44, paramString);
/*      */   }
/*      */ 
/*      */   public String getDISABLE_UNICHAR_SENDING()
/*      */   {
/* 1600 */     return getPropValue(44);
/*      */   }
/*      */ 
/*      */   public void setSECONDARY_SERVER_HOSTPORT(String paramString)
/*      */   {
/* 1608 */     setPropValue(45, paramString);
/*      */   }
/*      */ 
/*      */   public String getSECONDARY_SERVER_HOSTPORT()
/*      */   {
/* 1616 */     return getPropValue(45);
/*      */   }
/*      */ 
/*      */   public void setESCAPE_PROCESSING_DEFAULT(String paramString)
/*      */   {
/* 1624 */     setPropValue(46, paramString);
/*      */   }
/*      */ 
/*      */   public String getESCAPE_PROCESSING_DEFAULT()
/*      */   {
/* 1632 */     return getPropValue(46);
/*      */   }
/*      */ 
/*      */   public void setIMPLICIT_CURSOR_FETCH_SIZE(String paramString)
/*      */   {
/* 1640 */     setPropValue(47, paramString);
/*      */   }
/*      */ 
/*      */   public String getIMPLICIT_CURSOR_FETCH_SIZE()
/*      */   {
/* 1648 */     return getPropValue(47);
/*      */   }
/*      */ 
/*      */   public void setREQUEST_KERBEROS_SESSION(String paramString)
/*      */   {
/* 1656 */     setPropValue(48, paramString);
/*      */   }
/*      */ 
/*      */   public String getREQUEST_KERBEROS_SESSION()
/*      */   {
/* 1664 */     return getPropValue(48);
/*      */   }
/*      */ 
/*      */   public void setSERVICE_PRINCIPAL_NAME(String paramString)
/*      */   {
/* 1672 */     setPropValue(49, paramString);
/*      */   }
/*      */ 
/*      */   public String getSERVICE_PRINCIPAL_NAME()
/*      */   {
/* 1680 */     return getPropValue(49);
/*      */   }
/*      */ 
/*      */   public void setGSSMANAGER_CLASS(String paramString)
/*      */   {
/* 1690 */     setPropValue(50, paramString);
/*      */   }
/*      */ 
/*      */   public String getGSSMANAGER_CLASS()
/*      */   {
/* 1701 */     String str = getPropValue(50);
/* 1702 */     if ((str != null) && (!str instanceof String))
/*      */     {
/* 1704 */       str = str.getClass().getName();
/*      */     }
/* 1706 */     return (String)str;
/*      */   }
/*      */ 
/*      */   public void setQUERY_TIMEOUT_CANCELS_ALL(String paramString)
/*      */   {
/* 1714 */     setPropValue(51, paramString);
/*      */   }
/*      */ 
/*      */   public String getQUERY_TIMEOUT_CANCELS_ALL()
/*      */   {
/* 1722 */     return getPropValue(51);
/*      */   }
/*      */ 
/*      */   public void setCAPABILITY_TIME(String paramString)
/*      */   {
/* 1730 */     setPropValue(52, paramString);
/*      */   }
/*      */ 
/*      */   public String getCAPABILITY_TIME()
/*      */   {
/* 1738 */     return getPropValue(52);
/*      */   }
/*      */ 
/*      */   public void setSERVER_INITIATED_TRANSACTIONS(String paramString)
/*      */   {
/* 1746 */     setPropValue(53, paramString);
/*      */   }
/*      */ 
/*      */   public String getSERVER_INITIATED_TRANSACTIONS()
/*      */   {
/* 1754 */     return getPropValue(53);
/*      */   }
/*      */ 
/*      */   public void setENABLE_SERVER_PACKETSIZE(String paramString)
/*      */   {
/* 1762 */     setPropValue(54, paramString);
/*      */   }
/*      */ 
/*      */   public String getENABLE_SERVER_PACKETSIZE()
/*      */   {
/* 1770 */     return getPropValue(54);
/*      */   }
/*      */ 
/*      */   public void setENCRYPT_PASSWORD(String paramString)
/*      */   {
/* 1778 */     setPropValue(55, paramString);
/*      */   }
/*      */ 
/*      */   public String getENCRYPT_PASSWORD()
/*      */   {
/* 1786 */     return getPropValue(55);
/*      */   }
/*      */ 
/*      */   public void setTEXTSIZE(String paramString)
/*      */   {
/* 1794 */     setPropValue(56, paramString);
/*      */   }
/*      */ 
/*      */   public String getTEXTSIZE()
/*      */   {
/* 1802 */     return getPropValue(56);
/*      */   }
/*      */ 
/*      */   public void setSERVERTYPE(String paramString)
/*      */   {
/* 1810 */     setPropValue(57, paramString);
/*      */   }
/*      */ 
/*      */   public String getSERVERTYPE()
/*      */   {
/* 1818 */     return getPropValue(57);
/*      */   }
/*      */ 
/*      */   public void setCACHE_COLUMN_METADATA(String paramString)
/*      */   {
/* 1826 */     setPropValue(58, paramString);
/*      */   }
/*      */ 
/*      */   public String getCACHE_COLUMN_METADATA()
/*      */   {
/* 1834 */     return getPropValue(58);
/*      */   }
/*      */ 
/*      */   public void setCAPABILITY_WIDETABLE(String paramString)
/*      */   {
/* 1842 */     setPropValue(59, paramString);
/*      */   }
/*      */ 
/*      */   public String getCAPABILITY_WIDETABLE()
/*      */   {
/* 1850 */     return getPropValue(59);
/*      */   }
/*      */ 
/*      */   public void setDATABASE(String paramString)
/*      */   {
/* 1858 */     setPropValue(60, paramString);
/*      */   }
/*      */ 
/*      */   public String getDATABASE()
/*      */   {
/* 1866 */     return getPropValue(60);
/*      */   }
/*      */ 
/*      */   public void setINTERNAL_QUERY_TIMEOUT(String paramString)
/*      */   {
/* 1874 */     setPropValue(61, paramString);
/*      */   }
/*      */ 
/*      */   public String getINTERNAL_QUERY_TIMEOUT()
/*      */   {
/* 1882 */     return getPropValue(61);
/*      */   }
/*      */ 
/*      */   public void setDEFAULT_QUERY_TIMEOUT(String paramString)
/*      */   {
/* 1890 */     setPropValue(62, paramString);
/*      */   }
/*      */ 
/*      */   public String getDEFAULT_QUERY_TIMEOUT()
/*      */   {
/* 1898 */     return getPropValue(62);
/*      */   }
/*      */ 
/*      */   public void setCRC(String paramString)
/*      */   {
/* 1907 */     setPropValue(63, paramString);
/*      */   }
/*      */ 
/*      */   public String getCRC()
/*      */   {
/* 1915 */     return getPropValue(63);
/*      */   }
/*      */ 
/*      */   public void setJ2EE_TCK_COMPLIANT(String paramString)
/*      */   {
/* 1923 */     setPropValue(64, paramString);
/*      */   }
/*      */ 
/*      */   public String getJ2EE_TCK_COMPLIANT()
/*      */   {
/* 1931 */     return getPropValue(64);
/*      */   }
/*      */ 
/*      */   public void setJCE_PROVIDER_CLASS(String paramString)
/*      */   {
/* 1940 */     setPropValue(66, paramString);
/*      */   }
/*      */ 
/*      */   public String getJCE_PROVIDER_CLASS()
/*      */   {
/* 1951 */     String str = getPropValue(66);
/* 1952 */     if ((str != null) && (!str instanceof String))
/*      */     {
/* 1954 */       str = str.getClass().getName();
/*      */     }
/* 1956 */     return (String)str;
/*      */   }
/*      */ 
/*      */   public void setRETRY_WITH_NO_ENCRYPTION(String paramString)
/*      */   {
/* 1964 */     setPropValue(67, paramString);
/*      */   }
/*      */ 
/*      */   public String getRETRY_WITH_NO_ENCRYPTION()
/*      */   {
/* 1972 */     return getPropValue(67);
/*      */   }
/*      */ 
/*      */   public void setENABLE_BULK_LOAD(String paramString)
/*      */   {
/* 1980 */     setPropValue(68, paramString);
/*      */   }
/*      */ 
/*      */   public String getENABLE_BULK_LOAD()
/*      */   {
/* 1988 */     return getPropValue(68);
/*      */   }
/*      */ 
/*      */   public void setNEWPASSWORD(String paramString)
/*      */   {
/* 1996 */     setPropValue(69, paramString);
/*      */   }
/*      */ 
/*      */   public String getNEWPASSWORD()
/*      */   {
/* 2004 */     return getPropValue(69);
/*      */   }
/*      */ 
/*      */   public void setPROMPT_FOR_NEWPASSWORD(String paramString)
/*      */   {
/* 2012 */     setPropValue(70, paramString);
/*      */   }
/*      */ 
/*      */   public String getPROMPT_FOR_NEWPASSWORD()
/*      */   {
/* 2020 */     return getPropValue(70);
/*      */   }
/*      */ 
/*      */   public void setALLOW_LOADBALANCING(String paramString)
/*      */   {
/* 2028 */     setPropValue(71, paramString);
/*      */   }
/*      */ 
/*      */   public String getALLOW_LOADBALANCING()
/*      */   {
/* 2036 */     return getPropValue(71);
/*      */   }
/*      */ 
/*      */   public void setALLOW_CONTEXT_MIGRATION(String paramString)
/*      */   {
/* 2044 */     setPropValue(72, paramString);
/*      */   }
/*      */ 
/*      */   public String getALLOW_CONTEXT_MIGRATION()
/*      */   {
/* 2052 */     return getPropValue(72);
/*      */   }
/*      */ 
/*      */   public void setALTERNATE_SERVER_NAME(String paramString)
/*      */   {
/* 2060 */     setPropValue(73, paramString);
/*      */   }
/*      */ 
/*      */   public String getALTERNATE_SERVER_NAME()
/*      */   {
/* 2068 */     return getPropValue(73);
/*      */   }
/*      */ 
/*      */   public void setIGNORE_WARNINGS(String paramString)
/*      */   {
/* 2076 */     setPropValue(74, paramString);
/*      */   }
/*      */ 
/*      */   public String getIGNORE_WARNINGS()
/*      */   {
/* 2084 */     return getPropValue(74);
/*      */   }
/*      */ 
/*      */   public void setOPTIMIZE_FOR_PERFORMANCE(String paramString)
/*      */   {
/* 2092 */     setPropValue(75, paramString);
/*      */   }
/*      */ 
/*      */   public String getOPTIMIZE_FOR_PERFORMANCE()
/*      */   {
/* 2100 */     return getPropValue(75);
/*      */   }
/*      */ 
/*      */   public void setDELETE_WARNINGS_FROM_EXCEPTION_CHAIN(String paramString)
/*      */   {
/* 2108 */     setPropValue(76, paramString);
/*      */   }
/*      */ 
/*      */   public String getDELETE_WARNINGS_FROM_EXCEPTION_CHAIN()
/*      */   {
/* 2116 */     return getPropValue(76);
/*      */   }
/*      */ 
/*      */   public void setENABLE_RAWBULK_INTERFACE(String paramString)
/*      */   {
/* 2124 */     setPropValue(78, paramString);
/*      */   }
/*      */ 
/*      */   public String getENABLE_RAWBULK_INTERFACE()
/*      */   {
/* 2132 */     return getPropValue(78);
/*      */   }
/*      */ 
/*      */   public void setENABLE_LOB_LOCATOR(String paramString)
/*      */   {
/* 2140 */     setPropValue(79, paramString);
/*      */   }
/*      */ 
/*      */   public String getENABLE_LOB_LOCATOR()
/*      */   {
/* 2148 */     return getPropValue(79);
/*      */   }
/*      */ 
/*      */   public void setEXECUTE_BATCH_PAST_ERRORS(String paramString)
/*      */   {
/* 2156 */     setPropValue(80, paramString);
/*      */   }
/*      */ 
/*      */   public String getEXECUTE_BATCH_PAST_ERRORS()
/*      */   {
/* 2164 */     return getPropValue(80);
/*      */   }
/*      */ 
/*      */   public void setSETMAXROWS_AFFECTS_SELECT_ONLY(String paramString)
/*      */   {
/* 2172 */     setPropValue(81, paramString);
/*      */   }
/*      */ 
/*      */   public String getSETMAXROWS_AFFECTS_SELECT_ONLY()
/*      */   {
/* 2180 */     return getPropValue(81);
/*      */   }
/*      */ 
/*      */   public void setRELEASE_LOCKS_ON_CURSOR_CLOSE(String paramString)
/*      */   {
/* 2188 */     setPropValue(82, paramString);
/*      */   }
/*      */ 
/*      */   public String getRELEASE_LOCKS_ON_CURSOR_CLOSE()
/*      */   {
/* 2196 */     return getPropValue(82);
/*      */   }
/*      */ 
/*      */   public void setSEND_BATCHPARAMS_IMMEDIATE(String paramString)
/*      */   {
/* 2204 */     setPropValue(83, paramString);
/*      */   }
/*      */ 
/*      */   public String getSEND_BATCHPARAMS_IMMEDIATE()
/*      */   {
/* 2212 */     return getPropValue(83);
/*      */   }
/*      */ 
/*      */   public void setHOMOGENEOUS_BATCH(String paramString)
/*      */   {
/* 2220 */     setPropValue(84, paramString);
/*      */   }
/*      */ 
/*      */   public String getHOMOGENEOUS_BATCH()
/*      */   {
/* 2228 */     return getPropValue(84);
/*      */   }
/*      */ 
/*      */   public void setEARLY_BATCH_READ_THRESHOLD(String paramString)
/*      */   {
/* 2236 */     setPropValue(85, paramString);
/*      */   }
/*      */ 
/*      */   public void setOPTIMIZE_STRING_CONVERSIONS(String paramString)
/*      */   {
/* 2244 */     setPropValue(86, paramString);
/*      */   }
/*      */ 
/*      */   public String getOPTIMIZE_STRING_CONVERSIONS()
/*      */   {
/* 2252 */     return getPropValue(86);
/*      */   }
/*      */ 
/*      */   public void setSUPPRESS_CONTROL_TOKEN(String paramString)
/*      */   {
/* 2260 */     setPropValue(87, paramString);
/*      */   }
/*      */ 
/*      */   public String getSUPPRESS_CONTROL_TOKEN()
/*      */   {
/* 2268 */     return getPropValue(87);
/*      */   }
/*      */ 
/*      */   public void setSUPPRESS_ROW_FORMAT2(String paramString)
/*      */   {
/* 2276 */     setPropValue(88, paramString);
/*      */   }
/*      */ 
/*      */   public String getSUPPRESS_ROW_FORMAT2()
/*      */   {
/* 2284 */     return getPropValue(88);
/*      */   }
/*      */ 
/*      */   public void setSUPPRESS_ROW_FORMAT(String paramString)
/*      */   {
/* 2292 */     setPropValue(89, paramString);
/*      */   }
/*      */ 
/*      */   public String getSUPPRESS_ROW_FORMAT()
/*      */   {
/* 2300 */     return getPropValue(89);
/*      */   }
/*      */ 
/*      */   public String getCONNECTION_RETRY_COUNT()
/*      */   {
/* 2308 */     return getPropValue(94);
/*      */   }
/*      */ 
/*      */   public void setCONNECTION_RETRY_COUNT(String paramString)
/*      */   {
/* 2316 */     setPropValue(94, paramString);
/*      */   }
/*      */ 
/*      */   public String getCONNECTION_RETRY_DELAY()
/*      */   {
/* 2324 */     return getPropValue(95);
/*      */   }
/*      */ 
/*      */   public void setCONNECTION_RETRY_DELAY(String paramString)
/*      */   {
/* 2332 */     setPropValue(95, paramString);
/*      */   }
/*      */ 
/*      */   public String getHADR_MODE()
/*      */   {
/* 2340 */     return getPropValue(96);
/*      */   }
/*      */ 
/*      */   public void setHADR_MODE(String paramString)
/*      */   {
/* 2348 */     setPropValue(96, paramString);
/*      */   }
/*      */ 
/*      */   public String getCONNECT_READONLY()
/*      */   {
/* 2356 */     return getPropValue(97);
/*      */   }
/*      */ 
/*      */   public void setCONNECT_READONLY(String paramString)
/*      */   {
/* 2364 */     setPropValue(97, paramString);
/*      */   }
/*      */ 
/*      */   public String getENABLE_REDIRECTION()
/*      */   {
/* 2372 */     return getPropValue(98);
/*      */   }
/*      */ 
/*      */   public void setENABLE_REDIRECTION(String paramString)
/*      */   {
/* 2380 */     setPropValue(98, paramString);
/*      */   }
/*      */ 
/*      */   public void setSUPPRESS_PARAM_FORMAT(String paramString)
/*      */   {
/* 2388 */     setPropValue(90, paramString);
/*      */   }
/*      */ 
/*      */   public String getSUPPRESS_PARAM_FORMAT()
/*      */   {
/* 2396 */     return getPropValue(90);
/*      */   }
/*      */ 
/*      */   public String getENABLE_FUNCTIONALITY_GROUP()
/*      */   {
/* 2404 */     return getPropValue(91);
/*      */   }
/*      */ 
/*      */   public void setENABLE_FUNCTIONALITY_GROUP(String paramString)
/*      */   {
/* 2412 */     setPropValue(91, paramString);
/*      */   }
/*      */ 
/*      */   public String getSTRIP_BLANKS()
/*      */   {
/* 2420 */     return getPropValue(92);
/*      */   }
/*      */ 
/*      */   public void setSTRIP_BLANKS(String paramString)
/*      */   {
/* 2428 */     setPropValue(92, paramString);
/*      */   }
/*      */ 
/*      */   public String getSET_ENCRYPTED_COLUMN_VALUES_AS_CIPHERTEXT()
/*      */   {
/* 2436 */     return getPropValue(99);
/*      */   }
/*      */ 
/*      */   public void setSET_ENCRYPTED_COLUMN_VALUES_AS_CIPHERTEXT(String paramString)
/*      */   {
/* 2444 */     setPropValue(99, paramString);
/*      */   }
/*      */ 
/*      */   public String getEARLY_BATCH_READ_THRESHOLD()
/*      */   {
/* 2452 */     return getPropValue(85);
/*      */   }
/*      */ 
/*      */   public void setINTERNAL_READ_BUFFER_LIMIT(String paramString)
/*      */   {
/* 2460 */     setPropValue(93, paramString);
/*      */   }
/*      */ 
/*      */   public String getINTERNAL_READ_BUFFER_LIMIT()
/*      */   {
/* 2468 */     return getPropValue(93);
/*      */   }
/*      */ 
/*      */   public String getENABLE_SSL()
/*      */   {
/* 2476 */     return getPropValue(100);
/*      */   }
/*      */ 
/*      */   public void setENABLE_SSL(String paramString)
/*      */   {
/* 2484 */     setPropValue(100, paramString);
/*      */   }
/*      */ 
/*      */   public String getSSL_HOSTNAME_IN_CERT()
/*      */   {
/* 2492 */     return getPropValue(101);
/*      */   }
/*      */ 
/*      */   public void setSSL_HOSTNAME_IN_CERT(String paramString)
/*      */   {
/* 2500 */     setPropValue(101, paramString);
/*      */   }
/*      */ 
/*      */   public String getSSL_TRUST_ALL_CERTS()
/*      */   {
/* 2508 */     return getPropValue(102);
/*      */   }
/*      */ 
/*      */   public void setSSL_TRUST_ALL_CERTS(String paramString)
/*      */   {
/* 2516 */     setPropValue(102, paramString);
/*      */   }
/*      */ 
/*      */   protected void setPortNumber(String paramString)
/*      */   {
/* 2535 */     setPortNumber(Integer.parseInt(paramString));
/*      */   }
/*      */ 
/*      */   public SybProperty getSybProperty()
/*      */   {
/* 2549 */     return this._sybProperty;
/*      */   }
/*      */ 
/*      */   protected void setSybProperty(SybProperty paramSybProperty)
/*      */   {
/* 2565 */     this._sybProperty = paramSybProperty;
/*      */   }
/*      */ 
/*      */   protected void setPropValue(int paramInt, String paramString)
/*      */   {
/* 2573 */     this._sybProperty.setProperty(paramInt, paramString, true);
/*      */   }
/*      */ 
/*      */   protected void setPropValue(int paramInt, Object paramObject)
/*      */   {
/* 2578 */     this._sybProperty.setProperty(paramInt, paramObject, true);
/*      */   }
/*      */ 
/*      */   protected String getPropValue(int paramInt)
/*      */   {
/* 2583 */     String str1 = null;
/* 2584 */     String str2 = SybProperty.PROPNAME[paramInt];
/*      */ 
/* 2589 */     Object localObject = this._sybProperty.getConnProperty(str2);
/* 2590 */     switch (paramInt)
/*      */     {
/*      */     case 37:
/* 2593 */       if (localObject == null)
/*      */         break label84;
/* 2595 */       str1 = localObject.getClass().getName(); break;
/*      */     default:
/* 2599 */       if (localObject == null)
/*      */         break label84;
/* 2601 */       if (localObject instanceof EncryptedValue)
/*      */       {
/* 2603 */         str1 = ((EncryptedValue)localObject).getValue(); break label84:
/*      */       }
/*      */ 
/* 2607 */       str1 = localObject.toString();
/*      */     }
/*      */ 
/* 2612 */     label84: return str1;
/*      */   }
/*      */ 
/*      */   protected synchronized void checkSybProps()
/*      */   {
/* 2617 */     if (this._sybProperty != null)
/*      */       return;
/* 2619 */     this._sybProperty = new SybProperty(this._version);
/*      */   }
/*      */ 
/*      */   protected final void setAddressList(Vector paramVector)
/*      */   {
/* 2658 */     this._addressList = paramVector;
/*      */ 
/* 2665 */     String str = (String)this._addressList.elementAt(0);
/* 2666 */     this._serverName = str.substring(0, str.indexOf(":"));
/* 2667 */     setPortNumber(str.substring(str.indexOf(":") + 1));
/*      */   }
/*      */ 
/*      */   protected synchronized SybUrlProvider createSybUrlProvider()
/*      */     throws SQLException
/*      */   {
/* 2690 */     return new UrlProvider();
/*      */   }
/*      */ 
/*      */   protected void registerWithDriverManager()
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean isWrapperFor(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 2830 */     return paramClass.isInstance(this);
/*      */   }
/*      */ 
/*      */   public Object unwrap(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 2838 */     SybDataSource localSybDataSource = null;
/*      */     try
/*      */     {
/* 2841 */       localSybDataSource = this;
/*      */     }
/*      */     catch (ClassCastException localClassCastException)
/*      */     {
/* 2845 */       ErrorMessage.raiseError("JZ031", paramClass.getName());
/*      */     }
/*      */ 
/* 2848 */     return localSybDataSource;
/*      */   }
/*      */ 
/*      */   class UrlProvider extends SybUrlProviderImplBase
/*      */   {
/*      */     UrlProvider()
/*      */       throws SQLException
/*      */     {
/* 2734 */       SybDataSource.this._sybProperty.verifyProps();
/*      */ 
/* 2737 */       this._sybProps = ((SybProperty)SybDataSource.this._sybProperty.clone());
/*      */ 
/* 2741 */       this._protocol = SybUrlManager.loadProtocol(SybDataSource.this._networkProtocol);
/*      */ 
/* 2744 */       this._dbName = SybDataSource.this._databaseName;
/*      */ 
/* 2746 */       String str1 = null;
/*      */       try
/*      */       {
/* 2749 */         str1 = SybDataSource.this._sybProperty.getString(42);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/*      */       }
/*      */ 
/* 2756 */       if (SybDataSource.this._rmNameSetByMethod)
/*      */       {
/* 2760 */         this._rmName = SybDataSource.this._resourceManagerName;
/*      */       }
/* 2762 */       else if (str1 != null)
/*      */       {
/* 2766 */         SybDataSource.access$402(SybDataSource.this, str1);
/* 2767 */         this._rmName = SybDataSource.this._resourceManagerName;
/*      */       }
/*      */       else
/*      */       {
/* 2771 */         this._rmName = SybDataSource.this._resourceManagerName;
/*      */       }
/* 2773 */       this._rmType = SybDataSource.this._resourceManagerType;
/*      */ 
/* 2779 */       if (SybDataSource.this._addressList == null)
/*      */       {
/* 2781 */         this._hostportList = new Vector(1);
/* 2782 */         this._hostportList.addElement(SybDataSource.this._serverName + ":" + SybDataSource.this._portNumber);
/*      */       }
/*      */       else
/*      */       {
/* 2786 */         int i = SybDataSource.this._addressList.size();
/* 2787 */         this._hostportList = new Vector(i);
/*      */ 
/* 2789 */         for (int j = 0; j < i; ++j)
/*      */         {
/* 2791 */           this._hostportList.addElement(SybDataSource.this._addressList.elementAt(j));
/*      */         }
/*      */       }
/* 2794 */       if (this._sybProps == null)
/*      */         return;
/* 2796 */       String str2 = this._sybProps.getString(73);
/* 2797 */       if (str2 == null)
/*      */         return;
/* 2799 */       ASAUDPUtil localASAUDPUtil = new ASAUDPUtil();
/* 2800 */       String str3 = localASAUDPUtil.lookupServer(str2);
/* 2801 */       this._hostportList.clear();
/* 2802 */       this._hostportList.addElement(str3);
/*      */     }
/*      */ 
/*      */     public void init(String paramString1, String paramString2, Properties paramProperties, SybProperty paramSybProperty)
/*      */     {
/*      */     }
/*      */ 
/*      */     public Vector getSecondaryHostPortList()
/*      */     {
/* 2821 */       return null;
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybDataSource
 * JD-Core Version:    0.5.4
 */