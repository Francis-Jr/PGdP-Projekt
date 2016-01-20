package main;

import objects.DynamicTrap;
import level.Level;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * Project Labyrinth (PGdP 1)
 * WS15/16 TUM
 * <p>
 * The Main class that is used to launch the game
 * and statically count the number of completed levels
 * 
 * @version 19.01.2016
 * @author junfried
 */
public class Main {
	
	private static final long COMPUTE_INTERVALL = (long) (7e7);
	private static final String levelPath = "levels/",
								levelSuffix = ".properties";
	
	private static final String[] levels = {"level_small","level2","level3"};
	
	private static long last = System.nanoTime(), delta = 0, computeCounter = 0;
	
	private static Level level;
	private static int levelsWon = 0;
	
	public static void main(String[] args){
		Terminal terminal = TerminalFacade.createSwingTerminal();
		terminal.enterPrivateMode();
		
		terminal.setCursorVisible(false);
		terminal.applyBackgroundColor(Color.BLACK);
		terminal.clearScreen();
		
		playLevel(levelPath + levels[0] + levelSuffix,terminal);

		terminal.exitPrivateMode();
	}
	
	/**
	 * 
	 * @param path
	 * @param terminal
	 */
	public static void playLevel(String path, Terminal terminal){
		level = new Level(terminal, path);
		Key key;
		Key computeKey = null;
		
		while(terminal.getTerminalSize().getColumns() > 0){ //endlosschleife: return statements nicht ersatzlos entfernen
			key = terminal.readInput();
			if(key != null){
				computeKey = key;
			}
			
			computeDelta();
			computeCounter = computeCounter + delta;
			
			if(computeCounter > COMPUTE_INTERVALL){
				computeCounter = 0;
				computeLevel(terminal,level,computeKey);
				
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {e.printStackTrace();}
				
				computeKey = null;
			}
		}
		System.err.println("[ALERT] playLevel got past endless loop... this should not happen");
	}

	/**
	 * 
	 * @param terminal
	 * @param level
	 * @param computeKey
	 */
	private static void computeLevel(Terminal terminal, Level level, Key computeKey) {
		
		if(computeKey != null){
			if(computeKey.getKind().equals(Kind.ArrowUp)){
				level.getPlayer().unprint();
				level.getPlayer().move(0);
				computeKey = null;
			}
			else if(computeKey.getKind().equals(Kind.ArrowDown)){
				level.getPlayer().unprint();
				level.getPlayer().move(2);
				computeKey = null;
			}
			else if(computeKey.getKind().equals(Kind.ArrowRight)){
				level.getPlayer().unprint();
				level.getPlayer().move(1);
				computeKey = null;
			}
			else if(computeKey.getKind().equals(Kind.ArrowLeft)){
				level.getPlayer().unprint();
				level.getPlayer().move(3);
				computeKey = null;
			}
		}
		
		//move dynTraps
		for(DynamicTrap trap : level.getDynamicTraps()){
			if(!level.isFrozen()) {
				trap.unprint();
				trap.move();
			}
		}
		
		//collisions
		if(level.getPlayer().isOnDynamicTrap(level.getDynamicTraps())){
			level.getPlayer().hurt(1);
		}
		
		//print player and dyntraps
		for(DynamicTrap trap : level.getDynamicTraps()){
			trap.printInTerminal();
		}
		level.getPlayer().printInTerminal();
		
		if(computeKey!=null){//Player wurde nicht bewegt, aber evlt etwas anderes gemacht
			level.processKey(computeKey);
		}
	}


	/**
	 * computes the internal delta value.
	 * delta is the duration in nanosecond since the last computation cycle
	 */
	private static void computeDelta() {
		delta = System.nanoTime() - last;
		last = System.nanoTime();
	}
	
	public static int getLevelsWon(){
		return levelsWon;
	}
	
	public static void setLevelsWon(int a){
		levelsWon = a;
	}

	public static String getNextLevelPath() {
		return levelPath + levels[levelsWon % levels.length] + levelSuffix;
	}
	
}
