package objects;

import java.util.Vector;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Project Labyrinth (PGdP 1)
 * WS15/16 TUM
 * <p>
 * An object that can move around a level and hurts the player on contact
 * @version 19.01.2016
 * @author junfried
 */
public class DynamicTrap extends MovingGameObject{
	
	public DynamicTrap(int x, int y, Terminal term, Level lv) {
		this.x = x;
		this.y = y;
		terminal = term;
		level = lv;
		
		color = dynamicTrapColor;
		charRepresentation = '+';
		
		canWalkEmpty = true;
		canWalkEntry = false;
		canWalkExit = false;
		canWalkExitKey = false;
		canWalkPlayer = true;
		canWalkDynamicTrap = true;
		canWalkStaticTrap = true;
		canWalkWall = false;
		canWalkDefault = false;
	}

	@Override
	public boolean canWalk(int newX, int newY) {
		if (newX < 0 || newY < 0 || newX >= level.getWidth() || newY >= level.getHeight())
			return false;
		if(level.isFrozen())
			return false;
		StaticGameObject obj = level.getObjectAt(newX, newY);
		if (obj == null)
			return false;
		if(obj instanceof Empty)
			return canWalkEmpty;
		if(obj instanceof Wall)
			return canWalkWall;
		if(obj instanceof Entry)
			return canWalkEntry;
		if(obj instanceof ExitKey)
			return canWalkExitKey;
		if(obj instanceof StaticTrap)
			return canWalkStaticTrap;
		if(obj instanceof Exit)
			return canWalkExit;
		return canWalkDefault;
		
	}

	@Override
	public boolean hasKey() {
		return false; //traps dont have keys.
	}

	@Override
	public void win() {
		//do nothing. Traps dont win.
	}

	@Override
	public boolean tryGiveKey() {
		//traps dont take keys
		return false;
	}

	@Override
	public void hurt(int damage) {
		//do nothing
	}

	/**
	 * moves the DynamicObject to a random direction
	 */
	public void move() {
		int direction = 0; //0 UP   1 RIGHT   2 DOWN   3 LEFT    4 NoMovePossible
		
		//decide on direction
			Vector<Integer> possibleDirections = new Vector<>();
			if(canWalk(x, y-1)){
				possibleDirections.add(0);
			}
			if(canWalk(x+1, y)){
				possibleDirections.add(1);
			}
			if(canWalk(x, y+1)){
				possibleDirections.add(2);
			}
			if(canWalk(x-1, y)){
				possibleDirections.add(3);
			}
			direction = pickRandom(possibleDirections);
			
		switch(direction){
		case 0:	y -= 1;	break;
		case 1: x += 1; break;
		case 2: y += 1; break;
		case 3: x -= 1; break;
		}
			
		level.getObjectAt(x, y).onContact(this);
	}

	/**
	 * returns a random Element of a given Integer Vector
	 * @param a
	 * @return
	 */
	private int pickRandom(Vector<Integer> a) {
		if(a.size() == 0) return 4;
		return a.get((int) Math.floor((Math.random() * a.size())));
	}

	/**
	 * prints the DynamicTrap to the Terminal
	 */
	@Override
	public void printInTerminal() {
		if(level.isFrozen()) return;
		if(x < level.getWindowX() || y < level.getWindowY() || 
				x >= level.getWindowX() + level.getWindowWidth() || 
				y >= level.getWindowY() +  level.getWindowHeight())
			return;
		terminal.moveCursor(x - level.getWindowX(), y - level.getWindowY());
		terminal.applyBackgroundColor(level.getObjectAt(x, y).getColor());
		terminal.applyForegroundColor(getColor());
		terminal.putCharacter(getChar());
	}

	/**
	 * unprints the DynamicTrap. this happens by printing the underlying StaticGameObject
	 * this is used before moving.
	 */
	@Override
	public void unprint() {
		if(level.isFrozen()) return;
		if(x < level.getWindowX() || y < level.getWindowY() || 
				x >= level.getWindowX() + level.getWindowWidth() || 
				y >= level.getWindowY() +  level.getWindowHeight())
			return;
		terminal.moveCursor(x - level.getWindowX(), y - level.getWindowY());
		terminal.applyBackgroundColor(level.getObjectAt(x, y).getBgColor());
		terminal.applyForegroundColor(level.getObjectAt(x, y).getColor());
		terminal.putCharacter(level.getObjectAt(x, y).getChar());
	}

	/**
	 * Returns whether or not this trap is at the same position as any other trap in a given DynamicTrap Vector.
	 */
	@Override
	public boolean isOnDynamicTrap(Vector<DynamicTrap> dynTraps) {
		for(DynamicTrap trap : dynTraps){
			if(trap.getX() == x && trap.getY() == y && !(trap == this)){
				return true;
			}
		}
		return false;
	}
}
