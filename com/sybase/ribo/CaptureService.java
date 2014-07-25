/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.Capture;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.PipedInputStream;
/*     */ import java.io.Writer;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class CaptureService extends Thread
/*     */ {
/*     */   public static final int INITIALIZING = 0;
/*     */   public static final int RUNNING = 1;
/*     */   public static final int DEAD = 2;
/*     */   private ServerSocket _sSocket;
/*  38 */   private int _state = 0;
/*     */   private int _listen;
/*     */   private String _host;
/*     */   private int _port;
/*     */   protected Vector _connections;
/*     */ 
/*     */   public CaptureService(int inPort, String host, int port)
/*     */   {
/*  49 */     this._host = host;
/*  50 */     this._port = port;
/*  51 */     this._listen = inPort;
/*  52 */     this._connections = new Vector();
/*     */   }
/*     */ 
/*     */   private void setCaptureServiceState(int state)
/*     */   {
/*  60 */     this._state = state;
/*  61 */     RiboMgr.getInstance().captureServiceNotify(this._state);
/*     */   }
/*     */ 
/*     */   protected int getCaptureServiceState()
/*     */   {
/*  69 */     return this._state;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/*  79 */       this._sSocket = new ServerSocket(this._listen);
/*     */ 
/*  82 */       this._sSocket.setSoTimeout(1000);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  86 */       RiboMgr.getInstance().println(RiboMessage.makeMessage("Exception", e.toString()));
/*     */ 
/*  88 */       setCaptureServiceState(2);
/*  89 */       return;
/*     */     }
/*     */ 
/*  92 */     RiboMgr.getInstance().println(RiboMessage.makeMessage("ListeningOnPort", String.valueOf(this._listen)));
/*     */ 
/*  95 */     setCaptureServiceState(1);
/*  96 */     while (this._state == 1)
/*     */     {
/*     */       label73: Socket clientSocket;
/*     */       try
/*     */       {
/* 101 */         clientSocket = this._sSocket.accept();
/*     */       }
/*     */       catch (InterruptedIOException iioe)
/*     */       {
/* 106 */         break label73:
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/* 113 */         if (this._state == 1)
/*     */         {
/* 115 */           RiboMgr.getInstance().println(RiboMessage.makeMessage("IOException", ioe.toString()));
/*     */         }
/*     */ 
/* 118 */         die();
/* 119 */       }continue;
/*     */       Socket clientSocket;
/* 121 */       Socket dbSocket = null;
/*     */       try
/*     */       {
/* 126 */         dbSocket = new Socket(this._host, this._port);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 130 */         RiboMgr.getInstance().println(RiboMessage.makeMessage("Exception", e.toString()));
/*     */         try
/*     */         {
/* 134 */           if (clientSocket != null)
/*     */           {
/* 136 */             clientSocket.close();
/*     */           }
/*     */         }
/*     */         catch (IOException ex)
/*     */         {
/*     */         }
/*     */       }
/* 143 */       continue;
/*     */ 
/* 146 */       CaptureInfo ci = null;
/*     */       try
/*     */       {
/* 149 */         ci = new CaptureInfo(this);
/* 150 */         String client = clientSocket.getInetAddress().toString();
/*     */ 
/* 152 */         RiboMgr.getInstance().println(RiboMessage.makeMessage("AcceptingConnectionFrom", client));
/*     */ 
/* 156 */         String fileName = RiboMgr.getInstance().getNewCaptureFileName();
/*     */ 
/* 158 */         ci.setFileName(fileName);
/* 159 */         ci.setOutputStream(new FileOutputStream(fileName));
/* 160 */         RiboMgr.getInstance().println(RiboMessage.makeMessage("SendingTrafficTo", fileName));
/*     */ 
/* 164 */         Writer writer = RiboMgr.getInstance().getTransOutputWriter(fileName);
/*     */ 
/* 166 */         if (writer != null)
/*     */         {
/* 171 */           PipedInputStream pis = new PipedInputStream();
/* 172 */           DualPipedOutputStream pos = new DualPipedOutputStream(pis, ci.getOutputStream());
/* 173 */           ci.setOutputStream(pos);
/*     */ 
/* 175 */           AnalyzeThread at = new AnalyzeThread(pis, writer);
/* 176 */           at.start();
/*     */         }
/*     */ 
/* 179 */         Capture capture = new Capture(ci.getOutputStream(), "Ribo 3.0 (Build 0424)");
/*     */ 
/* 181 */         ci.setCapture(capture);
/*     */ 
/* 183 */         clientSocket.setSoTimeout(1000);
/* 184 */         dbSocket.setSoTimeout(1000);
/*     */ 
/* 187 */         IOThread clientThread = new IOThread(clientSocket.getInputStream(), capture.getOutputStream(dbSocket.getOutputStream()), ci);
/*     */ 
/* 192 */         IOThread serverThread = new IOThread(capture.getInputStream(dbSocket.getInputStream()), clientSocket.getOutputStream(), ci);
/*     */ 
/* 197 */         ci.setThreads(clientThread, serverThread);
/*     */ 
/* 199 */         ci.setSockets(clientSocket, dbSocket);
/* 200 */         this._connections.addElement(ci);
/* 201 */         clientThread.start();
/* 202 */         serverThread.start();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 206 */         RiboMgr.getInstance().println(RiboMessage.makeMessage("Exception", e.toString()));
/*     */ 
/* 208 */         if (ci != null)
/*     */         {
/*     */           try
/*     */           {
/* 212 */             ci.closeSockets();
/*     */           }
/*     */           catch (Exception ex)
/*     */           {
/* 216 */             RiboMgr.getInstance().println(RiboMessage.makeMessage("Exception", ex.toString()));
/*     */           }
/*     */ 
/* 219 */           ci = null;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 233 */     die();
/*     */   }
/*     */ 
/*     */   public void die()
/*     */   {
/* 241 */     if (this._state != 1)
/*     */     {
/* 243 */       return;
/*     */     }
/*     */ 
/* 246 */     setCaptureServiceState(2);
/*     */     try
/*     */     {
/* 250 */       while (!this._connections.isEmpty())
/*     */       {
/* 252 */         CaptureInfo ci = (CaptureInfo)this._connections.elementAt(0);
/* 253 */         ci.close();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 258 */       RiboMgr.getInstance().println(RiboMessage.makeMessage("Exception", e.toString()));
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 263 */       this._sSocket.close();
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/* 267 */       RiboMgr.getInstance().println(RiboMessage.makeMessage("IOException", ioe.toString()));
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.CaptureService
 * JD-Core Version:    0.5.4
 */