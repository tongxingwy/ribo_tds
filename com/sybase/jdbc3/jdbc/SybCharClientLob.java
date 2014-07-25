/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.LogUtil;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.sql.Clob;
/*     */ import java.sql.SQLException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class SybCharClientLob extends SybLob
/*     */   implements Clob
/*     */ {
/*  35 */   private static Logger LOG = Logger.getLogger(SybCharClientLob.class.getName());
/*  36 */   private static volatile long _logIdCounter = 0L;
/*     */   private StringBuffer data;
/*     */ 
/*     */   public SybCharClientLob(String paramString, ProtocolContext paramProtocolContext, StringBuffer paramStringBuffer, byte paramByte)
/*     */     throws SQLException
/*     */   {
/*  47 */     this._logId = (paramString + "_Cc" + _logIdCounter++);
/*  48 */     this.data = paramStringBuffer;
/*  49 */     this._lobType = paramByte;
/*     */   }
/*     */ 
/*     */   public InputStream getAsciiStream() throws SQLException
/*     */   {
/*  54 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  56 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/*  58 */       LOG.fine(this._logId + " getAsciiStream()");
/*     */     }
/*     */ 
/*  62 */     checkClientLOBValidity();
/*  63 */     return new BufferedInputStream(new LobClientInputStream(this, 1L, length()), 16384);
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream()
/*     */     throws SQLException
/*     */   {
/*  69 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  71 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/*  73 */       LOG.fine(this._logId + " getCharacterStream()");
/*     */     }
/*     */ 
/*  77 */     checkClientLOBValidity();
/*  78 */     return new BufferedReader(new LobClientReader(this, 1L, length()), 16384);
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream(long paramLong1, long paramLong2) throws SQLException
/*     */   {
/*  83 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/*  85 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/*  87 */         LOG.finer(this._logId + " getCharacterStream(long = [" + paramLong1 + "], long = [" + paramLong2 + "])");
/*     */       }
/*  90 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/*  92 */         LOG.fine(this._logId + " getCharacterStream(long, long)");
/*     */       }
/*     */     }
/*     */ 
/*  96 */     checkClientLOBValidity();
/*  97 */     long l = length();
/*  98 */     if ((paramLong1 < 1L) || (paramLong1 > l) || (paramLong1 + paramLong2 > l))
/*     */     {
/* 100 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/* 102 */     return new BufferedReader(new LobClientReader(this, paramLong1, paramLong2), 16384);
/*     */   }
/*     */ 
/*     */   public StringBuffer getDataRef()
/*     */   {
/* 108 */     return this.data;
/*     */   }
/*     */ 
/*     */   public String getSubString(long paramLong, int paramInt)
/*     */     throws SQLException
/*     */   {
/* 117 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 119 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 121 */         LOG.finer(this._logId + " getSubString(long = [" + paramLong + "], long = [" + paramInt + "])");
/*     */       }
/* 124 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 126 */         LOG.fine(this._logId + " getSubString(long, int)");
/*     */       }
/*     */     }
/*     */ 
/* 130 */     checkClientLOBValidity();
/* 131 */     if (paramLong < 1L)
/*     */     {
/* 133 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/* 135 */     else if (paramInt < 0)
/*     */     {
/* 137 */       ErrorMessage.raiseError("JZ038");
/*     */     }
/* 139 */     long l = length();
/*     */ 
/* 141 */     if ((this.data == null) || (paramLong > l))
/*     */     {
/* 143 */       return null;
/*     */     }
/*     */ 
/* 146 */     if (paramInt == 0)
/*     */     {
/* 148 */       return "";
/*     */     }
/*     */ 
/* 151 */     if (paramLong + paramInt - 1L > l)
/*     */     {
/* 153 */       paramInt = (int)(l - paramLong + 1L);
/*     */     }
/*     */ 
/* 156 */     return this.data.substring((int)paramLong - 1, (int)paramLong + paramInt - 1);
/*     */   }
/*     */ 
/*     */   public long position(String paramString, long paramLong)
/*     */     throws SQLException
/*     */   {
/* 165 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 167 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 169 */         LOG.finer(this._logId + " position(String = [" + paramString + "], long = [" + paramLong + "])");
/*     */       }
/* 172 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 174 */         LOG.fine(this._logId + " position(String, long)");
/*     */       }
/*     */     }
/*     */ 
/* 178 */     checkClientLOBValidity();
/* 179 */     if (paramLong < 1L)
/*     */     {
/* 181 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 184 */     int i = this.data.indexOf(paramString, (int)paramLong - 1);
/*     */ 
/* 187 */     i = (i < 0) ? i : i + 1;
/* 188 */     return i;
/*     */   }
/*     */ 
/*     */   private StringBuffer getData()
/*     */   {
/* 193 */     return this.data;
/*     */   }
/*     */ 
/*     */   private void setData(StringBuffer paramStringBuffer)
/*     */   {
/* 198 */     this.data = paramStringBuffer;
/*     */   }
/*     */ 
/*     */   public long position(Clob paramClob, long paramLong)
/*     */     throws SQLException
/*     */   {
/* 207 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 209 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 211 */         LOG.finer(LogUtil.logMethod(true, this._logId, " position", new Object[] { paramClob, new Long(paramLong) }));
/*     */       }
/* 214 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 216 */         LOG.fine(this._logId + " position(Clob, long)");
/*     */       }
/*     */     }
/*     */ 
/* 220 */     checkClientLOBValidity();
/* 221 */     if ((paramClob == null) || (this.data == null))
/*     */     {
/* 223 */       return -1L;
/*     */     }
/* 225 */     return position(((SybCharClientLob)paramClob).getString(), paramLong);
/*     */   }
/*     */ 
/*     */   public int setString(long paramLong, String paramString)
/*     */     throws SQLException
/*     */   {
/* 233 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 235 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 237 */         LOG.finer(this._logId + " setString(long = [" + paramLong + "], String = [" + paramString + "])");
/*     */       }
/* 240 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 242 */         LOG.fine(this._logId + " setString(long, String)");
/*     */       }
/*     */     }
/*     */ 
/* 246 */     checkClientLOBValidity();
/* 247 */     if (paramLong < 1L)
/*     */     {
/* 249 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 252 */     if (paramString != null)
/*     */     {
/* 254 */       long l = length();
/* 255 */       if ((l > 0L) && (paramLong > l + 1L))
/*     */       {
/* 257 */         ErrorMessage.raiseError("JZ037");
/*     */       }
/*     */ 
/* 262 */       if (this.data != null)
/*     */       {
/* 264 */         this.data.replace((int)paramLong - 1, (int)paramLong + paramString.length() - 1, paramString);
/*     */       }
/* 266 */       return paramString.length();
/*     */     }
/* 268 */     return 0;
/*     */   }
/*     */ 
/*     */   public int setString(long paramLong, String paramString, int paramInt1, int paramInt2)
/*     */     throws SQLException
/*     */   {
/* 277 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 279 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 281 */         LOG.finer(this._logId + " setString(long = [" + paramLong + "], String = [" + paramString + "], int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*     */       }
/* 285 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 287 */         LOG.fine(this._logId + " setString(long, String, int, int)");
/*     */       }
/*     */     }
/*     */ 
/* 291 */     checkClientLOBValidity();
/* 292 */     if (paramLong < 1L)
/*     */     {
/* 294 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 297 */     if (paramString != null)
/*     */     {
/* 299 */       return setString(paramLong, paramString.substring(paramInt1 - 1, paramInt1 - 1 + paramInt2));
/*     */     }
/*     */ 
/* 302 */     return 0;
/*     */   }
/*     */ 
/*     */   public OutputStream setAsciiStream(long paramLong) throws SQLException
/*     */   {
/* 307 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 309 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 311 */         LOG.finer(this._logId + " setAsciiStream(long = [" + paramLong + "])");
/*     */       }
/* 313 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 315 */         LOG.fine(this._logId + " setAsciiStream(long)");
/*     */       }
/*     */     }
/*     */ 
/* 319 */     checkClientLOBValidity();
/* 320 */     if ((paramLong < 1L) || (paramLong > length()))
/*     */     {
/* 322 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 325 */     return new BufferedOutputStream(new LobClientOutputStream(this, paramLong), 16384);
/*     */   }
/*     */ 
/*     */   public Writer setCharacterStream(long paramLong)
/*     */     throws SQLException
/*     */   {
/* 331 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 333 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 335 */         LOG.finer(this._logId + " setCharacterStream(long = [" + paramLong + "])");
/*     */       }
/* 338 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 340 */         LOG.fine(this._logId + " setCharacterStream(long)");
/*     */       }
/*     */     }
/*     */ 
/* 344 */     checkClientLOBValidity();
/* 345 */     if ((paramLong < 1L) || (paramLong > length()))
/*     */     {
/* 347 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 350 */     return new BufferedWriter(new LobClientWriter(this, paramLong), 16384);
/*     */   }
/*     */ 
/*     */   public String getString()
/*     */     throws SQLException
/*     */   {
/* 356 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 358 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 360 */       LOG.fine(this._logId + " getString()");
/*     */     }
/*     */ 
/* 364 */     checkClientLOBValidity();
/* 365 */     if (this.data != null)
/* 366 */       return this.data.toString();
/* 367 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 374 */       return getString();
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 382 */       throw new RuntimeException(localSQLException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void free()
/*     */     throws SQLException
/*     */   {
/* 397 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 399 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 401 */       LOG.fine(this._logId + " free()");
/*     */     }
/*     */ 
/* 405 */     if (this._freeCalled)
/*     */       return;
/* 407 */     this.data = null;
/* 408 */     this._freeCalled = true;
/*     */   }
/*     */ 
/*     */   private void checkClientLOBValidity()
/*     */     throws SQLException
/*     */   {
/* 415 */     if (!this._freeCalled)
/*     */       return;
/* 417 */     ErrorMessage.raiseError("JZ036");
/*     */   }
/*     */ 
/*     */   public byte[] getBytes()
/*     */     throws SQLException
/*     */   {
/* 423 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 425 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 427 */       LOG.fine(this._logId + " getBytes()");
/*     */     }
/*     */ 
/* 431 */     checkClientLOBValidity();
/* 432 */     return this.data.toString().getBytes();
/*     */   }
/*     */ 
/*     */   public void truncate(long paramLong)
/*     */     throws SQLException
/*     */   {
/* 443 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 445 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 447 */         LOG.finer(this._logId + " truncate(long = [" + paramLong + "])");
/*     */       }
/* 449 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 451 */         LOG.fine(this._logId + " truncate()");
/*     */       }
/*     */     }
/*     */ 
/* 455 */     checkClientLOBValidity();
/* 456 */     if (paramLong < 0L)
/*     */     {
/* 458 */       ErrorMessage.raiseError("JZ038");
/*     */     }
/* 460 */     this.data.setLength((int)paramLong);
/*     */   }
/*     */ 
/*     */   public long length()
/*     */     throws SQLException
/*     */   {
/* 469 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 471 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 473 */       LOG.fine(this._logId + " length()");
/*     */     }
/*     */ 
/* 477 */     checkClientLOBValidity();
/* 478 */     if (this.data == null)
/*     */     {
/* 480 */       return 0L;
/*     */     }
/* 482 */     return this.data.length();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybCharClientLob
 * JD-Core Version:    0.5.4
 */