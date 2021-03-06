package ca.mcgill.ecse211.testing;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import ca.mcgill.ecse211.odometer.OdometryCorrection;
import ca.mcgill.ecse211.project.LightLocalizer;
import ca.mcgill.ecse211.project.LightPoller;
import ca.mcgill.ecse211.project.Navigation;
import ca.mcgill.ecse211.project.UltrasonicLocalizer;
import ca.mcgill.ecse211.project.UltrasonicPoller;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

/**
 * This class implements a test of the tunnel navigation
 * @author Lucy Coyle
 */

public class TestTunnelNavigation {
	
	public static LightPoller lightPollerL;
	public static LightPoller lightPollerR;
	public static UltrasonicPoller usPoller;
	
	public static Navigation navigation;
	
	public static final double WHEEL_RAD = 2.1;
	public static final double TRACK = 11.2;
	
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	private static final Port usPort = LocalEV3.get().getPort("S2");
	public static Port lightPortL = LocalEV3.get().getPort("S3");
	public static Port lightPortR = LocalEV3.get().getPort("S1");

	public static final TextLCD lcd = LocalEV3.get().getTextLCD();
	
	public static Odometer odometer;

	
	public static UltrasonicLocalizer usLocalizer;
	public static LightLocalizer lightLocalizer;
	
	//Game Parameters
	
	//Test case 1: vertical tunnel (2x2)
	public static String teamColour = "green";
	public static int corner = 0;		
	public static int UR_x = 8;		
	public static int LL_x = 2;		
	public static int UR_y = 3;		
	public static int LL_y = 0;		
	public static int Island_UR_x = 6;	
	public static int Island_LL_x = 0;
	public static int Island_UR_y = 8;
	public static int Island_LL_y = 5;
	public static int Tunnel_UR_x = 4;
	public static int Tunnel_LL_x = 3;
	public static int Tunnel_UR_y = 5;
	public static int Tunnel_LL_y = 3;
	

	/**
	 * This main method implements the logic for the tunnel navigation test 
	 * 
	 * 1. create new instances of Odometer, Navigation, LightPollers, UltrasonicPoller, UltrasonicLocalizer,
	 *    LightLocalizer, and OdometryCorrection
	 * 2. wait for a button to be pressed which starts the odometry thread
	 * 3. call localize() on the UltrasonicLicalizer and LightLocalizer
	 * 4. Set x, y, theta based on corner
	 * 5. call moveThroughTunnel on the Navigation instance variable with parameters defined above
	 * @throws OdometerExceptions 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws OdometerExceptions, InterruptedException {
		
		odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
		navigation = new Navigation(leftMotor, rightMotor, TRACK, WHEEL_RAD, odometer);
		
		
		lightPollerL = new LightPoller(lightPortL);
		lightPollerR = new LightPoller(lightPortR);
		usPoller = new UltrasonicPoller(usPort);
		
		usLocalizer = new UltrasonicLocalizer(odometer, usPoller, navigation);
		lightLocalizer = new LightLocalizer(lightPollerL, lightPollerR, navigation);
		
		OdometryCorrection odoCorrect = new OdometryCorrection(navigation, odometer, lightLocalizer);
		navigation.setOdoCorrection(odoCorrect);
		
		// clear the display
		lcd.clear();

		// Press button to start
		lcd.drawString("Press any button", 0, 0);
		lcd.drawString("    to start    ", 0, 1);
		
		Button.waitForAnyPress();

		(new Thread() {
				public void run() {
					/*
					OdometerDisplay odometryDisplay = null;
					try {
						odometryDisplay = new OdometerDisplay(lcd);
					} catch (OdometerExceptions e1) {
						e1.printStackTrace();
					}*/
					Thread odoThread = new Thread(odometer);
					odoThread.start();
					//Thread odoDisplayThread = new Thread(odometryDisplay);
					//odoDisplayThread.start();
					
					//Step 2: Localize	
					usLocalizer.localize();
					lightLocalizer.localize();
					
					//Set x, y, theta based on corner
					navigation.initializeXYT(corner);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Sound.beep();
					Sound.beep();
					Sound.beep();
					navigation.turnTo(0);
					
					
					lcd.clear();
					navigation.moveThroughTunnel(Tunnel_LL_x, Tunnel_LL_y, Tunnel_UR_x, Tunnel_UR_y, Island_LL_x, Island_LL_y, Island_UR_x, Island_UR_y);
					
				}
		}).start();
		
		Button.waitForAnyPress(); 
		System.exit(0);
	}
	
}
