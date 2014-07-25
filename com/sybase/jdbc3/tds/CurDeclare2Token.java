/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class CurDeclare2Token extends CurDeclareToken
/*    */ {
/*    */   protected CurDeclare2Token()
/*    */   {
/*    */   }
/*    */ 
/*    */   public CurDeclare2Token(TdsCursor paramTdsCursor, String paramString)
/*    */     throws SQLException
/*    */   {
/* 43 */     super(paramTdsCursor, paramString);
/*    */   }
/*    */ 
/*    */   protected void sendTokenName(TdsOutputStream paramTdsOutputStream) throws IOException
/*    */   {
/* 48 */     paramTdsOutputStream.writeByte(35);
/*    */   }
/*    */ 
/*    */   protected void sendTokenLength(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/* 59 */     long l = 1 + this._nameLen + 1 + 1 + 4 + this._queryLen + 2L + this._colLen;
/* 60 */     paramTdsOutputStream.writeLongAsUnsignedInt(l);
/*    */   }
/*    */ 
/*    */   protected void sendNumColumns(TdsOutputStream paramTdsOutputStream, int paramInt)
/*    */     throws IOException
/*    */   {
/* 66 */     paramTdsOutputStream.writeShort(paramInt);
/*    */   }
/*    */ 
/*    */   protected void sendQueryLen(TdsOutputStream paramTdsOutputStream) throws IOException
/*    */   {
/* 71 */     paramTdsOutputStream.writeLongAsUnsignedInt(this._queryLen);
/*    */   }
/*    */ 
/*    */   protected String getTokenNameAsString()
/*    */   {
/* 76 */     return "CurDeclare2Token";
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CurDeclare2Token
 * JD-Core Version:    0.5.4
 */