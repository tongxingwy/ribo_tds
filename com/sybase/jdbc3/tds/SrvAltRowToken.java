/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvAltRowToken extends SrvRowToken
/*     */   implements SrvDataToken, Dumpable
/*     */ {
/*     */   int _id;
/*     */ 
/*     */   public SrvAltRowToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  37 */     super(paramTdsInputStream);
/*  38 */     this._id = paramTdsInputStream.readShort();
/*     */   }
/*     */ 
/*     */   public int getId()
/*     */   {
/*  46 */     return this._id;
/*     */   }
/*     */ 
/*     */   public void setFormatter(SrvTypeFormatter paramSrvTypeFormatter)
/*     */   {
/*  54 */     this._formatter = paramSrvTypeFormatter;
/*     */   }
/*     */ 
/*     */   public TdsInputStream getStream()
/*     */   {
/*  62 */     return this._in;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  71 */     if (this._formatter == null)
/*     */     {
/*  73 */       throw new Error("Ya godda have a formatter to dump rows.");
/*     */     }
/*  75 */     DumpInfo localDumpInfo = null;
/*     */ 
/*  78 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = (SrvJavaTypeFormatter)this._formatter;
/*  79 */     Object[] arrayOfObject = localSrvJavaTypeFormatter.convertData(this);
/*     */ 
/*  81 */     if (paramDumpFilter.includesToken(211))
/*     */     {
/*  83 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  84 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  86 */         localDumpInfo.addInfo("Token", 1, "ALTROW Token (0x" + HexConverts.hexConvert(211, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/*  92 */         localDumpInfo.addInfo("Token", 1, "ALTROW Token");
/*     */       }
/*  94 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(6)))
/*     */       {
/*  97 */         localDumpInfo.addInt("ID", 2, this._id);
/*     */ 
/*  99 */         int[] arrayOfInt1 = localSrvJavaTypeFormatter.getDataLengths();
/* 100 */         int[] arrayOfInt2 = localSrvJavaTypeFormatter.getLengthSizes();
/* 101 */         for (int i = 0; i < arrayOfObject.length; ++i)
/*     */         {
/* 103 */           localDumpInfo.addInfo("TDSFmt", 0, "Column " + (i + 1));
/* 104 */           if (paramDumpFilter.includesDetail(1))
/*     */           {
/* 106 */             localDumpInfo.addInt("Length", arrayOfInt2[i], arrayOfInt1[i]);
/*     */           }
/* 108 */           if (arrayOfObject[i] instanceof byte[])
/*     */           {
/* 110 */             localDumpInfo.addHex("Row Data", arrayOfInt1[i], (byte[])arrayOfObject[i]);
/*     */           }
/*     */           else
/*     */           {
/* 114 */             localDumpInfo.addInfo("Row Data", arrayOfInt1[i], arrayOfObject[i]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 119 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 127 */     return 211;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvAltRowToken
 * JD-Core Version:    0.5.4
 */