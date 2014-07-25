/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvParamDataFormat2 extends SrvDataFormat
/*     */ {
/*     */   public SrvParamDataFormat2(String paramString, int paramInt1, int paramInt2, int paramInt3, Object paramObject)
/*     */   {
/*  59 */     super(paramString, paramInt1, paramInt2, paramInt3, paramObject);
/*     */   }
/*     */ 
/*     */   public SrvParamDataFormat2(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Object paramObject)
/*     */   {
/*  84 */     super(paramString, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramObject);
/*     */   }
/*     */ 
/*     */   public SrvParamDataFormat2(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 100 */       newDataFormat(paramTdsInputStream, true);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 104 */       readSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void readStatus(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/* 114 */     this._status = paramTdsInputStream.readInt();
/*     */   }
/*     */ 
/*     */   protected void sendStatus(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 123 */     paramTdsOutputStream.writeInt(this._status);
/*     */   }
/*     */ 
/*     */   protected SrvParamDataFormat2()
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 142 */     return -1;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvParamDataFormat2
 * JD-Core Version:    0.5.4
 */