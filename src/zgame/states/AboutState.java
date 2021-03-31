package zgame.states;

import java.io.IOException;

import zgame.GameWindow;
import zgame.StaticRenderTools;
import zgame.gui.BitmapFont;

/**
 * 
 * @author zchira
 */
public class AboutState extends AbstractStoryState {
	public static final String NAME = "about";

//	protected String[] splitText() {
//		String[] toReturn = new String[8];
//
//		toReturn[0] = "Idea and lead programmer: Zoran Cirkovic (zchira@yahoo.com)";
//		toReturn[1] = "Programmer assist: Nenad Novkovic (familijaa@yahoo.com)";
//		toReturn[2] = "";
//		toReturn[3] = "Special thanx:";
//		toReturn[4] = "Dejan Ilijic (www.ilijicdejan.com) for some music themes";
//		toReturn[5] = "Kristina Tosic (eskalicka.com) for some textures for original levelPack";
//		toReturn[6] = "";
//		toReturn[7] = "...and my family :-)";
//		return toReturn;
//	}

	@Override
	protected BitmapFont getDescriptionFont() {
		return StaticRenderTools.getFont256();
	}

	@Override
	public String getName() {

		return NAME;
	}

	@Override
	protected String getPackDescription() {
		String toReturn = new String();

		toReturn+= "Idea and lead programmer: Zoran Cirkovic (zchira@yahoo.com)";
		toReturn+=  "\nProgrammer assist: Nenad Novkovic (familijaa@yahoo.com)";
		toReturn+=  "\n";
		toReturn+=  "\nSpecial thanx:";
		toReturn+=  "\nDejan Ilijic (www.ilijicdejan.com) for all music themes and sounds";
		toReturn+=  "\nKristina Tosic (eskalicka.com) for some textures for original levelPack";
		toReturn+=  "\n";
		toReturn+=  "\n...and my family :-)";
		return toReturn;
	}

	@Override
	protected String getTargetState() {
		return MenuState.NAME;
	}

	@Override
	protected String getTitleString() {
		return "Credits:";
	}

	@Override
	public void init(GameWindow window) throws IOException {
		super.init(window);
	}
	
	@Override
	protected boolean isDrawPackName() {
		return true;
	}

}
