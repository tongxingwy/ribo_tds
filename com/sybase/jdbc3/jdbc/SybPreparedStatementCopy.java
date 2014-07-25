/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class SybPreparedStatementCopy extends SybPreparedStatement
/*    */ {
/*    */   SybPreparedStatementCopy(ProtocolContext paramProtocolContext, SybPreparedStatement paramSybPreparedStatement)
/*    */     throws SQLException
/*    */   {
/* 47 */     super(null, paramProtocolContext, paramSybPreparedStatement);
/*    */   }
/*    */ 
/*    */   public void switchContext(ProtocolContext paramProtocolContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws SQLException
/*    */   {
/* 65 */     close(false);
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybPreparedStatementCopy
 * JD-Core Version:    0.5.4
 */