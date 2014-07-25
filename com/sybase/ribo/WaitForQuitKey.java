/*    */ package com.sybase.ribo;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ 
/*    */ public class WaitForQuitKey extends Thread
/*    */ {
/*    */   private String _quitKey;
/*    */ 
/*    */   public WaitForQuitKey(String quitKey)
/*    */   {
/* 35 */     this._quitKey = quitKey;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 44 */     String response = null;
/* 45 */     BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
/*    */     do
/*    */     {
/*    */       try
/*    */       {
/* 52 */         response = in.readLine();
/*    */       }
/*    */       catch (IOException ioe)
/*    */       {
/* 57 */         RiboMgr.getInstance().shutdown();
/* 58 */         return;
/*    */       }
/*    */     }
/* 61 */     while ((response == null) || (!response.equalsIgnoreCase(this._quitKey)));
/*    */ 
/* 65 */     RiboMgr.getInstance().shutdown();
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.WaitForQuitKey
 * JD-Core Version:    0.5.4
 */