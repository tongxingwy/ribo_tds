/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ class Escape
/*     */ {
/*     */   protected static final int CALL = 1;
/*     */   protected static final int RETURN_CALL = 2;
/*     */   protected static final int DATE = 3;
/*     */   protected static final int TIME = 4;
/*     */   protected static final int TIMESTAMP = 5;
/*     */   protected static final int FUNCTION = 6;
/*     */   protected static final int LIKE_ESCAPE = 7;
/*     */   protected static final int OUTER_JOIN = 8;
/*     */   protected static final int UNKNOWN = -1;
/*     */   private static final char LEFT_CURLY = '{';
/*     */   private static final char RIGHT_CURLY = '}';
/*     */   private static final char COMMA = ',';
/*     */   private static final char LEFT_PAREN = '(';
/*     */   private static final char RIGHT_PAREN = ')';
/*     */   private static final char PERCENT_SIGN = '%';
/*     */   private static final char SINGLE_QUOTE = '\'';
/*     */   private static final char DOUBLE_QUOTE = '"';
/*     */   private static final int MAX_ARGUMENTS = 4;
/*     */   private int _type;
/*     */   private String _body;
/*     */ 
/*     */   Escape(String paramString)
/*     */     throws SQLException
/*     */   {
/*  82 */     int l = paramString.length();
/*     */ 
/*  90 */     for (int i = 1; (i < l) && (Character.isWhitespace(paramString.charAt(i))); )
/*     */     {
/*  92 */       ++i;
/*     */     }
/*     */ 
/*  98 */     if (i == l)
/*     */     {
/* 100 */       ErrorMessage.raiseError("JZ0S8", paramString);
/*     */     }
/* 102 */     int j = i;
/* 103 */     while ((j < l) && (!Character.isWhitespace(paramString.charAt(j))))
/*     */     {
/* 105 */       ++j;
/*     */     }
/*     */ 
/* 111 */     if (j == l)
/*     */     {
/* 113 */       ErrorMessage.raiseError("JZ0S8", paramString);
/*     */     }
/* 115 */     String str = paramString.substring(i, j).toLowerCase();
/*     */ 
/* 121 */     if (str.equals("call"))
/*     */     {
/* 123 */       this._type = 1;
/*     */     }
/* 126 */     else if ((str.equals("?")) || (str.equals("?=")) || (str.equals("?=call")))
/*     */     {
/* 129 */       j = paramString.toLowerCase().indexOf("call") + 4;
/*     */ 
/* 131 */       this._type = 2;
/*     */     }
/* 133 */     else if (str.equals("d"))
/*     */     {
/* 135 */       this._type = 3;
/*     */     }
/* 137 */     else if (str.equals("t"))
/*     */     {
/* 139 */       this._type = 4;
/*     */     }
/* 141 */     else if (str.equals("ts"))
/*     */     {
/* 143 */       this._type = 5;
/*     */     }
/* 145 */     else if (str.equals("fn"))
/*     */     {
/* 147 */       this._type = 6;
/*     */     }
/* 149 */     else if (str.equals("escape"))
/*     */     {
/* 151 */       this._type = 7;
/*     */     }
/* 153 */     else if (str.equals("oj"))
/*     */     {
/* 155 */       this._type = 8;
/*     */     }
/*     */     else
/*     */     {
/* 159 */       ErrorMessage.raiseError("JZ0S8", paramString);
/*     */     }
/*     */ 
/* 163 */     int k = j;
/* 164 */     while ((k < l) && (Character.isWhitespace(paramString.charAt(k))))
/*     */     {
/* 166 */       ++k;
/*     */     }
/*     */ 
/* 170 */     if (k == l - 1)
/*     */     {
/* 173 */       ErrorMessage.raiseError("JZ0S8", paramString);
/*     */     }
/*     */ 
/* 180 */     int i1 = l - 2;
/* 181 */     while (Character.isWhitespace(paramString.charAt(i1))) {
/* 182 */       --i1;
/*     */     }
/*     */ 
/* 186 */     this._body = paramString.substring(k, i1 + 1);
/*     */   }
/*     */ 
/*     */   Escape(String paramString, int paramInt)
/*     */     throws SQLException
/*     */   {
/* 197 */     this._body = paramString;
/* 198 */     this._type = paramInt;
/*     */   }
/*     */ 
/*     */   protected int getType()
/*     */   {
/* 212 */     return this._type;
/*     */   }
/*     */ 
/*     */   protected String getBody()
/*     */   {
/* 223 */     return this._body;
/*     */   }
/*     */ 
/*     */   protected String functionName()
/*     */     throws SQLException
/*     */   {
/* 239 */     String str = null;
/*     */     try
/*     */     {
/* 243 */       StringTokenizer localStringTokenizer = new StringTokenizer(this._body, " \t\n\r(");
/*     */ 
/* 245 */       str = localStringTokenizer.nextToken();
/*     */     }
/*     */     catch (NoSuchElementException localNoSuchElementException)
/*     */     {
/* 249 */       ErrorMessage.raiseError("JZ0S8", this._body);
/*     */     }
/*     */ 
/* 252 */     str = str.toLowerCase();
/* 253 */     if ((str.equals("convert")) || (str.equals("timestampadd")) || (str.equals("timestampdiff")))
/*     */     {
/*     */       int i;
/*     */       int j;
/* 261 */       if (str.equals("convert"))
/*     */       {
/* 263 */         i = this._body.lastIndexOf(',') + 1;
/* 264 */         j = this._body.lastIndexOf(')') - 1;
/*     */       }
/*     */       else
/*     */       {
/* 268 */         i = this._body.indexOf('(') + 1;
/* 269 */         j = this._body.indexOf(',') - 1;
/*     */       }
/* 271 */       int k = this._body.length();
/*     */ 
/* 274 */       while ((i <= k) && (Character.isWhitespace(this._body.charAt(i))))
/*     */       {
/* 277 */         ++i;
/*     */       }
/* 279 */       while ((j > 0) && (Character.isWhitespace(this._body.charAt(j))))
/*     */       {
/* 282 */         --j;
/*     */       }
/*     */ 
/* 286 */       if ((i < 0) || (j < 0) || (j < i + 1))
/*     */       {
/* 288 */         ErrorMessage.raiseError("JZ0S8", this._body);
/*     */       }
/*     */ 
/* 291 */       str = str + this._body.substring(i, j + 1).toLowerCase();
/*     */     }
/*     */ 
/* 294 */     return str;
/*     */   }
/*     */ 
/*     */   protected String doMap(String paramString)
/*     */     throws SQLException
/*     */   {
/* 316 */     String[] arrayOfString1 = new String[5];
/* 317 */     String str1 = "";
/*     */ 
/* 322 */     int i = this._body.indexOf('(');
/* 323 */     int j = this._body.lastIndexOf(')');
/* 324 */     if ((i < 0) || (j < 0) || (j < i + 1))
/*     */     {
/* 326 */       ErrorMessage.raiseError("JZ0S8", this._body);
/*     */     }
/*     */ 
/* 330 */     String str2 = functionName();
/*     */     String[] arrayOfString2;
/* 331 */     if (str2.equalsIgnoreCase("extract"))
/*     */     {
/* 333 */       arrayOfString2 = this._body.split("[()]|[fF]+[rR]+[oO]+[mM]");
/* 334 */       if (arrayOfString2.length == 3)
/*     */       {
/* 336 */         str1 = arrayOfString2[2].trim();
/*     */       }
/*     */       else
/*     */       {
/* 340 */         ErrorMessage.raiseError("JZ0S8", this._body);
/*     */       }
/*     */     }
/* 343 */     else if (str2.equalsIgnoreCase("position"))
/*     */     {
/* 345 */       arrayOfString2 = getPositionArgs(this._body.substring(i + 1, j).trim());
/* 346 */       if (arrayOfString2.length == 2)
/*     */       {
/* 348 */         str1 = arrayOfString2[0] + "," + arrayOfString2[1];
/*     */       }
/*     */       else
/*     */       {
/* 352 */         ErrorMessage.raiseError("JZ0S8", this._body);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 357 */       str1 = this._body.substring(i + 1, j);
/*     */     }
/*     */ 
/* 360 */     for (int k = 1; (k <= 4) && (str1.length() > 0); )
/*     */     {
/* 363 */       l = nextComma(str1);
/* 364 */       if (l < 0)
/*     */       {
/* 366 */         arrayOfString1[k] = str1;
/* 367 */         break;
/*     */       }
/* 369 */       if (l == 0)
/*     */       {
/* 371 */         ErrorMessage.raiseError("JZ0S8", this._body);
/*     */       }
/*     */       else
/*     */       {
/* 376 */         arrayOfString1[k] = str1.substring(0, l);
/* 377 */         str1 = str1.substring(l + 1);
/*     */       }
/* 361 */       ++k;
/*     */     }
/*     */ 
/* 382 */     StringBuffer localStringBuffer = new StringBuffer();
/* 383 */     int l = paramString.length();
/* 384 */     for (int i1 = 0; i1 < l; ++i1)
/*     */     {
/* 386 */       if (paramString.charAt(i1) == '%')
/*     */       {
/* 388 */         int i2 = 0;
/*     */         try
/*     */         {
/* 391 */           i2 = Integer.parseInt(paramString.substring(i1 + 1, i1 + 2));
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException)
/*     */         {
/*     */         }
/*     */ 
/* 401 */         if (arrayOfString1[i2] == null)
/*     */         {
/* 407 */           ErrorMessage.raiseError("JZ0S8", this._body);
/*     */         }
/*     */         else
/*     */         {
/* 412 */           localStringBuffer.append(arrayOfString1[i2]);
/*     */         }
/* 414 */         ++i1;
/*     */       }
/*     */       else
/*     */       {
/* 418 */         localStringBuffer.append(paramString.charAt(i1));
/*     */       }
/*     */     }
/* 421 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   private String[] getPositionArgs(String paramString)
/*     */     throws SQLException
/*     */   {
/* 428 */     if ((paramString == null) || (paramString.length() < 6))
/*     */     {
/* 430 */       ErrorMessage.raiseError("JZ0S8", this._body);
/*     */     }
/*     */ 
/* 433 */     String[] arrayOfString = new String[2];
/* 434 */     int i = paramString.charAt(0);
/* 435 */     int j = 0;
/* 436 */     int k = 0;
/*     */ 
/* 439 */     if ((i == 39) || (i == 34))
/*     */     {
/* 441 */       while (j > -1)
/*     */       {
/* 443 */         j = paramString.indexOf(i, j + 1);
/* 444 */         if ((j <= -1) || (paramString.indexOf(i, j + 1) != j + 1))
/*     */           break;
/* 446 */         ++j;
/*     */       }
/*     */ 
/* 453 */       if (j == -1)
/*     */       {
/* 455 */         ErrorMessage.raiseError("JZ0S8", this._body);
/*     */       }
/* 457 */       arrayOfString[0] = paramString.substring(0, j + 1);
/*     */     }
/*     */     else
/*     */     {
/* 462 */       j = paramString.indexOf(' ');
/* 463 */       if (j == -1)
/*     */       {
/* 465 */         ErrorMessage.raiseError("JZ0S8", this._body);
/*     */       }
/*     */       else
/*     */       {
/* 469 */         arrayOfString[0] = paramString.substring(0, j);
/*     */       }
/*     */     }
/* 472 */     arrayOfString[1] = paramString.substring(j + 1).trim().substring(3).trim();
/*     */ 
/* 474 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   private int nextComma(String paramString)
/*     */     throws SQLException
/*     */   {
/* 482 */     int i = paramString.length();
/* 483 */     int j = -1;
/* 484 */     int k = 1;
/*     */ 
/* 488 */     for (int l = 0; l < i; ++l)
/*     */     {
/* 490 */       char c = paramString.charAt(l);
/* 491 */       switch (c)
/*     */       {
/*     */       case '"':
/*     */       case '\'':
/* 495 */         k = EscapeTokenizer.nextState(k, c);
/* 496 */         break;
/*     */       case '(':
/* 499 */         if (k != 1)
/*     */           continue;
/* 501 */         l = EscapeTokenizer.matchClosingChar(c, new StringBuffer(paramString), l);
/*     */ 
/* 503 */         if (l >= 0)
/*     */           continue;
/* 505 */         ErrorMessage.raiseError("JZ0S8", this._body); break;
/*     */       case ',':
/* 511 */         if (k != 1)
/*     */           continue;
/* 513 */         j = l;
/* 514 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 521 */     return j;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.Escape
 * JD-Core Version:    0.5.4
 */