/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvParamsToken extends ParamsToken
/*     */   implements SrvDataToken, Dumpable
/*     */ {
/*  29 */   SrvTypeFormatter _formatter = null;
/*     */ 
/*  33 */   TdsInputStream _in = null;
/*     */ 
/*     */   public SrvParamsToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  43 */     super(paramTdsInputStream);
/*  44 */     this._in = paramTdsInputStream;
/*     */   }
/*     */ 
/*     */   public SrvParamsToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*  62 */     paramTdsOutputStream.writeByte(215);
/*  63 */     this._formatter.sendDataStream(paramTdsOutputStream);
/*     */   }
/*     */ 
/*     */   public TdsInputStream getStream()
/*     */   {
/*  71 */     return this._in;
/*     */   }
/*     */ 
/*     */   public void setFormatter(SrvTypeFormatter paramSrvTypeFormatter)
/*     */   {
/*  80 */     this._formatter = paramSrvTypeFormatter;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  88 */     if (this._formatter == null)
/*     */     {
/*  90 */       throw new Error("Ya godda have a formatter to dump params.");
/*     */     }
/*     */ 
/*  93 */     DumpInfo localDumpInfo = null;
/*     */ 
/*  96 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = (SrvJavaTypeFormatter)this._formatter;
/*  97 */     Object[] arrayOfObject1 = localSrvJavaTypeFormatter.convertData(this);
/*     */ 
/*  99 */     if (paramDumpFilter.includesToken(215))
/*     */     {
/* 101 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 102 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 104 */         localDumpInfo.addInfo("Token", 1, "PARAMS Token (0x" + HexConverts.hexConvert(215, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 110 */         localDumpInfo.addInfo("Token", 1, "PARAMS Token");
/*     */       }
/* 112 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(6)))
/*     */       {
/* 115 */         int[] arrayOfInt1 = localSrvJavaTypeFormatter.getColumnStatusBytes();
/* 116 */         int[] arrayOfInt2 = localSrvJavaTypeFormatter.getSerializationTypes();
/* 117 */         int[] arrayOfInt3 = localSrvJavaTypeFormatter.getDataLengths();
/* 118 */         int[] arrayOfInt4 = localSrvJavaTypeFormatter.getLengthSizes();
/* 119 */         SrvFormatToken localSrvFormatToken = localSrvJavaTypeFormatter.getDataFormats();
/* 120 */         long[] arrayOfLong = localSrvJavaTypeFormatter.getLobLengths();
/* 121 */         int[] arrayOfInt5 = localSrvJavaTypeFormatter.getLocatorLengths();
/* 122 */         Object[] arrayOfObject2 = localSrvJavaTypeFormatter.getLocators();
/* 123 */         String[] arrayOfString = { "DATA_NORMAL", "DATA_NULL", "DATA_ZERO_LENGTH_TEXT_IMAGE", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>" };
/*     */ 
/* 131 */         for (int i = 0; i < arrayOfObject1.length; ++i)
/*     */         {
/* 133 */           localDumpInfo.addInfo("TDSFmt", 0, "Param " + (i + 1));
/*     */ 
/* 139 */           DataFormat localDataFormat = localSrvFormatToken.formatAt(i);
/* 140 */           String str = null;
/* 141 */           if (localDataFormat.isUnitype())
/*     */           {
/* 143 */             if (getStream().getBigEndian())
/*     */             {
/* 145 */               str = "UnicodeBigUnmarked";
/*     */             }
/*     */             else
/*     */             {
/* 149 */               str = "UnicodeLittleUnmarked";
/*     */             }
/*     */           }
/*     */ 
/* 153 */           if (paramDumpFilter.includesDetail(1))
/*     */           {
/* 156 */             if (arrayOfInt1[i] != 9999)
/*     */             {
/* 160 */               int j = (byte)arrayOfInt1[i];
/* 161 */               localDumpInfo.addBitfield("Column Status", 1, j, arrayOfString);
/*     */ 
/* 171 */               if ((j & 0x1) != 0)
/*     */               {
/* 173 */                 localDumpInfo.addValue("Param data", arrayOfInt3[i], arrayOfObject1[i], str);
/*     */ 
/* 176 */                 continue;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 181 */             localDumpInfo.addInt("Length", arrayOfInt4[i], arrayOfInt3[i]);
/*     */           }
/*     */ 
/* 185 */           if (arrayOfInt2[i] != 9999)
/*     */           {
/* 188 */             localDumpInfo.addInt("Blob Serialization Type", 1, arrayOfInt2[i]);
/*     */           }
/*     */ 
/* 191 */           if (arrayOfLong[i] != SrvJavaTypeFormatter.LENGTH_NOT_SET.longValue())
/*     */           {
/* 193 */             localDumpInfo.addInt("Lob Length", 8, arrayOfLong[i]);
/*     */           }
/*     */ 
/* 196 */           if (arrayOfInt5[i] != SrvJavaTypeFormatter.LENGTH_NOT_SET.longValue())
/*     */           {
/* 198 */             localDumpInfo.addInt("Locator Length", 2, arrayOfInt5[i]);
/*     */           }
/*     */ 
/* 201 */           if (arrayOfObject2[i] instanceof byte[])
/*     */           {
/* 203 */             byte[] arrayOfByte = (byte[])arrayOfObject2[i];
/* 204 */             localDumpInfo.addHex("Locator", arrayOfByte.length, arrayOfByte);
/*     */           }
/* 206 */           localDumpInfo.addValue("Param data", arrayOfInt3[i], arrayOfObject1[i], str);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 211 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 219 */     return 215;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvParamsToken
 * JD-Core Version:    0.5.4
 */