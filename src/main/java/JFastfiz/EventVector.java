/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package JFastfiz;

public class EventVector extends java.util.AbstractList<Event> implements java.util.RandomAccess {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected EventVector(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(EventVector obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(EventVector obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings({"deprecation", "removal"})
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        fastfizJNI.delete_EventVector(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public EventVector(Event[] initialElements) {
    this();
    reserve(initialElements.length);

    for (Event element : initialElements) {
      add(element);
    }
  }

  public EventVector(Iterable<Event> initialElements) {
    this();
    for (Event element : initialElements) {
      add(element);
    }
  }

  public Event get(int index) {
    return doGet(index);
  }

  public Event set(int index, Event e) {
    return doSet(index, e);
  }

  public boolean add(Event e) {
    modCount++;
    doAdd(e);
    return true;
  }

  public void add(int index, Event e) {
    modCount++;
    doAdd(index, e);
  }

  public Event remove(int index) {
    modCount++;
    return doRemove(index);
  }

  protected void removeRange(int fromIndex, int toIndex) {
    modCount++;
    doRemoveRange(fromIndex, toIndex);
  }

  public int size() {
    return doSize();
  }

  public int capacity() {
    return doCapacity();
  }

  public void reserve(int n) {
    doReserve(n);
  }

  public EventVector() {
    this(fastfizJNI.new_EventVector__SWIG_0(), true);
  }

  public EventVector(EventVector other) {
    this(fastfizJNI.new_EventVector__SWIG_1(EventVector.getCPtr(other), other), true);
  }

  public boolean isEmpty() {
    return fastfizJNI.EventVector_isEmpty(swigCPtr, this);
  }

  public void clear() {
    fastfizJNI.EventVector_clear(swigCPtr, this);
  }

  public EventVector(int count, Event value) {
    this(fastfizJNI.new_EventVector__SWIG_2(count, Event.getCPtr(value), value), true);
  }

  private int doCapacity() {
    return fastfizJNI.EventVector_doCapacity(swigCPtr, this);
  }

  private void doReserve(int n) {
    fastfizJNI.EventVector_doReserve(swigCPtr, this, n);
  }

  private int doSize() {
    return fastfizJNI.EventVector_doSize(swigCPtr, this);
  }

  private void doAdd(Event x) {
    fastfizJNI.EventVector_doAdd__SWIG_0(swigCPtr, this, Event.getCPtr(x), x);
  }

  private void doAdd(int index, Event x) {
    fastfizJNI.EventVector_doAdd__SWIG_1(swigCPtr, this, index, Event.getCPtr(x), x);
  }

  private Event doRemove(int index) {
    long cPtr = fastfizJNI.EventVector_doRemove(swigCPtr, this, index);
    return (cPtr == 0) ? null : new Event(cPtr, false);
  }

  private Event doGet(int index) {
    long cPtr = fastfizJNI.EventVector_doGet(swigCPtr, this, index);
    return (cPtr == 0) ? null : new Event(cPtr, false);
  }

  private Event doSet(int index, Event val) {
    long cPtr = fastfizJNI.EventVector_doSet(swigCPtr, this, index, Event.getCPtr(val), val);
    return (cPtr == 0) ? null : new Event(cPtr, false);
  }

  private void doRemoveRange(int fromIndex, int toIndex) {
    fastfizJNI.EventVector_doRemoveRange(swigCPtr, this, fromIndex, toIndex);
  }

}
