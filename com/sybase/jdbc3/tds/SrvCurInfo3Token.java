/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvCurInfo3Token extends SrvCurInfoToken
/*     */   implements Dumpable
/*     */ {
/*     */   static final int UNKNOWN = -1;
/*     */   static final int BEFORE_FIRST = 0;
/*     */   static final int AFTER_LAST = -2;
/*     */   int _totalNumRows;
/*     */   int _rowNum;
/*     */ 
/*     */   public SrvCurInfo3Token(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  40 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public SrvCurInfo3Token()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected int readStatus(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  50 */     return paramTdsInputStream.readInt();
/*     */   }
/*     */ 
/*     */   protected void readMetaInfo(TdsInputStream paramTdsInputStream, int paramInt) throws IOException
/*     */   {
/*  55 */     this._rowNum = paramTdsInputStream.readInt();
/*  56 */     paramInt -= 4;
/*     */ 
/*  58 */     this._totalNumRows = paramTdsInputStream.readInt();
/*  59 */     paramInt -= 4;
/*     */ 
/*  61 */     if ((this._status & 0x20) == 0)
/*     */       return;
/*  63 */     this._fetchSize = paramTdsInputStream.readInt();
/*  64 */     paramInt -= 4;
/*     */   }
/*     */ 
/*     */   protected int getStatusLength()
/*     */   {
/*  70 */     return 4;
/*     */   }
/*     */ 
/*     */   protected int getMetaLength()
/*     */   {
/*  75 */     int i = 0;
/*     */ 
/*  77 */     i += 8;
/*     */ 
/*  79 */     if ((this._cursor.getCurInfoStatus() & 0x20) != 0)
/*     */     {
/*  81 */       i += 4;
/*     */     }
/*  83 */     return i;
/*     */   }
/*     */ 
/*     */   protected void sendStatus(TdsOutputStream paramTdsOutputStream) throws IOException
/*     */   {
/*  88 */     paramTdsOutputStream.writeInt(this._cursor.getCurInfoStatus());
/*     */   }
/*     */ 
/*     */   protected void sendMetaInfo(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*     */     int i;
/*  97 */     if (this._cursor.getLastCommand() == 1)
/*     */     {
/*  99 */       i = -1;
/*     */     }
/*     */     else
/*     */     {
/* 103 */       i = this._cursor.getRow();
/*     */     }
/* 105 */     paramTdsOutputStream.writeInt(i);
/*     */ 
/* 108 */     int j = this._cursor.getTotalNumRows();
/* 109 */     paramTdsOutputStream.writeInt(j);
/*     */ 
/* 112 */     if ((this._cursor.getCurInfoStatus() & 0x20) == 0)
/*     */       return;
/* 114 */     paramTdsOutputStream.writeInt(this._cursor.getFetchSize());
/*     */   }
/*     */ 
/*     */   protected String getTokenName()
/*     */   {
/* 120 */     return "CURINFO3";
/*     */   }
/*     */ 
/*     */   protected void addCommandField(DumpInfo paramDumpInfo)
/*     */   {
/* 125 */     String[] arrayOfString = { "<unrecognized>", "CUR_SETCURROWS", "CUR_INQUIRE", "CUR_INFORM", "CUR_LISTALL", "CUR_GETTOTALROWS", "CUR_GETROWNUM" };
/*     */ 
/* 132 */     paramDumpInfo.addField("Command", 1, this._command, arrayOfString);
/*     */   }
/*     */ 
/*     */   protected void addStatusField(DumpInfo paramDumpInfo)
/*     */   {
/* 138 */     String[] arrayOfString = { "UNUSED", "CUR_IS_DECLARED", "CUR_IS_OPEN", "CUR_IS_CLOSED", "CUR_IS_RDONLY", "CUR_IS_UPDATABLE", "CUR_IS_ROWCNT", "CUR_IS_DALLOC", "CUR_IS_SCROLLABLE", "CUR_IS_IMPLICIT", "CUR_IS_SENSITIVE", "CUR_IS_INSENSITIVE", "CUR_IS_SEMISENSITIVE", "CUR_IS_KEYSETDRIVEN" };
/*     */ 
/* 147 */     paramDumpInfo.addBitfield("Status", 4, this._status, arrayOfString);
/*     */   }
/*     */ 
/*     */   protected void addMetaFields(DumpInfo paramDumpInfo)
/*     */   {
/* 152 */     String str1 = "";
/* 153 */     String str2 = "";
/* 154 */     switch (this._rowNum)
/*     */     {
/*     */     case 0:
/* 157 */       str1 = " (BEFORE_FIRST)";
/* 158 */       break;
/*     */     case -1:
/* 160 */       str1 = " (UNKNOWN)";
/* 161 */       break;
/*     */     case -2:
/* 163 */       str1 = " (AFTER_LAST)";
/*     */     }
/*     */ 
/* 166 */     switch (this._totalNumRows)
/*     */     {
/*     */     case -1:
/* 169 */       str2 = " (UNKNOWN)";
/*     */     }
/*     */ 
/* 173 */     paramDumpInfo.addInfo("Row Number", 4, this._rowNum + str1);
/* 174 */     paramDumpInfo.addInfo("Total Number of Rows", 4, this._totalNumRows + str2);
/*     */ 
/* 176 */     if ((this._status & 0x20) == 0)
/*     */       return;
/* 178 */     paramDumpInfo.addInt("Fetch Size", 4, this._fetchSize);
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 187 */     return 136;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCurInfo3Token
 * JD-Core Version:    0.5.4
 */