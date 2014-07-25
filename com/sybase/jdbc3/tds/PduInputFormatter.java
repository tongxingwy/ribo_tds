/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.BufferInterval;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class PduInputFormatter extends InputStream
/*     */ {
/*     */   private static final int LOW_BYTE = 255;
/*     */   private int _nextIn;
/*     */   private int _packetEnd;
/*     */   private int _status;
/*     */   private int _type;
/*     */   private BufferInterval _pdu;
/*  42 */   private byte[] _buf = null;
/*     */   private TdsProtocolContext _tpc;
/*     */ 
/*     */   public PduInputFormatter(TdsProtocolContext paramTdsProtocolContext)
/*     */   {
/*  53 */     this._nextIn = 0;
/*  54 */     this._packetEnd = 0;
/*  55 */     this._status = 1;
/*  56 */     this._tpc = paramTdsProtocolContext;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*     */     do
/*  71 */       if (this._nextIn < this._packetEnd)
/*     */         break label20;
/*  73 */     while (readPacket());
/*     */ 
/*  76 */     return -1;
/*     */ 
/*  80 */     label20: int i = this._buf[(this._nextIn++)] & 0xFF;
/*  81 */     return i;
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 105 */     int i = paramInt2;
/*     */ 
/* 109 */     while (i > 0)
/*     */     {
/* 112 */       int j = this._packetEnd - this._nextIn;
/* 113 */       while (j <= 0)
/*     */       {
/* 115 */         if (!readPacket())
/*     */         {
/* 117 */           if (i == paramInt2)
/*     */           {
/* 120 */             return -1;
/*     */           }
/*     */ 
/* 124 */           return paramInt2 - i;
/*     */         }
/*     */ 
/* 127 */         j = this._packetEnd - this._nextIn;
/*     */       }
/*     */ 
/* 130 */       int k = (j > i) ? i : j;
/*     */ 
/* 134 */       System.arraycopy(this._buf, this._nextIn, paramArrayOfByte, paramInt1, k);
/* 135 */       i -= k;
/* 136 */       paramInt1 += k;
/* 137 */       this._nextIn += k;
/*     */     }
/* 139 */     return paramInt2;
/*     */   }
/*     */ 
/*     */   public long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 151 */     int i = this._packetEnd - this._nextIn;
/* 152 */     int j = (int)paramLong;
/*     */ 
/* 154 */     while (i < j)
/*     */     {
/* 156 */       j -= i;
/* 157 */       this._nextIn += i;
/* 158 */       if (!readPacket())
/*     */       {
/* 165 */         return paramLong;
/*     */       }
/* 167 */       i = this._packetEnd - this._nextIn;
/*     */     }
/* 169 */     this._nextIn += j;
/* 170 */     return paramLong;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 177 */     int i = this._tpc.available();
/* 178 */     i += this._packetEnd - this._nextIn;
/* 179 */     return i;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */     while (true)
/*     */     {
/* 192 */       if ((this._status & 0x1) == 0);
/*     */       try
/*     */       {
/* 196 */         readPacket();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 203 */         this._nextIn = 0;
/* 204 */         this._packetEnd = 0;
/* 205 */         if (this._pdu == null)
/*     */           return;
/* 207 */         this._pdu.free();
/* 208 */         this._pdu = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean readPacket()
/*     */     throws IOException
/*     */   {
/* 235 */     if (this._pdu != null)
/*     */     {
/* 237 */       this._pdu.free();
/* 238 */       this._pdu = null;
/*     */     }
/* 240 */     this._pdu = this._tpc.getChunk();
/* 241 */     if (this._pdu == null)
/*     */     {
/* 244 */       this._nextIn = 0;
/* 245 */       this._packetEnd = 0;
/* 246 */       return false;
/*     */     }
/* 248 */     this._buf = this._pdu._buf;
/*     */ 
/* 250 */     this._nextIn = this._pdu._offset;
/* 251 */     this._type = (this._buf[(this._nextIn++)] & 0xFF);
/* 252 */     this._status = (this._buf[(this._nextIn++)] & 0xFF);
/* 253 */     int i = ((this._buf[(this._nextIn++)] & 0xFF) << 8) + (0xFF & this._buf[(this._nextIn++)]);
/* 254 */     this._nextIn += 4;
/* 255 */     this._packetEnd = (this._nextIn + i - 8);
/*     */ 
/* 262 */     if ((this._status & 0x2) != 0)
/*     */     {
/* 265 */       this._pdu.free();
/* 266 */       this._pdu = null;
/* 267 */       this._nextIn = 0;
/* 268 */       this._packetEnd = 0;
/*     */ 
/* 271 */       this._tpc._tdsToken = new DoneToken(32, 0, 0);
/*     */ 
/* 274 */       return false;
/*     */     }
/*     */ 
/* 277 */     return true;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.PduInputFormatter
 * JD-Core Version:    0.5.4
 */