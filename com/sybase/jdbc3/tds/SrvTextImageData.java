/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class SrvTextImageData
/*     */ {
/*  62 */   private String _writetext = null;
/*     */   private static final int DEFTXTPTRSIZE = 8;
/*  66 */   protected byte[] _textptr = null;
/*  67 */   protected long _time = 0L;
/*     */ 
/*  70 */   protected byte[] _bytes = new byte[0];
/*     */ 
/*  73 */   protected int _length = 0;
/*  74 */   protected InputStream _byteStream = null;
/*     */ 
/*  79 */   protected String _tableName = null;
/*     */ 
/*     */   public SrvTextImageData()
/*     */   {
/*  87 */     this._textptr = new byte[8];
/*  88 */     for (int i = 0; i < this._textptr.length; ++i)
/*     */     {
/*  90 */       this._textptr[i] = 0;
/*     */     }
/*  92 */     this._time = 0L;
/*     */   }
/*     */ 
/*     */   public SrvTextImageData(String paramString)
/*     */   {
/*  98 */     this._writetext = paramString;
/*  99 */     this._textptr = new byte[8];
/* 100 */     for (int i = 0; i < this._textptr.length; ++i)
/*     */     {
/* 102 */       this._textptr[i] = 0;
/*     */     }
/* 104 */     this._time = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public SrvTextImageData(byte[] paramArrayOfByte)
/*     */   {
/* 113 */     this._textptr = new byte[8];
/* 114 */     for (int i = 0; i < this._textptr.length; ++i)
/*     */     {
/* 116 */       this._textptr[i] = 0;
/*     */     }
/* 118 */     this._time = System.currentTimeMillis();
/*     */ 
/* 120 */     if (paramArrayOfByte == null)
/*     */       return;
/* 122 */     this._bytes = paramArrayOfByte;
/*     */   }
/*     */ 
/*     */   public SrvTextImageData(byte[] paramArrayOfByte1, long paramLong, byte[] paramArrayOfByte2)
/*     */   {
/* 132 */     this._textptr = paramArrayOfByte1;
/* 133 */     if (paramArrayOfByte2 != null)
/*     */     {
/* 135 */       this._bytes = paramArrayOfByte2;
/*     */     }
/* 137 */     this._time = paramLong;
/*     */   }
/*     */ 
/*     */   public SrvTextImageData(byte[] paramArrayOfByte, long paramLong, int paramInt, InputStream paramInputStream)
/*     */   {
/* 147 */     this._textptr = paramArrayOfByte;
/* 148 */     if (paramInputStream != null)
/*     */     {
/* 150 */       this._byteStream = paramInputStream;
/*     */     }
/* 152 */     this._time = paramLong;
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 159 */     if ((this._bytes != null) && (this._bytes.length != 0))
/*     */     {
/* 162 */       return this._bytes.length;
/*     */     }
/*     */ 
/* 166 */     return this._length;
/*     */   }
/*     */ 
/*     */   public int getTextPtrLength()
/*     */   {
/* 173 */     return this._textptr.length;
/*     */   }
/*     */ 
/*     */   public byte[] getTextPtr()
/*     */   {
/* 179 */     return this._textptr;
/*     */   }
/*     */ 
/*     */   public void setTextPtr(byte[] paramArrayOfByte)
/*     */   {
/* 185 */     this._textptr = paramArrayOfByte;
/*     */   }
/*     */ 
/*     */   public long getTimeStamp()
/*     */   {
/* 195 */     return this._time;
/*     */   }
/*     */ 
/*     */   public void setTextPtr(String paramString)
/*     */   {
/* 201 */     this._textptr = paramString.getBytes();
/*     */   }
/*     */ 
/*     */   public boolean isNull()
/*     */   {
/* 217 */     return (this._bytes == null) && (this._bytes.length == 0) && (this._length == 0);
/*     */   }
/*     */ 
/*     */   public void readBytes(SrvDataInputStream paramSrvDataInputStream)
/*     */     throws IOException
/*     */   {
/* 233 */     this._bytes = new byte[paramSrvDataInputStream.readInt()];
/*     */ 
/* 236 */     paramSrvDataInputStream.read(this._bytes);
/*     */   }
/*     */ 
/*     */   public void sendWritetextResult(SrvSession paramSrvSession)
/*     */     throws IOException
/*     */   {
/* 250 */     SrvParamFormatToken localSrvParamFormatToken = new SrvParamFormatToken();
/* 251 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = new SrvJavaTypeFormatter(localSrvParamFormatToken, paramSrvSession.getClientCapability());
/*     */ 
/* 254 */     Date localDate = new Date();
/*     */ 
/* 256 */     localSrvJavaTypeFormatter.addFormat(localDate, "pic", 1, 0);
/* 257 */     paramSrvSession.send(localSrvParamFormatToken);
/*     */ 
/* 260 */     SrvParamsToken localSrvParamsToken = new SrvParamsToken();
/* 261 */     Object[] arrayOfObject = { localDate };
/*     */ 
/* 266 */     localSrvJavaTypeFormatter.convertData(localSrvParamsToken, arrayOfObject);
/* 267 */     paramSrvSession.send(localSrvParamsToken);
/*     */ 
/* 270 */     paramSrvSession.sendDone(1, false, true, false);
/*     */   }
/*     */ 
/*     */   public void setTableName(String paramString) {
/* 274 */     this._tableName = paramString;
/*     */   }
/*     */ 
/*     */   public String getTableName() {
/* 278 */     return (this._tableName == null) ? "" : this._tableName;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvTextImageData
 * JD-Core Version:    0.5.4
 */