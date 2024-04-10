/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package JFastfiz;

public class GameShot {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected GameShot(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(GameShot obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(GameShot obj) {
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
        fastfizJNI.delete_GameShot(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setParams(ShotParams value) {
    fastfizJNI.GameShot_params_set(swigCPtr, this, ShotParams.getCPtr(value), value);
  }

  public ShotParams getParams() {
    long cPtr = fastfizJNI.GameShot_params_get(swigCPtr, this);
    return (cPtr == 0) ? null : new ShotParams(cPtr, false);
  }

  public void setCue_x(double value) {
    fastfizJNI.GameShot_cue_x_set(swigCPtr, this, value);
  }

  public double getCue_x() {
    return fastfizJNI.GameShot_cue_x_get(swigCPtr, this);
  }

  public void setCue_y(double value) {
    fastfizJNI.GameShot_cue_y_set(swigCPtr, this, value);
  }

  public double getCue_y() {
    return fastfizJNI.GameShot_cue_y_get(swigCPtr, this);
  }

  public void setBall(Ball.Type value) {
    fastfizJNI.GameShot_ball_set(swigCPtr, this, value.swigValue());
  }

  public Ball.Type getBall() {
    return Ball.Type.swigToEnum(fastfizJNI.GameShot_ball_get(swigCPtr, this));
  }

  public void setPocket(Table.Pocket value) {
    fastfizJNI.GameShot_pocket_set(swigCPtr, this, value.swigValue());
  }

  public Table.Pocket getPocket() {
    return Table.Pocket.swigToEnum(fastfizJNI.GameShot_pocket_get(swigCPtr, this));
  }

  public void setDecision(Decision value) {
    fastfizJNI.GameShot_decision_set(swigCPtr, this, value.swigValue());
  }

  public Decision getDecision() {
    return Decision.swigToEnum(fastfizJNI.GameShot_decision_get(swigCPtr, this));
  }

  public void setTimeSpent(double value) {
    fastfizJNI.GameShot_timeSpent_set(swigCPtr, this, value);
  }

  public double getTimeSpent() {
    return fastfizJNI.GameShot_timeSpent_get(swigCPtr, this);
  }

  public GameShot() {
    this(fastfizJNI.new_GameShot(), true);
  }

}
