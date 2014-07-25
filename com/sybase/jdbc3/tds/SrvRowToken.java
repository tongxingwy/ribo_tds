/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvRowToken extends RowToken
/*     */   implements SrvDataToken, Dumpable
/*     */ {
/*  28 */   SrvTypeFormatter _formatter = null;
/*     */ 
/*  32 */   TdsInputStream _in = null;
/*     */ 
/*     */   public SrvRowToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  42 */     super(paramTdsInputStream);
/*  43 */     this._in = paramTdsInputStream;
/*     */   }
/*     */ 
/*     */   public SrvRowToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*  68 */     paramTdsOutputStream.writeByte(209);
/*  69 */     this._formatter.sendDataStream(paramTdsOutputStream);
/*     */   }
/*     */ 
/*     */   public void setFormatter(SrvTypeFormatter paramSrvTypeFormatter)
/*     */   {
/*  79 */     this._formatter = paramSrvTypeFormatter;
/*     */   }
/*     */ 
/*     */   public TdsInputStream getStream()
/*     */   {
/*  87 */     return this._in;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  99 */     if (this._formatter == null)
/*     */     {
/* 101 */       throw new Error("Ya godda have a formatter to dump rows.");
/*     */     }
/*     */ 
/* 104 */     DumpInfo localDumpInfo = null;
/*     */ 
/* 107 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = (SrvJavaTypeFormatter)this._formatter;
/* 108 */     Object[] arrayOfObject1 = localSrvJavaTypeFormatter.convertData(this);
/*     */ 
/* 110 */     if (paramDumpFilter.includesToken(209))
/*     */     {
/* 112 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 113 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 115 */         localDumpInfo.addInfo("Token", 1, "ROW Token (0x" + HexConverts.hexConvert(209, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 121 */         localDumpInfo.addInfo("Token", 1, "ROW Token");
/*     */       }
/* 123 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(6)))
/*     */       {
/* 126 */         int[] arrayOfInt1 = localSrvJavaTypeFormatter.getColumnStatusBytes();
/* 127 */         int[] arrayOfInt2 = localSrvJavaTypeFormatter.getSerializationTypes();
/* 128 */         int[] arrayOfInt3 = localSrvJavaTypeFormatter.getDataLengths();
/* 129 */         int[] arrayOfInt4 = localSrvJavaTypeFormatter.getLengthSizes();
/* 130 */         long[] arrayOfLong = localSrvJavaTypeFormatter.getLobLengths();
/* 131 */         int[] arrayOfInt5 = localSrvJavaTypeFormatter.getLocatorLengths();
/* 132 */         Object[] arrayOfObject2 = localSrvJavaTypeFormatter.getLocators();
/* 133 */         SrvFormatToken localSrvFormatToken = localSrvJavaTypeFormatter.getDataFormats();
/*     */ 
/* 135 */         String[] arrayOfString = { "DATA_NORMAL", "DATA_NULL", "DATA_ZERO_LENGTH_TEXT_IMAGE", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>" };
/*     */ 
/* 143 */         for (int i = 0; i < arrayOfObject1.length; ++i)
/*     */         {
/* 145 */           localDumpInfo.addInfo("TDSFmt", 0, "Column " + (i + 1));
/*     */ 
/* 150 */           DataFormat localDataFormat = localSrvFormatToken.formatAt(i);
/* 151 */           String str = null;
/* 152 */           if (localDataFormat.isUnitype())
/*     */           {
/* 154 */             if (getStream().getBigEndian())
/*     */             {
/* 156 */               str = "UnicodeBigUnmarked";
/*     */             }
/*     */             else
/*     */             {
/* 160 */               str = "UnicodeLittleUnmarked";
/*     */             }
/*     */           }
/*     */ 
/* 164 */           if (paramDumpFilter.includesDetail(1))
/*     */           {
/* 167 */             if (arrayOfInt1[i] != 9999)
/*     */             {
/* 171 */               int j = (byte)arrayOfInt1[i];
/* 172 */               localDumpInfo.addBitfield("Column Status", 1, j, arrayOfString);
/*     */ 
/* 182 */               if ((j & 0x1) != 0)
/*     */               {
/* 184 */                 localDumpInfo.addValue("Row data", arrayOfInt3[i], arrayOfObject1[i], str);
/*     */ 
/* 187 */                 continue;
/*     */               }
/*     */             }
/*     */ 
/* 191 */             localDumpInfo.addInt("Length", arrayOfInt4[i], arrayOfInt3[i]);
/*     */           }
/*     */ 
/* 195 */           if (arrayOfInt2[i] != 9999)
/*     */           {
/* 198 */             localDumpInfo.addInt("Blob Serialization Type", 1, arrayOfInt2[i]);
/*     */           }
/*     */ 
/* 201 */           if (arrayOfLong[i] != SrvJavaTypeFormatter.LENGTH_NOT_SET.longValue())
/*     */           {
/* 203 */             localDumpInfo.addInt("Lob Length", 8, arrayOfLong[i]);
/*     */           }
/*     */ 
/* 206 */           if (arrayOfInt5[i] != SrvJavaTypeFormatter.LENGTH_NOT_SET.longValue())
/*     */           {
/* 208 */             localDumpInfo.addInt("Locator Length", 2, arrayOfInt5[i]);
/*     */           }
/*     */           byte[] arrayOfByte;
/* 211 */           if (arrayOfObject2[i] instanceof byte[])
/*     */           {
/* 213 */             arrayOfByte = (byte[])arrayOfObject2[i];
/* 214 */             localDumpInfo.addHex("Locator", arrayOfByte.length, arrayOfByte);
/*     */           }
/*     */ 
/* 217 */           if (arrayOfLong[i] != SrvJavaTypeFormatter.LENGTH_NOT_SET.longValue())
/*     */           {
/* 219 */             localDumpInfo.addInt("Lob Length", 8, arrayOfLong[i]);
/*     */           }
/*     */ 
/* 222 */           if (arrayOfInt5[i] != SrvJavaTypeFormatter.LENGTH_NOT_SET.longValue())
/*     */           {
/* 224 */             localDumpInfo.addInt("Locator Length", 2, arrayOfInt5[i]);
/*     */           }
/*     */ 
/* 227 */           if (arrayOfObject2[i] instanceof byte[])
/*     */           {
/* 229 */             arrayOfByte = (byte[])arrayOfObject2[i];
/* 230 */             localDumpInfo.addHex("Locator", arrayOfByte.length, arrayOfByte);
/*     */           }
/*     */ 
/* 234 */           localDumpInfo.addValue("Row data", arrayOfInt3[i], arrayOfObject1[i], str);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 240 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 248 */     return 209;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvRowToken
 * JD-Core Version:    0.5.4
 */