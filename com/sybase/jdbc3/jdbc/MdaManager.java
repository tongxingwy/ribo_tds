/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class MdaManager
/*     */ {
/*  37 */   private static volatile long _logIdCounter = 0L;
/*  38 */   protected String _logId = null;
/*     */   protected int _prependDBName;
/*     */   public static final int MDA_VERSION_REQUEST = 0;
/*     */   public static final int MDA_JDBC_FULL_REQUEST = 1;
/*     */   public static final int MDA_JDBC_MINIMAL_REQUEST = 2;
/*     */   private static final String FUNCTIONCALL = "FUNCTIONCALL";
/*     */   public static final String CONNECTCONFIG = "CONNECTCONFIG";
/*     */   public static final String SET_CATALOG = "SET_CATALOG";
/*     */   public static final String GET_CATALOG = "GET_CATALOG";
/*     */   public static final String COLUMNTYPENAME = "COLUMNTYPENAME";
/*     */   public static final String GET_AUTOCOMMIT = "GET_AUTOCOMMIT";
/*     */   public static final String SET_AUTOCOMMIT_ON = "SET_AUTOCOMMIT_ON";
/*     */   public static final String SET_AUTOCOMMIT_OFF = "SET_AUTOCOMMIT_OFF";
/*     */   public static final String BEGIN_TRAN = "BEGIN_TRAN";
/*     */   public static final String SET_ISOLATION = "SET_ISOLATION";
/*     */   public static final String GET_ISOLATION = "GET_ISOLATION";
/*     */   public static final String SET_ROWCOUNT = "SET_ROWCOUNT";
/*     */   public static final String GET_READONLY = "GET_READONLY";
/*     */   public static final String SET_READONLY_TRUE = "SET_READONLY_TRUE";
/*     */   public static final String SET_READONLY_FALSE = "SET_READONLY_FALSE";
/*     */   public static final String ISREADONLY = "ISREADONLY";
/*     */   public static final String MDAVERSION = "MDAVERSION";
/*     */   public static final String PREPEND_DB_NAME = "PREPEND_DB_NAME";
/*     */   public static final String MAXLONGVARCHARLENGTH = "MAXLONGVARCHARLENGTH";
/*     */   public static final String MAXLONGVARBINARYLENGTH = "MAXLONGVARBINARYLENGTH";
/*     */   public static final String SAVEPOINT = "SAVEPOINT";
/*     */   public static final String ROLL_TO_SAVEPOINT = "ROLL_TO_SAVEPOINT";
/*     */   public static final String BULK_INSERT = "BULK_INSERT";
/*     */   public static final String INIT_TEXTLOCATOR = "INIT_TEXTLOCATOR";
/*     */   public static final String INIT_IMAGELOCATOR = "INIT_IMAGELOCATOR";
/*     */   public static final String INIT_UNITEXTLOCATOR = "INIT_UNITEXTLOCATOR";
/*     */   public static final String INIT_NULL_LOBS = "INIT_NULL_LOBS";
/*     */   public static final String LOB_GETLOB = "LOB_GETLOB";
/*     */   public static final String LOB_GETBYTES = "LOB_GETBYTES";
/*     */   public static final String TRUNCATE_LOB = "TRUNCATE_LOB";
/*     */   public static final String DEALLOCATE_LOCATOR = "DEALLOCATE_LOCATOR";
/*     */   public static final String LOCATOR_VALID = "LOCATOR_VALID";
/*     */   public static final String SEARCH_LOB = "SEARCH_LOB";
/*     */   public static final String TEXT_SETDATA = "TEXT_SETDATA";
/*     */   public static final String UNITEXT_SETDATA = "UNITEXT_SETDATA";
/*     */   public static final String IMAGE_SETDATA = "IMAGE_SETDATA";
/*     */   public static final String LOB_LENGTH = "LOB_LENGTH";
/*     */   public static final String SURROGATEPROCESS = "SURROGATEPROCESS";
/*     */   public static final String SET_LOGBULKCOPY_ON = "SET_LOGBULKCOPY_ON";
/*     */   public static final String SET_CLIENT_INFO = "SET_CLIENT_INFO";
/*     */   public static final String GET_CLIENT_INFO = "GET_CLIENT_INFO";
/*     */   public static final int BASELINE_VERSION = 0;
/*     */   public static final int MDA_VER_COMPLIANT = 3;
/*     */   public static final int HIGHEST_VERSION = 9;
/*     */   public static final int OUTERJOIN_VERSION = 1;
/*     */   public static final String DEFAULT_CHARSET = "DEFAULT_CHARSET";
/*     */   private SybConnection _conn;
/* 140 */   private Hashtable _functionMapTable = null;
/* 141 */   private Hashtable _metaDataAccess = null;
/*     */   protected int _version;
/*     */   protected int _requestedVersion;
/*     */   public static final int NOT_SET = -1;
/* 153 */   private int _maxLongvarcharLength = -1;
/* 154 */   private int _maxLongvarbinaryLength = -1;
/*     */ 
/*     */   MdaManager(String paramString, SybConnection paramSybConnection, ProtocolContext paramProtocolContext)
/*     */     throws SQLException
/*     */   {
/* 170 */     this(paramString, paramSybConnection, 9, paramProtocolContext);
/*     */   }
/*     */ 
/*     */   MdaManager(String paramString, SybConnection paramSybConnection, int paramInt, ProtocolContext paramProtocolContext)
/*     */     throws SQLException
/*     */   {
/* 176 */     this._logId = (paramString + "_Md" + _logIdCounter++);
/*     */ 
/* 179 */     this._conn = paramSybConnection;
/* 180 */     this._version = 0;
/* 181 */     this._requestedVersion = paramInt;
/*     */ 
/* 184 */     this._conn._protocol.getSendLock(paramProtocolContext);
/*     */     try
/*     */     {
/* 187 */       loadMetaData(paramProtocolContext);
/* 188 */       PreparedStatement localPreparedStatement = null;
/* 189 */       ResultSet localResultSet = null;
/*     */       try
/*     */       {
/* 192 */         localPreparedStatement = getMetaDataAccessor("MDAVERSION", paramProtocolContext);
/* 193 */         localResultSet = localPreparedStatement.executeQuery();
/* 194 */         localResultSet.next();
/* 195 */         this._version = localResultSet.getInt(1);
/* 196 */         localResultSet.close();
/*     */       }
/*     */       catch (SQLException localSQLException2)
/*     */       {
/* 206 */         if ("JZ0F2".equals(localSQLException2.getSQLState()))
/*     */         {
/* 208 */           throw localSQLException2;
/*     */         }
/* 210 */         checkForConnectTimeoutEx(localSQLException2);
/*     */       }
/*     */       try
/*     */       {
/* 214 */         localPreparedStatement = getMetaDataAccessor("PREPEND_DB_NAME", paramProtocolContext);
/* 215 */         localResultSet = localPreparedStatement.executeQuery();
/* 216 */         localResultSet.next();
/* 217 */         this._prependDBName = localResultSet.getInt(1);
/* 218 */         localPreparedStatement.close();
/*     */       }
/*     */       catch (SQLException localSQLException3)
/*     */       {
/* 228 */         if ("JZ0F2".equals(localSQLException3.getSQLState()))
/*     */         {
/* 230 */           throw localSQLException3;
/*     */         }
/* 232 */         checkForConnectTimeoutEx(localSQLException3);
/*     */ 
/* 234 */         this._prependDBName = 0;
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SQLException localSQLException1)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 244 */       this._conn._protocol.freeSendLock(paramProtocolContext);
/*     */     }
/*     */ 
/* 247 */     if ((this._version != 0) && (((this._requestedVersion < 3) || (this._version >= this._requestedVersion))))
/*     */     {
/*     */       return;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 255 */       ErrorMessage.raiseWarning("010SL");
/*     */     }
/*     */     catch (SQLWarning localSQLWarning)
/*     */     {
/* 259 */       this._conn.handleSQLE(localSQLWarning);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized String getFunctionMap(String paramString, ProtocolContext paramProtocolContext)
/*     */     throws SQLException
/*     */   {
/* 281 */     if (this._functionMapTable == null)
/*     */     {
/* 285 */       this._functionMapTable = new Hashtable();
/*     */ 
/* 287 */       localObject1 = null;
/*     */       try
/*     */       {
/* 291 */         localObject1 = getMetaDataAccessor("FUNCTIONCALL", paramProtocolContext);
/* 292 */         ResultSet localResultSet = ((PreparedStatement)localObject1).executeQuery();
/*     */ 
/* 294 */         while (localResultSet.next())
/*     */         {
/* 296 */           String str1 = localResultSet.getString(1).trim();
/* 297 */           String str2 = localResultSet.getString(2).trim();
/*     */ 
/* 300 */           this._functionMapTable.put(str1, str2);
/*     */         }
/* 302 */         localResultSet.close();
/*     */       }
/*     */       catch (SQLException localSQLException)
/*     */       {
/* 314 */         if ("JZ0F2".equals(localSQLException.getSQLState()))
/*     */         {
/* 318 */           this._functionMapTable = null;
/* 319 */           throw localSQLException;
/*     */         }
/*     */ 
/* 323 */         ErrorMessage.raiseError("JZ0SH");
/*     */       }
/*     */       finally
/*     */       {
/* 328 */         if (localObject1 != null)
/*     */         {
/* 330 */           ((PreparedStatement)localObject1).close();
/*     */         }
/*     */       }
/*     */     }
/* 334 */     Object localObject1 = (String)this._functionMapTable.get(paramString);
/*     */ 
/* 338 */     return (String)localObject1;
/*     */   }
/*     */ 
/*     */   public PreparedStatement getMetaDataAccessor(String paramString, ProtocolContext paramProtocolContext)
/*     */     throws SQLException
/*     */   {
/* 346 */     return getMetaDataAccessor(paramString, "", paramProtocolContext);
/*     */   }
/*     */ 
/*     */   public PreparedStatement getMetaDataAccessor(String paramString1, String paramString2, ProtocolContext paramProtocolContext)
/*     */     throws SQLException
/*     */   {
/* 355 */     return getMetaDataAccessor(paramString1, paramString2, null, paramProtocolContext);
/*     */   }
/*     */ 
/*     */   public PreparedStatement getMetaDataAccessor(String paramString1, String paramString2, String paramString3, ProtocolContext paramProtocolContext)
/*     */     throws SQLException
/*     */   {
/* 372 */     if (this._metaDataAccess == null)
/*     */     {
/* 374 */       ErrorMessage.raiseError("JZ0SJ");
/*     */     }
/*     */ 
/* 378 */     MetaDataAccessor localMetaDataAccessor = (MetaDataAccessor)this._metaDataAccess.get(paramString1);
/*     */ 
/* 380 */     if (localMetaDataAccessor == null)
/*     */     {
/* 382 */       ErrorMessage.raiseError("JZ0SJ");
/*     */     }
/* 384 */     if (localMetaDataAccessor._queryType == 3)
/*     */     {
/* 386 */       ErrorMessage.raiseWarning("010SK", paramString1);
/*     */     }
/* 388 */     Object localObject = null;
/* 389 */     switch (localMetaDataAccessor._queryType)
/*     */     {
/*     */     case 1:
/* 392 */       if ((paramString3 != null) && (this._prependDBName == 1))
/*     */       {
/* 394 */         localObject = this._conn.prepareInternalCall("{call " + paramString3 + ".." + localMetaDataAccessor._query + "}" + paramString2);
/*     */       }
/*     */       else
/*     */       {
/* 399 */         localObject = this._conn.prepareInternalCall("{call " + localMetaDataAccessor._query + "}" + paramString2);
/*     */       }
/*     */ 
/* 402 */       break;
/*     */     case 2:
/* 406 */       localObject = this._conn.prepareInternalStatement(localMetaDataAccessor._query + paramString2, false);
/*     */ 
/* 408 */       break;
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/* 413 */       localObject = new SybLiteral(this._logId, localMetaDataAccessor._query, localMetaDataAccessor._queryType);
/*     */ 
/* 415 */       break;
/*     */     case 3:
/*     */     default:
/* 418 */       ErrorMessage.raiseError("JZ0SJ");
/*     */     }
/* 420 */     if (!localObject instanceof SybLiteral)
/*     */     {
/* 422 */       SybStatement localSybStatement = (SybStatement)localObject;
/* 423 */       localSybStatement._context.setSponsor(paramProtocolContext);
/*     */     }
/* 425 */     return (PreparedStatement)localObject;
/*     */   }
/*     */ 
/*     */   private void loadMetaData(ProtocolContext paramProtocolContext)
/*     */     throws SQLException
/*     */   {
/* 438 */     if (this._metaDataAccess == null)
/*     */     {
/* 442 */       this._metaDataAccess = new Hashtable();
/*     */     }
/*     */ 
/* 445 */     SybCallableStatement localSybCallableStatement = null;
/*     */     try
/*     */     {
/* 448 */       long l = 0L;
/*     */ 
/* 450 */       localSybCallableStatement = (SybCallableStatement)this._conn.prepareInternalCall("{call sp_mda(?,?)}");
/* 451 */       if (this._conn.okToThrowLoginTimeoutException())
/*     */       {
/* 453 */         l = this._conn.getLoginTimeRemaining();
/*     */ 
/* 455 */         if (l > 0L)
/*     */         {
/* 461 */           l = l / 1000L + 1L;
/*     */ 
/* 464 */           localSybCallableStatement.setQueryTimeout((int)l);
/*     */         }
/* 466 */         else if (l < 0L)
/*     */         {
/* 471 */           ErrorMessage.raiseError("JZ00M");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 476 */         localSybCallableStatement.setQueryTimeout(0);
/*     */       }
/* 478 */       localSybCallableStatement._context.setSponsor(paramProtocolContext);
/*     */ 
/* 480 */       localSybCallableStatement.setInt(1, 1);
/* 481 */       localSybCallableStatement.setInt(2, this._requestedVersion);
/*     */       ResultSet localResultSet;
/*     */       try
/*     */       {
/* 485 */         localResultSet = localSybCallableStatement.executeQuery();
/*     */       }
/*     */       catch (SQLException localSQLException2)
/*     */       {
/* 494 */         if ("JZ0F2".equals(localSQLException2.getSQLState()))
/*     */         {
/* 496 */           throw localSQLException2;
/*     */         }
/* 498 */         checkForConnectTimeoutEx(localSQLException2);
/*     */ 
/* 501 */         if (localSybCallableStatement != null) localSybCallableStatement.close();
/* 502 */         localSybCallableStatement = (SybCallableStatement)this._conn.prepareInternalCall("{call dba.sp_mda(?,?)}");
/*     */ 
/* 506 */         if (l > 0L)
/*     */         {
/* 512 */           l = l / 1000L + 1L;
/*     */ 
/* 515 */           localSybCallableStatement.setQueryTimeout((int)l);
/*     */         }
/* 517 */         localSybCallableStatement._context.setSponsor(paramProtocolContext);
/* 518 */         localSybCallableStatement.setInt(1, 1);
/* 519 */         localSybCallableStatement.setInt(2, this._requestedVersion);
/* 520 */         localResultSet = localSybCallableStatement.executeQuery();
/*     */       }
/* 522 */       while (localResultSet.next())
/*     */       {
/* 524 */         String str1 = localResultSet.getString(1).trim();
/* 525 */         int i = localResultSet.getInt(2);
/* 526 */         String str2 = localResultSet.getString(3);
/* 527 */         if (str1.equalsIgnoreCase("EXTRANAMECHARS"))
/*     */         {
/* 529 */           str2 = str2 + "£".toLowerCase() + "¥".toLowerCase();
/*     */         }
/* 531 */         MetaDataAccessor localMetaDataAccessor = new MetaDataAccessor(i, str2);
/*     */ 
/* 535 */         this._metaDataAccess.put(str1, localMetaDataAccessor);
/*     */       }
/* 537 */       localResultSet.close();
/*     */     }
/*     */     catch (SQLException localSQLException1)
/*     */     {
/* 547 */       if ("JZ0F2".equals(localSQLException1.getSQLState()))
/*     */       {
/* 549 */         throw localSQLException1;
/*     */       }
/* 551 */       checkForConnectTimeoutEx(localSQLException1);
/* 552 */       ErrorMessage.raiseError("JZ0SJ");
/*     */     }
/*     */     finally
/*     */     {
/* 556 */       if (localSybCallableStatement != null)
/*     */       {
/* 558 */         localSybCallableStatement.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkForConnectTimeoutEx(SQLException paramSQLException)
/*     */     throws SQLException
/*     */   {
/* 568 */     if (!this._conn.okToThrowLoginTimeoutException())
/*     */     {
/* 570 */       return;
/*     */     }
/*     */ 
/* 573 */     SQLException localSQLException = paramSQLException;
/* 574 */     while (localSQLException != null)
/*     */     {
/* 576 */       if ("JZ00M".equals(localSQLException.getSQLState()))
/*     */       {
/* 578 */         throw paramSQLException;
/*     */       }
/*     */ 
/* 582 */       localSQLException = localSQLException.getNextException();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaxLongvarcharLength(ProtocolContext paramProtocolContext)
/*     */     throws SQLException
/*     */   {
/* 589 */     if (this._maxLongvarcharLength == -1)
/*     */     {
/* 591 */       this._maxLongvarcharLength = 2147483647;
/*     */       try
/*     */       {
/* 594 */         PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXLONGVARCHARLENGTH", paramProtocolContext);
/*     */ 
/* 596 */         ResultSet localResultSet = null;
/* 597 */         localResultSet = localPreparedStatement.executeQuery();
/* 598 */         localResultSet.next();
/* 599 */         this._maxLongvarcharLength = localResultSet.getInt(1);
/* 600 */         localPreparedStatement.close();
/*     */       }
/*     */       catch (SQLException localSQLException)
/*     */       {
/* 613 */         if (!"JZ0SJ".equals(localSQLException.getSQLState()))
/*     */         {
/* 615 */           throw localSQLException;
/*     */         }
/*     */       }
/*     */     }
/* 619 */     return this._maxLongvarcharLength;
/*     */   }
/*     */ 
/*     */   public int getMaxLongvarbinaryLength(ProtocolContext paramProtocolContext) throws SQLException
/*     */   {
/* 624 */     if (this._maxLongvarbinaryLength == -1)
/*     */     {
/* 626 */       this._maxLongvarbinaryLength = 2147483647;
/*     */       try
/*     */       {
/* 629 */         PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXLONGVARBINARYLENGTH", paramProtocolContext);
/*     */ 
/* 631 */         ResultSet localResultSet = null;
/* 632 */         localResultSet = localPreparedStatement.executeQuery();
/* 633 */         localResultSet.next();
/* 634 */         this._maxLongvarbinaryLength = localResultSet.getInt(1);
/* 635 */         localPreparedStatement.close();
/*     */       }
/*     */       catch (SQLException localSQLException)
/*     */       {
/* 648 */         if (!"JZ0SJ".equals(localSQLException.getSQLState()))
/*     */         {
/* 650 */           throw localSQLException;
/*     */         }
/*     */       }
/*     */     }
/* 654 */     return this._maxLongvarbinaryLength;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.MdaManager
 * JD-Core Version:    0.5.4
 */