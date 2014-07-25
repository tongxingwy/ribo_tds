/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URLConnection;
/*     */ 
/*     */ public class TDSTunnellingIS extends InputStream
/*     */ {
/*     */   private URLDbio _dbio;
/*  36 */   private InputStream _is = null;
/*  37 */   private URLConnection _urlC = null;
/*  38 */   private int _length = -1;
/*  39 */   private long _timeout = 0L;
/*     */ 
/*     */   public TDSTunnellingIS(URLDbio paramURLDbio)
/*     */   {
/*  45 */     this._dbio = paramURLDbio;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  57 */     return -1;
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  72 */     if (this._length <= 0)
/*     */     {
/*  75 */       getIS();
/*     */     }
/*     */ 
/*  79 */     int i = (paramInt2 > this._length) ? this._length : paramInt2;
/*  80 */     int j = 0;
/*  81 */     while (j < i)
/*     */     {
/*  83 */       int k = this._is.read(paramArrayOfByte, paramInt1 + j, i);
/*  84 */       if (k < 1) break;
/*  85 */       i -= k;
/*  86 */       j += k;
/*     */     }
/*  88 */     this._length -= j;
/*  89 */     if (this._length == 0)
/*     */     {
/*  91 */       this._is.close();
/*  92 */       this._urlC = null;
/*     */     }
/*     */ 
/*  95 */     return j;
/*     */   }
/*     */ 
/*     */   public long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 105 */     return 0L;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 118 */     return 0;
/*     */   }
/*     */ 
/*     */   private void getIS()
/*     */     throws IOException
/*     */   {
/*     */     do
/*     */     {
/* 128 */       this._urlC = this._dbio.getURLC(this._timeout);
/* 129 */       this._length = this._urlC.getContentLength();
/* 130 */     }while (this._length <= 0);
/*     */ 
/* 137 */     this._is = this._urlC.getInputStream();
/*     */   }
/*     */ 
/*     */   protected synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong)
/*     */     throws IOException
/*     */   {
/* 147 */     this._timeout = paramLong;
/*     */ 
/* 149 */     return read(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.TDSTunnellingIS
 * JD-Core Version:    0.5.4
 */