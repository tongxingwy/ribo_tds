/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class RowFormat2Token extends RowFormatToken
/*     */ {
/*  29 */   private int _isGetColumnLabelForNameEnabled = -1;
/*     */ 
/*     */   public RowFormat2Token(int paramInt)
/*     */     throws SQLException
/*     */   {
/*  37 */     this._numUserColumns = paramInt;
/*  38 */     this._numColumns = paramInt;
/*  39 */     this._column = new RowDataFormat2[this._numColumns];
/*     */   }
/*     */ 
/*     */   public RowFormat2Token()
/*     */     throws SQLException
/*     */   {
/*  47 */     this._numColumns = 0;
/*  48 */     this._column = new RowDataFormat2[this._numColumns];
/*     */   }
/*     */ 
/*     */   public RowFormat2Token(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  58 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public RowFormat2Token(TdsInputStream paramTdsInputStream, long paramLong)
/*     */     throws IOException
/*     */   {
/*  64 */     super(paramTdsInputStream, paramLong);
/*     */   }
/*     */ 
/*     */   protected DataFormat dataFormatFactory(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  70 */     return new RowDataFormat2(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   protected long readLength(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  79 */     long l = paramTdsInputStream.readUnsignedIntAsLong();
/*  80 */     return l;
/*     */   }
/*     */ 
/*     */   protected String getLabel(int paramInt)
/*     */     throws SQLException
/*     */   {
/*  95 */     return ((RowDataFormat2)getDataFormat(paramInt)).getLabelName();
/*     */   }
/*     */ 
/*     */   protected String getDBOwnerName(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 110 */     return ((RowDataFormat2)getDataFormat(paramInt)).getSchemaName();
/*     */   }
/*     */ 
/*     */   public String getColumnLabel(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 120 */     int i = mapColumn(paramInt);
/* 121 */     return getLabel(i);
/*     */   }
/*     */ 
/*     */   public String getColumnName(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 130 */     if ((this._isGetColumnLabelForNameEnabled == -1) && (isGetColumnLabelForNameEnabled()))
/*     */     {
/* 132 */       this._isGetColumnLabelForNameEnabled = 1;
/*     */     }
/* 134 */     if (this._isGetColumnLabelForNameEnabled == 1)
/*     */     {
/* 136 */       return getColumnLabel(paramInt);
/*     */     }
/*     */ 
/* 140 */     return getName(mapColumn(paramInt));
/*     */   }
/*     */ 
/*     */   private boolean isGetColumnLabelForNameEnabled()
/*     */     throws SQLException
/*     */   {
/* 150 */     SybProperty localSybProperty = ((Tds)this._tpc._protocol).getSybProperty();
/* 151 */     return localSybProperty.getBoolean(65);
/*     */   }
/*     */ 
/*     */   public String getSchemaName(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 160 */     int i = mapColumn(paramInt);
/* 161 */     String str = getDBOwnerName(i);
/* 162 */     return str;
/*     */   }
/*     */ 
/*     */   public String getTableName(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 170 */     int i = mapColumn(paramInt);
/* 171 */     return ((RowDataFormat2)getDataFormat(i)).getTableName();
/*     */   }
/*     */ 
/*     */   public String getCatalogName(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 179 */     int i = mapColumn(paramInt);
/* 180 */     return ((RowDataFormat2)getDataFormat(i)).getCatalogName();
/*     */   }
/*     */ 
/*     */   public String getColumnTypeName(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 186 */     int i = mapColumn(paramInt);
/* 187 */     int j = getDataFormat(i)._datatype;
/* 188 */     String str = getCatalogName(paramInt);
/* 189 */     j = super.getColumnDataType(j, i);
/* 190 */     return ((Tds)this._tpc._protocol).getColumnTypeName(j, this._column[i]._usertype, str);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.RowFormat2Token
 * JD-Core Version:    0.5.4
 */