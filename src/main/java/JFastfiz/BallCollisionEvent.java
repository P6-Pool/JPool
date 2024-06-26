/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package JFastfiz;

public class BallCollisionEvent extends Event {
  private transient long swigCPtr;

  protected BallCollisionEvent(long cPtr, boolean cMemoryOwn) {
    super(fastfizJNI.BallCollisionEvent_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(BallCollisionEvent obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(BallCollisionEvent obj) {
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
        fastfizJNI.delete_BallCollisionEvent(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public BallCollisionEvent(double time, Ball.Type b1, Ball.Type b2) {
    this(fastfizJNI.new_BallCollisionEvent(time, b1.swigValue(), b2.swigValue()), true);
  }

  public Event.Type getType() {
    return Event.Type.swigToEnum(fastfizJNI.BallCollisionEvent_getType(swigCPtr, this));
  }

  public String getTypeString() {
    return fastfizJNI.BallCollisionEvent_getTypeString(swigCPtr, this);
  }

  public boolean relatedTo(Event other) {
    return fastfizJNI.BallCollisionEvent_relatedTo(swigCPtr, this, Event.getCPtr(other), other);
  }

  public boolean involvesBall(Ball.Type b) {
    return fastfizJNI.BallCollisionEvent_involvesBall(swigCPtr, this, b.swigValue());
  }

  public Ball.Type getBall2() {
    return Ball.Type.swigToEnum(fastfizJNI.BallCollisionEvent_getBall2(swigCPtr, this));
  }

  public Ball getBall2Data() {
    return new Ball(fastfizJNI.BallCollisionEvent_getBall2Data(swigCPtr, this), false);
  }

}
