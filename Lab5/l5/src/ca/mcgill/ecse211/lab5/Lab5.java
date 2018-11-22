package ca.mcgill.ecse211.lab5;

import ca.mcgill.ecse211.odometer.Display;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * This class implements the main control of lab 5
 * @author Lucy Coyle 
 */

public class Lab5 {

	// Port A to the leftMotor and Port B to the right
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	// Ultrasonic Sensor connected to port S2
	private static final Port usPort = LocalEV3.get().getPort("S1");
	public static Port lightPort = LocalEV3.get().getPort("S2");

	public static final TextLCD lcd = LocalEV3.get().getTextLCD();
	public static final double WHEEL_RAD = 2.1;
	public static final double TRACK = 10.3;
	
	public static final int LLX = 3;
	public static final int LLY = 2;
	public static final int URX = 5;
	public static final int URY = 5;
	public static final int r = 2;
	
	public static final double TILE_SIZE = 30.48;
	public static final int FORWARD_SPEED = 150; 
	public static final int ROTATE_SPEED = 90; 
	public static Odometer odometer;
	
	public static SampleProvider lightSensorValues;
	public static EV3ColorSensor lightSensor;
	static float[] lightSensorData;
	
	public static SensorModes usSensor = new EV3UltrasonicSensor(usPort); 
	public static SampleProvider usDistance = usSensor.getMode("Distance"); 
	public static float[] usData = new float[usDistance.sampleSize()];
	
	public static boolean localized = false;

	
	public static void main(String[] args) throws OdometerExceptions {

		// Create Odometer object
		odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
		Display odometryDisplay = new Display(lcd); // No need to change	
		
		lightSensor = new EV3ColorSensor(lightPort);
		lightSensorValues = lightSensor.getRedMode();
		lightSensorData = new float[lightSensor.sampleSize()];

		// clear the display
		lcd.clear();

		// Ask the user what version of the Ultrasonic localization should the robot follow
		// depending on the robot initial orientation
		lcd.drawString("< Left   | Right >", 0, 0);
		lcd.drawString("         |        ", 0, 1);
		lcd.drawString(" colour  | ring   ", 0, 2);
		lcd.drawString(" classify| find   ", 0, 3);
		int buttonChoice = Button.waitForAnyPress();

		if(buttonChoice == Button.ID_LEFT) {
			(new Thread() {
				public void run() {
					try {
						lightSensorValues = Lab5.lightSensor.getRGBMode();
						Lab5.lightSensorData = new float[3];
						RingFinder.colourClassify(lcd);
					}catch(InterruptedException e) {
						Sound.beep();
					}
				}
			}).start();
		}
		else {
			// Localizers
			final UltrasonicLocalizer ultrasonicLocalizer = new UltrasonicLocalizer(odometer, usDistance, usData);
			final LightLocalizer lightLocalizer = new LightLocalizer(odometer);
			// Start Odo Thread
			Thread odoThread = new Thread(odometer);
			odoThread.start();
			Thread odoDisplayThread = new Thread(odometryDisplay);
			odoDisplayThread.start();
			
			(new Thread() {
				public void run(){
						ultrasonicLocalizer.run();
						lightLocalizer.run();
						localized = true;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						localized = false;
						try {
							odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
							leftMotor.resetTachoCount();
							rightMotor.resetTachoCount();
							odometer.setXYT(TILE_SIZE, TILE_SIZE, 0);
							Thread odoThread = new Thread(odometer);
							odoThread.start();
						} catch (OdometerExceptions e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							Thread.sleep(500);
							lightSensorValues = Lab5.lightSensor.getRGBMode();
							lightSensorData = new float[3];
							RingFinder.find(r);
						}catch(InterruptedException e) {
							Sound.beep();
						}
				}

			}).start();
			
		}
		Button.waitForAnyPress(); 
		System.exit(0);
	}
	/**
	 * This travelTo method moves the robot to a point in the case of no obstacles
	 * @param x, y coordinates of point to travel to
	 */
	public static void travelTo(double x, double y) {
		double position[] = odometer.getXYT(); //get current position
		
		double delta_x = x - position[0]; //calculate change in x and y needed 
		double delta_y = y - position[1]; //to get to the way point
		
		double distance = Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2)); //calculate distance 
		
		double theta = Math.toDegrees(Math.acos(delta_y/distance)); //calculate angle required
		
		if (delta_x < 0) {		//If going to the negative x, change the theta
			theta = 360 - theta;
		} 
		
		double delta_theta = theta - position[2]; //calculate change in theta based on current angle
		
		if (delta_theta < -180) { //Adjust delta_theta
			delta_theta += 360;
		}
		
		turnTo(delta_theta); //Turn to delta_theta
	    leftMotor.setSpeed(FORWARD_SPEED);
	    rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(WHEEL_RAD, distance), true); //Set speed of both motors
		rightMotor.rotate(convertDistance(WHEEL_RAD, distance), false);
		
	}
	
	/**
	 * This turnTo method moves the robot to be facing an angle minimally
	 * @param theta angle to turn to
	 */
	public static void turnTo(double theta) {
		leftMotor.setSpeed(ROTATE_SPEED);	//Set motors to rotate speed
		rightMotor.setSpeed(ROTATE_SPEED);
		
		//Turn in the direction resulting in the minimal angle
		//turn clockwise
		if(theta < 180) {	
			leftMotor.rotate(convertAngle(WHEEL_RAD, TRACK, theta), true);
		    rightMotor.rotate(-convertAngle(WHEEL_RAD, TRACK, theta), false);
		}
		//turn counterclockwise
		else {
			leftMotor.rotate(-convertAngle(WHEEL_RAD, TRACK, (360 - theta)), true);
		    rightMotor.rotate(convertAngle(WHEEL_RAD, TRACK, (360 - theta)), false);
		}
		
	}

	/**
	 * This convertDistance method returns the distance a wheel would move to go a distance
	 * @param radius radius of the wheel
	 * @param distance distance for the robot to move
	 */
	public static int convertDistance(double radius, double distance) {
	    return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	/**
	 * This convertAngle method returns the distance a wheel would move to turn to an angle
	 * @param radius radius of the wheel
	 * @param width width of wheel base
	 * @param angle angle to move robot to
	 */
	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
}