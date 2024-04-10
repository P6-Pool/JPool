/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package JFastfiz;

public class RailCollisionEvent extends Event {
  private transient long swigCPtr;

  protected RailCollisionEvent(long cPtr, boolean cMemoryOwn) {
    super(fastfizJNI.RailCollisionEvent_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(RailCollisionEvent obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(RailCollisionEvent obj) {
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
        fastfizJNI.delete_RailCollisionEvent(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public RailCollisionEvent(double time, Ball.Type b, Table.BoundaryId rail) {
    this(fastfizJNI.new_RailCollisionEvent(time, b.swigValue(), rail.swigValue()), true);
  }

  public Event.Type getType() {
    return Event.Type.swigToEnum(fastfizJNI.RailCollisionEvent_getType(swigCPtr, this));
  }

  public String getTypeString() {
    return fastfizJNI.RailCollisionEvent_getTypeString(swigCPtr, this);
  }

  public Table.BoundaryId getRail() {
    return Table.BoundaryId.swigToEnum(fastfizJNI.RailCollisionEvent_getRail(swigCPtr, this));
  }

}