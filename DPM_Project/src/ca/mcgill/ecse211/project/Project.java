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
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * This class implements the main control for the project
 * @author Lucy Coyle
 */

public class Project {
	
	public static LightPoller lightPollerLeft;
	public static LightPoller lightPollerRight;
	public static UltrasonicPoller usPoller;
	public static Navigation navigation;
	
	public static final double WHEEL_RAD = 2.1;
	public static final double TRACK = 10.3;
	
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

	
	private static final Port usPort = LocalEV3.get().getPort("S1");
	public static Port lightPort = LocalEV3.get().getPort("S2");

	public static final TextLCD lcd = LocalEV3.get().getTextLCD();
	
	public static Odometer odometer;

	public static EV3ColorSensor lightSensor = new EV3ColorSensor(lightPort);
	public static SampleProvider lightSensorValues = lightSensor.getRedMode();
	static float[] lightSensorData = new float[lightSensor.sampleSize()];
	
	public static SensorModes usSensor = new EV3UltrasonicSensor(usPort); 
	public static SampleProvider usDistance = usSensor.getMode("Distance"); 
	public static float[] usData = new float[usDistance.sampleSize()];
	
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
		
		Button.waitForAnyPress();
		
		WiFiParameters p = new WiFiParameters();
		p.getParameters();
		
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
		UR_x = 15;
		LL_x = 10;	
		UR_y = 4;
		LL_y = 0;
		Tunnel_UR_x = 11;
		Tunnel_LL_x = 10; 
		Tunnel_UR_y = 5;
		Tunnel_LL_y = 3;
		Tree_x = 13;
		Tree_y = 7;
		Island_UR_x = 6;
		Island_LL_x = 15;
		Island_UR_y = 5;
		Island_LL_y = 9;
		
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
				usLocalizer.localize();
				lightLocalizer.localize();
			
				
				//Set x, y, theta based on corner
				navigation.initializeXYT(corner);
				Sound.beep();
				Sound.beep();
				Sound.beep();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
					
				navigation._travelToX(2);
				navigation._travelToY(2);
				odometryCorrection.coordinateCorrection();
				

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
