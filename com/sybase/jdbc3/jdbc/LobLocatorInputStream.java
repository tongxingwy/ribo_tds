/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class LobLocatorInputStream extends LobInputStream
/*     */ {
/*     */   public LobLocatorInputStream(SybLob paramSybLob)
/*     */   {
/*  28 */     this(paramSybLob, 1L, 9223372036854775807L);
/*     */   }
/*     */ 
/*     */   public LobLocatorInputStream(SybLob paramSybLob, long paramLong1, long paramLong2)
/*     */   {
/*  33 */     super(paramSybLob, paramLong1, paramLong2);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */   {
/*  38 */     checkIfClosed();
/*  39 */     if (paramArrayOfByte == null)
/*     */     {
/*  41 */       throw new NullPointerException();
/*     */     }
/*  43 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1))
/*     */     {
/*  45 */       throw new IndexOutOfBoundsException();
/*     */     }
/*  47 */     if (paramInt2 == 0)
/*     */     {
/*  49 */       return 0;
/*     */     }
/*     */ 
/*  52 */     if (this._nextReadPos > this._readLimit)
/*     */     {
/*  54 */       return -1;
/*     */     }
/*     */ 
/*  57 */     if (this._nextReadPos + paramInt2 > this._readLimit)
/*     */     {
/*  59 */       paramInt2 = (int)(this._readLimit - this._nextReadPos + 1L);
/*     */     }
/*  61 */     int i = 0;
/*     */     try
/*     */     {
/*  65 */       MdaManager localMdaManager = this._lob._context._conn.getMDA(this._lob._context);
/*  66 */       PreparedStatement localPreparedStatement = localMdaManager.getMetaDataAccessor("LOB_GETLOB", this._lob._context);
/*     */ 
/*  68 */       localPreparedStatement.setInt(1, this._lob._lobType);
/*  69 */       localPreparedStatement.setBytes(2, this._lob.getLocator());
/*  70 */       localPreparedStatement.setLong(3, this._nextReadPos);
/*  71 */       localPreparedStatement.setInt(4, paramInt2);
/*  72 */       ResultSet localResultSet = localPreparedStatement.executeQuery();
/*     */ 
/*  74 */       byte[] arrayOfByte = null;
/*  75 */       if (localResultSet.next())
/*     */       {
/*  77 */         if (this._lob._lobType == 0)
/*     */         {
/*  79 */           arrayOfByte = localResultSet.getBytes(1);
/*     */         }
/*     */         else
/*     */         {
/*  83 */           InputStream localInputStream = localResultSet.getAsciiStream(1);
/*  84 */           if (localInputStream != null)
/*     */           {
/*  86 */             arrayOfByte = new byte[localInputStream.available()];
/*  87 */             localInputStream.read(arrayOfByte);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*  92 */       if (arrayOfByte != null)
/*     */       {
/*  94 */         if (arrayOfByte.length < paramInt2)
/*     */         {
/*  96 */           i = arrayOfByte.length;
/*     */         }
/*     */         else
/*     */         {
/* 100 */           i = paramInt2;
/*     */         }
/* 102 */         System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt1, i);
/*     */       }
/*     */       else
/*     */       {
/* 106 */         return -1;
/*     */       }
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 111 */       ErrorMessage.raiseIOException("JZ041", "Read", "InputStream", localSQLException);
/*     */     }
/*     */ 
/* 115 */     this._nextReadPos += i;
/* 116 */     return i;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.LobLocatorInputStream
 * JD-Core Version:    0.5.4
 */