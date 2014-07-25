/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ public class HexConverts
/*     */ {
/*  39 */   public static final int[] HEX_INTS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*     */   private static final String HEX_DIGITS = "0123456789ABCDEF";
/*     */ 
/*     */   public static String hexConvert(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/*  60 */     if (paramArrayOfByte == null) return null;
/*     */ 
/*  62 */     StringBuffer localStringBuffer = new StringBuffer(2 * paramInt);
/*  63 */     for (int i = 0; i < paramInt; ++i)
/*     */     {
/*  65 */       localStringBuffer.append("0123456789ABCDEF".charAt((paramArrayOfByte[i] & 0xF0) >> 4));
/*  66 */       localStringBuffer.append("0123456789ABCDEF".charAt(paramArrayOfByte[i] & 0xF));
/*     */     }
/*  68 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   public static String hexConvert(byte[] paramArrayOfByte)
/*     */   {
/*  74 */     return hexConvert(paramArrayOfByte, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public static byte[] hexConvertToBytes(String paramString)
/*     */   {
/*  80 */     byte[] arrayOfByte = new byte[paramString.length() / 2];
/*  81 */     for (int i = 0; i < arrayOfByte.length; ++i)
/*     */     {
/*  83 */       int j = i * 2;
/*  84 */       int k = Integer.parseInt(paramString.substring(j, j + 2), 16);
/*  85 */       arrayOfByte[i] = (byte)k;
/*     */     }
/*  87 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public static String binaryConvert(long paramLong, int paramInt)
/*     */   {
/*  98 */     String str = Long.toBinaryString(paramLong);
/*  99 */     int i = paramInt * 8 - str.length();
/* 100 */     if (i < 0)
/*     */     {
/* 102 */       throw new IllegalArgumentException("The value translates to more than the specified number of digits");
/*     */     }
/*     */ 
/* 106 */     while (i-- > 0)
/*     */     {
/* 108 */       str = "0" + str;
/*     */     }
/* 110 */     return str;
/*     */   }
/*     */ 
/*     */   public static String hexConvert(long paramLong, int paramInt)
/*     */   {
/* 122 */     String str = Long.toHexString(paramLong).toUpperCase();
/* 123 */     int i = paramInt * 2 - str.length();
/* 124 */     if (i < 0)
/*     */     {
/* 127 */       if (str.regionMatches(0, "FFFFFFFFFFFFFFFF", 0, -i))
/*     */       {
/* 129 */         str = str.substring(-i, str.length());
/*     */       }
/*     */       else
/*     */       {
/* 133 */         throw new IllegalArgumentException("The value translates to more than the specified number of digits");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 138 */     while (i-- > 0)
/*     */     {
/* 140 */       str = "0" + str;
/*     */     }
/* 142 */     return str;
/*     */   }
/*     */ 
/*     */   public static String hexConvert(int paramInt1, int paramInt2)
/*     */   {
/* 154 */     String str = Integer.toHexString(paramInt1).toUpperCase();
/* 155 */     int i = paramInt2 * 2 - str.length();
/* 156 */     if (i < 0)
/*     */     {
/* 159 */       if (str.regionMatches(0, "FFFFFFFFFFFFFFFF", 0, -i))
/*     */       {
/* 161 */         str = str.substring(-i, str.length());
/*     */       }
/*     */       else
/*     */       {
/* 165 */         throw new IllegalArgumentException("The value translates to more than the specified number of digits");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 170 */     while (i-- > 0)
/*     */     {
/* 172 */       str = "0" + str;
/*     */     }
/* 174 */     return str;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.HexConverts
 * JD-Core Version:    0.5.4
 */