/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.LogUtil;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Clob;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class SybCharLob extends SybLob
/*     */   implements Clob
/*     */ {
/*  38 */   private static Logger LOG = Logger.getLogger(SybCharLob.class.getName());
/*  39 */   private static volatile long _logIdCounter = 0L;
/*     */ 
/*     */   public SybCharLob(String paramString, ProtocolContext paramProtocolContext, byte[] paramArrayOfByte, byte paramByte) throws SQLException
/*     */   {
/*  43 */     super(paramString, paramProtocolContext, paramArrayOfByte);
/*  44 */     this._lobType = paramByte;
/*  45 */     setLiteralSQL();
/*  46 */     this._lengthBuiltin = "char_length";
/*  47 */     this._logId = (paramString + "_Cl" + _logIdCounter++);
/*     */   }
/*     */ 
/*     */   public InputStream getAsciiStream() throws SQLException
/*     */   {
/*  52 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  54 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/*  56 */       LOG.fine(this._logId + " getAsciiStream()");
/*     */     }
/*     */ 
/*  60 */     return new LobLocatorBufferedInputStream(new LobLocatorInputStream(this), 16384);
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream()
/*     */     throws SQLException
/*     */   {
/*  66 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  68 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/*  70 */       LOG.fine(this._logId + " getCharacterStream()");
/*     */     }
/*     */ 
/*  74 */     return new LobLocatorBufferedReader(new LobLocatorReader(this), 16384);
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream(long paramLong1, long paramLong2)
/*     */     throws SQLException
/*     */   {
/*  80 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/*  82 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/*  84 */         LOG.finer(this._logId + " getCharacterStream(long = [" + paramLong1 + "], long = [" + paramLong2 + "])");
/*     */       }
/*  87 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/*  89 */         LOG.fine(this._logId + " getCharacterStream(long, long)");
/*     */       }
/*     */     }
/*     */ 
/*  93 */     long l = length();
/*  94 */     if ((paramLong1 < 1L) || (paramLong1 > l) || (paramLong1 + paramLong2 > l + 1L))
/*     */     {
/*  96 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*  98 */     return new LobLocatorBufferedReader(new LobLocatorReader(this, paramLong1, paramLong2), 16384);
/*     */   }
/*     */ 
/*     */   public String getSubString(long paramLong, int paramInt)
/*     */     throws SQLException
/*     */   {
/* 108 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 110 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 112 */         LOG.finer(this._logId + " getSubString(long = [" + paramLong + "], int = [" + paramInt + "])");
/*     */       }
/* 115 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 117 */         LOG.fine(this._logId + " getSubString(long, int)");
/*     */       }
/*     */     }
/*     */ 
/* 121 */     checkLocatorValidity();
/*     */ 
/* 123 */     if (paramLong < 1L)
/*     */     {
/* 125 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/* 127 */     else if (paramInt < 0)
/*     */     {
/* 129 */       ErrorMessage.raiseError("JZ038");
/*     */     }
/*     */ 
/* 132 */     MdaManager localMdaManager = this._context._conn.getMDA(this._context);
/* 133 */     PreparedStatement localPreparedStatement = localMdaManager.getMetaDataAccessor("LOB_GETBYTES", this._context);
/*     */ 
/* 135 */     localPreparedStatement.setInt(1, this._lobType);
/* 136 */     localPreparedStatement.setBytes(2, getLocator());
/* 137 */     localPreparedStatement.setLong(3, paramLong);
/* 138 */     localPreparedStatement.setInt(4, paramInt);
/* 139 */     ResultSet localResultSet = localPreparedStatement.executeQuery();
/*     */ 
/* 141 */     if (localResultSet.next())
/*     */     {
/* 143 */       String str = localResultSet.getString(1);
/* 144 */       int i = localResultSet.getInt(2);
/* 145 */       if (str != null)
/*     */       {
/* 147 */         return str;
/*     */       }
/* 149 */       if ((i != 0) && (paramLong <= i))
/*     */       {
/* 151 */         return "";
/*     */       }
/*     */     }
/* 154 */     return null;
/*     */   }
/*     */ 
/*     */   public long position(String paramString, long paramLong)
/*     */     throws SQLException
/*     */   {
/* 163 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 165 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 167 */         LOG.finer(this._logId + " position(String = [" + paramString + "], long = [" + paramLong + "])");
/*     */       }
/* 170 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 172 */         LOG.fine(this._logId + " position(String, long)");
/*     */       }
/*     */     }
/*     */ 
/* 176 */     checkLocatorValidity();
/*     */ 
/* 178 */     if (paramLong < 1L)
/*     */     {
/* 180 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 183 */     long l = -1L;
/*     */ 
/* 185 */     if (paramString != null)
/*     */     {
/* 187 */       CallableStatement localCallableStatement = (CallableStatement)this._mda.getMetaDataAccessor("SEARCH_LOB", this._context);
/*     */ 
/* 189 */       localCallableStatement.setInt(1, this._lobType);
/* 190 */       localCallableStatement.setBytes(2, null);
/* 191 */       localCallableStatement.setBytes(3, getLocator());
/* 192 */       localCallableStatement.setLong(4, paramLong);
/* 193 */       localCallableStatement.registerOutParameter(5, -5);
/* 194 */       localCallableStatement.setInt(6, 1);
/* 195 */       localCallableStatement.setString(7, paramString);
/* 196 */       localCallableStatement.execute();
/*     */ 
/* 198 */       l = localCallableStatement.getLong(5);
/* 199 */       if (l < paramLong)
/*     */       {
/* 201 */         l = -1L;
/*     */       }
/*     */     }
/* 204 */     return l;
/*     */   }
/*     */ 
/*     */   public long position(Clob paramClob, long paramLong)
/*     */     throws SQLException
/*     */   {
/* 213 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 215 */       if (LOG.isLoggable(Level.FINEST))
/*     */       {
/* 217 */         LOG.finest(LogUtil.logMethod(false, this._logId, " position", new Object[] { paramClob, new Long(paramLong) }));
/*     */       }
/* 220 */       else if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 222 */         LOG.finer(LogUtil.logMethod(true, this._logId, " position", new Object[] { paramClob, new Long(paramLong) }));
/*     */       }
/* 225 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 227 */         LOG.fine(this._logId + " position(Clob, long)");
/*     */       }
/*     */     }
/*     */ 
/* 231 */     return super.searchLocator((SybLob)paramClob, paramLong);
/*     */   }
/*     */ 
/*     */   public int setString(long paramLong, String paramString)
/*     */     throws SQLException
/*     */   {
/* 239 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 241 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 243 */         LOG.finer(this._logId + " setString(long = [" + paramLong + "], String = [" + paramString + "])");
/*     */       }
/* 246 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 248 */         LOG.fine(this._logId + " setString(long, String)");
/*     */       }
/*     */     }
/*     */ 
/* 252 */     if (paramLong < 1L)
/*     */     {
/* 254 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 257 */     if (paramString != null)
/*     */     {
/* 259 */       checkLocatorValidity();
/* 260 */       return setData(paramLong, paramString);
/*     */     }
/* 262 */     return 0;
/*     */   }
/*     */ 
/*     */   public int setString(long paramLong, String paramString, int paramInt1, int paramInt2)
/*     */     throws SQLException
/*     */   {
/* 271 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 273 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 275 */         LOG.finer(this._logId + " setString(long = [" + paramLong + "], String = [" + paramString + "], int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*     */       }
/* 279 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 281 */         LOG.fine(this._logId + " setString(long, String, int, int)");
/*     */       }
/*     */     }
/*     */ 
/* 285 */     if (paramLong < 1L)
/*     */     {
/* 287 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 290 */     if (paramString != null)
/*     */     {
/* 292 */       return setString(paramLong, paramString.substring(paramInt1 - 1, paramInt1 - 1 + paramInt2));
/*     */     }
/*     */ 
/* 295 */     return 0;
/*     */   }
/*     */ 
/*     */   public OutputStream setAsciiStream(long paramLong) throws SQLException
/*     */   {
/* 300 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 302 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 304 */         LOG.finer(this._logId + " setAsciiStream(long = [" + paramLong + "])");
/*     */       }
/* 306 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 308 */         LOG.fine(this._logId + " setAsciiStream(long)");
/*     */       }
/*     */     }
/*     */ 
/* 312 */     if ((paramLong < 1L) || (paramLong > length()))
/*     */     {
/* 314 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 317 */     return new BufferedOutputStream(new LobLocatorOutputStream(this, paramLong), 16384);
/*     */   }
/*     */ 
/*     */   public Writer setCharacterStream(long paramLong)
/*     */     throws SQLException
/*     */   {
/* 323 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 325 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 327 */         LOG.finer(this._logId + " setCharacterStyream(long = [" + paramLong + "])");
/*     */       }
/* 330 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 332 */         LOG.fine(this._logId + " setCharacterStream(long)");
/*     */       }
/*     */     }
/*     */ 
/* 336 */     if ((paramLong < 1L) || (paramLong > length()))
/*     */     {
/* 338 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 341 */     return new BufferedWriter(new LobLocatorWriter(this, paramLong), 16384);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 349 */       return getString();
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 356 */       throw new RuntimeException(localSQLException);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybCharLob
 * JD-Core Version:    0.5.4
 */