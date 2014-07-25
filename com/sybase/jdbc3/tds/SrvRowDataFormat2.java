/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvRowDataFormat2 extends RowDataFormat2
/*     */   implements Dumpable
/*     */ {
/*     */   public SrvRowDataFormat2(String paramString, int paramInt1, int paramInt2, int paramInt3, Object paramObject)
/*     */   {
/*  66 */     this(paramString, paramInt1, paramInt2, paramInt3, (paramObject == null) ? null : paramObject.getClass().getName());
/*     */   }
/*     */ 
/*     */   public SrvRowDataFormat2(String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2)
/*     */   {
/*  76 */     this._name = paramString1;
/*  77 */     if (this._name != null)
/*     */     {
/*  79 */       this._nameLen = paramString1.length();
/*     */     }
/*     */     else
/*     */     {
/*  83 */       this._name = "";
/*  84 */       this._nameLen = 0;
/*     */     }
/*  86 */     this._status = paramInt2;
/*  87 */     this._usertype = 0;
/*     */ 
/*  91 */     switch (paramInt1)
/*     */     {
/*     */     case 9217:
/*  94 */       this._datatype = 36;
/*  95 */       this._blobType = 1;
/*  96 */       this._className = paramString2;
/*  97 */       if (paramString2 != null)
/*     */       {
/*  99 */         this._classIdLen = this._className.length();
/*     */       }
/*     */       else
/*     */       {
/* 103 */         this._classIdLen = 0;
/*     */       }
/* 105 */       break;
/*     */     case 9219:
/* 107 */       this._datatype = 36;
/* 108 */       this._blobType = 3;
/* 109 */       this._classIdLen = 0;
/* 110 */       break;
/*     */     case 9221:
/* 112 */       this._datatype = 36;
/* 113 */       this._blobType = 5;
/* 114 */       this._classIdLen = 0;
/* 115 */       break;
/*     */     case 9220:
/* 117 */       this._datatype = 36;
/* 118 */       this._blobType = 4;
/* 119 */       this._classIdLen = 0;
/* 120 */       break;
/*     */     case 9218:
/*     */     default:
/* 122 */       this._datatype = paramInt1;
/*     */     }
/*     */ 
/* 125 */     this._length = paramInt3;
/*     */ 
/* 127 */     this._precision = 0;
/* 128 */     this._scale = 0;
/* 129 */     this._locale = null;
/* 130 */     this._localeLen = 0;
/*     */   }
/*     */ 
/*     */   public SrvRowDataFormat2(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Object paramObject)
/*     */   {
/* 157 */     this(paramString, paramInt1, paramInt2, paramInt3, paramObject);
/* 158 */     this._precision = paramInt4;
/* 159 */     this._scale = paramInt5;
/*     */   }
/*     */ 
/*     */   public SrvRowDataFormat2(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Object paramObject)
/*     */   {
/* 196 */     this(paramString5, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramObject);
/* 197 */     this._labelName = paramString1;
/* 198 */     if (this._labelName != null)
/*     */     {
/* 200 */       this._labelLen = paramString1.length();
/*     */     }
/*     */     else
/*     */     {
/* 206 */       this._labelName = this._name;
/*     */ 
/* 208 */       this._labelLen = this._nameLen;
/*     */     }
/*     */ 
/* 213 */     this._catalogName = paramString2;
/* 214 */     if (this._catalogName != null)
/*     */     {
/* 216 */       this._catalogLen = paramString2.length();
/*     */     }
/*     */     else
/*     */     {
/* 220 */       this._catalogName = "";
/* 221 */       this._catalogLen = 0;
/*     */     }
/*     */ 
/* 225 */     this._schemaName = paramString3;
/* 226 */     if (this._schemaName != null)
/*     */     {
/* 228 */       this._schemaLen = paramString3.length();
/*     */     }
/*     */     else
/*     */     {
/* 232 */       this._schemaName = "";
/* 233 */       this._schemaLen = 0;
/*     */     }
/*     */ 
/* 237 */     this._tableName = paramString4;
/* 238 */     if (this._tableName != null)
/*     */     {
/* 240 */       this._tableLen = paramString4.length();
/*     */     }
/*     */     else
/*     */     {
/* 244 */       this._tableName = "";
/* 245 */       this._tableLen = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SrvRowDataFormat2(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/* 261 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   protected SrvRowDataFormat2()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 285 */     DumpInfo localDumpInfo = paramDumpFilter.getDumpInfo();
/* 286 */     if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(7)))
/*     */     {
/* 289 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 292 */         localDumpInfo.addInt("Column Label Length", 1, this._labelLen);
/*     */       }
/* 294 */       if (this._labelLen > 0)
/*     */       {
/* 296 */         localDumpInfo.addText("Column Label", this._labelLen, getLabelName());
/*     */       }
/* 298 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 301 */         localDumpInfo.addInt("Catalog Name Length", 1, this._catalogLen);
/*     */       }
/* 303 */       if (this._catalogLen > 0)
/*     */       {
/* 305 */         localDumpInfo.addText("Catalog", this._catalogLen, getCatalogName());
/*     */       }
/* 307 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 310 */         localDumpInfo.addInt("Schema Length", 1, this._schemaLen);
/*     */       }
/* 312 */       if (this._schemaLen > 0)
/*     */       {
/* 314 */         localDumpInfo.addText("Scehma", this._schemaLen, getSchemaName());
/*     */       }
/* 316 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 319 */         localDumpInfo.addInt("Table Name Length", 1, this._tableLen);
/*     */       }
/* 321 */       if (this._tableLen > 0)
/*     */       {
/* 323 */         localDumpInfo.addText("Table Name", this._tableLen, getTableName());
/*     */       }
/* 325 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 328 */         localDumpInfo.addInt("Column Name Length", 1, this._nameLen);
/*     */       }
/* 330 */       if (this._nameLen > 0)
/*     */       {
/* 332 */         localDumpInfo.addText("Column Name", this._nameLen, getName());
/*     */       }
/* 334 */       String[] arrayOfString = { "<unrecognized>", "ROW_HIDDEN", "ROW_KEY", "ROW_VERSION", "ROW_COLUMNSTATUS", "ROW_UPDATABLE", "ROW_NULLALLOWED", "ROW_IDENTITY", "ROW_PADCHAR" };
/*     */ 
/* 341 */       localDumpInfo.addBitfield("Status", 4, this._status, arrayOfString);
/* 342 */       localDumpInfo.addHex("User Type", 4, this._usertype);
/* 343 */       localDumpInfo.addInfo("Data Type", 1, getDataTypeString(this._datatype));
/* 344 */       if (this._datatype == 36)
/*     */       {
/* 346 */         String str = null;
/* 347 */         switch (this._blobType)
/*     */         {
/*     */         case 1:
/* 350 */           str = "JAVA_OBJECT1";
/* 351 */           break;
/*     */         case 2:
/* 353 */           str = "JAVA_OBJECT2";
/* 354 */           break;
/*     */         case 3:
/* 356 */           str = "BLOB_VARCHAR";
/* 357 */           break;
/*     */         case 4:
/* 359 */           str = "BLOB_VARBINARY";
/* 360 */           break;
/*     */         case 5:
/* 362 */           str = "BLOB_UTF16";
/* 363 */           break;
/*     */         case 7:
/* 365 */           str = "IMAGE LOCATOR";
/* 366 */           break;
/*     */         case 6:
/* 368 */           str = "TEXT LOCATOR";
/* 369 */           break;
/*     */         case 8:
/* 371 */           str = "UNITEXT LOCATOR";
/*     */         }
/*     */ 
/* 376 */         localDumpInfo.addInfo("Blob Type", 1, str + " (" + this._blobType + ")");
/*     */ 
/* 379 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 381 */           localDumpInfo.addInt("Class ID Length", 2, this._classIdLen);
/*     */         }
/* 383 */         if (this._classIdLen > 0)
/*     */         {
/* 385 */           localDumpInfo.addText("ClassID", this._classIdLen, this._className);
/*     */         }
/*     */       }
/* 388 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 390 */         localDumpInfo.addInt("Length", lengthSize(this._datatype), this._length);
/*     */       }
/* 392 */       if ((this._datatype == 106) || (this._datatype == 108))
/*     */       {
/* 394 */         localDumpInfo.addInt("Precision", 1, this._precision);
/* 395 */         localDumpInfo.addInt("Scale", 1, this._scale);
/*     */       }
/* 397 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 399 */         localDumpInfo.addInt("Locale Length", 1, this._localeLen);
/*     */       }
/* 401 */       if (this._localeLen > 0)
/*     */       {
/* 403 */         localDumpInfo.addText("Locale", this._localeLen, this._locale);
/*     */       }
/*     */     }
/* 406 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 414 */     return -1;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvRowDataFormat2
 * JD-Core Version:    0.5.4
 */