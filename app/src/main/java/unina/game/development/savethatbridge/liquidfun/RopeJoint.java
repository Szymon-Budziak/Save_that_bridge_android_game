/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package unina.game.development.savethatbridge.liquidfun;

public class RopeJoint extends Joint {
    private transient long swigCPtr;

    protected RopeJoint(long cPtr, boolean cMemoryOwn) {
        super(liquidfunJNI.RopeJoint_SWIGUpcast(cPtr), cMemoryOwn);
        swigCPtr = cPtr;
    }

    protected static long getCPtr(RopeJoint obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                throw new UnsupportedOperationException("C++ destructor does not have public access");
            }
            swigCPtr = 0;
        }
        super.delete();
    }

    public float getReactionTorque(float inv_dt) {
        return liquidfunJNI.RopeJoint_getReactionTorque(swigCPtr, this, inv_dt);
    }

    public void setMaxLength(float length) {
        liquidfunJNI.RopeJoint_setMaxLength(swigCPtr, this, length);
    }

    public float getMaxLength() {
        return liquidfunJNI.RopeJoint_getMaxLength(swigCPtr, this);
    }

    public SWIGTYPE_p_b2LimitState getLimitState() {
        return new SWIGTYPE_p_b2LimitState(liquidfunJNI.RopeJoint_getLimitState(swigCPtr, this), true);
    }

    public void dump() {
        liquidfunJNI.RopeJoint_dump(swigCPtr, this);
    }

}
