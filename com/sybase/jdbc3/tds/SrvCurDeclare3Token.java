/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvCurDeclare3Token extends SrvCurDeclare2Token
/*     */ {
/*     */   public SrvCurDeclare3Token(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  42 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   protected int readOptions(TdsInputStream paramTdsInputStream) throws IOException
/*     */   {
/*  47 */     int i = paramTdsInputStream.readInt();
/*  48 */     return i;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  58 */     Object[] arrayOfObject = { "CURDECLARE3", new Integer(16), new Integer(4), new Integer((int)this._curlen), new Integer(0), new Integer(this._nameLen), this._cursorName, null, new Integer(this._status) };
/*     */ 
/*  72 */     DumpInfo localDumpInfo = preDump(paramDumpFilter, arrayOfObject);
/*     */ 
/*  75 */     if ((paramDumpFilter.includesToken(134)) && ((
/*  77 */       (paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(9)))))
/*     */     {
/*  80 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/*  82 */         localDumpInfo.addInt("Statement Length", 4, (int)this._queryLen);
/*     */       }
/*  84 */       String[] arrayOfString = { "UNUSED", "CUR_RDONLY", "CUR_UPDATABLE", "CUR_SENSITIVE", "CUR_DYNAMIC", "CUR_IMPLICIT", "CUR_INSENSITIVE", "CUR_SEMISENSITIVE", "CUR_KEYSETDRIVEN", "CUR_SCROLLABLE" };
/*     */ 
/*  92 */       localDumpInfo.addBitfield("Options", 4, this._options, arrayOfString);
/*  93 */       localDumpInfo.addText("Statement", (int)this._queryLen, this._query);
/*  94 */       localDumpInfo.addInt("No. of Columns", 2, this._numColumns);
/*  95 */       for (int i = 0; i < this._numColumns; ++i)
/*     */       {
/*  97 */         localDumpInfo.addInt("Column ", 0, i + 1);
/*     */ 
/*  99 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 101 */           localDumpInfo.addInt("Column Name Length", 1, this._colNames[i].length());
/*     */         }
/*     */ 
/* 104 */         localDumpInfo.addText("Column Name ", this._colNames[i].length(), this._colNames[i]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 109 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 117 */     return 16;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCurDeclare3Token
 * JD-Core Version:    0.5.4
 */