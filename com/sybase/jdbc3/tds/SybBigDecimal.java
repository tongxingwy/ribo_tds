/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*    */ import java.io.Serializable;
/*    */ import java.math.BigDecimal;
/*    */ import java.math.BigInteger;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class SybBigDecimal extends BigDecimal
/*    */   implements Serializable
/*    */ {
/*    */   protected int _precision;
/*    */   protected int _scale;
/*    */ 
/*    */   public SybBigDecimal(BigDecimal paramBigDecimal, int paramInt1, int paramInt2)
/*    */     throws SQLException
/*    */   {
/* 43 */     super(paramBigDecimal.toString());
/* 44 */     if ((paramInt1 < 1) || (paramInt2 < 0) || (paramInt1 < paramInt2) || (paramInt1 > TdsNumeric.NUME_MAXPREC))
/*    */     {
/* 47 */       ErrorMessage.raiseError("JZ00A");
/*    */     }
/*    */ 
/* 51 */     int i = paramBigDecimal.scale();
/* 52 */     String str = TdsNumeric.unscale(paramBigDecimal, i).toString();
/* 53 */     int j = 0;
/* 54 */     for (int k = 0; k < str.length(); ++k)
/*    */     {
/* 56 */       if (!Character.isDigit(str.charAt(k)))
/*    */         continue;
/* 58 */       ++j;
/*    */     }
/*    */ 
/* 63 */     if ((i > paramInt2) || (j > paramInt1))
/*    */     {
/* 65 */       ErrorMessage.raiseError("JZ00C", paramBigDecimal.toString());
/*    */     }
/*    */ 
/* 68 */     this._precision = paramInt1;
/* 69 */     this._scale = paramInt2;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SybBigDecimal
 * JD-Core Version:    0.5.4
 */