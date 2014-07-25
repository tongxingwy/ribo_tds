/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ class EscapeTokenizer
/*     */ {
/*     */   static final int START = 1;
/*     */   static final int IN_SINGLE_QUOTES = 2;
/*     */   static final int IN_DOUBLE_QUOTES = 3;
/*     */   private StringBuffer _text;
/*     */   private char[] _textCharBuf;
/*     */   private int _nextIndex;
/*     */   private SybStatement _stmt;
/*     */   private int _mark;
/*  74 */   private boolean _openCursor = false;
/*     */   private static final String SQL_DELIMITERS = " %&'()*+,-./:;<=>?[|\t\n";
/*     */ 
/*     */   EscapeTokenizer(SybStatement paramSybStatement, String paramString)
/*     */   {
/*  87 */     updateText(new StringBuffer(paramString));
/*  88 */     this._stmt = paramSybStatement;
/*  89 */     this._nextIndex = 0;
/*  90 */     this._stmt._type = 0;
/*  91 */     this._stmt._setEnd = 0;
/*     */   }
/*     */ 
/*     */   protected Object next()
/*     */     throws SQLException
/*     */   {
/* 112 */     int i = 1;
/*     */ 
/* 117 */     if (this._nextIndex == 0)
/*     */     {
/* 119 */       String str1 = nextWord(0);
/* 120 */       if (("create".equalsIgnoreCase(str1)) && 
/* 122 */         (this._stmt instanceof SybPreparedStatement) && (((SybPreparedStatement)this._stmt)._dynStmtName != null))
/*     */       {
/* 132 */         ErrorMessage.raiseError("XXXXX");
/*     */       }
/*     */ 
/* 136 */       if ("exec".equalsIgnoreCase(str1))
/*     */       {
/* 139 */         this._nextIndex = this._text.length();
/* 140 */         this._stmt._type = 1;
/* 141 */         return new Escape(this._text.toString(), 1);
/*     */       }
/* 143 */       if ("select".equalsIgnoreCase(str1))
/*     */       {
/* 147 */         this._stmt._type = 2;
/*     */       }
/* 150 */       else if ("delete".equalsIgnoreCase(str1))
/*     */       {
/* 155 */         this._stmt._type = 4;
/*     */ 
/* 157 */         str1 = nextWord(-1);
/*     */ 
/* 159 */         if ((str1 != null) && (str1.equalsIgnoreCase("from")))
/*     */         {
/* 161 */           str1 = nextWord(-1);
/*     */         }
/*     */ 
/* 164 */         this._stmt._table = completeTableName(str1);
/*     */       }
/* 167 */       else if ("update".equalsIgnoreCase(str1))
/*     */       {
/* 172 */         this._stmt._type = 8;
/*     */ 
/* 174 */         str1 = nextWord(-1);
/*     */ 
/* 176 */         str1 = completeTableName(str1);
/*     */ 
/* 178 */         if (str1 != null)
/*     */         {
/* 181 */           this._stmt._setStart = (this._mark - 1);
/*     */         }
/* 183 */         this._stmt._table = str1;
/*     */       }
/* 185 */       else if ("insert".equalsIgnoreCase(str1))
/*     */       {
/* 187 */         this._stmt._type = 16;
/*     */ 
/* 189 */         str1 = nextWord(-1);
/*     */ 
/* 191 */         if ((str1 != null) && (str1.equalsIgnoreCase("into")))
/*     */         {
/* 193 */           str1 = nextWord(-1);
/*     */         }
/*     */ 
/* 196 */         this._stmt._table = completeTableName(str1);
/*     */       }
/*     */     }
/*     */     Object localObject1;
/* 199 */     if (this._nextIndex >= this._text.length())
/*     */     {
/* 201 */       localObject1 = null;
/*     */     }
/*     */     else
/*     */     {
/*     */       int j;
/*     */       char c;
/*     */       Object localObject2;
/* 203 */       if (this._text.charAt(this._nextIndex) == '{')
/*     */       {
/* 206 */         j = -1;
/*     */ 
/* 208 */         for (int k = this._nextIndex + 1; k < this._text.length(); ++k)
/*     */         {
/* 210 */           c = this._text.charAt(k);
/* 211 */           switch (c)
/*     */           {
/*     */           case '"':
/*     */           case '\'':
/* 215 */             i = nextState(i, c);
/* 216 */             break;
/*     */           case '}':
/* 218 */             if (i != 1)
/*     */               continue;
/* 220 */             j = k;
/* 221 */             break;
/*     */           case '{':
/* 225 */             if (i != 1) {
/*     */               continue;
/*     */             }
/* 228 */             int i1 = matchClosingChar(c, this._text, k) + 1;
/* 229 */             if (i1 == 0)
/*     */             {
/* 231 */               ErrorMessage.raiseError("JZ0S8", this._text.toString());
/*     */             }
/*     */ 
/* 237 */             String str3 = this._stmt.processEscapes(new String(this._textCharBuf, k, i1 - k));
/*     */ 
/* 243 */             int i2 = str3.length();
/* 244 */             int i3 = k + i2 + this._textCharBuf.length - i1;
/* 245 */             StringBuffer localStringBuffer = new StringBuffer(i3);
/* 246 */             localStringBuffer.append(this._textCharBuf, 0, k);
/* 247 */             localStringBuffer.append(str3);
/* 248 */             localStringBuffer.append(this._textCharBuf, i1, this._textCharBuf.length - i1);
/*     */ 
/* 253 */             k += i2 - 1;
/* 254 */             updateText(localStringBuffer);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 261 */         if (j == -1)
/*     */         {
/* 263 */           ErrorMessage.raiseError("JZ0S8", this._text.toString());
/*     */         }
/*     */ 
/* 267 */         String str2 = new String(this._textCharBuf, this._nextIndex, j + 1 - this._nextIndex);
/*     */ 
/* 270 */         localObject2 = new Escape(str2);
/* 271 */         localObject1 = localObject2;
/*     */ 
/* 279 */         this._stmt._setEnd -= str2.length() - ((Escape)localObject2).getBody().length();
/* 280 */         switch (((Escape)localObject2).getType())
/*     */         {
/*     */         case 4:
/* 285 */           if (!this._stmt._context._conn._props.getBoolean(52))
/*     */           {
/* 287 */             this._stmt._setEnd += "1970-1-1 ".length(); } break;
/*     */         case 3:
/* 291 */           this._stmt._setEnd += "00:00:00 ".length();
/* 292 */           break;
/*     */         case 1:
/*     */         case 2:
/* 295 */           this._stmt._type = 1;
/*     */         }
/*     */ 
/* 303 */         this._nextIndex = (j + 1);
/*     */       }
/*     */       else
/*     */       {
/* 311 */         j = -1;
/*     */ 
/* 313 */         for (int l = this._nextIndex; l < this._text.length(); ++l)
/*     */         {
/* 315 */           c = this._text.charAt(l);
/* 316 */           switch (c)
/*     */           {
/*     */           case '"':
/*     */           case '\'':
/* 320 */             i = nextState(i, c);
/* 321 */             break;
/*     */           case '{':
/* 323 */             if (i != 1)
/*     */               continue;
/* 325 */             j = l;
/* 326 */             break;
/*     */           case 'F':
/*     */           case 'f':
/* 331 */             if ((i != 1) || (this._stmt._type != 2))
/*     */             {
/*     */               continue;
/*     */             }
/*     */ 
/* 337 */             localObject2 = nextWord(l);
/*     */ 
/* 341 */             if ("from".equalsIgnoreCase((String)localObject2))
/*     */             {
/* 343 */               localObject2 = nextWord(-1);
/*     */ 
/* 345 */               this._stmt._table = completeTableName((String)localObject2);
/* 346 */               if (this._stmt._cursor != null)
/*     */               {
/* 348 */                 this._stmt._cursor.setTable(this._stmt._table);
/*     */               }
/*     */ 
/* 354 */               if ((this._stmt._context._conn._props.getBoolean(33)) && (this._stmt._cursor == null)) {
/*     */                 continue;
/*     */               }
/*     */ 
/* 358 */               this._stmt._type |= 4096;
/*     */             }
/*     */             else
/*     */             {
/* 364 */               if ((!this._stmt._context._conn._props.getBoolean(33)) || (!"for".equalsIgnoreCase((String)localObject2))) {
/*     */                 continue;
/*     */               }
/*     */ 
/* 368 */               localObject2 = nextWord(-1);
/* 369 */               if ("update".equalsIgnoreCase((String)localObject2))
/*     */               {
/* 380 */                 this._openCursor = true;
/*     */               }
/*     */ 
/* 383 */               this._stmt._type |= 4096; } break;
/*     */           case 'W':
/*     */           case 'w':
/* 390 */             if ((i != 1) || ((this._stmt._type & (0x4 | 0x8)) == 0))
/*     */             {
/*     */               continue;
/*     */             }
/*     */ 
/* 398 */             localObject2 = nextWord(l);
/* 399 */             if (!"where".equalsIgnoreCase((String)localObject2))
/*     */               continue;
/* 401 */             localObject2 = nextWord(-1);
/* 402 */             if (!"current".equalsIgnoreCase((String)localObject2))
/*     */               continue;
/* 404 */             localObject2 = nextWord(-1);
/* 405 */             if (!"of".equalsIgnoreCase((String)localObject2))
/*     */               continue;
/* 407 */             if ((this._stmt instanceof SybPreparedStatement) && (((SybPreparedStatement)this._stmt)._dynStmtName != null))
/*     */             {
/* 413 */               ErrorMessage.raiseError("XXXXX");
/*     */             }
/*     */ 
/* 416 */             this._stmt._type |= 4096;
/* 417 */             this._stmt._setEnd += l - 1;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 430 */         if (j == -1)
/*     */         {
/* 432 */           j = this._text.length();
/*     */         }
/*     */ 
/* 436 */         localObject1 = new String(this._textCharBuf, this._nextIndex, j - this._nextIndex);
/*     */ 
/* 438 */         this._nextIndex = j;
/*     */       }
/*     */     }
/*     */ 
/* 442 */     if (this._openCursor)
/*     */     {
/* 448 */       this._stmt.checkCursor(false, 1008);
/* 449 */       this._stmt._cursor.getName();
/*     */ 
/* 452 */       this._openCursor = false;
/*     */     }
/* 454 */     return localObject1;
/*     */   }
/*     */ 
/*     */   protected static int matchClosingChar(char paramChar, StringBuffer paramStringBuffer, int paramInt)
/*     */   {
/* 463 */     int i = 1;
/* 464 */     int j = -1;
/*     */ 
/* 468 */     for (int k = paramInt + 1; k < paramStringBuffer.length(); ++k)
/*     */     {
/* 470 */       char c = paramStringBuffer.charAt(k);
/* 471 */       switch (c)
/*     */       {
/*     */       case '"':
/*     */       case '\'':
/* 475 */         i = nextState(i, c);
/* 476 */         break;
/*     */       case ')':
/*     */       case '}':
/* 479 */         if ((i != 1) || (
/* 481 */           (((c != '}') || (paramChar != '{'))) && (((c != ')') || (paramChar != '('))))) {
/*     */           continue;
/*     */         }
/* 484 */         j = k;
/* 485 */         break;
/*     */       case '(':
/*     */       case '{':
/* 493 */         if ((i != 1) || 
/* 495 */           (c != paramChar))
/*     */           continue;
/* 497 */         k = matchClosingChar(c, paramStringBuffer, k);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 505 */     return j;
/*     */   }
/*     */ 
/*     */   protected static int nextState(int paramInt, char paramChar)
/*     */   {
/* 512 */     int i = paramInt;
/* 513 */     switch (paramChar)
/*     */     {
/*     */     case '\'':
/* 516 */       if (paramInt == 2)
/*     */       {
/* 518 */         i = 1; break label68:
/*     */       }
/* 520 */       if (paramInt != 1)
/*     */         break label68;
/* 522 */       i = 2; break;
/*     */     case '"':
/* 526 */       if (paramInt == 3)
/*     */       {
/* 528 */         i = 1; break label68:
/*     */       }
/* 530 */       if (paramInt != 1)
/*     */         break label68;
/* 532 */       i = 3;
/*     */     }
/*     */ 
/* 540 */     label68: return i;
/*     */   }
/*     */ 
/*     */   private void updateText(StringBuffer paramStringBuffer)
/*     */   {
/* 547 */     this._text = paramStringBuffer;
/* 548 */     int i = paramStringBuffer.length();
/* 549 */     this._textCharBuf = new char[i];
/* 550 */     if (i <= 0)
/*     */       return;
/* 552 */     this._text.getChars(0, i, this._textCharBuf, 0);
/*     */   }
/*     */ 
/*     */   private String nextWord(int paramInt)
/*     */   {
/* 562 */     if (paramInt >= 0)
/*     */     {
/* 564 */       this._mark = paramInt;
/*     */     }
/* 566 */     String str = null;
/* 567 */     int i = this._text.length();
/*     */ 
/* 569 */     while ((this._mark < i) && (" %&'()*+,-./:;<=>?[|\t\n".indexOf(this._text.charAt(this._mark)) != -1))
/*     */     {
/* 571 */       this._mark += 1;
/*     */     }
/*     */ 
/* 577 */     if (this._mark >= i)
/*     */     {
/* 581 */       str = null;
/*     */     }
/*     */     else
/*     */     {
/* 592 */       if ((this._mark > 0) && (this._text.charAt(this._mark) == '"'))
/*     */       {
/* 596 */         i = this._text.toString().indexOf('"', this._mark + 1) + 1;
/*     */       }
/*     */       else
/*     */       {
/* 601 */         for (int j = this._mark; j < i; ++j)
/*     */         {
/* 604 */           if (" %&'()*+,-./:;<=>?[|\t\n".indexOf(this._text.charAt(j)) == -1)
/*     */             continue;
/* 606 */           i = j;
/* 607 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 611 */       if (i > this._mark)
/*     */       {
/* 613 */         str = String.copyValueOf(this._textCharBuf, this._mark, i - this._mark);
/* 614 */         this._mark = (i + 1);
/* 615 */         i = this._text.length();
/*     */       }
/*     */     }
/*     */ 
/* 619 */     return str;
/*     */   }
/*     */ 
/*     */   private String completeTableName(String paramString)
/*     */   {
/* 625 */     String str = paramString;
/*     */ 
/* 627 */     for (int i = 0; i < 2; ++i)
/*     */     {
/* 629 */       if (this._mark >= this._text.length())
/*     */         continue;
/* 631 */       if (this._text.charAt(this._mark - 1) != '.') {
/*     */         break;
/*     */       }
/*     */ 
/* 635 */       if (this._text.charAt(this._mark) == '.')
/*     */       {
/* 637 */         str = str + ".";
/* 638 */         ++i;
/*     */       }
/* 640 */       str = str + "." + nextWord(-1);
/*     */     }
/*     */ 
/* 643 */     return str;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.EscapeTokenizer
 * JD-Core Version:    0.5.4
 */