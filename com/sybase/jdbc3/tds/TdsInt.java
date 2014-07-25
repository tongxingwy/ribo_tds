/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.Convert;
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class TdsInt extends TdsDataObject
/*     */ {
/*     */   long _value;
/*     */   byte[] _valueAsBytes;
/*     */ 
/*     */   public TdsInt(TdsProtocolContext paramTdsProtocolContext)
/*     */     throws IOException
/*     */   {
/*  49 */     super(paramTdsProtocolContext);
/*     */   }
/*     */ 
/*     */   protected TdsDataObject createCachedCopy()
/*     */     throws IOException, SQLException
/*     */   {
/*  63 */     return new CachedTdsInt(this);
/*     */   }
/*     */ 
/*     */   public BigDecimal getBigDecimal(int paramInt)
/*     */     throws SQLException
/*     */   {
/*  69 */     doRead();
/*  70 */     BigDecimal localBigDecimal = null;
/*  71 */     if (!this._isNull) localBigDecimal = new BigDecimal(this._value);
/*  72 */     localBigDecimal = Convert.setScale(localBigDecimal, paramInt);
/*  73 */     return localBigDecimal;
/*     */   }
/*     */ 
/*     */   public boolean getBoolean() throws SQLException {
/*  77 */     doRead();
/*  78 */     return 0L != this._value;
/*     */   }
/*     */ 
/*     */   public byte getByte() throws SQLException {
/*  82 */     doRead();
/*  83 */     Convert.checkByteOflo(this._value);
/*  84 */     return (byte)(int)this._value;
/*     */   }
/*     */ 
/*     */   public byte[] getBytes() throws SQLException {
/*  88 */     doReadAsBytes();
/*  89 */     return this._valueAsBytes;
/*     */   }
/*     */ 
/*     */   public double getDouble() throws SQLException {
/*  93 */     doRead();
/*  94 */     return this._value;
/*     */   }
/*     */ 
/*     */   public float getFloat() throws SQLException {
/*  98 */     doRead();
/*  99 */     return (float)this._value;
/*     */   }
/*     */ 
/*     */   public int getInt() throws SQLException {
/* 103 */     doRead();
/* 104 */     Convert.checkIntOflo(this._value);
/* 105 */     return (int)this._value;
/*     */   }
/*     */ 
/*     */   public long getLong() throws SQLException {
/* 109 */     doRead();
/* 110 */     return this._value;
/*     */   }
/*     */ 
/*     */   public short getShort() throws SQLException {
/* 114 */     doRead();
/* 115 */     Convert.checkShortOflo(this._value);
/* 116 */     return (short)(int)this._value;
/*     */   }
/*     */ 
/*     */   public Object getObject() throws SQLException {
/* 120 */     doRead();
/* 121 */     Object localObject = null;
/* 122 */     if (!this._isNull)
/*     */     {
/* 127 */       if (this._dataLength == 8)
/*     */       {
/* 129 */         localObject = new Long(this._value);
/*     */       }
/*     */       else
/*     */       {
/* 133 */         localObject = new Integer((int)this._value);
/*     */       }
/*     */     }
/* 136 */     return localObject;
/*     */   }
/*     */ 
/*     */   public String getString() throws SQLException {
/* 140 */     doRead();
/* 141 */     String str = null;
/* 142 */     if (!this._isNull)
/*     */     {
/* 144 */       str = String.valueOf(this._value);
/*     */     }
/* 146 */     return str;
/*     */   }
/*     */ 
/*     */   public void cache()
/*     */     throws IOException
/*     */   {
/* 153 */     beginRead();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */     throws IOException
/*     */   {
/* 160 */     if (this._state == 0)
/*     */     {
/* 162 */       getSize();
/* 163 */       skip(this._dataLength);
/*     */     }
/*     */ 
/* 166 */     initialize();
/*     */   }
/*     */ 
/*     */   protected void beginReadAsBytes()
/*     */     throws IOException
/*     */   {
/* 172 */     startRead();
/* 173 */     switch (this._state)
/*     */     {
/*     */     case 1:
/* 176 */       if (this._isNull)
/*     */       {
/* 178 */         this._valueAsBytes = null;
/*     */       }
/*     */       else
/*     */       {
/* 182 */         switch (this._dataFmt._datatype)
/*     */         {
/*     */         case 48:
/* 185 */           this._valueAsBytes = readBytesForAllTypes(1);
/* 186 */           break;
/*     */         case 52:
/* 188 */           this._valueAsBytes = readBytesForAllTypes(2);
/* 189 */           break;
/*     */         case 65:
/* 191 */           this._valueAsBytes = readBytesForAllTypes(2);
/* 192 */           break;
/*     */         case 56:
/* 194 */           this._valueAsBytes = readBytesForAllTypes(4);
/* 195 */           break;
/*     */         case 66:
/* 197 */           this._valueAsBytes = readBytesForAllTypes(4);
/* 198 */           break;
/*     */         case 191:
/* 200 */           this._valueAsBytes = readBytesForAllTypes(8);
/* 201 */           break;
/*     */         case 68:
/* 203 */           this._valueAsBytes = readUINTNAsBytes();
/* 204 */           break;
/*     */         case 38:
/* 206 */           this._valueAsBytes = readINTNAsBytes();
/* 207 */           break;
/*     */         default:
/* 209 */           ErrorMessage.raiseIOException("JZ0TC");
/*     */         }
/*     */       }
/* 212 */       this._state = 4;
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void beginRead()
/*     */     throws IOException
/*     */   {
/* 222 */     startRead();
/* 223 */     switch (this._state)
/*     */     {
/*     */     case 1:
/* 226 */       if (this._isNull)
/*     */       {
/* 228 */         this._value = 0L;
/*     */       }
/*     */       else
/*     */       {
/* 232 */         switch (this._dataFmt._datatype)
/*     */         {
/*     */         case 48:
/* 235 */           this._value = readUnsignedByte();
/* 236 */           break;
/*     */         case 52:
/* 238 */           this._value = readShort();
/* 239 */           break;
/*     */         case 65:
/* 241 */           this._value = readUnsignedShortAsInt();
/* 242 */           break;
/*     */         case 56:
/* 244 */           this._value = readInt();
/* 245 */           break;
/*     */         case 66:
/* 247 */           this._value = readUnsignedIntAsLong();
/* 248 */           break;
/*     */         case 191:
/* 250 */           this._value = readLong();
/* 251 */           break;
/*     */         case 68:
/* 253 */           this._value = readUINTN();
/* 254 */           break;
/*     */         case 38:
/* 256 */           this._value = readINTN();
/* 257 */           break;
/*     */         default:
/* 259 */           ErrorMessage.raiseIOException("JZ0TC");
/*     */         }
/*     */       }
/* 262 */       this._state = 3;
/* 263 */       break;
/*     */     case 4:
/* 265 */       if (this._isNull)
/*     */       {
/* 267 */         this._value = 0L;
/*     */       }
/*     */       else
/*     */       {
/*     */         try
/*     */         {
/* 273 */           switch (this._dataFmt._datatype)
/*     */           {
/*     */           case 38:
/*     */           case 48:
/*     */           case 52:
/*     */           case 56:
/*     */           case 65:
/*     */           case 66:
/*     */           case 68:
/*     */           case 191:
/* 283 */             this._value = Convert.bufToLong(this._valueAsBytes);
/* 284 */             break;
/*     */           default:
/* 286 */             ErrorMessage.raiseIOException("JZ0TC");
/*     */           }
/*     */         }
/*     */         catch (SQLException localSQLException)
/*     */         {
/* 291 */           ErrorMessage.raiseIOException("JZ0TC", localSQLException.getMessage(), localSQLException.getCause());
/*     */         }
/*     */       }
/* 294 */       this._state = 3;
/*     */     case 2:
/*     */     case 3:
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsInt
 * JD-Core Version:    0.5.4
 */