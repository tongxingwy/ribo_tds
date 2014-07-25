/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class CurFetchToken extends Token
/*     */ {
/*     */   private TdsCursor _cursor;
/*     */   private int _type;
/*     */   private int _rowNum;
/*     */ 
/*     */   protected CurFetchToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CurFetchToken(TdsCursor paramTdsCursor, int paramInt1, int paramInt2)
/*     */   {
/*  47 */     this._cursor = paramTdsCursor;
/*  48 */     this._type = paramInt1;
/*  49 */     this._rowNum = paramInt2;
/*     */   }
/*     */ 
/*     */   public void send(TdsDataOutputStream paramTdsDataOutputStream)
/*     */     throws IOException
/*     */   {
/*  61 */     int i = 5;
/*  62 */     byte[] arrayOfByte = null;
/*  63 */     int j = 0;
/*     */ 
/*  65 */     if (this._cursor._id == 0)
/*     */     {
/*  67 */       arrayOfByte = paramTdsDataOutputStream.stringToByte(this._cursor.getName());
/*  68 */       j = arrayOfByte.length;
/*  69 */       i += 1 + j;
/*     */     }
/*  71 */     switch (this._type)
/*     */     {
/*     */     case 5:
/*     */     case 6:
/*  75 */       i += 4;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  80 */       paramTdsDataOutputStream.writeByte(130);
/*  81 */       paramTdsDataOutputStream.writeShort(i);
/*  82 */       paramTdsDataOutputStream.writeInt(this._cursor._id);
/*  83 */       if (this._cursor._id == 0)
/*     */       {
/*  85 */         paramTdsDataOutputStream.writeByte(j);
/*  86 */         paramTdsDataOutputStream.write(arrayOfByte);
/*     */       }
/*  88 */       paramTdsDataOutputStream.writeByte(this._type);
/*  89 */       switch (this._type)
/*     */       {
/*     */       case 5:
/*     */       case 6:
/*  93 */         paramTdsDataOutputStream.writeInt(this._rowNum);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*  99 */       writeSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 106 */     StringBuffer localStringBuffer = new StringBuffer("CurFetchToken: ");
/* 107 */     localStringBuffer.append("name= " + this._cursor.getName());
/*     */ 
/* 109 */     localStringBuffer.append(", type = " + this._type);
/* 110 */     localStringBuffer.append(", row# = " + this._rowNum);
/* 111 */     return localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CurFetchToken
 * JD-Core Version:    0.5.4
 */