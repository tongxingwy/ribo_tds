/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.SybConnection;
/*     */ import com.sybase.jdbc3.utils.Debug;
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class PduOutputFormatter extends FilterOutputStream
/*     */ {
/*     */   private int _packetSize;
/*     */   private byte[] _netBuf;
/*     */   private int _nextOut;
/*     */   private int _pduType;
/*     */   private int _pduStatus;
/*     */   private SybConnection _conn;
/*     */ 
/*     */   public PduOutputFormatter(OutputStream paramOutputStream, int paramInt, SybConnection paramSybConnection)
/*     */     throws SQLException
/*     */   {
/*  57 */     super(paramOutputStream);
/*  58 */     setNetBufSize(paramInt);
/*  59 */     this._nextOut = 8;
/*  60 */     this._pduType = 15;
/*  61 */     this._pduStatus = 0;
/*  62 */     this._conn = paramSybConnection;
/*     */   }
/*     */ 
/*     */   public void setNetBufSize(int paramInt)
/*     */   {
/*  75 */     byte[] arrayOfByte = new byte[paramInt];
/*  76 */     for (int i = 0; i < this._nextOut; ++i)
/*     */     {
/*  78 */       arrayOfByte[i] = this._netBuf[i];
/*     */     }
/*  80 */     for (i = this._nextOut; i < paramInt; ++i)
/*     */     {
/*  82 */       arrayOfByte[i] = 0;
/*     */     }
/*     */ 
/*  85 */     this._netBuf = arrayOfByte;
/*  86 */     this._packetSize = paramInt;
/*     */   }
/*     */ 
/*     */   public void write(int paramInt)
/*     */     throws IOException
/*     */   {
/*  99 */     if (this._nextOut >= this._packetSize)
/*     */     {
/* 101 */       doFlush(true);
/*     */     }
/* 103 */     this._netBuf[(this._nextOut++)] = (byte)paramInt;
/*     */   }
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte, int paramInt)
/*     */     throws IOException
/*     */   {
/* 120 */     write(paramArrayOfByte, 0, paramInt);
/*     */   }
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 126 */     Debug.asrt(this, paramInt2 + paramInt1 <= paramArrayOfByte.length, "Offset/Length values are incorrect");
/* 127 */     int i = paramInt1;
/* 128 */     while (paramInt2 > 0)
/*     */     {
/* 133 */       if (this._nextOut >= this._packetSize)
/*     */       {
/* 135 */         doFlush(true);
/*     */       }
/* 137 */       int j = this._packetSize - this._nextOut;
/* 138 */       int k = (j < paramInt2 - paramInt1) ? j : paramInt2 - paramInt1;
/* 139 */       System.arraycopy(paramArrayOfByte, i, this._netBuf, this._nextOut, k);
/* 140 */       this._nextOut += k;
/* 141 */       paramInt2 -= k;
/* 142 */       i += k;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setPDUHeader(int paramInt1, int paramInt2)
/*     */   {
/* 155 */     this._pduType = paramInt1;
/* 156 */     this._pduStatus = paramInt2;
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/* 166 */     doFlush(false);
/*     */   }
/*     */ 
/*     */   protected void changeOutput(OutputStream paramOutputStream)
/*     */   {
/* 178 */     this.out = paramOutputStream;
/*     */   }
/*     */ 
/*     */   private void doFlush(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 204 */       this._netBuf[0] = (byte)this._pduType;
/* 205 */       int i = this._packetSize;
/*     */ 
/* 207 */       if (paramBoolean)
/*     */       {
/* 209 */         this._netBuf[1] = (byte)this._pduStatus;
/*     */       }
/*     */       else
/*     */       {
/* 213 */         this._netBuf[1] = (byte)(this._pduStatus | 0x1);
/* 214 */         i = this._nextOut;
/*     */       }
/* 216 */       this._netBuf[2] = (byte)((i & 0xFF00) >> 8);
/* 217 */       this._netBuf[3] = (byte)(i & 0xFF);
/*     */ 
/* 223 */       this.out.write(this._netBuf, 0, i);
/* 224 */       if (!paramBoolean)
/*     */       {
/* 226 */         this.out.flush();
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 231 */       this._conn.markDeadTryHA();
/*     */     }
/*     */     finally
/*     */     {
/* 235 */       this._nextOut = 8;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.PduOutputFormatter
 * JD-Core Version:    0.5.4
 */