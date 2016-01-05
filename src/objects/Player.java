package objects;

public class Player extends MovingGameObject {

	public Player(int x, int y) {
		// TODO Auto-generated constructor stub
	}

	public void move(int direction){
		//TODO implement
		
		/*
		 * 
		 */
	}
	
	@Override
	public boolean canWalk(StaticGameObject obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPlayer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasKey() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void win() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean tryGiveKey() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void hurt(int damage) {
		// TODO Auto-generated method stub
		
	}

}
