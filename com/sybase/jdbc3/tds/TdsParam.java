/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.jdbc.Convert;
/*      */ import com.sybase.jdbc3.jdbc.DateObject;
/*      */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*      */ import com.sybase.jdbc3.jdbc.Param;
/*      */ import com.sybase.jdbc3.jdbc.Protocol;
/*      */ import com.sybase.jdbc3.jdbc.SybBinaryLob;
/*      */ import com.sybase.jdbc3.jdbc.SybCharLob;
/*      */ import com.sybase.jdbc3.jdbc.SybLob;
/*      */ import com.sybase.jdbc3.jdbc.SybProperty;
/*      */ import com.sybase.jdbc3.utils.HexConverts;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.DataOutput;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Calendar;
/*      */ 
/*      */ public class TdsParam extends Param
/*      */ {
/*      */   private static final String NULL = "null";
/*      */   private static final String ZERO = "0";
/*      */   private static final String QUOTE = "'";
/*      */   private static final String HEX_START = "0x";
/*      */   private static final String ASCII_ENCODING = "ISO8859_1";
/*      */   private static final int DATE = 1;
/*      */   private static final int DATETIME = 2;
/*      */   protected DataFormat _inDataFmt;
/*   55 */   byte[] _cvtString = null;
/*   56 */   protected String _parameterAsAString = null;
/*   57 */   protected boolean _paramIsLittleEndian = false;
/*      */ 
/*   62 */   protected boolean _parameterHoldsUnicharData = false;
/*      */ 
/*   64 */   protected TdsDataOutputStream _tdos = null;
/*      */ 
/*      */   public TdsParam(TdsDataOutputStream paramTdsDataOutputStream)
/*      */   {
/*   72 */     this._tdos = paramTdsDataOutputStream;
/*      */   }
/*      */ 
/*      */   protected TdsParam()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void clear(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*   85 */     this._sendAsLiteral = false;
/*   86 */     this._cvtString = null;
/*   87 */     this._parameterAsAString = null;
/*   88 */     this._parameterHoldsUnicharData = false;
/*   89 */     if (paramBoolean)
/*      */     {
/*   91 */       this._sqlType = -999;
/*   92 */       this._regType = -999;
/*   93 */       this._inValue = null;
/*   94 */       this._scale = -999;
/*      */     }
/*   96 */     if (this._outValue == null)
/*      */       return;
/*   98 */     ((TdsJdbcInputStream)this._outValue).clear();
/*   99 */     this._outValue = null;
/*      */   }
/*      */ 
/*      */   protected void normalizeForSend(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  110 */     if (this._sqlType == -999)
/*      */     {
/*  112 */       ErrorMessage.raiseError("JZ0SA", "" + paramInt);
/*      */     }
/*      */ 
/*  116 */     if ((this._targetType == -999) || (this._targetType == this._sqlType))
/*      */     {
/*      */       return;
/*      */     }
/*      */ 
/*  123 */     if ((!this._inValue instanceof SybCharLob) && (!this._inValue instanceof SybBinaryLob))
/*      */     {
/*  125 */       this._sqlType = this._targetType;
/*      */     }
/*      */ 
/*  128 */     switch (this._sqlType)
/*      */     {
/*      */     case -5:
/*  131 */       this._inValue = Convert.objectToLong(this._inValue);
/*  132 */       break;
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*  136 */       this._inValue = Convert.objectToBytes(this._inValue);
/*  137 */       break;
/*      */     case -7:
/*      */     case 16:
/*  140 */       this._inValue = Convert.objectToBoolean(this._inValue);
/*  141 */       break;
/*      */     case 2004:
/*      */     case 2005:
/*  145 */       break;
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/*  152 */       this._inValue = Convert.objectToString(this._inValue);
/*  153 */       break;
/*      */     case 91:
/*      */     case 92:
/*      */     case 93:
/*  157 */       this._inValue = Convert.objectToDateObject(this._inValue, this._targetType, null);
/*      */ 
/*  159 */       break;
/*      */     case 2:
/*      */     case 3:
/*  162 */       this._inValue = Convert.objectToBigDecimal(this._inValue);
/*  163 */       break;
/*      */     case 8:
/*  165 */       this._inValue = Convert.objectToDouble(this._inValue);
/*  166 */       break;
/*      */     case 6:
/*      */     case 7:
/*  169 */       this._inValue = Convert.objectToFloat(this._inValue);
/*  170 */       break;
/*      */     case -6:
/*      */     case 4:
/*      */     case 5:
/*  174 */       this._inValue = Convert.objectToInt(this._inValue);
/*  175 */       break;
/*      */     case 2000:
/*      */     case 0:
/*      */     case 70:
/*      */     case 1111:
/*      */     case 2001:
/*      */     case 2002:
/*      */     case 2006:
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void prepareForSend(Protocol paramProtocol, int paramInt, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  204 */     Tds localTds = (Tds)paramProtocol;
/*  205 */     CapabilitySet localCapabilitySet = localTds._capT._reqCaps;
/*      */ 
/*  211 */     if (this._sqlType == -999)
/*      */     {
/*  213 */       this._inValue = null;
/*  214 */       this._sqlType = this._regType;
/*      */     }
/*  216 */     if (this._sqlType == -999)
/*      */     {
/*  218 */       ErrorMessage.raiseError("JZ0SA", "" + paramInt);
/*      */     }
/*      */ 
/*  223 */     switch (this._sqlType)
/*      */     {
/*      */     case 2004:
/*      */     case 2005:
/*  231 */       SybLob localSybLob = (SybLob)this._inValue;
/*  232 */       if ((localSybLob != null) && (localSybLob.getLocator() == null))
/*      */       {
/*  235 */         switch (localSybLob.getLobType())
/*      */         {
/*      */         case 0:
/*  238 */           this._sqlType = -4;
/*  239 */           byte[] arrayOfByte = localSybLob.getBytes();
/*  240 */           this._inValue = ((arrayOfByte.length == 0) ? null : arrayOfByte);
/*  241 */           break;
/*      */         case 1:
/*  243 */           this._sqlType = -1;
/*  244 */           String str1 = localSybLob.getString();
/*  245 */           this._inValue = (((str1 == null) || (str1.trim().equals(""))) ? null : str1);
/*  246 */           break;
/*      */         case 2:
/*  248 */           this._sqlType = -1;
/*  249 */           String str2 = localSybLob.getString();
/*  250 */           this._inValue = (((str2 == null) || (str2.trim().equals(""))) ? null : str2);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  259 */     switch (this._regType)
/*      */     {
/*      */     case -4:
/*  262 */       if (localTds._sendLongAnyway)
/*      */       {
/*  265 */         this._sendAsLiteral = false;
/*      */       }
/*      */     case -1:
/*  269 */       this._sqlType = this._regType;
/*      */     }
/*      */ 
/*  275 */     if ((!paramBoolean) && (localTds._sendLiterals))
/*      */     {
/*  277 */       this._sendAsLiteral = true;
/*  278 */       return;
/*      */     }
/*      */ 
/*  281 */     int i = 0;
/*      */     int[] arrayOfInt;
/*  283 */     switch (this._sqlType)
/*      */     {
/*      */     case -5:
/*  287 */       if (!localCapabilitySet.get(51))
/*      */       {
/*  299 */         if (this._inValue != null)
/*      */         {
/*  303 */           long l = ((Long)this._inValue).longValue();
/*      */ 
/*  306 */           if ((l > 2147483647L) || (l < -2147483648L) || ((paramBoolean) && (this._regType != -999)))
/*      */           {
/*  311 */             this._inValue = new BigDecimal(String.valueOf(l));
/*  312 */             this._sqlType = 2;
/*      */ 
/*  314 */             if (localCapabilitySet.get(24))
/*      */               break label1850;
/*  316 */             this._sendAsLiteral = true; break label1850:
/*      */           }
/*      */ 
/*  324 */           this._inValue = new Integer((int)l);
/*      */         }
/*  326 */         else if ((paramBoolean) && (this._regType != -999))
/*      */         {
/*  330 */           this._sqlType = 2;
/*  331 */           if (localCapabilitySet.get(24))
/*      */             break label1850;
/*  333 */           this._sendAsLiteral = true; break label1850:
/*      */         }
/*      */ 
/*  338 */         this._sqlType = 4;
/*      */       }
/*      */     case 4:
/*  342 */       if (!localCapabilitySet.get(12))
/*      */       {
/*  344 */         this._sendAsLiteral = true; } break;
/*      */     case -7:
/*  349 */       if (!localCapabilitySet.get(13))
/*      */       {
/*  353 */         if (this._inValue != null)
/*      */         {
/*  355 */           this._inValue = new Integer((((Boolean)this._inValue).booleanValue()) ? 1 : 0);
/*      */         }
/*      */ 
/*  358 */         this._sqlType = -6;
/*      */       }
/*      */     case -6:
/*  361 */       if ((this._inValue == null) || (((Integer)this._inValue).intValue() < 0) || 
/*  366 */         (!localCapabilitySet.get(10)))
/*      */       {
/*  371 */         this._sqlType = 5;
/*      */       }
/*      */     case 5:
/*  374 */       if (!localCapabilitySet.get(11))
/*      */       {
/*  378 */         this._sqlType = 4;
/*      */ 
/*  380 */         if (!localCapabilitySet.get(12))
/*      */         {
/*  382 */           this._sendAsLiteral = true; } 
/*  382 */       }break;
/*      */     case 1:
/*      */     case 12:
/*  394 */       arrayOfInt = new int[4];
/*      */       try
/*      */       {
/*  397 */         this._tdos.convertString(this, arrayOfInt);
/*      */ 
/*  404 */         if (this._parameterHoldsUnicharData)
/*      */         {
/*  406 */           decideUnicodeSendingType(localTds);
/*  407 */           break label1850:
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException1)
/*      */       {
/*  412 */         ErrorMessage.raiseErrorCheckDead(localIOException1);
/*      */       }
/*  414 */       if (arrayOfInt[1] <= 255)
/*      */       {
/*  417 */         if ((this._sqlType != 1) || 
/*  419 */           (!localCapabilitySet.get(14)))
/*      */         {
/*  427 */           if (localCapabilitySet.get(15))
/*      */           {
/*  429 */             this._sqlType = 12;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  442 */         i = 1;
/*      */       }
/*      */     case -1:
/*  451 */       int j = ((this._inValue instanceof InputStream) || (this._inValue instanceof Reader)) ? 1 : 0;
/*      */ 
/*  461 */       if ((i == 0) && (j == 0))
/*      */       {
/*  463 */         arrayOfInt = new int[4];
/*      */         try
/*      */         {
/*  466 */           this._tdos.convertString(this, arrayOfInt);
/*      */         }
/*      */         catch (IOException localIOException2)
/*      */         {
/*  470 */           ErrorMessage.raiseErrorCheckDead(localIOException2);
/*      */         }
/*      */ 
/*  476 */         if ((this._inValue == null) || (this._inValue instanceof String))
/*      */         {
/*  485 */           if (this._parameterHoldsUnicharData)
/*      */           {
/*  487 */             decideUnicodeSendingType(localTds);
/*  488 */             break label1850:
/*      */           }
/*      */ 
/*  493 */           if ((localCapabilitySet.get(28)) && 
/*  495 */             (sendingLongcharIsOK(paramProtocol)))
/*      */           {
/*      */             break label1850;
/*      */           }
/*      */ 
/*  505 */           if (arrayOfInt[1] <= 255)
/*      */           {
/*  507 */             if (localCapabilitySet.get(15))
/*      */             {
/*  509 */               this._sqlType = 12;
/*  510 */               break label1850:
/*      */             }
/*  512 */             if (localCapabilitySet.get(14))
/*      */             {
/*  514 */               this._sqlType = 1;
/*  515 */               break label1850:
/*      */             }
/*      */ 
/*  518 */             this._sendAsLiteral = true;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*  557 */       else if (j != 0)
/*      */       {
/*  561 */         if ((this._scale != -1) && (this._scale <= 255) && (localCapabilitySet.get(15)))
/*      */         {
/*  572 */           arrayOfInt = new int[4];
/*      */           try
/*      */           {
/*  575 */             this._tdos.convertString(this, arrayOfInt);
/*      */           }
/*      */           catch (IOException localIOException3)
/*      */           {
/*  579 */             ErrorMessage.raiseErrorCheckDead(localIOException3);
/*      */           }
/*      */ 
/*  585 */           if (this._parameterHoldsUnicharData)
/*      */           {
/*  587 */             decideUnicodeSendingType(localTds);
/*      */           }
/*  590 */           else if (arrayOfInt[1] <= 255)
/*      */           {
/*  595 */             this._sqlType = 12;
/*      */           }
/*      */ 
/*      */         }
/*  601 */         else if (localCapabilitySet.get(55))
/*      */         {
/*  629 */           if (localCapabilitySet.get(84))
/*      */           {
/*  635 */             this._sqlType = 9221;
/*  636 */             break label1850:
/*      */           }
/*      */ 
/*  640 */           if (this._tdos._tds.isUnicharEnabled())
/*      */           {
/*  646 */             arrayOfInt = new int[4];
/*      */             try
/*      */             {
/*  649 */               this._tdos.convertString(this, arrayOfInt);
/*      */             }
/*      */             catch (IOException localIOException4)
/*      */             {
/*  653 */               ErrorMessage.raiseErrorCheckDead(localIOException4);
/*      */             }
/*      */ 
/*  659 */             if (this._parameterHoldsUnicharData)
/*      */             {
/*  661 */               decideUnicodeSendingType(localTds);
/*  662 */               break label1850:
/*      */             }
/*      */           }
/*      */ 
/*  666 */           this._sqlType = 9219;
/*      */         }
/*      */ 
/*      */       }
/*  687 */       else if ((localCapabilitySet.get(28)) && 
/*  689 */         (sendingLongcharIsOK(paramProtocol)))
/*      */       {
/*  694 */         this._sqlType = -1;
/*      */       }
/*  703 */       else if (localCapabilitySet.get(55))
/*      */       {
/*  705 */         convertParamToUnicodeInputStream();
/*  706 */         this._sqlType = 9219;
/*      */       }
/*      */       else
/*      */       {
/*  715 */         this._sendAsLiteral = true;
/*  716 */       }break;
/*      */     case -4:
/*  724 */       if (!localTds._sendLongAnyway)
/*      */       {
/*  729 */         if (this._inValue == null)
/*      */         {
/*  731 */           if ((!localCapabilitySet.get(29)) || 
/*  733 */             (!sendingLongbinIsOK(paramProtocol)))
/*      */           {
/*  738 */             if (localCapabilitySet.get(17))
/*      */             {
/*  740 */               this._sqlType = -3;
/*      */             }
/*  743 */             else if (localCapabilitySet.get(16))
/*      */             {
/*  745 */               this._sqlType = -2;
/*      */             }
/*      */             else
/*      */             {
/*  749 */               this._sendAsLiteral = true;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*  784 */         else if ((this._inValue instanceof InputStream) && 
/*  788 */           (localCapabilitySet.get(57)))
/*      */         {
/*  795 */           this._sqlType = 9220;
/*      */         }
/*  803 */         else if ((!localCapabilitySet.get(29)) || 
/*  805 */           (!sendingLongbinIsOK(paramProtocol)))
/*      */         {
/*  818 */           if (localCapabilitySet.get(57))
/*      */           {
/*  820 */             convertBytesParamToInputStream();
/*  821 */             this._sqlType = 9220;
/*      */           }
/*      */           else
/*      */           {
/*  828 */             this._sendAsLiteral = true;
/*      */           }
/*      */         }
/*  829 */       }break;
/*      */     case 2004:
/*      */     case 2005:
/*  836 */       if (!localCapabilitySet.get(17))
/*      */       {
/*  838 */         this._sendAsLiteral = true; } break;
/*      */     case -2:
/*  843 */       if (!localCapabilitySet.get(16))
/*      */       {
/*  847 */         this._sqlType = -3;
/*      */       }
/*      */     case -3:
/*  850 */       if (!localCapabilitySet.get(17))
/*      */       {
/*  852 */         this._sendAsLiteral = true; } break;
/*      */     case 3:
/*  857 */       if (!localCapabilitySet.get(27))
/*      */       {
/*  861 */         this._sqlType = 2;
/*      */       }
/*      */     case 2:
/*  864 */       if (!localCapabilitySet.get(24))
/*      */       {
/*  866 */         this._sendAsLiteral = true; } break;
/*      */     case 7:
/*  871 */       if (!localCapabilitySet.get(22))
/*      */       {
/*  873 */         this._sendAsLiteral = true; } break;
/*      */     case 6:
/*      */     case 8:
/*  878 */       if (!localCapabilitySet.get(23))
/*      */       {
/*  880 */         this._sendAsLiteral = true; } break;
/*      */     case 91:
/*  885 */       if (localCapabilitySet.get(71))
/*      */       {
/*  887 */         if (this._inValue == null)
/*      */           break label1850;
/*  889 */         checkDateRange(1); } break;
/*      */     case 93:
/*  895 */       if (!localCapabilitySet.get(20))
/*      */       {
/*  897 */         this._sendAsLiteral = true;
/*      */       }
/*  899 */       else if (this._inValue != null)
/*      */       {
/*  908 */         if ((localCapabilitySet.get(93)) && (localCapabilitySet.get(94)))
/*      */         {
/*  910 */           checkDateRange(1);
/*      */         }
/*      */         else
/*      */         {
/*  914 */           checkDateRange(2);
/*      */         }
/*  916 */         this._sqlType = 93; } break;
/*      */     case 92:
/*  920 */       if (!localCapabilitySet.get(72))
/*      */       {
/*  924 */         if (!localCapabilitySet.get(20))
/*      */         {
/*  926 */           this._sendAsLiteral = true;
/*      */         }
/*      */         else
/*      */         {
/*  930 */           this._sqlType = 93;
/*      */         }
/*      */       }
/*  932 */       break;
/*      */     case 2000:
/*  934 */       if ((!localCapabilitySet.get(54)) || (this._sendAsLiteral))
/*      */       {
/*  937 */         ErrorMessage.raiseError("JZ0ST");
/*      */       }
/*      */ 
/*      */     case -998:
/*      */     }
/*      */ 
/*  948 */     if ((!paramBoolean) || (!this._sendAsLiteral))
/*      */     {
/*      */       label1850: return;
/*      */     }
/*      */ 
/*  953 */     ErrorMessage.raiseError("JZ0SM");
/*      */   }
/*      */ 
/*      */   protected void send(OutputStream paramOutputStream, int paramInt)
/*      */     throws IOException
/*      */   {
/*  969 */     if ((this._sqlType == -998) || (this._sendAsLiteral))
/*      */       return;
/*  971 */     ((TdsDataOutputStream)paramOutputStream).writeParam(this, paramInt);
/*      */   }
/*      */ 
/*      */   protected int getLength()
/*      */   {
/*  979 */     if ((this._sqlType != -998) && (!this._sendAsLiteral))
/*      */     {
/*  981 */       return this._inDataFmt.length();
/*      */     }
/*  983 */     return 0;
/*      */   }
/*      */ 
/*      */   protected boolean makeFormat(Protocol paramProtocol, byte paramByte)
/*      */     throws IOException
/*      */   {
/*  990 */     if ((this._sqlType != -998) && (!this._sendAsLiteral))
/*      */     {
/*  992 */       this._inDataFmt = new DataFormat(this, ((Tds)paramProtocol)._out, paramByte);
/*  993 */       return true;
/*      */     }
/*  995 */     return false;
/*      */   }
/*      */ 
/*      */   public void setPrecision(int paramInt)
/*      */   {
/* 1000 */     this._inDataFmt._precision = paramInt;
/*      */   }
/*      */ 
/*      */   protected void sendFormat(DataOutput paramDataOutput)
/*      */     throws IOException
/*      */   {
/* 1013 */     if ((this._sqlType == -998) || (this._sendAsLiteral))
/*      */       return;
/* 1015 */     this._inDataFmt.send((TdsOutputStream)paramDataOutput);
/*      */   }
/*      */ 
/*      */   public String literalValue(Protocol paramProtocol, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/* 1028 */     Tds localTds = (Tds)paramProtocol;
/*      */     Object localObject;
/*      */     int l;
/* 1029 */     switch (this._sqlType)
/*      */     {
/*      */     case -7:
/* 1032 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1035 */       if (this._inValue == null)
/*      */       {
/* 1037 */         return "null";
/*      */       }
/* 1039 */       return (((Boolean)this._inValue).booleanValue()) ? "1" : "0";
/*      */     case -6:
/*      */     case 4:
/*      */     case 5:
/* 1045 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1048 */       if (this._inValue == null)
/*      */       {
/* 1050 */         return "null";
/*      */       }
/* 1052 */       return ((Integer)this._inValue).toString();
/*      */     case -5:
/* 1056 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1059 */       if (this._inValue == null)
/*      */       {
/* 1061 */         return "null";
/*      */       }
/* 1063 */       return ((Long)this._inValue).toString();
/*      */     case 2:
/*      */     case 3:
/* 1069 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1072 */       if (this._inValue == null)
/*      */       {
/* 1074 */         return "null";
/*      */       }
/* 1076 */       return ((BigDecimal)this._inValue).toString();
/*      */     case 7:
/* 1081 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1084 */       if (this._inValue == null)
/*      */       {
/* 1086 */         return "null";
/*      */       }
/*      */ 
/* 1097 */       return ((Float)this._inValue).toString();
/*      */     case 6:
/*      */     case 8:
/* 1102 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1105 */       if (this._inValue == null)
/*      */       {
/* 1107 */         return "null";
/*      */       }
/* 1109 */       return ((Double)this._inValue).toString();
/*      */     case 91:
/* 1114 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1117 */       if (this._inValue == null)
/*      */       {
/* 1119 */         return "null";
/*      */       }
/* 1121 */       String str1 = null;
/* 1122 */       if (this._tdos._tds.serverAcceptsDateData())
/*      */       {
/* 1124 */         str1 = "'" + ((DateObject)this._inValue).format(false, false) + "'";
/*      */       }
/*      */       else
/*      */       {
/* 1130 */         str1 = "'" + ((DateObject)this._inValue).format(false, true) + "'";
/*      */       }
/*      */ 
/* 1134 */       return str1;
/*      */     case 92:
/* 1138 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1141 */       if (this._inValue == null)
/*      */       {
/* 1143 */         return "null";
/*      */       }
/* 1145 */       boolean bool = false;
/*      */       try
/*      */       {
/* 1148 */         bool = localTds.getSybProperty().getBoolean(52);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 1153 */         ErrorMessage.raiseIOECheckDead(localSQLException);
/*      */       }
/* 1155 */       String str2 = null;
/* 1156 */       if ((this._tdos._tds.serverAcceptsTimeData()) && (bool))
/*      */       {
/* 1158 */         str2 = "'" + ((DateObject)this._inValue).format(true, false) + "'";
/*      */       }
/*      */       else
/*      */       {
/* 1164 */         str2 = "'" + ((DateObject)this._inValue).format(true, true) + "'";
/*      */       }
/*      */ 
/* 1168 */       return str2;
/*      */     case 93:
/* 1172 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1175 */       if (this._inValue == null)
/*      */       {
/* 1177 */         return "null";
/*      */       }
/*      */ 
/* 1181 */       if (localTds.isAse())
/*      */       {
/* 1183 */         localObject = (DateObject)this._inValue;
/* 1184 */         int i = ((DateObject)localObject).getNanos();
/* 1185 */         String str3 = ((DateObject)localObject).format(false, false);
/* 1186 */         l = i / 1000000;
/*      */ 
/* 1188 */         if (l < 10)
/*      */         {
/* 1190 */           str4 = str3 + ".00";
/*      */         }
/* 1192 */         else if (l < 100)
/*      */         {
/* 1194 */           str4 = str3 + ".0";
/*      */         }
/*      */         else
/*      */         {
/* 1198 */           str4 = str3 + ".";
/*      */         }
/* 1200 */         String str4 = str4 + String.valueOf(l);
/* 1201 */         return "'" + str4 + "'";
/*      */       }
/*      */ 
/* 1208 */       return "'" + ((DateObject)this._inValue).format(true, false) + "'";
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/* 1220 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1223 */       if (this._inValue == null)
/*      */       {
/* 1225 */         return "null";
/*      */       }
/*      */ 
/* 1230 */       localObject = new int[4];
/* 1231 */       this._tdos.convertString(this, localObject);
/*      */ 
/* 1233 */       if (this._parameterHoldsUnicharData)
/*      */       {
/* 1235 */         return literalizeUnicharData();
/*      */       }
/*      */ 
/* 1239 */       StringBuffer localStringBuffer = null;
/* 1240 */       if (this._inValue instanceof String)
/*      */       {
/* 1245 */         k = ((String)this._inValue).length();
/* 1246 */         localStringBuffer = new StringBuffer(k + 2);
/* 1247 */         localStringBuffer.append("'");
/* 1248 */         localStringBuffer.append(((String)this._inValue).substring(0, k));
/*      */       }
/* 1250 */       else if (this._inValue instanceof InputStream)
/*      */       {
/* 1253 */         k = getCharCount();
/*      */ 
/* 1256 */         localStringBuffer = new StringBuffer(k + 2);
/* 1257 */         localStringBuffer.append("'");
/* 1258 */         localStringBuffer.append(this._parameterAsAString);
/*      */       }
/* 1260 */       else if (this._inValue instanceof Reader)
/*      */       {
/* 1262 */         localStringBuffer = new StringBuffer(localObject[1] + 2);
/*      */ 
/* 1264 */         localStringBuffer.append("'");
/* 1265 */         localStringBuffer.append(this._parameterAsAString);
/*      */       }
/*      */ 
/* 1272 */       int k = 1;
/* 1273 */       l = localStringBuffer.length();
/*      */       do
/*      */       {
/* 1276 */         k = localStringBuffer.toString().indexOf('\'', k);
/*      */ 
/* 1278 */         if (k == -1) break;
/* 1279 */         localStringBuffer.insert(k, '\'');
/*      */ 
/* 1282 */         k += 2;
/* 1283 */         ++l;
/* 1284 */       }while (k <= l);
/*      */ 
/* 1290 */       localStringBuffer.append("'");
/* 1291 */       return localStringBuffer.toString();
/*      */     case -4:
/* 1304 */       if ((!this._parameterHoldsUnicharData) || 
/* 1306 */         (!this._sendAsLiteral)) {
/*      */         break label1177;
/*      */       }
/* 1309 */       if (this._inValue == null)
/*      */       {
/* 1311 */         return "null";
/*      */       }
/*      */ 
/* 1315 */       return literalizeUnicharData();
/*      */     case -3:
/*      */     case -2:
/* 1322 */       if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/* 1325 */       if (this._inValue == null) return "null";
/* 1326 */       if (this._inValue instanceof byte[])
/*      */       {
/* 1330 */         if ((((byte[])this._inValue).length == 0) && (this._tdos._tds.serverAcceptsColumnStatusByte()))
/*      */         {
/* 1341 */           return "''";
/*      */         }
/*      */ 
/* 1345 */         return "0x" + HexConverts.hexConvert((byte[])this._inValue);
/*      */       }
/*      */ 
/* 1356 */       localObject = (InputStream)this._inValue;
/*      */ 
/* 1358 */       int j = this._scale;
/* 1359 */       if (this._scale == -1)
/*      */       {
/* 1361 */         j = ((InputStream)localObject).available();
/*      */       }
/* 1363 */       if ((j == 0) && (this._tdos._tds.serverAcceptsColumnStatusByte()))
/*      */       {
/* 1374 */         return "''";
/*      */       }
/*      */ 
/* 1378 */       byte[] arrayOfByte = new byte[j];
/* 1379 */       for (l = 0; l < j; )
/*      */       {
/* 1381 */         int i1 = ((InputStream)localObject).read(arrayOfByte, l, j - l);
/*      */ 
/* 1383 */         if (i1 == -1) {
/*      */           break;
/*      */         }
/*      */ 
/* 1387 */         l += i1;
/*      */       }
/* 1389 */       return "0x" + HexConverts.hexConvert(arrayOfByte);
/*      */     case 2004:
/*      */     case 2005:
/* 1397 */       if ((!this._sendAsLiteral) || 
/* 1399 */         (!SybLob.class.isInstance(this._inValue)))
/*      */       {
/*      */         break label1468;
/*      */       }
/*      */ 
/* 1404 */       return "0x" + HexConverts.hexConvert(((SybLob)this._inValue).getLocator());
/*      */     case 2000:
/* 1410 */       label1177: if (!this._sendAsLiteral) {
/*      */         break label1468;
/*      */       }
/*      */ 
/* 1414 */       ErrorMessage.raiseIOException("JZ0ST");
/*      */     }
/*      */ 
/* 1419 */     label1468: return (String)("@p" + paramInt1);
/*      */   }
/*      */ 
/*      */   protected int getCharCount()
/*      */     throws IOException
/*      */   {
/* 1429 */     InputStream localInputStream = (InputStream)this._inValue;
/*      */ 
/* 1433 */     int i = this._scale;
/* 1434 */     if (this._scale == -1)
/*      */     {
/* 1436 */       i = localInputStream.available();
/*      */ 
/* 1438 */       i /= 2;
/*      */     }
/* 1440 */     return i;
/*      */   }
/*      */ 
/*      */   protected String literalizeUnicharData()
/*      */     throws IOException
/*      */   {
/* 1450 */     if ((this._cvtString.length == 0) && (this._tdos._tds.serverAcceptsColumnStatusByte()))
/*      */     {
/* 1461 */       return "''";
/*      */     }
/*      */ 
/* 1465 */     return "0x" + HexConverts.hexConvert(this._cvtString);
/*      */   }
/*      */ 
/*      */   protected String getStringFromStream(int paramInt)
/*      */     throws IOException
/*      */   {
/* 1478 */     InputStream localInputStream = (InputStream)this._inValue;
/* 1479 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */ 
/* 1486 */     int k = this._scale;
/* 1487 */     if ((k != -1) && (k % 2 != 0))
/*      */     {
/* 1490 */       --k;
/*      */     }
/* 1492 */     for (; k != 0; k -= 2)
/*      */     {
/* 1494 */       int i = localInputStream.read();
/* 1495 */       if (i == -1) break;
/* 1496 */       int j = localInputStream.read();
/* 1497 */       if (j == -1) break;
/* 1498 */       char c = (char)(i << 8 | j);
/* 1499 */       localStringBuffer.append(c);
/*      */     }
/* 1501 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private boolean sendingLongcharIsOK(Protocol paramProtocol) throws SQLException {
/* 1505 */     int i = 1;
/* 1506 */     int[] arrayOfInt = new int[4];
/*      */     try
/*      */     {
/* 1509 */       this._tdos.convertString(this, arrayOfInt);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1519 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */ 
/* 1522 */     int j = ((Tds)paramProtocol).getMaxLongvarcharLength();
/* 1523 */     if (arrayOfInt[1] > j)
/*      */     {
/* 1525 */       i = 0;
/*      */     }
/*      */ 
/* 1528 */     return i;
/*      */   }
/*      */ 
/*      */   private boolean sendingLongbinIsOK(Protocol paramProtocol) throws SQLException {
/* 1532 */     int i = 1;
/* 1533 */     int j = 0;
/* 1534 */     int k = 0;
/*      */ 
/* 1544 */     if (this._inValue != null)
/*      */     {
/* 1546 */       if (this._inValue instanceof byte[])
/*      */       {
/* 1548 */         k = ((byte[])this._inValue).length;
/*      */       }
/* 1550 */       else if (this._inValue instanceof InputStream)
/*      */       {
/* 1553 */         if (this._scale == -1)
/*      */         {
/* 1555 */           j = 1;
/*      */         }
/*      */         else
/*      */         {
/* 1559 */           k = this._scale;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1567 */     int l = ((Tds)paramProtocol).getMaxLongvarbinaryLength();
/* 1568 */     if ((k > l) || ((j != 0) && (l != 2147483647)))
/*      */     {
/* 1571 */       i = 0;
/*      */     }
/* 1573 */     return i;
/*      */   }
/*      */ 
/*      */   private void checkDateRange(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1583 */     int i = ((DateObject)this._inValue).getCalendar().get(1);
/* 1584 */     if (paramInt == 1)
/*      */     {
/* 1586 */       if ((i >= 1) && (i <= 9999)) {
/*      */         return;
/*      */       }
/* 1589 */       ErrorMessage.raiseError("JZ0SU", String.valueOf(i), String.valueOf(1), String.valueOf(9999));
/*      */     }
/*      */     else
/*      */     {
/* 1597 */       if ((i >= 1753) && (i <= 9999)) {
/*      */         return;
/*      */       }
/* 1600 */       ErrorMessage.raiseError("JZ0SU", String.valueOf(i), String.valueOf(1753), String.valueOf(9999));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected byte[] stringToUnicodeBytes(String paramString)
/*      */   {
/* 1632 */     byte[] arrayOfByte = null;
/*      */     try
/*      */     {
/* 1636 */       if (this._tdos.getBigEndian())
/*      */       {
/* 1640 */         arrayOfByte = paramString.getBytes("UnicodeBigUnmarked");
/*      */       }
/*      */       else
/*      */       {
/* 1646 */         arrayOfByte = paramString.getBytes("UnicodeLittleUnmarked");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*      */     {
/*      */     }
/*      */ 
/* 1654 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   protected void convertParamToUnicodeInputStream()
/*      */   {
/* 1675 */     this._cvtString = stringToUnicodeBytes(this._parameterAsAString);
/* 1676 */     ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(this._cvtString);
/* 1677 */     this._inValue = localByteArrayInputStream;
/* 1678 */     this._scale = this._parameterAsAString.length();
/*      */ 
/* 1686 */     if (this._tdos.getBigEndian())
/*      */       return;
/* 1688 */     this._paramIsLittleEndian = true;
/*      */   }
/*      */ 
/*      */   protected void convertBytesParamToInputStream()
/*      */   {
/* 1699 */     if (!this._inValue instanceof byte[])
/*      */       return;
/* 1701 */     byte[] arrayOfByte = (byte[])this._inValue;
/* 1702 */     ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
/* 1703 */     this._inValue = localByteArrayInputStream;
/* 1704 */     this._scale = arrayOfByte.length;
/*      */   }
/*      */ 
/*      */   protected void decideUnicodeSendingType(Tds paramTds)
/*      */     throws SQLException
/*      */   {
/* 1710 */     CapabilitySet localCapabilitySet = paramTds._capT._reqCaps;
/* 1711 */     if ((localCapabilitySet.get(29)) && 
/* 1713 */       (sendingLongbinIsOK(paramTds)))
/*      */     {
/* 1718 */       return;
/*      */     }
/*      */ 
/* 1726 */     if ((!localCapabilitySet.get(55)) || (!localCapabilitySet.get(84)))
/*      */       return;
/* 1728 */     convertParamToUnicodeInputStream();
/* 1729 */     this._sqlType = 9221;
/* 1730 */     return;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsParam
 * JD-Core Version:    0.5.4
 */