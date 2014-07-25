/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.tds.SybTimestamp;
/*      */ import com.sybase.jdbc3.utils.HexConverts;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ 
/*      */ public class Convert
/*      */ {
/*      */   private static final long LOW_BYTE = 255L;
/*      */ 
/*      */   public static final String objectToString(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*   52 */     if (paramObject == null) return null;
/*   53 */     if (paramObject instanceof String)
/*      */     {
/*   55 */       return (String)paramObject;
/*      */     }
/*   57 */     if (paramObject instanceof byte[])
/*      */     {
/*   59 */       return HexConverts.hexConvert((byte[])paramObject);
/*      */     }
/*   61 */     if (paramObject instanceof Reader)
/*      */     {
/*   63 */       ErrorMessage.raiseError("JZ0IR");
/*      */     }
/*   65 */     return paramObject.toString();
/*      */   }
/*      */ 
/*      */   public static final InputStream objectToStream(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*   73 */     if (paramObject == null) return null;
/*   74 */     if (paramObject instanceof String)
/*      */     {
/*   76 */       return new ByteArrayInputStream(((String)paramObject).getBytes());
/*      */     }
/*   78 */     if (paramObject instanceof byte[])
/*      */     {
/*   80 */       return new ByteArrayInputStream((byte[])paramObject);
/*      */     }
/*      */ 
/*   84 */     if (paramObject instanceof InputStream)
/*      */     {
/*   86 */       ErrorMessage.raiseError("JZ0IS");
/*      */     }
/*   88 */     ErrorMessage.raiseError("JZ0TE", "String, byte[]");
/*      */ 
/*   91 */     return null;
/*      */   }
/*      */ 
/*      */   public static final Reader objectToReader(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*   99 */     if (paramObject == null) return null;
/*  100 */     if (paramObject instanceof String)
/*      */     {
/*  102 */       return new StringReader((String)paramObject);
/*      */     }
/*  104 */     if (paramObject instanceof byte[])
/*      */     {
/*  106 */       return new InputStreamReader(new ByteArrayInputStream((byte[])paramObject));
/*      */     }
/*      */ 
/*  111 */     if (paramObject instanceof InputStream)
/*      */     {
/*  113 */       return new InputStreamReader((InputStream)paramObject);
/*      */     }
/*  115 */     if (paramObject instanceof Reader)
/*      */     {
/*  117 */       ErrorMessage.raiseError("JZ0IS");
/*      */     }
/*  119 */     ErrorMessage.raiseError("JZ0TC");
/*      */ 
/*  121 */     return null;
/*      */   }
/*      */ 
/*      */   public static Boolean objectToBoolean(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  130 */     if (paramObject == null) return null;
/*  131 */     if ((paramObject instanceof String) && (!((String)paramObject).trim().equals("0")) && (!((String)paramObject).trim().equals("1")))
/*      */     {
/*  134 */       return new Boolean((String)paramObject);
/*      */     }
/*  136 */     double d = objectToDoubleValue(paramObject);
/*  137 */     return new Boolean(d != 0.0D);
/*      */   }
/*      */ 
/*      */   public static Integer objectToUSmallInt(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  143 */     int i = 0;
/*  144 */     if (paramObject == null) return null;
/*  145 */     if (paramObject instanceof Integer)
/*      */     {
/*  147 */       return (Integer)paramObject;
/*      */     }
/*  149 */     long l = objectToLongValue(paramObject);
/*  150 */     checkUSmallIntOflo(l);
/*  151 */     return new Integer((int)l);
/*      */   }
/*      */ 
/*      */   public static Integer objectToUInt(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  158 */     int i = 0;
/*  159 */     if (paramObject == null) return null;
/*  160 */     if (paramObject instanceof Integer)
/*      */     {
/*  162 */       return (Integer)paramObject;
/*      */     }
/*  164 */     long l = objectToLongValue(paramObject);
/*  165 */     checkUintOflo(l);
/*  166 */     return new Integer((int)l);
/*      */   }
/*      */ 
/*      */   public static BigDecimal objectToUBigInt(Object paramObject) throws SQLException
/*      */   {
/*  171 */     if (paramObject == null) return null;
/*      */     BigDecimal localBigDecimal1;
/*  172 */     if (paramObject instanceof BigDecimal)
/*      */     {
/*  174 */       localBigDecimal1 = objectToBigDecimal(paramObject);
/*  175 */       checkUBigintOflo(localBigDecimal1);
/*  176 */       return localBigDecimal1;
/*      */     }
/*      */ 
/*  178 */     if ((paramObject instanceof String) || (paramObject instanceof Long));
/*      */     try
/*      */     {
/*  182 */       localBigDecimal1 = new BigDecimal(numString(paramObject));
/*  183 */       checkUBigintOflo(localBigDecimal1);
/*  184 */       return localBigDecimal1;
/*      */     }
/*      */     catch (NumberFormatException localBigDecimal2)
/*      */     {
/*  188 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */ 
/*  192 */       BigDecimal localBigDecimal2 = objectToBigDecimal(paramObject);
/*  193 */       checkUBigintOflo(localBigDecimal2);
/*  194 */     }return new BigDecimal(objectToDoubleValue(paramObject));
/*      */   }
/*      */ 
/*      */   public static Integer objectToInt(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  201 */     int i = 0;
/*  202 */     if (paramObject == null) return null;
/*  203 */     if (paramObject instanceof Integer)
/*      */     {
/*  205 */       return (Integer)paramObject;
/*      */     }
/*  207 */     long l = objectToLongValue(paramObject);
/*  208 */     checkIntOflo(l);
/*  209 */     return new Integer((int)l);
/*      */   }
/*      */ 
/*      */   public static Short objectToShort(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  216 */     int i = 0;
/*  217 */     if (paramObject == null) return null;
/*  218 */     if (paramObject instanceof Short)
/*      */     {
/*  220 */       return (Short)paramObject;
/*      */     }
/*  222 */     long l = objectToLongValue(paramObject);
/*  223 */     checkShortOflo(l);
/*  224 */     return new Short((short)(int)l);
/*      */   }
/*      */ 
/*      */   public static Long objectToLong(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  231 */     if (paramObject == null) return null;
/*  232 */     if (paramObject instanceof Long)
/*      */     {
/*  234 */       return (Long)paramObject;
/*      */     }
/*      */ 
/*  242 */     BigDecimal localBigDecimal = objectToBigDecimal(paramObject);
/*  243 */     checkLongOflo(localBigDecimal);
/*      */ 
/*  245 */     long l = objectToLongValue(paramObject);
/*  246 */     return new Long(l);
/*      */   }
/*      */ 
/*      */   public static Float objectToFloat(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  253 */     if (paramObject == null) return null;
/*  254 */     if (paramObject instanceof Float)
/*      */     {
/*  256 */       return (Float)paramObject;
/*      */     }
/*  258 */     return new Float(objectToDoubleValue(paramObject));
/*      */   }
/*      */ 
/*      */   public static Double objectToDouble(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  265 */     if (paramObject == null) return null;
/*  266 */     if (paramObject instanceof Double)
/*      */     {
/*  268 */       return (Double)paramObject;
/*      */     }
/*  270 */     return new Double(objectToDoubleValue(paramObject));
/*      */   }
/*      */ 
/*      */   public static double objectToDoubleValue(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  276 */     if (paramObject instanceof Double)
/*      */     {
/*  278 */       return ((Double)paramObject).doubleValue();
/*      */     }
/*  280 */     if (paramObject instanceof Float)
/*      */     {
/*  282 */       return ((Float)paramObject).doubleValue();
/*      */     }
/*  284 */     if (paramObject instanceof BigDecimal)
/*      */     {
/*  286 */       return ((BigDecimal)paramObject).doubleValue();
/*      */     }
/*  288 */     if (paramObject instanceof Boolean)
/*      */     {
/*  290 */       return (((Boolean)paramObject).booleanValue()) ? 1 : 0;
/*      */     }
/*  292 */     if (paramObject instanceof Integer)
/*      */     {
/*  294 */       return ((Integer)paramObject).doubleValue();
/*      */     }
/*  296 */     if (paramObject instanceof Long)
/*      */     {
/*  298 */       return ((Long)paramObject).doubleValue();
/*      */     }
/*  300 */     if (paramObject instanceof Short)
/*      */     {
/*  302 */       return ((Short)paramObject).doubleValue();
/*      */     }
/*  304 */     if (paramObject instanceof byte[])
/*      */     {
/*  306 */       return bufToDouble((byte[])paramObject);
/*      */     }
/*      */ 
/*  308 */     if (paramObject instanceof String);
/*      */     try
/*      */     {
/*  312 */       return new Double(numString(paramObject)).doubleValue();
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/*  316 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */ 
/*  320 */       ErrorMessage.raiseError("JZ0TE", "Double, Float, BigDecimal, Boolean, Integer, Long, Short, byte[], String");
/*      */     }
/*      */ 
/*  323 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public static long objectToLongValue(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  329 */     if (paramObject instanceof Integer)
/*      */     {
/*  331 */       return ((Integer)paramObject).longValue();
/*      */     }
/*  333 */     if (paramObject instanceof Long)
/*      */     {
/*  335 */       return ((Long)paramObject).longValue();
/*      */     }
/*  337 */     if (paramObject instanceof Short)
/*      */     {
/*  339 */       return ((Short)paramObject).longValue();
/*      */     }
/*  341 */     if (paramObject instanceof Double)
/*      */     {
/*  343 */       return ((Double)paramObject).longValue();
/*      */     }
/*  345 */     if (paramObject instanceof Float)
/*      */     {
/*  347 */       return ((Float)paramObject).longValue();
/*      */     }
/*  349 */     if (paramObject instanceof BigDecimal)
/*      */     {
/*  351 */       return ((BigDecimal)paramObject).longValue();
/*      */     }
/*  353 */     if (paramObject instanceof Boolean)
/*      */     {
/*  355 */       return (((Boolean)paramObject).booleanValue()) ? 1 : 0;
/*      */     }
/*  357 */     if (paramObject instanceof byte[])
/*      */     {
/*  359 */       return bufToLong((byte[])paramObject);
/*      */     }
/*      */ 
/*  361 */     if (paramObject instanceof String);
/*      */     try
/*      */     {
/*  365 */       return new Long(numString(paramObject)).longValue();
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/*  369 */       ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */ 
/*  373 */       ErrorMessage.raiseError("JZ0TE", "Double, Float, BigDecimal, Boolean, Integer, Long, Short, byte[], String");
/*      */     }
/*      */ 
/*  376 */     return 0L;
/*      */   }
/*      */ 
/*      */   public static BigDecimal objectToBigDecimal(Object paramObject) throws SQLException
/*      */   {
/*  381 */     if (paramObject == null) return null;
/*  382 */     if (paramObject instanceof BigDecimal)
/*      */     {
/*  384 */       return (BigDecimal)paramObject;
/*      */     }
/*  386 */     if ((paramObject instanceof String) || (paramObject instanceof Long))
/*      */     {
/*      */       try
/*      */       {
/*  390 */         return new BigDecimal(numString(paramObject));
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException)
/*      */       {
/*  394 */         ErrorMessage.raiseError("JZ009", localNumberFormatException.toString());
/*      */       }
/*      */     }
/*      */ 
/*  398 */     return new BigDecimal(objectToDoubleValue(paramObject));
/*      */   }
/*      */ 
/*      */   public static byte[] objectToBytes(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  407 */     if (paramObject == null)
/*      */     {
/*  409 */       return (byte[])null;
/*      */     }
/*  411 */     if (paramObject instanceof byte[])
/*      */     {
/*  413 */       return (byte[])paramObject;
/*      */     }
/*  415 */     if (paramObject instanceof String)
/*      */     {
/*  417 */       String str = (String)paramObject;
/*  418 */       int i = str.length();
/*      */ 
/*  427 */       if (i == 0)
/*      */       {
/*  429 */         return new byte[0];
/*      */       }
/*  431 */       if ((i & 0x1) == 0)
/*      */       {
/*  433 */         byte[] arrayOfByte = new byte[str.length() / 2 - 1];
/*  434 */         int j = 2; for (int k = 0; j < str.length(); ++k)
/*      */         {
/*  436 */           arrayOfByte[k] = (byte)(16 * hexToDecimal(str.charAt(j)) + hexToDecimal(str.charAt(j + 1)));
/*      */ 
/*  434 */           j += 2;
/*      */         }
/*      */ 
/*  439 */         return arrayOfByte;
/*      */       }
/*      */     }
/*  442 */     if (paramObject instanceof SybBinaryClientLob)
/*      */     {
/*  444 */       return ((SybBinaryClientLob)paramObject).getBytes();
/*      */     }
/*      */ 
/*  447 */     ErrorMessage.raiseError("JZ0TE", "Double, Float, BigDecimal, Integer, Long");
/*      */ 
/*  449 */     return null;
/*      */   }
/*      */ 
/*      */   public static java.sql.Date objectToDate(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  463 */     if (paramObject == null)
/*      */     {
/*  465 */       return null;
/*      */     }
/*  467 */     if (paramObject instanceof java.sql.Date)
/*      */     {
/*  469 */       return (java.sql.Date)paramObject;
/*      */     }
/*      */     Object localObject;
/*  471 */     if (paramObject instanceof String)
/*      */     {
/*  473 */       localObject = numString(paramObject);
/*  474 */       int i = ((String)localObject).indexOf(" ");
/*  475 */       if (i > 0)
/*      */       {
/*  477 */         localObject = ((String)localObject).substring(0, i);
/*      */       }
/*      */     }
/*      */     try {
/*  481 */       return java.sql.Date.valueOf((String)localObject);
/*      */     }
/*      */     catch (IllegalArgumentException localIllegalArgumentException)
/*      */     {
/*  485 */       ErrorMessage.raiseError("JZ009", localIllegalArgumentException.toString());
/*      */ 
/*  489 */       if (paramObject instanceof Timestamp)
/*      */       {
/*  491 */         localObject = (Timestamp)paramObject;
/*  492 */         Calendar localCalendar = Calendar.getInstance();
/*  493 */         localCalendar.clear();
/*  494 */         localCalendar.setTime((java.util.Date)localObject);
/*      */ 
/*  497 */         localCalendar.set(11, 0);
/*  498 */         localCalendar.set(12, 0);
/*  499 */         localCalendar.set(13, 0);
/*  500 */         localCalendar.set(14, 0);
/*  501 */         return new java.sql.Date(localCalendar.getTime().getTime());
/*      */       }
/*  503 */       if (paramObject instanceof DateObject)
/*      */       {
/*  506 */         localObject = (Calendar)((DateObject)paramObject).getCalendar().clone();
/*      */ 
/*  509 */         ((Calendar)localObject).set(11, 0);
/*  510 */         ((Calendar)localObject).set(12, 0);
/*  511 */         ((Calendar)localObject).set(13, 0);
/*  512 */         ((Calendar)localObject).set(14, 0);
/*      */ 
/*  514 */         return new java.sql.Date(((Calendar)localObject).getTime().getTime());
/*      */       }
/*      */ 
/*  517 */       ErrorMessage.raiseError("JZ0TC");
/*  518 */     }return (java.sql.Date)null;
/*      */   }
/*      */ 
/*      */   public static Calendar objectToCalendar(Object paramObject, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/*  528 */     if (paramObject == null)
/*      */     {
/*  530 */       return null;
/*      */     }
/*  532 */     if (paramObject instanceof Calendar)
/*      */     {
/*  534 */       return (Calendar)paramObject;
/*      */     }
/*      */ 
/*  537 */     Calendar localCalendar = paramCalendar;
/*  538 */     if (localCalendar == null)
/*      */     {
/*  540 */       localCalendar = Calendar.getInstance();
/*      */     }
/*      */ 
/*  543 */     if (paramObject instanceof java.util.Date)
/*      */     {
/*  545 */       paramCalendar.setTime((java.util.Date)paramObject);
/*      */     }
/*      */     else
/*      */     {
/*  549 */       ErrorMessage.raiseError("JZ0TC");
/*      */     }
/*      */ 
/*  553 */     return paramCalendar;
/*      */   }
/*      */ 
/*      */   public static Time objectToTime(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  568 */     if (paramObject == null) return null;
/*  569 */     if (paramObject instanceof Time)
/*      */     {
/*  571 */       return (Time)paramObject;
/*      */     }
/*      */     Object localObject;
/*  573 */     if (paramObject instanceof String)
/*      */     {
/*  575 */       localObject = numString(paramObject);
/*  576 */       int i = ((String)localObject).indexOf(" ");
/*  577 */       if (i > 0)
/*      */       {
/*  579 */         localObject = ((String)localObject).substring(i + 1, i + 9);
/*      */       }
/*      */     }
/*      */     try {
/*  583 */       return Time.valueOf((String)localObject);
/*      */     }
/*      */     catch (IllegalArgumentException localIllegalArgumentException)
/*      */     {
/*  587 */       ErrorMessage.raiseError("JZ009", localIllegalArgumentException.toString());
/*      */ 
/*  591 */       if (paramObject instanceof Timestamp)
/*      */       {
/*  593 */         localObject = (Timestamp)paramObject;
/*  594 */         Calendar localCalendar = Calendar.getInstance();
/*  595 */         localCalendar.clear();
/*  596 */         localCalendar.setTime((java.util.Date)localObject);
/*      */ 
/*  600 */         localCalendar.set(1, 1970);
/*  601 */         localCalendar.set(2, 0);
/*  602 */         localCalendar.set(5, 1);
/*      */ 
/*  615 */         return new Time(localCalendar.getTime().getTime());
/*      */       }
/*  617 */       if (paramObject instanceof DateObject)
/*      */       {
/*  620 */         localObject = (Calendar)((DateObject)paramObject).getCalendar().clone();
/*      */ 
/*  623 */         ((Calendar)localObject).set(6, 1);
/*  624 */         ((Calendar)localObject).set(1, 1970);
/*      */ 
/*  627 */         return new Time(((Calendar)localObject).getTime().getTime());
/*      */       }
/*  629 */       ErrorMessage.raiseError("JZ0TE", "java.sql.Time, java.sql.Timestamp, String");
/*      */     }
/*  631 */     return (Time)null;
/*      */   }
/*      */ 
/*      */   public static Timestamp objectToTimestamp(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  638 */     if (paramObject == null) return null;
/*  639 */     if (paramObject instanceof Timestamp)
/*      */     {
/*  641 */       return (Timestamp)paramObject;
/*      */     }
/*      */ 
/*  643 */     if (paramObject instanceof String);
/*      */     try
/*      */     {
/*  647 */       return Timestamp.valueOf(numString(paramObject));
/*      */     }
/*      */     catch (IllegalArgumentException localObject)
/*      */     {
/*  651 */       ErrorMessage.raiseError("JZ009", localIllegalArgumentException.toString());
/*      */       Object localObject;
/*      */       Calendar localCalendar;
/*  655 */       if (paramObject instanceof java.sql.Date)
/*      */       {
/*  657 */         localObject = (java.sql.Date)paramObject;
/*  658 */         localCalendar = Calendar.getInstance();
/*  659 */         localCalendar.clear();
/*  660 */         localCalendar.setTime((java.util.Date)localObject);
/*      */ 
/*  663 */         localCalendar.set(11, 0);
/*  664 */         localCalendar.set(12, 0);
/*  665 */         localCalendar.set(13, 0);
/*  666 */         localCalendar.set(14, 0);
/*      */ 
/*  668 */         return new SybTimestamp(localCalendar.getTime().getTime());
/*      */       }
/*      */ 
/*  672 */       if (paramObject instanceof Time)
/*      */       {
/*  674 */         localObject = (Time)paramObject;
/*      */ 
/*  676 */         localCalendar = Calendar.getInstance();
/*  677 */         localCalendar.clear();
/*  678 */         localCalendar.setTime((java.util.Date)localObject);
/*      */ 
/*  682 */         localCalendar.set(1, 1970);
/*  683 */         localCalendar.set(2, 0);
/*  684 */         localCalendar.set(5, 1);
/*      */ 
/*  686 */         return new SybTimestamp(localCalendar.getTime().getTime());
/*      */       }
/*      */ 
/*  689 */       if (paramObject instanceof DateObject)
/*      */       {
/*  692 */         return ((DateObject)paramObject).toSybTimestamp();
/*      */       }
/*      */ 
/*  695 */       ErrorMessage.raiseError("JZ0TC");
/*  696 */     }return (Timestamp)null;
/*      */   }
/*      */ 
/*      */   public static DateObject objectToDateObject(Object paramObject, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/*  712 */     return objectToDateObject(paramObject, 93, paramCalendar);
/*      */   }
/*      */ 
/*      */   public static DateObject objectToDateObject(Object paramObject, int paramInt, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/*  729 */     Object localObject = paramObject;
/*  730 */     DateObject localDateObject = null;
/*      */ 
/*  732 */     if (localObject == null)
/*      */     {
/*  734 */       return null;
/*      */     }
/*  736 */     if (paramObject instanceof DateObject)
/*      */     {
/*  738 */       return (DateObject)paramObject;
/*      */     }
/*      */ 
/*  745 */     if (!localObject instanceof Calendar)
/*      */     {
/*  747 */       Calendar localCalendar = null;
/*  748 */       switch (paramInt)
/*      */       {
/*      */       case 91:
/*  751 */         localObject = objectToDate(localObject);
/*  752 */         localDateObject = new DateObject(localObject, paramCalendar);
/*  753 */         localCalendar = localDateObject.getCalendar();
/*      */ 
/*  755 */         localCalendar.set(11, 0);
/*  756 */         localCalendar.set(12, 0);
/*  757 */         localCalendar.set(13, 0);
/*  758 */         localCalendar.set(14, 0);
/*  759 */         break;
/*      */       case 92:
/*  761 */         localObject = objectToTime(localObject);
/*  762 */         localDateObject = new DateObject(localObject, paramCalendar);
/*  763 */         localCalendar = localDateObject.getCalendar();
/*      */ 
/*  765 */         localCalendar.set(6, 1);
/*  766 */         localCalendar.set(1, 1970);
/*  767 */         break;
/*      */       case 93:
/*  769 */         localObject = objectToTimestamp(localObject);
/*  770 */         localDateObject = new DateObject(localObject, paramCalendar);
/*  771 */         break;
/*      */       default:
/*  773 */         ErrorMessage.raiseError("JZ0TC");
/*      */       }
/*      */ 
/*  776 */       return localDateObject;
/*      */     }
/*      */ 
/*  780 */     return (DateObject)new DateObject(localObject, paramCalendar);
/*      */   }
/*      */ 
/*      */   public static SybLob objectToLob(Object paramObject, byte paramByte)
/*      */     throws SQLException
/*      */   {
/*  787 */     if (paramObject == null)
/*      */     {
/*  789 */       return null;
/*      */     }
/*      */ 
/*  792 */     if ((paramByte == 0) && (((paramObject instanceof SybBinaryLob) || (paramObject instanceof SybBinaryClientLob))))
/*      */     {
/*  795 */       return (SybLob)paramObject;
/*      */     }
/*      */ 
/*  798 */     if ((((paramByte == 1) || (paramByte == 2))) && (((paramObject instanceof SybCharLob) || (paramObject instanceof SybCharClientLob))))
/*      */     {
/*  801 */       return (SybLob)paramObject;
/*      */     }
/*      */ 
/*  804 */     ErrorMessage.raiseError("JZ0TC");
/*      */ 
/*  806 */     return null;
/*      */   }
/*      */ 
/*      */   public static String numString(Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  817 */     String str = objectToString(paramObject);
/*      */ 
/*  820 */     int j = str.length();
/*      */ 
/*  823 */     for (int i = 0; i < j; ++i)
/*      */     {
/*  825 */       switch (str.charAt(i))
/*      */       {
/*      */       case '\t':
/*      */       case '\n':
/*      */       case ' ':
/*      */       case '+':
/*  831 */         break;
/*      */       default:
/*  833 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  838 */     for (--j; j > i; --j)
/*      */     {
/*  840 */       switch (str.charAt(j))
/*      */       {
/*      */       case '\t':
/*      */       case '\n':
/*      */       case ' ':
/*  845 */         break;
/*      */       default:
/*  847 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  852 */     return str.substring(i, j + 1);
/*      */   }
/*      */ 
/*      */   public static byte hexToDecimal(char paramChar)
/*      */     throws SQLException
/*      */   {
/*  859 */     switch (paramChar) { case '0':
/*  862 */       return 0;
/*      */     case '1':
/*  864 */       return 1;
/*      */     case '2':
/*  866 */       return 2;
/*      */     case '3':
/*  868 */       return 3;
/*      */     case '4':
/*  870 */       return 4;
/*      */     case '5':
/*  872 */       return 5;
/*      */     case '6':
/*  874 */       return 6;
/*      */     case '7':
/*  876 */       return 7;
/*      */     case '8':
/*  878 */       return 8;
/*      */     case '9':
/*  880 */       return 9;
/*      */     case 'a':
/*  882 */       return 10;
/*      */     case 'b':
/*  884 */       return 11;
/*      */     case 'c':
/*  886 */       return 12;
/*      */     case 'd':
/*  888 */       return 13;
/*      */     case 'e':
/*  890 */       return 14;
/*      */     case 'f':
/*  892 */       return 15;
/*      */     case ':':
/*      */     case ';':
/*      */     case '<':
/*      */     case '=':
/*      */     case '>':
/*      */     case '?':
/*      */     case '@':
/*      */     case 'A':
/*      */     case 'B':
/*      */     case 'C':
/*      */     case 'D':
/*      */     case 'E':
/*      */     case 'F':
/*      */     case 'G':
/*      */     case 'H':
/*      */     case 'I':
/*      */     case 'J':
/*      */     case 'K':
/*      */     case 'L':
/*      */     case 'M':
/*      */     case 'N':
/*      */     case 'O':
/*      */     case 'P':
/*      */     case 'Q':
/*      */     case 'R':
/*      */     case 'S':
/*      */     case 'T':
/*      */     case 'U':
/*      */     case 'V':
/*      */     case 'W':
/*      */     case 'X':
/*      */     case 'Y':
/*      */     case 'Z':
/*      */     case '[':
/*      */     case '\\':
/*      */     case ']':
/*      */     case '^':
/*      */     case '_':
/*      */     case '`': } ErrorMessage.raiseError("JZ0HC", "" + paramChar);
/*      */ 
/*  898 */     return 0;
/*      */   }
/*      */ 
/*      */   public static void checkByteOflo(long paramLong)
/*      */     throws SQLException
/*      */   {
/*  905 */     if ((paramLong >= -128L) && (paramLong <= 127L))
/*      */       return;
/*  907 */     ErrorMessage.raiseError("JZ00B");
/*      */   }
/*      */ 
/*      */   public static void checkIntOflo(long paramLong) throws SQLException
/*      */   {
/*  912 */     if ((paramLong >= -2147483648L) && (paramLong <= 2147483647L))
/*      */       return;
/*  914 */     ErrorMessage.raiseError("JZ00B");
/*      */   }
/*      */ 
/*      */   public static void checkShortOflo(long paramLong) throws SQLException
/*      */   {
/*  919 */     if ((paramLong >= -32768L) && (paramLong <= 32767L))
/*      */       return;
/*  921 */     ErrorMessage.raiseError("JZ00B");
/*      */   }
/*      */ 
/*      */   public static void checkUSmallIntOflo(long paramLong)
/*      */     throws SQLException
/*      */   {
/*  941 */     if ((paramLong >= 0L) && (paramLong <= 65535L))
/*      */       return;
/*  943 */     ErrorMessage.raiseError("JZ00B");
/*      */   }
/*      */ 
/*      */   public static void checkUintOflo(long paramLong) throws SQLException
/*      */   {
/*  948 */     if ((paramLong >= 0L) && (paramLong <= Long.parseLong("4294967295")))
/*      */       return;
/*  950 */     ErrorMessage.raiseError("JZ00B");
/*      */   }
/*      */ 
/*      */   public static void checkUBigintOflo(BigDecimal paramBigDecimal) throws SQLException
/*      */   {
/*  955 */     if ((paramBigDecimal.compareTo(new BigDecimal("0")) != -1) && (paramBigDecimal.compareTo(new BigDecimal("18446744073709551615")) != 1)) {
/*      */       return;
/*      */     }
/*  958 */     ErrorMessage.raiseError("JZ00B");
/*      */   }
/*      */ 
/*      */   public static void checkLongOflo(double paramDouble)
/*      */     throws SQLException
/*      */   {
/*  964 */     if ((paramDouble >= -9.223372036854776E+018D) && (paramDouble <= 9.223372036854776E+018D))
/*      */       return;
/*  966 */     ErrorMessage.raiseError("JZ00B");
/*      */   }
/*      */ 
/*      */   public static void checkLongOflo(BigDecimal paramBigDecimal) throws SQLException
/*      */   {
/*  971 */     if ((paramBigDecimal.compareTo(new BigDecimal("-9223372036854775808")) != -1) && (paramBigDecimal.compareTo(new BigDecimal("9223372036854775807")) != 1)) {
/*      */       return;
/*      */     }
/*  974 */     ErrorMessage.raiseError("JZ00B");
/*      */   }
/*      */ 
/*      */   public static BigDecimal setScale(BigDecimal paramBigDecimal, int paramInt) throws SQLException
/*      */   {
/*  979 */     if ((paramBigDecimal != null) && (paramInt >= 0))
/*      */       try
/*      */       {
/*  982 */         paramBigDecimal = paramBigDecimal.setScale(paramInt, 4);
/*      */       }
/*      */       catch (ArithmeticException localArithmeticException)
/*      */       {
/*  988 */         ErrorMessage.raiseError("JZ009", localArithmeticException.getMessage());
/*      */       }
/*  990 */     return paramBigDecimal;
/*      */   }
/*      */ 
/*      */   public static long bufToLong(byte[] paramArrayOfByte)
/*      */     throws SQLException
/*      */   {
/*  996 */     long l = 0L;
/*  997 */     switch (paramArrayOfByte.length)
/*      */     {
/*      */     case 1:
/* 1000 */       l = 0xFF & paramArrayOfByte[0];
/* 1001 */       break;
/*      */     case 2:
/* 1003 */       l = (0xFF & paramArrayOfByte[0]) << 8 | 0xFF & paramArrayOfByte[1];
/*      */ 
/* 1006 */       break;
/*      */     case 4:
/* 1008 */       l = (0xFF & paramArrayOfByte[0]) << 24 | (0xFF & paramArrayOfByte[1]) << 16 | (0xFF & paramArrayOfByte[2]) << 8 | 0xFF & paramArrayOfByte[3];
/*      */ 
/* 1014 */       break;
/*      */     case 8:
/* 1016 */       l = (0xFF & paramArrayOfByte[0]) << 56 | (0xFF & paramArrayOfByte[1]) << 48 | (0xFF & paramArrayOfByte[2]) << 40 | (0xFF & paramArrayOfByte[3]) << 32 | (0xFF & paramArrayOfByte[4]) << 24 | (0xFF & paramArrayOfByte[5]) << 16 | (0xFF & paramArrayOfByte[6]) << 8 | 0xFF & paramArrayOfByte[7];
/*      */ 
/* 1025 */       break;
/*      */     case 3:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     default:
/* 1027 */       ErrorMessage.raiseError("JZ0TC");
/*      */     }
/*      */ 
/* 1030 */     return l;
/*      */   }
/*      */ 
/*      */   public static double bufToDouble(byte[] paramArrayOfByte)
/*      */     throws SQLException
/*      */   {
/* 1037 */     double d = 0.0D;
/* 1038 */     switch (paramArrayOfByte.length)
/*      */     {
/*      */     case 4:
/* 1041 */       d = Float.intBitsToFloat((int)((0xFF & paramArrayOfByte[0]) << 24 | (0xFF & paramArrayOfByte[1]) << 16 | (0xFF & paramArrayOfByte[2]) << 8 | 0xFF & paramArrayOfByte[3]));
/*      */ 
/* 1046 */       break;
/*      */     case 8:
/* 1048 */       d = Double.longBitsToDouble((0xFF & paramArrayOfByte[0]) << 56 | (0xFF & paramArrayOfByte[1]) << 48 | (0xFF & paramArrayOfByte[2]) << 40 | (0xFF & paramArrayOfByte[3]) << 32 | (0xFF & paramArrayOfByte[4]) << 24 | (0xFF & paramArrayOfByte[5]) << 16 | (0xFF & paramArrayOfByte[6]) << 8 | 0xFF & paramArrayOfByte[7]);
/*      */ 
/* 1057 */       break;
/*      */     default:
/* 1059 */       ErrorMessage.raiseError("JZ0TC");
/*      */     }
/*      */ 
/* 1062 */     return d;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.Convert
 * JD-Core Version:    0.5.4
 */