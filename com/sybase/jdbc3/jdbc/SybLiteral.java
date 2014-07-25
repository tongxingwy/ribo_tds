/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.tds.RowFormatToken;
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import com.sybase.jdbc3.utils.LogUtil;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Connection;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ public class SybLiteral extends SybResultSet
/*      */   implements PreparedStatement
/*      */ {
/*   35 */   private static Logger LOG = Logger.getLogger(SybLiteral.class.getName());
/*   36 */   private static volatile long _logIdCounter = 0L;
/*      */   private static final String LIST_DELIMITER = ",";
/*   40 */   private int _queryType = 0;
/*   41 */   private String _query = null;
/*   42 */   private boolean _atFirstRow = false;
/*   43 */   private String[] _columnList = null;
/*   44 */   RowFormatToken _rowFmt = null;
/*      */ 
/*      */   public SybLiteral(String paramString1, String paramString2, int paramInt)
/*      */     throws SQLException
/*      */   {
/*   51 */     super(paramString1, (SybStatement)null, (ProtocolResultSet)null);
/*   52 */     this._logId = (paramString1 + "_Li" + _logIdCounter++);
/*      */ 
/*   55 */     this._query = paramString2;
/*   56 */     this._queryType = paramInt;
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/*   66 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*   68 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*   70 */       LOG.fine(this._logId + " executeQuery()");
/*      */     }
/*      */ 
/*   76 */     return this;
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery(String paramString) throws SQLException
/*      */   {
/*   81 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*   83 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*   85 */         LOG.finer(this._logId + " executeQuery(String = [" + paramString + "])");
/*      */       }
/*   87 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*   89 */         LOG.fine(this._logId + " executeQuery(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*   95 */     return null;
/*      */   }
/*      */ 
/*      */   public int executeUpdate()
/*      */     throws SQLException
/*      */   {
/*  103 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  105 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  107 */       LOG.fine(this._logId + " executeUpdate()");
/*      */     }
/*      */ 
/*  113 */     return 0;
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString)
/*      */     throws SQLException
/*      */   {
/*  119 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  121 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  123 */         LOG.finer(this._logId + " executeUpdate(String = [" + paramString + "])");
/*      */       }
/*  125 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  127 */         LOG.fine(this._logId + " executeUpdate(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  133 */     return 0;
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  142 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  144 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  146 */         LOG.finer(this._logId + " executeUpdate(String = [" + paramString + "], int = [" + paramInt + "])");
/*      */       }
/*  149 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  151 */         LOG.fine(this._logId + " executeUpdate(String, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  156 */     Debug.notImplemented(this, "executeUpdate(String, int)");
/*  157 */     return 0;
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/*  166 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  168 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  170 */         LOG.finest(LogUtil.logMethod(false, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/*  173 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  175 */         LOG.finer(LogUtil.logMethod(true, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/*  178 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  180 */         LOG.fine(this._logId + " executeUpdate(String, int[])");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  185 */     Debug.notImplemented(this, "executeUpdate(String, int[])");
/*  186 */     return 0;
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString, String[] paramArrayOfString)
/*      */     throws SQLException
/*      */   {
/*  195 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  197 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  199 */         LOG.finest(LogUtil.logMethod(false, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/*  202 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  204 */         LOG.finer(LogUtil.logMethod(true, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/*  207 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  209 */         LOG.fine(this._logId + " executeUpdate(String, String[])");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  214 */     Debug.notImplemented(this, "executeUpdate(String, String[])");
/*  215 */     return 0;
/*      */   }
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/*  223 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  225 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  227 */       LOG.fine(this._logId + " execute()");
/*      */     }
/*      */ 
/*  233 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString) throws SQLException
/*      */   {
/*  238 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  240 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  242 */         LOG.finer(this._logId + " execute(String = [" + paramString + "])");
/*      */       }
/*  244 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  246 */         LOG.fine(this._logId + " execute(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  252 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  261 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  263 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  265 */         LOG.finer(this._logId + " execute(String = [" + paramString + "], int = [" + paramInt + "])");
/*      */       }
/*  268 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  270 */         LOG.fine(this._logId + " execute(String, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  275 */     Debug.notImplemented(this, "execute(String, int)");
/*  276 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/*  285 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  287 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  289 */         LOG.finest(LogUtil.logMethod(false, this._logId, " execute", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/*  292 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  294 */         LOG.finer(LogUtil.logMethod(true, this._logId, " execute", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/*  297 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  299 */         LOG.fine(this._logId + " executeUpdate(String, int[])");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  304 */     Debug.notImplemented(this, "execute(String, int[])");
/*  305 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString, String[] paramArrayOfString)
/*      */     throws SQLException
/*      */   {
/*  314 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  316 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  318 */         LOG.finest(LogUtil.logMethod(false, this._logId, " execute", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/*  321 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  323 */         LOG.finer(LogUtil.logMethod(true, this._logId, " execute", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/*  326 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  328 */         LOG.fine(this._logId + " executeUpdate(String, int[])");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  333 */     Debug.notImplemented(this, "execute(String, String[])");
/*  334 */     return false;
/*      */   }
/*      */ 
/*      */   public void setNull(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  342 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  344 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  346 */       LOG.finer(this._logId + " setNull(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */     }
/*      */     else {
/*  349 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  351 */       LOG.fine(this._logId + " setNull(int, int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNull(int paramInt1, int paramInt2, String paramString)
/*      */     throws SQLException
/*      */   {
/*  364 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  366 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  368 */       LOG.finer(this._logId + " setNull(int = [" + paramInt1 + "], int = [" + paramInt2 + "] , String = [" + paramString + "])");
/*      */     }
/*      */     else
/*      */     {
/*  372 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  374 */       LOG.fine(this._logId + " setNull(int, int, String)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBoolean(int paramInt, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  387 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  389 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  391 */       LOG.finer(this._logId + " setBoolean(int = [" + paramInt + "], boolean = [" + paramBoolean + "])");
/*      */     }
/*      */     else {
/*  394 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  396 */       LOG.fine(this._logId + " setBoolean(int, boolean)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setByte(int paramInt, byte paramByte)
/*      */     throws SQLException
/*      */   {
/*  409 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  411 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  413 */       LOG.finer(this._logId + " setByte(int = [" + paramInt + "], byte = [" + paramByte + "])");
/*      */     }
/*      */     else {
/*  416 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  418 */       LOG.fine(this._logId + " setByte(int, byte)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setShort(int paramInt, short paramShort)
/*      */     throws SQLException
/*      */   {
/*  431 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  433 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  435 */       LOG.finer(this._logId + " setShort(int = [" + paramInt + "], short = [" + paramShort + "])");
/*      */     }
/*      */     else {
/*  438 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  440 */       LOG.fine(this._logId + " setShort(int, short)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setInt(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  453 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  455 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  457 */       LOG.finer(this._logId + " setInt(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */     }
/*      */     else {
/*  460 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  462 */       LOG.fine(this._logId + " setInt(int, int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setLong(int paramInt, long paramLong)
/*      */     throws SQLException
/*      */   {
/*  475 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  477 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  479 */       LOG.finer(this._logId + " setLong(int = [" + paramInt + "], long = [" + paramLong + "])");
/*      */     }
/*      */     else {
/*  482 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  484 */       LOG.fine(this._logId + " setLong(int, long)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFloat(int paramInt, float paramFloat)
/*      */     throws SQLException
/*      */   {
/*  497 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  499 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  501 */       LOG.finer(this._logId + " setFloat(int = [" + paramInt + "], float = [" + paramFloat + "])");
/*      */     }
/*      */     else {
/*  504 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  506 */       LOG.fine(this._logId + " setFloat(int, float)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDouble(int paramInt, double paramDouble)
/*      */     throws SQLException
/*      */   {
/*  519 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  521 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  523 */       LOG.finer(this._logId + " setDouble(int = [" + paramInt + "], double = [" + paramDouble + "])");
/*      */     }
/*      */     else {
/*  526 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  528 */       LOG.fine(this._logId + " setDouble(int, double)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(int paramInt, BigDecimal paramBigDecimal)
/*      */     throws SQLException
/*      */   {
/*  542 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  544 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  546 */       LOG.finer(this._logId + " setBigDecimal(int = [" + paramInt + "], BigDecimal = [" + paramBigDecimal + "])");
/*      */     }
/*      */     else {
/*  549 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  551 */       LOG.fine(this._logId + " setBigDecimal(int, BigDecimal)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setString(int paramInt, String paramString)
/*      */     throws SQLException
/*      */   {
/*  564 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  566 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  568 */       LOG.finer(this._logId + " setString(int = [" + paramInt + "], String = [" + paramString + "])");
/*      */     }
/*      */     else {
/*  571 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  573 */       LOG.fine(this._logId + " setString(int, String)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBytes(int paramInt, byte[] paramArrayOfByte)
/*      */     throws SQLException
/*      */   {
/*  586 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  588 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  590 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setBytes", new Object[] { new Integer(paramInt), paramArrayOfByte }));
/*      */     }
/*      */     else {
/*  593 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  595 */       LOG.fine(this._logId + " setBytes(int, byte)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(int paramInt, Date paramDate)
/*      */     throws SQLException
/*      */   {
/*  609 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  611 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  613 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setDate", new Object[] { new Integer(paramInt), paramDate }));
/*      */     }
/*      */     else {
/*  616 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  618 */       LOG.fine(this._logId + " setDate(int, java.sql.Date)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(int paramInt, Time paramTime)
/*      */     throws SQLException
/*      */   {
/*  632 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  634 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  636 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setTime", new Object[] { new Integer(paramInt), paramTime }));
/*      */     }
/*      */     else {
/*  639 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  641 */       LOG.fine(this._logId + " setTime(int, java.sql.Time)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int paramInt, Timestamp paramTimestamp)
/*      */     throws SQLException
/*      */   {
/*  655 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  657 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  659 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setTimestamp", new Object[] { new Integer(paramInt), paramTimestamp }));
/*      */     }
/*      */     else {
/*  662 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  664 */       LOG.fine(this._logId + " setTimestamp(int, java.sql.Timestamp)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setURL(int paramInt, URL paramURL)
/*      */     throws SQLException
/*      */   {
/*  678 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  680 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  682 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setURL", new Object[] { new Integer(paramInt), paramURL }));
/*      */     }
/*      */     else {
/*  685 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  687 */       LOG.fine(this._logId + " setURL(int, URL)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  702 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  704 */     if (LOG.isLoggable(Level.FINEST))
/*      */     {
/*  706 */       LOG.finest(LogUtil.logMethod(false, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */     }
/*  709 */     else if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  711 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */     }
/*      */     else {
/*  714 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  716 */       LOG.fine(this._logId + " setAsciiStream(int, java.io.InputStream, int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setUnicodeStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  731 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  733 */     if (LOG.isLoggable(Level.FINEST))
/*      */     {
/*  735 */       LOG.finest(LogUtil.logMethod(false, this._logId, " setUnicodeStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */     }
/*  739 */     else if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  741 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setUnicodeStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */     }
/*      */     else {
/*  744 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  746 */       LOG.fine(this._logId + " setUnicodeStream(int, java.io.InputStream, int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  761 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  763 */     if (LOG.isLoggable(Level.FINEST))
/*      */     {
/*  765 */       LOG.finest(LogUtil.logMethod(false, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */     }
/*  768 */     else if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  770 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */     }
/*      */     else {
/*  773 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  775 */       LOG.fine(this._logId + " setBinaryStream(int, java.io.InputStream, int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearParameters()
/*      */     throws SQLException
/*      */   {
/*  789 */     if ((!LogUtil.isLoggingEnabled(LOG)) || 
/*  791 */       (!LOG.isLoggable(Level.FINE)))
/*      */       return;
/*  793 */     LOG.fine(this._logId + " clearParameters()");
/*      */   }
/*      */ 
/*      */   public void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*  807 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  809 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  811 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setObject", new Object[] { new Integer(paramInt1), paramObject, new Integer(paramInt2), new Integer(paramInt3) }));
/*      */     }
/*      */     else
/*      */     {
/*  815 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  817 */       LOG.fine(this._logId + " setObject(int, Object, int, int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(int paramInt1, Object paramObject, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  831 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  833 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  835 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setObject", new Object[] { new Integer(paramInt1), paramObject, new Integer(paramInt2) }));
/*      */     }
/*      */     else {
/*  838 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  840 */       LOG.fine(this._logId + " setObject(int, Object, int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(int paramInt, Object paramObject)
/*      */     throws SQLException
/*      */   {
/*  853 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  855 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  857 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setObject", new Object[] { new Integer(paramInt), paramObject }));
/*      */     }
/*      */     else {
/*  860 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  862 */       LOG.fine(this._logId + " setObject(int, Object)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getMaxFieldSize()
/*      */     throws SQLException
/*      */   {
/*  873 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  875 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  877 */       LOG.fine(this._logId + " getMaxFielsSize()");
/*      */     }
/*      */ 
/*  883 */     return 0;
/*      */   }
/*      */ 
/*      */   public void setMaxFieldSize(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  891 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  893 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  895 */       LOG.finer(this._logId + " setMaxFieldSize(int = [" + paramInt + "])");
/*      */     } else {
/*  897 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  899 */       LOG.fine(this._logId + " setMaxFieldSize(int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getMaxRows()
/*      */     throws SQLException
/*      */   {
/*  912 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  914 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  916 */       LOG.fine(this._logId + " getMaxRows()");
/*      */     }
/*      */ 
/*  922 */     return 0;
/*      */   }
/*      */ 
/*      */   public void setMaxRows(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  930 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  932 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  934 */       LOG.finer(this._logId + " setMaxRows(int = [" + paramInt + "])");
/*      */     } else {
/*  936 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  938 */       LOG.fine(this._logId + " setMaxRows(int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setEscapeProcessing(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  951 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  953 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  955 */       LOG.finer(this._logId + " setEscapeProcessing(boolean = [" + paramBoolean + "])");
/*      */     }
/*      */     else {
/*  958 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  960 */       LOG.fine(this._logId + " setEscapeProcessing(boolean)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getQueryTimeout()
/*      */     throws SQLException
/*      */   {
/*  973 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  975 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  977 */       LOG.fine(this._logId + " getQueryTimeout()");
/*      */     }
/*      */ 
/*  983 */     return 0;
/*      */   }
/*      */ 
/*      */   public void setQueryTimeout(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  991 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/*  993 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/*  995 */       LOG.finer(this._logId + " setQueryTimeout(int = [" + paramInt + "])");
/*      */     } else {
/*  997 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/*  999 */       LOG.fine(this._logId + " setQueryTimeout(int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void cancel()
/*      */     throws SQLException
/*      */   {
/* 1012 */     if ((!LogUtil.isLoggingEnabled(LOG)) || 
/* 1014 */       (!LOG.isLoggable(Level.FINE)))
/*      */       return;
/* 1016 */     LOG.fine(this._logId + " cancel()");
/*      */   }
/*      */ 
/*      */   public void setCursorName(String paramString)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public ResultSet getResultSet()
/*      */     throws SQLException
/*      */   {
/* 1038 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1040 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1042 */       LOG.fine(this._logId + " getResultSet()");
/*      */     }
/*      */ 
/* 1048 */     return null;
/*      */   }
/*      */ 
/*      */   public int getUpdateCount()
/*      */     throws SQLException
/*      */   {
/* 1056 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1058 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1060 */       LOG.fine(this._logId + " getUpdateCount()");
/*      */     }
/*      */ 
/* 1066 */     return 0;
/*      */   }
/*      */ 
/*      */   public boolean getMoreResults()
/*      */     throws SQLException
/*      */   {
/* 1074 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1076 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1078 */       LOG.fine(this._logId + " getMoreResults()");
/*      */     }
/*      */ 
/* 1084 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean getMoreResults(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1092 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1094 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1096 */         LOG.finer(this._logId + " getMoreResults(int = [" + paramInt + "])");
/*      */       }
/* 1098 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1100 */         LOG.fine(this._logId + " getMoreResults(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1105 */     Debug.notImplemented(this, "getMoreResults(int)");
/* 1106 */     return false;
/*      */   }
/*      */ 
/*      */   public ResultSet getGeneratedKeys()
/*      */     throws SQLException
/*      */   {
/* 1114 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1116 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1118 */       LOG.fine(this._logId + " getGeneratedKeys()");
/*      */     }
/*      */ 
/* 1123 */     Debug.notImplemented(this, "getGeneratedKeys()");
/* 1124 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean next()
/*      */     throws SQLException
/*      */   {
/* 1134 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1136 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1138 */       LOG.fine(this._logId + " next()");
/*      */     }
/*      */ 
/* 1144 */     if (!this._atFirstRow)
/*      */     {
/* 1146 */       this._atFirstRow = true;
/* 1147 */       if (this._queryType != 7)
/*      */       {
/* 1149 */         this._columnList = tokenizeList(this._query);
/*      */       }
/* 1151 */       if ((this._columnList != null) && (this._columnList.length > 0))
/*      */       {
/* 1153 */         this._rowFmt = new RowFormatToken(this._columnList.length);
/* 1154 */         return true;
/*      */       }
/*      */     }
/* 1157 */     return false;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/* 1165 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1167 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1169 */       LOG.fine(this._logId + " close()");
/*      */     }
/*      */ 
/* 1175 */     this._atFirstRow = false;
/* 1176 */     this._columnList = null;
/* 1177 */     this._rowFmt = null;
/* 1178 */     this._query = null;
/*      */   }
/*      */ 
/*      */   public String getString(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1184 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1186 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1188 */         LOG.finer(this._logId + " getString(int = [" + paramInt + "])");
/*      */       }
/* 1190 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1192 */         LOG.fine(this._logId + " getString(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1198 */     if (this._queryType == 7)
/*      */     {
/* 1200 */       return this._query;
/*      */     }
/* 1202 */     return getColumnValue(paramInt);
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(int paramInt) throws SQLException {
/* 1206 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1208 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1210 */         LOG.finer(this._logId + " getBoolean(int = [" + paramInt + "])");
/*      */       }
/* 1212 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1214 */         LOG.fine(this._logId + " getBoolean(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1222 */     return getColumnValue(paramInt).trim().equals("1");
/*      */   }
/*      */ 
/*      */   public int getInt(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1228 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1230 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1232 */         LOG.finer(this._logId + " getInt(int = [" + paramInt + "])");
/*      */       }
/* 1234 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1236 */         LOG.fine(this._logId + " getInt(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1242 */     return new Integer(getColumnValue(paramInt).trim()).intValue();
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/* 1250 */     if ((!LogUtil.isLoggingEnabled(LOG)) || 
/* 1252 */       (!LOG.isLoggable(Level.FINE)))
/*      */       return;
/* 1254 */     LOG.fine(this._logId + " clearWarnings()");
/*      */   }
/*      */ 
/*      */   public ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 1266 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1268 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1270 */       LOG.fine(this._logId + " getMetaData()");
/*      */     }
/*      */ 
/* 1275 */     return this._rowFmt;
/*      */   }
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/* 1281 */     if ((!LogUtil.isLoggingEnabled(LOG)) || 
/* 1283 */       (!LOG.isLoggable(Level.FINE)))
/*      */       return;
/* 1285 */     LOG.fine(this._logId + " addBatch()");
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1299 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/* 1301 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/* 1303 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setCharacterStream", new Object[] { new Integer(paramInt1), paramReader, new Integer(paramInt2) }));
/*      */     }
/*      */     else
/*      */     {
/* 1307 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/* 1309 */       LOG.fine(this._logId + " setCharacterStream(int, java.io.Reader, int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setRef(int paramInt, Ref paramRef)
/*      */     throws SQLException
/*      */   {
/* 1322 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/* 1324 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/* 1326 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setRef", new Object[] { new Integer(paramInt), paramRef }));
/*      */     }
/*      */     else {
/* 1329 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/* 1331 */       LOG.fine(this._logId + " setRef(int, Ref)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(int paramInt, Blob paramBlob)
/*      */     throws SQLException
/*      */   {
/* 1342 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/* 1344 */     if (LOG.isLoggable(Level.FINEST))
/*      */     {
/* 1346 */       LOG.finest(LogUtil.logMethod(false, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramBlob }));
/*      */     }
/* 1349 */     else if (LOG.isLoggable(Level.FINER))
/*      */     {
/* 1351 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramBlob }));
/*      */     }
/*      */     else {
/* 1354 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/* 1356 */       LOG.fine(this._logId + " setBlob(int, Blob)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(int paramInt, Clob paramClob)
/*      */     throws SQLException
/*      */   {
/* 1367 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/* 1369 */     if (LOG.isLoggable(Level.FINEST))
/*      */     {
/* 1371 */       LOG.finest(LogUtil.logMethod(false, this._logId, " setClob", new Object[] { new Integer(paramInt), paramClob }));
/*      */     }
/* 1374 */     else if (LOG.isLoggable(Level.FINER))
/*      */     {
/* 1376 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setClob", new Object[] { new Integer(paramInt), paramClob }));
/*      */     }
/*      */     else {
/* 1379 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/* 1381 */       LOG.fine(this._logId + " setClob(int, Clob)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setArray(int paramInt, Array paramArray)
/*      */     throws SQLException
/*      */   {
/* 1393 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/* 1395 */     if (LOG.isLoggable(Level.FINEST))
/*      */     {
/* 1397 */       LOG.finest(LogUtil.logMethod(false, this._logId, " setArray", new Object[] { new Integer(paramInt), paramArray }));
/*      */     }
/* 1400 */     else if (LOG.isLoggable(Level.FINER))
/*      */     {
/* 1402 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setArray", new Object[] { new Integer(paramInt), paramArray }));
/*      */     }
/*      */     else {
/* 1405 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/* 1407 */       LOG.fine(this._logId + " setArray(int, Array)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(int paramInt, Date paramDate, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 1420 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/* 1422 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/* 1424 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setDate", new Object[] { new Integer(paramInt), paramDate, paramCalendar }));
/*      */     }
/*      */     else {
/* 1427 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/* 1429 */       LOG.fine(this._logId + " setDate(int, java.sql.Date, Calendar)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(int paramInt, Time paramTime, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 1442 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/* 1444 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/* 1446 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setTime", new Object[] { new Integer(paramInt), paramTime, paramCalendar }));
/*      */     }
/*      */     else {
/* 1449 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/* 1451 */       LOG.fine(this._logId + " setTime(int, java.sql.Time, Calendar)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 1464 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/* 1466 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/* 1468 */       LOG.finer(LogUtil.logMethod(true, this._logId, " setTimestamp", new Object[] { new Integer(paramInt), paramTimestamp, paramCalendar }));
/*      */     }
/*      */     else {
/* 1471 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/* 1473 */       LOG.fine(this._logId + " setTimestamp(int, java.sql.Timestamp, Calendar)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 1483 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1485 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1487 */       LOG.fine(this._logId + " getResultSetHoldability()");
/*      */     }
/*      */ 
/* 1492 */     return 0;
/*      */   }
/*      */ 
/*      */   public ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/* 1500 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1502 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1504 */       LOG.fine(this._logId + " getParameterMetaData()");
/*      */     }
/*      */ 
/* 1509 */     Debug.notImplemented(this, "getParameterMetaData()");
/* 1510 */     return null;
/*      */   }
/*      */ 
/*      */   public int getResultSetType() throws SQLException
/*      */   {
/* 1515 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1517 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1519 */       LOG.fine(this._logId + " getResultSet()");
/*      */     }
/*      */ 
/* 1524 */     return 0;
/*      */   }
/*      */ 
/*      */   public void addBatch(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1531 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/* 1533 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/* 1535 */       LOG.finer(this._logId + " addBatch(String = [" + paramString + "])");
/*      */     } else {
/* 1537 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/* 1539 */       LOG.fine(this._logId + " addBatch(String)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearBatch()
/*      */     throws SQLException
/*      */   {
/* 1550 */     if ((!LogUtil.isLoggingEnabled(LOG)) || 
/* 1552 */       (!LOG.isLoggable(Level.FINE)))
/*      */       return;
/* 1554 */     LOG.fine(this._logId + " clearBatch()");
/*      */   }
/*      */ 
/*      */   public int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/* 1565 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1567 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1569 */       LOG.fine(this._logId + " executeBatch()");
/*      */     }
/*      */ 
/* 1574 */     return null;
/*      */   }
/*      */ 
/*      */   public Connection getConnection()
/*      */     throws SQLException
/*      */   {
/* 1581 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1583 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1585 */       LOG.fine(this._logId + " getConnection()");
/*      */     }
/*      */ 
/* 1590 */     return null;
/*      */   }
/*      */ 
/*      */   public int getResultSetConcurrency()
/*      */     throws SQLException
/*      */   {
/* 1597 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1599 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1601 */       LOG.fine(this._logId + " getResultSetConcurrency()");
/*      */     }
/*      */ 
/* 1606 */     return 0;
/*      */   }
/*      */ 
/*      */   private String getColumnValue(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1615 */     if (!this._atFirstRow)
/*      */     {
/* 1617 */       ErrorMessage.raiseError("JZ0R1");
/*      */     }
/*      */ 
/* 1620 */     if ((this._columnList == null) || (paramInt < 0) || ((this._columnList != null) && (paramInt > this._columnList.length)))
/*      */     {
/* 1623 */       ErrorMessage.raiseError("JZ008", "" + paramInt);
/*      */     }
/*      */ 
/* 1627 */     return this._columnList[(paramInt - 1)];
/*      */   }
/*      */ 
/*      */   private static String[] tokenizeList(String paramString)
/*      */   {
/* 1633 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",", false);
/*      */ 
/* 1635 */     if (localStringTokenizer != null)
/*      */     {
/* 1637 */       int i = localStringTokenizer.countTokens();
/* 1638 */       String[] arrayOfString = new String[i];
/* 1639 */       for (int j = 0; j < i; ++j)
/*      */       {
/* 1641 */         arrayOfString[j] = localStringTokenizer.nextToken();
/*      */       }
/* 1643 */       return arrayOfString;
/*      */     }
/*      */ 
/* 1646 */     return null;
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 1653 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1655 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1657 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 1660 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1662 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 1665 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1667 */         LOG.fine(this._logId + " setAsciiStream(int, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 1671 */     Debug.notImplemented(this, "public void setAsciiStream(int parameterIndex, InputStream x)");
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 1678 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1680 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1682 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 1685 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1687 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 1690 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1692 */         LOG.fine(this._logId + " setAsciiStream(int, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 1696 */     Debug.notImplemented(this, "public void setAsciiStream(int parameterIndex, InputStream x, long length)");
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 1703 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1705 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1707 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 1710 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1712 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 1715 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1717 */         LOG.fine(this._logId + " setBinaryStream(int, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 1721 */     Debug.notImplemented(this, "public void setBinaryStream(int parameterIndex, InputStream x)");
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 1728 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1730 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1732 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 1735 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1737 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 1740 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1742 */         LOG.fine(this._logId + " setBinaryStream(int, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 1746 */     Debug.notImplemented(this, "public void setBinaryStream(int parameterIndex, InputStream x, long length)");
/*      */   }
/*      */ 
/*      */   public void setBlob(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 1753 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1755 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1757 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 1760 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1762 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 1765 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1767 */         LOG.fine(this._logId + " setBlob(int, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 1771 */     Debug.notImplemented(this, "public void setBlob(int parameterIndex, InputStream inputStream)");
/*      */   }
/*      */ 
/*      */   public void setBlob(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 1778 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1780 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1782 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 1785 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1787 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 1790 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1792 */         LOG.fine(this._logId + " setBlob(int, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 1796 */     Debug.notImplemented(this, "public void setBlob(int parameterIndex, InputStream inputStream, long length)");
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int paramInt, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 1803 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1805 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1807 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setCharacterStream", new Object[] { new Integer(paramInt), paramReader }));
/*      */       }
/* 1811 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1813 */         LOG.fine(this._logId + " setCharacterStream(int, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 1817 */     Debug.notImplemented(this, "public void setCharacterStream(int parameterIndex, Reader reader)");
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int paramInt, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 1824 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1826 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1828 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setCharacterStream", new Object[] { new Integer(paramInt), paramReader, new Long(paramLong) }));
/*      */       }
/* 1832 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1834 */         LOG.fine(this._logId + " setCharacterStream(int, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 1838 */     Debug.notImplemented(this, "public void setCharacterStream(int parameterIndex, Reader reader, long length)");
/*      */   }
/*      */ 
/*      */   public void setClob(int paramInt, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 1844 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1846 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1848 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setClob", new Object[] { new Integer(paramInt), paramReader }));
/*      */       }
/* 1851 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1853 */         LOG.fine(this._logId + " setClob(int, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 1857 */     Debug.notImplemented(this, "public void setClob(int parameterIndex, Reader reader)");
/*      */   }
/*      */ 
/*      */   public void setClob(int paramInt, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 1864 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1866 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1868 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setClob", new Object[] { new Integer(paramInt), paramReader, new Long(paramLong) }));
/*      */       }
/* 1871 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1873 */         LOG.fine(this._logId + " setClob(int, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 1877 */     Debug.notImplemented(this, "public void setClob(int parameterIndex, Reader reader, long length)");
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(int paramInt, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 1884 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1886 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1888 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setNCharacterStream", new Object[] { new Integer(paramInt), paramReader }));
/*      */       }
/* 1892 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1894 */         LOG.fine(this._logId + " setNCharacterStream(int, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 1898 */     Debug.notImplemented(this, "public void setNCharacterStream(int parameterIndex, Reader value)");
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(int paramInt, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 1905 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1907 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1909 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setNCharacterStream", new Object[] { new Integer(paramInt), paramReader, new Long(paramLong) }));
/*      */       }
/* 1913 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1915 */         LOG.fine(this._logId + " setNCharacterStream(int, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 1919 */     Debug.notImplemented(this, "public void setNCharacterStream(int parameterIndex, Reader value, long length)");
/*      */   }
/*      */ 
/*      */   public void setNString(int paramInt, String paramString)
/*      */     throws SQLException
/*      */   {
/* 1990 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1992 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1994 */         LOG.finer(this._logId + " setNString(int = [" + paramInt + "], String = [" + paramString + "])");
/*      */       }
/* 1997 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1999 */         LOG.fine(this._logId + " setNString(int, String)");
/*      */       }
/*      */     }
/*      */ 
/* 2003 */     Debug.notImplemented(this, "public void setNString(int parameterIndex, String value)");
/*      */   }
/*      */ 
/*      */   public boolean isPoolable()
/*      */     throws SQLException
/*      */   {
/* 2054 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2056 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2058 */       LOG.fine(this._logId + " isPoolable()");
/*      */     }
/*      */ 
/* 2062 */     Debug.notImplemented(this, "public boolean isPoolable()");
/* 2063 */     return false;
/*      */   }
/*      */ 
/*      */   public void setPoolable(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 2069 */     Debug.notImplemented(this, "public void setPoolable(boolean poolable)");
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybLiteral
 * JD-Core Version:    0.5.4
 */