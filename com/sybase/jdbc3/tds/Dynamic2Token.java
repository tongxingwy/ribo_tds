/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class Dynamic2Token extends DynamicToken
/*     */ {
/*     */   public static final int MAX_DYNAMIC2_LENGTH = 2147483647;
/*     */ 
/*     */   public Dynamic2Token(int paramInt, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
/*     */     throws SQLException
/*     */   {
/*  52 */     super(paramInt, paramString1, paramString2, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4);
/*     */   }
/*     */ 
/*     */   public Dynamic2Token(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  63 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   protected Dynamic2Token()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected long readLength(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  75 */     long l = paramTdsInputStream.readUnsignedIntAsLong();
/*  76 */     return l;
/*     */   }
/*     */ 
/*     */   protected void readBodyLength(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  84 */     this._bodyLen = paramTdsInputStream.readUnsignedIntAsLong();
/*     */ 
/* 102 */     this._body = paramTdsInputStream.readString((int)this._bodyLen);
/*     */   }
/*     */ 
/*     */   protected void sendTokenName(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 114 */     paramTdsOutputStream.writeByte(98);
/*     */   }
/*     */ 
/*     */   protected void sendTotalLength(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 122 */     paramTdsOutputStream.writeLongAsUnsignedInt(this._totalOutLen);
/*     */   }
/*     */ 
/*     */   protected void sendBodyLength(long paramLong, TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 131 */     paramTdsOutputStream.writeLongAsUnsignedInt(paramLong);
/*     */   }
/*     */ 
/*     */   protected int getStatementLengthFieldSize()
/*     */   {
/* 136 */     return 4;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.Dynamic2Token
 * JD-Core Version:    0.5.4
 */