package zgame.sound;

/**
 * A sound that can be played through OpenAL
 * 
 * @author Kevin Glass
 */
public class Sound {
	/** The store from which this sound was loaded */
	private SoundLoader store;
	/** The buffer containing the sound */
	private int buffer;
	
	private int source;
	
//	private boolean playing;
	
	/**
	 * Create a new sound
	 * 
	 * @param store The sound store from which the sound was created
	 * @param buffer The buffer containing the sound data
	 */
	Sound(SoundLoader store, int buffer) {
		this.store = store;
		this.buffer = buffer;
	}
	
	/**
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 */
	public void play(float pitch, float gain) {
		source = store.playAsSound(buffer, pitch, gain);
	}
	
	/**
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 */
	public void playAsMusic(float pitch, float gain) {
		source = store.playAsMusic(buffer, pitch, gain);
	}
	
	public void playAsLoopSound(float pitch, float gain) {
		source = store.playAsLoopSound(buffer, pitch, gain);
	}
	
	public void playAsLoopMusic(float pitch, float gain) {
		source = store.playAsLoopMusic(buffer, pitch, gain);
	}
	
	public void stop(){
		store.stop(source);
	}
}
