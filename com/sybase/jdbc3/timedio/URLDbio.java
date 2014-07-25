/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import com.sybase.jdbc3.utils.BufferInterval;
/*     */ import com.sybase.jdbc3.utils.Queue;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class URLDbio extends Dbio
/*     */ {
/*  34 */   private URL _urlNone = null;
/*  35 */   private URL _urlMore = null;
/*  36 */   private URL _urlPoll = null;
/*  37 */   private URL _urlClose = null;
/*  38 */   private URL _urlConnect = null;
/*  39 */   private URL _url = null;
/*  40 */   private String _protocol = null;
/*  41 */   private StringBuffer _file = null;
/*  42 */   private String _urlBase = null;
/*  43 */   protected String _tdsSessionID = null;
/*  44 */   private boolean _readRequested = false;
/*  45 */   private boolean _closing = false;
/*  46 */   private Queue _responses = null;
/*  47 */   private TDSTunnellingIS _ttis = null;
/*  48 */   private TDSTunnellingOS _ttos = null;
/*     */   public static final int NONE = 1;
/*     */   public static final int MORE = 2;
/*     */   public static final int POLL = 3;
/*     */   public static final int CLOSE = 4;
/*     */   public static final String TDS_SESSION = "Tds-Session";
/*     */   public static final String IGNORE_SESSION = "IGNORE";
/*     */   public static final String TDS_OPERATION = "Operation";
/*     */   public static final String TDS_TIMEOUT = "Timeout";
/*     */   public static final String OPERATION_POLL = "poll";
/*     */   public static final String OPERATION_MORE = "more";
/*     */   public static final String OPERATION_CLOSE = "close";
/*     */   public static final String HOST = "host";
/*     */   public static final String PORT = "port";
/*     */ 
/*     */   protected URLDbio(String paramString, int paramInt, InStreamMgr paramInStreamMgr, SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/*  75 */     super(paramString, paramInt, paramInStreamMgr, paramSybProperty);
/*  76 */     this._tdsSessionID = paramSybProperty.getString(20);
/*  77 */     if (this._tdsSessionID != null)
/*     */     {
/*     */       try
/*     */       {
/*  82 */         registerSessionId();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*  86 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*     */       }
/*  88 */       return;
/*     */     }
/*     */ 
/*  91 */     int i = paramSybProperty.getInteger(19);
/*  92 */     this._file = new StringBuffer();
/*  93 */     this._file.append("?");
/*  94 */     this._file.append("host");
/*  95 */     this._file.append('=');
/*  96 */     this._file.append(paramString);
/*  97 */     this._file.append('&');
/*  98 */     this._file.append("port");
/*  99 */     this._file.append('=');
/* 100 */     this._file.append(paramInt);
/* 101 */     this._file.append('&');
/* 102 */     this._file.append("Operation");
/* 103 */     this._file.append('=');
/* 104 */     this._file.append("more");
/* 105 */     if (i <= 0)
/*     */       return;
/* 107 */     this._file.append('&');
/* 108 */     this._file.append("Timeout");
/* 109 */     this._file.append('=');
/*     */ 
/* 111 */     this._file.append(i * 1000);
/*     */   }
/*     */ 
/*     */   public String getSessionID()
/*     */   {
/* 119 */     return this._tdsSessionID;
/*     */   }
/*     */ 
/*     */   protected void closing()
/*     */   {
/* 126 */     this._closing = true;
/*     */   }
/*     */ 
/*     */   protected synchronized void close()
/*     */   {
/* 131 */     while (!this._responses.empty())
/*     */     {
/*     */       try
/*     */       {
/* 135 */         URLConnection localURLConnection = (URLConnection)this._responses.pop();
/* 136 */         InputStream localInputStream = localURLConnection.getInputStream();
/* 137 */         localInputStream.close();
/*     */       }
/*     */       catch (IOException localIOException1)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 146 */       if (!this._closing)
/*     */       {
/* 149 */         this._ttos.close();
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException2)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doConnect(int paramInt)
/*     */     throws IOException
/*     */   {
/* 167 */     if (this._tdsSessionID == null)
/*     */     {
/* 169 */       this._urlConnect = new URL(this._proxyHost + this._file);
/*     */     }
/*     */ 
/* 175 */     this._ttis = new TDSTunnellingIS(this);
/* 176 */     this._in = this._ttis;
/* 177 */     this._ttos = new TDSTunnellingOS(this);
/* 178 */     this._out = this._ttos;
/*     */ 
/* 180 */     this._responses = new Queue(4, 4);
/*     */   }
/*     */ 
/*     */   protected int reallyRead(long paramLong)
/*     */     throws IOException
/*     */   {
/* 192 */     int i = 0;
/* 193 */     i = this._ttis.read(this._bufIntv._buf, this._bufIntv._offset + this._bRead, this._bufIntv._length - this._bRead, paramLong);
/*     */ 
/* 196 */     return i;
/*     */   }
/*     */ 
/*     */   protected void write(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 213 */     switch (paramInt1)
/*     */     {
/* 226 */     case 1:
/* 216 */       this._url = this._urlNone;
/* 217 */       break;
/*     */     case 2:
/* 219 */       this._url = this._urlMore;
/* 220 */       break;
/*     */     case 3:
/* 222 */       this._url = this._urlPoll;
/* 223 */       break;
/*     */     case 4:
/* 226 */       if (this._urlClose == null) return;
/* 227 */       this._url = this._urlClose;
/*     */     }
/*     */ 
/* 232 */     if (this._closing)
/*     */     {
/* 234 */       this._url = this._urlClose;
/*     */     }
/* 236 */     if (this._url == null)
/*     */     {
/* 238 */       this._url = this._urlConnect;
/*     */     }
/*     */ 
/* 241 */     URLConnection localURLConnection = this._url.openConnection();
/*     */ 
/* 244 */     localURLConnection.setUseCaches(false);
/* 245 */     localURLConnection.setDoInput(true);
/* 246 */     localURLConnection.setRequestProperty("Connection", " Keep-Alive");
/*     */     Object localObject;
/* 248 */     if (paramInt2 > 0)
/*     */     {
/* 250 */       localURLConnection.setDoOutput(true);
/* 251 */       localObject = localURLConnection.getOutputStream();
/* 252 */       ((OutputStream)localObject).write(paramArrayOfByte, 0, paramInt2);
/* 253 */       ((OutputStream)localObject).flush();
/*     */     }
/* 255 */     if (paramInt1 == 4)
/*     */     {
/* 258 */       localObject = localURLConnection.getInputStream();
/* 259 */       ((InputStream)localObject).close();
/*     */     }
/*     */     else
/*     */     {
/* 263 */       this._responses.push(localURLConnection);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized URLConnection getURLC(long paramLong)
/*     */     throws IOException
/*     */   {
/* 275 */     URLConnection localURLConnection = null;
/* 276 */     while (localURLConnection == null)
/*     */     {
/* 278 */       localURLConnection = (URLConnection)this._responses.popNoEx();
/* 279 */       if (localURLConnection != null) {
/*     */         continue;
/*     */       }
/*     */ 
/* 283 */       this._ttos.moreData(paramLong);
/*     */     }
/*     */ 
/* 298 */     String str = localURLConnection.getHeaderField("Tds-Session");
/* 299 */     if (str == null)
/*     */     {
/* 301 */       ErrorMessage.raiseIOException("JZ0T6");
/*     */     }
/* 303 */     if (str.equals("IGNORE"))
/*     */     {
/* 307 */       InputStream localInputStream = localURLConnection.getInputStream();
/* 308 */       localInputStream.close();
/* 309 */       return getURLC(paramLong);
/*     */     }
/* 311 */     if (this._tdsSessionID == null)
/*     */     {
/* 313 */       this._tdsSessionID = str;
/* 314 */       registerSessionId();
/*     */     }
/* 316 */     return localURLConnection;
/*     */   }
/*     */ 
/*     */   private void registerSessionId()
/*     */     throws IOException
/*     */   {
/* 324 */     this._urlBase = ("?Tds-Session=" + this._tdsSessionID);
/* 325 */     this._urlNone = new URL(this._proxyHost + this._urlBase);
/* 326 */     this._urlMore = new URL(this._proxyHost + this._urlBase + "&" + "Operation" + "=" + "more");
/*     */ 
/* 328 */     this._urlPoll = new URL(this._proxyHost + this._urlBase + "&" + "Operation" + "=" + "poll");
/*     */ 
/* 330 */     this._urlClose = new URL(this._proxyHost + this._urlBase + "&" + "Operation" + "=" + "close");
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.URLDbio
 * JD-Core Version:    0.5.4
 */