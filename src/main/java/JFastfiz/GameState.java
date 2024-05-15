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

  public boolean isTerminal() {
    return fastfizJNI.GameState_isTerminal(swigCPtr, this);
  }

  public boolean positionRequired() {
    return fastfizJNI.GameState_positionRequired(swigCPtr, this);
  }

  public boolean decisionAllowed() {
    return fastfizJNI.GameState_decisionAllowed(swigCPtr, this);
  }

  public boolean shotRequired() {
    return fastfizJNI.GameState_shotRequired(swigCPtr, this);
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

  public boolean isLegalTurnType() {
    return fastfizJNI.GameState_isLegalTurnType(swigCPtr, this);
  }

  public void importFromStream(SWIGTYPE_p_std__istream sourceStream) {
    fastfizJNI.GameState_importFromStream(swigCPtr, this, SWIGTYPE_p_std__istream.getCPtr(sourceStream));
  }

  public GameState.PreProcessCode preProcess(GameShot shot) {
    return GameState.PreProcessCode.swigToEnum(fastfizJNI.GameState_preProcess(swigCPtr, this, GameShot.getCPtr(shot), shot));
  }

  public void processShot(EventVector eventList, GameShot gameShot) {
    fastfizJNI.GameState_processShot(swigCPtr, this, EventVector.getCPtr(eventList), eventList, GameShot.getCPtr(gameShot), gameShot);
  }

  public void rack() {
    fastfizJNI.GameState_rack(swigCPtr, this);
  }

  public void switchSides() {
    fastfizJNI.GameState_switchSides(swigCPtr, this);
  }

  public void set_tableState(TableState value) {
    fastfizJNI.GameState__tableState_set(swigCPtr, this, TableState.getCPtr(value), value);
  }

  public TableState get_tableState() {
    long cPtr = fastfizJNI.GameState__tableState_get(swigCPtr, this);
    return (cPtr == 0) ? null : new TableState(cPtr, false);
  }

  public void set_turnType(TurnType value) {
    fastfizJNI.GameState__turnType_set(swigCPtr, this, value.swigValue());
  }

  public TurnType get_turnType() {
    return TurnType.swigToEnum(fastfizJNI.GameState__turnType_get(swigCPtr, this));
  }

  public void set_timeLeft(double value) {
    fastfizJNI.GameState__timeLeft_set(swigCPtr, this, value);
  }

  public double get_timeLeft() {
    return fastfizJNI.GameState__timeLeft_get(swigCPtr, this);
  }

  public void set_timeLeftOpp(double value) {
    fastfizJNI.GameState__timeLeftOpp_set(swigCPtr, this, value);
  }

  public double get_timeLeftOpp() {
    return fastfizJNI.GameState__timeLeftOpp_get(swigCPtr, this);
  }

  public void set_curPlayerStarted(boolean value) {
    fastfizJNI.GameState__curPlayerStarted_set(swigCPtr, this, value);
  }

  public boolean get_curPlayerStarted() {
    return fastfizJNI.GameState__curPlayerStarted_get(swigCPtr, this);
  }

  public void set_switchedSides(boolean value) {
    fastfizJNI.GameState__switchedSides_set(swigCPtr, this, value);
  }

  public boolean get_switchedSides() {
    return fastfizJNI.GameState__switchedSides_get(swigCPtr, this);
  }

  public final static class PreProcessCode {
    public final static GameState.PreProcessCode PPC_BADPARAMS = new GameState.PreProcessCode("PPC_BADPARAMS", fastfizJNI.GameState_PPC_BADPARAMS_get());
    public final static GameState.PreProcessCode PPC_G_NOEXECUTE = new GameState.PreProcessCode("PPC_G_NOEXECUTE");
    public final static GameState.PreProcessCode PPC_G_NORMAL = new GameState.PreProcessCode("PPC_G_NORMAL");
    public final static GameState.PreProcessCode PPC_G_PLACECUE = new GameState.PreProcessCode("PPC_G_PLACECUE");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static PreProcessCode swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + PreProcessCode.class + " with value " + swigValue);
    }

    private PreProcessCode(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private PreProcessCode(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private PreProcessCode(String swigName, PreProcessCode swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static PreProcessCode[] swigValues = { PPC_BADPARAMS, PPC_G_NOEXECUTE, PPC_G_NORMAL, PPC_G_PLACECUE };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

}
