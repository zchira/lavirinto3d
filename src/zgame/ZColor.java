package zgame;

import org.lwjgl.opengl.GL11;

/**
 * 
 * @author zchira
 */
public class ZColor {
	public float red;
	public float green;
	public float blue;
	public float alpha = 0.8f;
	
	public void setAsGlColor(){
		GL11.glColor4f(red, green, blue, alpha);
	}
	
	public void setAsGlColor(float alpha){
		GL11.glColor4f(red, green, blue, alpha);
	}
	
	public ZColor(int r, int g, int b){
		red = (float)r / 255f;
		green = (float)g / 255f;
		blue = (float)b / 255f;
	}
	
	public ZColor(float r, float g, float b){
		red = r;
		green = g;
		blue = b;
	}
}
