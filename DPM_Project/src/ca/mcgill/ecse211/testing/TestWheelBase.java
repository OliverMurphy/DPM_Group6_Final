
package ca.mcgill.ecse211.testing;

import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class is used to drive the robot on the demo floor.
 *  @author Nima Chatlani
 */
 
 
public class TestWheelBase {
	
	
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
  public static final double WHEEL_BASE = 11; //Measured distance between wheels
  

  /**
   * This method is meant to drive the robot in a square of size 2x2 Tiles. It is to run in parallel
   * with the odometer and Odometer correcton classes allow testing their functionality.
   * 
   * @param leftMotor
   * @param rightMotor
   * @param leftRadius
   * @param rightRadius
   * @param width
   */
   
  public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      double leftRadius, double rightRadius, double track) {
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
	
	for (int i = 0; i < 4; i++) {
	  // drive forward two tiles
	  leftMotor.setSpeed(FORWARD_SPEED);
	  rightMotor.setSpeed(FORWARD_SPEED);
	
	  leftMotor.rotate(convertDistance(leftRadius, 3 * TILE_SIZE), true);
	  rightMotor.rotate(convertDistance(rightRadius, 3 * TILE_SIZE), false);
	
	  // turn 90 degrees clockwise
	  leftMotor.setSpeed(ROTATE_SPEED);
	  rightMotor.setSpeed(ROTATE_SPEED);
	
	  leftMotor.rotate(convertAngle(leftRadius, track, 90.0), true);
	  rightMotor.rotate(-convertAngle(rightRadius, track, 90.0), false);
	}
   }
  

  /**
   * This method allows the conversion of a distance to the total rotation of each wheel need to
   * cover that distance.
   * 
   * @param radius
   * @param distance
   * @return
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
   * @return
   */

  private static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }
  
  /**
	 * This main method implements the logic for the wheel base test
	 * @throws OdometerExceptions 
	 */
  public static void main(String[] args){

		// clear the display
		lcd.clear();

		// Choose light or ultrasonic sensor test
		lcd.drawString("< Left   | Right     >", 0, 0);
		lcd.drawString("         |            ", 0, 1);
		lcd.drawString(" square  | N/A ", 0, 2);
		lcd.drawString(" driver  |        ", 0, 3);
		
		int buttonChoice = Button.waitForAnyPress();
		
		if(buttonChoice == Button.ID_LEFT) {
			lcd.drawString("press left", 0, 0);
			(new Thread() {
		        public void run() {
		          drive(leftMotor, rightMotor, WHEEL_RAD, WHEEL_RAD, WHEEL_BASE);
		        }
		      }).start();
		}
		else {
			lcd.drawString("error", 0, 0);
		}
		Button.waitForAnyPress(); 
		System.exit(0);
  }
}
