/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvCurCloseToken extends CurCloseToken
/*     */   implements Dumpable
/*     */ {
/*  40 */   int _curlen = 0;
/*  41 */   int _curId = 0;
/*  42 */   int _nameLen = 0;
/*  43 */   String _cursorName = null;
/*  44 */   int _options = 0;
/*     */ 
/*     */   public SrvCurCloseToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  55 */     this._curlen = paramTdsInputStream.readShort();
/*  56 */     this._curId = paramTdsInputStream.readInt();
/*  57 */     if (this._curId == 0)
/*     */     {
/*  59 */       this._nameLen = paramTdsInputStream.readUnsignedByte();
/*  60 */       this._cursorName = paramTdsInputStream.readString(this._nameLen);
/*     */     }
/*     */ 
/*  63 */     this._options = paramTdsInputStream.readUnsignedByte();
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  70 */     Object[] arrayOfObject = { "CURCLOSE", new Integer(128), new Integer(2), new Integer(this._curlen), new Integer(this._curId), new Integer(this._nameLen), this._cursorName, null, null, new Integer(this._nameLen), this._cursorName };
/*     */ 
/*  84 */     DumpInfo localDumpInfo = SrvCurDeclareToken.preDump(paramDumpFilter, arrayOfObject);
/*     */ 
/*  86 */     if ((paramDumpFilter.includesToken(128)) && ((
/*  88 */       (paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(9)))))
/*     */     {
/*  91 */       String[] arrayOfString = { "CUR_UNUSED", "CUR_DEALLOC" };
/*     */ 
/*  96 */       localDumpInfo.addField("Options", 1, this._options, arrayOfString);
/*     */     }
/*     */ 
/*  99 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 107 */     return 128;
/*     */   }
/*     */ 
/*     */   public String getCursorName()
/*     */   {
/* 112 */     return this._cursorName;
/*     */   }
/*     */ 
/*     */   public boolean isDeallocate()
/*     */   {
/* 117 */     return (this._options & 0x1) != 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCurCloseToken
 * JD-Core Version:    0.5.4
 */