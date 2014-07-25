/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*      */ import com.sybase.jdbc3.jdbc.SybConnection;
/*      */ import com.sybase.jdbc3.jdbc.SybDatabaseMetaData;
/*      */ import com.sybase.jdbc3.jdbc.SybProperty;
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import java.io.IOException;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ 
/*      */ public class RowFormatToken extends Token
/*      */   implements ResultSetMetaData
/*      */ {
/*      */   public static final int IMAGE_COLUMN_DISPLAY_SIZE = 1;
/*      */   protected int _numColumns;
/*      */   protected long _length;
/*      */   protected int _numUserColumns;
/*      */   protected int[] _userColumnMap;
/*      */   protected DataFormat[] _column;
/*   50 */   protected TdsProtocolContext _tpc = null;
/*      */ 
/*      */   public RowFormatToken(int paramInt)
/*      */     throws SQLException
/*      */   {
/*   59 */     this._numUserColumns = (this._numColumns = paramInt);
/*   60 */     this._column = new DataFormat[this._numColumns];
/*      */   }
/*      */ 
/*      */   public RowFormatToken()
/*      */   {
/*   68 */     this._numColumns = 0;
/*   69 */     this._column = new DataFormat[this._numColumns];
/*      */   }
/*      */ 
/*      */   public RowFormatToken(TdsInputStream paramTdsInputStream)
/*      */     throws IOException
/*      */   {
/*   80 */     readLength(paramTdsInputStream);
/*   81 */     this._numColumns = paramTdsInputStream.readShort();
/*   82 */     addDataFormats(paramTdsInputStream, this._numColumns);
/*      */   }
/*      */ 
/*      */   public RowFormatToken(TdsInputStream paramTdsInputStream, long paramLong)
/*      */     throws IOException
/*      */   {
/*   88 */     this._length = paramLong;
/*   89 */     this._numColumns = paramTdsInputStream.readShort();
/*   90 */     addDataFormats(paramTdsInputStream, this._numColumns);
/*      */   }
/*      */ 
/*      */   public long getRowFmtLength()
/*      */   {
/*   95 */     return this._length;
/*      */   }
/*      */ 
/*      */   public void addDataFormats(TdsInputStream paramTdsInputStream, int paramInt)
/*      */     throws IOException
/*      */   {
/*  105 */     this._column = new DataFormat[paramInt];
/*  106 */     this._numUserColumns = 0;
/*  107 */     prepareUserColMap(paramInt);
/*  108 */     for (int i = 0; i < paramInt; ++i)
/*      */     {
/*  110 */       this._column[i] = dataFormatFactory(paramTdsInputStream);
/*  111 */       if ((this._column[i]._status & 0x1) != 0)
/*      */         continue;
/*  113 */       this._userColumnMap[this._numUserColumns] = i;
/*  114 */       this._numUserColumns += 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected DataFormat dataFormatFactory(TdsInputStream paramTdsInputStream)
/*      */     throws IOException
/*      */   {
/*  131 */     return new DataFormat(paramTdsInputStream);
/*      */   }
/*      */ 
/*      */   protected long readLength(TdsInputStream paramTdsInputStream)
/*      */     throws IOException
/*      */   {
/*  141 */     long l = paramTdsInputStream.readShort();
/*  142 */     this._length = l;
/*  143 */     return l;
/*      */   }
/*      */ 
/*      */   protected void prepareUserColMap(int paramInt)
/*      */   {
/*  156 */     this._userColumnMap = new int[paramInt];
/*      */   }
/*      */ 
/*      */   protected DataFormat getDataFormat(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  176 */     return this._column[paramInt];
/*      */   }
/*      */ 
/*      */   protected String getName(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  190 */     return getDataFormat(paramInt).getName();
/*      */   }
/*      */ 
/*      */   protected String getLabel(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  205 */     return getName(paramInt);
/*      */   }
/*      */ 
/*      */   protected int getStatus(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  219 */     return getDataFormat(paramInt)._status;
/*      */   }
/*      */ 
/*      */   protected int getDatatype(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  233 */     return getDataFormat(paramInt)._datatype;
/*      */   }
/*      */ 
/*      */   public int getLength(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  247 */     return getDataFormat(paramInt)._length;
/*      */   }
/*      */ 
/*      */   private boolean isFakeMetadataEnabled()
/*      */     throws SQLException
/*      */   {
/*  256 */     SybProperty localSybProperty = ((Tds)this._tpc._protocol).getSybProperty();
/*  257 */     return localSybProperty.getBoolean(39);
/*      */   }
/*      */ 
/*      */   public int getPrecision(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  267 */     int i = mapColumn(paramInt);
/*  268 */     switch (getDataFormat(i)._datatype)
/*      */     {
/*      */     case 48:
/*  271 */       return 3;
/*      */     case 52:
/*      */     case 65:
/*  274 */       return 5;
/*      */     case 56:
/*      */     case 66:
/*  277 */       return 10;
/*      */     case 67:
/*  279 */       return 20;
/*      */     case 191:
/*  281 */       return 19;
/*      */     case 38:
/*      */     case 68:
/*  284 */       switch (getDataFormat(i)._length)
/*      */       {
/*      */       case 1:
/*  287 */         return 3;
/*      */       case 2:
/*  289 */         return 5;
/*      */       case 4:
/*  291 */         return 10;
/*      */       case 8:
/*  293 */         if (getDataFormat(i)._datatype == 38) {
/*  294 */           return 19; } return 20;
/*      */       case 3:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7: } return 10;
/*      */     case 59:
/*  301 */       return 7;
/*      */     case 62:
/*  303 */       return 15;
/*      */     case 109:
/*  305 */       switch (getDataFormat(i)._length)
/*      */       {
/*      */       case 4:
/*  308 */         return 7;
/*      */       case 8:
/*  310 */         return 15;
/*      */       }
/*  312 */       return 7;
/*      */     case 122:
/*  315 */       return 10;
/*      */     case 60:
/*  317 */       return 19;
/*      */     case 110:
/*  319 */       switch (getDataFormat(i)._length)
/*      */       {
/*      */       case 4:
/*  322 */         return 10;
/*      */       case 8:
/*  324 */         return 19;
/*      */       }
/*  326 */       return 19;
/*      */     case 51:
/*      */     case 61:
/*      */     case 147:
/*  331 */       return 3;
/*      */     case 187:
/*      */     case 188:
/*  334 */       return 6;
/*      */     case 111:
/*  336 */       if ((getDataFormat(i)._length == 8) && (getDataFormat(i)._usertype != 50))
/*      */       {
/*  339 */         return 3;
/*      */       }
/*      */ 
/*  343 */       return 0;
/*      */     }
/*      */ 
/*  346 */     return getDataFormat(i)._precision;
/*      */   }
/*      */ 
/*      */   public int getScale(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  357 */     int i = mapColumn(paramInt);
/*  358 */     return getDataFormat(i)._scale;
/*      */   }
/*      */ 
/*      */   public int getColumnCount()
/*      */     throws SQLException
/*      */   {
/*  373 */     return this._numUserColumns;
/*      */   }
/*      */ 
/*      */   public boolean isAutoIncrement(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  382 */     return (getDataFormat(mapColumn(paramInt))._status & 0x40) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isCaseSensitive(int paramInt)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  397 */       return ((SybDatabaseMetaData)this._tpc._conn.getMetaData()).isCaseSensitive();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  403 */       throw localSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isSearchable(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  413 */     Object localObject1 = null;
/*  414 */     Object localObject2 = null;
/*      */ 
/*  416 */     --paramInt;
/*  417 */     int i = sqlTypeToJdbcType(getDataFormat(paramInt), false);
/*      */ 
/*  420 */     return ((Tds)this._tpc._protocol).isTypeSearchable(i);
/*      */   }
/*      */ 
/*      */   public boolean isCurrency(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  429 */     int i = mapColumn(paramInt);
/*  430 */     switch (getDataFormat(i)._datatype)
/*      */     {
/*      */     case 60:
/*      */     case 110:
/*      */     case 122:
/*  435 */       return true;
/*      */     }
/*  437 */     return false;
/*      */   }
/*      */ 
/*      */   public int isNullable(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  446 */     int i = mapColumn(paramInt);
/*  447 */     return ((getDataFormat(i)._status & 0x20) == 0) ? 0 : 1;
/*      */   }
/*      */ 
/*      */   public boolean isSigned(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  457 */     int i = mapColumn(paramInt);
/*  458 */     switch (getDataFormat(i)._datatype)
/*      */     {
/*      */     case 38:
/*      */     case 48:
/*      */     case 52:
/*      */     case 56:
/*      */     case 59:
/*      */     case 60:
/*      */     case 62:
/*      */     case 106:
/*      */     case 108:
/*      */     case 109:
/*      */     case 110:
/*      */     case 122:
/*      */     case 191:
/*  474 */       return true;
/*      */     }
/*      */ 
/*  500 */     return false;
/*      */   }
/*      */ 
/*      */   public int getColumnDisplaySize(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  508 */     DataFormat localDataFormat = getDataFormat(mapColumn(paramInt));
/*  509 */     switch (localDataFormat._datatype)
/*      */     {
/*  535 */     case 48:
/*  512 */       return 3;
/*      */     case 52:
/*      */     case 65:
/*  515 */       return 6;
/*      */     case 56:
/*      */     case 66:
/*  518 */       return 11;
/*      */     case 67:
/*      */     case 191:
/*  521 */       return 20;
/*      */     case 38:
/*      */     case 68:
/*  524 */       switch (localDataFormat._length) { case 1:
/*  527 */         return 3;
/*      */       case 2:
/*  529 */         return 6;
/*      */       case 4:
/*  531 */         return 11;
/*      */       case 8:
/*  533 */         return 20;
/*      */       case 3:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7: } return 11;
/*      */     case 59:
/*  538 */       return 46;
/*      */     case 62:
/*  540 */       return 85;
/*      */     case 109:
/*  542 */       switch (localDataFormat._length)
/*      */       {
/*      */       case 4:
/*  545 */         return 46;
/*      */       case 8:
/*  547 */         return 85;
/*      */       }
/*  549 */       return 46;
/*      */     case 58:
/*      */     case 61:
/*      */     case 111:
/*  554 */       return 25;
/*      */     case 187:
/*  556 */       return 29;
/*      */     case 188:
/*  558 */       return 15;
/*      */     case 49:
/*      */     case 123:
/*  561 */       return 10;
/*      */     case 51:
/*      */     case 147:
/*  564 */       return 8;
/*      */     case 122:
/*  566 */       return 12;
/*      */     case 60:
/*  568 */       return 21;
/*      */     case 110:
/*  570 */       switch (localDataFormat._length)
/*      */       {
/*      */       case 4:
/*  573 */         return 12;
/*      */       case 8:
/*  575 */         return 21;
/*      */       }
/*  577 */       return 21;
/*      */     case 106:
/*      */     case 108:
/*  581 */       return localDataFormat._precision + 2;
/*      */     case 37:
/*      */     case 45:
/*  585 */       return localDataFormat._length * 2;
/*      */     case 225:
/*  588 */       if ((localDataFormat._usertype == 34) || (localDataFormat._usertype == 35))
/*      */       {
/*  593 */         return localDataFormat._length / 2;
/*      */       }
/*      */ 
/*  597 */       return localDataFormat._length * 2;
/*      */     case 34:
/*      */     case 36:
/*  601 */       return 1;
/*      */     case 35:
/*      */     case 39:
/*      */     case 47:
/*      */     case 50:
/*      */     case 103:
/*      */     case 104:
/*      */     case 175:
/*  609 */       return localDataFormat._length;
/*      */     case 174:
/*  620 */       return localDataFormat._length / 2;
/*      */     }
/*  622 */     ErrorMessage.raiseError("JZ0P4");
/*      */ 
/*  625 */     return -1;
/*      */   }
/*      */ 
/*      */   public String getColumnLabel(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  634 */     return getColumnName(paramInt);
/*      */   }
/*      */ 
/*      */   public String getColumnName(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  643 */     return getName(mapColumn(paramInt));
/*      */   }
/*      */ 
/*      */   public String getSchemaName(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  651 */     mapColumn(paramInt);
/*      */ 
/*  654 */     if (!isFakeMetadataEnabled())
/*      */     {
/*  656 */       Debug.notImplemented(this, "getSchemaName");
/*      */     }
/*      */ 
/*  659 */     return "";
/*      */   }
/*      */ 
/*      */   public String getTableName(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  669 */     int i = mapColumn(paramInt);
/*  670 */     switch (getDataFormat(i)._datatype)
/*      */     {
/*      */     case 34:
/*      */     case 35:
/*      */     case 174:
/*  675 */       return getDataFormat(i)._tableName;
/*      */     }
/*      */ 
/*  679 */     if (!isFakeMetadataEnabled())
/*      */     {
/*  681 */       Debug.notImplemented(this, "getTableName");
/*      */     }
/*      */ 
/*  684 */     return "";
/*      */   }
/*      */ 
/*      */   public String getCatalogName(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  693 */     mapColumn(paramInt);
/*      */ 
/*  696 */     if (!isFakeMetadataEnabled())
/*      */     {
/*  698 */       Debug.notImplemented(this, "getCatalogName");
/*      */     }
/*      */ 
/*  701 */     return "";
/*      */   }
/*      */ 
/*      */   public int getColumnType(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  710 */     return sqlTypeToJdbcType(getDataFormat(mapColumn(paramInt)), false);
/*      */   }
/*      */ 
/*      */   public String getColumnTypeName(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  719 */     int i = mapColumn(paramInt);
/*  720 */     int j = getDataFormat(i)._datatype;
/*  721 */     j = getColumnDataType(j, i);
/*  722 */     return ((Tds)this._tpc._protocol).getColumnTypeName(j, this._column[i]._usertype);
/*      */   }
/*      */ 
/*      */   protected int getColumnDataType(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  733 */     switch (paramInt1)
/*      */     {
/*      */     case 37:
/*      */     case 38:
/*      */     case 39:
/*      */     case 68:
/*      */     case 109:
/*      */     case 110:
/*      */     case 111:
/*      */     case 175:
/*      */     case 225:
/*  744 */       paramInt1 = sqlTypeToJdbcType(this._column[paramInt2], true);
/*      */     }
/*      */ 
/*  748 */     return paramInt1;
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  757 */     return (getDataFormat(mapColumn(paramInt))._status & 0x10) == 0;
/*      */   }
/*      */ 
/*      */   public boolean isWritable(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  766 */     return (getDataFormat(mapColumn(paramInt))._status & 0x10) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isDefinitelyWritable(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  775 */     mapColumn(paramInt);
/*      */ 
/*  777 */     return false;
/*      */   }
/*      */ 
/*      */   public String getColumnClassName(int paramInt) throws SQLException
/*      */   {
/*  782 */     return this._column[mapColumn(paramInt)]._className;
/*      */   }
/*      */ 
/*      */   protected int mapColumn(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  793 */     if ((paramInt > this._numUserColumns) || (paramInt < 1))
/*      */     {
/*  795 */       ErrorMessage.raiseError("JZ008", "" + paramInt);
/*      */     }
/*      */ 
/*  798 */     --paramInt;
/*  799 */     return this._userColumnMap[paramInt];
/*      */   }
/*      */ 
/*      */   protected static int sqlTypeToJdbcType(DataFormat paramDataFormat, boolean paramBoolean)
/*      */   {
/*  806 */     switch (paramDataFormat._datatype)
/*      */     {
/*  819 */     case 50:
/*  809 */       return -7;
/*      */     case 68:
/*  811 */       switch (paramDataFormat._length) { case 1:
/*  813 */         return (paramBoolean) ? 48 : -6;
/*      */       case 2:
/*  814 */         return (paramBoolean) ? 65 : 5;
/*      */       case 4:
/*  815 */         return (paramBoolean) ? 66 : 4;
/*      */       case 8:
/*  816 */         return (paramBoolean) ? 67 : -5;
/*      */       case 3:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7: } return 1111;
/*      */     case 38:
/*  821 */       switch (paramDataFormat._length)
/*      */       {
/*      */       case 1:
/*  824 */         if (paramDataFormat._usertype == 16)
/*      */         {
/*  826 */           return (paramBoolean) ? 50 : -7; } 
/*      */ return (paramBoolean) ? 48 : -6;
/*      */       case 2:
/*  832 */         return (paramBoolean) ? 52 : 5;
/*      */       case 4:
/*  833 */         return (paramBoolean) ? 56 : 4;
/*      */       case 8:
/*  834 */         return (paramBoolean) ? 191 : -5;
/*      */       case 3:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7: } return 1111;
/*      */     case 48:
/*  840 */       return -6;
/*      */     case 52:
/*      */     case 65:
/*  843 */       return 5;
/*      */     case 56:
/*      */     case 66:
/*  846 */       return 4;
/*      */     case 67:
/*      */     case 191:
/*  849 */       return -5;
/*      */     case 109:
/*  854 */       switch (paramDataFormat._length)
/*      */       {
/*      */       case 4:
/*  856 */         return (paramBoolean) ? 59 : 7;
/*      */       case 8:
/*  857 */         return (paramBoolean) ? 62 : 8;
/*      */       }
/*      */ 
/*  860 */       return 1111;
/*      */     case 59:
/*  862 */       return 7;
/*      */     case 62:
/*  864 */       return 8;
/*      */     case 60:
/*      */     case 110:
/*      */     case 122:
/*  868 */       if (paramBoolean)
/*  869 */         return (paramDataFormat._length == 4) ? 122 : 60;
/*  870 */       return 3;
/*      */     case 106:
/*      */     case 108:
/*  873 */       if (paramDataFormat._usertype == 26)
/*      */       {
/*  875 */         return 3;
/*      */       }
/*  877 */       return 2;
/*      */     case 39:
/*  879 */       if (paramDataFormat._usertype == 1)
/*      */       {
/*  881 */         return (paramBoolean) ? 47 : 1;
/*      */       }
/*  883 */       if (paramDataFormat._usertype == 25)
/*      */       {
/*  885 */         return 39;
/*      */       }
/*  887 */       return (paramBoolean) ? 39 : 12;
/*      */     case 47:
/*  889 */       return 1;
/*      */     case 175:
/*  891 */       return (paramBoolean) ? 39 : -1;
/*      */     case 35:
/*      */     case 174:
/*  894 */       return -1;
/*      */     case 111:
/*  897 */       switch (paramDataFormat._length)
/*      */       {
/*      */       case 4:
/*  900 */         return (paramBoolean) ? 58 : 93;
/*      */       case 8:
/*  903 */         return (paramBoolean) ? 61 : 93;
/*      */       }
/*      */ 
/*  906 */       return 1111;
/*      */     case 58:
/*      */     case 61:
/*      */     case 187:
/*  910 */       return 93;
/*      */     case 49:
/*      */     case 123:
/*  913 */       return 91;
/*      */     case 51:
/*      */     case 147:
/*      */     case 188:
/*  917 */       return 92;
/*      */     case 45:
/*  919 */       return -2;
/*      */     case 37:
/*  921 */       if (paramDataFormat._usertype == 3)
/*      */       {
/*  923 */         return (paramBoolean) ? 45 : -2;
/*      */       }
/*  925 */       return (paramBoolean) ? 37 : -3;
/*      */     case 225:
/*  928 */       if (paramDataFormat._usertype == 34)
/*      */       {
/*  930 */         return 1;
/*      */       }
/*  932 */       if (paramDataFormat._usertype == 35)
/*      */       {
/*  934 */         return 12;
/*      */       }
/*      */ 
/*  938 */       return (paramBoolean) ? 37 : -4;
/*      */     case 34:
/*  942 */       return -4;
/*      */     case 31:
/*      */     case 103:
/*      */     case 104:
/*  946 */       return 1111;
/*      */     case 36:
/*      */     case 255:
/*  949 */       switch (paramDataFormat._blobType)
/*      */       {
/*      */       case 1:
/*      */       case 2:
/*  953 */         return 2000;
/*      */       case 3:
/*      */       case 5:
/*  956 */         return -1;
/*      */       case 4:
/*  958 */         return -4;
/*      */       case 7:
/*  960 */         return 2004;
/*      */       case 6:
/*  962 */         return 2005;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  971 */     return 1111;
/*      */   }
/*      */ 
/*      */   protected void setPc(TdsProtocolContext paramTdsProtocolContext)
/*      */   {
/*  984 */     this._tpc = paramTdsProtocolContext;
/*      */   }
/*      */ 
/*      */   public boolean isWrapperFor(Class paramClass)
/*      */     throws SQLException
/*      */   {
/*  992 */     return paramClass.isInstance(this);
/*      */   }
/*      */ 
/*      */   public Object unwrap(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 1000 */     RowFormatToken localRowFormatToken = null;
/*      */     try
/*      */     {
/* 1003 */       localRowFormatToken = this;
/*      */     }
/*      */     catch (ClassCastException localClassCastException)
/*      */     {
/* 1007 */       ErrorMessage.raiseError("JZ031", paramClass.getName());
/*      */     }
/*      */ 
/* 1010 */     return localRowFormatToken;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.RowFormatToken
 * JD-Core Version:    0.5.4
 */