/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public abstract class SybUrlProviderImplBase
/*     */   implements SybUrlProvider
/*     */ {
/*     */   protected String _rmName;
/*     */   protected String _rmNameDefault;
/*     */   protected int _rmType;
/*     */   protected String _dbName;
/*     */   protected SybProperty _sybProps;
/*     */   protected Vector _hostportList;
/*     */   protected Vector _secondaryHostportList;
/*     */   protected Protocol _protocol;
/*     */   protected String _dataSourceInterface;
/*     */ 
/*     */   public SybUrlProviderImplBase()
/*     */   {
/*  56 */     this._rmName = null;
/*     */ 
/*  65 */     this._rmNameDefault = null;
/*     */ 
/*  71 */     this._rmType = 0;
/*     */ 
/*  74 */     this._dbName = null;
/*     */ 
/*  77 */     this._sybProps = null;
/*     */ 
/*  83 */     this._hostportList = null;
/*     */ 
/*  88 */     this._secondaryHostportList = null;
/*     */ 
/*  93 */     this._protocol = null;
/*     */ 
/* 100 */     this._dataSourceInterface = "DataSource";
/*     */   }
/*     */ 
/*     */   public Vector getHostPortList()
/*     */   {
/* 107 */     return this._hostportList;
/*     */   }
/*     */ 
/*     */   public Vector getSecondaryHostPortList()
/*     */   {
/* 112 */     return this._secondaryHostportList;
/*     */   }
/*     */ 
/*     */   public Protocol getProtocol()
/*     */   {
/* 120 */     return this._protocol;
/*     */   }
/*     */ 
/*     */   public String getDatabaseName()
/*     */   {
/* 128 */     return this._dbName;
/*     */   }
/*     */ 
/*     */   public SybProperty getSybProperty()
/*     */   {
/* 136 */     return this._sybProps;
/*     */   }
/*     */ 
/*     */   public String getResourceManagerName()
/*     */   {
/* 145 */     return this._rmName;
/*     */   }
/*     */ 
/*     */   public int getResourceManagerType()
/*     */   {
/* 159 */     return this._rmType;
/*     */   }
/*     */ 
/*     */   public String getDataSourceInterface()
/*     */   {
/* 169 */     return this._dataSourceInterface;
/*     */   }
/*     */ 
/*     */   public abstract void init(String paramString1, String paramString2, Properties paramProperties, SybProperty paramSybProperty)
/*     */     throws SQLException;
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybUrlProviderImplBase
 * JD-Core Version:    0.5.4
 */