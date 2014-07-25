/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.DateObject;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class SrvSQLTypeFormatter extends SrvTypeFormatter
/*     */ {
/*  67 */   ResultSet _rs = null;
/*     */ 
/*  70 */   static Hashtable _formatmap = new Hashtable();
/*     */ 
/*     */   public SrvSQLTypeFormatter(SrvFormatToken paramSrvFormatToken, SrvCapabilityToken paramSrvCapabilityToken)
/*     */   {
/* 185 */     super(paramSrvFormatToken, paramSrvCapabilityToken);
/*     */   }
/*     */ 
/*     */   public void addFormat(DataFormat paramDataFormat)
/*     */   {
/* 194 */     this._format.addFormat(paramDataFormat);
/*     */   }
/*     */ 
/*     */   public DataFormat[] buildDataFormat(ResultSet paramResultSet)
/*     */     throws IOException
/*     */   {
/* 206 */     SrvDataFormat[] arrayOfSrvDataFormat = null;
/*     */     try
/*     */     {
/* 210 */       ResultSetMetaData localResultSetMetaData = paramResultSet.getMetaData();
/* 211 */       int i = localResultSetMetaData.getColumnCount();
/* 212 */       if (i == 0)
/*     */       {
/* 214 */         throw new SrvTypeException("Cannot format result set with  0 columns");
/*     */       }
/*     */ 
/* 217 */       arrayOfSrvDataFormat = new SrvDataFormat[i];
/*     */ 
/* 220 */       for (int j = 1; j <= i; ++j)
/*     */       {
/* 223 */         Object localObject = _formatmap.get(new Integer(localResultSetMetaData.getColumnType(j)));
/*     */ 
/* 225 */         if (localObject == null)
/*     */         {
/* 227 */           throw new SrvTypeException("Cannot map JDBC type " + localResultSetMetaData.getColumnTypeName(j) + " to TDS type");
/*     */         }
/*     */ 
/* 234 */         int[] arrayOfInt = (int[])localObject;
/* 235 */         if (this._cap.handlesDataType(arrayOfInt[1]))
/*     */         {
/* 238 */           int k = 0;
/*     */ 
/* 244 */           if (localResultSetMetaData.isNullable(j) == 1)
/*     */           {
/* 247 */             k |= 32;
/*     */           }
/*     */ 
/* 250 */           if (localResultSetMetaData.isWritable(j))
/*     */           {
/* 252 */             k |= 16;
/*     */           }
/*     */ 
/* 255 */           arrayOfSrvDataFormat[(j - 1)] = new SrvDataFormat(localResultSetMetaData.getColumnName(j), arrayOfInt[1], k, arrayOfInt[2], localResultSetMetaData.getPrecision(j), localResultSetMetaData.getScale(j), null);
/*     */         }
/*     */         else
/*     */         {
/* 266 */           throw new SrvTypeException("Not handling client literal  conversion for JDBC types " + localResultSetMetaData.getColumnTypeName(j));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 277 */       throw new SrvTypeException("SQL to TDS conversion error: " + localSQLException);
/*     */     }
/* 279 */     return arrayOfSrvDataFormat;
/*     */   }
/*     */ 
/*     */   public void convertData(Token paramToken, ResultSet paramResultSet)
/*     */     throws IOException
/*     */   {
/* 290 */     this._rs = paramResultSet;
/* 291 */     setFormatter(paramToken, this);
/*     */   }
/*     */ 
/*     */   public void sendDataStream(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 307 */       if (this._rs.getMetaData().getColumnCount() != this._format.getFormatCount())
/*     */       {
/* 309 */         throw new SrvTypeException("Format column count(" + this._format.getFormatCount() + ") does not match row column count (" + this._rs.getMetaData().getColumnCount() + ")");
/*     */       }
/*     */ 
/* 317 */       for (int i = 1; i <= this._format.getFormatCount(); ++i)
/*     */       {
/* 319 */         DataFormat localDataFormat = this._format.formatAt(i - 1);
/*     */ 
/* 322 */         sendDataStream(paramTdsOutputStream, i, localDataFormat);
/*     */       }
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 327 */       throw new SrvTypeException("SQL error on type conversion: " + localSQLException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void sendDataStream(TdsOutputStream paramTdsOutputStream, int paramInt, DataFormat paramDataFormat)
/*     */     throws SQLException, IOException
/*     */   {
/*     */     Object localObject2;
/*     */     Object localObject3;
/* 347 */     switch (paramDataFormat._datatype)
/*     */     {
/*     */     case 50:
/* 350 */       if (this._rs.getBoolean(paramInt))
/*     */       {
/* 352 */         paramTdsOutputStream.writeByte(1); return;
/*     */       }
/*     */ 
/* 356 */       paramTdsOutputStream.writeByte(0);
/*     */ 
/* 358 */       break;
/*     */     case 39:
/*     */     case 47:
/* 361 */       byte[] arrayOfByte1 = paramTdsOutputStream.stringToByte(this._rs.getString(paramInt));
/* 362 */       if (arrayOfByte1 == null)
/*     */       {
/* 364 */         paramTdsOutputStream.writeByte(0); return;
/*     */       }
/*     */ 
/* 368 */       paramTdsOutputStream.writeByte(arrayOfByte1.length);
/* 369 */       paramTdsOutputStream.write(arrayOfByte1);
/*     */ 
/* 371 */       break;
/*     */     case 175:
/* 373 */       byte[] arrayOfByte2 = paramTdsOutputStream.stringToByte(this._rs.getString(paramInt));
/* 374 */       if (arrayOfByte2 == null)
/*     */       {
/* 376 */         paramTdsOutputStream.writeInt(0); return;
/*     */       }
/*     */ 
/* 380 */       paramTdsOutputStream.writeInt(arrayOfByte2.length);
/* 381 */       paramTdsOutputStream.write(arrayOfByte2);
/*     */ 
/* 383 */       break;
/*     */     case 225:
/* 385 */       byte[] arrayOfByte3 = this._rs.getBytes(paramInt);
/* 386 */       if (this._rs.wasNull())
/*     */       {
/* 388 */         paramTdsOutputStream.writeByte(0); return;
/*     */       }
/*     */ 
/* 392 */       paramTdsOutputStream.writeInt(arrayOfByte3.length);
/* 393 */       paramTdsOutputStream.write(arrayOfByte3);
/*     */ 
/* 395 */       break;
/*     */     case 48:
/* 397 */       paramTdsOutputStream.writeByte(this._rs.getByte(paramInt));
/* 398 */       break;
/*     */     case 52:
/* 400 */       paramTdsOutputStream.writeShort(this._rs.getShort(paramInt));
/* 401 */       break;
/*     */     case 56:
/* 403 */       paramTdsOutputStream.writeInt(this._rs.getInt(paramInt));
/* 404 */       break;
/*     */     case 191:
/* 406 */       paramTdsOutputStream.writeLong(this._rs.getLong(paramInt));
/* 407 */       break;
/*     */     case 38:
/* 409 */       int i = this._rs.getInt(paramInt);
/* 410 */       if (this._rs.wasNull())
/*     */       {
/* 412 */         paramTdsOutputStream.writeByte(0); return;
/*     */       }
/*     */ 
/* 416 */       paramTdsOutputStream.writeByte(paramDataFormat._length);
/* 417 */       switch (paramDataFormat._length) {
/*     */       case 1:
/* 420 */         paramTdsOutputStream.writeByte((byte)i);
/* 421 */         break;
/*     */       case 2:
/* 423 */         paramTdsOutputStream.writeShort((short)i);
/* 424 */         break;
/*     */       case 4:
/* 426 */         paramTdsOutputStream.writeInt(i);
/* 427 */         break;
/*     */       case 8:
/* 429 */         paramTdsOutputStream.writeLong(i);
/*     */       case 3:
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/* 433 */       }break;
/*     */     case 59:
/* 435 */       paramTdsOutputStream.writeFloat(this._rs.getFloat(paramInt));
/* 436 */       break;
/*     */     case 62:
/* 438 */       paramTdsOutputStream.writeDouble(this._rs.getDouble(paramInt));
/* 439 */       break;
/*     */     case 109:
/* 443 */       float f = 0.0F;
/* 444 */       double d = 0.0D;
/* 445 */       switch (paramDataFormat._length)
/*     */       {
/*     */       case 4:
/* 448 */         f = this._rs.getFloat(paramInt);
/* 449 */         break;
/*     */       case 8:
/* 451 */         d = this._rs.getDouble(paramInt);
/*     */       }
/*     */ 
/* 454 */       if (this._rs.wasNull())
/*     */       {
/* 456 */         paramTdsOutputStream.writeByte(0); return;
/*     */       }
/*     */ 
/* 460 */       paramTdsOutputStream.writeByte(paramDataFormat._length);
/* 461 */       switch (paramDataFormat._length)
/*     */       {
/*     */       case 4:
/* 464 */         paramTdsOutputStream.writeFloat(f);
/* 465 */         break;
/*     */       case 8:
/* 467 */         paramTdsOutputStream.writeDouble(d);
/*     */       }
/*     */ 
/* 471 */       break;
/*     */     case 37:
/* 475 */       byte[] arrayOfByte4 = this._rs.getBytes(paramInt);
/* 476 */       if (this._rs.wasNull())
/*     */       {
/* 478 */         paramTdsOutputStream.writeByte(0); return;
/*     */       }
/*     */ 
/* 482 */       paramTdsOutputStream.writeByte(arrayOfByte4.length);
/* 483 */       paramTdsOutputStream.write(arrayOfByte4);
/*     */ 
/* 485 */       break;
/*     */     case 106:
/*     */     case 108:
/* 489 */       Object localObject1 = this._rs.getBigDecimal(paramInt, paramDataFormat._scale);
/* 490 */       if (this._rs.wasNull())
/*     */       {
/* 492 */         paramTdsOutputStream.writeByte(0); return;
/*     */       }
/*     */ 
/* 497 */       localObject1 = new SybBigDecimal((BigDecimal)localObject1, paramDataFormat._precision, paramDataFormat._scale);
/*     */ 
/* 499 */       localObject2 = new int[4];
/*     */ 
/* 501 */       localObject3 = TdsNumeric.tdsNumeric((BigDecimal)localObject1, paramDataFormat._scale, localObject2, false);
/*     */ 
/* 503 */       paramTdsOutputStream.writeByte(localObject2[1]);
/* 504 */       if (localObject2[1] <= 0)
/*     */         return;
/* 506 */       paramTdsOutputStream.write(localObject3); break;
/*     */     case 111:
/* 512 */       localObject2 = null;
/* 513 */       switch (this._rs.getMetaData().getColumnType(paramInt))
/*     */       {
/*     */       case 92:
/* 516 */         localObject2 = new DateObject(this._rs.getTime(paramInt), 92);
/*     */ 
/* 518 */         break;
/*     */       case 91:
/* 520 */         localObject2 = new DateObject(this._rs.getDate(paramInt), 91);
/*     */ 
/* 522 */         break;
/*     */       case 93:
/* 524 */         localObject2 = new DateObject(this._rs.getTimestamp(paramInt), 93);
/*     */       }
/*     */ 
/* 529 */       if (this._rs.wasNull())
/*     */       {
/* 531 */         paramTdsOutputStream.writeByte(0); return;
/*     */       }
/*     */ 
/* 535 */       localObject3 = TdsDateTime.tdsDateTime((DateObject)localObject2);
/* 536 */       paramTdsOutputStream.writeByte(8);
/*     */ 
/* 538 */       paramTdsOutputStream.writeInt(localObject3[0]);
/* 539 */       paramTdsOutputStream.writeInt(localObject3[1]);
/*     */ 
/* 541 */       break;
/*     */     case 49:
/*     */     case 123:
/* 545 */       localObject2 = null;
/* 546 */       localObject2 = new DateObject(this._rs.getDate(paramInt), 91);
/*     */ 
/* 548 */       if (this._rs.wasNull())
/*     */       {
/* 550 */         paramTdsOutputStream.writeByte(0); return;
/*     */       }
/*     */ 
/* 554 */       localObject3 = TdsDateTime.tdsDateTime((DateObject)localObject2);
/* 555 */       paramTdsOutputStream.writeByte(4);
/* 556 */       paramTdsOutputStream.writeInt(localObject3[0]);
/*     */ 
/* 558 */       break;
/*     */     case 51:
/*     */     case 147:
/* 562 */       localObject2 = null;
/* 563 */       localObject2 = new DateObject(this._rs.getTime(paramInt), 92);
/*     */ 
/* 565 */       if (this._rs.wasNull())
/*     */       {
/* 567 */         paramTdsOutputStream.writeByte(0); return;
/*     */       }
/*     */ 
/* 571 */       localObject3 = TdsDateTime.tdsDateTime((DateObject)localObject2);
/* 572 */       paramTdsOutputStream.writeByte(4);
/* 573 */       paramTdsOutputStream.writeInt(localObject3[1]);
/*     */ 
/* 575 */       break;
/*     */     case 36:
/* 578 */       switch (paramDataFormat._blobType)
/*     */       {
/*     */       case 1:
/* 581 */         localObject3 = this._rs.getObject(paramInt);
/* 582 */         paramTdsOutputStream.writeByte(1);
/* 583 */         paramTdsOutputStream.writeShort(0);
/* 584 */         if (localObject3 == null)
/*     */         {
/* 586 */           paramTdsOutputStream.writeInt(0);
/* 587 */           return;
/*     */         }
/* 589 */         BlobOutputStream localBlobOutputStream = new BlobOutputStream(paramTdsOutputStream);
/* 590 */         ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localBlobOutputStream);
/* 591 */         localObjectOutputStream.writeObject(localObject3);
/* 592 */         localObjectOutputStream.close();
/* 593 */         break;
/*     */       default:
/* 596 */         throw new SrvTypeException("Unmappable blob data type: " + this._rs.getMetaData().getColumnType(paramInt));
/*     */       }
/*     */     default:
/* 602 */       throw new SrvTypeException("Unmappable java data type: " + this._rs.getMetaData().getColumnType(paramInt));
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  75 */     int[][] arrayOfInt = { { -7, 50, 0, 0, 0 }, { -6, 38, 1, 48, 0 }, { 5, 38, 2, 52, 0 }, { 4, 38, 4, 56, 0 }, { -5, 38, 8, 0, 0 }, { 7, 109, 4, 0, 0 }, { 6, 109, 8, 0, 0 }, { 8, 109, 8, 0, 0 }, { 2, 108, 33, 0, 0 }, { 3, 106, 33, 0, 0 }, { 1, 39, 255, 0, 0 }, { 12, 39, 255, 0, 0 }, { -1, 175, 16384, 0, 0 }, { -4, 225, 16384, 0, 0 }, { 91, 123, 4, 0, 0 }, { 92, 147, 4, 0, 0 }, { 93, 111, 8, 0, 0 }, { -2, 37, 255, 0, 0 }, { -3, 37, 255, 0, 0 }, { 2000, 9217, 0, 0, 0 } };
/*     */ 
/* 171 */     for (int i = 0; i < arrayOfInt.length; ++i)
/* 172 */       _formatmap.put(new Integer(arrayOfInt[i][0]), arrayOfInt[i]);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvSQLTypeFormatter
 * JD-Core Version:    0.5.4
 */