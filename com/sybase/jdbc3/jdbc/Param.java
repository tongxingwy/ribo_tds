/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public abstract class Param
/*     */   implements Cloneable
/*     */ {
/*     */   public static final int NOT_SET = -999;
/*     */   public static final int STATUS_RETURN = -998;
/*     */   public static final int JAVA_OBJECT = 2000;
/*     */   public int _sqlType;
/*     */   public int _targetType;
/*     */   public int _colId;
/*     */   public String _sqlTypeName;
/*     */   public int _regType;
/*     */   public int _precision;
/*     */   public int _scale;
/*     */   public int _isNullable;
/*     */   public boolean _isSigned;
/*     */   public Object _inValue;
/*     */   public JdbcDataObject _outValue;
/*     */   protected int _paramMarkerOffset;
/*     */   protected boolean _sendAsLiteral;
/*     */   public String _name;
/*     */   public String _outParamClassName;
/*     */   public boolean _isUnicodeType;
/* 135 */   public static final String[] UNICODE_SQLTYPES = { "unichar", "univarchar", "unitext" };
/*     */ 
/*     */   public Param()
/*     */   {
/*  59 */     this._sqlType = -999;
/*     */ 
/*  63 */     this._targetType = -999;
/*  64 */     this._colId = -999;
/*     */ 
/*  68 */     this._sqlTypeName = null;
/*     */ 
/*  72 */     this._regType = -999;
/*     */ 
/*  77 */     this._precision = -999;
/*     */ 
/*  82 */     this._scale = -999;
/*     */ 
/*  87 */     this._isNullable = 2;
/*     */ 
/*  92 */     this._isSigned = true;
/*     */ 
/*  97 */     this._inValue = null;
/*     */ 
/* 103 */     this._outValue = null;
/*     */ 
/* 109 */     this._paramMarkerOffset = -1;
/*     */ 
/* 116 */     this._sendAsLiteral = false;
/*     */ 
/* 121 */     this._name = null;
/*     */ 
/* 128 */     this._outParamClassName = null;
/*     */ 
/* 133 */     this._isUnicodeType = false;
/*     */   }
/*     */ 
/*     */   public Param cloneMe()
/*     */   {
/*     */     try
/*     */     {
/* 159 */       return (Param)super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException localCloneNotSupportedException)
/*     */     {
/*     */     }
/*     */ 
/* 168 */     return null;
/*     */   }
/*     */ 
/*     */   protected abstract void clear(boolean paramBoolean)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void normalizeForSend(int paramInt)
/*     */     throws SQLException;
/*     */ 
/*     */   protected abstract void prepareForSend(Protocol paramProtocol, int paramInt, boolean paramBoolean)
/*     */     throws SQLException;
/*     */ 
/*     */   protected abstract String literalValue(Protocol paramProtocol, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void send(OutputStream paramOutputStream, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract int getLength();
/*     */ 
/*     */   protected abstract boolean makeFormat(Protocol paramProtocol, byte paramByte)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void sendFormat(DataOutput paramDataOutput)
/*     */     throws IOException;
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.Param
 * JD-Core Version:    0.5.4
 */