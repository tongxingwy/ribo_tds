/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvParamFormatToken extends ParamFormatToken
/*     */   implements SrvFormatToken, Dumpable
/*     */ {
/*     */   protected Vector _dataformats;
/*     */   private long _len;
/*     */   protected int _numParams;
/*     */ 
/*     */   public SrvParamFormatToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  55 */     this._dataformats = new Vector();
/*  56 */     this._len = readLength(paramTdsInputStream);
/*  57 */     this._numParams = paramTdsInputStream.readShort();
/*  58 */     addDataFormats(paramTdsInputStream, this._numParams);
/*     */   }
/*     */ 
/*     */   public SrvParamFormatToken()
/*     */   {
/*  66 */     this._dataformats = new Vector();
/*     */   }
/*     */ 
/*     */   public void addDataFormats(TdsInputStream paramTdsInputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/*  78 */     for (int i = 0; i < paramInt; ++i)
/*     */     {
/*  80 */       this._dataformats.addElement(dataFormatFactory(paramTdsInputStream));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected DataFormat dataFormatFactory(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  92 */     return new SrvDataFormat(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 101 */     int i = 2;
/* 102 */     for (int j = 0; j < this._dataformats.size(); ++j)
/*     */     {
/* 104 */       SrvDataFormat localSrvDataFormat = (SrvDataFormat)this._dataformats.elementAt(j);
/* 105 */       i += localSrvDataFormat.length();
/*     */     }
/* 107 */     return i;
/*     */   }
/*     */ 
/*     */   public void addFormat(DataFormat paramDataFormat)
/*     */   {
/* 120 */     this._dataformats.addElement(paramDataFormat);
/*     */   }
/*     */ 
/*     */   public int getFormatCount()
/*     */   {
/* 130 */     return this._dataformats.size();
/*     */   }
/*     */ 
/*     */   public DataFormat formatAt(int paramInt)
/*     */   {
/* 139 */     return (SrvDataFormat)this._dataformats.elementAt(paramInt);
/*     */   }
/*     */ 
/*     */   public void sendFormat(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 149 */     for (int i = 0; i < this._dataformats.size(); ++i)
/*     */     {
/* 151 */       SrvDataFormat localSrvDataFormat = (SrvDataFormat)this._dataformats.elementAt(i);
/* 152 */       localSrvDataFormat.send(paramTdsOutputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getByteLength(int paramInt)
/*     */   {
/* 161 */     DataFormat localDataFormat = formatAt(paramInt);
/* 162 */     return SrvDataFormat.lengthSize(localDataFormat._datatype);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 173 */     DumpInfo localDumpInfo = null;
/* 174 */     if (paramDumpFilter.includesToken(236))
/*     */     {
/* 176 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 177 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 179 */         localDumpInfo.addInfo("Token", 1, "PARAMFMT Token (0x" + HexConverts.hexConvert(236, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 185 */         localDumpInfo.addInfo("Token", 1, "PARAMFMT Token");
/*     */       }
/* 187 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 189 */         localDumpInfo.addInt("Length", 2, this._len);
/*     */       }
/* 191 */       localDumpInfo.addInt("Number of Params", 2, this._numParams);
/* 192 */       for (int i = 0; i < getFormatCount(); ++i)
/*     */       {
/* 194 */         localDumpInfo.addInfo("TDSFmt", 0, "Param " + (i + 1));
/* 195 */         DataFormat localDataFormat = formatAt(i);
/* 196 */         ((SrvDataFormat)localDataFormat).setParentTokenType(getTokenType());
/* 197 */         localDumpInfo.addInfo(((SrvDataFormat)localDataFormat).dump(paramDumpFilter));
/*     */       }
/*     */     }
/* 200 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 209 */     return 236;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvParamFormatToken
 * JD-Core Version:    0.5.4
 */