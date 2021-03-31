package zgame.sound;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

public class SoundLoader {

	/** The single instance of this class */
	private static final SoundLoader store = new SoundLoader();

	/** True if sound effects are turned on */
	private boolean sounds;
	/** True if sound initialisation succeeded */
	private boolean soundWorks;
	/** The number of sound sources enabled - default 8 */
	private int sourceCount;
	/** The map of references to IDs of previously loaded sounds */
	private HashMap<String, Integer> loaded = new HashMap<String, Integer>();
	/** The OpenGL AL sound sources in use */
	private IntBuffer sources;
	/** The next source to be used for sound effects */
	private int nextSource;
	/** The music source to be used for noninterruptable music */
	private int musicSource;

	/** True if the sound system has been initialise */
	private boolean inited = false;
	
	/**
	 * soundVolume 0-1f
	 */
	private float soundVolume = 1f;
	
	/**
	 * musicVolume 0-1f
	 */
	private float musicVolume = 1f;

	/**
	 * Create a new sound store
	 */
	private SoundLoader() {
	}

	/**
	 * Indicate whether sound effects should be played
	 * 
	 * @param sounds
	 *            True if sound effects should be played
	 */
	public void setSoundsOn(boolean sounds) {
		if (soundWorks) {
			this.sounds = sounds;
		}
	}

	/**
	 * Check if sound effects are currently enabled
	 * 
	 * @return True if sound effects are currently enabled
	 */
	public boolean soundsOn() {
		return sounds;
	}

	/**
	 * Initialise the sound effects stored. This must be called before anything
	 * else will work
	 */
	public void init() {
		inited = true;
		try {
			AL.create();
			soundWorks = true;
			sounds = true;
		} catch (Exception e) {
			e.printStackTrace();
			soundWorks = false;
			sounds = false;
		}

		if (soundWorks) {
			sourceCount = 8;
			sources = BufferUtils.createIntBuffer(8);
			AL10.alGenSources(sources);

			if (AL10.alGetError() != AL10.AL_NO_ERROR) {
				sounds = false;
				soundWorks = false;
			}
		}
	}

	/**
	 * Play the specified buffer as a sound effect with the specified pitch and
	 * gain.
	 * 
	 * @param buffer
	 *            The ID of the buffer to play
	 * @param pitch
	 *            The pitch to play at
	 * @param gain
	 *            The gain to play at
	 */
	int playAsSound(int buffer, float pitch, float gain) {
		return play(buffer, pitch, gain * soundVolume, false);
	}

	private int play(int buffer, float pitch, float gain, boolean loop) {
		if (soundWorks) {
			if (sounds) {
				nextSource++;
				if (nextSource > sourceCount - 1) {
					nextSource = 1;

				}
				if (nextSource == musicSource) {
					nextSource++;
					if (nextSource > sourceCount - 1) {
						nextSource = 1;
					}
				}
				AL10.alSourceStop(sources.get(nextSource));

				AL10.alSourcei(sources.get(nextSource), AL10.AL_BUFFER, buffer);
				AL10.alSourcef(sources.get(nextSource), AL10.AL_PITCH, pitch);
				AL10.alSourcef(sources.get(nextSource), AL10.AL_GAIN, gain);
				if (loop) {
					AL10.alSourcei(sources.get(nextSource), AL10.AL_LOOPING,
							AL10.AL_TRUE);
				} else {
					AL10.alSourcei(sources.get(nextSource), AL10.AL_LOOPING,
							AL10.AL_FALSE);
				}

				AL10.alSourcePlay(sources.get(nextSource));
				AL10.alSourcePlay(sources.get(nextSource));
				return nextSource;
			}
		}
		return 0;
	}

	int playAsLoopSound(int buffer, float pitch, float gain) {
		return play(buffer, pitch, gain * soundVolume, true);
	}

	int playAsLoopMusic(int buffer, float pitch, float gain) {
		if (soundWorks) {
			if (sounds) {
				if (musicSource != 0) {
					stop(musicSource);
					musicSource = 0;
				}
				musicSource = play(buffer, pitch, gain * musicVolume, true);
				return musicSource;
			}
		}
		return 0;
	}

	int playAsMusic(int buffer, float pitch, float gain) {
		if (soundWorks) {
			if (sounds) {
				if (musicSource != 0) {
					stop(musicSource);
					musicSource = 0;
				}
				musicSource = play(buffer, pitch, gain * musicVolume, false);
				return musicSource;
			}
		}
		return 0;
	}

	void stop(int source) {
		if (soundWorks) {
			if (sounds) {
				AL10.alSourceStop(sources.get(source));

			}
		}
	}

	public boolean isMusicPlaying() {
		if (musicSource == 0)
			return false;
		return true;
	}

	/**
	 * Get the Sound based on a specified OGG file
	 * 
	 * @param ref
	 *            The reference to the OGG file in the classpath
	 * @return The Sound read from the OGG file
	 * @throws IOException
	 *             Indicates a failure to load the OGG
	 */
	public Sound getOgg(String ref) throws IOException {
		if (!soundWorks) {
			return new Sound(this, 0);
		}

		if (!inited) {
			throw new RuntimeException(
					"Can't load sounds until SoundLoader is init()");
		}
		int buffer = -1;

		if (loaded.get(ref) != null) {
			buffer = ((Integer) loaded.get(ref)).intValue();
		} else {
			System.out.println("Loading resource: " + ref);
			try {
				IntBuffer buf = BufferUtils.createIntBuffer(1);

				InputStream in = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(ref);

				OggDecoder decoder = new OggDecoder();
				OggData ogg = decoder.getData(in);

				AL10.alGenBuffers(buf);
				AL10.alBufferData(buf.get(0),
						ogg.channels > 1 ? AL10.AL_FORMAT_STEREO16
								: AL10.AL_FORMAT_MONO16, ogg.data, ogg.rate);

				loaded.put(ref, new Integer(buf.get(0)));

				buffer = buf.get(0);
			} catch (Exception e) {
				e.printStackTrace();
				Sys.alert("Error", "Failed to load: " + ref + " - "
						+ e.getMessage());
				System.exit(0);
			}
		}

		if (buffer == -1) {
			throw new IOException("Unable to load: " + ref);
		}

		return new Sound(this, buffer);
	}

	/**
	 * Get the single instance of this class
	 * 
	 * @return The single instnace of this class
	 */
	public static SoundLoader getInstance() {
		return store;
	}

	public void stopMusic() {
		if (musicSource != 0) {
			stop(musicSource);
			musicSource = 0;
		}
	}

	public float getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;
		if (musicSource != 0){
			AL10.alSourcef(sources.get(musicSource), AL10.AL_GAIN, 1*musicVolume);
		}
		
	}

	public float getSoundVolume() {
		return soundVolume;
	}

	public void setSoundVolume(float soundVolume) {
		this.soundVolume = soundVolume;
	}
}
