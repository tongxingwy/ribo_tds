/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class SybDebug
/*    */   implements com.sybase.jdbcx.Debug
/*    */ {
/*    */   public void debug(boolean paramBoolean, String paramString)
/*    */     throws IOException
/*    */   {
/* 27 */     com.sybase.jdbc3.utils.Debug.debug(paramBoolean, paramString);
/*    */   }
/*    */ 
/*    */   public void debug(boolean paramBoolean, String paramString, PrintStream paramPrintStream)
/*    */     throws IOException
/*    */   {
/* 33 */     com.sybase.jdbc3.utils.Debug.debug(paramBoolean, paramString, paramPrintStream);
/*    */   }
/*    */ 
/*    */   public void startTimer(Object paramObject)
/*    */   {
/* 38 */     com.sybase.jdbc3.utils.Debug.startTimer(paramObject);
/*    */   }
/*    */ 
/*    */   public void stopTimer(Object paramObject, String paramString)
/*    */   {
/* 43 */     com.sybase.jdbc3.utils.Debug.stopTimer(paramObject, paramString);
/*    */   }
/*    */ 
/*    */   public void println(String paramString)
/*    */   {
/* 48 */     com.sybase.jdbc3.utils.Debug.println(paramString);
/*    */   }
/*    */ 
/*    */   public void println(Object paramObject, String paramString)
/*    */   {
/* 53 */     com.sybase.jdbc3.utils.Debug.println(paramObject, paramString);
/*    */   }
/*    */ 
/*    */   public void asrt(Object paramObject, boolean paramBoolean, String paramString)
/*    */     throws RuntimeException
/*    */   {
/* 59 */     com.sybase.jdbc3.utils.Debug.asrt(paramObject, paramBoolean, paramString);
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybDebug
 * JD-Core Version:    0.5.4
 */