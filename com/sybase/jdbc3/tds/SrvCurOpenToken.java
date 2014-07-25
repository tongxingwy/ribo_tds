/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvCurOpenToken extends CurOpenToken
/*     */   implements Dumpable
/*     */ {
/*  40 */   int _curlen = 0;
/*  41 */   int _curId = 0;
/*  42 */   int _nameLen = 0;
/*  43 */   String _cursorName = null;
/*  44 */   int _status = 0;
/*     */ 
/*     */   public SrvCurOpenToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  54 */     this._curlen = paramTdsInputStream.readShort();
/*  55 */     this._curId = paramTdsInputStream.readInt();
/*  56 */     if (this._curId == 0)
/*     */     {
/*  58 */       this._nameLen = paramTdsInputStream.readUnsignedByte();
/*  59 */       this._cursorName = paramTdsInputStream.readString(this._nameLen);
/*     */     }
/*  61 */     this._status = paramTdsInputStream.readUnsignedByte();
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  71 */     Object[] arrayOfObject = { "CUROPEN", new Integer(132), new Integer(2), new Integer(this._curlen), new Integer(this._curId), new Integer(this._nameLen), this._cursorName, null, new Integer(this._status) };
/*     */ 
/*  84 */     DumpInfo localDumpInfo = SrvCurDeclareToken.preDump(paramDumpFilter, arrayOfObject);
/*  85 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/*  93 */     return 132;
/*     */   }
/*     */ 
/*     */   public boolean hasParams()
/*     */   {
/* 103 */     return this._status == 1;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCurOpenToken
 * JD-Core Version:    0.5.4
 */