/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class TDSTunnellingOS extends OutputStream
/*     */ {
/*     */   private URLDbio _dbio;
/*  32 */   private int _length = 0;
/*     */   private static final int MAX_WRITE = 4096;
/*  34 */   private byte[] _buf = new byte[4096];
/*     */ 
/*     */   public TDSTunnellingOS(URLDbio paramURLDbio)
/*     */   {
/*  44 */     this._dbio = paramURLDbio;
/*     */   }
/*     */ 
/*     */   public void write(int paramInt)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  70 */     while (paramInt2 > 0) {
/*     */       while (true) {
/*  72 */         int i = 4096 - this._length;
/*  73 */         if (i >= paramInt2) {
/*     */           break;
/*     */         }
/*     */ 
/*  77 */         paramInt2 -= i;
/*  78 */         System.arraycopy(paramArrayOfByte, paramInt1, this._buf, this._length, i);
/*  79 */         this._length += i;
/*  80 */         flush(1);
/*     */       }
/*     */ 
/*  85 */       System.arraycopy(paramArrayOfByte, paramInt1, this._buf, this._length, paramInt2);
/*  86 */       this._length += paramInt2;
/*  87 */       paramInt2 = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/* 102 */     flush(2);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 114 */     flush(4);
/*     */   }
/*     */ 
/*     */   protected synchronized void flush(int paramInt)
/*     */     throws IOException
/*     */   {
/* 127 */     this._dbio.write(paramInt, this._buf, this._length);
/* 128 */     this._length = 0;
/* 129 */     super.notify();
/*     */   }
/*     */ 
/*     */   protected synchronized void moreData(long paramLong)
/*     */     throws IOException
/*     */   {
/* 142 */     if (this._length == 0)
/*     */     {
/* 145 */       flush(2);
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 152 */         super.wait(paramLong);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException)
/*     */       {
/*     */       }
/* 157 */       if (this._length == 0) {
/*     */         return;
/*     */       }
/* 160 */       ErrorMessage.raiseIOException("JZ0T3");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.TDSTunnellingOS
 * JD-Core Version:    0.5.4
 */