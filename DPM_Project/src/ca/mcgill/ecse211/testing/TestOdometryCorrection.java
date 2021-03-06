
package ca.mcgill.ecse211.testing;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import ca.mcgill.ecse211.odometer.*;
import ca.mcgill.ecse211.project.LightPoller;
import ca.mcgill.ecse211.project.Navigation;
import ca.mcgill.ecse211.project.UltrasonicPoller;
import ca.mcgill.ecse211.project.LightLocalizer;

/**
 * This class is used to test the odometry correction feature
 *  @author Nima Chatlani
 *
 **/
 
 
public class TestOdometryCorrection {
	
  private static final int FORWARD_SPEED = 250;
  private static final int ROTATE_SPEED = 150;
  private static final double TILE_SIZE = 30.48;
  
  //Set up screen display
  public static final TextLCD lcd = LocalEV3.get().getTextLCD();
  
  //Motor Objects, and Robot related parameters
  private static final EV3LargeRegulatedMotor leftMotor =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
  private static final EV3LargeRegulatedMotor rightMotor =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
 
  public static final double WHEEL_RAD = 2; //Measured wheel radius
  public static final double WHEEL_BASE = 11.2; //Measured distance between wheels
  
  
  private static final Port usPort = LocalEV3.get().getPort("S2");
  public static Port lightPortL = LocalEV3.get().getPort("S1");
  public static Port lightPortR = LocalEV3.get().getPort("S3");
	
  public static LightPoller lightPollerL  = new LightPoller(lightPortL);
  public static LightPoller lightPollerR = new LightPoller(lightPortR);
  public static UltrasonicPoller usPoller = new UltrasonicPoller(usPort);
  
  public static Odometer odometer;
  public static Navigation navigation; 
  
  public static LightLocalizer lightLocalizer = new LightLocalizer(lightPollerL, lightPollerR, navigation);



  /**
   * This method is meant to drive the robot in a square of size 1x1 Tiles. It is to run in parallel
   * with the odometer and Odometer correcton classes allow testing their functionality.
   * 
   * 1. call travelTo on the Navigation instance with correction enabled
   * 
   * @param leftMotor
   * @param rightMotor
   * @param leftRadius
   * @param rightRadius
   * @param width
   */
   
  public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      double leftRadius, double rightRadius, double track, Navigation navigation) {
	  boolean correct = true;
    // reset the motors
	for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {leftMotor, rightMotor}) {
	  motor.stop();
	  motor.setAcceleration(3000);
	}
	
	// Sleep for 2 seconds
	try {
	  Thread.sleep(2000);
	} catch (InterruptedException e) {
	  // There is nothing to be done here
	}
	
	navigation.travelTo(0,3,true);
	navigation.travelTo(3,3,true);
	navigation.travelTo(3,0,true);
	navigation.travelTo(0,0,true);
	
   }
  

  /**
   * This method allows the conversion of a distance to the total rotation of each wheel need to
   * cover that distance.
   * 
   * @param radius
   * @param distance
   * @return distance
   */
  private static int convertDistance(double radius, double distance) {
    return (int) ((180.0 * distance) / (Math.PI * radius));
  }
  
  /**
   * This method allows the conversion of an angle to the total rotation of each wheel need to
   * turn to the angle.
   * 
   * @param radius
   * @param distance
   * @param angle
   */

  private static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }
  
  /**
	 * This main method implements the logic for the odometer correction test 
	 * 
	 * 1. create new instances of Odometer, Navigation, OdometryDisplay, and Odometry Correction
	 * 2. wait for a button to be pressed, which starts the odometry thread
	 * 3. call drive which implements a square driver with the odometry correction 
	 * 
	 * @throws OdometerExceptions 
	 */
  public static void main(String[] args)throws OdometerExceptions{
	    
	  	odometer = Odometer.getOdometer(leftMotor, rightMotor, WHEEL_BASE, WHEEL_RAD);
		navigation = new Navigation(leftMotor, rightMotor, WHEEL_BASE, WHEEL_RAD, odometer);
		OdometerDisplay odometryDisplay = new OdometerDisplay(lcd);
		OdometryCorrection odoCorrect = new OdometryCorrection(navigation, odometer, lightLocalizer);
		navigation.setOdoCorrection(odoCorrect);
	  
		// clear the display
		lcd.clear();

		// Choose light or ultrasonic sensor test
		lcd.drawString("< Left          >", 0, 0);
		lcd.drawString("                 ", 0, 1);
		lcd.drawString(" square  driver  ", 0, 2);
		lcd.drawString(" with correction ", 0, 3);
		
		int buttonChoice = Button.waitForAnyPress();
		
		// Start odometer and display threads
	    Thread odoThread = new Thread(odometer);
	    odoThread.start();
	    Thread odoDisplayThread = new Thread(odometryDisplay);
	    odoDisplayThread.start();

		
		if(buttonChoice == Button.ID_LEFT) {
			//lcd.drawString("press left", 0, 0);
		
			(new Thread() {
		        public void run() {
		          drive(leftMotor, rightMotor, WHEEL_RAD, WHEEL_RAD, WHEEL_BASE, navigation);
		        }
		      }).start();
		}
		Button.waitForAnyPress(); 
		System.exit(0);
  }
}
