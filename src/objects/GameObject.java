package objects;

import com.googlecode.lanterna.terminal.Terminal.Color;

public abstract class GameObject {
	protected int x,y;
	
	public abstract char getDisplayChar();
	public abstract Color getBgColor();
	public abstract Color getFgColor();

	public abstract int getX();
	public abstract int getY();
	
	public abstract int getInteractionCode(); 
	/*interactions:	
	*	//TODO implement em all	
	*
	*	0 nothing
	*	1 not walk-through (e.g. wall)
	*	2 entry (nothing happens, just alerts that this is the entry
	*	3 exit (you win! or sth)
	*	4 Damage: 1 Life
	*	5 Damage: 2 Lives
	*	6 Damage: 3 Lives
	*	7 Key! (you get a key) [more colours?]
	*	8
	*	9
	*	10 Lever
	*	11 Door
	*/
}
