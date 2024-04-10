/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package JFastfiz;

public class GameState {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected GameState(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(GameState obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(GameState obj) {
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
        fastfizJNI.delete_GameState(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public static GameState Factory(SWIGTYPE_p_std__istream sourceStream) {
    long cPtr = fastfizJNI.GameState_Factory__SWIG_0(SWIGTYPE_p_std__istream.getCPtr(sourceStream));
    return (cPtr == 0) ? null : new GameState(cPtr, false);
  }

  public static GameState Factory(String sourceString) {
    long cPtr = fastfizJNI.GameState_Factory__SWIG_1(sourceString);
    return (cPtr == 0) ? null : new GameState(cPtr, false);
  }

  public static GameState RackedState(GameType gameType) {
    long cPtr = fastfizJNI.GameState_RackedState(gameType.swigValue());
    return (cPtr == 0) ? null : new GameState(cPtr, false);
  }

  public String toString() {
    return fastfizJNI.GameState_toString(swigCPtr, this);
  }

  public GameType gameType() {
    return GameType.swigToEnum(fastfizJNI.GameState_gameType(swigCPtr, this));
  }

  public boolean isOpenTable() {
    return fastfizJNI.GameState_isOpenTable(swigCPtr, this);
  }

  public TurnType getTurnType() {
    return TurnType.swigToEnum(fastfizJNI.GameState_getTurnType(swigCPtr, this));
  }

  public boolean playingSolids() {
    return fastfizJNI.GameState_playingSolids(swigCPtr, this);
  }

  public boolean curPlayerStarted() {
    return fastfizJNI.GameState_curPlayerStarted(swigCPtr, this);
  }

  public double timeLeft() {
    return fastfizJNI.GameState_timeLeft(swigCPtr, this);
  }

  public double timeLeftOpp() {
    return fastfizJNI.GameState_timeLeftOpp(swigCPtr, this);
  }

  public TableState tableState() {
    return new TableState(fastfizJNI.GameState_tableState(swigCPtr, this), false);
  }

  public ShotResult executeShot(GameShot shot, SWIGTYPE_p_p_Pool__Shot shotObj) {
    return ShotResult.swigToEnum(fastfizJNI.GameState_executeShot__SWIG_0(swigCPtr, this, GameShot.getCPtr(shot), shot, SWIGTYPE_p_p_Pool__Shot.getCPtr(shotObj)));
  }

  public ShotResult executeShot(GameShot shot) {
    return ShotResult.swigToEnum(fastfizJNI.GameState_executeShot__SWIG_1(swigCPtr, this, GameShot.getCPtr(shot), shot));
  }

}
