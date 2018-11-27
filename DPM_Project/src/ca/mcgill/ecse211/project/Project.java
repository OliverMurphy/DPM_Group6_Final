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
 * This class implements the main control for the project
 * @author Lucy Coyle
 * @author Oliver Murphy
 */

public class Project {
	
	public static Navigation navigation;
	
	public static final double WHEEL_RAD = 2.1;
	public static final double TRACK = 11.2;//11.2 or was 10.3
	
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	private static final Port usPort = LocalEV3.get().getPort("S2");
	public static Port lightPortRight = LocalEV3.get().getPort("S1");
	public static Port colourPort = LocalEV3.get().getPort("S4");
	public static Port lightPortLeft = LocalEV3.get().getPort("S3");
	
	public static LightPoller lightPollerRight  = new LightPoller(lightPortRight);
	public static UltrasonicPoller usPoller = new UltrasonicPoller(usPort);
	public static LightPoller lightPollerLeft = new LightPoller(lightPortLeft);
	
	public static EV3ColorSensor colourSensor = new EV3ColorSensor(colourPort);
	public static SampleProvider colourSensorValues = colourSensor.getRGBMode();
	static float[] colourSensorData = new float[3];

	public static final TextLCD lcd = LocalEV3.get().getTextLCD();
	
	public static Odometer odometer;

	public static UltrasonicLocalizer usLocalizer;
	public static LightLocalizer lightLocalizer;
	
	public static String teamColour;
	public static int corner;		
	public static int UR_x;		
	public static int LL_x;		
	public static int UR_y;		
	public static int LL_y;		
	public static int Island_UR_x;	
	public static int Island_LL_x;
	public static int Island_UR_y;
	public static int Island_LL_y;
	public static int Tunnel_UR_x;
	public static int Tunnel_LL_x;
	public static int Tunnel_UR_y;
	public static int Tunnel_LL_y;
	public static int Tree_x;
	public static int Tree_y;
	
	public static OdometryCorrection odometryCorrection;
	
	/**
	 * This main method implements the logic for the project
	 * @throws OdometerExceptions 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws OdometerExceptions, InterruptedException {
		//Step 1: Receive parameters from game controller
		odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);

		navigation = new Navigation(leftMotor, rightMotor, TRACK, WHEEL_RAD, odometer);
		
		usLocalizer = new UltrasonicLocalizer(odometer, usPoller, navigation);
		
		lightLocalizer = new LightLocalizer(lightPollerLeft, lightPollerRight, navigation);
		
		odometryCorrection = new OdometryCorrection(navigation, odometer, lightLocalizer);
		
		navigation.setOdoCorrection(odometryCorrection);
		
		
		// clear the display
		lcd.clear();

		// Press button to start
		lcd.drawString("Press any button", 0, 0);
		lcd.drawString("    to start    ", 0, 1);
		
		//Button.waitForAnyPress();
		
//		WiFiParameters p = new WiFiParameters();
//		p.getParameters();
		
//		if(p.redTeam == 6) {
//			teamColour = "red";
//			corner = p.redCorner;		
//			UR_x = p.Red_UR_x;		
//			LL_x = p.Red_LL_x;		
//			UR_y = p.Red_UR_y;		
//			LL_y = p.Red_LL_y;		
//			Tunnel_UR_x = p.TNR_UR_x;
//			Tunnel_LL_x = p.TNR_LL_x;
//			Tunnel_UR_y = p.TNR_UR_y;
//			Tunnel_LL_y = p.TNR_LL_y;
//			Tree_x = p.TR_x;
//			Tree_y = p.TR_y;
//			
//		}
//		else {
//			teamColour = "green";
//			corner = p.greenCorner;		
//			UR_x = p.Green_UR_x;		
//			LL_x = p.Green_LL_x;		
//			UR_y = p.Green_UR_y;		
//			LL_y = p.Green_LL_y;	
//			Tunnel_UR_x = p.TNG_UR_x;
//			Tunnel_LL_x = p.TNG_LL_x;
//			Tunnel_UR_y = p.TNG_UR_y;
//			Tunnel_LL_y = p.TNG_LL_y;
//			Tree_x = p.TG_x;
//			Tree_y = p.TG_y;
//		}
//		
//		Island_UR_x = p.Island_UR_x;	
//		Island_LL_x = p.Island_LL_x;
//		Island_UR_y = p.Island_UR_y;
//		Island_LL_y = p.Island_LL_y;
		
		teamColour = "green";
		corner = 0;		
		UR_x = 8;		
		LL_x = 0;		
		UR_y = 3;		
		LL_y = 0;		
		Island_UR_x = 6;	
		Island_LL_x = 0;
		Island_UR_y = 8;
		Island_LL_y = 5;
		Tunnel_UR_x = 3;
		Tunnel_LL_x = 2;
		Tunnel_UR_y = 5;
		Tunnel_LL_y = 3;
		Tree_x = 5;
		Tree_y = 7;
		
		Button.waitForAnyPress();
		
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
				
				
				navigation.moveThroughTunnel(Tunnel_LL_x, Tunnel_LL_y, Tunnel_UR_x, Tunnel_UR_y, Island_LL_x, Island_LL_y, Island_UR_x, Island_UR_y);

				navigation.travelToTree(Tree_x, Tree_y, Island_LL_x, Island_LL_y, Island_UR_x, Island_UR_y);
			
				GetRing g = new GetRing(colourSensorValues, colourSensorData, navigation);
				g.grabRing();
				
				//Return to start
				navigation.moveThroughTunnel(Tunnel_LL_x, Tunnel_LL_y, Tunnel_UR_x, Tunnel_UR_y, LL_x, LL_y, UR_x, UR_y);

				navigation.moveToStartingCorner(corner);

			}
		}).start();
		
		Button.waitForAnyPress(); 
		System.exit(0);
		
	}
	
}
