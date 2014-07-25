/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class LimiterInputStream extends InputStream
/*     */ {
/*     */   public static final int NO_LIMIT = -1;
/*     */   private int _lengthLimit;
/*     */   private InputStream _in;
/*     */ 
/*     */   public LimiterInputStream(InputStream paramInputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/*  45 */     this._lengthLimit = paramInt;
/*  46 */     this._in = paramInputStream;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  55 */     if (this._lengthLimit == 0)
/*     */     {
/*  57 */       return -1;
/*     */     }
/*  59 */     if (this._lengthLimit != -1)
/*     */     {
/*  61 */       this._lengthLimit -= 1;
/*     */     }
/*  63 */     return this._in.read();
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/*  70 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  77 */     int i = paramInt2;
/*  78 */     if (this._lengthLimit == 0)
/*     */     {
/*  80 */       return -1;
/*     */     }
/*  82 */     if ((this._lengthLimit != -1) && (i > this._lengthLimit))
/*     */     {
/*  84 */       i = this._lengthLimit;
/*     */     }
/*     */ 
/*  87 */     int j = this._in.read(paramArrayOfByte, paramInt1, i);
/*  88 */     if (this._lengthLimit != -1)
/*     */     {
/*  90 */       this._lengthLimit -= j;
/*     */     }
/*  92 */     return j;
/*     */   }
/*     */ 
/*     */   public long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/*  98 */     long l1 = paramLong;
/*  99 */     if ((this._lengthLimit != -1) && (l1 > this._lengthLimit))
/*     */     {
/* 101 */       l1 = this._lengthLimit;
/*     */     }
/* 103 */     long l2 = this._in.skip(l1);
/* 104 */     if (this._lengthLimit != -1)
/*     */     {
/* 106 */       this._lengthLimit = (int)(this._lengthLimit - l2);
/*     */     }
/* 108 */     return l2;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 114 */     int i = this._in.available();
/* 115 */     if ((this._lengthLimit != -1) && (i > this._lengthLimit))
/*     */     {
/* 117 */       return this._lengthLimit;
/*     */     }
/* 119 */     return i;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 125 */     this._lengthLimit = 0;
/* 126 */     this._in.close();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.LimiterInputStream
 * JD-Core Version:    0.5.4
 */