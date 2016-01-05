package level;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import objects.DynamicTrap;
import objects.Empty;
import objects.Entry;
import objects.Exit;
import objects.ExitKey;
import objects.Player;
import objects.StaticGameObject;
import objects.StaticTrap;
import objects.Wall;
import main.T;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class Level {
	
	//TODO Chars aussuchen
	private final char  heart = '\u2665',
						key = '$',
						outerWall = ' ', //'\u00B7',
						frameVertical = '\u2502',
						frameHorizontal = '\u2500',
						frameUpperRight = '\u2510',
						frameUpperLeft = '\u250C',
						frameLowerRight = '\u2518',
						frameLowerLeft = '\u2514',
						escape = '\u241B',
						enter = '\u23CE';
						
	
	private Terminal terminal;
	
	private StaticGameObject[][] objects;
	private Player player;
	private Vector<DynamicTrap> dynTraps = new Vector<DynamicTrap>();
	
	private int levelWidth, levelHeight;
	private int windowWidth, windowHeight, windowPositionX, windowPositionY;
	private boolean isFrozen = false;
	private boolean isWon = false;
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
		 *  convert Properties to Array AND 
		 *	find entry and place Player AND 
		 *  put dynamic Traps in Vector
		 */
			levelWidth = Integer.parseInt(obj.getProperty("Width"));
			levelHeight = Integer.parseInt(obj.getProperty("Height"));
			objects = new StaticGameObject[levelWidth][levelHeight];
			for(int x = 0 ; x < objects.length ; x++){
				for(int y = 0 ; y < objects[x].length ; y++){
					//(1)
					try {
						switch(Integer.parseInt(obj.getProperty(x + "," + y))){
						case 0:	objects[x][y] = new Wall(x==0 || y==0 || x==levelWidth-1 || y==levelHeight-1);
								break;
						case 1: objects[x][y] = new Entry();
								player = new Player(x,y); 
								break;
						case 2: objects[x][y] = new Exit();			
								break;
						case 3: objects[x][y] = new StaticTrap();	
								break;
						case 4: dynTraps.add(new DynamicTrap(x,y));	
								break;
						case 5: objects[x][y] = new ExitKey();
								break;
						case 6: objects[x][y] = new Empty();
								break;
						}
					}
					catch(NumberFormatException e){
						objects[x][y] = new Empty();
					}
				}
			}
			
		//Fenstergroesse setzen und Fenster um Player zentrieren
			windowWidth = terminal.getTerminalSize().getColumns();
			windowHeight = terminal.getTerminalSize().getRows() - 5; //5 Zeilen Scoreboard
			windowPositionX = player.getX() - windowWidth/2;
			windowPositionY = player.getY() - windowHeight/2;
			if(windowPositionX < 0 ) windowPositionX = 0;
			else if (windowPositionX > levelWidth - windowWidth - 1) windowPositionX = levelWidth - windowWidth - 1;
			if(windowPositionY < 0 ) windowPositionY = 0;
			else if (windowPositionY > levelHeight - windowHeight - 1) windowPositionY = levelHeight - windowHeight - 1;
			
		//Alle zeichen ins Terminal schreiben
			printWholeWindow();
	}

	public StaticGameObject getObjectAt(int x , int y){
		return objects[x][y];
	}
	
	public int getWidth(){
		return levelWidth;
	}
	
	public int getHeight(){
		return levelHeight;
	}

	public void unprintMovingObjects(){
		if(isFrozen) return;
		for(DynamicTrap trap : dynTraps){
			printInTerminal(trap.getX(), trap.getY());
		}
		printInTerminal(player.getX() , player.getY());
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
		terminal.applyForegroundColor(objects[positionX][positionY].getColor());
		terminal.applyBackgroundColor(objects[positionX][positionY].getBgColor());
		terminal.moveCursor(positionX - windowPositionX, positionY - windowPositionY);
		terminal.putCharacter(objects[positionX][positionY].getChar());
	}
	
	private void printPlayer(boolean onDynamicTrap){
		terminal.applyForegroundColor(getColor(7));
		if(onDynamicTrap)
			terminal.applyBackgroundColor(getColor(4));
		else
			terminal.applyBackgroundColor(objects[player.getX()][player.getY()].getColor());
		terminal.moveCursor(player.getX() - windowPositionX, player.getY() - windowPositionY);
		terminal.putCharacter(getChar(7));
	}
	
	private void printDynamicTrap(DynamicTrap trap){
		if(trap.getX() < windowPositionX || trap.getX() >= windowPositionX + windowWidth || 
				trap.getY() < windowPositionY || trap.getY() >= windowPositionY + windowWidth)
			return;
		terminal.applyForegroundColor(getColor(4));
		terminal.applyBackgroundColor(objects[trap.getX()][trap.getY()].getColor());
		terminal.moveCursor(trap.getX() - windowPositionX, trap.getY() - windowPositionY);
		terminal.putCharacter(trap.getChar());
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
			
		
		//Schl�ssel
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
		case 8: return Color.CYAN;
		case 0: return Color.WHITE;
		default: return Color.BLACK;
		}
	}
	
	public void moveDynamicTraps(){
		if(isFrozen) return;
		for(DynamicTrap trap : dynTraps){
			trap.move(trap.canWalk(objects[trap.getX()][trap.getY()-1]), 
					trap.canWalk(objects[trap.getX()+1][trap.getY()]),
					trap.canWalk(objects[trap.getX()][trap.getY()+1]),
					trap.canWalk(objects[trap.getX()-1][trap.getY()]));
			printDynamicTrap(trap);
			if(trap.getX() == player.getX() && trap.getY() == player.getY()){
				printPlayer(true);
				damage(1);
			}
		}
	}
	
	//TODO weiter objektorientierung einf�hren
	
	public void movePlayer(int direction){ //0=UP , 1=RIGHT , 2=DOWN , 3=LEFT, 4=NO_MOVE
		if(isFrozen) return;
		int newX = player.getX() + (direction==1 ? 1 : 0) + (direction==3 ? -1 : 0);
		int newY = player.getY() + (direction==0 ? -1 : 0) + (direction==2 ? 1 : 0);
		
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
		case 2: endLevel(true); break; //w/o key player couldnt walk on exit
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
			endLevel(false);
		}
		printScoreboard();
	}
	
	private void endLevel(boolean won){
		isWon = won;
		
		int startcolumn = 26;
		int lastRow = terminal.getTerminalSize().getRows() - 1;
		int lastColumn = terminal.getTerminalSize().getColumns() - 1;
		
		terminal.applyBackgroundColor(Color.BLACK);
		terminal.applyForegroundColor(won ? Color.GREEN : Color.RED);
		
		terminal.moveCursor(startcolumn, lastRow-2);
		printString(won ? "You won!" : "You Lost!");
		
		int menucolumn = 40;
		terminal.moveCursor(menucolumn, lastRow-2);
		printString("(1) " + (won ? "next Level" : "try again"));
		terminal.moveCursor(menucolumn, lastRow-1);
		printString("(2) quit");

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
	 * 3 next Level
	 * @param key
	 * @return an operation code (list see above)
	 */
	public int getOperationCode (Key key){
		//TODO nach h�ufigkeit sortieren
		if(key.getKind().equals(Key.Kind.Escape))
			return 2;
		if(isFrozen && key.getKind().equals(Key.Kind.NormalKey) && key.getCharacter() == '1')
			return 3;
		if(isFrozen && key.getKind().equals(Key.Kind.NormalKey) && key.getCharacter() == '2')
			return 1;
		if(key.getKind().equals(Key.Kind.NormalKey) && key.getCharacter() == 'e')
			return 1; //TODO nur zum testen
		return 0;
	}
	
	public boolean isWon(){
		return isWon;
	}
	
	public void save(int slot){
		//TODO
	}
	
	public static Level load(int slot){
		//TODO
		return null;
	}
}
