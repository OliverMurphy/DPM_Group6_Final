package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometryCorrection;
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

	private OdometryCorrection odoCorrection;
	
  /**
   * This is the default constructor of this class. It initiates all motors and variables once
   * 
   * @param leftMotor
   * @param rightMotor
   * @param TRACK
   * @param WHEEL_RAD
   * @param odometer
   */
	public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double TRACK, double WHEEL_RAD, Odometer odometer){
		this.odometer = odometer;
	    this.leftMotor = leftMotor;
	    this.rightMotor = rightMotor;
	    this.TRACK = TRACK;
	    this.WHEEL_RAD = WHEEL_RAD;
	  }
	
	/** 
	 * This method sets the odometer correction variable
	 * @param odoCorrection
	 */
	public void setOdoCorrection(OdometryCorrection odoCorrection) {
		this.odoCorrection = odoCorrection;
	}
	
	/** 
	 * This method moves the robot to a coordinate with optional correction
	 * @param x
	 * @param y
	 * @param correct
	 */
	public void travelTo(double x, double y, boolean correct) {
		_travelToX(x, correct);
		_travelToY(y, correct);
	}
	
	/**
	 * This method moves the robot to an x coordinate
	 * @param x
	 * @param correct
	 */
	private void _travelToX(double x, boolean correct) {
		double[] position = this.odometer.getXYT(); //get current position
		x =  x * TILE_SIZE;
		double delta_x = x - position[0]; //calculate change in x needed
		double theta = 90; //calculate angle required
		
		if (delta_x < 0) {		//If going to the negative x, change the theta
			theta = 270;
		}
		
		position = this.odometer.getXYT(); //get current position
		
		double delta_theta = theta - position[2]; //calculate change in theta based on current angle
		turn(delta_theta); //Turn to delta_theta
		if(correct) {
			odoCorrection.angleCorrection();//correct angle -> tunnel travel wont work
		}
		
		sleep(500);
		position = this.odometer.getXYT();
		
		delta_x = x - position[0];
		
		travelForward(Math.abs(delta_x));
			    
	    stopRobot();
	}
	/**
	 * This method moves the robot to a y coordinate
	 * @param y
	 * @param correct
	 */
	private void _travelToY(double y, boolean correct) {
		
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
		
		double delta_theta = theta - position[2]; //calculate change in theta based on current angle
		
		turn(delta_theta); //Turn to delta_theta
		if(correct) {
			odoCorrection.angleCorrection(); //correct angle -> tunnel travel wont work
		}
		
		sleep(500);
		
		position = this.odometer.getXYT();
		
		delta_y = y - position[1];
		
		travelForward(Math.abs(delta_y));

	    stopRobot();
	}
	
	
	/**
	 * This method moves the robot to a point while avoiding obstacles
	 * @param x, y coordinates of point to travel to
	 */
	
	public void travelToAvoid(int x, int y) {
		
	}
	
	
	/**
	 * This method turns the robot by an angle
	 * @param theta
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
	/**
	 * This method turns the robot to face an angle
	 * @param theta
	 */
	public void turnTo(double theta) {
		double[] position = odometer.getXYT();
		turn(theta - position[2]);
	}

	/**
	 * This convertDistance method returns the distance a wheel would move to go a distance
	 * @param radius radius of the wheel
	 * @param distance distance for the robot to move
	 */
	private int convertDistance(double distance) {
	    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
	}
	/**
	 * This convertAngle method returns the distance a wheel would move to turn to an angle
	 * @param radius radius of the wheel
	 * @param width width of wheel base
	 * @param angle angle to move robot to
	 */
	private int convertAngle(double angle) {
		return convertDistance(Math.PI * TRACK * angle / 360.0);
	}
	
	/**
	 * This method moves the forward a certain number of centimeters
	 * @param distance
	 */
	public void travelForward(double distance) {
		leftMotor.setAcceleration(acceleration);
		rightMotor.setAcceleration(acceleration);
		sleep(200);
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		sleep(200);
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
		sleep(10);
		leftMotor.forward();
	}
	
	/**
	 * This method starts moving the left motor continuously
	 */
	public void moveRightForward()
	{
		rightMotor.setSpeed(CORRECTION_SPEED);
		sleep(10);
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
	 * This method moves the robot through the tunnel
	 * @param LLX
	 * @param LLY
	 * @param URX
	 * @param URY
	 * @param iLLX
	 * @param iLLY
	 * @param iURX
	 * @param iURY
	 */
	public void moveThroughTunnel(int LLX, int LLY, int URX, int URY, int iLLX, int iLLY, int iURX, int iURY) {
		//Horizontal Backwards
		if(inIsland(LLX - 0.5, LLY + 0.5, iLLX, iLLY, iURX, iURY)) {
			travelTo(URX - 1, URY + 0.5, true);
			turnTo(270);
			odoCorrection.angleCorrection();
			travelForward((URX-LLX + 2)* TILE_SIZE);
			odoCorrection.angleCorrection();
			odometer.setX((LLX + 1)* TILE_SIZE);
			odometer.setTheta(270);
		}
		//Vertical Upwards
		else if(inIsland(URX - 0.5, URY + 0.5, iLLX, iLLY, iURX, iURY)) {
			travelTo(LLX + 0.6, LLY - 1, true);
			odoCorrection.angleCorrection();
			travelForward((URY - LLY + 2) * TILE_SIZE);
			odoCorrection.angleCorrection();
			odometer.setY((URY + 1)* TILE_SIZE);
			odometer.setTheta(0);
			
		}
		//Horizontal Forwards
		else if(inIsland(URX + 0.5, URY - 0.5, iLLX, iLLY, iURX, iURY)) {
			travelTo(LLX - 1, LLY + 0.5, true);
			turnTo(90);
			odoCorrection.angleCorrection();
			travelForward((URX-LLX + 2)* TILE_SIZE);
			odoCorrection.angleCorrection();
			odometer.setX((URX + 1)* TILE_SIZE);
			odometer.setTheta(90);
			
		}
		//Vertical Downwards
		else if(inIsland(LLX + 0.5, LLY - 0.5, iLLX, iLLY, iURX, iURY)) {
			travelTo(URX - 0.5, URY + 1, true);
			odoCorrection.angleCorrection();
			travelForward((URY-LLY + 2)* TILE_SIZE);
			odoCorrection.angleCorrection();
			odometer.setY((LLY + 1)* TILE_SIZE);
			odometer.setTheta(180);
		}
		sleep(600);
	}
	
	/**
	 * This method returns true if a position is in the island and false if it is not
	 * @param x
	 * @param y
	 * @param LLX
	 * @param LLY
	 * @param URX
	 * @param URY
	 * @return
	 */
	private boolean inIsland(double x, double y, int LLX, int LLY, int URX, int URY) {
		if(x < LLX || x > URX || y < LLY || y > URY) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method initializes XYT in the odometer based on the corner the robot is localized at
	 * @param c
	 */
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
		sleep(500);
	}
	
	public void moveToStartingCorner(int c) {
		switch(c){
		case 0:
			travelTo(1, 1, true);
			break;
		case 1:
			travelTo((width - 1), 1, true);
			break;
		case 2:
			travelTo((width - 1), (length - 1), true);
			break;
		case 3:
			travelTo(1, (length - 1), true);
			break;
		}
		sleep(500);
	}
	
	/**
	 * This method moves the robot to the tree
	 * @param x
	 * @param y
	 * @param iLLX
	 * @param iLLY
	 * @param iURX
	 * @param iURY
	 */
	public void travelToTree(int x, int y, int iLLX, int iLLY, int iURX, int iURY) {
		double[] position = odometer.getXYT();

		if(x * TILE_SIZE > position[1] && x + 1 != width){
			_travelToY(y - 0.25, true);
			_travelToX(x + 1, false);
		}
		else if(x * TILE_SIZE < position[1] && x - 1 != 0){
			_travelToY(y - 0.25, true);
			_travelToX(x - 1, false);
		}
		else if(y * TILE_SIZE > position[1] && y - 1 != 0) {
			_travelToX(x - 0.25, true);
			_travelToY(y + 1, false);
		}
		else if(y * TILE_SIZE < position[1] && y + 1 != length){
			_travelToX(x - 0.25, true);
			_travelToY(y - 1, false);
		}
		
	}
	
	/**
	 * This method moves the robot to a new side of the tree
	 * @param x
	 * @param y
	 */
	public void moveSideOfTree(int x, int y) {
		double[] position = odometer.getXYT();
		
		if(y * TILE_SIZE > position[1]) {
			travelTo(x + 0.5, y, true);
		}
		else if(x * TILE_SIZE < position[0]) {
			travelTo(x, y + 0.5, true);
		}
		else if(y * TILE_SIZE < position[1]) {
			travelTo(x - 0.5, y, true);
		}
		else {
			travelTo(x, y - 0.5, true);
		}
	}
	
	/**
	 * This method sleeps the thread for an amount of miliseconds
	 * @param s
	 */
	private void sleep(int s) {
		try {
			Thread.sleep(s);
		} catch (InterruptedException e) {}
	}
	
}
