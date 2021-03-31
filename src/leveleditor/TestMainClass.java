package leveleditor;
import ZXMLUtils.GamePackReader;
import ZXMLUtils.LevelPacksReader;


public class TestMainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LevelPacksReader lpr = new LevelPacksReader("/res/levelpacks.xml");
		
		int packCount = lpr.getGamePacksCount();
		
		for(int i = 0; i < packCount; i++) {
			System.out.println(lpr.getGamePackName(i));
		}
		
		GamePackReader gpr = lpr.getGamePack(0);
		
		System.out.println(gpr.getStartStory());
	}
}
