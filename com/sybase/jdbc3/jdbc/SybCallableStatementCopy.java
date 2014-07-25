/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class SybCallableStatementCopy extends SybCallableStatement
/*    */ {
/*    */   SybCallableStatementCopy(ProtocolContext paramProtocolContext, SybCallableStatement paramSybCallableStatement)
/*    */     throws SQLException
/*    */   {
/* 47 */     super(null, paramProtocolContext, paramSybCallableStatement);
/*    */   }
/*    */ 
/*    */   public void switchContext(ProtocolContext paramProtocolContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws SQLException
/*    */   {
/* 64 */     close(false);
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybCallableStatementCopy
 * JD-Core Version:    0.5.4
 */