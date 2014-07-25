/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import com.sybase.jdbc3.utils.LogUtil;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Map;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ public class SybCallableStatement extends SybPreparedStatement
/*      */   implements com.sybase.jdbcx.SybCallableStatement
/*      */ {
/*   35 */   private static Logger LOG = Logger.getLogger(SybCallableStatement.class.getName());
/*   36 */   private static volatile long _logIdCounter = 0L;
/*      */ 
/*   38 */   protected boolean _hasOutParam = false;
/*      */ 
/*      */   SybCallableStatement(String paramString1, ProtocolContext paramProtocolContext, String paramString2)
/*      */     throws SQLException
/*      */   {
/*   60 */     super(paramString1, paramProtocolContext, paramString2, 2);
/*   61 */     this._logId = (paramString1 + "_Cs" + _logIdCounter++);
/*   62 */     this._sqlStr = paramString2;
/*      */ 
/*   65 */     if (!this._hasReturn)
/*      */       return;
/*   67 */     this._paramMgr.doSetParam(1, -998, new Integer(0), 0);
/*   68 */     this._paramMgr.doRegisterParam(1, 4);
/*   69 */     this._returnHasBeenRegistered = false;
/*      */   }
/*      */ 
/*      */   SybCallableStatement(String paramString, ProtocolContext paramProtocolContext, SybCallableStatement paramSybCallableStatement)
/*      */     throws SQLException
/*      */   {
/*   79 */     super(paramString, paramProtocolContext, paramSybCallableStatement);
/*   80 */     this._logId = (paramString + "_Cs" + _logIdCounter++);
/*      */ 
/*   88 */     this._allowsOutputParms = paramSybCallableStatement._allowsOutputParms;
/*   89 */     this._rpcName = paramSybCallableStatement._rpcName;
/*   90 */     this._sendAsRpc = paramSybCallableStatement._sendAsRpc;
/*   91 */     this._hasReturn = paramSybCallableStatement._hasReturn;
/*   92 */     if (!this._hasReturn)
/*      */       return;
/*   94 */     this._paramMgr.doSetParam(1, -998, new Integer(0), 0);
/*   95 */     this._paramMgr.doRegisterParam(1, 4);
/*   96 */     this._returnHasBeenRegistered = false;
/*      */   }
/*      */ 
/*      */   protected void setDoEscapes()
/*      */   {
/*  103 */     this._doEscapes = true;
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/*  113 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  115 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  117 */       LOG.fine(this._logId + " executeQuery()");
/*      */     }
/*      */ 
/*  123 */     clearParams();
/*  124 */     if (this._sendAsRpc)
/*      */     {
/*  126 */       checkStatement(true);
/*      */ 
/*  128 */       if (sendRpc(this._rpcName, this._paramMgr))
/*      */       {
/*  130 */         return queryLoop();
/*      */       }
/*  132 */       return this._currentRS;
/*      */     }
/*      */ 
/*  137 */     return super.executeQuery(this._query, this._paramMgr);
/*      */   }
/*      */ 
/*      */   public int executeUpdate()
/*      */     throws SQLException
/*      */   {
/*  146 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  148 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  150 */       LOG.fine(this._logId + " executeUpdate()");
/*      */     }
/*      */ 
/*  156 */     clearParams();
/*  157 */     int i = -1;
/*  158 */     if (this._sendAsRpc)
/*      */     {
/*  161 */       checkStatement(true);
/*  162 */       if (this._cursor != null)
/*      */       {
/*  164 */         ErrorMessage.raiseError("JZ0S3");
/*      */       }
/*  166 */       if (sendRpc(this._rpcName, this._paramMgr))
/*      */       {
/*  168 */         i = updateLoop();
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  173 */       i = super.executeUpdate(this._query, this._paramMgr);
/*      */     }
/*  175 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/*  183 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  185 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  187 */       LOG.fine(this._logId + " execute()");
/*      */     }
/*      */ 
/*  193 */     clearParams();
/*  194 */     if (this._sendAsRpc)
/*      */     {
/*  197 */       checkStatement(true);
/*  198 */       if (sendRpc(this._rpcName, this._paramMgr))
/*      */       {
/*  200 */         return executeLoop();
/*      */       }
/*  202 */       return this._currentRS != null;
/*      */     }
/*      */ 
/*  206 */     return super.execute(this._query, this._paramMgr);
/*      */   }
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/*  216 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  218 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  220 */       LOG.fine(this._logId + " addBatch()");
/*      */     }
/*      */ 
/*  226 */     if ((this._sendBatchParamsImmediate) && (((this._hasReturn) || (this._hasOutParam))))
/*      */     {
/*  228 */       ErrorMessage.raiseError("JZ0BP");
/*      */     }
/*      */ 
/*  233 */     if (this._batchCmdsCount < 1)
/*      */     {
/*  235 */       this._query = handleCallBody(this._query);
/*      */     }
/*      */ 
/*  238 */     super.addBatch();
/*      */   }
/*      */ 
/*      */   public int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/*  248 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  250 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  252 */       LOG.fine(this._logId + " executeBatch()");
/*      */     }
/*      */ 
/*  257 */     if ((this._hasReturn) || (this._hasOutParam))
/*      */     {
/*  259 */       ErrorMessage.raiseError("JZ0BP");
/*      */     }
/*  261 */     return super.executeBatch();
/*      */   }
/*      */ 
/*      */   public void cancel()
/*      */     throws SQLException
/*      */   {
/*  270 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  272 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  274 */       LOG.fine(this._logId + " cancel()");
/*      */     }
/*      */ 
/*  280 */     checkStatement(false);
/*      */     try
/*      */     {
/*  283 */       this._paramMgr.clearParams(false);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */ 
/*  289 */     super.cancel();
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  298 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  300 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  302 */         LOG.finer(this._logId + " registerOutParameter(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */       }
/*  305 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  307 */         LOG.fine(this._logId + " registerOutParameter(int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  312 */     checkOutParamRegistration(paramInt1);
/*  313 */     this._paramMgr.registerParam(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  322 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  324 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  326 */         LOG.finer(this._logId + " registerOutParameter(String = [" + paramString + "], int = [" + paramInt + "])");
/*      */       }
/*  329 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  331 */         LOG.fine(this._logId + " registerOutParameter(String, int)");
/*      */       }
/*      */     }
/*      */ 
/*  335 */     this._paramMgr.registerParam(paramString, paramInt);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int paramInt1, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*  344 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  346 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  348 */         LOG.finer(this._logId + " registerOutParameter(int = [" + paramInt1 + "], int = [" + paramInt2 + "], int = [" + paramInt3 + "])");
/*      */       }
/*  352 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  354 */         LOG.fine(this._logId + " registerOutParameter(int, int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  359 */     checkOutParamRegistration(paramInt1);
/*  360 */     this._paramMgr.registerParam(paramInt1, paramInt2, paramInt3);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String paramString, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  369 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  371 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  373 */         LOG.finer(this._logId + " registerOutParameter(String = [" + paramString + "], int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */       }
/*  377 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  379 */         LOG.fine(this._logId + " registerOutParameter(String, int, int)");
/*      */       }
/*      */     }
/*      */ 
/*  383 */     this._paramMgr.registerParam(paramString, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int paramInt1, int paramInt2, String paramString)
/*      */     throws SQLException
/*      */   {
/*  395 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  397 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  399 */         LOG.finer(this._logId + " registerOutParameter(int = [" + paramInt1 + "], int = [" + paramInt2 + "], String = [" + paramString + "])");
/*      */       }
/*  403 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  405 */         LOG.fine(this._logId + " registerOutParameter(int, int, String)");
/*      */       }
/*      */     }
/*      */ 
/*  409 */     this._paramMgr.registerParam(paramInt1, paramInt2, paramString);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String paramString1, int paramInt, String paramString2)
/*      */     throws SQLException
/*      */   {
/*  418 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  420 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  422 */         LOG.finer(this._logId + " registerOutParameter(String = [" + paramString1 + "], int = [" + paramInt + "], String = [" + paramString2 + "])");
/*      */       }
/*  426 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  428 */         LOG.fine(this._logId + " registerOutParameter(String, int, String)");
/*      */       }
/*      */     }
/*      */ 
/*  432 */     this._paramMgr.registerParam(paramString1, paramInt, paramString2);
/*      */   }
/*      */ 
/*      */   public boolean wasNull()
/*      */     throws SQLException
/*      */   {
/*  440 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  442 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  444 */       LOG.fine(this._logId + " wassNull()");
/*      */     }
/*      */ 
/*  449 */     return this._paramMgr.wasNull();
/*      */   }
/*      */ 
/*      */   public String getString(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  457 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  459 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  461 */         LOG.finer(this._logId + " getString(int = [" + paramInt + "])");
/*      */       }
/*  464 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  466 */         LOG.fine(this._logId + " getString(int)");
/*      */       }
/*      */     }
/*      */ 
/*  470 */     return this._paramMgr.getOutValueAt(paramInt).getString();
/*      */   }
/*      */ 
/*      */   public String getString(String paramString)
/*      */     throws SQLException
/*      */   {
/*  478 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  480 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  482 */         LOG.finer(this._logId + " getString(String = [" + paramString + "])");
/*      */       }
/*  485 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  487 */         LOG.fine(this._logId + " getString(String)");
/*      */       }
/*      */     }
/*      */ 
/*  491 */     return this._paramMgr.getOutValueAt(paramString).getString();
/*      */   }
/*      */ 
/*      */   public void setString(String paramString1, String paramString2)
/*      */     throws SQLException
/*      */   {
/*  499 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  501 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  503 */         LOG.finer(this._logId + " getString(String = [" + paramString1 + "], String = [" + paramString2 + "])");
/*      */       }
/*  506 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  508 */         LOG.fine(this._logId + " getString(String, String)");
/*      */       }
/*      */     }
/*      */ 
/*  512 */     setParam(12, paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  520 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  522 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  524 */         LOG.finer(this._logId + " getBoolean(int = [" + paramInt + "])");
/*      */       }
/*  527 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  529 */         LOG.fine(this._logId + " getBoolean(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  534 */     return this._paramMgr.getOutValueAt(paramInt).getBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(String paramString)
/*      */     throws SQLException
/*      */   {
/*  542 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  544 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  546 */         LOG.finer(this._logId + " getString(String = [" + paramString + "])");
/*      */       }
/*  549 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  551 */         LOG.fine(this._logId + " getString(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  556 */     return this._paramMgr.getOutValueAt(paramString).getBoolean();
/*      */   }
/*      */ 
/*      */   public void setBoolean(String paramString, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  565 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  567 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  569 */         LOG.finer(this._logId + " setBoolean(String = [" + paramString + "], boolean = [" + paramBoolean + "])");
/*      */       }
/*  572 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  574 */         LOG.fine(this._logId + " setBoolean(String, boolean)");
/*      */       }
/*      */     }
/*      */ 
/*  578 */     Boolean localBoolean = new Boolean(paramBoolean);
/*  579 */     setParam(-7, paramString, localBoolean);
/*      */   }
/*      */ 
/*      */   public byte getByte(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  587 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  589 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  591 */         LOG.finer(this._logId + " getByte(int = [" + paramInt + "])");
/*      */       }
/*  593 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  595 */         LOG.fine(this._logId + " getByte(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  600 */     return this._paramMgr.getOutValueAt(paramInt).getByte();
/*      */   }
/*      */ 
/*      */   public byte getByte(String paramString)
/*      */     throws SQLException
/*      */   {
/*  608 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  610 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  612 */         LOG.finer(this._logId + " getByte(String = [" + paramString + "])");
/*      */       }
/*  616 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  618 */         LOG.fine(this._logId + " getByte(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  623 */     return this._paramMgr.getOutValueAt(paramString).getByte();
/*      */   }
/*      */ 
/*      */   public void setByte(String paramString, byte paramByte)
/*      */     throws SQLException
/*      */   {
/*  631 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  633 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  635 */         LOG.finer(this._logId + " setByte(String = [" + paramString + "], byte = [" + paramByte + "])");
/*      */       }
/*  638 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  640 */         LOG.fine(this._logId + " setByte(String, byte)");
/*      */       }
/*      */     }
/*      */ 
/*  644 */     Integer localInteger = new Integer(paramByte);
/*  645 */     if (localInteger.intValue() < 0)
/*      */     {
/*  651 */       setParam(5, paramString, localInteger);
/*      */     }
/*      */     else
/*      */     {
/*  655 */       setParam(-6, paramString, localInteger);
/*      */     }
/*      */   }
/*      */ 
/*      */   public short getShort(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  664 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  666 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  668 */         LOG.finer(this._logId + " getShort(int = [" + paramInt + "])");
/*      */       }
/*  670 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  672 */         LOG.fine(this._logId + " getShort(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  677 */     return this._paramMgr.getOutValueAt(paramInt).getShort();
/*      */   }
/*      */ 
/*      */   public short getShort(String paramString)
/*      */     throws SQLException
/*      */   {
/*  685 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  687 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  689 */         LOG.finer(this._logId + " getShort(String = [" + paramString + "])");
/*      */       }
/*  692 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  694 */         LOG.fine(this._logId + " getShort(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  699 */     return this._paramMgr.getOutValueAt(paramString).getShort();
/*      */   }
/*      */ 
/*      */   public void setShort(String paramString, short paramShort)
/*      */     throws SQLException
/*      */   {
/*  707 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  709 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  711 */         LOG.finer(this._logId + " setShort(String = [" + paramString + "], short = [" + paramShort + "])");
/*      */       }
/*  714 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  716 */         LOG.fine(this._logId + " setShort(String, short)");
/*      */       }
/*      */     }
/*      */ 
/*  720 */     Integer localInteger = new Integer(paramShort);
/*  721 */     setParam(5, paramString, localInteger);
/*      */   }
/*      */ 
/*      */   public int getInt(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  729 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  731 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  733 */         LOG.finer(this._logId + " getInt(int = [" + paramInt + "])");
/*      */       }
/*  735 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  737 */         LOG.fine(this._logId + " getInt(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  742 */     return this._paramMgr.getOutValueAt(paramInt).getInt();
/*      */   }
/*      */ 
/*      */   public int getInt(String paramString)
/*      */     throws SQLException
/*      */   {
/*  750 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  752 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  754 */         LOG.finer(this._logId + " getInt(String = [" + paramString + "])");
/*      */       }
/*  756 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  758 */         LOG.fine(this._logId + " getInt(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  763 */     return this._paramMgr.getOutValueAt(paramString).getInt();
/*      */   }
/*      */ 
/*      */   public void setInt(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  771 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  773 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  775 */         LOG.finer(this._logId + " setInt(String = [" + paramString + "], int = [" + paramInt + "])");
/*      */       }
/*  778 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  780 */         LOG.fine(this._logId + " setInt(String, int)");
/*      */       }
/*      */     }
/*      */ 
/*  784 */     Integer localInteger = new Integer(paramInt);
/*  785 */     setParam(4, paramString, localInteger);
/*      */   }
/*      */ 
/*      */   public long getLong(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  793 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  795 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  797 */         LOG.finer(this._logId + " getLong(int = [" + paramInt + "])");
/*      */       }
/*  799 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  801 */         LOG.fine(this._logId + " getLong(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  806 */     return this._paramMgr.getOutValueAt(paramInt).getLong();
/*      */   }
/*      */ 
/*      */   public long getLong(String paramString)
/*      */     throws SQLException
/*      */   {
/*  814 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  816 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  818 */         LOG.finer(this._logId + " getLong(String = [" + paramString + "])");
/*      */       }
/*  822 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  824 */         LOG.fine(this._logId + " getLong(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  829 */     return this._paramMgr.getOutValueAt(paramString).getLong();
/*      */   }
/*      */ 
/*      */   public void setLong(String paramString, long paramLong)
/*      */     throws SQLException
/*      */   {
/*  837 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  839 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  841 */         LOG.finer(this._logId + " setLong(String = [" + paramString + "], long = [" + paramLong + "])");
/*      */       }
/*  844 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  846 */         LOG.fine(this._logId + " setLong(String, long)");
/*      */       }
/*      */     }
/*      */ 
/*  850 */     Long localLong = new Long(paramLong);
/*  851 */     setParam(-5, paramString, localLong);
/*      */   }
/*      */ 
/*      */   public float getFloat(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  859 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  861 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  863 */         LOG.finer(this._logId + " getFloat(int = [" + paramInt + "])");
/*      */       }
/*  865 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  867 */         LOG.fine(this._logId + " getFloat(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  872 */     return this._paramMgr.getOutValueAt(paramInt).getFloat();
/*      */   }
/*      */ 
/*      */   public float getFloat(String paramString)
/*      */     throws SQLException
/*      */   {
/*  880 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  882 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  884 */         LOG.finer(this._logId + " getFloat(String = [" + paramString + "])");
/*      */       }
/*  887 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  889 */         LOG.fine(this._logId + " getFloat(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  894 */     return this._paramMgr.getOutValueAt(paramString).getFloat();
/*      */   }
/*      */ 
/*      */   public void setFloat(String paramString, float paramFloat)
/*      */     throws SQLException
/*      */   {
/*  902 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  904 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  906 */         LOG.finer(this._logId + " setFloat(String = [" + paramString + "], float = [" + paramFloat + "])");
/*      */       }
/*  909 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  911 */         LOG.fine(this._logId + " setFloat(String, float)");
/*      */       }
/*      */     }
/*      */ 
/*  915 */     Float localFloat = new Float(paramFloat);
/*  916 */     setParam(7, paramString, localFloat);
/*      */   }
/*      */ 
/*      */   public double getDouble(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  924 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  926 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  928 */         LOG.finer(this._logId + " getDouble(int = [" + paramInt + "])");
/*      */       }
/*  931 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  933 */         LOG.fine(this._logId + " getDouble(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  938 */     return this._paramMgr.getOutValueAt(paramInt).getDouble();
/*      */   }
/*      */ 
/*      */   public double getDouble(String paramString)
/*      */     throws SQLException
/*      */   {
/*  946 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  948 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  950 */         LOG.finer(this._logId + " getDouble(String = [" + paramString + "])");
/*      */       }
/*  953 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  955 */         LOG.fine(this._logId + " getDouble(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  960 */     return this._paramMgr.getOutValueAt(paramString).getDouble();
/*      */   }
/*      */ 
/*      */   public void setDouble(String paramString, double paramDouble)
/*      */     throws SQLException
/*      */   {
/*  968 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  970 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  972 */         LOG.finer(this._logId + " setDouble(String = [" + paramString + "], double = [" + paramDouble + "])");
/*      */       }
/*  975 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  977 */         LOG.fine(this._logId + " setDouble(String, double)");
/*      */       }
/*      */     }
/*      */ 
/*  981 */     Double localDouble = new Double(paramDouble);
/*  982 */     setParam(8, paramString, localDouble);
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  991 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  993 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  995 */         LOG.finer(this._logId + " getBigDecimal(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */       }
/*  998 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1000 */         LOG.fine(this._logId + " getBigDecimal(int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1005 */     return this._paramMgr.getOutValueAt(paramInt1).getBigDecimal(paramInt2);
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1016 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1018 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1020 */         LOG.finer(this._logId + " getBigDecimal(int = [" + paramInt + "])");
/*      */       }
/* 1023 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1025 */         LOG.fine(this._logId + " getBigDecimal(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1030 */     return getBigDecimal(paramInt, -1);
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(String paramString) throws SQLException
/*      */   {
/* 1035 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1037 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1039 */         LOG.finer(this._logId + " getBigDecimal(String = [" + paramString + "])");
/*      */       }
/* 1042 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1044 */         LOG.fine(this._logId + " getBigDecimal(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1049 */     return this._paramMgr.getOutValueAt(paramString).getBigDecimal(-1);
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(String paramString, BigDecimal paramBigDecimal)
/*      */     throws SQLException
/*      */   {
/* 1055 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1057 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1059 */         LOG.finer(this._logId + " setBigDecimal(String = [" + paramString + "], BigDecimal = [" + paramBigDecimal + "])");
/*      */       }
/* 1062 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1064 */         LOG.fine(this._logId + " setBigDecimal(String, BigDecimal)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1069 */     if (paramBigDecimal == null)
/*      */     {
/* 1071 */       setNull(paramString, 2);
/*      */     }
/*      */     else
/*      */     {
/* 1075 */       setParam(2, paramString, paramBigDecimal, paramBigDecimal.scale());
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1084 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1086 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1088 */         LOG.finer(this._logId + " getBytes(int = [" + paramInt + "])");
/*      */       }
/* 1090 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1092 */         LOG.fine(this._logId + " getBytes(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1097 */     return this._paramMgr.getOutValueAt(paramInt).getBytes();
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1105 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1107 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1109 */         LOG.finer(this._logId + " getBytes(String = [" + paramString + "])");
/*      */       }
/* 1112 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1114 */         LOG.fine(this._logId + " getBytes(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1119 */     return this._paramMgr.getOutValueAt(paramString).getBytes();
/*      */   }
/*      */ 
/*      */   public void setBytes(String paramString, byte[] paramArrayOfByte)
/*      */     throws SQLException
/*      */   {
/* 1127 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1129 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1131 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBytes", new Object[] { paramString, paramArrayOfByte }));
/*      */       }
/* 1134 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1136 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBytes", new Object[] { paramString, paramArrayOfByte }));
/*      */       }
/* 1139 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1141 */         LOG.fine(this._logId + " setBytes(String, byte[])");
/*      */       }
/*      */     }
/*      */ 
/* 1145 */     int i = 0;
/* 1146 */     if (paramArrayOfByte != null)
/*      */     {
/* 1148 */       i = paramArrayOfByte.length;
/*      */     }
/* 1150 */     if (i > 255)
/*      */     {
/* 1152 */       setParam(-4, paramString, paramArrayOfByte);
/*      */     }
/*      */     else
/*      */     {
/* 1156 */       setParam(-3, paramString, paramArrayOfByte);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Date getDate(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1165 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1167 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1169 */         LOG.finer(this._logId + " getDate(int = [" + paramInt + "])");
/*      */       }
/* 1171 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1173 */         LOG.fine(this._logId + " getDate(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1178 */     return Convert.objectToDate(this._paramMgr.getOutValueAt(paramInt).getDateObject(91, null));
/*      */   }
/*      */ 
/*      */   public Date getDate(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1187 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1189 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1191 */         LOG.finer(this._logId + " getDate(String = [" + paramString + "])");
/*      */       }
/* 1195 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1197 */         LOG.fine(this._logId + " getDate(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1202 */     return Convert.objectToDate(this._paramMgr.getOutValueAt(paramString).getDateObject(91, null));
/*      */   }
/*      */ 
/*      */   public void setDate(String paramString, Date paramDate)
/*      */     throws SQLException
/*      */   {
/* 1212 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1214 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1216 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setDate", new Object[] { paramString, paramDate }));
/*      */       }
/* 1219 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1221 */         LOG.fine(this._logId + " setDate(String, java.sql.Date)");
/*      */       }
/*      */     }
/*      */ 
/* 1225 */     if (paramDate == null)
/*      */     {
/* 1227 */       setParam(91, paramString, paramDate);
/*      */     }
/*      */     else
/*      */     {
/* 1231 */       setParam(91, paramString, new DateObject(paramDate, 91));
/*      */     }
/*      */   }
/*      */ 
/*      */   public Time getTime(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1240 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1242 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1244 */         LOG.finer(this._logId + " gettime(int = [" + paramInt + "])");
/*      */       }
/* 1246 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1248 */         LOG.fine(this._logId + " getTime(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1253 */     return Convert.objectToTime(this._paramMgr.getOutValueAt(paramInt).getDateObject(92, null));
/*      */   }
/*      */ 
/*      */   public Time getTime(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1262 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1264 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1266 */         LOG.finer(this._logId + " getTime(String = [" + paramString + "])");
/*      */       }
/* 1270 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1272 */         LOG.fine(this._logId + " getTime(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1277 */     return Convert.objectToTime(this._paramMgr.getOutValueAt(paramString).getDateObject(92, null));
/*      */   }
/*      */ 
/*      */   public void setTime(String paramString, Time paramTime)
/*      */     throws SQLException
/*      */   {
/* 1287 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1289 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1291 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setTime", new Object[] { paramString, paramTime }));
/*      */       }
/* 1294 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1296 */         LOG.fine(this._logId + " setTime(String, java.sql.Time)");
/*      */       }
/*      */     }
/*      */ 
/* 1300 */     if (paramTime == null)
/*      */     {
/* 1302 */       setParam(92, paramString, paramTime);
/*      */     }
/*      */     else
/*      */     {
/* 1306 */       setParam(92, paramString, new DateObject(paramTime, 92));
/*      */     }
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1316 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1318 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1320 */         LOG.finer(this._logId + " getTimestamp(int = [" + paramInt + "])");
/*      */       }
/* 1323 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1325 */         LOG.fine(this._logId + " getTimestamp(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1330 */     return Convert.objectToTimestamp(this._paramMgr.getOutValueAt(paramInt).getDateObject(93, null));
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1340 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1342 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1344 */         LOG.finer(this._logId + " getTimestamp(String = [" + paramString + "])");
/*      */       }
/* 1347 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1349 */         LOG.fine(this._logId + " getTimestamp(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1354 */     return Convert.objectToTimestamp(this._paramMgr.getOutValueAt(paramString).getDateObject(93, null));
/*      */   }
/*      */ 
/*      */   public void setTimestamp(String paramString, Timestamp paramTimestamp)
/*      */     throws SQLException
/*      */   {
/* 1364 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1366 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1368 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setTimestamp", new Object[] { paramString, paramTimestamp }));
/*      */       }
/* 1371 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1373 */         LOG.fine(this._logId + " setTimestamp(String, java.sql.Timestamp)");
/*      */       }
/*      */     }
/*      */ 
/* 1377 */     if (paramTimestamp == null)
/*      */     {
/* 1379 */       setParam(93, paramString, paramTimestamp);
/*      */     }
/*      */     else
/*      */     {
/* 1383 */       setParam(93, paramString, new DateObject(paramTimestamp, 93));
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getObject(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1396 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1398 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1400 */         LOG.finer(this._logId + " getObject(int = [" + paramInt + "])");
/*      */       }
/* 1404 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1406 */         LOG.fine(this._logId + " getObject(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1411 */     return this._paramMgr.getOutObjectAt(paramInt);
/*      */   }
/*      */ 
/*      */   public Object getObject(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1419 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1421 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1423 */         LOG.finer(this._logId + " getObject(String = [" + paramString + "])");
/*      */       }
/* 1426 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1428 */         LOG.fine(this._logId + " getObject(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1433 */     return this._paramMgr.getOutObjectAt(paramString);
/*      */   }
/*      */ 
/*      */   public void setObject(String paramString, Object paramObject)
/*      */     throws SQLException
/*      */   {
/* 1441 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1443 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1445 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setObject", new Object[] { paramString, paramObject }));
/*      */       }
/* 1448 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1450 */         LOG.fine(this._logId + " setObject(String, Object)");
/*      */       }
/*      */     }
/*      */ 
/* 1454 */     if (paramObject == null)
/*      */     {
/* 1456 */       ErrorMessage.raiseError("JZ0SE");
/*      */     }
/*      */ 
/* 1459 */     if (paramObject instanceof Short)
/*      */     {
/* 1461 */       setShort(paramString, ((Short)paramObject).shortValue());
/*      */     }
/* 1463 */     else if (paramObject instanceof Byte)
/*      */     {
/* 1465 */       setByte(paramString, ((Byte)paramObject).byteValue());
/*      */     }
/* 1467 */     else if (paramObject instanceof String)
/*      */     {
/* 1469 */       setString(paramString, (String)paramObject);
/*      */     }
/* 1471 */     else if (paramObject instanceof BigDecimal)
/*      */     {
/* 1473 */       setBigDecimal(paramString, (BigDecimal)paramObject);
/*      */     }
/* 1475 */     else if (paramObject instanceof Boolean)
/*      */     {
/* 1477 */       setParam(-7, paramString, paramObject);
/*      */     }
/* 1479 */     else if (paramObject instanceof Integer)
/*      */     {
/* 1481 */       setParam(4, paramString, paramObject);
/*      */     }
/* 1483 */     else if (paramObject instanceof Long)
/*      */     {
/* 1485 */       setParam(-5, paramString, paramObject);
/*      */     }
/* 1487 */     else if (paramObject instanceof Float)
/*      */     {
/* 1489 */       setParam(7, paramString, paramObject);
/*      */     }
/* 1491 */     else if (paramObject instanceof Double)
/*      */     {
/* 1493 */       setParam(8, paramString, paramObject);
/*      */     }
/* 1495 */     else if (paramObject instanceof byte[])
/*      */     {
/* 1497 */       setBytes(paramString, (byte[])paramObject);
/*      */     }
/* 1499 */     else if (paramObject instanceof Date)
/*      */     {
/* 1501 */       setDate(paramString, (Date)paramObject);
/*      */     }
/* 1503 */     else if (paramObject instanceof Time)
/*      */     {
/* 1505 */       setTime(paramString, (Time)paramObject);
/*      */     }
/* 1507 */     else if (paramObject instanceof Timestamp)
/*      */     {
/* 1509 */       setTimestamp(paramString, (Timestamp)paramObject);
/*      */     }
/* 1511 */     else if (paramObject instanceof Serializable)
/*      */     {
/* 1514 */       setParam(2000, paramString, paramObject);
/*      */     }
/*      */     else
/*      */     {
/* 1518 */       ErrorMessage.raiseError("JZ0SE");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(String paramString, Object paramObject, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1529 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1531 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1533 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setObject", new Object[] { paramString, paramObject, new Integer(paramInt) }));
/*      */       }
/* 1536 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1538 */         LOG.fine(this._logId + " setObject(String, Object, int)");
/*      */       }
/*      */     }
/*      */ 
/* 1542 */     setObject(paramString, paramObject, paramInt, 0);
/*      */   }
/*      */ 
/*      */   public void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1551 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1553 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1555 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setObject", new Object[] { paramString, paramObject, new Integer(paramInt1), new Integer(paramInt2) }));
/*      */       }
/* 1558 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1560 */         LOG.fine(this._logId + " setObject(String, Object, int, int )");
/*      */       }
/*      */     }
/*      */ 
/* 1564 */     if (paramObject == null)
/*      */     {
/* 1566 */       setParam(paramInt1, paramString, null, paramInt2);
/*      */     }
/* 1568 */     switch (paramInt1)
/*      */     {
/*      */     case -7:
/* 1571 */       setParam(paramInt1, paramString, Convert.objectToBoolean(paramObject), paramInt2);
/*      */ 
/* 1573 */       break;
/*      */     case -6:
/*      */     case 4:
/*      */     case 5:
/* 1577 */       setParam(paramInt1, paramString, Convert.objectToInt(paramObject), paramInt2);
/*      */ 
/* 1579 */       break;
/*      */     case -5:
/* 1581 */       setParam(paramInt1, paramString, Convert.objectToLong(paramObject), paramInt2);
/*      */ 
/* 1583 */       break;
/*      */     case 1:
/*      */     case 12:
/* 1586 */       setString(paramString, Convert.objectToString(paramObject));
/* 1587 */       break;
/*      */     case -1:
/* 1599 */       setParam(-1, paramString, Convert.objectToString(paramObject));
/*      */ 
/* 1601 */       break;
/*      */     case 2:
/*      */     case 3:
/* 1604 */       if (paramInt2 < 0)
/*      */       {
/* 1607 */         ErrorMessage.raiseError("JZ00I");
/*      */       }
/* 1609 */       setParam(paramInt1, paramString, Convert.objectToBigDecimal(paramObject), paramInt2);
/*      */ 
/* 1611 */       break;
/*      */     case 7:
/* 1613 */       setParam(paramInt1, paramString, Convert.objectToFloat(paramObject), paramInt2);
/*      */ 
/* 1615 */       break;
/*      */     case 6:
/*      */     case 8:
/* 1618 */       setParam(paramInt1, paramString, Convert.objectToDouble(paramObject), paramInt2);
/*      */ 
/* 1620 */       break;
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/* 1624 */       setBytes(paramString, Convert.objectToBytes(paramObject));
/* 1625 */       break;
/*      */     case 91:
/*      */     case 92:
/*      */     case 93:
/* 1629 */       setParam(paramInt1, paramString, Convert.objectToDateObject(paramObject, paramInt1, null), paramInt2);
/*      */ 
/* 1632 */       break;
/*      */     case 1111:
/*      */     case 2000:
/* 1637 */       if ((!paramObject instanceof Serializable) && (paramObject != null)) {
/*      */         break label485;
/*      */       }
/* 1640 */       setParam(2000, paramString, paramObject);
/* 1641 */       break;
/*      */     default:
/* 1645 */       label485: ErrorMessage.raiseError("JZ0SE");
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean sendRpc(String paramString, ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1661 */       if (this._rsConcur == -9)
/*      */       {
/* 1663 */         this._rsConcur = 1007;
/*      */       }
/*      */ 
/* 1666 */       if (this._rsConcur == 1008)
/*      */       {
/* 1670 */         checkCursor(false, 1008);
/*      */       }
/*      */ 
/* 1674 */       if (this._cursor != null)
/*      */       {
/* 1677 */         ProtocolResultSet localProtocolResultSet = this._cursor.open(paramString, paramParamManager, true);
/*      */ 
/* 1679 */         localProtocolResultSet.setType(this._rsType);
/* 1680 */         if (this._cursor.scrollingAtServer())
/*      */         {
/* 1682 */           this._currentRS = new SybScrollCursorResultSet(this._logId, this, localProtocolResultSet);
/*      */         }
/*      */         else
/*      */         {
/* 1686 */           this._currentRS = new SybCursorResultSet(this._logId, this, localProtocolResultSet);
/*      */         }
/* 1688 */         this._context._conn.addCursorResultSet(this._cursor.getName(), this._currentRS);
/*      */ 
/* 1690 */         this._state = 3;
/* 1691 */         return false;
/*      */       }
/*      */ 
/* 1696 */       this._protocol.rpc(this._context, paramString, paramParamManager);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1701 */       handleSQLE(localSQLException);
/*      */     }
/* 1703 */     return true;
/*      */   }
/*      */ 
/*      */   public Ref getRef(int paramInt) throws SQLException
/*      */   {
/* 1708 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1710 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1712 */         LOG.finer(this._logId + " getRef(int = [" + paramInt + "])");
/*      */       }
/* 1714 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1716 */         LOG.fine(this._logId + " getRef(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1721 */     Debug.notSupported(this, "getRef(int)");
/* 1722 */     return null;
/*      */   }
/*      */ 
/*      */   public Ref getRef(String paramString) throws SQLException
/*      */   {
/* 1727 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1729 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1731 */         LOG.finer(this._logId + " getRef(String = [" + paramString + "])");
/*      */       }
/* 1733 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1735 */         LOG.fine(this._logId + " getRef(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1740 */     Debug.notSupported(this, "getRef(String)");
/* 1741 */     return null;
/*      */   }
/*      */ 
/*      */   public Blob getBlob(int paramInt) throws SQLException
/*      */   {
/* 1746 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1748 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1750 */         LOG.finer(this._logId + " getBlob(int = [" + paramInt + "])");
/*      */       }
/* 1752 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1754 */         LOG.fine(this._logId + " getBlob(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1759 */     Debug.notSupported(this, "getBlob(int)");
/* 1760 */     return null;
/*      */   }
/*      */ 
/*      */   public Blob getBlob(String paramString) throws SQLException
/*      */   {
/* 1765 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1767 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1769 */         LOG.finer(this._logId + " getBlob(String = [" + paramString + "])");
/*      */       }
/* 1772 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1774 */         LOG.fine(this._logId + " getBlob(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1779 */     Debug.notSupported(this, "getBlob(String)");
/* 1780 */     return null;
/*      */   }
/*      */ 
/*      */   public Clob getClob(int paramInt) throws SQLException
/*      */   {
/* 1785 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1787 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1789 */         LOG.finer(this._logId + " getClob(int = [" + paramInt + "])");
/*      */       }
/* 1791 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1793 */         LOG.fine(this._logId + " getClob(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1798 */     Debug.notSupported(this, "getClob(int)");
/* 1799 */     return null;
/*      */   }
/*      */ 
/*      */   public Clob getClob(String paramString) throws SQLException
/*      */   {
/* 1804 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1806 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1808 */         LOG.finer(this._logId + " getClob(String = [" + paramString + "])");
/*      */       }
/* 1811 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1813 */         LOG.fine(this._logId + " getClob(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1818 */     Debug.notSupported(this, "getClob(String)");
/* 1819 */     return null;
/*      */   }
/*      */ 
/*      */   public Array getArray(int paramInt) throws SQLException
/*      */   {
/* 1824 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1826 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1828 */         LOG.finer(this._logId + " getArray(int = [" + paramInt + "])");
/*      */       }
/* 1830 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1832 */         LOG.fine(this._logId + " getArray(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1837 */     Debug.notSupported(this, "getArray(int)");
/* 1838 */     return null;
/*      */   }
/*      */ 
/*      */   public Array getArray(String paramString) throws SQLException
/*      */   {
/* 1843 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1845 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1847 */         LOG.finer(this._logId + " getArray(String = [" + paramString + "])");
/*      */       }
/* 1850 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1852 */         LOG.fine(this._logId + " getArray(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1857 */     Debug.notSupported(this, "getArray(String)");
/* 1858 */     return null;
/*      */   }
/*      */ 
/*      */   public Date getDate(int paramInt, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 1866 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1868 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1870 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getDate", new Object[] { new Integer(paramInt), paramCalendar }));
/*      */       }
/* 1873 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1875 */         LOG.fine(this._logId + " getDate(int, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 1879 */     return Convert.objectToDate(this._paramMgr.getOutValueAt(paramInt).getDateObject(91, paramCalendar));
/*      */   }
/*      */ 
/*      */   public Date getDate(String paramString, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 1886 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1888 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1890 */         LOG.finer(LogUtil.logMethod(true, this._logId, "getDate", new Object[] { paramString, paramCalendar }));
/*      */       }
/* 1893 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1895 */         LOG.fine(this._logId + " getDate(String, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 1899 */     return Convert.objectToDate(this._paramMgr.getOutValueAt(paramString).getDateObject(91, paramCalendar));
/*      */   }
/*      */ 
/*      */   public void setDate(String paramString, Date paramDate, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 1909 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1911 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1913 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setDate", new Object[] { paramString, paramDate, paramCalendar }));
/*      */       }
/* 1916 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1918 */         LOG.fine(this._logId + " setDate(String, java.sql.Date, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 1922 */     if (paramDate == null)
/*      */     {
/* 1924 */       setParam(91, paramString, paramDate);
/*      */     }
/*      */     else
/*      */     {
/* 1928 */       setParam(91, paramString, new DateObject(paramDate, paramCalendar, 91));
/*      */     }
/*      */   }
/*      */ 
/*      */   public Time getTime(int paramInt, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 1937 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1939 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1941 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getTime", new Object[] { new Integer(paramInt), paramCalendar }));
/*      */       }
/* 1944 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1946 */         LOG.fine(this._logId + " getTime(int, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 1950 */     return Convert.objectToTime(this._paramMgr.getOutValueAt(paramInt).getDateObject(92, paramCalendar));
/*      */   }
/*      */ 
/*      */   public Time getTime(String paramString, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 1960 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1962 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1964 */         LOG.finer(LogUtil.logMethod(true, this._logId, "getTime", new Object[] { paramString, paramCalendar }));
/*      */       }
/* 1967 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1969 */         LOG.fine(this._logId + " getTime(String, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 1973 */     return Convert.objectToTime(this._paramMgr.getOutValueAt(paramString).getDateObject(92, paramCalendar));
/*      */   }
/*      */ 
/*      */   public void setTime(String paramString, Time paramTime, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 1982 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1984 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1986 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setTime", new Object[] { paramString, paramTime, paramCalendar }));
/*      */       }
/* 1989 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1991 */         LOG.fine(this._logId + " setTime(String, java.sql.Time, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 1995 */     if (paramTime == null)
/*      */     {
/* 1997 */       setParam(92, paramString, paramTime);
/*      */     }
/*      */     else
/*      */     {
/* 2001 */       setParam(92, paramString, new DateObject(paramTime, paramCalendar, 92));
/*      */     }
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 2010 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2012 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2014 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getTimestamp", new Object[] { new Integer(paramInt), paramCalendar }));
/*      */       }
/* 2017 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2019 */         LOG.fine(this._logId + " getTimestamp(int, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 2023 */     return Convert.objectToTimestamp(this._paramMgr.getOutValueAt(paramInt).getDateObject(93, paramCalendar));
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String paramString, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 2033 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2035 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2037 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getTimestamp", new Object[] { paramString, paramCalendar }));
/*      */       }
/* 2040 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2042 */         LOG.fine(this._logId + " getTimestamp(String, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 2046 */     return Convert.objectToTimestamp(this._paramMgr.getOutValueAt(paramString).getDateObject(93, paramCalendar));
/*      */   }
/*      */ 
/*      */   public void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 2056 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2058 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2060 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setTimestamp", new Object[] { paramString, paramTimestamp, paramCalendar }));
/*      */       }
/* 2063 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2065 */         LOG.fine(this._logId + " setTimestamp(String, java.sql.Timestamp, Calendar)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2070 */     if (paramTimestamp == null)
/*      */     {
/* 2072 */       setParam(93, paramString, paramTimestamp);
/*      */     }
/*      */     else
/*      */     {
/* 2076 */       setParam(93, paramString, new DateObject(paramTimestamp, paramCalendar, 93));
/*      */     }
/*      */   }
/*      */ 
/*      */   public URL getURL(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2083 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2085 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2087 */         LOG.finer(this._logId + " getURL(int = [" + paramInt + "])");
/*      */       }
/* 2089 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2091 */         LOG.fine(this._logId + " getURL(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2096 */     Debug.notSupported(this, "getURL(int)");
/* 2097 */     return null;
/*      */   }
/*      */ 
/*      */   public URL getURL(String paramString) throws SQLException
/*      */   {
/* 2102 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2104 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2106 */         LOG.finer(this._logId + " getURL(String = [" + paramString + "])");
/*      */       }
/* 2108 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2110 */         LOG.fine(this._logId + " getURL(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2115 */     Debug.notSupported(this, "getURL(String)");
/* 2116 */     return null;
/*      */   }
/*      */ 
/*      */   public void setURL(String paramString, URL paramURL) throws SQLException
/*      */   {
/* 2121 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2123 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2125 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setURL", new Object[] { paramString, paramURL }));
/*      */       }
/* 2128 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2130 */         LOG.fine(this._logId + " setURL(String, URL)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2135 */     Debug.notSupported(this, "setURL(String, URL)");
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2141 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2143 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2145 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setAsciiStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 2148 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2150 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setAsciiStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 2153 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2155 */         LOG.fine(this._logId + " setAsciiStream(String, InputStream, int)");
/*      */       }
/*      */     }
/*      */ 
/* 2159 */     if (paramInputStream == null)
/*      */     {
/* 2161 */       setNull(paramString, -1);
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/* 2175 */         InputStreamReader localInputStreamReader = new InputStreamReader(new LimiterInputStream(paramInputStream, paramInt), "ISO8859_1");
/*      */ 
/* 2178 */         String str = this._paramMgr.drainReader(localInputStreamReader, paramInt);
/* 2179 */         if (str != null)
/*      */         {
/* 2181 */           setParam(12, paramString, str, 0);
/*      */         }
/*      */         else
/*      */         {
/* 2185 */           setParam(-1, paramString, localInputStreamReader, paramInt);
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 2190 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2198 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2200 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2202 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBinaryStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 2205 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2207 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBinaryStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 2210 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2212 */         LOG.fine(this._logId + " setBinaryStream(String, InputStream, int)");
/*      */       }
/*      */     }
/*      */ 
/* 2216 */     byte[] arrayOfByte = this._paramMgr.drainStreams(paramInputStream, paramInt);
/* 2217 */     if (arrayOfByte != null)
/*      */     {
/* 2219 */       setBytes(paramString, arrayOfByte);
/*      */     }
/*      */     else
/*      */     {
/* 2223 */       setParam(-4, paramString, paramInputStream, paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String paramString, Reader paramReader, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2230 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2232 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2234 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setCharacterStream", new Object[] { paramString, paramReader, new Integer(paramInt) }));
/*      */       }
/* 2238 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2240 */         LOG.fine(this._logId + " setCharacterStream(String, Reader, int)");
/*      */       }
/*      */     }
/*      */ 
/* 2244 */     String str = this._paramMgr.drainReader(paramReader, paramInt);
/* 2245 */     if (str != null)
/*      */     {
/* 2247 */       setParam(12, paramString, str, 0);
/*      */     }
/*      */     else
/*      */     {
/* 2251 */       setParam(-1, paramString, paramReader, paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNull(String paramString, int paramInt) throws SQLException
/*      */   {
/* 2257 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2259 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2261 */         LOG.finer(this._logId + " setNull(String = [" + paramString + "], int = [" + paramInt + "])");
/*      */       }
/* 2264 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2266 */         LOG.fine(this._logId + " setNull(String, int)");
/*      */       }
/*      */     }
/*      */ 
/* 2270 */     setParam(paramInt, paramString, null);
/*      */   }
/*      */ 
/*      */   public void setNull(String paramString1, int paramInt, String paramString2)
/*      */     throws SQLException
/*      */   {
/* 2276 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2278 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2280 */         LOG.finer(this._logId + " setNull(String = [" + paramString1 + "], int = [" + paramInt + "], String = [" + paramString2 + "])");
/*      */       }
/* 2284 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2286 */         LOG.fine(this._logId + " setNull(String, int, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2291 */     Debug.notImplemented(this, "setNull(String, int, String)");
/*      */   }
/*      */ 
/*      */   public void clearParams()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2300 */       this._paramMgr.clearParams(false);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 2304 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void handleParam(ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 2314 */     if (this._batchCmds == null)
/*      */     {
/* 2316 */       this._paramMgr.getParameter();
/*      */     }
/*      */     else
/*      */     {
/* 2320 */       paramParamManager.getParameter();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkOutParamRegistration(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2332 */     if (this._allowsOutputParms) {
/*      */       return;
/*      */     }
/*      */ 
/* 2336 */     ErrorMessage.raiseError("JZ0SB", String.valueOf(paramInt));
/*      */   }
/*      */ 
/*      */   public void setParameterName(int paramInt, String paramString)
/*      */   {
/* 2360 */     Param[] arrayOfParam = this._paramMgr.getParams();
/*      */ 
/* 2363 */     if ((arrayOfParam.length < paramInt) || (paramInt <= 0)) {
/*      */       return;
/*      */     }
/* 2366 */     arrayOfParam[(paramInt - 1)]._name = paramString;
/*      */   }
/*      */ 
/*      */   public ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/* 2380 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2382 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2384 */       LOG.fine(this._logId + " getParameterMetaData()");
/*      */     }
/*      */ 
/* 2388 */     if (this._type != 1)
/*      */     {
/* 2390 */       return super.getParameterMetaData();
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 2395 */       if (this._dbmda == null)
/*      */       {
/* 2397 */         this._dbmda = ((SybDatabaseMetaData)this._context._conn.getMetaData());
/*      */       }
/* 2399 */       if (this._dbmda.getDatabaseProductName().indexOf("Anywhere") != -1)
/*      */       {
/* 2402 */         return null;
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2408 */       return null;
/*      */     }
/*      */ 
/* 2411 */     SybCallableStatement localSybCallableStatement = new SybCallableStatement(this._logId, this._context, "{call sp_jdbc_getprocedurecolumns (@sp_name=?, @parammetadata=1, @paramcolids=?, @paramnames=?)}");
/*      */ 
/* 2415 */     if (this._paramMgr.getParamSetType() == 1)
/*      */     {
/* 2422 */       this._paramNames = this._paramMgr.getColumnNames();
/*      */ 
/* 2428 */       this._paramColids = ((this._hasReturn) ? "0" : null);
/*      */     }
/*      */ 
/* 2431 */     localSybCallableStatement.setString(1, this._rpcName);
/* 2432 */     localSybCallableStatement.setString(2, this._paramColids);
/* 2433 */     localSybCallableStatement.setString(3, this._paramNames);
/* 2434 */     ResultSet localResultSet = localSybCallableStatement.executeQuery();
/*      */ 
/* 2436 */     this._paramMgr.setParamMd(localResultSet);
/* 2437 */     return this;
/*      */   }
/*      */ 
/*      */   private void setParam(int paramInt, String paramString, Object paramObject)
/*      */     throws SQLException
/*      */   {
/* 2445 */     setParam(paramInt, paramString, paramObject, 0);
/*      */   }
/*      */ 
/*      */   private void setParam(int paramInt1, String paramString, Object paramObject, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 2452 */     this._paramMgr.setParam(paramString, paramInt1, paramObject, paramInt2);
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2459 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2461 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2463 */         LOG.finer(this._logId + " getCharacterStream(int = [" + paramInt + "])");
/*      */       }
/* 2466 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2468 */         LOG.fine(this._logId + " getCharacterSream(int)");
/*      */       }
/*      */     }
/*      */ 
/* 2472 */     return this._paramMgr.getOutValueAt(paramInt).getCharacterStream();
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2480 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2482 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2484 */         LOG.finer(this._logId + " getCharacterStream(String = [" + paramString + "])");
/*      */       }
/* 2487 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2489 */         LOG.fine(this._logId + " getCharacterStream(String)");
/*      */       }
/*      */     }
/*      */ 
/* 2493 */     return this._paramMgr.getOutValueAt(paramString).getCharacterStream();
/*      */   }
/*      */ 
/*      */   public Reader getNCharacterStream(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2501 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2503 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2505 */         LOG.finer(this._logId + " getNCharacterStream(int = [" + paramInt + "])");
/*      */       }
/* 2508 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2510 */         LOG.fine(this._logId + " getNCharacterStream(int)");
/*      */       }
/*      */     }
/*      */ 
/* 2514 */     return this._paramMgr.getOutValueAt(paramInt).getCharacterStream();
/*      */   }
/*      */ 
/*      */   public Reader getNCharacterStream(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2522 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2524 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2526 */         LOG.finer(this._logId + " getNCharacterStream(String = [" + paramString + "])");
/*      */       }
/* 2529 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2531 */         LOG.fine(this._logId + " getNCharacterStream(String)");
/*      */       }
/*      */     }
/*      */ 
/* 2535 */     return this._paramMgr.getOutValueAt(paramString).getCharacterStream();
/*      */   }
/*      */ 
/*      */   public String getNString(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2543 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2545 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2547 */         LOG.finer(this._logId + " getNString(int = [" + paramInt + "])");
/*      */       }
/* 2550 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2552 */         LOG.fine(this._logId + " getNString(int)");
/*      */       }
/*      */     }
/*      */ 
/* 2556 */     return this._paramMgr.getOutValueAt(paramInt).getString();
/*      */   }
/*      */ 
/*      */   public String getNString(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2564 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2566 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2568 */         LOG.finer(this._logId + " getNString(String = [" + paramString + "])");
/*      */       }
/* 2571 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2573 */         LOG.fine(this._logId + " getNString(String)");
/*      */       }
/*      */     }
/*      */ 
/* 2577 */     return this._paramMgr.getOutValueAt(paramString).getString();
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String paramString, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 2667 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2669 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2671 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setAsciiStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2674 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2676 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setAsciiStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2679 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2681 */         LOG.fine(this._logId + " setAsciiStream(String, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 2685 */     setAsciiStream(paramString, paramInputStream, -1);
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String paramString, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2694 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2696 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2698 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setAsciiStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2701 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2703 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setAsciiStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2706 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2708 */         LOG.fine(this._logId + " setAsciiStream(String, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 2712 */     if (paramInputStream == null)
/*      */     {
/* 2714 */       setNull(paramString, -1);
/*      */     }
/*      */     else
/*      */     {
/* 2718 */       int i = checkLongLength(paramLong);
/* 2719 */       setAsciiStream(paramString, paramInputStream, i);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String paramString, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 2729 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2731 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2733 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBinaryStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2736 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2738 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBinaryStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2741 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2743 */         LOG.fine(this._logId + " setBinaryStream(String, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 2747 */     setBinaryStream(paramString, paramInputStream, -1);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String paramString, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2756 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2758 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2760 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBinaryStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2763 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2765 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBinaryStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2768 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2770 */         LOG.fine(this._logId + " setBinaryStream(String, InputStream, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2775 */     int i = checkLongLength(paramLong);
/* 2776 */     setBinaryStream(paramString, paramInputStream, i);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String paramString, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 2786 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2788 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2790 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setCharacterStream", new Object[] { paramString, paramReader }));
/*      */       }
/* 2794 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2796 */         LOG.fine(this._logId + " setCharacterStream(String, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 2800 */     setCharacterStream(paramString, paramReader, -1);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String paramString, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2809 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2811 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2813 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setCharacterStream", new Object[] { paramString, paramReader, new Long(paramLong) }));
/*      */       }
/* 2817 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2819 */         LOG.fine(this._logId + " setCharacterStream(String, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 2823 */     int i = checkLongLength(paramLong);
/* 2824 */     setCharacterStream(paramString, paramReader, i);
/*      */   }
/*      */ 
/*      */   public void setBlob(String paramString, Blob paramBlob)
/*      */     throws SQLException
/*      */   {
/* 2902 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2904 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2906 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBlob", new Object[] { paramString, paramBlob }));
/*      */       }
/* 2909 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2911 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBlob", new Object[] { paramString, paramBlob }));
/*      */       }
/* 2914 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2916 */         LOG.fine(this._logId + " setBlob(String, Blob)");
/*      */       }
/*      */     }
/*      */ 
/* 2920 */     Debug.notImplemented(this, "public void setBlob(String arg0, Blob arg1)");
/*      */   }
/*      */ 
/*      */   public void setBlob(String paramString, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 2928 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2930 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2932 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBlob", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2935 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2937 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBlob", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2940 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2942 */         LOG.fine(this._logId + " setBlob(String, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 2946 */     setParam(-4, paramString, paramInputStream, -1);
/*      */   }
/*      */ 
/*      */   public void setBlob(String paramString, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2955 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2957 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2959 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBlob", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2962 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2964 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBlob", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2967 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2969 */         LOG.fine(this._logId + " setBlob(String, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 2973 */     if (paramLong < -1L)
/*      */     {
/* 2975 */       ErrorMessage.raiseError("JZ039");
/*      */     }
/*      */ 
/* 2978 */     int i = checkLongLength(paramLong);
/* 2979 */     setParam(-4, paramString, paramInputStream, i);
/*      */   }
/*      */ 
/*      */   public void setClob(String paramString, Clob paramClob) throws SQLException
/*      */   {
/* 2984 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2986 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2988 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setClob", new Object[] { paramString, paramClob }));
/*      */       }
/* 2991 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2993 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setClob", new Object[] { paramString, paramClob }));
/*      */       }
/* 2996 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2998 */         LOG.fine(this._logId + " setClob(String, Clob)");
/*      */       }
/*      */     }
/*      */ 
/* 3002 */     Debug.notImplemented(this, "public void setClob(String arg0, Clob arg1)");
/*      */   }
/*      */ 
/*      */   public void setClob(String paramString, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 3010 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3012 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3014 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setClob", new Object[] { paramString, paramReader }));
/*      */       }
/* 3017 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3019 */         LOG.fine(this._logId + " setClob(String, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 3023 */     setParam(-1, paramString, paramReader, -1);
/*      */   }
/*      */ 
/*      */   public void setClob(String paramString, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3032 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3034 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3036 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setClob", new Object[] { paramString, paramReader, new Long(paramLong) }));
/*      */       }
/* 3039 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3041 */         LOG.fine(this._logId + " setClob(String, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 3045 */     if (paramLong < -1L)
/*      */     {
/* 3047 */       ErrorMessage.raiseError("JZ039");
/*      */     }
/*      */ 
/* 3050 */     int i = checkLongLength(paramLong);
/* 3051 */     setParam(-1, paramString, paramReader, i);
/*      */   }
/*      */ 
/*      */   public Object getObject(int paramInt, Map paramMap)
/*      */     throws SQLException
/*      */   {
/* 3166 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3168 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3170 */         LOG.finest(LogUtil.logMethod(false, this._logId, " getObject", new Object[] { new Integer(paramInt), paramMap }));
/*      */       }
/* 3173 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3175 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getObject", new Object[] { new Integer(paramInt), paramMap }));
/*      */       }
/* 3178 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3180 */         LOG.fine(this._logId + " getObject(int, Map<String, Class<?>>)");
/*      */       }
/*      */     }
/*      */ 
/* 3184 */     Debug.notImplemented(this, "public Object getObject(int arg0, Map<String, Class<?>> arg1)");
/* 3185 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getObject(String paramString, Map paramMap)
/*      */     throws SQLException
/*      */   {
/* 3191 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3193 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3195 */         LOG.finest(LogUtil.logMethod(false, this._logId, " getObject", new Object[] { paramString, paramMap }));
/*      */       }
/* 3198 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3200 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getObject", new Object[] { paramString, paramMap }));
/*      */       }
/* 3203 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3205 */         LOG.fine(this._logId + " getObject(String, Map<String, Class<?>>)");
/*      */       }
/*      */     }
/*      */ 
/* 3209 */     Debug.notImplemented(this, "public Object getObject(String arg0, Map<String, Class<?>> arg1)");
/* 3210 */     return null;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybCallableStatement
 * JD-Core Version:    0.5.4
 */