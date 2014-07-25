/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.Debug;
/*     */ import java.io.CharConversionException;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import sun.io.CharToByteConverter;
/*     */ 
/*     */ public class SrvDataOutputStream extends TdsOutputStream
/*     */ {
/*  37 */   private SrvPdu _pdu = new SrvPdu();
/*  38 */   private boolean _bufstatEvent = false;
/*  39 */   CharToByteConverter _ctbc = null;
/*     */ 
/*  43 */   protected int _last = 7;
/*     */ 
/*  48 */   private byte[] _buf = new byte[this._pdu._packetSize];
/*     */ 
/*     */   public SrvDataOutputStream(OutputStream paramOutputStream) throws Exception
/*     */   {
/*  52 */     super(paramOutputStream);
/*  53 */     this._ctbc = CharToByteConverter.getConverter(this._pdu._charset);
/*     */   }
/*     */ 
/*     */   public void setEncoding(String paramString)
/*     */     throws Exception
/*     */   {
/*  59 */     this._pdu._charset = paramString;
/*  60 */     this._ctbc = CharToByteConverter.getConverter(this._pdu._charset);
/*     */   }
/*     */ 
/*     */   public void setPacketSize(int paramInt)
/*     */   {
/*  66 */     Debug.asrt(this._last == 7, "Buffer in use, cannot reset packetsize");
/*  67 */     this._pdu._packetSize = paramInt;
/*  68 */     this._buf = new byte[this._pdu._packetSize];
/*     */   }
/*     */ 
/*     */   public void setAttention()
/*     */   {
/*  74 */     this._pdu._msgStatus |= 2;
/*     */   }
/*     */ 
/*     */   public void setNotify()
/*     */   {
/*  80 */     this._bufstatEvent = true;
/*     */   }
/*     */ 
/*     */   public void write(int paramInt)
/*     */     throws IOException
/*     */   {
/*  87 */     if (this._last == this._buf.length - 1)
/*     */     {
/*  89 */       flush(false);
/*     */     }
/*  91 */     this._buf[(++this._last)] = (byte)paramInt;
/*     */   }
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 100 */     write(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 108 */     int i = paramInt1;
/* 109 */     while (paramInt2 > 0)
/*     */     {
/* 111 */       if (this._last >= this._buf.length - 1)
/*     */       {
/* 113 */         flush(false);
/*     */       }
/* 115 */       int j = this._buf.length - this._last - 1;
/* 116 */       int k = (j < paramInt2 - paramInt1) ? j : paramInt2 - paramInt1;
/* 117 */       System.arraycopy(paramArrayOfByte, i, this._buf, this._last + 1, k);
/* 118 */       this._last += k;
/* 119 */       paramInt2 -= k;
/* 120 */       i += k;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flush(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 133 */     if (this._bufstatEvent)
/*     */     {
/* 135 */       this._pdu._msgStatus |= 8;
/* 136 */       this._pdu.setpdu(this._buf, paramBoolean, this._last + 1, 16);
/*     */     }
/*     */     else
/*     */     {
/* 141 */       this._pdu.setpdu(this._buf, paramBoolean, this._last + 1, 15);
/*     */     }
/*     */ 
/* 149 */     synchronized (this.out)
/*     */     {
/* 151 */       this.out.write(this._buf, 0, this._pdu._length);
/* 152 */       this.out.flush();
/*     */     }
/* 154 */     this._pdu.setpdu();
/* 155 */     this._last = 7;
/*     */   }
/*     */ 
/*     */   public String getCharset()
/*     */   {
/* 163 */     return this._pdu._charset;
/*     */   }
/*     */ 
/*     */   public int getPacketSize()
/*     */   {
/* 170 */     return this._pdu._packetSize;
/*     */   }
/*     */ 
/*     */   protected byte[] toBytes(String paramString)
/*     */     throws CharConversionException
/*     */   {
/* 179 */     if (paramString == null)
/*     */     {
/* 181 */       return null;
/*     */     }
/* 183 */     return this._ctbc.convertAll(paramString.toCharArray());
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDataOutputStream
 * JD-Core Version:    0.5.4
 */