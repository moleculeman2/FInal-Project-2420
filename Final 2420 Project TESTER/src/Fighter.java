import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JPanel;

/**
 * child of Unit, a fighter can move around the screen and attack.
 * can queue up move commands, and is built from structure.
 * 
 * @author Mason Wickersham
 *
 */
public class Fighter extends Unit
{
	int damage;
	int speed;
	boolean moving;
	double angle;
	boolean attacking;
	Queue<Point> moveQueue = new  LinkedList<>();
	Unit target;

	/**
	 * 	constructs fighter
	 * 
	 * @param health     	max health of fighter
	 * @param position   	current coordinatates
	 * @param destination	destination coordinates
	 * @param panel		JPanel from main method
	 */
	public Fighter(int health, Point position, Point destination, JPanel panel)
	{
		super(health, position, destination, panel);
		this.damage = 6;
		this.c = new Color(152, 77, 209);
		this.cHover = new Color(175, 107, 227);
		this.size = 50;
		spriteInit(size, c, cHover);
		this.speed = 9;
		this.destination = destination;
		if (this.destination != null) {
			setDestination(this.destination, false);
		}
		
	}
	
	/**
	 * 	Uses moving boolean to tell whether it should move. Uses position of self and destination/target
	 * 		to find the angle to move on. Updates HP text label to follow it. 
	 * 	If fighter gets within 27 pixels, either stops, or moves to next command in moveQueue. 
	 *   Also calls attack if it has a target.
	 */
	public void movement(){
		if (moving) {
			if (attacking && Math.abs(position.x - target.getX()) > 27 && Math.abs(position.y - target.getY()) > 27) {
				destination = target.getPosition();
				angle();
			}
			double x = (Math.cos(angle));
			double y = (Math.sin(angle));
			x = (position.getX() + (speed * x));
			y = (position.getY() + (speed * y));
			x = Math.round(x);
			y = Math.round(y);
			position.setLocation(x, y);
			this.setLocation(position);
			hp.setText(Integer.toString(this.health));
			hp.setBounds((int)position.getX() +15, (int)position.getY() + 55, 20, 20);
		}
		//stop if near destination, or go to next queued point, attacks if it has target
		if (destination != null && Math.abs(position.x - destination.x) < 27 && Math.abs(position.y - destination.y) < 27) {
			moveQueue.poll();
			if (moveQueue.size() == 0 && target == null) {
				moving = false;
			}
			else {
				destination = moveQueue.peek();
				angle();
			}
			
			if (target!= null) {
				attack(target);
			}
		}
	}
	
	/**
	 * 	calculates angle for movement
	 */
	public void angle() {
		if (destination != null) {
			double x = destination.getX() - position.getX();
			double y = destination.getY() - position.getY();
			this.angle = Math.atan2(y, x);
		}
		
	}
	
	/**
	 * 	if target would die to damage, resets attack values and modifies target's HP.
	 * 	otherwise, just modifies target's HP.
	 * 
	 * @param target targeted unit to damage.
	 */
	public void attack (Unit target) {
		if (target.getHealth() <= damage) {
			attacking = false;
			moving = false;
			destination = null;
			target.setHealth(damage);
			target = null;
		}
		else {
			target.setHealth(damage);
		}
		
	}
	
	
	/**
	 *	starts the movement loop, checks if shift is held. If it is, destination is added to queue.
	 *	if shift isn't held, it resets the queue and moves to destination
	 */
	public void setDestination(Point destination, boolean shiftHeld)
	{
		this.moving = true;
		if (!shiftHeld) {
			moveQueue.clear();
			moveQueue.add(destination);
			this.destination = destination;
			angle();
		}
		else if (shiftHeld){
			moveQueue.add(destination);
			if (moveQueue.size() == 1) {
				this.destination = moveQueue.peek();
				angle();
			}
		}
	}
	
	/**
	 * @return is moving?
	 */
	public boolean isMoving()
	{
		return moving;
	}


	/**
	 * @param set moving true/false
	 */
	public void setMoving(boolean moving)
	{
		this.moving = moving;
	}

	/**
	 * @return is attacking?
	 */
	public boolean isAttacking()
	{
		return attacking;
	}

	/**
	 * @param  set attacking true/false
	 */
	public void setAttacking(boolean attacking)
	{
		this.attacking = attacking;
	}

	/**
	 * @return the target
	 */
	public Unit getTarget()
	{
		return target;
	}

	/**
	 * @param target the unit to set as target
	 */
	public void setTarget(Unit target)
	{
		this.target = target;
	}
}
