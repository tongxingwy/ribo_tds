/*    */ package com.sybase.ribo;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InterruptedIOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class IOThread extends Thread
/*    */ {
/*    */   private InputStream _is;
/*    */   private OutputStream _os;
/*    */   private CaptureInfo _ci;
/*    */   private static final int NETWORK_BUFFER_SIZE = 4096;
/* 32 */   private byte[] buffer = new byte[4096];
/* 33 */   private boolean _running = false;
/*    */ 
/*    */   IOThread(InputStream is, OutputStream os, CaptureInfo ci)
/*    */   {
/* 38 */     this._is = is;
/* 39 */     this._os = os;
/* 40 */     this._ci = ci;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 45 */     this._running = true;
/*    */     try
/*    */     {
/* 48 */       while (this._running)
/*    */       {
/*    */         try
/*    */         {
/* 53 */           int length = this._is.read(this.buffer);
/* 54 */           if (length > 0)
/*    */           {
/* 56 */             this._os.write(this.buffer, 0, length);
/* 57 */             this._os.flush();
/*    */           }
/*    */           else
/*    */           {
/* 63 */             killConnection();
/*    */           }
/*    */         }
/*    */         catch (InterruptedIOException iioe)
/*    */         {
/*    */         }
/*    */       }
/*    */ 
/*    */     }
/*    */     catch (IOException ioe)
/*    */     {
/* 74 */       killConnection();
/*    */     }
/*    */   }
/*    */ 
/*    */   private void killConnection()
/*    */   {
/* 80 */     this._ci.close();
/*    */   }
/*    */ 
/*    */   protected void shutdown()
/*    */   {
/* 85 */     this._running = false;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.IOThread
 * JD-Core Version:    0.5.4
 */