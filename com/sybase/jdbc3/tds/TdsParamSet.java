/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class TdsParamSet extends TdsResultSet
/*    */ {
/*    */   private boolean _empty;
/*    */ 
/*    */   protected TdsParamSet(TdsProtocolContext paramTdsProtocolContext, boolean paramBoolean, int paramInt)
/*    */     throws SQLException
/*    */   {
/* 41 */     super(paramTdsProtocolContext);
/* 42 */     this._empty = paramBoolean;
/* 43 */     this._tdsResultSetType = paramInt;
/*    */   }
/*    */ 
/*    */   public boolean next()
/*    */     throws SQLException
/*    */   {
/* 59 */     if ((this._rowCount == 0) && (!this._empty))
/*    */     {
/* 61 */       this._needNext = false;
/* 62 */       this._rowCount += 1;
/* 63 */       return true;
/*    */     }
/* 65 */     if ((this._rowCount == 1) || (this._empty))
/*    */     {
/* 67 */       this._tpc._lastResult = -1;
/*    */       try
/*    */       {
/* 70 */         for (int i = 0; i < this._columns.length; ++i)
/*    */         {
/* 72 */           this._columns[i].clear();
/*    */         }
/*    */       }
/*    */       catch (IOException localIOException)
/*    */       {
/* 77 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*    */       }
/*    */     }
/* 80 */     this._rowCount += 1;
/* 81 */     markDead();
/* 82 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsParamSet
 * JD-Core Version:    0.5.4
 */