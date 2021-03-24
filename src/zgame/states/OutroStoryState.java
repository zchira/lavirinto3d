package zgame.states;

import java.io.IOException;

import zgame.GameWindow;
import zgame.Status;
import zgame.ZSounds;
import zgame.sound.SoundLoader;

/**
 * 
 * @author zchira
 */
public class OutroStoryState extends AbstractStoryState{
	public static final String NAME = "outrostory";
	
	@Override
	protected String getPackDescription() {
		return Status.getGamePack().getEndStory();
	}

	
	@Override
	public void enter(GameWindow window) {
		super.enter(window);
		try {
			SoundLoader.getInstance().getOgg(ZSounds.LEVEL_PACK_COMPLETED_MUSIC)
					.playAsMusic(1, 1);
		} catch (IOException e) {
			System.err.println("Unable to load sound:"
					+ ZSounds.LEVEL_PACK_COMPLETED_MUSIC);
		}
		
	}
	
	@Override
	protected String getTargetState() {
		return GameOverState.NAME;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected boolean isDrawPackName() {
		return true;
	}

	@Override
	protected String getTitleString() {
		return Status.getGamePack().getPackName();
	}

}
