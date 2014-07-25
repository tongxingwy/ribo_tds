/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import com.sybase.jdbc3.utils.AsciiInput;
/*     */ import com.sybase.jdbc3.utils.BufferInterval;
/*     */ import com.sybase.jdbc3.utils.BufferPool;
/*     */ import com.sybase.jdbc3.utils.SybVersion;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class RawDbio extends Dbio
/*     */   implements Runnable
/*     */ {
/*  41 */   protected Socket _socket = null;
/*  42 */   private Thread _runner = null;
/*  43 */   private long _currentTimeout = 0L;
/*  44 */   private volatile boolean _runAsync = false;
/*     */   static final boolean READAHEAD = true;
/*     */   private static final String CONNECT = "CONNECT";
/*     */   private static final String TERMINATOR = "\r\n";
/*     */   private static final String HTTP_VERSION = "HTTP/1.0";
/*     */   private static final String USER_AGENT = "User-agent:";
/* 341 */   private static final String JDBC_VERSION = "Sybase jConnect for JDBC (TM) " + SybVersion.MAJOR_VERSION + "." + SybVersion.MINOR_VERSION;
/*     */   private static final String SUCCESS_CONNECT = "200";
/*     */ 
/*     */   public RawDbio(String paramString, int paramInt, InStreamMgr paramInStreamMgr, SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/*  56 */     super(paramString, paramInt, paramInStreamMgr, paramSybProperty);
/*     */   }
/*     */ 
/*     */   protected void close()
/*     */   {
/*  68 */     if (this._socket == null)
/*     */       return;
/*     */     try
/*     */     {
/*  72 */       this._socket.close();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */ 
/*  78 */     this._socket = null;
/*     */   }
/*     */ 
/*     */   protected void doConnect(int paramInt)
/*     */     throws IOException
/*     */   {
/*  91 */     int i = 0;
/*     */ 
/*  93 */     if (i < 1000)
/*     */     {
/*     */       try
/*     */       {
/*  99 */         this._socket = new Socket();
/* 100 */         InetSocketAddress localInetSocketAddress = null;
/* 101 */         if (this._proxyHost != null)
/*     */         {
/* 103 */           localInetSocketAddress = new InetSocketAddress(this._proxyHost, this._proxyPort);
/*     */         }
/*     */         else
/*     */         {
/* 107 */           localInetSocketAddress = new InetSocketAddress(this._host, this._port);
/*     */         }
/* 109 */         this._socket.connect(localInetSocketAddress, paramInt);
/*     */       }
/*     */       catch (SocketException localSocketException)
/*     */       {
/* 115 */         if (localSocketException.getMessage().startsWith("Address already in use"))
/*     */         {
/* 117 */           ++i;
/*     */ 
/* 140 */           if (i == 1000)
/*     */           {
/* 142 */             throw localSocketException;
/*     */           }
/*     */         }
/*     */ 
/* 146 */         throw localSocketException;
/*     */       }
/*     */     }
/* 149 */     setUpSocket(paramInt);
/*     */   }
/*     */ 
/*     */   protected void setUpSocket(int paramInt)
/*     */     throws IOException
/*     */   {
/* 160 */     this._socket.setKeepAlive(true);
/* 161 */     this._socket.setTcpNoDelay(true);
/* 162 */     if (paramInt != this._currentTimeout)
/*     */     {
/* 164 */       this._currentTimeout = paramInt;
/* 165 */       this._socket.setSoTimeout(paramInt);
/*     */     }
/* 167 */     this._out = this._socket.getOutputStream();
/* 168 */     this._in = this._socket.getInputStream();
/* 169 */     if (this._proxyHost == null) {
/*     */       return;
/*     */     }
/*     */ 
/* 173 */     sendHTTPConnect(this._in, this._out, this._host + ":" + this._port);
/*     */   }
/*     */ 
/*     */   public boolean startAsync()
/*     */   {
/* 183 */     if (this._runner == null)
/*     */     {
/* 185 */       this._runAsync = true;
/* 186 */       this._runner = new Thread(this);
/* 187 */       this._runner.setDaemon(true);
/* 188 */       this._runner.start();
/*     */     }
/*     */ 
/* 191 */     return true;
/*     */   }
/*     */ 
/*     */   public void stopAsync()
/*     */   {
/* 200 */     if (this._runner == null)
/*     */       return;
/*     */     try
/*     */     {
/* 204 */       this._runAsync = false;
/* 205 */       this._runner.join(1L);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 212 */       this._runner = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 223 */     int i = 1; int k = 0;
/*     */ 
/* 225 */     if (this._info.isPropertySet(93))
/*     */     {
/* 227 */       BufferPool localBufferPool = this._ioMgr.getBufferPool();
/*     */       while (true) { if (!this._runAsync) {
/*     */           return;
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 234 */           if (this._info.getMaxBICount() > localBufferPool.getInMemoryBICount())
/*     */           {
/* 236 */             doRead(0L);
/*     */ 
/* 239 */             i = 1;
/*     */           }
/* 245 */           else if (this._ioMgr.isRequestQueueEmpty())
/*     */           {
/* 249 */             ++k;
/*     */ 
/* 251 */             if ((k > 10) && (i < 50))
/*     */             {
/* 253 */               i += 2;
/* 254 */               k = 0;
/*     */             }
/* 256 */             Thread.sleep(i);
/*     */           }
/*     */           else
/*     */           {
/* 264 */             localBufferPool.resetInMemoryBICount();
/* 265 */             doRead(0L);
/*     */ 
/* 268 */             int j = 1;
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (SQLException localSQLException)
/*     */         {
/*     */         }
/*     */         catch (IOException localIOException2)
/*     */         {
/* 278 */           this._lastEx = localIOException2;
/* 279 */           this._ioMgr.reportError("JZ0T2");
/*     */         }
/*     */         catch (InterruptedException localInterruptedException)
/*     */         {
/*     */         }
/*     */  }
/*     */ 
/*     */ 
/*     */     }
/*     */ 
/* 289 */     while (this._runAsync)
/*     */     {
/*     */       try
/*     */       {
/* 293 */         doRead(0L);
/*     */       }
/*     */       catch (IOException localIOException1)
/*     */       {
/* 297 */         this._lastEx = localIOException1;
/* 298 */         this._ioMgr.reportError("JZ0T2");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int reallyRead(long paramLong)
/*     */     throws IOException
/*     */   {
/* 317 */     if (paramLong != this._currentTimeout)
/*     */     {
/* 319 */       this._currentTimeout = paramLong;
/* 320 */       this._socket.setSoTimeout((int)paramLong);
/*     */     }
/*     */ 
/* 323 */     return this._in.read(this._bufIntv._buf, this._bufIntv._offset + this._bRead, this._bufIntv._length - this._bRead);
/*     */   }
/*     */ 
/*     */   private void sendHTTPConnect(InputStream paramInputStream, OutputStream paramOutputStream, String paramString)
/*     */     throws IOException
/*     */   {
/* 350 */     StringBuffer localStringBuffer = new StringBuffer("CONNECT");
/* 351 */     localStringBuffer.append(" ");
/* 352 */     localStringBuffer.append(paramString);
/* 353 */     localStringBuffer.append(" ");
/* 354 */     localStringBuffer.append("HTTP/1.0");
/* 355 */     localStringBuffer.append("\r\n");
/* 356 */     localStringBuffer.append("User-agent:");
/* 357 */     localStringBuffer.append(" ");
/* 358 */     localStringBuffer.append(JDBC_VERSION);
/* 359 */     localStringBuffer.append("\r\n");
/* 360 */     localStringBuffer.append("\r\n");
/*     */ 
/* 362 */     int i = localStringBuffer.length();
/* 363 */     byte[] arrayOfByte = localStringBuffer.toString().getBytes();
/* 364 */     paramOutputStream.write(arrayOfByte, 0, i);
/* 365 */     paramOutputStream.flush();
/*     */ 
/* 367 */     String str = AsciiInput.readLine(paramInputStream);
/* 368 */     if (str == null)
/*     */     {
/* 370 */       ErrorMessage.raiseIOException("JZ0I7");
/*     */     }
/*     */ 
/* 373 */     if (str.indexOf("200") == -1)
/*     */     {
/* 375 */       ErrorMessage.raiseIOException("JZ0I8", str);
/*     */     }
/*     */ 
/* 379 */     while (str != null)
/*     */     {
/* 381 */       str = AsciiInput.readLine(paramInputStream);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.RawDbio
 * JD-Core Version:    0.5.4
 */