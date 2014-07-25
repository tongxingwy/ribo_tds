/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ 
/*    */ public class SrvAcceptor extends Thread
/*    */ {
/*    */   private ServerSocket _serverSocket;
/*    */   private SrvReceiver _tdsReceiver;
/*    */   private boolean _idebug;
/*    */   private boolean _odebug;
/* 35 */   private boolean _running = false;
/*    */ 
/*    */   public SrvAcceptor(ServerSocket paramServerSocket, SrvReceiver paramSrvReceiver, boolean paramBoolean1, boolean paramBoolean2)
/*    */   {
/* 46 */     this._serverSocket = paramServerSocket;
/* 47 */     this._tdsReceiver = paramSrvReceiver;
/* 48 */     this._idebug = paramBoolean1;
/* 49 */     this._odebug = paramBoolean2;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 58 */     this._running = true;
/* 59 */     this._tdsReceiver.registerAcceptor(this);
/* 60 */     while (this._running)
/*    */     {
/*    */       try
/*    */       {
/* 64 */         Socket localSocket = this._serverSocket.accept();
/* 65 */         this._tdsReceiver.createSession(localSocket, this._idebug, this._odebug);
/*    */       }
/*    */       catch (IOException localIOException)
/*    */       {
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void shutdown()
/*    */   {
/* 77 */     if (!this._running)
/*    */       return;
/* 79 */     this._running = false;
/* 80 */     if (this._serverSocket == null)
/*    */       return;
/*    */     try
/*    */     {
/* 84 */       this._serverSocket.close();
/*    */     }
/*    */     catch (Exception localException)
/*    */     {
/*    */     }
/*    */ 
/* 91 */     this._serverSocket = null;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvAcceptor
 * JD-Core Version:    0.5.4
 */