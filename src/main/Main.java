package main;

import level.Level;

import com.googlecode.lanterna.TerminalFacade;
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
	
	private static boolean menu = false;
	
	public static void main(String[] args){
		Terminal terminal = TerminalFacade.createSwingTerminal();
		terminal.enterPrivateMode();
		terminal.setCursorVisible(false);
		terminal.applyBackgroundColor(Color.BLACK);
		
		
		terminal.exitPrivateMode();
	}
	
	
	public static void playLevel(String path, Terminal terminal){
		Level level = new Level(path);
		
		while(terminal.getTerminalSize().getColumns() > 0){ //break; nicht ohne ersatz entfernen!
			computeDelta();
			if(menu){
				if(computeMenu()) break;
				paintMenu();
			}
			else{
				computeLevel();
				paintLevel();
			}
		}
		
	}

	
	private static void paintLevel() {
		// TODO Auto-generated method stub
	}


	private static void computeLevel() {
		// TODO Auto-generated method stub
		
	}


	private static void paintMenu() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @return true if player wants to quit
	 */
	private static boolean computeMenu() {
		// TODO Auto-generated method stub
		return true;
	}


	private static void computeDelta() {
		// TODO Auto-generated method stub
		
	}
	
}
