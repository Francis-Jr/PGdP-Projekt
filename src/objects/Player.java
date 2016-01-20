package objects;

import java.util.Vector;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Project Labyrinth (PGdP 1)
 * WS15/16 TUM
 * <p>
 * a player that can move through the labyrinth
 * there should only be one player in a level
 * @version 19.01.2016
 * @author junfried
 */
public class Player extends MovingGameObject {

	private static final int MAX_LIVES = 5;
	private static final boolean START_WITH_KEY = false;
	
	private boolean hasKey;
	private int lives;
	
	public Player(int x, int y, Terminal term, Level lv) {
		this.x = x;
		this.y = y;
		terminal = term;
		level = lv;
		
		lives = MAX_LIVES;
		hasKey = START_WITH_KEY;
		
		color = playerColor;
		charRepresentation = playerChar;
		
		canWalkEmpty = true;
		canWalkEntry = true;
		canWalkExitKey = true;
		canWalkPlayer = true; //shouldnt happen though...
		canWalkDynamicTrap = true;
		canWalkStaticTrap = true;
		canWalkWall = false;
		canWalkDefault = true;
	}

	/**
	 * moves the player in the specified direction
	 * this mehtod includes eventual recentering the screen, 
	 * if the player reaches the border of the screen and interaction with 
	 * StaticGameObjects, but not printing
	 * @param direction 0=Up , 1=Right , 2=Down , 3=Left
	 */
	public void move(int direction){
		switch(direction){
		case 0: 
			if(canWalk(x, y-1)) y-=1;
			break;
		case 1:
			if(canWalk(x+1, y)) x+=1;
			break;
		case 2:
			if(canWalk(x, y+1)) y+=1;
			break;
		case 3:
			if(canWalk(x-1, y)) x-=1;
			break;
		}
		
		if((y <= level.getWindowY() && y > 0)
				|| (y >= level.getWindowY() + level.getWindowHeight() - 1 &&
				y < level.getHeight()-1) ) {
			level.reCenterY();
		}
		
		if((x <= level.getWindowX() && x > 0)
				|| (x >= level.getWindowX() + level.getWindowWidth() - 1 &&
				x < level.getWidth()-1) ) {
			level.reCenterX();
		}
		
		level.getObjectAt(x, y).onContact(this);
	}
	
	/**
	 * @return whether or not the Player can walk the field at a specified location
	 */
	@Override
	public boolean canWalk(int newX, int newY) {
		if (newX < 0 || newY < 0 || newX >= level.getWidth() || newY >= level.getHeight())
			return false;
		if(level.isFrozen())
			return false;
		StaticGameObject obj = level.getObjectAt(newX, newY);
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
			return hasKey;
		return canWalkDefault;
	}

	/**
	 * @return whether or not the Player has a key
	 */
	@Override
	public boolean hasKey() {
		return hasKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void win() {
		level.endLevel(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean tryGiveKey() {
		hasKey = true;
		level.updateScoreBoard();
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hurt(int damage) {
		lives -= damage;
		if(lives < 0){
			lives = 0;
		}
		level.updateScoreBoard();
		if(lives == 0){
			level.endLevel(false);
		}
	}

	/**
	 * @return the number of lives left
	 */
	public int getLives(){
		return lives;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printInTerminal() {
		if(level.isFrozen()) return;
		if(x < level.getWindowX() || y < level.getWindowY() || 
				x >= level.getWindowX() + level.getWindowWidth() || 
				y >= level.getWindowY() +  level.getWindowHeight())
			return;
		terminal.moveCursor(x - level.getWindowX(), y - level.getWindowY());
		terminal.applyBackgroundColor(isOnDynamicTrap(level.getDynamicTraps()) ? dynamicTrapColor : level.getObjectAt(x, y).getColor());
		terminal.applyForegroundColor(color);
		terminal.putCharacter(charRepresentation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unprint() {
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
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOnDynamicTrap(Vector<DynamicTrap> dynTraps) {
		for(DynamicTrap trap : dynTraps){
			if(trap.getX() == x && trap.getY() == y){
				return true;
			}
		}
		return false;
	}

	/**
	 * sets the amount of lives left to a specified value
	 */
	public void setLives(int newAmount) {
		lives = newAmount;
		if(lives <= 0){
			lives = 1;
			System.err.println("[ALERT] Player.setLives() tried to set lives <= 0");
		}
	}

	/**
	 * 
	 * @return the maximum amount of lives possible for a Player
	 */
	public static int getMaxLives() {
		return MAX_LIVES; 
	}
	
}
