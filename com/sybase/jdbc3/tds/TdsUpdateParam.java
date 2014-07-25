/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.Convert;
/*     */ import com.sybase.jdbc3.jdbc.DateObject;
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.JdbcDataObject;
/*     */ import com.sybase.jdbc3.jdbc.TextPointer;
/*     */ import com.sybase.jdbc3.utils.CacheManager;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Calendar;
/*     */ 
/*     */ public class TdsUpdateParam extends TdsParam
/*     */   implements JdbcDataObject
/*     */ {
/*     */   public TdsUpdateParam(TdsDataOutputStream paramTdsDataOutputStream, CacheManager paramCacheManager)
/*     */   {
/*  61 */     super(paramTdsDataOutputStream);
/*     */   }
/*     */ 
/*     */   public boolean isNull()
/*     */     throws SQLException
/*     */   {
/*  70 */     if ((this._sqlType == -999) || (this._sqlType == -998))
/*     */     {
/*  72 */       ErrorMessage.raiseError("JZ0TC");
/*     */     }
/*     */ 
/*  75 */     return this._sqlType == 0;
/*     */   }
/*     */ 
/*     */   public InputStream getAsciiStream()
/*     */     throws SQLException
/*     */   {
/*  82 */     checkStream(false);
/*  83 */     return Convert.objectToStream(this._inValue);
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream()
/*     */     throws SQLException
/*     */   {
/*  89 */     checkStream(false);
/*  90 */     return Convert.objectToReader(this._inValue);
/*     */   }
/*     */ 
/*     */   public InputStream getUnicodeStream()
/*     */     throws SQLException
/*     */   {
/*  96 */     checkStream(false);
/*  97 */     return Convert.objectToStream(this._inValue);
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryStream()
/*     */     throws SQLException
/*     */   {
/* 103 */     checkStream(false);
/* 104 */     return Convert.objectToStream(this._inValue);
/*     */   }
/*     */ 
/*     */   public byte[] getBytes()
/*     */     throws SQLException
/*     */   {
/* 110 */     checkStream(false);
/* 111 */     return Convert.objectToBytes(this._inValue);
/*     */   }
/*     */ 
/*     */   public DateObject getDateObject(int paramInt, Calendar paramCalendar)
/*     */     throws SQLException
/*     */   {
/* 118 */     checkStream(false);
/* 119 */     return Convert.objectToDateObject(this._inValue, paramCalendar);
/*     */   }
/*     */ 
/*     */   public TextPointer getTextPtr() throws SQLException
/*     */   {
/* 124 */     ErrorMessage.raiseError("JZ0R4");
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */   public BigDecimal getBigDecimal(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 131 */     checkStream(false);
/* 132 */     BigDecimal localBigDecimal = Convert.objectToBigDecimal(this._inValue);
/* 133 */     return Convert.setScale(localBigDecimal, paramInt);
/*     */   }
/*     */ 
/*     */   public boolean getBoolean()
/*     */     throws SQLException
/*     */   {
/* 141 */     checkStream(true);
/* 142 */     return Convert.objectToBoolean(this._inValue).booleanValue();
/*     */   }
/*     */ 
/*     */   public byte getByte()
/*     */     throws SQLException
/*     */   {
/* 151 */     checkStream(true);
/* 152 */     long l = Convert.objectToLongValue(this._inValue);
/* 153 */     Convert.checkByteOflo(l);
/* 154 */     return (byte)(int)l;
/*     */   }
/*     */ 
/*     */   public double getDouble()
/*     */     throws SQLException
/*     */   {
/* 162 */     checkStream(true);
/* 163 */     return Convert.objectToDoubleValue(this._inValue);
/*     */   }
/*     */ 
/*     */   public float getFloat()
/*     */     throws SQLException
/*     */   {
/* 171 */     checkStream(true);
/* 172 */     return (float)Convert.objectToDoubleValue(this._inValue);
/*     */   }
/*     */ 
/*     */   public int getInt()
/*     */     throws SQLException
/*     */   {
/* 180 */     checkStream(true);
/* 181 */     long l = Convert.objectToLongValue(this._inValue);
/* 182 */     Convert.checkIntOflo(l);
/* 183 */     return (int)l;
/*     */   }
/*     */ 
/*     */   public long getLong()
/*     */     throws SQLException
/*     */   {
/* 191 */     checkStream(true);
/* 192 */     return Convert.objectToLongValue(this._inValue);
/*     */   }
/*     */ 
/*     */   public Object getObject()
/*     */     throws SQLException
/*     */   {
/* 200 */     checkStream(false);
/* 201 */     return this._inValue;
/*     */   }
/*     */ 
/*     */   public short getShort()
/*     */     throws SQLException
/*     */   {
/* 209 */     checkStream(true);
/* 210 */     long l = Convert.objectToLongValue(this._inValue);
/* 211 */     Convert.checkShortOflo(l);
/* 212 */     return (short)(int)l;
/*     */   }
/*     */ 
/*     */   public String getString()
/*     */     throws SQLException
/*     */   {
/* 220 */     checkStream(false);
/* 221 */     return Convert.objectToString(this._inValue);
/*     */   }
/*     */ 
/*     */   public Blob getBlob()
/*     */     throws SQLException
/*     */   {
/* 229 */     checkStream(false);
/* 230 */     return (Blob)Convert.objectToLob(this._inValue, 0);
/*     */   }
/*     */ 
/*     */   public Clob getClob()
/*     */     throws SQLException
/*     */   {
/* 238 */     checkStream(false);
/* 239 */     return (Clob)Convert.objectToLob(this._inValue, 1);
/*     */   }
/*     */ 
/*     */   public Clob getInitializedClob()
/*     */   {
/* 254 */     return null;
/*     */   }
/*     */ 
/*     */   public Blob getInitializedBlob()
/*     */   {
/* 259 */     return null;
/*     */   }
/*     */ 
/*     */   private void checkStream(boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/* 268 */     if (this._inValue == null)
/*     */     {
/* 270 */       if (!paramBoolean)
/*     */         return;
/* 272 */       ErrorMessage.raiseError("JZ0TC");
/*     */     }
/*     */     else
/*     */     {
/* 276 */       if (!this._inValue instanceof InputStream)
/*     */         return;
/* 278 */       ErrorMessage.raiseError("JZ0IS");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsUpdateParam
 * JD-Core Version:    0.5.4
 */