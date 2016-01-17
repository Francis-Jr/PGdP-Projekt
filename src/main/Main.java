package main;

import objects.DynamicTrap;
import level.Level;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

/*
 *  Mindestanforderungen:
 *  - Level einlesen und verwenden
 *  - Menu über Esc (fortsetzen, laden, speichern, beenden)
 *  - Leben
 *  - Hindernisse (statisch, dynamisch)
 *  
 *  
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
		
		
		if(computeKey != null){ //Player wurde nicht bewegt, aber evlt etwas anderes gemacht
			
			/*
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
			 */
			T.p("operation Code: " + T.oC(level.getOperationCode(computeKey)));
			switch(level.getOperationCode(computeKey)){
			case 1: 
				System.exit(0);	
				break;
			case 2: 
				level.enterMenu();	
				break;
			case 3: 
				level.continueLevel(); 
				break;
			case 4: 
				levelsWon += 1;
				level.load(terminal, levelPath + getNextLevel() + levelSuffix);
				level.printWholeLevel();
				level.continueLevel();
				break;
			case 5: 
				level.reset();
				level.printWholeLevel();
				level.continueLevel();
				break;
			case 6: 
				level.enterSaveMenu();
				break;
			case 7:
				level.enterLoadMenu();
				break;
			case 8:
				level.printHowToPlay();
				break;
			case 101: case 102: case 103: case 104:
				level.save(level.getOperationCode(computeKey)-100);
				level.enterMenu();
				break;
			case 201: case 202: case 203: case 204:
				level.load(level.getOperationCode(computeKey)-200, terminal);
				level.continueLevel();
				break;
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
		level.getPlayer().printInTerminal();
		for(DynamicTrap trap : level.getDynamicTraps()){
			trap.printInTerminal();
		}
		
	}

	private static String getNextLevel() {
		return levels[levelsWon % levels.length];
	}

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
	
}
