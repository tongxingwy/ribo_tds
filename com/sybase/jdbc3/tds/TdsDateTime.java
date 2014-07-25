/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.DateObject;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Calendar;
/*     */ 
/*     */ public class TdsDateTime
/*     */ {
/*     */   private static final int BASEBIAS = 693595;
/*     */   private static final int BASEYEAR = 1;
/*     */   private static final int CENTURY_ADJUST = 52;
/*     */   private static final int TIMEMASK = 33554431;
/*     */   private static final int C300TH_P_HOUR = 1080000;
/*     */   private static final int C300TH_P_MIN = 18000;
/*     */   private static final int BASE_YEAR = 1970;
/*  47 */   private static final int[] MONTHDAYS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
/*     */ 
/*  53 */   private static final int[] LMONTHDAYS = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
/*     */   private static final int YEAR = 0;
/*     */   private static final int MONTH = 1;
/*     */   private static final int DAY = 2;
/*     */   private static final int HOUR = 3;
/*     */   private static final int MINUTE = 4;
/*     */   private static final int SECS = 5;
/*     */   public static final int DATETIME = 1;
/*     */   public static final int SMALLDATETIME = 2;
/*     */   public static final int DATE = 3;
/*     */   public static final int TIME = 4;
/*     */   public static final int BIGDATETIME = 5;
/*     */   public static final int BIGTIME = 6;
/*     */   public static final long MAXBIGDATETIME = 315569519999999999L;
/*     */   public static final long MINBIGDATETIME = 31622400000000L;
/*     */   private static final long microSecondsInADay = 86400000000L;
/*     */   private static final long microsecsInaSecond = 1000000L;
/*     */   private static final long microsecInaHour = 3600000000L;
/*     */   private static final long microSecInaMin = 60000000L;
/*  82 */   int[] _timestamp = { 0, 0, 0, 0, 0, 0 };
/*     */ 
/*  87 */   long _nano = 0L;
/*  88 */   DateObject _dateObj = null;
/*  89 */   int _type = 0;
/*     */ 
/*     */   public TdsDateTime(BigDecimal paramBigDecimal, int paramInt)
/*     */   {
/* 114 */     this._type = paramInt;
/* 115 */     long l1 = paramBigDecimal.longValue();
/* 116 */     if (paramInt == 5)
/*     */     {
/* 118 */       calculateBigDatetime(l1, this._timestamp);
/*     */     } else {
/* 120 */       if (paramInt != 6)
/*     */         return;
/* 122 */       long l2 = l1 / 86400000000L;
/* 123 */       calculateBigtime(l1, l2);
/* 124 */       this._timestamp[0] = 1;
/* 125 */       this._timestamp[1] = 0;
/* 126 */       this._timestamp[2] = 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public TdsDateTime(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 147 */     this._type = paramInt3;
/*     */ 
/* 149 */     if (paramInt3 == 1)
/*     */     {
/* 151 */       numToYearMonthDay(paramInt1, this._timestamp);
/* 152 */       convertFourByteTime(paramInt2);
/*     */     }
/* 154 */     else if (paramInt3 == 2)
/*     */     {
/* 156 */       numToYearMonthDay(paramInt1, this._timestamp);
/* 157 */       convertTwoByteTime(paramInt2);
/*     */     }
/* 159 */     else if (paramInt3 == 3)
/*     */     {
/* 161 */       numToYearMonthDay(paramInt1, this._timestamp);
/*     */     }
/*     */     else
/*     */     {
/* 166 */       if (paramInt3 != 4)
/*     */         return;
/* 168 */       convertFourByteTime(paramInt2);
/*     */ 
/* 178 */       this._timestamp[0] = 1970;
/* 179 */       this._timestamp[1] = 0;
/* 180 */       this._timestamp[2] = 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertFourByteTime(int paramInt)
/*     */   {
/* 206 */     paramInt &= 33554431;
/* 207 */     this._timestamp[3] = (paramInt / 1080000);
/* 208 */     this._timestamp[4] = (paramInt / 18000 % 60);
/* 209 */     this._timestamp[5] = (paramInt / 300 % 60);
/* 210 */     this._nano = (paramInt % 300L * 10000000L / 3L);
/*     */   }
/*     */ 
/*     */   private void convertTwoByteTime(int paramInt)
/*     */   {
/* 222 */     this._timestamp[3] = (paramInt / 60);
/* 223 */     this._timestamp[4] = (paramInt % 60);
/*     */   }
/*     */ 
/*     */   public static int[] tdsDateTime(DateObject paramDateObject)
/*     */   {
/* 235 */     int[] arrayOfInt = { 0, 0, 0 };
/* 236 */     Calendar localCalendar = paramDateObject.getCalendar();
/* 237 */     int i = paramDateObject.getBaseType();
/*     */ 
/* 239 */     int j = 1970;
/* 240 */     int k = 0;
/* 241 */     int l = 0;
/*     */ 
/* 243 */     if ((i == 91) || (i == 93))
/*     */     {
/* 246 */       j = localCalendar.get(1);
/* 247 */       k = localCalendar.get(2);
/* 248 */       l = localCalendar.get(5) - 1;
/*     */     }
/* 250 */     arrayOfInt[0] = ymdToNum(j, k, l);
/*     */ 
/* 252 */     if ((i == 92) || (i == 93))
/*     */     {
/* 254 */       int i1 = localCalendar.get(11);
/* 255 */       int i2 = localCalendar.get(12);
/* 256 */       int i3 = localCalendar.get(13);
/* 257 */       double d = paramDateObject.getNanos();
/* 258 */       arrayOfInt[2] = (i1 * 60 + i2);
/*     */ 
/* 263 */       int i4 = (int)Math.round(d * 3.0D / 10000000.0D);
/*     */ 
/* 267 */       arrayOfInt[1] = ((arrayOfInt[2] * 60 + i3) * 300 + i4);
/*     */ 
/* 269 */       if ((d != 0.0D) && (i4 == 300))
/*     */       {
/* 271 */         arrayOfInt[1] -= 1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 277 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public static long tdsDateTime(DateObject paramDateObject, int paramInt)
/*     */   {
/* 295 */     Calendar localCalendar = paramDateObject.getCalendar();
/* 296 */     int i = paramDateObject.getBaseType();
/* 297 */     int j = 1970;
/* 298 */     int k = 0;
/* 299 */     int l = 0;
/* 300 */     if ((i == 91) || (i == 93))
/*     */     {
/* 303 */       j = localCalendar.get(1);
/* 304 */       k = localCalendar.get(2);
/* 305 */       l = localCalendar.get(5) - 1;
/*     */     }
/* 307 */     int i1 = ymdToNum(j, k, l);
/* 308 */     i1 += 693595;
/* 309 */     i1 += 366;
/*     */ 
/* 311 */     int i2 = localCalendar.get(11);
/* 312 */     int i3 = localCalendar.get(12);
/* 313 */     int i4 = localCalendar.get(13);
/* 314 */     int i5 = paramDateObject.getNanos() % 10000000;
/* 315 */     long l1 = paramDateObject.getNanos() / 1000;
/* 316 */     if ((i5 == 6666666) || (i5 == 3333333) || (i5 == 0))
/*     */     {
/* 319 */       l1 = localCalendar.get(14) * 1000;
/*     */     }
/*     */ 
/* 322 */     long l2 = 0L;
/* 323 */     long l3 = i1;
/* 324 */     if (paramInt == 187)
/*     */     {
/* 326 */       l2 = l3 * 86400000000L;
/*     */     }
/*     */ 
/* 329 */     long l4 = i2;
/* 330 */     if (l4 > 0L)
/*     */     {
/* 333 */       l2 += l4 * 3600000000L;
/*     */     }
/* 335 */     long l5 = i3;
/* 336 */     if (l5 > 0L)
/*     */     {
/* 338 */       l2 += l5 * 60000000L;
/*     */     }
/*     */ 
/* 341 */     long l6 = i4;
/* 342 */     if (l6 > 0L)
/*     */     {
/* 344 */       l2 += l6 * 1000000L;
/*     */     }
/* 346 */     long l7 = l1;
/* 347 */     if (l1 > 0L)
/*     */     {
/* 349 */       l2 += l7;
/*     */     }
/*     */ 
/* 355 */     return l2;
/*     */   }
/*     */ 
/*     */   public DateObject dateObjectValue()
/*     */     throws SQLException
/*     */   {
/* 361 */     return dateObjectValue(null);
/*     */   }
/*     */ 
/*     */   public DateObject dateObjectValue(Calendar paramCalendar)
/*     */     throws SQLException
/*     */   {
/* 367 */     if (this._dateObj == null)
/*     */     {
/* 369 */       this._dateObj = convertToDateObject(paramCalendar);
/*     */     }
/* 371 */     return this._dateObj;
/*     */   }
/*     */ 
/*     */   public String stringValue()
/*     */   {
/* 380 */     StringBuffer localStringBuffer = new StringBuffer(26);
/*     */ 
/* 382 */     if ((this._type != 4) && (this._type != 6))
/*     */     {
/* 384 */       int i = 4;
/* 385 */       while (Integer.toString(this._timestamp[0]).length() < i)
/*     */       {
/* 387 */         localStringBuffer.append('0');
/* 388 */         --i;
/*     */       }
/*     */ 
/* 391 */       localStringBuffer.append(this._timestamp[0]);
/* 392 */       localStringBuffer.append('-');
/* 393 */       if (this._timestamp[1] < 9) localStringBuffer.append('0');
/* 394 */       localStringBuffer.append(this._timestamp[1] + 1);
/* 395 */       localStringBuffer.append('-');
/* 396 */       if (this._timestamp[2] < 10) localStringBuffer.append('0');
/* 397 */       localStringBuffer.append(this._timestamp[2]);
/*     */     }
/* 399 */     if (this._type != 3)
/*     */     {
/* 401 */       double d = this._nano / 1000000000.0D;
/*     */       String str;
/* 403 */       if (d == 0.0D)
/*     */       {
/* 405 */         if ((this._type == 5) || (this._type == 6))
/*     */         {
/* 407 */           str = "000000";
/*     */         }
/*     */         else
/*     */         {
/* 411 */           str = "0.0";
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 416 */         str = String.valueOf(d);
/*     */ 
/* 418 */         if ((this._type == 5) || (this._type == 6))
/*     */         {
/* 420 */           int j = 6;
/* 421 */           str = String.valueOf((int)this._nano / 1000);
/*     */           while (true) { if (str.length() >= j)
/*     */               break label292;
/* 424 */             str = "0" + str; }
/*     */ 
/*     */         }
/* 427 */         if (str.length() > 5)
/*     */         {
/* 429 */           str = str.substring(0, 5);
/*     */         }
/*     */       }
/* 432 */       if (this._type != 4)
/*     */       {
/* 434 */         label292: localStringBuffer.append(' ');
/*     */       }
/* 436 */       if (this._timestamp[3] < 10) localStringBuffer.append('0');
/* 437 */       localStringBuffer.append(this._timestamp[3]);
/* 438 */       localStringBuffer.append(':');
/* 439 */       if (this._timestamp[4] < 10) localStringBuffer.append('0');
/* 440 */       localStringBuffer.append(this._timestamp[4]);
/* 441 */       localStringBuffer.append(':');
/* 442 */       if (this._timestamp[5] < 10) localStringBuffer.append('0');
/* 443 */       localStringBuffer.append(this._timestamp[5]);
/* 444 */       localStringBuffer.append('.');
/* 445 */       if ((this._type == 5) || (this._type == 6))
/*     */       {
/* 447 */         localStringBuffer.append(str);
/*     */       }
/*     */       else
/*     */       {
/* 451 */         localStringBuffer.append(str.substring(2));
/*     */       }
/*     */     }
/* 454 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   private void calculateBigDatetime(long paramLong, int[] paramArrayOfInt)
/*     */   {
/* 459 */     long l = paramLong / 86400000000L;
/* 460 */     int i = (int)l;
/*     */ 
/* 462 */     i -= 693595;
/* 463 */     i -= 366;
/*     */ 
/* 465 */     numToYearMonthDay(i, this._timestamp);
/*     */ 
/* 467 */     calculateBigtime(paramLong, l);
/*     */   }
/*     */ 
/*     */   private void calculateBigtime(long paramLong1, long paramLong2)
/*     */   {
/* 474 */     long l1 = 86400000000L * paramLong2;
/* 475 */     l1 = paramLong1 - l1;
/*     */ 
/* 478 */     long l2 = l1 / 3600000000L;
/* 479 */     this._timestamp[3] = (int)l2;
/*     */ 
/* 481 */     long l3 = l1 % 3600000000L;
/* 482 */     long l4 = l3 / 60000000L;
/* 483 */     this._timestamp[4] = (int)l4;
/*     */ 
/* 485 */     l3 %= 60000000L;
/* 486 */     long l5 = l3 / 1000000L;
/* 487 */     this._timestamp[5] = (int)l5;
/*     */ 
/* 489 */     l3 %= 1000000L;
/* 490 */     this._nano = (l3 * 1000L);
/*     */   }
/*     */ 
/*     */   private static void numToYearMonthDay(int paramInt, int[] paramArrayOfInt)
/*     */   {
/* 506 */     paramInt += 693595;
/*     */ 
/* 508 */     int i = paramInt / 365;
/*     */     int j;
/*     */     while (true)
/*     */     {
/* 512 */       int k = leapcnt(i);
/* 513 */       j = paramInt - (i * 365 + k);
/* 514 */       if (j >= 0) break;
/* 515 */       --i;
/*     */     }
/*     */ 
/* 519 */     int[] arrayOfInt = MONTHDAYS;
/* 520 */     if (leapyear(i))
/*     */     {
/* 522 */       arrayOfInt = LMONTHDAYS;
/*     */     }
/*     */ 
/* 525 */     ++i;
/*     */ 
/* 528 */     int i1 = j;
/* 529 */     for (int l = 0; i1 >= arrayOfInt[l]; ++l)
/*     */     {
/* 531 */       i1 -= arrayOfInt[l];
/*     */     }
/* 533 */     paramArrayOfInt[0] = i;
/* 534 */     paramArrayOfInt[1] = l;
/* 535 */     paramArrayOfInt[2] = (i1 + 1);
/*     */   }
/*     */ 
/*     */   private static int ymdToNum(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 548 */     --paramInt1;
/* 549 */     int i = paramInt1 * 365 + leapcnt(paramInt1);
/* 550 */     int[] arrayOfInt = MONTHDAYS;
/* 551 */     if (leapyear(paramInt1))
/*     */     {
/* 553 */       arrayOfInt = LMONTHDAYS;
/*     */     }
/* 555 */     for (int j = 0; j < paramInt2; ++j)
/*     */     {
/* 557 */       i += arrayOfInt[j];
/*     */     }
/* 559 */     i += paramInt3;
/* 560 */     i -= 693595;
/* 561 */     return i;
/*     */   }
/*     */ 
/*     */   private static boolean leapyear(int paramInt)
/*     */   {
/* 574 */     ++paramInt;
/* 575 */     int i = (((paramInt % 4 == 0) && (paramInt % 100 != 0)) || (paramInt % 400 == 0)) ? 1 : 0;
/*     */ 
/* 579 */     return i;
/*     */   }
/*     */ 
/*     */   private static int leapcnt(int paramInt)
/*     */   {
/* 589 */     int i = paramInt / 100;
/* 590 */     int j = paramInt / 4 - i + i / 4;
/*     */ 
/* 593 */     return j;
/*     */   }
/*     */ 
/*     */   private DateObject convertToDateObject(Calendar paramCalendar)
/*     */     throws SQLException
/*     */   {
/* 600 */     DateObject localDateObject = null;
/* 601 */     localDateObject = new DateObject(paramCalendar);
/*     */ 
/* 606 */     Calendar localCalendar = localDateObject.getCalendar();
/* 607 */     localCalendar.set(1, this._timestamp[0]);
/* 608 */     localCalendar.set(2, this._timestamp[1]);
/* 609 */     localCalendar.set(5, this._timestamp[2]);
/* 610 */     localCalendar.set(11, this._timestamp[3]);
/* 611 */     localCalendar.set(12, this._timestamp[4]);
/* 612 */     localCalendar.set(13, this._timestamp[5]);
/* 613 */     localCalendar.set(14, (int)this._nano / 1000000);
/* 614 */     localDateObject.setNanos((int)this._nano);
/*     */ 
/* 616 */     return localDateObject;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsDateTime
 * JD-Core Version:    0.5.4
 */