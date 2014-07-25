/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class SrvDoneProcToken extends SrvDoneToken
/*    */ {
/*    */   public SrvDoneProcToken(int paramInt1, int paramInt2, int paramInt3)
/*    */   {
/* 45 */     super(paramInt1, paramInt2, paramInt3);
/*    */   }
/*    */ 
/*    */   public SrvDoneProcToken(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/* 53 */     super(paramTdsInputStream);
/*    */   }
/*    */ 
/*    */   public void send(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/* 63 */     super.send(paramTdsOutputStream, 254);
/*    */   }
/*    */ 
/*    */   public int getTokenType()
/*    */   {
/* 71 */     return 254;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDoneProcToken
 * JD-Core Version:    0.5.4
 */