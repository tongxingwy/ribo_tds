/*     */ package com.sybase.jdbc3.charset;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ 
/*     */ public class SybUTF8CharsetEncoder extends CharsetEncoder
/*     */ {
/*  30 */   private boolean isSet = false;
/*  31 */   private char storedChar = '\000';
/*     */ 
/*     */   public SybUTF8CharsetEncoder(Charset paramCharset, float paramFloat1, float paramFloat2)
/*     */   {
/*  36 */     super(paramCharset, paramFloat1, paramFloat2);
/*     */   }
/*     */ 
/*     */   public SybUTF8CharsetEncoder(Charset paramCharset, float paramFloat1, float paramFloat2, byte[] paramArrayOfByte)
/*     */   {
/*  46 */     super(paramCharset, paramFloat1, paramFloat2, paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */   {
/*  91 */     if ((this.isSet) && (paramCharBuffer.position() > 0) && (this.storedChar != paramCharBuffer.get(paramCharBuffer.position() - 1)))
/*     */     {
/*  94 */       return CoderResult.malformedForLength(paramCharBuffer.position());
/*     */     }
/*     */ 
/*  97 */     while (paramCharBuffer.hasRemaining())
/*     */     {
/*  99 */       if (!paramByteBuffer.hasRemaining())
/*     */       {
/* 101 */         return CoderResult.OVERFLOW;
/*     */       }
/*     */ 
/* 104 */       if (!this.isSet)
/*     */       {
/* 106 */         this.storedChar = paramCharBuffer.get();
/* 107 */         this.isSet = true;
/*     */       }
/*     */ 
/* 113 */       if (this.storedChar <= '')
/*     */       {
/* 115 */         paramByteBuffer.put((byte)this.storedChar);
/* 116 */         this.isSet = false;
/*     */       }
/*     */ 
/* 122 */       if (this.storedChar <= '߿')
/*     */       {
/* 124 */         if (paramByteBuffer.remaining() < 2)
/*     */         {
/* 126 */           return CoderResult.OVERFLOW;
/*     */         }
/* 128 */         paramByteBuffer.put((byte)(0xC0 | this.storedChar >> '\006'));
/* 129 */         paramByteBuffer.put((byte)(0x80 | this.storedChar & 0x3F));
/* 130 */         this.isSet = false;
/*     */       }
/*     */ 
/* 146 */       if (paramByteBuffer.remaining() < 3)
/*     */       {
/* 148 */         return CoderResult.OVERFLOW;
/*     */       }
/* 150 */       paramByteBuffer.put((byte)(0xE0 | this.storedChar >> '\f'));
/* 151 */       paramByteBuffer.put((byte)(0x80 | this.storedChar >> '\006' & 0x3F));
/* 152 */       paramByteBuffer.put((byte)(0x80 | this.storedChar & 0x3F));
/* 153 */       this.isSet = false;
/*     */     }
/*     */ 
/* 156 */     return CoderResult.UNDERFLOW;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.charset.SybUTF8CharsetEncoder
 * JD-Core Version:    0.5.4
 */