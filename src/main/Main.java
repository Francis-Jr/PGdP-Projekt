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


//TODO objects objektorientieren...
public class Main {
	
	private static final long COMPUTE_INTERVALL = (long) (7e7);
	
	private static boolean menu = false;
	
	private static long last = System.nanoTime(), delta = 0, computeCounter = 0;
	
	public static void main(String[] args){
		Terminal terminal = TerminalFacade.createSwingTerminal();
		terminal.enterPrivateMode();
		terminal.setCursorVisible(false);
		terminal.applyBackgroundColor(Color.BLACK);
		
		playLevel("level_small    .properties",terminal);
		
		terminal.exitPrivateMode();
	}
	
	/**
	 * 
	 * @param path
	 * @param terminal
	 * @return level was one or lost
	 */
	public static boolean playLevel(String path, Terminal terminal){
		Level level = new Level(terminal, path);
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
				if(menu){
					computeMenu(terminal,computeKey);
				}
				else{
					switch(computeLevel(terminal,level,computeKey)){
					case 1: return true;
					case 2: return false;
					}
				}
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {e.printStackTrace();}
				
				computeKey = null; //computeKey wurde verarbeitet
			}
		}
		System.err.println("[ALERT] playLevel got past endless loop... this should not happen");
		return false;
	}

	/**
	 * 
	 * @param terminal
	 * @param level
	 * @param computeKey
	 * @return 0 default, 1 won, 2 lost
	 */
	private static int computeLevel(Terminal terminal, Level level, Key computeKey) {
		if(computeKey != null){
			if(computeKey.getKind().equals(Kind.ArrowUp)){
				level.getPlayer().unprint();
				level.getPlayer().move(0);
			}
			else if(computeKey.getKind().equals(Kind.ArrowDown)){
				level.getPlayer().unprint();
				level.getPlayer().move(2);
			}
			else if(computeKey.getKind().equals(Kind.ArrowRight)){
				level.getPlayer().unprint();
				level.getPlayer().move(1);
			}
			else if(computeKey.getKind().equals(Kind.ArrowLeft)){
				level.getPlayer().unprint();
				level.getPlayer().move(3);
			}
			else {//Player wird nicht bewegt, aber evlt etwas anderes gemacht
				
				level.getPlayer().printInTerminal(); 
				
				T.p("operation Code: " + T.oC(level.getOperationCode(computeKey)));
				switch(level.getOperationCode(computeKey)){
				case 1: System.exit(0);
				case 2: menu = true;
						printMenu(terminal);
				case 3: 
					if(level.isWon()){
						return 1;
					}
					else{
						return 2;
					}
				}
			}
		}
		
		//move dynTraps
		for(DynamicTrap trap : level.getDynamicTraps()){
			trap.unprint();
			trap.move();
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
		
		return 0;
	}

	private static void printMenu(Terminal terminal) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 */
	private static void computeMenu(Terminal terminal, Key computeKey) {
		// TODO Auto-generated method stub
	}


	private static void computeDelta() {
		delta = System.nanoTime() - last;
		last = System.nanoTime();
	}
	
}
