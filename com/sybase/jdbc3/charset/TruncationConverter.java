/*     */ package com.sybase.jdbc3.charset;
/*     */ 
/*     */ import com.sybase.jdbcx.CharsetConverter;
/*     */ import java.io.CharConversionException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class TruncationConverter
/*     */   implements CharsetConverter
/*     */ {
/*     */   private static final short SEVEN_BITS = 127;
/*     */   private static final short EIGHT_BITS = 255;
/*     */   private short _mask;
/*     */ 
/*     */   public TruncationConverter()
/*     */     throws UnsupportedEncodingException
/*     */   {
/*  49 */     this._mask = 127;
/*     */   }
/*     */ 
/*     */   public void setEncoding(String paramString)
/*     */     throws UnsupportedEncodingException
/*     */   {
/*  64 */     if (paramString.equals("ISO8859_1"))
/*     */     {
/*  66 */       this._mask = 255;
/*     */     }
/*     */     else
/*     */     {
/*  70 */       this._mask = 127;
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] fromUnicode(String paramString)
/*     */     throws CharConversionException
/*     */   {
/*  81 */     int i = paramString.length();
/*  82 */     byte[] arrayOfByte = new byte[i];
/*  83 */     for (int j = 0; j < i; ++j)
/*     */     {
/*  85 */       int k = (short)paramString.charAt(j);
/*  86 */       if ((k & this._mask) != k)
/*     */       {
/*  89 */         throw new CharConversionException(String.valueOf(paramString.charAt(j)));
/*     */       }
/*     */ 
/*  92 */       arrayOfByte[j] = (byte)(k & this._mask);
/*     */     }
/*  94 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public String toUnicode(byte[] paramArrayOfByte)
/*     */     throws CharConversionException
/*     */   {
/* 103 */     return new String(paramArrayOfByte, 0);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.charset.TruncationConverter
 * JD-Core Version:    0.5.4
 */