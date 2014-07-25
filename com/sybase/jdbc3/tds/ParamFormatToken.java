/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.Param;
/*     */ import com.sybase.jdbc3.jdbc.ParamManager;
/*     */ import com.sybase.jdbc3.jdbc.Protocol;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class ParamFormatToken extends RowFormatToken
/*     */ {
/*     */   public static final int MAX_PARAMFMT_LENGTH = 65535;
/*     */   protected DataFormat[] _param;
/*     */   protected ParamManager _paramMgr;
/*     */   protected int _numParams;
/*  44 */   private int _length = -1;
/*     */ 
/*     */   public ParamFormatToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ParamFormatToken(ParamManager paramParamManager, boolean paramBoolean)
/*     */     throws IOException, SQLException
/*     */   {
/*  62 */     this._numParams = paramParamManager.makeFormats(paramBoolean);
/*  63 */     this._paramMgr = paramParamManager;
/*     */   }
/*     */ 
/*     */   public ParamFormatToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  74 */     readLength(paramTdsInputStream);
/*  75 */     this._numColumns = paramTdsInputStream.readShort();
/*  76 */     addDataFormats(paramTdsInputStream, this._numColumns);
/*     */   }
/*     */ 
/*     */   public ParamFormatToken(ParamManager paramParamManager, Param[] paramArrayOfParam, Protocol paramProtocol, byte paramByte, boolean paramBoolean)
/*     */     throws IOException, SQLException
/*     */   {
/*  83 */     this._paramMgr = paramParamManager;
/*  84 */     TdsParam localTdsParam = null;
/*  85 */     this._length = 0;
/*  86 */     int i = paramArrayOfParam.length;
/*  87 */     for (int j = 0; j < i; ++j)
/*     */     {
/*  89 */       localTdsParam = (TdsParam)paramArrayOfParam[j];
/*  90 */       if (!localTdsParam.makeFormat(paramProtocol, paramByte))
/*     */         continue;
/*  92 */       this._length += localTdsParam._inDataFmt.length();
/*  93 */       this._numParams += 1;
/*     */ 
/*  99 */       if ((!paramBoolean) && (((!paramProtocol.isSuppressParamFormatSupportedAndSet()) || (paramByte != 0)))) {
/*     */         continue;
/*     */       }
/* 102 */       localTdsParam._inDataFmt._precision = this._paramMgr.getParamMD(j)._precision;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected long getLength()
/*     */   {
/* 114 */     int i = 2;
/* 115 */     if (this._length < 0)
/*     */     {
/* 117 */       i += this._paramMgr.getLength();
/*     */     }
/*     */     else
/*     */     {
/* 121 */       i += this._length;
/*     */     }
/*     */ 
/* 127 */     return i;
/*     */   }
/*     */ 
/*     */   public int getFormatCount()
/*     */   {
/* 138 */     return this._numParams;
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 156 */     if (getLength() > 65535L)
/*     */     {
/* 158 */       ErrorMessage.raiseIOException("JZ0PC");
/*     */     }
/*     */     else
/*     */     {
/* 163 */       paramTdsOutputStream.writeByte(236);
/* 164 */       paramTdsOutputStream.writeShort((int)getLength());
/* 165 */       paramTdsOutputStream.writeShort(getFormatCount());
/* 166 */       sendFormat(paramTdsOutputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void sendFormat(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 177 */     this._paramMgr.sendFormats(paramTdsOutputStream);
/*     */   }
/*     */ 
/*     */   public void sendAddBatch(Param[] paramArrayOfParam, TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 191 */     long l = getLength();
/* 192 */     if (l > 65535L)
/*     */     {
/* 194 */       ErrorMessage.raiseIOException("JZ0PC");
/*     */     }
/*     */     else
/*     */     {
/* 198 */       paramTdsOutputStream.writeByte(236);
/* 199 */       paramTdsOutputStream.writeShort((int)l);
/* 200 */       paramTdsOutputStream.writeShort(getFormatCount());
/* 201 */       TdsParam localTdsParam = null;
/* 202 */       for (int i = 0; i < paramArrayOfParam.length; ++i)
/*     */       {
/* 204 */         localTdsParam = (TdsParam)paramArrayOfParam[i];
/* 205 */         localTdsParam.sendFormat(paramTdsOutputStream);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.ParamFormatToken
 * JD-Core Version:    0.5.4
 */