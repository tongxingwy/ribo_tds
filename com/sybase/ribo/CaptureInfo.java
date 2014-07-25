/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.Capture;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class CaptureInfo
/*     */ {
/*     */   private Capture _capture;
/*     */   private Socket _clientSocket;
/*     */   private Socket _serverSocket;
/*     */   private IOThread _client;
/*     */   private IOThread _server;
/*     */   private OutputStream _outputStream;
/*     */   private String _fileName;
/*     */   private CaptureService _parent;
/*  38 */   private boolean _closing = false;
/*     */ 
/*     */   protected CaptureInfo(CaptureService parent)
/*     */   {
/*  46 */     this._parent = parent;
/*     */   }
/*     */ 
/*     */   protected void setFileName(String name)
/*     */   {
/*  56 */     this._fileName = name;
/*     */   }
/*     */ 
/*     */   protected void setOutputStream(OutputStream os)
/*     */   {
/*  61 */     this._outputStream = os;
/*     */   }
/*     */ 
/*     */   protected OutputStream getOutputStream()
/*     */   {
/*  66 */     return this._outputStream;
/*     */   }
/*     */ 
/*     */   protected void setCapture(Capture capture)
/*     */   {
/*  71 */     this._capture = capture;
/*     */   }
/*     */ 
/*     */   protected void setThreads(IOThread client, IOThread server)
/*     */   {
/*  76 */     this._client = client;
/*  77 */     this._server = server;
/*     */   }
/*     */ 
/*     */   protected void setSockets(Socket client, Socket server)
/*     */   {
/*  83 */     this._clientSocket = client;
/*  84 */     this._serverSocket = server;
/*     */   }
/*     */ 
/*     */   protected synchronized void close()
/*     */   {
/*  93 */     if (this._closing)
/*     */       return;
/*  95 */     this._closing = true;
/*     */     try
/*     */     {
/*  98 */       this._parent._connections.removeElement(this);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 102 */       e.printStackTrace();
/*     */     }
/* 104 */     this._client.shutdown();
/* 105 */     this._server.shutdown();
/*     */     try
/*     */     {
/* 108 */       this._server.join(3000L);
/*     */     }
/*     */     catch (InterruptedException ie)
/*     */     {
/* 112 */       RiboMgr.getInstance().println(RiboMessage.makeMessage("ShuttingDownServerConnection", ie.toString()));
/*     */ 
/* 115 */       this._server.stop();
/*     */     }
/*     */     try
/*     */     {
/* 119 */       this._client.join(3000L);
/*     */     }
/*     */     catch (InterruptedException ie)
/*     */     {
/* 123 */       RiboMgr.getInstance().println(RiboMessage.makeMessage("ShuttingDownClientConnection", ie.toString()));
/*     */ 
/* 126 */       this._client.stop();
/*     */     }
/*     */ 
/* 129 */     if (this._fileName != null)
/*     */     {
/* 131 */       RiboMgr.getInstance().println(RiboMessage.makeMessage("ClosingDumpFile", this._fileName));
/*     */     }
/*     */ 
/* 136 */     closeSockets();
/*     */     try
/*     */     {
/* 140 */       this._outputStream.close();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void closeSockets()
/*     */   {
/* 153 */     if (this._clientSocket != null)
/*     */     {
/*     */       try
/*     */       {
/* 157 */         this._clientSocket.close();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/* 162 */       this._clientSocket = null;
/*     */     }
/*     */ 
/* 165 */     if (this._serverSocket == null)
/*     */       return;
/*     */     try
/*     */     {
/* 169 */       this._serverSocket.close();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/* 174 */     this._serverSocket = null;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.CaptureInfo
 * JD-Core Version:    0.5.4
 */