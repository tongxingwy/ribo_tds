/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.LogUtil;
/*     */ import java.sql.SQLException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class SybScrollCursorResultSet extends SybCursorResultSet
/*     */   implements com.sybase.jdbcx.SybCursorResultSet
/*     */ {
/*  44 */   private static Logger LOG = Logger.getLogger(SybScrollCursorResultSet.class.getName());
/*  45 */   private static volatile long _logIdCounter = 0L;
/*     */ 
/*  47 */   private int _totalNumRows = -4;
/*     */ 
/*     */   protected SybScrollCursorResultSet(String paramString, SybStatement paramSybStatement, ProtocolResultSet paramProtocolResultSet)
/*     */     throws SQLException
/*     */   {
/*  54 */     super(paramString, paramSybStatement, paramProtocolResultSet);
/*  55 */     this._logId = (paramString + "_Sr" + _logIdCounter++);
/*  56 */     int i = this._cursor.getTotalRowCount();
/*  57 */     if (i == -1) {
/*     */       return;
/*     */     }
/*     */ 
/*  61 */     this._totalNumRows = i;
/*     */   }
/*     */ 
/*     */   public boolean absolute(int paramInt)
/*     */     throws SQLException
/*     */   {
/*  76 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/*  78 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/*  80 */         LOG.finer(this._logId + " absolute(int = [" + paramInt + "])");
/*     */       }
/*  82 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/*  84 */         LOG.fine(this._logId + " absolute(int)");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  89 */     clearWarnings();
/*  90 */     boolean bool = false;
/*  91 */     if (paramInt == 0)
/*     */     {
/*  97 */       beforeFirst();
/*     */ 
/*  99 */       return false;
/*     */     }
/* 101 */     if (this._prs.isResultSetEmpty())
/*     */     {
/* 105 */       return bool;
/*     */     }
/*     */ 
/* 109 */     bool = this._prs.absolute(paramInt);
/*     */ 
/* 111 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean previous() throws SQLException
/*     */   {
/* 116 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 118 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 120 */       LOG.fine(this._logId + " previous()");
/*     */     }
/*     */ 
/* 127 */     clearWarnings();
/* 128 */     boolean bool = this._prs.previous();
/* 129 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean relative(int paramInt) throws SQLException
/*     */   {
/* 134 */     if (LogUtil.isLoggingEnabled(LOG))
/*     */     {
/* 136 */       if (LOG.isLoggable(Level.FINER))
/*     */       {
/* 138 */         LOG.finer(this._logId + " relative(int = [" + paramInt + "])");
/*     */       }
/* 140 */       else if (LOG.isLoggable(Level.FINE))
/*     */       {
/* 142 */         LOG.fine(this._logId + " relative(int)");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 150 */     clearWarnings();
/* 151 */     boolean bool = false;
/*     */ 
/* 164 */     if ((this._prs.isBeforeFirst()) || (this._prs.isAfterLast()))
/*     */     {
/* 166 */       return false;
/*     */     }
/*     */ 
/* 170 */     bool = this._prs.relative(paramInt);
/*     */ 
/* 172 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean first() throws SQLException
/*     */   {
/* 177 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 179 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 181 */       LOG.fine(this._logId + " first()");
/*     */     }
/*     */ 
/* 186 */     clearWarnings();
/* 187 */     boolean bool = this._prs.first();
/* 188 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean last() throws SQLException
/*     */   {
/* 193 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 195 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 197 */       LOG.fine(this._logId + " last()");
/*     */     }
/*     */ 
/* 202 */     clearWarnings();
/* 203 */     boolean bool = this._prs.last();
/* 204 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean isBeforeFirst() throws SQLException
/*     */   {
/* 209 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 211 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 213 */       LOG.fine(this._logId + " isBeforeFirst()");
/*     */     }
/*     */ 
/* 218 */     boolean bool = false;
/*     */ 
/* 222 */     if (!this._prs.isResultSetEmpty())
/*     */     {
/* 224 */       bool = this._prs.isBeforeFirst();
/*     */     }
/* 226 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean isFirst() throws SQLException
/*     */   {
/* 231 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 233 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 235 */       LOG.fine(this._logId + " isFirst()");
/*     */     }
/*     */ 
/* 240 */     boolean bool = this._prs.isFirst();
/* 241 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean isLast() throws SQLException
/*     */   {
/* 246 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 248 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 250 */       LOG.fine(this._logId + " isLast()");
/*     */     }
/*     */ 
/* 255 */     boolean bool = this._prs.isLast();
/* 256 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean isAfterLast() throws SQLException
/*     */   {
/* 261 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 263 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 265 */       LOG.fine(this._logId + " isAfterLast()");
/*     */     }
/*     */ 
/* 270 */     boolean bool = false;
/*     */ 
/* 273 */     if (!this._prs.isResultSetEmpty())
/*     */     {
/* 275 */       bool = this._prs.isAfterLast();
/*     */     }
/* 277 */     return bool;
/*     */   }
/*     */ 
/*     */   public int getRow() throws SQLException
/*     */   {
/* 282 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 284 */       (LOG.isLoggable(Level.FINE)))
/*     */     {
/* 286 */       LOG.fine(this._logId + " getRow()");
/*     */     }
/*     */ 
/* 292 */     int i = this._prs.getRowNumber();
/*     */ 
/* 300 */     if (i == -1)
/*     */     {
/* 302 */       return 0;
/*     */     }
/*     */ 
/* 306 */     return i;
/*     */   }
/*     */ 
/*     */   protected void checkIfReadableRow()
/*     */     throws SQLException
/*     */   {
/* 314 */     if (this._prs.isBeforeFirst())
/*     */     {
/* 316 */       ErrorMessage.raiseError("JZ0R1");
/*     */     } else {
/* 318 */       if (!this._prs.isAfterLast())
/*     */         return;
/* 320 */       ErrorMessage.raiseError("JZ0R5");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean checkRowIndexBeforeProtocolNext()
/*     */   {
/* 328 */     return false;
/*     */   }
/*     */ 
/*     */   protected void adjustRowIndexesAfterProtocolNext()
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybScrollCursorResultSet
 * JD-Core Version:    0.5.4
 */