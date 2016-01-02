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
	
	private static final long COMPUTE_INTERVALL = (long) (5e8);
	
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
		
		while(terminal.getTerminalSize().getColumns() > 0){ //break; nicht ohne ersatz entfernen!
			computeDelta();
			computeCounter = computeCounter + delta;
			if(computeCounter > COMPUTE_INTERVALL){
				computeCounter = 0;
				if(menu){
					if(computeMenu(terminal)) break;
				}
				else{
					computeLevel(terminal,level);
				}
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}

	private static void computeLevel(Terminal terminal, Level level) {
		level.moveDynamicTraps();
		Key key = terminal.readInput();
		if (key == null) return;
		if(key.getKind().equals(Kind.ArrowUp)) level.movePlayerUp();
		else if(key.getKind().equals(Kind.ArrowDown)) level.movePlayerDown();
		else if(key.getKind().equals(Kind.ArrowRight)) level.movePlayerRight();
		else if(key.getKind().equals(Kind.ArrowLeft)) level.movePlayerLeft();
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
