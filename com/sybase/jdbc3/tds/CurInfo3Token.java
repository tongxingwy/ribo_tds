/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class CurInfo3Token extends CurInfoToken
/*     */ {
/*     */   protected CurInfo3Token()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CurInfo3Token(TdsProtocolContext paramTdsProtocolContext)
/*     */     throws IOException
/*     */   {
/*  46 */     super(paramTdsProtocolContext);
/*     */   }
/*     */ 
/*     */   public CurInfo3Token(TdsCursor paramTdsCursor) throws SQLException
/*     */   {
/*  51 */     super(paramTdsCursor);
/*     */   }
/*     */ 
/*     */   protected int readStatus(TdsDataInputStream paramTdsDataInputStream) throws IOException
/*     */   {
/*  56 */     return paramTdsDataInputStream.readInt();
/*     */   }
/*     */ 
/*     */   protected void sendStatus(TdsDataOutputStream paramTdsDataOutputStream) throws IOException
/*     */   {
/*  61 */     paramTdsDataOutputStream.writeInt(this._status);
/*     */   }
/*     */ 
/*     */   protected int getStatusLength()
/*     */   {
/*  66 */     return 4;
/*     */   }
/*     */ 
/*     */   protected String getTokenName()
/*     */   {
/*  71 */     return "CurInfo3Token";
/*     */   }
/*     */ 
/*     */   protected int getTokenID()
/*     */   {
/*  76 */     return 136;
/*     */   }
/*     */ 
/*     */   protected void getMetaInformation(TdsDataInputStream paramTdsDataInputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/*  83 */     int i = paramTdsDataInputStream.readInt();
/*     */ 
/*  86 */     this._cursor.setRowNum(i);
/*  87 */     paramInt -= 4;
/*     */ 
/*  93 */     int j = paramTdsDataInputStream.readInt();
/*     */ 
/*  96 */     this._cursor.setTotalRowCount(j);
/*  97 */     paramInt -= 4;
/*     */ 
/* 100 */     if ((this._status & 0x20) == 0) {
/*     */       return;
/*     */     }
/*     */ 
/* 104 */     int k = paramTdsDataInputStream.readInt();
/*     */ 
/* 107 */     paramInt -= 4;
/*     */   }
/*     */ 
/*     */   protected void sendMetaInformation(TdsDataOutputStream paramTdsDataOutputStream)
/*     */     throws IOException
/*     */   {
/* 117 */     ErrorMessage.raiseIOException("JZ0P4");
/*     */   }
/*     */ 
/*     */   protected int getLength()
/*     */   {
/* 122 */     int i = 13 + getStatusLength();
/*     */ 
/* 124 */     if (this._command == 1)
/*     */     {
/* 126 */       i += 4;
/*     */     }
/* 128 */     return i;
/*     */   }
/*     */ 
/*     */   protected void setCursorType()
/*     */   {
/*     */     try
/*     */     {
/* 135 */       if ((this._status & 0x8) != 0)
/*     */       {
/* 137 */         this._cursor.setType(1);
/*     */       }
/* 139 */       if ((this._status & 0x10) != 0)
/*     */       {
/* 141 */         this._cursor.setType(2);
/*     */       }
/* 143 */       if ((this._status & 0x80) != 0)
/*     */       {
/* 145 */         this._cursor.setType(256);
/*     */       }
/* 147 */       if ((this._status & 0x400) != 0)
/*     */       {
/* 149 */         this._cursor.setType(32);
/*     */       }
/* 151 */       if ((this._status & 0x1000) != 0)
/*     */       {
/* 153 */         this._cursor.setType(128);
/*     */       }
/* 155 */       if ((this._status & 0x800) != 0)
/*     */       {
/* 157 */         this._cursor.setType(64);
/*     */       }
/* 159 */       if ((this._status & 0x200) != 0)
/*     */       {
/* 161 */         this._cursor.setType(4);
/*     */       }
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CurInfo3Token
 * JD-Core Version:    0.5.4
 */