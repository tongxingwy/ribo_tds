/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.Misc;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Writer;
/*     */ 
/*     */ public class RiboMgr
/*     */   implements Runnable
/*     */ {
/*     */   private static final int ACCEPT_THREAD_TIMEOUT = 10;
/*     */   private static final String WAIT_CHARACTER = ".";
/*     */   public static final int WAIT_OFF = 0;
/*     */   public static final int WAIT_ON = 1;
/*     */   public static final int WAIT_TICK = 2;
/*  44 */   private static RiboMgr _instance = null;
/*     */   private Preferences _prefs;
/*     */   private CaptureService _captureService;
/*     */   private boolean _waitIsOn;
/*     */   private int _fileCounter;
/*     */   private RiboFrame _riboFrame;
/*     */   private boolean _shutdownRequested;
/*     */   private int _chunkCounter;
/*     */ 
/*     */   public static RiboMgr getInstance()
/*     */   {
/*  61 */     if (_instance == null)
/*     */     {
/*  63 */       _instance = new RiboMgr();
/*     */     }
/*  65 */     return _instance;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  74 */     RiboMgr riboMgr = getInstance();
/*  75 */     riboMgr.parseArguments(args);
/*  76 */     riboMgr.run();
/*     */ 
/*  80 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   protected static Preferences getPrefs()
/*     */   {
/*  88 */     return getInstance()._prefs;
/*     */   }
/*     */ 
/*     */   private RiboMgr()
/*     */   {
/*  97 */     this._waitIsOn = false;
/*  98 */     this._fileCounter = 0;
/*  99 */     this._riboFrame = null;
/* 100 */     this._shutdownRequested = false;
/*     */ 
/* 102 */     if (_instance != null)
/*     */     {
/* 104 */       throw new Error("A RiboMgr already exists!");
/*     */     }
/* 106 */     _instance = this;
/*     */   }
/*     */ 
/*     */   public void parseArguments(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 117 */       this._prefs = new Preferences(args);
/*     */     }
/*     */     catch (RiboException e)
/*     */     {
/* 121 */       printError(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 134 */     if (this._prefs == null)
/*     */       return;
/* 136 */     boolean renameRequired = true;
/*     */     try
/*     */     {
/* 140 */       int mode = this._prefs._mode;
/* 141 */       if (mode == 1)
/*     */       {
/* 144 */         doAnalyzer();
/*     */       }
/* 146 */       else if (mode == 0)
/*     */       {
/* 149 */         doCapture();
/*     */       }
/*     */     }
/*     */     catch (RiboException re)
/*     */     {
/* 154 */       if (re.getMessage().indexOf(" already exists.") != -1)
/*     */       {
/* 156 */         renameRequired = false;
/*     */       }
/* 158 */       printError(re.getMessage());
/*     */     }
/*     */     finally
/*     */     {
/* 162 */       if (renameRequired)
/*     */       {
/* 164 */         renameDumpFiles();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isCaptureServiceAlive()
/*     */   {
/* 176 */     boolean answer = false;
/* 177 */     if (this._captureService != null)
/*     */     {
/* 179 */       int capState = this._captureService.getCaptureServiceState();
/* 180 */       if (capState == 0)
/*     */       {
/* 182 */         answer = true;
/*     */       }
/* 184 */       else if (capState == 1)
/*     */       {
/* 186 */         answer = true;
/*     */       }
/*     */     }
/* 189 */     return answer;
/*     */   }
/*     */ 
/*     */   public boolean toggleRibo(int listenPort, String serverHost, int serverPort)
/*     */   {
/* 198 */     if ((this._captureService != null) && (this._captureService.getCaptureServiceState() == 1))
/*     */     {
/* 201 */       stopCaptureService();
/*     */     }
/*     */     else
/*     */     {
/* 205 */       this._captureService = new CaptureService(listenPort, serverHost, serverPort);
/*     */ 
/* 207 */       this._captureService.start();
/*     */     }
/*     */ 
/* 211 */     boolean answer = isCaptureServiceAlive();
/* 212 */     return answer;
/*     */   }
/*     */ 
/*     */   protected void captureServiceNotify(int state)
/*     */   {
/* 223 */     if (state != 2)
/*     */       return;
/* 225 */     if ((this._prefs._showGUI) && (this._riboFrame != null))
/*     */     {
/* 228 */       this._riboFrame.setState(false);
/*     */     }
/*     */     else
/*     */     {
/* 234 */       shutdown();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void printError(String message)
/*     */   {
/* 245 */     System.err.println(message);
/*     */   }
/*     */ 
/*     */   protected void println(String message)
/*     */   {
/* 254 */     if ((this._prefs._showGUI) && (this._riboFrame != null))
/*     */     {
/* 256 */       this._riboFrame.println(message);
/*     */     }
/*     */     else
/*     */     {
/* 260 */       System.out.println(message);
/* 261 */       System.out.flush();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void shutdown()
/*     */   {
/* 271 */     this._shutdownRequested = true;
/* 272 */     synchronized (this)
/*     */     {
/* 274 */       super.notify();
/*     */     }
/* 276 */     stopCaptureService();
/*     */   }
/*     */ 
/*     */   protected void stillWorking(int mode)
/*     */   {
/* 285 */     switch (mode)
/*     */     {
/*     */     case 1:
/* 288 */       this._waitIsOn = true;
/* 289 */       break;
/*     */     case 0:
/* 292 */       if ((this._waitIsOn) && 
/* 294 */         (!this._prefs._showGUI))
/*     */       {
/* 297 */         println("");
/*     */       }
/*     */ 
/* 300 */       this._waitIsOn = false;
/* 301 */       break;
/*     */     case 2:
/* 304 */       if ((!this._waitIsOn) || (this._prefs._showGUI))
/*     */         return;
/* 306 */       System.out.print(".");
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getFileNamePrefix()
/*     */   {
/* 318 */     StringBuffer fileName = new StringBuffer(this._prefs._dumpFileName);
/* 319 */     int endIndex = fileName.lastIndexOf(".");
/* 320 */     return fileName.substring(0, endIndex) + "_part";
/*     */   }
/*     */ 
/*     */   public Writer getNextFileWriter()
/*     */   {
/* 325 */     Writer writer = null;
/*     */     try
/*     */     {
/* 328 */       String chunkFileName = getFileNamePrefix() + ++this._chunkCounter + ".txt";
/* 329 */       Misc.checkOutputFilePath(chunkFileName);
/* 330 */       File outputFile = new File(chunkFileName);
/* 331 */       writer = new OutputStreamWriter(new FileOutputStream(outputFile));
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/*     */     }
/*     */ 
/* 337 */     return writer;
/*     */   }
/*     */ 
/*     */   private void renameDumpFiles()
/*     */   {
/* 342 */     if (this._prefs._chunkSize <= 0) {
/*     */       return;
/*     */     }
/* 345 */     File folder = new File(System.getProperty("user.dir"));
/* 346 */     File[] listOfFiles = folder.listFiles();
/* 347 */     for (int i = 0; i < listOfFiles.length; ++i)
/*     */     {
/* 349 */       if (!listOfFiles[i].isFile())
/*     */         continue;
/* 351 */       String fileName = listOfFiles[i].getName();
/* 352 */       if (fileName.indexOf(getFileNamePrefix()) == -1)
/*     */         continue;
/* 354 */       File oldname = new File(fileName);
/* 355 */       fileName = fileName.replaceAll(".txt", "_of_" + this._chunkCounter + ".txt");
/* 356 */       File newname = new File(fileName);
/* 357 */       oldname.renameTo(newname);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doAnalyzer()
/*     */     throws RiboException
/*     */   {
/* 369 */     InputStream inputStream = null;
/* 370 */     Writer writer = null;
/*     */     try
/*     */     {
/* 376 */       String captureFilename = this._prefs._captureFileName;
/*     */       String outputFilename;
/*     */       String outputFilename;
/* 378 */       if (this._prefs._chunkSize == 0)
/*     */       {
/* 381 */         outputFilename = this._prefs._dumpFileName;
/*     */       }
/*     */       else
/*     */       {
/* 387 */         outputFilename = getFileNamePrefix() + ++this._chunkCounter + ".txt";
/*     */       }
/*     */ 
/* 390 */       File inputFile = new File(captureFilename);
/*     */ 
/* 393 */       if (outputFilenameExist())
/*     */       {
/* 395 */         throw new IOException("File " + this._prefs._dumpFileName.replaceAll(".txt", "_part*_of_*.txt") + " already exists.");
/*     */       }
/*     */ 
/* 400 */       if ((!inputFile.exists()) || (!inputFile.isFile()))
/*     */       {
/* 402 */         RiboMessage.raiseError("CaptureInputFileDoesNotExist", captureFilename);
/*     */       }
/*     */ 
/* 406 */       if (!inputFile.canRead())
/*     */       {
/* 408 */         RiboMessage.raiseError("CaptureInputFileIsNotReadable", captureFilename);
/*     */       }
/*     */ 
/* 412 */       inputStream = new FileInputStream(inputFile);
/*     */ 
/* 415 */       if (outputFilename.length() == 0)
/*     */       {
/* 419 */         writer = new OutputStreamWriter(System.out);
/*     */       }
/*     */       else
/*     */       {
/* 424 */         Misc.checkOutputFilePath(outputFilename);
/* 425 */         File outputFile = new File(outputFilename);
/* 426 */         writer = new OutputStreamWriter(new FileOutputStream(outputFile));
/* 427 */         stillWorking(1);
/*     */       }
/*     */ 
/* 430 */       DumpTds dumpTds = new DumpTds(inputStream, writer);
/* 431 */       dumpTds.processTds();
/* 432 */       writer = dumpTds.getWriter();
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/* 436 */       RiboMessage.raiseError("IOException", ioe.toString());
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 443 */         if (inputStream != null)
/*     */         {
/* 445 */           inputStream.close();
/* 446 */           inputStream = null;
/*     */         }
/*     */ 
/* 449 */         if (writer != null)
/*     */         {
/* 451 */           writer.close();
/* 452 */           writer = null;
/*     */         }
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/*     */       }
/*     */ 
/* 459 */       stillWorking(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean outputFilenameExist()
/*     */   {
/* 466 */     File folder = new File(System.getProperty("user.dir"));
/* 467 */     File[] listOfFiles = folder.listFiles();
/* 468 */     for (int i = 0; i < listOfFiles.length; ++i)
/*     */     {
/* 470 */       if (!listOfFiles[i].isFile())
/*     */         continue;
/* 472 */       String fileName = listOfFiles[i].getName();
/* 473 */       if ((this._prefs._chunkSize > 0) && 
/* 475 */         (fileName.indexOf(getFileNamePrefix()) != -1))
/*     */       {
/* 477 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 482 */     return false;
/*     */   }
/*     */ 
/*     */   private void doCapture()
/*     */     throws RiboException
/*     */   {
/* 490 */     boolean captureIsRunning = true;
/* 491 */     RiboMessage.getMessage("QuitKey");
/*     */ 
/* 493 */     if (this._prefs._showGUI)
/*     */     {
/* 496 */       this._riboFrame = new RiboFrame();
/* 497 */       this._riboFrame.setVisible(true);
/*     */     }
/*     */     else
/*     */     {
/* 502 */       captureIsRunning = toggleRibo(this._prefs._listenPort, this._prefs._hostName, this._prefs._hostPort);
/*     */ 
/* 506 */       if (!captureIsRunning);
/*     */     }
/*     */ 
/* 513 */     WaitForQuitKey waitForQuitKey = null;
/*     */ 
/* 516 */     if (!this._shutdownRequested)
/*     */     {
/* 518 */       if (!this._prefs._showGUI);
/* 524 */       synchronized (this)
/*     */       {
/*     */         try
/*     */         {
/* 528 */           super.wait();
/*     */         }
/*     */         catch (InterruptedException ie)
/*     */         {
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 538 */     if (waitForQuitKey == null)
/*     */       return;
/* 540 */     waitForQuitKey.stop();
/*     */   }
/*     */ 
/*     */   protected String getNewCaptureFileName()
/*     */   {
/* 549 */     String answer = makeUniqueFileName(".tds");
/* 550 */     return answer;
/*     */   }
/*     */ 
/*     */   protected Writer getTransOutputWriter(String captureFilename)
/*     */     throws IOException
/*     */   {
/* 562 */     Writer answer = null;
/*     */ 
/* 564 */     if (this._prefs._translateToStream)
/*     */     {
/* 566 */       if (!this._prefs._showGUI)
/*     */       {
/* 571 */         answer = new OutputStreamWriter(System.out);
/*     */       }
/*     */       else
/*     */       {
/* 576 */         OutputFrame tOutput = new OutputFrame(captureFilename, this._prefs._maxOutputLines);
/*     */ 
/* 578 */         tOutput.setVisible(true);
/* 579 */         answer = tOutput.getOutputWriter();
/*     */       }
/*     */     }
/* 582 */     else if (this._prefs._translateToFile)
/*     */     {
/* 584 */       String fileName = makeUniqueFileName(".txt");
/* 585 */       Misc.checkOutputFilePath(fileName);
/* 586 */       OutputStream stream = new FileOutputStream(fileName);
/* 587 */       answer = new OutputStreamWriter(stream);
/*     */     }
/* 589 */     return answer;
/*     */   }
/*     */ 
/*     */   private synchronized String makeUniqueFileName(String extension)
/*     */   {
/* 598 */     String fileName = null;
/* 599 */     String prefix = this._prefs._filePrefix;
/*     */     while (true)
/*     */     {
/* 604 */       fileName = prefix + this._fileCounter + extension;
/*     */ 
/* 606 */       File theFile = new File(fileName);
/*     */ 
/* 609 */       if (!theFile.exists()) {
/*     */         break;
/*     */       }
/*     */ 
/* 613 */       this._fileCounter += 1;
/*     */     }
/*     */ 
/* 616 */     return fileName;
/*     */   }
/*     */ 
/*     */   private void stopCaptureService()
/*     */   {
/* 626 */     if (this._captureService != null)
/*     */     {
/* 628 */       println(RiboMessage.makeMessage("StoppingAcceptThread", "10"));
/*     */ 
/* 630 */       this._captureService.die();
/*     */       try
/*     */       {
/* 633 */         this._captureService.join(10000L);
/*     */       }
/*     */       catch (InterruptedException ie)
/*     */       {
/* 638 */         this._captureService.stop();
/*     */       }
/*     */     }
/* 641 */     this._captureService = null;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.RiboMgr
 * JD-Core Version:    0.5.4
 */