package objects;

import java.util.Vector;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * Project Labyrinth (PGdP 1)
 * WS15/16 TUM
 * <p>
 * abstract class for any objects in the level that can move around
 * @version 19.01.2016
 * @author junfried
 */

public abstract class MovingGameObject {
	
//Appearance 
		public static final char 	playerChar = 'o',
									dynamicTrapChar = '+';
	
		public static final Color	playerColor = Color.GREEN,
									dynamicTrapColor = Color.RED;
	
//fields
	protected boolean 	canWalkEmpty,
						canWalkEntry,
						canWalkExit,
						canWalkExitKey,
						canWalkPlayer,
						canWalkDynamicTrap,
						canWalkStaticTrap,
						canWalkWall,
						canWalkDefault;
	
	protected char charRepresentation;
	protected Color color;
	
	protected Terminal terminal;
	protected Level level; 
	
	protected int x,y;
	
	
//abstract methods
	
	/**
	 * 
	 * @param newX
	 * @param newY
	 * @return whether or not this MovingGameObject can walk on the specified location
	 */
	public abstract boolean canWalk(int newX, int newY); 
	
	/**
	 * 
	 * @return whether or not this MovingGameObject has a Key
	 */
	public abstract boolean hasKey();
	
	/**
	 * prints the MovingGameObject in terminal.
	 */
	 /* this is not implemented here, because different objects could "stack". 
	 * In that case a Player is printed and an eventual DynamicTrap changes his 
	 * background color.
	 */
	public abstract void printInTerminal();
	
	/**
	 * unprints the MovingGameObject.
	 * this means printing the underlying StaticGameObject alone.
	 */
	public abstract void unprint();
	
	/**
	 * causes this object to "win" gets called if the object enters an exit with a key
	 */
	public abstract void win();
	
	/**
	 * gives a key to the MovingGameObject
	 * @return whether or not the MovingGameObject "took the key with him". 
	 * in this case the key is removed
	 */
	public abstract boolean tryGiveKey();
	
	/**
	 * hurts the MovingGameObject for a specified amount of lives
	 * @param damage
	 */
	public abstract void hurt(int damage);
	
	/**
	 * 
	 * @param dynTraps
	 * @return whether or not this MovingGameObject is on the same field with any other
	 * DynamicTrap from a given DynamicTrap Vector
	 */
	public abstract boolean isOnDynamicTrap(Vector<DynamicTrap> dynTraps);
	
	
//implemented methods
	
	/**
	 * 
	 * @return a char representation of the MovingGameObject
	 */
	public char getChar(){
		return charRepresentation;
	}
	
	/**
	 * 
	 * @return the color of the object in terminal
	 */
	public Color getColor(){
		return color;
	}
	
	/**
	 * 
	 * @return the x-coordinate of this object's position
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * 
	 * @return the y-coordinate of this object's position
	 */
	public int getY(){
		return y;
	}
	
	/**
	 * moves this object to a specified position
	 * this does not unprint or print the object
	 * @param x
	 * @param y
	 */
	public void setPosition(int x ,int y){
		this.x = x;
		this.y = y;
	}
}
