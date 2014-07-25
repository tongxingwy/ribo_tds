/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class RowDataFormat2 extends DataFormat
/*     */ {
/*     */   protected String _labelName;
/*     */   protected byte[] _labelNameBytes;
/*     */   protected int _labelLen;
/*     */   protected String _catalogName;
/*     */   protected byte[] _catalogNameBytes;
/*     */   protected int _catalogLen;
/*     */   protected String _schemaName;
/*     */   protected byte[] _schemaNameBytes;
/*     */   protected int _schemaLen;
/*     */   protected int _tableLen;
/*     */   protected byte[] _tableNameBytes;
/*  49 */   protected boolean _labelNameEmpty = false;
/*     */ 
/*     */   protected RowDataFormat2()
/*     */   {
/*     */   }
/*     */ 
/*     */   public RowDataFormat2(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  68 */     super(paramTdsInputStream, true);
/*     */   }
/*     */ 
/*     */   public RowDataFormat2(TdsInputStream paramTdsInputStream, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  81 */     super(paramTdsInputStream, paramBoolean);
/*     */   }
/*     */ 
/*     */   protected void readMetaInfo(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  95 */       this._labelLen = paramTdsInputStream.readUnsignedByte();
/*  96 */       if (this._labelLen > 0)
/*     */       {
/* 101 */         this._labelNameBytes = new byte[this._labelLen];
/* 102 */         paramTdsInputStream.read(this._labelNameBytes, 0, this._labelLen);
/*     */       }
/*     */       else
/*     */       {
/* 111 */         this._labelNameEmpty = true;
/*     */       }
/*     */ 
/* 114 */       this._catalogLen = paramTdsInputStream.readUnsignedByte();
/* 115 */       if (this._catalogLen > 0)
/*     */       {
/* 120 */         this._catalogNameBytes = new byte[this._catalogLen];
/* 121 */         paramTdsInputStream.read(this._catalogNameBytes, 0, this._catalogLen);
/*     */       }
/*     */ 
/* 131 */       this._schemaLen = paramTdsInputStream.readUnsignedByte();
/* 132 */       if (this._schemaLen > 0)
/*     */       {
/* 137 */         this._schemaNameBytes = new byte[this._schemaLen];
/* 138 */         paramTdsInputStream.read(this._schemaNameBytes, 0, this._schemaLen);
/*     */       }
/*     */ 
/* 148 */       this._tableLen = paramTdsInputStream.readUnsignedByte();
/* 149 */       if (this._tableLen > 0)
/*     */       {
/* 154 */         this._tableNameBytes = new byte[this._tableLen];
/* 155 */         paramTdsInputStream.read(this._tableNameBytes, 0, this._tableLen);
/*     */       }
/*     */ 
/* 165 */       this._nameLen = paramTdsInputStream.readUnsignedByte();
/* 166 */       if (this._nameLen > 0)
/*     */       {
/* 171 */         this._nameBytes = new byte[this._nameLen];
/* 172 */         paramTdsInputStream.read(this._nameBytes, 0, this._nameLen);
/*     */ 
/* 175 */         if (this._labelNameEmpty)
/*     */         {
/* 177 */           this._labelNameBytes = this._nameBytes;
/* 178 */           this._labelLen = this._nameLen;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 193 */       readSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void readStatus(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/* 203 */     this._status = paramTdsInputStream.readInt();
/*     */   }
/*     */ 
/*     */   public String getLabelName()
/*     */   {
/* 208 */     if (this._labelName != null)
/*     */     {
/* 210 */       return this._labelName;
/*     */     }
/* 212 */     if (this._labelLen == 0)
/*     */     {
/* 214 */       this._labelName = "";
/* 215 */       return this._labelName;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 223 */       this._labelName = this._tdsIn.convertBytesToString(this._labelNameBytes);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */ 
/* 229 */     return this._labelName;
/*     */   }
/*     */ 
/*     */   public String getCatalogName()
/*     */   {
/* 235 */     if (this._catalogName != null)
/*     */     {
/* 237 */       return this._catalogName;
/*     */     }
/* 239 */     if (this._catalogLen == 0)
/*     */     {
/* 241 */       this._catalogName = "";
/* 242 */       return this._catalogName;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 250 */       this._catalogName = this._tdsIn.convertBytesToString(this._catalogNameBytes);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */ 
/* 256 */     return this._catalogName;
/*     */   }
/*     */ 
/*     */   public String getSchemaName()
/*     */   {
/* 262 */     if (this._schemaName != null)
/*     */     {
/* 264 */       return this._schemaName;
/*     */     }
/* 266 */     if (this._schemaLen == 0)
/*     */     {
/* 268 */       this._schemaName = "";
/* 269 */       return this._schemaName;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 277 */       this._schemaName = this._tdsIn.convertBytesToString(this._schemaNameBytes);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */ 
/* 283 */     return this._schemaName;
/*     */   }
/*     */ 
/*     */   public String getTableName()
/*     */   {
/* 289 */     if (this._tableName != null)
/*     */     {
/* 291 */       return this._tableName;
/*     */     }
/* 293 */     if (this._tableLen == 0)
/*     */     {
/* 295 */       this._tableName = "";
/* 296 */       return this._tableName;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 304 */       this._tableName = this._tdsIn.convertBytesToString(this._tableNameBytes);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */ 
/* 310 */     return this._tableName;
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/* 337 */     int i = super.length();
/* 338 */     i += 7 + ((this._labelName == "") ? 0 : this._labelName.length()) + ((this._catalogName == null) ? 0 : this._catalogName.length()) + ((this._schemaName == null) ? 0 : this._schemaName.length()) + ((this._tableName == null) ? 0 : this._tableName.length());
/*     */ 
/* 343 */     return i;
/*     */   }
/*     */ 
/*     */   protected void sendMetaInfo(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*     */     byte[] arrayOfByte;
/* 357 */     if (this._labelName == "")
/*     */     {
/* 359 */       paramTdsOutputStream.writeByte(0);
/*     */     }
/*     */     else
/*     */     {
/* 363 */       arrayOfByte = paramTdsOutputStream.stringToByte(this._labelName);
/* 364 */       paramTdsOutputStream.writeByte(arrayOfByte.length);
/* 365 */       paramTdsOutputStream.write(arrayOfByte);
/*     */     }
/*     */ 
/* 368 */     if (this._catalogName == "")
/*     */     {
/* 370 */       paramTdsOutputStream.writeByte(0);
/*     */     }
/*     */     else
/*     */     {
/* 374 */       arrayOfByte = paramTdsOutputStream.stringToByte(this._catalogName);
/* 375 */       paramTdsOutputStream.writeByte(arrayOfByte.length);
/* 376 */       paramTdsOutputStream.write(arrayOfByte);
/*     */     }
/*     */ 
/* 379 */     if (this._schemaName == "")
/*     */     {
/* 381 */       paramTdsOutputStream.writeByte(0);
/*     */     }
/*     */     else
/*     */     {
/* 385 */       arrayOfByte = paramTdsOutputStream.stringToByte(this._schemaName);
/* 386 */       paramTdsOutputStream.writeByte(arrayOfByte.length);
/* 387 */       paramTdsOutputStream.write(arrayOfByte);
/*     */     }
/*     */ 
/* 390 */     if (this._tableName == "")
/*     */     {
/* 392 */       paramTdsOutputStream.writeByte(0);
/*     */     }
/*     */     else
/*     */     {
/* 396 */       arrayOfByte = paramTdsOutputStream.stringToByte(this._tableName);
/* 397 */       paramTdsOutputStream.writeByte(arrayOfByte.length);
/* 398 */       paramTdsOutputStream.write(arrayOfByte);
/*     */     }
/*     */ 
/* 401 */     if (this._name == "")
/*     */     {
/* 403 */       paramTdsOutputStream.writeByte(0);
/*     */     }
/*     */     else
/*     */     {
/* 407 */       arrayOfByte = paramTdsOutputStream.stringToByte(this._name);
/* 408 */       paramTdsOutputStream.writeByte(arrayOfByte.length);
/* 409 */       paramTdsOutputStream.write(arrayOfByte);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void sendStatus(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 419 */     paramTdsOutputStream.writeInt(this._status);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.RowDataFormat2
 * JD-Core Version:    0.5.4
 */