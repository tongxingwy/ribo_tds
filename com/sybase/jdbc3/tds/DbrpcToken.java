/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.jdbc.ParamManager;
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class DbrpcToken extends Token
/*    */ {
/*    */   protected String _rpcname;
/*    */   protected int _options;
/*    */ 
/*    */   public DbrpcToken(String paramString, ParamManager paramParamManager)
/*    */     throws SQLException
/*    */   {
/* 43 */     this._rpcname = paramString;
/* 44 */     this._options = ((paramParamManager.hasInParams()) ? 2 : 0);
/*    */   }
/*    */ 
/*    */   protected DbrpcToken()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void send(TdsDataOutputStream paramTdsDataOutputStream)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 68 */       paramTdsDataOutputStream.writeByte(230);
/*    */ 
/* 70 */       byte[] arrayOfByte = paramTdsDataOutputStream.stringToByte(this._rpcname);
/* 71 */       paramTdsDataOutputStream.writeShort(3 + arrayOfByte.length);
/* 72 */       paramTdsDataOutputStream.writeByte(arrayOfByte.length);
/* 73 */       paramTdsDataOutputStream.write(arrayOfByte);
/* 74 */       paramTdsDataOutputStream.writeShort(this._options);
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 78 */       writeSQE(localIOException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.DbrpcToken
 * JD-Core Version:    0.5.4
 */