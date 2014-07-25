/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbcx.EedInfo;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLWarning;
/*     */ 
/*     */ public class SybSQLWarning extends SQLWarning
/*     */   implements EedInfo
/*     */ {
/*     */   private int _state;
/*     */   private int _severity;
/*     */   private String _serverName;
/*     */   private String _procName;
/*     */   private int _lineNum;
/*     */   private transient ResultSet _params;
/*     */   private int _tranState;
/*     */   private int _status;
/*     */ 
/*     */   public SybSQLWarning(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, String paramString3, String paramString4, int paramInt4, ResultSet paramResultSet, int paramInt5, int paramInt6)
/*     */   {
/*  86 */     super(paramString1, paramString2, paramInt1);
/*     */ 
/*  88 */     this._state = paramInt2;
/*  89 */     this._severity = paramInt3;
/*  90 */     this._serverName = paramString3;
/*  91 */     this._procName = paramString4;
/*  92 */     this._lineNum = paramInt4;
/*  93 */     this._params = paramResultSet;
/*  94 */     this._tranState = paramInt5;
/*  95 */     this._status = paramInt6;
/*     */   }
/*     */ 
/*     */   public int getState()
/*     */   {
/* 104 */     return this._state;
/*     */   }
/*     */ 
/*     */   public int getSeverity()
/*     */   {
/* 113 */     return this._severity;
/*     */   }
/*     */ 
/*     */   public String getServerName()
/*     */   {
/* 122 */     return this._serverName;
/*     */   }
/*     */ 
/*     */   public String getProcedureName()
/*     */   {
/* 131 */     return this._procName;
/*     */   }
/*     */ 
/*     */   public int getLineNumber()
/*     */   {
/* 140 */     return this._lineNum;
/*     */   }
/*     */ 
/*     */   public ResultSet getEedParams()
/*     */   {
/* 149 */     return this._params;
/*     */   }
/*     */ 
/*     */   public int getTranState()
/*     */   {
/* 158 */     return this._tranState;
/*     */   }
/*     */ 
/*     */   public int getStatus()
/*     */   {
/* 167 */     return this._status;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybSQLWarning
 * JD-Core Version:    0.5.4
 */