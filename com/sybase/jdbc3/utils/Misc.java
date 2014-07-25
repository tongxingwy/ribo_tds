/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ import B;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.SecureRandom;
/*     */ import java.text.DateFormat;
/*     */ import java.text.ParseException;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ 
/*     */ public class Misc
/*     */ {
/*  38 */   private static final Calendar BASE = Calendar.getInstance();
/*     */   private static final long MAGICDELTA = 633826800000L;
/*     */   private static final String ASE = "AES";
/*  41 */   private static Integer BEG = new Integer(-1);
/*     */   private static final int K_LEN1 = 12;
/*     */   private static final int K_LEN2 = 4;
/*     */ 
/*     */   private Misc()
/*     */   {
/*  51 */     BASE.set(90, 1, 1);
/*     */   }
/*     */ 
/*     */   public static byte[] makeKey(String paramString1, String paramString2, Date paramDate)
/*     */   {
/*  57 */     int i = hashString(paramString1);
/*  58 */     int j = hashString(paramString2);
/*  59 */     int k = 1195853639;
/*  60 */     if (paramDate != null)
/*     */     {
/*  62 */       k = hashDate(paramDate);
/*     */     }
/*  64 */     byte[] arrayOfByte = new byte[12];
/*  65 */     arrayOfByte[0] = (byte)((i & 0xFF000000) >> 24);
/*  66 */     arrayOfByte[11] = (byte)((i & 0xFF0000) >> 16);
/*  67 */     arrayOfByte[2] = (byte)((i & 0xFF00) >> 8);
/*  68 */     arrayOfByte[9] = (byte)((i & 0xFF) >> 0);
/*  69 */     arrayOfByte[4] = (byte)((j & 0xFF000000) >> 24);
/*  70 */     arrayOfByte[7] = (byte)((j & 0xFF0000) >> 16);
/*  71 */     arrayOfByte[6] = (byte)((j & 0xFF00) >> 8);
/*  72 */     arrayOfByte[5] = (byte)((j & 0xFF) >> 0);
/*  73 */     arrayOfByte[8] = (byte)((k & 0xFF000000) >> 24);
/*  74 */     arrayOfByte[3] = (byte)((k & 0xFF0000) >> 16);
/*  75 */     arrayOfByte[10] = (byte)((k & 0xFF00) >> 8);
/*  76 */     arrayOfByte[1] = (byte)((k & 0xFF) >> 0);
/*  77 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   private static int hashString(String paramString)
/*     */   {
/*  83 */     long l = 47L;
/*  84 */     for (int i = 0; i < paramString.length(); ++i)
/*     */     {
/*  90 */       l += (paramString.charAt(i) << i % 25);
/*     */     }
/*  92 */     return (int)(l & 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   private static int hashDate(Date paramDate)
/*     */   {
/* 111 */     Calendar localCalendar = Calendar.getInstance();
/* 112 */     localCalendar.setTime(paramDate);
/* 113 */     GregorianCalendar localGregorianCalendar1 = new GregorianCalendar();
/* 114 */     localGregorianCalendar1.set(BASE.get(1), BASE.get(2), BASE.get(5), 0, 0, 0);
/* 115 */     long l1 = localGregorianCalendar1.getTime().getTime();
/* 116 */     GregorianCalendar localGregorianCalendar2 = new GregorianCalendar();
/* 117 */     localGregorianCalendar2.set(localCalendar.get(1), localCalendar.get(2), localCalendar.get(5), 0, 0, 0);
/* 118 */     long l2 = localGregorianCalendar2.getTime().getTime();
/* 119 */     long l3 = l2 - l1 + 633826800000L;
/*     */ 
/* 121 */     return (int)(l3 >> 24 & 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   public static BigDecimal[] divideAndRemainder(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2)
/*     */   {
/* 134 */     BigDecimal[] arrayOfBigDecimal = new BigDecimal[2];
/* 135 */     arrayOfBigDecimal[0] = paramBigDecimal1.divide(paramBigDecimal2, 1);
/* 136 */     arrayOfBigDecimal[1] = paramBigDecimal1.subtract(arrayOfBigDecimal[0].multiply(paramBigDecimal2));
/* 137 */     return arrayOfBigDecimal;
/*     */   }
/*     */ 
/*     */   public static void checkOutputFilePath(String paramString) throws IOException
/*     */   {
/* 142 */     File localFile = new File(paramString);
/* 143 */     if (!localFile.exists())
/*     */       return;
/* 145 */     throw new IOException("File " + paramString + " already exists.");
/*     */   }
/*     */ 
/*     */   public static byte[] encrypt(byte[] paramArrayOfByte)
/*     */   {
/* 151 */     if (paramArrayOfByte == null)
/*     */     {
/* 153 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 157 */       SecretKeySpec localSecretKeySpec = new SecretKeySpec(getMyBytes(), "AES");
/* 158 */       Cipher localCipher = Cipher.getInstance("AES");
/* 159 */       localCipher.init(1, localSecretKeySpec, localCipher.getParameters());
/* 160 */       return localCipher.doFinal(paramArrayOfByte);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */     catch (GeneralSecurityException localGeneralSecurityException)
/*     */     {
/*     */     }
/*     */ 
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */   private static byte[] getMyBytes() throws IOException
/*     */   {
/* 175 */     Date localDate = null;
/*     */     try
/*     */     {
/* 178 */       DateFormat localDateFormat = DateFormat.getDateInstance(1);
/* 179 */       localDate = localDateFormat.parse(SybVersion.BUILD_DATE);
/*     */     }
/*     */     catch (ParseException localParseException)
/*     */     {
/*     */     }
/*     */ 
/* 185 */     ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/*     */ 
/* 187 */     byte[] arrayOfByte1 = makeKey(localClassLoader.getClass().getName(), "AES", localDate);
/*     */ 
/* 189 */     SecureRandom localSecureRandom = new SecureRandom(arrayOfByte1);
/* 190 */     synchronized (BEG)
/*     */     {
/* 192 */       if (BEG.intValue() == -1)
/*     */       {
/* 194 */         BEG = new Integer(localSecureRandom.nextInt(256));
/*     */       }
/*     */     }
/* 197 */     ??? = Misc.class.getName().replace('.', '/') + ".class";
/*     */ 
/* 199 */     URL localURL = localClassLoader.getResource((String)???);
/* 200 */     InputStream localInputStream = localURL.openStream();
/* 201 */     byte[] arrayOfByte2 = new byte[4];
/* 202 */     localInputStream.skip(BEG.intValue());
/* 203 */     localInputStream.read(arrayOfByte2);
/* 204 */     byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
/* 205 */     int i = 0;
/* 206 */     for (i = 0; i < arrayOfByte1.length; ++i)
/*     */     {
/* 208 */       arrayOfByte3[i] = arrayOfByte1[i];
/*     */     }
/* 210 */     for (int j = 0; j < arrayOfByte2.length; ++j)
/*     */     {
/* 212 */       arrayOfByte3[(i + j)] = arrayOfByte2[j];
/*     */     }
/* 214 */     return (B)arrayOfByte3;
/*     */   }
/*     */ 
/*     */   public static byte[] decrypt(byte[] paramArrayOfByte)
/*     */   {
/* 219 */     if (paramArrayOfByte == null)
/*     */     {
/* 221 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 225 */       SecretKeySpec localSecretKeySpec = new SecretKeySpec(getMyBytes(), "AES");
/* 226 */       Cipher localCipher = Cipher.getInstance("AES");
/* 227 */       localCipher.init(2, localSecretKeySpec);
/* 228 */       return localCipher.doFinal(paramArrayOfByte);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */     catch (GeneralSecurityException localGeneralSecurityException)
/*     */     {
/*     */     }
/*     */ 
/* 238 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.Misc
 * JD-Core Version:    0.5.4
 */