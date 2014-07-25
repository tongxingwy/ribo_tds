/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class CurDeclareToken extends Token
/*     */ {
/*     */   protected TdsCursor _cursor;
/*     */   protected String _query;
/*     */   protected int _nameLen;
/*     */   protected long _queryLen;
/*     */   protected int _colLen;
/*     */ 
/*     */   protected CurDeclareToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CurDeclareToken(TdsCursor paramTdsCursor, String paramString)
/*     */     throws SQLException
/*     */   {
/*  50 */     this._query = paramString;
/*  51 */     this._cursor = paramTdsCursor;
/*     */   }
/*     */ 
/*     */   public void send(TdsDataOutputStream paramTdsDataOutputStream)
/*     */     throws IOException
/*     */   {
/*  61 */     byte[] arrayOfByte1 = paramTdsDataOutputStream.stringToByte(this._cursor.getName());
/*  62 */     this._nameLen = arrayOfByte1.length;
/*  63 */     byte[] arrayOfByte2 = paramTdsDataOutputStream.stringToByte(this._query);
/*  64 */     this._queryLen = arrayOfByte2.length;
/*  65 */     this._colLen = 0;
/*     */     int j;
/*     */     byte[] arrayOfByte3;
/*  66 */     if (this._cursor._columns != null)
/*     */     {
/*  69 */       this._colLen = this._cursor._columns.length;
/*  70 */       for (j = 0; j < this._cursor._columns.length; ++j)
/*     */       {
/*  72 */         arrayOfByte3 = paramTdsDataOutputStream.stringToByte(this._cursor._columns[j]);
/*  73 */         this._colLen += arrayOfByte3.length;
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/*  78 */       sendTokenName(paramTdsDataOutputStream);
/*     */ 
/*  92 */       sendTokenLength(paramTdsDataOutputStream);
/*  93 */       paramTdsDataOutputStream.writeByte(this._nameLen);
/*  94 */       paramTdsDataOutputStream.write(arrayOfByte1);
/*  95 */       sendOptions(paramTdsDataOutputStream);
/*  96 */       paramTdsDataOutputStream.writeByte(this._cursor._hasArgs);
/*  97 */       sendQueryLen(paramTdsDataOutputStream);
/*  98 */       paramTdsDataOutputStream.write(arrayOfByte2);
/*  99 */       if (this._cursor._columns != null)
/*     */       {
/* 101 */         sendNumColumns(paramTdsDataOutputStream, this._cursor._columns.length);
/* 102 */         for (j = 0; ; ++j) { if (j >= this._cursor._columns.length)
/*     */             break label245;
/* 104 */           arrayOfByte3 = paramTdsDataOutputStream.stringToByte(this._cursor._columns[j]);
/* 105 */           int i = arrayOfByte3.length;
/* 106 */           paramTdsDataOutputStream.writeByte(i);
/* 107 */           paramTdsDataOutputStream.write(arrayOfByte3); }
/*     */ 
/*     */ 
/*     */       }
/*     */ 
/* 112 */       label245: sendNumColumns(paramTdsDataOutputStream, 0);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 117 */       writeSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void sendTokenName(TdsOutputStream paramTdsOutputStream) throws IOException
/*     */   {
/* 123 */     paramTdsOutputStream.writeByte(134);
/*     */   }
/*     */ 
/*     */   protected void sendTokenLength(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 134 */     long l = 1 + this._nameLen + 1 + 1 + 2 + this._queryLen + 1L + this._colLen;
/* 135 */     paramTdsOutputStream.writeShort((int)l);
/*     */   }
/*     */ 
/*     */   protected void sendNumColumns(TdsOutputStream paramTdsOutputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/* 141 */     paramTdsOutputStream.writeByte(paramInt);
/*     */   }
/*     */ 
/*     */   protected void sendQueryLen(TdsOutputStream paramTdsOutputStream) throws IOException
/*     */   {
/* 146 */     paramTdsOutputStream.writeShort((int)this._queryLen);
/*     */   }
/*     */ 
/*     */   protected void sendOptions(TdsOutputStream paramTdsOutputStream) throws IOException
/*     */   {
/* 151 */     paramTdsOutputStream.writeByte(this._cursor._type);
/*     */   }
/*     */ 
/*     */   protected String getTokenNameAsString() {
/* 155 */     return "CurDeclareToken";
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CurDeclareToken
 * JD-Core Version:    0.5.4
 */