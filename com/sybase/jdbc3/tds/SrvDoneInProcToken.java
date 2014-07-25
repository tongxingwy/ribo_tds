/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class SrvDoneInProcToken extends SrvDoneToken
/*    */ {
/*    */   public SrvDoneInProcToken(int paramInt1, int paramInt2, int paramInt3)
/*    */   {
/* 33 */     super(paramInt1, paramInt2, paramInt3);
/*    */   }
/*    */ 
/*    */   public SrvDoneInProcToken(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/* 40 */     super(paramTdsInputStream);
/*    */   }
/*    */ 
/*    */   public void send(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/* 50 */     super.send(paramTdsOutputStream, 255);
/*    */   }
/*    */ 
/*    */   public int getTokenType()
/*    */   {
/* 58 */     return 255;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDoneInProcToken
 * JD-Core Version:    0.5.4
 */