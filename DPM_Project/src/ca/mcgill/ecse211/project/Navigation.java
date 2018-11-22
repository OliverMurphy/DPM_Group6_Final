package ca.mcgill.ecse211.project;

import java.util.concurrent.TimeUnit;

import ca.mcgill.ecse211.odometer.Odometer;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class implements navigation for the robot
 * @author Lucy Coyle 
 * @author Oliver Murphy
 */

public class Navigation {
	
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private final double TRACK;
	private final double WHEEL_RAD;
	public final int FORWARD_SPEED = 200;
	public final int CORRECTION_SPEED = 100;
	public final int ROTATE_SPEED = 100;
	public final int CIRCLE_SPEED = 90;
	public static int width = 8;
	public static int length = 8;
	public final double TILE_SIZE  = 30.48;
	public final int acceleration = 100;
	public final int defAcceleration = 6000;
	
	//correction variables
	private LightPoller lpLeft;
	private LightPoller2 lpRight;
	private double position[]  = new double[3];
	public static final double correction = 5.0;
	private double tileSize = 30.48;
	
  /**
   * This is the default constructor of this class. It initiates all motors and variables once
   * 
   * @param leftMotor
   * @param rightMotor
   * @param TRACK
   * @param WHEEL_RAD
   * @param odometer
   */
	public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double TRACK, double WHEEL_RAD, Odometer odometer, LightPoller lpLeft, LightPoller2 lpRight){
		this.odometer = odometer;
	    this.leftMotor = leftMotor;
	    this.rightMotor = rightMotor;
	    this.lpLeft = lpLeft;//left one
	    this.lpRight = lpRight;//right one
	    
	    

	    this.TRACK = TRACK;
	    this.WHEEL_RAD = WHEEL_RAD;
	  }
	
	public void _travelToX(double x) {
		double[] position = this.odometer.getXYT(); //get current position
		x =  x * TILE_SIZE;
		double delta_x = x - position[0]; //calculate change in x needed
		double theta = 270; //calculate angle required
		
		if (delta_x < 0) {		//If going to the negative x, change the theta
			theta = 90;
		}
		
		position = this.odometer.getXYT(); //get current position
		
		double delta_theta = position[2] - theta; //calculate change in theta based on current angle
		
		turn(delta_theta); //Turn to delta_theta
		
		angleCorrection();//correct angle -> tunnel travel wont work
		
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		position = this.odometer.getXYT();
		
		delta_x = x - position[0];
		
		travelForward(Math.abs(delta_x));
		
	    /*while( Math.abs(Math.abs(position[0]) - Math.abs(x)) > 1) //dont need abs value on the X -> coordinates are always positive
	    	{
	    	moveForward();
	    	try {
				Thread.sleep(10);
			} 
	    	catch (InterruptedException e) 
	    	{
				e.printStackTrace();
			}
	    	position = this.odometer.getXYT();
	    	
	    }*/
	    
	    stopRobot();
	}
	public void _travelToY(double y) {
		
		double[] position = this.odometer.getXYT(); //get current position
		y =  y * TILE_SIZE;
		double delta_y = y - position[1];
		double theta;
		if(delta_y < 0) {
			theta = 180; //calculate angle required
		}
		else {
			theta = 0;
		}
		
		position = this.odometer.getXYT(); //get current position
		
		double delta_theta = position[2] - theta; //calculate change in theta based on current angle
		
		turn(delta_theta); //Turn to delta_theta
		
		angleCorrection(); //correct angle -> tunnel travel wont work
	    
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		position = this.odometer.getXYT();
		
		delta_y = y - position[1];
		
		travelForward(Math.abs(delta_y));
		/*while( Math.abs((Math.abs(position[1]) - Math.abs(y))) > 1) //dont need abs value on the Y -> coordinates are always positive 
		{
	    	moveForward();
	    	try {
				Thread.sleep(10);
			} 
	    	catch (InterruptedException e) 
	    	{
	    		e.printStackTrace();
	    	}
	    	position = this.odometer.getXYT();
	    }
		*/
	    stopRobot();
	}
	
	public void travelTo(double x, double y) {
		_travelToX(x);
		_travelToY(y);
		
	}
	/**
	 * This method moves the robot to a point while avoiding obstacles
	 * @param x, y coordinates of point to travel to
	 */
	
	public void travelToAvoid(int x, int y) {
		
	}
	
	
	/**
	 * This turn method moves the robot to be facing an angle minimally
	 * @param theta angle to turn to
	 */
	public void turn(double theta) {
		leftMotor.setSpeed(ROTATE_SPEED);	//Set motors to rotate speed
		rightMotor.setSpeed(ROTATE_SPEED);

		//Turn in the direction resulting in the minimal angle
		//turn clockwise
		if(theta < -180) {
			theta += 360;
		}
		if(theta < 180) {	
			leftMotor.rotate(convertAngle(theta), true);
		    rightMotor.rotate(-convertAngle(theta), false);
		}
		//turn counterclockwise
		else {
			leftMotor.rotate(-convertAngle((360 - theta)), true);
		    rightMotor.rotate(convertAngle((360 - theta)), false);
		}
	}
	
	public void turnTo(double theta) {
		double[] position = odometer.getXYT();
		turn(theta - position[2]);
	}

	/**
	 * This convertDistance method returns the distance a wheel would move to go a distance
	 * @param radius radius of the wheel
	 * @param distance distance for the robot to move
	 */
	public int convertDistance(double distance) {
	    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
	}
	/**
	 * This convertAngle method returns the distance a wheel would move to turn to an angle
	 * @param radius radius of the wheel
	 * @param width width of wheel base
	 * @param angle angle to move robot to
	 */
	public int convertAngle(double angle) {
		return convertDistance(Math.PI * TRACK * angle / 360.0);
	}
	
	/**
	 * This method moves the forward a certain number of centimeters
	 * @param distance
	 */
	public void travelForward(double distance) {
		leftMotor.setAcceleration(acceleration);
		rightMotor.setAcceleration(acceleration);
		
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.rotate(convertDistance(distance), true);
		rightMotor.rotate(convertDistance(distance), false);
		
		leftMotor.setAcceleration(defAcceleration);
		rightMotor.setAcceleration(defAcceleration);
		
	}
	
	/**
	 * This method moves the backward a certain number of centimeters
	 * @param distance
	 */
	public void travelBackward(double distance) {
		leftMotor.setSpeed(CORRECTION_SPEED);
		rightMotor.setSpeed(CORRECTION_SPEED);
		leftMotor.rotate(-convertDistance(distance), true);
		rightMotor.rotate(-convertDistance(distance), false);
	}
	/**
	 * This method stops the robot
	 */
	
	public void stopRobot() {
		leftMotor.stop(true);
		rightMotor.stop(false);
	}
	
	/**
	 * This method starts the robot moving continuously forward
	 */
	public void moveForward() {
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	/**
	 * This method starts the robot moving continuously forward slowly
	 */
	public void moveForwardSlowly() {
		leftMotor.setSpeed(CORRECTION_SPEED);
		rightMotor.setSpeed(CORRECTION_SPEED);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	
	/**
	 * This method starts moving the left motor continuously
	 */
	public void moveLeftForward()
	{
		leftMotor.setSpeed(CORRECTION_SPEED);
		rightMotor.setSpeed(CORRECTION_SPEED);
		leftMotor.forward();
	}
	
	/**
	 * This method starts moving the left motor continuously
	 */
	public void moveRightForward()
	{
		leftMotor.setSpeed(CORRECTION_SPEED);
		rightMotor.setSpeed(CORRECTION_SPEED);
		rightMotor.forward();
	}
	
	/**
	 * This method moves the robot in a full circle
	 * @param d direction of circle
	 */
	public void continuousCircle(int d) {
		leftMotor.setSpeed(CIRCLE_SPEED);
		rightMotor.setSpeed(CIRCLE_SPEED);
		if(d == 1) {
			leftMotor.rotate(convertAngle(360), true);
			rightMotor.rotate(-convertAngle(360), true);
		}
		else {
			leftMotor.rotate(-convertAngle(360), true);
			rightMotor.rotate(convertAngle(360), true);
		}
	}
	
	/**
	 * This method moves the robot through a tunnel
	 * @param LLY
	 * @param LLX
	 * @param URX
	 * @param URY
	 */
	public void moveThroughTunnel(int LLX, int LLY, int URX, int URY, int iLLX, int iLLY, int iURX, int iURY) {
		//Horizontal Backwards
		if(inIsland(LLX + 0.5, LLY - 0.5, iLLX, iLLY, iURX, iURY)) {
			travelTo(URX - 0.5, URY + 0.4);
			turnTo(270);
			travelForward((URX - LLX + 1.5) * TILE_SIZE);
			odometer.setX((LLX + 1)*TILE_SIZE);
		}
		//Vertical Upwards
		else if(inIsland(URX - 0.5, URY + 0.5, iLLX, iLLY, iURX, iURY)) {
			travelTo(LLX + 0.6, LLY - 0.5);
			travelForward((URY - LLY + 1.5) * TILE_SIZE);
			odometer.setY((URY + 1)*TILE_SIZE);
		}
		//Horizontal Forwards
		else if(inIsland(URX + 0.5, URY - 0.5, iLLX, iLLY, iURX, iURY)) {
			travelTo(LLX - 0.5, LLY + 0.4);
			turnTo(90);
			travelForward((URX - LLX + 1.5) * TILE_SIZE);
			odometer.setX((URX + 1)*TILE_SIZE);
			
		}
		//Vertical Downwards
		else if(inIsland(LLX - 0.5, LLY + 0.5, iLLX, iLLY, iURX, iURY)) {
			travelTo(URX + 0.6, URY - 0.5);
			travelForward((URY - LLY + 1.5) * TILE_SIZE);
			odometer.setY((LLY + 1)*TILE_SIZE);
		}
	}
	
	public boolean inIsland(double x, double y, int LLX, int LLY, int URX, int URY) {
		if(x < LLX || x > URX || y < LLY || y > URY) {
			return false;
		}
		return true;
	}
	
	public void initializeXYT(int c) {
		switch(c){
		case 0:
			odometer.setXYT(TILE_SIZE, TILE_SIZE, 0);
			break;
		case 1:
			odometer.setXYT(TILE_SIZE * (width-1), TILE_SIZE, 270);
			break;
		case 2:
			odometer.setXYT(TILE_SIZE * (width-1), TILE_SIZE * (length-1), 180);
			break;
		case 3:
			odometer.setXYT(TILE_SIZE, TILE_SIZE * (length-1), 90);
			break;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void travelToTree(int x, int y) {
		double[] position = odometer.getXYT();
		boolean left = true;
		boolean right = true;
		boolean up = true;
		boolean down = true;
		
		
		//Check edge cases
		if(x + 1 == 8 || x + 1 == BetaDemo.Island_UR_x) {
			right = false;
		}
		if(x - 1 == 0 || x - 1 == BetaDemo.Island_LL_x) {
			left = false;
		}
		if(y + 1 == 8 || y + 1 == BetaDemo.Island_UR_y) {
			down = false;
		}
		if(y - 1 == 0 || y - 1 == BetaDemo.Island_LL_y) {
			up = false;
		}
		
		if(x * TILE_SIZE > position[1] && right){
			_travelToY(y);
			_travelToX(x + 1);
		}
		else if(x * TILE_SIZE < position[1] && left){
			_travelToY(y);
			_travelToX(x - 1);
		}
		else if(y * TILE_SIZE > position[1] && up) {
			travelTo(x, y + 1);
		}
		else if(y * TILE_SIZE < position[1] && down){
			travelTo(x, y - 1);
		}
		
	}
	
	public void moveSideOfTree(int x, int y) {
		double[] position = odometer.getXYT();
		
		if(y*TILE_SIZE > position[1]) {
			travelTo(x + 0.5, y);
		}
		else if(x*TILE_SIZE < position[0]) {
			travelTo(x, y + 0.5);
		}
		else if(y*TILE_SIZE < position[1]) {
			travelTo(x - 0.5, y);
		}
		else {
			travelTo(x, y - 0.5);
		}
	}
	
	public boolean betweenLines() {
		double[] position = odometer.getXYT();
		
		if(position[0] % TILE_SIZE < (TILE_SIZE/12) && position[1] % TILE_SIZE < (TILE_SIZE/12)) {
			return false;
		}
		return true;
	}

	public void adjustOdometer() {
		double[] position = odometer.getXYT();
		
		if(position[0] % TILE_SIZE < (TILE_SIZE/8)) {
			odometer.setX((int)(position[0]/TILE_SIZE) * TILE_SIZE);
		}
		if(position[1] % TILE_SIZE < (TILE_SIZE/8)) {
			odometer.setY((int)(position[1]/TILE_SIZE) * TILE_SIZE);
		}
	}
	
	public void adjustTheta(float a) {
		odometer.setTheta(a);
	}
	
	/**
	   * This method corrects the position of the robot on a coordinate. It moves the robot to the of the intersection of the lines. The coordinate values must be integers
	   */

	  public void coordinateCorrection() {
		 
		  //travel backward
		  travelBackward(correction); //5 centimeters can be too much -> will see the perpendicular line which is not what we want
		  
		  //travel forward if sees a line => robot will stop moving when it sees a line
		  while(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == -1) //MAYBE CHANGE == -1 TO != 1
		  {
			  moveForwardSlowly();
		  }
		  
		  stopRobot();//just in case
		  Sound.beep();
		  
		  //move  one isn't on a line
		  while(this.lpRight.detectLineRight() != 1 || this.lpLeft.detectLineLeft() != 1)//might have to correct it self twice
		  {
			  Sound.beep();
			  if(this.lpRight.detectLineRight() == 1 && this.lpLeft.detectLineLeft() == -1) //right sensor sees a line and left one doesn't
			  {
				  Sound.beep();
				  while(this.lpLeft.detectLineLeft() == -1)//move left wheel forward until it sees the line
				  {
					  moveLeftForward();
				  }
				 
				  stopRobot();
			  }
			  
			  
			  if(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == 1) //right sensor doesn't see a line and left does see a line
			  {
				  Sound.beep();
				  while(this.lpRight.detectLineRight() == -1)
				  {
					  moveRightForward();
				  }
				  
				  stopRobot();
			  }
		  }
		  
		  
		  //PART TWO
		  position = this.odometer.getXYT(); //get XYT from odometer
		  turnTo(position[2] + 90);
		  
		  travelBackward(correction);
		  
		  
		  while(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == -1) //MAYBE CHANGE == -1 TO != 1
		  {
			  moveForwardSlowly();
		  }
		  
		  stopRobot();
		  
		//move  one isn't on a line
		  while(this.lpRight.detectLineRight() != 1 || this.lpLeft.detectLineLeft() != 1)//might have to correct it self twice
		  {
			  Sound.beep();
			  if(this.lpRight.detectLineRight() == 1 && this.lpLeft.detectLineLeft() == -1) //right sensor sees a line and left one doesn't
			  {
				  Sound.beep();
				  while(this.lpLeft.detectLineLeft() == -1)//move left wheel forward until it sees the line
				  {
					  moveLeftForward();
				  }
				 
				  stopRobot();
			  }
			  
			  
			  if(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == 1) //right sensor doesn't see a line and left does see a line
			  {
				  Sound.beep();
				  while(this.lpRight.detectLineRight() == -1)
				  {
					  moveRightForward();
				  }
				  
				  stopRobot();
			  }
		  }
		 
		  position = this.odometer.getXYT(); 
		  
		//set odometer angle
		  if(325 < position[2] || position[2] < 35)
		  {
			  odometer.setTheta(0);
		  }
		  
		  if(55 < position[2] && position[2] < 125)
		  {
			  odometer.setTheta(90);
		  }
		  
		  if(145 < position[2] && position[2] < 215)
		  {
			  odometer.setTheta(180);
		  }
		  
		  if(235 < position[2] && position[2] < 305)
		  {
			  odometer.setTheta(270);
		  }
			  
		  //CORRECT X AND Y ON ODOMETER
		  
		  position = this.odometer.getXYT(); 
		  
		  double x = position[0];
		  double y = position[1];

		  
		  x = x / tileSize;
		  y = y / tileSize;
		  
		  x = Math.rint(x);
		  y = Math.rint(y);
		  	  
		  x = Math.abs(x * tileSize);
		  y = Math.abs(y * tileSize);
		  
		  odometer.setX(x);
		  odometer.setY(y);
		  
	    // do i need to sleep something?
	  }
	  
	  
	  
	  /**
	   * This method corrects angles which are perpendicular to a line.
	   */
	  public void angleCorrection()
	  {
		  //travel backward
		  travelBackward(correction); //5 centimeters can be too much -> will see the perpendicular line which is not what we want
		  
		  //travel forward if sees a line => robot will stop moving when it sees a line
		  while(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == -1) //MAYBE CHANGE == -1 TO != 1
		  {
			  moveForwardSlowly();
		  }
		  
		  stopRobot();//just in case
		  Sound.beep();//found a line
		  
		  //move  one isn't on a line
		  
		  while(this.lpRight.detectLineRight() != 1 || this.lpLeft.detectLineLeft() != 1)//might have to correct it self twice
		  {
			  Sound.beep();
			  if(this.lpRight.detectLineRight() == 1 && this.lpLeft.detectLineLeft() == -1) //right sensor sees a line and left one doesn't
			  {
				  Sound.beep();
				  while(this.lpLeft.detectLineLeft() == -1)//move left wheel forward until it sees the line
				  {
					  moveLeftForward();
				  }
				 
				  stopRobot();
			  }
			  
			  
			  if(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == 1) //right sensor doesn't see a line and left does see a line
			  {
				  Sound.beep();
				  while(this.lpRight.detectLineRight() == -1)
				  {
					  moveRightForward();
				  }
				  
				  stopRobot();
			  }
		  }
		  
		  position = this.odometer.getXYT(); 
		  
		//set odometer angle
		  if(325 < position[2] || position[2] < 35)
		  {
			  odometer.setTheta(0);
		  }
		  
		  if(55 < position[2] && position[2] < 125)
		  {
			  odometer.setTheta(90);
		  }
		  
		  if(145 < position[2] && position[2] < 215)
		  {
			  odometer.setTheta(180);
		  }
		  
		  if(235 < position[2] && position[2] < 305)
		  {
			  odometer.setTheta(270);
		  }
	  }
	
	
	
}
