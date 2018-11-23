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

public class OdometryCorrection 
{
	private double tileSize = 30.48;
	public static final double correction = 5.0;
	private LightPoller lpLeft;
	private LightPoller lpRight;
	private Navigation nav;
	private Odometer odometer;
	private double position[]  = new double[3];
	//private boolean notSeenBothLines = true;
	
	
  /**
   * This is the default constructor of this class.
   * 
   * @param lpLeft
   * @param nav
   * @param odometer
   * @param lpRight
   */
  public OdometryCorrection(LightPoller lpLeft, LightPoller lpRight, Navigation nav, Odometer odometer)
  {
    this.lpRight = lpRight;//right one
    this.nav = nav;
    this.lpLeft = lpLeft;//left one
    this.odometer = odometer;
  }


  /**
   * This method corrects the position of the robot on a coordinate. It moves the robot to the of the intersection of the lines. The coordinate values must be integers
   */

  public void coordinateCorrection() {
	
	  //travel backward
	  nav.travelBackward(correction); //5 centimeters can be too much -> will see the perpendicular line which is not what we want
	  
	  //travel forward if sees a line => robot will stop moving when it sees a line
	  while(this.lpRight.detectLine() == -1 && this.lpLeft.detectLine() == -1) //MAYBE CHANGE == -1 TO != 1
	  {
		  nav.moveForwardSlowly();
	  }
	  
	  nav.stopRobot();//just in case
	  Sound.beep();
	  
	  //move  one isn't on a line
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
			  while(this.lpRight.detectLine() == -1)
			  {
				  nav.moveRightForward();
			  }
			  
			  nav.stopRobot();
		  }
		  try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	  }
	  
	  
	  //PART TWO
	  position = this.odometer.getXYT(); //get XYT from odometer
	  nav.turnTo(position[2] + 90);
	  
	  nav.travelBackward(correction);
	  
	  
	  while(this.lpRight.detectLine() == -1 && this.lpLeft.detectLine() == -1) //MAYBE CHANGE == -1 TO != 1
	  {
		  nav.moveForwardSlowly();
	  }
	  
	  nav.stopRobot();
	  
	//move  one isn't on a line
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
			  while(this.lpRight.detectLine() == -1)
			  {
				  nav.moveRightForward();
			  }
			  
			  nav.stopRobot();
		  }
		  try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		  
	  }
	  
	  
	  
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
	  nav.travelBackward(correction); //5 centimeters can be too much -> will see the perpendicular line which is not what we want
	  
	  //travel forward if sees a line => robot will stop moving when it sees a line
	  while(this.lpRight.detectLine() == -1 && this.lpLeft.detectLine() == -1) //MAYBE CHANGE == -1 TO != 1
	  {
		  nav.moveForwardSlowly();
	  }
	  
	  nav.stopRobot();//just in case
	  //Sound.beep();//found a line
	  
	  //move  one isn't on a line
	  
	  while(this.lpRight.detectLine() != 1 || this.lpLeft.detectLine() != 1)//might have to correct it self twice
	  {
		  //Sound.beep();
		  if(this.lpRight.detectLine() == 1 && this.lpLeft.detectLine() == -1) //right sensor sees a line and left one doesn't
		  {
			  //Sound.beep();
			  while(this.lpLeft.detectLine() == -1)//move left wheel forward until it sees the line
			  {
				  nav.moveLeftForward();
			  }
			 
			  nav.stopRobot();
		  }
		  
		  
		  if(this.lpRight.detectLine() == -1 && this.lpLeft.detectLine() == 1) //right sensor doesn't see a line and left does see a line
		  {
			  //Sound.beep();
			  while(this.lpRight.detectLine() == -1)
			  {
				  nav.moveRightForward();
			  }
			  
			  nav.stopRobot();
		  }
	  }
	  
	  
	//set odometer angle
	  if(325 < position[2] && position[2] < 35)
	  {
		  odometer.setTheta(0);
		  Sound.beep();
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
