/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ import java.util.EmptyStackException;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class Queue
/*     */ {
/*  35 */   private Object[] _list = null;
/*  36 */   private int _head = -1;
/*  37 */   private int _tail = -1;
/*  38 */   private int _size = 0;
/*  39 */   private int _incr = 0;
/*  40 */   private int _iter = -1;
/*     */ 
/*     */   public Queue()
/*     */   {
/*  45 */     init(1, 0);
/*     */   }
/*     */ 
/*     */   public Queue(int paramInt)
/*     */   {
/*  50 */     init(paramInt, paramInt);
/*     */   }
/*     */ 
/*     */   public Queue(int paramInt1, int paramInt2)
/*     */   {
/*  55 */     init(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   private void init(int paramInt1, int paramInt2)
/*     */   {
/*  60 */     if (paramInt1 < 1)
/*     */     {
/*  62 */       this._size = 1;
/*     */     }
/*     */     else
/*     */     {
/*  66 */       this._size = paramInt1;
/*     */     }
/*  68 */     if (paramInt2 < 0)
/*     */     {
/*  70 */       this._incr = 1;
/*     */     }
/*     */     else
/*     */     {
/*  74 */       this._incr = paramInt2;
/*     */     }
/*  76 */     this._list = new Object[this._size];
/*     */   }
/*     */ 
/*     */   public boolean empty()
/*     */   {
/*  87 */     return this._head == -1;
/*     */   }
/*     */ 
/*     */   public synchronized Object peek()
/*     */     throws EmptyStackException
/*     */   {
/*  98 */     if (this._head == -1)
/*     */     {
/* 100 */       EmptyStackException localEmptyStackException = new EmptyStackException();
/* 101 */       throw localEmptyStackException;
/*     */     }
/* 103 */     return this._list[this._head];
/*     */   }
/*     */ 
/*     */   public synchronized Object peek(int paramInt)
/*     */     throws EmptyStackException
/*     */   {
/* 115 */     if ((this._head == -1) || (paramInt < 0))
/*     */     {
/* 117 */       localEmptyStackException = new EmptyStackException();
/* 118 */       throw localEmptyStackException;
/*     */     }
/* 120 */     if (this._head <= this._tail)
/*     */     {
/* 122 */       if (paramInt <= this._tail - this._head)
/*     */       {
/* 124 */         return this._list[(paramInt + this._head)];
/*     */       }
/*     */ 
/* 128 */       localEmptyStackException = new EmptyStackException();
/* 129 */       throw localEmptyStackException;
/*     */     }
/*     */ 
/* 134 */     if (paramInt < this._size - this._head)
/*     */     {
/* 136 */       return this._list[(paramInt + this._head)];
/*     */     }
/* 138 */     paramInt -= this._head - this._size + 1;
/* 139 */     if (paramInt <= this._tail)
/*     */     {
/* 141 */       return this._list[paramInt];
/*     */     }
/*     */ 
/* 145 */     EmptyStackException localEmptyStackException = new EmptyStackException();
/* 146 */     throw localEmptyStackException;
/*     */   }
/*     */ 
/*     */   public synchronized Object pop()
/*     */     throws EmptyStackException
/*     */   {
/* 189 */     Object localObject = peek();
/* 190 */     this._list[this._head] = null;
/* 191 */     if (this._tail == this._head)
/*     */     {
/* 194 */       this._tail = (this._head = -1);
/*     */     }
/*     */     else
/*     */     {
/* 199 */       this._head += 1;
/* 200 */       if (this._head == this._size)
/*     */       {
/* 202 */         this._head = 0;
/*     */       }
/*     */     }
/* 205 */     return localObject;
/*     */   }
/*     */ 
/*     */   public synchronized Object popNoEx()
/*     */   {
/* 215 */     if (this._head == -1)
/*     */     {
/* 217 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 221 */       return pop();
/*     */     }
/*     */     catch (EmptyStackException localEmptyStackException)
/*     */     {
/*     */     }
/* 226 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized Object push(Object paramObject)
/*     */   {
/* 238 */     if (this._tail == -1)
/*     */     {
/* 240 */       this._head = (this._tail = 0);
/* 241 */       this._list[0] = paramObject;
/* 242 */       return paramObject;
/*     */     }
/* 244 */     this._tail += 1;
/* 245 */     if (this._tail == this._size)
/*     */     {
/* 247 */       this._tail = 0;
/*     */     }
/* 249 */     if (this._tail == this._head)
/*     */     {
/* 252 */       expand();
/*     */     }
/* 254 */     this._list[this._tail] = paramObject;
/* 255 */     return paramObject;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */     throws NoSuchElementException
/*     */   {
/* 264 */     this._iter = this._head;
/*     */   }
/*     */ 
/*     */   public Object next()
/*     */     throws NoSuchElementException
/*     */   {
/* 274 */     if (this._iter == -1)
/*     */     {
/* 276 */       localObject = new NoSuchElementException();
/* 277 */       throw ((Throwable)localObject);
/*     */     }
/* 279 */     Object localObject = this._list[this._iter];
/* 280 */     if (this._iter == this._tail)
/*     */     {
/* 283 */       this._iter = -1;
/*     */     }
/*     */     else
/*     */     {
/* 287 */       this._iter += 1;
/*     */     }
/* 289 */     if (this._iter == this._size)
/*     */     {
/* 291 */       this._iter = 0;
/*     */     }
/* 293 */     if (localObject == null)
/*     */     {
/* 295 */       NoSuchElementException localNoSuchElementException = new NoSuchElementException();
/* 296 */       throw localNoSuchElementException;
/*     */     }
/* 298 */     return localObject;
/*     */   }
/*     */ 
/*     */   public synchronized boolean removeElement(Object paramObject)
/*     */   {
/* 308 */     int i = search(paramObject);
/* 309 */     if (i == -1)
/*     */     {
/* 311 */       return false;
/*     */     }
/* 313 */     remove(i);
/* 314 */     return true;
/*     */   }
/*     */ 
/*     */   private int search(Object paramObject)
/*     */   {
/* 326 */     if (this._head == -1)
/*     */     {
/* 328 */       return -1;
/*     */     }
/*     */ 
/* 331 */     int j = (this._head <= this._tail) ? this._tail + 1 : this._size;
/* 332 */     for (int i = this._head; i < j; ++i)
/*     */     {
/* 334 */       if (paramObject == this._list[i])
/*     */       {
/* 336 */         return i;
/*     */       }
/*     */     }
/* 339 */     if (this._tail < this._head)
/*     */     {
/* 341 */       for (i = 0; i <= this._tail; ++i)
/*     */       {
/* 343 */         if (paramObject == this._list[i])
/*     */         {
/* 345 */           return i;
/*     */         }
/*     */       }
/*     */     }
/* 349 */     return -1;
/*     */   }
/*     */ 
/*     */   private void remove(int paramInt)
/*     */   {
/* 358 */     if ((paramInt == this._tail) && (this._head == this._tail))
/*     */     {
/* 360 */       this._tail = (this._head = -1);
/* 361 */       return;
/*     */     }
/* 363 */     if (paramInt > this._tail)
/*     */     {
/* 365 */       for (; paramInt < this._size - 1; ++paramInt)
/*     */       {
/* 367 */         this._list[paramInt] = this._list[(paramInt + 1)];
/*     */       }
/* 369 */       this._list[paramInt] = this._list[0];
/* 370 */       paramInt = 0;
/*     */     }
/* 372 */     for (; paramInt < this._tail; ++paramInt)
/*     */     {
/* 374 */       this._list[paramInt] = this._list[(paramInt + 1)];
/*     */     }
/* 376 */     if (this._tail == 0)
/*     */     {
/* 378 */       this._tail = (this._size - 1);
/* 379 */       return;
/*     */     }
/* 381 */     this._tail -= 1;
/*     */   }
/*     */ 
/*     */   private void expand()
/*     */   {
/* 390 */     int i = (this._incr == 0) ? this._size : this._incr;
/* 391 */     Object[] arrayOfObject = new Object[this._size + i];
/* 392 */     int j = 0;
/* 393 */     for (int k = this._head; k < this._size; ++k)
/*     */     {
/* 395 */       arrayOfObject[(j++)] = this._list[k];
/*     */     }
/* 397 */     this._head = 0;
/*     */ 
/* 399 */     for (k = 0; k < this._tail; ++k)
/*     */     {
/* 401 */       arrayOfObject[(j++)] = this._list[k];
/*     */     }
/* 403 */     this._list = arrayOfObject;
/* 404 */     this._tail = this._size;
/* 405 */     this._size += i;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.Queue
 * JD-Core Version:    0.5.4
 */