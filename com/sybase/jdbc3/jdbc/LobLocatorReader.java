/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class LobLocatorReader extends LobReader
/*     */ {
/*     */   public LobLocatorReader(SybCharLob paramSybCharLob)
/*     */   {
/*  27 */     this(paramSybCharLob, 1L, 9223372036854775807L);
/*     */   }
/*     */ 
/*     */   public LobLocatorReader(SybCharLob paramSybCharLob, long paramLong1, long paramLong2)
/*     */   {
/*  32 */     super(paramSybCharLob, paramLong1, paramLong2);
/*     */   }
/*     */ 
/*     */   public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException
/*     */   {
/*  37 */     checkIfClosed();
/*  38 */     if (paramArrayOfChar == null)
/*     */     {
/*  40 */       throw new NullPointerException();
/*     */     }
/*  42 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfChar.length - paramInt1))
/*     */     {
/*  44 */       throw new IndexOutOfBoundsException();
/*     */     }
/*  46 */     if (paramInt2 == 0)
/*     */     {
/*  48 */       return 0;
/*     */     }
/*     */ 
/*  51 */     if (this._nextReadPos > this._readLimit)
/*     */     {
/*  53 */       return -1;
/*     */     }
/*     */ 
/*  56 */     if (this._nextReadPos + paramInt2 > this._readLimit)
/*     */     {
/*  58 */       paramInt2 = (int)(this._readLimit - this._nextReadPos + 1L);
/*     */     }
/*  60 */     int i = 0;
/*     */     try
/*     */     {
/*  64 */       MdaManager localMdaManager = this._lob._context._conn.getMDA(this._lob._context);
/*  65 */       PreparedStatement localPreparedStatement = localMdaManager.getMetaDataAccessor("LOB_GETLOB", this._lob._context);
/*     */ 
/*  67 */       localPreparedStatement.setInt(1, this._lob._lobType);
/*  68 */       localPreparedStatement.setBytes(2, this._lob.getLocator());
/*  69 */       localPreparedStatement.setLong(3, this._nextReadPos);
/*  70 */       localPreparedStatement.setInt(4, paramInt2);
/*  71 */       ResultSet localResultSet = localPreparedStatement.executeQuery();
/*     */ 
/*  73 */       String str = null;
/*  74 */       char[] arrayOfChar = null;
/*  75 */       if (localResultSet.next())
/*     */       {
/*  77 */         str = localResultSet.getString(1);
/*     */       }
/*     */ 
/*  80 */       if (str != null)
/*     */       {
/*  82 */         arrayOfChar = str.toCharArray();
/*  83 */         if (arrayOfChar.length < paramInt2)
/*     */         {
/*  85 */           i = arrayOfChar.length;
/*     */         }
/*     */         else
/*     */         {
/*  89 */           i = paramInt2;
/*     */         }
/*  91 */         System.arraycopy(arrayOfChar, 0, paramArrayOfChar, paramInt1, i);
/*     */       }
/*     */       else
/*     */       {
/*  95 */         return -1;
/*     */       }
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 100 */       ErrorMessage.raiseIOException("JZ041", "Read", "Reader", localSQLException);
/*     */     }
/*     */ 
/* 104 */     this._nextReadPos += i;
/* 105 */     return i;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.LobLocatorReader
 * JD-Core Version:    0.5.4
 */