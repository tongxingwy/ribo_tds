/*    */ package com.sybase.jdbc3.charset;
/*    */ 
/*    */ import java.io.CharConversionException;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ 
/*    */ public class CheckPureConverter extends PureConverter
/*    */ {
/*    */   public CheckPureConverter()
/*    */     throws UnsupportedEncodingException
/*    */   {
/*    */   }
/*    */ 
/*    */   public byte[] fromUnicode(String paramString)
/*    */     throws CharConversionException
/*    */   {
/* 62 */     byte[] arrayOfByte = null;
/*    */     try
/*    */     {
/* 65 */       if (this._encoding == null)
/*    */       {
/* 67 */         arrayOfByte = paramString.getBytes();
/*    */       }
/* 69 */       else if (this._encoding.equals("x-SybUTF8"))
/*    */       {
/* 71 */         arrayOfByte = CharsetUtil.fromSybUTF8(paramString);
/*    */       }
/*    */       else
/*    */       {
/* 75 */         arrayOfByte = paramString.getBytes(this._encoding);
/*    */       }
/*    */ 
/* 78 */       String str = toUnicode(arrayOfByte);
/* 79 */       if (!paramString.equals(str))
/*    */       {
/* 84 */         throw new CharConversionException();
/*    */       }
/*    */ 
/*    */     }
/*    */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*    */     {
/* 90 */       throw new CharConversionException(localUnsupportedEncodingException.toString());
/*    */     }
/* 92 */     return arrayOfByte;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.charset.CheckPureConverter
 * JD-Core Version:    0.5.4
 */