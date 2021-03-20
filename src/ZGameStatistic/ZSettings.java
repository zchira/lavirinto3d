package ZGameStatistic;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import zgame.StaticRenderTools.DisplayModes;

/**
 * Load and save settings
 * @author Nenad Novkovic
 *
 */
public class ZSettings  implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static String FILE_NAME = "Settings.xml";
		
	private boolean fullScreen;
	
	private boolean fullDetails;

	private DisplayModes mode;

	private int soundVolume;

	private int musicVolume;
	
	public ZSettings()
	{
		this(false, true, DisplayModes._1024X768, 10, 10);
	}
	
	public ZSettings(boolean fullScreen, boolean fullDetails,
			DisplayModes mode, int soundVolume, int musicVolume)
	{
		this.fullScreen = fullScreen;
		this.fullDetails = fullDetails;
		this.mode = mode;
		this.soundVolume = soundVolume;
		this.musicVolume = musicVolume;
	}
	
	/**
	 * Gets saved settings from file. If file dose not exist returns default settings.
	 * @return
	 */
	public static ZSettings getSettings()
	{
		ZSettings toReturn = null;

		FileInputStream fis;
		try {
			fis = new FileInputStream(FILE_NAME);
			BufferedInputStream bis = new BufferedInputStream(fis);

			XMLDecoder decoder = new XMLDecoder(bis);
			toReturn = (ZSettings)decoder.readObject();
			decoder.close();
			try {
				fis.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e) {
			toReturn = new ZSettings();
		}

		return toReturn;
	}
	
	public static boolean saveSettings(boolean fullScreen, boolean fullDetails,
			DisplayModes mode, int soundVolume, int musicVolume)
	{
		try {
			ZSettings obj = new ZSettings(fullScreen, fullDetails, mode, soundVolume, musicVolume);
			FileOutputStream fos = new FileOutputStream(FILE_NAME);
			XMLEncoder encoder = new XMLEncoder(fos);		
			encoder.writeObject(obj);
			encoder.close();
			
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();			
		} 
		
		
		return true;
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	public boolean isFullDetails() {
		return fullDetails;
	}

	public void setFullDetails(boolean fullDetails) {
		this.fullDetails = fullDetails;
	}

	public DisplayModes getMode() {
		return mode;
	}

	public void setMode(DisplayModes mode) {
		this.mode = mode;
	}

	public int getSoundVolume() {
		return soundVolume;
	}

	public void setSoundVolume(int soundVolume) {
		this.soundVolume = soundVolume;
	}

	public int getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(int musicVolume) {
		this.musicVolume = musicVolume;
	}
	
	@Override
	public String toString() {		
		return "Full screen: " + fullScreen 
		+ " Full details: " + fullDetails 
		+ " Mode: " + mode 
		+ " Sound volume: " + soundVolume 
		+ " Music volume: " + musicVolume;
	}
	
}
