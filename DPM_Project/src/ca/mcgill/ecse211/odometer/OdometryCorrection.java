package ca.mcgill.ecse211.odometer;

import java.util.concurrent.TimeUnit;

import ca.mcgill.ecse211.project.LightPoller;
import ca.mcgill.ecse211.project.Navigation;
import lejos.hardware.Sound;

/**
 * This class corrects the odometer value
 * @author Lucy Coyle
 * @author Oliver Murphy
 */

public class OdometryCorrection {
	private final double TILE_SIZE = 30.48;
	private static final double CORRECTION = 5.0;
	private LightPoller lpLeft;
	private LightPoller lpRight;
	private Navigation nav;
	private Odometer odometer;
	private double position[]  = new double[3];
	
	
  /**
   * This is the default constructor of this class.
   * 
   * @param lpLeft
   * @param nav
   * @param odometer
   * @param lpRight
   */
  public OdometryCorrection(LightPoller lpLeft, LightPoller lpRight, Navigation nav, Odometer odometer){
    this.lpRight = lpRight;
    this.nav = nav;
    this.lpLeft = lpLeft;
    this.odometer = odometer;
  }


  /**
   * This method corrects the position of the robot on a coordinate. 
   * It moves the robot to the of the intersection of the lines. 
   */

  public void coordinateCorrection() {
	  straightenOnLine();
	   
	  position = this.odometer.getXYT(); //get XYT from odometer
	  nav.turnTo(position[2] + 90);
	  
	  angleCorrection();
		  
	  //CORRECT X AND Y ON ODOMETER
	  
	  position = this.odometer.getXYT(); 
	  
	  double x = position[0];
	  double y = position[1];
	  
	  x = x / TILE_SIZE;
	  y = y / TILE_SIZE;
	  
	  x = Math.rint(x);
	  y = Math.rint(y);
	  	  
	  x = Math.abs(x * TILE_SIZE);
	  y = Math.abs(y * TILE_SIZE);
	  
	  odometer.setX(x);
	  odometer.setY(y);
	  
   
  }
  
  /**
   * This method corrects theta to the nearest multiple of 90.
   */
  
  private void correctTheta() {
	  position = this.odometer.getXYT();
	  //set odometer angle
	  if((325 < position[2] && position[2] < 361)|| (position[2] >= 0 && position[2] < 35)){
		  odometer.setTheta(0);
	  }
	  
	  if(55 < position[2] && position[2] < 125){
		  odometer.setTheta(90);
	  }
	  
	  if(145 < position[2] && position[2] < 215){
		  odometer.setTheta(180);
	  }
	  
	  if(235 < position[2] && position[2] < 305){
		  odometer.setTheta(270);
	  }
  }
  
  /**
   * This method straightens the robot onto the nearest line.
   */
  
  private void straightenOnLine() {
	  nav.travelBackward(CORRECTION);
	  
	  
	  while(this.lpRight.detectLine() == -1 && this.lpLeft.detectLine() == -1) //MAYBE CHANGE == -1 TO != 1
	  {
		  nav.moveForwardSlowly();
	  }
	  
	  nav.stopRobot();
	  
	  while(this.lpRight.detectLine() != 1 || this.lpLeft.detectLine() != 1)//might have to correct it self twice
	  {
		  Sound.beep();
		  if(this.lpRight.detectLine() == 1 && this.lpLeft.detectLine() == -1) //right sensor sees a line and left one doesn't
		  {
			  Sound.beep();
			  while(this.lpLeft.detectLine() == -1)//move left wheel forward until it sees the line
			  {
				  nav.moveLeftForward();
			  }
			 
			  nav.stopRobot();
		  }
		  
		  
		  if(this.lpRight.detectLine() == -1 && this.lpLeft.detectLine() == 1) //right sensor doesn't see a line and left does see a line
		  {
			  Sound.beep();
			  while(this.lpRight.detectLine() == -1){
				  nav.moveRightForward();
			  }
			  
			  nav.stopRobot();
		  }
		  try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e1) {}
	  }
  }
  
  /**
   * This method corrects angles which are perpendicular to a line.
   */
  public void angleCorrection(){
	  straightenOnLine();
	  correctTheta();
  }

}
