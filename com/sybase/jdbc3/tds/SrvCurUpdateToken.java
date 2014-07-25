/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvCurUpdateToken extends CurUpdateToken
/*     */   implements Dumpable
/*     */ {
/*  31 */   int _curlen = 0;
/*  32 */   public int _curId = 0;
/*  33 */   int _nameLen = 0;
/*  34 */   String _cursorName = null;
/*  35 */   int _status = 0;
/*     */   int _tableNameLength;
/*     */   String _tableName;
/*  38 */   int _stmtLen = 0;
/*  39 */   String _stmt = null;
/*     */ 
/*     */   public SrvCurUpdateToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  49 */     this._curlen = paramTdsInputStream.readShort();
/*  50 */     this._curId = paramTdsInputStream.readInt();
/*     */ 
/*  61 */     this._status = paramTdsInputStream.readUnsignedByte();
/*  62 */     this._tableNameLength = paramTdsInputStream.readUnsignedByte();
/*  63 */     if (this._tableNameLength > 0)
/*     */     {
/*  65 */       this._tableName = paramTdsInputStream.readString(this._tableNameLength);
/*     */     }
/*  67 */     this._stmtLen = paramTdsInputStream.readShort();
/*  68 */     this._stmt = paramTdsInputStream.readString(this._stmtLen);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  75 */     Object[] arrayOfObject = { "CURUPDATE", new Integer(133), new Integer(2), new Integer(this._curlen), new Integer(this._curId), new Integer(this._nameLen), this._cursorName, null, new Integer(this._status) };
/*     */ 
/*  88 */     DumpInfo localDumpInfo = SrvCurDeclareToken.preDump(paramDumpFilter, arrayOfObject);
/*     */ 
/*  90 */     if ((paramDumpFilter.includesToken(133)) && ((
/*  92 */       (paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(9)))))
/*     */     {
/*  95 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/*  97 */         localDumpInfo.addInt("Length of Table Name", 1, this._tableNameLength);
/*     */       }
/*  99 */       if (this._tableNameLength > 0)
/*     */       {
/* 101 */         localDumpInfo.addText("Table Name", this._tableNameLength, this._tableName);
/*     */       }
/* 103 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 105 */         localDumpInfo.addInt("Statement Length", 2, this._stmtLen);
/*     */       }
/* 107 */       localDumpInfo.addText("Statement", this._stmtLen, this._stmt);
/*     */     }
/*     */ 
/* 110 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 118 */     return 133;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCurUpdateToken
 * JD-Core Version:    0.5.4
 */