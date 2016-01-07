package objects;
import level.Level;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;


public abstract class StaticGameObject {
	
	//TODO Klasse aufr�umen
	
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
	
	protected int x,y;
	protected Terminal terminal;
	protected Level level;
	protected char charRepresentation = defaultChar;
	protected Color color = defaultColor;
	protected Color bgColor = defaultBgColor;
	
	public static char getKeyChar(){
		return keyChar;
	}
	
	public void setLevel(Level lv){
		level = lv;
	}
	
	public char getChar(){
		return charRepresentation;
	}
	
	public Color getColor(){
		return color;
	}
	
	public Color getBgColor(){
		return bgColor;
	}
	
	public abstract void onContact(MovingGameObject mov);
	
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

	public static Color getKeyColor() {
		return keyColor;
	}
}
