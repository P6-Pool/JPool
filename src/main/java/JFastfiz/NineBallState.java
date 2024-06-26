/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package JFastfiz;

public class NineBallState extends GameState {
  private transient long swigCPtr;

  protected NineBallState(long cPtr, boolean cMemoryOwn) {
    super(fastfizJNI.NineBallState_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(NineBallState obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(NineBallState obj) {
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
        fastfizJNI.delete_NineBallState(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public NineBallState(double timeleft, double timeleft_opp) {
    this(fastfizJNI.new_NineBallState__SWIG_0(timeleft, timeleft_opp), true);
  }

  public NineBallState(double timeleft) {
    this(fastfizJNI.new_NineBallState__SWIG_1(timeleft), true);
  }

  public NineBallState() {
    this(fastfizJNI.new_NineBallState__SWIG_2(), true);
  }

  public NineBallState(String gameString) {
    this(fastfizJNI.new_NineBallState__SWIG_3(gameString), true);
  }

  public GameType gameType() {
    return GameType.swigToEnum(fastfizJNI.NineBallState_gameType(swigCPtr, this));
  }

  public NineBallState(SWIGTYPE_p_std__istream is) {
    this(fastfizJNI.new_NineBallState__SWIG_4(SWIGTYPE_p_std__istream.getCPtr(is)), true);
  }

  public boolean shotRequired() {
    return fastfizJNI.NineBallState_shotRequired(swigCPtr, this);
  }

}
