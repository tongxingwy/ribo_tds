/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.jdbc.DateObject;
/*      */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*      */ import com.sybase.jdbc3.jdbc.JdbcDataObject;
/*      */ import com.sybase.jdbc3.jdbc.TextPointer;
/*      */ import com.sybase.jdbc3.utils.Chainable;
/*      */ import com.sybase.jdbcx.CharsetConverter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Calendar;
/*      */ 
/*      */ public abstract class TdsDataObject extends TdsDataInputStream
/*      */   implements JdbcDataObject, Chainable
/*      */ {
/*      */   public static final int UNKNOWN = -1;
/*      */   public static final int UNINITIALIZED = -2;
/*      */   protected TdsProtocolContext _context;
/*      */   protected int _state;
/*      */   protected DataFormat _dataFmt;
/*      */   protected boolean _isNull;
/*   61 */   protected int _dataLength = -2;
/*      */   protected byte _columnStatus;
/*   63 */   protected boolean _holds0LNN = false;
/*      */ 
/*   65 */   protected int _textptrlen = 0;
/*   66 */   protected byte[] _textptr = null;
/*   67 */   protected byte[] _timestamp = null;
/*      */   protected int _classIDLen;
/*      */   protected byte[] _classID;
/*      */   protected int _dbID;
/*      */   protected int _classNum;
/*      */   protected long _lobLength;
/*      */   protected int _locatorLength;
/*   76 */   protected byte[] _locator = null;
/*      */ 
/*  198 */   TdsDataObject _next = null;
/*  199 */   TdsDataObject _prev = null;
/*      */ 
/*      */   public TdsDataObject(TdsProtocolContext paramTdsProtocolContext)
/*      */     throws IOException
/*      */   {
/*   85 */     super((Tds)paramTdsProtocolContext._protocol, paramTdsProtocolContext._inFormat);
/*   86 */     this._context = paramTdsProtocolContext;
/*   87 */     setBigEndian(paramTdsProtocolContext._bigEndian);
/*      */ 
/*   89 */     initialize();
/*      */   }
/*      */ 
/*      */   protected abstract TdsDataObject createCachedCopy()
/*      */     throws IOException, SQLException;
/*      */ 
/*      */   protected void copyInto(TdsDataObject paramTdsDataObject) throws IOException
/*      */   {
/*   97 */     paramTdsDataObject._state = this._state;
/*   98 */     paramTdsDataObject._dataFmt = this._dataFmt;
/*   99 */     paramTdsDataObject._isNull = this._isNull;
/*  100 */     paramTdsDataObject._dataLength = this._dataLength;
/*  101 */     paramTdsDataObject._textptrlen = this._textptrlen;
/*  102 */     paramTdsDataObject._classIDLen = this._classIDLen;
/*  103 */     paramTdsDataObject._dbID = this._dbID;
/*  104 */     paramTdsDataObject._classNum = this._classNum;
/*      */   }
/*      */ 
/*      */   public InputStream getAsciiStream()
/*      */     throws SQLException
/*      */   {
/*  111 */     badConversionCombination();
/*  112 */     return null;
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream() throws SQLException {
/*  116 */     badConversionCombination();
/*  117 */     return null;
/*      */   }
/*      */ 
/*      */   public InputStream getUnicodeStream() throws SQLException {
/*  121 */     badConversionCombination();
/*  122 */     return null;
/*      */   }
/*      */ 
/*      */   public InputStream getBinaryStream() throws SQLException {
/*  126 */     badConversionCombination();
/*  127 */     return null;
/*      */   }
/*      */ 
/*      */   public byte[] getBytes() throws SQLException {
/*  131 */     badConversionCombination();
/*  132 */     return null;
/*      */   }
/*      */ 
/*      */   public DateObject getDateObject(int paramInt, Calendar paramCalendar) throws SQLException
/*      */   {
/*  137 */     badConversionCombination();
/*  138 */     return null;
/*      */   }
/*      */ 
/*      */   public Blob getBlob() throws SQLException
/*      */   {
/*  143 */     badConversionCombination();
/*  144 */     return null;
/*      */   }
/*      */ 
/*      */   public Clob getClob() throws SQLException
/*      */   {
/*  149 */     badConversionCombination();
/*  150 */     return null;
/*      */   }
/*      */ 
/*      */   public Blob getInitializedBlob()
/*      */     throws SQLException
/*      */   {
/*  163 */     badConversionCombination();
/*  164 */     return null;
/*      */   }
/*      */ 
/*      */   public Clob getInitializedClob() throws SQLException
/*      */   {
/*  169 */     badConversionCombination();
/*  170 */     return null;
/*      */   }
/*      */ 
/*      */   public TextPointer getTextPtr() throws SQLException
/*      */   {
/*  175 */     noTextPointer();
/*  176 */     return null; } 
/*      */   public abstract BigDecimal getBigDecimal(int paramInt) throws SQLException;
/*      */ 
/*      */   public abstract boolean getBoolean() throws SQLException;
/*      */ 
/*      */   public abstract byte getByte() throws SQLException;
/*      */ 
/*      */   public abstract double getDouble() throws SQLException;
/*      */ 
/*      */   public abstract float getFloat() throws SQLException;
/*      */ 
/*      */   public abstract int getInt() throws SQLException;
/*      */ 
/*      */   public abstract long getLong() throws SQLException;
/*      */ 
/*      */   public abstract short getShort() throws SQLException;
/*      */ 
/*      */   public abstract Object getObject() throws SQLException;
/*      */ 
/*      */   public abstract String getString() throws SQLException;
/*      */ 
/*  194 */   public boolean isNull() throws SQLException { return this._isNull; }
/*      */ 
/*      */ 
/*      */   public void setNext(Chainable paramChainable)
/*      */   {
/*  202 */     this._next = ((TdsDataObject)paramChainable);
/*      */   }
/*      */ 
/*      */   public Chainable getNext() {
/*  206 */     return this._next;
/*      */   }
/*      */ 
/*      */   public void setPrevious(Chainable paramChainable) {
/*  210 */     this._prev = ((TdsDataObject)paramChainable);
/*      */   }
/*      */ 
/*      */   public Chainable getPrevious() {
/*  214 */     return this._prev;
/*      */   }
/*      */ 
/*      */   public abstract void cache()
/*      */     throws IOException;
/*      */ 
/*      */   public abstract void clear()
/*      */     throws IOException;
/*      */ 
/*      */   protected void getSize()
/*      */     throws IOException
/*      */   {
/*  234 */     if (this._dataLength != -2) return;
/*      */ 
/*  236 */     int i = 0;
/*  237 */     if (this._dataFmt._colStatusBytePresent)
/*      */     {
/*  245 */       this._columnStatus = (byte)readUnsignedByte();
/*      */ 
/*  249 */       if ((this._columnStatus & 0x1) != 0)
/*      */       {
/*  255 */         this._dataLength = 0;
/*  256 */         this._isNull = true;
/*  257 */         return;
/*      */       }
/*  259 */       if ((this._columnStatus == 0) || (this._columnStatus == 2))
/*      */       {
/*  262 */         i = 1;
/*      */       }
/*      */     }
/*  265 */     switch (this._dataFmt._datatype)
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
/*  284 */       this._dataLength = this._dataFmt._length;
/*  285 */       break;
/*      */     case 38:
/*      */     case 47:
/*      */     case 68:
/*      */     case 106:
/*      */     case 108:
/*      */     case 109:
/*      */     case 110:
/*      */     case 111:
/*      */     case 123:
/*      */     case 147:
/*      */     case 187:
/*      */     case 188:
/*  300 */       this._dataLength = readUnsignedByte();
/*  301 */       break;
/*      */     case 37:
/*      */     case 39:
/*      */     case 45:
/*      */     case 103:
/*      */     case 104:
/*  308 */       this._dataLength = readUnsignedByte();
/*      */ 
/*  319 */       this._holds0LNN = ((this._dataLength == 0) && (i != 0));
/*      */ 
/*  321 */       break;
/*      */     case 34:
/*      */     case 35:
/*      */     case 174:
/*  327 */       this._textptrlen = readUnsignedByte();
/*  328 */       if (this._textptrlen == 0)
/*      */       {
/*  331 */         this._dataLength = 0;
/*      */       }
/*      */       else
/*      */       {
/*  339 */         this._textptr = new byte[this._textptrlen + 8];
/*  340 */         this.in.read(this._textptr, 0, this._textptrlen + 8);
/*      */       }
/*      */     case 175:
/*      */     case 225:
/*  344 */       this._dataLength = readInt();
/*      */ 
/*  355 */       this._holds0LNN = ((this._dataLength == 0) && (i != 0));
/*      */ 
/*  357 */       break;
/*      */     case 36:
/*  359 */       this._dataLength = -1;
/*  360 */       int j = readUnsignedByte();
/*  361 */       this._classIDLen = readShort();
/*      */ 
/*  363 */       switch (this._dataFmt._blobType)
/*      */       {
/*      */       case 1:
/*  368 */         if (this._classIDLen > 0)
/*      */         {
/*  370 */           this._classID = new byte[this._classIDLen];
/*  371 */           read(this._classID, 0, this._classIDLen); } break;
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*  378 */         if (this._classIDLen > 0)
/*      */         {
/*  380 */           skip(this._classIDLen); } break;
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  388 */         if (this._classIDLen > 0)
/*      */         {
/*  390 */           skip(this._classIDLen);
/*      */         }
/*  392 */         this._lobLength = readLong();
/*  393 */         this._locatorLength = readShort();
/*  394 */         this._locator = new byte[this._locatorLength];
/*  395 */         read(this._locator, 0, this._locatorLength);
/*  396 */         break;
/*      */       case 2:
/*      */       default:
/*  404 */         skip(this._classIDLen);
/*      */       }
/*  406 */       break;
/*      */     default:
/*  409 */       ErrorMessage.raiseIOException("JZ0P4");
/*      */     }
/*      */ 
/*  414 */     if ((this._dataLength != 0) || 
/*  416 */       (this._holds0LNN))
/*      */       return;
/*  418 */     this._isNull = true;
/*      */   }
/*      */ 
/*      */   protected final long readINTN()
/*      */     throws IOException
/*      */   {
/*  435 */     switch (this._dataLength) { case 0:
/*  438 */       return 0L;
/*      */     case 1:
/*  440 */       return readUnsignedByte();
/*      */     case 2:
/*  442 */       return readShort();
/*      */     case 4:
/*  444 */       return readInt();
/*      */     case 8:
/*  446 */       return readLong();
/*      */     case 3:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7: } ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  451 */     return 0L;
/*      */   }
/*      */ 
/*      */   protected final byte[] readINTNAsBytes()
/*      */     throws IOException
/*      */   {
/*  457 */     switch (this._dataLength) { case 0:
/*  460 */       return null;
/*      */     case 1:
/*      */     case 2:
/*      */     case 4:
/*      */     case 8:
/*  465 */       return readBytesForAllTypes(this._dataLength);
/*      */     case 3:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7: } ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  470 */     return null;
/*      */   }
/*      */ 
/*      */   protected final long readUINTN()
/*      */     throws IOException
/*      */   {
/*  480 */     switch (this._dataLength) {
/*      */     case 1:
/*  483 */       return readUnsignedByte();
/*      */     case 2:
/*  485 */       return readUnsignedShortAsInt();
/*      */     case 4:
/*  487 */       return readUnsignedIntAsLong();
/*      */     case 3:
/*      */     }
/*  489 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  492 */     return 0L;
/*      */   }
/*      */ 
/*      */   protected final byte[] readUINTNAsBytes()
/*      */     throws IOException
/*      */   {
/*  498 */     switch (this._dataLength) {
/*      */     case 1:
/*      */     case 2:
/*      */     case 4:
/*  503 */       return readBytesForAllTypes(this._dataLength);
/*      */     case 3:
/*      */     }
/*  505 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  508 */     return null;
/*      */   }
/*      */ 
/*      */   protected final BigDecimal readNUMERIC(int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  520 */     if (this._dataLength == 0)
/*      */     {
/*  522 */       return null;
/*      */     }
/*  524 */     byte[] arrayOfByte = new byte[this._dataLength];
/*  525 */     this.in.read(arrayOfByte);
/*  526 */     return TdsNumeric.numericValue(arrayOfByte, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   protected final byte[] readNUMERIC()
/*      */     throws IOException
/*      */   {
/*  533 */     if (this._dataLength == 0)
/*      */     {
/*  535 */       return null;
/*      */     }
/*  537 */     byte[] arrayOfByte = new byte[this._dataLength];
/*  538 */     this.in.read(arrayOfByte);
/*  539 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   protected final String readString()
/*      */     throws IOException
/*      */   {
/*  552 */     if (this._isNull)
/*      */     {
/*  554 */       return null;
/*      */     }
/*  556 */     if ((this._dataLength == 0) && (this._holds0LNN))
/*      */     {
/*  563 */       return "";
/*      */     }
/*      */ 
/*  566 */     int i = 0; int j = 0;
/*      */ 
/*  569 */     if (this._context._maxFieldSize != 0)
/*      */     {
/*  571 */       i = Math.min(this._dataLength, this._context._maxFieldSize);
/*  572 */       j = this._dataLength - i;
/*      */     }
/*      */     else
/*      */     {
/*  576 */       i = this._dataLength;
/*      */     }
/*      */ 
/*  579 */     byte[] arrayOfByte = new byte[i];
/*  580 */     this.in.read(arrayOfByte, 0, i);
/*  581 */     this.in.skip(j);
/*  582 */     return this._tds._charsetConverter.toUnicode(arrayOfByte);
/*      */   }
/*      */ 
/*      */   protected final byte[] readStringAsBytes()
/*      */     throws IOException
/*      */   {
/*  593 */     return readString().getBytes();
/*      */   }
/*      */ 
/*      */   protected final String readUnicodeString()
/*      */     throws IOException
/*      */   {
/*  605 */     if ((this._dataLength == 0) && (this._holds0LNN))
/*      */     {
/*  607 */       return "";
/*      */     }
/*  609 */     if (this._dataLength == 0)
/*      */     {
/*  611 */       return null;
/*      */     }
/*  613 */     int i = 0; int j = 0;
/*      */ 
/*  616 */     if (this._context._maxFieldSize != 0)
/*      */     {
/*  618 */       i = Math.min(this._dataLength, this._context._maxFieldSize);
/*  619 */       j = this._dataLength - i;
/*      */     }
/*      */     else
/*      */     {
/*  623 */       i = this._dataLength;
/*      */     }
/*      */ 
/*  627 */     byte[] arrayOfByte = new byte[i];
/*  628 */     this.in.read(arrayOfByte, 0, i);
/*  629 */     this.in.skip(j);
/*      */ 
/*  639 */     String str1 = null;
/*  640 */     String str2 = null;
/*  641 */     if (getBigEndian())
/*      */     {
/*  643 */       str2 = "UnicodeBigUnmarked";
/*      */     }
/*      */     else
/*      */     {
/*  647 */       str2 = "UnicodeLittleUnmarked";
/*      */     }
/*      */     try
/*      */     {
/*  651 */       str1 = new String(arrayOfByte, 0, i, str2);
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*      */     {
/*      */     }
/*      */ 
/*  658 */     return str1;
/*      */   }
/*      */ 
/*      */   protected final double readFLTN()
/*      */     throws IOException
/*      */   {
/*  669 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  672 */       return 0.0D;
/*      */     case 4:
/*  674 */       return readFloat();
/*      */     case 8:
/*  676 */       return readDouble();
/*      */     }
/*  678 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  681 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   protected final byte[] readFLTNAsBytes()
/*      */     throws IOException
/*      */   {
/*  687 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  690 */       return null;
/*      */     case 4:
/*      */     case 8:
/*  693 */       return readBytesForAllTypes(this._dataLength);
/*      */     }
/*  695 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  698 */     return null;
/*      */   }
/*      */ 
/*      */   protected final BigDecimal readMONEYN()
/*      */     throws IOException
/*      */   {
/*  711 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  714 */       return null;
/*      */     case 4:
/*  716 */       int i = readInt();
/*  717 */       return new BigDecimal(BigInteger.valueOf(i), 4);
/*      */     case 8:
/*  719 */       long l = readLong();
/*  720 */       return new BigDecimal(BigInteger.valueOf(l), 4);
/*      */     }
/*  722 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  725 */     return null;
/*      */   }
/*      */ 
/*      */   protected final byte[] readMONEYNAsBytes()
/*      */     throws IOException
/*      */   {
/*  733 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  736 */       return null;
/*      */     case 4:
/*      */     case 8:
/*  739 */       return readBytesForAllTypes(this._dataLength);
/*      */     }
/*  741 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  744 */     return null;
/*      */   }
/*      */ 
/*      */   protected final TdsDateTime readDATETIMN()
/*      */     throws IOException, SQLException
/*      */   {
/*  758 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  761 */       return null;
/*      */     case 4:
/*  763 */       int i = readUnsignedShort();
/*  764 */       int j = readUnsignedShort();
/*  765 */       return new TdsDateTime(i, j, 2);
/*      */     case 8:
/*  768 */       int k = readInt();
/*  769 */       int l = readInt();
/*  770 */       return new TdsDateTime(k, l, 1);
/*      */     }
/*  772 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  775 */     return null;
/*      */   }
/*      */ 
/*      */   protected final byte[] readDATETIMNAsBytes()
/*      */     throws IOException
/*      */   {
/*  781 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  784 */       return null;
/*      */     case 4:
/*      */     case 8:
/*  787 */       return readBytesForAllTypes(this._dataLength);
/*      */     }
/*  789 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  792 */     return null;
/*      */   }
/*      */ 
/*      */   protected final TdsDateTime readBIGDATETIMN(int paramInt)
/*      */     throws IOException
/*      */   {
/*  798 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  801 */       return null;
/*      */     case 8:
/*  803 */       BigDecimal localBigDecimal = readUnsignedLongAsBigDecimal();
/*  804 */       if (paramInt == 187)
/*      */       {
/*  806 */         paramInt = 5;
/*      */       }
/*  808 */       else if (paramInt == 188)
/*      */       {
/*  810 */         paramInt = 6;
/*      */       }
/*  812 */       return new TdsDateTime(localBigDecimal, paramInt);
/*      */     }
/*  814 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  819 */     return null;
/*      */   }
/*      */ 
/*      */   protected final byte[] readBIGDATETIMNAsBytes()
/*      */     throws IOException
/*      */   {
/*  825 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  828 */       return null;
/*      */     case 8:
/*  830 */       return readBytesForAllTypes(8);
/*      */     }
/*  832 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  837 */     return null;
/*      */   }
/*      */ 
/*      */   protected final TdsDateTime readDATEN()
/*      */     throws IOException, SQLException
/*      */   {
/*  848 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  851 */       return null;
/*      */     case 4:
/*  853 */       int i = readInt();
/*  854 */       return new TdsDateTime(i, 0, 3);
/*      */     }
/*  856 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  859 */     return null;
/*      */   }
/*      */ 
/*      */   protected final byte[] readDATENAsBytes()
/*      */     throws IOException
/*      */   {
/*  865 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  868 */       return null;
/*      */     case 4:
/*  870 */       return readBytesForAllTypes(4);
/*      */     }
/*  872 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  875 */     return null;
/*      */   }
/*      */ 
/*      */   protected final TdsDateTime readTIMEN()
/*      */     throws IOException, SQLException
/*      */   {
/*  886 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  889 */       return null;
/*      */     case 4:
/*  891 */       int i = readInt();
/*  892 */       return new TdsDateTime(0, i, 4);
/*      */     }
/*  894 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  897 */     return null;
/*      */   }
/*      */ 
/*      */   protected final byte[] readTIMENAsBytes()
/*      */     throws IOException
/*      */   {
/*  903 */     switch (this._dataLength)
/*      */     {
/*      */     case 0:
/*  906 */       return null;
/*      */     case 4:
/*  908 */       return readBytesForAllTypes(4);
/*      */     }
/*  910 */     ErrorMessage.raiseIOException("JZ0P4");
/*      */ 
/*  913 */     return null;
/*      */   }
/*      */ 
/*      */   protected byte[] funkyBinaryReader()
/*      */     throws IOException
/*      */   {
/*  920 */     byte[] arrayOfByte = null;
/*  921 */     int i = 0; int j = 0; int k = 0;
/*      */ 
/*  925 */     if (this._context._maxFieldSize != 0)
/*      */     {
/*  927 */       i = Math.min(this._dataLength, this._context._maxFieldSize);
/*      */ 
/*  931 */       if ((this._dataFmt._datatype == 37) && (this._dataFmt._usertype == 3))
/*      */       {
/*  936 */         j = Math.min(this._context._maxFieldSize, this._dataFmt._length) - i;
/*      */       }
/*      */ 
/*  939 */       k = this._dataLength - i;
/*      */     }
/*      */     else
/*      */     {
/*  943 */       i = this._dataLength;
/*  944 */       if ((this._dataFmt._datatype == 37) && (this._dataFmt._usertype == 3))
/*      */       {
/*  947 */         j = this._dataFmt._length - i;
/*      */       }
/*      */     }
/*      */ 
/*  951 */     switch (this._dataFmt._datatype)
/*      */     {
/*      */     case 34:
/*      */     case 37:
/*      */     case 45:
/*      */     case 174:
/*      */     case 225:
/*  958 */       arrayOfByte = new byte[i + j];
/*  959 */       this.in.read(arrayOfByte, 0, i);
/*  960 */       for (int l = i; l < arrayOfByte.length; ++l)
/*      */       {
/*  962 */         arrayOfByte[l] = 0;
/*      */       }
/*      */ 
/*  968 */       if ((this._context._maxFieldSize > 0) && (this._context._maxFieldSize < this._dataLength))
/*      */       {
/*  985 */         this.in.read(new byte[k]); break label275:
/*      */       }
/*      */ 
/*  995 */       this.in.skip(k);
/*      */     }
/*      */ 
/* 1001 */     label275: return arrayOfByte;
/*      */   }
/*      */ 
/*      */   protected void badConversionCombination()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1013 */       startRead();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1017 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/* 1019 */     if (this._isNull) return;
/* 1020 */     ErrorMessage.raiseError("JZ0TC");
/*      */   }
/*      */ 
/*      */   protected void badConversion(String paramString)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1030 */       startRead();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1034 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/* 1036 */     if (this._isNull) return;
/* 1037 */     ErrorMessage.raiseError("JZ009", paramString);
/*      */   }
/*      */ 
/*      */   protected void noTextPointer() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1044 */       startRead();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1048 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/* 1050 */     if (this._isNull) return;
/* 1051 */     ErrorMessage.raiseError("JZ0R4");
/*      */   }
/*      */ 
/*      */   public void initialize()
/*      */   {
/* 1060 */     this._state = 0;
/* 1061 */     this._isNull = false;
/* 1062 */     this._dataLength = -2;
/*      */   }
/*      */ 
/*      */   public void startRead()
/*      */     throws IOException
/*      */   {
/* 1070 */     switch (this._state)
/*      */     {
/*      */     case 0:
/* 1075 */       if ((this._prev != null) && (this._prev._state != 3))
/*      */       {
/* 1077 */         this._prev.cache();
/*      */       }
/* 1079 */       this._state = 1;
/* 1080 */       getSize();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected abstract void beginRead() throws IOException;
/*      */ 
/*      */   protected abstract void beginReadAsBytes() throws IOException;
/*      */ 
/*      */   protected void doRead() throws SQLException
/*      */   {
/*      */     try {
/* 1091 */       beginRead();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1095 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void doReadAsBytes() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1103 */       beginReadAsBytes();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1107 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void endRead()
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsDataObject
 * JD-Core Version:    0.5.4
 */