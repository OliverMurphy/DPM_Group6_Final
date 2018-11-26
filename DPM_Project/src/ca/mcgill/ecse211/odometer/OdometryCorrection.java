package ca.mcgill.ecse211.odometer;

import ca.mcgill.ecse211.project.LightLocalizer;
import ca.mcgill.ecse211.project.Navigation;

/**
 * This class corrects the odometer value
 * @author Lucy Coyle
 * @author Oliver Murphy
 */

public class OdometryCorrection {
	
	private static final double CORRECTION_BACKWARDS = 5.0;
	private static final int ANGLE_RANGE = 35;
	
	private Navigation nav;
	private Odometer odometer;
	private LightLocalizer lightLocalizer;
	
  /**
   * This is the default constructor of this class.
   * 
   * @param nav
   * @param odometer
   * @param lightLocalizer
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
	   
	  double [] position = this.odometer.getXYT(); 
	  nav.turnTo(position[2] + 90);
	  
	  angleCorrection();

	  position = this.odometer.getXYT(); 
	  
	  double x = position[0];
	  double y = position[1];
	  
	  x = x / nav.TILE_SIZE;
	  y = y / nav.TILE_SIZE;
	  
	  x = Math.rint(x);
	  y = Math.rint(y);
	  	  
	  x = Math.abs(x * nav.TILE_SIZE);
	  y = Math.abs(y * nav.TILE_SIZE);
	  
	  odometer.setX(x);
	  odometer.setY(y);
	  
   
  }
  
  /**
   * This method corrects theta to the nearest multiple of 90.
   */
  
  private void correctTheta() {
	  double [] position = this.odometer.getXYT();
	 
	  if((360 - ANGLE_RANGE < position[2] && position[2] <= 360)|| (0 <= position[2] && position[2] < 0 + ANGLE_RANGE)){
		  odometer.setTheta(0);
	  }
	  
	  if(90 - ANGLE_RANGE < position[2] && position[2] < 90 + ANGLE_RANGE){
		  odometer.setTheta(90);
	  }
	  
	  if(180 - ANGLE_RANGE < position[2] && position[2] < 180 + ANGLE_RANGE){
		  odometer.setTheta(180);
	  }
	  
	  if(270 - ANGLE_RANGE < position[2] && position[2] < 270 + ANGLE_RANGE){
		  odometer.setTheta(270);
	  }
  }
  
  /**
   * This method straightens the robot onto the nearest line.
   */
  
  private void straightenOnLine() {
	  nav.travelBackward(CORRECTION_BACKWARDS);
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
