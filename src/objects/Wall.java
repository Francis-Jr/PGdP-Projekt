package objects;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Project Labyrinth (PGdP 1)
 * WS15/16 TUM
 * <p>
 * a non-walkthrough game object
 * @version 19.01.2016
 * @author junfried
 */
public class Wall extends StaticGameObject {

	private boolean isBorderWall = false;
	
	public Wall(int posX, int posY, Terminal term, Level lv, boolean isBorderWall){
		x = posX;
		y = posY;
		terminal = term;
		level = lv;
		charRepresentation = emptyChar;
		setBorderWall(isBorderWall);
	}

	/**
	 * 
	 * @return whether or not this Wall is at the border of the level
	 * those border walls are displayed in a different color.
	 */
	public boolean isBorderWall() {
		return isBorderWall;
	}

	/**
	 * sets this wall to be (or not to be) a border wall.
	 * those border walls are displayed in a different color.
	 * @param isBorderWall
	 */
	public void setBorderWall(boolean isBorderWall) {
		this.isBorderWall = isBorderWall;
		bgColor = (isBorderWall ? borderWallColor : wallColor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onContact(MovingGameObject mov) {
		System.err.println("[ALERT] onContact was called from a Wall...");
		//should not be called...
	}
}
