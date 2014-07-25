/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*      */ import com.sybase.jdbc3.jdbc.Protocol;
/*      */ import java.io.IOException;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class TdsScrollResultSet extends TdsResultSet
/*      */ {
/*   94 */   protected static int UNDEFINED = -47;
/*      */ 
/*   99 */   protected boolean _resultSetHasAtLeastOneRow = false;
/*  100 */   protected boolean _resultSetIsEmpty = false;
/*      */ 
/*      */   protected TdsScrollResultSet(TdsProtocolContext paramTdsProtocolContext)
/*      */     throws SQLException
/*      */   {
/*  109 */     super(paramTdsProtocolContext);
/*      */ 
/*  111 */     this._serverSideScrolling = true;
/*      */   }
/*      */ 
/*      */   public boolean previous()
/*      */     throws SQLException
/*      */   {
/*  122 */     if ((quickEmptyResultSetCheck()) || (isBeforeFirst()))
/*      */     {
/*  125 */       return false;
/*      */     }
/*      */ 
/*  128 */     boolean bool = false;
/*      */ 
/*  130 */     if (this._rowIndex > 1)
/*      */     {
/*  134 */       this._rowIndex -= 1;
/*  135 */       this._columns = ((TdsDataObject[])this._cachedRows.get(this._rowIndex - 1));
/*  136 */       bool = true;
/*      */     }
/*      */     else
/*      */     {
/*  145 */       advanceColumnArray();
/*  146 */       int i = -1;
/*  147 */       int j = -1;
/*  148 */       int k = this._tpc._cursor.getFetchSize();
/*  149 */       int l = getRowNumberNoCurInfo();
/*  150 */       if ((l == -1) && (!isAfterLast()))
/*      */       {
/*  154 */         l = getRowNumber();
/*      */       }
/*  156 */       int i1 = this._tpc._cursor.getTotalRowCount();
/*      */ 
/*  158 */       if (l != -1)
/*      */       {
/*  160 */         if (l == 1)
/*      */         {
/*  162 */           i = 0;
/*      */         }
/*  164 */         else if (l <= k)
/*      */         {
/*  166 */           i = 1;
/*  167 */           j = l - 1;
/*      */         }
/*      */         else
/*      */         {
/*  171 */           i = l - k;
/*  172 */           j = l - 1;
/*      */         }
/*      */       }
/*  175 */       else if (i1 != -1)
/*      */       {
/*  179 */         j = i1;
/*  180 */         if (i1 < k)
/*      */         {
/*  182 */           i = 1;
/*      */         }
/*      */         else
/*      */         {
/*  186 */           i = i1 - k + 1;
/*      */         }
/*      */       }
/*  189 */       if (i != -1)
/*      */       {
/*  193 */         this._tpc._cursor.fetch(2, 0, false);
/*  194 */         bool = next();
/*      */ 
/*  196 */         for (int i2 = i; ; ++i2) { if (i2 >= j)
/*      */             break label259;
/*  198 */           bool = next(); }
/*      */ 
/*      */ 
/*      */       }
/*      */ 
/*  207 */       this._tpc._cursor.fetch(6, -1, false);
/*  208 */       bool = next();
/*      */     }
/*      */ 
/*  211 */     label259: return bool;
/*      */   }
/*      */ 
/*      */   public boolean relative(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  221 */     boolean bool1 = true;
/*  222 */     int i = 0;
/*  223 */     boolean bool2 = false;
/*  224 */     int j = this._rowIndex + paramInt - 1;
/*      */ 
/*  226 */     if (quickEmptyResultSetCheck())
/*      */     {
/*  229 */       return false;
/*      */     }
/*      */ 
/*  232 */     int k = getRowNumberNoCurInfo();
/*  233 */     if (k != -1)
/*      */     {
/*  235 */       k = k + this._rowIndex - 1;
/*      */     }
/*      */ 
/*  238 */     int l = this._tpc._cursor.getTotalRowCount();
/*  239 */     int i1 = this._tpc._cursor.getFetchSize();
/*      */ 
/*  244 */     if (paramInt < 0)
/*      */     {
/*  246 */       if (!isBeforeFirst())
/*      */       {
/*  250 */         if (this._rowIndex + paramInt > 0)
/*      */         {
/*  255 */           this._rowIndex += paramInt;
/*  256 */           this._columns = ((TdsDataObject[])this._cachedRows.get(this._rowIndex - 1));
/*      */         }
/*      */         else
/*      */         {
/*  262 */           i = 1;
/*  263 */           advanceColumnArray();
/*      */         }
/*      */       }
/*      */     }
/*  266 */     else if (paramInt != 0)
/*      */     {
/*  272 */       if (!isAfterLast())
/*      */       {
/*  276 */         if (this._rowIndex + paramInt <= this._lastFetchSize)
/*      */         {
/*  279 */           if ((l != -1) && (k != -1) && 
/*  282 */             (k + paramInt > l))
/*      */           {
/*  289 */             advanceColumnArray();
/*  290 */             i = 1;
/*      */           }
/*      */ 
/*  293 */           if (i != 0)
/*      */           {
/*      */             break label232;
/*      */           }
/*      */ 
/*  300 */           for (int i2 = 0; ; ++i2) { if (i2 >= paramInt)
/*      */               break label232;
/*  302 */             if (next())
/*      */             {
/*      */               continue;
/*      */             }
/*      */ 
/*  311 */             i = 1;
/*  312 */             break label232: }
/*      */ 
/*      */ 
/*      */         }
/*      */ 
/*  324 */         advanceColumnArray();
/*  325 */         i = 1;
/*      */       }
/*      */     }
/*      */ 
/*  329 */     if (i != 0)
/*      */     {
/*  331 */       if ((isAfterLast()) && (paramInt < 0))
/*      */       {
/*  338 */         if (l != -1)
/*      */         {
/*  340 */           if ((Math.abs(paramInt) > l) && (Math.abs(paramInt) <= i1))
/*      */           {
/*  348 */             label232: first();
/*  349 */             previous();
/*  350 */             return false;
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  359 */           bool2 = true;
/*      */         }
/*      */       }
/*  362 */       else if (paramInt < 0)
/*      */       {
/*  366 */         bool2 = true;
/*      */       }
/*      */ 
/*  370 */       this._tpc._cursor.fetch(6, j, bool2);
/*      */ 
/*  372 */       bool1 = next();
/*      */     }
/*  374 */     return bool1;
/*      */   }
/*      */ 
/*      */   public boolean first()
/*      */     throws SQLException
/*      */   {
/*  383 */     if (quickEmptyResultSetCheck())
/*      */     {
/*  386 */       return false;
/*      */     }
/*  388 */     boolean bool = false;
/*      */ 
/*  391 */     int i = getRowNumberNoCurInfo();
/*  392 */     if ((i != -1) && (i - this._rowIndex == 0))
/*      */     {
/*  395 */       this._rowIndex = 1;
/*  396 */       this._columns = ((TdsDataObject[])this._cachedRows.get(this._rowIndex - 1));
/*  397 */       return true;
/*      */     }
/*      */ 
/*  401 */     advanceColumnArray();
/*  402 */     this._tpc._cursor.fetch(3, 0, false);
/*  403 */     bool = next();
/*  404 */     if (!bool)
/*      */     {
/*  410 */       this._resultSetIsEmpty = true;
/*      */     }
/*      */     else
/*      */     {
/*  416 */       this._tpc._cursor.setRowNum(1);
/*      */     }
/*  418 */     return bool;
/*      */   }
/*      */ 
/*      */   public boolean last()
/*      */     throws SQLException
/*      */   {
/*  427 */     if (quickEmptyResultSetCheck())
/*      */     {
/*  429 */       return false;
/*      */     }
/*      */ 
/*  432 */     boolean bool1 = false;
/*  433 */     boolean bool2 = false;
/*      */ 
/*  436 */     int i = this._tpc._cursor.getTotalRowCount();
/*  437 */     int j = getRowNumberNoCurInfo();
/*  438 */     int k = this._tpc._cursor.getFetchSize();
/*      */     int l;
/*  440 */     if ((i != -1) && (j != -1))
/*      */     {
/*  443 */       if (j - this._rowIndex + this._lastFetchSize >= i)
/*      */       {
/*  447 */         for (l = j; l < i; ++l)
/*      */         {
/*  449 */           next();
/*      */         }
/*  451 */         return true;
/*      */       }
/*      */     }
/*  454 */     else if (i == -1)
/*      */     {
/*  459 */       bool2 = true;
/*      */     }
/*      */ 
/*  463 */     advanceColumnArray();
/*  464 */     this._tpc._cursor.fetch(4, 0, bool2);
/*  465 */     bool1 = next();
/*      */ 
/*  475 */     if (k != 1) if (!bool2)
/*      */       {
/*  479 */         if (k <= i)
/*      */         {
/*  484 */           l = i - k + 1;
/*  485 */           for (int i1 = l; ; ++i1) { if (i1 >= i)
/*      */               break label190;
/*  487 */             bool1 = next(); }
/*      */ 
/*      */ 
/*      */         }
/*      */ 
/*  493 */         for (l = 1; l < i; ++l)
/*      */         {
/*  495 */           bool1 = next();
/*      */         }
/*      */       }
/*      */ 
/*  499 */     if (!bool1)
/*      */     {
/*  505 */       label190: this._resultSetIsEmpty = true;
/*      */     }
/*      */ 
/*  508 */     return bool1;
/*      */   }
/*      */ 
/*      */   public boolean isBeforeFirst()
/*      */     throws SQLException
/*      */   {
/*  522 */     int i = 0;
/*  523 */     if ((this._rowIndex == 0) && 
/*  526 */       (this._tpc._cursor.getRowNum() == 0))
/*      */     {
/*  528 */       i = 1;
/*      */     }
/*      */ 
/*  531 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean isFirst()
/*      */     throws SQLException
/*      */   {
/*  540 */     int i = 0;
/*      */ 
/*  547 */     if (this._rowIndex != 1)
/*      */     {
/*  549 */       i = 0;
/*      */     }
/*  551 */     else if (this._tpc._cursor.getRowNum() == 1)
/*      */     {
/*  553 */       i = 1;
/*      */     }
/*  555 */     else if (getRowNumber() == 1)
/*      */     {
/*  557 */       i = 1;
/*      */     }
/*  559 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean isLast()
/*      */     throws SQLException
/*      */   {
/*  569 */     int i = 0;
/*  570 */     int j = this._tpc._cursor.getTotalRowCount();
/*  571 */     int k = getRowNumberNoCurInfo();
/*      */ 
/*  578 */     if (this._rowIndex == 0)
/*      */     {
/*  580 */       i = 0;
/*      */     }
/*  582 */     else if ((j != -1) && (k != -1))
/*      */     {
/*  585 */       if (j == k)
/*      */       {
/*  587 */         i = 1;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  594 */       this._tpc._cursor.doCurInfo(2, 131);
/*  595 */       j = this._tpc._cursor.getTotalRowCount();
/*  596 */       k = getRowNumberNoCurInfo();
/*  597 */       if ((j != -1) && (k != -1))
/*      */       {
/*  600 */         if (j == k)
/*      */         {
/*  602 */           i = 1;
/*      */         }
/*      */ 
/*      */       }
/*  614 */       else if (next())
/*      */       {
/*  618 */         previous();
/*  619 */         i = 0;
/*      */       }
/*      */       else
/*      */       {
/*  628 */         this._tpc._cursor.fetch(1, 0, true);
/*  629 */         if (next())
/*      */         {
/*  634 */           i = 0;
/*      */         }
/*      */         else
/*      */         {
/*  641 */           i = 1;
/*      */         }
/*      */ 
/*  645 */         previous();
/*      */       }
/*      */     }
/*      */ 
/*  649 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean isAfterLast()
/*      */     throws SQLException
/*      */   {
/*  658 */     int i = 0;
/*  659 */     if ((this._rowIndex == 0) && (this._tpc._cursor.getRowNum() == -2))
/*      */     {
/*  662 */       i = 1;
/*      */     }
/*  664 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean absolute(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  673 */     boolean bool = true;
/*  674 */     int i = 1;
/*      */ 
/*  676 */     if (quickEmptyResultSetCheck())
/*      */     {
/*  679 */       return false;
/*      */     }
/*      */ 
/*  682 */     int j = this._tpc._cursor.getTotalRowCount();
/*  683 */     int k = getRowNumberNoCurInfo();
/*      */ 
/*  689 */     if (paramInt == 0)
/*      */     {
/*  691 */       i = 1;
/*      */     }
/*  693 */     else if ((j != -1) && (k != -1) && (Math.abs(paramInt) <= j))
/*      */     {
/*  697 */       int l = paramInt;
/*  698 */       if (paramInt < 0)
/*      */       {
/*  700 */         l = j + 1 + paramInt;
/*      */       }
/*  702 */       int i1 = k - this._rowIndex + this._lastFetchSize;
/*  703 */       int i2 = k - this._rowIndex + 1;
/*      */       int i3;
/*  704 */       if ((l >= k) && (l <= i1))
/*      */       {
/*  707 */         for (i3 = k; i3 < l; ++i3)
/*      */         {
/*  709 */           next();
/*      */         }
/*  711 */         i = 0;
/*      */       }
/*  713 */       else if ((l < k) && (l >= i2))
/*      */       {
/*  717 */         for (i3 = k; i3 > l; --i3)
/*      */         {
/*  719 */           previous();
/*      */         }
/*  721 */         i = 0;
/*      */       }
/*      */     }
/*  724 */     if (i != 0)
/*      */     {
/*  726 */       advanceColumnArray();
/*      */ 
/*  735 */       this._tpc._cursor.fetch(5, paramInt, true);
/*  736 */       bool = next();
/*      */     }
/*      */ 
/*  741 */     return bool;
/*      */   }
/*      */ 
/*      */   public boolean next()
/*      */     throws SQLException
/*      */   {
/*  763 */     if (this._rowIndex < this._cachedRows.size())
/*      */     {
/*  765 */       this._rowIndex += 1;
/*      */ 
/*  771 */       this._columns = ((TdsDataObject[])this._cachedRows.get(this._rowIndex - 1));
/*      */ 
/*  773 */       return true;
/*      */     }
/*      */ 
/*  778 */     if (this._dead)
/*      */     {
/*  781 */       return false;
/*      */     }
/*      */ 
/*  784 */     advanceColumnArray();
/*      */ 
/*  786 */     int i = nextResult();
/*      */ 
/*  788 */     switch (i)
/*      */     {
/*      */     case 209:
/*  793 */       this._rowCount += 1;
/*  794 */       this._rowIndex += 1;
/*      */ 
/*  796 */       cacheCurrentRow();
/*  797 */       this._needNext = false;
/*  798 */       this._resultSetHasAtLeastOneRow = true;
/*  799 */       return true;
/*      */     case 5:
/*  803 */       this._rowCount = this._tpc._protocol.count(this._tpc);
/*  804 */       if (this._tpc._cursor != null)
/*      */       {
/*  806 */         this._totalCursorRows += this._rowCount;
/*      */       }
/*      */ 
/*  809 */       markDead();
/*  810 */       return false;
/*      */     }
/*      */ 
/*  813 */     this._tpc._protocol.ungetResult(this._tpc, i);
/*  814 */     markDead();
/*  815 */     return false;
/*      */   }
/*      */ 
/*      */   private void advanceColumnArray()
/*      */     throws SQLException
/*      */   {
/*  823 */     if (this._needNext)
/*      */       return;
/*  825 */     this._tpc._lastResult = -1;
/*      */     try
/*      */     {
/*  828 */       this._columns = this._savedCols;
/*      */ 
/*  832 */       for (int i = 0; i < this._totalColumns; ++i)
/*      */       {
/*  834 */         this._columns[i].clear();
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  840 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void prepareForNextFetch()
/*      */   {
/*  855 */     this._needNext = true;
/*  856 */     this._rowIndex = 0;
/*  857 */     this._lastFetchSize = this._tpc._cursor.getFetchSizeForLastFetch();
/*      */ 
/*  861 */     this._cachedRows = new Vector();
/*  862 */     this._dead = false;
/*      */   }
/*      */ 
/*      */   public boolean isResultSetEmpty()
/*      */     throws SQLException
/*      */   {
/*  870 */     int i = 0;
/*  871 */     if (quickEmptyResultSetCheck())
/*      */     {
/*  873 */       i = 1;
/*      */     }
/*  875 */     else if (this._resultSetHasAtLeastOneRow)
/*      */     {
/*  880 */       i = 0;
/*      */     }
/*      */     else
/*      */     {
/*  893 */       int j = this._tpc._cursor.getFetchSize();
/*  894 */       if (j > 1)
/*      */       {
/*  896 */         this._tpc._cursor.setFetchSize(1);
/*      */       }
/*      */ 
/*  900 */       this._tpc._cursor.fetch(1, 0, false);
/*  901 */       boolean bool = next();
/*  902 */       if (bool)
/*      */       {
/*  904 */         previous();
/*      */       }
/*      */       else
/*      */       {
/*  908 */         i = 1;
/*      */       }
/*      */ 
/*  911 */       if (j > 1)
/*      */       {
/*  913 */         this._tpc._cursor.setFetchSize(j);
/*      */       }
/*      */     }
/*  916 */     return i;
/*      */   }
/*      */ 
/*      */   private boolean quickEmptyResultSetCheck()
/*      */   {
/*  927 */     int i = 0;
/*      */ 
/*  929 */     if (this._resultSetIsEmpty)
/*      */     {
/*  931 */       i = 1;
/*      */     }
/*  933 */     else if (this._resultSetHasAtLeastOneRow)
/*      */     {
/*  935 */       i = 0;
/*      */     }
/*  937 */     else if (this._tpc._cursor.getTotalRowCount() == 0)
/*      */     {
/*  939 */       this._resultSetIsEmpty = true;
/*  940 */       i = 1;
/*      */     }
/*  942 */     else if (this._tpc._cursor.getTotalRowCount() > 0)
/*      */     {
/*  944 */       this._resultSetHasAtLeastOneRow = true;
/*  945 */       i = 0;
/*      */     }
/*      */     else
/*      */     {
/*  955 */       i = 0;
/*      */     }
/*  957 */     return i;
/*      */   }
/*      */ 
/*      */   public int getRowNumber()
/*      */     throws SQLException
/*      */   {
/*  967 */     int i = -1;
/*      */ 
/*  971 */     if (this._rowIndex != 0)
/*      */     {
/*  973 */       int j = this._tpc._cursor.getRowNum();
/*  974 */       if (j != -1)
/*      */       {
/*  976 */         i = j + this._rowIndex - 1;
/*      */       }
/*      */       else
/*      */       {
/*  982 */         this._tpc._cursor.doCurInfo(2, 131);
/*  983 */         j = this._tpc._cursor.getRowNum();
/*  984 */         if (j != -1)
/*      */         {
/*  986 */           i = j + this._rowIndex - 1;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  992 */     return i;
/*      */   }
/*      */ 
/*      */   private int getRowNumberNoCurInfo()
/*      */   {
/* 1003 */     int i = this._tpc._cursor.getRowNum();
/* 1004 */     if (i > 0)
/*      */     {
/* 1006 */       i = i + this._rowIndex - 1;
/*      */     }
/*      */     else
/*      */     {
/* 1010 */       i = -1;
/*      */     }
/* 1012 */     return i;
/*      */   }
/*      */ 
/*      */   protected void dump()
/*      */   {
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsScrollResultSet
 * JD-Core Version:    0.5.4
 */