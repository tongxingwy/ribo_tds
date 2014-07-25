/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class ProtocolManager
/*     */ {
/*  55 */   private static Hashtable _protocols = new Hashtable();
/*     */ 
/*     */   public static synchronized void registerProtocol(Protocol paramProtocol)
/*     */   {
/*  70 */     _protocols.put(paramProtocol.getClass().getName(), paramProtocol);
/*     */   }
/*     */ 
/*     */   protected static synchronized Protocol getProtocol(String paramString)
/*     */     throws SQLException
/*     */   {
/*  91 */     Protocol localProtocol = (Protocol)_protocols.get(paramString);
/*  92 */     if (localProtocol == null)
/*     */     {
/*     */       try
/*     */       {
/*  98 */         Class localClass = Class.forName(paramString);
/*  99 */         localProtocol = (Protocol)_protocols.get(paramString);
/*     */ 
/* 103 */         if (localProtocol == null)
/*     */         {
/* 105 */           localProtocol = (Protocol)localClass.newInstance();
/* 106 */           registerProtocol(localProtocol);
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (Throwable localThrowable)
/*     */       {
/* 124 */         ErrorMessage.raiseError("JZ0D5", paramString);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 129 */     return localProtocol.getProtocol();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.ProtocolManager
 * JD-Core Version:    0.5.4
 */