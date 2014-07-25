/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class CurDeleteToken extends Token
/*     */ {
/*     */   private TdsCursor _cursor;
/*     */   private TdsResultSet _trs;
/*     */ 
/*     */   protected CurDeleteToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CurDeleteToken(TdsCursor paramTdsCursor, TdsResultSet paramTdsResultSet)
/*     */   {
/*  46 */     this._cursor = paramTdsCursor;
/*  47 */     this._trs = paramTdsResultSet;
/*     */   }
/*     */ 
/*     */   public void send(TdsDataOutputStream paramTdsDataOutputStream)
/*     */     throws IOException
/*     */   {
/*  57 */     int i = 5;
/*  58 */     byte[] arrayOfByte1 = null;
/*  59 */     int j = 0;
/*  60 */     byte[] arrayOfByte2 = null;
/*  61 */     int k = 0;
/*     */ 
/*  63 */     if (this._cursor._id == 0)
/*     */     {
/*  65 */       arrayOfByte1 = paramTdsDataOutputStream.stringToByte(this._cursor.getName());
/*  66 */       j = arrayOfByte1.length;
/*  67 */       i += 1 + j;
/*     */     }
/*  69 */     if (this._cursor._table != null)
/*     */     {
/*  71 */       arrayOfByte2 = paramTdsDataOutputStream.stringToByte(this._cursor._table);
/*  72 */       k = arrayOfByte2.length;
/*  73 */       i += 1 + k;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  78 */       paramTdsDataOutputStream.writeByte(129);
/*  79 */       paramTdsDataOutputStream.writeShort(i);
/*  80 */       paramTdsDataOutputStream.writeInt(this._cursor._id);
/*  81 */       if (this._cursor._id == 0)
/*     */       {
/*  83 */         paramTdsDataOutputStream.writeByte(j);
/*  84 */         paramTdsDataOutputStream.write(arrayOfByte1);
/*     */       }
/*  86 */       paramTdsDataOutputStream.writeByte(0);
/*  87 */       paramTdsDataOutputStream.writeByte(k);
/*  88 */       if (this._cursor._table != null)
/*     */       {
/*  90 */         paramTdsDataOutputStream.write(arrayOfByte2);
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*  95 */       writeSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 102 */     StringBuffer localStringBuffer = new StringBuffer("CurDeleteToken: ");
/* 103 */     localStringBuffer.append("name= " + this._cursor.getName());
/* 104 */     localStringBuffer.append(", table= " + this._cursor._table);
/* 105 */     return localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CurDeleteToken
 * JD-Core Version:    0.5.4
 */