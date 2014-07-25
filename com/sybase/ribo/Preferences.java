/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.tds.Iana;
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class Preferences
/*     */ {
/*     */   protected static final String VERSION_STRING = "Ribo 3.0 (Build 0424)";
/*     */   protected static final int UNKNOWN_MODE = -1;
/*     */   protected static final int CAPTURE_MODE = 0;
/*     */   protected static final int ANALYZE_MODE = 1;
/*     */   protected static final String CAPTURE_FILE_EXT = ".tds";
/*     */   protected static final String DUMP_FILE_EXT = ".txt";
/*     */   private static final int NO_HELP = 0;
/*     */   private static final int HELP_USAGE = 1;
/*     */   private static final int HELP_UNEXPECTED_PARM = 2;
/*     */   private static final int HELP_EXPECTED_PARM_MISSING = 3;
/*     */   private static final int HELP_INVALID_FLAG = 4;
/*     */   private static final int HELP_VERSION = 5;
/*     */   private static final int DEFAULT_LISTEN_PORT = 5005;
/*     */   private static final String DEFAULT_HOSTNAME = "localhost";
/*     */   private static final int DEFAULT_HOST_PORT = 2638;
/*     */   private static final String DEFAULT_FILE_PREFIX = "cap";
/*     */   protected int _mode;
/*     */   protected String _hostName;
/*     */   protected int _hostPort;
/*     */   protected String _filePrefix;
/*     */   protected String _dumpFileName;
/*     */   protected boolean _showGUI;
/*     */   protected String _captureFileName;
/*     */   protected int _listenPort;
/*     */   protected boolean _translateToFile;
/*     */   protected boolean _translateToStream;
/*     */   protected String _encoding;
/*     */   protected int _maxOutputLines;
/*     */   protected int _chunkSize;
/*     */   protected transient DumpFilter _filter;
/*     */   private transient String _filterName;
/*     */ 
/*     */   private Preferences()
/*     */   {
/*  84 */     this._mode = -1;
/*  85 */     this._hostName = "localhost";
/*  86 */     this._hostPort = 2638;
/*  87 */     this._filePrefix = "cap";
/*  88 */     this._dumpFileName = "";
/*  89 */     this._showGUI = false;
/*  90 */     this._captureFileName = "";
/*  91 */     this._listenPort = 5005;
/*  92 */     this._translateToFile = false;
/*  93 */     this._translateToStream = false;
/*  94 */     this._encoding = "";
/*  95 */     this._filter = DumpFilterImpl.newUserFilter();
/*  96 */     this._maxOutputLines = 500;
/*  97 */     this._filterName = this._filter.getFileName();
/*     */   }
/*     */ 
/*     */   protected Preferences(String[] args)
/*     */     throws RiboException
/*     */   {
/* 107 */     processCommandLineArgs(args);
/*     */   }
/*     */ 
/*     */   private void processCommandLineArgs(String[] args)
/*     */     throws RiboException
/*     */   {
/* 115 */     int helpMode = 0;
/* 116 */     int argIndex = 0;
/*     */ 
/* 119 */     if ((args == null) || (args.length == 0))
/*     */     {
/* 121 */       this._mode = 0;
/* 122 */       return;
/*     */     }
/*     */ 
/* 128 */     while ((helpMode == 0) && (argIndex < args.length))
/*     */     {
/* 130 */       if (args[argIndex].equalsIgnoreCase("-h"))
/*     */       {
/* 132 */         helpMode = 1;
/*     */       }
/*     */ 
/* 135 */       if (args[argIndex].equalsIgnoreCase("-v"))
/*     */       {
/* 137 */         helpMode = 5;
/*     */       }
/*     */ 
/* 141 */       String flag = null;
/* 142 */       String value = null;
/*     */ 
/* 146 */       boolean argReady = true;
/*     */ 
/* 149 */       if (args[argIndex].startsWith("-"))
/*     */       {
/* 151 */         flag = args[argIndex];
/*     */ 
/* 155 */         if (argIndex + 1 < args.length)
/*     */         {
/* 158 */           if (!args[(argIndex + 1)].startsWith("-"))
/*     */           {
/* 161 */             ++argIndex;
/* 162 */             argReady = true;
/*     */           }
/*     */           else
/*     */           {
/* 167 */             argReady = false;
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 173 */           argReady = false;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 178 */       if ((argReady) && (!args[argIndex].startsWith("-")))
/*     */       {
/* 180 */         value = args[argIndex];
/*     */       }
/*     */ 
/* 187 */       helpMode = setFlag(flag, value);
/*     */ 
/* 190 */       if (helpMode == 0)
/*     */       {
/* 192 */         ++argIndex;
/*     */       }
/* 194 */       else if (helpMode != 2)
/*     */       {
/* 198 */         if (helpMode != 3)
/*     */         {
/* 202 */           if (helpMode == 4)
/*     */           {
/* 205 */             helpMode = 1;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 210 */     if (helpMode != 0)
/*     */     {
/* 212 */       if (helpMode == 1)
/*     */       {
/* 215 */         RiboMessage.raiseError("RiboUsage", String.valueOf(5005), "localhost", String.valueOf(2638), "cap");
/*     */       }
/*     */ 
/* 221 */       if (helpMode == 5)
/*     */       {
/* 223 */         RiboMessage.raiseError("RiboVersion", "Ribo 3.0 (Build 0424)");
/*     */       }
/*     */ 
/* 229 */       String helpMessage = null;
/* 230 */       String argMessage = null;
/* 231 */       switch (helpMode)
/*     */       {
/*     */       case 2:
/* 234 */         helpMessage = "RiboUsageUnexpected";
/* 235 */         argMessage = args[argIndex];
/* 236 */         break;
/*     */       case 3:
/* 238 */         helpMessage = "RiboUsageExpectedParmMissing";
/* 239 */         argMessage = args[argIndex];
/* 240 */         break;
/*     */       case 4:
/* 242 */         helpMessage = "RiboUsageInvalidFlag";
/* 243 */         argMessage = args[argIndex];
/* 244 */         break;
/*     */       default:
/* 246 */         helpMessage = "RiboUsageUnexpected";
/* 247 */         argMessage = args[argIndex];
/*     */       }
/*     */ 
/* 252 */       RiboMessage.raiseError(helpMessage, argMessage);
/*     */     }
/*     */ 
/* 256 */     if (this._filterName.length() > 0)
/*     */     {
/*     */       try
/*     */       {
/* 261 */         this._filter = DumpFilterImpl.loadFilter(this._filterName);
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/* 266 */         RiboMessage.raiseError("IOException", ioe.toString());
/*     */       }
/*     */       catch (ClassNotFoundException cne)
/*     */       {
/* 271 */         RiboMessage.raiseError("Exception", cne.toString());
/*     */       }
/*     */     }
/*     */ 
/* 275 */     if (this._encoding.length() > 0)
/*     */     {
/*     */       try
/*     */       {
/* 279 */         new String(new byte[0], Iana.lookupIana(this._encoding));
/*     */       }
/*     */       catch (UnsupportedEncodingException e)
/*     */       {
/* 284 */         RiboMessage.raiseError("Exception", e.toString());
/*     */       }
/*     */     }
/*     */ 
/* 288 */     if ((this._mode != 1) || (!this._dumpFileName.equals("")) || (this._chunkSize <= 0)) {
/*     */       return;
/*     */     }
/* 291 */     RiboMessage.raiseError("RiboUsageExpectedParmMissing", "-n (missing dump file name)");
/*     */   }
/*     */ 
/*     */   private int setFlag(String flag, String value)
/*     */   {
/* 303 */     int retHelp = 0;
/*     */ 
/* 305 */     if (flag == null)
/*     */     {
/* 307 */       if (value != null)
/*     */       {
/* 310 */         if ((this._mode == -1) || (this._mode == 1))
/*     */         {
/* 313 */           if (this._captureFileName.length() <= 0)
/*     */           {
/* 315 */             this._captureFileName = value;
/* 316 */             this._mode = 1;
/*     */           }
/* 319 */           else if (this._dumpFileName.length() <= 0)
/*     */           {
/* 321 */             this._dumpFileName = value;
/* 322 */             this._mode = 1;
/*     */           }
/*     */           else
/*     */           {
/* 327 */             retHelp = 2;
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 334 */           retHelp = 2;
/*     */         }
/*     */ 
/*     */       }
/*     */       else {
/* 339 */         retHelp = 1;
/*     */       }
/*     */ 
/*     */     }
/* 345 */     else if (flag.equalsIgnoreCase("-f"))
/*     */     {
/* 350 */       if (value == null)
/*     */       {
/* 352 */         retHelp = 3;
/*     */       }
/*     */       else
/*     */       {
/* 356 */         this._filterName = value;
/*     */       }
/*     */     }
/* 359 */     else if (flag.equalsIgnoreCase("-x"))
/*     */     {
/* 364 */       if (value == null)
/*     */       {
/* 366 */         retHelp = 3;
/*     */       }
/*     */       else
/*     */       {
/* 370 */         this._encoding = value;
/*     */       }
/*     */ 
/*     */     }
/* 376 */     else if (this._mode == 1)
/*     */     {
/* 378 */       if (flag.equalsIgnoreCase("-n"))
/*     */       {
/* 383 */         if (value == null)
/*     */         {
/* 385 */           retHelp = 3;
/*     */         }
/*     */         else
/*     */         {
/* 389 */           this._chunkSize = Integer.parseInt(value);
/* 390 */           this._mode = 1;
/*     */         }
/*     */ 
/*     */       }
/*     */       else {
/* 395 */         retHelp = 2;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 400 */       if (flag.equalsIgnoreCase("-l"))
/*     */       {
/* 405 */         if (value == null)
/*     */         {
/* 407 */           retHelp = 3;
/*     */         }
/*     */         else
/*     */         {
/* 411 */           this._listenPort = Integer.parseInt(value);
/*     */         }
/*     */       }
/* 414 */       else if (flag.equalsIgnoreCase("-s"))
/*     */       {
/* 419 */         if (value == null)
/*     */         {
/* 421 */           retHelp = 3;
/*     */         }
/*     */         else
/*     */         {
/* 425 */           this._hostName = value;
/*     */         }
/*     */       }
/* 428 */       else if (flag.equalsIgnoreCase("-p"))
/*     */       {
/* 433 */         if (value == null)
/*     */         {
/* 435 */           retHelp = 3;
/*     */         }
/*     */         else
/*     */         {
/* 439 */           this._hostPort = Integer.parseInt(value);
/*     */         }
/*     */       }
/* 442 */       else if (flag.equalsIgnoreCase("-c"))
/*     */       {
/* 447 */         if (value == null)
/*     */         {
/* 449 */           retHelp = 3;
/*     */         }
/*     */         else
/*     */         {
/* 453 */           this._filePrefix = value;
/*     */         }
/*     */       }
/* 456 */       else if (flag.equalsIgnoreCase("-t"))
/*     */       {
/* 462 */         System.err.println("The flag -t is temporarily not supported, it will be ignored!");
/*     */       }
/* 464 */       else if (flag.equalsIgnoreCase("-gui"))
/*     */       {
/* 469 */         this._showGUI = true;
/*     */       }
/* 471 */       else if (flag.equalsIgnoreCase("-d"))
/*     */       {
/* 477 */         System.err.println("The flag -d is temporarily not supported, it will be ignored!");
/*     */       }
/* 479 */       else if (flag.equalsIgnoreCase("-n"))
/*     */       {
/* 484 */         if (value == null)
/*     */         {
/* 486 */           retHelp = 3;
/*     */         }
/*     */         else
/*     */         {
/* 490 */           this._chunkSize = Integer.parseInt(value);
/* 491 */           this._mode = 1;
/*     */         }
/*     */ 
/*     */       }
/*     */       else {
/* 496 */         retHelp = 4;
/*     */       }
/*     */ 
/* 500 */       if ((retHelp == 0) && (this._mode != 1))
/*     */       {
/* 502 */         this._mode = 0;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 507 */     return retHelp;
/*     */   }
/*     */ 
/*     */   public int getChunkSize()
/*     */   {
/* 514 */     return this._chunkSize * 1024;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.Preferences
 * JD-Core Version:    0.5.4
 */