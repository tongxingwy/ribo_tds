/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvCurInfoToken extends CurInfoToken
/*     */   implements Dumpable
/*     */ {
/*  32 */   int _curlen = 0;
/*  33 */   int _curId = 0;
/*  34 */   int _nameLen = 0;
/*  35 */   String _cursorName = null;
/*  36 */   int _command = 0;
/*  37 */   int _status = 0;
/*  38 */   int _fetchSize = 0;
/*     */ 
/*  40 */   protected SrvCursor _cursor = null;
/*     */ 
/*     */   public SrvCurInfoToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  46 */     this._curlen = paramTdsInputStream.readShort();
/*  47 */     int i = this._curlen;
/*     */ 
/*  49 */     this._curId = paramTdsInputStream.readInt();
/*  50 */     i -= 4;
/*     */ 
/*  52 */     if (this._curId == 0)
/*     */     {
/*  54 */       this._nameLen = paramTdsInputStream.readUnsignedByte();
/*  55 */       --i;
/*  56 */       this._cursorName = paramTdsInputStream.readString(this._nameLen);
/*  57 */       i -= this._nameLen;
/*     */     }
/*  59 */     this._command = paramTdsInputStream.readUnsignedByte();
/*  60 */     --i;
/*  61 */     this._status = readStatus(paramTdsInputStream);
/*  62 */     i -= getStatusLength();
/*  63 */     readMetaInfo(paramTdsInputStream, i);
/*     */   }
/*     */ 
/*     */   public SrvCurInfoToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void setCursor(SrvCursor paramSrvCursor)
/*     */   {
/*  74 */     this._cursor = paramSrvCursor;
/*     */   }
/*     */ 
/*     */   protected void setCommand(int paramInt)
/*     */   {
/*  79 */     this._command = paramInt;
/*     */   }
/*     */ 
/*     */   public int curId()
/*     */   {
/*  85 */     return this._curId;
/*     */   }
/*     */ 
/*     */   public String name()
/*     */   {
/*  91 */     return this._cursorName;
/*     */   }
/*     */ 
/*     */   public int rowcnt()
/*     */   {
/*  97 */     return this._fetchSize;
/*     */   }
/*     */ 
/*     */   protected int readStatus(TdsInputStream paramTdsInputStream) throws IOException
/*     */   {
/* 102 */     return paramTdsInputStream.readShort();
/*     */   }
/*     */ 
/*     */   protected void readMetaInfo(TdsInputStream paramTdsInputStream, int paramInt) throws IOException
/*     */   {
/* 107 */     if (paramInt != 4)
/*     */       return;
/* 109 */     this._fetchSize = paramTdsInputStream.readInt();
/*     */   }
/*     */ 
/*     */   protected int getStatusLength()
/*     */   {
/* 115 */     return 2;
/*     */   }
/*     */ 
/*     */   protected int getMetaLength()
/*     */   {
/* 120 */     if ((this._cursor.getCurInfoStatus() & 0x20) != 0)
/*     */     {
/* 122 */       return 4;
/*     */     }
/*     */ 
/* 126 */     return 0;
/*     */   }
/*     */ 
/*     */   protected String getTokenName()
/*     */   {
/* 132 */     return "CURINFO";
/*     */   }
/*     */ 
/*     */   protected void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 142 */     int i = 5 + getStatusLength() + getMetaLength();
/*     */ 
/* 144 */     paramTdsOutputStream.writeByte(getTokenType());
/* 145 */     paramTdsOutputStream.writeShort(i);
/* 146 */     paramTdsOutputStream.writeInt(this._cursor.getID());
/* 147 */     paramTdsOutputStream.writeByte(this._command);
/* 148 */     sendStatus(paramTdsOutputStream);
/* 149 */     sendMetaInfo(paramTdsOutputStream);
/*     */   }
/*     */ 
/*     */   protected void sendStatus(TdsOutputStream paramTdsOutputStream) throws IOException
/*     */   {
/* 154 */     paramTdsOutputStream.writeShort(this._cursor.getCurInfoStatus());
/*     */   }
/*     */ 
/*     */   protected void sendMetaInfo(TdsOutputStream paramTdsOutputStream) throws IOException
/*     */   {
/* 159 */     if ((this._cursor.getCurInfoStatus() & 0x20) == 0)
/*     */       return;
/* 161 */     paramTdsOutputStream.writeInt(this._cursor.getFetchSize());
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 171 */     Object[] arrayOfObject = { getTokenName(), new Integer(getTokenType()), new Integer(2), new Integer(this._curlen), new Integer(this._curId), new Integer(this._nameLen), this._cursorName, null, null, null, null };
/*     */ 
/* 188 */     DumpInfo localDumpInfo = SrvCurDeclareToken.preDump(paramDumpFilter, arrayOfObject);
/*     */ 
/* 190 */     if ((paramDumpFilter.includesToken(131)) && ((
/* 192 */       (paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(9)))))
/*     */     {
/* 195 */       addCommandField(localDumpInfo);
/* 196 */       addStatusField(localDumpInfo);
/* 197 */       addMetaFields(localDumpInfo);
/*     */     }
/*     */ 
/* 200 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   protected void addCommandField(DumpInfo paramDumpInfo)
/*     */   {
/* 205 */     String[] arrayOfString = { "<unrecognized>", "CUR_SETCURROWS", "CUR_INQUIRE", "CUR_INFORM", "CUR_LISTALL" };
/*     */ 
/* 211 */     paramDumpInfo.addField("Command", 1, this._command, arrayOfString);
/*     */   }
/*     */ 
/*     */   protected void addStatusField(DumpInfo paramDumpInfo) {
/* 215 */     String[] arrayOfString = { "UNUSED", "CUR_IS_DECLARED", "CUR_IS_OPEN", "CUR_IS_CLOSED", "CUR_IS_RDONLY", "CUR_IS_UPDATABLE", "CUR_IS_ROWCNT", "CUR_IS_DALLOC" };
/*     */ 
/* 222 */     paramDumpInfo.addBitfield("Status", 2, this._status, arrayOfString);
/*     */   }
/*     */ 
/*     */   protected void addMetaFields(DumpInfo paramDumpInfo)
/*     */   {
/* 227 */     if (this._fetchSize == 0)
/*     */       return;
/* 229 */     paramDumpInfo.addInt("Fetch Size", 4, this._fetchSize);
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 238 */     return 131;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCurInfoToken
 * JD-Core Version:    0.5.4
 */