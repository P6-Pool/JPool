/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package JFastfiz;

public final class ShotResult {
  public final static ShotResult SR_OK = new ShotResult("SR_OK");
  public final static ShotResult SR_OK_LOST_TURN = new ShotResult("SR_OK_LOST_TURN");
  public final static ShotResult SR_BAD_PARAMS = new ShotResult("SR_BAD_PARAMS");
  public final static ShotResult SR_SHOT_IMPOSSIBLE = new ShotResult("SR_SHOT_IMPOSSIBLE");
  public final static ShotResult SR_TIMEOUT = new ShotResult("SR_TIMEOUT");

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static ShotResult swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + ShotResult.class + " with value " + swigValue);
  }

  private ShotResult(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private ShotResult(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private ShotResult(String swigName, ShotResult swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static ShotResult[] swigValues = { SR_OK, SR_OK_LOST_TURN, SR_BAD_PARAMS, SR_SHOT_IMPOSSIBLE, SR_TIMEOUT };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}
