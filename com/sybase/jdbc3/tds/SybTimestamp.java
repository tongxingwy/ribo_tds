/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.jdbc.DateObject;
/*    */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Timestamp;
/*    */ 
/*    */ public class SybTimestamp extends Timestamp
/*    */ {
/*    */   /** @deprecated */
/*    */   public SybTimestamp(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*    */   {
/* 38 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*    */   }
/*    */ 
/*    */   public SybTimestamp(long paramLong) {
/* 42 */     super(paramLong);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 49 */     String str = super.toString();
/* 50 */     int i = str.lastIndexOf('.');
/* 51 */     if (i >= 0)
/*    */     {
/* 53 */       int j = i + 4;
/* 54 */       if (j > str.length())
/*    */       {
/* 56 */         j = str.length();
/*    */       }
/* 58 */       str = str.substring(0, j);
/*    */     }
/* 60 */     return str;
/*    */   }
/*    */ 
/*    */   public static void checkNanos(DateObject paramDateObject)
/*    */     throws SQLException
/*    */   {
/* 78 */     int i = paramDateObject.getNanos() % 10000000;
/* 79 */     switch (i)
/*    */     {
/*    */     case 0:
/*    */     case 3333333:
/*    */     case 6666666:
/* 84 */       break;
/*    */     default:
/* 87 */       ErrorMessage.raiseWarning("01S07");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SybTimestamp
 * JD-Core Version:    0.5.4
 */