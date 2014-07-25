/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.utils.CacheManager;
/*      */ import com.sybase.jdbc3.utils.Cacheable;
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import com.sybase.jdbc3.utils.LogUtil;
/*      */ import com.sybase.jdbcx.TextPointer;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Map;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ public class SybResultSet
/*      */   implements com.sybase.jdbcx.SybResultSet, Cacheable
/*      */ {
/*   55 */   private static Logger LOG = Logger.getLogger(SybResultSet.class.getName());
/*   56 */   protected String _logId = null;
/*   57 */   private static volatile long _logIdCounter = 0L;
/*      */   protected static final int BEFORE_FIRST = -1;
/*      */   protected static final int AFTER_LAST = -2;
/*      */   protected static final int INSERT_ROW = -3;
/*      */   protected static final int UNDEFINED = -4;
/*      */   protected ProtocolResultSet _prs;
/*      */   protected JdbcDataObject _currentColumn;
/*      */   protected SybStatement _statement;
/*   84 */   protected ProtocolContext _pc = null;
/*   85 */   protected byte _state = 3;
/*   86 */   private Hashtable _nameToColumn = null;
/*   87 */   private Hashtable _labelToColumn = null;
/*      */   protected CacheManager _cm;
/*      */   protected int _rowIndex;
/*   94 */   protected int _lastRowIndex = -4;
/*      */ 
/*   97 */   protected int _concurType = 1007;
/*      */ 
/*   99 */   protected String _concurTypeString = "CONCUR_READ_ONLY";
/*      */ 
/*  101 */   protected int _scrollType = 1003;
/*      */ 
/*  104 */   protected int _fetchSize = 0;
/*      */ 
/*  107 */   protected int _fetchDirection = 1000;
/*      */ 
/*  116 */   protected boolean _usedForParams = false;
/*      */ 
/*  118 */   protected SQLWarning _savedWarnings = null;
/*      */ 
/*  124 */   protected int _currentStatus = 1;
/*      */ 
/*      */   public SybResultSet(String paramString, SybStatement paramSybStatement, ProtocolResultSet paramProtocolResultSet)
/*      */     throws SQLException
/*      */   {
/*  130 */     this._logId = (paramString + "_Rs" + _logIdCounter++);
/*      */ 
/*  133 */     this._statement = paramSybStatement;
/*  134 */     this._prs = paramProtocolResultSet;
/*  135 */     this._rowIndex = -1;
/*  136 */     if (this._prs != null)
/*      */     {
/*  138 */       this._scrollType = this._prs.getType();
/*      */     }
/*  140 */     if (this._statement == null)
/*      */       return;
/*  142 */     this._pc = this._statement._context;
/*      */   }
/*      */ 
/*      */   public SybResultSet(String paramString, ProtocolResultSet paramProtocolResultSet, ProtocolContext paramProtocolContext)
/*      */     throws SQLException
/*      */   {
/*  149 */     this._logId = (paramString + "_Rs" + _logIdCounter++);
/*      */ 
/*  153 */     this._prs = paramProtocolResultSet;
/*  154 */     this._rowIndex = -1;
/*  155 */     if (this._prs != null)
/*      */     {
/*  157 */       this._scrollType = this._prs.getType();
/*      */     }
/*  159 */     this._pc = paramProtocolContext;
/*      */   }
/*      */ 
/*      */   public boolean next()
/*      */     throws SQLException
/*      */   {
/*  167 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  169 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  171 */       LOG.fine(this._logId + " next()");
/*      */     }
/*      */ 
/*  178 */     checkResultSet();
/*  179 */     clearWarnings();
/*  180 */     moveToCurrentRow();
/*      */ 
/*  182 */     if (checkRowIndexBeforeProtocolNext())
/*      */     {
/*  184 */       return false;
/*      */     }
/*      */ 
/*  189 */     boolean bool = this._prs.next();
/*      */ 
/*  191 */     if (!bool)
/*      */     {
/*  201 */       if ((this._statement != null) && (this._prs != null))
/*      */       {
/*  203 */         this._statement.setRowCount(this._prs.getCount());
/*      */       }
/*      */ 
/*  206 */       adjustRowIndexesAfterProtocolNext();
/*      */     }
/*      */ 
/*  209 */     this._currentColumn = null;
/*  210 */     return bool;
/*      */   }
/*      */ 
/*      */   protected boolean checkRowIndexBeforeProtocolNext()
/*      */   {
/*  218 */     int i = 0;
/*  219 */     switch (this._rowIndex)
/*      */     {
/*      */     case -2:
/*  222 */       i = 1;
/*  223 */       break;
/*      */     case -1:
/*  225 */       this._rowIndex = 1;
/*  226 */       break;
/*      */     default:
/*  228 */       if (this._rowIndex <= 0)
/*      */         break label62;
/*  230 */       this._rowIndex += 1;
/*      */     }
/*      */ 
/*  238 */     label62: return i;
/*      */   }
/*      */ 
/*      */   protected void adjustRowIndexesAfterProtocolNext()
/*      */   {
/*  246 */     if (this._rowIndex == 1)
/*      */     {
/*  248 */       this._lastRowIndex = 0;
/*  249 */       this._rowIndex = -1;
/*      */     }
/*      */     else
/*      */     {
/*  255 */       this._lastRowIndex = (this._rowIndex - 1);
/*  256 */       this._rowIndex = -2;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  265 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  267 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  269 */       LOG.fine(this._logId + " close()");
/*      */     }
/*      */ 
/*  275 */     close(false);
/*      */   }
/*      */ 
/*      */   public boolean wasNull()
/*      */     throws SQLException
/*      */   {
/*  283 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  285 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  287 */       LOG.fine(this._logId + " wasNull()");
/*      */     }
/*      */ 
/*  292 */     checkResultSet();
/*  293 */     if (this._currentColumn == null)
/*      */     {
/*  295 */       ErrorMessage.raiseError("JZ0NC");
/*      */     }
/*  297 */     return this._currentColumn.isNull();
/*      */   }
/*      */ 
/*      */   public String getString(int paramInt) throws SQLException
/*      */   {
/*  302 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  304 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  306 */         LOG.finer(this._logId + " getString(int = [" + paramInt + "])");
/*      */       }
/*  308 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  310 */         LOG.fine(this._logId + " getString(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  315 */     return getColumn(paramInt).getString();
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(int paramInt) throws SQLException {
/*  319 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  321 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  323 */         LOG.finer(this._logId + " getBoolean(int = [" + paramInt + "])");
/*      */       }
/*  325 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  327 */         LOG.fine(this._logId + " getBoolean(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  332 */     return getColumn(paramInt).getBoolean();
/*      */   }
/*      */ 
/*      */   public byte getByte(int paramInt) throws SQLException {
/*  336 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  338 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  340 */         LOG.finer(this._logId + " getByte(int = [" + paramInt + "])");
/*      */       }
/*  342 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  344 */         LOG.fine(this._logId + " getByte(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  349 */     return getColumn(paramInt).getByte();
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(int paramInt) throws SQLException {
/*  353 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  355 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  357 */         LOG.finer(this._logId + " getBytes(int = [" + paramInt + "])");
/*      */       }
/*  359 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  361 */         LOG.fine(this._logId + " getBytes(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  366 */     return getColumn(paramInt).getBytes();
/*      */   }
/*      */ 
/*      */   public TextPointer getSybTextPointer(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  379 */     return getColumn(paramInt).getTextPtr();
/*      */   }
/*      */ 
/*      */   public short getShort(int paramInt) throws SQLException
/*      */   {
/*  384 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  386 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  388 */         LOG.finer(this._logId + " getShort(int = [" + paramInt + "])");
/*      */       }
/*  390 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  392 */         LOG.fine(this._logId + " getShort(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  397 */     return getColumn(paramInt).getShort();
/*      */   }
/*      */ 
/*      */   public int getInt(int paramInt) throws SQLException {
/*  401 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  403 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  405 */         LOG.finer(this._logId + " getInt(int = [" + paramInt + "])");
/*      */       }
/*  407 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  409 */         LOG.fine(this._logId + " getInt(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  414 */     return getColumn(paramInt).getInt();
/*      */   }
/*      */ 
/*      */   public long getLong(int paramInt) throws SQLException {
/*  418 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  420 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  422 */         LOG.finer(this._logId + " getLong(int = [" + paramInt + "])");
/*      */       }
/*  424 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  426 */         LOG.fine(this._logId + " getLong(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  431 */     return getColumn(paramInt).getLong();
/*      */   }
/*      */ 
/*      */   public float getFloat(int paramInt) throws SQLException {
/*  435 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  437 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  439 */         LOG.finer(this._logId + " getFloat(int = [" + paramInt + "])");
/*      */       }
/*  441 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  443 */         LOG.fine(this._logId + " getFloat(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  448 */     return getColumn(paramInt).getFloat();
/*      */   }
/*      */ 
/*      */   public double getDouble(int paramInt) throws SQLException {
/*  452 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  454 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  456 */         LOG.finer(this._logId + " getDouble(int = [" + paramInt + "])");
/*      */       }
/*  458 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  460 */         LOG.fine(this._logId + " getDouble(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  465 */     return getColumn(paramInt).getDouble();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public BigDecimal getBigDecimal(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  477 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  479 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  481 */         LOG.finer(this._logId + " getBigDecimal(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */       }
/*  484 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  486 */         LOG.fine(this._logId + " getBigDecimal(int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  491 */     return getColumn(paramInt1).getBigDecimal(paramInt2);
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  500 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  502 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  504 */         LOG.finer(this._logId + " getBigDecimal(int = [" + paramInt + "])");
/*      */       }
/*  507 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  509 */         LOG.fine(this._logId + " getBigDecimal(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  514 */     return getBigDecimal(paramInt, -1);
/*      */   }
/*      */ 
/*      */   public Date getDate(int paramInt) throws SQLException {
/*  518 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  520 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  522 */         LOG.finer(this._logId + " getDate(int = [" + paramInt + "])");
/*      */       }
/*  524 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  526 */         LOG.fine(this._logId + " getDate(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  531 */     return Convert.objectToDate(getColumn(paramInt).getDateObject(91, null));
/*      */   }
/*      */ 
/*      */   public Time getTime(int paramInt) throws SQLException
/*      */   {
/*  536 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  538 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  540 */         LOG.finer(this._logId + " getTime(int = [" + paramInt + "])");
/*      */       }
/*  542 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  544 */         LOG.fine(this._logId + " getTime(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  549 */     return Convert.objectToTime(getColumn(paramInt).getDateObject(92, null));
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int paramInt) throws SQLException
/*      */   {
/*  554 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  556 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  558 */         LOG.finer(this._logId + " getTimestamp(int = [" + paramInt + "])");
/*      */       }
/*  561 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  563 */         LOG.fine(this._logId + " getTimestamp(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  568 */     return Convert.objectToTimestamp(getColumn(paramInt).getDateObject(93, null));
/*      */   }
/*      */ 
/*      */   public InputStream getAsciiStream(int paramInt) throws SQLException
/*      */   {
/*  573 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  575 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  577 */         LOG.finer(this._logId + " getAsciiStream(int = [" + paramInt + "])");
/*      */       }
/*  580 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  582 */         LOG.fine(this._logId + " getAsciiStream(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  587 */     return getColumn(paramInt).getAsciiStream();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public InputStream getUnicodeStream(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  596 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  598 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  600 */         LOG.finer(this._logId + " getUnicodeStream(int = [" + paramInt + "])");
/*      */       }
/*  603 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  605 */         LOG.fine(this._logId + " getUnicodeStream(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  610 */     return getColumn(paramInt).getUnicodeStream();
/*      */   }
/*      */ 
/*      */   public InputStream getBinaryStream(int paramInt) throws SQLException
/*      */   {
/*  615 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  617 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  619 */         LOG.finer(this._logId + " getBinaryStream(int = [" + paramInt + "])");
/*      */       }
/*  622 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  624 */         LOG.fine(this._logId + " getBinaryStream(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  629 */     return getColumn(paramInt).getBinaryStream();
/*      */   }
/*      */ 
/*      */   public Object getObject(int paramInt) throws SQLException {
/*  633 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  635 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  637 */         LOG.finer(this._logId + " getObject(int = [" + paramInt + "])");
/*      */       }
/*  639 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  641 */         LOG.fine(this._logId + " getObject(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  646 */     return getColumn(paramInt).getObject();
/*      */   }
/*      */ 
/*      */   public String getString(String paramString) throws SQLException
/*      */   {
/*  651 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  653 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  655 */         LOG.finer(this._logId + " getString(String = [" + paramString + "])");
/*      */       }
/*  657 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  659 */         LOG.fine(this._logId + " getString(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  664 */     return getString(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(String paramString) throws SQLException {
/*  668 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  670 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  672 */         LOG.finer(this._logId + " getBoolean(String = [" + paramString + "])");
/*      */       }
/*  675 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  677 */         LOG.fine(this._logId + " getBoolean(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  682 */     return getBoolean(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public byte getByte(String paramString) throws SQLException {
/*  686 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  688 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  690 */         LOG.finer(this._logId + " getByte(String = [" + paramString + "])");
/*      */       }
/*  692 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  694 */         LOG.fine(this._logId + " getByte(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  699 */     return getByte(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public short getShort(String paramString) throws SQLException {
/*  703 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  705 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  707 */         LOG.finer(this._logId + " getShort(String = [" + paramString + "])");
/*      */       }
/*  709 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  711 */         LOG.fine(this._logId + " getShort(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  716 */     return getShort(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public int getInt(String paramString) throws SQLException {
/*  720 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  722 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  724 */         LOG.finer(this._logId + " getInt(String = [" + paramString + "])");
/*      */       }
/*  726 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  728 */         LOG.fine(this._logId + " getInt(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  733 */     return getInt(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public long getLong(String paramString) throws SQLException {
/*  737 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  739 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  741 */         LOG.finer(this._logId + " getLong(String = [" + paramString + "])");
/*      */       }
/*  743 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  745 */         LOG.fine(this._logId + " getLong(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  750 */     return getLong(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public float getFloat(String paramString) throws SQLException {
/*  754 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  756 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  758 */         LOG.finer(this._logId + " getFloat(String = [" + paramString + "])");
/*      */       }
/*  760 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  762 */         LOG.fine(this._logId + " getFloat(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  767 */     return getFloat(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public double getDouble(String paramString) throws SQLException {
/*  771 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  773 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  775 */         LOG.finer(this._logId + " getDouble(String = [" + paramString + "])");
/*      */       }
/*  777 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  779 */         LOG.fine(this._logId + " getDouble(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  784 */     return getDouble(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public BigDecimal getBigDecimal(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  795 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  797 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  799 */         LOG.finer(this._logId + " getBigDecimal(String = [" + paramString + "], int = [" + paramInt + "])");
/*      */       }
/*  802 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  804 */         LOG.fine(this._logId + " getBigDecimal(String, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  809 */     return getBigDecimal(findColumn(paramString), paramInt);
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(String paramString)
/*      */     throws SQLException
/*      */   {
/*  818 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  820 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  822 */         LOG.finer(this._logId + " getBigDecimal(String = [" + paramString + "])");
/*      */       }
/*  825 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  827 */         LOG.fine(this._logId + " getBigDecimal(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  832 */     return getBigDecimal(paramString, -1);
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(String paramString) throws SQLException {
/*  836 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  838 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  840 */         LOG.finer(this._logId + " getBytes(String = [" + paramString + "])");
/*      */       }
/*  842 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  844 */         LOG.fine(this._logId + " getBytes(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  849 */     return getBytes(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public TextPointer getSybTextPointer(String paramString)
/*      */     throws SQLException
/*      */   {
/*  860 */     return getSybTextPointer(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public Date getDate(String paramString) throws SQLException
/*      */   {
/*  865 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  867 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  869 */         LOG.finer(this._logId + " getDate(String = [" + paramString + "])");
/*      */       }
/*  871 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  873 */         LOG.fine(this._logId + " getDate(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  878 */     return getDate(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public Time getTime(String paramString) throws SQLException {
/*  882 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  884 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  886 */         LOG.finer(this._logId + " getTime(String = [" + paramString + "])");
/*      */       }
/*  888 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  890 */         LOG.fine(this._logId + " getTime(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  895 */     return getTime(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String paramString) throws SQLException {
/*  899 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  901 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  903 */         LOG.finer(this._logId + " getTimestamp(String = [" + paramString + "])");
/*      */       }
/*  906 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  908 */         LOG.fine(this._logId + " getTimestamp(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  913 */     return getTimestamp(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public InputStream getAsciiStream(String paramString) throws SQLException {
/*  917 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  919 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  921 */         LOG.finer(this._logId + " getAsciiStream(String = [" + paramString + "])");
/*      */       }
/*  924 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  926 */         LOG.fine(this._logId + " getAsciiStream(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  931 */     return getAsciiStream(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public InputStream getUnicodeStream(String paramString)
/*      */     throws SQLException
/*      */   {
/*  940 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  942 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  944 */         LOG.finer(this._logId + " getUnicodeStream(String = [" + paramString + "])");
/*      */       }
/*  947 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  949 */         LOG.fine(this._logId + " getUnicodeStream(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  954 */     return getUnicodeStream(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public InputStream getBinaryStream(String paramString) throws SQLException
/*      */   {
/*  959 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  961 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  963 */         LOG.finer(this._logId + " getBinaryStream(String = [" + paramString + "])");
/*      */       }
/*  966 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  968 */         LOG.fine(this._logId + " getBinaryStream(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  973 */     return getBinaryStream(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public Object getObject(String paramString) throws SQLException {
/*  977 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  979 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  981 */         LOG.finer(this._logId + " getObject(String = [" + paramString + "])");
/*      */       }
/*  983 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  985 */         LOG.fine(this._logId + " getObject(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  990 */     return getObject(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public URL getURL(int paramInt) throws SQLException
/*      */   {
/*  995 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  997 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  999 */         LOG.finer(this._logId + " getURL(int = [" + paramInt + "])");
/*      */       }
/* 1001 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1003 */         LOG.fine(this._logId + " getURL(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1008 */     Debug.notImplemented(this, "getURL(int)");
/* 1009 */     return null;
/*      */   }
/*      */ 
/*      */   public URL getURL(String paramString) throws SQLException
/*      */   {
/* 1014 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1016 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1018 */         LOG.finer(this._logId + " getURL(String = [" + paramString + "])");
/*      */       }
/* 1020 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1022 */         LOG.fine(this._logId + " getURL(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1027 */     Debug.notImplemented(this, "getURL(String)");
/* 1028 */     return null;
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 1038 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1040 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1042 */       LOG.fine(this._logId + " getWarnings()");
/*      */     }
/*      */ 
/* 1047 */     SQLWarning localSQLWarning = null;
/* 1048 */     if (this._prs != null)
/*      */     {
/* 1050 */       if (this._savedWarnings != null)
/*      */       {
/* 1052 */         this._savedWarnings.setNextWarning(this._prs.getWarnings());
/* 1053 */         this._prs.clearWarnings();
/* 1054 */         localSQLWarning = this._savedWarnings;
/*      */       }
/*      */       else
/*      */       {
/* 1058 */         localSQLWarning = this._prs.getWarnings();
/*      */       }
/*      */     }
/* 1061 */     else if (this._savedWarnings != null)
/*      */     {
/* 1063 */       localSQLWarning = this._savedWarnings;
/*      */     }
/* 1065 */     return localSQLWarning;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/* 1073 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1075 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1077 */       LOG.fine(this._logId + " clearWarnings()");
/*      */     }
/*      */ 
/* 1081 */     if (this._prs != null)
/*      */     {
/* 1083 */       this._prs.clearWarnings();
/*      */     }
/* 1085 */     this._savedWarnings = null;
/*      */   }
/*      */ 
/*      */   public String getCursorName()
/*      */     throws SQLException
/*      */   {
/* 1095 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1097 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1099 */       LOG.fine(this._logId + " getCursorName()");
/*      */     }
/*      */ 
/* 1105 */     return null;
/*      */   }
/*      */ 
/*      */   public ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 1113 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1115 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1117 */       LOG.fine(this._logId + " getMetaData()");
/*      */     }
/*      */ 
/* 1122 */     checkResultSet();
/* 1123 */     return this._prs.getMetaData();
/*      */   }
/*      */ 
/*      */   public int findColumn(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1132 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1134 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1136 */         LOG.finer(this._logId + " findColumn(String = [" + paramString + "])");
/*      */       }
/* 1140 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1142 */         LOG.fine(this._logId + " findColumn(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1159 */     if (this._pc._conn._props.getBoolean(40))
/*      */     {
/* 1165 */       if (this._labelToColumn == null)
/*      */       {
/* 1169 */         this._labelToColumn = new Hashtable();
/*      */       }
/*      */ 
/* 1172 */       localInteger = (Integer)this._labelToColumn.get(paramString);
/* 1173 */       if (localInteger != null)
/*      */       {
/* 1177 */         return localInteger.intValue();
/*      */       }
/*      */ 
/* 1181 */       i = this._prs.findColumnByLabel(paramString);
/* 1182 */       this._labelToColumn.put(paramString, new Integer(i));
/*      */ 
/* 1185 */       return i;
/*      */     }
/*      */ 
/* 1193 */     if (this._nameToColumn == null)
/*      */     {
/* 1197 */       this._nameToColumn = new Hashtable();
/*      */     }
/*      */ 
/* 1200 */     Integer localInteger = (Integer)this._nameToColumn.get(paramString);
/* 1201 */     if (localInteger != null)
/*      */     {
/* 1205 */       return localInteger.intValue();
/*      */     }
/*      */ 
/* 1209 */     int i = this._prs.findColumn(paramString);
/* 1210 */     this._nameToColumn.put(paramString, new Integer(i));
/*      */ 
/* 1213 */     return i;
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1219 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1221 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1223 */         LOG.finer(this._logId + " getCharacterStream(int = [" + paramInt + "])");
/*      */       }
/* 1226 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1228 */         LOG.fine(this._logId + " getCharacterStream(int)");
/*      */       }
/*      */     }
/*      */ 
/* 1232 */     return getColumn(paramInt).getCharacterStream();
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1238 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1240 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1242 */         LOG.finer(this._logId + " getCharacterStream(String = [" + paramString + "])");
/*      */       }
/* 1245 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1247 */         LOG.fine(this._logId + " getCharacterStream(String)");
/*      */       }
/*      */     }
/*      */ 
/* 1251 */     return getCharacterStream(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public int getRow() throws SQLException
/*      */   {
/* 1256 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1258 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1260 */       LOG.fine(this._logId + " getRow()");
/*      */     }
/*      */ 
/* 1267 */     if (this._rowIndex <= 0)
/*      */     {
/* 1270 */       return 0;
/*      */     }
/*      */ 
/* 1274 */     return this._rowIndex;
/*      */   }
/*      */ 
/*      */   public boolean isBeforeFirst()
/*      */     throws SQLException
/*      */   {
/* 1285 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1287 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1289 */       LOG.fine(this._logId + " isBeforeFirst()");
/*      */     }
/*      */ 
/* 1293 */     return this._rowIndex == -1;
/*      */   }
/*      */ 
/*      */   public boolean isAfterLast()
/*      */     throws SQLException
/*      */   {
/* 1303 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1305 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1307 */       LOG.fine(this._logId + " isAfterLast()");
/*      */     }
/*      */ 
/* 1311 */     return this._rowIndex == -2;
/*      */   }
/*      */ 
/*      */   public boolean isFirst() throws SQLException
/*      */   {
/* 1316 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1318 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1320 */       LOG.fine(this._logId + " isFirst()");
/*      */     }
/*      */ 
/* 1324 */     return this._rowIndex == 1;
/*      */   }
/*      */ 
/*      */   public boolean isLast() throws SQLException
/*      */   {
/* 1329 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1331 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1333 */       LOG.fine(this._logId + " isLast()");
/*      */     }
/*      */ 
/* 1338 */     if (this._lastRowIndex != -4)
/*      */     {
/* 1341 */       return this._rowIndex == this._lastRowIndex;
/*      */     }
/*      */ 
/* 1347 */     Debug.notImplemented(this, "isLast()");
/*      */ 
/* 1350 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean previous() throws SQLException
/*      */   {
/* 1355 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1357 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1359 */       LOG.fine(this._logId + " previous()");
/*      */     }
/*      */ 
/* 1366 */     checkForScrollability("previous()");
/* 1367 */     clearWarnings();
/*      */ 
/* 1369 */     int i = 1;
/*      */ 
/* 1371 */     switch (this._rowIndex)
/*      */     {
/*      */     case -1:
/* 1374 */       return false;
/*      */     case 1:
/* 1376 */       this._rowIndex = -1;
/* 1377 */       i = 0;
/* 1378 */       break;
/*      */     case -2:
/* 1380 */       this._rowIndex = this._lastRowIndex;
/* 1381 */       break;
/*      */     case 0:
/*      */     default:
/* 1383 */       this._rowIndex -= 1;
/*      */     }
/*      */ 
/* 1387 */     this._prs.previous();
/* 1388 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean absolute(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1397 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1399 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1401 */         LOG.finer(this._logId + " absolute(int = [" + paramInt + "])");
/*      */       }
/* 1403 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1405 */         LOG.fine(this._logId + " absolute(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1411 */     checkForScrollability("absolute(int)");
/* 1412 */     clearWarnings();
/*      */ 
/* 1414 */     if (this._lastRowIndex == 0)
/*      */     {
/* 1416 */       return false;
/*      */     }
/*      */ 
/* 1419 */     if (paramInt == 0)
/*      */     {
/* 1421 */       ErrorMessage.raiseError("JZ0I3", String.valueOf(paramInt), "absolute(int)");
/*      */     }
/*      */ 
/* 1427 */     if (paramInt < 0)
/*      */     {
/* 1429 */       if ((this._lastRowIndex == -4) && 
/* 1433 */         (!last()))
/*      */       {
/* 1438 */         return false;
/*      */       }
/*      */ 
/* 1445 */       paramInt += this._lastRowIndex + 1;
/*      */ 
/* 1449 */       if (paramInt <= 0)
/*      */       {
/* 1451 */         beforeFirst();
/* 1452 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1457 */     int i = this._prs.getNumRowsCached();
/* 1458 */     if (paramInt <= i)
/*      */     {
/* 1461 */       this._rowIndex = paramInt;
/* 1462 */       this._prs.absolute(paramInt);
/* 1463 */       return true;
/*      */     }
/*      */ 
/* 1469 */     if (i > 0)
/*      */     {
/* 1471 */       this._rowIndex = i;
/* 1472 */       this._prs.absolute(this._rowIndex);
/*      */     }
/*      */ 
/* 1476 */     while ((this._rowIndex < paramInt) && (next()));
/* 1481 */     return this._rowIndex == paramInt;
/*      */   }
/*      */ 
/*      */   public void afterLast()
/*      */     throws SQLException
/*      */   {
/* 1488 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1490 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1492 */       LOG.fine(this._logId + " afterLast()");
/*      */     }
/*      */ 
/* 1498 */     checkForScrollability("afterLast()");
/* 1499 */     clearWarnings();
/*      */ 
/* 1501 */     if (!last())
/*      */       return;
/* 1503 */     next();
/*      */   }
/*      */ 
/*      */   public void beforeFirst()
/*      */     throws SQLException
/*      */   {
/* 1510 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1512 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1514 */       LOG.fine(this._logId + " beforeFirst()");
/*      */     }
/*      */ 
/* 1520 */     checkForScrollability("beforeFirst()");
/* 1521 */     clearWarnings();
/*      */ 
/* 1523 */     if (!first())
/*      */       return;
/* 1525 */     previous();
/*      */   }
/*      */ 
/*      */   public boolean first()
/*      */     throws SQLException
/*      */   {
/* 1531 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1533 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1535 */       LOG.fine(this._logId + " first()");
/*      */     }
/*      */ 
/* 1542 */     checkForScrollability("first()");
/* 1543 */     clearWarnings();
/* 1544 */     moveToCurrentRow();
/*      */ 
/* 1546 */     if (this._rowIndex == 1)
/*      */     {
/* 1548 */       return true;
/*      */     }
/*      */ 
/* 1552 */     return absolute(1);
/*      */   }
/*      */ 
/*      */   public boolean last()
/*      */     throws SQLException
/*      */   {
/* 1559 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1561 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1563 */       LOG.fine(this._logId + " last()");
/*      */     }
/*      */ 
/* 1570 */     checkForScrollability("last()");
/* 1571 */     clearWarnings();
/* 1572 */     moveToCurrentRow();
/*      */ 
/* 1574 */     if (this._lastRowIndex == -4)
/*      */     {
/* 1577 */       if (this._prs.getNumRowsCached() > 0)
/*      */       {
/* 1579 */         this._rowIndex = this._prs.getNumRowsCached();
/* 1580 */         this._prs.absolute(this._rowIndex);
/*      */       }
/*      */ 
/* 1584 */       while (next());
/* 1587 */       previous();
/*      */     }
/* 1589 */     else if (this._lastRowIndex != 0)
/*      */     {
/* 1592 */       this._rowIndex = this._lastRowIndex;
/* 1593 */       this._prs.absolute(this._rowIndex);
/*      */     }
/* 1595 */     return this._lastRowIndex != 0;
/*      */   }
/*      */ 
/*      */   public boolean relative(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1601 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1603 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1605 */         LOG.finer(this._logId + " relative(int = [" + paramInt + "])");
/*      */       }
/* 1607 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1609 */         LOG.fine(this._logId + " relative(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1617 */     checkForScrollability("relative(int)");
/* 1618 */     clearWarnings();
/* 1619 */     moveToCurrentRow();
/*      */ 
/* 1621 */     boolean bool = false;
/*      */ 
/* 1625 */     if ((this._rowIndex != -1) && (this._rowIndex != -2))
/*      */     {
/* 1627 */       int i = this._rowIndex + paramInt;
/*      */ 
/* 1631 */       if (i <= 0)
/*      */       {
/* 1633 */         beforeFirst();
/*      */       }
/*      */       else
/*      */       {
/* 1639 */         bool = absolute(i);
/*      */       }
/*      */     }
/* 1642 */     return bool;
/*      */   }
/*      */ 
/*      */   public void refreshRow() throws SQLException
/*      */   {
/* 1647 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1649 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1651 */       LOG.fine(this._logId + " refreshRow()");
/*      */     }
/*      */ 
/* 1655 */     switch (this._scrollType)
/*      */     {
/*      */     case 1003:
/*      */     case 1004:
/* 1661 */       break;
/*      */     case 1005:
/* 1663 */       Debug.notImplemented(this, "refreshRow() FOR TYPE_SCROLL_SENSITIVE");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void moveToCurrentRow()
/*      */     throws SQLException
/*      */   {
/* 1674 */     if ((!LogUtil.isLoggingEnabled(LOG)) || 
/* 1676 */       (!LOG.isLoggable(Level.FINE)))
/*      */       return;
/* 1678 */     LOG.fine(this._logId + " moveToCurrentRow()");
/*      */   }
/*      */ 
/*      */   public void moveToInsertRow()
/*      */     throws SQLException
/*      */   {
/* 1690 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1692 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1694 */       LOG.fine(this._logId + " moveToInsertRow()");
/*      */     }
/*      */ 
/* 1698 */     ErrorMessage.raiseError("JZ0BT", "moveToInsertRow()", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void setFetchDirection(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1705 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1707 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1709 */         LOG.finer(this._logId + " setFetchDirection(int = [" + paramInt + "])");
/*      */       }
/* 1712 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1714 */         LOG.fine(this._logId + " setFetchDirection(int)");
/*      */       }
/*      */     }
/*      */ 
/* 1718 */     switch (this._scrollType)
/*      */     {
/*      */     case 1003:
/* 1721 */       ErrorMessage.raiseError("JZ0BT", "setFetchDirection(int)", "TYPE_FORWARD_ONLY");
/*      */ 
/* 1724 */       break;
/*      */     case 1004:
/*      */     case 1005:
/* 1728 */       switch (paramInt)
/*      */       {
/*      */       case 1000:
/*      */       case 1001:
/*      */       case 1002:
/* 1733 */         this._fetchDirection = paramInt;
/* 1734 */         break;
/*      */       default:
/* 1736 */         ErrorMessage.raiseError("JZ0I3", String.valueOf(paramInt), "setFetchDirection(int)");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getFetchDirection()
/*      */     throws SQLException
/*      */   {
/* 1745 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1747 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1749 */       LOG.fine(this._logId + " getFetchDirection()");
/*      */     }
/*      */ 
/* 1753 */     return this._fetchDirection;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setFetchSize(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1769 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1771 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1773 */         LOG.finer(this._logId + " setFetchSize(int = [" + paramInt + "])");
/*      */       }
/* 1775 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1777 */         LOG.fine(this._logId + " setFetchSize(int)");
/*      */       }
/*      */     }
/*      */ 
/* 1781 */     if (paramInt == 0)
/*      */     {
/* 1783 */       return;
/*      */     }
/*      */ 
/* 1786 */     int i = this._pc._protocol.getIntOption(this._pc, 4);
/*      */ 
/* 1789 */     if ((paramInt < 0) || ((i > 0) && (paramInt > i)))
/*      */     {
/* 1792 */       ErrorMessage.raiseError("JZ0BI");
/*      */     }
/*      */ 
/* 1795 */     this._fetchSize = paramInt;
/*      */   }
/*      */ 
/*      */   public int getFetchSize() throws SQLException
/*      */   {
/* 1800 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1802 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1804 */       LOG.fine(this._logId + " getFetchSize()");
/*      */     }
/*      */ 
/* 1808 */     return this._fetchSize;
/*      */   }
/*      */ 
/*      */   public int getType() throws SQLException
/*      */   {
/* 1813 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1815 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1817 */       LOG.fine(this._logId + " getType()");
/*      */     }
/*      */ 
/* 1821 */     return this._scrollType;
/*      */   }
/*      */ 
/*      */   public int getConcurrency() throws SQLException
/*      */   {
/* 1826 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1828 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1830 */       LOG.fine(this._logId + " getConcurrency()");
/*      */     }
/*      */ 
/* 1834 */     return this._concurType;
/*      */   }
/*      */ 
/*      */   public boolean rowUpdated()
/*      */     throws SQLException
/*      */   {
/* 1843 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1845 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1847 */       LOG.fine(this._logId + " rowUpdated()");
/*      */     }
/*      */ 
/* 1852 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean rowInserted()
/*      */     throws SQLException
/*      */   {
/* 1861 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1863 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1865 */       LOG.fine(this._logId + " rowInserted()");
/*      */     }
/*      */ 
/* 1870 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean rowDeleted()
/*      */     throws SQLException
/*      */   {
/* 1879 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1881 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1883 */       LOG.fine(this._logId + " rowDeleted()");
/*      */     }
/*      */ 
/* 1888 */     return false;
/*      */   }
/*      */ 
/*      */   public void updateNull(int paramInt) throws SQLException
/*      */   {
/* 1893 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1895 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1897 */         LOG.finer(this._logId + " updateNull(int = [" + paramInt + "])");
/*      */       }
/* 1899 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1901 */         LOG.fine(this._logId + " updateNull(int)");
/*      */       }
/*      */     }
/*      */ 
/* 1905 */     ErrorMessage.raiseError("JZ0BT", "updateNull(int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBoolean(int paramInt, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 1912 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1914 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1916 */         LOG.finer(this._logId + " updatedBoolean(int = [" + paramInt + "], boolean = [" + paramBoolean + "])");
/*      */       }
/* 1919 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1921 */         LOG.fine(this._logId + " updateBoolean(int, boolean)");
/*      */       }
/*      */     }
/*      */ 
/* 1925 */     ErrorMessage.raiseError("JZ0BT", "updateBoolean(int, boolean)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateByte(int paramInt, byte paramByte)
/*      */     throws SQLException
/*      */   {
/* 1932 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1934 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1936 */         LOG.finer(this._logId + " updateByte(int = [" + paramInt + "], byte = [" + paramByte + "])");
/*      */       }
/* 1939 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1941 */         LOG.fine(this._logId + " updateByte(int, byte)");
/*      */       }
/*      */     }
/*      */ 
/* 1945 */     ErrorMessage.raiseError("JZ0BT", "updateByte(int, byte)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateShort(int paramInt, short paramShort)
/*      */     throws SQLException
/*      */   {
/* 1952 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1954 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1956 */         LOG.finer(this._logId + " updateShort(int = [" + paramInt + "], short = [" + paramShort + "])");
/*      */       }
/* 1959 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1961 */         LOG.fine(this._logId + " updateShort(int, short)");
/*      */       }
/*      */     }
/*      */ 
/* 1965 */     ErrorMessage.raiseError("JZ0BT", "updateShort(int, short)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateInt(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1972 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1974 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1976 */         LOG.finer(this._logId + " updateInt(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */       }
/* 1979 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1981 */         LOG.fine(this._logId + " updateInt(int, int)");
/*      */       }
/*      */     }
/*      */ 
/* 1985 */     ErrorMessage.raiseError("JZ0BT", "updateInt(int, int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateLong(int paramInt, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 1992 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1994 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1996 */         LOG.finer(this._logId + " updateLong(int = [" + paramInt + "], long = [" + paramLong + "])");
/*      */       }
/* 1999 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2001 */         LOG.fine(this._logId + " updateLong(int, long)");
/*      */       }
/*      */     }
/*      */ 
/* 2005 */     ErrorMessage.raiseError("JZ0BT", "updateLong(int, long)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateFloat(int paramInt, float paramFloat)
/*      */     throws SQLException
/*      */   {
/* 2012 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2014 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2016 */         LOG.finer(this._logId + " updateFloat(int = [" + paramInt + "], float = [" + paramFloat + "])");
/*      */       }
/* 2019 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2021 */         LOG.fine(this._logId + " updateFloat(int, float)");
/*      */       }
/*      */     }
/*      */ 
/* 2025 */     ErrorMessage.raiseError("JZ0BT", "updateFloat(int, float)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateDouble(int paramInt, double paramDouble)
/*      */     throws SQLException
/*      */   {
/* 2032 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2034 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2036 */         LOG.finer(this._logId + " updateDouble(int = [" + paramInt + "], double = [" + paramDouble + "])");
/*      */       }
/* 2039 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2041 */         LOG.fine(this._logId + " updateDouble(int, double)");
/*      */       }
/*      */     }
/*      */ 
/* 2045 */     ErrorMessage.raiseError("JZ0BT", "updateDouble(int, double)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
/*      */     throws SQLException
/*      */   {
/* 2052 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2054 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2056 */         LOG.finer(this._logId + " updateBigDecimal(int = [" + paramInt + "], BigDecimal = [" + paramBigDecimal + "])");
/*      */       }
/* 2059 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2061 */         LOG.fine(this._logId + " updateBigDecimal(int, BigDecimal)");
/*      */       }
/*      */     }
/*      */ 
/* 2065 */     ErrorMessage.raiseError("JZ0BT", "updateBigDecimal(int, BigDecimal)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateString(int paramInt, String paramString)
/*      */     throws SQLException
/*      */   {
/* 2072 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2074 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2076 */         LOG.finer(this._logId + " updateString(int = [" + paramInt + "], String = [" + paramString + "])");
/*      */       }
/* 2079 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2081 */         LOG.fine(this._logId + " updateString(int, String)");
/*      */       }
/*      */     }
/*      */ 
/* 2085 */     ErrorMessage.raiseError("JZ0BT", "updateString(int, String)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBytes(int paramInt, byte[] paramArrayOfByte)
/*      */     throws SQLException
/*      */   {
/* 2092 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2094 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2096 */         LOG.finest(LogUtil.logMethod(false, this._logId, "updateBytes", new Object[] { new Integer(paramInt), paramArrayOfByte }));
/*      */       }
/* 2099 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2101 */         LOG.finer(LogUtil.logMethod(true, this._logId, "updateBytes", new Object[] { new Integer(paramInt), paramArrayOfByte }));
/*      */       }
/* 2104 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2106 */         LOG.fine(this._logId + " updateBytes(int, byte)");
/*      */       }
/*      */     }
/*      */ 
/* 2110 */     ErrorMessage.raiseError("JZ0BT", "updateBytes(int, byte[])", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateDate(int paramInt, Date paramDate)
/*      */     throws SQLException
/*      */   {
/* 2117 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2119 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2121 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateDate", new Object[] { new Integer(paramInt), paramDate }));
/*      */       }
/* 2124 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2126 */         LOG.fine(this._logId + " updateDate(int, java.sql.Date)");
/*      */       }
/*      */     }
/*      */ 
/* 2130 */     ErrorMessage.raiseError("JZ0BT", "updateDate(int, java.sql.Date)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateTime(int paramInt, Time paramTime)
/*      */     throws SQLException
/*      */   {
/* 2137 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2139 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2141 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateTime", new Object[] { new Integer(paramInt), paramTime }));
/*      */       }
/* 2144 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2146 */         LOG.fine(this._logId + " updateTime(int, java.sql.Time)");
/*      */       }
/*      */     }
/*      */ 
/* 2150 */     ErrorMessage.raiseError("JZ0BT", "updateTime(int, java.sql.Time)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
/*      */     throws SQLException
/*      */   {
/* 2158 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2160 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2162 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateTimestamp", new Object[] { new Integer(paramInt), paramTimestamp }));
/*      */       }
/* 2165 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2167 */         LOG.fine(this._logId + " updateTimestamp(int, java.sql.Timestamp)");
/*      */       }
/*      */     }
/*      */ 
/* 2171 */     ErrorMessage.raiseError("JZ0BT", "updateTimestamp(int, java.sql.Timestamp)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 2180 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2182 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2184 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/* 2188 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2190 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/* 2193 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2195 */         LOG.fine(this._logId + " updateAsciiStream(int, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2200 */     ErrorMessage.raiseError("JZ0BT", "updateAsciiStream(int, java.io.InputStream, int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 2210 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2212 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2214 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/* 2218 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2220 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/* 2224 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2226 */         LOG.fine(this._logId + " updateBinaryStream(int, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2231 */     ErrorMessage.raiseError("JZ0BT", "updateBinaryStream(int, java.io.InputStream, int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 2241 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2243 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2245 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { new Integer(paramInt1), paramReader, new Integer(paramInt2) }));
/*      */       }
/* 2249 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2251 */         LOG.fine(this._logId + " updateChracterStream(int, java.io.Redear, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2256 */     ErrorMessage.raiseError("JZ0BT", "updateCharacterStream(int, java.io.Reader, int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateObject(int paramInt1, Object paramObject, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 2265 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2267 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2269 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateObject", new Object[] { new Integer(paramInt1), paramObject, new Integer(paramInt2) }));
/*      */       }
/* 2272 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2274 */         LOG.fine(this._logId + " updateObject(int, Object, int)");
/*      */       }
/*      */     }
/*      */ 
/* 2278 */     ErrorMessage.raiseError("JZ0BT", "updateObject(int, Object, int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateObject(int paramInt, Object paramObject)
/*      */     throws SQLException
/*      */   {
/* 2285 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2287 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2289 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateObject", new Object[] { new Integer(paramInt), paramObject }));
/*      */       }
/* 2292 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2294 */         LOG.fine(this._logId + " updateObject(int, Object)");
/*      */       }
/*      */     }
/*      */ 
/* 2298 */     ErrorMessage.raiseError("JZ0BT", "updateObject(int, Object)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateNull(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2305 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2307 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2309 */         LOG.finer(this._logId + " updateNull(String = [" + paramString + "])");
/*      */       }
/* 2312 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2314 */         LOG.fine(this._logId + " updateNull(String)");
/*      */       }
/*      */     }
/*      */ 
/* 2318 */     ErrorMessage.raiseError("JZ0BT", "updateNull(String)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBoolean(String paramString, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 2325 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2327 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2329 */         LOG.finer(this._logId + " updateBoolean(String = [" + paramString + "], boolean = [" + paramBoolean + "])");
/*      */       }
/* 2332 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2334 */         LOG.fine(this._logId + " updateBoolean(String, boolean)");
/*      */       }
/*      */     }
/*      */ 
/* 2338 */     ErrorMessage.raiseError("JZ0BT", "updateBoolean(String, boolean)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateByte(String paramString, byte paramByte)
/*      */     throws SQLException
/*      */   {
/* 2345 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2347 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2349 */         LOG.finer(this._logId + " updateByte(String = [" + paramString + "], byte = [" + paramByte + "])");
/*      */       }
/* 2352 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2354 */         LOG.fine(this._logId + " updateByte(String, byte)");
/*      */       }
/*      */     }
/*      */ 
/* 2358 */     ErrorMessage.raiseError("JZ0BT", "updateByte(String, byte)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateShort(String paramString, short paramShort)
/*      */     throws SQLException
/*      */   {
/* 2365 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2367 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2369 */         LOG.finer(this._logId + " updateShort(String = [" + paramString + "], short = [" + paramShort + "])");
/*      */       }
/* 2372 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2374 */         LOG.fine(this._logId + " updateShort(String, short)");
/*      */       }
/*      */     }
/*      */ 
/* 2378 */     ErrorMessage.raiseError("JZ0BT", "updateShort(String, short)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateInt(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2385 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2387 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2389 */         LOG.finer(this._logId + " updateInt(String = [" + paramString + "], int = [" + paramInt + "])");
/*      */       }
/* 2392 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2394 */         LOG.fine(this._logId + " updateInt(String, int)");
/*      */       }
/*      */     }
/*      */ 
/* 2398 */     ErrorMessage.raiseError("JZ0BT", "updateInt(String, int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateLong(String paramString, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2405 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2407 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2409 */         LOG.finer(this._logId + " updateLong(String = [" + paramString + "], long = [" + paramLong + "])");
/*      */       }
/* 2412 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2414 */         LOG.fine(this._logId + " updateLong(String, long)");
/*      */       }
/*      */     }
/*      */ 
/* 2418 */     ErrorMessage.raiseError("JZ0BT", "updateLong(String, long)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateFloat(String paramString, float paramFloat)
/*      */     throws SQLException
/*      */   {
/* 2425 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2427 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2429 */         LOG.finer(this._logId + " updateFloat(String = [" + paramString + "], float = [" + paramFloat + "])");
/*      */       }
/* 2432 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2434 */         LOG.fine(this._logId + " updateFloat(String, float)");
/*      */       }
/*      */     }
/*      */ 
/* 2438 */     ErrorMessage.raiseError("JZ0BT", "updateFloat(String, float)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateDouble(String paramString, double paramDouble)
/*      */     throws SQLException
/*      */   {
/* 2445 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2447 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2449 */         LOG.finer(this._logId + " updateDouble(String = [" + paramString + "], double = [" + paramDouble + "])");
/*      */       }
/* 2452 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2454 */         LOG.fine(this._logId + " updateDouble(String, double)");
/*      */       }
/*      */     }
/*      */ 
/* 2458 */     ErrorMessage.raiseError("JZ0BT", "updateDouble(String, double)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal)
/*      */     throws SQLException
/*      */   {
/* 2465 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2467 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2469 */         LOG.finer(this._logId + " updateBigDecimal(String = [" + paramString + "], BigDecimal = [" + paramBigDecimal + "])");
/*      */       }
/* 2472 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2474 */         LOG.fine(this._logId + " updateBigDecimal(String, BigDecimal)");
/*      */       }
/*      */     }
/*      */ 
/* 2478 */     ErrorMessage.raiseError("JZ0BT", "updateBigDecimal(String, BigDecimal)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateString(String paramString1, String paramString2)
/*      */     throws SQLException
/*      */   {
/* 2485 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2487 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2489 */         LOG.finer(this._logId + " updateString(String = [" + paramString1 + "], String = [" + paramString2 + "])");
/*      */       }
/* 2492 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2494 */         LOG.fine(this._logId + " updateString(String, String)");
/*      */       }
/*      */     }
/*      */ 
/* 2498 */     ErrorMessage.raiseError("JZ0BT", "updateString(String, String)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBytes(String paramString, byte[] paramArrayOfByte)
/*      */     throws SQLException
/*      */   {
/* 2505 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2507 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2509 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBytes", new Object[] { paramString, paramArrayOfByte }));
/*      */       }
/* 2512 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2514 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBytes", new Object[] { paramString, paramArrayOfByte }));
/*      */       }
/* 2517 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2519 */         LOG.fine(this._logId + " updateBytes(String, byte)");
/*      */       }
/*      */     }
/*      */ 
/* 2523 */     ErrorMessage.raiseError("JZ0BT", "updateBytes(String, byte[])", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateDate(String paramString, Date paramDate)
/*      */     throws SQLException
/*      */   {
/* 2530 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2532 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2534 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateDate", new Object[] { paramString, paramDate }));
/*      */       }
/* 2537 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2539 */         LOG.fine(this._logId + " updateDate(String, java.sql.Date)");
/*      */       }
/*      */     }
/*      */ 
/* 2543 */     ErrorMessage.raiseError("JZ0BT", "updateDate(String, java.sql.Date)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateTime(String paramString, Time paramTime)
/*      */     throws SQLException
/*      */   {
/* 2550 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2552 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2554 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateTime", new Object[] { paramString, paramTime }));
/*      */       }
/* 2557 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2559 */         LOG.fine(this._logId + " updateTime(String, java.sql.Time)");
/*      */       }
/*      */     }
/*      */ 
/* 2563 */     ErrorMessage.raiseError("JZ0BT", "updateTime(String, java.sql.Time)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateTimestamp(String paramString, Timestamp paramTimestamp)
/*      */     throws SQLException
/*      */   {
/* 2571 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2573 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2575 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateTimestamp", new Object[] { paramString, paramTimestamp }));
/*      */       }
/* 2578 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2580 */         LOG.fine(this._logId + " updateTimestamp(String, java.sql.Timestamp)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2585 */     ErrorMessage.raiseError("JZ0BT", "updateTimestamp(String, java.sql.Timestamp)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2594 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2596 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2598 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 2602 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2604 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 2607 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2609 */         LOG.fine(this._logId + " updateAsciiStream(String, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2614 */     ErrorMessage.raiseError("JZ0BT", "updateAsciiStream(String, java.io.InputStream, int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2624 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2626 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2628 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 2632 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2634 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 2638 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2640 */         LOG.fine(this._logId + " updateBinaryStream(String, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2645 */     ErrorMessage.raiseError("JZ0BT", "updateBinaryStream(String, java.io.InputStream, int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(String paramString, Reader paramReader, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2655 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2657 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2659 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { paramString, paramReader, new Integer(paramInt) }));
/*      */       }
/* 2663 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2665 */         LOG.fine(this._logId + " updateCharacterStream(String, java.io.Reader, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2670 */     ErrorMessage.raiseError("JZ0BT", "updateCharacterStream(String, java.io.Reader, int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateObject(String paramString, Object paramObject, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2679 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2681 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2683 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateObject", new Object[] { paramString, paramObject, new Integer(paramInt) }));
/*      */       }
/* 2686 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2688 */         LOG.fine(this._logId + " updateObject(String, Object, int)");
/*      */       }
/*      */     }
/*      */ 
/* 2692 */     ErrorMessage.raiseError("JZ0BT", "updateObject(String, Object, int)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateObject(String paramString, Object paramObject)
/*      */     throws SQLException
/*      */   {
/* 2699 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2701 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2703 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateObject", new Object[] { paramString, paramObject }));
/*      */       }
/* 2706 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2708 */         LOG.fine(this._logId + " updateObject(String, Object)");
/*      */       }
/*      */     }
/*      */ 
/* 2712 */     ErrorMessage.raiseError("JZ0BT", "updateObject(String, Object)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void insertRow()
/*      */     throws SQLException
/*      */   {
/* 2719 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2721 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2723 */       LOG.fine(this._logId + " insertRow()");
/*      */     }
/*      */ 
/* 2727 */     ErrorMessage.raiseError("JZ0BT", "insertRow()", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateRow()
/*      */     throws SQLException
/*      */   {
/* 2734 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2736 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2738 */       LOG.fine(this._logId + " updateRow()");
/*      */     }
/*      */ 
/* 2742 */     ErrorMessage.raiseError("JZ0BT", "updateRow()", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void deleteRow()
/*      */     throws SQLException
/*      */   {
/* 2749 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2751 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2753 */       LOG.fine(this._logId + " deleteRow()");
/*      */     }
/*      */ 
/* 2757 */     ErrorMessage.raiseError("JZ0BT", "deleteRow()", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void cancelRowUpdates()
/*      */     throws SQLException
/*      */   {
/* 2764 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2766 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2768 */       LOG.fine(this._logId + " cancelRowUpdates()");
/*      */     }
/*      */ 
/* 2772 */     ErrorMessage.raiseError("JZ0BT", "cancelRowUpdates()", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public Statement getStatement()
/*      */     throws SQLException
/*      */   {
/* 2779 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2781 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2783 */       LOG.fine(this._logId + " getStatement()");
/*      */     }
/*      */ 
/* 2787 */     return this._statement;
/*      */   }
/*      */ 
/*      */   public Ref getRef(int paramInt) throws SQLException
/*      */   {
/* 2792 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2794 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2796 */         LOG.finer(this._logId + " getRef(int = [" + paramInt + "])");
/*      */       }
/* 2798 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2800 */         LOG.fine(this._logId + " getRef(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2805 */     Debug.notSupported(this, "getRef(int)");
/* 2806 */     return null;
/*      */   }
/*      */ 
/*      */   public Blob getBlob(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2812 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2814 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2816 */         LOG.finer(this._logId + " getBlob(int = [" + paramInt + "])");
/*      */       }
/* 2818 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2820 */         LOG.fine(this._logId + " getBlob(int)");
/*      */       }
/*      */     }
/*      */ 
/* 2824 */     return getColumn(paramInt).getBlob();
/*      */   }
/*      */ 
/*      */   protected Blob getInitializedBlob(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2835 */     return getColumn(paramInt).getInitializedBlob();
/*      */   }
/*      */ 
/*      */   public void updateBlob(int paramInt, Blob paramBlob) throws SQLException
/*      */   {
/* 2840 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2842 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2844 */         LOG.finest(LogUtil.logMethod(false, this._logId, "updateBlob", new Object[] { new Integer(paramInt), paramBlob }));
/*      */       }
/* 2847 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2849 */         LOG.finer(LogUtil.logMethod(true, this._logId, "updateBlob", new Object[] { new Integer(paramInt), paramBlob }));
/*      */       }
/* 2852 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2854 */         LOG.fine(this._logId + " updateBlob(int, Blob)");
/*      */       }
/*      */     }
/*      */ 
/* 2858 */     ErrorMessage.raiseError("JZ0BT", "updateBlob(int, Blob)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBlob(String paramString, Blob paramBlob)
/*      */     throws SQLException
/*      */   {
/* 2865 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2867 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2869 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBlob", new Object[] { paramString, paramBlob }));
/*      */       }
/* 2872 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2874 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBlob", new Object[] { paramString, paramBlob }));
/*      */       }
/* 2877 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2879 */         LOG.fine(this._logId + " updateBlob(String, Blob)");
/*      */       }
/*      */     }
/*      */ 
/* 2883 */     ErrorMessage.raiseError("JZ0BT", "updateBlob(String, Blob)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public Clob getClob(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2890 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2892 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2894 */         LOG.finer(this._logId + " updateClob(int = [" + paramInt + "])");
/*      */       }
/* 2896 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2898 */         LOG.fine(this._logId + " updateClob(int)");
/*      */       }
/*      */     }
/*      */ 
/* 2902 */     return getColumn(paramInt).getClob();
/*      */   }
/*      */ 
/*      */   protected Clob getInitializedClob(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2917 */     return getColumn(paramInt).getInitializedClob();
/*      */   }
/*      */ 
/*      */   public void updateClob(int paramInt, Clob paramClob) throws SQLException
/*      */   {
/* 2922 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2924 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2926 */         LOG.finest(LogUtil.logMethod(false, this._logId, "updateClob", new Object[] { new Integer(paramInt), paramClob }));
/*      */       }
/* 2929 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2931 */         LOG.finer(LogUtil.logMethod(true, this._logId, "updateClob", new Object[] { new Integer(paramInt), paramClob }));
/*      */       }
/* 2934 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2936 */         LOG.fine(this._logId + " updateClob(int, Clob)");
/*      */       }
/*      */     }
/*      */ 
/* 2940 */     ErrorMessage.raiseError("JZ0BT", "updateClob(int, Clob)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateClob(String paramString, Clob paramClob)
/*      */     throws SQLException
/*      */   {
/* 2947 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2949 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2951 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateClob", new Object[] { paramString, paramClob }));
/*      */       }
/* 2954 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2956 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateClob", new Object[] { paramString, paramClob }));
/*      */       }
/* 2959 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2961 */         LOG.fine(this._logId + " updateClob(String, Clob)");
/*      */       }
/*      */     }
/*      */ 
/* 2965 */     ErrorMessage.raiseError("JZ0BT", "updateClob(String, Clob)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public Array getArray(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2972 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2974 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2976 */         LOG.finer(this._logId + " getArray(int = [" + paramInt + "])");
/*      */       }
/* 2978 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2980 */         LOG.fine(this._logId + " getArray(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2985 */     Debug.notSupported(this, "getArray(int)");
/* 2986 */     return null;
/*      */   }
/*      */ 
/*      */   public void updateArray(int paramInt, Array paramArray) throws SQLException
/*      */   {
/* 2991 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2993 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2995 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateArray", new Object[] { new Integer(paramInt), paramArray }));
/*      */       }
/* 2998 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3000 */         LOG.fine(this._logId + " updateArray(int, Array)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3005 */     Debug.notSupported(this, "updateArray(int, Array)");
/*      */   }
/*      */ 
/*      */   public void updateArray(String paramString, Array paramArray) throws SQLException
/*      */   {
/* 3010 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3012 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3014 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateArray", new Object[] { paramString, paramArray }));
/*      */       }
/* 3017 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3019 */         LOG.fine(this._logId + " updateArray(String, Array)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3024 */     Debug.notSupported(this, "updateArray(String, Array)");
/*      */   }
/*      */ 
/*      */   public Ref getRef(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3031 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3033 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3035 */         LOG.finer(this._logId + " getRef(String = [" + paramString + "])");
/*      */       }
/* 3037 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3039 */         LOG.fine(this._logId + " getRef(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3044 */     Debug.notSupported(this, "getRef(String)");
/* 3045 */     return null;
/*      */   }
/*      */ 
/*      */   public void updateRef(int paramInt, Ref paramRef) throws SQLException
/*      */   {
/* 3050 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3052 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3054 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateRef", new Object[] { new Integer(paramInt), paramRef }));
/*      */       }
/* 3057 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3059 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateRef", new Object[] { new Integer(paramInt), paramRef }));
/*      */       }
/* 3062 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3064 */         LOG.fine(this._logId + " updateRef(int, Ref)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3069 */     Debug.notSupported(this, "updateRef(int, Ref)");
/*      */   }
/*      */ 
/*      */   public void updateRef(String paramString, Ref paramRef) throws SQLException
/*      */   {
/* 3074 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3076 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3078 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateRef", new Object[] { paramString, paramRef }));
/*      */       }
/* 3081 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3083 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateRef", new Object[] { paramString, paramRef }));
/*      */       }
/* 3086 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3088 */         LOG.fine(this._logId + " updateRef(String, Ref)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3093 */     Debug.notSupported(this, "updateRef(String, Ref)");
/*      */   }
/*      */ 
/*      */   public Blob getBlob(String paramString) throws SQLException
/*      */   {
/* 3098 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3100 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3102 */         LOG.finer(this._logId + " getBlob(String = [" + paramString + "])");
/*      */       }
/* 3104 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3106 */         LOG.fine(this._logId + " getBlob(String)");
/*      */       }
/*      */     }
/*      */ 
/* 3110 */     return getBlob(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public Clob getClob(String paramString) throws SQLException
/*      */   {
/* 3115 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3117 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3119 */         LOG.finer(this._logId + " getClob(String = [" + paramString + "])");
/*      */       }
/* 3121 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3123 */         LOG.fine(this._logId + " getClob(String)");
/*      */       }
/*      */     }
/*      */ 
/* 3127 */     return getClob(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public Array getArray(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3134 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3136 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3138 */         LOG.finer(this._logId + " getArray(String = [" + paramString + "])");
/*      */       }
/* 3140 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3142 */         LOG.fine(this._logId + " getArray(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3147 */     Debug.notSupported(this, "getArray(String)");
/* 3148 */     return null;
/*      */   }
/*      */ 
/*      */   public Date getDate(int paramInt, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 3156 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3158 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3160 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getDate", new Object[] { new Integer(paramInt), paramCalendar }));
/*      */       }
/* 3163 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3165 */         LOG.fine(this._logId + " getDate(int, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 3169 */     return Convert.objectToDate(getColumn(paramInt).getDateObject(91, paramCalendar));
/*      */   }
/*      */ 
/*      */   public Date getDate(String paramString, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 3176 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3178 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3180 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getDate", new Object[] { paramString, paramCalendar }));
/*      */       }
/* 3183 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3185 */         LOG.fine(this._logId + " getDate(String, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 3189 */     return getDate(findColumn(paramString), paramCalendar);
/*      */   }
/*      */ 
/*      */   public Time getTime(int paramInt, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 3195 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3197 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3199 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getTime", new Object[] { new Integer(paramInt), paramCalendar }));
/*      */       }
/* 3202 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3204 */         LOG.fine(this._logId + " getTime(int, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 3208 */     return Convert.objectToTime(getColumn(paramInt).getDateObject(92, paramCalendar));
/*      */   }
/*      */ 
/*      */   public Time getTime(String paramString, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 3215 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3217 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3219 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getTime", new Object[] { paramString, paramCalendar }));
/*      */       }
/* 3222 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3224 */         LOG.fine(this._logId + " getTime(String, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 3228 */     return getTime(findColumn(paramString), paramCalendar);
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 3234 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3236 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3238 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getTimestamp", new Object[] { new Integer(paramInt), paramCalendar }));
/*      */       }
/* 3241 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3243 */         LOG.fine(this._logId + " getTimestamp(int, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 3247 */     return Convert.objectToTimestamp(getColumn(paramInt).getDateObject(93, paramCalendar));
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String paramString, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 3254 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3256 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3258 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getTimestamp", new Object[] { paramString, paramCalendar }));
/*      */       }
/* 3261 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3263 */         LOG.fine(this._logId + " getTimestamp(String, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 3267 */     return getTimestamp(findColumn(paramString), paramCalendar);
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */     throws IOException
/*      */   {
/* 3273 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3275 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3277 */       LOG.fine(this._logId + " clear()");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 3284 */       close(false);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 3288 */       ErrorMessage.raiseIOECheckDead(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setManager(CacheManager paramCacheManager)
/*      */   {
/* 3294 */     this._cm = paramCacheManager;
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */     throws IOException
/*      */   {
/* 3301 */     Debug.notSupported(this, "reset()");
/*      */   }
/*      */ 
/*      */   public void resetInputStream(InputStream paramInputStream)
/*      */   {
/* 3307 */     Debug.notSupported(this, "resetInputStream(InputStream is)");
/*      */   }
/*      */ 
/*      */   public void cache() throws IOException
/*      */   {
/*      */     try
/*      */     {
/* 3314 */       switch (this._currentStatus)
/*      */       {
/*      */       case 2:
/* 3317 */         if (this._rowIndex != -2)
/*      */         {
/* 3319 */           if (this._scrollType != 1004)
/*      */           {
/* 3321 */             this._prs.setType(1004);
/*      */           }
/*      */ 
/* 3326 */           while (this._prs.next());
/* 3327 */           if (this._rowIndex == -1)
/*      */           {
/* 3329 */             this._prs.absolute(1);
/* 3330 */             this._prs.previous();
/*      */           }
/*      */           else
/*      */           {
/* 3334 */             this._prs.absolute(this._rowIndex);
/*      */           }
/* 3336 */           this._statement._currentOpenRS.addElement(this); } break;
/*      */       case 3:
/* 3340 */         Enumeration localEnumeration = this._statement._currentOpenRS.elements();
/* 3341 */         while (localEnumeration.hasMoreElements())
/*      */         {
/* 3343 */           ((SybResultSet)localEnumeration.nextElement()).close();
/*      */         }
/* 3345 */         this._statement._currentOpenRS.clear();
/*      */       case 1:
/* 3348 */         close();
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 3357 */       ErrorMessage.raiseIOECheckDead(localSQLException);
/*      */     }
/*      */     finally
/*      */     {
/* 3361 */       if (this._cm != null)
/*      */       {
/* 3363 */         this._cm.doneReading();
/*      */ 
/* 3365 */         this._cm.dead(this);
/* 3366 */         this._cm = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void open(boolean paramBoolean)
/*      */   {
/*      */   }
/*      */ 
/*      */   public int getState()
/*      */   {
/* 3378 */     if (this._state == 2)
/*      */     {
/* 3380 */       return 0;
/*      */     }
/* 3382 */     return 1;
/*      */   }
/*      */ 
/*      */   protected synchronized void markDead(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 3397 */     if (this._state == 2)
/*      */     {
/* 3402 */       return;
/*      */     }
/*      */ 
/* 3405 */     this._state = 2;
/*      */     try
/*      */     {
/* 3409 */       if (this._prs != null)
/*      */       {
/* 3411 */         this._prs.close(paramBoolean);
/* 3412 */         if (this._statement != null)
/*      */         {
/*      */           try
/*      */           {
/* 3424 */             SQLWarning localSQLWarning = this._prs.getWarnings();
/* 3425 */             if (localSQLWarning != null)
/*      */             {
/* 3427 */               this._statement.handleSQLE(localSQLWarning);
/*      */             }
/*      */           }
/*      */           catch (SQLException localSQLException)
/*      */           {
/*      */           }
/* 3433 */           this._statement.setRowCount(this._prs.getCount());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 3447 */       if (this._cm != null)
/*      */       {
/* 3449 */         this._cm.doneReading();
/*      */ 
/* 3451 */         this._cm.dead(this);
/* 3452 */         this._cm = null;
/*      */       }
/* 3454 */       if (this._statement != null)
/*      */       {
/* 3456 */         this._statement._currentRS = null;
/* 3457 */         this._statement = null;
/*      */       }
/* 3459 */       this._nameToColumn = null;
/* 3460 */       this._prs = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkResultSet()
/*      */     throws SQLException
/*      */   {
/* 3468 */     if (this._state == 3)
/*      */       return;
/* 3470 */     ErrorMessage.raiseError("JZ0R0");
/*      */   }
/*      */ 
/*      */   protected void close(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 3479 */     markDead(paramBoolean);
/*      */   }
/*      */ 
/*      */   protected JdbcDataObject getColumn(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3487 */     checkResultSet();
/* 3488 */     checkIfReadableRow();
/* 3489 */     this._currentColumn = this._prs.getColumn(paramInt);
/* 3490 */     return this._currentColumn;
/*      */   }
/*      */ 
/*      */   protected void checkIfReadableRow()
/*      */     throws SQLException
/*      */   {
/* 3503 */     if ((this._rowIndex == -1) && (!this._usedForParams))
/*      */     {
/* 3505 */       ErrorMessage.raiseError("JZ0R1");
/*      */     } else {
/* 3507 */       if (this._rowIndex != -2)
/*      */         return;
/* 3509 */       ErrorMessage.raiseError("JZ0R5");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setHoldsParams(boolean paramBoolean)
/*      */   {
/* 3515 */     this._usedForParams = paramBoolean;
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(int paramInt1, BigDecimal paramBigDecimal, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/* 3528 */     Debug.notImplemented(this, "updateBigDecimal");
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 3541 */     Debug.notImplemented(this, "updateBigDecimal");
/*      */   }
/*      */ 
/*      */   protected void checkForScrollability(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3547 */     switch (this._scrollType)
/*      */     {
/*      */     case 1003:
/* 3550 */       ErrorMessage.raiseError("JZ0BT", paramString, "TYPE_FORWARD_ONLY");
/*      */ 
/* 3553 */       break;
/*      */     case 1004:
/* 3556 */       break;
/*      */     case 1005:
/* 3558 */       Debug.notImplemented(this, paramString + " for TYPE_SCROLL_SENSITIVE");
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getHoldability()
/*      */     throws SQLException
/*      */   {
/* 3573 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3575 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3577 */       LOG.fine(this._logId + " getHoldability()");
/*      */     }
/*      */ 
/* 3581 */     return this._statement.getResultSetHoldability();
/*      */   }
/*      */ 
/*      */   public Reader getNCharacterStream(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3587 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3589 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3591 */         LOG.finer(this._logId + " getNCharacterStream(int = [" + paramInt + "])");
/*      */       }
/* 3594 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3596 */         LOG.fine(this._logId + " getNCharacterStream(int)");
/*      */       }
/*      */     }
/*      */ 
/* 3600 */     return getColumn(paramInt).getCharacterStream();
/*      */   }
/*      */ 
/*      */   public Reader getNCharacterStream(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3606 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3608 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3610 */         LOG.finer(this._logId + " getNCharacterStream(String = [" + paramString + "])");
/*      */       }
/* 3613 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3615 */         LOG.fine(this._logId + " getNCharacterStream(String)");
/*      */       }
/*      */     }
/*      */ 
/* 3619 */     return getNCharacterStream(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public String getNString(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3677 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3679 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3681 */         LOG.finer(this._logId + " getNString(int = [" + paramInt + "])");
/*      */       }
/* 3683 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3685 */         LOG.fine(this._logId + " getNString(int)");
/*      */       }
/*      */     }
/*      */ 
/* 3689 */     return getColumn(paramInt).getString();
/*      */   }
/*      */ 
/*      */   public String getNString(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3695 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3697 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3699 */         LOG.finer(this._logId + " getNString(String = [" + paramString + "])");
/*      */       }
/* 3702 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3704 */         LOG.fine(this._logId + " getNString(String)");
/*      */       }
/*      */     }
/*      */ 
/* 3708 */     return getNString(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */     throws SQLException
/*      */   {
/* 3717 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3719 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3721 */       LOG.fine(this._logId + " isClosed()");
/*      */     }
/*      */ 
/* 3725 */     return this._state == 2;
/*      */   }
/*      */ 
/*      */   public Object getObject(int paramInt, Map paramMap)
/*      */     throws SQLException
/*      */   {
/* 3731 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3733 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3735 */         LOG.finest(LogUtil.logMethod(false, this._logId, " getObject", new Object[] { new Integer(paramInt), paramMap }));
/*      */       }
/* 3738 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3740 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getObject", new Object[] { new Integer(paramInt), paramMap }));
/*      */       }
/* 3743 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3745 */         LOG.fine(this._logId + " getObject(int, Map<String, Class<?>>)");
/*      */       }
/*      */     }
/* 3748 */     Debug.notImplemented(this, "getObject(int columnIndex, Map map)");
/* 3749 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getObject(String paramString, Map paramMap)
/*      */     throws SQLException
/*      */   {
/* 3756 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3758 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3760 */         LOG.finest(LogUtil.logMethod(false, this._logId, " getObject", new Object[] { paramString, paramMap }));
/*      */       }
/* 3763 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3765 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getObject", new Object[] { paramString, paramMap }));
/*      */       }
/* 3768 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3770 */         LOG.fine(this._logId + " getObject(String, Map<String, Class<?>>)");
/*      */       }
/*      */     }
/*      */ 
/* 3774 */     Debug.notImplemented(this, "getObject(String columnLabel, Map<String, Class<?>> map)");
/* 3775 */     return null;
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 3859 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3861 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3863 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 3866 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3868 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 3871 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3873 */         LOG.fine(this._logId + " updateAsciiStream(int, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 3877 */     Debug.notImplemented(this, "updateAsciiStream(int columnIndex, InputStream x)");
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 3884 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3886 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3888 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 3891 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3893 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 3896 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3898 */         LOG.fine(this._logId + " updateAsciiStream(String, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 3902 */     Debug.notImplemented(this, "updateAsciiStream(String columnLabel, InputStream x)");
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3909 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3911 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3913 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 3917 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3919 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 3922 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3924 */         LOG.fine(this._logId + " updateAsciiStream(int, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 3928 */     Debug.notImplemented(this, "updateAsciiStream(int columnIndex, InputStream x, long length)");
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3936 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3938 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3940 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 3944 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3946 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 3949 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3951 */         LOG.fine(this._logId + " updateAsciiStream(String, InputStream, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3956 */     Debug.notImplemented(this, "updateAsciiStream(String columnLabel, InputStream x, long length)");
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 3964 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3966 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3968 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 3971 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3973 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 3976 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3978 */         LOG.fine(this._logId + " updateBinaryStream(int, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 3982 */     Debug.notImplemented(this, "updateBinaryStream(int columnIndex, InputStream x)");
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 3990 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3992 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3994 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 3997 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3999 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 4002 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4004 */         LOG.fine(this._logId + " updateBinaryStream(String, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 4008 */     Debug.notImplemented(this, "updateBinaryStream(String columnLabel, InputStream x)");
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 4016 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4018 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4020 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 4024 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4026 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 4030 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4032 */         LOG.fine(this._logId + " updateBinaryStream(int, InputStream, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4037 */     Debug.notImplemented(this, "updateBinaryStream(int columnIndex, InputStream x, long length)");
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 4045 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4047 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4049 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 4053 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4055 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 4059 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4061 */         LOG.fine(this._logId + " updateBinaryStream(String, InputStream, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4066 */     Debug.notImplemented(this, "updateBinaryStream(String columnLabel, InputStream x, long length)");
/*      */   }
/*      */ 
/*      */   public void updateBlob(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 4074 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4076 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4078 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBlob", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 4081 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4083 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBlob", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 4086 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4088 */         LOG.fine(this._logId + " updateBlob(int, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 4092 */     ErrorMessage.raiseError("JZ0BT", "updateBlob(int, InputStream)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBlob(String paramString, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 4101 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4103 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4105 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBlob", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 4108 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4110 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBlob", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 4113 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4115 */         LOG.fine(this._logId + " updateBlob(String, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 4119 */     ErrorMessage.raiseError("JZ0BT", "updateBlob(String, InputStream)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 4128 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4130 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4132 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBlob", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 4135 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4137 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBlob", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 4140 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4142 */         LOG.fine(this._logId + " updateBlob(int, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 4146 */     ErrorMessage.raiseError("JZ0BT", "updateBlob(int, InputStream, long)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateBlob(String paramString, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 4155 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4157 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4159 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBlob", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 4162 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4164 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBlob", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 4167 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4169 */         LOG.fine(this._logId + " updateBlob(String, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 4173 */     ErrorMessage.raiseError("JZ0BT", "updateBlob(String, InputStream, long)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(int paramInt, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 4182 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4184 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4186 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { new Integer(paramInt), paramReader }));
/*      */       }
/* 4189 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4191 */         LOG.fine(this._logId + " updateCharacterStream(int, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 4195 */     Debug.notImplemented(this, "updateCharacterStream(int columnIndex, Reader x)");
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(String paramString, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 4203 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4205 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4207 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { paramString, paramReader }));
/*      */       }
/* 4211 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4213 */         LOG.fine(this._logId + " updateCharacterStream(String, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 4217 */     Debug.notImplemented(this, "updateCharacterStream(String columnLabel, Reader reader)");
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 4225 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4227 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4229 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { new Integer(paramInt), paramReader, new Long(paramLong) }));
/*      */       }
/* 4233 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4235 */         LOG.fine(this._logId + " updateCharacterStream(int, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 4239 */     Debug.notImplemented(this, "updateCharacterStream(int columnIndex, Reader x, long length)");
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(String paramString, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 4247 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4249 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4251 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { paramString, paramReader, new Long(paramLong) }));
/*      */       }
/* 4255 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4257 */         LOG.fine(this._logId + " updateCharacterStream(String, Reader, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4262 */     Debug.notImplemented(this, "updateCharacterStream(String columnLabel, Reader reader, long length)");
/*      */   }
/*      */ 
/*      */   public void updateClob(int paramInt, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 4269 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4271 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4273 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateClob", new Object[] { new Integer(paramInt), paramReader }));
/*      */       }
/* 4276 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4278 */         LOG.fine(this._logId + " updateClob(int, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 4282 */     ErrorMessage.raiseError("JZ0BT", "updateClob(int, Reader)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateClob(String paramString, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 4291 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4293 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4295 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateClob", new Object[] { paramString, paramReader }));
/*      */       }
/* 4298 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4300 */         LOG.fine(this._logId + " updateClob(String, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 4304 */     ErrorMessage.raiseError("JZ0BT", "updateClob(String, Readear)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateClob(int paramInt, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 4313 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4315 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4317 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateClob", new Object[] { new Integer(paramInt), paramReader, new Long(paramLong) }));
/*      */       }
/* 4320 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4322 */         LOG.fine(this._logId + " updateClob(int, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 4326 */     ErrorMessage.raiseError("JZ0BT", "updateClob(int, Reader, long)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateClob(String paramString, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 4335 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4337 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4339 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { paramString, paramReader }));
/*      */       }
/* 4343 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4345 */         LOG.fine(this._logId + " updateCharacterStream(String, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 4349 */     ErrorMessage.raiseError("JZ0BT", "updateClob(String, Reader, long)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateNCharacterStream(int paramInt, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 4358 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4360 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4362 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateNCharacterStream", new Object[] { new Integer(paramInt), paramReader }));
/*      */       }
/* 4366 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4368 */         LOG.fine(this._logId + " updateNCharacterStream(int, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 4372 */     ErrorMessage.raiseError("JZ0BT", "updateNCharacterStream(int, java.io.Reader)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateNCharacterStream(String paramString, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 4382 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4384 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4386 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateNCharacterStream", new Object[] { paramString, paramReader }));
/*      */       }
/* 4390 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4392 */         LOG.fine(this._logId + " updateNCharacterStream(String, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 4396 */     ErrorMessage.raiseError("JZ0BT", "updateNCharacterStream(String, java.io.Reader)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 4406 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4408 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4410 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateNCharacterStream", new Object[] { new Integer(paramInt), paramReader, new Long(paramLong) }));
/*      */       }
/* 4414 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4416 */         LOG.fine(this._logId + " updateNCharacterStream(int, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 4420 */     ErrorMessage.raiseError("JZ0BT", "updateNCharacterStream(int, java.io.Reader, long)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 4430 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4432 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4434 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateNCharacterStream", new Object[] { paramString, paramReader, new Long(paramLong) }));
/*      */       }
/* 4438 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4440 */         LOG.fine(this._logId + " updateNCharacterStream(String, Reader, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4445 */     ErrorMessage.raiseError("JZ0BT", "updateNCharacterStream(String, java.io.Reader, long)", this._concurTypeString);
/*      */   }
/*      */ 
/*      */   public boolean isWrapperFor(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 4725 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4727 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4729 */         LOG.finer(LogUtil.logMethod(true, this._logId, " isWrapperFor", new Object[] { paramClass }));
/*      */       }
/* 4732 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4734 */         LOG.fine(this._logId + " isWrapperFor(Class<?>)");
/*      */       }
/*      */     }
/* 4737 */     return paramClass.isInstance(this);
/*      */   }
/*      */ 
/*      */   public Object unwrap(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 4745 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4747 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4749 */         LOG.finer(LogUtil.logMethod(true, this._logId, " unwrap", new Object[] { paramClass }));
/*      */       }
/* 4752 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4754 */         LOG.fine(this._logId + " unwrap(Class<T>)");
/*      */       }
/*      */     }
/* 4757 */     SybResultSet localSybResultSet = null;
/*      */     try
/*      */     {
/* 4760 */       localSybResultSet = this;
/*      */     }
/*      */     catch (ClassCastException localClassCastException)
/*      */     {
/* 4764 */       ErrorMessage.raiseError("JZ031", paramClass.getName());
/*      */     }
/*      */ 
/* 4767 */     return localSybResultSet;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybResultSet
 * JD-Core Version:    0.5.4
 */