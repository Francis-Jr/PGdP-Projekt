package main;

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
	
	private static boolean menu = false;
	
	private static long last = System.nanoTime(), delta = 0, computeCounter = 0;
	
	public static void main(String[] args){
		Terminal terminal = TerminalFacade.createSwingTerminal();
		terminal.enterPrivateMode();
		terminal.setCursorVisible(false);
		terminal.applyBackgroundColor(Color.BLACK);
		
		playLevel("level_small.properties",terminal);
		
		terminal.exitPrivateMode();
	}
	
	
	public static void playLevel(String path, Terminal terminal){
		Level level = new Level(terminal, path);
		Key key;
		Key computeKey = null;
		
		while(terminal.getTerminalSize().getColumns() > 0){ //break; nicht ohne ersatz entfernen!
			key = terminal.readInput();
			if(key != null) computeKey = key;
			
			computeDelta();
			computeCounter = computeCounter + delta;
			
			if(computeCounter > COMPUTE_INTERVALL){
				computeCounter = 0;
				if(menu){
					if(computeMenu(terminal)) break;
				}
				else{
					computeLevel(terminal,level,computeKey);
					computeKey = null;
				}
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}

	private static void computeLevel(Terminal terminal, Level level, Key computeKey) {
		level.unprintMovingObjects();
		if (computeKey == null) {
			level.movePlayer(4); //this is not actually moving the player, but is necessary for reprinting
		}
		else if(computeKey.getKind().equals(Kind.ArrowUp)) level.movePlayer(0);
		else if(computeKey.getKind().equals(Kind.ArrowDown)) level.movePlayer(2);
		else if(computeKey.getKind().equals(Kind.ArrowRight)) level.movePlayer(1);
		else if(computeKey.getKind().equals(Kind.ArrowLeft)) level.movePlayer(3);
		else {
			level.movePlayer(4); //this is not actually moving the player, but is necessary for reprinting
			switch(level.getOperationCode(computeKey)){
			case 1: System.exit(0);
			case 2: //TODO enter menu
			}
		}
		
		level.moveDynamicTraps();
	}

	/**
	 * 
	 * @return true if player wants to quit
	 */
	private static boolean computeMenu(Terminal terminal) {
		// TODO Auto-generated method stub
		return true;
	}


	private static void computeDelta() {
		delta = System.nanoTime() - last;
		last = System.nanoTime();
	}
	
}
