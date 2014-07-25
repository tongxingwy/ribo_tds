/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import com.sybase.jdbc3.utils.CacheManager;
/*    */ import com.sybase.jdbc3.utils.Cacheable;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ class ResultGetter
/*    */   implements Cacheable
/*    */ {
/*    */   protected SybStatement _statement;
/*    */   private CacheManager _cm;
/*    */ 
/*    */   public ResultGetter(SybStatement paramSybStatement)
/*    */   {
/* 37 */     this._statement = paramSybStatement;
/*    */   }
/*    */ 
/*    */   public void open(boolean paramBoolean)
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void reset() throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void resetInputStream(InputStream paramInputStream)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void setManager(CacheManager paramCacheManager)
/*    */   {
/* 60 */     this._cm = paramCacheManager;
/*    */   }
/*    */ 
/*    */   public void cache() throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public int getState()
/*    */   {
/* 69 */     return 1;
/*    */   }
/*    */ 
/*    */   protected int nextResult()
/*    */     throws SQLException
/*    */   {
/* 75 */     int i = -1;
/*    */     try
/*    */     {
/* 78 */       this._cm.open(this);
/* 79 */       i = this._statement._protocol.nextResult(this._statement._context);
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 83 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*    */     }
/*    */     finally
/*    */     {
/* 87 */       this._cm.doneReading();
/*    */     }
/* 89 */     return i;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.ResultGetter
 * JD-Core Version:    0.5.4
 */