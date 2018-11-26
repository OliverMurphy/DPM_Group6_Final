package ca.mcgill.ecse211.testing;

import ca.mcgill.ecse211.odometer.*;
import ca.mcgill.ecse211.testing.TestWheelBase;

/**
 * This class tests the odometer
 * @author Nima Chatlani
 */

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class implements a test of the odometer 
 * @author Nima Chatlani
 */


public class TestOdometry {
	public static final double WHEEL_RAD = 2.1;
	public static final double TRACK = 11.2;
	
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));


	//Set up light sensor
	public static Port lightPort = LocalEV3.get().getPort("S2");
	public static EV3ColorSensor lightSensor = new EV3ColorSensor(lightPort);
	public static SampleProvider lightSensorValues = lightSensor.getRedMode();
	static float[] lightSensorData = new float[lightSensor.sampleSize()];

	//Set up screen display
	public static final TextLCD lcd = LocalEV3.get().getTextLCD();
	
	public static Odometer odometer;

	/**
	 * This main method implements the logic for the odometry test
	 * 
	 * 1. create an instance of the Odometer and Odometer display
	 * 2. print options for either float motors or square driver on the lcd display
	 * 3. if float motors is selected, the odometry and odometry display threads are started
	 *    and any changes in x,y,theta positions are displayed on the lcd screen to demonstrate 
	 *    odometry capabilities 
	 * 4. if square driver is selected, drive() is called
	 * @throws OdometerExceptions 
	 */
	public static void main(String [] args) throws OdometerExceptions{
		int buttonChoice;
		
		//Odometer related objects
		odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
		OdometerDisplay odometryDisplay = new OdometerDisplay(lcd);

	
	    do {
	      // clear the display
	      lcd.clear();
	
	      // ask the user whether the motors should drive in a square or float
	      lcd.drawString("< Left | Right >", 0, 0);
	      lcd.drawString("       |        ", 0, 1);
	      lcd.drawString(" Float | Drive  ", 0, 2);
	      lcd.drawString("motors | in a   ", 0, 3);
	      lcd.drawString("       | square ", 0, 4);
	
	      buttonChoice = Button.waitForAnyPress(); // Record choice (left or right press)
	    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
	
	    if (buttonChoice == Button.ID_LEFT) {
	      // Float the motors
	      leftMotor.forward();
	      leftMotor.flt();
	      rightMotor.forward();
	      rightMotor.flt();
	
	      // Display changes in position as wheels are (manually) moved
	      
	      Thread odoThread = new Thread(odometer);
	      odoThread.start();
	      Thread odoDisplayThread = new Thread(odometryDisplay);
	      odoDisplayThread.start();
	
	    } 
	    else {
	    	 // spawn a new Thread to avoid SquareDriver.drive() from blocking
	      (new Thread() {
	        public void run() {
	          TestWheelBase.drive(leftMotor, rightMotor, WHEEL_RAD, WHEEL_RAD, TRACK);
	        }
	      }).start();
	   }
      
	    while (Button.waitForAnyPress() != Button.ID_ESCAPE);
	    System.exit(0);
	  }
	
}
		