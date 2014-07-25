/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvTabNameToken extends Token
/*     */   implements Dumpable
/*     */ {
/*     */   public static final int TDS_OFF_SELECT = 365;
/*     */   public static final int TDS_OFF_FROM = 335;
/*     */   public static final int TDS_OFF_ORDER = 357;
/*     */   public static final int TDS_OFF_COMPUTE = 313;
/*     */   public static final int TDS_OFF_TABLE = 371;
/*     */   public static final int TDS_OFF_PROC = 362;
/*     */   public static final int TDS_OFF_STMT = 459;
/*     */   public static final int TDS_OFF_PARAM = 452;
/*     */   private int _length;
/*     */   private Vector _names;
/*     */ 
/*     */   public SrvTabNameToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  48 */     this._length = paramTdsInputStream.readShort();
/*  49 */     this._names = new Vector();
/*     */ 
/*  51 */     for (int j = this._length; j > 0; )
/*     */     {
/*  53 */       int i = paramTdsInputStream.readUnsignedByte();
/*  54 */       --j;
/*  55 */       if (i == 0)
/*     */       {
/*     */         return;
/*     */       }
/*     */ 
/*  60 */       String str = paramTdsInputStream.readString(i);
/*  61 */       j -= i;
/*     */ 
/*  63 */       this._names.addElement(str);
/*     */     }
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  78 */     DumpInfo localDumpInfo = null;
/*  79 */     if (paramDumpFilter.includesToken(164))
/*     */     {
/*  81 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  82 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  84 */         localDumpInfo.addInfo("Token", 1, "TABNAME Token (0x" + HexConverts.hexConvert(164, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/*  90 */         localDumpInfo.addInfo("Token", 1, "TABNAME Token");
/*     */       }
/*     */ 
/*  93 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/*  95 */         localDumpInfo.addInt("Length", 2, this._length);
/*     */       }
/*     */ 
/*  98 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 100 */         Enumeration localEnumeration = this._names.elements();
/* 101 */         while (localEnumeration.hasMoreElements())
/*     */         {
/* 103 */           String str = (String)localEnumeration.nextElement();
/* 104 */           if (paramDumpFilter.includesDetail(1))
/*     */           {
/* 106 */             localDumpInfo.addInt("Name Length", 1, str.length());
/*     */           }
/* 108 */           localDumpInfo.addText("Table Name", str.length(), str);
/*     */         }
/*     */       }
/*     */     }
/* 112 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 121 */     return 164;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvTabNameToken
 * JD-Core Version:    0.5.4
 */