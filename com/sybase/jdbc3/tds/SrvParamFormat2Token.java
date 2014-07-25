/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvParamFormat2Token extends ParamFormat2Token
/*     */   implements SrvFormatToken, Dumpable
/*     */ {
/*  35 */   protected Vector _dataformats = null;
/*     */   private long _len;
/*     */ 
/*     */   public SrvParamFormat2Token(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  54 */     this._dataformats = new Vector();
/*  55 */     this._len = readLength(paramTdsInputStream);
/*  56 */     this._numParams = paramTdsInputStream.readShort();
/*  57 */     addDataFormats(paramTdsInputStream, this._numParams);
/*     */   }
/*     */ 
/*     */   public SrvParamFormat2Token()
/*     */   {
/*  65 */     this._dataformats = new Vector();
/*     */   }
/*     */ 
/*     */   public void addDataFormats(TdsInputStream paramTdsInputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/*  77 */     for (int i = 0; i < paramInt; ++i)
/*     */     {
/*  79 */       this._dataformats.addElement(dataFormatFactory(paramTdsInputStream));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected DataFormat dataFormatFactory(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  91 */     return new SrvParamDataFormat2(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 100 */     long l = 2L;
/* 101 */     for (int i = 0; i < this._dataformats.size(); ++i)
/*     */     {
/* 103 */       SrvDataFormat localSrvDataFormat = (SrvDataFormat)this._dataformats.elementAt(i);
/* 104 */       l += localSrvDataFormat.length();
/*     */     }
/* 106 */     return l;
/*     */   }
/*     */ 
/*     */   public void addFormat(DataFormat paramDataFormat)
/*     */   {
/* 119 */     this._dataformats.addElement(paramDataFormat);
/*     */   }
/*     */ 
/*     */   public int getFormatCount()
/*     */   {
/* 129 */     return this._dataformats.size();
/*     */   }
/*     */ 
/*     */   public DataFormat formatAt(int paramInt)
/*     */   {
/* 138 */     return (SrvParamDataFormat2)this._dataformats.elementAt(paramInt);
/*     */   }
/*     */ 
/*     */   public void sendFormat(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 148 */     for (int i = 0; i < this._dataformats.size(); ++i)
/*     */     {
/* 150 */       SrvParamDataFormat2 localSrvParamDataFormat2 = (SrvParamDataFormat2)this._dataformats.elementAt(i);
/*     */ 
/* 152 */       localSrvParamDataFormat2.send(paramTdsOutputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getByteLength(int paramInt)
/*     */   {
/* 161 */     SrvParamDataFormat2 localSrvParamDataFormat2 = (SrvParamDataFormat2)formatAt(paramInt);
/* 162 */     return SrvParamDataFormat2.lengthSize(localSrvParamDataFormat2._datatype);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 173 */     DumpInfo localDumpInfo = null;
/* 174 */     if (paramDumpFilter.includesToken(32))
/*     */     {
/* 176 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 177 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 179 */         localDumpInfo.addInfo("Token", 1, "PARAMFMT2 Token (0x" + HexConverts.hexConvert(32, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 185 */         localDumpInfo.addInfo("Token", 1, "PARAMFMT2 Token");
/*     */       }
/* 187 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 191 */         localDumpInfo.addInt("Length", 4, this._len);
/*     */       }
/* 193 */       localDumpInfo.addInt("Number of Params", 2, this._numParams);
/* 194 */       for (int i = 0; i < getFormatCount(); ++i)
/*     */       {
/* 196 */         localDumpInfo.addInfo("TDSFmt", 0, "Param " + (i + 1));
/* 197 */         SrvParamDataFormat2 localSrvParamDataFormat2 = (SrvParamDataFormat2)formatAt(i);
/* 198 */         localDumpInfo.addInfo(localSrvParamDataFormat2.dump(paramDumpFilter));
/*     */       }
/*     */     }
/* 201 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 210 */     return 32;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvParamFormat2Token
 * JD-Core Version:    0.5.4
 */