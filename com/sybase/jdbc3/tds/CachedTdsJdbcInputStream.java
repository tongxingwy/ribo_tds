/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.CacheManager;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class CachedTdsJdbcInputStream extends TdsJdbcInputStream
/*     */ {
/*     */   public CachedTdsJdbcInputStream(TdsJdbcInputStream paramTdsJdbcInputStream)
/*     */     throws IOException
/*     */   {
/*  58 */     super(null, paramTdsJdbcInputStream._context, null);
/*     */ 
/*  64 */     byte[] arrayOfByte = null;
/*     */     try
/*     */     {
/*  67 */       arrayOfByte = paramTdsJdbcInputStream.getRawBytes();
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */ 
/*  78 */     paramTdsJdbcInputStream.copyInto(this);
/*     */ 
/*  86 */     if (arrayOfByte == null)
/*     */     {
/*  93 */       arrayOfByte = new byte[0];
/*     */     }
/*     */ 
/*  96 */     this.in = new ByteArrayInputStream(arrayOfByte);
/*     */   }
/*     */ 
/*     */   public void open(boolean paramBoolean)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setManager(CacheManager paramCacheManager)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void cache()
/*     */     throws IOException
/*     */   {
/* 124 */     reset();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */     throws IOException
/*     */   {
/* 135 */     reset();
/*     */   }
/*     */ 
/*     */   public void resetInputStream(InputStream paramInputStream)
/*     */   {
/* 143 */     safeReset();
/*     */   }
/*     */ 
/*     */   public void initialize()
/*     */   {
/* 150 */     if (this._state == 3)
/*     */       return;
/* 152 */     this._state = 3;
/*     */   }
/*     */ 
/*     */   protected void getSize()
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */     throws IOException
/*     */   {
/* 169 */     this.in.reset();
/*     */   }
/*     */ 
/*     */   private void safeReset()
/*     */   {
/*     */     try
/*     */     {
/* 176 */       this.in.reset();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void beginRead()
/*     */     throws IOException
/*     */   {
/* 190 */     reset();
/*     */   }
/*     */ 
/*     */   protected void doRead()
/*     */     throws SQLException
/*     */   {
/* 198 */     safeReset();
/*     */   }
/*     */ 
/*     */   public void startRead()
/*     */     throws IOException
/*     */   {
/* 206 */     reset();
/*     */   }
/*     */ 
/*     */   public void endRead()
/*     */     throws SQLException
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CachedTdsJdbcInputStream
 * JD-Core Version:    0.5.4
 */