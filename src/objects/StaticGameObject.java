package objects;
import com.googlecode.lanterna.terminal.Terminal.Color;


public abstract class StaticGameObject {
	
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
	
	protected char charRepresentation = defaultChar;
	protected Color color = defaultColor;
	protected Color bgColor = defaultBgColor;
	
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
	
}
