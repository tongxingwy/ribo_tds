/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public final class SrvPdu
/*     */   implements Dumpable
/*     */ {
/*     */   public static final int NETHDRSIZE = 8;
/*     */   int _msgType;
/*     */   int _msgStatus;
/*     */   int _length;
/*     */   int _channel;
/*     */   int _packet;
/*     */   int _window;
/*     */   int _packetSize;
/*     */   String _charset;
/*     */   boolean _eom;
/*     */   boolean _attention;
/*     */   boolean _inBulk;
/*     */   boolean _bulkNotified;
/*     */ 
/*     */   public SrvPdu()
/*     */   {
/*  58 */     this._msgType = 0;
/*  59 */     this._msgStatus = 0;
/*  60 */     this._length = 0;
/*  61 */     this._channel = 0;
/*  62 */     this._packet = 0;
/*  63 */     this._window = 0;
/*     */ 
/*  65 */     this._packetSize = 512;
/*     */ 
/*  67 */     this._charset = new String("ISO8859_1");
/*     */ 
/*  70 */     this._eom = false;
/*  71 */     this._attention = false;
/*     */ 
/*  74 */     this._inBulk = false;
/*     */ 
/*  76 */     this._bulkNotified = true;
/*     */   }
/*     */ 
/*     */   protected void setpdu()
/*     */   {
/*  82 */     this._msgType = 15;
/*  83 */     this._msgStatus = 0;
/*  84 */     this._length = 0;
/*     */   }
/*     */ 
/*     */   protected void setpdu(byte[] paramArrayOfByte, boolean paramBoolean, int paramInt1, int paramInt2)
/*     */   {
/*  91 */     this._msgType = paramInt2;
/*     */ 
/*  93 */     if (paramBoolean)
/*  94 */       this._msgStatus |= 1;
/*     */     else {
/*  96 */       this._msgStatus |= 0;
/*     */     }
/*  98 */     this._length = paramInt1;
/*     */ 
/* 101 */     paramArrayOfByte[0] = (byte)this._msgType;
/* 102 */     paramArrayOfByte[1] = (byte)this._msgStatus;
/* 103 */     paramArrayOfByte[2] = (byte)(this._length >>> 8 & 0xFF);
/* 104 */     paramArrayOfByte[3] = (byte)(this._length & 0xFF);
/* 105 */     paramArrayOfByte[4] = (byte)(this._channel >>> 8 & 0xFF);
/* 106 */     paramArrayOfByte[5] = (byte)(this._channel & 0xFF);
/* 107 */     paramArrayOfByte[6] = (byte)this._packet;
/* 108 */     paramArrayOfByte[7] = (byte)this._window;
/*     */   }
/*     */ 
/*     */   public void setpdu(byte[] paramArrayOfByte)
/*     */     throws SrvAttentionException
/*     */   {
/* 118 */     this._msgType = paramArrayOfByte[0];
/* 119 */     this._msgStatus = paramArrayOfByte[1];
/* 120 */     this._length = (((paramArrayOfByte[2] & 0xFF) << 8) + (paramArrayOfByte[3] & 0xFF));
/* 121 */     this._channel = (((paramArrayOfByte[4] & 0xFF) << 8) + (paramArrayOfByte[5] & 0xFF));
/* 122 */     this._packet = paramArrayOfByte[6];
/* 123 */     this._window = paramArrayOfByte[7];
/*     */ 
/* 126 */     if (this._msgType == 6)
/*     */     {
/* 129 */       throw new SrvAttentionException();
/*     */     }
/*     */ 
/* 134 */     if ((this._msgType != 7) || 
/* 136 */       (this._inBulk))
/*     */       return;
/* 138 */     this._inBulk = true;
/* 139 */     this._bulkNotified = false;
/*     */   }
/*     */ 
/*     */   protected boolean bulkOccurred()
/*     */   {
/* 154 */     if (!this._bulkNotified)
/*     */     {
/* 156 */       this._bulkNotified = true;
/* 157 */       return true;
/*     */     }
/* 159 */     return false;
/*     */   }
/*     */ 
/*     */   public int dataLength()
/*     */   {
/* 165 */     return this._length - 8;
/*     */   }
/*     */ 
/*     */   protected static int size()
/*     */   {
/* 172 */     return 8;
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/* 179 */     this._msgStatus = 0;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 186 */     return "PDU Header: type " + this._msgType + ", status " + this._msgStatus + ", length " + this._length + ", channel " + this._channel + ", window " + this._window + ", packet size " + this._packetSize;
/*     */   }
/*     */ 
/*     */   public int getStatus()
/*     */   {
/* 198 */     return this._msgStatus;
/*     */   }
/*     */ 
/*     */   public int getType()
/*     */   {
/* 204 */     return this._msgType;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 220 */     DumpInfo localDumpInfo = null;
/* 221 */     if (paramDumpFilter.includesToken(256))
/*     */     {
/* 223 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 224 */       localDumpInfo.addInfo("Token", 0, "PDU Header");
/* 225 */       String[] arrayOfString1 = { "<unrecognized>", "BUF_LANG", "BUF_LOGIN", "BUF_RPC", "BUF_RESPONSE", "BUF_UNFMT", "BUF_ATTN", "BUF_BULK ", "BUF_SETUP", "BUF_CLOSE ", "BUF_ERROR", "BUF_PROTACK ", "BUF_ECHO", "BUF_LOGOUT", "BUF_ENDPARAM", "BUF_NORMAL", "BUF_URGENT", "BUF_MIGRATE" };
/*     */ 
/* 233 */       localDumpInfo.addField("TDS Packet Type", 1, this._msgType, arrayOfString1);
/* 234 */       String[] arrayOfString2 = { "BUFSTAT_BEGIN", "BUFSTAT_EOM", "BUFSTAT_ATTNACK", "BUFSTAT_ATTN", "BUFSTAT_EVENT" };
/*     */ 
/* 240 */       localDumpInfo.addBitfield("Status", 1, this._msgStatus, arrayOfString2);
/* 241 */       localDumpInfo.addInt("Length", 2, this._length);
/* 242 */       localDumpInfo.addInt("Channel", 2, this._channel);
/* 243 */       localDumpInfo.addInt("Packet No.", 1, this._packet);
/* 244 */       localDumpInfo.addInt("Window", 1, this._window);
/*     */     }
/* 246 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 255 */     return 256;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvPdu
 * JD-Core Version:    0.5.4
 */