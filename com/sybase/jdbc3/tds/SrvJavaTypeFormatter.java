/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.jdbc.Convert;
/*      */ import com.sybase.jdbc3.jdbc.DateObject;
/*      */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.util.Calendar;
/*      */ 
/*      */ public class SrvJavaTypeFormatter extends SrvTypeFormatter
/*      */ {
/*      */   private static final int BUF_SIZE = 2048;
/*      */   protected static final int COLSTATUS_NOT_SET = 9999;
/*      */   protected static final int SERIALIZATION_TYPE_NOT_SET = 9999;
/*  118 */   protected static final Integer LENGTH_NOT_SET = new Integer(-999);
/*      */ 
/*  121 */   protected Object[] _data = null;
/*      */ 
/*  124 */   private int[] _dataLengths = null;
/*      */ 
/*  127 */   private int[] _colStatus = null;
/*      */ 
/*  130 */   private int[] _serializationTypes = null;
/*      */ 
/*  132 */   private long[] _lobLengths = null;
/*  133 */   private int[] _locatorLengths = null;
/*  134 */   private Object[] _locators = null;
/*      */ 
/*  140 */   private int _columnNo = 0;
/*      */ 
/*  143 */   private int _lastColumnIndex = 0;
/*      */ 
/*  146 */   private boolean _mustConvertChars = true;
/*      */ 
/*      */   public SrvJavaTypeFormatter(SrvFormatToken paramSrvFormatToken, SrvCapabilityToken paramSrvCapabilityToken)
/*      */   {
/*  165 */     this(paramSrvFormatToken, paramSrvCapabilityToken, true);
/*      */   }
/*      */ 
/*      */   public SrvJavaTypeFormatter(SrvFormatToken paramSrvFormatToken, SrvCapabilityToken paramSrvCapabilityToken, boolean paramBoolean)
/*      */   {
/*  183 */     super(paramSrvFormatToken, paramSrvCapabilityToken);
/*  184 */     this._mustConvertChars = paramBoolean;
/*      */   }
/*      */ 
/*      */   public void addFormat(Object paramObject, String paramString, int paramInt1, int paramInt2)
/*      */     throws IOException, SrvTypeException
/*      */   {
/*  190 */     addFormat(paramObject, paramString, paramInt1, paramInt2, null);
/*      */   }
/*      */ 
/*      */   public void addFormat(Object paramObject, String paramString1, int paramInt1, int paramInt2, String paramString2)
/*      */     throws IOException, SrvTypeException
/*      */   {
/*  221 */     addFormat(paramObject, null, null, null, null, paramString1, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public void addFormat(Object paramObject, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt1, int paramInt2)
/*      */     throws IOException, SrvTypeException
/*      */   {
/*  272 */     int i = 0;
/*      */ 
/*  274 */     if (paramObject == null)
/*      */     {
/*  276 */       paramObject = new Object();
/*      */     }
/*      */ 
/*  280 */     if (paramObject instanceof String)
/*      */     {
/*  282 */       if ((paramInt2 > 0) && (paramInt2 <= 255))
/*      */       {
/*  285 */         i = 47;
/*      */       }
/*  287 */       else if (paramInt2 > 255)
/*      */       {
/*  289 */         i = 175;
/*      */       }
/*      */       else
/*      */       {
/*  296 */         paramInt2 = 255;
/*  297 */         i = 39;
/*      */       }
/*      */     }
/*  300 */     else if (paramObject instanceof Boolean) {
/*  301 */       i = 50;
/*  302 */     } else if (paramObject instanceof byte[])
/*      */     {
/*  304 */       byte[] arrayOfByte = (byte[])paramObject;
/*  305 */       if (arrayOfByte.length > 255)
/*      */       {
/*  307 */         paramInt2 = 2147483647;
/*  308 */         i = 225;
/*      */       }
/*      */       else
/*      */       {
/*  312 */         i = 37;
/*  313 */         paramInt2 = 255;
/*      */       }
/*      */     }
/*  316 */     else if (paramObject instanceof Byte)
/*      */     {
/*  318 */       i = 38;
/*  319 */       paramInt2 = 1;
/*      */     }
/*  321 */     else if (paramObject instanceof Short)
/*      */     {
/*  323 */       i = 38;
/*  324 */       paramInt2 = 2;
/*      */     }
/*  326 */     else if (paramObject instanceof Integer)
/*      */     {
/*  328 */       i = 38;
/*  329 */       paramInt2 = 4;
/*      */     }
/*  331 */     else if (paramObject instanceof Long)
/*      */     {
/*  333 */       i = 38;
/*  334 */       paramInt2 = 8;
/*      */     }
/*  336 */     else if (paramObject instanceof Float)
/*      */     {
/*  338 */       i = 59;
/*      */     }
/*  340 */     else if (paramObject instanceof Double)
/*      */     {
/*  342 */       i = 62;
/*      */     }
/*  344 */     else if (paramObject instanceof java.sql.Date)
/*      */     {
/*  346 */       if (this._cap.handlesDataType(49))
/*      */       {
/*  348 */         i = 123;
/*  349 */         paramInt2 = 4;
/*      */       }
/*      */       else
/*      */       {
/*  353 */         i = 111;
/*  354 */         paramInt2 = 8;
/*      */       }
/*      */     }
/*  357 */     else if (paramObject instanceof Time)
/*      */     {
/*  359 */       if (this._cap.handlesDataType(51))
/*      */       {
/*  361 */         i = 147;
/*  362 */         paramInt2 = 4;
/*      */       }
/*      */       else
/*      */       {
/*  366 */         i = 111;
/*  367 */         paramInt2 = 8;
/*      */       }
/*      */     }
/*  370 */     else if ((paramObject instanceof java.util.Date) || (paramObject instanceof Calendar))
/*      */     {
/*  373 */       i = 111;
/*  374 */       paramInt2 = 8;
/*      */     }
/*  376 */     else if (paramObject instanceof InputStream)
/*      */     {
/*  378 */       i = 9220;
/*  379 */       paramInt2 = -1;
/*      */     }
/*  381 */     else if (paramObject instanceof Reader)
/*      */     {
/*  383 */       i = 9219;
/*  384 */       paramInt2 = -1;
/*      */     }
/*  386 */     else if (paramObject instanceof SrvTextImageData)
/*      */     {
/*  389 */       i = 34;
/*  390 */       paramInt2 = 2147483647;
/*      */     }
/*      */     else
/*      */     {
/*  395 */       i = 9217;
/*  396 */       paramInt2 = -1;
/*      */     }
/*      */ 
/*  408 */     if (this._format instanceof SrvParamFormat2Token)
/*      */     {
/*  410 */       ((SrvParamFormat2Token)this._format).addFormat(new SrvParamDataFormat2(paramString5, i, paramInt1, paramInt2, paramObject));
/*      */     }
/*  412 */     else if (this._format instanceof SrvRowFormat2Token)
/*      */     {
/*  416 */       ((SrvRowFormat2Token)this._format).addFormat(new SrvRowDataFormat2(paramString1, paramString2, paramString3, paramString4, paramString5, i, paramInt1, paramInt2, 0, 0, paramObject));
/*      */     }
/*      */     else
/*      */     {
/*  424 */       this._format.addFormat(new SrvDataFormat(paramString5, i, paramInt1, paramInt2, paramObject));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addFormat(DataFormat paramDataFormat)
/*      */     throws IOException, SrvTypeException
/*      */   {
/*  438 */     this._format.addFormat(paramDataFormat);
/*      */   }
/*      */ 
/*      */   public void convertData(Token paramToken, Object[] paramArrayOfObject)
/*      */     throws IOException
/*      */   {
/*  452 */     setFormatter(paramToken, this);
/*  453 */     this._data = paramArrayOfObject;
/*      */   }
/*      */ 
/*      */   public void sendDataStream(TdsOutputStream paramTdsOutputStream)
/*      */     throws IOException
/*      */   {
/*  461 */     if (this._data.length != this._format.getFormatCount()) {
/*  462 */       throw new SrvTypeException("Format to data item count is off, " + this._data.length + " data items" + " and " + this._format.getFormatCount() + " formats");
/*      */     }
/*      */ 
/*  467 */     for (int i = 0; i < this._data.length; ++i)
/*      */     {
/*  469 */       sendData(paramTdsOutputStream, this._data[i], this._format.formatAt(i));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void sendData(TdsOutputStream paramTdsOutputStream, Object paramObject, DataFormat paramDataFormat)
/*      */     throws IOException, SrvTypeException
/*      */   {
/*  497 */     if (paramObject == null);
/*      */     byte[] arrayOfByte1;
/*      */     DateObject localDateObject;
/*      */     int[] arrayOfInt1;
/*      */     Object localObject;
/*  507 */     switch (paramDataFormat._datatype)
/*      */     {
/*      */     case 50:
/*  510 */       if (((Boolean)paramObject).booleanValue())
/*      */       {
/*  512 */         paramTdsOutputStream.writeByte(1); return;
/*      */       }
/*      */ 
/*  516 */       paramTdsOutputStream.writeByte(0);
/*      */ 
/*  518 */       break;
/*      */     case 37:
/*      */     case 45:
/*      */     case 225:
/*  522 */       if (paramObject == null)
/*      */       {
/*  524 */         if (paramDataFormat._datatype == 225)
/*      */         {
/*  526 */           paramTdsOutputStream.writeInt(0); return;
/*      */         }
/*      */ 
/*  530 */         paramTdsOutputStream.writeByte(0); return;
/*      */       }
/*      */ 
/*  533 */       if (paramObject instanceof byte[])
/*      */       {
/*  535 */         arrayOfByte1 = (byte[])paramObject;
/*  536 */         if (paramDataFormat._datatype == 225)
/*      */         {
/*  538 */           paramTdsOutputStream.writeInt(arrayOfByte1.length);
/*      */         }
/*      */         else
/*      */         {
/*  542 */           paramTdsOutputStream.writeByte(arrayOfByte1.length);
/*      */         }
/*  544 */         paramTdsOutputStream.write(arrayOfByte1); return;
/*      */       }
/*  546 */       if (paramObject instanceof Byte)
/*      */       {
/*  548 */         paramTdsOutputStream.writeByte(1);
/*  549 */         paramTdsOutputStream.writeByte(((Byte)paramObject).byteValue()); return;
/*      */       }
/*      */ 
/*  553 */       throw new SrvTypeException("Expected Byte or Byte[], got " + paramObject.getClass().getName());
/*      */     case 39:
/*      */     case 47:
/*  559 */       if (paramObject == null)
/*      */       {
/*  561 */         paramTdsOutputStream.writeByte(0); return;
/*      */       }
/*      */ 
/*  565 */       arrayOfByte1 = paramTdsOutputStream.stringToByte((String)paramObject);
/*  566 */       paramTdsOutputStream.writeByte(arrayOfByte1.length);
/*  567 */       paramTdsOutputStream.write(arrayOfByte1);
/*      */ 
/*  569 */       break;
/*      */     case 175:
/*  571 */       if (paramObject == null)
/*      */       {
/*  573 */         paramTdsOutputStream.writeInt(0); return;
/*      */       }
/*      */ 
/*  577 */       arrayOfByte1 = paramTdsOutputStream.stringToByte((String)paramObject);
/*  578 */       paramTdsOutputStream.writeInt(arrayOfByte1.length);
/*  579 */       paramTdsOutputStream.write(arrayOfByte1);
/*      */ 
/*  581 */       break;
/*      */     case 48:
/*  584 */       paramTdsOutputStream.writeByte(((Number)paramObject).intValue());
/*  585 */       break;
/*      */     case 52:
/*  587 */       paramTdsOutputStream.writeShort(((Number)paramObject).intValue());
/*  588 */       break;
/*      */     case 56:
/*  590 */       paramTdsOutputStream.writeInt(((Number)paramObject).intValue());
/*  591 */       break;
/*      */     case 191:
/*  593 */       paramTdsOutputStream.writeLong(((Number)paramObject).longValue());
/*  594 */       break;
/*      */     case 59:
/*  596 */       paramTdsOutputStream.writeFloat(((Number)paramObject).floatValue());
/*  597 */       break;
/*      */     case 62:
/*  599 */       paramTdsOutputStream.writeDouble(((Number)paramObject).doubleValue());
/*  600 */       break;
/*      */     case 109:
/*  602 */       if (paramObject == null)
/*      */       {
/*  604 */         paramTdsOutputStream.writeByte(0); return;
/*      */       }
/*      */ 
/*  608 */       paramTdsOutputStream.writeByte(paramDataFormat._length);
/*  609 */       switch (paramDataFormat._length)
/*      */       {
/*      */       case 4:
/*  612 */         paramTdsOutputStream.writeFloat(((Number)paramObject).floatValue());
/*  613 */         break;
/*      */       case 8:
/*  615 */         paramTdsOutputStream.writeDouble(((Number)paramObject).doubleValue());
/*      */       }
/*      */ 
/*  619 */       break;
/*      */     case 38:
/*  622 */       if (paramObject == null)
/*      */       {
/*  624 */         paramTdsOutputStream.writeByte(0); return;
/*      */       }
/*      */ 
/*  628 */       paramTdsOutputStream.writeByte(paramDataFormat._length);
/*      */       try
/*      */       {
/*  631 */         switch (paramDataFormat._length) {
/*      */         case 1:
/*  634 */           paramTdsOutputStream.writeByte(((Number)paramObject).byteValue());
/*  635 */           break;
/*      */         case 2:
/*  637 */           paramTdsOutputStream.writeShort(((Number)paramObject).shortValue());
/*  638 */           break;
/*      */         case 4:
/*  640 */           paramTdsOutputStream.writeInt(((Number)paramObject).intValue());
/*  641 */           break;
/*      */         case 8:
/*  643 */           paramTdsOutputStream.writeLong(((Number)paramObject).longValue());
/*      */         case 3:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         }
/*      */       } catch (ClassCastException localClassCastException) {
/*  649 */         throw new SrvTypeException("Object does not match data format requested");
/*      */       }
/*      */     case 187:
/*      */     case 188:
/*  656 */       if (paramObject == null)
/*      */       {
/*  658 */         paramTdsOutputStream.writeByte(0); return;
/*      */       }
/*      */ 
/*  662 */       localDateObject = null;
/*      */       try
/*      */       {
/*  665 */         localDateObject = Convert.objectToDateObject(paramObject, null);
/*      */       }
/*      */       catch (SQLException localSQLException1)
/*      */       {
/*  669 */         throw new SrvTypeException("Cannot cast " + paramObject.getClass().getName() + " to a TDS_BIGDATIMEN or TDS_BIGTIMEM: " + localSQLException1);
/*      */       }
/*      */ 
/*  673 */       long l = TdsDateTime.tdsDateTime(localDateObject, paramDataFormat._datatype);
/*  674 */       paramTdsOutputStream.writeByte(8);
/*  675 */       paramTdsOutputStream.writeLong(l);
/*      */ 
/*  677 */       break;
/*      */     case 61:
/*  680 */       localDateObject = null;
/*      */       try
/*      */       {
/*  683 */         localDateObject = Convert.objectToDateObject(paramObject, null);
/*      */       }
/*      */       catch (SQLException localSQLException2)
/*      */       {
/*  687 */         throw new SrvTypeException("Cannot cast " + paramObject.getClass().getName() + " to a TDS_DATIMN: " + localSQLException2);
/*      */       }
/*      */ 
/*  691 */       arrayOfInt1 = TdsDateTime.tdsDateTime(localDateObject);
/*  692 */       paramTdsOutputStream.writeInt(arrayOfInt1[0]);
/*  693 */       paramTdsOutputStream.writeInt(arrayOfInt1[1]);
/*  694 */       break;
/*      */     case 111:
/*  697 */       if (paramObject == null)
/*      */       {
/*  699 */         paramTdsOutputStream.writeByte(0); return;
/*      */       }
/*      */ 
/*  703 */       localDateObject = null;
/*      */       try
/*      */       {
/*  706 */         localDateObject = Convert.objectToDateObject(paramObject, null);
/*      */       }
/*      */       catch (SQLException localSQLException3)
/*      */       {
/*  710 */         throw new SrvTypeException("Cannot cast " + paramObject.getClass().getName() + " to a TDS_DATIMN: " + localSQLException3);
/*      */       }
/*      */ 
/*  714 */       arrayOfInt1 = TdsDateTime.tdsDateTime(localDateObject);
/*  715 */       paramTdsOutputStream.writeByte(8);
/*  716 */       paramTdsOutputStream.writeInt(arrayOfInt1[0]);
/*  717 */       paramTdsOutputStream.writeInt(arrayOfInt1[1]);
/*      */ 
/*  719 */       break;
/*      */     case 123:
/*  721 */       if (paramObject == null)
/*      */       {
/*  723 */         paramTdsOutputStream.writeByte(0); return;
/*      */       }
/*      */ 
/*  727 */       localDateObject = null;
/*      */       try
/*      */       {
/*  730 */         localDateObject = Convert.objectToDateObject(paramObject, null);
/*      */       }
/*      */       catch (SQLException localSQLException4)
/*      */       {
/*  734 */         throw new SrvTypeException("Cannot cast " + paramObject.getClass().getName() + " to a TDS_DATEN: " + localSQLException4);
/*      */       }
/*      */ 
/*  738 */       int[] arrayOfInt2 = TdsDateTime.tdsDateTime(localDateObject);
/*  739 */       paramTdsOutputStream.writeByte(4);
/*  740 */       paramTdsOutputStream.writeInt(arrayOfInt2[0]);
/*      */ 
/*  742 */       break;
/*      */     case 147:
/*  744 */       if (paramObject == null)
/*      */       {
/*  746 */         paramTdsOutputStream.writeByte(0); return;
/*      */       }
/*      */ 
/*  750 */       localDateObject = null;
/*      */       try
/*      */       {
/*  753 */         localDateObject = Convert.objectToDateObject(paramObject, null);
/*      */       }
/*      */       catch (SQLException localSQLException5)
/*      */       {
/*  757 */         throw new SrvTypeException("Cannot cast " + paramObject.getClass().getName() + " to a TDS_TIMEN: " + localSQLException5);
/*      */       }
/*      */ 
/*  761 */       arrayOfInt1 = TdsDateTime.tdsDateTime(localDateObject);
/*  762 */       paramTdsOutputStream.writeByte(4);
/*  763 */       paramTdsOutputStream.writeInt(arrayOfInt1[1]);
/*      */ 
/*  765 */       break;
/*      */     case 36:
/*      */       BlobOutputStream localBlobOutputStream;
/*      */       InputStream localInputStream;
/*      */       byte[] arrayOfByte2;
/*      */       OutputStreamWriter localOutputStreamWriter;
/*      */       char[] arrayOfChar;
/*      */       int k;
/*  767 */       switch (paramDataFormat._blobType)
/*      */       {
/*      */       case 1:
/*  770 */         paramTdsOutputStream.writeByte(1);
/*  771 */         paramTdsOutputStream.writeShort(0);
/*  772 */         if (paramObject == null)
/*      */         {
/*  774 */           paramTdsOutputStream.writeInt(0);
/*  775 */           return;
/*      */         }
/*  777 */         localBlobOutputStream = new BlobOutputStream(paramTdsOutputStream);
/*  778 */         localObject = new ObjectOutputStream(localBlobOutputStream);
/*  779 */         ((ObjectOutputStream)localObject).writeObject(paramObject);
/*  780 */         ((ObjectOutputStream)localObject).close();
/*  781 */         break;
/*      */       case 4:
/*  783 */         paramTdsOutputStream.writeByte(0);
/*  784 */         paramTdsOutputStream.writeShort(0);
/*  785 */         if (paramObject == null)
/*      */         {
/*  787 */           paramTdsOutputStream.writeInt(0);
/*  788 */           return;
/*      */         }
/*  790 */         localBlobOutputStream = new BlobOutputStream(paramTdsOutputStream);
/*  791 */         localInputStream = (InputStream)paramObject;
/*  792 */         arrayOfByte2 = new byte[2048];
/*      */         while (true)
/*      */         {
/*  795 */           int j = localInputStream.read(arrayOfByte2);
/*  796 */           if (j < 0) break;
/*  797 */           localBlobOutputStream.write(arrayOfByte2, 0, j);
/*      */         }
/*  799 */         localBlobOutputStream.close();
/*  800 */         break;
/*      */       case 3:
/*  802 */         paramTdsOutputStream.writeByte(0);
/*  803 */         paramTdsOutputStream.writeShort(0);
/*  804 */         if (paramObject == null)
/*      */         {
/*  806 */           paramTdsOutputStream.writeInt(0);
/*  807 */           return;
/*      */         }
/*  809 */         localBlobOutputStream = new BlobOutputStream(paramTdsOutputStream);
/*  810 */         localOutputStreamWriter = new OutputStreamWriter(localBlobOutputStream, paramTdsOutputStream.getCharset());
/*      */ 
/*  812 */         arrayOfChar = new char[2048];
/*  813 */         Reader localReader1 = (Reader)paramObject;
/*      */         while (true)
/*      */         {
/*  816 */           k = localReader1.read(arrayOfChar);
/*  817 */           if (k < 0) break;
/*  818 */           localOutputStreamWriter.write(arrayOfChar, 0, k);
/*      */         }
/*  820 */         localOutputStreamWriter.flush();
/*  821 */         localBlobOutputStream.close();
/*  822 */         break;
/*      */       case 5:
/*  830 */         paramTdsOutputStream.writeByte(0);
/*  831 */         paramTdsOutputStream.writeShort(0);
/*  832 */         if (paramObject == null)
/*      */         {
/*  834 */           paramTdsOutputStream.writeInt(0);
/*  835 */           return;
/*      */         }
/*  837 */         localBlobOutputStream = new BlobOutputStream(paramTdsOutputStream);
/*      */         int i1;
/*  838 */         if (paramObject instanceof InputStream)
/*      */         {
/*  840 */           localInputStream = (InputStream)paramObject;
/*  841 */           arrayOfByte2 = new byte[2048];
/*      */           while (true)
/*      */           {
/*  844 */             k = localInputStream.read(arrayOfByte2);
/*  845 */             if (k < 0) break;
/*  846 */             if (k % 2 != 0)
/*      */             {
/*  853 */               i1 = localInputStream.read();
/*      */ 
/*  856 */               if (i1 == -1) break;
/*  857 */               arrayOfByte2[(k++)] = (byte)i1;
/*      */             }
/*  859 */             localBlobOutputStream.write(arrayOfByte2, 0, k);
/*      */           }
/*  861 */           localBlobOutputStream.close();
/*  862 */           return;
/*      */         }
/*  864 */         if (paramObject instanceof Reader)
/*      */         {
/*  866 */           Reader localReader2 = (Reader)paramObject;
/*  867 */           arrayOfChar = new char[2048];
/*  868 */           localOutputStreamWriter = null;
/*  869 */           if (!paramTdsOutputStream.getBigEndian())
/*      */           {
/*  871 */             localOutputStreamWriter = new OutputStreamWriter(localBlobOutputStream, "UnicodeLittleUnmarked");
/*      */           }
/*      */           else
/*      */           {
/*  876 */             localOutputStreamWriter = new OutputStreamWriter(localBlobOutputStream, "UnicodeBigUnmarked");
/*      */           }
/*      */ 
/*      */           while (true)
/*      */           {
/*  881 */             i1 = localReader2.read(arrayOfChar);
/*  882 */             if (i1 < 0) break;
/*  883 */             localOutputStreamWriter.write(arrayOfChar, 0, i1);
/*      */           }
/*  885 */           localOutputStreamWriter.flush();
/*  886 */           localBlobOutputStream.close();
/*  887 */           return;
/*      */         }
/*      */ 
/*  891 */         throw new SrvTypeException("Need an InputStream or Reader when sending BLOB_UTF16 data. Passed-in value was " + paramObject.getClass().getName());
/*      */       case 2:
/*      */       }
/*      */ 
/*  899 */       break;
/*      */     case 106:
/*      */     case 108:
/*  906 */       if (paramObject == null)
/*      */       {
/*  908 */         paramTdsOutputStream.writeByte(0); return;
/*      */       }
/*      */ 
/*      */       SybBigDecimal localSybBigDecimal;
/*      */       try
/*      */       {
/*  915 */         localSybBigDecimal = new SybBigDecimal((BigDecimal)paramObject, paramDataFormat._precision, paramDataFormat._scale);
/*      */       }
/*      */       catch (SQLException localSQLException6)
/*      */       {
/*  919 */         throw new SrvTypeException("Unable to convert object to decimal: " + paramObject.getClass().getName());
/*      */       }
/*      */ 
/*  923 */       int[] arrayOfInt3 = new int[4];
/*  924 */       localObject = TdsNumeric.tdsNumeric(localSybBigDecimal, paramDataFormat._scale, arrayOfInt3, false);
/*  925 */       paramTdsOutputStream.writeByte(arrayOfInt3[1]);
/*  926 */       if (arrayOfInt3[1] <= 0)
/*      */         return;
/*  928 */       paramTdsOutputStream.write(localObject); break;
/*      */     case 34:
/*      */     case 35:
/*      */     case 174:
/*  937 */       if ((paramObject == null) || ((paramObject instanceof SrvTextImageData) && (((SrvTextImageData)paramObject).isNull())))
/*      */       {
/*  941 */         paramTdsOutputStream.writeByte(0);
/*  942 */         return;
/*      */       }
/*  944 */       if (!paramObject instanceof SrvTextImageData)
/*      */       {
/*  946 */         throw new SrvTypeException("Unmappable IMAGE, TEXT or UNITEXT data type: " + paramObject.getClass().getName());
/*      */       }
/*      */ 
/*  951 */       SrvTextImageData localSrvTextImageData = (SrvTextImageData)paramObject;
/*  952 */       paramTdsOutputStream.writeByte(localSrvTextImageData.getTextPtrLength());
/*  953 */       paramTdsOutputStream.write(localSrvTextImageData.getTextPtr());
/*  954 */       paramTdsOutputStream.writeLong(localSrvTextImageData.getTimeStamp());
/*      */ 
/*  959 */       if (localSrvTextImageData._bytes != null)
/*      */       {
/*  961 */         paramTdsOutputStream.writeInt(localSrvTextImageData.getLength());
/*  962 */         paramTdsOutputStream.write(localSrvTextImageData._bytes); return;
/*      */       }
/*  964 */       if (localSrvTextImageData._byteStream != null)
/*      */       {
/*  968 */         paramTdsOutputStream.writeInt(localSrvTextImageData.getLength());
/*      */         try
/*      */         {
/*  972 */           int i = localSrvTextImageData._byteStream.read();
/*  973 */           while (i != -1)
/*      */           {
/*  975 */             paramTdsOutputStream.write(i);
/*  976 */             i = localSrvTextImageData._byteStream.read();
/*      */           }
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*  981 */           throw new SrvTypeException("Problem reading SrvTextImageData InputStream:" + localIOException);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  986 */       throw new SrvTypeException("Invalid construction of SrvTextImageData: no data!!!");
/*      */     default:
/*  992 */       throw new SrvTypeException("Unmappable java data type: " + paramObject.getClass().getName());
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object[] convertData(Token paramToken)
/*      */     throws IOException
/*      */   {
/* 1006 */     Object[] arrayOfObject = new Object[this._format.getFormatCount()];
/* 1007 */     this._dataLengths = new int[this._format.getFormatCount()];
/* 1008 */     this._colStatus = new int[this._format.getFormatCount()];
/* 1009 */     this._serializationTypes = new int[this._format.getFormatCount()];
/* 1010 */     this._lobLengths = new long[this._format.getFormatCount()];
/* 1011 */     this._locatorLengths = new int[this._format.getFormatCount()];
/* 1012 */     this._locators = new Object[this._format.getFormatCount()];
/*      */ 
/* 1015 */     for (int i = 0; i < this._serializationTypes.length; ++i)
/*      */     {
/* 1017 */       this._serializationTypes[i] = 9999;
/* 1018 */       this._lobLengths[i] = LENGTH_NOT_SET.intValue();
/* 1019 */       this._locatorLengths[i] = LENGTH_NOT_SET.intValue();
/* 1020 */       this._locators[i] = LENGTH_NOT_SET;
/*      */     }
/*      */ 
/* 1026 */     TdsInputStream localTdsInputStream = null;
/* 1027 */     this._lastColumnIndex = (arrayOfObject.length - 1);
/* 1028 */     for (int j = 0; j < arrayOfObject.length; this._columnNo += 1)
/*      */     {
/* 1030 */       if (paramToken instanceof SrvDataToken)
/*      */       {
/* 1033 */         localTdsInputStream = ((SrvDataToken)paramToken).getStream();
/*      */       }
/* 1035 */       if ((!paramToken instanceof SrvKeyToken) || ((paramToken instanceof SrvKeyToken) && ((this._format.formatAt(j)._status & 0x6) != 0)))
/*      */       {
/* 1038 */         arrayOfObject[j] = receiveData(localTdsInputStream, this._format.formatAt(j));
/*      */       }
/* 1028 */       ++j;
/*      */     }
/*      */ 
/* 1041 */     return arrayOfObject;
/*      */   }
/*      */ 
/*      */   public int[] getDataLengths()
/*      */   {
/* 1052 */     return this._dataLengths;
/*      */   }
/*      */ 
/*      */   public int[] getColumnStatusBytes()
/*      */   {
/* 1065 */     return this._colStatus;
/*      */   }
/*      */ 
/*      */   public int[] getSerializationTypes()
/*      */   {
/* 1077 */     return this._serializationTypes;
/*      */   }
/*      */ 
/*      */   public SrvFormatToken getDataFormats()
/*      */   {
/* 1082 */     return this._format;
/*      */   }
/*      */ 
/*      */   public int[] getLengthSizes()
/*      */   {
/* 1088 */     int i = this._format.getFormatCount();
/* 1089 */     int[] arrayOfInt = new int[i];
/* 1090 */     for (int j = 0; j < i; ++j)
/*      */     {
/* 1093 */       DataFormat localDataFormat = this._format.formatAt(j);
/*      */ 
/* 1096 */       arrayOfInt[j] = DataFormat.lengthSize(localDataFormat._datatype);
/*      */     }
/* 1098 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   protected Object receiveData(TdsInputStream paramTdsInputStream, DataFormat paramDataFormat)
/*      */     throws IOException, SrvTypeException
/*      */   {
/* 1115 */     int i = 0;
/*      */     int j;
/* 1123 */     if (paramDataFormat._colStatusBytePresent)
/*      */     {
/* 1131 */       j = paramTdsInputStream.readByte();
/*      */ 
/* 1133 */       this._colStatus[this._columnNo] = j;
/* 1134 */       if ((j & 0x1) != 0)
/*      */       {
/* 1138 */         this._dataLengths[this._columnNo] = 0;
/*      */ 
/* 1141 */         return null;
/*      */       }
/* 1143 */       if (j == 0)
/*      */       {
/* 1145 */         i = 1;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1150 */       this._colStatus[this._columnNo] = 9999;
/*      */     }
/*      */     byte[] arrayOfByte1;
/*      */     int k;
/*      */     int i3;
/*      */     byte[] arrayOfByte2;
/*      */     int i4;
/* 1153 */     switch (paramDataFormat._datatype)
/*      */     {
/*      */     case 50:
/* 1156 */       this._dataLengths[this._columnNo] = 1;
/* 1157 */       return new Boolean(paramTdsInputStream.readByte() == 0);
/*      */     case 37:
/*      */     case 45:
/*      */     case 225:
/* 1163 */       if (paramDataFormat._datatype == 225)
/*      */       {
/* 1165 */         j = paramTdsInputStream.readInt();
/*      */       }
/*      */       else
/*      */       {
/* 1169 */         j = paramTdsInputStream.readUnsignedByte();
/*      */       }
/* 1171 */       this._dataLengths[this._columnNo] = j;
/*      */ 
/* 1174 */       if (j == 0)
/*      */       {
/* 1176 */         if (i == 0)
/*      */         {
/* 1178 */           return null;
/*      */         }
/*      */ 
/* 1182 */         return new byte[0];
/*      */       }
/*      */ 
/* 1188 */       if (paramDataFormat.isUnitype())
/*      */       {
/* 1190 */         return paramTdsInputStream.readUnicodeString(j);
/*      */       }
/* 1192 */       arrayOfByte1 = new byte[j];
/* 1193 */       while (j > 0)
/*      */       {
/* 1195 */         k = paramTdsInputStream.read(arrayOfByte1, arrayOfByte1.length - j, j);
/* 1196 */         if (k == -1)
/*      */         {
/* 1199 */           throw new IOException("Unexpected end of data from client");
/*      */         }
/*      */ 
/* 1202 */         j -= k;
/*      */       }
/* 1204 */       return arrayOfByte1;
/*      */     case 47:
/* 1207 */       k = paramTdsInputStream.readUnsignedByte();
/* 1208 */       this._dataLengths[this._columnNo] = k;
/* 1209 */       if (k == 0)
/*      */       {
/* 1211 */         return null;
/*      */       }
/* 1213 */       if (this._mustConvertChars)
/*      */       {
/* 1215 */         return paramTdsInputStream.readString(k);
/*      */       }
/* 1217 */       arrayOfByte1 = new byte[k];
/* 1218 */       paramTdsInputStream.read(arrayOfByte1);
/* 1219 */       return new SrvVarCharData(arrayOfByte1, paramTdsInputStream.getCharset());
/*      */     case 39:
/*      */     case 103:
/*      */     case 104:
/* 1224 */       int l = paramTdsInputStream.readUnsignedByte();
/* 1225 */       this._dataLengths[this._columnNo] = l;
/* 1226 */       if (l == 0)
/*      */       {
/* 1228 */         if (i != 0)
/*      */         {
/* 1230 */           return "";
/*      */         }
/*      */ 
/* 1234 */         return null;
/*      */       }
/*      */ 
/* 1237 */       if (this._mustConvertChars)
/*      */       {
/* 1239 */         return paramTdsInputStream.readString(l);
/*      */       }
/* 1241 */       arrayOfByte1 = new byte[l];
/* 1242 */       paramTdsInputStream.read(arrayOfByte1);
/* 1243 */       return new SrvVarCharData(arrayOfByte1, paramTdsInputStream.getCharset());
/*      */     case 175:
/* 1246 */       k = paramTdsInputStream.readInt();
/* 1247 */       this._dataLengths[this._columnNo] = k;
/*      */ 
/* 1250 */       if ((k == 0) && 
/* 1252 */         (i != 0))
/*      */       {
/* 1254 */         return "";
/*      */       }
/*      */ 
/* 1257 */       return paramTdsInputStream.readString(k);
/*      */     case 48:
/* 1260 */       this._dataLengths[this._columnNo] = 1;
/* 1261 */       return new Byte(paramTdsInputStream.readByte());
/*      */     case 52:
/* 1264 */       this._dataLengths[this._columnNo] = 2;
/* 1265 */       return new Short(paramTdsInputStream.readShort());
/*      */     case 65:
/* 1268 */       this._dataLengths[this._columnNo] = 4;
/* 1269 */       return new Integer(paramTdsInputStream.readUnsignedShortAsInt());
/*      */     case 56:
/* 1272 */       this._dataLengths[this._columnNo] = 4;
/* 1273 */       return new Integer(paramTdsInputStream.readInt());
/*      */     case 66:
/* 1276 */       this._dataLengths[this._columnNo] = 8;
/* 1277 */       return new Long(paramTdsInputStream.readUnsignedIntAsLong());
/*      */     case 191:
/* 1280 */       this._dataLengths[this._columnNo] = 8;
/* 1281 */       return new Long(paramTdsInputStream.readLong());
/*      */     case 67:
/* 1284 */       this._dataLengths[this._columnNo] = 8;
/* 1285 */       return paramTdsInputStream.readUnsignedLongAsBigDecimal();
/*      */     case 68:
/* 1288 */       int i1 = paramTdsInputStream.readUnsignedByte();
/* 1289 */       this._dataLengths[this._columnNo] = i1;
/* 1290 */       if (i1 == 0)
/*      */       {
/* 1292 */         return null;
/*      */       }
/* 1294 */       switch (i1) { case 1:
/* 1297 */         return new Byte(paramTdsInputStream.readByte());
/*      */       case 2:
/* 1299 */         return new Integer(paramTdsInputStream.readUnsignedShortAsInt());
/*      */       case 4:
/* 1301 */         return new Long(paramTdsInputStream.readUnsignedIntAsLong());
/*      */       case 8:
/* 1303 */         return paramTdsInputStream.readUnsignedLongAsBigDecimal();
/*      */       case 3:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7: } throw new SrvTypeException("Unmappable TDS data UINTN type received: length " + i1);
/*      */     case 38:
/* 1310 */       int i2 = paramTdsInputStream.readUnsignedByte();
/* 1311 */       this._dataLengths[this._columnNo] = i2;
/* 1312 */       if (i2 == 0)
/*      */       {
/* 1314 */         return null;
/*      */       }
/* 1316 */       switch (i2) { case 1:
/* 1319 */         return new Byte(paramTdsInputStream.readByte());
/*      */       case 2:
/* 1321 */         return new Short(paramTdsInputStream.readShort());
/*      */       case 4:
/* 1323 */         return new Integer(paramTdsInputStream.readInt());
/*      */       case 8:
/* 1325 */         return new Long(paramTdsInputStream.readLong());
/*      */       case 3:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7: } throw new SrvTypeException("Unmappable TDS data INTN type received: length " + i2);
/*      */     case 59:
/* 1332 */       this._dataLengths[this._columnNo] = 4;
/* 1333 */       return readFLTN(paramTdsInputStream, 4);
/*      */     case 62:
/* 1336 */       this._dataLengths[this._columnNo] = 8;
/* 1337 */       return readFLTN(paramTdsInputStream, 8);
/*      */     case 109:
/* 1340 */       k = paramTdsInputStream.readUnsignedByte();
/* 1341 */       this._dataLengths[this._columnNo] = k;
/* 1342 */       return readFLTN(paramTdsInputStream, k);
/*      */     case 106:
/*      */     case 108:
/* 1346 */       k = paramTdsInputStream.readUnsignedByte();
/* 1347 */       this._dataLengths[this._columnNo] = k;
/* 1348 */       if (k == 0)
/*      */       {
/* 1350 */         return null;
/*      */       }
/* 1352 */       arrayOfByte1 = new byte[k];
/* 1353 */       paramTdsInputStream.read(arrayOfByte1);
/* 1354 */       paramDataFormat._precision = TdsNumeric.NUME_MAXPREC;
/* 1355 */       return TdsNumeric.numericValue(arrayOfByte1, paramDataFormat._precision, paramDataFormat._scale);
/*      */     case 58:
/*      */     case 61:
/* 1360 */       this._dataLengths[this._columnNo] = paramDataFormat._length;
/* 1361 */       return readDATETIMN(paramTdsInputStream, paramDataFormat._length);
/*      */     case 187:
/*      */     case 188:
/* 1365 */       k = paramTdsInputStream.readUnsignedByte();
/* 1366 */       this._dataLengths[this._columnNo] = k;
/* 1367 */       return readBIGDATETIMN(paramTdsInputStream, k, paramDataFormat._datatype);
/*      */     case 111:
/* 1370 */       k = paramTdsInputStream.readUnsignedByte();
/* 1371 */       this._dataLengths[this._columnNo] = k;
/* 1372 */       return readDATETIMN(paramTdsInputStream, k);
/*      */     case 49:
/* 1375 */       this._dataLengths[this._columnNo] = paramDataFormat._length;
/* 1376 */       return readDATEN(paramTdsInputStream, paramDataFormat._length);
/*      */     case 123:
/* 1379 */       k = paramTdsInputStream.readUnsignedByte();
/* 1380 */       this._dataLengths[this._columnNo] = k;
/* 1381 */       return readDATEN(paramTdsInputStream, k);
/*      */     case 51:
/* 1384 */       this._dataLengths[this._columnNo] = paramDataFormat._length;
/* 1385 */       return readTIMEN(paramTdsInputStream, paramDataFormat._length);
/*      */     case 147:
/* 1388 */       k = paramTdsInputStream.readUnsignedByte();
/* 1389 */       this._dataLengths[this._columnNo] = k;
/* 1390 */       return readTIMEN(paramTdsInputStream, k);
/*      */     case 36:
/* 1393 */       return readBLOB(paramTdsInputStream, paramDataFormat._blobType);
/*      */     case 35:
/*      */     case 174:
/* 1398 */       i3 = paramTdsInputStream.readUnsignedByte();
/* 1399 */       if (i3 == 0)
/*      */       {
/* 1401 */         return null;
/*      */       }
/*      */ 
/* 1408 */       arrayOfByte2 = new byte[i3 + 8];
/* 1409 */       paramTdsInputStream.read(arrayOfByte2, 0, i3 + 8);
/* 1410 */       i4 = paramTdsInputStream.readInt();
/*      */ 
/* 1414 */       if (i4 == 0)
/*      */       {
/* 1416 */         return null;
/*      */       }
/* 1418 */       this._dataLengths[this._columnNo] = i4;
/*      */ 
/* 1420 */       String str = null;
/* 1421 */       if (paramDataFormat._datatype == 35)
/*      */       {
/* 1423 */         str = paramTdsInputStream.readString(i4);
/*      */       }
/*      */       else
/*      */       {
/* 1427 */         str = paramTdsInputStream.readUnicodeString(i4);
/*      */       }
/* 1429 */       return str;
/*      */     case 34:
/* 1431 */       i3 = paramTdsInputStream.readUnsignedByte();
/* 1432 */       if (i3 == 0)
/*      */       {
/* 1434 */         return null;
/*      */       }
/*      */ 
/* 1441 */       arrayOfByte2 = new byte[i3 + 8];
/* 1442 */       paramTdsInputStream.read(arrayOfByte2, 0, i3 + 8);
/* 1443 */       i4 = paramTdsInputStream.readInt();
/*      */ 
/* 1447 */       if (i4 == 0)
/*      */       {
/* 1449 */         return null;
/*      */       }
/* 1451 */       this._dataLengths[this._columnNo] = i4;
/*      */ 
/* 1453 */       arrayOfByte1 = new byte[i4];
/* 1454 */       while (i4 > 0)
/*      */       {
/* 1456 */         int i5 = paramTdsInputStream.read(arrayOfByte1, arrayOfByte1.length - i4, i4);
/*      */ 
/* 1458 */         if (i5 == -1)
/*      */         {
/* 1461 */           throw new IOException("Unexpected end of data from client");
/*      */         }
/*      */ 
/* 1464 */         i4 -= i5;
/*      */       }
/* 1466 */       return arrayOfByte1;
/*      */     case 60:
/*      */     case 122:
/* 1470 */       this._dataLengths[this._columnNo] = paramDataFormat._length;
/* 1471 */       return readMONEYN(paramTdsInputStream, paramDataFormat._length);
/*      */     case 110:
/* 1474 */       k = paramTdsInputStream.readUnsignedByte();
/* 1475 */       this._dataLengths[this._columnNo] = k;
/* 1476 */       return readMONEYN(paramTdsInputStream, k);
/*      */     }
/*      */ 
/* 1479 */     throw new SrvTypeException("Unmappable TDS data type received: 0x" + Integer.toHexString(paramDataFormat._datatype));
/*      */   }
/*      */ 
/*      */   protected String unicodeArrayToString(TdsInputStream paramTdsInputStream, byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/* 1490 */     return paramTdsInputStream.convertUnicodeBytesToString(paramArrayOfByte);
/*      */   }
/*      */ 
/*      */   private TdsDateTime readBIGDATETIMN(TdsInputStream paramTdsInputStream, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/* 1497 */     switch (paramInt1)
/*      */     {
/*      */     case 0:
/* 1500 */       return null;
/*      */     case 8:
/* 1502 */       BigDecimal localBigDecimal = paramTdsInputStream.readUnsignedLongAsBigDecimal();
/* 1503 */       if (paramInt2 == 187)
/*      */       {
/* 1505 */         paramInt2 = 5;
/*      */       }
/* 1507 */       else if (paramInt2 == 188)
/*      */       {
/* 1509 */         paramInt2 = 6;
/*      */       }
/* 1511 */       return new TdsDateTime(localBigDecimal, paramInt2);
/*      */     }
/* 1513 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/* 1517 */     return null;
/*      */   }
/*      */ 
/*      */   private TdsDateTime readDATETIMN(TdsInputStream paramTdsInputStream, int paramInt)
/*      */     throws IOException
/*      */   {
/* 1536 */     switch (paramInt)
/*      */     {
/*      */     case 0:
/* 1539 */       return null;
/*      */     case 4:
/* 1542 */       int i = paramTdsInputStream.readUnsignedShort();
/* 1543 */       int j = paramTdsInputStream.readUnsignedShort();
/* 1544 */       return new TdsDateTime(i, j, 2);
/*      */     case 8:
/* 1548 */       int k = paramTdsInputStream.readInt();
/* 1549 */       int l = paramTdsInputStream.readInt();
/* 1550 */       return new TdsDateTime(k, l, 1);
/*      */     }
/*      */ 
/* 1554 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/* 1558 */     return null;
/*      */   }
/*      */ 
/*      */   private TdsDateTime readDATEN(TdsInputStream paramTdsInputStream, int paramInt)
/*      */     throws IOException
/*      */   {
/* 1570 */     switch (paramInt)
/*      */     {
/*      */     case 0:
/* 1573 */       return null;
/*      */     case 4:
/* 1575 */       int i = paramTdsInputStream.readInt();
/* 1576 */       return new TdsDateTime(i, 0, 3);
/*      */     }
/* 1578 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/* 1581 */     return null;
/*      */   }
/*      */ 
/*      */   private TdsDateTime readTIMEN(TdsInputStream paramTdsInputStream, int paramInt)
/*      */     throws IOException
/*      */   {
/* 1593 */     switch (paramInt)
/*      */     {
/*      */     case 0:
/* 1596 */       return null;
/*      */     case 4:
/* 1598 */       int i = paramTdsInputStream.readInt();
/* 1599 */       return new TdsDateTime(0, i, 4);
/*      */     }
/* 1601 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/* 1604 */     return null;
/*      */   }
/*      */ 
/*      */   private BigDecimal readMONEYN(TdsInputStream paramTdsInputStream, int paramInt)
/*      */     throws IOException
/*      */   {
/* 1615 */     switch (paramInt)
/*      */     {
/*      */     case 0:
/* 1618 */       return null;
/*      */     case 4:
/* 1621 */       int i = paramTdsInputStream.readInt();
/* 1622 */       return new BigDecimal(BigInteger.valueOf(i), 4);
/*      */     case 8:
/* 1625 */       long l = paramTdsInputStream.readLong();
/* 1626 */       return new BigDecimal(BigInteger.valueOf(l), 4);
/*      */     }
/*      */ 
/* 1629 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/* 1632 */     return null;
/*      */   }
/*      */ 
/*      */   private Number readFLTN(TdsInputStream paramTdsInputStream, int paramInt)
/*      */     throws IOException
/*      */   {
/* 1641 */     switch (paramInt)
/*      */     {
/*      */     case 0:
/* 1644 */       return null;
/*      */     case 4:
/* 1647 */       return new Float(paramTdsInputStream.readFloat());
/*      */     case 8:
/* 1650 */       return new Double(paramTdsInputStream.readDouble());
/*      */     }
/*      */ 
/* 1653 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/* 1656 */     return null;
/*      */   }
/*      */ 
/*      */   private Object readBLOB(TdsInputStream paramTdsInputStream, int paramInt)
/*      */     throws IOException, SrvTypeException
/*      */   {
/* 1667 */     Object localObject1 = null;
/* 1668 */     int i = paramTdsInputStream.readUnsignedByte();
/* 1669 */     this._serializationTypes[this._columnNo] = i;
/* 1670 */     int j = paramTdsInputStream.readShort();
/* 1671 */     this._dataLengths[this._columnNo] = 0;
/*      */     Object localObject2;
/*      */     InputStreamReader localInputStreamReader;
/* 1676 */     switch (paramInt)
/*      */     {
/*      */     case 1:
/* 1679 */       if (j > 0)
/*      */       {
/* 1681 */         localObject2 = paramTdsInputStream.readString(j);
/*      */       }
/*      */ 
/* 1688 */       localObject2 = new SrvBlobInputStream(paramTdsInputStream);
/*      */       try
/*      */       {
/* 1691 */         ObjectInputStream localObjectInputStream = new ObjectInputStream((InputStream)localObject2);
/*      */         try
/*      */         {
/* 1694 */           localObject1 = localObjectInputStream.readObject();
/*      */         }
/*      */         catch (ClassNotFoundException localClassNotFoundException)
/*      */         {
/*      */         }
/*      */         finally
/*      */         {
/* 1703 */           localObjectInputStream.close();
/*      */         }
/*      */ 
/* 1707 */         this._dataLengths[this._columnNo] = ((localObject1 == null) ? 0 : localObject1.toString().length());
/*      */       }
/*      */       catch (EOFException localEOFException)
/*      */       {
/*      */       }
/*      */       finally
/*      */       {
/* 1717 */         ((SrvBlobInputStream)localObject2).close();
/*      */       }
/*      */     case 3:
/* 1724 */       if (j > 0)
/*      */       {
/* 1726 */         paramTdsInputStream.skip(j);
/*      */       }
/*      */ 
/* 1731 */       localObject2 = new SrvBlobInputStream(paramTdsInputStream);
/* 1732 */       localInputStreamReader = null;
/*      */ 
/* 1740 */       if (this._columnNo < this._lastColumnIndex)
/*      */       {
/* 1743 */         localInputStreamReader = new InputStreamReader(cacheBlob((InputStream)localObject2), paramTdsInputStream.getCharset());
/*      */       }
/*      */       else
/*      */       {
/* 1748 */         localInputStreamReader = new InputStreamReader((InputStream)localObject2, paramTdsInputStream.getCharset());
/*      */       }
/* 1750 */       localObject1 = localInputStreamReader;
/* 1751 */       break;
/*      */     case 5:
/* 1762 */       if (j > 0)
/*      */       {
/* 1764 */         paramTdsInputStream.skip(j);
/*      */       }
/*      */ 
/* 1769 */       localObject2 = new SrvBlobInputStream(paramTdsInputStream);
/* 1770 */       localInputStreamReader = null;
/*      */ 
/* 1777 */       String str = null;
/* 1778 */       if (!paramTdsInputStream.getBigEndian())
/*      */       {
/* 1780 */         str = "UnicodeLittleUnmarked";
/*      */       }
/*      */       else
/*      */       {
/* 1784 */         str = "UnicodeBigUnmarked";
/*      */       }
/* 1786 */       if (this._columnNo < this._lastColumnIndex)
/*      */       {
/* 1789 */         localInputStreamReader = new InputStreamReader(cacheBlob((InputStream)localObject2), str);
/*      */       }
/*      */       else
/*      */       {
/* 1794 */         localInputStreamReader = new InputStreamReader((InputStream)localObject2, str);
/*      */       }
/* 1796 */       localObject1 = localInputStreamReader;
/* 1797 */       break;
/*      */     case 4:
/* 1803 */       if (j > 0)
/*      */       {
/* 1805 */         paramTdsInputStream.skip(j);
/*      */       }
/*      */ 
/* 1810 */       if (this._columnNo < this._lastColumnIndex)
/*      */       {
/* 1813 */         localObject1 = cacheBlob(new SrvBlobInputStream(paramTdsInputStream)); break label575:
/*      */       }
/*      */ 
/* 1817 */       localObject1 = new SrvBlobInputStream(paramTdsInputStream);
/*      */ 
/* 1819 */       break;
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/* 1826 */       if (j > 0)
/*      */       {
/* 1828 */         paramTdsInputStream.skip(j);
/*      */       }
/*      */ 
/* 1833 */       this._lobLengths[this._columnNo] = paramTdsInputStream.readLong();
/* 1834 */       this._locatorLengths[this._columnNo] = paramTdsInputStream.readShort();
/* 1835 */       byte[] arrayOfByte = new byte[this._locatorLengths[this._columnNo]];
/* 1836 */       paramTdsInputStream.read(arrayOfByte, 0, this._locatorLengths[this._columnNo]);
/* 1837 */       this._locators[this._columnNo] = arrayOfByte;
/* 1838 */       if (this._columnNo < this._lastColumnIndex)
/*      */       {
/* 1841 */         localObject1 = cacheBlob(new SrvBlobInputStream(paramTdsInputStream)); break label575:
/*      */       }
/*      */ 
/* 1845 */       localObject1 = new SrvBlobInputStream(paramTdsInputStream);
/*      */     case 2:
/*      */     }
/*      */ 
/* 1852 */     label575: return localObject1;
/*      */   }
/*      */ 
/*      */   private InputStream cacheBlob(InputStream paramInputStream)
/*      */     throws IOException
/*      */   {
/* 1864 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(2048);
/*      */ 
/* 1866 */     while ((i = paramInputStream.read()) != -1)
/*      */     {
/*      */       int i;
/* 1868 */       localByteArrayOutputStream.write(i);
/*      */     }
/* 1870 */     return new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
/*      */   }
/*      */ 
/*      */   private byte[] cacheBlobInByteArray(InputStream paramInputStream)
/*      */     throws IOException
/*      */   {
/* 1882 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(2048);
/*      */ 
/* 1884 */     while ((i = paramInputStream.read()) != -1)
/*      */     {
/*      */       int i;
/* 1886 */       localByteArrayOutputStream.write(i);
/*      */     }
/* 1888 */     return localByteArrayOutputStream.toByteArray();
/*      */   }
/*      */ 
/*      */   public long[] getLobLengths()
/*      */   {
/* 1893 */     return this._lobLengths;
/*      */   }
/*      */ 
/*      */   public int[] getLocatorLengths()
/*      */   {
/* 1898 */     return this._locatorLengths;
/*      */   }
/*      */ 
/*      */   public Object[] getLocators()
/*      */   {
/* 1903 */     return this._locators;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvJavaTypeFormatter
 * JD-Core Version:    0.5.4
 */