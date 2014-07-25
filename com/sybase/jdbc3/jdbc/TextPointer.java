/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class TextPointer
/*     */   implements com.sybase.jdbcx.TextPointer
/*     */ {
/*     */   public byte[] _textPtr;
/*     */   public byte[] _timeStamp;
/*     */   public String _tableName;
/*     */   public String _columnName;
/*     */   private Protocol _protocol;
/*     */   private ProtocolContext _ctx;
/*     */ 
/*     */   public TextPointer(ProtocolContext paramProtocolContext)
/*     */     throws SQLException
/*     */   {
/*  47 */     this._textPtr = null;
/*  48 */     this._timeStamp = null;
/*  49 */     this._tableName = null;
/*  50 */     this._columnName = null;
/*  51 */     this._ctx = paramProtocolContext;
/*  52 */     this._protocol = paramProtocolContext._protocol;
/*     */   }
/*     */ 
/*     */   public void sendData(InputStream paramInputStream, boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/*  74 */     this._protocol.bulkWrite(this, paramInputStream, 0, 0, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void sendData(InputStream paramInputStream, int paramInt, boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/*  97 */     this._protocol.bulkWrite(this, paramInputStream, 0, paramInt, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void sendData(InputStream paramInputStream, int paramInt1, int paramInt2, boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/* 122 */     this._protocol.bulkWrite(this, paramInputStream, paramInt1, paramInt2, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void sendData(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/* 143 */     ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
/* 144 */     this._protocol.bulkWrite(this, localByteArrayInputStream, paramInt1, paramInt2, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void writePage(String paramString, int paramInt, byte[] paramArrayOfByte)
/*     */     throws SQLException
/*     */   {
/* 155 */     this._protocol.writePage(this, paramArrayOfByte, paramString, paramInt);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.TextPointer
 * JD-Core Version:    0.5.4
 */