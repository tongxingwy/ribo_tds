/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.CacheManager;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class UnicharToUniInputStream extends CharsetToUniInputStream
/*     */ {
/*  55 */   private int[] _currentBytes = new int[2];
/*  56 */   private int _firstByte = 0;
/*  57 */   private int _secondByte = 0;
/*  58 */   private boolean _reverseBytes = false;
/*     */ 
/*     */   public UnicharToUniInputStream(InputStream paramInputStream, int paramInt1, int paramInt2, CacheManager paramCacheManager, boolean paramBoolean)
/*     */     throws UnsupportedEncodingException, IOException
/*     */   {
/*  64 */     super(paramInputStream, paramInt1, paramInt2, paramCacheManager);
/*     */ 
/*  68 */     this._lengthLimit = (paramInt2 * 2);
/*     */ 
/*  72 */     this._ris = new RawInputStream(paramInputStream, paramInt1, paramInt2, paramCacheManager);
/*  73 */     this._reverseBytes = paramBoolean;
/*  74 */     this._even = true;
/*  75 */     this._closed = false;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  88 */     if (this._closed) return -1;
/*     */     int i;
/*  92 */     if (this._even)
/*     */     {
/*  95 */       this._currentBytes[0] = this._ris.read();
/*  96 */       this._currentBytes[1] = this._ris.read();
/*  97 */       if ((this._currentBytes[0] == -1) || (this._currentBytes[1] == -1))
/*     */       {
/* 100 */         this._closed = true;
/* 101 */         return -1;
/*     */       }
/*     */ 
/* 105 */       if (this._reverseBytes)
/*     */       {
/* 107 */         this._firstByte = this._currentBytes[1];
/* 108 */         this._secondByte = this._currentBytes[0];
/*     */       }
/*     */       else
/*     */       {
/* 112 */         this._firstByte = this._currentBytes[0];
/* 113 */         this._secondByte = this._currentBytes[1];
/*     */       }
/* 115 */       i = this._firstByte;
/*     */     }
/*     */     else
/*     */     {
/* 120 */       i = this._secondByte;
/*     */     }
/* 122 */     this._even = (!this._even);
/* 123 */     this._readBytes += 1;
/* 124 */     return i;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.UnicharToUniInputStream
 * JD-Core Version:    0.5.4
 */