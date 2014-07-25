/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class SrvTypeFormatter
/*    */ {
/* 32 */   protected SrvFormatToken _format = null;
/*    */ 
/* 35 */   protected SrvCapabilityToken _cap = null;
/*    */ 
/*    */   public SrvTypeFormatter(SrvFormatToken paramSrvFormatToken, SrvCapabilityToken paramSrvCapabilityToken)
/*    */   {
/* 47 */     this._format = paramSrvFormatToken;
/* 48 */     this._cap = paramSrvCapabilityToken;
/*    */   }
/*    */ 
/*    */   public abstract void sendDataStream(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException;
/*    */ 
/*    */   protected void setFormatter(Token paramToken, SrvTypeFormatter paramSrvTypeFormatter)
/*    */   {
/* 58 */     if (paramToken instanceof SrvParamsToken)
/*    */     {
/* 60 */       ((SrvParamsToken)paramToken).setFormatter(paramSrvTypeFormatter);
/*    */     }
/*    */     else
/*    */     {
/* 64 */       ((SrvRowToken)paramToken).setFormatter(paramSrvTypeFormatter);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvTypeFormatter
 * JD-Core Version:    0.5.4
 */