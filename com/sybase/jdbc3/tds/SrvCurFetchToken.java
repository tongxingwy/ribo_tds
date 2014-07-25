/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvCurFetchToken extends CurFetchToken
/*     */   implements Dumpable
/*     */ {
/*  33 */   int _curlen = 0;
/*  34 */   public int _curId = 0;
/*  35 */   int _nameLen = 0;
/*  36 */   String _cursorName = null;
/*  37 */   int _type = 0;
/*  38 */   int _rows = 0;
/*     */ 
/*     */   public SrvCurFetchToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  48 */     this._curlen = paramTdsInputStream.readShort();
/*  49 */     this._curId = paramTdsInputStream.readInt();
/*  50 */     if (this._curId == 0)
/*     */     {
/*  52 */       this._nameLen = paramTdsInputStream.readUnsignedByte();
/*  53 */       this._cursorName = paramTdsInputStream.readString(this._nameLen);
/*     */     }
/*  55 */     this._type = paramTdsInputStream.readUnsignedByte();
/*  56 */     switch (this._type)
/*     */     {
/*     */     case 5:
/*     */     case 6:
/*  60 */       this._rows = paramTdsInputStream.readInt();
/*     */     }
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  70 */     Object[] arrayOfObject = { "CURFETCH", new Integer(130), new Integer(2), new Integer(this._curlen), new Integer(this._curId), new Integer(this._nameLen), this._cursorName, null, null };
/*     */ 
/*  83 */     DumpInfo localDumpInfo = SrvCurDeclareToken.preDump(paramDumpFilter, arrayOfObject);
/*     */ 
/*  85 */     if ((paramDumpFilter.includesToken(130)) && ((
/*  87 */       (paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(9)))))
/*     */     {
/*  90 */       String[] arrayOfString = { "<unrecognized>", "CUR_NEXT", "CUR_PREV", "CUR_FIRST", "CUR_LAST", "CUR_ABS", "CUR_REL" };
/*     */ 
/*  96 */       localDumpInfo.addField("Type", 1, this._type, arrayOfString);
/*  97 */       localDumpInfo.addInt("No. of Rows", 4, this._rows);
/*     */     }
/*     */ 
/* 100 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 108 */     return 130;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCurFetchToken
 * JD-Core Version:    0.5.4
 */