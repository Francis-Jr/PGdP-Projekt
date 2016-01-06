package objects;

import java.util.Vector;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

public abstract class MovingGameObject {

	//TODO Klasse aufräumen
	
	public static final char 	playerChar = 'o',
								dynamicTrapChar = '+';
	
	public static final Color	playerColor = Color.GREEN,
								dynamicTrapColor = Color.RED;
	
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
	
	public abstract boolean canWalk(int newX, int newY); //TODO frozen implementierens
	public abstract boolean hasKey();
	public abstract void printInTerminal();
	public abstract void unprint();
	
	protected int x,y;
	
	public abstract void win();
	
	/**
	 * 
	 * @return if the MovingGameObject removes the Key
	 */
	public abstract boolean tryGiveKey();
	
	public char getChar(){
		return charRepresentation;
	}
	
	public Color getColor(){
		return color;
	}
	
	public abstract void hurt(int damage);
	public abstract boolean isOnDynamicTrap(Vector<DynamicTrap> dynTraps);
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setPosition(int x ,int y){
		this.x = x;
		this.y = y;
	}
}
