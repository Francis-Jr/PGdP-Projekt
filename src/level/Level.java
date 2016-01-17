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
	
	private TextBox menuBox, howToPlayBox, saveMenuBox, loadMenuBox, wonBox, lostBox;
	private ScoreBoard scoreBoard;
	private LevelOverlay[] textBoxes = {menuBox, howToPlayBox, saveMenuBox, loadMenuBox, wonBox, lostBox};
	
	private final static int scoreBoardHeight = 5;
	
	private final static Color 	menuTextColor = Color.WHITE,
								menuBgColor = Color.BLUE,
								wonColor = Color.GREEN,
								lostColor = Color.RED;
	
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
			
		//TextBoxes initialisieren,
			menuBox = new TextBox(menuMessage, menuTextColor, menuBgColor, terminal);
			howToPlayBox = new TextBox(howToPlayText, menuTextColor, menuBgColor, 0 , 0 , terminal);
			saveMenuBox = new TextBox(saveMenuMessage , menuTextColor, menuBgColor, terminal);
			loadMenuBox = new TextBox(loadMenuMessage , menuTextColor, menuBgColor, terminal);
			wonBox = new TextBox(wonMessage , menuTextColor , wonColor , terminal);
			lostBox =  new TextBox(lostMessage , menuTextColor , lostColor , terminal);
			scoreBoard = new ScoreBoard(terminal);
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
			scoreBoard.print(this);
	}
	
	public void unPrintActiveTextBoxes(){
		for(LevelOverlay ol : textBoxes){
			ol.unPrint(this);
		}
	}
	
	public void printHowToPlay() {
		howToPlayBox.print();
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Vector<DynamicTrap> getDynamicTraps(){
		return dynTraps;
	}
	
	public void endLevel(boolean won){
		T.p("ending level " + won);
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
		setFrozen(true);
		menu = true;
		saveMenu = false;
		loadMenu = false;
		menuBox.print();
	}
	
	public void continueLevel(){
		setFrozen(false);
		menu = false;
		saveMenu = false;
		loadMenu = false;
		unPrintActiveTextBoxes();
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
	 * resets the level
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

	public void updateScoreBoard() {
		scoreBoard.update(this);
	}
}
