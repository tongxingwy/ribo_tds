/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class TdsNumeric
/*     */ {
/*  75 */   private static final int[] NUME_PREC_TO_LEN = { -1, 2, 2, 3, 3, 4, 4, 4, 5, 5, 6, 6, 6, 7, 7, 8, 8, 9, 9, 9, 10, 10, 11, 11, 11, 12, 12, 13, 13, 14, 14, 14, 15, 15, 16, 16, 16, 17, 17, 18, 18, 19, 19, 19, 20, 20, 21, 21, 21, 22, 22, 23, 23, 24, 24, 24, 25, 25, 26, 26, 26, 27, 27, 28, 28, 28, 29, 29, 30, 30, 31, 31, 31, 32, 32, 33, 33, 33, 34, 34, 35, 35, 36, 36, 36, 37, 37, 38, 38, 38, 39, 39, 40, 40, 41, 41, 41, 42, 42, 43, 43, 43, 44, 44, 45, 45, 46, 46, 46, 47, 47, 48, 48, 48, 49, 49, 50, 50, 50, 51, 51, 52, 52, 53, 53, 53, 54, 54 };
/*     */ 
/*  88 */   protected static final int NUME_MAXPREC = NUME_PREC_TO_LEN.length - 1;
/*     */   private static final int NUME_BASEBITS = 8;
/*     */   protected static final int NUME_USERPREC = 38;
/*     */   private static final int NUME_MAXLEN = 33;
/*     */   private static final int NUME_NULLSCALE = 19;
/*  94 */   private static final BigDecimal D_ZERO = BigDecimal.valueOf(0L, 0);
/*  95 */   private static final BigDecimal NEGATIVE_ONE = BigDecimal.valueOf(-1L, 0);
/*     */ 
/* 101 */   private static BigDecimal MAX_NUMERIC = null;
/* 102 */   private static BigDecimal MIN_NUMERIC = null;
/*     */ 
/*     */   protected static BigDecimal numericValue(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 138 */     if (paramArrayOfByte == null)
/*     */     {
/* 140 */       return null;
/*     */     }
/* 142 */     int i = paramArrayOfByte.length;
/*     */ 
/* 148 */     int j = NUME_PREC_TO_LEN[paramInt1];
/*     */ 
/* 153 */     int k = (paramArrayOfByte[0] == 0) ? 1 : -1;
/*     */ 
/* 156 */     paramArrayOfByte[0] = 0;
/*     */ 
/* 158 */     byte[] arrayOfByte = paramArrayOfByte;
/* 159 */     if (i > j)
/*     */     {
/* 171 */       if (j > (i + 1) / 2)
/*     */       {
/* 194 */         ErrorMessage.raiseIOException("JZ00B");
/*     */       }
/* 196 */       int l = 1;
/* 197 */       for (int i1 = i - 1; i1 >= j; --i1)
/*     */       {
/* 199 */         if (paramArrayOfByte[i1] == 0)
/*     */           continue;
/* 201 */         l = 0;
/* 202 */         break;
/*     */       }
/*     */ 
/* 205 */       if (l != 0)
/*     */       {
/* 207 */         arrayOfByte = new byte[j];
/* 208 */         System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, j);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 215 */     BigDecimal localBigDecimal = new BigDecimal(new BigInteger(k, arrayOfByte), paramInt2);
/*     */ 
/* 217 */     paramArrayOfByte[0] = (byte)((k == 1) ? 0 : 1);
/* 218 */     return localBigDecimal;
/*     */   }
/*     */ 
/*     */   protected static byte[] tdsNumeric(BigDecimal paramBigDecimal, int paramInt, int[] paramArrayOfInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 236 */     if (paramBigDecimal == null)
/*     */     {
/* 243 */       paramArrayOfInt[1] = 33;
/* 244 */       paramArrayOfInt[2] = 38;
/* 245 */       if (paramInt >= 0)
/*     */       {
/* 247 */         paramArrayOfInt[3] = paramInt;
/*     */       }
/*     */       else
/*     */       {
/* 251 */         paramArrayOfInt[3] = 19;
/*     */       }
/* 253 */       return (byte[])null;
/*     */     }
/* 255 */     if ((!paramBoolean) && ((
/* 257 */       (MAX_NUMERIC.compareTo(paramBigDecimal) < 0) || (MIN_NUMERIC.compareTo(paramBigDecimal) > 0))))
/*     */     {
/* 260 */       ErrorMessage.raiseIOException("JZ00B");
/*     */     }
/*     */ 
/* 263 */     int i = 0;
/* 264 */     if (paramBigDecimal.compareTo(D_ZERO) < 0)
/*     */     {
/* 266 */       i = 1;
/*     */     }
/* 268 */     int j = paramArrayOfInt[2];
/* 269 */     if (j == 0)
/*     */     {
/* 271 */       j = -1;
/*     */     }
/*     */     int k;
/* 274 */     if (paramBigDecimal instanceof SybBigDecimal)
/*     */     {
/* 276 */       k = ((SybBigDecimal)paramBigDecimal)._scale;
/* 277 */       j = ((SybBigDecimal)paramBigDecimal)._precision;
/*     */     }
/*     */     else
/*     */     {
/* 283 */       k = paramInt;
/*     */     }
/* 285 */     paramArrayOfInt[3] = k;
/*     */ 
/* 288 */     BigInteger localBigInteger = unscale(paramBigDecimal, k);
/*     */ 
/* 299 */     byte[] arrayOfByte1 = localBigInteger.toByteArray();
/*     */ 
/* 303 */     int l = String.valueOf(localBigInteger).length();
/* 304 */     int i1 = 0;
/* 305 */     if (j >= l)
/*     */     {
/* 309 */       i1 = NUME_PREC_TO_LEN[j];
/*     */     }
/* 311 */     else if (paramArrayOfInt[2] > 0)
/*     */     {
/* 313 */       i1 = NUME_PREC_TO_LEN[j];
/*     */     }
/*     */     else
/*     */     {
/* 320 */       j = l;
/* 321 */       if (j < k)
/*     */       {
/* 323 */         j = k;
/*     */       }
/*     */ 
/* 327 */       i1 = NUME_PREC_TO_LEN[j];
/*     */     }
/*     */ 
/* 354 */     byte[] arrayOfByte2 = null;
/* 355 */     if ((paramArrayOfInt[2] == 0) && (paramArrayOfInt[1] > 0))
/*     */     {
/* 357 */       arrayOfByte2 = new byte[paramArrayOfInt[1]];
/* 358 */       if (i1 != paramArrayOfInt[1])
/*     */       {
/* 360 */         int i2 = arrayOfByte1.length - 1;
/* 361 */         int i3 = arrayOfByte2.length - 1;
/*     */         while (true) { if ((i3 < 0) || (i2 < 0))
/*     */             break label346;
/* 363 */           if (arrayOfByte1[i2] != 0)
/*     */           {
/* 365 */             arrayOfByte2[i3] = arrayOfByte1[i2];
/* 366 */             --i3;
/*     */           }
/* 368 */           --i2; }
/*     */ 
/*     */ 
/*     */       }
/*     */ 
/* 373 */       System.arraycopy(arrayOfByte1, 0, arrayOfByte2, i1 - arrayOfByte1.length, arrayOfByte1.length);
/*     */     }
/*     */     else
/*     */     {
/* 378 */       arrayOfByte2 = new byte[i1];
/* 379 */       System.arraycopy(arrayOfByte1, 0, arrayOfByte2, i1 - arrayOfByte1.length, arrayOfByte1.length);
/* 380 */       arrayOfByte2[0] = (byte)i;
/*     */     }
/* 382 */     label346: paramArrayOfInt[2] = j;
/* 383 */     paramArrayOfInt[1] = arrayOfByte2.length;
/*     */ 
/* 388 */     return arrayOfByte2;
/*     */   }
/*     */ 
/*     */   public static void checkRange(BigDecimal paramBigDecimal, int paramInt) throws SQLException {
/* 392 */     BigDecimal localBigDecimal = paramBigDecimal.movePointRight(paramInt);
/* 393 */     if ((MAX_NUMERIC.compareTo(localBigDecimal) >= 0) && (MIN_NUMERIC.compareTo(localBigDecimal) <= 0)) {
/*     */       return;
/*     */     }
/*     */ 
/* 397 */     ErrorMessage.raiseError("JZ00B");
/*     */   }
/*     */ 
/*     */   protected static BigInteger unscale(BigDecimal paramBigDecimal, int paramInt)
/*     */   {
/* 409 */     if (paramBigDecimal.compareTo(D_ZERO) < 0)
/*     */     {
/* 411 */       paramBigDecimal = paramBigDecimal.multiply(NEGATIVE_ONE);
/*     */     }
/* 413 */     return paramBigDecimal.movePointRight(paramInt).toBigInteger();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 105 */     StringBuffer localStringBuffer = new StringBuffer();
/* 106 */     for (int i = 0; i < NUME_MAXPREC; ++i)
/*     */     {
/* 108 */       localStringBuffer.append("9");
/*     */     }
/* 110 */     MAX_NUMERIC = new BigDecimal(localStringBuffer.toString());
/* 111 */     MIN_NUMERIC = new BigDecimal("-" + localStringBuffer.toString());
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsNumeric
 * JD-Core Version:    0.5.4
 */