package objects;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

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

	public boolean isBorderWall() {
		return isBorderWall;
	}

	public void setBorderWall(boolean isBorderWall) {
		this.isBorderWall = isBorderWall;
		bgColor = (isBorderWall ? borderWallColor : wallColor);
	}

	@Override
	public void onContact(MovingGameObject mov) {
		System.err.println("[ALERT] onContact was called from a Wall...");
		//should not be called...
	}
}
