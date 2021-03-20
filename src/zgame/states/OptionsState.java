package zgame.states;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import zgame.GameWindow;
import zgame.StaticRenderTools;
import zgame.ZSounds;
import zgame.StaticRenderTools.DisplayModes;
import zgame.sound.SoundLoader;
import ZGameStatistic.ZSettings;
import ZXMLUtils.LevelPacksReader;

public class OptionsState extends AbstractMenuState {
	/** The game unique name of this state */
	public static final String NAME = "options";

	/** The options to present to the user */
	protected String[] options = new String[] { "Fullscreen", "Resolution",
			"Details", "Sound volume", "Music volume"
		//	,"Clear all high scores"
			, "...back" };

	//
	private final int FULLSCREEN = 0;
	private final int RESOLUTION = 1;
	private final int DETAILS = 2;
	private final int SOUND = 3;
	private final int MUSIC = 4;
	//private final int RESET = 5;

	private boolean _fullScreen = false;
	
	private boolean _fullDetails = true;

	private DisplayModes _mode = DisplayModes._1024X768;

	private int _soundVolume = 10;

	private int _musicVolume = 10;

	//

	LevelPacksReader reader;

	@Override
	public void enter(GameWindow window) {
		ZSettings settings = ZSettings.getSettings();
		_mode = settings.getMode();
		_fullScreen = settings.isFullScreen();
		_fullDetails = settings.isFullDetails();
		_soundVolume = settings.getSoundVolume();
		_musicVolume = settings.getMusicVolume();

	}
	
	@Override
	public void leave(GameWindow window) {
		super.leave(window);
//		ZSettings settings = ZSettings.getSettings();
//		settings.setMode(_mode);
//		settings.setFullScreen(_fullScreen);
//		settings.setFullDetails(_fullDetails);
//		settings.setMode(_mode);
//		settings.setMusicVolume(_musicVolume);
//		settings.setSoundVolume(_soundVolume);
		ZSettings.saveSettings(_fullScreen, _fullDetails, _mode, _soundVolume, _musicVolume);
	}

	@Override
	public String getName() {
		return NAME;
	}

	protected void renderMenu(GameWindow window) {
		window.enterOrtho();

		int topMargin = 250;

		int menuLeftMargin = 200;
		int optionsLeftMargin = 450;

		for (int i = 0; i < options.length; i++) {
			GL11.glColor3f(1f, 0.59f, 0);
			if (selected == i) {
				GL11.glColor3f(1f, 1f, 1f);
			}
			StaticRenderTools.getFont512().drawString(0, options[i],
					menuLeftMargin, topMargin + (i * 40));
		}

		StaticRenderTools.setOrangeColor();
		// render options
		// fullscreen
		int i = 0;
		if (selected == i) {
			GL11.glColor3f(1f, 1f, 1f);
		}
		String selectedOption = "< Off >";
		if (_fullScreen) {
			selectedOption = "< On  >";
		}

		StaticRenderTools.getFont512().drawString(0, selectedOption,
				optionsLeftMargin, topMargin + (i * 40));
		// resolution
		i++;
		StaticRenderTools.setOrangeColor();
		if (selected == i) {
			GL11.glColor3f(1f, 1f, 1f);
		}
		StaticRenderTools.getFont512().drawString(0,
				"< " + _mode.toString().substring(1) + " >", optionsLeftMargin,
				topMargin + (i * 40));
		
		
		// details
		i++;
		StaticRenderTools.setOrangeColor();
		if (selected == i) {
			GL11.glColor3f(1f, 1f, 1f);
		}
		String fullDetails = "< low  >";
		if (_fullDetails) {
			fullDetails = "< high >";
		}
		
		StaticRenderTools.getFont512().drawString(0,
				fullDetails, optionsLeftMargin,
				topMargin + (i * 40));
		
		// sound volume
		i++;
		StaticRenderTools.setOrangeColor();
		if (selected == i) {
			GL11.glColor3f(1f, 1f, 1f);
		}
		selectedOption = "";
		for (int j = 0; j < 10; j++) {
			if (j < _soundVolume) {
				selectedOption += "|";
			} else {
				selectedOption += "-";
			}

		}
		selectedOption += "  " + _soundVolume;

		StaticRenderTools.getFont512().drawString(0, selectedOption,
				optionsLeftMargin, topMargin + (i * 40));

		// music volume
		i++;
		StaticRenderTools.setOrangeColor();
		if (selected == i) {
			GL11.glColor3f(1f, 1f, 1f);
		}
		selectedOption = "";
		for (int j = 0; j < 10; j++) {
			if (j < _musicVolume) {
				selectedOption += "|";
			} else {
				selectedOption += "-";
			}

		}
		selectedOption += "  " + _musicVolume;

		StaticRenderTools.getFont512().drawString(0, selectedOption,
				optionsLeftMargin, topMargin + (i * 40));

		window.leaveOrtho();
	}

	@Override
	public void update(GameWindow window, int delta) {
		super.update(window, delta);

	}

	@Override
	protected void updateKeyboard(GameWindow window) {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				try {
					SoundLoader.getInstance().getOgg(ZSounds.CLICK)
							.play(1f, 1f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_UP) {

					selected--;
					if (selected < 0) {
						selected = options.length - 1;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
					selected++;
					if (selected >= options.length) {
						selected = 0;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RETURN
						|| Keyboard.getEventKey() == Keyboard.KEY_SPACE) {

					if (selected == options.length - 1) {
						window.changeToState(MenuState.NAME);
					}
					
//					if (selected == RESET){						
//						ManageStatistics.clearPlayerScores();
//					}

				}
				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					window.changeToState(MenuState.NAME);
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
					switch (selected) {
					case FULLSCREEN:
						_fullScreen = !_fullScreen;
						StaticRenderTools.setFullscreenMode(_fullScreen);
						break;
					case RESOLUTION:
						_mode.ordinal();
						if (_mode.ordinal() == 0) {
							_mode = DisplayModes.values()[0];
						} else {
							_mode = DisplayModes.values()[_mode.ordinal() - 1];
						}
						StaticRenderTools.setDisplayMode(_mode, window);

						break;
					case DETAILS:
						_fullDetails = !_fullDetails;
						StaticRenderTools.fullDetails = _fullDetails;
						break;	
						
					case SOUND:
						_soundVolume--;
						if (_soundVolume < 0) {
							_soundVolume = 0;
						}
						
						SoundLoader.getInstance().setSoundVolume((float)_soundVolume / 10f);

						break;
					case MUSIC:
						_musicVolume--;
						if (_musicVolume < 0) {
							_musicVolume = 0;
						}
						SoundLoader.getInstance().setMusicVolume((float)_musicVolume / 10f);
						break;
					default:
						break;
					}
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
					switch (selected) {
					case FULLSCREEN:
						_fullScreen = !_fullScreen;

						StaticRenderTools.setFullscreenMode(_fullScreen);
						break;
					case RESOLUTION:
						_mode.ordinal();
						int numOfModes = DisplayModes.values().length;
						if (_mode.ordinal() == numOfModes - 1) {
							_mode = DisplayModes.values()[numOfModes - 1];
						} else {
							_mode = DisplayModes.values()[_mode.ordinal() + 1];
						}
						StaticRenderTools.setDisplayMode(_mode, window);
						break;
					case DETAILS:
						_fullDetails = !_fullDetails;
						StaticRenderTools.fullDetails = _fullDetails;
						break;	
					case SOUND:
						_soundVolume++;
						if (_soundVolume > 10) {
							_soundVolume = 10;
						}
						SoundLoader.getInstance().setSoundVolume((float)_soundVolume / 10f);
						break;
					case MUSIC:
						_musicVolume++;
						if (_musicVolume > 10) {
							_musicVolume = 10;
						}
						SoundLoader.getInstance().setMusicVolume((float)_musicVolume / 10f);
						break;
					default:
						break;
					}
				}

			}
		}
	}

}
