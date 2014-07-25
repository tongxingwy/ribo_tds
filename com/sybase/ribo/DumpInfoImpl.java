/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.tds.SrvVarCharData;
/*     */ import com.sybase.jdbc3.tds.TdsDateTime;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.StringWriter;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class DumpInfoImpl
/*     */   implements DumpInfo
/*     */ {
/*  47 */   private static int BUF_SIZE = 1024;
/*     */   private Vector _table;
/*     */ 
/*     */   public DumpInfoImpl()
/*     */   {
/*  62 */     this._table = new Vector();
/*     */   }
/*     */ 
/*     */   public void addInfo(String label, int size, Object value)
/*     */   {
/*  71 */     this._table.addElement(new TableEntry(label, size, value));
/*     */   }
/*     */ 
/*     */   public void addInfo(DumpInfo info)
/*     */   {
/*  79 */     if (info == null)
/*     */       return;
/*  81 */     Enumeration sybenum = ((DumpInfoImpl)info)._table.elements();
/*  82 */     while (sybenum.hasMoreElements())
/*     */     {
/*  84 */       this._table.addElement(sybenum.nextElement());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addValue(String label, int size, Object data)
/*     */     throws IOException
/*     */   {
/* 101 */     addValue(label, size, data, null);
/*     */   }
/*     */ 
/*     */   public void addValue(String label, int size, Object data, String encType)
/*     */     throws IOException
/*     */   {
/* 119 */     if (data instanceof byte[])
/*     */     {
/* 122 */       addHex(label, size, (byte[])data);
/*     */     }
/* 124 */     else if (data instanceof InputStreamReader)
/*     */     {
/* 127 */       InputStreamReader reader = (InputStreamReader)data;
/*     */ 
/* 129 */       StringWriter writer = new StringWriter();
/* 130 */       char[] c = new char[BUF_SIZE];
/*     */       while (true)
/*     */       {
/* 133 */         int n = reader.read(c, 0, BUF_SIZE);
/* 134 */         if (n <= 0)
/*     */         {
/* 136 */           reader.close();
/* 137 */           break;
/*     */         }
/* 139 */         writer.write(c, 0, n);
/*     */       }
/* 141 */       String value = writer.toString();
/* 142 */       addText(label, value.length(), value);
/*     */ 
/* 144 */       if (encType != null)
/*     */       {
/* 146 */         byte[] uniBytes = value.getBytes(encType);
/* 147 */         addValue("    (as bytes)", uniBytes.length, uniBytes);
/*     */       }
/*     */     }
/* 150 */     else if (data instanceof InputStream)
/*     */     {
/* 153 */       InputStream is = (InputStream)data;
/* 154 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 155 */       byte[] b = new byte[BUF_SIZE];
/*     */       while (true)
/*     */       {
/* 158 */         int n = is.read(b, 0, BUF_SIZE);
/* 159 */         if (n <= 0)
/*     */         {
/* 161 */           is.close();
/* 162 */           break;
/*     */         }
/* 164 */         baos.write(b, 0, n);
/*     */       }
/* 166 */       byte[] value = baos.toByteArray();
/* 167 */       addHex(label, value.length, value);
/*     */     }
/* 169 */     else if (data instanceof SrvVarCharData)
/*     */     {
/* 173 */       SrvVarCharData varchar = (SrvVarCharData)data;
/* 174 */       addInfo(label, size, "\"" + varchar.getString() + "\"" + " (0x" + HexConverts.hexConvert(varchar.getBytes()) + ")");
/*     */     }
/* 178 */     else if (data instanceof String)
/*     */     {
/* 181 */       addText(label, size, (String)data);
/* 182 */       if (encType == null)
/*     */         return;
/* 184 */       byte[] uniBytes = ((String)data).getBytes(encType);
/* 185 */       addValue("    (as bytes)", uniBytes.length, uniBytes);
/*     */     }
/* 188 */     else if (data instanceof BigDecimal)
/*     */     {
/* 190 */       BigDecimal bd = (BigDecimal)data;
/* 191 */       addInfo(label, size, bd);
/*     */     }
/* 198 */     else if (data instanceof Float)
/*     */     {
/* 200 */       Float fl = (Float)data;
/* 201 */       addInfo(label, size, fl);
/*     */     }
/* 203 */     else if (data instanceof Number)
/*     */     {
/* 205 */       Number number = (Number)data;
/* 206 */       addInfo(label, size, number + " (0x" + HexConverts.hexConvert(number.longValue(), size) + ")");
/*     */     }
/* 210 */     else if (data instanceof TdsDateTime)
/*     */     {
/* 216 */       addInfo(label, size, ((TdsDateTime)data).stringValue());
/*     */     }
/*     */     else
/*     */     {
/* 223 */       addInfo(label, size, data);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addInt(String label, int size, long value)
/*     */   {
/* 233 */     addInfo(label, size, new Long(value));
/*     */   }
/*     */ 
/*     */   public void addBitfield(String label, int size, int value, String[] names)
/*     */   {
/* 241 */     addInfo(label, size, bitfieldToString(size, value, names));
/*     */   }
/*     */ 
/*     */   public void addField(String label, int size, int value, String[] names)
/*     */   {
/* 249 */     addInfo(label, size, fieldToString(size, value, names));
/*     */   }
/*     */ 
/*     */   public void addBitfield(String label, int size, byte[] value, String[] names)
/*     */   {
/* 259 */     addInfo(label, size, bitfieldToString(size, value, names));
/*     */   }
/*     */ 
/*     */   public void addText(String label, int size, String value)
/*     */   {
/* 270 */     int trimmedSize = value.length();
/* 271 */     if (size < trimmedSize)
/*     */     {
/* 273 */       trimmedSize = size;
/*     */     }
/* 275 */     addInfo(label, size, "\"" + value.substring(0, trimmedSize) + "\"");
/*     */   }
/*     */ 
/*     */   public void addHex(String label, int size, long value)
/*     */   {
/* 283 */     String str = "0x" + HexConverts.hexConvert(value, size);
/* 284 */     addInfo(label, size, str);
/*     */   }
/*     */ 
/*     */   public void addHex(String label, int size, int value)
/*     */   {
/* 292 */     String str = "0x" + HexConverts.hexConvert(value, size);
/* 293 */     addInfo(label, size, str);
/*     */   }
/*     */ 
/*     */   public void addHex(String label, int size, byte[] value)
/*     */   {
/* 302 */     String str = "0x" + HexConverts.hexConvert(value);
/* 303 */     addInfo(label, size, str);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 321 */     String indentation = "  ";
/* 322 */     String answer = new String();
/*     */ 
/* 325 */     boolean tdsData = false;
/* 326 */     boolean tdsfmt = false;
/*     */ 
/* 328 */     int len = this._table.size();
/* 329 */     for (int i = 0; i < len; ++i)
/*     */     {
/* 331 */       String line = new String();
/* 332 */       TableEntry entry = (TableEntry)this._table.elementAt(i);
/*     */ 
/* 335 */       if (entry.label.equals("Token"))
/*     */       {
/* 337 */         line = line + entry.value;
/*     */       }
/* 339 */       else if (entry.label.equals("TDSData"))
/*     */       {
/* 341 */         if (tdsData)
/*     */         {
/* 343 */           line = line + "\n";
/*     */         }
/* 345 */         tdsData = true;
/* 346 */         line = line + indentation + entry.value;
/*     */       }
/* 348 */       else if (entry.label.equals("TDSFmt"))
/*     */       {
/* 350 */         tdsfmt = true;
/* 351 */         line = line + indentation + entry.value;
/*     */       }
/*     */       else
/*     */       {
/* 355 */         if (tdsfmt)
/*     */         {
/* 357 */           line = line + indentation;
/*     */         }
/* 359 */         line = line + indentation + entry.label;
/* 360 */         line = line + " [" + entry.size + "]";
/* 361 */         line = line + ":";
/*     */ 
/* 363 */         if (tdsData)
/*     */         {
/* 366 */           line = line + "\t";
/*     */         }
/*     */         else
/*     */         {
/* 370 */           for (int j = line.length(); j < 32; ++j)
/*     */           {
/* 372 */             line = line + " ";
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 379 */         if (entry.size == 0)
/*     */         {
/* 381 */           line = line + "[" + entry.value + "]";
/*     */         }
/*     */         else
/*     */         {
/* 385 */           line = line + entry.value;
/* 386 */           if ((tdsData) && 
/* 389 */             (entry.label.equalsIgnoreCase("length")))
/*     */           {
/* 394 */             int valueLen = ((Integer)entry.value).toString().length();
/*     */ 
/* 396 */             for (int j = 0; j < 4 - valueLen; ++j)
/*     */             {
/* 398 */               line = line + " ";
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 404 */       answer = answer + line + ((tdsData) ? "" : "\n");
/*     */     }
/* 406 */     answer = answer + ((tdsData) ? "\n" : "");
/* 407 */     return answer;
/*     */   }
/*     */ 
/*     */   private String bitfieldToString(int size, int value, String[] names)
/*     */   {
/* 421 */     String answer = "";
/* 422 */     if (value == 0)
/*     */     {
/* 424 */       answer = answer + names[0];
/*     */     }
/*     */     else
/*     */     {
/* 428 */       int field = value;
/* 429 */       int bit = 0;
/* 430 */       while (field != 0)
/*     */       {
/* 432 */         if ((field & 0x1) != 0)
/*     */         {
/* 434 */           if (answer.length() > 0)
/*     */           {
/* 436 */             answer = answer + " + ";
/*     */           }
/* 438 */           if (bit + 1 >= names.length)
/*     */           {
/* 440 */             answer = answer + "<unrecognized>";
/*     */           }
/*     */           else
/*     */           {
/* 444 */             answer = answer + names[(bit + 1)];
/*     */           }
/*     */         }
/* 447 */         ++bit;
/* 448 */         field >>= 1;
/*     */       }
/*     */     }
/* 451 */     return answer + " (0x" + HexConverts.hexConvert(value, size) + ")";
/*     */   }
/*     */ 
/*     */   private String bitfieldToString(int size, byte[] value, String[] names)
/*     */   {
/* 464 */     String answer = "\n";
/*     */ 
/* 466 */     return answer;
/*     */   }
/*     */ 
/*     */   public String fieldToString(int size, int value, String[] names)
/*     */   {
/*     */     String answer;
/*     */     String answer;
/*     */     try
/*     */     {
/* 481 */       answer = names[value];
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException e)
/*     */     {
/* 485 */       answer = "<unrecognized>";
/*     */     }
/*     */ 
/* 488 */     return answer + " (0x" + HexConverts.hexConvert(value, size) + ")";
/*     */   }
/*     */ 
/*     */   private class TableEntry
/*     */   {
/*     */     protected String label;
/*     */     protected int size;
/*     */     protected Object value;
/*     */ 
/*     */     protected TableEntry(String label, int size, Object value)
/*     */     {
/* 506 */       this.label = label;
/* 507 */       this.size = size;
/* 508 */       this.value = value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.DumpInfoImpl
 * JD-Core Version:    0.5.4
 */