/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.tds.SybTimestamp;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class DateObject
/*     */ {
/*     */   private static final String ZEROS = "0000000000";
/*  33 */   private int _nanos = 0;
/*  34 */   private Calendar _calendar = null;
/*     */ 
/*  39 */   private int _baseType = 93;
/*     */ 
/*     */   public DateObject()
/*     */     throws SQLException
/*     */   {
/*  49 */     init(null, null, false);
/*     */   }
/*     */ 
/*     */   public DateObject(Calendar paramCalendar)
/*     */     throws SQLException
/*     */   {
/*  59 */     init(null, paramCalendar, false);
/*     */   }
/*     */ 
/*     */   public DateObject(Object paramObject)
/*     */     throws SQLException
/*     */   {
/*  69 */     init(paramObject, null, false);
/*     */   }
/*     */ 
/*     */   public DateObject(Object paramObject, int paramInt)
/*     */     throws SQLException
/*     */   {
/*  84 */     this._baseType = paramInt;
/*  85 */     init(paramObject, null, false);
/*     */   }
/*     */ 
/*     */   public DateObject(Object paramObject, Calendar paramCalendar)
/*     */     throws SQLException
/*     */   {
/*  98 */     init(paramObject, paramCalendar, false);
/*     */   }
/*     */ 
/*     */   public DateObject(Object paramObject, Calendar paramCalendar, int paramInt)
/*     */     throws SQLException
/*     */   {
/* 115 */     this._baseType = paramInt;
/* 116 */     init(paramObject, paramCalendar, false);
/*     */   }
/*     */ 
/*     */   public DateObject(Object paramObject, Calendar paramCalendar, int paramInt, boolean paramBoolean) throws SQLException
/*     */   {
/* 121 */     this._baseType = paramInt;
/* 122 */     init(paramObject, paramCalendar, paramBoolean);
/*     */   }
/*     */ 
/*     */   public int getNanos()
/*     */   {
/* 130 */     return this._nanos;
/*     */   }
/*     */ 
/*     */   public Calendar getCalendar()
/*     */   {
/* 138 */     return this._calendar;
/*     */   }
/*     */ 
/*     */   public int getBaseType()
/*     */   {
/* 146 */     return this._baseType;
/*     */   }
/*     */ 
/*     */   public void setNanos(int paramInt)
/*     */   {
/* 154 */     this._nanos = paramInt;
/*     */   }
/*     */ 
/*     */   public String format(boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 171 */     int i = 0;
/*     */ 
/* 177 */     StringBuffer localStringBuffer = new StringBuffer();
/* 178 */     if ((this._baseType != 92) || (paramBoolean2))
/*     */     {
/* 180 */       localStringBuffer.append(this._calendar.get(1) + "-");
/*     */ 
/* 183 */       i = this._calendar.get(2) + 1;
/* 184 */       localStringBuffer.append(((i < 10) ? "0" : "") + i + "-");
/*     */ 
/* 187 */       i = this._calendar.get(5);
/* 188 */       localStringBuffer.append(((i < 10) ? "0" : "") + i);
/*     */     }
/* 190 */     if ((this._baseType != 91) || (paramBoolean2))
/*     */     {
/* 192 */       if ((this._baseType != 92) || (paramBoolean2))
/*     */       {
/* 195 */         localStringBuffer.append(" ");
/*     */       }
/*     */ 
/* 198 */       i = this._calendar.get(11);
/* 199 */       localStringBuffer.append(((i < 10) ? "0" : "") + i + ":");
/*     */ 
/* 202 */       i = this._calendar.get(12);
/* 203 */       localStringBuffer.append(((i < 10) ? "0" : "") + i + ":");
/*     */ 
/* 206 */       i = this._calendar.get(13);
/* 207 */       localStringBuffer.append(((i < 10) ? "0" : "") + i);
/*     */ 
/* 209 */       if ((paramBoolean1) || (paramBoolean2))
/*     */       {
/* 211 */         String str = null;
/* 212 */         if (this._nanos == 0)
/*     */         {
/* 214 */           str = "0";
/*     */         }
/*     */         else
/*     */         {
/* 218 */           str = Integer.toString(this._nanos);
/*     */ 
/* 221 */           if (str.length() < 9)
/*     */           {
/* 223 */             str = "0000000000".substring(0, 9 - str.length()) + str;
/*     */           }
/*     */ 
/* 228 */           char[] arrayOfChar = new char[str.length()];
/* 229 */           str.getChars(0, str.length(), arrayOfChar, 0);
/* 230 */           int j = 8;
/* 231 */           while (arrayOfChar[j] == '0')
/*     */           {
/* 233 */             --j;
/*     */           }
/* 235 */           str = new String(arrayOfChar, 0, j + 1);
/*     */         }
/* 237 */         localStringBuffer.append("." + str);
/*     */       }
/*     */     }
/*     */ 
/* 241 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   public SybTimestamp toSybTimestamp()
/*     */   {
/* 249 */     SybTimestamp localSybTimestamp = new SybTimestamp(this._calendar.getTime().getTime());
/*     */ 
/* 251 */     localSybTimestamp.setNanos(this._nanos);
/* 252 */     return localSybTimestamp;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 261 */     String str = format(true, false);
/* 262 */     int i = str.lastIndexOf('.');
/* 263 */     if (i >= 0)
/*     */     {
/* 265 */       int j = i + 4;
/* 266 */       if (j > str.length())
/*     */       {
/* 268 */         j = str.length();
/*     */       }
/* 270 */       str = str.substring(0, j);
/*     */     }
/* 272 */     return str;
/*     */   }
/*     */ 
/*     */   private void init(Object paramObject, Calendar paramCalendar, boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/* 278 */     Calendar localCalendar = null;
/*     */ 
/* 284 */     if (paramBoolean)
/*     */     {
/* 286 */       localCalendar = paramCalendar;
/*     */     }
/* 288 */     else if (paramCalendar != null)
/*     */     {
/* 290 */       localCalendar = (Calendar)paramCalendar.clone();
/*     */     }
/*     */     else
/*     */     {
/* 294 */       localCalendar = Calendar.getInstance();
/*     */     }
/*     */ 
/* 297 */     if (paramObject != null)
/*     */     {
/* 300 */       this._calendar = Convert.objectToCalendar(paramObject, localCalendar);
/*     */ 
/* 303 */       if (paramObject instanceof Timestamp)
/*     */       {
/* 305 */         this._nanos = ((Timestamp)paramObject).getNanos();
/*     */       }
/*     */       else
/*     */       {
/* 309 */         if (!paramObject instanceof Time)
/*     */           return;
/* 311 */         int i = this._calendar.get(14);
/* 312 */         this._nanos = (i * 1000000);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 318 */       this._calendar = localCalendar;
/* 319 */       this._calendar.clear();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.DateObject
 * JD-Core Version:    0.5.4
 */