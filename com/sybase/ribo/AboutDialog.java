/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import java.awt.Button;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ 
/*     */ public class AboutDialog extends Dialog
/*     */ {
/*     */   Button okButton;
/*     */   TextArea textArea1;
/*  29 */   boolean fComponentsAdjusted = false;
/*     */ 
/*     */   public AboutDialog(Frame parent, boolean modal)
/*     */   {
/*  34 */     super(parent, modal);
/*     */ 
/*  43 */     setLayout(null);
/*  44 */     setVisible(false);
/*  45 */     setSize(477, 338);
/*  46 */     this.okButton = new Button();
/*  47 */     this.okButton.setBounds(205, 288, 66, 27);
/*  48 */     add(this.okButton);
/*  49 */     this.textArea1 = new TextArea("", 0, 0, 1);
/*  50 */     this.textArea1.setBounds(12, 24, 456, 242);
/*  51 */     this.textArea1.setFont(new Font("Dialog", 0, 12));
/*  52 */     add(this.textArea1);
/*  53 */     setTitle("About");
/*     */ 
/*  55 */     this.okButton.setLabel(RiboMessage.getMessage("OK"));
/*  56 */     this.textArea1.setText(RiboMessage.getMessage("AboutRibo"));
/*     */ 
/*  59 */     SymWindow aSymWindow = new SymWindow();
/*  60 */     addWindowListener(aSymWindow);
/*  61 */     SymAction lSymAction = new SymAction();
/*  62 */     this.okButton.addActionListener(lSymAction);
/*     */   }
/*     */ 
/*     */   public AboutDialog(Frame parent, String title, boolean modal)
/*     */   {
/*  69 */     this(parent, modal);
/*  70 */     setTitle(title);
/*     */   }
/*     */ 
/*     */   public void addNotify()
/*     */   {
/*  76 */     Dimension d = getSize();
/*     */ 
/*  78 */     super.addNotify();
/*     */ 
/*  81 */     if (this.fComponentsAdjusted) {
/*  82 */       return;
/*     */     }
/*     */ 
/*  85 */     setSize(insets().left + insets().right + d.width, insets().top + insets().bottom + d.height);
/*  86 */     Component[] components = getComponents();
/*  87 */     for (int i = 0; i < components.length; ++i)
/*     */     {
/*  89 */       Point p = components[i].getLocation();
/*  90 */       p.translate(insets().left, insets().top);
/*  91 */       components[i].setLocation(p);
/*     */     }
/*     */ 
/*  95 */     this.fComponentsAdjusted = true;
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean b)
/*     */   {
/* 100 */     if (b)
/*     */     {
/* 102 */       Rectangle bounds = getParent().bounds();
/* 103 */       Rectangle abounds = bounds();
/*     */ 
/* 105 */       move(bounds.x + (bounds.width - abounds.width) / 2, bounds.y + (bounds.height - abounds.height) / 2);
/*     */     }
/*     */ 
/* 109 */     super.setVisible(b);
/*     */   }
/*     */ 
/*     */   void AboutDialog_WindowClosing(WindowEvent event)
/*     */   {
/* 124 */     dispose();
/*     */   }
/*     */ 
/*     */   void okButton_Clicked(ActionEvent event)
/*     */   {
/* 141 */     dispose();
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
/* 131 */       Object object = event.getSource();
/* 132 */       if (object == AboutDialog.this.okButton)
/* 133 */         AboutDialog.this.okButton_Clicked(event);
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
/* 116 */       Object object = event.getSource();
/* 117 */       if (object == AboutDialog.this)
/* 118 */         AboutDialog.this.AboutDialog_WindowClosing(event);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.AboutDialog
 * JD-Core Version:    0.5.4
 */