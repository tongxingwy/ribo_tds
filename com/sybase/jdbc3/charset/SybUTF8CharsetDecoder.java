/*     */ package com.sybase.jdbc3.charset;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ 
/*     */ public class SybUTF8CharsetDecoder extends CharsetDecoder
/*     */ {
/*  31 */   private boolean[] areSet = { false, false, false, false };
/*  32 */   private int[] storedBytes = { 0, 0, 0, 0 };
/*     */ 
/*     */   public SybUTF8CharsetDecoder(Charset paramCharset, float paramFloat1, float paramFloat2)
/*     */   {
/*  42 */     super(paramCharset, paramFloat1, paramFloat2);
/*     */   }
/*     */ 
/*     */   protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */   {
/*  85 */     for (int k = this.storedBytes.length - 1; k >= 0; --k)
/*     */     {
/*  87 */       if ((this.areSet[k] != 0) && (paramByteBuffer.position() > this.storedBytes.length - k) && (this.storedBytes[k] != paramByteBuffer.get(paramByteBuffer.position() + k - this.storedBytes.length)))
/*     */       {
/*  92 */         return CoderResult.malformedForLength(paramByteBuffer.position());
/*     */       }
/*     */     }
/*     */     while (true) {
/*  96 */       if ((!paramByteBuffer.hasRemaining()) && (this.areSet[0] == 0))
/*     */         break label690;
/*  98 */       if (!paramCharBuffer.hasRemaining())
/*     */       {
/* 101 */         return CoderResult.OVERFLOW;
/*     */       }
/*     */ 
/* 106 */       if ((checkAndGet(0, paramByteBuffer) & 0x80) == 0)
/*     */       {
/* 108 */         if (!paramCharBuffer.hasRemaining())
/*     */         {
/* 110 */           return CoderResult.OVERFLOW;
/*     */         }
/* 112 */         paramCharBuffer.put((char)this.storedBytes[0]);
/* 113 */         this.areSet[0] = false;
/*     */       }
/*     */ 
/* 118 */       if ((this.storedBytes[0] & 0xE0) == 192)
/*     */       {
/* 120 */         if ((!paramByteBuffer.hasRemaining()) || ((checkAndGet(1, paramByteBuffer) & 0xC0) != 128))
/*     */         {
/* 122 */           return CoderResult.unmappableForLength(paramByteBuffer.position());
/*     */         }
/* 124 */         if (!paramCharBuffer.hasRemaining())
/*     */         {
/* 126 */           return CoderResult.OVERFLOW;
/*     */         }
/* 128 */         paramCharBuffer.put((char)((this.storedBytes[0] & 0x1F) << 6 | this.storedBytes[1] & 0x3F));
/* 129 */         this.areSet[0] = false;
/* 130 */         this.areSet[1] = false;
/*     */       }
/*     */ 
/* 135 */       if ((this.storedBytes[0] & 0xF0) == 224)
/*     */       {
/* 137 */         if ((paramByteBuffer.remaining() < 2) || ((checkAndGet(1, paramByteBuffer) & 0xC0) != 128) || ((checkAndGet(2, paramByteBuffer) & 0xC0) != 128))
/*     */         {
/* 140 */           return CoderResult.unmappableForLength(paramByteBuffer.position());
/*     */         }
/* 142 */         if (!paramCharBuffer.hasRemaining())
/*     */         {
/* 144 */           return CoderResult.OVERFLOW;
/*     */         }
/* 146 */         int i = (this.storedBytes[0] & 0xF) << 12 | (this.storedBytes[1] & 0x3F) << 6 | this.storedBytes[2] & 0x3F;
/*     */ 
/* 149 */         paramCharBuffer.put((char)i);
/* 150 */         this.areSet[0] = false;
/* 151 */         this.areSet[1] = false;
/* 152 */         this.areSet[2] = false;
/*     */       }
/*     */ 
/* 157 */       if ((this.storedBytes[0] & 0xF8) != 240)
/*     */         break;
/* 159 */       if ((paramByteBuffer.remaining() < 3) || ((checkAndGet(1, paramByteBuffer) & 0xC0) != 128) || ((checkAndGet(2, paramByteBuffer) & 0xC0) != 128) || ((checkAndGet(3, paramByteBuffer) & 0xC0) != 128))
/*     */       {
/* 163 */         return CoderResult.unmappableForLength(paramByteBuffer.position());
/*     */       }
/*     */ 
/* 166 */       int j = (this.storedBytes[0] & 0x7) << 18 | (this.storedBytes[1] & 0x3F) << 12 | (this.storedBytes[2] & 0x3F) << 6 | this.storedBytes[3] & 0x3F;
/*     */ 
/* 173 */       if (j > 1114111)
/*     */       {
/* 175 */         return CoderResult.unmappableForLength(paramByteBuffer.position());
/*     */       }
/*     */ 
/* 183 */       if (j <= 65535)
/*     */       {
/* 185 */         if (!paramCharBuffer.hasRemaining())
/*     */         {
/* 187 */           return CoderResult.OVERFLOW;
/*     */         }
/* 189 */         paramCharBuffer.put((char)j);
/* 190 */         this.areSet[0] = false;
/* 191 */         this.areSet[1] = false;
/* 192 */         this.areSet[2] = false;
/* 193 */         this.areSet[3] = false;
/*     */       }
/*     */ 
/* 201 */       if (paramCharBuffer.remaining() < 2)
/*     */       {
/* 203 */         return CoderResult.OVERFLOW;
/*     */       }
/* 205 */       paramCharBuffer.put((char)(0xD800 | j - 65536 >> 10 & 0x3FF));
/* 206 */       paramCharBuffer.put((char)(0xDC00 | j - 65536 & 0x3FF));
/* 207 */       this.areSet[0] = false;
/* 208 */       this.areSet[1] = false;
/* 209 */       this.areSet[2] = false;
/* 210 */       this.areSet[3] = false;
/*     */     }
/*     */ 
/* 218 */     return CoderResult.unmappableForLength(paramByteBuffer.position());
/*     */ 
/* 222 */     label690: return CoderResult.UNDERFLOW;
/*     */   }
/*     */ 
/*     */   private int checkAndGet(int paramInt, ByteBuffer paramByteBuffer)
/*     */   {
/* 227 */     if (this.areSet[paramInt] != 0)
/*     */     {
/* 229 */       return this.storedBytes[paramInt];
/*     */     }
/*     */ 
/* 232 */     this.storedBytes[paramInt] = paramByteBuffer.get();
/* 233 */     this.areSet[paramInt] = true;
/* 234 */     return this.storedBytes[paramInt];
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.charset.SybUTF8CharsetDecoder
 * JD-Core Version:    0.5.4
 */