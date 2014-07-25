/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import com.sybase.jdbcx.SybSocketFactory;
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class SocketDbio extends RawDbio
/*     */ {
/*  36 */   private SybSocketFactory _socketFactory = null;
/*     */ 
/*     */   public SocketDbio(String paramString, int paramInt, InStreamMgr paramInStreamMgr, SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/*  43 */     super(paramString, paramInt, paramInStreamMgr, paramSybProperty);
/*     */ 
/*  46 */     String str = paramSybProperty.getString(31);
/*     */     try
/*     */     {
/*  53 */       if (str.equals("DEFAULT"))
/*     */       {
/*  55 */         this._socketFactory = null;
/*     */       }
/*     */       else
/*     */       {
/*  59 */         this._socketFactory = ((SybSocketFactory)Class.forName(str).newInstance());
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*  65 */       ErrorMessage.raiseError("JZ0NF");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doConnect(int paramInt)
/*     */     throws IOException
/*     */   {
/*  79 */     String str = null;
/*  80 */     int i = 0;
/*  81 */     if (this._proxyHost != null)
/*     */     {
/*  83 */       str = this._proxyHost;
/*  84 */       i = this._proxyPort;
/*     */     }
/*     */     else
/*     */     {
/*  88 */       str = this._host;
/*  89 */       i = this._port;
/*     */     }
/*     */ 
/*  92 */     if (this._socketFactory == null)
/*     */     {
/*  95 */       this._socket = new Socket(str, i);
/*     */     }
/*     */     else
/*     */     {
/*  99 */       this._socket = this._socketFactory.createSocket(str, i, this._info.getProperties());
/*     */     }
/*     */ 
/* 102 */     setUpSocket(paramInt);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.SocketDbio
 * JD-Core Version:    0.5.4
 */