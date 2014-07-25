/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class LobClientInputStream extends LobInputStream
/*     */ {
/*     */   public LobClientInputStream(SybLob paramSybLob)
/*     */   {
/*  25 */     this(paramSybLob, 1L, 9223372036854775807L);
/*     */   }
/*     */ 
/*     */   public LobClientInputStream(SybLob paramSybLob, long paramLong1, long paramLong2)
/*     */   {
/*  30 */     super(paramSybLob, paramLong1, paramLong2);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */   {
/*  35 */     checkIfClosed();
/*  36 */     if (paramArrayOfByte == null)
/*     */     {
/*  38 */       throw new NullPointerException();
/*     */     }
/*  40 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1))
/*     */     {
/*  42 */       throw new IndexOutOfBoundsException();
/*     */     }
/*  44 */     if (paramInt2 == 0)
/*     */     {
/*  46 */       return 0;
/*     */     }
/*     */ 
/*  49 */     if (this._nextReadPos > this._readLimit)
/*     */     {
/*  51 */       return -1;
/*     */     }
/*     */ 
/*  54 */     if (this._nextReadPos + paramInt2 > this._readLimit)
/*     */     {
/*  58 */       paramInt2 = (int)(this._readLimit - this._nextReadPos + 1L);
/*     */     }
/*  60 */     int i = 0;
/*     */ 
/*  62 */     byte[] arrayOfByte = null;
/*     */ 
/*  64 */     if (this._lob._lobType == 0)
/*     */     {
/*     */       try
/*     */       {
/*  68 */         arrayOfByte = ((SybBinaryClientLob)this._lob).getBytes(this._nextReadPos, paramInt2);
/*     */       }
/*     */       catch (SQLException localSQLException)
/*     */       {
/*  72 */         ErrorMessage.raiseIOException("JZ041", "Read", "InputStream", localSQLException);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*  78 */       StringBuffer localStringBuffer = ((SybCharClientLob)this._lob).getDataRef();
/*  79 */       arrayOfByte = localStringBuffer.toString().getBytes();
/*     */     }
/*     */ 
/*  83 */     if (arrayOfByte != null)
/*     */     {
/*  85 */       if (arrayOfByte.length < paramInt2)
/*     */       {
/*  87 */         i = arrayOfByte.length;
/*     */       }
/*     */       else
/*     */       {
/*  91 */         i = paramInt2;
/*     */       }
/*  93 */       System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt1, i);
/*     */     }
/*     */     else
/*     */     {
/*  97 */       return -1;
/*     */     }
/*     */ 
/* 100 */     this._nextReadPos += i;
/* 101 */     return i;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.LobClientInputStream
 * JD-Core Version:    0.5.4
 */