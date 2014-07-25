/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.charset.SybUTF8Charset;
/*      */ import com.sybase.jdbc3.jdbc.DateObject;
/*      */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*      */ import com.sybase.jdbc3.jdbc.RawInputStream;
/*      */ import com.sybase.jdbc3.jdbc.SybLob;
/*      */ import com.sybase.jdbc3.jdbc.SybProperty;
/*      */ import com.sybase.jdbcx.CharsetConverter;
/*      */ import java.io.CharConversionException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.nio.charset.MalformedInputException;
/*      */ import java.nio.charset.UnmappableCharacterException;
/*      */ import java.sql.SQLException;
/*      */ 
/*      */ public class TdsDataOutputStream extends TdsOutputStream
/*      */ {
/*      */   Tds _tds;
/*      */   protected static final int BUF_SIZE = 2048;
/*   59 */   private int _optimizeStringConversions = 0;
/*      */ 
/*   61 */   private byte[] _numericToBytes = null;
/*      */   protected static final int TDS_TYPE = 0;
/*      */   protected static final int LENGTH = 1;
/*      */   protected static final int PRECISION = 2;
/*      */   protected static final int SCALE = 3;
/*      */   protected static final int INFO_LENGTH = 4;
/*      */ 
/*      */   public TdsDataOutputStream(Tds paramTds, PduOutputFormatter paramPduOutputFormatter)
/*      */     throws IOException, SQLException
/*      */   {
/*   73 */     super(paramPduOutputFormatter);
/*   74 */     this._tds = paramTds;
/*   75 */     this._optimizeStringConversions = this._tds.getSybProperty().getInteger(86);
/*      */   }
/*      */ 
/*      */   protected void writeParam(TdsParam paramTdsParam, int[] paramArrayOfInt)
/*      */     throws IOException
/*      */   {
/*   82 */     int i = paramArrayOfInt[1];
/*      */     Object localObject1;
/*      */     Object localObject3;
/*      */     Object localObject2;
/*      */     Object localObject5;
/*      */     int i6;
/*      */     label1224: Object localObject4;
/*   83 */     switch (paramArrayOfInt[0])
/*      */     {
/*      */     case 50:
/*   86 */       writeByte((((Boolean)paramTdsParam._inValue).booleanValue()) ? 1 : 0);
/*   87 */       break;
/*      */     case 38:
/*   89 */       switch (paramArrayOfInt[1]) {
/*      */       case 0:
/*   92 */         break;
/*      */       case 1:
/*   94 */         writeByte(((Integer)paramTdsParam._inValue).intValue());
/*   95 */         break;
/*      */       case 2:
/*   97 */         writeShort(((Integer)paramTdsParam._inValue).intValue());
/*   98 */         break;
/*      */       case 4:
/*  100 */         writeInt(((Integer)paramTdsParam._inValue).intValue());
/*  101 */         break;
/*      */       case 8:
/*  103 */         writeLong(((Long)paramTdsParam._inValue).longValue());
/*  104 */         return;
/*      */       case 3:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*  108 */       }break;
/*      */     case 109:
/*  112 */       switch (paramArrayOfInt[1])
/*      */       {
/*      */       case 0:
/*  115 */         break;
/*      */       case 4:
/*  117 */         writeFloat(((Float)paramTdsParam._inValue).floatValue());
/*  118 */         break;
/*      */       case 8:
/*  120 */         writeDouble(((Double)paramTdsParam._inValue).doubleValue());
/*  121 */         return;
/*      */       }
/*      */ 
/*  125 */       break;
/*      */     case 39:
/*      */     case 175:
/*  130 */       if (i <= 0)
/*      */         return;
/*  132 */       write(paramTdsParam._cvtString, 0, i); break;
/*      */     case 37:
/*  136 */       if (i <= 0)
/*      */         return;
/*  138 */       localObject1 = null;
/*      */ 
/*  145 */       if (SybLob.class.isInstance(paramTdsParam._inValue))
/*      */       {
/*  147 */         localObject1 = ((SybLob)paramTdsParam._inValue).getLocator();
/*      */       }
/*      */       else
/*      */       {
/*  151 */         localObject1 = (byte[])paramTdsParam._inValue;
/*      */       }
/*      */ 
/*  154 */       write(localObject1, 0, i); break;
/*      */     case 225:
/*  158 */       if (paramTdsParam._inValue instanceof byte[])
/*      */       {
/*  160 */         write((byte[])paramTdsParam._inValue, 0, i); return;
/*      */       }
/*  162 */       if (!paramTdsParam._inValue instanceof InputStream)
/*      */         return;
/*  164 */       localObject1 = (InputStream)paramTdsParam._inValue;
/*  165 */       int j = (i < 2048) ? i : 2048;
/*  166 */       localObject3 = new byte[j];
/*      */       while (true) { if (i <= 0)
/*      */           return;
/*  169 */         int i1 = 0;
/*      */         try
/*      */         {
/*  172 */           i1 = ((InputStream)localObject1).read(localObject3, 0, (i > j) ? j : i);
/*      */         }
/*      */         catch (IOException localIOException1)
/*      */         {
/*  177 */           throw new TdsInputStreamIOException(localIOException1.toString());
/*      */         }
/*  179 */         if (i1 < 0) {
/*      */           return;
/*      */         }
/*      */ 
/*  183 */         write(localObject3, 0, i1);
/*  184 */         i -= i1; }
/*      */ 
/*      */     case 108:
/*  189 */       if (i <= 0)
/*      */         return;
/*  191 */       write(this._numericToBytes); break;
/*      */     case 111:
/*  195 */       if (i <= 0)
/*      */         return;
/*  197 */       localObject1 = TdsDateTime.tdsDateTime((DateObject)paramTdsParam._inValue);
/*      */ 
/*  199 */       if (i == 4)
/*      */       {
/*  201 */         localObject1[1] = localObject1[2];
/*  202 */         writeShort(localObject1[0]);
/*  203 */         writeShort(localObject1[1]);
/*  204 */         return;
/*      */       }
/*  206 */       writeInt(localObject1[0]);
/*  207 */       writeInt(localObject1[1]); break;
/*      */     case 187:
/*      */     case 188:
/*  212 */       if (i <= 0)
/*      */         return;
/*  214 */       long l = TdsDateTime.tdsDateTime((DateObject)paramTdsParam._inValue, paramArrayOfInt[0]);
/*      */ 
/*  216 */       writeLong(l); break;
/*      */     case 123:
/*  220 */       if (i <= 0)
/*      */         return;
/*  222 */       localObject2 = TdsDateTime.tdsDateTime((DateObject)paramTdsParam._inValue);
/*      */ 
/*  224 */       writeInt(localObject2[0]); break;
/*      */     case 147:
/*  228 */       if (i <= 0)
/*      */         return;
/*  230 */       localObject2 = TdsDateTime.tdsDateTime((DateObject)paramTdsParam._inValue);
/*      */ 
/*  232 */       writeInt(localObject2[1]); break;
/*      */     case 9217:
/*  237 */       localObject2 = new BlobOutputStream(this);
/*  238 */       ObjectOutputStream localObjectOutputStream = new ObjectOutputStream((OutputStream)localObject2);
/*  239 */       localObjectOutputStream.writeObject(paramTdsParam._inValue);
/*  240 */       localObjectOutputStream.close();
/*  241 */       break;
/*      */     case 9219:
/*  243 */       localObject2 = new BlobOutputStream(this);
/*      */ 
/*  248 */       if (paramTdsParam._parameterAsAString != null)
/*      */       {
/*  254 */         paramTdsParam.convertParamToUnicodeInputStream();
/*      */       }
/*      */       Object localObject6;
/*      */       int i8;
/*  257 */       if (paramTdsParam._inValue instanceof InputStream)
/*      */       {
/*  259 */         localObject3 = (InputStream)paramTdsParam._inValue;
/*      */ 
/*  261 */         i = paramArrayOfInt[1];
/*  262 */         OutputStreamWriter localOutputStreamWriter1 = null;
/*  263 */         if (this._tds._charsetName.equals("x-SybUTF8"))
/*      */         {
/*  265 */           localOutputStreamWriter1 = new OutputStreamWriter((OutputStream)localObject2, SybUTF8Charset.getInstance());
/*      */         }
/*      */         else
/*      */         {
/*  269 */           localOutputStreamWriter1 = new OutputStreamWriter((OutputStream)localObject2, this._tds._charsetName);
/*      */         }
/*  271 */         if ((!this._tds.serverAcceptsColumnStatusByte()) && (((i == 0) || (((InputStream)localObject3).available() == 0))))
/*      */         {
/*  274 */           localOutputStreamWriter1.write(32);
/*      */         }
/*      */         else
/*      */         {
/*  278 */           if (i > 0)
/*      */           {
/*  280 */             i *= 2;
/*      */           }
/*  282 */           localObject5 = new byte[2048];
/*  283 */           localObject6 = new char[1024];
/*      */ 
/*  285 */           while (i != 0)
/*      */           {
/*  287 */             i6 = 2048;
/*  288 */             if ((i > 0) && (i < i6))
/*      */             {
/*  290 */               i6 = i;
/*      */             }
/*  292 */             i8 = 0;
/*      */             try
/*      */             {
/*  295 */               i8 = ((InputStream)localObject3).read(localObject5, 0, i6);
/*      */             }
/*      */             catch (IOException localIOException4)
/*      */             {
/*  299 */               ((BlobOutputStream)localObject2).close();
/*  300 */               throw new TdsInputStreamIOException(localIOException4.toString());
/*      */             }
/*      */ 
/*  303 */             if (i8 < 0)
/*      */               break;
/*  305 */             if (i8 % 2 != 0)
/*      */             {
/*  309 */               i10 = 0;
/*      */               try
/*      */               {
/*  312 */                 i10 = ((InputStream)localObject3).read();
/*      */               }
/*      */               catch (IOException localIOException7)
/*      */               {
/*  316 */                 ((BlobOutputStream)localObject2).close();
/*  317 */                 throw new TdsInputStreamIOException(localIOException7.toString());
/*      */               }
/*      */ 
/*  322 */               if (i10 == -1)
/*      */                 break;
/*  324 */               localObject5[(i8++)] = (byte)i10;
/*      */             }
/*  326 */             if (i > 0)
/*      */             {
/*  330 */               i -= i8;
/*      */             }
/*      */ 
/*  363 */             int i10 = i8 / 2;
/*  364 */             if (!paramTdsParam._paramIsLittleEndian)
/*      */             {
/*  368 */               for (i12 = 0; ; ++i12) { if (i12 >= i10)
/*      */                 {
/*      */                   break label1224;
/*      */                 }
/*      */ 
/*  383 */                 localObject6[i12] = (char)(localObject5[(i12 * 2)] << 8 | localObject5[(i12 * 2 + 1)] & 0xFF); }
/*      */ 
/*      */ 
/*      */             }
/*      */ 
/*  390 */             for (int i12 = 0; i12 < i10; ++i12)
/*      */             {
/*  405 */               localObject6[i12] = (char)(localObject5[(i12 * 2 + 1)] << 8 | localObject5[(i12 * 2)] & 0xFF);
/*      */             }
/*      */ 
/*  408 */             localOutputStreamWriter1.write(localObject6, 0, i10);
/*      */           }
/*  410 */           localOutputStreamWriter1.flush();
/*      */         }
/*      */       }
/*  413 */       else if (paramTdsParam._inValue instanceof Reader)
/*      */       {
/*  415 */         localObject3 = (Reader)paramTdsParam._inValue;
/*  416 */         int i2 = 1;
/*      */ 
/*  418 */         i = paramArrayOfInt[1];
/*  419 */         localObject5 = new char[2048];
/*  420 */         localObject6 = null;
/*  421 */         if (this._tds._charsetName.equals("x-SybUTF8"))
/*      */         {
/*  423 */           localObject6 = new OutputStreamWriter((OutputStream)localObject2, SybUTF8Charset.getInstance());
/*      */         }
/*      */         else
/*      */         {
/*  427 */           localObject6 = new OutputStreamWriter((OutputStream)localObject2, this._tds._charsetName);
/*      */         }
/*      */ 
/*  431 */         while (i != 0)
/*      */         {
/*  433 */           i6 = 2048;
/*  434 */           if ((i > 0) && (i < i6))
/*      */           {
/*  436 */             i6 = i;
/*      */           }
/*  438 */           i8 = 0;
/*      */           try
/*      */           {
/*  441 */             i8 = ((Reader)localObject3).read(localObject5, 0, i6);
/*      */           }
/*      */           catch (IOException localIOException5)
/*      */           {
/*  445 */             ((BlobOutputStream)localObject2).close();
/*  446 */             throw new TdsInputStreamIOException(localIOException5.toString());
/*      */           }
/*  448 */           if (i8 < 0)
/*      */             break;
/*  450 */           if (i > 0)
/*      */           {
/*  454 */             i -= i8;
/*      */           }
/*  456 */           ((OutputStreamWriter)localObject6).write(localObject5, 0, i8);
/*  457 */           if (i2 == 0)
/*      */             continue;
/*  459 */           i2 = 0;
/*      */         }
/*      */ 
/*  462 */         if ((!this._tds.serverAcceptsColumnStatusByte()) && (i2 != 0))
/*      */         {
/*  464 */           ((OutputStreamWriter)localObject6).write(32);
/*      */         }
/*  466 */         ((OutputStreamWriter)localObject6).flush();
/*      */       }
/*      */ 
/*  474 */       ((BlobOutputStream)localObject2).close();
/*  475 */       break;
/*      */     case 9221:
/*  477 */       localObject2 = new BlobOutputStream(this);
/*      */ 
/*  484 */       if (paramTdsParam._parameterAsAString != null)
/*      */       {
/*  490 */         paramTdsParam.convertParamToUnicodeInputStream();
/*      */       }
/*      */       int i9;
/*  492 */       if (paramTdsParam._inValue instanceof InputStream)
/*      */       {
/*  494 */         localObject4 = (InputStream)paramTdsParam._inValue;
/*      */ 
/*  497 */         i = paramArrayOfInt[1];
/*  498 */         if ((!this._tds.serverAcceptsColumnStatusByte()) && (((i == 0) || (((InputStream)localObject4).available() == 0))))
/*      */         {
/*  501 */           ((BlobOutputStream)localObject2).write(32); break label1953:
/*      */         }
/*      */ 
/*  505 */         if (i > 0)
/*      */         {
/*  507 */           i *= 2;
/*      */         }
/*  509 */         localObject5 = new byte[2048];
/*      */         while (true) {
/*  511 */           if (i == 0)
/*      */             break label1953;
/*  513 */           int i4 = 2048;
/*  514 */           if ((i > 0) && (i < i4))
/*      */           {
/*  516 */             i4 = i;
/*      */           }
/*  518 */           i6 = 0;
/*      */           try
/*      */           {
/*  521 */             i6 = ((InputStream)localObject4).read(localObject5, 0, i4);
/*      */           }
/*      */           catch (IOException localIOException2)
/*      */           {
/*  525 */             ((BlobOutputStream)localObject2).close();
/*  526 */             throw new TdsInputStreamIOException(localIOException2.toString());
/*      */           }
/*      */ 
/*  529 */           if (i6 < 0)
/*      */             break label1953;
/*  531 */           if (i6 % 2 != 0)
/*      */           {
/*  537 */             i9 = 0;
/*      */             try
/*      */             {
/*  540 */               i9 = ((InputStream)localObject4).read();
/*      */             }
/*      */             catch (IOException localIOException6)
/*      */             {
/*  544 */               ((BlobOutputStream)localObject2).close();
/*  545 */               throw new TdsInputStreamIOException(localIOException6.toString());
/*      */             }
/*      */ 
/*  550 */             if (i9 == -1)
/*      */               break label1953;
/*  552 */             localObject5[(i6++)] = (byte)i9;
/*      */           }
/*  554 */           if (i > 0)
/*      */           {
/*  558 */             i -= i6;
/*      */           }
/*  560 */           if (!getBigEndian())
/*      */           {
/*  565 */             for (i9 = 0; i9 < i6; i9 += 2)
/*      */             {
/*  567 */               int k = localObject5[i9];
/*  568 */               localObject5[i9] = localObject5[(i9 + 1)];
/*  569 */               localObject5[(i9 + 1)] = k;
/*      */             }
/*      */           }
/*  572 */           ((BlobOutputStream)localObject2).write(localObject5, 0, i6);
/*      */         }
/*      */       }
/*      */ 
/*  576 */       if (paramTdsParam._inValue instanceof Reader)
/*      */       {
/*  578 */         localObject4 = (Reader)paramTdsParam._inValue;
/*  579 */         int i3 = 1;
/*      */ 
/*  581 */         i = paramArrayOfInt[1];
/*  582 */         char[] arrayOfChar = new char[2048];
/*  583 */         OutputStreamWriter localOutputStreamWriter2 = null;
/*  584 */         if (!getBigEndian())
/*      */         {
/*  586 */           localOutputStreamWriter2 = new OutputStreamWriter((OutputStream)localObject2, "UnicodeLittleUnmarked");
/*      */         }
/*      */         else
/*      */         {
/*  591 */           localOutputStreamWriter2 = new OutputStreamWriter((OutputStream)localObject2, "UnicodeBigUnmarked");
/*      */         }
/*      */ 
/*  595 */         while (i != 0)
/*      */         {
/*  597 */           i9 = 2048;
/*  598 */           if ((i > 0) && (i < i9))
/*      */           {
/*  600 */             i9 = i;
/*      */           }
/*  602 */           int i11 = 0;
/*      */           try
/*      */           {
/*  605 */             i11 = ((Reader)localObject4).read(arrayOfChar, 0, i9);
/*      */           }
/*      */           catch (IOException localIOException8)
/*      */           {
/*  609 */             ((BlobOutputStream)localObject2).close();
/*  610 */             throw new TdsInputStreamIOException(localIOException8.toString());
/*      */           }
/*  612 */           if (i11 < 0)
/*      */             break;
/*  614 */           if (i > 0)
/*      */           {
/*  618 */             i -= i11;
/*      */           }
/*  620 */           localOutputStreamWriter2.write(arrayOfChar, 0, i11);
/*  621 */           if (i3 == 0)
/*      */             continue;
/*  623 */           i3 = 0;
/*      */         }
/*      */ 
/*  626 */         if ((!this._tds.serverAcceptsColumnStatusByte()) && (i3 != 0))
/*      */         {
/*  628 */           localOutputStreamWriter2.write(32);
/*      */         }
/*  630 */         localOutputStreamWriter2.flush();
/*      */       }
/*      */ 
/*  638 */       ((BlobOutputStream)localObject2).close();
/*  639 */       break;
/*      */     case 9220:
/*  641 */       localObject2 = new BlobOutputStream(this);
/*  642 */       localObject4 = (InputStream)paramTdsParam._inValue;
/*      */ 
/*  644 */       i = paramArrayOfInt[1];
/*      */ 
/*  646 */       byte[] arrayOfByte = new byte[2048];
/*      */ 
/*  648 */       while (i != 0)
/*      */       {
/*  650 */         int i5 = 2048;
/*  651 */         if ((i > 0) && (i < i5))
/*      */         {
/*  653 */           i5 = i;
/*      */         }
/*      */ 
/*  662 */         int i7 = 0;
/*      */         try
/*      */         {
/*  665 */           i7 = ((InputStream)localObject4).read(arrayOfByte, 0, i5);
/*      */         }
/*      */         catch (IOException localIOException3)
/*      */         {
/*  669 */           ((BlobOutputStream)localObject2).close();
/*  670 */           throw new TdsInputStreamIOException(localIOException3.toString());
/*      */         }
/*  672 */         if (i7 < 0)
/*      */           break;
/*  674 */         if (i > 0)
/*      */         {
/*  678 */           i -= i7;
/*      */         }
/*  680 */         ((BlobOutputStream)localObject2).write(arrayOfByte, 0, i7);
/*      */       }
/*      */ 
/*  683 */       ((BlobOutputStream)localObject2).close();
/*  684 */       break;
/*      */     case 2004:
/*      */     case 2005:
/*  688 */       label1953: write(((SybLob)paramTdsParam._inValue).getLocator());
/*  689 */       writeInt(0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeParam(TdsParam paramTdsParam, int paramInt)
/*      */     throws IOException
/*      */   {
/*  720 */     int[] arrayOfInt = new int[4];
/*  721 */     dataTypeInfo(paramTdsParam, arrayOfInt);
/*  722 */     checkColStatus(paramTdsParam);
/*  723 */     if ((paramTdsParam._inValue == null) && (paramTdsParam._inDataFmt._colStatusBytePresent))
/*      */     {
/*  725 */       return;
/*      */     }
/*  727 */     if (paramTdsParam._inValue == null)
/*      */     {
/*  732 */       switch (arrayOfInt[0])
/*      */       {
/*      */       case 50:
/*  735 */         writeByte(0);
/*  736 */         break;
/*      */       case 37:
/*      */       case 38:
/*      */       case 39:
/*      */       case 108:
/*      */       case 109:
/*      */       case 111:
/*      */       case 123:
/*      */       case 147:
/*      */       case 187:
/*      */       case 188:
/*  747 */         writeByte(0);
/*  748 */         break;
/*      */       case 175:
/*      */       case 225:
/*  751 */         writeInt(0);
/*  752 */         break;
/*      */       case 36:
/*      */       case 9217:
/*  756 */         writeByte(1);
/*  757 */         writeShort(0);
/*  758 */         writeInt(0);
/*  759 */         break;
/*      */       case 9219:
/*      */       case 9220:
/*      */       case 9221:
/*  764 */         writeByte(0);
/*  765 */         writeShort(0);
/*  766 */         writeInt(0);
/*      */       }
/*      */ 
/*  774 */       return;
/*      */     }
/*      */     int i;
/*  780 */     switch (arrayOfInt[0])
/*      */     {
/*      */     case 50:
/*  785 */       writeParam(paramTdsParam, arrayOfInt);
/*  786 */       break;
/*      */     case 38:
/*  795 */       writeByte(arrayOfInt[1]);
/*      */ 
/*  797 */       writeParam(paramTdsParam, arrayOfInt);
/*  798 */       break;
/*      */     case 109:
/*  800 */       writeByte(arrayOfInt[1]);
/*      */ 
/*  804 */       writeParam(paramTdsParam, arrayOfInt);
/*  805 */       break;
/*      */     case 39:
/*      */     case 175:
/*  808 */       i = arrayOfInt[1];
/*  809 */       if (arrayOfInt[0] == 39)
/*      */       {
/*  812 */         writeByte(i);
/*      */       }
/*      */       else
/*      */       {
/*  816 */         writeInt(i);
/*      */       }
/*      */ 
/*  820 */       writeParam(paramTdsParam, arrayOfInt);
/*  821 */       break;
/*      */     case 37:
/*  823 */       i = arrayOfInt[1];
/*  824 */       writeByte(i);
/*      */ 
/*  827 */       writeParam(paramTdsParam, arrayOfInt);
/*  828 */       break;
/*      */     case 225:
/*  830 */       i = arrayOfInt[1];
/*  831 */       writeInt(i);
/*  832 */       writeParam(paramTdsParam, arrayOfInt);
/*  833 */       break;
/*      */     case 108:
/*  837 */       writeByte(arrayOfInt[1]);
/*  838 */       writeParam(paramTdsParam, arrayOfInt);
/*  839 */       this._numericToBytes = null;
/*  840 */       break;
/*      */     case 111:
/*  847 */       writeByte(arrayOfInt[1]);
/*  848 */       writeParam(paramTdsParam, arrayOfInt);
/*  849 */       break;
/*      */     case 123:
/*      */     case 187:
/*      */     case 188:
/*  853 */       writeByte(arrayOfInt[1]);
/*  854 */       writeParam(paramTdsParam, arrayOfInt);
/*  855 */       break;
/*      */     case 147:
/*  857 */       writeByte(arrayOfInt[1]);
/*  858 */       writeParam(paramTdsParam, arrayOfInt);
/*  859 */       break;
/*      */     case 9217:
/*  863 */       writeByte(1);
/*  864 */       writeShort(0);
/*  865 */       writeParam(paramTdsParam, arrayOfInt);
/*  866 */       break;
/*      */     case 9219:
/*      */     case 9220:
/*      */     case 9221:
/*  872 */       writeByte(0);
/*  873 */       writeShort(0);
/*  874 */       writeParam(paramTdsParam, arrayOfInt);
/*  875 */       break;
/*      */     case 2004:
/*      */     case 2005:
/*  879 */       writeByte(0);
/*  880 */       writeShort(0);
/*  881 */       writeLong(0L);
/*  882 */       writeShort(arrayOfInt[1]);
/*  883 */       writeParam(paramTdsParam, arrayOfInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void dataTypeInfo(TdsParam paramTdsParam, int[] paramArrayOfInt)
/*      */     throws IOException
/*      */   {
/*  913 */     switch (paramTdsParam._sqlType)
/*      */     {
/*      */     case -7:
/*  916 */       paramArrayOfInt[0] = 50;
/*  917 */       break;
/*      */     case -6:
/*  919 */       paramArrayOfInt[0] = 38;
/*  920 */       paramArrayOfInt[1] = 1;
/*  921 */       break;
/*      */     case 5:
/*  923 */       paramArrayOfInt[0] = 38;
/*  924 */       paramArrayOfInt[1] = 2;
/*  925 */       break;
/*      */     case -998:
/*  929 */       paramArrayOfInt[0] = 56;
/*  930 */       paramArrayOfInt[1] = 4;
/*  931 */       break;
/*      */     case 4:
/*  933 */       paramArrayOfInt[0] = 38;
/*  934 */       paramArrayOfInt[1] = 4;
/*  935 */       break;
/*      */     case -5:
/*  937 */       paramArrayOfInt[0] = 38;
/*  938 */       paramArrayOfInt[1] = 8;
/*  939 */       break;
/*      */     case 7:
/*  941 */       paramArrayOfInt[0] = 109;
/*  942 */       paramArrayOfInt[1] = 4;
/*  943 */       break;
/*      */     case 6:
/*      */     case 8:
/*  946 */       paramArrayOfInt[0] = 109;
/*  947 */       paramArrayOfInt[1] = 8;
/*  948 */       break;
/*      */     case 2:
/*      */     case 3:
/*  951 */       paramArrayOfInt[0] = 108;
/*      */ 
/*  956 */       if (paramTdsParam != null)
/*      */       {
/*  958 */         if ((paramTdsParam._regType != -999) && (!paramTdsParam._inValue instanceof SybBigDecimal))
/*      */         {
/*  960 */           paramArrayOfInt[2] = 38;
/*      */         }
/*  962 */         this._numericToBytes = TdsNumeric.tdsNumeric((BigDecimal)paramTdsParam._inValue, paramTdsParam._scale, paramArrayOfInt, false); } break;
/*      */     case 9219:
/*      */     case 9220:
/*      */     case 9221:
/*  969 */       paramArrayOfInt[0] = paramTdsParam._sqlType;
/*  970 */       paramArrayOfInt[1] = paramTdsParam._scale;
/*  971 */       break;
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/*  978 */       paramArrayOfInt[0] = ((paramTdsParam._sqlType == -1) ? '¯' : 39);
/*      */ 
/*  981 */       convertString(paramTdsParam, paramArrayOfInt);
/*      */ 
/*  985 */       if (paramTdsParam._parameterHoldsUnicharData)
/*      */       {
/*  987 */         paramArrayOfInt[0] = 225; } break;
/*      */     case 91:
/*  991 */       if (this._tds.serverAcceptsDateData())
/*      */       {
/*  993 */         paramArrayOfInt[0] = 123;
/*  994 */         paramArrayOfInt[1] = 4;
/*      */       }
/*      */       else
/*      */       {
/*  999 */         paramArrayOfInt[0] = 111;
/* 1000 */         paramArrayOfInt[1] = 8;
/*      */       }
/* 1002 */       break;
/*      */     case 92:
/* 1004 */       boolean bool = false;
/*      */       try
/*      */       {
/* 1007 */         bool = this._tds.getSybProperty().getBoolean(52);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 1012 */         ErrorMessage.raiseIOECheckDead(localSQLException);
/*      */       }
/* 1014 */       if (this._tds.serverAcceptsBigDateTimeData())
/*      */       {
/* 1016 */         paramArrayOfInt[0] = 187;
/* 1017 */         paramArrayOfInt[1] = 8;
/* 1018 */         paramArrayOfInt[3] = 6;
/* 1019 */         if (bool)
/*      */         {
/* 1021 */           paramArrayOfInt[0] = 188;
/*      */         }
/*      */       }
/* 1024 */       else if ((this._tds.serverAcceptsTimeData()) && (bool))
/*      */       {
/* 1026 */         paramArrayOfInt[0] = 147;
/* 1027 */         paramArrayOfInt[1] = 4;
/*      */       }
/*      */       else
/*      */       {
/* 1032 */         paramArrayOfInt[0] = 111;
/* 1033 */         paramArrayOfInt[1] = 8;
/*      */       }
/* 1035 */       break;
/*      */     case 93:
/* 1037 */       paramArrayOfInt[0] = 111;
/* 1038 */       if (this._tds.serverAcceptsBigDateTimeData())
/*      */       {
/* 1040 */         paramArrayOfInt[0] = 187;
/*      */       }
/* 1042 */       paramArrayOfInt[1] = 8;
/* 1043 */       break;
/*      */     case -3:
/*      */     case -2:
/* 1046 */       paramArrayOfInt[0] = 37;
/*      */ 
/* 1049 */       if (paramTdsParam._inValue != null)
/* 1050 */         paramArrayOfInt[1] = ((byte[])paramTdsParam._inValue).length; break;
/*      */     case -4:
/* 1053 */       paramArrayOfInt[0] = 225;
/* 1054 */       if (paramTdsParam._inValue != null)
/*      */       {
/* 1056 */         if (paramTdsParam._inValue instanceof byte[])
/*      */         {
/* 1058 */           paramArrayOfInt[1] = ((byte[])paramTdsParam._inValue).length;
/*      */         }
/* 1060 */         else if (paramTdsParam._inValue instanceof InputStream)
/*      */         {
/* 1063 */           if (paramTdsParam._scale == -1)
/*      */           {
/* 1065 */             InputStream localInputStream = (InputStream)paramTdsParam._inValue;
/* 1066 */             paramArrayOfInt[1] = localInputStream.available();
/*      */           }
/*      */           else
/*      */           {
/* 1070 */             paramArrayOfInt[1] = paramTdsParam._scale; } 
/*      */         }
/* 1070 */       }break;
/*      */     case 2000:
/* 1080 */       paramArrayOfInt[0] = 9217;
/* 1081 */       paramArrayOfInt[1] = -1;
/* 1082 */       break;
/*      */     case 2004:
/*      */     case 2005:
/* 1088 */       paramArrayOfInt[0] = paramTdsParam._sqlType;
/* 1089 */       if (paramTdsParam._inValue != null)
/*      */       {
/* 1091 */         paramArrayOfInt[1] = ((SybLob)paramTdsParam._inValue).getLocator().length; } break;
/*      */     case 0:
/*      */     case 1111:
/*      */     default:
/* 1098 */       ErrorMessage.raiseIOException("JZ0SL", "" + paramTdsParam._sqlType);
/*      */     }
/*      */ 
/* 1101 */     if (paramTdsParam._inValue != null) {
/*      */       return;
/*      */     }
/* 1104 */     paramArrayOfInt[1] = 0;
/*      */   }
/*      */ 
/*      */   protected void convertString(TdsParam paramTdsParam, int[] paramArrayOfInt)
/*      */     throws IOException
/*      */   {
/* 1115 */     if (paramTdsParam._inValue == null)
/*      */       return;
/* 1117 */     if (paramTdsParam._inValue instanceof String)
/*      */     {
/* 1119 */       if (paramTdsParam._cvtString == null)
/*      */       {
/* 1121 */         paramTdsParam._parameterAsAString = ((String)paramTdsParam._inValue);
/* 1122 */         check0LNNString(paramTdsParam);
/*      */ 
/* 1125 */         if (this._tds.isUnicharEnabled())
/*      */         {
/* 1127 */           if (paramTdsParam._isUnicodeType)
/*      */           {
/* 1129 */             setUnicharParameter(paramTdsParam);
/*      */           }
/* 1133 */           else if ((this._optimizeStringConversions == 2) || ((this._optimizeStringConversions == 1) && (this._tds.isUTF8OrServerCharset())))
/*      */           {
/* 1137 */             paramTdsParam._cvtString = stringToByte((String)paramTdsParam._inValue);
/*      */           }
/*      */           else
/*      */           {
/* 1141 */             setUnicharParameter(paramTdsParam);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1147 */           paramTdsParam._cvtString = stringToByte((String)paramTdsParam._inValue);
/*      */         }
/*      */       }
/* 1150 */       paramArrayOfInt[1] = paramTdsParam._cvtString.length;
/*      */     }
/*      */     else
/*      */     {
/*      */       Object localObject;
/* 1152 */       if (paramTdsParam._inValue instanceof InputStream)
/*      */       {
/* 1154 */         if (paramTdsParam._cvtString == null)
/*      */         {
/* 1159 */           localObject = null;
/* 1160 */           localObject = paramTdsParam.getStringFromStream(0);
/* 1161 */           paramTdsParam._parameterAsAString = ((String)localObject);
/* 1162 */           check0LNNString(paramTdsParam);
/*      */ 
/* 1165 */           if (this._tds.isUnicharEnabled())
/*      */           {
/* 1167 */             setUnicharParameter(paramTdsParam);
/*      */           }
/*      */           else
/*      */           {
/* 1171 */             paramTdsParam._cvtString = stringToByte((String)localObject);
/*      */           }
/*      */         }
/* 1174 */         paramArrayOfInt[1] = paramTdsParam._cvtString.length;
/*      */       }
/* 1176 */       else if (paramTdsParam._inValue instanceof Reader)
/*      */       {
/* 1180 */         if (paramTdsParam._cvtString == null)
/*      */         {
/* 1184 */           localObject = new char[2048];
/* 1185 */           StringBuffer localStringBuffer = new StringBuffer(2048);
/* 1186 */           int i = 0;
/*      */           while (true)
/*      */           {
/* 1189 */             int j = 2048;
/* 1190 */             if ((paramTdsParam._scale >= 0) && (paramTdsParam._scale < 2048))
/*      */             {
/* 1192 */               j = paramTdsParam._scale - i;
/*      */             }
/*      */ 
/* 1196 */             if (j <= 0)
/*      */             {
/*      */               break;
/*      */             }
/*      */ 
/* 1202 */             int k = ((Reader)paramTdsParam._inValue).read(localObject, 0, j);
/* 1203 */             if (k <= 0)
/*      */               break;
/* 1205 */             localStringBuffer.append(localObject, 0, k);
/* 1206 */             i += k;
/*      */           }
/*      */ 
/* 1214 */           paramTdsParam._parameterAsAString = localStringBuffer.toString();
/* 1215 */           check0LNNString(paramTdsParam);
/*      */ 
/* 1218 */           if (this._tds.isUnicharEnabled())
/*      */           {
/* 1220 */             setUnicharParameter(paramTdsParam);
/*      */           }
/*      */           else
/*      */           {
/* 1224 */             paramTdsParam._cvtString = stringToByte(localStringBuffer.toString());
/*      */           }
/*      */         }
/* 1227 */         paramArrayOfInt[1] = paramTdsParam._cvtString.length;
/*      */       }
/*      */       else
/*      */       {
/* 1231 */         if (paramTdsParam._cvtString == null)
/*      */         {
/* 1234 */           paramTdsParam._parameterAsAString = paramTdsParam._inValue.toString();
/* 1235 */           check0LNNString(paramTdsParam);
/*      */ 
/* 1239 */           if (this._tds.isUnicharEnabled())
/*      */           {
/* 1241 */             setUnicharParameter(paramTdsParam);
/*      */           }
/*      */           else
/*      */           {
/* 1245 */             paramTdsParam._cvtString = stringToByte(paramTdsParam._inValue.toString());
/*      */           }
/*      */         }
/* 1248 */         paramArrayOfInt[1] = paramTdsParam._cvtString.length;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setUnicharParameter(TdsParam paramTdsParam)
/*      */     throws IOException
/*      */   {
/* 1258 */     int i = 0;
/*      */ 
/* 1286 */     if (!this._tds._usingCheckingConverter)
/*      */     {
/* 1288 */       paramTdsParam._cvtString = stringToByte(paramTdsParam._parameterAsAString);
/* 1289 */       String str = this._tds._charsetConverter.toUnicode(paramTdsParam._cvtString);
/*      */ 
/* 1291 */       label97: if (!paramTdsParam._parameterAsAString.equals(str))
/*      */       {
/* 1299 */         i = 1;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */       Throwable localThrowable;
/*      */       try
/*      */       {
/* 1308 */         paramTdsParam._cvtString = toBytes(paramTdsParam._parameterAsAString);
/*      */       }
/*      */       catch (CharConversionException localCharConversionException)
/*      */       {
/* 1312 */         localThrowable = localCharConversionException.getCause();
/*      */ 
/* 1314 */         if ((localThrowable != null) && (!localThrowable instanceof UnmappableCharacterException)) {
/*      */           break label97;
/*      */         }
/*      */ 
/* 1318 */         i = 1; } break label116:
/*      */ 
/* 1320 */       if (localThrowable instanceof MalformedInputException)
/*      */       {
/* 1325 */         ErrorMessage.raiseIOException("JZ0I6", localCharConversionException.toString(), localThrowable);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1331 */     if (i == 0)
/*      */     {
/*      */       label116: return;
/*      */     }
/*      */ 
/* 1340 */     paramTdsParam._cvtString = paramTdsParam.stringToUnicodeBytes(paramTdsParam._parameterAsAString);
/* 1341 */     paramTdsParam._inValue = paramTdsParam._cvtString;
/* 1342 */     paramTdsParam._sqlType = -4;
/* 1343 */     paramTdsParam._parameterHoldsUnicharData = true;
/*      */   }
/*      */ 
/*      */   protected void check0LNNString(TdsParam paramTdsParam)
/*      */   {
/* 1356 */     if (paramTdsParam._parameterAsAString.length() != 0)
/*      */     {
/*      */       return;
/*      */     }
/*      */ 
/* 1362 */     if (this._tds.serverAcceptsColumnStatusByte())
/*      */     {
/*      */       return;
/*      */     }
/*      */ 
/* 1379 */     paramTdsParam._inValue = " ";
/* 1380 */     paramTdsParam._parameterAsAString = " ";
/*      */   }
/*      */ 
/*      */   private void checkColStatus(TdsParam paramTdsParam)
/*      */     throws IOException
/*      */   {
/* 1391 */     if (!paramTdsParam._inDataFmt._colStatusBytePresent) {
/*      */       return;
/*      */     }
/*      */ 
/* 1395 */     int i = 0;
/* 1396 */     if (paramTdsParam._inValue == null)
/*      */     {
/* 1398 */       i = 1;
/*      */     }
/*      */ 
/* 1402 */     writeByte(i);
/*      */   }
/*      */ 
/*      */   protected byte[] toBytes(String paramString)
/*      */     throws CharConversionException
/*      */   {
/* 1413 */     if (paramString == null) return null;
/* 1414 */     return this._tds._charsetConverter.fromUnicode(paramString);
/*      */   }
/*      */ 
/*      */   protected void send(TdsDataObject paramTdsDataObject)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1423 */       paramTdsDataObject.beginRead();
/* 1424 */       if (!paramTdsDataObject._isNull)
/*      */       {
/* 1426 */         int i = paramTdsDataObject._dataLength;
/* 1427 */         switch (paramTdsDataObject._dataFmt._datatype)
/*      */         {
/*      */         case 48:
/* 1433 */           writeByte(paramTdsDataObject.getByte());
/*      */           return;
/*      */         case 52:
/* 1436 */           writeShort(paramTdsDataObject.getShort());
/*      */           return;
/*      */         case 56:
/* 1439 */           writeInt(paramTdsDataObject.getInt());
/*      */           return;
/*      */         case 191:
/* 1442 */           writeLong(paramTdsDataObject.getLong());
/*      */           return;
/*      */         case 38:
/* 1445 */           writeByte(i);
/* 1446 */           long l = paramTdsDataObject.getLong();
/* 1447 */           switch (i)
/*      */           {
/*      */           case 1:
/* 1451 */             writeByte((byte)(int)l);
/* 1452 */             break;
/*      */           case 2:
/* 1454 */             writeShort((short)(int)l);
/* 1455 */             break;
/*      */           case 4:
/* 1457 */             writeInt((int)l);
/* 1458 */             break;
/*      */           case 8:
/* 1460 */             writeLong(l);
/*      */           case 3:
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/*      */           }
/*      */           return;
/*      */         case 59:
/* 1467 */           writeFloat(paramTdsDataObject.getFloat());
/*      */           return;
/*      */         case 62:
/* 1470 */           writeDouble(paramTdsDataObject.getDouble());
/*      */           return;
/*      */         case 109:
/* 1473 */           writeByte(i);
/* 1474 */           double d = paramTdsDataObject.getDouble();
/* 1475 */           switch (i)
/*      */           {
/*      */           case 4:
/* 1478 */             writeFloat((float)d);
/* 1479 */             break;
/*      */           case 8:
/* 1481 */             writeDouble(d);
/*      */           }
/*      */           return;
/*      */         case 49:
/*      */         case 50:
/*      */         case 51:
/*      */         case 58:
/*      */         case 60:
/*      */         case 61:
/*      */         case 122:
/* 1496 */           break;
/*      */         case 37:
/*      */         case 39:
/*      */         case 45:
/*      */         case 47:
/*      */         case 103:
/*      */         case 104:
/*      */         case 106:
/*      */         case 108:
/*      */         case 110:
/*      */         case 111:
/*      */         case 123:
/*      */         case 147:
/* 1511 */           writeByte(i);
/* 1512 */           break;
/*      */         case 34:
/*      */         case 35:
/*      */         case 174:
/*      */         case 175:
/*      */         case 225:
/* 1521 */           writeInt(i);
/* 1522 */           break;
/*      */         case 36:
/* 1526 */           paramTdsDataObject.reset();
/* 1527 */           RawInputStream localRawInputStream = ((TdsJdbcInputStream)paramTdsDataObject).makeNewRIS(4);
/*      */ 
/* 1529 */           byte[] arrayOfByte2 = new byte[2048];
/*      */           while (true)
/*      */           {
/* 1532 */             int i2 = localRawInputStream.read(arrayOfByte2);
/* 1533 */             if (i2 < 0) break;
/* 1534 */             write(arrayOfByte2, 0, i2);
/*      */           }
/*      */           return;
/*      */         default:
/* 1538 */           ErrorMessage.raiseError("JZ0TC");
/*      */         }
/*      */ 
/* 1541 */         byte[] arrayOfByte1 = new byte[(i < 2048) ? i : 2048];
/* 1542 */         int j = arrayOfByte1.length;
/* 1543 */         int k = 0;
/*      */         while (true) { if (k >= i)
/*      */           {
/*      */             break label917;
/*      */           }
/*      */ 
/* 1551 */           int i1 = paramTdsDataObject.read(arrayOfByte1, 0, j);
/* 1552 */           if (i1 < 0) break label917;
/* 1553 */           write(arrayOfByte1, 0, i1);
/* 1554 */           k += i1;
/* 1555 */           j = (i - k > 2048) ? 2048 : i - k; }
/*      */ 
/*      */       }
/*      */ 
/* 1559 */       label917: if ((paramTdsDataObject._dataFmt._status & 0x20) != 0)
/*      */       {
/* 1561 */         switch (paramTdsDataObject._dataFmt._datatype)
/*      */         {
/*      */         case 37:
/*      */         case 38:
/*      */         case 39:
/*      */         case 45:
/*      */         case 47:
/*      */         case 68:
/*      */         case 103:
/*      */         case 104:
/*      */         case 106:
/*      */         case 108:
/*      */         case 109:
/*      */         case 110:
/*      */         case 111:
/*      */         case 123:
/*      */         case 147:
/* 1600 */           writeByte(0);
/* 1601 */           break;
/*      */         case 34:
/*      */         case 35:
/*      */         case 36:
/*      */         case 174:
/*      */         case 175:
/*      */         case 225:
/* 1609 */           writeInt(0);
/* 1610 */           break;
/*      */         default:
/* 1612 */           ErrorMessage.raiseError("JZ0TC");
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1618 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */     finally
/*      */     {
/* 1622 */       paramTdsDataObject.endRead();
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getCharset() {
/* 1627 */     return this._tds._charsetName;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsDataOutputStream
 * JD-Core Version:    0.5.4
 */