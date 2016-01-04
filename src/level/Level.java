package level;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import main.T;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class Level {
	
	//TODO Chars aussuchen
	private final char  heart = '\u2665',
						key = '$',
						outerWall = '\u00B7',
						frameVertical = '\u2502',
						frameHorizontal = '\u2500',
						frameUpperRight = '\u2510',
						frameUpperLeft = '\u250C',
						frameLowerRight = '\u2518',
						frameLowerLeft = '\u2514';
						
	
	private Terminal terminal;
	
	private int[][] objects;
	private Vector<DynamicTrap> dynTraps = new Vector<DynamicTrap>();
	private int levelWidth, levelHeight;
	private int windowWidth, windowHeight, windowPositionX, windowPositionY;
	private int playerX, playerY;
	private boolean isFrozen = false;
	private boolean hasKey = false;
	private int livesLeft = 5;
	
	public Level(Terminal terminal, String path){
		this.terminal = terminal;
		
		Properties obj = new Properties();
		
		//create InputStream
			BufferedInputStream stream = null;
			try {
				stream = new BufferedInputStream(new FileInputStream(path));
			} 
			catch (FileNotFoundException e) {
				System.err.println("[ERROR] @ Level() : File " + path + " not found."); 
				e.printStackTrace();
			}
		
		//Load Level
			try {
				obj.load(stream);
			} catch (IOException e) { e.printStackTrace(); }
		
		//Close Stream
			try {
				stream.close();
			} catch (IOException e) { e.printStackTrace();}
			
		/*	
		 *  (1) convert Properties to Array AND 
		 *	(2) find entry and place Player AND 
		 *  (3) put dynamic Traps in Vector
		 */
			levelWidth = Integer.parseInt(obj.getProperty("Width"));
			levelHeight = Integer.parseInt(obj.getProperty("Height"));
			objects = new int[levelWidth][levelHeight];
			for(int x = 0 ; x < objects.length ; x++){
				for(int y = 0 ; y < objects[x].length ; y++){
					//(1)
					try {
						objects[x][y] = Integer.parseInt(obj.getProperty(x + "," + y));
					}
					catch(NumberFormatException e){
						objects[x][y] = 6;
					}
					
					//(2)
					if(objects[x][y] == 1){
						playerX = x; playerY = y;
					}
					
					//(3)
					else if(objects[x][y] == 4){
						dynTraps.add(new DynamicTrap(x,y));
						objects[x][y] = 6;
					}
				}
			}
			
		//Ränder hervorheben:
			for(int x = 0 ; x < levelWidth ; x++){
				if(objects[x][0] == 0)
					objects[x][0] = 8;
				if(objects[x][levelHeight - 1] == 0)
					objects[x][levelHeight - 1] = 8;
			}
			for(int y = 0 ; y < levelHeight ; y++){
				if(objects[0][y] == 0)
					objects[0][y] = 8;
				if(objects[levelWidth - 1][y] == 0)
					objects[levelWidth - 1][y] = 8;
			}
			
		//Fenstergroesse setzen und Fenster um Player zentrieren
			windowWidth = terminal.getTerminalSize().getColumns();
			windowHeight = terminal.getTerminalSize().getRows() - 5; //5 Zeilen Scoreboard
			windowPositionX = playerX - windowWidth/2;
			windowPositionY = playerY - windowHeight/2;
			if(windowPositionX < 0 ) windowPositionX = 0;
			else if (windowPositionX > levelWidth - windowWidth - 1) windowPositionX = levelWidth - windowWidth - 1;
			if(windowPositionY < 0 ) windowPositionY = 0;
			else if (windowPositionY > levelHeight - windowHeight - 1) windowPositionY = levelHeight - windowHeight - 1;
			
		//Alle zeichen ins Terminal schreiben
			printWholeWindow();
	}

	public int getObjectAt(int x , int y){
		return objects[x][y];
	}
	
	public int getWidth(){
		return levelWidth;
	}
	
	public int getHeight(){
		return levelHeight;
	}
	
	private boolean isWalkableForTrap(int positionX, int positionY) {
		//player walkable!
		if(positionX < 0 || positionX >= levelWidth || positionY < 0 || positionY >= levelHeight) return false;
		switch(objects[positionX][positionY]){
		case 1:
		case 6:
		case 4:
		case 5:
			return true;
		default:
			return false;
		}
	}

	private boolean isWalkableForPlayer(int positionX, int positionY) {
		if(positionX < 0 || positionX >= levelWidth || positionY < 0 || positionY >= levelHeight) return false;
		switch(objects[positionX][positionY]){
		case 0:
		case 8:
			return false;
		case 2:
			return hasKey;
		default:
			return true;
		}
	}

	public void unprintMovingObjects(){
		if(isFrozen) return;
		for(DynamicTrap trap : dynTraps){
			printInTerminal(trap.getPositionX(), trap.getPositionY());
		}
		printInTerminal(playerX , playerY);
	}

	/**
	 * 
	 * @param positionX position relativ zum Level
	 * @param positionY position relativ zum Level
	 */
	private void printInTerminal(int positionX, int positionY){
		if(positionX >= levelWidth || positionY >= levelHeight) 
			return;
		if(positionX < windowPositionX || positionX >= windowPositionX + windowWidth ||
				positionY < windowPositionY || positionY >= windowPositionY + windowHeight)
			return;
		terminal.applyForegroundColor(getColor(objects[positionX][positionY]));
		terminal.applyBackgroundColor(getBgColor(objects[positionX][positionY]));
		terminal.moveCursor(positionX - windowPositionX, positionY - windowPositionY);
		terminal.putCharacter(getChar(objects[positionX][positionY]));
	}
	
	private void printPlayer(boolean onDynamicTrap){
		terminal.applyForegroundColor(getColor(7));
		if(onDynamicTrap)
			terminal.applyBackgroundColor(getColor(4));
		else
			terminal.applyBackgroundColor(getColor(objects[playerX][playerY]));
		terminal.moveCursor(playerX - windowPositionX, playerY - windowPositionY);
		terminal.putCharacter(getChar(7));
	}
	
	private void printDynamicTrap(DynamicTrap trap){
		if(trap.getPositionX() < windowPositionX || trap.getPositionX() >= windowPositionX + windowWidth || 
				trap.getPositionY() < windowPositionY || trap.getPositionY() >= windowPositionY + windowWidth)
			return;
		terminal.applyForegroundColor(getColor(4));
		terminal.applyBackgroundColor(getColor(objects[trap.getPositionX()][trap.getPositionY()]));
		terminal.moveCursor(trap.getPositionX() - windowPositionX, trap.getPositionY() - windowPositionY);
		terminal.putCharacter(getChar(4));
	}
	
	private void printWholeWindow(){
		terminal.clearScreen();
		//Labyrinth
			for(int x = windowPositionX ; x < windowPositionX + windowWidth ; x++){
				for(int y = windowPositionY ; y < windowPositionY + windowHeight; y++){
					printInTerminal(x,y);
				}
			}
			
		//DynamicTraps and Player
			printPlayer(false);
			for(DynamicTrap trap : dynTraps){
				printDynamicTrap(trap);
			}
			
		//Scoreboard
			printScoreboard();
				
	}
	
	private void printScoreboard(){
		int lastRow = terminal.getTerminalSize().getRows() - 1;
		int lastColumn = terminal.getTerminalSize().getColumns() - 1;
		
		//Rahmen
		terminal.applyForegroundColor(Color.YELLOW);
		terminal.applyBackgroundColor(Color.BLACK);
		terminal.moveCursor(0, lastRow-4);
		
		terminal.putCharacter(frameUpperLeft);
		for(int n = 0 ; n < terminal.getTerminalSize().getColumns() - 2 ; n++){
			terminal.putCharacter(frameHorizontal);
		}
		terminal.putCharacter(frameUpperRight);

		putChar(frameVertical, 0, lastRow-3);
		putChar(frameVertical, 0, lastRow-2);
		putChar(frameVertical, 0, lastRow-1);
		putChar(frameVertical, lastColumn, lastRow-3);
		putChar(frameVertical, lastColumn, lastRow-2);
		putChar(frameVertical, lastColumn, lastRow-1);
		
		terminal.moveCursor(0, lastRow);
		terminal.putCharacter(frameLowerLeft);
		for(int n = 0 ; n < terminal.getTerminalSize().getColumns() - 2 ; n++){
			terminal.putCharacter(frameHorizontal);
		}
		terminal.putCharacter(frameLowerRight);
		
		//Herzen
		terminal.applyForegroundColor(Color.RED);
		terminal.applyBackgroundColor(Color.BLACK);
		
		terminal.moveCursor(3, lastRow-3);
		terminal.putCharacter(frameUpperLeft);
		putCharacterTimes(frameHorizontal,11);
		terminal.putCharacter(frameUpperRight);
		
		terminal.moveCursor(3, lastRow - 2);
		terminal.putCharacter(frameVertical);
		terminal.putCharacter(' ');
		for(int n = 0 ; n < livesLeft ; n++){
			terminal.putCharacter(heart);
			terminal.putCharacter(' ');
		}
		if(livesLeft >= 0){
			for(int n = 0 ; n < 5 - livesLeft ; n++){
				terminal.putCharacter(' ');
				terminal.putCharacter(' ');
			}
		}
		terminal.putCharacter(frameVertical);
		
		terminal.moveCursor(3, lastRow-1);
		terminal.putCharacter(frameLowerLeft);
		putCharacterTimes(frameHorizontal,11);
		terminal.putCharacter(frameLowerRight);
			
		
		//Schlüssel
		terminal.applyForegroundColor(Color.YELLOW);
		terminal.applyBackgroundColor(Color.BLACK);
		
		terminal.moveCursor(18, lastRow-2);
		terminal.putCharacter(frameVertical);
		terminal.putCharacter(' ');
		terminal.putCharacter(hasKey ? key : ' ');
		terminal.putCharacter(' ');
		terminal.putCharacter(frameVertical);
		
		terminal.moveCursor(18, lastRow-3);
		terminal.putCharacter(frameUpperLeft);
		terminal.putCharacter(frameHorizontal);
		terminal.putCharacter(frameHorizontal);
		terminal.putCharacter(frameHorizontal);
		terminal.putCharacter(frameUpperRight);
		
		terminal.moveCursor(18, lastRow-1);
		terminal.putCharacter(frameLowerLeft);
		terminal.putCharacter(frameHorizontal);
		terminal.putCharacter(frameHorizontal);
		terminal.putCharacter(frameHorizontal);
		terminal.putCharacter(frameLowerRight);
	}
	
	private void putChar(char c, int x, int y){
		terminal.moveCursor(x, y);
		terminal.putCharacter(c);
	}
	
	private void putCharacterTimes(char c, int n){
		for(int i = 0 ; i < n ; i++)
		terminal.putCharacter(c);
	}
	
	private void printString(String text){
		char[] chars = text.toCharArray();
		for(int n = 0 ; n < chars.length ; n++){
			terminal.putCharacter(chars[n]);
		}
	}

	private char getChar(int obj){
		switch(obj){
		case 8:	return outerWall;//WandRand
		case 0: return 'X'; //wand
		case 2: return 'E'; //Ausgang
		case 3: return '#'; //static trap
		case 4: return '+'; //dynamic trap
		case 5: return key; //Schluessel
		case 7: return 'o'; //Player
		case 1:	return '-';	//eingang
		case 6: 			//leer
		default: return ' ';
		}
	}

	private Color getColor(int obj){
		switch(obj){
		case 0: return Color.WHITE; //wand
		case 2: return Color.BLUE; 	//Ausgang
		case 3: return Color.RED; 	//static trap
		case 4: return Color.RED; 	//dynamic trap
		case 5: return Color.YELLOW;//Schluessel
		case 7: return Color.GREEN;	//Player
		case 8:						//WandRand
		case 1:						//eingang
		case 6: 					//leer
		default: return Color.BLACK;
		}
	}
	
	private Color getBgColor(int obj){
		switch(obj){
		case 8:
		case 0: return Color.WHITE;
		default: return Color.BLACK;
		}
	}
	
	public void moveDynamicTraps(){
		if(isFrozen) return;
		for(DynamicTrap trap : dynTraps){
			trap.move(isWalkableForTrap(trap.getPositionX(),trap.getPositionY()-1), 
					isWalkableForTrap(trap.getPositionX()+1,trap.getPositionY()), 
					isWalkableForTrap(trap.getPositionX(),trap.getPositionY()+1), 
					isWalkableForTrap(trap.getPositionX()-1,trap.getPositionY()));
			printDynamicTrap(trap);
			if(trap.getPositionX() == playerX && trap.getPositionY() == playerY){
				printPlayer(true);
				damage(1);
			}
		}
	}
	
	public void movePlayer(int direction){ //0=UP , 1=RIGHT , 2=DOWN , 3=LEFT, 4=NO_MOVE
		if(isFrozen) return;
		int newX = playerX + (direction==1 ? 1 : 0) + (direction==3 ? -1 : 0);
		int newY = playerY + (direction==0 ? -1 : 0) + (direction==2 ? 1 : 0);
		
		if(newY >= 0 && newY < levelHeight && newX >= 0 && newX < levelWidth && isWalkableForPlayer(newX , newY)){
			playerX = newX;
			playerY = newY;
		}
		printPlayer(false);
		
		if(direction != 4){
			//falls Spieler ganz am Rand, neu zentrieren
			if(playerX <= windowPositionX || playerX >= windowPositionX + windowWidth -1){
				windowPositionX = playerX - windowWidth/2;
				if(windowPositionX < 0 ) windowPositionX = 0;
				else if (windowPositionX > levelWidth - windowWidth - 1) windowPositionX = levelWidth - windowWidth - 1;
				printWholeWindow();
				unprintMovingObjects();
				printPlayer(false);
			}	
			if(playerY <= windowPositionY || playerY >= windowPositionY + windowHeight -1){
				windowPositionY = playerY - windowHeight/2;
				if(windowPositionY < 0 ) windowPositionY = 0;
				else if (windowPositionY > levelHeight - windowHeight - 1) windowPositionY = levelHeight - windowHeight - 1;
				printWholeWindow();
				unprintMovingObjects();
				printPlayer(false);
			}
			
			//auf objekt reagieren
			interact();
		}
	}
	
	private void interact() {
		switch(objects[playerX][playerY]){
		case 2: winLevel(); break; //w/o key player couldnt walk on exit
		case 3: damage(2); break;
		case 5: hasKey = true;
				printScoreboard();
				objects[playerX][playerY] = 6; //removing key
				break; 
		}
	}

	private void damage(int i) {
		livesLeft -= i;
		
		if(livesLeft <= 0){
			livesLeft = 0;
			lose();
		}
		printScoreboard();
	}
	
	private void lose(){
		int startcolumn = 26;
		int lastRow = terminal.getTerminalSize().getRows() - 1;
		int lastColumn = terminal.getTerminalSize().getColumns() - 1;
		
		terminal.applyBackgroundColor(Color.BLACK);
		terminal.applyForegroundColor(Color.RED);
		
		terminal.moveCursor(startcolumn, lastRow-2);
		printString("You Lost!");
		
		freeze();
	}
	
	private void winLevel(){
		T.p("You win!");
		int startcolumn = 26;
		int lastRow = terminal.getTerminalSize().getRows() - 1;
		int lastColumn = terminal.getTerminalSize().getColumns() - 1;
		
		terminal.applyBackgroundColor(Color.BLACK);
		terminal.applyForegroundColor(Color.GREEN);
		
		terminal.moveCursor(startcolumn, lastRow-2);
		printString("You Won!");
		
		freeze();
	}
	
	public void freeze(){
		isFrozen = true;
		for(DynamicTrap trap : dynTraps){
			printDynamicTrap(trap);
		}
	}
	
	/**
	 * Operation codes:
	 * 0 Default (do nothing)
	 * 1 Quit Game
	 * 2 enter Menu
	 * @param key
	 * @return an operation code (list see above)
	 */
	public int getOperationCode (Key key){
		if(isFrozen && key.getKind().equals(Key.Kind.Escape))
			return 1;
		if(!isFrozen && key.getKind().equals(Key.Kind.Escape))
			return 2;
		return 0;
	}
}
