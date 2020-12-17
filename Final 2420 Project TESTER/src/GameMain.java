import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 	<pre>
 * 	central class of the program, uses a Unit list to keep track of all units
 *	Also monitors inputs, then searches list for relevant units. Lets you build
 *	fighters and structures. Fighters can move and attack, structures can build fighters.
 *
 *	CONTROLS:
 *	- Select a unit by LEFT-clicking it. To de-select, LEFT-click in an empty space.
 *	- With a selected unit, RIGHT-click to set destination/rally point. Holding shift while 
 *		doing this will queue up move commands for fighters. Right clicking without holding 
 *		shift will reset and overwrite the fighter move queue.
 *	- With a selected structure, press F to create a fighter. It will move to the structure's
 *		 rally point, if you set it beforehand. Otherwise it goes to the corner.
 *	- With a fighter selected, hold 'A' and LEFT-click another unit to make it attack.
 *	- Hover over any open space and press 'B' to spawn a structure on the grid.
 *	- Press ESCAPE to close the program.
 *
 * 	</pre>
 * @author Mason Wickersham
 *	
 */
public class GameMain
{
	// Ended up declaring these out here to bypass conflicts with scope in listeners
	// would have been better to make a separate class for this, I think
	static boolean shiftHeld = false;
	static boolean aHeld = false;
	static LinkedList<Unit> unitList = new LinkedList<>();
	
	public static void main(String[] args)
	{
		//intializes some starter stuff
		JFrame frame = new JFrame();
		Timer timer = new Timer(true);
		JPanel panel = new JPanel();
		int frameWidth = 800;
		int frameHeight = 800;
		ArrayList<Point> temp = new ArrayList<>();
		temp = populate(temp, frameWidth, frameHeight);
		final ArrayList<Point> coordinatesGrid = temp;
		
		//sets properties of frame/panel
		panel.setLayout(null);
		frame.add(panel);
		panel.setBackground(Color.DARK_GRAY);
		frame.setSize(frameWidth+ 16, frameHeight+ 40);  //add 16 and 40 because of strange mis-alignment
		frame.setVisible(true);
		frame.setResizable(false);
		
		//listeners handle unit selection logic
		panelMouseListeners(panel, unitList);
		
		//checks for shift, B, F, A, and esc
		//holding shift lets you queue up move commands
		//pressing B builds a structure at the cursor, aligned to grid
		//with a structure selected, F builds a fighter
		//holding A and right clicking with fighter selected makes it attack target at cursor
		//escape closes the program
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e)
			{
				shiftHeld = e.isShiftDown();
				aHeld = (e.getKeyCode() == KeyEvent.VK_A);
				attackCursor(frame);
				
				if(e.getKeyCode() == KeyEvent.VK_B && (panel.getMousePosition() != null)){
					buildStructure(panel,unitList,coordinatesGrid);
				}
				if(e.getKeyCode() == KeyEvent.VK_F && (panel.getMousePosition() != null)){
					buildFighter(panel,unitList);
				}
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					frame.dispose();
				}	
			}
			@Override
			public void keyReleased(KeyEvent e)
			{
				shiftHeld = e.isShiftDown();
				aHeld = false;
				attackCursor(frame);
			}
		});
		
		//terminates program when window is closed
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
			    System.exit(0);
			  }
		});
		
		//initialized a few units to start with, adds them to list
		Structure starter = new Structure(400, new Point(100,100), null, panel);
		Fighter fighter = new Fighter(50, new Point(400,400), null, panel);
		Fighter fighter2 = new Fighter(50, new Point(500,400), null, panel);
		Fighter fighter3 = new Fighter(50, new Point(500,500), null, panel);
		unitList.add(starter);
		unitList.add(fighter);
		unitList.add(fighter2);
		unitList.add(fighter3);
		
		//This timer is the game loop, updates movement and attacks for units
		TimerTask timerTask = new MyTimerTask2(unitList);
		timer.scheduleAtFixedRate(timerTask, 35, 35);
		
	}

	
	/**
	 * Checks if you have a selected structure, then calls its createFighter method.
	 * 
	 * @param panel only JPanel game runs on
	 * @param unitList Unit list with all units
	 */
	public static void buildFighter(JPanel panel, LinkedList<Unit> unitList)
	{
		for (int i = 0; i < unitList.size(); i++) {
			Unit temp = unitList.get(i);
			if (temp instanceof Structure == true && temp.isSelected()) {
				((Structure) temp).createFighter();
			}
		}
	}

	/**
	 * Sets up mouse listeners to check for left and right click
	 * left click can select a unit, designate a target, or deselect if you have no unit under mouse
	 * right click only works with a unit selected. If shift is held, a move command is added to queue
	 * 	otherwise, it clears the queue and sets the destination to mouse click.
	 * 
	 * @param panel only JPanel game runs on
	 * @param unitList Unit list with all units
	 */
	private static void panelMouseListeners(JPanel panel, LinkedList<Unit> unitList)
	{
		panel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				//unselects all units if you click empty space
				if(e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
					Unit target = null;
					for (Unit unit : unitList) {
						if (!unit.isHovered() && !aHeld) {
							unit.setSelected(false);
						}
						if (unit.isHovered()) {
							if (!aHeld) unit.setSelected(true);
							target = unit;
						}
					}
					
					for (Unit unit : unitList) {
						if (target != null && aHeld && unit instanceof Fighter && unit.isSelected()) {
							((Fighter) unit).setAttacking(true);
							((Fighter) unit).setTarget(target);
							unit.setDestination(target.position, false);
						}
					}	
				}

				//if any unit is selected, then set the destination or rally point on right click
				if(e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
					unitList.forEach(unit ->{
						if (unit.isSelected()) {
							if (unit instanceof Fighter) {
								unit.setDestination(panel.getMousePosition(), shiftHeld);
								((Fighter) unit).setTarget(null);
								((Fighter) unit).setAttacking(false);
							}
							else unit.setDestination(panel.getMousePosition());
						}
					});
				}
			}
		});
	}
	

	/**
	 * creates structure at cursor, but fits it to closest grid point
	 * 
	 * 
	 * @param j only JPanel game runs on
	 * @param u Unit list with all units
	 * @param c array of points for building on grid
	 */
	private static void buildStructure (JPanel j, LinkedList<Unit> u, ArrayList<Point> c) {
		
		Point buildPoint = j.getMousePosition();
		Point closest = new Point();
		boolean empty = true;
		
		for (Point p : c){

			if (Math.abs(buildPoint.getX() - p.getX()) < 50) {
				if (Math.abs(buildPoint.getY() - p.getY()) < 50) {
					closest = p;
				}
			}
		}
		//checks to see if space is occupied
		for (Unit unit : u) {
			if (unit.getPosition() == closest) {
				empty = false;
			}
		}
		//creates structure if empty space
		if (empty) {
			Structure temp = new Structure(400, closest, null, j);
			u.add(temp);
			temp.repaint();
		}
	}

	//
	/**
	 * populates the grid with coordinates based on window size
	 * 
	 * @param c array of points for building on grid
	 * @param w width of window
	 * @param h height of window
	 * @return
	 */
	private static ArrayList<Point> populate(ArrayList<Point> c, int w, int h)
	{
		w -= 50; //offset
		h -= 50; //offset
		int temp = h;
		
		while (w >= 0) {
			h = temp;
			while (h >= 0) {
				c.add(new Point(w,h));
				h -= 50;
			}
			w -= 50;
		}
		return c;
	}

	/**
	 * changes cursor to crosshair when A is held down
	 * 
	 * @param frame main game JFrame
	 */
	private static void attackCursor(JFrame frame)
	{
		if (aHeld) {
			Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
			frame.setCursor(cursor);
		}
		else {
			Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
			frame.setCursor(cursor);
		}
	}
}

/**
 * Implementation of TimerTask, used to run the game loop ~30 times per second
 * 
 * @author Mason Wickersham
 *
 */
class MyTimerTask2 extends TimerTask{
	
	LinkedList<Unit> u = new LinkedList<>();
	
	/**
	 * Constructor of MyTimerTask2
	 * 
	 * @param unitList	unit list passed from main method
	 */
	public MyTimerTask2(LinkedList<Unit> unitList) {
		this.u = unitList;
	}
	
	/**
	 * updates fighter movement
	 */
	@Override
	public void run()
	{
		int size = u.size();
		Unit[] temp = new Unit[size];
		this.u.toArray(temp);
		for (Unit unit : temp) {
			if (unit instanceof Fighter) {
				if (((Fighter) unit).isMoving()) {
					((Fighter) unit).movement();
				}
			}
		}
	}
	
}
