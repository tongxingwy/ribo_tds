/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvRowFormatToken extends RowFormatToken
/*     */   implements SrvFormatToken, Dumpable
/*     */ {
/*     */   protected Vector _dataformats;
/*     */   protected int _len;
/*     */ 
/*     */   public SrvRowFormatToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException, SQLException
/*     */   {
/*  53 */     this._dataformats = new Vector();
/*  54 */     this._len = (int)readLength(paramTdsInputStream);
/*  55 */     this._numColumns = paramTdsInputStream.readShort();
/*  56 */     addDataFormats(paramTdsInputStream, this._numColumns);
/*     */   }
/*     */ 
/*     */   public SrvRowFormatToken()
/*     */     throws SQLException
/*     */   {
/*  67 */     this._dataformats = new Vector();
/*     */   }
/*     */ 
/*     */   protected void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*  78 */     paramTdsOutputStream.writeByte(238);
/*  79 */     paramTdsOutputStream.writeShort((int)getLength());
/*  80 */     paramTdsOutputStream.writeShort(this._dataformats.size());
/*  81 */     for (int i = 0; i < this._dataformats.size(); ++i)
/*     */     {
/*  83 */       SrvDataFormat localSrvDataFormat = (SrvDataFormat)this._dataformats.elementAt(i);
/*  84 */       localSrvDataFormat.send(paramTdsOutputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addDataFormats(TdsInputStream paramTdsInputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/*  95 */     for (int i = 0; i < paramInt; ++i)
/*  96 */       this._dataformats.addElement(dataFormatFactory(paramTdsInputStream));
/*     */   }
/*     */ 
/*     */   protected DataFormat dataFormatFactory(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/* 107 */     return new SrvDataFormat(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 117 */     int i = 2;
/* 118 */     for (int j = 0; j < this._dataformats.size(); ++j)
/*     */     {
/* 120 */       SrvDataFormat localSrvDataFormat = (SrvDataFormat)this._dataformats.elementAt(j);
/* 121 */       i += localSrvDataFormat.length();
/*     */     }
/* 123 */     return i;
/*     */   }
/*     */ 
/*     */   public void addFormat(DataFormat paramDataFormat)
/*     */   {
/* 137 */     this._dataformats.addElement(paramDataFormat);
/*     */   }
/*     */ 
/*     */   public int getFormatCount()
/*     */   {
/* 146 */     return this._dataformats.size();
/*     */   }
/*     */ 
/*     */   public DataFormat formatAt(int paramInt)
/*     */   {
/* 156 */     return (SrvDataFormat)getDataFormat(paramInt);
/*     */   }
/*     */ 
/*     */   protected DataFormat getDataFormat(int paramInt)
/*     */   {
/* 161 */     return (SrvDataFormat)this._dataformats.elementAt(paramInt);
/*     */   }
/*     */ 
/*     */   public void sendFormat(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 172 */     for (int i = 0; i < this._dataformats.size(); ++i)
/*     */     {
/* 174 */       SrvDataFormat localSrvDataFormat = (SrvDataFormat)this._dataformats.elementAt(i);
/* 175 */       localSrvDataFormat.send(paramTdsOutputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getByteLength(int paramInt)
/*     */   {
/* 184 */     DataFormat localDataFormat = formatAt(paramInt);
/* 185 */     return SrvDataFormat.lengthSize(localDataFormat._datatype);
/*     */   }
/*     */ 
/*     */   public boolean hasKeyColumns()
/*     */   {
/* 193 */     int i = 0;
/* 194 */     for (int j = 0; j < this._dataformats.size(); ++j)
/*     */     {
/* 196 */       SrvDataFormat localSrvDataFormat = (SrvDataFormat)this._dataformats.elementAt(j);
/* 197 */       if ((localSrvDataFormat._status & 0x2) == 0)
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
/* 214 */     if (paramDumpFilter.includesToken(238))
/*     */     {
/* 216 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 217 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 219 */         localDumpInfo.addInfo("Token", 1, "ROWFMT Token (0x" + HexConverts.hexConvert(238, 1) + ");");
/*     */       }
/*     */       else
/*     */       {
/* 224 */         localDumpInfo.addInfo("Token", 1, "ROWFMT Token");
/*     */       }
/* 226 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 228 */         localDumpInfo.addInt("Length", 2, this._len);
/*     */       }
/* 230 */       localDumpInfo.addInt("Number of Columns", 2, this._numColumns);
/* 231 */       for (int i = 0; i < getFormatCount(); ++i)
/*     */       {
/* 233 */         localDumpInfo.addInfo("TDSFmt", 0, "Column " + (i + 1));
/* 234 */         DataFormat localDataFormat = formatAt(i);
/* 235 */         ((SrvDataFormat)localDataFormat).setParentTokenType(getTokenType());
/* 236 */         localDumpInfo.addInfo(((SrvDataFormat)localDataFormat).dump(paramDumpFilter));
/*     */       }
/*     */     }
/* 239 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 248 */     return 238;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvRowFormatToken
 * JD-Core Version:    0.5.4
 */