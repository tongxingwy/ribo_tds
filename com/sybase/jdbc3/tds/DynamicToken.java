/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class DynamicToken extends Token
/*     */ {
/*     */   public static final int PREPARE = 1;
/*     */   public static final int EXEC = 2;
/*     */   public static final int DEALLOC = 4;
/*     */   public static final int EXEC_IMMED = 8;
/*     */   public static final int PROCNAME = 16;
/*     */   public static final int ACK = 32;
/*     */   public static final int DESCIN = 64;
/*     */   public static final int DESCOUT = 128;
/*     */   public static final int MAX_DYNAMIC_LENGTH = 32767;
/*     */   protected long _totalLen;
/*     */   protected long _totalOutLen;
/*     */   protected int _type;
/*     */   protected int _status;
/*     */   protected String _name;
/*     */   protected int _nameLen;
/*     */   protected String _body;
/*     */   protected long _bodyLen;
/*     */ 
/*     */   public DynamicToken(int paramInt, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
/*     */     throws SQLException
/*     */   {
/*  91 */     this._type = paramInt;
/*  92 */     this._name = paramString1;
/*  93 */     this._body = paramString2;
/*  94 */     this._status = 0;
/*  95 */     if (paramBoolean1)
/*     */     {
/*  97 */       this._status |= 1;
/*  98 */       if (paramBoolean2)
/*     */       {
/* 100 */         this._status |= 4;
/*     */       }
/* 102 */       if (paramBoolean4)
/*     */       {
/* 104 */         this._status |= 8;
/*     */       }
/*     */     }
/* 107 */     if (!paramBoolean3)
/*     */       return;
/* 109 */     this._status |= 2;
/*     */   }
/*     */ 
/*     */   public DynamicToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 124 */       this._totalLen = readLength(paramTdsInputStream);
/* 125 */       this._type = paramTdsInputStream.readUnsignedByte();
/* 126 */       this._status = paramTdsInputStream.readUnsignedByte();
/* 127 */       this._nameLen = paramTdsInputStream.readUnsignedByte();
/* 128 */       this._name = paramTdsInputStream.readString(this._nameLen);
/*     */ 
/* 132 */       long l = this._totalLen - (3 + this._nameLen);
/* 133 */       if (l < 2L)
/*     */       {
/* 137 */         this._body = null;
/* 138 */         this._bodyLen = 0L;
/*     */       }
/*     */       else
/*     */       {
/* 142 */         readBodyLength(paramTdsInputStream);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 148 */       readSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected DynamicToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected long readLength(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/* 165 */     long l = paramTdsInputStream.readShort();
/* 166 */     return l;
/*     */   }
/*     */ 
/*     */   protected void readBodyLength(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/* 175 */     this._bodyLen = paramTdsInputStream.readShort();
/* 176 */     this._body = paramTdsInputStream.readString((int)this._bodyLen);
/*     */   }
/*     */ 
/*     */   public void send(TdsDataOutputStream paramTdsDataOutputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 199 */       Tds localTds = paramTdsDataOutputStream._tds;
/* 200 */       sendTokenName(paramTdsDataOutputStream);
/* 201 */       byte[] arrayOfByte1 = paramTdsDataOutputStream.stringToByte(this._name);
/* 202 */       byte[] arrayOfByte2 = paramTdsDataOutputStream.stringToByte(this._body);
/* 203 */       this._totalOutLen = (3 + arrayOfByte1.length);
/* 204 */       if (this._type != 32)
/*     */       {
/* 206 */         this._totalOutLen += getStatementLengthFieldSize();
/* 207 */         if (arrayOfByte2 != null)
/*     */         {
/* 209 */           this._totalOutLen += arrayOfByte2.length;
/*     */         }
/*     */       }
/* 212 */       sendTotalLength(paramTdsDataOutputStream);
/* 213 */       paramTdsDataOutputStream.writeByte(this._type);
/* 214 */       paramTdsDataOutputStream.writeByte(this._status);
/* 215 */       paramTdsDataOutputStream.writeByte(arrayOfByte1.length);
/* 216 */       paramTdsDataOutputStream.write(arrayOfByte1);
/* 217 */       if (this._type != 32)
/*     */       {
/* 219 */         if (arrayOfByte2 == null)
/*     */         {
/* 221 */           sendBodyLength(0L, paramTdsDataOutputStream);
/*     */         }
/*     */         else
/*     */         {
/* 225 */           sendBodyLength(arrayOfByte2.length, paramTdsDataOutputStream);
/* 226 */           paramTdsDataOutputStream.write(arrayOfByte2);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 232 */       writeSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void sendTokenName(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 242 */     paramTdsOutputStream.writeByte(231);
/*     */   }
/*     */ 
/*     */   protected void sendTotalLength(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 250 */     paramTdsOutputStream.writeShort((int)this._totalOutLen);
/*     */   }
/*     */ 
/*     */   protected void sendBodyLength(long paramLong, TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 259 */     paramTdsOutputStream.writeShort((int)paramLong);
/*     */   }
/*     */ 
/*     */   protected int getStatementLengthFieldSize()
/*     */   {
/* 264 */     return 2;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     String str;
/* 277 */     switch (this._type)
/*     */     {
/*     */     case 1:
/* 280 */       str = "PREPARE";
/* 281 */       break;
/*     */     case 2:
/* 283 */       str = "EXEC";
/* 284 */       break;
/*     */     case 4:
/* 286 */       str = "DEALLOC";
/* 287 */       break;
/*     */     case 32:
/* 289 */       str = "ACK";
/* 290 */       break;
/*     */     default:
/* 292 */       str = "Unknown!";
/*     */     }
/*     */ 
/* 295 */     return "DYNAMIC: type(" + this._type + ")=" + str + " name: >" + this._name + "< body: >" + this._body + (((this._status & 0x1) != 0) ? "< (Params)" : "< (No params)");
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 303 */     return this._name;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.DynamicToken
 * JD-Core Version:    0.5.4
 */