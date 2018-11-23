package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerDisplay;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import ca.mcgill.ecse211.odometer.OdometryCorrection;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

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
	public static Port lightPortRight = LocalEV3.get().getPort("S1");//right one when robot is facing away from you
	public static Port colourPort = LocalEV3.get().getPort("S4");//high light sensor for rings
	public static Port lightPortLeft = LocalEV3.get().getPort("S3");//left one when robot is facing away from you
	
	public static LightPoller lightPollerRight  = new LightPoller(lightPortRight);
	public static UltrasonicPoller usPoller = new UltrasonicPoller(usPort);
	public static LightPoller lightPollerLeft = new LightPoller(lightPortLeft);

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
		
		lightLocalizer = new LightLocalizer(odometer, lightPollerLeft, lightPollerRight, navigation);
		
		odometryCorrection = new OdometryCorrection(lightPollerLeft, lightPollerRight, navigation, odometer);
		
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
		LL_x = 2;		
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
		Tree_x = 6;
		Tree_y = 6;
		
		Button.waitForAnyPress();
		
		(new Thread() {
			public void run() {
				OdometerDisplay odometryDisplay = null;
				try {
					odometryDisplay = new OdometerDisplay(lcd);
				} catch (OdometerExceptions e1) {}
				
				Thread odoThread = new Thread(odometer);
				odoThread.start();
				Thread odoDisplayThread = new Thread(odometryDisplay);
				odoDisplayThread.start();
				
				
				//Step 2: Localize	
//				navigation.travelForward(10);
				usLocalizer.localize();
				lightLocalizer.localize();
			

				
				//Set x, y, theta based on corner
				navigation.initializeXYT(corner);
				Sound.beep();
				Sound.beep();
				Sound.beep();
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				
				
				//navigation.travelTo(2, 2);
				navigation.moveThroughTunnel(Tunnel_LL_x, Tunnel_LL_y, Tunnel_UR_x, Tunnel_UR_y, Island_LL_x, Island_LL_y, Island_UR_x, Island_UR_y);
				navigation.travelToTree(Tree_x, Tree_y);
//				odometryCorrection.coordinateCorrection();
//				

			}
		}).start();
		
		Button.waitForAnyPress(); 
		System.exit(0);
		
		//Localize
		
		//Navigate to tunnel and through tunnel
		
		//Navigate to tree
		
		//Grabs rings
		
		//Navigate to tunnel and through tunnel
		
		//Navigate back to beginning
		
	}
	
}
