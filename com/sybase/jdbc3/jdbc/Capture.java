/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class Capture
/*     */   implements com.sybase.jdbcx.Capture
/*     */ {
/*     */   public static final int OFFLINE = 0;
/*     */   public static final int PAUSE = 1;
/*     */   public static final int RECORD = 2;
/*     */   public static final int TDS_DUMP_VSN_0 = 0;
/*     */   public static final int TDS_DUMP_VSN_1 = 1;
/*     */   public static final String PROGRAM_NAME = "Capture";
/*     */   public static final String CLIENT_NAME = "jConnect Application";
/*     */   public static final String SERVER_NAME = "Application Server";
/*     */   public static final int NAME_LENGTH = 256;
/*     */   public static final int REQUEST = 1;
/*     */   public static final int RESPONSE = 2;
/*     */   private DataOutputStream _captureStream;
/*     */   private TraceOutputStream _out;
/*     */   private TraceInputStream _in;
/*     */   private String _programName;
/*     */ 
/*     */   public Capture(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 109 */     this(paramOutputStream, "Capture");
/*     */   }
/*     */ 
/*     */   public Capture(OutputStream paramOutputStream, String paramString)
/*     */     throws IOException
/*     */   {
/* 123 */     this._programName = paramString;
/* 124 */     this._captureStream = new DataOutputStream(paramOutputStream);
/* 125 */     writeStreamHeader();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void resume()
/*     */   {
/* 138 */     if ((this._in == null) || (this._out == null))
/*     */       return;
/* 140 */     this._in.setState(2);
/* 141 */     this._out.setState(2);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void pause()
/*     */   {
/* 152 */     if ((this._in == null) || (this._out == null))
/*     */       return;
/* 154 */     this._in.setState(1);
/* 155 */     this._out.setState(1);
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream(InputStream paramInputStream)
/*     */   {
/* 168 */     if (this._in == null)
/*     */     {
/* 170 */       this._in = new TraceInputStream(this, paramInputStream, 2);
/*     */     }
/* 172 */     return this._in;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream(OutputStream paramOutputStream)
/*     */   {
/* 179 */     if (this._out == null)
/*     */     {
/* 181 */       this._out = new TraceOutputStream(this, paramOutputStream, 2);
/*     */     }
/* 183 */     return this._out;
/*     */   }
/*     */ 
/*     */   public synchronized void writeBuffer(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 193 */     this._captureStream.writeInt(paramInt1);
/* 194 */     this._captureStream.writeInt(paramInt3);
/* 195 */     this._captureStream.write(paramArrayOfByte, paramInt2, paramInt3);
/* 196 */     this._captureStream.flush();
/*     */   }
/*     */ 
/*     */   private void writeStreamHeader()
/*     */     throws IOException
/*     */   {
/* 204 */     this._captureStream.writeInt(1);
/*     */ 
/* 206 */     this._captureStream.writeInt(885842144);
/*     */ 
/* 208 */     this._captureStream.write(getNameByteArray(this._programName), 0, 256);
/*     */ 
/* 210 */     this._captureStream.writeInt(0);
/* 211 */     this._captureStream.writeInt(0);
/*     */ 
/* 213 */     this._captureStream.write(getNameByteArray("jConnect Application"), 0, 256);
/*     */ 
/* 215 */     this._captureStream.write(getNameByteArray("Application Server"), 0, 256);
/* 216 */     this._captureStream.flush();
/*     */   }
/*     */ 
/*     */   private byte[] getNameByteArray(String paramString)
/*     */   {
/* 222 */     byte[] arrayOfByte = new byte[256];
/* 223 */     int i = (paramString.length() < 256) ? paramString.length() : 256;
/* 224 */     System.arraycopy(paramString.getBytes(), 0, arrayOfByte, 0, i);
/* 225 */     return arrayOfByte;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.Capture
 * JD-Core Version:    0.5.4
 */