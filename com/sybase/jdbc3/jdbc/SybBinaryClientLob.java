/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import com.sybase.jdbc3.utils.LogUtil;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.Blob;
/*     */ import java.sql.SQLException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class SybBinaryClientLob extends SybLob
/*     */   implements Blob
/*     */ {
/*  37 */   private static Logger LOG = Logger.getLogger(SybBinaryClientLob.class.getName());
/*     */ 
/*  39 */   private static volatile long _logIdCounter = 0L;
/*     */   private byte[] data;
/*     */ 
/*     */   public SybBinaryClientLob(String paramString, ProtocolContext paramProtocolContext, byte[] paramArrayOfByte)
/*     */     throws SQLException
/*     */   {
/*  51 */     this._logId = (paramString + "_Bc" + _logIdCounter++);
/*  52 */     this.data = paramArrayOfByte;
/*  53 */     this._lobType = 0;
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryStream() throws SQLException
/*     */   {
/*  58 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  60 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/*  62 */       LOG.fine(this._logId + " getBinaryStream()");
/*     */     }
/*     */ 
/*  66 */     checkClientLOBValidity();
/*  67 */     return new BufferedInputStream(new LobClientInputStream(this, 1L, length()), 16384);
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryStream(long paramLong1, long paramLong2)
/*     */     throws SQLException
/*     */   {
/*  73 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/*  75 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/*  77 */         LOG.finer(this._logId + " getBinaryStream(long = [" + paramLong1 + "], long = [" + paramLong2 + "])");
/*     */       }
/*  80 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/*  82 */         LOG.fine(this._logId + " getBinaryStream(long, long)");
/*     */       }
/*     */     }
/*     */ 
/*  86 */     checkClientLOBValidity();
/*  87 */     long l = length();
/*  88 */     if ((paramLong1 < 1L) || (paramLong1 > l) || (paramLong1 + paramLong2 > l + 1L))
/*     */     {
/*  90 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/*  93 */     return new BufferedInputStream(new LobClientInputStream(this, paramLong1, paramLong2), 16384);
/*     */   }
/*     */ 
/*     */   public byte[] getBytes()
/*     */     throws SQLException
/*     */   {
/*  99 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 101 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 103 */       LOG.fine(this._logId + " getBytes()");
/*     */     }
/*     */ 
/* 107 */     checkClientLOBValidity();
/* 108 */     return this.data;
/*     */   }
/*     */ 
/*     */   public byte[] getBytes(long paramLong, int paramInt)
/*     */     throws SQLException
/*     */   {
/* 118 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 120 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 122 */         LOG.finer(this._logId + " getBytes(long = [" + paramLong + "], int = [" + paramInt + "])");
/*     */       }
/* 125 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 127 */         LOG.fine(this._logId + " getBytes(long, int)");
/*     */       }
/*     */     }
/*     */ 
/* 131 */     checkClientLOBValidity();
/* 132 */     if (paramLong < 1L)
/*     */     {
/* 134 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/* 136 */     else if (paramInt < 0)
/*     */     {
/* 138 */       ErrorMessage.raiseError("JZ038");
/*     */     }
/*     */ 
/* 141 */     long l = length();
/*     */ 
/* 143 */     if ((this.data == null) || (paramLong > l))
/*     */     {
/* 145 */       return null;
/*     */     }
/*     */ 
/* 148 */     if (paramInt == 0)
/*     */     {
/* 150 */       return new byte[0];
/*     */     }
/*     */ 
/* 153 */     if (paramLong + paramInt - 1L > l)
/*     */     {
/* 155 */       paramInt = (int)(l - paramLong + 1L);
/*     */     }
/*     */ 
/* 158 */     byte[] arrayOfByte = new byte[paramInt];
/* 159 */     System.arraycopy(this.data, (int)paramLong - 1, arrayOfByte, 0, paramInt);
/* 160 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public long position(byte[] paramArrayOfByte, long paramLong)
/*     */     throws SQLException
/*     */   {
/* 170 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 172 */       if (LOG.isLoggable(Level.FINEST))
/*     */       {
/* 174 */         LOG.finest(LogUtil.logMethod(false, this._logId, " position", new Object[] { paramArrayOfByte, new Long(paramLong) }));
/*     */       }
/* 177 */       else if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 179 */         LOG.finer(LogUtil.logMethod(true, this._logId, " position", new Object[] { paramArrayOfByte, new Long(paramLong) }));
/*     */       }
/* 182 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 184 */         LOG.fine(this._logId + " position(byte[], long)");
/*     */       }
/*     */     }
/*     */ 
/* 188 */     checkClientLOBValidity();
/* 189 */     int i = (int)indexOf((byte[])getBytes(paramLong, (int)(this.data.length - paramLong)), paramArrayOfByte);
/*     */ 
/* 192 */     return (i == -1) ? i : i + paramLong;
/*     */   }
/*     */ 
/*     */   public long position(Blob paramBlob, long paramLong)
/*     */     throws SQLException
/*     */   {
/* 202 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 204 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 206 */         LOG.finer(LogUtil.logMethod(true, this._logId, " position", new Object[] { paramBlob, new Long(paramLong) }));
/*     */       }
/* 209 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 211 */         LOG.fine(this._logId + " position(Blob, long)");
/*     */       }
/*     */     }
/*     */ 
/* 215 */     checkClientLOBValidity();
/* 216 */     return position(((SybBinaryClientLob)paramBlob).getBytes(), paramLong);
/*     */   }
/*     */ 
/*     */   public long length()
/*     */     throws SQLException
/*     */   {
/* 225 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 227 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 229 */       LOG.fine(this._logId + " length()");
/*     */     }
/*     */ 
/* 233 */     checkClientLOBValidity();
/* 234 */     if (this.data == null)
/*     */     {
/* 236 */       return 0L;
/*     */     }
/* 238 */     return this.data.length;
/*     */   }
/*     */ 
/*     */   public int setBytes(long paramLong, byte[] paramArrayOfByte)
/*     */     throws SQLException
/*     */   {
/* 246 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 248 */       if (LOG.isLoggable(Level.FINEST))
/*     */       {
/* 250 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBytes", new Object[] { new Long(paramLong), paramArrayOfByte }));
/*     */       }
/* 253 */       else if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 255 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBytes", new Object[] { new Long(paramLong), paramArrayOfByte }));
/*     */       }
/* 258 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 260 */         LOG.fine(this._logId + " setBytes(long, byte[])");
/*     */       }
/*     */     }
/*     */ 
/* 264 */     checkClientLOBValidity();
/* 265 */     if (paramLong < 1L)
/*     */     {
/* 267 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/*     */ 
/* 270 */     if (paramArrayOfByte != null)
/*     */     {
/* 272 */       long l = length();
/*     */ 
/* 274 */       if (paramLong > l + 1L)
/*     */       {
/* 276 */         ErrorMessage.raiseError("JZ037");
/*     */       }
/*     */ 
/* 280 */       if (l > paramLong + paramArrayOfByte.length)
/*     */       {
/* 283 */         for (int i = 0; ; ++i) { if (i >= paramArrayOfByte.length)
/*     */             break label289;
/* 285 */           this.data[(i + (int)paramLong - 1)] = paramArrayOfByte[i]; }
/*     */ 
/*     */ 
/*     */       }
/*     */ 
/* 293 */       byte[] arrayOfByte = new byte[(int)(paramLong + paramArrayOfByte.length - 1L)];
/* 294 */       if (l > 0L)
/*     */       {
/* 296 */         System.arraycopy(this.data, 0, arrayOfByte, 0, (int)(paramLong - 1L));
/*     */       }
/* 298 */       if (paramArrayOfByte.length > 0)
/*     */       {
/* 300 */         System.arraycopy(paramArrayOfByte, 0, arrayOfByte, (int)paramLong - 1, paramArrayOfByte.length);
/*     */       }
/*     */ 
/* 303 */       this.data = arrayOfByte;
/*     */ 
/* 305 */       label289: return paramArrayOfByte.length;
/*     */     }
/*     */ 
/* 308 */     return 0;
/*     */   }
/*     */ 
/*     */   public int setBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws SQLException
/*     */   {
/* 317 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 319 */       if (LOG.isLoggable(Level.FINEST))
/*     */       {
/* 321 */         LOG.finest(LogUtil.logMethod(false, this._logId, "setBytes", new Object[] { new Long(paramLong), paramArrayOfByte, new Integer(paramInt1), new Integer(paramInt2) }));
/*     */       }
/* 324 */       else if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 326 */         LOG.finer(LogUtil.logMethod(true, this._logId, "setBytes", new Object[] { new Long(paramLong), paramArrayOfByte, new Integer(paramInt1), new Integer(paramInt2) }));
/*     */       }
/* 329 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 331 */         LOG.fine(this._logId + " setBytes(long, byte[], int, int)");
/*     */       }
/*     */     }
/*     */ 
/* 335 */     checkClientLOBValidity();
/* 336 */     byte[] arrayOfByte = null;
/* 337 */     if (paramArrayOfByte != null)
/*     */     {
/* 339 */       arrayOfByte = new byte[paramInt2];
/* 340 */       System.arraycopy(paramArrayOfByte, paramInt1 - 1, arrayOfByte, 0, paramInt2);
/* 341 */       return setBytes(paramLong, arrayOfByte);
/*     */     }
/* 343 */     return 0;
/*     */   }
/*     */ 
/*     */   public OutputStream setBinaryStream(long paramLong) throws SQLException
/*     */   {
/* 348 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 350 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 352 */         LOG.finer(this._logId + " setBinaryStream(long = [" + paramLong + "])");
/*     */       }
/* 354 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 356 */         LOG.fine(this._logId + " setBinaryStream(long)");
/*     */       }
/*     */     }
/*     */ 
/* 360 */     checkClientLOBValidity();
/* 361 */     if ((paramLong < 1L) || (paramLong > length()))
/*     */     {
/* 363 */       ErrorMessage.raiseError("JZ037");
/*     */     }
/* 365 */     return new BufferedOutputStream(new LobClientOutputStream(this, paramLong), 16384);
/*     */   }
/*     */ 
/*     */   public String getString()
/*     */     throws SQLException
/*     */   {
/* 371 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 373 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 375 */       LOG.fine(this._logId + " getString()");
/*     */     }
/*     */ 
/* 379 */     checkClientLOBValidity();
/* 380 */     if (this.data != null)
/*     */     {
/* 382 */       return HexConverts.hexConvert(this.data);
/*     */     }
/* 384 */     return null;
/*     */   }
/*     */ 
/*     */   public void free()
/*     */     throws SQLException
/*     */   {
/* 398 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 400 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 402 */       LOG.fine(this._logId + " free()");
/*     */     }
/*     */ 
/* 406 */     if (this._freeCalled)
/*     */       return;
/* 408 */     this.data = null;
/* 409 */     this._freeCalled = true;
/*     */   }
/*     */ 
/*     */   public void truncate(long paramLong)
/*     */     throws SQLException
/*     */   {
/* 422 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 424 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 426 */         LOG.finer(this._logId + " truncate(long = [" + paramLong + "])");
/*     */       }
/* 428 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 430 */         LOG.fine(this._logId + " truncate(long)");
/*     */       }
/*     */     }
/*     */ 
/* 434 */     checkClientLOBValidity();
/* 435 */     if (paramLong < 0L)
/*     */     {
/* 437 */       ErrorMessage.raiseError("JZ038");
/*     */     }
/* 439 */     if (this.data == null)
/*     */     {
/* 441 */       ErrorMessage.raiseError("JZ036");
/*     */     }
/*     */     else
/*     */     {
/* 447 */       byte[] arrayOfByte = new byte[(int)paramLong];
/* 448 */       System.arraycopy(this.data, 0, arrayOfByte, 0, (int)paramLong);
/* 449 */       this.data = arrayOfByte;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkClientLOBValidity() throws SQLException
/*     */   {
/* 455 */     if (!this._freeCalled)
/*     */       return;
/* 457 */     ErrorMessage.raiseError("JZ036");
/*     */   }
/*     */ 
/*     */   public long indexOf(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*     */   {
/* 468 */     int[] arrayOfInt = computeFailure(paramArrayOfByte2);
/* 469 */     int i = 0;
/*     */ 
/* 471 */     for (int j = 0; j < paramArrayOfByte1.length; ++j)
/*     */     {
/* 473 */       while ((i > 0) && (paramArrayOfByte2[i] != paramArrayOfByte1[j]))
/*     */       {
/* 475 */         i = arrayOfInt[(i - 1)];
/*     */       }
/* 477 */       if (paramArrayOfByte2[i] == paramArrayOfByte1[j])
/*     */       {
/* 479 */         ++i;
/*     */       }
/* 481 */       if (i == paramArrayOfByte2.length)
/*     */       {
/* 483 */         return j - paramArrayOfByte2.length + 1;
/*     */       }
/*     */     }
/* 486 */     return -1L;
/*     */   }
/*     */ 
/*     */   private int[] computeFailure(byte[] paramArrayOfByte)
/*     */   {
/* 496 */     int[] arrayOfInt = new int[paramArrayOfByte.length];
/* 497 */     int i = 0;
/* 498 */     for (int j = 1; j < paramArrayOfByte.length; ++j)
/*     */     {
/* 500 */       while ((i > 0) && (paramArrayOfByte[i] != paramArrayOfByte[j]))
/*     */       {
/* 502 */         i = arrayOfInt[(i - 1)];
/*     */       }
/* 504 */       if (paramArrayOfByte[i] == paramArrayOfByte[j])
/*     */       {
/* 506 */         ++i;
/*     */       }
/* 508 */       arrayOfInt[j] = i;
/*     */     }
/* 510 */     return arrayOfInt;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybBinaryClientLob
 * JD-Core Version:    0.5.4
 */