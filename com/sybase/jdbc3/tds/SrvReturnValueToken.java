/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvReturnValueToken extends Token
/*     */   implements SrvDataToken, SrvFormatToken, Dumpable
/*     */ {
/*     */   private int _totalLength;
/*     */   private TdsInputStream _in;
/*     */   private SrvDataFormat _currentFormat;
/*     */ 
/*     */   public SrvReturnValueToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  41 */     this._totalLength = paramTdsInputStream.readShort();
/*  42 */     this._currentFormat = new ReturnFormat(paramTdsInputStream);
/*  43 */     this._in = paramTdsInputStream;
/*     */   }
/*     */ 
/*     */   public TdsInputStream getStream()
/*     */   {
/*  54 */     return this._in;
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
/*  82 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getFormatCount()
/*     */   {
/*  90 */     return 1;
/*     */   }
/*     */ 
/*     */   public DataFormat formatAt(int paramInt)
/*     */   {
/*  96 */     return this._currentFormat;
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
/* 119 */     DumpInfo localDumpInfo = null;
/* 120 */     if (paramDumpFilter.includesToken(172))
/*     */     {
/* 122 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 123 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 125 */         localDumpInfo.addInfo("Token", 1, "RETURN_VALUE Token (0x" + HexConverts.hexConvert(172, 1) + ");");
/*     */       }
/*     */       else
/*     */       {
/* 130 */         localDumpInfo.addInfo("Token", 1, "RETURN_VALUE Token");
/*     */       }
/* 132 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 134 */         localDumpInfo.addInt("Length", 2, this._totalLength);
/*     */       }
/*     */ 
/* 138 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(6)))
/*     */       {
/* 141 */         localDumpInfo.addInfo(this._currentFormat.dump(paramDumpFilter));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 146 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = new SrvJavaTypeFormatter(this, null, false);
/*     */ 
/* 148 */     Object[] arrayOfObject = localSrvJavaTypeFormatter.convertData(this);
/* 149 */     if ((paramDumpFilter.includesToken(172)) && ((
/* 151 */       (paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(6)))))
/*     */     {
/* 155 */       int[] arrayOfInt1 = localSrvJavaTypeFormatter.getDataLengths();
/* 156 */       int[] arrayOfInt2 = localSrvJavaTypeFormatter.getLengthSizes();
/* 157 */       localDumpInfo.addInfo("TDSFmt", 0, "Returned Data ");
/* 158 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 161 */         localDumpInfo.addInt("Length", arrayOfInt2[0], arrayOfInt1[0]);
/*     */       }
/* 163 */       localDumpInfo.addValue("Data", arrayOfInt1[0], arrayOfObject[0]);
/*     */     }
/*     */ 
/* 166 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 174 */     return 172;
/*     */   }
/*     */ 
/*     */   private class ReturnFormat extends SrvDataFormat
/*     */     implements Dumpable
/*     */   {
/*     */     protected ReturnFormat(TdsInputStream arg2)
/*     */       throws IOException
/*     */     {
/*     */       try
/*     */       {
/*     */         Object localObject;
/* 193 */         this._nameLen = localObject.readUnsignedByte();
/* 194 */         this._name = localObject.readString(this._nameLen);
/* 195 */         if (this._name == null)
/*     */         {
/* 197 */           this._name = "";
/*     */         }
/* 199 */         this._status = localObject.readUnsignedByte();
/* 200 */         this._usertype = localObject.readInt();
/* 201 */         this._datatype = localObject.readUnsignedByte();
/*     */ 
/* 206 */         int i = lengthSize(this._datatype);
/* 207 */         if ((this._datatype == 106) || (this._datatype == 108))
/*     */         {
/* 209 */           this._length = localObject.readUnsignedByte();
/* 210 */           this._precision = localObject.readUnsignedByte();
/* 211 */           label329: this._scale = localObject.readUnsignedByte();
/*     */         }
/*     */         else
/*     */         {
/* 215 */           if (i == 0);
/* 218 */           switch (this._datatype)
/*     */           {
/*     */           case 48:
/*     */           case 50:
/* 223 */             this._length = 1;
/* 224 */             break;
/*     */           case 52:
/*     */           case 65:
/* 229 */             this._length = 2;
/* 230 */             break;
/*     */           case 56:
/*     */           case 58:
/*     */           case 59:
/*     */           case 66:
/*     */           case 122:
/* 238 */             this._length = 4;
/* 239 */             break;
/*     */           case 60:
/*     */           case 61:
/*     */           case 62:
/*     */           case 67:
/*     */           case 187:
/*     */           case 188:
/*     */           case 191:
/* 249 */             this._length = 8;
/* 250 */             break;
/*     */           default:
/* 252 */             break label329:
/*     */ 
/* 255 */             if (i == 1)
/*     */             {
/* 257 */               this._length = localObject.readUnsignedByte();
/*     */             }
/* 259 */             else if (i == 4)
/*     */             {
/* 261 */               this._length = localObject.readInt();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException) {
/* 267 */         readSQE(localIOException);
/*     */       }
/*     */     }
/*     */ 
/*     */     public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */       throws IOException
/*     */     {
/* 286 */       DumpInfo localDumpInfo = paramDumpFilter.getDumpInfo();
/* 287 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(7)))
/*     */       {
/* 290 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 292 */           localDumpInfo.addInt("Name Length", 1, this._name.length());
/*     */         }
/*     */ 
/* 295 */         localDumpInfo.addText("Name", this._name.length(), this._name);
/* 296 */         String[] arrayOfString = { "PARAM_UNUSED", "<unrecognized>", "<unrecognized>", "<unrecognized>", "PARAM_RETURN" };
/*     */ 
/* 302 */         localDumpInfo.addBitfield("Status", 1, this._status, arrayOfString);
/* 303 */         localDumpInfo.addHex("User Type", 4, this._usertype);
/* 304 */         localDumpInfo.addInfo("Data Type", 1, getDataTypeString(this._datatype));
/* 305 */         if ((paramDumpFilter.includesDetail(1)) && 
/* 307 */           (lengthSize(this._datatype) > 0))
/*     */         {
/* 309 */           localDumpInfo.addInt("Maximum Length", lengthSize(this._datatype), this._length);
/*     */         }
/*     */ 
/* 313 */         if ((this._datatype == 106) || (this._datatype == 108))
/*     */         {
/* 315 */           localDumpInfo.addHex("Precision", 1, this._precision);
/* 316 */           localDumpInfo.addHex("Scale", 1, this._scale);
/*     */         }
/*     */       }
/* 319 */       return localDumpInfo;
/*     */     }
/*     */ 
/*     */     public int getType()
/*     */     {
/* 327 */       return -1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvReturnValueToken
 * JD-Core Version:    0.5.4
 */