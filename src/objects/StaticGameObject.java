package objects;
import level.Level;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * Project Labyrinth (PGdP 1)
 * WS15/16 TUM
 * <p>
 * an object in the labyrinth that does not move
 * @version 19.01.2016
 * @author junfried
 */

public abstract class StaticGameObject {
	
//Appearance
	protected static final Color	defaultColor = Color.WHITE,
									defaultBgColor = Color.BLACK,
									wallColor = Color.WHITE,
									borderWallColor = Color.CYAN,
									exitColor = Color.BLUE,
									staticTrapColor = Color.RED,
									keyColor = Color.YELLOW;
	
	protected static final char defaultChar = ' ',
								emptyChar = ' ',
								exitChar = 'E',
								staticTrapChar = '#',
								keyChar = '$';

//fields
	
	protected int x,y; 
	protected Terminal terminal;
	protected Level level;
	protected char charRepresentation = defaultChar;
	protected Color color = defaultColor;
	protected Color bgColor = defaultBgColor;
	
//abstract methods
	
	/**
	 * lets the StaticGameObject interact with a "trespassing" MovingGameObject
	 * @param mov
	 */
	public abstract void onContact(MovingGameObject mov);
	
//implemented methods
	
	/**
	 * 
	 * @return a char representation
	 */
	public char getChar(){
		return charRepresentation;
	}
	
	/**
	 * 
	 * @return the foregroundcolor
	 */
	public Color getColor(){
		return color;
	}
	
	/**
	 * 
	 * @return the backgrpundcolor
	 */
	public Color getBgColor(){
		return bgColor;
	}
	
	public void printInTerminal(){
		try{
			terminal.moveCursor(x - level.getWindowX(), y - level.getWindowY());
		} catch(NullPointerException e){
			System.err.println("Nullpointer trying to print " + this.getClass().getName() + " at (" + x + "," + y +")");
		}
		terminal.applyBackgroundColor(bgColor);
		terminal.applyForegroundColor(color);
		terminal.putCharacter(charRepresentation);
	}

//static methods
	
	public static Color getKeyColor() {
		return keyColor;
	}
	
	public static char getKeyChar(){
		return keyChar;
	}
}
