/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class ParamsToken extends Token
/*    */ {
/*    */   public ParamsToken(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public ParamsToken()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void send(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 55 */       paramTdsOutputStream.writeByte(215);
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 59 */       writeSQE(localIOException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.ParamsToken
 * JD-Core Version:    0.5.4
 */