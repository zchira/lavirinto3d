package zentity;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import zgame.Status;
import zgame.Status.PlayngState;
import zgame.model.ObjModel;
import zgame.texture.Texture;
import zgraphutils.ZGraphTraverser;
import zgraphutils.ZNode;
import zgraphutils.ZNodeInterface;
import zgraphutils.ZTraversingState;
import ZTableUtils.Direction;
import ZTableUtils.FieldListener;
import ZTableUtils.RotationDirection;
import ZTableUtils.SpriteListener;
import ZTableUtils.SpriteMovedToField;
import ZTableUtils.ZGraphForGame;
import ZXMLUtils.LevelReader;
import ZXMLUtils.TableFieldType;
import ZXMLUtils.ZXMLMonster;
import ZXMLUtils.ZXMLPlayer;

/**
 * 
 * @author zchira
 */
public class TableEntity extends AbstractEntity implements SpriteListener,
		FieldListener {
	private Texture texture;
	private Texture textureCenter;
	private ObjModel model;

	private ZGraphForGame<ZNode<String>> graf;

	private FieldEntity[][] table;

	private int dimX, dimY;

	private PlayerEntity player;

	private ArrayList<AbstractSprite> monsterList;

	// lista obidjenih cvorova
	private ArrayList<ZNodeInterface> traversedFieldList;

	private ZTraversingState traversingState;

	private long startTraversingTime;

	private long traversingTimePause = 50;

	/**
	 * number of all non-empty fields
	 */
	private int numberOfFields;

	/**
	 * number of traversed fields. If == numberOfFields, go to next level.
	 */
	private int numberOfTraversedFields;

	public TableEntity(Texture tex, Texture texCenter, ObjModel m) {
		texture = tex;
		textureCenter = texCenter;
		model = m;
	}

	public void collide(EntityManager manager, Entity other) {

	}

	/**
	 * Povezuje graf.
	 */
	private void connectGraph() {
		for (int j = 0; j < dimY; j++) {
			for (int i = 0; i < dimX; i++) {
				// set UP neghbour
				if (table[i][j].getNeighbour(Direction.UP) != null) {
					if (table[i][j].U
							&& table[i][j].getNeighbour(Direction.UP).D) {
						graf.insertEdge(table[i][j].getNode(), table[i][j]
								.getNeighbour(Direction.UP).getNode());

					}
				}

				// set RIGHT neghbour
				if (table[i][j].getNeighbour(Direction.RIGHT) != null) {
					if (table[i][j].R
							&& table[i][j].getNeighbour(Direction.RIGHT).L) {
						graf.insertEdge(table[i][j].getNode(), table[i][j]
								.getNeighbour(Direction.RIGHT).getNode());
					}
				}

				// set DOWN neghbour
				if (table[i][j].getNeighbour(Direction.DOWN) != null) {
					if (table[i][j].D
							&& table[i][j].getNeighbour(Direction.DOWN).U) {
						graf.insertEdge(table[i][j].getNode(), table[i][j]
								.getNeighbour(Direction.DOWN).getNode());
					}
				}

				// set LEFT neghbour
				if (table[i][j].getNeighbour(Direction.LEFT) != null) {
					if (table[i][j].L
							&& table[i][j].getNeighbour(Direction.LEFT).R) {
						graf.insertEdge(table[i][j].getNode(), table[i][j]
								.getNeighbour(Direction.LEFT).getNode());
					}
				}
			}
		}
	}

	public ArrayList<AbstractSprite> getMonsterList() {
		if (monsterList == null) {
			monsterList = new ArrayList<AbstractSprite>();
		}

		return monsterList;
	}

	public int getNumberOfFields() {
		return numberOfFields;
	}

	public int getNumberOfTraversedFields() {
		return numberOfTraversedFields;
	}

	public PlayerEntity getPlayer() {
		// if (player == null){
		// player = new PlayerEntity(texture, model);
		// }
		return player;
	}

	// @Override
	public float getSize() {
		return 0;
	}

	private ArrayList<ZNodeInterface> getTraversedFieldList() {
		if (traversedFieldList == null) {
			traversedFieldList = new ArrayList<ZNodeInterface>();
		}
		return traversedFieldList;
	}

	/**
	 * Inicijalizuje tablu. Puni susede ('neghbours').
	 */
	private void initTable() {
		for (int j = 0; j < dimY; j++) {
			for (int i = 0; i < dimX; i++) {
				graf.insertnode(table[i][j].getNode());

				// set UP neghbour
				if (j > 0) {
					table[i][j].setNeighbour(Direction.UP, table[i][j - 1]);
				} else {
					table[i][j].setNeighbour(Direction.UP, null);
				}

				// set RIGHT neghbour
				if (i < dimX - 1) {
					table[i][j].setNeighbour(Direction.RIGHT, table[i + 1][j]);
				} else {
					table[i][j].setNeighbour(Direction.RIGHT, null);
				}

				// set DOWN neghbour
				if (j < dimY - 1) {
					table[i][j].setNeighbour(Direction.DOWN, table[i][j + 1]);
				} else {
					table[i][j].setNeighbour(Direction.DOWN, null);
				}

				// set LEFT neghbour
				if (i > 0) {
					table[i][j].setNeighbour(Direction.LEFT, table[i - 1][j]);
				} else {
					table[i][j].setNeighbour(Direction.LEFT, null);
				}
				connectGraph();
				table[i][j].addFieldListener(this);
			}
		}
	}

	public boolean isAllConnected() {
		return getNumberOfFields() == getNumberOfTraversedFields();
	}

	@Deprecated
	public void loadTable(int dX, int dY) {

		dimX = dX;
		dimY = dY;
		// tmp
		dimX = 3;
		dimY = 4;
		graf = new ZGraphForGame<ZNode<String>>();

		table = new FieldEntity[dimX][dimY];

		table[0][0] = new FieldEntity(0, 0, 0, texture, texture, model);
		table[0][0].setPipes(false, true, true, false);

		table[1][0] = new FieldEntity(1, 1, 0, texture, texture, model);
		table[1][0].setPipes(false, true, true, true);

		table[2][0] = new FieldEntity(2, 2, 0, texture, texture, model);
		table[2][0].setPipes(true, false, false, true);

		table[0][1] = new FieldEntity(3, 0, 1, texture, texture, model);
		table[0][1].setPipes(true, true, true, false);

		table[1][1] = new FieldEntity(4, 1, 1, texture, texture, model);
		table[1][1].setPipes(true, true, true, true);

		table[2][1] = new FieldEntity(5, 2, 1, texture, texture, model);
		table[2][1].setPipes(true, false, true, false);

		table[0][2] = new FieldEntity(6, 0, 2, texture, texture, model);
		table[0][2].setPipes(false, true, true, false);

		table[1][2] = new FieldEntity(7, 1, 2, texture, texture, model);
		table[1][2].setPipes(false, true, true, true);

		table[2][2] = new FieldEntity(8, 2, 2, texture, texture, model);
		table[2][2].setPipes(true, false, false, true);

		table[0][3] = new FieldEntity(9, 0, 3, texture, texture, model);
		table[0][3].setPipes(false, true, false, false);

		table[1][3] = new FieldEntity(10, 1, 3, texture, texture, model);
		table[1][3].setPipes(false, true, true, true);

		table[2][3] = new FieldEntity(11, 2, 3, texture, texture, model);
		table[2][3].setPipes(true, false, true, false);

		initTable();

		table[1][3].addSprite(getPlayer());
		getPlayer().setInitialPosition(table[1][3]);
		getPlayer().addSpriteListener(this);

		MonsterDummy monster = new MonsterDummy(model);
		getMonsterList().clear();
		getMonsterList().add(monster);

		monster.addSpriteListener(this);
		table[0][1].addSprite(monster);
		monster.setInitialPosition(table[0][1]);

		traverseStart(table[1][3]);

	}

	public void loadTable(LevelReader levelReader) {
		numberOfFields = 0;
		// LevelReader levelReader = new LevelReader(levelName);
		dimX = levelReader.getLevelWidth() / 3;
		dimY = levelReader.getLevelHeight() / 3;

		graf = new ZGraphForGame<ZNode<String>>();
		table = new FieldEntity[dimX][dimY];

		TableFieldType[][] boardTable = levelReader.getLevelBoardField();

		int id = 0;
		for (int i = 0; i < dimY; i++) {
			for (int j = 0; j < dimX; j++) {
				table[j][i] = new FieldEntity(id, j, i, texture, textureCenter,
						model);
				boolean u = false;
				boolean r = false;
				boolean b = false;
				boolean l = false;

				if (boardTable[j * 3 + 1][i * 3] == TableFieldType.FullField) {
					u = true;
				}
				if (boardTable[j * 3 + 2][i * 3 + 1] == TableFieldType.FullField) {
					r = true;
				}
				if (boardTable[j * 3 + 1][i * 3 + 2] == TableFieldType.FullField) {
					b = true;
				}
				if (boardTable[j * 3][i * 3 + 1] == TableFieldType.FullField) {
					l = true;
				}

				if (u || r || b || l) {
					numberOfFields++;
				}

				// System.out.println("j: " + j + " i: " + i);
				// System.out.println(id + ": " + u + " " + r + " " + b + " " +
				// l);

				table[j][i].setPipes(u, r, b, l);
				// TODO set colors
				table[j][i].setVisitedColor(levelReader
						.getConectedFieldsColor());
				table[j][i].setNotVisitedColor(levelReader
						.getDisconectedFieldsColor());
				id++;
			}
		}

		initTable();

		ZXMLPlayer player = levelReader.getPlayerData();

		table[player.getXCord()][player.getYCord()].addSprite(getPlayer());
		getPlayer().setInitialPosition(
				table[player.getXCord()][player.getYCord()]);
		getPlayer().addSpriteListener(this);

		ArrayList<ZXMLMonster> monsterList = levelReader.getMonstersData();

		getMonsterList().clear();
		for (ZXMLMonster xmlMonster : monsterList) {
			MonsterDummy monster = null;
			switch (xmlMonster.getType()) {
			case dummyCcw:
				monster = new MonsterDummy(model, RotationDirection.ccw);
				break;
			case dummyCw:
				monster = new MonsterDummy(model, RotationDirection.cw);
				break;
			}

			getMonsterList().add(monster);

			monster.addSpriteListener(this);
			table[xmlMonster.getXCord()][xmlMonster.getYCord()]
					.addSprite(monster);
			monster.setInitialPosition(table[xmlMonster.getXCord()][xmlMonster
					.getYCord()]);
		}

		traverseStart(table[player.getXCord()][player.getYCord()]);
	}

	// @Override
	public void render() {
		GL11.glEnable(GL11.GL_LIGHTING);

		// store the original matrix setup so we can modify it
		// without worrying about effecting things outside of this
		// class
		GL11.glPushMatrix();

		GL11.glTranslatef(positionX, positionY, 0);

		GL11.glRotatef(rotationZ, 0, 0, 1);

		// GL11.glColor3f(0.6f, 0.5f, 0.8f);
//		for (int j = 0; j < dimY; j++) {
//			for (int i = 0; i < dimX; i++) {
//				table[i][j].render();
//
//			}
//		}

		/**
		 * reflection
		 */
		// GL11.glLoadIdentity();
		// getCamera().render();
//		GL11.glColorMask(false, false, false, false);
//
//		GL11.glEnable(GL11.GL_STENCIL_TEST);
//		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
//		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
//		GL11.glDisable(GL11.GL_DEPTH_TEST);
//
//		for (int j = 0; j < dimY; j++) {
//			for (int i = 0; i < dimX; i++) {
//				table[i][j].renderMirrorPlane();
//
//			}
//		}
//
//		// StaticRenderTools.drawShadow(0, 0, 0.0001f, 2, 2);
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
//		GL11.glColorMask(true, true, true, true);
//		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
//		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
//
//		GL11.glPushMatrix();
//		GL11.glScalef(1.0f, 1.0f, -1.0f);
//		// GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, this.buffer);
//
//		GL11.glTranslatef(0.0f, 0, 0.0f);
//		// GL11.glRotatef(angle, 0, 0, -1);
//
//		for (int j = 0; j < dimY; j++) {
//			for (int i = 0; i < dimX; i++) {
//				table[i][j].renderSprites();
//
//			}
//		}
//		GL11.glPopMatrix();
//
//		GL11.glDisable(GL11.GL_STENCIL_TEST);
//		// GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, this.buffer);
//
//		GL11.glEnable(GL11.GL_BLEND);
//
//		GL11.glDisable(GL11.GL_LIGHTING);
//		// GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, this.buffer); //
//		// Lighting
//		GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//		GL11.glEnable(GL11.GL_LIGHTING);
		
		
		
		for (int j = 0; j < dimY; j++) {
			for (int i = 0; i < dimX; i++) {
//				GL11.glEnable(GL11.GL_BLEND);
////				table[i][j].renderMirrorPlane();
//				GL11.glDisable(GL11.GL_BLEND);
				
				table[i][j].render();
				table[i][j].renderFieldAura();
				
			}
		}
		// StaticRenderTools.drawShadow(0, 0, 0.0001f, 2, 2);
		GL11.glEnable(GL11.GL_LIGHTING); // Enable Lighting
		GL11.glEnable(GL11.GL_BLEND); // Disable Blending
		// ///////////////////////

		for (int j = 0; j < dimY; j++) {
			for (int i = 0; i < dimX; i++) {
//				table[i][j].render();
				table[i][j].renderSprites();
				table[i][j].renderSpritesShadows();
				
			}
		}

		GL11.glPopMatrix();

	}

	// @Override
	public void rotationFinished(FieldEntity field) {
		if (field.getNeighbour(Direction.UP) != null) {
			if (field.U && field.getNeighbour(Direction.UP).D) {
				graf.insertBidirectionalEdge(field.getNode(), field
						.getNeighbour(Direction.UP).getNode());
			}
		}

		if (field.getNeighbour(Direction.RIGHT) != null) {
			if (field.R && field.getNeighbour(Direction.RIGHT).L) {
				graf.insertBidirectionalEdge(field.getNode(), field
						.getNeighbour(Direction.RIGHT).getNode());
			}
		}

		if (field.getNeighbour(Direction.DOWN) != null) {
			if (field.D && field.getNeighbour(Direction.DOWN).U) {
				graf.insertBidirectionalEdge(field.getNode(), field
						.getNeighbour(Direction.DOWN).getNode());
			}
		}

		if (field.getNeighbour(Direction.LEFT) != null) {
			if (field.L && field.getNeighbour(Direction.LEFT).R) {
				graf.insertBidirectionalEdge(field.getNode(), field
						.getNeighbour(Direction.LEFT).getNode());
			}
		}

		// traversingState = ZGraphTraverser.breadthTrav(graf, field,
		// getTraversedFieldList());
		traverseStart(field);
	}

	// @Override
	public void rotationStarted(FieldEntity field, RotationDirection dir) {
		if (field.getNeighbour(Direction.UP) != null) {
			graf.deleteEdges(field.getNode(), field.getNeighbour(Direction.UP)
					.getNode());
		}
		if (field.getNeighbour(Direction.RIGHT) != null) {
			graf.deleteEdges(field.getNode(), field.getNeighbour(
					Direction.RIGHT).getNode());
		}
		if (field.getNeighbour(Direction.DOWN) != null) {
			graf.deleteEdges(field.getNode(), field
					.getNeighbour(Direction.DOWN).getNode());
		}
		if (field.getNeighbour(Direction.LEFT) != null) {
			graf.deleteEdges(field.getNode(), field
					.getNeighbour(Direction.LEFT).getNode());
		}
		traverseStart(field);
	}

	public void setMonsterList(ArrayList<AbstractSprite> monsterList) {
		this.monsterList = monsterList;
	}

	public void setPlayer(PlayerEntity player) {
		this.player = player;
	}

	// @Override
	public void spriteMovedToField(SpriteMovedToField e) {
		FieldEntity oldP = e.getOldField();
		FieldEntity newP = e.getNewField();
		AbstractSprite sprite = (AbstractSprite) e.getSource();

		if (oldP != null && newP != null) {
			oldP.removeSprite(sprite);
			newP.addSprite(sprite);
			sprite.setCurrentField(newP);
		}
	}

	private void traverseContinue() {
		// System.out.println("TableEntity.traverseContinue()");
		traversingState = ZGraphTraverser.breadthTrav(graf, null,
				getTraversedFieldList(), ZTraversingState.inProgress);
		startTraversingTime = System.currentTimeMillis();
		numberOfTraversedFields = ZGraphTraverser.getNumOfTraversed();
		// System.out.println(numberOfTraversedFields + " of " +
		// numberOfFields);

	}

	private void traverseStart(FieldEntity field) {
		// System.out.println("TableEntity.traverseStart()");
		numberOfTraversedFields = 0;
		traversingState = ZGraphTraverser.breadthTrav(graf, field.getNode(),
				getTraversedFieldList(), ZTraversingState.started);
		startTraversingTime = System.currentTimeMillis();
	}

	@Override
	public void update(EntityManager manager, int delta) {
		for (int j = 0; j < dimY; j++) {
			for (int i = 0; i < dimX; i++) {
				table[i][j].update(manager, delta);
			}
		}

		// zato sto se update sada vrsi u inGameState
		// for (AbstractSprite sprite : getMonsterList()) {
		// sprite.update(manager, delta);
		// }

		// animiranje obilaska grafa
		if (traversingState == ZTraversingState.inProgress) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - startTraversingTime > traversingTimePause) {
				traverseContinue();
			}
		}
		if (traversingState == ZTraversingState.finished
				&& Status.getPlayngState() != PlayngState.allFieldsConnected
				&& Status.getPlayngState() != PlayngState.levelCompleted) {
			if (numberOfTraversedFields == numberOfFields && player.isAlive()) {
				Status.setPlayngState(PlayngState.allFieldsConnected);
				// System.out.println("LEVEL COMPLETED");
			}
		}
	}

}
