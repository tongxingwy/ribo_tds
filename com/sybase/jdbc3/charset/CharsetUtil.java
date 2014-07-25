/*     */ package com.sybase.jdbc3.charset;
/*     */ 
/*     */ import java.io.CharConversionException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.CharacterCodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ 
/*     */ public class CharsetUtil
/*     */ {
/*  31 */   private static ThreadLocal charsetEncoder = new ThreadLocal();
/*  32 */   private static ThreadLocal charsetDecoder = new ThreadLocal();
/*     */ 
/*     */   public static byte[] fromSybUTF8(String paramString) throws CharConversionException
/*     */   {
/*  36 */     return fromUnicode(paramString, "x-SybUTF8", SybUTF8Charset.getInstance());
/*     */   }
/*     */ 
/*     */   public static byte[] fromUnicode(String paramString1, String paramString2, Charset paramCharset)
/*     */     throws CharConversionException
/*     */   {
/*  44 */     if (paramString2 == null)
/*     */     {
/*  46 */       return paramString1.getBytes();
/*     */     }
/*     */ 
/*  49 */     byte[] arrayOfByte = null;
/*  50 */     CharsetEncoder localCharsetEncoder = (CharsetEncoder)charsetEncoder.get();
/*     */ 
/*  52 */     if (localCharsetEncoder == null)
/*     */     {
/*     */       try
/*     */       {
/*  58 */         localCharsetEncoder = paramCharset.newEncoder();
/*  59 */         charsetEncoder.set(localCharsetEncoder);
/*     */       }
/*     */       catch (UnsupportedOperationException localUnsupportedOperationException)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  72 */       ByteBuffer localByteBuffer = localCharsetEncoder.encode(CharBuffer.wrap(paramString1));
/*  73 */       arrayOfByte = new byte[localByteBuffer.limit()];
/*  74 */       localByteBuffer.get(arrayOfByte);
/*     */     }
/*     */     catch (CharacterCodingException localCharacterCodingException)
/*     */     {
/*  78 */       CharConversionException localCharConversionException = new CharConversionException(localCharacterCodingException.toString());
/*     */ 
/*  80 */       localCharConversionException.initCause(localCharacterCodingException);
/*  81 */       throw localCharConversionException;
/*     */     }
/*     */     catch (IllegalStateException localIllegalStateException)
/*     */     {
/*  89 */       throw new CharConversionException(localIllegalStateException.toString());
/*     */     }
/*     */ 
/*  92 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public static String toSybUTF8(byte[] paramArrayOfByte) throws CharConversionException
/*     */   {
/*  97 */     return toUnicode(paramArrayOfByte, "x-SybUTF8", SybUTF8Charset.getInstance());
/*     */   }
/*     */ 
/*     */   public static String toUnicode(byte[] paramArrayOfByte, String paramString, Charset paramCharset)
/*     */     throws CharConversionException
/*     */   {
/* 106 */     if (paramString == null)
/*     */     {
/* 108 */       return new String(paramArrayOfByte);
/*     */     }
/*     */ 
/* 111 */     String str = null;
/* 112 */     CharsetDecoder localCharsetDecoder = (CharsetDecoder)charsetDecoder.get();
/*     */ 
/* 116 */     if (localCharsetDecoder == null)
/*     */     {
/* 118 */       localCharsetDecoder = paramCharset.newDecoder();
/* 119 */       charsetDecoder.set(localCharsetDecoder);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 124 */       str = localCharsetDecoder.decode(ByteBuffer.wrap(paramArrayOfByte)).toString();
/*     */     }
/*     */     catch (CharacterCodingException localCharacterCodingException)
/*     */     {
/* 128 */       CharConversionException localCharConversionException = new CharConversionException(localCharacterCodingException.toString());
/*     */ 
/* 130 */       localCharConversionException.initCause(localCharacterCodingException);
/* 131 */       throw localCharConversionException;
/*     */     }
/*     */     catch (IllegalStateException localIllegalStateException)
/*     */     {
/* 139 */       throw new CharConversionException(localIllegalStateException.toString());
/*     */     }
/* 141 */     return str;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.charset.CharsetUtil
 * JD-Core Version:    0.5.4
 */