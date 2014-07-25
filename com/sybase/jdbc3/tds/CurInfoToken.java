/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class CurInfoToken extends Token
/*     */ {
/*     */   protected TdsCursor _cursor;
/*     */   protected int _status;
/*     */   protected int _command;
/*     */ 
/*     */   protected CurInfoToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CurInfoToken(TdsProtocolContext paramTdsProtocolContext)
/*     */     throws IOException
/*     */   {
/*  49 */     TdsDataInputStream localTdsDataInputStream = paramTdsProtocolContext._in;
/*  50 */     this._cursor = paramTdsProtocolContext._cursor;
/*     */ 
/*  52 */     int i = -1;
/*  53 */     String str = null;
/*  54 */     int j = 0;
/*  55 */     int k = 0;
/*  56 */     int l = 0;
/*  57 */     Integer localInteger = null;
/*     */     try
/*     */     {
/*  61 */       k = localTdsDataInputStream.readShort();
/*     */ 
/*  63 */       j = localTdsDataInputStream.readInt();
/*  64 */       k -= 4;
/*     */ 
/*  66 */       if (j == 0)
/*     */       {
/*  68 */         int i1 = localTdsDataInputStream.readUnsignedByte();
/*  69 */         if (i1 > 0)
/*     */         {
/*  71 */           str = localTdsDataInputStream.readString(i1);
/*     */         }
/*     */ 
/*  76 */         k -= 1 + i1;
/*     */       }
/*     */       else
/*     */       {
/*  91 */         localInteger = new Integer(j);
/*  92 */         this._cursor = ((TdsCursor)localTdsDataInputStream._tds._cursors.get(localInteger));
/*  93 */         if (this._cursor == null)
/*     */         {
/* 103 */           l = 1;
/*     */ 
/* 107 */           if (paramTdsProtocolContext._cursor._id == 0)
/*     */           {
/* 109 */             paramTdsProtocolContext._cursor._id = j;
/*     */           }
/* 111 */           this._cursor = paramTdsProtocolContext._cursor;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 119 */       if (k > 0)
/*     */       {
/* 121 */         this._command = localTdsDataInputStream.readUnsignedByte();
/* 122 */         --k;
/*     */       }
/* 124 */       if (k > 0)
/*     */       {
/* 126 */         this._status = readStatus(localTdsDataInputStream);
/* 127 */         k -= getStatusLength();
/*     */       }
/* 129 */       if (k > 0)
/*     */       {
/* 131 */         getMetaInformation(localTdsDataInputStream, k);
/*     */       }
/* 133 */       if (this._command == 3)
/*     */       {
/* 135 */         if ((this._status & 0x2) != 0)
/*     */         {
/* 137 */           this._cursor._state = 1;
/*     */ 
/* 140 */           if (l != 0)
/*     */           {
/* 146 */             localTdsDataInputStream._tds._cursors.put(localInteger, paramTdsProtocolContext._cursor);
/*     */           }
/*     */         }
/* 149 */         if ((this._status & 0x4) != 0)
/*     */         {
/* 151 */           this._cursor._state = 2;
/*     */         }
/* 153 */         if ((this._status & 0x40) != 0)
/*     */         {
/* 155 */           this._cursor._state = 3;
/*     */         }
/* 157 */         setCursorType();
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 162 */       readSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/* 170 */     return (this._status & 0x2) != 0;
/*     */   }
/*     */ 
/*     */   public CurInfoToken(TdsCursor paramTdsCursor)
/*     */     throws SQLException
/*     */   {
/* 179 */     this._cursor = paramTdsCursor;
/*     */   }
/*     */ 
/*     */   protected int readStatus(TdsDataInputStream paramTdsDataInputStream) throws IOException
/*     */   {
/* 184 */     return paramTdsDataInputStream.readShort();
/*     */   }
/*     */ 
/*     */   protected void sendStatus(TdsDataOutputStream paramTdsDataOutputStream) throws IOException
/*     */   {
/* 189 */     paramTdsDataOutputStream.writeShort(this._status);
/*     */   }
/*     */ 
/*     */   protected int getStatusLength()
/*     */   {
/* 194 */     return 2;
/*     */   }
/*     */ 
/*     */   protected String getTokenName()
/*     */   {
/* 199 */     return "CurInfoToken";
/*     */   }
/*     */ 
/*     */   protected int getTokenID()
/*     */   {
/* 204 */     return 131;
/*     */   }
/*     */ 
/*     */   protected void setCursorType()
/*     */   {
/*     */     try
/*     */     {
/* 211 */       if ((this._status & 0x8) != 0)
/*     */       {
/* 213 */         this._cursor.setType(1);
/*     */       }
/* 215 */       if ((this._status & 0x10) != 0)
/*     */       {
/* 217 */         this._cursor.setType(2);
/*     */       }
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void getMetaInformation(TdsDataInputStream paramTdsDataInputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/* 231 */     int i = paramTdsDataInputStream.readInt();
/*     */ 
/* 234 */     paramInt -= 4;
/*     */   }
/*     */ 
/*     */   protected void sendMetaInformation(TdsDataOutputStream paramTdsDataOutputStream)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   protected int getLength()
/*     */   {
/* 247 */     int i = 5 + getStatusLength();
/* 248 */     if (this._command == 1)
/*     */     {
/* 250 */       i += 4;
/*     */     }
/* 252 */     return i;
/*     */   }
/*     */ 
/*     */   protected void send(TdsDataOutputStream paramTdsDataOutputStream, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 259 */     this._command = paramInt1;
/*     */ 
/* 262 */     int i = getLength();
/*     */ 
/* 264 */     if (paramInt1 == 1)
/*     */     {
/* 266 */       this._status = 32;
/*     */     }
/*     */     else
/*     */     {
/* 270 */       this._status = 0;
/*     */     }
/*     */ 
/* 274 */     byte[] arrayOfByte = null;
/* 275 */     int j = 0;
/*     */ 
/* 277 */     if (this._cursor._id == 0)
/*     */     {
/* 279 */       arrayOfByte = paramTdsDataOutputStream.stringToByte(this._cursor.getName());
/* 280 */       j = arrayOfByte.length;
/* 281 */       i += 1 + j;
/*     */     }
/*     */     try
/*     */     {
/* 285 */       paramTdsDataOutputStream.writeByte(getTokenID());
/* 286 */       paramTdsDataOutputStream.writeShort(i);
/* 287 */       paramTdsDataOutputStream.writeInt(this._cursor._id);
/* 288 */       if (this._cursor._id == 0)
/*     */       {
/* 290 */         paramTdsDataOutputStream.writeByte(j);
/* 291 */         paramTdsDataOutputStream.write(arrayOfByte);
/*     */       }
/* 293 */       paramTdsDataOutputStream.writeByte(this._command);
/* 294 */       sendStatus(paramTdsDataOutputStream);
/* 295 */       if (paramInt1 == 1)
/*     */       {
/* 297 */         paramTdsDataOutputStream.writeInt(paramInt2);
/*     */       }
/* 299 */       sendMetaInformation(paramTdsDataOutputStream);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 303 */       writeSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 310 */     StringBuffer localStringBuffer = new StringBuffer(getTokenName() + ": ");
/* 311 */     localStringBuffer.append("name= " + this._cursor.getName());
/*     */ 
/* 313 */     localStringBuffer.append(", command= " + this._command);
/* 314 */     localStringBuffer.append(", status= " + this._status);
/* 315 */     return localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CurInfoToken
 * JD-Core Version:    0.5.4
 */