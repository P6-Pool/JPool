/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package JFastfiz;

public class EightBallState extends GameState {
  private transient long swigCPtr;

  protected EightBallState(long cPtr, boolean cMemoryOwn) {
    super(fastfizJNI.EightBallState_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(EightBallState obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(EightBallState obj) {
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
        fastfizJNI.delete_EightBallState(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public EightBallState(double timeleft, double timeleft_opp) {
    this(fastfizJNI.new_EightBallState__SWIG_0(timeleft, timeleft_opp), true);
  }

  public EightBallState(double timeleft) {
    this(fastfizJNI.new_EightBallState__SWIG_1(timeleft), true);
  }

  public EightBallState() {
    this(fastfizJNI.new_EightBallState__SWIG_2(), true);
  }

  public EightBallState(String gameString) {
    this(fastfizJNI.new_EightBallState__SWIG_3(gameString), true);
  }

  public GameType gameType() {
    return GameType.swigToEnum(fastfizJNI.EightBallState_gameType(swigCPtr, this));
  }

  public EightBallState(SWIGTYPE_p_std__istream is) {
    this(fastfizJNI.new_EightBallState__SWIG_4(SWIGTYPE_p_std__istream.getCPtr(is)), true);
  }

  public void setTurnTypeNormal() {
    fastfizJNI.EightBallState_setTurnTypeNormal(swigCPtr, this);
  }

  public boolean playingSolids() {
    return fastfizJNI.EightBallState_playingSolids(swigCPtr, this);
  }

  public boolean isOpenTable() {
    return fastfizJNI.EightBallState_isOpenTable(swigCPtr, this);
  }

  public boolean shotRequired() {
    return fastfizJNI.EightBallState_shotRequired(swigCPtr, this);
  }

}
