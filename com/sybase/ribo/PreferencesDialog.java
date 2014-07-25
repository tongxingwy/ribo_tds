/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import java.awt.Button;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Label;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ 
/*     */ public class PreferencesDialog extends Dialog
/*     */ {
/*     */   private Label lblFilePrefix;
/*     */   private Label lblTranslationFilter;
/*     */   private TextField tfFilePrefix;
/*     */   private Label tfTranslateFileFilter;
/*     */   private Checkbox cbTranslateToFile;
/*     */   private Checkbox cbDisplayTranslationInWindow;
/*     */   private Button okButton;
/*     */   private Button cancelButton;
/*  33 */   boolean fComponentsAdjusted = false;
/*     */ 
/*     */   public PreferencesDialog(Frame parent)
/*     */   {
/*  40 */     super(parent, true);
/*     */ 
/*  42 */     setLayout(null);
/*  43 */     setVisible(false);
/*  44 */     setSize(339, 328);
/*  45 */     this.lblFilePrefix = new Label("");
/*  46 */     this.lblFilePrefix.setBounds(36, 12, 204, 24);
/*  47 */     add(this.lblFilePrefix);
/*  48 */     this.lblTranslationFilter = new Label("");
/*  49 */     this.lblTranslationFilter.setBounds(36, 132, 204, 24);
/*  50 */     add(this.lblTranslationFilter);
/*  51 */     this.tfFilePrefix = new TextField();
/*  52 */     this.tfFilePrefix.setBounds(36, 36, 180, 32);
/*  53 */     add(this.tfFilePrefix);
/*  54 */     this.tfTranslateFileFilter = new Label();
/*  55 */     this.tfTranslateFileFilter.setBounds(36, 156, 180, 32);
/*  56 */     add(this.tfTranslateFileFilter);
/*  57 */     this.cbTranslateToFile = new Checkbox("");
/*  58 */     this.cbTranslateToFile.setBounds(36, 204, 276, 24);
/*  59 */     add(this.cbTranslateToFile);
/*  60 */     this.cbDisplayTranslationInWindow = new Checkbox("");
/*  61 */     this.cbDisplayTranslationInWindow.setBounds(36, 240, 276, 24);
/*  62 */     add(this.cbDisplayTranslationInWindow);
/*  63 */     this.okButton = new Button();
/*  64 */     this.okButton.setBounds(60, 288, 84, 24);
/*  65 */     this.okButton.setBackground(new Color(12632256));
/*  66 */     add(this.okButton);
/*  67 */     this.cancelButton = new Button();
/*  68 */     this.cancelButton.setBounds(204, 288, 84, 24);
/*  69 */     this.cancelButton.setBackground(new Color(12632256));
/*  70 */     add(this.cancelButton);
/*  71 */     setTitle("Ribo Preferences");
/*     */ 
/*  74 */     this.lblFilePrefix.setText(RiboMessage.getMessage("CaptureFilePrefix"));
/*     */ 
/*  76 */     this.lblTranslationFilter.setText(RiboMessage.getMessage("TranslationFilter"));
/*     */ 
/*  78 */     this.cbTranslateToFile.setLabel(RiboMessage.getMessage("TranslateFile"));
/*     */ 
/*  80 */     this.cbTranslateToFile.setEnabled(false);
/*  81 */     this.cbDisplayTranslationInWindow.setLabel(RiboMessage.getMessage("DisplayTranslation"));
/*     */ 
/*  83 */     this.cbDisplayTranslationInWindow.setEnabled(false);
/*  84 */     this.okButton.setLabel(RiboMessage.getMessage("OK"));
/*     */ 
/*  86 */     this.cancelButton.setLabel(RiboMessage.getMessage("Cancel"));
/*     */ 
/*  88 */     this.tfFilePrefix.setText(RiboMgr.getPrefs()._filePrefix);
/*  89 */     String filterFile = RiboMgr.getPrefs()._filter.getFileName();
/*  90 */     if (filterFile.length() <= 0)
/*     */     {
/*  92 */       filterFile = "(Default)";
/*     */     }
/*  94 */     this.tfTranslateFileFilter.setText(filterFile);
/*  95 */     this.cbDisplayTranslationInWindow.setState(RiboMgr.getPrefs()._translateToStream);
/*  96 */     this.cbTranslateToFile.setState(RiboMgr.getPrefs()._translateToFile);
/*     */ 
/*  98 */     SymWindow aSymWindow = new SymWindow();
/*  99 */     addWindowListener(aSymWindow);
/* 100 */     SymAction lSymAction = new SymAction();
/* 101 */     this.okButton.addActionListener(lSymAction);
/* 102 */     this.cancelButton.addActionListener(lSymAction);
/*     */   }
/*     */ 
/*     */   public PreferencesDialog(Frame parent, String title)
/*     */   {
/* 110 */     this(parent);
/* 111 */     setTitle(title);
/*     */   }
/*     */ 
/*     */   public void addNotify()
/*     */   {
/* 120 */     Dimension d = getSize();
/*     */ 
/* 122 */     super.addNotify();
/*     */ 
/* 125 */     if (this.fComponentsAdjusted) {
/* 126 */       return;
/*     */     }
/*     */ 
/* 129 */     setSize(insets().left + insets().right + d.width, insets().top + insets().bottom + d.height);
/*     */ 
/* 131 */     Component[] components = getComponents();
/* 132 */     for (int i = 0; i < components.length; ++i)
/*     */     {
/* 134 */       Point p = components[i].getLocation();
/* 135 */       p.translate(insets().left, insets().top);
/* 136 */       components[i].setLocation(p);
/*     */     }
/*     */ 
/* 140 */     this.fComponentsAdjusted = true;
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean b)
/*     */   {
/* 148 */     if (b)
/*     */     {
/* 150 */       Rectangle bounds = getParent().bounds();
/* 151 */       Rectangle abounds = bounds();
/*     */ 
/* 153 */       move(bounds.x + (bounds.width - abounds.width) / 2, bounds.y + (bounds.height - abounds.height) / 2);
/*     */     }
/*     */ 
/* 157 */     super.setVisible(b);
/*     */   }
/*     */ 
/*     */   void AboutDialog_WindowClosing(WindowEvent event)
/*     */   {
/* 178 */     dispose();
/*     */   }
/*     */ 
/*     */   void okButton_Clicked(ActionEvent event)
/*     */   {
/* 205 */     RiboMgr.getPrefs()._filePrefix = this.tfFilePrefix.getText();
/* 206 */     RiboMgr.getPrefs()._translateToStream = this.cbDisplayTranslationInWindow.getState();
/*     */ 
/* 208 */     RiboMgr.getPrefs()._translateToFile = this.cbTranslateToFile.getState();
/*     */ 
/* 210 */     dispose();
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
/* 188 */       Object object = event.getSource();
/* 189 */       if (object == PreferencesDialog.this.okButton)
/*     */       {
/* 191 */         PreferencesDialog.this.okButton_Clicked(event);
/*     */       } else {
/* 193 */         if (object != PreferencesDialog.this.cancelButton)
/*     */           return;
/* 195 */         PreferencesDialog.this.dispose();
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
/* 167 */       Object object = event.getSource();
/* 168 */       if (object == PreferencesDialog.this)
/* 169 */         PreferencesDialog.this.AboutDialog_WindowClosing(event);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.PreferencesDialog
 * JD-Core Version:    0.5.4
 */