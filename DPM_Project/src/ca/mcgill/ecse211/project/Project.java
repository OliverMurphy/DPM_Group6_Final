package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import ca.mcgill.ecse211.odometer.OdometryCorrection;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

/**
 * This class implements the main control for the project. It consists of one main method that is run for the demo.
 * It is where all the ports and variables for the demo run are set.
 * 
 * In the main method, first the navigation, odometer, odemetry correction and localizer objects are created. Then
 * the program waits for a button press, after which the game parameters are received from the server. Then, the main
 * thread begins. This thread can be stopped at any time by a button press. The second thread, the odometer is started.
 * Now the robot localizes and the x, y and theta values in the odometer are set based on the starting corner. The 
 * robot then beeps three times to indicate that localization is over. Then the robot moves through the tunnel, once 
 * out of the tunnel, it travels to the tree and tries to pick up one ring. Once the ring is gathered, the robot moves
 * back through the tunnel, returns to the starting corner and beeps 5 times signaling the end of the demo.
 * 
 * @author Lucy Coyle
 * @author Oliver Murphy
 */

public class Project {
	
	private static Navigation navigation;
	
	private static final double WHEEL_RAD = 2.1;
	private static final double TRACK = 11.2;
	private static final int TEAM_NUM = 6;
	
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	private static final Port usPort = LocalEV3.get().getPort("S2");
	private static Port lightPortRight = LocalEV3.get().getPort("S1");
	private static Port colourPort = LocalEV3.get().getPort("S4");
	private static Port lightPortLeft = LocalEV3.get().getPort("S3");
	
	private static LightPoller lightPollerRight  = new LightPoller(lightPortRight);
	private static UltrasonicPoller usPoller = new UltrasonicPoller(usPort);
	private static LightPoller lightPollerLeft = new LightPoller(lightPortLeft);
	
	private static EV3ColorSensor colourSensor = new EV3ColorSensor(colourPort);
	private static SampleProvider colourSensorValues = colourSensor.getRGBMode();
	private static float[] colourSensorData = new float[3];

	private static final TextLCD lcd = LocalEV3.get().getTextLCD();
	
	private static Odometer odometer;

	private static UltrasonicLocalizer usLocalizer;
	private static LightLocalizer lightLocalizer;
	
	private static int corner;		
	private static int UR_x;		
	private static int LL_x;		
	private static int UR_y;		
	private static int LL_y;		
	private static int Island_UR_x;	
	private static int Island_LL_x;
	private static int Island_UR_y;
	private static int Island_LL_y;
	private static int Tunnel_UR_x;
	private static int Tunnel_LL_x;
	private static int Tunnel_UR_y;
	private static int Tunnel_LL_y;
	private static int Tree_x;
	private static int Tree_y;
	
	private static OdometryCorrection odometryCorrection;
	
	/**
	 * This main method implements the logic for the project demo
	 * @throws OdometerExceptions 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws OdometerExceptions, InterruptedException {
		
		odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);

		navigation = new Navigation(leftMotor, rightMotor, TRACK, WHEEL_RAD, odometer);
		
		usLocalizer = new UltrasonicLocalizer(odometer, usPoller, navigation);
		
		lightLocalizer = new LightLocalizer(lightPollerLeft, lightPollerRight, navigation);
		
		odometryCorrection = new OdometryCorrection(navigation, odometer, lightLocalizer);
		
		navigation.setOdoCorrection(odometryCorrection);
	
		lcd.clear();

		lcd.drawString("Press any button", 0, 0);
		lcd.drawString("    to start    ", 0, 1);
		
		Button.waitForAnyPress();
		
		//Step 1: Get WiFi Parameters
		WiFiParameters p = new WiFiParameters();
		p.getParameters();
		
		if(p.redTeam == TEAM_NUM) {
			corner = p.redCorner;		
			UR_x = p.Red_UR_x;		
			LL_x = p.Red_LL_x;		
			UR_y = p.Red_UR_y;		
			LL_y = p.Red_LL_y;		
			Tunnel_UR_x = p.TNR_UR_x;
			Tunnel_LL_x = p.TNR_LL_x;
			Tunnel_UR_y = p.TNR_UR_y;
			Tunnel_LL_y = p.TNR_LL_y;
			Tree_x = p.TR_x;
			Tree_y = p.TR_y;
			
		}
		else {
			corner = p.greenCorner;		
			UR_x = p.Green_UR_x;		
			LL_x = p.Green_LL_x;		
			UR_y = p.Green_UR_y;		
			LL_y = p.Green_LL_y;	
			Tunnel_UR_x = p.TNG_UR_x;
			Tunnel_LL_x = p.TNG_LL_x;
			Tunnel_UR_y = p.TNG_UR_y;
			Tunnel_LL_y = p.TNG_LL_y;
			Tree_x = p.TG_x;
			Tree_y = p.TG_y;
		}
		
		Island_UR_x = p.Island_UR_x;	
		Island_LL_x = p.Island_LL_x;
		Island_UR_y = p.Island_UR_y;
		Island_LL_y = p.Island_LL_y;
		
		
		(new Thread() {
			public void run() {

 				Thread odoThread = new Thread(odometer);
 				odoThread.start();

				//Step 2: Localize	
				usLocalizer.localize();
				lightLocalizer.localize();
				
				navigation.initializeXYT(corner);
				Sound.beep();
				Sound.beep();
				Sound.beep();
				
				//Step 3: Move through tunnel
				navigation.moveThroughTunnel(Tunnel_LL_x, Tunnel_LL_y, Tunnel_UR_x, Tunnel_UR_y, Island_LL_x, Island_LL_y, Island_UR_x, Island_UR_y);

				//Step 4: Go To Tree
				navigation.travelToTree(Tree_x, Tree_y);
			
				//Step 5: Grab a ring and detect colour
				GetRing g = new GetRing(colourSensorValues, colourSensorData, navigation);
				g.getRing(Tree_x, Tree_y);
				
				//Step 6: Return through tunnel
				navigation.moveThroughTunnel(Tunnel_LL_x, Tunnel_LL_y, Tunnel_UR_x, Tunnel_UR_y, LL_x, LL_y, UR_x, UR_y);

				//Step 7: Return to start
				navigation.moveToStartingCorner(corner);
				Sound.beep();
				Sound.beep();
				Sound.beep();
				Sound.beep();
				Sound.beep();

			}
		}).start();
		
		Button.waitForAnyPress(); 
		System.exit(0);
		
	}
	
}
