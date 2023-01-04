/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package unina.game.development.savethatbridge.liquidfun;

public class FixtureDef {
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    protected FixtureDef(long cPtr, boolean cMemoryOwn) {
        swigCMemOwn = cMemoryOwn;
        swigCPtr = cPtr;
    }

    protected static long getCPtr(FixtureDef obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                liquidfunJNI.delete_FixtureDef(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    public void setShape(Shape value) {
        liquidfunJNI.FixtureDef_shape_set(swigCPtr, this, Shape.getCPtr(value), value);
    }

    public Shape getShape() {
        long cPtr = liquidfunJNI.FixtureDef_shape_get(swigCPtr, this);
        return (cPtr == 0) ? null : new Shape(cPtr, false);
    }

    public void setFriction(float value) {
        liquidfunJNI.FixtureDef_friction_set(swigCPtr, this, value);
    }

    public float getFriction() {
        return liquidfunJNI.FixtureDef_friction_get(swigCPtr, this);
    }

    public void setRestitution(float value) {
        liquidfunJNI.FixtureDef_restitution_set(swigCPtr, this, value);
    }

    public float getRestitution() {
        return liquidfunJNI.FixtureDef_restitution_get(swigCPtr, this);
    }

    public void setDensity(float value) {
        liquidfunJNI.FixtureDef_density_set(swigCPtr, this, value);
    }

    public float getDensity() {
        return liquidfunJNI.FixtureDef_density_get(swigCPtr, this);
    }

    public FixtureDef() {
        this(liquidfunJNI.new_FixtureDef(), true);
    }

}
