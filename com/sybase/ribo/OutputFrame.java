/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Button;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Panel;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ 
/*     */ public class OutputFrame extends Frame
/*     */ {
/*     */   private TextArea _translatedOutput;
/*     */   private Button _cancelButton;
/*     */   private int _maxOutputChars;
/*     */ 
/*     */   public OutputFrame(String title, int maxOutputLines)
/*     */   {
/*  37 */     super(title);
/*     */ 
/*  39 */     setVisible(false);
/*  40 */     setBackground(new Color(-9585219));
/*  41 */     addWindowListener(new CloseWindowListener(null));
/*     */ 
/*  43 */     Panel taPanel = new Panel(new BorderLayout());
/*  44 */     taPanel.setCursor(new Cursor(0));
/*  45 */     add(taPanel, "Center");
/*     */ 
/*  47 */     this._translatedOutput = new TextArea(30, 80);
/*  48 */     this._translatedOutput.setEditable(false);
/*  49 */     taPanel.add(this._translatedOutput);
/*     */ 
/*  52 */     Panel btnPanel = new Panel(new FlowLayout());
/*  53 */     btnPanel.setCursor(new Cursor(0));
/*  54 */     add(btnPanel, "South");
/*     */ 
/*  56 */     this._cancelButton = new Button();
/*  57 */     this._cancelButton.setBackground(new Color(12632256));
/*  58 */     this._cancelButton.setLabel(RiboMessage.getMessage("Close"));
/*  59 */     this._cancelButton.addActionListener(new ButtonListener(null));
/*  60 */     btnPanel.add(this._cancelButton);
/*     */ 
/*  62 */     pack();
/*     */ 
/*  64 */     this._maxOutputChars = (maxOutputLines * this._translatedOutput.getColumns());
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean b)
/*     */   {
/*  75 */     if (b)
/*     */     {
/*  77 */       setLocation(50, 50);
/*     */     }
/*  79 */     super.setVisible(b);
/*     */   }
/*     */ 
/*     */   public Insets getInsets()
/*     */   {
/*  89 */     Insets answer = (Insets)super.getInsets().clone();
/*  90 */     answer.left += 12;
/*  91 */     answer.right += 12;
/*  92 */     answer.top += 12;
/*  93 */     answer.bottom += 12;
/*  94 */     return answer;
/*     */   }
/*     */ 
/*     */   protected Writer getOutputWriter()
/*     */   {
/* 103 */     return new TranslatedOutputWriter();
/*     */   }
/*     */ 
/*     */   private class ButtonListener
/*     */     implements ActionListener
/*     */   {
/*     */     private final OutputFrame this$0;
/*     */ 
/*     */     private ButtonListener()
/*     */     {
/* 176 */       this.this$0 = this$0;
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent event) {
/* 180 */       Object source = event.getSource();
/* 181 */       if (source != this.this$0._cancelButton)
/*     */         return;
/* 183 */       this.this$0.dispose();
/*     */     }
/*     */ 
/*     */     ButtonListener(OutputFrame.1 x1)
/*     */     {
/* 176 */       this(x0);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CloseWindowListener extends WindowAdapter
/*     */   {
/*     */     private final OutputFrame this$0;
/*     */ 
/*     */     private CloseWindowListener()
/*     */     {
/* 164 */       this.this$0 = this$0;
/*     */     }
/*     */ 
/*     */     public void windowClosing(WindowEvent event) {
/* 168 */       Object source = event.getSource();
/* 169 */       if (source != this.this$0)
/*     */         return;
/* 171 */       this.this$0.dispose();
/*     */     }
/*     */ 
/*     */     CloseWindowListener(OutputFrame.1 x1)
/*     */     {
/* 164 */       this(x0);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TranslatedOutputWriter extends Writer
/*     */   {
/*     */     public TranslatedOutputWriter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/*     */     }
/*     */ 
/*     */     public void flush()
/*     */       throws IOException
/*     */     {
/*     */     }
/*     */ 
/*     */     public void write(char[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 144 */       if (!OutputFrame.this.isShowing())
/*     */         return;
/* 146 */       if (OutputFrame.this._translatedOutput.getText().length() > OutputFrame.this._maxOutputChars)
/*     */       {
/* 148 */         int chop = OutputFrame.this._translatedOutput.getText().indexOf('\n', len - 1);
/* 149 */         OutputFrame.this._translatedOutput.replaceRange("...", 0, chop);
/*     */       }
/* 151 */       OutputFrame.this._translatedOutput.setCaretPosition(OutputFrame.this._translatedOutput.getText().length());
/*     */ 
/* 153 */       OutputFrame.this._translatedOutput.append(new String(b, off, len));
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.OutputFrame
 * JD-Core Version:    0.5.4
 */