/*    */ package com.sybase.ribo;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.io.Writer;
/*    */ 
/*    */ public class AnalyzeThread extends Thread
/*    */ {
/*    */   private InputStream _is;
/*    */   private Writer _writer;
/*    */ 
/*    */   public AnalyzeThread(InputStream is, Writer writer)
/*    */   {
/* 37 */     this._is = is;
/* 38 */     this._writer = writer;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/* 48 */       DumpTds dt = new DumpTds(this._is, this._writer);
/* 49 */       dt.processTds();
/* 50 */       this._is.close();
/* 51 */       this._writer.close();
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 55 */       e.printStackTrace();
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 59 */       t.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.AnalyzeThread
 * JD-Core Version:    0.5.4
 */