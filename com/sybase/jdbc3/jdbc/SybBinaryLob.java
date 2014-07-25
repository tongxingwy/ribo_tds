/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.LogUtil;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.Blob;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class SybBinaryLob extends SybLob
/*     */   implements Blob
/*     */ {
/*  34 */   private static Logger LOG = Logger.getLogger(SybBinaryLob.class.getName());
/*  35 */   private static volatile long _logIdCounter = 0L;
/*     */ 
/*     */   public SybBinaryLob(String paramString, ProtocolContext paramProtocolContext, byte[] paramArrayOfByte)
/*     */     throws SQLException
/*     */   {
/*  40 */     super(paramString, paramProtocolContext, paramArrayOfByte);
/*  41 */     this._lobType = 0;
/*  42 */     setLiteralSQL();
/*  43 */     this._lengthBuiltin = "datalength";
/*  44 */     this._logId = (paramString + "_Bl" + _logIdCounter++);
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryStream() throws SQLException
/*     */   {
/*  49 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  51 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/*  53 */       LOG.fine(this._logId + " getBinaryStream()");
/*     */     }
/*     */ 
/*  57 */     return new LobLocatorBufferedInputStream(new LobLocatorInputStream(this), 16384);
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryStream(long paramLong1, long paramLong2)
/*     */     throws SQLException
/*     */   {
/*  63 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/*  65 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/*  67 */         LOG.finer(this._logId + " getBinaryStream(long = [" + paramLong1 + "], long = [" + paramLong2 + "])");
/*     */       }
/*  70 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/*  72 */         LOG.fine(this._logId + " getBinaryStream(long, long)");
/*     */       }
/*     */     }
/*     */ 
/*  76 */     long l = length();
/*  77 */     if ((paramLong1 < 1L) || (paramLong1 > l) || (paramLong1 + paramLong2 > l + 1L))
/*     */     {
/*  79 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/*  82 */     return new LobLocatorBufferedInputStream(new LobLocatorInputStream(this, paramLong1, paramLong2), 16384);
/*     */   }
/*     */ 
/*     */   public byte[] getBytes(long paramLong, int paramInt)
/*     */     throws SQLException
/*     */   {
/*  92 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/*  94 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/*  96 */         LOG.finer(this._logId + " getBytes(long = [" + paramLong + "], int = [" + paramInt + "])");
/*     */       }
/*  99 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 101 */         LOG.fine(this._logId + " getBytes(long, int)");
/*     */       }
/*     */     }
/*     */ 
/* 105 */     checkLocatorValidity();
/*     */ 
/* 107 */     if (paramLong < 1L)
/*     */     {
/* 109 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/* 111 */     else if (paramInt < 0)
/*     */     {
/* 113 */       ErrorMessage.raiseError("JZ038");
/*     */     }
/*     */ 
/* 116 */     MdaManager localMdaManager = this._context._conn.getMDA(this._context);
/* 117 */     PreparedStatement localPreparedStatement = localMdaManager.getMetaDataAccessor("LOB_GETBYTES", this._context);
/*     */ 
/* 119 */     localPreparedStatement.setInt(1, this._lobType);
/* 120 */     localPreparedStatement.setBytes(2, getLocator());
/* 121 */     localPreparedStatement.setLong(3, paramLong);
/* 122 */     localPreparedStatement.setInt(4, paramInt);
/* 123 */     ResultSet localResultSet = localPreparedStatement.executeQuery();
/*     */ 
/* 125 */     if (localResultSet.next())
/*     */     {
/* 127 */       byte[] arrayOfByte = localResultSet.getBytes(1);
/* 128 */       int i = localResultSet.getInt(2);
/* 129 */       if (arrayOfByte != null)
/*     */       {
/* 131 */         return arrayOfByte;
/*     */       }
/* 133 */       if ((i != 0) && (paramLong <= i))
/*     */       {
/* 135 */         return new byte[0];
/*     */       }
/*     */     }
/* 138 */     return null;
/*     */   }
/*     */ 
/*     */   public long position(byte[] paramArrayOfByte, long paramLong)
/*     */     throws SQLException
/*     */   {
/* 147 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 149 */       if (LOG.isLoggable(Level.FINEST))
/*     */       {
/* 151 */         LOG.finest(LogUtil.logMethod(false, this._logId, " position", new Object[] { paramArrayOfByte, new Long(paramLong) }));
/*     */       }
/* 154 */       else if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 156 */         LOG.finer(LogUtil.logMethod(true, this._logId, " position", new Object[] { paramArrayOfByte, new Long(paramLong) }));
/*     */       }
/* 159 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 161 */         LOG.fine(this._logId + " position(byte[], long)");
/*     */       }
/*     */     }
/*     */ 
/* 165 */     checkLocatorValidity();
/*     */ 
/* 167 */     if (paramLong < 1L)
/*     */     {
/* 169 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 172 */     long l = -1L;
/*     */ 
/* 174 */     if (paramArrayOfByte != null)
/*     */     {
/* 176 */       CallableStatement localCallableStatement = (CallableStatement)this._mda.getMetaDataAccessor("SEARCH_LOB", this._context);
/*     */ 
/* 178 */       localCallableStatement.setInt(1, this._lobType);
/* 179 */       localCallableStatement.setBytes(2, paramArrayOfByte);
/* 180 */       localCallableStatement.setBytes(3, getLocator());
/* 181 */       localCallableStatement.setLong(4, paramLong);
/* 182 */       localCallableStatement.registerOutParameter(5, -5);
/* 183 */       localCallableStatement.setInt(6, 1);
/* 184 */       localCallableStatement.setNull(7, 12);
/* 185 */       localCallableStatement.execute();
/*     */ 
/* 187 */       l = localCallableStatement.getLong(5);
/* 188 */       if (l < paramLong)
/*     */       {
/* 190 */         l = -1L;
/*     */       }
/*     */     }
/* 193 */     return l;
/*     */   }
/*     */ 
/*     */   public long position(Blob paramBlob, long paramLong)
/*     */     throws SQLException
/*     */   {
/* 202 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 204 */       if (LOG.isLoggable(Level.FINEST))
/*     */       {
/* 206 */         LOG.finest(LogUtil.logMethod(false, this._logId, " position", new Object[] { paramBlob, new Long(paramLong) }));
/*     */       }
/* 209 */       else if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 211 */         LOG.finer(LogUtil.logMethod(true, this._logId, " position", new Object[] { paramBlob, new Long(paramLong) }));
/*     */       }
/* 214 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 216 */         LOG.fine(this._logId + " position(Blob, long)");
/*     */       }
/*     */     }
/*     */ 
/* 220 */     return super.searchLocator((SybLob)paramBlob, paramLong);
/*     */   }
/*     */ 
/*     */   public int setBytes(long paramLong, byte[] paramArrayOfByte)
/*     */     throws SQLException
/*     */   {
/* 228 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 230 */       if (LOG.isLoggable(Level.FINEST))
/*     */       {
/* 232 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBytes", new Object[] { new Long(paramLong), paramArrayOfByte }));
/*     */       }
/* 235 */       else if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 237 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBytes", new Object[] { new Long(paramLong), paramArrayOfByte }));
/*     */       }
/* 240 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 242 */         LOG.fine(this._logId + " setBytes(long, byte[])");
/*     */       }
/*     */     }
/*     */ 
/* 246 */     if (paramLong < 1L)
/*     */     {
/* 248 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/* 250 */     int i = 0;
/* 251 */     if (paramArrayOfByte != null)
/*     */     {
/* 253 */       checkLocatorValidity();
/* 254 */       CallableStatement localCallableStatement = (CallableStatement)this._mda.getMetaDataAccessor("IMAGE_SETDATA", this._context);
/*     */ 
/* 256 */       localCallableStatement.setBytes(1, getLocator());
/* 257 */       localCallableStatement.setLong(2, paramLong);
/* 258 */       localCallableStatement.setBytes(3, paramArrayOfByte);
/* 259 */       localCallableStatement.registerOutParameter(4, 4);
/* 260 */       localCallableStatement.execute();
/*     */ 
/* 262 */       i = localCallableStatement.getInt(4);
/*     */ 
/* 264 */       if ((i == 0) && (paramArrayOfByte.length != 0))
/*     */       {
/* 266 */         ErrorMessage.raiseError("JZ037");
/*     */       }
/*     */     }
/* 269 */     return i;
/*     */   }
/*     */ 
/*     */   public int setBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws SQLException
/*     */   {
/* 278 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 280 */       if (LOG.isLoggable(Level.FINEST))
/*     */       {
/* 282 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBytes", new Object[] { new Long(paramLong), paramArrayOfByte, new Integer(paramInt1), new Integer(paramInt2) }));
/*     */       }
/* 285 */       else if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 287 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBytes", new Object[] { new Long(paramLong), paramArrayOfByte, new Integer(paramInt1), new Integer(paramInt2) }));
/*     */       }
/* 290 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 292 */         LOG.fine(this._logId + " setBytes(long, byte[], int, int)");
/*     */       }
/*     */     }
/*     */ 
/* 296 */     byte[] arrayOfByte = null;
/* 297 */     if (paramArrayOfByte != null)
/*     */     {
/* 299 */       arrayOfByte = new byte[paramInt2];
/* 300 */       System.arraycopy(paramArrayOfByte, paramInt1 - 1, arrayOfByte, 0, paramInt2);
/* 301 */       return setBytes(paramLong, arrayOfByte);
/*     */     }
/*     */ 
/* 304 */     return 0;
/*     */   }
/*     */ 
/*     */   public OutputStream setBinaryStream(long paramLong)
/*     */     throws SQLException
/*     */   {
/* 310 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 312 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 314 */         LOG.finer(this._logId + " setBinaryStream(long = [" + paramLong + "])");
/*     */       }
/* 316 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 318 */         LOG.fine(this._logId + " setBinaryStream(long)");
/*     */       }
/*     */     }
/*     */ 
/* 322 */     if ((paramLong < 1L) || (paramLong > length()))
/*     */     {
/* 324 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 327 */     return new BufferedOutputStream(new LobLocatorOutputStream(this, paramLong), 16384);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybBinaryLob
 * JD-Core Version:    0.5.4
 */