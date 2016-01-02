package level;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import main.T;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class Level {
	
	private Terminal terminal;
	
	private int[][] objects;
	private Vector<DynamicTrap> dynTraps = new Vector<DynamicTrap>();
	private int levelWidth, levelHeight;
	private int windowWidth, windowHeight, windowPositionX, windowPositionY;
	private int playerX, playerY;
	private boolean hasKey = false;
	private int livesLeft = 5;
	
	public int getObjectAt(int x , int y){
		return objects[x][y];
	}
	
	public int getWidth(){
		return levelWidth;
	}
	
	public int getHeight(){
		return levelHeight;
	}
	
	private boolean isWalkable(int positionX, int positionY) {
		if(positionX < 0 || positionX >= levelWidth || positionY < 0 || positionY >= levelHeight) return false;
		switch(objects[positionX][positionY]){
		case 1:
		case 6:
			return true;
		default:
			return false;
		}
	}

	private boolean isWalkableForPlayer(int positionX, int positionY) {
		if(positionX < 0 || positionX >= levelWidth || positionY < 0 || positionY >= levelHeight) return false;
		switch(objects[positionX][positionY]){
		case 0:
			return false;
		case 2:
			return hasKey;
		default:
			return true;
		}
	}

	private void setObject(int obj, int positionX, int positionY) {
		objects[positionX][positionY] = obj;
		printInTerminal(positionX , positionY);
	}

	/**
	 * 
	 * @param positionX position relativ zum Level
	 * @param positionY position relativ zum Level
	 */
	private void printInTerminal(int positionX, int positionY){
		if(positionX >= levelWidth || positionY >= levelHeight) return;
		terminal.applyForegroundColor(getColor(objects[positionX][positionY]));
		terminal.moveCursor(positionX - windowPositionX, positionY - windowPositionY);
		terminal.putCharacter(getChar(objects[positionX][positionY]));
	}
	
	private char getChar(int obj){
		switch(obj){
		case 0: return 'X'; //wand
		case 2: return 'E'; //Ausgang
		case 3: return '#'; //static trap
		case 4: return '+'; //dynamic trap
		case 5: return '$'; //Schluessel
		case 7: return '�'; //Player
		case 1:				//eingang
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
		case 1:						//eingang
		case 6: 					//leer
		default: return Color.WHITE;
		}
	}
	
	public void moveDynamicTraps(){
		for(DynamicTrap trap : dynTraps){
			setObject(6, trap.getPositionX(), trap.getPositionY());
			trap.move(isWalkable(trap.getPositionX(),trap.getPositionY()-1), 
					isWalkable(trap.getPositionX()+1,trap.getPositionY()), 
					isWalkable(trap.getPositionX(),trap.getPositionY()+1), 
					isWalkable(trap.getPositionX()-1,trap.getPositionY()));
			setObject(4,trap.getPositionX(),trap.getPositionY());
		}
	}

	public void movePlayerUp(){ //TODO player nur als overlay
		if(playerY > 0 && isWalkableForPlayer(playerX , playerY-1)){
			setObject(6, playerX , playerY);
			playerY -= 1;
			interact();
			setObject(7, playerX , playerY);
		}
		//TODO check for windowBorders
	}
	
	public void movePlayerDown(){
		if(playerY < levelHeight-1 && isWalkableForPlayer(playerX , playerY+1)){
			setObject(6, playerX , playerY);
			playerY += 1;
			interact();
			setObject(7, playerX , playerY);
		}
	}
	
	public void movePlayerLeft(){
		if(playerX > 0 && isWalkableForPlayer(playerX - 1, playerY)){
			setObject(6, playerX , playerY);
			playerX -= 1;
			interact();
			setObject(7, playerX , playerY);
		}
	}
	
	public void movePlayerRight(){
		if(playerX < levelWidth -1 && isWalkableForPlayer(playerX +1, playerY)){
			setObject(6, playerX , playerY);
			playerX += 1;
			interact();
			setObject(7, playerX , playerY);
		}
	}
	
	private void interact() {
		switch(objects[playerX][playerY]){
		case 2: /*win();*/ break;
		case 3: damage(2); break;
		case 4: damage(1); break;
		case 5: hasKey = true; break; //TODO Anzeige ändern
		}
	}

	private void damage(int i) {
		livesLeft -= i;
		if(livesLeft <= 0);
			//TODO lose()
	}

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
						objects[x][y] = 7;
					}
					
					//(3)
					else if(objects[x][y] == 4){
						dynTraps.add(new DynamicTrap(x,y));
					}
				}
			}
			
		//Fenstergroesse setzen und Fenster um Player zentrieren
			windowWidth = terminal.getTerminalSize().getColumns();
			windowHeight = terminal.getTerminalSize().getRows();
			windowPositionX = playerX - windowWidth/2;
			windowPositionY = playerY - windowHeight/2;
			if(windowPositionX < 0 ) windowPositionX = 0;
			else if (windowPositionX > levelWidth - windowWidth - 1) windowPositionX = levelWidth - windowWidth - 1;
			if(windowPositionY < 0 ) windowPositionY = 0;
			else if (windowPositionY > levelHeight - windowHeight - 1) windowPositionY = levelHeight - windowHeight - 1;
			
		//Alle zeichen ins Terminal schreiben
			for(int x = windowPositionX ; x < windowPositionX + windowWidth ; x++){
				for(int y = windowPositionY ; y < windowPositionY + windowHeight ; y++){
					printInTerminal(x,y);
				}
			}
	}
}
