/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import javax.sql.ConnectionPoolDataSource;
/*     */ import javax.sql.PooledConnection;
/*     */ 
/*     */ public class SybConnectionPoolDataSource extends SybDataSource
/*     */   implements ConnectionPoolDataSource
/*     */ {
/*     */   public PooledConnection getPooledConnection()
/*     */     throws SQLException
/*     */   {
/*  85 */     return (PooledConnection)super.getConnection();
/*     */   }
/*     */ 
/*     */   public PooledConnection getPooledConnection(String paramString1, String paramString2)
/*     */     throws SQLException
/*     */   {
/* 103 */     return (PooledConnection)super.getConnection(paramString1, paramString2);
/*     */   }
/*     */ 
/*     */   public Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 119 */     ErrorMessage.raiseError("JZ0S3", "getConnection()");
/*     */ 
/* 123 */     return null;
/*     */   }
/*     */ 
/*     */   public Connection getConnection(String paramString1, String paramString2)
/*     */     throws SQLException
/*     */   {
/* 132 */     return getConnection();
/*     */   }
/*     */ 
/*     */   protected SybConnection createConnection(String paramString, SybUrlProvider paramSybUrlProvider, int paramInt)
/*     */     throws SQLException
/*     */   {
/* 150 */     return new SybPooledConnection(paramSybUrlProvider, paramInt);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybConnectionPoolDataSource
 * JD-Core Version:    0.5.4
 */