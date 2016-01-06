package level;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

	private final static char  	heart = '\u2665',
								frameVertical = '\u2502',
								frameHorizontal = '\u2500',
								frameUpperRight = '\u2510',
								frameUpperLeft = '\u250C',
								frameLowerRight = '\u2518',
								frameLowerLeft = '\u2514';
	
	private final static String[] menuMessage = 
		{	"",
			"           Menu          ",
			"",
			"   (1) Continue Level",
			"   (2) Save Progress",
			"   (3) Load saved Game   ",
			"   (4) Quit",
			""
		};
	
	private final static Color 	scoreBoardBgColor = Color.BLACK,
								scoreBoardFrameColor = Color.YELLOW,
								heartColor = Color.RED;
	
	private static final String saveGamePath = "/saves/",
								saveGameSuffix = ".properties";
	
	private Terminal terminal;
	
	private StaticGameObject[][] objects;
	private String sourcePath;
	private Player player = null;
	private Vector<DynamicTrap> dynTraps = new Vector<DynamicTrap>();
	
	private int levelWidth, levelHeight;
	private int windowWidth, windowHeight, windowPositionX, windowPositionY;
	private boolean isFrozen = false;
	private boolean menu = false;
	private boolean isWon = false;
	
	public Level(Terminal terminal, String path){
		this.terminal = terminal;
		sourcePath = path;
		
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
						case 0:	objects[x][y] = new Wall(x,y,terminal,this,x==0 || y==0 || x==levelWidth-1 || y==levelHeight-1);
								break;
						case 1: objects[x][y] = new Entry(x,y,terminal,this);
								player = new Player(x,y,terminal,this); 
								break;
						case 2: objects[x][y] = new Exit(x,y,terminal,this);			
								break;
						case 3: objects[x][y] = new StaticTrap(x,y,terminal,this);	
								break;
						case 4: objects[x][y] = new Empty(x,y,terminal,this);
								dynTraps.add(new DynamicTrap(x,y,terminal,this));	
								break;
						case 5: objects[x][y] = new ExitKey(x,y,terminal,this);
								break;
						case 6: objects[x][y] = new Empty(x,y,terminal,this);
								break;
						}
					}
					catch(NumberFormatException e){
						objects[x][y] = new Empty(x,y,terminal,this);
					}
				}
			}
			if(player == null) System.err.println("[ERROR] @ Level() : no entry found. no player placed.");
			
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
		if(x<0 || x >= levelWidth || y<0 || y >= levelHeight)
			return null;
		return objects[x][y];
	}
	
	public int getWidth(){
		return levelWidth;
	}
	
	public int getHeight(){
		return levelHeight;
	}
	
	public int getWindowX(){
		return windowPositionX;
	}
	
	public int getWindowY(){
		return windowPositionY;
	}
	
	public int getWindowWidth(){
		return windowWidth;
	}
	
	public int getWindowHeight(){
		return windowHeight;
	}

	private void printWholeWindow(){
		terminal.clearScreen();
		//Labyrinth
			for(int x = windowPositionX ; x < windowPositionX + windowWidth ; x++){
				if(x >= levelWidth) break;
				for(int y = windowPositionY ; y < windowPositionY + windowHeight; y++){
					if(y >= levelHeight) break;
					objects[x][y].printInTerminal();
				}
			}
			
		//DynamicTraps and Player
			player.printInTerminal();
			for(DynamicTrap trap : dynTraps){
				trap.printInTerminal();
			}
			
		//Scoreboard
			printScoreboard();
	}
	
	public void printScoreboard(){
		int lastRow = terminal.getTerminalSize().getRows() - 1;
		int lastColumn = terminal.getTerminalSize().getColumns() - 1;
		
		//Rahmen
		terminal.applyForegroundColor(scoreBoardFrameColor);
		terminal.applyBackgroundColor(scoreBoardBgColor);
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
		int heartStartColumn = 3;
		terminal.applyForegroundColor(heartColor);
		terminal.applyBackgroundColor(scoreBoardBgColor);
		
		terminal.moveCursor(heartStartColumn, lastRow-3);
		terminal.putCharacter(frameUpperLeft);
		putCharacterTimes(frameHorizontal,11);
		terminal.putCharacter(frameUpperRight);
		
		terminal.moveCursor(heartStartColumn, lastRow - 2);
		terminal.putCharacter(frameVertical);
		terminal.putCharacter(' ');
		for(int n = 0 ; n < player.getLives() ; n++){
			terminal.putCharacter(heart);
			terminal.putCharacter(' ');
		}
		if(player.getLives() >= 0){
			for(int n = 0 ; n < 5 - player.getLives() ; n++){
				terminal.putCharacter(' ');
				terminal.putCharacter(' ');
			}
		}
		terminal.putCharacter(frameVertical);
		
		terminal.moveCursor(heartStartColumn, lastRow-1);
		terminal.putCharacter(frameLowerLeft);
		putCharacterTimes(frameHorizontal,11);
		terminal.putCharacter(frameLowerRight);
			
		
		//Schl�ssel
		int keyStartColumn = 18;
		terminal.applyForegroundColor(StaticGameObject.getKeyColor()); 
		terminal.applyBackgroundColor(scoreBoardBgColor);
		
		terminal.moveCursor(keyStartColumn, lastRow-2);
		terminal.putCharacter(frameVertical);
		terminal.putCharacter(' ');
		terminal.putCharacter(player.hasKey() ? StaticGameObject.getKeyChar() : ' ');
		terminal.putCharacter(' ');
		terminal.putCharacter(frameVertical);
		
		terminal.moveCursor(keyStartColumn, lastRow-3);
		terminal.putCharacter(frameUpperLeft);
		terminal.putCharacter(frameHorizontal);
		terminal.putCharacter(frameHorizontal);
		terminal.putCharacter(frameHorizontal);
		terminal.putCharacter(frameUpperRight);
		
		terminal.moveCursor(keyStartColumn, lastRow-1);
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
	
	private void putMultipleChars(char c , int amount){
		for(int n = 0; n < amount ; n++){
			terminal.putCharacter(c);
		}
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
	
	private void printMenuBox(Color color, Color bgColor , String[] message){
		terminal.applyForegroundColor(color);
		terminal.applyBackgroundColor(bgColor);
		
		int frameWidth = 1;
		
		//bestimme Maße & Position der Box
		int boxWidth = 0;
		for(String line : message){ //bestimme längste Zeile von message
			if(line.length() > boxWidth){
				boxWidth = line.length();
			}
		}
		for(int n = 0 ; n<message.length ; n++){ //alle Zeile auf diese Länge erwitern
			while(message[n].length() < boxWidth){
				message[n] += " ";
			}
		}
		boxWidth += 2 * frameWidth;
		int boxHeight = message.length + 2* frameWidth;
		
		int boxX = (windowWidth - boxWidth) / 2, boxY = (windowHeight - boxHeight)/2; 
		
		//Rahmen
		terminal.moveCursor(boxX, boxY);
		terminal.putCharacter(frameUpperLeft);
		putMultipleChars(frameHorizontal,boxWidth - 2);
		terminal.putCharacter(frameUpperRight);
			
		for(int n = 1 ; n < boxHeight -1 ; n++){
			putChar(frameVertical , boxX , boxY + n);
			putChar(frameVertical , boxX + boxWidth - 1, boxY + n);
		}
		
		terminal.moveCursor(boxX, boxY + boxHeight - 1);
		terminal.putCharacter(frameLowerLeft);
		putMultipleChars(frameHorizontal,boxWidth - 2);
		terminal.putCharacter(frameLowerRight);
		
		//Message
		for(int n = 1 ; n < boxHeight - 1 ; n++){
			terminal.moveCursor(boxX + 1, boxY + n);
			printString(message[n-1]);
		}
			
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Vector<DynamicTrap> getDynamicTraps(){
		return dynTraps;
	}
	
	public void endLevel(boolean won){
		isWon = won;
		
		int startcolumn = 26;
		int lastRow = terminal.getTerminalSize().getRows() - 1;
		
		
		//Anzeigebox
		
		String[] message = 	{		"",
							(won ?  "      You won!" 	: "      You Lost!"),
									"",
									"   (1) try again    ",
									"   (2) quit",
									"",
									""};	   
		
		printMenuBox(Color.WHITE,(won ? Color.GREEN : Color.RED),  message);

		isFrozen = true;
		for(DynamicTrap trap : dynTraps){
			trap.printInTerminal();;
		}
	}
	
	/**
	 * Operation codes:
	 * 0 Default (do nothing)
	 * 1 Quit Game
	 * 2 enter Menu
	 * 3 next Level
	 * 4 retry Level
	 * @param key
	 * @return an operation code (list see above)
	 */
	public int getOperationCode (Key key){
		if(key.getKind().equals(Key.Kind.Escape))
			return 2;
		if(isFrozen && isWon && key.getKind().equals(Key.Kind.NormalKey) && key.getCharacter() == '1')
			return 3;
		if(isFrozen && key.getKind().equals(Key.Kind.NormalKey) && key.getCharacter() == '2')
			return 1;
		if(key.getKind().equals(Key.Kind.NormalKey) && key.getCharacter() == 'e')
			return 1; //TODO nur zum testen
		return 0;
	}
	
	public void save(int slot){
		//edit saveGame
		Properties saveGame = new Properties();
		
		saveGame.put("sourcePath", sourcePath);
		
		saveGame.put("playerLives",player.getLives());
		saveGame.put("playerHasKey", player.hasKey());
		saveGame.put("playerPositionX", player.getX());
		saveGame.put("playerPositionY", player.getY());

		for(int n = 0 ; n<dynTraps.size() ; n++){
			saveGame.put("dynamicTrap" + n + "X", dynTraps.get(n).getX());
			saveGame.put("dynamicTrap" + n + "Y", dynTraps.get(n).getY());
		}

		//write saveGame to file
		try{ 
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
					saveGamePath + "slot" + slot + saveGameSuffix));
			saveGame.store(out, "SaveGame for PGdP-Projekt");
			out.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("[ERROR] @ Level.save() : FileNotFound trying to write on " 
						+ saveGamePath + "slot" + slot + saveGameSuffix); 
			e.printStackTrace();
		}
		catch (IOException e){
			System.err.println("[ERROR] @ Level.save() : IOException trying to write on " 
					+ saveGamePath + "slot" + slot + saveGameSuffix); 
			e.printStackTrace();
		}
	}
	
	public static Level load(int slot, Terminal terminal){
		Properties loadedGame = new Properties();
		try{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(
					saveGamePath + "slot" + slot + saveGameSuffix));
			loadedGame.load(in);
			in.close();
		}
		catch(FileNotFoundException e){
			System.err.println("[ERROR] @ Level.load() : File " + saveGamePath + 
					"slot" + slot + saveGameSuffix + " not found."); 
			e.printStackTrace();
		}
		catch (IOException e) {
			System.err.println("[ERROR] @ Level.load() : IOException trying to load "
					+ saveGamePath + "slot" + slot + saveGameSuffix );
			e.printStackTrace();
		}
		Level returnLevel = new Level(terminal,loadedGame.getProperty("sourcePath"));
		
		returnLevel.getPlayer().setLives(Integer.parseInt(loadedGame.getProperty("playerLives")));
		
		if(Boolean.parseBoolean(loadedGame.getProperty("playerHasKey"))){
			returnLevel.getPlayer().tryGiveKey();
		}
		
		returnLevel.getPlayer().setPosition(
				Integer.parseInt(loadedGame.getProperty("playerPositonX")), 
				Integer.parseInt(loadedGame.getProperty("playerPositonY")));
		
		for(int n = 0 ; n < returnLevel.getDynamicTraps().size() ; n++){
			returnLevel.getDynamicTraps().get(n).setPosition(
					Integer.parseInt(loadedGame.getProperty("dynamicTrap" + n + "X")),
					Integer.parseInt(loadedGame.getProperty("dynamicTrap" + n + "Y")));
		}
		
		return returnLevel;
	}

	public boolean isWon() {
		return isWon;
	}
	
	public boolean isFrozen(){
		return isFrozen;
	}

	public void reCenterX() {
		windowPositionX = player.getX() - windowWidth/2;
		if(windowPositionX < 0 ) windowPositionX = 0;
		else if (windowPositionX > levelWidth - windowWidth - 1) 
			windowPositionX = levelWidth - windowWidth - 1;
		printWholeWindow();
	}

	public void reCenterY() {
		windowPositionY = player.getY() - windowHeight/2;
		if(windowPositionY < 0 ) windowPositionY = 0;
		else if (windowPositionY > levelHeight - windowHeight - 1) 
			windowPositionY = levelHeight - windowHeight - 1;
		printWholeWindow();
	}
	
	public void enterMenu(){
		menu = true;
		printMenuBox(Color.WHITE , Color.BLUE , menuMessage);
	}
	
	public void continueLevel(){
		menu = false;
	}
}
