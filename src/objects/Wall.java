package objects;

public class Wall extends StaticGameObject {

	private boolean isBorderWall = false;
	
	public Wall(boolean isBorderWall){
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
		//should not be called...
	}
}
