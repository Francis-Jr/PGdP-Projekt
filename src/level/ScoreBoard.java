package level;

import main.Main;
import objects.Player;
import objects.StaticGameObject;

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

public class ScoreBoard{

	private Terminal terminal;
	
	//TextBoxes that are displayed in the Scoreboard
	private TextBox heartBox, keyBox, levelScoreBox;
	
	//Colors and Looks
	private static final Color 	scoreBoardBgColor = Color.BLACK,
								scoreBoardFrameColor = Color.YELLOW,
								heartColor = Color.RED,
								levelScoreColor = Color.WHITE;
	private final static char  	heartChar = '\u2665',
								frameVertical = '\u2502',
								frameHorizontal = '\u2500',
								frameUpperRight = '\u2510',
								frameUpperLeft = '\u250C',
								frameLowerRight = '\u2518',
								frameLowerLeft = '\u2514';
	
	//Positioning:
	private int lastTerminalRow, width;
	private static final int 	heartStartColumn = 3,
								keyStartColumn = 18,
								levelScoreStartColumn = 30,
								scoreBoardHeight = 5;
	
	/**
	 * Initializes a Scoreboard.
	 * @param terminal the terminal to print in
	 */
	public ScoreBoard(Terminal terminal){
		this.terminal = terminal;
		
		lastTerminalRow = terminal.getTerminalSize().getRows() - 1;
		width = terminal.getTerminalSize().getColumns();
		String[] empty = {" "};
		heartBox = new TextBox(empty, heartColor, scoreBoardBgColor, heartStartColumn , 
				lastTerminalRow - scoreBoardHeight + 2, terminal);
		keyBox = new TextBox(empty, StaticGameObject.getKeyColor(), scoreBoardBgColor, keyStartColumn , 
				lastTerminalRow - scoreBoardHeight + 2, terminal);
		levelScoreBox = new TextBox(empty, levelScoreColor, scoreBoardBgColor, levelScoreStartColumn , 
				lastTerminalRow - scoreBoardHeight + 2, terminal);
	}
	
	/**
	 * updates the values of all textboxes to the state of the game
	 * this includes number of lives, whether the player has a key and the levelScore
	 * this does not print anything
	 * @param level
	 */
	private void updateTexts(Level level){
		heartBox.setText(getHeartText(level.getPlayer()));
		keyBox.setText(getKeyText(level.getPlayer()));
		levelScoreBox.setText(getLevelScoreText());
	}
	
	/**
	 * updates the scoreboard, then prints it to the terminal
	 * @param level
	 */
	public void print(Level level){
		width = terminal.getTerminalSize().getColumns();
		updateTexts(level);
		
		//Rahmen
			terminal.applyBackgroundColor(scoreBoardBgColor);
			terminal.applyForegroundColor(scoreBoardFrameColor);
			terminal.moveCursor(0, lastTerminalRow - scoreBoardHeight + 1);
			terminal.putCharacter(frameUpperLeft);
			putMultipleChars(frameHorizontal,width - 2);
			terminal.putCharacter(frameUpperRight);
			
			for(int n = 2 ; n < scoreBoardHeight ; n++){
				terminal.moveCursor(0 , lastTerminalRow - scoreBoardHeight + n);
				terminal.putCharacter(frameVertical);
				terminal.moveCursor(width - 1, lastTerminalRow - scoreBoardHeight + n);
				terminal.putCharacter(frameVertical);
			}
			terminal.moveCursor(0, lastTerminalRow);
			terminal.putCharacter(frameLowerLeft);
			putMultipleChars(frameHorizontal,width - 2);
			terminal.putCharacter(frameLowerRight);
		
		heartBox.print();
		keyBox.print();
		levelScoreBox.print();
	}
	
	/**
	 * updates the scoreboard and reprints the updated parts
	 * @param level
	 */
	public void update(Level level){
		updateTexts(level);
		heartBox.reprintText();
		keyBox.reprintText();
		levelScoreBox.reprintText();
	}
	
	public int getHeight(){
		return scoreBoardHeight;
	}
	
	/**
	 * returns a String corresponding to the number of lives the Player in the current Level has
	 * @param player
	 * @return
	 */
	private String[] getHeartText(Player player){
		String returnString =  " ";
		for(int n = 0 ; n < player.getLives() ; n++){
			returnString = returnString  + heartChar + " ";
		}
		for(int n = 0 ; n < Player.getMaxLives() - player.getLives() ; n++){
			returnString = returnString + "  ";
		}
		String[] returnArray = {returnString};
		return returnArray;
	}

	/**
	 * returns a String corresponding to whether the Player in the current Level has a key
	 * @param player
	 * @return
	 */
	private String[] getKeyText(Player player){
		String returnString = " " + (player.hasKey() ? StaticGameObject.getKeyChar() : " ") + " ";
		String[] returnArray = {returnString};
		return returnArray;
	}
	
	/**
	 * returns a String corresponding to the levelScore
	 * @return
	 */
	private String[] getLevelScoreText(){
		int score = Main.getLevelsWon();
		String returnString = " " + (score >= 10 ? "" : "0") + score + " ";
		String[] returnArray = {returnString};
		return returnArray;
	}
	
	/**
	 * prints a specified char into the terminal a specified amount of times
	 * @param c
	 * @param amount
	 */
	private void putMultipleChars(char c , int amount){
		for(int n = 0; n < amount ; n++){
			terminal.putCharacter(c);
		}
	}
}
