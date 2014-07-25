/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import com.sybase.jdbc3.utils.LogUtil;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public abstract class SybLob
/*     */ {
/*  36 */   private static Logger LOG = Logger.getLogger(SybLob.class.getName());
/*  37 */   protected String _logId = null;
/*     */   public static final byte RETURN_TYPE_LONG = 1;
/*     */   public static final byte RETURN_TYPE_BYTEARRAY = 2;
/*     */   public static final byte RETURN_TYPE_ASCII_STREAM = 3;
/*     */   public static final byte RETURN_TYPE_CHAR_STREAM = 4;
/*     */   public static final byte RETURN_TYPE_BIN_STREAM = 5;
/*     */   public static final byte LOB_TYPE_IMAGE = 0;
/*     */   public static final byte LOB_TYPE_TEXT = 1;
/*     */   public static final byte LOB_TYPE_UNITEXT = 2;
/*  55 */   protected boolean _freeCalled = false;
/*     */   private byte[] _locator;
/*     */   private String _locatorHexString;
/*     */   private StringBuffer _lobLiteralSql;
/*     */   protected byte _lobType;
/*     */   protected String _lengthBuiltin;
/*     */   protected long _lobLength;
/*     */   protected ProtocolContext _context;
/*     */   protected MdaManager _mda;
/*     */   protected Statement _utilStmt;
/*     */   protected static final int OUT_STREAM_BUFFER_SIZE = 16384;
/*     */   protected static final int IN_STREAM_BUFFER_SIZE = 16384;
/*     */   private static final byte RETURN_TYPE_STRING = 0;
/*     */ 
/*     */   public SybLob()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SybLob(String paramString, ProtocolContext paramProtocolContext, byte[] paramArrayOfByte)
/*     */     throws SQLException
/*     */   {
/*  91 */     this._context = paramProtocolContext._conn.initProtocol();
/*  92 */     this._mda = this._context._conn.getMDA(this._context);
/*  93 */     this._utilStmt = this._context._conn.createInternalStatement();
/*  94 */     this._locator = paramArrayOfByte;
/*  95 */     if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 0))
/*     */     {
/*  97 */       this._locatorHexString = ("0x" + HexConverts.hexConvert(this._locator));
/*     */     }
/*  99 */     this._freeCalled = false;
/*     */   }
/*     */ 
/*     */   protected String getLobTypeName()
/*     */   {
/* 104 */     switch (this._lobType)
/*     */     {
/*     */     case 0:
/* 107 */       return "IMAGE";
/*     */     case 1:
/* 109 */       return "TEXT";
/*     */     case 2:
/* 111 */       return "UNITEXT";
/*     */     }
/* 113 */     return "UNKNOWN LOB TYPE";
/*     */   }
/*     */ 
/*     */   protected void setLiteralSQL()
/*     */   {
/* 118 */     this._lobLiteralSql = new StringBuffer("locator_literal(");
/* 119 */     this._lobLiteralSql.append(getLobTypeName()).append("_locator").append(",").append(this._locatorHexString).append(")");
/*     */   }
/*     */ 
/*     */   protected String getLocatorHexString()
/*     */   {
/* 125 */     return this._locatorHexString;
/*     */   }
/*     */ 
/*     */   public byte[] getLocator()
/*     */   {
/* 130 */     return this._locator;
/*     */   }
/*     */ 
/*     */   protected String getLobLiteralSql()
/*     */   {
/* 135 */     return this._lobLiteralSql.toString();
/*     */   }
/*     */ 
/*     */   public String getString() throws SQLException
/*     */   {
/* 140 */     return (String)getObject(0);
/*     */   }
/*     */ 
/*     */   public InputStream getResultSetAsciiStream() throws SQLException
/*     */   {
/* 145 */     return (InputStream)getObject(3);
/*     */   }
/*     */ 
/*     */   public Reader getResultSetCharacterStream() throws SQLException
/*     */   {
/* 150 */     return (Reader)getObject(4);
/*     */   }
/*     */ 
/*     */   public InputStream getResultSetBinaryStream() throws SQLException
/*     */   {
/* 155 */     return (InputStream)getObject(5);
/*     */   }
/*     */ 
/*     */   public long getLong() throws SQLException
/*     */   {
/* 160 */     return ((Long)getObject(1)).longValue();
/*     */   }
/*     */ 
/*     */   public byte[] getBytes() throws SQLException
/*     */   {
/* 165 */     return (byte[])getObject(2);
/*     */   }
/*     */ 
/*     */   public Object getObject(byte paramByte) throws SQLException
/*     */   {
/* 170 */     checkLocatorValidity();
/* 171 */     if (this._lobLength == 0L)
/*     */     {
/* 173 */       return null;
/*     */     }
/* 175 */     Object localObject = null;
/*     */ 
/* 177 */     PreparedStatement localPreparedStatement = this._mda.getMetaDataAccessor("LOB_GETLOB", this._context);
/*     */ 
/* 179 */     localPreparedStatement.setInt(1, this._lobType);
/* 180 */     localPreparedStatement.setBytes(2, getLocator());
/* 181 */     localPreparedStatement.setInt(3, 0);
/* 182 */     localPreparedStatement.setInt(4, -1);
/* 183 */     ResultSet localResultSet = localPreparedStatement.executeQuery();
/*     */ 
/* 185 */     if (localResultSet.next())
/*     */     {
/* 187 */       switch (paramByte)
/*     */       {
/*     */       case 0:
/* 190 */         localObject = localResultSet.getString(1);
/* 191 */         break;
/*     */       case 1:
/* 193 */         localObject = new Long(localResultSet.getLong(1));
/* 194 */         break;
/*     */       case 2:
/* 196 */         localObject = localResultSet.getBytes(1);
/* 197 */         break;
/*     */       case 3:
/* 199 */         localObject = localResultSet.getAsciiStream(1);
/* 200 */         break;
/*     */       case 4:
/* 202 */         localObject = localResultSet.getCharacterStream(1);
/* 203 */         break;
/*     */       case 5:
/* 205 */         localObject = localResultSet.getBinaryStream(1);
/*     */       }
/*     */     }
/*     */ 
/* 209 */     localResultSet = null;
/* 210 */     return localObject;
/*     */   }
/*     */ 
/*     */   public void truncate(long paramLong)
/*     */     throws SQLException
/*     */   {
/* 222 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 224 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 226 */         LOG.finer(this._logId + " truncate(long = [" + paramLong + "])");
/*     */       }
/* 228 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 230 */         LOG.fine(this._logId + " truncate(long)");
/*     */       }
/*     */     }
/*     */ 
/* 234 */     if (paramLong < 0L)
/*     */     {
/* 236 */       ErrorMessage.raiseError("JZ038");
/*     */     }
/* 238 */     checkLocatorValidity();
/* 239 */     PreparedStatement localPreparedStatement = this._mda.getMetaDataAccessor("TRUNCATE_LOB", this._context);
/*     */ 
/* 241 */     localPreparedStatement.setBytes(1, this._locator);
/* 242 */     localPreparedStatement.setLong(2, paramLong);
/* 243 */     localPreparedStatement.executeUpdate();
/*     */   }
/*     */ 
/*     */   public void free()
/*     */     throws SQLException
/*     */   {
/* 257 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 259 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 261 */       LOG.fine(this._logId + " free()");
/*     */     }
/*     */ 
/* 265 */     if (this._freeCalled)
/*     */       return;
/* 267 */     checkLocatorValidity();
/* 268 */     PreparedStatement localPreparedStatement = this._mda.getMetaDataAccessor("DEALLOCATE_LOCATOR", this._context);
/*     */ 
/* 270 */     localPreparedStatement.setBytes(1, this._locator);
/* 271 */     localPreparedStatement.executeUpdate();
/*     */ 
/* 273 */     this._freeCalled = true;
/*     */   }
/*     */ 
/*     */   protected void checkLocatorValidity()
/*     */     throws SQLException
/*     */   {
/* 286 */     CallableStatement localCallableStatement = (CallableStatement)this._mda.getMetaDataAccessor("LOCATOR_VALID", this._context);
/*     */ 
/* 288 */     localCallableStatement.setBytes(1, this._locator);
/* 289 */     localCallableStatement.registerOutParameter(2, 4);
/* 290 */     localCallableStatement.execute();
/*     */ 
/* 292 */     if (localCallableStatement.getInt(2) == 1)
/*     */       return;
/* 294 */     ErrorMessage.raiseError("JZ036");
/*     */   }
/*     */ 
/*     */   protected long searchLocator(SybLob paramSybLob, long paramLong)
/*     */     throws SQLException
/*     */   {
/* 312 */     checkLocatorValidity();
/*     */ 
/* 314 */     if (paramLong < 1L)
/*     */     {
/* 316 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 319 */     long l = -1L;
/*     */ 
/* 321 */     if (paramSybLob != null)
/*     */     {
/* 323 */       CallableStatement localCallableStatement = (CallableStatement)this._mda.getMetaDataAccessor("SEARCH_LOB", this._context);
/*     */ 
/* 325 */       localCallableStatement.setInt(1, this._lobType);
/* 326 */       localCallableStatement.setBytes(2, paramSybLob.getLocator());
/* 327 */       localCallableStatement.setBytes(3, this._locator);
/* 328 */       localCallableStatement.setLong(4, paramLong);
/* 329 */       localCallableStatement.registerOutParameter(5, -5);
/* 330 */       localCallableStatement.setInt(6, 0);
/* 331 */       localCallableStatement.setNull(7, 12);
/* 332 */       localCallableStatement.execute();
/*     */ 
/* 334 */       l = localCallableStatement.getLong(5);
/* 335 */       if (l < paramLong)
/*     */       {
/* 337 */         l = -1L;
/*     */       }
/*     */     }
/* 340 */     return l;
/*     */   }
/*     */ 
/*     */   protected int setData(long paramLong, String paramString)
/*     */     throws SQLException
/*     */   {
/* 355 */     String str = (this._lobType == 1) ? "TEXT_SETDATA" : "UNITEXT_SETDATA";
/*     */ 
/* 357 */     CallableStatement localCallableStatement = (CallableStatement)this._mda.getMetaDataAccessor(str, this._context);
/*     */ 
/* 359 */     localCallableStatement.setBytes(1, getLocator());
/* 360 */     localCallableStatement.setLong(2, paramLong);
/* 361 */     localCallableStatement.setString(3, paramString);
/* 362 */     localCallableStatement.registerOutParameter(4, 4);
/* 363 */     localCallableStatement.execute();
/*     */ 
/* 365 */     int i = localCallableStatement.getInt(4);
/*     */ 
/* 367 */     if ((i == 0) && (!paramString.equals("")))
/*     */     {
/* 369 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 372 */     return i;
/*     */   }
/*     */ 
/*     */   public long length()
/*     */     throws SQLException
/*     */   {
/* 382 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 384 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 386 */       LOG.fine(this._logId + " length()");
/*     */     }
/*     */ 
/* 390 */     checkLocatorValidity();
/*     */ 
/* 392 */     CallableStatement localCallableStatement = (CallableStatement)this._mda.getMetaDataAccessor("LOB_LENGTH", this._context);
/*     */ 
/* 394 */     localCallableStatement.setInt(1, this._lobType);
/* 395 */     localCallableStatement.setBytes(2, this._locator);
/* 396 */     localCallableStatement.registerOutParameter(3, -5);
/* 397 */     localCallableStatement.execute();
/*     */ 
/* 399 */     return localCallableStatement.getLong(3);
/*     */   }
/*     */ 
/*     */   public void setLobLength(long paramLong)
/*     */   {
/* 404 */     this._lobLength = paramLong;
/*     */   }
/*     */ 
/*     */   public byte getLobType()
/*     */   {
/* 409 */     return this._lobType;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybLob
 * JD-Core Version:    0.5.4
 */