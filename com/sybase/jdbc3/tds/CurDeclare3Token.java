/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class CurDeclare3Token extends CurDeclare2Token
/*    */ {
/*    */   protected CurDeclare3Token()
/*    */   {
/*    */   }
/*    */ 
/*    */   public CurDeclare3Token(TdsCursor paramTdsCursor, String paramString)
/*    */     throws SQLException
/*    */   {
/* 43 */     super(paramTdsCursor, paramString);
/*    */   }
/*    */ 
/*    */   protected void sendTokenName(TdsOutputStream paramTdsOutputStream) throws IOException
/*    */   {
/* 48 */     paramTdsOutputStream.writeByte(16);
/*    */   }
/*    */ 
/*    */   protected void sendTokenLength(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/* 59 */     long l = 1 + this._nameLen + 4 + 1 + 4 + this._queryLen + 2L + this._colLen;
/* 60 */     paramTdsOutputStream.writeLongAsUnsignedInt(l);
/*    */   }
/*    */ 
/*    */   protected void sendOptions(TdsOutputStream paramTdsOutputStream) throws IOException
/*    */   {
/* 65 */     paramTdsOutputStream.writeInt(this._cursor._type);
/*    */   }
/*    */ 
/*    */   protected String getTokenNameAsString()
/*    */   {
/* 70 */     return "CurDeclare3Token";
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CurDeclare3Token
 * JD-Core Version:    0.5.4
 */