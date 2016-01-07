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
import main.Main;
import main.T;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
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
		},
		saveMenuMessage = 
			{	"",
				"       Select Slot       ",
				"",
				"   (1)  Save to Slot 1",
				"   (2)  Save to Slot 2",
				"   (3)  Save to Slot 3",
				"   (4)  Save to Slot 4",
				"   Esc  back to Menu",
				""
			},
		loadMenuMessage = 
			{	"",
				"       Select Slot       ",
				"",
				"   (1)  Load from Slot 1",
				"   (2)  Load from Slot 2",
				"   (3)  Load from Slot 3",
				"   (4)  Load from Slot 4",
				"   Esc  back to Menu",
				""
			};
	
	private final static int scoreBoardHeight = 5;
	
	private final static Color 	scoreBoardBgColor = Color.BLACK,
								scoreBoardFrameColor = Color.YELLOW,
								heartColor = Color.RED,
								levelScoreColor = Color.WHITE;
	
	private static final String saveGamePath = "saves/",
								saveGameSuffix = ".properties";
	
	private Terminal terminal;
	
	private StaticGameObject[][] objects;
	private String sourcePath;
	private Player player = null;
	private Vector<DynamicTrap> dynTraps = new Vector<DynamicTrap>();
	
	private int levelWidth, levelHeight;
	private int windowWidth, windowHeight, windowPositionX, windowPositionY;
	private boolean isFrozen = false;
	private boolean menu = false,
					saveMenu = false,
					loadMenu = false;
	private boolean isWon = false;
	
	public Level(Terminal terminal, String path){
		init(terminal,path);
		//Alle zeichen ins Terminal schreiben
			printWholeLevel();
	}
	
	private void init(Terminal terminal, String path){
		this.terminal = terminal;
		sourcePath = path;
		
		Properties obj = new Properties();
		
		//Load obj
			try {
				BufferedInputStream stream = new BufferedInputStream(new FileInputStream(path));
				obj.load(stream);
				stream.close();
			} 
			catch (FileNotFoundException e) {
				System.err.println("[ERROR] @ Level() : File " + path + " not found."); 
				e.printStackTrace();
			}
			catch (IOException e) { e.printStackTrace(); }
			
		/*	
		 *  convert Properties to Array AND 
		 *	find entry and place Player AND 
		 *  put dynamic Traps in Vector
		 */
			levelWidth = Integer.parseInt(obj.getProperty("Width"));
			levelHeight = Integer.parseInt(obj.getProperty("Height"));
			objects = new StaticGameObject[levelWidth][levelHeight];
			dynTraps = new Vector<DynamicTrap>();
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
			windowHeight = terminal.getTerminalSize().getRows() - scoreBoardHeight; 
			windowPositionX = player.getX() - windowWidth/2;
			windowPositionY = player.getY() - windowHeight/2;
			if(windowPositionX < 0 ) windowPositionX = 0;
			else if (windowPositionX > levelWidth - windowWidth - 1) windowPositionX = levelWidth - windowWidth - 1;
			if(windowPositionY < 0 ) windowPositionY = 0;
			else if (windowPositionY > levelHeight - windowHeight - 1) windowPositionY = levelHeight - windowHeight - 1;
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

	public void printWholeLevel(){
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
		
		//Rahmen
		printTextBox(scoreBoardFrameColor,scoreBoardBgColor,
				getEmptyStringArray(terminal.getTerminalSize().getColumns()-2,scoreBoardHeight-2),
				0,lastRow - (scoreBoardHeight - 1));
				
		
		/*terminal.applyForegroundColor(scoreBoardFrameColor);
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
		terminal.putCharacter(frameLowerRight); TODO final remove */
		
		//Herzen
			int heartStartColumn = 3;
			printTextBox(heartColor,scoreBoardBgColor,
					getHeartString(),
					heartStartColumn,lastRow - (scoreBoardHeight - 1) + 1);
						
		
		/*terminal.applyForegroundColor(heartColor);
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
		terminal.putCharacter(frameLowerRight);TODO final remove*/
			
		
		//Schl�ssel
		int keyStartColumn = 18;
		
		printTextBox(StaticGameObject.getKeyColor(),scoreBoardBgColor,
				" " + (player.hasKey() ? StaticGameObject.getKeyChar() : " ") + " ",
				keyStartColumn,lastRow - (scoreBoardHeight - 1) + 1 );
		
		
		/*
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
		terminal.putCharacter(frameLowerRight);TODO final remove */
		
		//level score
		int levelScoreStartColumn = 30;
		printTextBox(levelScoreColor,scoreBoardBgColor,
				" " + Main.getLevelsWon() + " ",
				levelScoreStartColumn,lastRow - (scoreBoardHeight - 1) + 1);
	}

	private String getHeartString() {
		String returnString =  " ";
		for(int n = 0 ; n < player.getLives() ; n++){
			returnString = returnString  + heart + " ";
		}
		for(int n = 0 ; n < 5 - player.getLives() ; n++){
			returnString = returnString + "  ";
		}
		
		return returnString;
	}

	private String[] getEmptyStringArray(int lineLength, int lines) {
		String[] returnArray = new String[lines];
		String line = "";
		while(line.length() < lineLength){
			line = line + " ";
		}
		for(int n = 0 ; n < returnArray.length ; n++){
			returnArray[n] = line;
		}
		return returnArray;
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
	
	private void printString(String text){
		char[] chars = text.toCharArray();
		for(int n = 0 ; n < chars.length ; n++){
			terminal.putCharacter(chars[n]);
		}
	}
	
	private void printTextBox(Color color, Color bgColor, String text, int positionX, int postionY) {
		String[] textArray = {text};
		printTextBox(color, bgColor, textArray, positionX, postionY);
	}

	private void printTextBox(Color color, Color bgColor, String[] text, int positionX, int postionY){
		terminal.applyForegroundColor(color);
		terminal.applyBackgroundColor(bgColor);

		
		//bestimme Maße & Position der Box
		int boxWidth = 0;
		for(String line : text){ //bestimme längste Zeile von message
			if(line.length() > boxWidth){
				boxWidth = line.length();
			}
		}
		for(int n = 0 ; n<text.length ; n++){ //alle Zeile auf diese Länge erwitern
			while(text[n].length() < boxWidth){
				text[n] += " ";
			}
		}
		boxWidth += 2;
		int boxHeight = text.length + 2;
		
		 
		
		//Rahmen
		terminal.moveCursor(positionX, postionY);
		terminal.putCharacter(frameUpperLeft);
		putMultipleChars(frameHorizontal,boxWidth - 2);
		terminal.putCharacter(frameUpperRight);
			
		for(int n = 1 ; n < boxHeight -1 ; n++){
			putChar(frameVertical , positionX , postionY + n);
			putChar(frameVertical , positionX + boxWidth - 1, postionY + n);
		}
		
		terminal.moveCursor(positionX, postionY + boxHeight - 1);
		terminal.putCharacter(frameLowerLeft);
		putMultipleChars(frameHorizontal,boxWidth - 2);
		terminal.putCharacter(frameLowerRight);
		
		//Message
		for(int n = 1 ; n < boxHeight - 1 ; n++){
			terminal.moveCursor(positionX + 1, postionY + n);
			printString(text[n-1]);
		}
			
	}
	
	private void printMenuBox(Color color, Color bgColor , String[] message){
		int boxWidth = 0;
		for(String line : message){ //bestimme längste Zeile von message
			if(line.length() > boxWidth){
				boxWidth = line.length();
			}
		}
		boxWidth += 2;
		int boxHeight = message.length + 2;
		printTextBox(color,bgColor,message, (windowWidth - boxWidth) / 2, (windowHeight - boxHeight)/2);
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Vector<DynamicTrap> getDynamicTraps(){
		return dynTraps;
	}
	
	public void endLevel(boolean won){
		isWon = won;
		
		//Anzeigebox
		
		String[] message = 	{		"",
							(won ?  "      You won!" 	: "      You Lost!"),
									"",
							(won ?  "   (1) next level" :  "   (1) try again"),
									"   (2) quit        ",
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
	 * 3 leave Menu (continue Level)
	 * 4 next Level
	 * 5 retry Level
	 * 6 to save Menu
	 * 7 to load Menu
	 * 101-104 save to slot 1-4
	 * 201-204 load from slot 1-4
	 * @param key
	 * @return an operation code (list see above)
	 */
	public int getOperationCode (Key key){
		Kind kind = key.getKind();
		
		if(menu){
			if(kind.equals(Kind.Escape)){
				return 3;
			}
			if(kind.equals(Kind.NormalKey)){
				switch(key.getCharacter()){
				case '1': return 3;
				case '2': return 6;
				case '3': return 7;
				case '4': return 1;
				}
			}
		}
		
		else if(saveMenu){
			if(kind.equals(Kind.Escape)){
				return 2;
			}
			if(kind.equals(Kind.NormalKey)){
				switch(key.getCharacter()){
				case '1': case '2': case '3': case '4':
					return Integer.parseInt("" + key.getCharacter()) + 100;
				}
			}
		}
		
		else if(loadMenu){
			if(kind.equals(Kind.Escape)){
				return 2;
			}
			if(kind.equals(Kind.NormalKey)){
				switch(key.getCharacter()){
				case '1': case '2': case '3': case '4':
					return Integer.parseInt("" + key.getCharacter()) + 200;
				}
			}
		}
		
		//"End Level Menu"
		else if (isFrozen){
			if(kind.equals(Kind.NormalKey)){
				switch(key.getCharacter()){
				case '1': return (isWon ? 4 : 5);
				case '2': return 1;
				}
			}
		}
		
		//running level
		else{ 
			if(key.getKind().equals(Key.Kind.Escape))
				return 2;
			if(kind.equals(Kind.NormalKey)){
				switch(key.getCharacter()){
				case 'e': return 1;
				}
			}
		}
		
		//default
		return 0;
	}
	
	public void save(int slot){
		//TODO create slotX.properties if not existing
		
		//edit saveGame
		Properties saveGame = new Properties();
		
		saveGame.put("sourcePath", sourcePath);
		
		saveGame.put("levelsWon", ""+Main.getLevelsWon());
		
		saveGame.put("playerLives",""+player.getLives());
		saveGame.put("playerHasKey", ""+player.hasKey());
		saveGame.put("playerPositionX", ""+player.getX());
		saveGame.put("playerPositionY", ""+player.getY());

		for(int n = 0 ; n<dynTraps.size() ; n++){
			saveGame.put("dynamicTrap" + n + "X", ""+dynTraps.get(n).getX());
			saveGame.put("dynamicTrap" + n + "Y", ""+dynTraps.get(n).getY());
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
	
	/**
	 * loads from savegame
	 * @param slot
	 * @param terminal
	 */
	public void load(int slot, Terminal terminal){
		//load savegame
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
		
		//everything the constructor would do
			init(terminal,loadedGame.getProperty("sourcePath"));
		
		Main.setLevelsWon(Integer.parseInt(loadedGame.getProperty("levelsWon")));
			
		player.setLives(Integer.parseInt(loadedGame.getProperty("playerLives")));
		if(Boolean.parseBoolean(loadedGame.getProperty("playerHasKey"))){
			player.tryGiveKey();
		}
		player.setPosition(
				Integer.parseInt(loadedGame.getProperty("playerPositionX")), 
				Integer.parseInt(loadedGame.getProperty("playerPositionY")));
		
		for(int n = 0 ; n < dynTraps.size() ; n++){
			dynTraps.get(n).setPosition(
					Integer.parseInt(loadedGame.getProperty("dynamicTrap" + n + "X")),
					Integer.parseInt(loadedGame.getProperty("dynamicTrap" + n + "Y")));
		}
	}
	
	/**
	 * load new unplayed level (NOT from a savegame)
	 */
	public void load(Terminal terminal, String path){
		init(terminal, path);
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
		printWholeLevel();
	}

	public void reCenterY() {
		windowPositionY = player.getY() - windowHeight/2;
		if(windowPositionY < 0 ) windowPositionY = 0;
		else if (windowPositionY > levelHeight - windowHeight - 1) 
			windowPositionY = levelHeight - windowHeight - 1;
		printWholeLevel();
	}
	
	public void enterMenu(){
		isFrozen = true;
		menu = true;
		saveMenu = false;
		loadMenu = false;
		printWholeLevel();
		printMenuBox(Color.WHITE , Color.BLUE , menuMessage);
	}
	
	public void continueLevel(){
		isFrozen = false;
		menu = false;
		saveMenu = false;
		loadMenu = false;
		
		printWholeLevel();
	}

	public void enterSaveMenu() {
		isFrozen = true;
		menu = false;
		loadMenu = false;
		saveMenu = true;
		
		printWholeLevel();
		printMenuBox(Color.WHITE , Color.BLUE , saveMenuMessage);
	}

	public void enterLoadMenu() {
		isFrozen = true;
		menu = false;
		saveMenu = false;
		loadMenu = true;
		
		printWholeLevel();
		printMenuBox(Color.WHITE , Color.BLUE , loadMenuMessage);
	}

	/**
	 * resets the level
	 */
	public void reset() {
		init(terminal, sourcePath);
		printWholeLevel();
	}
}
