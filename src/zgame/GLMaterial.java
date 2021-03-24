package zgame;

import java.nio.*;
import org.lwjgl.opengl.*;

public class GLMaterial {
    // A sampling of color and shininess values
    public static final float colorNone[]   = { 0.0f, 0.0f, 0.0f, 1.0f};
    public static final float colorYellow[] = { 1.0f, 1.0f, 0.0f, 1.0f};
    public static final float colorRed[]    = { .4f, 0.0f, 0.0f, .50f};
    public static final float colorGreen[]  = { 0.1f, .8f, 0.2f, .40f};
    public static final float colorBlue[]   = { 0.0f, 0.0f, 1.0f, 1.0f};
    public static final float colorGray[]   = { .5f, .5f, .5f, 1.0f};
    public static final float colorWhite[]  = { 1f, 1f, 1f, 1f};
    public static final float fcolorBlack[]  = { 0f, 0f, 0f, 1f};
    public static final float colorBeige[]  = { .7f, .7f, .4f, 1f}; //
    public static final float colorCyan[]   = { 0.1f, 0.1f, 0.9f, 1f}; //
    public static final float colorDefaultDiffuse[]   = { .8f, .8f, .8f, 1f}; // default diffuse color
    public static final float colorDefaultAmbient[]   = { .2f, .2f, .2f, 1f}; // default ambient color
    public static final float minShine   = 0.0f;
    public static final float maxShine   = 128.0f;
    //
    private static FloatBuffer noColor;
    private static FloatBuffer noShine;
    private static FloatBuffer noAmbient;
    private static FloatBuffer noDiffuse;
    //
    private FloatBuffer ambient;
    private FloatBuffer diffuse;
    private FloatBuffer specular;
    private FloatBuffer emission;
    private FloatBuffer shininess;

    static {
        float[] shine = {minShine,0,0,0};  // has to be four items for LWJGL
        noShine = allocFloats(shine);
        noColor = allocFloats(colorNone);
        noAmbient = allocFloats(colorDefaultAmbient);
        noDiffuse = allocFloats(colorDefaultDiffuse);
    }

    public GLMaterial() {
        setDefaults();
    }

    public GLMaterial(float[] color) {
        setDefaults();
        setSurfaceColor(color);
    }

    public void setDefaults() {
        setSurfaceColorLit(colorDefaultDiffuse);
        setSurfaceColorShadow(colorDefaultAmbient);
        setReflectionColor(colorNone);
        setGlowColor(colorNone);
        setShininess(50);
    }


    public void setSurfaceColor(float[] color) {
    	// By default, OpenGL sets diffuse to .8 and ambient to .2.
    	// Following that convention, I'll set diffuse to the given 
    	// color and ambient to 1/4 of the given color.
        float[] darker = {color[0]/4f, color[1]/4f, color[2]/4f, color[3]/4f};
        diffuse = allocFloats(color);   // surface directly lit
        ambient = allocFloats(darker);  // surface in shadow
    }

    public void setSurfaceColorLit(float[] color) {
        diffuse = allocFloats(color);
    }

    public void setSurfaceColorShadow(float[] color) {
        ambient = allocFloats(color);
    }

    /**
     * Make material appear to emit light
     * @param color
     */
    public void setGlowColor(float[] color) {
        emission = allocFloats(color);
    }

    /**
     * Set color of reflection.  Must be set if using setShininess()
     * @param color
     */
    public void setReflectionColor(float[] color) {
        specular = allocFloats(color);
    }

    /**
     * Set size of the reflection.  Must also set specular color:
     *           setReflectionColor(GLMaterial.colorWhite);
     *
     * @param howShiny  How large reflection is: 0 - 128
     */
    public void setShininess(float howShiny) {
        if (howShiny >= minShine && howShiny <= maxShine) {
            float[] tmp = {howShiny,0,0,0};
            shininess = allocFloats(tmp);
        }
    }

    /**
     * Activate these material settings in the GL environment.
     */
    public void apply() {
    	int faceType = GL11.GL_FRONT;  //GL_FRONT_AND_BACK;
        GL11.glMaterial(faceType, GL11.GL_AMBIENT, ambient);
        GL11.glMaterial(faceType, GL11.GL_DIFFUSE, diffuse);
        GL11.glMaterial(faceType, GL11.GL_EMISSION, emission);
        GL11.glMaterial(faceType, GL11.GL_SPECULAR, specular);
        GL11.glMaterial(faceType, GL11.GL_SHININESS, shininess);
    }

    /**
     * Reset all material settings in the GL environment.
     */
    public static void clear() {
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, noAmbient);
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, noDiffuse);
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, noColor);
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, noColor);
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SHININESS, noShine );
    }
    
    public static final int SIZE_FLOAT = 4;

    public static FloatBuffer allocFloats(int howmany) {
        return ByteBuffer.allocateDirect(howmany * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public static FloatBuffer allocFloats(float[] floatarray) {
        FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(floatarray).flip();
        return fb;
    }
}
