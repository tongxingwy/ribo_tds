/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import java.awt.Button;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Label;
/*     */ import java.awt.MediaTracker;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Point;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.image.ImageProducer;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class RiboFrame extends Frame
/*     */ {
/*     */   private Image _baseImage;
/*  32 */   private boolean _isRunning = false;
/*     */ 
/*  35 */   private boolean fComponentsAdjusted = false;
/*     */   Button bRun;
/*     */   Panel panel1;
/*     */   Button bQuit;
/*     */   TextField tfListenPort;
/*     */   Label lblListenPort;
/*     */   TextField tfServerHost;
/*     */   TextField tfServerPort;
/*     */   Label lblServerHost;
/*     */   Label lblServerPort;
/*     */   TextArea taStatus;
/*     */   MenuBar mainMenuBar;
/*     */   Menu menu1;
/*     */   MenuItem miPreferences;
/*     */   MenuItem miEditFilter;
/*     */   MenuItem miExit;
/*     */   Menu menu3;
/*     */   MenuItem miAbout;
/*     */ 
/*     */   public RiboFrame()
/*     */   {
/*  59 */     setLayout(null);
/*  60 */     setVisible(false);
/*  61 */     setSize(449, 397);
/*  62 */     setBackground(new Color(7190198));
/*  63 */     this.bRun = new Button();
/*  64 */     this.bRun.setBounds(72, 360, 120, 24);
/*  65 */     this.bRun.setBackground(new Color(12632256));
/*  66 */     add(this.bRun);
/*  67 */     this.panel1 = new Panel();
/*  68 */     this.panel1.setLayout(null);
/*  69 */     this.panel1.setVisible(false);
/*  70 */     this.panel1.setBounds(24, 36, 168, 144);
/*  71 */     add(this.panel1);
/*  72 */     this.bQuit = new Button();
/*  73 */     this.bQuit.setBounds(264, 360, 120, 24);
/*  74 */     this.bQuit.setBackground(new Color(12632256));
/*  75 */     add(this.bQuit);
/*  76 */     this.tfListenPort = new TextField();
/*  77 */     this.tfListenPort.setBounds(240, 36, 156, 32);
/*  78 */     add(this.tfListenPort);
/*  79 */     this.tfListenPort.setEnabled(false);
/*  80 */     this.lblListenPort = new Label("");
/*  81 */     this.lblListenPort.setBounds(240, 12, 180, 24);
/*  82 */     this.lblListenPort.setFont(new Font("Dialog", 0, 12));
/*  83 */     add(this.lblListenPort);
/*  84 */     this.tfServerHost = new TextField();
/*  85 */     this.tfServerHost.setBounds(240, 96, 156, 32);
/*  86 */     add(this.tfServerHost);
/*  87 */     this.tfServerHost.setEnabled(false);
/*  88 */     this.tfServerPort = new TextField();
/*  89 */     this.tfServerPort.setBounds(240, 156, 156, 32);
/*  90 */     add(this.tfServerPort);
/*  91 */     this.tfServerPort.setEnabled(false);
/*  92 */     this.lblServerHost = new Label("");
/*  93 */     this.lblServerHost.setBounds(240, 72, 180, 24);
/*  94 */     this.lblServerHost.setFont(new Font("Dialog", 0, 12));
/*  95 */     add(this.lblServerHost);
/*  96 */     this.lblServerPort = new Label("");
/*  97 */     this.lblServerPort.setBounds(240, 132, 180, 24);
/*  98 */     this.lblServerPort.setFont(new Font("Dialog", 0, 12));
/*  99 */     add(this.lblServerPort);
/* 100 */     this.taStatus = new TextArea();
/* 101 */     this.taStatus.setBounds(12, 204, 420, 132);
/* 102 */     add(this.taStatus);
/* 103 */     setTitle("Ribo");
/* 104 */     setResizable(false);
/*     */ 
/* 107 */     this.lblServerPort.setText(RiboMessage.getMessage("ServerPort"));
/* 108 */     this.lblServerHost.setText(RiboMessage.getMessage("ServerHost"));
/* 109 */     this.lblListenPort.setText(RiboMessage.getMessage("ListenPort"));
/* 110 */     this.bQuit.setLabel(RiboMessage.getMessage("Exit"));
/*     */ 
/* 113 */     this.tfListenPort.setText(String.valueOf(RiboMgr.getPrefs()._listenPort));
/*     */ 
/* 115 */     this.tfServerHost.setText(RiboMgr.getPrefs()._hostName);
/* 116 */     this.tfServerPort.setText(String.valueOf(RiboMgr.getPrefs()._hostPort));
/*     */ 
/* 120 */     MediaTracker _tracker = new MediaTracker(this.panel1);
/*     */     try
/*     */     {
/* 125 */       URL url = super.getClass().getResource("/ribo2.gif");
/* 126 */       ImageProducer ip = (ImageProducer)url.getContent();
/* 127 */       this._baseImage = this.panel1.createImage(ip);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */ 
/* 134 */     if (this._baseImage != null)
/*     */     {
/* 136 */       _tracker.addImage(this._baseImage, 0);
/*     */       try
/*     */       {
/* 139 */         _tracker.waitForID(0);
/*     */       }
/*     */       catch (InterruptedException ie)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 147 */     this.mainMenuBar = new MenuBar();
/* 148 */     this.menu1 = new Menu("");
/* 149 */     this.miPreferences = new MenuItem("");
/* 150 */     this.menu1.add(this.miPreferences);
/* 151 */     this.miEditFilter = new MenuItem("");
/* 152 */     this.menu1.add(this.miEditFilter);
/*     */ 
/* 154 */     this.menu1.addSeparator();
/*     */ 
/* 156 */     this.miExit = new MenuItem("");
/* 157 */     this.menu1.add(this.miExit);
/* 158 */     this.mainMenuBar.add(this.menu1);
/*     */ 
/* 161 */     this.menu3 = new Menu("");
/* 162 */     this.mainMenuBar.setHelpMenu(this.menu3);
/* 163 */     this.miAbout = new MenuItem("");
/* 164 */     this.menu3.add(this.miAbout);
/* 165 */     this.mainMenuBar.add(this.menu3);
/* 166 */     setMenuBar(this.mainMenuBar);
/*     */ 
/* 168 */     this.menu1.setLabel(RiboMessage.getMessage("File"));
/* 169 */     this.miPreferences.setLabel(RiboMessage.getMessage("Preferences"));
/* 170 */     this.miEditFilter.setLabel(RiboMessage.getMessage("EditFilter"));
/* 171 */     this.menu3.setLabel(RiboMessage.getMessage("Help"));
/* 172 */     this.miExit.setLabel(RiboMessage.getMessage("Exit"));
/* 173 */     this.miAbout.setLabel(RiboMessage.getMessage("About"));
/*     */ 
/* 175 */     SymWindow aSymWindow = new SymWindow();
/* 176 */     addWindowListener(aSymWindow);
/* 177 */     SymAction lSymAction = new SymAction();
/* 178 */     this.miAbout.addActionListener(lSymAction);
/* 179 */     this.miPreferences.addActionListener(lSymAction);
/* 180 */     this.miEditFilter.addActionListener(lSymAction);
/* 181 */     this.miExit.addActionListener(lSymAction);
/* 182 */     this.bRun.addActionListener(lSymAction);
/* 183 */     this.bQuit.addActionListener(lSymAction);
/*     */ 
/* 186 */     setRunning(false);
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean b)
/*     */   {
/* 196 */     if (b)
/*     */     {
/* 198 */       setLocation(50, 50);
/*     */     }
/* 200 */     super.setVisible(b);
/*     */   }
/*     */ 
/*     */   public void addNotify()
/*     */   {
/* 210 */     Dimension d = getSize();
/*     */ 
/* 212 */     super.addNotify();
/*     */ 
/* 214 */     if (this.fComponentsAdjusted) {
/* 215 */       return;
/*     */     }
/*     */ 
/* 218 */     setSize(insets().left + insets().right + d.width, insets().top + insets().bottom + d.height);
/*     */ 
/* 220 */     Component[] components = getComponents();
/* 221 */     for (int i = 0; i < components.length; ++i)
/*     */     {
/* 223 */       Point p = components[i].getLocation();
/* 224 */       p.translate(insets().left, insets().top);
/* 225 */       components[i].setLocation(p);
/*     */     }
/* 227 */     this.fComponentsAdjusted = true;
/*     */   }
/*     */ 
/*     */   void Frame1_WindowClosing(WindowEvent event)
/*     */   {
/* 249 */     setVisible(false);
/* 250 */     dispose();
/* 251 */     RiboMgr.getInstance().shutdown();
/*     */   }
/*     */ 
/*     */   void miAbout_Action(ActionEvent event)
/*     */   {
/* 291 */     new AboutDialog(this, true).setVisible(true);
/*     */   }
/*     */ 
/*     */   void miPreferences_Action(ActionEvent event)
/*     */   {
/* 300 */     new PreferencesDialog(this).setVisible(true);
/*     */   }
/*     */ 
/*     */   void miEditFilter_Action(ActionEvent event)
/*     */   {
/* 309 */     new FilterDialog(this).setVisible(true);
/*     */   }
/*     */ 
/*     */   void miExit_Action(ActionEvent event)
/*     */   {
/* 318 */     setVisible(false);
/* 319 */     dispose();
/* 320 */     RiboMgr.getInstance().shutdown();
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g)
/*     */   {
/* 329 */     if ((this.panel1 == null) || (this._baseImage == null))
/*     */       return;
/* 331 */     g.drawImage(this._baseImage, 18, 72, this.panel1);
/*     */   }
/*     */ 
/*     */   private void setRunning(boolean doCaptureToggle)
/*     */   {
/* 341 */     if (doCaptureToggle)
/*     */     {
/* 343 */       setPropsFromForm();
/* 344 */       this._isRunning = RiboMgr.getInstance().toggleRibo(RiboMgr.getPrefs()._listenPort, RiboMgr.getPrefs()._hostName, RiboMgr.getPrefs()._hostPort);
/*     */     }
/*     */ 
/* 350 */     this.tfListenPort.setEnabled(!this._isRunning);
/* 351 */     this.tfServerHost.setEnabled(!this._isRunning);
/* 352 */     this.tfServerPort.setEnabled(!this._isRunning);
/* 353 */     this.miPreferences.setEnabled(!this._isRunning);
/* 354 */     this.miEditFilter.setEnabled(!this._isRunning);
/* 355 */     if (this._isRunning)
/*     */     {
/* 357 */       this.bRun.setLabel(RiboMessage.getMessage("StopCapture"));
/*     */     }
/*     */     else
/*     */     {
/* 361 */       this.bRun.setLabel(RiboMessage.getMessage("StartCapture"));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setPropsFromForm()
/*     */   {
/* 370 */     RiboMgr.getPrefs()._listenPort = Integer.parseInt(this.tfListenPort.getText());
/*     */ 
/* 372 */     RiboMgr.getPrefs()._hostName = this.tfServerHost.getText();
/* 373 */     RiboMgr.getPrefs()._hostPort = Integer.parseInt(this.tfServerPort.getText());
/*     */   }
/*     */ 
/*     */   protected void setState(boolean isRunning)
/*     */   {
/* 382 */     this._isRunning = isRunning;
/* 383 */     setRunning(false);
/*     */   }
/*     */ 
/*     */   protected void println(String msg)
/*     */   {
/* 391 */     this.taStatus.append(msg + "\n");
/*     */   }
/*     */ 
/*     */   class SymAction
/*     */     implements ActionListener
/*     */   {
/*     */     SymAction()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent event)
/*     */     {
/* 261 */       Object object = event.getSource();
/* 262 */       if (object == RiboFrame.this.miAbout)
/*     */       {
/* 264 */         RiboFrame.this.miAbout_Action(event);
/*     */       }
/* 266 */       else if (object == RiboFrame.this.bRun)
/*     */       {
/* 268 */         RiboFrame.this.setRunning(true);
/*     */       }
/* 270 */       else if (object == RiboFrame.this.miPreferences)
/*     */       {
/* 272 */         RiboFrame.this.miPreferences_Action(event);
/*     */       }
/* 274 */       else if (object == RiboFrame.this.miEditFilter)
/*     */       {
/* 276 */         RiboFrame.this.miEditFilter_Action(event);
/*     */       } else {
/* 278 */         if ((object != RiboFrame.this.miExit) && (object != RiboFrame.this.bQuit))
/*     */           return;
/* 280 */         RiboFrame.this.miExit_Action(event);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class SymWindow extends WindowAdapter
/*     */   {
/*     */     SymWindow()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void windowClosing(WindowEvent event)
/*     */     {
/* 238 */       Object object = event.getSource();
/* 239 */       if (object == RiboFrame.this)
/* 240 */         RiboFrame.this.Frame1_WindowClosing(event);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.RiboFrame
 * JD-Core Version:    0.5.4
 */