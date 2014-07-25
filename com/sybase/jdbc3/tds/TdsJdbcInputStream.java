/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.charset.SybUTF8Charset;
/*      */ import com.sybase.jdbc3.jdbc.CharsetToUniInputStream;
/*      */ import com.sybase.jdbc3.jdbc.Convert;
/*      */ import com.sybase.jdbc3.jdbc.DateObject;
/*      */ import com.sybase.jdbc3.jdbc.DynamicClassLoader;
/*      */ import com.sybase.jdbc3.jdbc.DynamicObjectInputStream;
/*      */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*      */ import com.sybase.jdbc3.jdbc.RawInputStream;
/*      */ import com.sybase.jdbc3.jdbc.RawToAsciiInputStream;
/*      */ import com.sybase.jdbc3.jdbc.SybBinaryClientLob;
/*      */ import com.sybase.jdbc3.jdbc.SybBinaryLob;
/*      */ import com.sybase.jdbc3.jdbc.SybCharClientLob;
/*      */ import com.sybase.jdbc3.jdbc.SybCharLob;
/*      */ import com.sybase.jdbc3.jdbc.SybLob;
/*      */ import com.sybase.jdbc3.jdbc.TextPointer;
/*      */ import com.sybase.jdbc3.jdbc.UnicharToAsciiInputStream;
/*      */ import com.sybase.jdbc3.jdbc.UnicharToUniInputStream;
/*      */ import com.sybase.jdbc3.utils.CacheManager;
/*      */ import com.sybase.jdbc3.utils.CacheStream;
/*      */ import com.sybase.jdbc3.utils.Cacheable;
/*      */ import com.sybase.jdbc3.utils.HexConverts;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Calendar;
/*      */ 
/*      */ public class TdsJdbcInputStream extends TdsDataObject
/*      */   implements Cacheable
/*      */ {
/*   51 */   private static volatile long _logIdCounter = 0L;
/*   52 */   private String _logId = null;
/*      */   private static final String JAVA_LANG_BOOLEAN = "java.lang.Boolean";
/*      */   private static final String JAVA_LANG_DOUBLE = "java.lang.Double";
/*      */   private static final String JAVA_LANG_FLOAT = "java.lang.Float";
/*      */   private static final String JAVA_LANG_INTEGER = "java.lang.Integer";
/*      */   private static final String JAVA_LANG_LONG = "java.lang.Long";
/*      */   private static final String JAVA_LANG_OBJECT = "java.lang.Object";
/*      */   private static final String JAVA_LANG_STRING = "java.lang.String";
/*      */   private static final String JAVA_MATH_BIGDECIMAL = "java.math.BigDecimal";
/*      */   private static final String JAVA_IO_FILTERINPUTSTREAM = "java.io.FilterInputStream";
/*      */   private static final String JAVA_SQL_TIMESTAMP = "java.sql.Timestamp";
/*      */   private static final String JAVA_SQL_TIME = "java.sql.Time";
/*      */   private static final String JAVA_SQL_DATE = "java.sql.Date";
/*      */   private static final int BUFLEN = 512;
/*      */   public static final int RAW_TO_ASCII_STREAM = 1;
/*      */   public static final int RAW_TO_UNI_STREAM = 2;
/*      */   public static final int CHARSET_TO_UNI_STREAM = 3;
/*      */   public static final int RAW_STREAM = 4;
/*      */   public static final int UNITYPE_TO_UNI_STREAM = 5;
/*      */   public static final int UNITYPE_TO_ASCII_STREAM = 6;
/*      */   private CacheManager _monitor;
/*      */   private RawInputStream _columnInputStream;
/*      */   private InputStreamReader _columnInputStreamReader;
/*      */   private DynamicClassLoader _classLoader;
/*      */ 
/*      */   public TdsJdbcInputStream(String paramString, TdsProtocolContext paramTdsProtocolContext, Tds paramTds)
/*      */     throws IOException
/*      */   {
/*  108 */     super(paramTdsProtocolContext);
/*  109 */     this._logId = ("Ji" + _logIdCounter++);
/*      */ 
/*  111 */     this._classLoader = this._tds.getClassLoader();
/*      */   }
/*      */ 
/*      */   protected TdsDataObject createCachedCopy()
/*      */     throws SQLException, IOException
/*      */   {
/*  127 */     return new CachedTdsJdbcInputStream(this);
/*      */   }
/*      */ 
/*      */   public InputStream getAsciiStream()
/*      */     throws SQLException
/*      */   {
/*  137 */     InputStream localInputStream = null;
/*      */     try
/*      */     {
/*  141 */       beginRead();
/*  142 */       if (!this._isNull)
/*      */       {
/*  144 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 36:
/*  147 */           switch (this._dataFmt._blobType)
/*      */           {
/*      */           case 3:
/*  150 */             this._columnInputStream = makeNewRIS(4);
/*  151 */             return this._columnInputStream;
/*      */           case 4:
/*  153 */             this._columnInputStream = makeNewRIS(1);
/*      */ 
/*  155 */             return this._columnInputStream;
/*      */           case 5:
/*  157 */             this._columnInputStream = makeNewRIS(6);
/*      */ 
/*  159 */             return this._columnInputStream;
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/*  163 */             SybLob localSybLob = readLobInfo();
/*  164 */             endRead();
/*  165 */             localInputStream = localSybLob.getResultSetAsciiStream();
/*  166 */             break;
/*      */           default:
/*  168 */             ErrorMessage.raiseError("JZ0TC");
/*      */           }
/*      */ 
/*  171 */           break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/*  176 */           this._columnInputStream = makeNewRIS(4);
/*  177 */           return this._columnInputStream;
/*      */         case 34:
/*      */         case 37:
/*      */         case 45:
/*  181 */           this._columnInputStream = makeNewRIS(1);
/*  182 */           return this._columnInputStream;
/*      */         case 174:
/*      */         case 225:
/*  185 */           if (this._dataFmt.isUnitype())
/*      */           {
/*  187 */             this._columnInputStream = makeNewRIS(6);
/*      */           }
/*      */           else
/*      */           {
/*  192 */             this._columnInputStream = makeNewRIS(1);
/*      */           }
/*      */ 
/*  195 */           return this._columnInputStream;
/*      */         default:
/*  197 */           ErrorMessage.raiseError("JZ0TE", "char, varchar, unichar, univarchar, text, unitext, image, binary, long binary, varbinary");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  206 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */ 
/*  209 */     return localInputStream;
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream()
/*      */     throws SQLException
/*      */   {
/*  217 */     Object localObject = null;
/*      */     try
/*      */     {
/*  221 */       beginRead();
/*  222 */       if (!this._isNull)
/*      */       {
/*      */         String str;
/*  224 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 36:
/*  227 */           switch (this._dataFmt._blobType)
/*      */           {
/*      */           case 3:
/*  230 */             this._columnInputStream = makeNewRIS(4);
/*      */ 
/*  232 */             if (this._tds._charsetName.equals("x-SybUTF8"))
/*      */             {
/*  234 */               this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, SybUTF8Charset.getInstance());
/*      */             }
/*      */             else
/*      */             {
/*  239 */               this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, this._tds._charsetName);
/*      */             }
/*      */ 
/*  242 */             return this._columnInputStreamReader;
/*      */           case 4:
/*  244 */             this._columnInputStream = makeNewRIS(1);
/*      */ 
/*  246 */             if (this._tds._charsetName.equals("x-SybUTF8"))
/*      */             {
/*  248 */               this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, SybUTF8Charset.getInstance());
/*      */             }
/*      */             else
/*      */             {
/*  253 */               this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, this._tds._charsetName);
/*      */             }
/*      */ 
/*  256 */             return this._columnInputStreamReader;
/*      */           case 5:
/*  258 */             this._columnInputStream = makeNewRIS(5);
/*      */ 
/*  260 */             str = null;
/*  261 */             if (getBigEndian())
/*      */             {
/*  263 */               str = "UnicodeBig";
/*      */             }
/*      */             else
/*      */             {
/*  267 */               str = "UnicodeLittle";
/*      */             }
/*  269 */             this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, str);
/*      */ 
/*  272 */             return this._columnInputStreamReader;
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/*  276 */             SybLob localSybLob = readLobInfo();
/*  277 */             endRead();
/*  278 */             return localSybLob.getResultSetCharacterStream();
/*      */           }
/*  280 */           ErrorMessage.raiseError("JZ0TC");
/*      */ 
/*  283 */           break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/*  288 */           this._columnInputStream = makeNewRIS(4);
/*  289 */           if (this._tds._charsetName.equals("x-SybUTF8"))
/*      */           {
/*  291 */             this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, SybUTF8Charset.getInstance());
/*      */           }
/*      */           else
/*      */           {
/*  296 */             this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, this._tds._charsetName);
/*      */           }
/*      */ 
/*  299 */           return this._columnInputStreamReader;
/*      */         case 34:
/*      */         case 37:
/*      */         case 45:
/*  303 */           this._columnInputStream = makeNewRIS(1);
/*  304 */           if (this._tds._charsetName.equals("x-SybUTF8"))
/*      */           {
/*  306 */             this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, SybUTF8Charset.getInstance());
/*      */           }
/*      */           else
/*      */           {
/*  311 */             this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, this._tds._charsetName);
/*      */           }
/*      */ 
/*  314 */           return this._columnInputStreamReader;
/*      */         case 174:
/*      */         case 225:
/*  317 */           if (this._dataFmt.isUnitype())
/*      */           {
/*  319 */             this._columnInputStream = makeNewRIS(5);
/*      */ 
/*  321 */             str = null;
/*  322 */             if (getBigEndian())
/*      */             {
/*  324 */               str = "UnicodeBig";
/*      */             }
/*      */             else
/*      */             {
/*  328 */               str = "UnicodeLittle";
/*      */             }
/*  330 */             this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, str);
/*      */           }
/*      */           else
/*      */           {
/*  336 */             this._columnInputStream = makeNewRIS(1);
/*      */ 
/*  338 */             if (this._tds._charsetName.equals("x-SybUTF8"))
/*      */             {
/*  340 */               this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, SybUTF8Charset.getInstance());
/*      */             }
/*      */             else
/*      */             {
/*  345 */               this._columnInputStreamReader = new InputStreamReader(this._columnInputStream, this._tds._charsetName);
/*      */             }
/*      */           }
/*      */ 
/*  349 */           return this._columnInputStreamReader;
/*      */         default:
/*  351 */           ErrorMessage.raiseError("JZ0TC");
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  358 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */ 
/*  361 */     return localObject;
/*      */   }
/*      */ 
/*      */   public InputStream getUnicodeStream()
/*      */     throws SQLException
/*      */   {
/*  370 */     Object localObject = null;
/*      */     try
/*      */     {
/*  373 */       beginRead();
/*  374 */       if (!this._isNull)
/*      */       {
/*  376 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 36:
/*  379 */           switch (this._dataFmt._blobType)
/*      */           {
/*      */           case 3:
/*  382 */             this._columnInputStream = makeNewRIS(3);
/*      */ 
/*  384 */             return this._columnInputStream;
/*      */           case 4:
/*  386 */             this._columnInputStream = makeNewRIS(2);
/*      */ 
/*  388 */             return this._columnInputStream;
/*      */           case 5:
/*  390 */             this._columnInputStream = makeNewRIS(5);
/*      */ 
/*  392 */             return this._columnInputStream;
/*      */           }
/*  394 */           ErrorMessage.raiseError("JZ0TC");
/*      */ 
/*  397 */           break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/*  402 */           this._columnInputStream = makeNewRIS(3);
/*  403 */           return this._columnInputStream;
/*      */         case 34:
/*      */         case 37:
/*      */         case 45:
/*  407 */           this._columnInputStream = makeNewRIS(2);
/*  408 */           return this._columnInputStream;
/*      */         case 174:
/*      */         case 225:
/*  411 */           if (this._dataFmt.isUnitype())
/*      */           {
/*  413 */             this._columnInputStream = makeNewRIS(5);
/*      */           }
/*      */           else
/*      */           {
/*  418 */             this._columnInputStream = makeNewRIS(2);
/*      */           }
/*  420 */           return this._columnInputStream;
/*      */         default:
/*  422 */           ErrorMessage.raiseError("JZ0TE", "char, varchar, unichar, univarchar, text, unitext, image, binary, long binary, varbinary");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  431 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */ 
/*  434 */     return localObject;
/*      */   }
/*      */ 
/*      */   public InputStream getBinaryStream()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  445 */       beginRead();
/*  446 */       if (!this._isNull)
/*      */       {
/*  449 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 36:
/*  452 */           switch (this._dataFmt._blobType)
/*      */           {
/*      */           case 7:
/*  455 */             SybLob localSybLob = readLobInfo();
/*  456 */             endRead();
/*  457 */             return localSybLob.getResultSetBinaryStream();
/*      */           case 4:
/*  459 */             break;
/*      */           default:
/*  461 */             ErrorMessage.raiseError("JZ0TC");
/*      */           }
/*      */         case 34:
/*      */         case 37:
/*      */         case 45:
/*      */         case 225:
/*  468 */           this._columnInputStream = makeNewRIS(4);
/*  469 */           return this._columnInputStream;
/*      */         }
/*  471 */         ErrorMessage.raiseError("JZ0TE", "image, binary, long binary, varbinary");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  479 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*  481 */     return null;
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  491 */     BigDecimal localBigDecimal = null;
/*  492 */     int i = 0;
/*      */     try
/*      */     {
/*  495 */       beginRead();
/*  496 */       if (!this._isNull)
/*      */       {
/*  498 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 48:
/*      */         case 50:
/*  502 */           localBigDecimal = new BigDecimal(readUnsignedByte());
/*  503 */           break;
/*      */         case 52:
/*  505 */           localBigDecimal = new BigDecimal(readShort());
/*  506 */           break;
/*      */         case 65:
/*  508 */           localBigDecimal = new BigDecimal(readUnsignedShortAsInt());
/*  509 */           break;
/*      */         case 56:
/*  511 */           localBigDecimal = new BigDecimal(readInt());
/*  512 */           break;
/*      */         case 66:
/*  514 */           localBigDecimal = new BigDecimal(readUnsignedIntAsLong());
/*  515 */           break;
/*      */         case 191:
/*  517 */           localBigDecimal = new BigDecimal(readLong());
/*  518 */           break;
/*      */         case 67:
/*  520 */           localBigDecimal = readUnsignedLongAsBigDecimal();
/*  521 */           break;
/*      */         case 38:
/*  523 */           localBigDecimal = new BigDecimal(readINTN());
/*  524 */           break;
/*      */         case 68:
/*  526 */           if (this._dataLength < 8)
/*      */           {
/*  528 */             localBigDecimal = new BigDecimal(readUINTN());
/*      */           }
/*      */           else
/*      */           {
/*  532 */             localBigDecimal = readUnsignedLongAsBigDecimal();
/*      */           }
/*  534 */           break;
/*      */         case 59:
/*  536 */           localBigDecimal = new BigDecimal(readFloat());
/*  537 */           break;
/*      */         case 62:
/*  539 */           localBigDecimal = new BigDecimal(readDouble());
/*  540 */           break;
/*      */         case 109:
/*  542 */           localBigDecimal = new BigDecimal(readFLTN());
/*  543 */           break;
/*      */         case 106:
/*      */         case 108:
/*  546 */           localBigDecimal = readNUMERIC(this._dataFmt._precision, this._dataFmt._scale);
/*      */ 
/*  548 */           break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/*  553 */           localBigDecimal = new BigDecimal(Convert.numString(readString()));
/*      */ 
/*  555 */           break;
/*      */         case 174:
/*      */         case 225:
/*  558 */           if (this._dataFmt.isUnitype())
/*      */           {
/*  560 */             localBigDecimal = new BigDecimal(Convert.numString(readUnicodeString()));
/*      */           }
/*      */           else
/*      */           {
/*  565 */             i = 1;
/*      */           }
/*  567 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/*  571 */           localBigDecimal = readMONEYN();
/*  572 */           break;
/*      */         default:
/*  586 */           i = 1;
/*      */         }
/*  588 */         if (i != 0)
/*      */         {
/*  590 */           ErrorMessage.raiseError("JZ0TE", "tinyint, smallint, int, bit, float, decimal, numeric, char, unichar, varchar, univarchar, text, unitext, money, short money");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  600 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/*  606 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */     }
/*      */     finally
/*      */     {
/*  611 */       endRead();
/*      */     }
/*  613 */     localBigDecimal = Convert.setScale(localBigDecimal, paramInt);
/*  614 */     return localBigDecimal;
/*      */   }
/*      */ 
/*      */   public boolean getBoolean()
/*      */     throws SQLException
/*      */   {
/*  623 */     int i = 0;
/*  624 */     int j = 0;
/*      */     try
/*      */     {
/*  627 */       beginRead();
/*  628 */       if (!this._isNull)
/*      */       {
/*      */         BigDecimal localBigDecimal;
/*      */         String str;
/*  630 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 48:
/*      */         case 50:
/*  634 */           i = (0 != readUnsignedByte()) ? 1 : 0;
/*  635 */           break;
/*      */         case 52:
/*  637 */           i = (0 != readShort()) ? 1 : 0;
/*  638 */           break;
/*      */         case 65:
/*  640 */           i = (0 != readUnsignedShortAsInt()) ? 1 : 0;
/*  641 */           break;
/*      */         case 56:
/*  643 */           i = (0 != readInt()) ? 1 : 0;
/*  644 */           break;
/*      */         case 66:
/*  646 */           i = (0L != readUnsignedIntAsLong()) ? 1 : 0;
/*  647 */           break;
/*      */         case 191:
/*  649 */           i = (0L != readLong()) ? 1 : 0;
/*  650 */           break;
/*      */         case 67:
/*  652 */           i = (0L != readUnsignedLongAsBigDecimal().longValue()) ? 1 : 0;
/*  653 */           break;
/*      */         case 38:
/*  655 */           i = (0L != readINTN()) ? 1 : 0;
/*  656 */           break;
/*      */         case 68:
/*  658 */           if (this._dataLength < 8)
/*      */           {
/*  660 */             i = (0L != readUINTN()) ? 1 : 0;
/*      */           }
/*      */           else
/*      */           {
/*  664 */             i = (0L != readUnsignedLongAsBigDecimal().longValue()) ? 1 : 0;
/*      */           }
/*  666 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/*  670 */           localBigDecimal = readMONEYN();
/*  671 */           i = (0 != localBigDecimal.signum()) ? 1 : 0;
/*  672 */           break;
/*      */         case 59:
/*      */         case 62:
/*      */         case 109:
/*  676 */           i = (0.0D != readFLTN()) ? 1 : 0;
/*  677 */           break;
/*      */         case 106:
/*      */         case 108:
/*  680 */           localBigDecimal = readNUMERIC(this._dataFmt._precision, this._dataFmt._scale);
/*  681 */           i = (0 != localBigDecimal.signum()) ? 1 : 0;
/*  682 */           break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/*  687 */           str = readString();
/*  688 */           i = ((str != null) && (((str.trim().toLowerCase().equals("true")) || (str.trim().equals("1"))))) ? 1 : 0;
/*      */ 
/*  691 */           break;
/*      */         case 174:
/*      */         case 225:
/*  694 */           if (this._dataFmt.isUnitype())
/*      */           {
/*  696 */             str = readUnicodeString();
/*  697 */             i = ((str != null) && (((str.trim().toLowerCase().equals("true")) || (str.trim().equals("1"))))) ? 1 : 0;
/*      */           }
/*      */           else
/*      */           {
/*  703 */             j = 1;
/*      */           }
/*  705 */           break;
/*      */         default:
/*  707 */           j = 1;
/*      */         }
/*  709 */         if (j != 0)
/*      */         {
/*  711 */           ErrorMessage.raiseError("JZ0TE", "tinyint, smallint, int, bit, float, decimal, numeric, char, unichar, varchar, univarchar, text, unitext, money, short money");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  721 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/*  727 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */     }
/*      */     finally
/*      */     {
/*  732 */       endRead();
/*      */     }
/*  734 */     return i;
/*      */   }
/*      */ 
/*      */   public byte getByte()
/*      */     throws SQLException
/*      */   {
/*  743 */     long l = 0L;
/*  744 */     int i = 0;
/*      */     try
/*      */     {
/*  747 */       beginRead();
/*  748 */       if (!this._isNull)
/*      */       {
/*  750 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 48:
/*      */         case 50:
/*  754 */           l = readUnsignedByte();
/*  755 */           break;
/*      */         case 52:
/*  757 */           l = readShort();
/*  758 */           break;
/*      */         case 65:
/*  760 */           l = readUnsignedShortAsInt();
/*  761 */           break;
/*      */         case 56:
/*  763 */           l = readInt();
/*  764 */           break;
/*      */         case 66:
/*  766 */           l = readUnsignedIntAsLong();
/*  767 */           break;
/*      */         case 191:
/*  769 */           l = readLong();
/*  770 */           break;
/*      */         case 38:
/*  772 */           l = readINTN();
/*  773 */           break;
/*      */         case 67:
/*  775 */           l = readUnsignedLongAsBigDecimal().longValue();
/*  776 */           break;
/*      */         case 68:
/*  778 */           if (this._dataLength < 8)
/*      */           {
/*  780 */             l = readUINTN();
/*      */           }
/*      */           else
/*      */           {
/*  784 */             l = readUnsignedLongAsBigDecimal().longValue();
/*      */           }
/*  786 */           break;
/*      */         case 59:
/*      */         case 62:
/*      */         case 109:
/*  790 */           l = new Double(readFLTN()).longValue();
/*  791 */           break;
/*      */         case 106:
/*      */         case 108:
/*  794 */           l = readNUMERIC(this._dataFmt._precision, this._dataFmt._scale).longValue();
/*      */ 
/*  796 */           break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/*  804 */           l = new BigDecimal(Convert.numString(readString())).longValue();
/*      */ 
/*  807 */           break;
/*      */         case 174:
/*      */         case 225:
/*  810 */           if (this._dataFmt.isUnitype())
/*      */           {
/*  812 */             l = new BigDecimal(Convert.numString(readUnicodeString())).longValue();
/*      */           }
/*      */           else
/*      */           {
/*  818 */             i = 1;
/*      */           }
/*  820 */           break;
/*      */         case 36:
/*  822 */           switch (this._dataFmt._blobType)
/*      */           {
/*      */           case 6:
/*      */           case 8:
/*  826 */             l = readLobInfo().getLong();
/*  827 */             break;
/*      */           default:
/*  829 */             i = 1;
/*      */           }
/*  831 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/*  835 */           l = readMONEYN().longValue();
/*  836 */           break;
/*      */         default:
/*  838 */           i = 1;
/*      */         }
/*  840 */         if (i != 0)
/*      */         {
/*  842 */           ErrorMessage.raiseError("JZ0TE", "tinyint, smallint, int, bit, float, decimal, numeric, char, unichar, varchar, univarchar, text, unitext, money, short money, unsigned bigint");
/*      */         }
/*      */ 
/*  848 */         Convert.checkByteOflo(l);
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  853 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/*  859 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */     }
/*      */     finally
/*      */     {
/*  864 */       if (i == 0)
/*      */       {
/*  866 */         endRead();
/*      */       }
/*      */     }
/*      */ 
/*  870 */     return (byte)(int)l;
/*      */   }
/*      */ 
/*      */   public byte[] getBytes()
/*      */     throws SQLException
/*      */   {
/*  879 */     byte[] arrayOfByte1 = null;
/*  880 */     int i = this._dataFmt._usertype;
/*  881 */     int j = 0;
/*      */     try
/*      */     {
/*  884 */       beginRead();
/*  885 */       if (!this._isNull)
/*      */       {
/*  887 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 36:
/*  890 */           switch (this._dataFmt._blobType)
/*      */           {
/*      */           case 7:
/*      */           case 8:
/*  894 */             arrayOfByte1 = readLobInfo().getBytes();
/*  895 */             break;
/*      */           case 4:
/*  898 */             this._columnInputStream = makeNewRIS(4);
/*  899 */             ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*  900 */             byte[] arrayOfByte2 = new byte[512];
/*      */             while (true)
/*      */             {
/*  903 */               int k = this._columnInputStream.read(arrayOfByte2, 0, 512);
/*  904 */               if (k == -1) {
/*      */                 break;
/*      */               }
/*      */ 
/*  908 */               localByteArrayOutputStream.write(arrayOfByte2, 0, k);
/*      */             }
/*  910 */             arrayOfByte1 = localByteArrayOutputStream.toByteArray();
/*  911 */             localByteArrayOutputStream.close();
/*  912 */             this._columnInputStream.close();
/*  913 */             this._columnInputStream = null;
/*  914 */             break;
/*      */           case 5:
/*      */           case 6:
/*      */           default:
/*  916 */             j = 1;
/*      */           }
/*  918 */           break;
/*      */         case 34:
/*      */         case 37:
/*      */         case 45:
/*      */         case 174:
/*      */         case 225:
/*  928 */           arrayOfByte1 = funkyBinaryReader();
/*  929 */           break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/*  934 */           arrayOfByte1 = readStringAsBytes();
/*  935 */           break;
/*      */         case 48:
/*      */         case 50:
/*  938 */           arrayOfByte1 = readBytesForAllTypes(1);
/*  939 */           break;
/*      */         case 52:
/*  941 */           arrayOfByte1 = readBytesForAllTypes(2);
/*  942 */           break;
/*      */         case 65:
/*  944 */           arrayOfByte1 = readBytesForAllTypes(2);
/*  945 */           break;
/*      */         case 56:
/*  947 */           arrayOfByte1 = readBytesForAllTypes(4);
/*  948 */           break;
/*      */         case 66:
/*  950 */           arrayOfByte1 = readBytesForAllTypes(4);
/*  951 */           break;
/*      */         case 191:
/*  953 */           arrayOfByte1 = readBytesForAllTypes(8);
/*  954 */           break;
/*      */         case 38:
/*  956 */           arrayOfByte1 = readINTNAsBytes();
/*  957 */           break;
/*      */         case 67:
/*  959 */           arrayOfByte1 = readBytesForAllTypes(8);
/*  960 */           break;
/*      */         case 68:
/*  962 */           if (this._dataLength < 8)
/*      */           {
/*  964 */             arrayOfByte1 = readUINTNAsBytes();
/*      */           }
/*      */           else
/*      */           {
/*  968 */             arrayOfByte1 = readBytesForAllTypes(8);
/*      */           }
/*  970 */           break;
/*      */         case 59:
/*      */         case 62:
/*      */         case 109:
/*  974 */           arrayOfByte1 = readFLTNAsBytes();
/*  975 */           break;
/*      */         case 106:
/*      */         case 108:
/*  978 */           arrayOfByte1 = readNUMERIC();
/*  979 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/*  983 */           arrayOfByte1 = readMONEYNAsBytes();
/*  984 */           break;
/*      */         case 58:
/*      */         case 61:
/*      */         case 111:
/*  995 */           arrayOfByte1 = readDATETIMNAsBytes();
/*  996 */           break;
/*      */         case 187:
/*      */         case 188:
/*  999 */           arrayOfByte1 = readBIGDATETIMNAsBytes();
/* 1000 */           break;
/*      */         case 49:
/*      */         case 123:
/* 1003 */           if (i == 92)
/*      */           {
/* 1008 */             ErrorMessage.raiseError("JZ0TI", "date", "time");
/*      */           }
/*      */           else
/*      */           {
/* 1014 */             arrayOfByte1 = readDATENAsBytes();
/*      */           }
/* 1016 */           break;
/*      */         case 51:
/*      */         case 147:
/* 1019 */           if (i == 91)
/*      */           {
/* 1024 */             ErrorMessage.raiseError("JZ0TI", "time", "date");
/*      */           }
/*      */           else
/*      */           {
/* 1030 */             arrayOfByte1 = readTIMENAsBytes();
/*      */           }
/* 1032 */           break;
/*      */         default:
/* 1034 */           j = 1;
/*      */         }
/* 1036 */         if (j != 0)
/*      */         {
/* 1038 */           ErrorMessage.raiseError("JZ0TE", "varbinay, long binary, binary, image, unitext");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1046 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     finally
/*      */     {
/* 1050 */       if (j == 0)
/*      */       {
/* 1052 */         endRead();
/*      */       }
/*      */     }
/*      */ 
/* 1056 */     return arrayOfByte1;
/*      */   }
/*      */ 
/*      */   protected byte[] getRawBytes()
/*      */     throws SQLException
/*      */   {
/* 1068 */     byte[] arrayOfByte1 = null;
/*      */     try
/*      */     {
/* 1072 */       beginRead();
/* 1073 */       if (!this._isNull)
/*      */       {
/* 1076 */         this._columnInputStream = makeNewRIS(4);
/* 1077 */         ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*      */ 
/* 1080 */         byte[] arrayOfByte2 = new byte[512];
/*      */         while (true)
/*      */         {
/* 1085 */           int i = this._columnInputStream.read(arrayOfByte2, 0, 512);
/* 1086 */           if (i == -1) {
/*      */             break;
/*      */           }
/*      */ 
/* 1090 */           localByteArrayOutputStream.write(arrayOfByte2, 0, i);
/*      */         }
/*      */ 
/* 1093 */         arrayOfByte1 = localByteArrayOutputStream.toByteArray();
/* 1094 */         localByteArrayOutputStream.close();
/* 1095 */         this._columnInputStream.close();
/* 1096 */         this._columnInputStream = null;
/* 1097 */         this._dataLength = arrayOfByte1.length;
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1102 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     finally
/*      */     {
/* 1106 */       endRead();
/*      */     }
/* 1108 */     return arrayOfByte1;
/*      */   }
/*      */ 
/*      */   public TextPointer getTextPtr()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1120 */       beginRead();
/* 1121 */       if (!this._isNull);
/* 1123 */       switch (this._dataFmt._datatype)
/*      */       {
/*      */       case 34:
/*      */       case 35:
/*      */       case 174:
/* 1128 */         break;
/*      */       default:
/* 1130 */         ErrorMessage.raiseError("JZ0R4"); break label68:
/*      */ 
/* 1135 */         label68: ErrorMessage.raiseError("JZ0R4");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException1)
/*      */     {
/* 1142 */       ErrorMessage.raiseErrorCheckDead(localIOException1);
/*      */     }
/*      */     finally
/*      */     {
/* 1146 */       endRead();
/*      */       try
/*      */       {
/* 1150 */         if (this._dataFmt._datatype == 34)
/*      */         {
/* 1152 */           funkyBinaryReader();
/*      */         }
/*      */         else
/*      */         {
/* 1156 */           String str = readString();
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException2)
/*      */       {
/*      */       }
/*      */     }
/* 1163 */     TextPointer localTextPointer = new TextPointer(this._context);
/*      */ 
/* 1165 */     byte[] arrayOfByte1 = new byte[this._textptrlen];
/* 1166 */     byte[] arrayOfByte2 = new byte[8];
/* 1167 */     System.arraycopy(this._textptr, 0, arrayOfByte1, 0, this._textptrlen);
/* 1168 */     System.arraycopy(this._textptr, this._textptrlen, arrayOfByte2, 0, 8);
/* 1169 */     localTextPointer._textPtr = arrayOfByte1;
/* 1170 */     localTextPointer._timeStamp = arrayOfByte2;
/* 1171 */     localTextPointer._tableName = this._dataFmt._tableName;
/* 1172 */     localTextPointer._columnName = this._dataFmt.getName();
/* 1173 */     return localTextPointer;
/*      */   }
/*      */ 
/*      */   public DateObject getDateObject(int paramInt, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 1183 */     DateObject localDateObject = null;
/* 1184 */     int i = 0;
/*      */     try
/*      */     {
/* 1187 */       beginRead();
/* 1188 */       if (!this._isNull)
/*      */       {
/* 1190 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/* 1196 */           localDateObject = Convert.objectToDateObject(readString(), paramInt, paramCalendar);
/*      */ 
/* 1198 */           break;
/*      */         case 174:
/*      */         case 225:
/* 1201 */           if (this._dataFmt.isUnitype())
/*      */           {
/* 1203 */             localDateObject = Convert.objectToDateObject(readUnicodeString(), paramInt, paramCalendar);
/*      */           }
/*      */           else
/*      */           {
/* 1208 */             i = 1;
/*      */           }
/* 1210 */           break;
/*      */         case 58:
/*      */         case 61:
/*      */         case 111:
/* 1221 */           localDateObject = readDATETIMN().dateObjectValue(paramCalendar);
/* 1222 */           break;
/*      */         case 187:
/*      */         case 188:
/* 1225 */           localDateObject = readBIGDATETIMN(this._dataFmt._datatype).dateObjectValue(paramCalendar);
/* 1226 */           break;
/*      */         case 49:
/*      */         case 123:
/* 1229 */           if (paramInt == 92)
/*      */           {
/* 1234 */             ErrorMessage.raiseError("JZ0TI", "date", "time");
/*      */           }
/*      */           else
/*      */           {
/* 1240 */             localDateObject = readDATEN().dateObjectValue(paramCalendar);
/*      */           }
/* 1242 */           break;
/*      */         case 51:
/*      */         case 147:
/* 1245 */           if (paramInt == 91)
/*      */           {
/* 1250 */             ErrorMessage.raiseError("JZ0TI", "time", "date");
/*      */           }
/*      */           else
/*      */           {
/* 1256 */             localDateObject = readTIMEN().dateObjectValue(paramCalendar);
/*      */           }
/* 1258 */           break;
/*      */         default:
/* 1260 */           i = 1;
/*      */         }
/* 1262 */         if (i != 0)
/*      */         {
/* 1264 */           ErrorMessage.raiseError("JZ0TE", "char, unichar, varchar, univarchar, text, unitext, datetime, short datetime, date, time");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1274 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (IllegalArgumentException localIllegalArgumentException)
/*      */     {
/* 1278 */       ErrorMessage.raiseError("JZ009", localIllegalArgumentException.getMessage());
/*      */     }
/*      */     finally
/*      */     {
/* 1283 */       endRead();
/*      */     }
/* 1285 */     return localDateObject;
/*      */   }
/*      */ 
/*      */   public double getDouble()
/*      */     throws SQLException
/*      */   {
/* 1294 */     double d = 0.0D;
/* 1295 */     int i = 0;
/*      */     try
/*      */     {
/* 1298 */       beginRead();
/* 1299 */       if (!this._isNull)
/*      */       {
/* 1301 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 48:
/*      */         case 50:
/* 1305 */           d = readUnsignedByte();
/* 1306 */           break;
/*      */         case 52:
/* 1308 */           d = readShort();
/* 1309 */           break;
/*      */         case 65:
/* 1311 */           d = readUnsignedShortAsInt();
/* 1312 */           break;
/*      */         case 56:
/* 1314 */           d = readInt();
/* 1315 */           break;
/*      */         case 66:
/* 1317 */           d = readUnsignedIntAsLong();
/* 1318 */           break;
/*      */         case 191:
/* 1320 */           d = readLong();
/* 1321 */           break;
/*      */         case 67:
/* 1323 */           d = readUnsignedLongAsBigDecimal().doubleValue();
/* 1324 */           break;
/*      */         case 38:
/* 1326 */           d = readINTN();
/* 1327 */           break;
/*      */         case 68:
/* 1329 */           if (this._dataLength < 8)
/*      */           {
/* 1331 */             d = readINTN();
/*      */           }
/*      */           else
/*      */           {
/* 1335 */             d = readUnsignedLongAsBigDecimal().doubleValue();
/*      */           }
/* 1337 */           break;
/*      */         case 59:
/*      */         case 62:
/*      */         case 109:
/* 1341 */           d = readFLTN();
/* 1342 */           break;
/*      */         case 106:
/*      */         case 108:
/* 1345 */           BigDecimal localBigDecimal1 = readNUMERIC(this._dataFmt._precision, this._dataFmt._scale);
/*      */ 
/* 1347 */           if (localBigDecimal1 != null)
/*      */           {
/* 1349 */             d = localBigDecimal1.doubleValue(); } break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/* 1356 */           d = Double.valueOf(Convert.numString(readString())).doubleValue();
/*      */ 
/* 1358 */           break;
/*      */         case 174:
/*      */         case 225:
/* 1361 */           if (this._dataFmt.isUnitype())
/*      */           {
/* 1363 */             d = Double.valueOf(Convert.numString(readUnicodeString())).doubleValue();
/*      */           }
/*      */           else
/*      */           {
/* 1368 */             i = 1;
/*      */           }
/* 1370 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/* 1374 */           BigDecimal localBigDecimal2 = readMONEYN();
/* 1375 */           if (localBigDecimal2 != null)
/*      */           {
/* 1377 */             d = localBigDecimal2.doubleValue(); } break;
/*      */         default:
/* 1381 */           i = 1;
/*      */         }
/* 1383 */         if (i != 0)
/*      */         {
/* 1385 */           ErrorMessage.raiseError("JZ0TE", "tinyint, smallint, int, bit, float, decimal, numeric, char, unichar, varchar, univarchar, text, unitext, money, short money");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1395 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/* 1401 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */     }
/*      */     finally
/*      */     {
/* 1406 */       endRead();
/*      */     }
/* 1408 */     return d;
/*      */   }
/*      */ 
/*      */   public float getFloat()
/*      */     throws SQLException
/*      */   {
/* 1417 */     float f = 0.0F;
/* 1418 */     int i = 0;
/*      */     try
/*      */     {
/* 1421 */       beginRead();
/* 1422 */       if (!this._isNull)
/*      */       {
/* 1424 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 48:
/*      */         case 50:
/* 1428 */           f = readUnsignedByte();
/* 1429 */           break;
/*      */         case 52:
/* 1431 */           f = readShort();
/* 1432 */           break;
/*      */         case 65:
/* 1434 */           f = readUnsignedShortAsInt();
/* 1435 */           break;
/*      */         case 56:
/* 1437 */           f = readInt();
/* 1438 */           break;
/*      */         case 66:
/* 1440 */           f = (float)readUnsignedIntAsLong();
/* 1441 */           break;
/*      */         case 191:
/* 1443 */           f = (float)readLong();
/* 1444 */           break;
/*      */         case 38:
/* 1446 */           f = (float)readINTN();
/* 1447 */           break;
/*      */         case 67:
/* 1449 */           f = readUnsignedLongAsBigDecimal().floatValue();
/* 1450 */           break;
/*      */         case 68:
/* 1452 */           if (this._dataLength < 8)
/*      */           {
/* 1454 */             f = (float)readUINTN();
/*      */           }
/*      */           else
/*      */           {
/* 1458 */             f = readUnsignedLongAsBigDecimal().floatValue();
/*      */           }
/* 1460 */           break;
/*      */         case 59:
/* 1462 */           f = readFloat();
/* 1463 */           break;
/*      */         case 62:
/* 1465 */           f = (float)readDouble();
/* 1466 */           break;
/*      */         case 109:
/* 1468 */           f = (float)readFLTN();
/* 1469 */           break;
/*      */         case 106:
/*      */         case 108:
/* 1472 */           BigDecimal localBigDecimal1 = readNUMERIC(this._dataFmt._precision, this._dataFmt._scale);
/*      */ 
/* 1474 */           if (localBigDecimal1 != null)
/*      */           {
/* 1476 */             f = localBigDecimal1.floatValue(); } break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/* 1483 */           f = Float.valueOf(Convert.numString(readString())).floatValue();
/*      */ 
/* 1485 */           break;
/*      */         case 174:
/*      */         case 225:
/* 1488 */           if (this._dataFmt.isUnitype())
/*      */           {
/* 1490 */             f = Float.valueOf(Convert.numString(readUnicodeString())).floatValue();
/*      */           }
/*      */           else
/*      */           {
/* 1495 */             i = 1;
/*      */           }
/* 1497 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/* 1501 */           BigDecimal localBigDecimal2 = readMONEYN();
/* 1502 */           if (localBigDecimal2 != null)
/*      */           {
/* 1504 */             f = localBigDecimal2.floatValue(); } break;
/*      */         default:
/* 1508 */           i = 1;
/*      */         }
/* 1510 */         if (i != 0)
/*      */         {
/* 1512 */           ErrorMessage.raiseError("JZ0TE", "tinyint, smallint, int, bit, float, decimal, numeric, char, unichar, varchar, univarchar, text, unitext, money, short money, unsigned bigint");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1522 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/* 1528 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */     }
/*      */     finally
/*      */     {
/* 1533 */       endRead();
/*      */     }
/* 1535 */     return f;
/*      */   }
/*      */ 
/*      */   public int getInt()
/*      */     throws SQLException
/*      */   {
/* 1544 */     long l = 0L;
/* 1545 */     int i = 0;
/*      */     try
/*      */     {
/* 1548 */       beginRead();
/* 1549 */       if (!this._isNull)
/*      */       {
/* 1551 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 48:
/*      */         case 50:
/* 1555 */           l = readUnsignedByte();
/* 1556 */           break;
/*      */         case 52:
/* 1558 */           l = readShort();
/* 1559 */           break;
/*      */         case 65:
/* 1561 */           l = readUnsignedShortAsInt();
/* 1562 */           break;
/*      */         case 56:
/* 1564 */           l = readInt();
/* 1565 */           break;
/*      */         case 66:
/* 1567 */           l = readUnsignedIntAsLong();
/* 1568 */           break;
/*      */         case 191:
/* 1570 */           l = readLong();
/* 1571 */           break;
/*      */         case 38:
/* 1573 */           l = readINTN();
/* 1574 */           break;
/*      */         case 67:
/* 1576 */           l = readUnsignedLongAsBigDecimal().longValue();
/* 1577 */           break;
/*      */         case 68:
/* 1579 */           if (this._dataLength < 8)
/*      */           {
/* 1581 */             l = readUINTN();
/*      */           }
/*      */           else
/*      */           {
/* 1585 */             l = readUnsignedLongAsBigDecimal().longValue();
/*      */           }
/* 1587 */           break;
/*      */         case 59:
/*      */         case 62:
/*      */         case 109:
/* 1591 */           l = new Double(readFLTN()).longValue();
/* 1592 */           break;
/*      */         case 106:
/*      */         case 108:
/* 1595 */           l = readNUMERIC(this._dataFmt._precision, this._dataFmt._scale).longValue();
/*      */ 
/* 1597 */           break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/* 1605 */           l = new BigDecimal(Convert.numString(readString())).longValue();
/*      */ 
/* 1608 */           break;
/*      */         case 174:
/*      */         case 225:
/* 1611 */           if (this._dataFmt.isUnitype())
/*      */           {
/* 1613 */             l = new BigDecimal(Convert.numString(readUnicodeString())).longValue();
/*      */           }
/*      */           else
/*      */           {
/* 1618 */             i = 1;
/*      */           }
/* 1620 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/* 1624 */           l = readMONEYN().longValue();
/* 1625 */           break;
/*      */         default:
/* 1627 */           i = 1;
/*      */         }
/* 1629 */         if (i != 0)
/*      */         {
/* 1631 */           ErrorMessage.raiseError("JZ0TE", "tinyint, smallint, int, bit, float, decimal, numeric, char, unichar, varchar, univarchar, text, unitext, money, short money,unsigned bigint");
/*      */         }
/*      */ 
/* 1637 */         Convert.checkIntOflo(l);
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1642 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/* 1648 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */     }
/*      */     finally
/*      */     {
/* 1653 */       endRead();
/*      */     }
/* 1655 */     return (int)l;
/*      */   }
/*      */ 
/*      */   public long getLong()
/*      */     throws SQLException
/*      */   {
/* 1664 */     long l = 0L;
/* 1665 */     int i = 0;
/*      */     try
/*      */     {
/* 1668 */       beginRead();
/* 1669 */       if (!this._isNull)
/*      */       {
/* 1671 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 48:
/*      */         case 50:
/* 1675 */           l = readUnsignedByte();
/* 1676 */           break;
/*      */         case 52:
/* 1678 */           l = readShort();
/* 1679 */           break;
/*      */         case 65:
/* 1681 */           l = readUnsignedShortAsInt();
/* 1682 */           break;
/*      */         case 56:
/* 1684 */           l = readInt();
/* 1685 */           break;
/*      */         case 66:
/* 1687 */           l = readUnsignedIntAsLong();
/* 1688 */           break;
/*      */         case 191:
/* 1690 */           l = readLong();
/* 1691 */           break;
/*      */         case 38:
/* 1693 */           l = readINTN();
/* 1694 */           break;
/*      */         case 67:
/* 1696 */           l = readUnsignedLongAsBigDecimal().longValue();
/* 1697 */           break;
/*      */         case 68:
/* 1699 */           if (this._dataLength < 8)
/*      */           {
/* 1701 */             l = readUINTN();
/*      */           }
/*      */           else
/*      */           {
/* 1705 */             l = readUnsignedLongAsBigDecimal().longValue();
/*      */           }
/* 1707 */           break;
/*      */         case 59:
/*      */         case 62:
/*      */         case 109:
/* 1711 */           l = new Double(readFLTN()).longValue();
/* 1712 */           break;
/*      */         case 106:
/*      */         case 108:
/* 1715 */           l = readNUMERIC(this._dataFmt._precision, this._dataFmt._scale).longValue();
/*      */ 
/* 1717 */           break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/* 1725 */           l = new BigDecimal(Convert.numString(readString())).longValue();
/*      */ 
/* 1728 */           break;
/*      */         case 174:
/*      */         case 225:
/* 1731 */           if (this._dataFmt.isUnitype())
/*      */           {
/* 1733 */             l = new BigDecimal(Convert.numString(readUnicodeString())).longValue();
/*      */           }
/*      */           else
/*      */           {
/* 1738 */             i = 1;
/*      */           }
/* 1740 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/* 1744 */           l = readMONEYN().longValue();
/* 1745 */           break;
/*      */         default:
/* 1747 */           i = 1;
/*      */         }
/* 1749 */         if (i != 0)
/*      */         {
/* 1751 */           ErrorMessage.raiseError("JZ0TE", "tinyint, smallint, int, bit, float, decimal, numeric, char, unichar, varchar, univarchar, text, unitext,  money, short money, unsigned bigint");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1761 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/* 1767 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */     }
/*      */     finally
/*      */     {
/* 1772 */       endRead();
/*      */     }
/* 1774 */     return l;
/*      */   }
/*      */ 
/*      */   public Object getObject()
/*      */     throws SQLException
/*      */   {
/* 1783 */     Object localObject1 = null;
/*      */     try
/*      */     {
/* 1786 */       beginRead();
/* 1787 */       if (!this._isNull)
/*      */       {
/* 1789 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 50:
/* 1792 */           int i = readUnsignedByte();
/* 1793 */           localObject1 = new Boolean(i != 0);
/* 1794 */           break;
/*      */         case 48:
/* 1796 */           localObject1 = new Integer((byte)readUnsignedByte());
/* 1797 */           break;
/*      */         case 52:
/* 1799 */           localObject1 = new Integer(readShort());
/* 1800 */           break;
/*      */         case 65:
/* 1802 */           localObject1 = new Integer(readUnsignedShortAsInt());
/* 1803 */           break;
/*      */         case 56:
/* 1805 */           localObject1 = new Integer(readInt());
/* 1806 */           break;
/*      */         case 66:
/* 1808 */           localObject1 = new Long(readUnsignedIntAsLong());
/* 1809 */           break;
/*      */         case 191:
/* 1811 */           localObject1 = new Long(readLong());
/* 1812 */           break;
/*      */         case 67:
/* 1814 */           localObject1 = readUnsignedLongAsBigDecimal();
/* 1815 */           break;
/*      */         case 38:
/* 1817 */           if (this._dataLength <= 4)
/*      */           {
/* 1819 */             localObject1 = new Integer((int)readINTN());
/*      */           }
/*      */           else
/*      */           {
/* 1823 */             localObject1 = new Long(readINTN());
/*      */           }
/* 1825 */           break;
/*      */         case 68:
/* 1827 */           if (this._dataLength < 8)
/*      */           {
/* 1829 */             localObject1 = new Integer((int)readUINTN());
/*      */           }
/*      */           else
/*      */           {
/* 1833 */             localObject1 = readUnsignedLongAsBigDecimal();
/*      */           }
/* 1835 */           break;
/*      */         case 59:
/* 1838 */           localObject1 = new Float(readFloat());
/* 1839 */           break;
/*      */         case 62:
/*      */         case 109:
/* 1842 */           if (this._dataLength == 4)
/*      */           {
/* 1844 */             localObject1 = new Float(readFloat());
/*      */           }
/*      */           else
/*      */           {
/* 1848 */             localObject1 = new Double(readFLTN());
/*      */           }
/* 1850 */           break;
/*      */         case 106:
/*      */         case 108:
/* 1853 */           localObject1 = readNUMERIC(this._dataFmt._precision, this._dataFmt._scale);
/*      */ 
/* 1855 */           break;
/*      */         case 39:
/*      */         case 47:
/* 1858 */           localObject1 = readString();
/* 1859 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/* 1863 */           localObject1 = readMONEYN();
/* 1864 */           break;
/*      */         case 37:
/*      */         case 45:
/*      */         case 225:
/* 1868 */           if ((this._dataFmt._usertype == 35) || (this._dataFmt._usertype == 34))
/*      */           {
/* 1871 */             localObject1 = readUnicodeString();
/* 1872 */           }break;
/*      */         case 34:
/* 1876 */           localObject1 = funkyBinaryReader();
/* 1877 */           break;
/*      */         case 35:
/*      */         case 175:
/* 1881 */           localObject1 = readString();
/* 1882 */           break;
/*      */         case 174:
/* 1884 */           localObject1 = readUnicodeString();
/* 1885 */           break;
/*      */         case 58:
/*      */         case 61:
/*      */         case 111:
/* 1897 */           localObject1 = Convert.objectToTimestamp(readDATETIMN().dateObjectValue());
/*      */ 
/* 1899 */           break;
/*      */         case 187:
/*      */         case 188:
/* 1902 */           localObject1 = Convert.objectToTimestamp(readBIGDATETIMN(this._dataFmt._datatype).dateObjectValue());
/*      */ 
/* 1904 */           break;
/*      */         case 49:
/*      */         case 123:
/* 1907 */           localObject1 = Convert.objectToDate(readDATEN().dateObjectValue());
/*      */ 
/* 1909 */           break;
/*      */         case 51:
/*      */         case 147:
/* 1912 */           localObject1 = Convert.objectToTime(readTIMEN().dateObjectValue());
/*      */ 
/* 1914 */           break;
/*      */         case 103:
/*      */         case 104:
/* 1917 */           byte[] arrayOfByte = new byte[this._dataLength];
/* 1918 */           this.in.read(arrayOfByte, 0, this._dataLength);
/* 1919 */           localObject1 = arrayOfByte;
/* 1920 */           break;
/*      */         case 36:
/* 1924 */           switch (this._dataFmt._blobType)
/*      */           {
/*      */           case 1:
/* 1927 */             localObject1 = readObject();
/* 1928 */             break;
/*      */           case 3:
/* 1931 */             this._columnInputStream = makeNewRIS(3);
/*      */ 
/* 1933 */             localObject1 = this._columnInputStream;
/* 1934 */             break;
/*      */           case 5:
/* 1937 */             this._columnInputStream = makeNewRIS(5);
/*      */ 
/* 1939 */             localObject1 = this._columnInputStream;
/* 1940 */             break;
/*      */           case 4:
/* 1943 */             this._columnInputStream = makeNewRIS(4);
/* 1944 */             localObject1 = this._columnInputStream;
/* 1945 */             break;
/*      */           case 2:
/* 1948 */             break;
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/* 1952 */             localObject1 = readLobInfo();
/*      */           }
/*      */ 
/* 1955 */           break;
/*      */         default:
/* 1957 */           ErrorMessage.raiseError("JZ0TC");
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1964 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/* 1970 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */     }
/*      */     finally
/*      */     {
/* 1976 */       if (this._columnInputStream == null)
/*      */       {
/* 1978 */         endRead();
/*      */       }
/*      */     }
/* 1981 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public short getShort()
/*      */     throws SQLException
/*      */   {
/* 1990 */     long l = 0L;
/* 1991 */     int i = 0;
/*      */     try
/*      */     {
/* 1994 */       beginRead();
/* 1995 */       if (!this._isNull)
/*      */       {
/* 1997 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 48:
/*      */         case 50:
/* 2001 */           l = readUnsignedByte();
/* 2002 */           break;
/*      */         case 52:
/* 2004 */           l = readShort();
/* 2005 */           break;
/*      */         case 65:
/* 2007 */           l = readUnsignedShortAsInt();
/* 2008 */           break;
/*      */         case 56:
/* 2010 */           l = readInt();
/* 2011 */           break;
/*      */         case 66:
/* 2013 */           l = readUnsignedIntAsLong();
/* 2014 */           break;
/*      */         case 191:
/* 2016 */           l = readLong();
/* 2017 */           break;
/*      */         case 38:
/* 2019 */           l = readINTN();
/* 2020 */           break;
/*      */         case 67:
/* 2022 */           l = readUnsignedLongAsBigDecimal().longValue();
/* 2023 */           break;
/*      */         case 68:
/* 2025 */           if (this._dataLength < 8)
/*      */           {
/* 2027 */             l = readUINTN();
/*      */           }
/*      */           else
/*      */           {
/* 2031 */             l = readUnsignedLongAsBigDecimal().longValue();
/*      */           }
/* 2033 */           break;
/*      */         case 59:
/*      */         case 62:
/*      */         case 109:
/* 2037 */           l = new Double(readFLTN()).longValue();
/* 2038 */           break;
/*      */         case 106:
/*      */         case 108:
/* 2041 */           l = readNUMERIC(this._dataFmt._precision, this._dataFmt._scale).longValue();
/*      */ 
/* 2043 */           break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/* 2051 */           l = new BigDecimal(Convert.numString(readString())).longValue();
/*      */ 
/* 2054 */           break;
/*      */         case 174:
/*      */         case 225:
/* 2057 */           if (this._dataFmt.isUnitype())
/*      */           {
/* 2059 */             l = new BigDecimal(Convert.numString(readUnicodeString())).longValue();
/*      */           }
/*      */           else
/*      */           {
/* 2064 */             i = 1;
/*      */           }
/* 2066 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/* 2070 */           l = readMONEYN().longValue();
/* 2071 */           break;
/*      */         default:
/* 2073 */           i = 1;
/*      */         }
/*      */ 
/* 2076 */         if (i != 0)
/*      */         {
/* 2078 */           ErrorMessage.raiseError("JZ0TE", "tinyint, smallint, int, bit, float, decimal, numeric, char, unichar, varchar, univarchar, text, unitext, money, short money, unsigned bigint");
/*      */         }
/*      */ 
/* 2084 */         Convert.checkShortOflo(l);
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 2089 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/* 2095 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */     }
/*      */     finally
/*      */     {
/* 2100 */       endRead();
/*      */     }
/* 2102 */     return (short)(int)l;
/*      */   }
/*      */ 
/*      */   public String getString()
/*      */     throws SQLException
/*      */   {
/* 2111 */     String str1 = null;
/*      */     try
/*      */     {
/* 2114 */       beginRead();
/* 2115 */       if (!this._isNull)
/*      */       {
/* 2117 */         switch (this._dataFmt._datatype)
/*      */         {
/*      */         case 48:
/*      */         case 50:
/* 2121 */           str1 = String.valueOf(readUnsignedByte());
/* 2122 */           break;
/*      */         case 52:
/* 2124 */           str1 = String.valueOf(readShort());
/* 2125 */           break;
/*      */         case 65:
/* 2127 */           str1 = String.valueOf(readUnsignedShortAsInt());
/* 2128 */           break;
/*      */         case 56:
/* 2130 */           str1 = String.valueOf(readInt());
/* 2131 */           break;
/*      */         case 66:
/* 2133 */           str1 = String.valueOf(readUnsignedIntAsLong());
/* 2134 */           break;
/*      */         case 191:
/* 2136 */           str1 = String.valueOf(readLong());
/* 2137 */           break;
/*      */         case 67:
/* 2139 */           str1 = String.valueOf(readUnsignedLongAsBigDecimal());
/* 2140 */           break;
/*      */         case 38:
/* 2142 */           str1 = String.valueOf(readINTN());
/* 2143 */           break;
/*      */         case 68:
/* 2145 */           if (this._dataLength < 8)
/*      */           {
/* 2147 */             str1 = String.valueOf(readUINTN());
/*      */           }
/*      */           else
/*      */           {
/* 2151 */             str1 = String.valueOf(readUnsignedLongAsBigDecimal());
/*      */           }
/* 2153 */           break;
/*      */         case 59:
/* 2155 */           str1 = String.valueOf(readFloat());
/* 2156 */           break;
/*      */         case 62:
/* 2158 */           str1 = String.valueOf(readDouble());
/* 2159 */           break;
/*      */         case 109:
/* 2161 */           str1 = String.valueOf(readFLTN());
/* 2162 */           break;
/*      */         case 106:
/*      */         case 108:
/* 2165 */           BigDecimal localBigDecimal1 = readNUMERIC(this._dataFmt._precision, this._dataFmt._scale);
/*      */ 
/* 2167 */           if (localBigDecimal1 != null)
/*      */           {
/* 2169 */             str1 = localBigDecimal1.toString(); } break;
/*      */         case 35:
/*      */         case 39:
/*      */         case 47:
/*      */         case 175:
/* 2176 */           str1 = readString();
/* 2177 */           break;
/*      */         case 34:
/*      */         case 37:
/*      */         case 45:
/*      */         case 103:
/*      */         case 104:
/* 2184 */           if (this._holds0LNN)
/*      */           {
/* 2186 */             str1 = "";
/*      */           }
/*      */           else
/*      */           {
/* 2190 */             str1 = HexConverts.hexConvert(funkyBinaryReader());
/*      */           }
/*      */ 
/* 2193 */           break;
/*      */         case 225:
/* 2195 */           if ((this._dataFmt._usertype == 35) || (this._dataFmt._usertype == 34))
/*      */           {
/* 2198 */             str1 = readUnicodeString();
/*      */           }
/* 2202 */           else if (this._holds0LNN)
/*      */           {
/* 2204 */             str1 = "";
/*      */           }
/*      */           else
/*      */           {
/* 2208 */             str1 = HexConverts.hexConvert(funkyBinaryReader());
/*      */           }
/*      */ 
/* 2212 */           break;
/*      */         case 174:
/* 2214 */           str1 = readUnicodeString();
/* 2215 */           break;
/*      */         case 60:
/*      */         case 110:
/*      */         case 122:
/* 2219 */           BigDecimal localBigDecimal2 = readMONEYN();
/* 2220 */           if (localBigDecimal2 != null)
/*      */           {
/* 2222 */             str1 = localBigDecimal2.toString(); } break;
/*      */         case 58:
/*      */         case 61:
/*      */         case 111:
/* 2228 */           str1 = readDATETIMN().stringValue();
/* 2229 */           break;
/*      */         case 187:
/*      */         case 188:
/* 2232 */           str1 = readBIGDATETIMN(this._dataFmt._datatype).stringValue();
/* 2233 */           break;
/*      */         case 49:
/*      */         case 123:
/* 2236 */           str1 = readDATEN().stringValue();
/* 2237 */           break;
/*      */         case 51:
/*      */         case 147:
/* 2240 */           str1 = readTIMEN().stringValue();
/* 2241 */           break;
/*      */         case 36:
/*      */           InputStreamReader localInputStreamReader;
/*      */           char[] arrayOfChar;
/*      */           StringBuffer localStringBuffer;
/* 2243 */           switch (this._dataFmt._blobType)
/*      */           {
/*      */           case 1:
/* 2246 */             Object localObject1 = readObject();
/* 2247 */             if (localObject1 == null)
/*      */             {
/* 2249 */               str1 = null;
/*      */             }
/*      */             else
/*      */             {
/* 2253 */               str1 = localObject1.toString();
/*      */             }
/* 2255 */             break;
/*      */           case 3:
/* 2258 */             this._columnInputStream = makeNewRIS(4);
/*      */ 
/* 2262 */             localInputStreamReader = null;
/* 2263 */             if (this._tds._charsetName.equals("x-SybUTF8"))
/*      */             {
/* 2265 */               localInputStreamReader = new InputStreamReader(this._columnInputStream, SybUTF8Charset.getInstance());
/*      */             }
/*      */             else
/*      */             {
/* 2270 */               localInputStreamReader = new InputStreamReader(this._columnInputStream, this._tds._charsetName);
/*      */             }
/*      */ 
/* 2274 */             arrayOfChar = new char[512];
/* 2275 */             localStringBuffer = new StringBuffer();
/*      */             while (true)
/*      */             {
/* 2278 */               int i = localInputStreamReader.read(arrayOfChar, 0, 512);
/* 2279 */               if (i == -1) {
/*      */                 break;
/*      */               }
/*      */ 
/* 2283 */               localStringBuffer.append(arrayOfChar, 0, i);
/*      */             }
/* 2285 */             str1 = localStringBuffer.toString();
/* 2286 */             localInputStreamReader.close();
/* 2287 */             this._columnInputStream.close();
/* 2288 */             this._columnInputStream = null;
/* 2289 */             break;
/*      */           case 5:
/* 2292 */             this._columnInputStream = makeNewRIS(5);
/*      */ 
/* 2294 */             String str2 = null;
/* 2295 */             if (getBigEndian())
/*      */             {
/* 2297 */               str2 = "UnicodeBig";
/*      */             }
/*      */             else
/*      */             {
/* 2301 */               str2 = "UnicodeLittle";
/*      */             }
/*      */ 
/* 2304 */             localInputStreamReader = new InputStreamReader(this._columnInputStream, str2);
/*      */ 
/* 2306 */             arrayOfChar = new char[512];
/* 2307 */             localStringBuffer = new StringBuffer();
/*      */             while (true)
/*      */             {
/* 2310 */               int j = localInputStreamReader.read(arrayOfChar, 0, 512);
/* 2311 */               if (j == -1) {
/*      */                 break;
/*      */               }
/*      */ 
/* 2315 */               localStringBuffer.append(arrayOfChar, 0, j);
/*      */             }
/* 2317 */             str1 = localStringBuffer.toString();
/* 2318 */             localInputStreamReader.close();
/* 2319 */             this._columnInputStream.close();
/* 2320 */             this._columnInputStream = null;
/* 2321 */             break;
/*      */           case 4:
/* 2324 */             this._columnInputStream = makeNewRIS(4);
/*      */ 
/* 2326 */             byte[] arrayOfByte = new byte[512];
/* 2327 */             localStringBuffer = new StringBuffer();
/*      */             while (true)
/*      */             {
/* 2330 */               int k = this._columnInputStream.read(arrayOfByte, 0, 512);
/*      */ 
/* 2332 */               if (k == -1) {
/*      */                 break;
/*      */               }
/*      */ 
/* 2336 */               localStringBuffer.append(HexConverts.hexConvert(arrayOfByte, k));
/*      */             }
/* 2338 */             str1 = localStringBuffer.toString();
/* 2339 */             this._columnInputStream.close();
/* 2340 */             this._columnInputStream = null;
/* 2341 */             break;
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/* 2345 */             str1 = readLobInfo().getString();
/* 2346 */             break;
/*      */           case 2:
/*      */           default:
/* 2352 */             ErrorMessage.raiseError("JZ0TC");
/*      */           }
/* 2354 */           break;
/*      */         default:
/* 2362 */           ErrorMessage.raiseError("JZ0TC");
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 2369 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/* 2375 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */     }
/*      */     finally
/*      */     {
/* 2380 */       endRead();
/*      */     }
/* 2382 */     return str1;
/*      */   }
/*      */ 
/*      */   public Blob getBlob()
/*      */     throws SQLException
/*      */   {
/* 2388 */     SybBinaryClientLob localSybBinaryClientLob = null;
/* 2389 */     if (this._dataFmt._datatype == 34)
/*      */     {
/*      */       try
/*      */       {
/* 2393 */         beginRead();
/* 2394 */         localSybBinaryClientLob = (this._isNull) ? null : new SybBinaryClientLob(this._logId, this._context, funkyBinaryReader());
/*      */ 
/* 2396 */         endRead();
/* 2397 */         return localSybBinaryClientLob;
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 2401 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */       }
/*      */     }
/* 2404 */     return (SybBinaryLob)getLob(7, false);
/*      */   }
/*      */ 
/*      */   public Clob getClob()
/*      */     throws SQLException
/*      */   {
/* 2461 */     SybCharClientLob localSybCharClientLob = null;
/* 2462 */     if (this._dataFmt._datatype == 35);
/*      */     try
/*      */     {
/* 2466 */       beginRead();
/* 2467 */       localSybCharClientLob = (this._isNull) ? null : new SybCharClientLob(this._logId, this._context, new StringBuffer(readString()), 1);
/*      */ 
/* 2469 */       endRead();
/* 2470 */       return localSybCharClientLob;
/*      */     }
/*      */     catch (IOException localIOException2)
/*      */     {
/* 2474 */       ErrorMessage.raiseErrorCheckDead(localIOException1);
/* 2475 */       break label188:
/*      */ 
/* 2477 */       if (this._dataFmt._datatype == 174);
/*      */       try
/*      */       {
/* 2481 */         beginRead();
/*      */ 
/* 2485 */         localSybCharClientLob = (this._isNull) ? null : new SybCharClientLob(this._logId, this._context, new StringBuffer(readUnicodeString()), 1);
/*      */ 
/* 2487 */         endRead();
/* 2488 */         return localSybCharClientLob;
/*      */       }
/*      */       catch (IOException localIOException2)
/*      */       {
/* 2492 */         ErrorMessage.raiseErrorCheckDead(localIOException2);
/* 2493 */         break label188:
/*      */ 
/* 2495 */         if ((this._dataFmt._blobType == 6) || (this._dataFmt._blobType == 8))
/*      */         {
/* 2497 */           return (SybCharLob)getLob(this._dataFmt._blobType, false);
/*      */         }
/*      */ 
/* 2501 */         ErrorMessage.raiseError("JZ0TE", "(text, Clob) (unitext, Clob) (unitext, NClob) (unitext, Clob) (image, Blob)");
/*      */       }
/*      */     }
/* 2504 */     label188: return null;
/*      */   }
/*      */ 
/*      */   private SybLob getLob(int paramInt, boolean paramBoolean) throws SQLException
/*      */   {
/* 2509 */     SybLob localSybLob = null;
/* 2510 */     int i = 0;
/*      */     try
/*      */     {
/* 2513 */       beginRead();
/*      */ 
/* 2515 */       switch (this._dataFmt._datatype)
/*      */       {
/*      */       case 36:
/* 2518 */         if (this._dataFmt._blobType == paramInt)
/*      */         {
/* 2520 */           localSybLob = readLobInfo();
/* 2521 */           if ((this._lobLength != 0L) || (paramBoolean))
/*      */             break label80;
/* 2523 */           localSybLob = null; } break;
/*      */       default:
/* 2528 */         i = 1;
/* 2529 */         label80: ErrorMessage.raiseError("JZ0TE", "(text, Clob) (unitext, Clob) (unitext, NClob) (unitext, Clob) (image, Blob)");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 2537 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     finally
/*      */     {
/* 2541 */       if (i == 0)
/*      */       {
/* 2543 */         endRead();
/*      */       }
/*      */     }
/* 2546 */     return localSybLob;
/*      */   }
/*      */ 
/*      */   protected SybLob readLobInfo() throws IOException, SQLException
/*      */   {
/* 2551 */     Object localObject = null;
/* 2552 */     RawInputStream localRawInputStream = makeNewRIS(4);
/* 2553 */     localRawInputStream.close();
/*      */ 
/* 2555 */     switch (this._dataFmt._blobType)
/*      */     {
/*      */     case 7:
/* 2558 */       localObject = new SybBinaryLob(this._logId, this._context, this._locator);
/* 2559 */       break;
/*      */     case 6:
/* 2561 */       localObject = new SybCharLob(this._logId, this._context, this._locator, 1);
/* 2562 */       break;
/*      */     case 8:
/* 2564 */       localObject = new SybCharLob(this._logId, this._context, this._locator, 2);
/*      */     }
/*      */ 
/* 2567 */     ((SybLob)localObject).setLobLength(this._lobLength);
/* 2568 */     return (SybLob)localObject;
/*      */   }
/*      */ 
/*      */   public boolean isNull()
/*      */     throws SQLException
/*      */   {
/* 2575 */     switch (this._dataFmt._datatype)
/*      */     {
/*      */     case 36:
/* 2578 */       switch (this._dataFmt._blobType)
/*      */       {
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/* 2583 */         return this._lobLength == 0L;
/*      */       }
/*      */     }
/* 2586 */     return this._isNull;
/*      */   }
/*      */ 
/*      */   public void open(boolean paramBoolean)
/*      */   {
/* 2599 */     if (paramBoolean)
/*      */     {
/* 2601 */       this._state = 2;
/*      */     }
/*      */     else
/*      */     {
/* 2605 */       this._state = 1;
/*      */     }
/*      */ 
/* 2609 */     if (this._context._lastResult != 209)
/*      */       return;
/* 2611 */     this._context._lastResult = -1;
/*      */   }
/*      */ 
/*      */   public int getState()
/*      */   {
/* 2621 */     return this._state;
/*      */   }
/*      */ 
/*      */   public void setManager(CacheManager paramCacheManager)
/*      */   {
/* 2631 */     this._monitor = paramCacheManager;
/*      */   }
/*      */ 
/*      */   public void cache()
/*      */     throws IOException
/*      */   {
/* 2641 */     if (this._state == 3) return;
/* 2642 */     if (this._monitor == null)
/*      */     {
/* 2646 */       startRead();
/*      */ 
/* 2648 */       if ((this._context._rereadable) && (((this._state == 0) || (this._columnInputStream == null))))
/*      */       {
/* 2651 */         openCacheStream();
/*      */       }
/*      */     }
/* 2654 */     this._state = 2;
/* 2655 */     if (this._columnInputStream != null)
/*      */     {
/* 2657 */       if (this._context._rereadable)
/*      */       {
/* 2659 */         this._columnInputStream.cache((CacheStream)this.in);
/*      */       }
/*      */       else
/*      */       {
/* 2663 */         this._columnInputStream.close();
/* 2664 */         this._columnInputStream = null;
/*      */       }
/* 2666 */       this._state = 3;
/* 2667 */       return;
/*      */     }
/*      */ 
/* 2671 */     getSize();
/*      */ 
/* 2679 */     if ((this._dataLength == -1) && (this._columnInputStream == null))
/*      */     {
/*      */       try
/*      */       {
/* 2684 */         this._columnInputStream = makeNewRIS(4);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 2688 */         ErrorMessage.raiseIOECheckDead(localSQLException);
/*      */       }
/* 2690 */       this._columnInputStream.cache((CacheStream)this.in);
/* 2691 */       this._state = 3;
/* 2692 */       reset();
/* 2693 */       this._columnInputStream = null;
/* 2694 */       return;
/*      */     }
/*      */ 
/* 2697 */     byte[] arrayOfByte = new byte[512];
/* 2698 */     int i = this._dataLength;
/* 2699 */     while (i > 0)
/*      */     {
/* 2702 */       int j = (i < 512) ? i : 512;
/* 2703 */       this.in.read(arrayOfByte, 0, j);
/* 2704 */       i -= j;
/*      */     }
/* 2706 */     if (this._monitor != null) this._monitor.doneReading();
/* 2707 */     this._state = 3;
/* 2708 */     if (this._monitor == null)
/*      */       return;
/* 2710 */     this.in.reset();
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */     throws IOException
/*      */   {
/* 2721 */     if (this._columnInputStream != null)
/*      */     {
/* 2723 */       this._columnInputStream.close();
/* 2724 */       this._columnInputStream = null;
/*      */     }
/*      */     else
/*      */     {
/* 2728 */       switch (this._state)
/*      */       {
/*      */       case 0:
/* 2731 */         if (this._monitor == null)
/*      */         {
/* 2734 */           skipParam(); } break;
/*      */       case 1:
/* 2738 */         skipParam();
/* 2739 */         if (this._monitor != null) this._monitor.doneReading(); break;
/*      */       case 2:
/* 2742 */         getSize();
/*      */ 
/* 2745 */         this.in.skip(this._dataLength);
/* 2746 */         if (this._monitor != null) this._monitor.doneReading();
/*      */       case 3:
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2752 */     initialize();
/*      */   }
/*      */ 
/*      */   private void skipParam()
/*      */     throws IOException
/*      */   {
/* 2760 */     getSize();
/* 2761 */     if (this._dataLength == -1)
/*      */     {
/*      */       try
/*      */       {
/* 2766 */         this._columnInputStream = makeNewRIS(4);
/* 2767 */         this._columnInputStream.close();
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 2771 */         ErrorMessage.raiseIOECheckDead(localSQLException);
/*      */       }
/*      */       finally
/*      */       {
/* 2775 */         this._columnInputStream = null;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/* 2780 */       this.in.skip(this._dataLength);
/*      */   }
/*      */ 
/*      */   public void resetInputStream(InputStream paramInputStream)
/*      */   {
/* 2792 */     this.in = paramInputStream;
/*      */   }
/*      */ 
/*      */   public int available()
/*      */     throws IOException
/*      */   {
/* 2800 */     return (this._dataLength != -1) ? this._dataLength : this.in.available();
/*      */   }
/*      */ 
/*      */   public void initialize()
/*      */   {
/* 2810 */     super.initialize();
/* 2811 */     this._columnInputStream = null;
/*      */ 
/* 2813 */     if (!this.in instanceof CacheStream)
/*      */       return;
/*      */     try
/*      */     {
/* 2817 */       this.in.close();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */ 
/* 2823 */     this.in = this._context._in;
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */     throws IOException
/*      */   {
/* 2833 */     if ((this._state == 3) && (!this._isNull))
/*      */     {
/* 2836 */       this.in.reset();
/* 2837 */       if (this._monitor == null)
/*      */       {
/*      */         return;
/*      */       }
/*      */ 
/* 2848 */       this._dataLength = -2;
/* 2849 */       getSize();
/*      */     }
/*      */     else
/*      */     {
/* 2854 */       ErrorMessage.raiseIOException("JZ0P7");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void beginReadAsBytes()
/*      */     throws IOException
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void beginRead()
/*      */     throws IOException
/*      */   {
/* 2872 */     if ((this._monitor == null) && (this._state == 0) && 
/* 2875 */       (this._prev != null) && (this._prev._state != 3))
/*      */     {
/* 2877 */       this._prev.cache();
/*      */     }
/*      */ 
/* 2880 */     if (this._columnInputStream != null)
/*      */     {
/* 2882 */       if (this._context._rereadable)
/*      */       {
/* 2885 */         this._columnInputStream.cache((CacheStream)this.in);
/* 2886 */         this._state = 3;
/*      */       }
/*      */       else
/*      */       {
/* 2890 */         ErrorMessage.raiseIOException("JZ0R3");
/*      */       }
/*      */     }
/* 2893 */     getSize();
/* 2894 */     if (this._isNull)
/*      */     {
/* 2898 */       return;
/*      */     }
/* 2900 */     if (this._monitor == null)
/*      */     {
/* 2902 */       if (this._state == 0)
/*      */       {
/* 2904 */         if (this._context._rereadable)
/*      */         {
/* 2906 */           openCacheStream();
/*      */         }
/* 2908 */         this._state = 1;
/*      */       }
/*      */     }
/* 2911 */     else if (this._state == 0)
/*      */     {
/* 2913 */       ErrorMessage.raiseIOException("JZ0R3");
/*      */     }
/*      */ 
/* 2917 */     if (this._state != 3)
/*      */       return;
/* 2919 */     if (this._context._rereadable)
/*      */     {
/* 2921 */       reset();
/*      */     }
/*      */     else
/*      */     {
/* 2925 */       ErrorMessage.raiseIOException("JZ0R3");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void endRead()
/*      */     throws SQLException
/*      */   {
/* 2940 */     if ((this._state != 3) && 
/* 2942 */       (this._monitor != null))
/*      */     {
/* 2944 */       this._monitor.doneReading();
/*      */     }
/*      */ 
/* 2947 */     this._state = 3;
/*      */   }
/*      */ 
/*      */   protected RawInputStream makeNewRIS(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2957 */     if (this._columnInputStream != null)
/*      */     {
/* 2959 */       if (this._context._rereadable)
/*      */       {
/*      */         try
/*      */         {
/* 2963 */           this._columnInputStream.close();
/* 2964 */           this._state = 3;
/* 2965 */           reset();
/*      */         }
/*      */         catch (IOException localIOException1)
/*      */         {
/* 2969 */           ErrorMessage.raiseErrorCheckDead(localIOException1);
/*      */         }
/*      */         finally
/*      */         {
/* 2973 */           this._columnInputStream = null;
/*      */         }
/*      */ 
/*      */       }
/*      */       else {
/* 2978 */         ErrorMessage.raiseError("JZ0R3");
/*      */       }
/*      */     }
/* 2981 */     Object localObject1 = null;
/* 2982 */     CacheManager localCacheManager = null;
/*      */ 
/* 2985 */     int i = this._dataLength;
/* 2986 */     if ((this._dataFmt._datatype == 37) && (this._dataFmt._usertype == 3))
/*      */     {
/* 2990 */       i = this._dataFmt._length;
/*      */     }
/* 2992 */     if (this._context._maxFieldSize > 0)
/*      */     {
/* 2994 */       i = Math.min(i, this._context._maxFieldSize);
/*      */     }
/* 2996 */     if (this._state != 3)
/*      */     {
/* 3001 */       localCacheManager = this._monitor;
/*      */ 
/* 3003 */       this._state = 1;
/*      */     }
/*      */     try
/*      */     {
/* 3007 */       switch (paramInt)
/*      */       {
/*      */       case 4:
/* 3017 */         localObject1 = new RawInputStream(this, this._dataLength, i, localCacheManager);
/* 3018 */         break;
/*      */       case 1:
/* 3020 */         localObject1 = new RawToAsciiInputStream(this, this._dataLength, i, localCacheManager);
/*      */ 
/* 3022 */         break;
/*      */       case 3:
/* 3028 */         localObject1 = new CharsetToUniInputStream(this, this._dataLength, i, localCacheManager, this._tds._charsetName);
/*      */ 
/* 3030 */         break;
/*      */       case 2:
/* 3036 */         int j = (this._dataLength == -1) ? -1 : 2 * this._dataLength;
/*      */ 
/* 3038 */         int k = (i == -1) ? -1 : 2 * i;
/*      */ 
/* 3040 */         localObject1 = new CharsetToUniInputStream(new RawToAsciiInputStream(this, this._dataLength, i, localCacheManager), j, k, null, "ISO8859_1");
/*      */ 
/* 3044 */         break;
/*      */       case 5:
/* 3055 */         if (getBigEndian())
/*      */         {
/* 3058 */           localObject1 = new UnicharToUniInputStream(this, this._dataLength, i, localCacheManager, false);
/*      */         }
/*      */         else
/*      */         {
/* 3065 */           localObject1 = new UnicharToUniInputStream(this, this._dataLength, i, localCacheManager, true);
/*      */         }
/*      */ 
/* 3068 */         break;
/*      */       case 6:
/* 3077 */         if (i != this._dataLength)
/*      */         {
/* 3079 */           if (i * 2 < this._dataLength)
/*      */           {
/* 3081 */             i *= 2;
/*      */           }
/*      */           else
/*      */           {
/* 3085 */             i = this._dataLength;
/*      */           }
/*      */         }
/* 3088 */         localObject1 = new UnicharToAsciiInputStream(this, this._dataLength, i, getBigEndian(), localCacheManager);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException2)
/*      */     {
/* 3099 */       ErrorMessage.raiseError("JZ0I5", localIOException2.toString());
/*      */     }
/*      */ 
/* 3102 */     ((RawInputStream)localObject1).setCached(this._state == 3);
/* 3103 */     return (RawInputStream)localObject1;
/*      */   }
/*      */ 
/*      */   protected static String getObjectClassName(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws SQLException
/*      */   {
/* 3112 */     String str = "java.lang.Object";
/*      */ 
/* 3114 */     switch (paramInt1)
/*      */     {
/*      */     case 50:
/* 3117 */       str = "java.lang.Boolean";
/* 3118 */       break;
/*      */     case 48:
/*      */     case 52:
/*      */     case 56:
/*      */     case 65:
/* 3123 */       str = "java.lang.Integer";
/* 3124 */       break;
/*      */     case 66:
/*      */     case 191:
/* 3127 */       str = "java.lang.Long";
/* 3128 */       break;
/*      */     case 67:
/* 3130 */       str = "java.math.BigDecimal";
/* 3131 */       break;
/*      */     case 68:
/* 3133 */       switch (paramInt4)
/*      */       {
/*      */       case 2:
/* 3136 */         str = "java.lang.Integer";
/* 3137 */         break;
/*      */       case 4:
/* 3139 */         str = "java.lang.Long";
/* 3140 */         break;
/*      */       case 8:
/* 3142 */         str = "java.math.BigDecimal";
/*      */       }
/*      */ 
/* 3145 */       break;
/*      */     case 38:
/* 3147 */       if (paramInt4 <= 4)
/*      */       {
/* 3149 */         str = "java.lang.Integer"; break label595:
/*      */       }
/*      */ 
/* 3153 */       str = "java.lang.Long";
/*      */ 
/* 3155 */       break;
/*      */     case 59:
/* 3157 */       str = "java.lang.Float";
/* 3158 */       break;
/*      */     case 62:
/*      */     case 109:
/* 3161 */       if (paramInt4 == 4)
/*      */       {
/* 3163 */         str = "java.lang.Float"; break label595:
/*      */       }
/*      */ 
/* 3167 */       str = "java.lang.Double";
/*      */ 
/* 3169 */       break;
/*      */     case 35:
/*      */     case 39:
/*      */     case 47:
/*      */     case 174:
/*      */     case 175:
/* 3175 */       str = "java.lang.String";
/* 3176 */       break;
/*      */     case 60:
/*      */     case 106:
/*      */     case 108:
/*      */     case 110:
/*      */     case 122:
/* 3182 */       str = "java.math.BigDecimal";
/* 3183 */       break;
/*      */     case 34:
/*      */     case 37:
/*      */     case 45:
/*      */     case 103:
/*      */     case 104:
/* 3189 */       str = new byte[1].getClass().getName();
/* 3190 */       break;
/*      */     case 225:
/* 3192 */       if ((paramInt2 == 34) || (paramInt2 == 35))
/*      */       {
/* 3194 */         str = "java.lang.String"; break label595:
/*      */       }
/*      */ 
/* 3198 */       str = new byte[1].getClass().getName();
/*      */ 
/* 3200 */       break;
/*      */     case 58:
/*      */     case 61:
/*      */     case 111:
/*      */     case 187:
/* 3205 */       str = "java.sql.Timestamp";
/* 3206 */       break;
/*      */     case 49:
/*      */     case 123:
/* 3209 */       str = "java.sql.Date";
/* 3210 */       break;
/*      */     case 51:
/*      */     case 147:
/*      */     case 188:
/* 3214 */       str = "java.sql.Time";
/* 3215 */       break;
/*      */     case 36:
/* 3217 */       switch (paramInt3)
/*      */       {
/*      */       case 1:
/*      */       case 2:
/* 3221 */         str = "java.lang.Object";
/* 3222 */         break;
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/* 3226 */         str = "java.io.FilterInputStream";
/*      */       }
/*      */ 
/* 3229 */       break;
/*      */     default:
/* 3231 */       ErrorMessage.raiseError("JZ0TC");
/*      */     }
/*      */ 
/* 3234 */     label595: return str;
/*      */   }
/*      */ 
/*      */   private void openCacheStream()
/*      */   {
/* 3240 */     this.in = new CacheStream(this._context._cm, this.in, this._context._timeout);
/*      */   }
/*      */ 
/*      */   private Object readObject()
/*      */     throws SQLException
/*      */   {
/* 3249 */     Object localObject1 = null;
/*      */     try
/*      */     {
/* 3254 */       this._columnInputStream = makeNewRIS(4);
/* 3255 */       if (this._columnInputStream.available() > 0)
/*      */       {
/*      */         Object localObject2;
/* 3258 */         if (this._classLoader == null)
/*      */         {
/* 3260 */           localObject2 = new ObjectInputStream(this._columnInputStream);
/*      */         }
/*      */         else
/*      */         {
/* 3265 */           localObject2 = new DynamicObjectInputStream(this._columnInputStream, this._classLoader);
/*      */         }
/*      */ 
/* 3268 */         localObject1 = ((ObjectInputStream)localObject2).readObject();
/* 3269 */         ((ObjectInputStream)localObject2).close();
/*      */       }
/*      */       else
/*      */       {
/* 3274 */         this._isNull = true;
/*      */       }
/* 3276 */       this._columnInputStream.close();
/* 3277 */       this._columnInputStream = null;
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 3281 */       ErrorMessage.raiseError("JZ010", localException.getMessage());
/*      */     }
/*      */ 
/* 3284 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public Blob getInitializedBlob()
/*      */     throws SQLException
/*      */   {
/* 3291 */     Blob localBlob = (Blob)getLob(7, true);
/* 3292 */     return localBlob;
/*      */   }
/*      */ 
/*      */   public Clob getInitializedClob()
/*      */     throws SQLException
/*      */   {
/* 3300 */     Clob localClob = (Clob)getLob(6, true);
/* 3301 */     return localClob;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsJdbcInputStream
 * JD-Core Version:    0.5.4
 */