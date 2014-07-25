/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import java.awt.Button;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Label;
/*     */ import java.awt.List;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class FilterDialog extends Dialog
/*     */ {
/*  33 */   private static final String[] TOKEN_NAMES = { "EED Tokens", "LOGINACK Tokens", "LOGOUT Tokens", "ENVCHANGE Tokens", "ERROR Tokens", "INFO Tokens", "LANGUAGE Tokens", "RPC Tokens", "DBRPC Tokens", "DYNAMIC Tokens", "DYNAMIC2 Tokens", "MSG Tokens", "EVENT_NOTICE Tokens", "ROW Tokens", "ROWFMT Tokens", "ROWFMT2 Tokens", "ALTFMT Tokens", "PARAMS Tokens", "PARAMFMT Tokens", "PARAMFMT2 Tokens", "RETURN_VALUE Tokens", "RETURN_STATUS Tokens", "CAPABILITY Tokens", "CONTROL Tokens", "DONE Tokens", "DONEPROC Tokens", "DONEINPROC Tokens", "CURCLOSE Tokens", "CURDELETE Tokens", "CURFETCH Tokens", "CURINFO Tokens", "CURINFO3 Tokens", "CUROPEN Tokens", "CURUPDATE Tokens", "CURDECLARE Tokens", "CURDECLARE2 Tokens", "CURDECLARE3 Tokens", "KEY Tokens", "DEBUG_CMD Tokens", "ORDERBY Tokens", "ORDERBY2 Tokens", "ALTNAME Tokens", "ALTROW Tokens", "COLINFO Tokens", "OFFSET Tokens", "OPTIONCMD Tokens", "TABNAME Tokens", "LOGIN Records", "PDU Headers", "TDS Stream Headers", "Unformatted PDU Packets" };
/*     */ 
/*  94 */   private static final int[] TOKEN_VALUES = { 229, 173, 113, 227, 170, 171, 33, 224, 230, 231, 98, 101, 162, 209, 238, 97, 168, 215, 236, 32, 172, 121, 226, 174, 253, 254, 255, 128, 129, 130, 131, 136, 132, 133, 134, 35, 16, 202, 96, 169, 34, 167, 211, 165, 120, 166, 164, 258, 256, 257, 259 };
/*     */ 
/* 156 */   private static final String[] DETAIL_NAMES = { "TOKEN Details", "LENGTH Details", "SQL_TEXT Details", "DATA Details", "VERBOSE_CAP Details", "PASSWORD Details", "ROW Details", "FORMAT Details", "RPC Details", "CURSOR Details", "EED Details" };
/*     */ 
/* 177 */   private static final int[] DETAIL_VALUES = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
/*     */   private Button _okButton;
/*     */   private Button _cancelButton;
/*     */   private List _tokensList;
/*     */   private List _detailsList;
/*     */   private String _fileName;
/*     */ 
/*     */   protected FilterDialog(Frame frame)
/*     */   {
/* 209 */     super(frame, true);
/*     */ 
/* 211 */     this._fileName = RiboMgr.getPrefs()._filter.getFileName();
/* 212 */     if (this._fileName.length() <= 0)
/*     */     {
/* 214 */       setTitle(RiboMessage.getMessage("Untitled"));
/*     */     }
/*     */     else
/*     */     {
/* 218 */       setTitle(this._fileName);
/*     */     }
/*     */ 
/* 222 */     Panel titlePanel = new Panel(new GridLayout(1, 2));
/* 223 */     titlePanel.add(new Label(RiboMessage.getMessage("Tokens")));
/* 224 */     titlePanel.add(new Label(RiboMessage.getMessage("Details")));
/*     */ 
/* 226 */     Panel headerPanel = new Panel(new GridLayout(2, 1));
/* 227 */     headerPanel.add(new Label(RiboMessage.getMessage("EditFilterLabel")));
/* 228 */     headerPanel.add(titlePanel);
/*     */ 
/* 230 */     add(headerPanel, "North");
/*     */ 
/* 232 */     GridLayout l1 = new GridLayout(1, 2);
/* 233 */     l1.setHgap(12);
/* 234 */     l1.setVgap(12);
/* 235 */     Panel listPanel = new Panel(l1);
/*     */ 
/* 237 */     this._tokensList = makeTokenList();
/* 238 */     listPanel.add(this._tokensList);
/* 239 */     this._detailsList = makeDetailList();
/* 240 */     listPanel.add(this._detailsList);
/*     */ 
/* 242 */     add(listPanel, "Center");
/*     */ 
/* 245 */     Panel buttonPanel = new Panel(new FlowLayout());
/*     */ 
/* 247 */     this._okButton = new Button(RiboMessage.getMessage("OK"));
/* 248 */     this._okButton.setBackground(new Color(12632256));
/* 249 */     buttonPanel.add(this._okButton);
/*     */ 
/* 251 */     this._cancelButton = new Button(RiboMessage.getMessage("Cancel"));
/* 252 */     this._cancelButton.setBackground(new Color(12632256));
/* 253 */     buttonPanel.add(this._cancelButton);
/*     */ 
/* 255 */     add(buttonPanel, "South");
/*     */ 
/* 258 */     headerPanel.setCursor(new Cursor(0));
/* 259 */     titlePanel.setCursor(new Cursor(0));
/* 260 */     listPanel.setCursor(new Cursor(0));
/* 261 */     buttonPanel.setCursor(new Cursor(0));
/*     */ 
/* 265 */     ActionListener actionListener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent event)
/*     */       {
/* 269 */         Object object = event.getSource();
/* 270 */         if (object == FilterDialog.this._okButton)
/*     */         {
/* 272 */           FilterDialog.this.handleOKButton(event);
/*     */         } else {
/* 274 */           if (object != FilterDialog.this._cancelButton)
/*     */             return;
/* 276 */           FilterDialog.this.dispose();
/*     */         }
/*     */       }
/*     */     };
/* 281 */     this._okButton.addActionListener(actionListener);
/* 282 */     this._cancelButton.addActionListener(actionListener);
/*     */ 
/* 285 */     addWindowListener(new CloseWindowListener(null));
/*     */ 
/* 289 */     populate();
/*     */ 
/* 291 */     pack();
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize()
/*     */   {
/* 299 */     return new Dimension(400, 350);
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean b)
/*     */   {
/* 307 */     if (b)
/*     */     {
/* 309 */       Rectangle bounds = getParent().bounds();
/* 310 */       Rectangle abounds = bounds();
/*     */ 
/* 312 */       move(bounds.x + (bounds.width - abounds.width) / 2, bounds.y + (bounds.height - abounds.height) / 2);
/*     */     }
/*     */ 
/* 316 */     super.setVisible(b);
/*     */   }
/*     */ 
/*     */   private List makeTokenList()
/*     */   {
/* 326 */     List list = new List(6, true);
/* 327 */     for (int i = 0; i < TOKEN_NAMES.length; ++i)
/*     */     {
/* 329 */       list.add(TOKEN_NAMES[i]);
/*     */     }
/* 331 */     return list;
/*     */   }
/*     */ 
/*     */   private int lookUpToken(int itemNo)
/*     */   {
/* 339 */     return TOKEN_VALUES[itemNo];
/*     */   }
/*     */ 
/*     */   private List makeDetailList()
/*     */   {
/* 349 */     List list = new List(6, true);
/* 350 */     for (int i = 0; i < DETAIL_NAMES.length; ++i)
/*     */     {
/* 352 */       list.add(DETAIL_NAMES[i]);
/*     */     }
/* 354 */     return list;
/*     */   }
/*     */ 
/*     */   private int lookUpDetail(int itemNo)
/*     */   {
/* 362 */     return DETAIL_VALUES[itemNo];
/*     */   }
/*     */ 
/*     */   public Insets getInsets()
/*     */   {
/* 372 */     Insets answer = (Insets)super.getInsets().clone();
/* 373 */     answer.left += 12;
/* 374 */     answer.right += 12;
/* 375 */     answer.top += 12;
/* 376 */     answer.bottom += 12;
/* 377 */     return answer;
/*     */   }
/*     */ 
/*     */   private void handleOKButton(ActionEvent event)
/*     */   {
/* 386 */     DumpFilterImpl filter = (DumpFilterImpl)RiboMgr.getPrefs()._filter;
/*     */ 
/* 390 */     filter.excludeAllTokens();
/* 391 */     int[] selection = this._tokensList.getSelectedIndexes();
/* 392 */     for (int i = 0; i < selection.length; ++i)
/*     */     {
/* 394 */       filter.includeToken(lookUpToken(selection[i]));
/*     */     }
/*     */ 
/* 398 */     filter.excludeAllDetails();
/* 399 */     selection = this._detailsList.getSelectedIndexes();
/* 400 */     for (int i = 0; i < selection.length; ++i)
/*     */     {
/* 402 */       filter.includeDetails(lookUpDetail(selection[i]));
/*     */     }
/*     */ 
/* 406 */     dispose();
/*     */ 
/* 410 */     if (this._fileName.length() <= 0)
/*     */     {
/* 412 */       FileDialog saveDialog = new FileDialog((Frame)getParent(), RiboMessage.getMessage("SaveChanges"), 1);
/*     */ 
/* 416 */       saveDialog.show();
/* 417 */       this._fileName = saveDialog.getFile();
/*     */     }
/*     */ 
/* 421 */     if (this._fileName.length() <= 0)
/*     */       return;
/*     */     try
/*     */     {
/* 425 */       filter.saveFilter(this._fileName);
/*     */ 
/* 428 */       RiboMgr.getPrefs()._filter.setFileName(this._fileName);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 432 */       RiboMgr.getInstance().println(RiboMessage.makeMessage("IOException", e.toString()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void populate()
/*     */   {
/* 445 */     DumpFilter filter = RiboMgr.getPrefs()._filter;
/*     */ 
/* 448 */     for (int i = 0; i < TOKEN_VALUES.length; ++i)
/*     */     {
/* 450 */       if (!filter.includesToken(TOKEN_VALUES[i]))
/*     */         continue;
/* 452 */       this._tokensList.select(i);
/*     */     }
/*     */ 
/* 457 */     for (int i = 0; i < DETAIL_VALUES.length; ++i)
/*     */     {
/* 459 */       if (!filter.includesDetail(DETAIL_VALUES[i]))
/*     */         continue;
/* 461 */       this._detailsList.select(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CloseWindowListener extends WindowAdapter
/*     */   {
/*     */     private final FilterDialog this$0;
/*     */ 
/*     */     private CloseWindowListener()
/*     */     {
/* 471 */       this.this$0 = this$0;
/*     */     }
/*     */ 
/*     */     public void windowClosing(WindowEvent event) {
/* 475 */       Object source = event.getSource();
/* 476 */       if (source != this.this$0)
/*     */         return;
/* 478 */       this.this$0.dispose();
/*     */     }
/*     */ 
/*     */     CloseWindowListener(FilterDialog.1 x1)
/*     */     {
/* 471 */       this(x0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.FilterDialog
 * JD-Core Version:    0.5.4
 */