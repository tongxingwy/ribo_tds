/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.Convert;
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class TdsReal extends TdsDataObject
/*     */ {
/*     */   double _value;
/*     */   byte[] _valueAsBytes;
/*     */ 
/*     */   public TdsReal(TdsProtocolContext paramTdsProtocolContext)
/*     */     throws IOException
/*     */   {
/*  50 */     super(paramTdsProtocolContext);
/*     */   }
/*     */ 
/*     */   public TdsDataObject createCachedCopy()
/*     */     throws IOException, SQLException
/*     */   {
/*  65 */     return new CachedTdsReal(this);
/*     */   }
/*     */ 
/*     */   public BigDecimal getBigDecimal(int paramInt)
/*     */     throws SQLException
/*     */   {
/*  71 */     doRead();
/*  72 */     BigDecimal localBigDecimal = null;
/*  73 */     if (!this._isNull) localBigDecimal = new BigDecimal(this._value);
/*  74 */     localBigDecimal = Convert.setScale(localBigDecimal, paramInt);
/*  75 */     return localBigDecimal;
/*     */   }
/*     */ 
/*     */   public boolean getBoolean() throws SQLException {
/*  79 */     doRead();
/*  80 */     return 0.0D != this._value;
/*     */   }
/*     */ 
/*     */   public byte getByte() throws SQLException {
/*  84 */     doRead();
/*  85 */     Convert.checkByteOflo(new Double(this._value).longValue());
/*  86 */     return (byte)(int)this._value;
/*     */   }
/*     */ 
/*     */   public byte[] getBytes() throws SQLException {
/*  90 */     doReadAsBytes();
/*  91 */     return this._valueAsBytes;
/*     */   }
/*     */ 
/*     */   public double getDouble() throws SQLException {
/*  95 */     doRead();
/*  96 */     return this._value;
/*     */   }
/*     */ 
/*     */   public float getFloat() throws SQLException {
/* 100 */     doRead();
/*     */ 
/* 105 */     return (float)this._value;
/*     */   }
/*     */ 
/*     */   public int getInt() throws SQLException {
/* 109 */     doRead();
/* 110 */     Convert.checkIntOflo(new Double(this._value).longValue());
/* 111 */     return (int)this._value;
/*     */   }
/*     */ 
/*     */   public long getLong() throws SQLException {
/* 115 */     doRead();
/* 116 */     Convert.checkLongOflo(this._value);
/* 117 */     return ()this._value;
/*     */   }
/*     */ 
/*     */   public short getShort() throws SQLException {
/* 121 */     doRead();
/* 122 */     Convert.checkShortOflo(new Double(this._value).longValue());
/* 123 */     return (short)(int)this._value;
/*     */   }
/*     */ 
/*     */   public Object getObject() throws SQLException {
/* 127 */     doRead();
/* 128 */     Object localObject = null;
/* 129 */     if (!this._isNull)
/*     */     {
/* 131 */       switch (this._dataLength)
/*     */       {
/*     */       case 4:
/* 134 */         localObject = new Float(this._value);
/* 135 */         break;
/*     */       case 8:
/* 137 */         localObject = new Double(this._value);
/*     */       }
/*     */     }
/*     */ 
/* 141 */     return localObject;
/*     */   }
/*     */ 
/*     */   public String getString() throws SQLException {
/* 145 */     doRead();
/* 146 */     String str = null;
/* 147 */     if (!this._isNull)
/*     */     {
/* 149 */       str = String.valueOf(this._value);
/*     */     }
/* 151 */     return str;
/*     */   }
/*     */ 
/*     */   public void cache()
/*     */     throws IOException
/*     */   {
/* 158 */     beginRead();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */     throws IOException
/*     */   {
/* 165 */     if (this._state == 0)
/*     */     {
/* 167 */       getSize();
/* 168 */       skip(this._dataLength);
/*     */     }
/*     */ 
/* 171 */     initialize();
/*     */   }
/*     */ 
/*     */   protected void beginReadAsBytes()
/*     */     throws IOException
/*     */   {
/* 177 */     startRead();
/* 178 */     switch (this._state)
/*     */     {
/*     */     case 1:
/* 181 */       if (this._isNull)
/*     */       {
/* 183 */         this._value = 0.0D;
/*     */       }
/*     */       else
/*     */       {
/* 187 */         switch (this._dataFmt._datatype)
/*     */         {
/*     */         case 59:
/* 190 */           this._valueAsBytes = readBytesForAllTypes(4);
/* 191 */           break;
/*     */         case 62:
/* 193 */           this._valueAsBytes = readBytesForAllTypes(8);
/* 194 */           break;
/*     */         case 109:
/* 196 */           this._valueAsBytes = readFLTNAsBytes();
/* 197 */           break;
/*     */         default:
/* 199 */           ErrorMessage.raiseIOException("JZ0TC");
/*     */         }
/*     */       }
/* 202 */       this._state = 4;
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void beginRead()
/*     */     throws IOException
/*     */   {
/* 212 */     startRead();
/* 213 */     switch (this._state)
/*     */     {
/*     */     case 1:
/* 216 */       if (this._isNull)
/*     */       {
/* 218 */         this._value = 0.0D;
/*     */       }
/*     */       else
/*     */       {
/* 222 */         switch (this._dataFmt._datatype)
/*     */         {
/*     */         case 59:
/* 225 */           this._value = readFloat();
/* 226 */           break;
/*     */         case 62:
/* 228 */           this._value = readDouble();
/* 229 */           break;
/*     */         case 109:
/* 231 */           this._value = readFLTN();
/* 232 */           break;
/*     */         default:
/* 234 */           ErrorMessage.raiseIOException("JZ0TC");
/*     */         }
/*     */       }
/* 237 */       this._state = 3;
/* 238 */       break;
/*     */     case 4:
/* 240 */       if (this._isNull)
/*     */       {
/* 242 */         this._value = 0.0D;
/*     */       }
/*     */       else
/*     */       {
/*     */         try
/*     */         {
/* 248 */           switch (this._dataFmt._datatype)
/*     */           {
/*     */           case 59:
/* 251 */             this._value = Float.intBitsToFloat((int)Convert.bufToLong(this._valueAsBytes));
/* 252 */             break;
/*     */           case 62:
/* 254 */             this._value = Double.longBitsToDouble(Convert.bufToLong(this._valueAsBytes));
/* 255 */             break;
/*     */           case 109:
/* 257 */             if (this._valueAsBytes.length == 0)
/*     */             {
/* 259 */               this._value = 0.0D;
/*     */             }
/*     */             else
/*     */             {
/* 263 */               this._value = Convert.bufToDouble(this._valueAsBytes);
/*     */             }
/* 265 */             break;
/*     */           default:
/* 267 */             ErrorMessage.raiseIOException("JZ0TC");
/*     */           }
/*     */         }
/*     */         catch (SQLException localSQLException)
/*     */         {
/* 272 */           ErrorMessage.raiseIOException("JZ0TC", localSQLException.getMessage(), localSQLException.getCause());
/*     */         }
/*     */       }
/* 275 */       this._state = 3;
/*     */     case 2:
/*     */     case 3:
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsReal
 * JD-Core Version:    0.5.4
 */