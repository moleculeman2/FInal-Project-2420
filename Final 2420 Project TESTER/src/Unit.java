import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Abstract parent class of fighter/structure
 * has several properties and methods they can use.
 * 
 * @author Mason Wickersham
 *
 */
public abstract class Unit extends JLabel
{
	protected int health;
	protected Point position, destination;
	protected boolean selected = false;
	protected boolean hovered;
	protected JPanel panel;
	protected Color c;
	protected Color cHover;
	protected int size;
	protected boolean targeted;
	JLabel hp;

	/**
	 * intialized a unit
	 * 
	 * @param health		hp of unit
	 * @param position		coordinates of unit
	 * @param destination	coordinates of destination
	 * @param panel		main panel from main method
	 */
	public Unit(int health, Point position, Point destination, JPanel panel){
		this.health = health;
		this.position = position;
		this.destination = destination;
		this.panel = panel;
		hp = new JLabel(Integer.toString(this.health));
		hp.setBounds((int)position.getX() +15, (int)position.getY() + 55, 20, 20);
		hp.setBorder(BorderFactory.createLineBorder(Color.black,1, true));
		hp.setForeground(Color.red);
		panel.add(hp);
	}
	
	/**
	 * @return the health
	 */
	public int getHealth()
	{
		return health;
	}

	/**
	 * @param dmg damage to done health
	 */
	public void setHealth(int dmg)
	{
		this.health -= dmg;
		hp.setText(Integer.toString(this.health));
		if (this.health <= 0) {
			this.destroySelf();
		}
	}

	/**
	 * @return the destination
	 */
	public Point getDestination()
	{
		return destination;
	}

	/**
	 * @param destination the destination to set to
	 */
	public void setDestination(Point destination)
	{
		this.destination = destination;
	}

	/**
	 * @return is selected?
	 */
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * if selected, changes the border color, if not changes it back.
	 * 
	 * @param selected true/false
	 */
	public void setSelected(boolean selected)
	{
		this.selected = selected;
		if (selected) {
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.green,1, true),
					BorderFactory.createLineBorder(cHover,size, true)));
		}
		else {
			setBorder(BorderFactory.createLineBorder(c,size, true));
		}
		
		
	}

	/**
	 * @return is hovered?
	 */
	public boolean isHovered()
	{
		return hovered;
	}

	/**
	 * 	changes border based on combination of being hovered, selected, or neither
	 * 
	 * @param hovered true/false
	 */
	public void setHovered(boolean hovered)
	{
		this.hovered = hovered;
		if (selected && hovered) {
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.green,1, true),
					BorderFactory.createLineBorder(cHover,size, true)));
		}
		else if (hovered) {
			setBorder(BorderFactory.createLineBorder(cHover,size, true));
		}
		else if (!hovered && !selected) {
			setBorder(BorderFactory.createLineBorder(c,size, true));
		}
	}

	/**
	 * @return the position
	 */
	public Point getPosition()
	{
		return position;
	}
	
	/**
	 * 	sets up the look of the sprite as well as moves it to its location.
	 *  	also has mouse monitors for hover over, so it can change colors.
	 * 
	 * @param size 	size of unit
	 * @param c		color of unit
	 * @param cHover	hovered over color
	 */
	public void spriteInit(int size, Color c, Color cHover) {
		this.setBackground(panel.getBackground());
		this.setBounds((int)position.getX(), (int)position.getY(), 50, 50);
		this.setBorder(BorderFactory.createLineBorder(c,size, true));
		this.addMouseListener(new MouseAdapter() {
			
			public void mouseEntered(MouseEvent evt) {
				setHovered(true);
			}
			
			public void mouseExited(MouseEvent evt) {
				setHovered(false);
		     }
			
			
			public void mousePressed(MouseEvent e) {
				MouseListener[] m = panel.getMouseListeners();
				m[0].mousePressed(e);
			}
			
		 });
		panel.add(this);
	}

	/**
	 *  cleans up and deletes the Unit and its JLabel HP bar
	 */
	public void destroySelf() {
		hp.setVisible(false);
		this.setVisible(false);
		this.remove(hp);
		this.setEnabled(false);
	}

	/**
	 * 	sets destination to go to, if shift is held (only for fighters) it will add to queue.
	 * @param mousePos	mouse coordinates
	 * @param shiftHeld	shift key held?
	 */
	public void setDestination(Point mousePos, boolean shiftHeld)
	{
		
	}

}
