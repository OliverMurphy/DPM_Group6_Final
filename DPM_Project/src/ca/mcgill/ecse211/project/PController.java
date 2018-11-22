package ca.mcgill.ecse211.project;

/**
 * This class implements a P controller
 * @author Lucy Coyle
 */

public class PController {

  /* Constants */
  private static final int MOTOR_SPEED = 175;
  private static final int FILTER_OUT = 10;
  private static final int GAIN_CONSTANT = 5; 
  private final int bandCenter;
  private final int bandWidth;
  private int distance;
  private int filterControl;
  
  /**
   * This is the default constructor of this class. It initiates the band center and band width
   * 
   * @param bandCenter
   * @param bandwidth
   */

  public PController(int bandCenter, int bandwidth) {
    this.bandCenter = bandCenter;
    this.bandWidth = bandwidth;
    this.filterControl = 0;

    Project.leftMotor.setSpeed(MOTOR_SPEED); // Initialize motor rolling forward
    Project.rightMotor.setSpeed(MOTOR_SPEED);
    Project.leftMotor.forward();
    Project.rightMotor.forward();
  }
  
  /**
   * This method is used to process the distance from the ultrasonic and move accordingly
   * 
   * @param distance
   */
  
  public void processUSData(int distance) {

    // rudimentary filter - toss out invalid samples corresponding to null
    // signal.
    // (n.b. this was not included in the Bang-bang controller, but easily
    // could have).
    //
    if (distance >= 255 && filterControl < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the
      // filter value
      filterControl++;
    } else if (distance >= 255) {
      // We have repeated large values, so there must actually be nothing
      // there: leave the distance alone
      this.distance = distance;
    } else {
      // distance went below 255: reset filter and leave
      // distance alone.
      filterControl = 0;
      this.distance = distance;
    }
    
    int error = this.bandCenter - this.distance;
    
    if (Math.abs(error) <= this.bandWidth) {
    	Project.leftMotor.setSpeed(MOTOR_SPEED); // Set robot to same speed as before
        Project.rightMotor.setSpeed(MOTOR_SPEED);
    }
    else if (error > 0){
    	//Turn robot to the right if it is too close to the wall
    	Project.leftMotor.setSpeed(MOTOR_SPEED + (GAIN_CONSTANT * error)); 
    	Project.rightMotor.setSpeed(MOTOR_SPEED - (GAIN_CONSTANT * error));
    }
    else {
    	//Turn robot to the left if it is too far from the wall
    	//Make sure the speed is not set to a negative number
    	if ((GAIN_CONSTANT * error) + MOTOR_SPEED <= 0){
    		Project.leftMotor.setSpeed(0);
    		Project.rightMotor.setSpeed(MOTOR_SPEED*2);
    	}
    	else {
    		Project.leftMotor.setSpeed(MOTOR_SPEED + (GAIN_CONSTANT * error));
    		Project.rightMotor.setSpeed(MOTOR_SPEED - (GAIN_CONSTANT * error));
    	}
        
    }
    Project.leftMotor.forward();
    Project.rightMotor.forward();

  }
  /**
   * This method is used to return the ultrasonic distance
   * 
   * @returns distance
   */

  public int readUSDistance() {
    return this.distance;
  }

}
