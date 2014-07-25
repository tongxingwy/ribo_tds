/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.charset.SybUTF8Charset;
/*     */ import com.sybase.jdbc3.utils.CacheManager;
/*     */ import com.sybase.jdbc3.utils.CacheStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class CharsetToUniInputStream extends RawInputStream
/*     */ {
/*     */   private int _currentChar;
/*  52 */   protected int _readBytes = 0;
/*  53 */   protected int _lengthLimit = 0;
/*  54 */   private InputStreamReader _isr = null;
/*  55 */   protected RawInputStream _ris = null;
/*     */   protected boolean _even;
/*     */   protected boolean _closed;
/*     */ 
/*     */   public CharsetToUniInputStream(InputStream paramInputStream, int paramInt1, int paramInt2, CacheManager paramCacheManager)
/*     */     throws IOException
/*     */   {
/*  64 */     super(paramInputStream, paramInt1, paramInt2, paramCacheManager);
/*     */   }
/*     */ 
/*     */   public CharsetToUniInputStream(InputStream paramInputStream, int paramInt1, int paramInt2, CacheManager paramCacheManager, String paramString)
/*     */     throws UnsupportedEncodingException, IOException
/*     */   {
/*  72 */     super(paramInputStream, paramInt1, paramInt2, paramCacheManager);
/*     */ 
/*  76 */     this._lengthLimit = (paramInt2 * 2);
/*     */ 
/*  80 */     this._ris = new RawInputStream(paramInputStream, paramInt1, paramInt2, paramCacheManager);
/*  81 */     if (paramString == null)
/*     */     {
/*  84 */       this._isr = new InputStreamReader(this._ris);
/*     */     }
/*  89 */     else if (paramString.equals("x-SybUTF8"))
/*     */     {
/*  91 */       this._isr = new InputStreamReader(this._ris, SybUTF8Charset.getInstance());
/*     */     }
/*     */     else
/*     */     {
/*  95 */       this._isr = new InputStreamReader(this._ris, paramString);
/*     */     }
/*     */ 
/*  98 */     this._even = true;
/*  99 */     this._closed = false;
/*     */   }
/*     */ 
/*     */   public boolean cache(CacheStream paramCacheStream)
/*     */     throws IOException
/*     */   {
/* 107 */     return this._ris.cache(paramCacheStream);
/*     */   }
/*     */ 
/*     */   public void setCached(boolean paramBoolean) {
/* 111 */     this._ris.setCached(paramBoolean);
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/* 120 */     if (this._closed) return -1;
/*     */     int i;
/* 124 */     if (this._even)
/*     */     {
/* 127 */       this._currentChar = this._isr.read();
/* 128 */       if (this._currentChar == -1)
/*     */       {
/* 131 */         this._closed = true;
/* 132 */         return -1;
/*     */       }
/*     */ 
/* 136 */       i = (this._currentChar & 0xFF00) >> 8;
/*     */     }
/*     */     else
/*     */     {
/* 141 */       i = this._currentChar & 0xFF;
/*     */     }
/* 143 */     this._even = (!this._even);
/* 144 */     this._readBytes += 1;
/* 145 */     return i;
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 151 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 159 */     for (int i = 0; i < paramInt2; ++i)
/*     */     {
/* 161 */       int j = read();
/* 162 */       if (j == -1)
/*     */       {
/* 164 */         return (i == 0) ? -1 : i;
/*     */       }
/* 166 */       paramArrayOfByte[(i + paramInt1)] = (byte)(j & 0xFF);
/*     */     }
/* 168 */     return i;
/*     */   }
/*     */ 
/*     */   public long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 175 */     for (long l = 0L; l < paramLong; l += 1L)
/*     */     {
/* 177 */       if (read() < 0) {
/*     */         break;
/*     */       }
/*     */     }
/*     */ 
/* 182 */     return l;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 188 */     if (this._closed) return 0;
/*     */ 
/* 193 */     int i = this._lengthLimit - this._readBytes;
/*     */ 
/* 196 */     return i;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 205 */     this._ris.close();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.CharsetToUniInputStream
 * JD-Core Version:    0.5.4
 */