/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvCurDeclare2Token extends SrvCurDeclareToken
/*     */ {
/*     */   public SrvCurDeclare2Token(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  42 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   protected long readTokenLength(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  48 */     long l = paramTdsInputStream.readUnsignedIntAsLong();
/*  49 */     return l;
/*     */   }
/*     */ 
/*     */   protected long readQueryLength(TdsInputStream paramTdsInputStream) throws IOException
/*     */   {
/*  54 */     long l = paramTdsInputStream.readUnsignedIntAsLong();
/*  55 */     return l;
/*     */   }
/*     */ 
/*     */   protected int readNumColumns(TdsInputStream paramTdsInputStream) throws IOException
/*     */   {
/*  60 */     int i = paramTdsInputStream.readShort();
/*  61 */     return i;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  71 */     Object[] arrayOfObject = { "CURDECLARE2", new Integer(35), new Integer(4), new Integer((int)this._curlen), new Integer(0), new Integer(this._nameLen), this._cursorName, null, new Integer(this._status) };
/*     */ 
/*  85 */     DumpInfo localDumpInfo = preDump(paramDumpFilter, arrayOfObject);
/*     */ 
/*  88 */     if ((paramDumpFilter.includesToken(134)) && ((
/*  90 */       (paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(9)))))
/*     */     {
/*  93 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/*  95 */         localDumpInfo.addInt("Statement Length", 4, (int)this._queryLen);
/*     */       }
/*  97 */       String[] arrayOfString = { "UNUSED", "CUR_RDONLY", "CUR_UPDATABLE", "<unrecognized>", "CUR_DYNAMIC" };
/*     */ 
/* 103 */       localDumpInfo.addBitfield("Options", 1, this._options, arrayOfString);
/* 104 */       localDumpInfo.addText("Statement", (int)this._queryLen, this._query);
/* 105 */       localDumpInfo.addInt("No. of Columns", 2, this._numColumns);
/* 106 */       for (int i = 0; i < this._numColumns; ++i)
/*     */       {
/* 108 */         localDumpInfo.addInt("Column ", 0, i + 1);
/*     */ 
/* 110 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 112 */           localDumpInfo.addInt("Column Name Length", 1, this._colNames[i].length());
/*     */         }
/* 114 */         localDumpInfo.addText("Column Name ", this._colNames[i].length(), this._colNames[i]);
/*     */       }
/*     */     }
/*     */ 
/* 118 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 126 */     return 35;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCurDeclare2Token
 * JD-Core Version:    0.5.4
 */