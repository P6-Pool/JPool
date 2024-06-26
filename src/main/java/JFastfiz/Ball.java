/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package JFastfiz;

public class Ball {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected Ball(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Ball obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(Ball obj) {
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
        fastfizJNI.delete_Ball(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public Ball() {
    this(fastfizJNI.new_Ball__SWIG_0(), true);
  }

  public Ball(Ball.Type _type) {
    this(fastfizJNI.new_Ball__SWIG_1(_type.swigValue()), true);
  }

  public Ball(Ball rhs) {
    this(fastfizJNI.new_Ball__SWIG_2(Ball.getCPtr(rhs), rhs), true);
  }

  public double getRadius() {
    return fastfizJNI.Ball_getRadius(swigCPtr, this);
  }

  public Ball.Type getID() {
    return Ball.Type.swigToEnum(fastfizJNI.Ball_getID(swigCPtr, this));
  }

  public String getIDString() {
    return fastfizJNI.Ball_getIDString(swigCPtr, this);
  }

  public Ball.State getState() {
    return Ball.State.swigToEnum(fastfizJNI.Ball_getState(swigCPtr, this));
  }

  public String getStateString() {
    return fastfizJNI.Ball_getStateString(swigCPtr, this);
  }

  public Point getPos() {
    return new Point(fastfizJNI.Ball_getPos(swigCPtr, this), false);
  }

  public Vector getVelocity() {
    return new Vector(fastfizJNI.Ball_getVelocity(swigCPtr, this), false);
  }

  public Vector getSpin() {
    return new Vector(fastfizJNI.Ball_getSpin(swigCPtr, this), false);
  }

  public void setID(Ball.Type t) {
    fastfizJNI.Ball_setID(swigCPtr, this, t.swigValue());
  }

  public void setPos(Point pos) {
    fastfizJNI.Ball_setPos(swigCPtr, this, Point.getCPtr(pos), pos);
  }

  public void setVelocity(Vector vel) {
    fastfizJNI.Ball_setVelocity(swigCPtr, this, Vector.getCPtr(vel), vel);
  }

  public void setSpin(Vector spin) {
    fastfizJNI.Ball_setSpin(swigCPtr, this, Vector.getCPtr(spin), spin);
  }

  public void setState(Ball.State s) {
    fastfizJNI.Ball_setState(swigCPtr, this, s.swigValue());
  }

  public boolean isInPlay() {
    return fastfizJNI.Ball_isInPlay(swigCPtr, this);
  }

  public boolean isPocketed() {
    return fastfizJNI.Ball_isPocketed(swigCPtr, this);
  }

  public void updateState(boolean VERBOSE) {
    fastfizJNI.Ball_updateState__SWIG_0(swigCPtr, this, VERBOSE);
  }

  public void updateState() {
    fastfizJNI.Ball_updateState__SWIG_1(swigCPtr, this);
  }

  public String toString() {
    return fastfizJNI.Ball_toString(swigCPtr, this);
  }

  public void fromString(String s) {
    fastfizJNI.Ball_fromString(swigCPtr, this, s);
  }

  public final static class State {
    public final static Ball.State NOTINPLAY = new Ball.State("NOTINPLAY");
    public final static Ball.State STATIONARY = new Ball.State("STATIONARY");
    public final static Ball.State SPINNING = new Ball.State("SPINNING");
    public final static Ball.State SLIDING = new Ball.State("SLIDING");
    public final static Ball.State ROLLING = new Ball.State("ROLLING");
    public final static Ball.State POCKETED_SW = new Ball.State("POCKETED_SW");
    public final static Ball.State POCKETED_W = new Ball.State("POCKETED_W");
    public final static Ball.State POCKETED_NW = new Ball.State("POCKETED_NW");
    public final static Ball.State POCKETED_NE = new Ball.State("POCKETED_NE");
    public final static Ball.State POCKETED_E = new Ball.State("POCKETED_E");
    public final static Ball.State POCKETED_SE = new Ball.State("POCKETED_SE");
    public final static Ball.State SLIDING_SPINNING = new Ball.State("SLIDING_SPINNING");
    public final static Ball.State ROLLING_SPINNING = new Ball.State("ROLLING_SPINNING");
    public final static Ball.State UNKNOWN_STATE = new Ball.State("UNKNOWN_STATE");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static State swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + State.class + " with value " + swigValue);
    }

    private State(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private State(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private State(String swigName, State swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static State[] swigValues = { NOTINPLAY, STATIONARY, SPINNING, SLIDING, ROLLING, POCKETED_SW, POCKETED_W, POCKETED_NW, POCKETED_NE, POCKETED_E, POCKETED_SE, SLIDING_SPINNING, ROLLING_SPINNING, UNKNOWN_STATE };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class Type {
    public final static Ball.Type CUE = new Ball.Type("CUE");
    public final static Ball.Type ONE = new Ball.Type("ONE");
    public final static Ball.Type TWO = new Ball.Type("TWO");
    public final static Ball.Type THREE = new Ball.Type("THREE");
    public final static Ball.Type FOUR = new Ball.Type("FOUR");
    public final static Ball.Type FIVE = new Ball.Type("FIVE");
    public final static Ball.Type SIX = new Ball.Type("SIX");
    public final static Ball.Type SEVEN = new Ball.Type("SEVEN");
    public final static Ball.Type EIGHT = new Ball.Type("EIGHT");
    public final static Ball.Type NINE = new Ball.Type("NINE");
    public final static Ball.Type TEN = new Ball.Type("TEN");
    public final static Ball.Type ELEVEN = new Ball.Type("ELEVEN");
    public final static Ball.Type TWELVE = new Ball.Type("TWELVE");
    public final static Ball.Type THIRTEEN = new Ball.Type("THIRTEEN");
    public final static Ball.Type FOURTEEN = new Ball.Type("FOURTEEN");
    public final static Ball.Type FIFTEEN = new Ball.Type("FIFTEEN");
    public final static Ball.Type UNKNOWN_ID = new Ball.Type("UNKNOWN_ID");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static Type swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + Type.class + " with value " + swigValue);
    }

    private Type(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private Type(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private Type(String swigName, Type swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static Type[] swigValues = { CUE, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE, THIRTEEN, FOURTEEN, FIFTEEN, UNKNOWN_ID };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

}
