/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class SrvSQLTypeFormatter2 extends SrvSQLTypeFormatter
/*     */ {
/*     */   public SrvSQLTypeFormatter2(SrvFormatToken paramSrvFormatToken, SrvCapabilityToken paramSrvCapabilityToken)
/*     */   {
/*  47 */     super(paramSrvFormatToken, paramSrvCapabilityToken);
/*     */   }
/*     */ 
/*     */   public DataFormat[] buildDataFormat(ResultSet paramResultSet)
/*     */     throws IOException
/*     */   {
/*  59 */     SrvRowDataFormat2[] arrayOfSrvRowDataFormat2 = null;
/*     */     try
/*     */     {
/*  64 */       ResultSetMetaData localResultSetMetaData = paramResultSet.getMetaData();
/*  65 */       int j = localResultSetMetaData.getColumnCount();
/*  66 */       if (j == 0)
/*     */       {
/*  68 */         throw new SrvTypeException("Cannot format result set with  0 columns");
/*     */       }
/*     */ 
/*  71 */       arrayOfSrvRowDataFormat2 = new SrvRowDataFormat2[j];
/*     */ 
/*  74 */       for (int k = 1; k <= j; ++k)
/*     */       {
/*  77 */         Object localObject = _formatmap.get(new Integer(localResultSetMetaData.getColumnType(k)));
/*     */ 
/*  79 */         if (localObject == null)
/*     */         {
/*  81 */           throw new SrvTypeException("Cannot map JDBC type " + localResultSetMetaData.getColumnTypeName(k) + " to TDS type");
/*     */         }
/*     */ 
/*  88 */         int[] arrayOfInt = (int[])localObject;
/*  89 */         if (this._cap.handlesDataType(arrayOfInt[1]))
/*     */         {
/*  92 */           int l = 0;
/*     */ 
/*  98 */           if (localResultSetMetaData.isNullable(k) == 1)
/*     */           {
/* 101 */             l |= 32;
/*     */           }
/*     */ 
/* 104 */           if (localResultSetMetaData.isWritable(k))
/*     */           {
/* 106 */             l |= 16;
/*     */           }
/*     */ 
/* 118 */           String str1 = localResultSetMetaData.getColumnLabel(k);
/* 119 */           String str2 = localResultSetMetaData.getCatalogName(k);
/* 120 */           String str3 = localResultSetMetaData.getSchemaName(k);
/* 121 */           String str4 = localResultSetMetaData.getTableName(k);
/*     */           int i;
/* 127 */           switch (arrayOfInt[1])
/*     */           {
/*     */           case 39:
/*     */           case 47:
/*     */           case 175:
/* 132 */             i = localResultSetMetaData.getColumnDisplaySize(k);
/* 133 */             break;
/*     */           case 37:
/*     */           case 45:
/*     */           case 225:
/* 138 */             i = localResultSetMetaData.getColumnDisplaySize(k) / 2;
/* 139 */             break;
/*     */           default:
/* 142 */             i = arrayOfInt[2];
/*     */           }
/*     */ 
/* 145 */           arrayOfSrvRowDataFormat2[(k - 1)] = new SrvRowDataFormat2(str1, str2, str3, str4, localResultSetMetaData.getColumnName(k), arrayOfInt[1], l, i, localResultSetMetaData.getPrecision(k), localResultSetMetaData.getScale(k), null);
/*     */         }
/*     */         else
/*     */         {
/* 160 */           throw new SrvTypeException("Not handling client literal  conversion for JDBC types " + localResultSetMetaData.getColumnTypeName(k));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 171 */       throw new SrvTypeException("SQL to TDS conversion error: " + localSQLException);
/*     */     }
/* 173 */     return arrayOfSrvRowDataFormat2;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvSQLTypeFormatter2
 * JD-Core Version:    0.5.4
 */