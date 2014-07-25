/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class LogoutToken extends Token
/*    */ {
/*    */   public void send(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 48 */       paramTdsOutputStream.writeByte(113);
/* 49 */       paramTdsOutputStream.writeByte(0);
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 53 */       writeSQE(localIOException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.LogoutToken
 * JD-Core Version:    0.5.4
 */