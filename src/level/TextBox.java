package level;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class TextBox {

	private final static char	frameVertical = '\u2502',
								frameHorizontal = '\u2500',
								frameUpperRight = '\u2510',
								frameUpperLeft = '\u250C',
								frameLowerRight = '\u2518',
								frameLowerLeft = '\u2514';
	
	private String[] message;
	private Terminal terminal;
	private Level level;
	
	private Color fgColor, bgColor;
	private int positionX, positionY, width, height;
	
	private boolean active = false;
	
	public TextBox(String[] msg, Color foreGround , Color backGround , int x, int y,Terminal term, Level lv){
		message = msg;
		fgColor = foreGround;
		bgColor = backGround;
		positionX = x;
		positionY = y;
		terminal = term;
		level = lv;
		this.setSize();
	}
	
	public TextBox(String[] msg, Color color, Color bgColor , Terminal term, Level lv){
		message = msg;
		fgColor = color;
		this.bgColor = bgColor;
		this.setSize();
		positionX = (terminal.getTerminalSize().getColumns() - width)/2;
		positionY = (terminal.getTerminalSize().getRows() - height)/2;
		terminal = term;
		level = lv;
	}
	
	private void setSize(){
		width = 0;
		for(String line : message){ //bestimme längste Zeile von message
			if(line.length() > width){
				width = line.length();
			}
		}
		for(int n = 0 ; n<message.length ; n++){ //alle Zeile auf diese Länge erweitern
			while(message[n].length() < width){
				message[n] += " ";
			}
		}
		width += 2; // 2 Spalten für den Rand
		height = message.length + 2; // 2 Zeilen für den Rand
	}
	
	public void printInTerminal(){
		active = true;
		
		terminal.applyForegroundColor(fgColor);
		terminal.applyBackgroundColor(bgColor);
		
		//Rahmen
			terminal.moveCursor(positionX, positionY);
			terminal.putCharacter(frameUpperLeft);
			putMultipleChars(frameHorizontal,width - 2);
			terminal.putCharacter(frameUpperRight);
				
			for(int n = 1 ; n < height -1 ; n++){
				terminal.moveCursor(positionX , positionY + n);
				terminal.putCharacter(frameVertical);
				terminal.moveCursor( positionX + width - 1, positionY + n);
				terminal.putCharacter(frameVertical);
			}
			terminal.moveCursor(positionX, positionY + height - 1);
			terminal.putCharacter(frameLowerLeft);
			putMultipleChars(frameHorizontal,width - 2);
			terminal.putCharacter(frameLowerRight);
			
			
		//Message
			for(int n = 1 ; n < height - 1 ; n++){
				terminal.moveCursor(positionX + 1, positionY + n);
				printString(message[n-1]);
			}
	}
	
	public void unPrint(){
		active = false;
	
		for(int x = positionX ; x < positionX + width ; x++){
			for(int y = positionY ; y < positionY + height ; y++){
				level.getObjectAt(x, y).printInTerminal();
			}
		}
	}
	
	private void putMultipleChars(char c , int amount){
		for(int n = 0; n < amount ; n++){
			terminal.putCharacter(c);
		}
	}
	
	private void printString(String text){
		char[] chars = text.toCharArray();
		for(int n = 0 ; n < chars.length ; n++){
			terminal.putCharacter(chars[n]);
		}
	}
	
	public boolean isActive(){
		return active;
	}
}
