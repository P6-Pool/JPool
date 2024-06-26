/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package JFastfiz;

public class GaussianNoise extends Noise {
  private transient long swigCPtr;

  protected GaussianNoise(long cPtr, boolean cMemoryOwn) {
    super(fastfizJNI.GaussianNoise_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(GaussianNoise obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(GaussianNoise obj) {
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
        fastfizJNI.delete_GaussianNoise(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public GaussianNoise(double magnitude) {
    this(fastfizJNI.new_GaussianNoise__SWIG_0(magnitude), true);
  }

  public GaussianNoise() {
    this(fastfizJNI.new_GaussianNoise__SWIG_1(), true);
  }

  public GaussianNoise(double _a, double _b, double _theta, double _phi, double _v) {
    this(fastfizJNI.new_GaussianNoise__SWIG_2(_a, _b, _theta, _phi, _v), true);
  }

  public GaussianNoise(SWIGTYPE_p_std__istream sourceStream) {
    this(fastfizJNI.new_GaussianNoise__SWIG_3(SWIGTYPE_p_std__istream.getCPtr(sourceStream)), true);
  }

  public GaussianNoise(String noiseString) {
    this(fastfizJNI.new_GaussianNoise__SWIG_4(noiseString), true);
  }

  public NoiseType noiseType() {
    return NoiseType.swigToEnum(fastfizJNI.GaussianNoise_noiseType(swigCPtr, this));
  }

  public void toStream(SWIGTYPE_p_std__ostream out) {
    fastfizJNI.GaussianNoise_toStream(swigCPtr, this, SWIGTYPE_p_std__ostream.getCPtr(out));
  }

  public void applyNoise(ShotParams sp) {
    fastfizJNI.GaussianNoise_applyNoise(swigCPtr, this, ShotParams.getCPtr(sp), sp);
  }

}
