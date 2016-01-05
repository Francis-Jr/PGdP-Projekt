package objects;

public abstract class MovingGameObject {

	public abstract boolean canWalk(StaticGameObject obj);
	public abstract boolean isPlayer();
	public abstract boolean hasKey();
	
	protected int x,y;
	
	public abstract void win();
	
	/**
	 * 
	 * @return if the MovingGameObject removes the Key
	 */
	public abstract boolean tryGiveKey();
	
	public abstract char getChar();
	public abstract void hurt(int damage);
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
}
