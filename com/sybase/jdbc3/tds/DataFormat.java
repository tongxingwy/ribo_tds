/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.sql.SQLException;
/*      */ 
/*      */ public class DataFormat extends Token
/*      */ {
/*      */   public static final int MAX_DATAFMT_LENGTH = 136;
/*      */   protected String _name;
/*      */   protected int _status;
/*      */   protected int _usertype;
/*      */   protected int _datatype;
/*      */   protected int _length;
/*      */   protected int _precision;
/*      */   protected int _scale;
/*      */   protected String _locale;
/*      */   protected int _localeLen;
/*      */   protected String _tableName;
/*   64 */   protected String _className = null;
/*      */   protected int _blobType;
/*      */   protected int _classIdLen;
/*      */   protected boolean _colStatusBytePresent;
/*      */   protected byte[] _nameBytes;
/*      */   protected int _nameLen;
/*      */   protected TdsInputStream _tdsIn;
/*      */   protected static final int FIXED_LENGTH_PART = 8;
/*      */ 
/*      */   protected DataFormat()
/*      */   {
/*      */   }
/*      */ 
/*      */   public DataFormat(TdsInputStream paramTdsInputStream)
/*      */     throws IOException
/*      */   {
/*   95 */     newDataFormat(paramTdsInputStream, true);
/*      */   }
/*      */ 
/*      */   public DataFormat(TdsInputStream paramTdsInputStream, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  108 */     newDataFormat(paramTdsInputStream, paramBoolean);
/*      */   }
/*      */ 
/*      */   protected void newDataFormat(TdsInputStream paramTdsInputStream, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  119 */     this._tdsIn = paramTdsInputStream;
/*      */     try
/*      */     {
/*  122 */       readMetaInfo(paramTdsInputStream);
/*  123 */       readStatus(paramTdsInputStream);
/*  124 */       this._colStatusBytePresent = ((this._status & 0x8) != 0);
/*  125 */       this._usertype = paramTdsInputStream.readInt();
/*  126 */       this._datatype = paramTdsInputStream.readUnsignedByte();
/*  127 */       this._precision = 0;
/*  128 */       this._scale = 0;
/*  129 */       this._length = 0;
/*      */ 
/*  133 */       switch (this._datatype)
/*      */       {
/*      */       case 48:
/*      */       case 50:
/*  139 */         this._length = 1;
/*  140 */         break;
/*      */       case 52:
/*      */       case 65:
/*  144 */         this._length = 2;
/*  145 */         break;
/*      */       case 49:
/*      */       case 51:
/*      */       case 56:
/*      */       case 58:
/*      */       case 59:
/*      */       case 66:
/*      */       case 122:
/*  154 */         this._length = 4;
/*  155 */         break;
/*      */       case 60:
/*      */       case 61:
/*      */       case 62:
/*      */       case 191:
/*  161 */         this._length = 8;
/*  162 */         break;
/*      */       case 67:
/*  164 */         this._length = 8;
/*  165 */         break;
/*      */       case 37:
/*      */       case 38:
/*      */       case 39:
/*      */       case 45:
/*      */       case 47:
/*      */       case 68:
/*      */       case 103:
/*      */       case 104:
/*      */       case 109:
/*      */       case 110:
/*      */       case 111:
/*      */       case 123:
/*      */       case 147:
/*  180 */         this._length = paramTdsInputStream.readUnsignedByte();
/*  181 */         break;
/*      */       case 187:
/*      */       case 188:
/*  184 */         this._length = paramTdsInputStream.readUnsignedByte();
/*  185 */         this._scale = paramTdsInputStream.readUnsignedByte();
/*  186 */         break;
/*      */       case 106:
/*      */       case 108:
/*  190 */         this._length = paramTdsInputStream.readUnsignedByte();
/*  191 */         this._precision = paramTdsInputStream.readUnsignedByte();
/*  192 */         this._scale = paramTdsInputStream.readUnsignedByte();
/*  193 */         break;
/*      */       case 175:
/*      */       case 225:
/*  198 */         this._length = paramTdsInputStream.readInt();
/*  199 */         break;
/*      */       case 34:
/*      */       case 35:
/*      */       case 174:
/*  205 */         this._length = paramTdsInputStream.readInt();
/*  206 */         this._tableName = paramTdsInputStream.readString(paramTdsInputStream.readUnsignedShort());
/*  207 */         break;
/*      */       case 36:
/*  210 */         this._blobType = paramTdsInputStream.readUnsignedByte();
/*  211 */         this._classIdLen = paramTdsInputStream.readShort();
/*      */ 
/*  214 */         switch (this._blobType)
/*      */         {
/*      */         case 1:
/*  217 */           this._className = paramTdsInputStream.readString(this._classIdLen);
/*      */ 
/*  220 */           break;
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 8:
/*  229 */           paramTdsInputStream.skip(this._classIdLen);
/*  230 */           break;
/*      */         case 2:
/*      */         default:
/*  233 */           paramTdsInputStream.skip(this._classIdLen);
/*      */ 
/*  236 */           throw new IOException("JZ033");
/*      */         }
/*      */ 
/*      */       case 31:
/*      */       case 255:
/*      */       }
/*      */ 
/*  251 */       if (this._className == null)
/*      */       {
/*      */         try
/*      */         {
/*  255 */           this._className = TdsJdbcInputStream.getObjectClassName(this._datatype, this._usertype, this._blobType, this._length);
/*      */         }
/*      */         catch (SQLException localSQLException)
/*      */         {
/*  260 */           throw new IOException(localSQLException.getMessage());
/*      */         }
/*      */       }
/*      */ 
/*  264 */       if (paramBoolean)
/*      */       {
/*  266 */         int i = paramTdsInputStream.readUnsignedByte();
/*  267 */         if (i > 0)
/*      */         {
/*  269 */           this._locale = paramTdsInputStream.readString(i);
/*      */         }
/*      */         else
/*      */         {
/*  273 */           this._locale = "";
/*      */         }
/*      */       }
/*      */ 
/*  277 */       switch (this._datatype)
/*      */       {
/*      */       case 60:
/*      */       case 110:
/*      */       case 122:
/*  282 */         this._scale = 4;
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  290 */       readSQE(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public DataFormat(TdsParam paramTdsParam, TdsOutputStream paramTdsOutputStream, byte paramByte)
/*      */     throws IOException
/*      */   {
/*  310 */     this._nameLen = 0;
/*  311 */     this._name = paramTdsParam._name;
/*  312 */     if (this._name != null)
/*      */     {
/*  314 */       this._nameLen = this._name.length();
/*      */     }
/*      */ 
/*  317 */     this._status = ((paramTdsParam._inValue == null) ? 32 : 0);
/*  318 */     this._status |= ((paramTdsParam._regType != -999) ? 1 : 0);
/*  319 */     if ((paramTdsParam._tdos._tds.serverAcceptsColumnStatusByte()) && (paramTdsParam._sqlType != -998))
/*      */     {
/*  323 */       this._status |= 8;
/*  324 */       this._colStatusBytePresent = true;
/*      */     }
/*  326 */     this._usertype = 0;
/*  327 */     this._locale = null;
/*  328 */     int[] arrayOfInt = new int[4];
/*  329 */     ((TdsDataOutputStream)paramTdsOutputStream).dataTypeInfo(paramTdsParam, arrayOfInt);
/*  330 */     this._datatype = arrayOfInt[0];
/*  331 */     switch (this._datatype)
/*      */     {
/*      */     case 9217:
/*  335 */       this._datatype = 36;
/*  336 */       this._blobType = 1;
/*  337 */       if (paramTdsParam._inValue != null)
/*      */       {
/*  341 */         if (paramTdsParam._outParamClassName != null)
/*      */         {
/*  343 */           this._className = paramTdsParam._outParamClassName;
/*      */         }
/*      */         else
/*      */         {
/*  347 */           this._className = paramTdsParam._inValue.getClass().getName();
/*      */         }
/*      */ 
/*      */       }
/*  354 */       else if (paramTdsParam._outParamClassName != null)
/*      */       {
/*  356 */         this._className = paramTdsParam._outParamClassName;
/*      */       }
/*      */       else
/*      */       {
/*  361 */         this._className = "";
/*      */       }
/*      */ 
/*  366 */       this._classIdLen = this._className.length();
/*  367 */       break;
/*      */     case 9220:
/*  369 */       this._datatype = 36;
/*  370 */       this._blobType = 4;
/*  371 */       if ((paramByte == 0) || (paramByte == 3))
/*      */       {
/*  373 */         this._usertype = 20; } break;
/*      */     case 9219:
/*  377 */       this._datatype = 36;
/*  378 */       this._blobType = 3;
/*  379 */       if ((paramByte == 0) || (paramByte == 3))
/*      */       {
/*  381 */         this._usertype = 19; } break;
/*      */     case 9221:
/*  385 */       this._datatype = 36;
/*  386 */       this._blobType = 5;
/*  387 */       if ((paramByte == 0) || (paramByte == 3))
/*      */       {
/*  389 */         this._usertype = 36; } break;
/*      */     case 2004:
/*  393 */       this._blobType = 7;
/*  394 */       this._datatype = 36;
/*  395 */       break;
/*      */     case 2005:
/*  397 */       this._blobType = 6;
/*  398 */       this._datatype = 36;
/*  399 */       break;
/*      */     case 225:
/*  406 */       if (paramTdsParam._parameterHoldsUnicharData)
/*      */       {
/*  408 */         this._usertype = 35;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  416 */     this._length = maxLength(paramTdsParam, this._datatype, arrayOfInt[1]);
/*      */ 
/*  418 */     this._precision = arrayOfInt[2];
/*  419 */     this._scale = arrayOfInt[3];
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  424 */     if (this._name != null)
/*      */     {
/*  426 */       return this._name;
/*      */     }
/*  428 */     if (this._nameLen == 0)
/*      */     {
/*  430 */       this._name = "";
/*  431 */       return this._name;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  438 */       this._name = this._tdsIn.convertBytesToString(this._nameBytes);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */ 
/*  444 */     return this._name;
/*      */   }
/*      */ 
/*      */   public int getStatus()
/*      */   {
/*  450 */     return this._status;
/*      */   }
/*      */ 
/*      */   public int getDataType()
/*      */   {
/*  455 */     return this._datatype;
/*      */   }
/*      */ 
/*      */   public int getDataTypeLength()
/*      */   {
/*  460 */     return this._length;
/*      */   }
/*      */ 
/*      */   public int getUserType()
/*      */   {
/*  465 */     return this._usertype;
/*      */   }
/*      */ 
/*      */   public int getBlobType()
/*      */   {
/*  470 */     return this._blobType;
/*      */   }
/*      */ 
/*      */   public int length()
/*      */   {
/*  481 */     int i = getFixedLengthPart() + ((this._name == null) ? 0 : this._name.length()) + lengthSize(this._datatype) + ((this._locale == null) ? 0 : this._locale.length());
/*      */ 
/*  486 */     switch (this._datatype)
/*      */     {
/*      */     case 187:
/*      */     case 188:
/*  490 */       ++i;
/*  491 */       break;
/*      */     case 106:
/*      */     case 108:
/*  494 */       i += 2;
/*  495 */       break;
/*      */     case 36:
/*      */     case 9217:
/*  498 */       switch (this._blobType)
/*      */       {
/*      */       case 1:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  507 */         i += 3 + this._classIdLen;
/*      */ 
/*  509 */         break label238:
/*      */       case 2:
/*      */       }
/*  512 */       break;
/*      */     case 34:
/*      */     case 35:
/*      */     case 174:
/*  521 */       i += 2 + ((this._tableName == null) ? 0 : this._tableName.length());
/*      */     }
/*      */ 
/*  524 */     label238: return i;
/*      */   }
/*      */ 
/*      */   protected int getFixedLengthPart()
/*      */   {
/*  529 */     return 8;
/*      */   }
/*      */ 
/*      */   protected static int lengthSize(int paramInt)
/*      */   {
/*  537 */     switch (paramInt)
/*      */     {
/*      */     case 36:
/*      */     case 48:
/*      */     case 49:
/*      */     case 50:
/*      */     case 51:
/*      */     case 52:
/*      */     case 56:
/*      */     case 58:
/*      */     case 59:
/*      */     case 60:
/*      */     case 61:
/*      */     case 62:
/*      */     case 65:
/*      */     case 66:
/*      */     case 67:
/*      */     case 122:
/*      */     case 191:
/*      */     case 9219:
/*      */     case 9220:
/*      */     case 9221:
/*  560 */       return 0;
/*      */     case 37:
/*      */     case 38:
/*      */     case 39:
/*      */     case 45:
/*      */     case 47:
/*      */     case 68:
/*      */     case 103:
/*      */     case 104:
/*      */     case 106:
/*      */     case 108:
/*      */     case 109:
/*      */     case 110:
/*      */     case 111:
/*      */     case 123:
/*      */     case 147:
/*      */     case 187:
/*      */     case 188:
/*  580 */       return 1;
/*      */     case 34:
/*      */     case 35:
/*      */     case 174:
/*      */     case 175:
/*      */     case 225:
/*  588 */       return 4;
/*      */     case 31:
/*      */     case 255:
/*      */     }
/*      */ 
/*  602 */     return -1;
/*      */   }
/*      */ 
/*      */   public void send(TdsOutputStream paramTdsOutputStream)
/*      */     throws IOException
/*      */   {
/*  609 */     sendMetaInfo(paramTdsOutputStream);
/*  610 */     sendStatus(paramTdsOutputStream);
/*      */ 
/*  612 */     paramTdsOutputStream.writeInt(this._usertype);
/*  613 */     switch (this._datatype)
/*      */     {
/*      */     case 9219:
/*  616 */       this._blobType = 3;
/*  617 */       paramTdsOutputStream.writeByte(36);
/*  618 */       break;
/*      */     case 9221:
/*  620 */       this._blobType = 5;
/*  621 */       paramTdsOutputStream.writeByte(36);
/*  622 */       break;
/*      */     case 9220:
/*  624 */       this._blobType = 4;
/*  625 */       paramTdsOutputStream.writeByte(36);
/*  626 */       break;
/*      */     default:
/*  628 */       paramTdsOutputStream.writeByte(this._datatype);
/*      */     }
/*      */     byte[] arrayOfByte;
/*  631 */     switch (this._datatype)
/*      */     {
/*      */     case 48:
/*      */     case 49:
/*      */     case 50:
/*      */     case 51:
/*      */     case 52:
/*      */     case 56:
/*      */     case 58:
/*      */     case 59:
/*      */     case 60:
/*      */     case 61:
/*      */     case 62:
/*      */     case 65:
/*      */     case 66:
/*      */     case 67:
/*      */     case 122:
/*      */     case 191:
/*  650 */       break;
/*      */     case 37:
/*      */     case 38:
/*      */     case 39:
/*      */     case 45:
/*      */     case 47:
/*      */     case 68:
/*      */     case 103:
/*      */     case 104:
/*      */     case 109:
/*      */     case 110:
/*      */     case 111:
/*      */     case 123:
/*      */     case 147:
/*  666 */       paramTdsOutputStream.writeByte(this._length);
/*  667 */       break;
/*      */     case 187:
/*      */     case 188:
/*  670 */       paramTdsOutputStream.writeByte(this._length);
/*  671 */       paramTdsOutputStream.writeByte(this._scale);
/*  672 */       break;
/*      */     case 106:
/*      */     case 108:
/*  676 */       paramTdsOutputStream.writeByte(this._length);
/*  677 */       paramTdsOutputStream.writeByte(this._precision);
/*  678 */       paramTdsOutputStream.writeByte(this._scale);
/*  679 */       break;
/*      */     case 175:
/*      */     case 225:
/*  684 */       paramTdsOutputStream.writeInt(this._length);
/*  685 */       break;
/*      */     case 36:
/*      */     case 9219:
/*      */     case 9220:
/*      */     case 9221:
/*  690 */       paramTdsOutputStream.writeByte(this._blobType);
/*  691 */       switch (this._blobType)
/*      */       {
/*      */       case 1:
/*  694 */         arrayOfByte = paramTdsOutputStream.stringToByte(this._className);
/*  695 */         if (arrayOfByte == null)
/*      */         {
/*  697 */           paramTdsOutputStream.writeShort(0);
/*      */         }
/*      */         else
/*      */         {
/*  701 */           paramTdsOutputStream.writeShort(arrayOfByte.length);
/*  702 */           paramTdsOutputStream.write(arrayOfByte);
/*      */         }
/*      */ 
/*  704 */         break;
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  711 */         paramTdsOutputStream.writeShort(0);
/*  712 */         break label672:
/*      */       case 2:
/*      */       }
/*      */ 
/*  715 */       break;
/*      */     case 34:
/*      */     case 35:
/*      */     case 174:
/*  722 */       paramTdsOutputStream.writeInt(this._length);
/*      */ 
/*  724 */       arrayOfByte = paramTdsOutputStream.stringToByte(this._tableName);
/*  725 */       paramTdsOutputStream.writeShort(arrayOfByte.length);
/*  726 */       paramTdsOutputStream.write(arrayOfByte);
/*  727 */       break;
/*      */     case 31:
/*      */     case 255:
/*      */     }
/*      */ 
/*  740 */     if (this._locale == null)
/*      */     {
/*  742 */       label672: paramTdsOutputStream.writeByte(0);
/*      */     }
/*      */     else
/*      */     {
/*  748 */       arrayOfByte = paramTdsOutputStream.stringToByte(this._locale);
/*  749 */       int i = (arrayOfByte.length > 255) ? 255 : arrayOfByte.length;
/*      */ 
/*  751 */       paramTdsOutputStream.writeByte(i);
/*  752 */       paramTdsOutputStream.write(arrayOfByte, 0, i);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static int maxLength(TdsParam paramTdsParam, int paramInt1, int paramInt2)
/*      */   {
/*  759 */     switch (paramInt1)
/*      */     {
/*  805 */     case 48:
/*      */     case 50:
/*  764 */       return 1;
/*      */     case 52:
/*      */     case 65:
/*  767 */       return 2;
/*      */     case 49:
/*      */     case 51:
/*      */     case 56:
/*      */     case 58:
/*      */     case 59:
/*      */     case 66:
/*      */     case 122:
/*  775 */       return 4;
/*      */     case 60:
/*      */     case 61:
/*      */     case 62:
/*      */     case 67:
/*      */     case 187:
/*      */     case 188:
/*      */     case 191:
/*  783 */       return 8;
/*      */     case 123:
/*      */     case 147:
/*  789 */       return (paramInt2 > 0) ? paramInt2 : 4;
/*      */     case 38:
/*      */     case 68:
/*  805 */       if (paramInt2 > 0) return paramInt2;
/*  806 */       if (paramTdsParam._sqlType == -5)
/*      */       {
/*  808 */         return 8;
/*      */       }
/*  810 */       if (paramTdsParam._sqlType == 5)
/*      */       {
/*  812 */         return 2;
/*      */       }
/*      */ 
/*  816 */       return 4;
/*      */     case 110:
/*      */     case 111:
/*  820 */       return (paramInt2 > 0) ? paramInt2 : 8;
/*      */     case 109:
/*  822 */       if (paramInt2 > 0) return paramInt2;
/*  823 */       if (paramTdsParam._sqlType == 7)
/*      */       {
/*  825 */         return 4;
/*      */       }
/*      */ 
/*  829 */       return 8;
/*      */     case 37:
/*      */     case 39:
/*      */     case 45:
/*      */     case 47:
/*      */     case 103:
/*      */     case 104:
/*  840 */       return 255;
/*      */     case 106:
/*      */     case 108:
/*  843 */       return 54;
/*      */     case 34:
/*      */     case 35:
/*      */     case 36:
/*      */     case 174:
/*      */     case 175:
/*      */     case 225:
/*      */     case 9219:
/*      */     case 9220:
/*      */     case 9221:
/*  854 */       return 2147483647;
/*      */     case 31:
/*      */     case 255:
/*      */     }
/*      */ 
/*  866 */     return 0;
/*      */   }
/*      */ 
/*      */   protected void readMetaInfo(TdsInputStream paramTdsInputStream)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  881 */       this._nameLen = paramTdsInputStream.readUnsignedByte();
/*  882 */       if (this._nameLen > 0)
/*      */       {
/*  884 */         this._nameBytes = new byte[this._nameLen];
/*  885 */         paramTdsInputStream.read(this._nameBytes, 0, this._nameLen);
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  890 */       readSQE(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void sendMetaInfo(TdsOutputStream paramTdsOutputStream)
/*      */     throws IOException
/*      */   {
/*  901 */     byte[] arrayOfByte = paramTdsOutputStream.stringToByte(this._name);
/*  902 */     if (arrayOfByte == null)
/*      */     {
/*  904 */       paramTdsOutputStream.writeByte(0);
/*      */     }
/*      */     else
/*      */     {
/*  908 */       paramTdsOutputStream.writeByte(arrayOfByte.length);
/*  909 */       paramTdsOutputStream.write(arrayOfByte);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void readStatus(TdsInputStream paramTdsInputStream)
/*      */     throws IOException
/*      */   {
/*  925 */     this._status = paramTdsInputStream.readUnsignedByte();
/*      */   }
/*      */ 
/*      */   protected void sendStatus(TdsOutputStream paramTdsOutputStream)
/*      */     throws IOException
/*      */   {
/*  935 */     paramTdsOutputStream.writeByte(this._status);
/*      */   }
/*      */ 
/*      */   public static String getDataTypeString(int paramInt)
/*      */   {
/*  944 */     switch (paramInt)
/*      */     {
/*      */     case 34:
/*  946 */       return "IMAGE";
/*      */     case 35:
/*  947 */       return "TEXT";
/*      */     case 174:
/*  948 */       return "UNITEXT";
/*      */     case 37:
/*  949 */       return "VARBINARY";
/*      */     case 68:
/*  950 */       return "UINTN";
/*      */     case 38:
/*  951 */       return "INTN";
/*      */     case 45:
/*  952 */       return "BINARY";
/*      */     case 47:
/*  953 */       return "CHAR";
/*      */     case 50:
/*  954 */       return "BIT";
/*      */     case 61:
/*  955 */       return "DATETIM";
/*      */     case 58:
/*  956 */       return "SHORTDATE";
/*      */     case 111:
/*  957 */       return "DATETIMN";
/*      */     case 187:
/*  958 */       return "BIGDATETIMEN";
/*      */     case 188:
/*  959 */       return "BIGTIMEN";
/*      */     case 49:
/*  960 */       return "DATE";
/*      */     case 123:
/*  961 */       return "DATEN";
/*      */     case 51:
/*  962 */       return "TIME";
/*      */     case 147:
/*  963 */       return "TIMEN";
/*      */     case 106:
/*  964 */       return "DECN";
/*      */     case 59:
/*  965 */       return "FLT4";
/*      */     case 62:
/*  966 */       return "FLT8";
/*      */     case 109:
/*  967 */       return "FLTN";
/*      */     case 48:
/*  968 */       return "INT1";
/*      */     case 65:
/*  969 */       return "UINT2";
/*      */     case 52:
/*  970 */       return "INT2";
/*      */     case 66:
/*  971 */       return "UINT4";
/*      */     case 56:
/*  972 */       return "INT4";
/*      */     case 67:
/*  973 */       return "UINT8";
/*      */     case 191:
/*  974 */       return "INT8";
/*      */     case 225:
/*  975 */       return "LONGBINARY";
/*      */     case 175:
/*  976 */       return "LONGCHAR";
/*      */     case 60:
/*  977 */       return "MONEY";
/*      */     case 122:
/*  978 */       return "SHORTMONEY";
/*      */     case 110:
/*  979 */       return "MONEYN";
/*      */     case 108:
/*  980 */       return "NUMN";
/*      */     case 103:
/*  981 */       return "SENSITIVITY";
/*      */     case 104:
/*  982 */       return "BOUNDARY";
/*      */     case 39:
/*  983 */       return "VARCHAR";
/*      */     case 36:
/*  984 */       return "BLOB";
/*      */     case 31:
/*  992 */       return "VOID";
/*      */     case 255:
/*  993 */       return "UNKNOWN";
/*      */     }
/*  995 */     return "<unrecognized>";
/*      */   }
/*      */ 
/*      */   protected boolean isUnitype()
/*      */   {
/* 1002 */     return (this._datatype == 174) || ((this._datatype == 225) && (((this._usertype == 35) || (this._usertype == 34)))) || ((this._datatype == 36) && (this._blobType == 5));
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.DataFormat
 * JD-Core Version:    0.5.4
 */