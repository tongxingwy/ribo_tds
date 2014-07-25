/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvCurDeclareToken extends CurDeclareToken
/*     */   implements Dumpable
/*     */ {
/*  32 */   protected long _curlen = 0L;
/*     */ 
/*  35 */   protected int _options = 0;
/*  36 */   protected int _status = 0;
/*  37 */   protected int _nameLen = 0;
/*  38 */   protected String _cursorName = null;
/*  39 */   protected long _queryLen = 0L;
/*  40 */   protected String _query = null;
/*  41 */   protected int _numColumns = 0;
/*     */   protected String[] _colNames;
/*     */   public static final int CUR_TOKEN_NAME = 0;
/*     */   public static final int CUR_TOKEN = 1;
/*     */   public static final int CUR_LEN_FIELD_SIZE = 2;
/*     */   public static final int CUR_LEN = 3;
/*     */   public static final int CUR_ID = 4;
/*     */   public static final int CUR_NAMELEN = 5;
/*     */   public static final int CUR_NAME = 6;
/*     */   public static final int CUR_OPTIONS = 7;
/*     */   public static final int CUR_STATUS = 8;
/*     */ 
/*     */   public SrvCurDeclareToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  54 */     this._curlen = readTokenLength(paramTdsInputStream);
/*     */ 
/*  61 */     this._nameLen = paramTdsInputStream.readUnsignedByte();
/*  62 */     this._cursorName = paramTdsInputStream.readString(this._nameLen);
/*  63 */     this._options = readOptions(paramTdsInputStream);
/*  64 */     this._status = paramTdsInputStream.readUnsignedByte();
/*  65 */     this._queryLen = readQueryLength(paramTdsInputStream);
/*  66 */     this._query = paramTdsInputStream.readString((int)this._queryLen);
/*     */ 
/*  68 */     this._numColumns = readNumColumns(paramTdsInputStream);
/*  69 */     if (this._numColumns <= 0)
/*     */       return;
/*  71 */     this._colNames = new String[this._numColumns];
/*     */ 
/*  73 */     for (int i = 0; i < this._numColumns; ++i)
/*     */     {
/*  75 */       int j = paramTdsInputStream.readUnsignedByte();
/*  76 */       this._colNames[i] = paramTdsInputStream.readString(j);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected long readTokenLength(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  83 */     int i = paramTdsInputStream.readUnsignedShort();
/*  84 */     return i;
/*     */   }
/*     */ 
/*     */   protected long readQueryLength(TdsInputStream paramTdsInputStream) throws IOException
/*     */   {
/*  89 */     int i = paramTdsInputStream.readUnsignedShort();
/*  90 */     return i;
/*     */   }
/*     */ 
/*     */   protected int readNumColumns(TdsInputStream paramTdsInputStream) throws IOException
/*     */   {
/*  95 */     int i = paramTdsInputStream.readUnsignedByte();
/*  96 */     return i;
/*     */   }
/*     */ 
/*     */   protected int readOptions(TdsInputStream paramTdsInputStream) throws IOException
/*     */   {
/* 101 */     int i = paramTdsInputStream.readUnsignedByte();
/* 102 */     return i;
/*     */   }
/*     */ 
/*     */   public String[] cols()
/*     */   {
/* 114 */     return this._colNames;
/*     */   }
/*     */ 
/*     */   public String name()
/*     */   {
/* 124 */     return this._cursorName;
/*     */   }
/*     */ 
/*     */   public static DumpInfo preDump(DumpFilter paramDumpFilter, Object[] paramArrayOfObject)
/*     */   {
/* 151 */     DumpInfo localDumpInfo = null;
/*     */ 
/* 153 */     if (paramDumpFilter.includesToken(mInt(paramArrayOfObject[1])))
/*     */     {
/* 155 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 156 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 158 */         localDumpInfo.addInfo("Token", 1, paramArrayOfObject[0] + " Token (0x" + HexConverts.hexConvert(mInt(paramArrayOfObject[1]), 1) + "); variable length");
/*     */       }
/*     */       else
/*     */       {
/* 165 */         localDumpInfo.addInfo("Token", 1, paramArrayOfObject[0] + " Token");
/*     */       }
/*     */ 
/* 168 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 170 */         localDumpInfo.addInt("Length", mInt(paramArrayOfObject[2]), mInt(paramArrayOfObject[3]));
/*     */       }
/*     */ 
/* 174 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(9)))
/*     */       {
/* 177 */         if (mInt(paramArrayOfObject[4]) == 0)
/*     */         {
/* 179 */           if (paramDumpFilter.includesDetail(1))
/*     */           {
/* 181 */             localDumpInfo.addInt("Cursor Name Length", 1, mInt(paramArrayOfObject[5]));
/*     */           }
/*     */ 
/* 184 */           localDumpInfo.addText("Cursor Name", mInt(paramArrayOfObject[5]), (String)paramArrayOfObject[6]);
/*     */         }
/*     */         else
/*     */         {
/* 189 */           localDumpInfo.addHex("Cursor Id", 4, mInt(paramArrayOfObject[4]));
/*     */         }
/* 191 */         if (paramArrayOfObject[8] != null)
/*     */         {
/* 193 */           String[] arrayOfString = { "UNUSED", "CUR_HASARGS" };
/*     */ 
/* 198 */           localDumpInfo.addBitfield("Status", 1, mInt(paramArrayOfObject[8]), arrayOfString);
/*     */         }
/*     */       }
/*     */     }
/* 202 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   protected static int mInt(Object paramObject)
/*     */   {
/* 209 */     return ((Integer)paramObject).intValue();
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 220 */     Object[] arrayOfObject = { "CURDECLARE", new Integer(134), new Integer(2), new Integer((int)this._curlen), new Integer(0), new Integer(this._nameLen), this._cursorName, null, new Integer(this._status) };
/*     */ 
/* 234 */     DumpInfo localDumpInfo = preDump(paramDumpFilter, arrayOfObject);
/*     */ 
/* 237 */     if ((paramDumpFilter.includesToken(134)) && ((
/* 239 */       (paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(9)))))
/*     */     {
/* 242 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 244 */         localDumpInfo.addInt("Statement Length", 2, this._queryLen);
/*     */       }
/* 246 */       String[] arrayOfString = { "UNUSED", "CUR_RDONLY", "CUR_UPDATABLE", "<unrecognized>", "CUR_DYNAMIC" };
/*     */ 
/* 252 */       localDumpInfo.addBitfield("Options", 1, this._options, arrayOfString);
/* 253 */       localDumpInfo.addText("Statement", (int)this._queryLen, this._query);
/* 254 */       localDumpInfo.addInt("No. of Columns", 1, this._numColumns);
/* 255 */       for (int i = 0; i < this._numColumns; ++i)
/*     */       {
/* 257 */         localDumpInfo.addInt("Column ", 0, i + 1);
/*     */ 
/* 259 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 261 */           localDumpInfo.addInt("Column Name Length", 1, this._colNames[i].length());
/*     */         }
/* 263 */         localDumpInfo.addText("Column Name ", this._colNames[i].length(), this._colNames[i]);
/*     */       }
/*     */     }
/*     */ 
/* 267 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 275 */     return 134;
/*     */   }
/*     */ 
/*     */   public String getCursorName()
/*     */   {
/* 280 */     return this._cursorName;
/*     */   }
/*     */ 
/*     */   public int getCursorOptions()
/*     */   {
/* 285 */     return this._options;
/*     */   }
/*     */ 
/*     */   public String getCursorStatement()
/*     */   {
/* 290 */     return this._query;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCurDeclareToken
 * JD-Core Version:    0.5.4
 */