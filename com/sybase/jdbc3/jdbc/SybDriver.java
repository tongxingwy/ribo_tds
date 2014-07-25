/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.EncryptedValue;
/*     */ import com.sybase.jdbc3.utils.LogUtil;
/*     */ import com.sybase.jdbc3.utils.SybVersion;
/*     */ import com.sybase.jdbcx.Debug;
/*     */ import com.sybase.jdbcx.SybMessageHandler;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.DriverPropertyInfo;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class SybDriver
/*     */   implements com.sybase.jdbcx.SybDriver
/*     */ {
/*  83 */   private static Logger LOG = Logger.getLogger(SybDriver.class.getName());
/*  84 */   private static volatile long _logIdCounter = 0L;
/*     */   protected static final int HIGHEST_JCONNECT_VERSION = 100;
/*     */   public static final int DEFAULT_DRIVER_VERSION = 99;
/*  95 */   protected int _version = 99;
/*     */   private transient SybMessageHandler _msgHandler;
/*  99 */   private String _logId = null;
/*     */ 
/*     */   public SybDriver()
/*     */   {
/* 132 */     this._logId = ("Dr" + _logIdCounter++);
/*     */ 
/* 139 */     registerWithDriverManager();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public final Connection connect(String paramString, Properties paramProperties)
/*     */     throws SQLException
/*     */   {
/* 187 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 189 */       if (LOG.isLoggable(Level.FINEST))
/*     */       {
/* 191 */         LOG.finest(LogUtil.logMethod(false, this._logId, " connect", new Object[] { paramString, paramProperties }));
/*     */       }
/* 194 */       else if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 196 */         LOG.finer(LogUtil.logMethod(true, this._logId, " connect", new Object[] { paramString, paramProperties }));
/*     */       }
/* 199 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 201 */         LOG.fine(this._logId + " connect(String, Properties)");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 207 */     SybProperty localSybProperty = null;
/*     */ 
/* 209 */     if ((this._version == 0) || (this._version == 99) || (this._version == 100))
/*     */     {
/* 213 */       localSybProperty = new SybProperty(paramProperties, 7);
/*     */     }
/*     */     else
/*     */     {
/* 217 */       localSybProperty = new SybProperty(paramProperties, this._version);
/*     */     }
/*     */ 
/* 221 */     SybUrlManager localSybUrlManager = new SybUrlManager(paramString, paramProperties, localSybProperty);
/*     */ 
/* 224 */     SybUrlProvider localSybUrlProvider = localSybUrlManager.getUrlProvider();
/*     */ 
/* 226 */     if (localSybUrlProvider == null)
/*     */     {
/* 228 */       return null;
/*     */     }
/*     */ 
/* 232 */     SybConnection localSybConnection = new SybConnection(this._logId, localSybUrlProvider, paramString);
/*     */ 
/* 234 */     if (this._msgHandler != null)
/*     */     {
/* 236 */       localSybConnection.setSybMessageHandler(this._msgHandler);
/*     */     }
/*     */ 
/* 240 */     SQLWarning localSQLWarning = localSybProperty.getWarnings();
/* 241 */     if (localSQLWarning != null)
/*     */     {
/* 243 */       localSybConnection.handleSQLE(localSQLWarning);
/*     */     }
/* 245 */     return localSybConnection;
/*     */   }
/*     */ 
/*     */   public boolean acceptsURL(String paramString)
/*     */     throws SQLException
/*     */   {
/* 253 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 255 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 257 */         LOG.finer(this._logId + "acceptsURL(String = [" + paramString + "])");
/*     */       }
/* 259 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 261 */         LOG.fine(this._logId + "acceptsURL(String)");
/*     */       }
/*     */     }
/*     */ 
/* 265 */     int i = paramString.indexOf('/');
/* 266 */     if (i != -1)
/*     */     {
/* 268 */       paramString = paramString.substring(0, i);
/*     */     }
/*     */ 
/* 273 */     SybUrlManager localSybUrlManager = new SybUrlManager(paramString, null, null);
/*     */ 
/* 276 */     SybUrlProvider localSybUrlProvider = localSybUrlManager.getUrlProvider();
/* 277 */     if (localSybUrlProvider == null)
/*     */     {
/* 279 */       return false;
/*     */     }
/*     */ 
/* 287 */     if ((localSybUrlProvider instanceof SybJndiProvider) || (localSybUrlProvider instanceof SybSqlIniProvider))
/*     */     {
/* 289 */       return true;
/*     */     }
/*     */ 
/* 294 */     Protocol localProtocol = localSybUrlProvider.getProtocol();
/*     */ 
/* 296 */     return localProtocol != null;
/*     */   }
/*     */ 
/*     */   public DriverPropertyInfo[] getPropertyInfo(String paramString, Properties paramProperties)
/*     */     throws SQLException
/*     */   {
/* 307 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 309 */       if (LOG.isLoggable(Level.FINEST))
/*     */       {
/* 311 */         LOG.finest(LogUtil.logMethod(false, this._logId, " getPropertyInfo", new Object[] { paramString, paramProperties }));
/*     */       }
/* 314 */       else if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 316 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getPropertyInfo", new Object[] { paramString, paramProperties }));
/*     */       }
/* 319 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 321 */         LOG.fine(this._logId + "getPropertyInfo(String, java.util.Properties)");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 327 */     SybProperty localSybProperty = null;
/* 328 */     DriverPropertyInfo[] arrayOfDriverPropertyInfo = new DriverPropertyInfo[103];
/* 329 */     if ((this._version == 0) || (this._version == 99) || (this._version == 100))
/*     */     {
/* 333 */       localSybProperty = new SybProperty(paramProperties, 7);
/*     */     }
/*     */     else
/*     */     {
/* 337 */       localSybProperty = new SybProperty(paramProperties, this._version);
/*     */     }
/* 339 */     if (paramString != null)
/*     */     {
/* 342 */       i = paramString.indexOf('?');
/*     */ 
/* 345 */       if ((i > 0) && (i++ < paramString.length()))
/*     */       {
/* 347 */         localSybProperty.parsePropertyString(paramString.substring(i));
/*     */       }
/*     */     }
/* 350 */     for (int i = 0; i < 103; ++i)
/*     */     {
/* 352 */       arrayOfDriverPropertyInfo[i] = new DriverPropertyInfo(SybProperty.PROPNAME[i], (null == localSybProperty._propValue[i]) ? null : localSybProperty._propValue[i].toString());
/*     */ 
/* 354 */       arrayOfDriverPropertyInfo[i].required = false;
/*     */ 
/* 357 */       arrayOfDriverPropertyInfo[i].description = localSybProperty.getPropertyDescription(SybProperty.PROPNAME[i]);
/*     */     }
/*     */ 
/* 362 */     arrayOfDriverPropertyInfo[3].required = true;
/* 363 */     arrayOfDriverPropertyInfo[4].required = true;
/* 364 */     arrayOfDriverPropertyInfo[11].value = "";
/* 365 */     if (arrayOfDriverPropertyInfo[12].value.equals("0"))
/* 366 */       arrayOfDriverPropertyInfo[12].value = "512";
/* 367 */     String str = arrayOfDriverPropertyInfo[22].value.toString();
/* 368 */     if (str.length() > 1)
/*     */     {
/* 370 */       if (str.substring(2, str.length()).equals("0"))
/* 371 */         arrayOfDriverPropertyInfo[22].value = str.substring(0, 1);
/*     */     }
/* 373 */     else if (str.equals("0"))
/*     */     {
/* 375 */       arrayOfDriverPropertyInfo[22].value = String.valueOf(7);
/*     */     }
/*     */ 
/* 378 */     arrayOfDriverPropertyInfo[10].value += "\n\nConfidential property of Sybase, Inc.\nCopyright 1997, 2011\nSybase, Inc.  All rights reserved.\nUnpublished rights reserved under U.S. copyright laws.\nThis software contains confidential and trade secret information of Sybase,\nInc.  Use, duplication or disclosure of the software and documentation by\nthe U.S. Government is subject to restrictions set forth in a license\nagreement between the Government and Sybase, Inc. or other written\nagreement specifying the Government's rights to use the software and any\napplicable FAR provisions, for example, FAR 52.227-19.\n\nSybase, Inc. One Sybase Drive, Dublin, CA 94568\n";
/*     */ 
/* 380 */     return arrayOfDriverPropertyInfo;
/*     */   }
/*     */ 
/*     */   public int getMajorVersion()
/*     */   {
/* 390 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 392 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 394 */       LOG.fine(this._logId + "getMajorVersion()");
/*     */     }
/*     */ 
/* 399 */     return SybVersion.MAJOR_VERSION;
/*     */   }
/*     */ 
/*     */   public int getMinorVersion()
/*     */   {
/* 407 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 409 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 411 */       LOG.fine(this._logId + "getMinorVersion()");
/*     */     }
/*     */ 
/* 416 */     return SybVersion.MINOR_VERSION;
/*     */   }
/*     */ 
/*     */   public boolean jdbcCompliant()
/*     */   {
/* 425 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 427 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 429 */       LOG.fine(this._logId + "jdbcComliant()");
/*     */     }
/*     */ 
/* 434 */     return false;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setVersion(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 460 */     switch (paramInt) {
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/* 468 */       this._version = paramInt;
/* 469 */       return;
/*     */     case 0:
/* 471 */       this._version = 100;
/* 472 */       return;
/*     */     case 1:
/*     */     }
/* 474 */     ErrorMessage.raiseError("JZ0D6", "" + paramInt);
/*     */   }
/*     */ 
/*     */   public void setSybMessageHandler(SybMessageHandler paramSybMessageHandler)
/*     */   {
/* 487 */     this._msgHandler = paramSybMessageHandler;
/*     */   }
/*     */ 
/*     */   public SybMessageHandler getSybMessageHandler()
/*     */   {
/* 497 */     return this._msgHandler;
/*     */   }
/*     */ 
/*     */   public void setMessageHandler(SybMessageHandler paramSybMessageHandler)
/*     */   {
/* 508 */     setSybMessageHandler(paramSybMessageHandler);
/*     */   }
/*     */ 
/*     */   public SybMessageHandler getMessageHandler()
/*     */   {
/* 518 */     return getSybMessageHandler();
/*     */   }
/*     */ 
/*     */   public final Debug getDebug()
/*     */   {
/* 527 */     return new SybDebug();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public final void setRemotePassword(String paramString1, String paramString2, Properties paramProperties)
/*     */   {
/* 548 */     StringBuffer localStringBuffer = new StringBuffer();
/* 549 */     String str = SybProperty.PROPNAME[9];
/* 550 */     Object localObject = paramProperties.get(str);
/* 551 */     if ((localObject != null) && (localObject instanceof EncryptedValue))
/*     */     {
/* 553 */       localStringBuffer.append(((EncryptedValue)localObject).getValue());
/*     */     }
/* 555 */     else if (localObject != null)
/*     */     {
/* 557 */       localStringBuffer.append(localObject);
/*     */     }
/* 559 */     localStringBuffer.append(',');
/* 560 */     if (paramString1 != null)
/*     */     {
/* 562 */       localStringBuffer.append(escapeSpecialCharacters(paramString1));
/*     */     }
/* 564 */     localStringBuffer.append(',');
/* 565 */     if (paramString2 != null)
/*     */     {
/* 567 */       localStringBuffer.append(escapeSpecialCharacters(paramString2));
/*     */     }
/* 569 */     paramProperties.put(str, new EncryptedValue(localStringBuffer.toString()));
/*     */   }
/*     */ 
/*     */   private static StringBuffer escapeSpecialCharacters(String paramString)
/*     */   {
/* 575 */     StringBuffer localStringBuffer = new StringBuffer();
/* 576 */     int i = paramString.length();
/* 577 */     char c = '\000';
/* 578 */     for (int j = 0; j < i; ++j)
/*     */     {
/* 580 */       c = paramString.charAt(j);
/* 581 */       if ((c == '\\') || (c == ','))
/*     */       {
/* 583 */         localStringBuffer.append('\\');
/*     */       }
/* 585 */       localStringBuffer.append(c);
/*     */     }
/* 587 */     return localStringBuffer;
/*     */   }
/*     */ 
/*     */   public com.sybase.jdbcx.DynamicClassLoader getClassLoader(String paramString, Properties paramProperties)
/*     */   {
/* 601 */     LoaderConnection localLoaderConnection = new LoaderConnection(this, paramString, paramProperties);
/* 602 */     DynamicClassLoader localDynamicClassLoader = new DynamicClassLoader(localLoaderConnection);
/* 603 */     return localDynamicClassLoader;
/*     */   }
/*     */ 
/*     */   protected final SybConnection connect(String paramString, SybUrlProvider paramSybUrlProvider, int paramInt)
/*     */     throws SQLException
/*     */   {
/* 619 */     SybConnection localSybConnection = createConnection(paramString, paramSybUrlProvider, paramInt);
/* 620 */     if (this._msgHandler != null)
/*     */     {
/* 622 */       localSybConnection.setSybMessageHandler(this._msgHandler);
/*     */     }
/*     */ 
/* 627 */     SQLWarning localSQLWarning = paramSybUrlProvider.getSybProperty().getWarnings();
/*     */ 
/* 629 */     if (localSQLWarning != null)
/*     */     {
/* 631 */       localSybConnection.handleSQLE(localSQLWarning);
/*     */     }
/*     */ 
/* 634 */     return localSybConnection;
/*     */   }
/*     */ 
/*     */   protected final Connection connect(SybUrlProvider paramSybUrlProvider, int paramInt)
/*     */     throws SQLException
/*     */   {
/* 652 */     return connect(null, paramSybUrlProvider, paramInt);
/*     */   }
/*     */ 
/*     */   protected SybConnection createConnection(String paramString, SybUrlProvider paramSybUrlProvider, int paramInt)
/*     */     throws SQLException
/*     */   {
/* 672 */     SybConnection localSybConnection = null;
/*     */ 
/* 674 */     if (paramSybUrlProvider != null)
/*     */     {
/* 676 */       Protocol localProtocol = paramSybUrlProvider.getProtocol();
/*     */ 
/* 678 */       if (localProtocol != null)
/*     */       {
/* 680 */         localSybConnection = new SybConnection(this._logId, paramSybUrlProvider, paramString, paramInt);
/*     */       }
/*     */     }
/* 683 */     return localSybConnection;
/*     */   }
/*     */ 
/*     */   protected void registerWithDriverManager()
/*     */   {
/*     */     try
/*     */     {
/* 705 */       synchronized (DriverManager.class)
/*     */       {
/* 708 */         DriverManager.registerDriver(this);
/*     */ 
/* 710 */         Enumeration localEnumeration = DriverManager.getDrivers();
/* 711 */         while (localEnumeration.hasMoreElements())
/*     */         {
/* 713 */           Driver localDriver = (Driver)localEnumeration.nextElement();
/* 714 */           if ((!localDriver instanceof com.sybase.jdbcx.SybDriver) || (localDriver == this)) {
/*     */             continue;
/*     */           }
/* 717 */           DriverManager.deregisterDriver(localDriver);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 104 */     new SybDriver();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybDriver
 * JD-Core Version:    0.5.4
 */