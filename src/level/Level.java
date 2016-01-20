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

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;


/**
 * Project Labyrinth (PGdP 1)
 * WS15/16 TUM
 * <p>
 * TODO
 * @version 19.01.2016
 * @author junfried
 */
public class Level {
	
	//###  APPEARANCE (texts and colours)
	private final static Color 	menuTextColor = Color.WHITE,
								menuBgColor = Color.BLUE,
								wonColor = Color.GREEN,
								lostColor = Color.RED;
	
	private final static String[] menuMessage = 
		{	"",
			"           Menu          ",
			"",
			"   (1) Continue Level",
			"   (2) Save Progress",
			"   (3) Load saved Game   ",
			"   (4) How to play?      ",
			"   (5) Quit",
			""
		},
		howToPlayText = 
		{"",
		"        How to play       ",
		"",
		"    o Player   $ Key      ",
		"    # Trap     + Enemy    ",
		"    E Exit     x Wall     ",
		"    x Levelborder         ",
		"",
		"   find a key, then get   ",
		"  to the exit. dont die.  "
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
			},
		wonMessage = 
			{	"",
				"      You won!",
				"",
				"   (1) next level",
				"   (2) quit        ",
				"",
				""},
		lostMessage = 
			{	"",
				"      You Lost!",
				"",
				"   (1) try again",
				"   (2) quit        ",
				"",
				""};
	
	//These TextBoxes are menus or messages that can be displayed
	private TextBox menuBox, howToPlayBox, saveMenuBox, loadMenuBox, wonBox, lostBox;
	//The scoreboard displayes lives, key and levelscore at the bottom of the display
	private ScoreBoard scoreBoard;
	//elements of textBoxes will be unprinted from screen, when the game continues
	private TextBox[] textBoxes ;
	
	//Editable path and suffix for savegames
	private static final String saveGamePath = "saves/",
								saveGameSuffix = ".properties";
	
	//The terminal will be initialized with a swing terminal.
	private Terminal terminal;
	
	private StaticGameObject[][] staticObjects;
	
	//The path that this Level was loadad from and can be reset from (with path + suffix!)
	private String sourcePath;

	private Player player = null;
	private Vector<DynamicTrap> dynTraps = new Vector<DynamicTrap>();
	
	
	private int levelWidth, levelHeight;
	
	//the window can be smaller than the level!
	private int windowWidth, windowHeight;
	
	//The Position of the topmost, leftmost position of the WINDOW, relative o the LEVEL
	private int windowPositionX, windowPositionY;
	
	//In Menus the Level will stop moving. dynamic traps dont move, the player cant move
	private boolean isFrozen = false;
	
	//true if the corresponding menu is active. only one can be active at a time
	private boolean menu = false,
					saveMenu = false,
					loadMenu = false;
	
	//true if this level was won
	private boolean isWon = false;
	
	/**
	 * Loads the level from the specified sourcefile and displays it in the terminal
	 * @param terminal the terminal to display the level in
	 * @param path the path of the sourcefile
	 */
	public Level(Terminal terminal, String path){
		init(terminal,path);
		printWholeLevel();
	}
	
	/**
	 * initisalises the fields. is used in constructor and on resetting
	 * @param terminal
	 * @param path the path of the sourcefile
	 */
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
			staticObjects = new StaticGameObject[levelWidth][levelHeight];
			dynTraps = new Vector<DynamicTrap>();
			for(int x = 0 ; x < staticObjects.length ; x++){
				for(int y = 0 ; y < staticObjects[x].length ; y++){
					//(1)
					try {
						switch(Integer.parseInt(obj.getProperty(x + "," + y))){
						case 0:	staticObjects[x][y] = new Wall(x,y,terminal,this,x==0 || y==0 || x==levelWidth-1 || y==levelHeight-1);
								break;
						case 1: staticObjects[x][y] = new Entry(x,y,terminal,this);
								player = new Player(x,y,terminal,this); 
								break;
						case 2: staticObjects[x][y] = new Exit(x,y,terminal,this);			
								break;
						case 3: staticObjects[x][y] = new StaticTrap(x,y,terminal,this);	
								break;
						case 4: staticObjects[x][y] = new Empty(x,y,terminal,this);
								dynTraps.add(new DynamicTrap(x,y,terminal,this));	
								break;
						case 5: staticObjects[x][y] = new ExitKey(x,y,terminal,this);
								break;
						case 6: staticObjects[x][y] = new Empty(x,y,terminal,this);
								break;
						default:staticObjects[x][y] = new Empty(x,y,terminal,this);
						}
					}
					catch(NumberFormatException e){
						staticObjects[x][y] = new Empty(x,y,terminal,this);
					}
				}
			}
			if(player == null) System.err.println("[ERROR] @ Level() : no entry found. no player placed.");
			
		//TextBoxes initialisieren,
			menuBox = new TextBox(menuMessage, menuTextColor, menuBgColor, terminal);
			howToPlayBox = new TextBox(howToPlayText, menuTextColor, menuBgColor, 0 , 0 , terminal);
			saveMenuBox = new TextBox(saveMenuMessage , menuTextColor, menuBgColor, terminal);
			loadMenuBox = new TextBox(loadMenuMessage , menuTextColor, menuBgColor, terminal);
			wonBox = new TextBox(wonMessage , menuTextColor , wonColor , terminal);
			lostBox =  new TextBox(lostMessage , menuTextColor , lostColor , terminal);
			textBoxes = new TextBox[6];
			textBoxes[0] = menuBox;
			textBoxes[1] = howToPlayBox;
			textBoxes[2] = saveMenuBox;
			textBoxes[3] = loadMenuBox;
			textBoxes[4] = wonBox;
			textBoxes[5] = lostBox;
			scoreBoard = new ScoreBoard(terminal);	
			
		//Fenstergroesse setzen und Fenster um Player zentrieren
			windowWidth = terminal.getTerminalSize().getColumns();
			windowHeight = terminal.getTerminalSize().getRows() - 
					scoreBoard.getHeight(); 
			windowPositionX = player.getX() - windowWidth/2;
			windowPositionY = player.getY() - windowHeight/2;
			if(windowPositionX < 0 ) windowPositionX = 0;
			else if (windowPositionX > levelWidth - windowWidth - 1) windowPositionX = levelWidth - windowWidth - 1;
			if(windowPositionY < 0 ) windowPositionY = 0;
			else if (windowPositionY > levelHeight - windowHeight - 1) windowPositionY = levelHeight - windowHeight - 1;
	}

	/**
	 * returns the StaticGameObject at a specified location.
	 * This is not effected by any DynamicTrap or Player at the same location
	 * @param x the x-coordinate relative to the LEVEL
	 * @param y the y-coordinate relative to the LEVEL
	 * @return the StaticGameObject at the specified location
	 */
	public StaticGameObject getObjectAt(int x , int y){
		if(x<0 || x >= levelWidth || y<0 || y >= levelHeight)
			return null;
		return staticObjects[x][y];
	}
	
	public int getWidth(){
		return levelWidth;
	}
	
	public int getHeight(){
		return levelHeight;
	}
	
	/**
	 * 
	 * @return the x-position of the top-left corner of the terminal-window relative to Level
	 */
	public int getWindowX(){
		return windowPositionX;
	}
	
	/**
	 * 
	 * @return the y-position of the top-left corner of the terminal-window relative to Level
	 */
	public int getWindowY(){
		return windowPositionY;
	}
	
	/**
	 * 
	 * @return the width of the terminal-window
	 */
	public int getWindowWidth(){
		return windowWidth;
	}
	
	/**
	 * 
	 * @return the height of the terminal-window
	 */
	public int getWindowHeight(){
		return windowHeight;
	}

	/**
	 * prints the whole level (StaticGameObjects, DynamicTraps, Player and ScoreBoard) to terminal
	 */
	public void printWholeLevel(){
		terminal.clearScreen();
		//Labyrinth
			for(int x = windowPositionX ; x < windowPositionX + windowWidth ; x++){
				if(x >= levelWidth) break;
				for(int y = windowPositionY ; y < windowPositionY + windowHeight; y++){
					if(y >= levelHeight) break;
					staticObjects[x][y].printInTerminal();
				}
			}
			
		//DynamicTraps and Player
			player.printInTerminal();
			for(DynamicTrap trap : dynTraps){
				trap.printInTerminal();
			}
			
		//Scoreboard
			scoreBoard.print(this);
	}
	
	/**
	 * prints the howToPlay Mesage to terminal
	 */
	public void printHowToPlay() {
		howToPlayBox.print();
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Vector<DynamicTrap> getDynamicTraps(){
		return dynTraps;
	}
	
	/**
	 * ends the level. the level gets frozen. and either winBox or lostBox is printed
	 * @param won the levels result (true if won, false if lost)
	 */
	public void endLevel(boolean won){
		isWon = won;   
		setFrozen(true);
		for(DynamicTrap trap : dynTraps){
			trap.printInTerminal();;
		}
		if(won){
			wonBox.print();
		}
		else{
			lostBox.print();
		}
	}
	
	public Terminal getTerminal(){
		return terminal;
	}
	
	/** TODO dont do this
	 * Operation codes:
	 * 0 Default (do nothing)
	 * 1 Quit Game
	 * 2 enter Menu
	 * 3 leave Menu (continue Level)
	 * 4 next Level
	 * 5 retry Level
	 * 6 to save Menu
	 * 7 to load Menu
	 * 8 How to play
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
				case '4': return 8;
				case '5': return 1;
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
	
	/**
	 * saves information about the  state of this level to a savegame slot in a .properties file
	 * this file does not contain information about the StaticGameObjects, it only contains
	 * a reference to a sourcefile, from which the level can be recreated
	 * @param slot
	 */
	public void save(int slot){
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
	 * loads level from savegame file at specified slot
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

	/**
	 * centers the terminal-window around the player along the x-axis
	 */
	public void reCenterX() {
		//TODO dont reprint if you didnt move...
		windowPositionX = player.getX() - windowWidth/2;
		if(windowPositionX < 0 ) windowPositionX = 0;
		else if (windowPositionX > levelWidth - windowWidth - 1) 
			windowPositionX = levelWidth - windowWidth - 1;
		printWholeLevel();
	}

	/**
	 * centers the terminal-window around the player along the y-axis
	 */
	public void reCenterY() {
		windowPositionY = player.getY() - windowHeight/2;
		if(windowPositionY < 0 ) windowPositionY = 0;
		else if (windowPositionY > levelHeight - windowHeight - 1) 
			windowPositionY = levelHeight - windowHeight - 1;
		printWholeLevel();
	}
	
	public void enterMenu(){
		setFrozen(true);
		menu = true;
		saveMenu = false;
		loadMenu = false;
		menuBox.print();
	}
	
	/**
	 * continues level after exiting a menu etc.
	 */
	public void continueLevel(){
		setFrozen(false);
		menu = false;
		saveMenu = false;
		loadMenu = false;
		for(TextBox box : textBoxes){
			box.unPrint(this);
		}
	}

	public void enterSaveMenu() {
		setFrozen(true);
		menu = false;
		loadMenu = false;
		saveMenu = true;
		saveMenuBox.print();
	}

	public void enterLoadMenu() {
		setFrozen(true);
		menu = false;
		saveMenu = false;
		loadMenu = true;
		loadMenuBox.print();
	}

	/**
	 * puts the level in the same state, a new level with the same terminal, 
	 * loaded from the same source file would be in
	 */
	public void reset() {
		init(terminal, sourcePath);
		printWholeLevel();
	}
	
	private void setFrozen(boolean a){
		if(a)
			printWholeLevel();
		isFrozen = a;
	}

	/**
	 * updates and reprints the scoreboard
	 * is used e.g. on live-loss
	 */
	public void updateScoreBoard() {
		scoreBoard.update(this);
	}
}
