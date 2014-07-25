/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvRowFormat2Token extends RowFormat2Token
/*     */   implements SrvFormatToken, Dumpable
/*     */ {
/*     */   protected Vector _dataformats;
/*     */   protected long _len;
/*     */ 
/*     */   public SrvRowFormat2Token(TdsInputStream paramTdsInputStream)
/*     */     throws IOException, SQLException
/*     */   {
/*  52 */     this._dataformats = new Vector();
/*  53 */     this._len = readLength(paramTdsInputStream);
/*  54 */     this._numColumns = paramTdsInputStream.readShort();
/*  55 */     addDataFormats(paramTdsInputStream, this._numColumns);
/*     */   }
/*     */ 
/*     */   public SrvRowFormat2Token()
/*     */     throws SQLException
/*     */   {
/*  66 */     this._dataformats = new Vector();
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*  76 */     paramTdsOutputStream.writeByte(97);
/*  77 */     paramTdsOutputStream.writeLongAsUnsignedInt(getLength());
/*  78 */     paramTdsOutputStream.writeShort(this._dataformats.size());
/*  79 */     for (int i = 0; i < this._dataformats.size(); ++i)
/*     */     {
/*  81 */       SrvRowDataFormat2 localSrvRowDataFormat2 = (SrvRowDataFormat2)this._dataformats.elementAt(i);
/*     */ 
/*  83 */       localSrvRowDataFormat2.send(paramTdsOutputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addDataFormats(TdsInputStream paramTdsInputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/*  94 */     for (int i = 0; i < paramInt; ++i)
/*  95 */       this._dataformats.addElement(dataFormatFactory(paramTdsInputStream));
/*     */   }
/*     */ 
/*     */   protected DataFormat dataFormatFactory(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/* 106 */     return new SrvRowDataFormat2(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 116 */     long l = 2L;
/* 117 */     for (int i = 0; i < this._dataformats.size(); ++i)
/*     */     {
/* 119 */       SrvRowDataFormat2 localSrvRowDataFormat2 = (SrvRowDataFormat2)this._dataformats.elementAt(i);
/*     */ 
/* 121 */       l += localSrvRowDataFormat2.length();
/*     */     }
/* 123 */     return l;
/*     */   }
/*     */ 
/*     */   public void addFormat(DataFormat paramDataFormat)
/*     */   {
/* 136 */     this._dataformats.addElement(paramDataFormat);
/*     */   }
/*     */ 
/*     */   public int getFormatCount()
/*     */   {
/* 145 */     return this._dataformats.size();
/*     */   }
/*     */ 
/*     */   public DataFormat formatAt(int paramInt)
/*     */   {
/* 155 */     return getDataFormat(paramInt);
/*     */   }
/*     */ 
/*     */   protected DataFormat getDataFormat(int paramInt)
/*     */   {
/* 160 */     return (SrvRowDataFormat2)this._dataformats.elementAt(paramInt);
/*     */   }
/*     */ 
/*     */   public void sendFormat(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 170 */     for (int i = 0; i < this._dataformats.size(); ++i)
/*     */     {
/* 172 */       SrvRowDataFormat2 localSrvRowDataFormat2 = (SrvRowDataFormat2)this._dataformats.elementAt(i);
/*     */ 
/* 174 */       localSrvRowDataFormat2.send(paramTdsOutputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getByteLength(int paramInt)
/*     */   {
/* 183 */     DataFormat localDataFormat = formatAt(paramInt);
/* 184 */     return SrvRowDataFormat2.lengthSize(localDataFormat._datatype);
/*     */   }
/*     */ 
/*     */   public boolean hasKeyColumns()
/*     */   {
/* 192 */     int i = 0;
/* 193 */     for (int j = 0; j < this._dataformats.size(); ++j)
/*     */     {
/* 195 */       SrvRowDataFormat2 localSrvRowDataFormat2 = (SrvRowDataFormat2)this._dataformats.elementAt(j);
/*     */ 
/* 197 */       if ((localSrvRowDataFormat2._status & 0x2) == 0)
/*     */         continue;
/* 199 */       i = 1;
/* 200 */       break;
/*     */     }
/*     */ 
/* 203 */     return i;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 213 */     DumpInfo localDumpInfo = null;
/* 214 */     if (paramDumpFilter.includesToken(97))
/*     */     {
/* 216 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 217 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 219 */         localDumpInfo.addInfo("Token", 1, "ROWFMT2 Token (0x" + HexConverts.hexConvert(97, 1) + ");");
/*     */       }
/*     */       else
/*     */       {
/* 224 */         localDumpInfo.addInfo("Token", 1, "ROWFMT2 Token");
/*     */       }
/* 226 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 230 */         localDumpInfo.addInt("Length", 4, this._len);
/*     */       }
/* 232 */       localDumpInfo.addInt("Number of Columns", 2, this._numColumns);
/* 233 */       for (int i = 0; i < getFormatCount(); ++i)
/*     */       {
/* 235 */         localDumpInfo.addInfo("TDSFmt", 0, "Column " + (i + 1));
/* 236 */         DataFormat localDataFormat = formatAt(i);
/* 237 */         localDumpInfo.addInfo(((SrvRowDataFormat2)localDataFormat).dump(paramDumpFilter));
/*     */       }
/*     */     }
/* 240 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 249 */     return 97;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvRowFormat2Token
 * JD-Core Version:    0.5.4
 */