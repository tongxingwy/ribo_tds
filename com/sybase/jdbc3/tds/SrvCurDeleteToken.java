/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvCurDeleteToken extends CurDeleteToken
/*     */   implements Dumpable
/*     */ {
/*  31 */   int _curlen = 0;
/*  32 */   public int _curId = 0;
/*  33 */   int _nameLen = 0;
/*  34 */   String _cursorName = null;
/*  35 */   int _status = 0;
/*     */   int _tableNameLength;
/*     */   String _tableName;
/*     */ 
/*     */   public SrvCurDeleteToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  48 */     this._curlen = paramTdsInputStream.readShort();
/*  49 */     this._curId = paramTdsInputStream.readInt();
/*  50 */     if (this._curId == 0)
/*     */     {
/*  52 */       this._nameLen = paramTdsInputStream.readUnsignedByte();
/*  53 */       this._cursorName = paramTdsInputStream.readString(this._nameLen);
/*     */     }
/*  55 */     this._status = paramTdsInputStream.readUnsignedByte();
/*  56 */     this._tableNameLength = paramTdsInputStream.readUnsignedByte();
/*  57 */     this._tableName = paramTdsInputStream.readString(this._tableNameLength);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  68 */     Object[] arrayOfObject = { "CURDELETE", new Integer(129), new Integer(2), new Integer(this._curlen), new Integer(this._curId), new Integer(this._nameLen), this._cursorName, null, new Integer(this._status) };
/*     */ 
/*  82 */     DumpInfo localDumpInfo = SrvCurDeclareToken.preDump(paramDumpFilter, arrayOfObject);
/*     */ 
/*  84 */     if ((paramDumpFilter.includesToken(129)) && ((
/*  86 */       (paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(9)))))
/*     */     {
/*  89 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/*  91 */         localDumpInfo.addInt("Length of Table Name", 1, this._tableNameLength);
/*     */       }
/*  93 */       localDumpInfo.addText("Table Name ", this._tableNameLength, this._tableName);
/*     */     }
/*     */ 
/*  96 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 104 */     return 129;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCurDeleteToken
 * JD-Core Version:    0.5.4
 */