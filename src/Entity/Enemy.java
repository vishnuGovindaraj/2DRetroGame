package Entity;

import TileMap.TileMap;

public class Enemy extends MapObject {

	// enemy variables
	protected int health;
	protected int maxHealth;
	protected boolean dead;
	protected int damage;

	protected boolean flinching;
	protected long flinchTimer;
	protected boolean isIntersecting = false;
	protected boolean isPickUp;

	public Enemy(TileMap tm) {
		super(tm);
		// TODO Auto-generated constructor stub
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean b) {
		dead = b;
	}

	public int getDamage() {
		return damage;
	}

	public void hit(double d) {
		if (dead || flinching)
			return;
		health -= d;
		if (health < 0)
			health = 0;
		if (health == 0)
			dead = true;
		flinching = true;
		flinchTimer = System.nanoTime();
	}

	public void update() {

	}

	public boolean getIntersecting() {
		return isIntersecting;

	}

	public void setIntersecting(boolean b) {
		isIntersecting = b;

	}

	public boolean isPickUp() {
		return isPickUp;
	}

	public void setPickUp(boolean isPickUp) {
		this.isPickUp = isPickUp;
	}

}
