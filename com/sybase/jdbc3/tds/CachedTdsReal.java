/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class CachedTdsReal extends TdsReal
/*    */ {
/*    */   public CachedTdsReal(TdsReal paramTdsReal)
/*    */     throws IOException
/*    */   {
/* 43 */     super(paramTdsReal._context);
/*    */     try
/*    */     {
/* 46 */       this._value = paramTdsReal.getDouble();
/*    */     }
/*    */     catch (SQLException localSQLException)
/*    */     {
/*    */     }
/*    */ 
/* 52 */     paramTdsReal.copyInto(this);
/*    */   }
/*    */ 
/*    */   public void cache()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   protected void beginRead()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   protected void doRead()
/*    */     throws SQLException
/*    */   {
/*    */   }
/*    */ 
/*    */   protected void getSize()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void startRead()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void endRead()
/*    */     throws SQLException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void initialize()
/*    */   {
/* 94 */     if (this._state == 3)
/*    */       return;
/* 96 */     this._state = 3;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CachedTdsReal
 * JD-Core Version:    0.5.4
 */