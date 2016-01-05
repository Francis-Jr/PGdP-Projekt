package objects;

public class StaticTrap extends StaticGameObject {

	protected static final int defaultDamage = 1;
	
	private int damage = defaultDamage;
	
	public StaticTrap(){
		charRepresentation = staticTrapChar;
		color = staticTrapColor;
	}
	
	@Override
	public void onContact(MovingGameObject mov) {
		mov.hurt(damage);
	}
	
	
}
