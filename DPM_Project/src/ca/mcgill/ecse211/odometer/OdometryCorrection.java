package ca.mcgill.ecse211.odometer;

import ca.mcgill.ecse211.project.LightLocalizer;
import ca.mcgill.ecse211.project.Navigation;

/**
 * This class corrects the odometer value
 * @author Lucy Coyle
 * @author Oliver Murphy
 */

public class OdometryCorrection {
	private final double TILE_SIZE = 30.48;
	private static final double CORRECTION = 5.0;
	
	private Navigation nav;
	private Odometer odometer;
	private double position[]  = new double[3];
	private LightLocalizer lightLocalizer;
	
  /**
   * This is the default constructor of this class.
   * 
   * @param lpLeft
   * @param nav
   * @param odometer
   * @param lpRight
   */
  public OdometryCorrection(Navigation nav, Odometer odometer, LightLocalizer lightLocalizer){
    this.nav = nav;
    this.odometer = odometer;
    this.lightLocalizer = lightLocalizer;
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
	  lightLocalizer.straightenOnLine();
  }
  
  /**
   * This method corrects angles which are perpendicular to a line.
   */
  public void angleCorrection(){
	  straightenOnLine();
	  correctTheta();
  }

}
