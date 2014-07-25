/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class SybHAException extends IOException
/*    */ {
/*    */   private String _sqlState;
/*    */ 
/*    */   public SybHAException(String paramString1, String paramString2)
/*    */   {
/* 34 */     super(paramString2);
/* 35 */     this._sqlState = paramString1;
/*    */   }
/*    */ 
/*    */   public String getSqlState()
/*    */   {
/* 40 */     return this._sqlState;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybHAException
 * JD-Core Version:    0.5.4
 */