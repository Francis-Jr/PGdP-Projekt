package objects;

import java.util.Vector;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

public class DynamicTrap extends MovingGameObject{

	private int lastDirection = (int) Math.floor( 4 * Math.random() );
	//Direction code:    0 = UP   ,   1 = RIGHT   ,   2 = DOWN   ,   3 = LEFT
	
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
		return false;
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

	public void move() {
		
		//TODO KI???
		
		int direction = 0; //0 UP   1 RIGHT   2 DOWN   3 LEFT
		
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

	private int pickRandom(Vector<Integer> a) {
		return a.get((int) Math.floor((Math.random() * a.size())));
	}

	@Override
	public void printInTerminal() {
		terminal.moveCursor(x, y);
		terminal.applyBackgroundColor(level.getObjectAt(x, y).getColor());
		terminal.applyForegroundColor(getColor());
		terminal.putCharacter(getChar());
	}

	@Override
	public void unprint() {
		terminal.moveCursor(x, y);
		terminal.applyBackgroundColor(level.getObjectAt(x, y).getBgColor());
		terminal.applyForegroundColor(level.getObjectAt(x, y).getColor());
		terminal.putCharacter(level.getObjectAt(x, y).getChar());
	}

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
