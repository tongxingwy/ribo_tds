/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.LogUtil;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.sql.XADataSource;
/*     */ 
/*     */ public class SybXADataSource extends SybDataSource
/*     */   implements XADataSource
/*     */ {
/*  59 */   private static Logger LOG = Logger.getLogger(SybXADataSource.class.getName());
/*  60 */   private static volatile long _logIdCounter = 0L;
/*     */ 
/*  63 */   private String _logId = null;
/*     */ 
/*     */   public SybXADataSource()
/*     */   {
/*  70 */     this._logId = ("Xa" + _logIdCounter++);
/*     */   }
/*     */ 
/*     */   public XAConnection getXAConnection()
/*     */     throws SQLException
/*     */   {
/*  98 */     return (XAConnection)super.getConnection();
/*     */   }
/*     */ 
/*     */   public XAConnection getXAConnection(String paramString1, String paramString2)
/*     */     throws SQLException
/*     */   {
/* 116 */     return (XAConnection)super.getConnection(paramString1, paramString2);
/*     */   }
/*     */ 
/*     */   public Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 129 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 131 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 133 */       LOG.fine(this._logId + "getConnection()");
/*     */     }
/*     */ 
/* 141 */     ErrorMessage.raiseError("JZ0S3", "getConnection()");
/*     */ 
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */   public Connection getConnection(String paramString1, String paramString2)
/*     */     throws SQLException
/*     */   {
/* 155 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 157 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 159 */         LOG.finer(this._logId + "getConnection(String = [" + paramString1 + "], String = [" + paramString2 + "])");
/*     */       }
/* 162 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 164 */         LOG.fine(this._logId + "getConnection(String, String)");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 169 */     return getConnection();
/*     */   }
/*     */ 
/*     */   protected SybConnection createConnection(String paramString, SybUrlProvider paramSybUrlProvider, int paramInt)
/*     */     throws SQLException
/*     */   {
/* 188 */     return new SybXAConnection(this, paramSybUrlProvider);
/*     */   }
/*     */ 
/*     */   protected synchronized SybUrlProvider createSybUrlProvider()
/*     */     throws SQLException
/*     */   {
/* 198 */     SybUrlProvider localSybUrlProvider = super.createSybUrlProvider();
/*     */ 
/* 203 */     if (getResourceManagerType() == 1)
/*     */     {
/* 209 */       boolean bool = true;
/* 210 */       SybProperty localSybProperty = localSybUrlProvider.getSybProperty();
/*     */       try
/*     */       {
/* 213 */         bool = localSybProperty.getBoolean(6);
/*     */       }
/*     */       catch (SQLException localSQLException)
/*     */       {
/*     */       }
/*     */ 
/* 221 */       if (bool)
/*     */       {
/* 226 */         localSybProperty.setProperty(6, new Boolean(false));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 233 */     String str = getServerName() + ":" + getPortNumber();
/*     */ 
/* 236 */     ((SybUrlProviderImplBase)localSybUrlProvider)._rmNameDefault = str;
/*     */ 
/* 238 */     return localSybUrlProvider;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybXADataSource
 * JD-Core Version:    0.5.4
 */