package zgame;

import org.lwjgl.input.Keyboard;

/**
 * 
 * @author zchira
 */
public class ZGlobals {
	//sirina polja u pixelima = 3 x spriteDim
	public static final int WIDTH = 90; 
	public static final boolean PAINT_GRAPH = false;
	
	public static final double GL_WIDTH = 6; 
	
	public static final double TOLLERANCE = 0.10002;
	
	//kontrole
	public static final int KEY_LEFT = Keyboard.KEY_LEFT;
	public static final int KEY_RIGHT = Keyboard.KEY_RIGHT;
	public static final int KEY_UP = Keyboard.KEY_UP;
	public static final int KEY_DOWN = Keyboard.KEY_DOWN;
	
	public static final int KEY_ROTATE_CCW = Keyboard.KEY_PERIOD;
	public static final int KEY_ROTATE_CW = Keyboard.KEY_COMMA;
	
//	public static final int KEY_ZOOM_OUT = KeyEvent.VK_MINUS;
//	public static final int KEY_ZOOM_IN = KeyEvent.VK_EQUALS;
	
	public static final int KEY_SPACE = Keyboard.KEY_SPACE;
	
	//camera switch
	public static final int KEY_PAUSE = Keyboard.KEY_P;
	public static final int KEY_ESCAPE = Keyboard.KEY_ESCAPE;
	
	//camera moving
	public static final int KEY_CAMERA_UP = Keyboard.KEY_W;
	public static final int KEY_CAMERA_DOWN = Keyboard.KEY_S;
	public static final int KEY_CAMERA_LEFT = Keyboard.KEY_A;
	public static final int KEY_CAMERA_RIGHT = Keyboard.KEY_D;
	
	//camera zoom
	public static final int KEY_CAMERA_ZOOM_IN = Keyboard.KEY_Q;
	public static final int KEY_CAMERA_ZOOM_OUT = Keyboard.KEY_E;
	
//	public static final int KEY_TOGGLE_GRAPH_PAINT = KeyEvent.VK_G;
	
	public static final int GAMEOVER_TIMEOUT = 10000;
	public static final int LIFE_LOST_TIMEOUT = 6000;
	public static final int ALL_CONNECTED_TIMEOUT = 3000;
	
	public static final int RESPAWNING_TIME = 5000; 
	
	public static final int TIME_OUT_WARNING = 20;
	
	
}
