/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvRPCToken extends Token
/*     */   implements SrvDataToken, SrvFormatToken, Dumpable
/*     */ {
/*     */   private int _totalLength;
/*     */   private String _rpcName;
/*     */   private int _options;
/*     */   private TdsInputStream _in;
/*     */   private SrvDataFormat _currentFormat;
/*     */ 
/*     */   public SrvRPCToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  50 */     this._totalLength = paramTdsInputStream.readShort();
/*  51 */     int i = paramTdsInputStream.readUnsignedByte();
/*  52 */     this._rpcName = paramTdsInputStream.readString(i);
/*  53 */     this._options = paramTdsInputStream.readUnsignedShort();
/*  54 */     this._in = paramTdsInputStream;
/*     */   }
/*     */ 
/*     */   public TdsInputStream getStream()
/*     */   {
/*  65 */     return this._in;
/*     */   }
/*     */ 
/*     */   public void setFormatter(SrvTypeFormatter paramSrvTypeFormatter)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addFormat(DataFormat paramDataFormat)
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/*  93 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getFormatCount()
/*     */   {
/* 101 */     return 1;
/*     */   }
/*     */ 
/*     */   public DataFormat formatAt(int paramInt)
/*     */   {
/* 107 */     return this._currentFormat;
/*     */   }
/*     */ 
/*     */   public void sendFormat(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 131 */     DumpInfo localDumpInfo = null;
/*     */ 
/* 133 */     if (paramDumpFilter.includesToken(224))
/*     */     {
/* 135 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 136 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 138 */         localDumpInfo.addInfo("Token", 1, "RPC Token (0x" + HexConverts.hexConvert(224, 1) + ");");
/*     */       }
/*     */       else
/*     */       {
/* 143 */         localDumpInfo.addInfo("Token", 1, "RPC Token");
/*     */       }
/* 145 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 147 */         localDumpInfo.addInt("Length", 2, this._totalLength);
/*     */       }
/* 149 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 152 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 154 */           localDumpInfo.addInt("Name Length", 1, this._rpcName.length());
/*     */         }
/* 156 */         localDumpInfo.addText("Name", this._rpcName.length(), this._rpcName);
/* 157 */         String[] arrayOfString = { "RPC_UNUSED", "RPC_RECOMPILE" };
/*     */ 
/* 162 */         localDumpInfo.addBitfield("Options", 2, this._options, arrayOfString);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 167 */     for (int i = this._totalLength; i > 0; )
/*     */     {
/* 170 */       this._currentFormat = new RPCFormat(this._in);
/* 171 */       i -= this._currentFormat.length();
/*     */ 
/* 174 */       SrvJavaTypeFormatter localSrvJavaTypeFormatter = new SrvJavaTypeFormatter(this, null, false);
/*     */ 
/* 176 */       Object[] arrayOfObject = localSrvJavaTypeFormatter.convertData(this);
/* 177 */       int[] arrayOfInt1 = localSrvJavaTypeFormatter.getDataLengths();
/* 178 */       i -= arrayOfInt1[0];
/*     */ 
/* 180 */       if ((!paramDumpFilter.includesToken(224)) || (
/* 182 */         (!paramDumpFilter.includesDetail(3)) && (!paramDumpFilter.includesDetail(6)))) {
/*     */         continue;
/*     */       }
/*     */ 
/* 186 */       localDumpInfo.addInfo(this._currentFormat.dump(paramDumpFilter));
/*     */ 
/* 189 */       int[] arrayOfInt2 = localSrvJavaTypeFormatter.getLengthSizes();
/* 190 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 193 */         localDumpInfo.addInt("Length", arrayOfInt2[0], arrayOfInt1[0]);
/*     */       }
/* 195 */       localDumpInfo.addValue("Row data", arrayOfInt1[0], arrayOfObject[0]);
/*     */     }
/*     */ 
/* 200 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 208 */     return 224;
/*     */   }
/*     */ 
/*     */   private class RPCFormat extends SrvDataFormat
/*     */     implements Dumpable
/*     */   {
/*     */     private int _maxLength;
/*     */     private int _actualLength;
/*     */ 
/*     */     protected RPCFormat(TdsInputStream arg2)
/*     */       throws IOException
/*     */     {
/*     */       try
/*     */       {
/*     */         Object localObject;
/* 234 */         int i = localObject.readUnsignedByte();
/* 235 */         this._name = localObject.readString(i);
/* 236 */         this._status = localObject.readUnsignedByte();
/* 237 */         this._datatype = localObject.readUnsignedByte();
/*     */ 
/* 242 */         int j = lengthSize(this._datatype);
/* 243 */         if ((this._datatype == 106) || (this._datatype == 108))
/*     */         {
/* 245 */           this._maxLength = localObject.readUnsignedByte();
/* 246 */           this._precision = localObject.readUnsignedByte();
/* 247 */           this._scale = localObject.readUnsignedByte();
/* 248 */           this._actualLength = localObject.readUnsignedByte();
/*     */         }
/* 252 */         else if (j == 1)
/*     */         {
/* 254 */           this._maxLength = localObject.readUnsignedByte();
/* 255 */           this._actualLength = localObject.readUnsignedByte();
/*     */         }
/* 257 */         else if (j == 4)
/*     */         {
/* 259 */           this._maxLength = localObject.readInt();
/* 260 */           this._actualLength = localObject.readInt();
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 266 */         readSQE(localIOException);
/*     */       }
/*     */     }
/*     */ 
/*     */     public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */       throws IOException
/*     */     {
/* 285 */       DumpInfo localDumpInfo = paramDumpFilter.getDumpInfo();
/* 286 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(7)))
/*     */       {
/* 289 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 291 */           localDumpInfo.addInt("Name Length", 1, this._name.length());
/*     */         }
/* 293 */         localDumpInfo.addText("Name", this._name.length(), this._name);
/* 294 */         String[] arrayOfString = { "RPC_STATUS_UNUSED", "RPC_OUTPUT", "RPC_NODEF" };
/*     */ 
/* 299 */         localDumpInfo.addBitfield("Status", 1, this._status, arrayOfString);
/* 300 */         localDumpInfo.addInfo("Data Type", 1, getDataTypeString(this._datatype));
/* 301 */         if ((paramDumpFilter.includesDetail(1)) && 
/* 303 */           (lengthSize(this._datatype) > 0))
/*     */         {
/* 305 */           localDumpInfo.addInt("Maximum Length", lengthSize(this._datatype), this._maxLength);
/*     */         }
/*     */ 
/* 309 */         if ((this._datatype == 106) || (this._datatype == 108))
/*     */         {
/* 311 */           localDumpInfo.addHex("Precision", 1, this._precision);
/* 312 */           localDumpInfo.addHex("Scale", 1, this._scale);
/*     */         }
/* 314 */         if ((paramDumpFilter.includesDetail(1)) && 
/* 316 */           (lengthSize(this._datatype) > 0))
/*     */         {
/* 318 */           localDumpInfo.addInt("Actual Length", lengthSize(this._datatype), this._actualLength);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 323 */       return localDumpInfo;
/*     */     }
/*     */ 
/*     */     public int getType()
/*     */     {
/* 331 */       return -1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvRPCToken
 * JD-Core Version:    0.5.4
 */