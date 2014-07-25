/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class CurUpdateToken extends Token
/*     */ {
/*     */   private TdsCursor _cursor;
/*     */   private TdsResultSet _trs;
/*     */   private String _setClause;
/*     */   protected int _status;
/*     */ 
/*     */   protected CurUpdateToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CurUpdateToken(TdsCursor paramTdsCursor, TdsResultSet paramTdsResultSet, String paramString, boolean paramBoolean)
/*     */   {
/*  48 */     this._cursor = paramTdsCursor;
/*  49 */     this._trs = paramTdsResultSet;
/*  50 */     this._setClause = paramString;
/*  51 */     this._status = ((paramBoolean) ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public void send(TdsDataOutputStream paramTdsDataOutputStream)
/*     */     throws IOException
/*     */   {
/*  62 */     int i = 8;
/*     */ 
/*  64 */     Object localObject = null;
/*  65 */     int j = 0;
/*  66 */     byte[] arrayOfByte1 = null;
/*  67 */     int k = 0;
/*     */ 
/*  77 */     if (this._cursor._table != null)
/*     */     {
/*  79 */       arrayOfByte1 = paramTdsDataOutputStream.stringToByte(this._cursor._table);
/*  80 */       k = arrayOfByte1.length;
/*  81 */       i += k;
/*     */     }
/*  83 */     byte[] arrayOfByte2 = paramTdsDataOutputStream.stringToByte(this._setClause);
/*  84 */     int l = arrayOfByte2.length;
/*  85 */     i += l;
/*     */     try
/*     */     {
/*  89 */       paramTdsDataOutputStream.writeByte(133);
/*  90 */       paramTdsDataOutputStream.writeShort(i);
/*  91 */       paramTdsDataOutputStream.writeInt(this._cursor._id);
/*     */ 
/* 100 */       paramTdsDataOutputStream.writeByte(this._status);
/* 101 */       paramTdsDataOutputStream.writeByte(k);
/* 102 */       if (this._cursor._table != null)
/*     */       {
/* 104 */         paramTdsDataOutputStream.write(arrayOfByte1);
/*     */       }
/* 106 */       paramTdsDataOutputStream.writeShort(l);
/* 107 */       paramTdsDataOutputStream.write(arrayOfByte2);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 111 */       writeSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 116 */     StringBuffer localStringBuffer = new StringBuffer("CurUpdateToken: ");
/* 117 */     localStringBuffer.append("name= " + this._cursor.getName());
/* 118 */     localStringBuffer.append(", status= " + this._status);
/* 119 */     localStringBuffer.append(", table= " + this._cursor._table);
/* 120 */     localStringBuffer.append(", setClause= " + this._setClause);
/* 121 */     return localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CurUpdateToken
 * JD-Core Version:    0.5.4
 */