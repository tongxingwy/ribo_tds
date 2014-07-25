/*    */ package com.sybase.jdbc3.charset;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.CharsetDecoder;
/*    */ import java.nio.charset.CharsetEncoder;
/*    */ 
/*    */ public class SybUTF8Charset extends Charset
/*    */ {
/*    */   public static final String CHARSET_NAME = "x-SybUTF8";
/* 33 */   private static SybUTF8Charset _thisRef = new SybUTF8Charset("x-SybUTF8", new String[] { "x-SybUTF8" });
/*    */   public static final int maxBytesPerChar = 3;
/*    */   public static final int maxCharsPerByte = 1;
/*    */ 
/*    */   private SybUTF8Charset(String paramString, String[] paramArrayOfString)
/*    */   {
/* 41 */     super(paramString, paramArrayOfString);
/*    */   }
/*    */ 
/*    */   public static SybUTF8Charset getInstance()
/*    */   {
/* 46 */     return _thisRef;
/*    */   }
/*    */ 
/*    */   public boolean contains(Charset paramCharset)
/*    */   {
/* 56 */     return paramCharset.name().equalsIgnoreCase("x-SybUTF8");
/*    */   }
/*    */ 
/*    */   public CharsetDecoder newDecoder()
/*    */   {
/* 66 */     return new SybUTF8CharsetDecoder(this, 1.0F, 1.0F);
/*    */   }
/*    */ 
/*    */   public CharsetEncoder newEncoder()
/*    */   {
/* 89 */     return new SybUTF8CharsetEncoder(this, 3.0F, 3.0F);
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.charset.SybUTF8Charset
 * JD-Core Version:    0.5.4
 */