package zgame.states;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import zentity.AbstractSprite;
import zentity.Entity;
import zentity.EntityManager;
import zentity.PlayerEntity;
import zentity.TableEntity;
import zentity.ZCamera;
import zgame.GameWindow;
import zgame.StaticRenderTools;
import zgame.Status;
import zgame.ZGlobals;
import zgame.ZSounds;
import zgame.Status.PlayngState;
import zgame.model.ObjLoader;
import zgame.model.ObjModel;
import zgame.sound.Sound;
import zgame.sound.SoundLoader;
import zgame.texture.Texture;
import zgame.texture.TextureLoader;
import ZGameStatistic.ManageStatistics;
import ZXMLUtils.LevelReader;

/**
 * 
 * @author zchira
 */
public class InGameState implements GameState, EntityManager {

	/** The unique name of this state */
	public static final String NAME = "ingame";
	/** The texture for the back drop */
	private Texture background;

	private Texture fieldTexture;
	private Texture fieldTextureCenter;

	/** The model rendered for the asteroids */
	private ObjModel fieldModel;
	/** The texture applied to the asteroids */
	private Texture playerTexture;

	private Texture particleTexture;
	/** The model rendered for the asteroids */
	private ObjModel playerModel;

	// /** The fonts used to draw the text to the screen */
	// private BitmapFont font256;
	//
	// private BitmapFont font512;

	private TableEntity tableEntity;

	private PlayerEntity playerEntity;
	/** The entities in the game */
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	/** The list of entities to be added at the next opportunity */
	private ArrayList<Entity> addList = new ArrayList<Entity>();
	/** The list of entities to be removed at the next opportunity */
	private ArrayList<Entity> removeList = new ArrayList<Entity>();

	/** The OpenGL material properties applied to everything in the game */
	private FloatBuffer material;

	private float globalScaleFactor = 1f;

	private float targetScaleFactor = 1f;

	private float bonusPoints;

	private float timeBonus;

	// neede for bonus
	private float numberOfFields;

	private float rotateAcceleration = 1f;

	/** The sound effect to play when shooting */
	private Sound music;
	/** The sound effect to play when rocks split apart */
	private Sound lifelostSound;
	private Sound lifelostEffect;

	private Sound gameOverEffect;

	private Sound timeoutSound;

	private Sound levelCompletedSound;

	private boolean playingTimeoutSound;

	/** The timeout for the game over message before resetting to the menu */
	private int gameOverTimeout;

	/** The timeout for the life lost before respawning */
	private int lifeLostTimeout;

	/** The timeout before going to next level */
	private int allConnectedTimeout;

	private ZCamera camera;

	/**
	 * true ako je player stradao zbog isteka vremena
	 */
	private boolean killedByTime;

	/**
	 * level timer
	 */
	private float time;

	private int backgroundDisplayListId;

	private float backgroundAngle;

	private int hudQuadsId;

	private boolean materialInitialized = false;

	/**
	 * Create a new game state
	 */
	public InGameState() {
	}

	// private boolean lightInitialized = false;

	/**
	 * @see zentity.EntityManager#addEntity(zentity.Entity)
	 */
	public void addEntity(Entity entity) {
		addList.add(entity);
	}

	/**
	 * Defint the light setup to view the scene
	 */
	private void defineLight() {
		// if (!lightInitialized) {

		FloatBuffer buffer;

		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(0).put(0).put(0).put(0);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, buffer);

		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(0.5f).put(0.5f).put(0.5f).put(1f);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, buffer);

		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(1f).put(1f).put(1f).put(1f);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, buffer);

		// setup the ambient light
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(0.4f).put(0.4f).put(0.4f).put(0.4f);
		buffer.flip();
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, buffer);
		// GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);

		// set up the position of the light
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(10).put(-10).put(20).put(0);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, buffer);

		GL11.glEnable(GL11.GL_LIGHT0);

	}

	private void defineMaterial() {

		if (!materialInitialized) {
			// material.put(1).put(1).put(1).put(1);
			// material.flip();
			// GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, material);
			// GL11.glMaterial(GL11.GL_BACK, GL11.GL_DIFFUSE, material);

			float[] no_mat = { 0.0f, 0.0f, 0.0f, 1.0f };
			float[] mat_ambient = { 1f, 1f, 1f, 1.0f };
			// float[] mat_ambient_color = { 0.8f, 0.8f, 0.2f, 1.0f };
			float[] mat_diffuse = { 0.1f, 0.5f, 0.8f, 1.0f };
			// float[] mat_specular = {1.0f, 1.0f, 1.0f, 1.0f};
			float[] mat_specular = { 0.5f, 0.5f, 0.5f, 0.5f };
			// float no_shininess = 0.0f;
			float low_shininess = 5.0f;
			// float high_shininess = 100.0f;
			// float[] mat_emission = { 0.3f, 0.2f, 0.2f, 0.0f };

			material = BufferUtils.createFloatBuffer(4);

			material.put(mat_ambient).flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, material);

			material.put(mat_diffuse).flip();// .put(1).put(1).put(1).put(1);
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, material);

			material.put(mat_specular).flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, material);

			// material.put(low_shininess);
			GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, low_shininess);

			material.put(no_mat).flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, material);
			materialInitialized = true;
		}
	}

	/**
	 * Draw the background image
	 * 
	 * @param window
	 *            The window to display the background in
	 */
	private void drawBackground(GameWindow window) {
		GL11.glColor3f(1,1,1);
		GL11.glPushMatrix();
		GL11.glRotatef(45, 1, 0, 0);
		GL11.glRotatef(backgroundAngle, 1, 1, 0);
		GL11.glCallList(getBackgroundCubeId());
		GL11.glPopMatrix();
	}

	/**
	 * Draw the overlay for score and lifes
	 * 
	 * @param window
	 *            The window in which the GUI is displayed
	 */
	private void drawHUD(GameWindow window) {
		window.enterOrtho();

		GL11.glCallList(getHudQuadsId());

		if (Status.getPlayngState() == PlayngState.paused) {
			// GL11.glEnable(GL11.GL_BLEND);
			StaticRenderTools.drawGrayQuad(0, 250, 800, 50);
			GL11.glColor3f(1, 1, 1);
			StaticRenderTools.getFont512().drawString(1, "PAUSE", 360, 255);
			// GL11.glDisable(GL11.GL_BLEND);
			StaticRenderTools.getFont512().drawString(1,
					"Press space to continue", 30, 480);
			StaticRenderTools.getFont256().drawString(1,
					"Press esc to go back to menu", 30, 510);

		}

		if (killedByTime) {
			// GL11.glEnable(GL11.GL_BLEND);
			StaticRenderTools.drawGrayQuad(0, 150, 800, 50);
			GL11.glColor3f(1, 1, 1);
			StaticRenderTools.getFont512().drawString(1, "No more time", 320,
					155);
			// GL11.glDisable(GL11.GL_BLEND);
		}

		GL11.glColor3f(1f, 0.59f, 0);
		StaticRenderTools.getFont256().drawString(1, "SCORE", 5, 5);
		StaticRenderTools.getFont256().drawString(1, "LIVES", 745, 5);
		StaticRenderTools.getFont256().drawString(0, "CONNECTED", 5, 600 - 45);
		StaticRenderTools.getFont256().drawString(0, "TIME", 375, 5);

		GL11.glColor3f(1, 1, 1);
		// points
		StaticRenderTools.getFont512().drawString(1,
				Long.toString(Status.getPoints()), 5, 25);
		// lives
		StaticRenderTools.getFont512().drawString(0,
				Integer.toString(Status.getLives()), 775, 25);
		// draw connected
		if (tableEntity.isAllConnected()) {
			GL11.glColor3f(1f, 0.59f, 0);

		} else {
			GL11.glColor3f(1, 1, 1);
		}
		StaticRenderTools.getFont512().drawString(
				0,
				tableEntity.getNumberOfTraversedFields() + " of "
						+ tableEntity.getNumberOfFields(), 5, 600 - 25);
		// draw time
		if (time < ZGlobals.TIME_OUT_WARNING) {
			GL11.glColor3f(1, 0, 0);
		}
		String timeString = ((Integer) (int) time).toString();
		int delta = ((timeString.length() - 1) * 8) + 16;
		StaticRenderTools.getFont512().drawString(0, timeString, 405 - delta,
				25);

		if (Status.getPlayngState() == zgame.Status.PlayngState.gameover) {
			GL11.glColor3f(1, 1, 1);
			StaticRenderTools.getFont512().drawString(1, "GAME OVER", 280, 286);
		}

		if (Status.getPlayngState() == zgame.Status.PlayngState.levelCompleted) {
			GL11.glColor3f(1, 1, 1);
			StaticRenderTools.getFont512().drawString(1, "LEVEL COMPLETED",
					512 - (int) 15 * 24, 236);
			StaticRenderTools.getFont512().drawString(0,
					"press space for next level", 512 - (int) 26 * 18, 286);

			StaticRenderTools.getFont512().drawString(1,
					((Integer) (int) timeBonus).toString(),
					652 - (int) 15 * 24, 127);
			StaticRenderTools.getFont512().drawString(1,
					((Integer) (int) bonusPoints).toString(),
					700 - (int) 15 * 24, 157);

			GL11.glColor3f(1f, 0.59f, 0);
			StaticRenderTools.getFont256().drawString(1, "Time bonus: ",
					512 - (int) 15 * 24, 136);
			StaticRenderTools.getFont256().drawString(1, "Connected bonus: ",
					512 - (int) 15 * 24, 166);

		}
		GL11.glColor3f(1, 1, 1);
		window.leaveOrtho();
	}

	/**
	 * @see zgame.states.GameState#enter(zgame.GameWindow)
	 */
	public void enter(GameWindow window) {
		initLevel();
	}

	private int getBackgroundCubeId() {
		if (backgroundDisplayListId == 0) {
			backgroundDisplayListId = GL11.glGenLists(1);
			GL11.glNewList(backgroundDisplayListId, GL11.GL_COMPILE);

			float dim = 200;
			background.bind();
			GL11.glBegin(GL11.GL_QUADS);

			// setSkyColor();
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(dim, -dim, -dim);
			// setGroundColor();
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(dim, dim, -dim);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(-dim, dim, -dim);
			// setSkyColor();
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(-dim, -dim, -dim);

			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(-dim, -dim, -dim);
			// setGroundColor();
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(-dim, dim, -dim);

			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(-dim, dim, dim);
			// setSkyColor();

			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(-dim, -dim, dim);

			// setGroundColor();
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(dim, dim, -dim);
			// setSkyColor();
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(dim, -dim, -dim);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(dim, -dim, dim);
			// setGroundColor();
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(dim, dim, dim);

			// back
			// setSkyColor();
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(-dim, -dim, dim);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(-dim, dim, dim);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(dim, dim, dim);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(dim, -dim, dim);
			// setGroundColor();

			// bottom
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(-dim, -dim, dim);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(dim, -dim, dim);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(dim, -dim, -dim);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(-dim, -dim, -dim);

			// top();
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(-dim, dim, -dim);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(dim, dim, -dim);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(dim, dim, dim);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(-dim, dim, dim);

			GL11.glEnd();

			GL11.glEndList();
		}
		return backgroundDisplayListId;
	}

	private ZCamera getCamera() {
		if (camera == null) {
			camera = new ZCamera(playerEntity);
		}
		return camera;
	}

	private int getHudQuadsId() {
		if (hudQuadsId == 0) {
			hudQuadsId = GL11.glGenLists(1);
			GL11.glNewList(hudQuadsId, GL11.GL_COMPILE);
			// score
			StaticRenderTools.drawGrayQuad(0, 0, 100, 50);
			// lives
			StaticRenderTools.drawGrayQuad(740, 0, 60, 50);
			// time
			StaticRenderTools.drawGrayQuad(360, 0, 75, 50);
			// connected
			StaticRenderTools.drawGrayQuad(0, 550, 140, 50);

			GL11.glEndList();
		}
		return hudQuadsId;
	}

	// private boolean pause;

	/**
	 * @see zgame.states.GameState#getName()
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * @see zgame.states.GameState#init(zgame.GameWindow)
	 */
	public void init(GameWindow window) throws IOException {
		// defineLight();

		loadTextures();
		playerModel = ObjLoader.loadObj("res/models/playerBall.obj");

		music = SoundLoader.getInstance().getOgg(ZSounds.IN_GAME_MUSIC);
		lifelostSound = SoundLoader.getInstance().getOgg(
				ZSounds.LIFE_LOST_MUSIC);
		lifelostEffect = SoundLoader.getInstance().getOgg(
				ZSounds.LIFE_LOST_EFFECT);
		timeoutSound = SoundLoader.getInstance().getOgg(ZSounds.TIME_OUT_MUSIC);
		gameOverEffect = SoundLoader.getInstance().getOgg(
				ZSounds.GAME_OVER_EFFECT);
		levelCompletedSound = SoundLoader.getInstance().getOgg(
				ZSounds.LEVEL_COMPLETED_MUSIC);
	}

	private void initLevel() {
		entities.clear();
		if (backgroundDisplayListId != 0) {
			GL11.glDeleteLists(backgroundDisplayListId, 1);
			backgroundDisplayListId = 0;
		}

		LevelReader levelReader = Status.getCurrentLevel();

		playerEntity = new PlayerEntity(playerTexture, particleTexture,
				playerModel);
		entities.add(playerEntity);

		getCamera().setTarget(playerEntity);

		getCamera().resetZoom();
		// String levelName = levelReader.getLevelName();
		// String description = levelReader.getLevelDescription();
		try {
//			System.out.println("Loading level textures.");
			background = TextureLoader.getInstance().getTexture(
					levelReader.getBackgrounImage());
		} catch (IOException e) {
			System.out.println("Can't find texture file.");
		}

		Status.setPlayngState(PlayngState.play);

		tableEntity = new TableEntity(fieldTexture, fieldTextureCenter,
				fieldModel);
		tableEntity.setPlayer(playerEntity);

		tableEntity.loadTable(levelReader);
		entities.addAll(tableEntity.getMonsterList());
		entities.add(tableEntity);

		allConnectedTimeout = ZGlobals.ALL_CONNECTED_TIMEOUT;
		globalScaleFactor = 100;
		targetScaleFactor = 1f;

		rotateAcceleration = 1;
		playingTimeoutSound = false;
		time = levelReader.getTime();
		music.playAsLoopMusic(1f, 1f);
		// pause = false;
		killedByTime = false;
		numberOfFields = tableEntity.getNumberOfFields() + 1;
		bonusPoints = 0;
		timeBonus = 0;
	}

	/**
	 * @see zgame.states.GameState#leave(zgame.GameWindow)
	 */
	public void leave(GameWindow window) {
		SoundLoader.getInstance().stopMusic();
		TextureLoader.getInstance().clearTexture(background);
		ManageStatistics.SaveStats();
		System.gc();
	}

	/**
	 * @see zentity.EntityManager#playerHit()
	 */
	public void playerHit() {

		lifelostSound.playAsMusic(1, 1);
		lifelostEffect.play(1, 1);
		Status.setLives(Status.getLives() - 1);
		if (Status.getLives() <= 0) {
			Status.setPlayngState(PlayngState.gameover);
			gameOverEffect.play(1f, 0.5f);
			gameOverTimeout = ZGlobals.GAMEOVER_TIMEOUT;
		} else {
			Status.setPlayngState(PlayngState.lifelost);
			lifeLostTimeout = ZGlobals.LIFE_LOST_TIMEOUT;
		}

	}

	/**
	 * @see zentity.EntityManager#removeEntity(zentity.Entity)
	 */
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}

	/**
	 * @see zgame.states.GameState#render(zgame.GameWindow, int)
	 */
	public void render(GameWindow window, int delta) {
		// GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
		// GL11.GL_MODULATE);
		
		
		GL11.glLoadIdentity();
		defineMaterial();
		
		// draw our background image
		GL11.glDisable(GL11.GL_LIGHTING);

		getCamera().render();

		drawBackground(window);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		GL11.glScaled(globalScaleFactor, globalScaleFactor, globalScaleFactor);

		// TODO: da li ovde ovo?
		defineLight();
		GL11.glEnable(GL11.GL_LIGHTING);
		// loop through all entities in the game rendering them
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);

			// ne renderujemo AbstractSprite zato sto njeh renderuje
			// FieldEntity
			if (!(entity instanceof AbstractSprite)) {
				entity.render();
			}
		}

//		GL11.glDisable(GL11.GL_LIGHTING);
//		drawBackground(window);
		
		drawHUD(window);
	}

	/**
	 * @see zentity.EntityManager#rockDestroyed(int)
	 */
	public void rockDestroyed(int size) {
	}

	/**
	 * @see zentity.EntityManager#shotFired()
	 */
	public void shotFired() {
	}

	/**
	 * @see zgame.states.GameState#update(zgame.GameWindow, int)
	 */
	public void update(GameWindow window, int delta) {
		float scaleSpeed = 100;

		boolean updateTimer = true;
		getCamera().update(this, delta);

		if (Status.getPlayngState() == PlayngState.levelCompleted) {
			updateTimer = false;
			targetScaleFactor = 0;
			scaleSpeed = 10000f;

			time -= delta / 6f;
			if (time > 0) {
				timeBonus += delta / 6f;
			} else {
				time = 0;
			}
			numberOfFields -= delta / 6f;
			if (numberOfFields > 0) {
				bonusPoints += delta / 6f;
			} else {
				numberOfFields = 0;
			}

			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.isKeyDown(ZGlobals.KEY_SPACE)) {
						music.stop();
						bonusPoints += numberOfFields;
						timeBonus += time;
						Status.nextLevel();
						Status.addPoints((int) timeBonus);
						Status.addPoints((int) bonusPoints);
						if (Status.getGamePack().getLevelCount() <= Status
								.getLevel()) {
							window.changeToState(OutroStoryState.NAME);
							return;
						}
						window.changeToState(LevelInfoState.NAME);
						// this.initLevel();
						return;
					}
				}
			}
		}

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.isKeyDown(ZGlobals.KEY_PAUSE)
						|| Keyboard.isKeyDown(ZGlobals.KEY_ESCAPE)
						|| Keyboard.isKeyDown(ZGlobals.KEY_SPACE)) {

					if (Status.getPlayngState() == PlayngState.paused) {
						if (Keyboard.isKeyDown(ZGlobals.KEY_ESCAPE)) {
							Status.nextLevel();
							window.changeToState(GameOverState.NAME);
						}
						Status.unPauseGame();
					} else if (Status.getPlayngState() == PlayngState.play) {
						Status.pauseGame();
					}
				}
			}
		}

		backgroundAngle += delta / 500f * rotateAcceleration;
		if (backgroundAngle >= 360)
			backgroundAngle = 0;

		// zoom in - zoom out
		if (globalScaleFactor < targetScaleFactor) {

			globalScaleFactor += delta / scaleSpeed
					* (targetScaleFactor - globalScaleFactor);

			if (globalScaleFactor >= targetScaleFactor) {
				globalScaleFactor = targetScaleFactor;
			}

		} else if (globalScaleFactor > targetScaleFactor) {
			globalScaleFactor -= delta / scaleSpeed
					* (globalScaleFactor - targetScaleFactor);
			if (globalScaleFactor <= targetScaleFactor) {
				globalScaleFactor = targetScaleFactor;
			}
		}

		if (Status.getPlayngState() == PlayngState.paused) {
			return;
		}

		if (Status.getPlayngState() == PlayngState.gameover) {
			updateTimer = false;
			gameOverTimeout -= delta;
			if (gameOverTimeout < 0) {
				Status.nextLevel();
				window.changeToState(GameOverState.NAME);
			}
		}

		if (Status.getPlayngState() == PlayngState.lifelost) {
			updateTimer = false;
			lifeLostTimeout -= delta;
			if (lifeLostTimeout < 0) {
				if (killedByTime) {
					initLevel();
				} else {
					Status.setPlayngState(PlayngState.play);
					lifeLostTimeout = ZGlobals.LIFE_LOST_TIMEOUT;
					playerEntity.respawn();
					music.playAsLoopMusic(1, 1);
					playingTimeoutSound = false;
					if (time <= 20) {
						time = 20;
					}
				}
			}
		}

		if (Status.getPlayngState() == PlayngState.allFieldsConnected) {
			updateTimer = false;
			if (playerEntity.isAlive()) {
				allConnectedTimeout -= delta;
				if (allConnectedTimeout < 0) {
					Status.setPlayngState(PlayngState.levelCompleted);
					allConnectedTimeout = ZGlobals.ALL_CONNECTED_TIMEOUT;
					levelCompletedSound.playAsMusic(1, 1);
				}
			} else {
				allConnectedTimeout = ZGlobals.ALL_CONNECTED_TIMEOUT;
			}
		}

		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);
			for (int j = i + 1; j < entities.size(); j++) {
				Entity other = (Entity) entities.get(j);

				if (!((entities.get(i) instanceof TableEntity) || (other instanceof TableEntity))) {
					if (entity.collides(other)) {
						entity.collide(this, other);
						other.collide(this, entity);
					}
				}
			}
		}

		if (updateTimer) {
			time -= delta / 1000f;
			if (time < ZGlobals.TIME_OUT_WARNING / 4) {
				rotateAcceleration = 4;
			} else if (time < ZGlobals.TIME_OUT_WARNING / 2) {
				rotateAcceleration = 3;
			} else if (time < ZGlobals.TIME_OUT_WARNING) {
				rotateAcceleration = 2;
				if (!playingTimeoutSound) {
					timeoutSound.playAsLoopMusic(1, 1);
					playingTimeoutSound = true;
				}
			} else {
				rotateAcceleration = 1;
				playingTimeoutSound = false;
			}

			if (time <= 0) {
				time = 0;
				if (!killedByTime) {
					playerEntity.kill();
					playerHit();
					killedByTime = true;
				}
			}

		}

		entities.removeAll(removeList);
		entities.addAll(addList);

		removeList.clear();
		addList.clear();

		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);
			entity.update(this, delta);
		}
	}

	@Override
	public void loadTextures() throws IOException {
		fieldTextureCenter = TextureLoader.getInstance().getTexture(
				"res/textures/tileDark.jpg");
		fieldTexture = TextureLoader.getInstance().getTexture(
				"res/textures/tileLight.jpg");

		fieldModel = ObjLoader.loadObj("res/models/cube4.obj");

		particleTexture = TextureLoader.getInstance().getTexture(
				"res/textures/particle.png");

		playerTexture = TextureLoader.getInstance().getTexture(
				"res/textures/field.jpg");

	}

}
