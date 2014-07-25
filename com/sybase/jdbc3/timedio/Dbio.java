/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.Capture;
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import com.sybase.jdbc3.utils.BufferInterval;
/*     */ import com.sybase.jdbc3.utils.Misc;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.SocketException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public abstract class Dbio
/*     */ {
/*     */   public static final int NOT_WAITING = 0;
/*     */   public static final int WAITING = 1;
/*     */   protected SybProperty _info;
/*     */   protected String _host;
/*     */   protected int _port;
/*  59 */   protected String _proxyHost = null;
/*     */   protected int _proxyPort;
/*     */   protected IOException _lastEx;
/*     */   protected int _threadState;
/*     */   protected InStreamMgr _ioMgr;
/*     */   protected BufferInterval _bufIntv;
/*     */   protected int _bRead;
/*     */   protected InputStream _in;
/*     */   protected OutputStream _out;
/*     */   protected Capture _cap;
/*     */ 
/*     */   protected Dbio(String paramString, int paramInt, InStreamMgr paramInStreamMgr, SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/*  82 */     this._info = paramSybProperty;
/*  83 */     this._host = paramString;
/*  84 */     this._port = paramInt;
/*  85 */     this._ioMgr = paramInStreamMgr;
/*  86 */     this._bufIntv = null;
/*  87 */     this._threadState = 0;
/*  88 */     checkProxy();
/*     */   }
/*     */ 
/*     */   protected static Dbio connect(String paramString, int paramInt, InStreamMgr paramInStreamMgr, SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/* 102 */     Object localObject = null;
/*     */ 
/* 104 */     String str1 = paramSybProperty.getString(17);
/*     */ 
/* 107 */     String str2 = paramSybProperty.getString(31);
/*     */ 
/* 111 */     if ((str1 != null) && (str1.indexOf("//") > 0))
/*     */     {
/* 113 */       if (str2 != null)
/*     */       {
/* 115 */         ErrorMessage.raiseError("JZ0US");
/*     */       }
/*     */ 
/* 118 */       localObject = new URLDbio(paramString, paramInt, paramInStreamMgr, paramSybProperty);
/*     */     }
/* 122 */     else if (str2 != null)
/*     */     {
/*     */       try
/*     */       {
/* 130 */         localObject = new SocketDbio(paramString, paramInt, paramInStreamMgr, paramSybProperty);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 136 */         if ((localException instanceof SQLException) && 
/* 138 */           (((SQLException)localException).getSQLState().equals("JZ0NF")))
/*     */         {
/* 141 */           throw ((SQLException)localException);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 157 */       localObject = new RawDbio(paramString, paramInt, paramInStreamMgr, paramSybProperty);
/*     */     }
/*     */ 
/* 160 */     return (Dbio)localObject;
/*     */   }
/*     */ 
/*     */   public String getSessionID()
/*     */   {
/* 171 */     return null;
/*     */   }
/*     */ 
/*     */   protected void closing()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected abstract void close();
/*     */ 
/*     */   protected abstract void doConnect(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   protected boolean startAsync()
/*     */   {
/* 203 */     return false;
/*     */   }
/*     */ 
/*     */   protected void stopAsync()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void setBufferInfo(BufferInterval paramBufferInterval, int paramInt)
/*     */   {
/* 227 */     this._bufIntv = paramBufferInterval;
/* 228 */     this._bRead = paramInt;
/*     */   }
/*     */ 
/*     */   protected void doRead(long paramLong)
/*     */     throws IOException
/*     */   {
/* 238 */     if (this._bufIntv == null)
/*     */     {
/* 241 */       this._ioMgr.setBuffer(paramLong);
/* 242 */       if (this._bufIntv == null)
/*     */       {
/* 245 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 249 */     int i = 0;
/*     */     Object localObject;
/*     */     while (true)
/*     */     {
/*     */       try {
/* 254 */         i = reallyRead(paramLong);
/*     */ 
/* 256 */         if (i > 0)
/*     */         {
/* 258 */           i += this._bRead;
/* 259 */           break label196:
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (SocketException localSocketException)
/*     */       {
/* 267 */         if (!localSocketException.getMessage().startsWith("Interrupted system call"));
/* 274 */         if (this._ioMgr._migrating)
/*     */         {
/* 276 */           throw localSocketException;
/*     */         }
/*     */ 
/* 294 */         this._ioMgr.markDead();
/*     */       }
/*     */       catch (ThreadDeath localThreadDeath)
/*     */       {
/* 310 */         this._lastEx = ErrorMessage.makeIOException("JZ0TD");
/*     */ 
/* 312 */         this._ioMgr.reportError("JZ0T7");
/* 313 */         throw localThreadDeath;
/*     */       }
/*     */       catch (InterruptedIOException localInterruptedIOException)
/*     */       {
/* 319 */         this._lastEx = localInterruptedIOException;
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 325 */         this._ioMgr.markDead();
/*     */       }
/*     */ 
/* 328 */       if (this._lastEx != null)
/*     */       {
/* 337 */         localObject = this._lastEx;
/* 338 */         if (this._lastEx instanceof InterruptedIOException)
/*     */         {
/* 342 */           this._lastEx = null;
/*     */         }
/* 344 */         throw ((Throwable)localObject);
/*     */       }
/*     */ 
/* 347 */       if (i != -1)
/*     */         continue;
/* 349 */       if (this._ioMgr._migrating)
/*     */       {
/* 354 */         throw new SocketException();
/*     */       }
/*     */ 
/* 358 */       this._ioMgr.markDead();
/*     */     }
/*     */ 
/* 363 */     if (i > 0)
/*     */     {
/* 365 */       label196: localObject = this._bufIntv;
/*     */ 
/* 368 */       this._bufIntv = null;
/*     */ 
/* 370 */       this._ioMgr.moreData((BufferInterval)localObject, i);
/*     */     }
/*     */     else
/*     */     {
/* 374 */       this._bufIntv = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract int reallyRead(long paramLong)
/*     */     throws IOException;
/*     */ 
/*     */   protected OutputStream getOutputStream()
/*     */   {
/* 387 */     if (this._cap != null)
/*     */     {
/* 389 */       this._out = this._cap.getOutputStream(this._out);
/* 390 */       this._in = this._cap.getInputStream(this._in);
/*     */     }
/* 392 */     return this._out;
/*     */   }
/*     */ 
/*     */   protected InputStream getInputStream()
/*     */   {
/* 400 */     return this._in;
/*     */   }
/*     */ 
/*     */   protected Capture getCapture()
/*     */   {
/* 407 */     return this._cap;
/*     */   }
/*     */ 
/*     */   protected void createCapture(SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/* 415 */     String str = paramSybProperty.getString(24);
/* 416 */     if (str == null)
/*     */       return;
/*     */     try
/*     */     {
/* 420 */       Misc.checkOutputFilePath(str);
/* 421 */       this._cap = new Capture(new FileOutputStream(str));
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 426 */       ErrorMessage.raiseWarning("010SP", str, localIOException.getMessage());
/*     */     }
/*     */     catch (SecurityException localSecurityException)
/*     */     {
/* 432 */       ErrorMessage.raiseWarning("010SN", str, localSecurityException.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void checkProxy()
/*     */     throws SQLException
/*     */   {
/* 455 */     String str1 = this._info.getString(17);
/*     */ 
/* 458 */     if (str1 == null) return;
/*     */ 
/* 460 */     int i = str1.indexOf(':');
/* 461 */     if (i == -1)
/*     */     {
/* 464 */       ErrorMessage.raiseError("JZ003", str1);
/*     */     }
/*     */ 
/* 467 */     if ((str1.length() > i + 2) && (str1.charAt(i + 1) == '/') && (str1.charAt(i + 2) == '/'))
/*     */     {
/* 472 */       this._proxyHost = str1;
/*     */     }
/*     */     else
/*     */     {
/* 476 */       this._proxyHost = str1.substring(0, i);
/* 477 */       String str2 = str1.substring(i + 1);
/*     */       try
/*     */       {
/* 482 */         this._proxyPort = Integer.parseInt(str2);
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException)
/*     */       {
/* 486 */         ErrorMessage.raiseError("JZ0NE", str1, localNumberFormatException.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.Dbio
 * JD-Core Version:    0.5.4
 */