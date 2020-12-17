import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import javax.swing.JPanel;


/**
 * Structures are child of Unit class. They can't move, and build on the grid.
 * They only produce fighters and can be destroyed.
 * If a destination is set, fighters will move to it when produced.
 * 
 * @author Mason Wickersham
 *
 */
public class Structure extends Unit
{
	/**
	 * initialize new structure
	 * 
	 * @param health		hp of structure
	 * @param position		coordinates of structure
	 * @param destination	coordinates of rally point
	 * @param panel		main panel from main method
	 */
	public Structure(int health, Point position, Point destination, JPanel panel)
	{
		super(health, position, destination, panel);
		this.c = new Color(79, 126, 201);
		this.cHover = new Color(123, 163, 227);
		this.size = 25;
		spriteInit(size, c, cHover);
		hp.setBounds((int)position.getX() +15, (int)position.getY() + 55, 30, 20);
	}
	
	/**
	 * 	creates a fighter at structures position, sends it to rally point.
	 *   if no rally point, just sends it to corner of screen.
	 */
	public void createFighter() {
		Point tempPos;
		Point tempDest;
		tempPos = new Point(position.x, position.y);
		if (destination == null) {
			tempDest = new Point(0, 0);
		}
		else {
			tempDest = new Point(destination.x, destination.y);
		}
		
		GameMain.unitList.add(new Fighter(50, tempPos, tempDest, panel));
	}
}
