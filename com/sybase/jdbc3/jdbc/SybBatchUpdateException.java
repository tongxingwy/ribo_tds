/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbcx.EedInfo;
/*     */ import java.sql.BatchUpdateException;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class SybBatchUpdateException extends BatchUpdateException
/*     */   implements EedInfo
/*     */ {
/*  42 */   SybSQLException _sqe = null;
/*     */ 
/*     */   protected SybBatchUpdateException(SybSQLException paramSybSQLException, String paramString1, String paramString2, int paramInt, int[] paramArrayOfInt)
/*     */   {
/*  70 */     super(paramString1, paramString2, paramInt, paramArrayOfInt);
/*     */ 
/*  75 */     this._sqe = paramSybSQLException;
/*     */   }
/*     */ 
/*     */   protected SQLException getOrigSQE()
/*     */   {
/*  80 */     return this._sqe;
/*     */   }
/*     */ 
/*     */   public int getState()
/*     */   {
/*  90 */     return this._sqe.getState();
/*     */   }
/*     */ 
/*     */   public int getSeverity()
/*     */   {
/*  99 */     return this._sqe.getSeverity();
/*     */   }
/*     */ 
/*     */   public String getServerName()
/*     */   {
/* 108 */     return this._sqe.getServerName();
/*     */   }
/*     */ 
/*     */   public String getProcedureName()
/*     */   {
/* 117 */     return this._sqe.getProcedureName();
/*     */   }
/*     */ 
/*     */   public int getLineNumber()
/*     */   {
/* 126 */     return this._sqe.getLineNumber();
/*     */   }
/*     */ 
/*     */   public ResultSet getEedParams()
/*     */   {
/* 135 */     return this._sqe.getEedParams();
/*     */   }
/*     */ 
/*     */   public int getTranState()
/*     */   {
/* 144 */     return this._sqe.getTranState();
/*     */   }
/*     */ 
/*     */   public int getStatus()
/*     */   {
/* 153 */     return this._sqe.getStatus();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybBatchUpdateException
 * JD-Core Version:    0.5.4
 */