/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.Queue;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class SrvNotifier extends Thread
/*     */ {
/*  41 */   Queue _rpcs = null;
/*     */ 
/*  45 */   Hashtable _sessionList = null;
/*  46 */   String _procName = null;
/*  47 */   Object[] _params = null;
/*     */ 
/*     */   protected SrvNotifier()
/*     */   {
/*  53 */     this._rpcs = new Queue();
/*     */   }
/*     */ 
/*     */   private SrvNotifier(Hashtable paramHashtable, String paramString, Object[] paramArrayOfObject)
/*     */   {
/*  59 */     this._sessionList = paramHashtable;
/*  60 */     this._procName = paramString;
/*  61 */     this._params = paramArrayOfObject;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     while (true)
/*     */     {
/*  71 */       SrvNotifier localSrvNotifier = null;
/*  72 */       synchronized (this._rpcs)
/*     */       {
/*  74 */         localSrvNotifier = (SrvNotifier)this._rpcs.popNoEx();
/*  75 */         if (localSrvNotifier == null)
/*     */         {
/*     */           try
/*     */           {
/*  80 */             this._rpcs.wait();
/*     */           }
/*     */           catch (InterruptedException localInterruptedException)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*  87 */       if (localSrvNotifier == null)
/*     */         continue;
/*  89 */       localSrvNotifier.sendNotifications();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void queueNotifications(Hashtable paramHashtable, String paramString, Object[] paramArrayOfObject)
/*     */   {
/*  98 */     SrvNotifier localSrvNotifier = new SrvNotifier(paramHashtable, paramString, paramArrayOfObject);
/*  99 */     synchronized (this._rpcs)
/*     */     {
/* 101 */       this._rpcs.push(localSrvNotifier);
/*     */ 
/* 103 */       this._rpcs.notify();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void sendNotifications()
/*     */   {
/* 109 */     Enumeration localEnumeration = this._sessionList.keys();
/* 110 */     while (localEnumeration.hasMoreElements())
/*     */     {
/* 112 */       SrvSession localSrvSession = (SrvSession)localEnumeration.nextElement();
/* 113 */       SrvEventListener localSrvEventListener = (SrvEventListener)this._sessionList.get(localSrvSession);
/*     */       try
/*     */       {
/* 118 */         localSrvSession.sendNotify(this._procName, this._params);
/* 119 */         if ((localSrvEventListener._options & 0x2) != 0)
/*     */         {
/* 123 */           localSrvEventListener.close();
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 131 */         localSrvSession.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvNotifier
 * JD-Core Version:    0.5.4
 */