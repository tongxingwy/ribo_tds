/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class Slurp extends Token
/*     */ {
/*     */   public Slurp(TdsInputStream paramTdsInputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  50 */       int i = getLength(paramTdsInputStream, paramInt);
/*  51 */       paramTdsInputStream.skipBytes(i);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*  55 */       readSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Slurp()
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getLength(TdsInputStream paramTdsInputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/*  72 */     int i = 0;
/*     */     try
/*     */     {
/*  76 */       if ((paramInt & 0xE0) == 192)
/*     */       {
/*  79 */         i = 0;
/*     */       }
/*  83 */       else if ((paramInt & 0x30) == 48)
/*     */       {
/*  86 */         switch (paramInt & 0xC)
/*     */         {
/*     */         case 0:
/*  89 */           i = 1; break;
/*     */         case 4:
/*  91 */           i = 2; break;
/*     */         case 8:
/*  93 */           i = 4; break;
/*     */         case 12:
/*  95 */           i = 8;
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 103 */         switch (paramInt & 0xFC)
/*     */         {
/*     */         case 36:
/*     */         case 40:
/*     */         case 100:
/*     */         case 104:
/* 111 */           i = paramTdsInputStream.readUnsignedByte();
/*     */ 
/* 114 */           break;
/*     */         case 32:
/*     */         case 96:
/* 119 */           i = paramTdsInputStream.readInt();
/*     */ 
/* 123 */           break;
/*     */         default:
/* 127 */           i = paramTdsInputStream.readUnsignedShort();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 136 */       readSQE(localIOException);
/*     */     }
/* 138 */     return i;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.Slurp
 * JD-Core Version:    0.5.4
 */