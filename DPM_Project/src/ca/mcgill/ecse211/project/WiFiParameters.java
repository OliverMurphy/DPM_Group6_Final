package ca.mcgill.ecse211.project;

import java.util.Map;


import ca.mcgill.ecse211.WiFiClient.WifiConnection;

/**
 * Example class using WifiConnection to communicate with a server and receive data concerning the
 * competition such as the starting corner the robot is placed in.
 * 
 * Keep in mind that this class is an **example** of how to use the WiFi code; you must use the
 * WifiConnection class yourself in your own code as appropriate. In this example, we simply show
 * how to get and process different types of data.
 * 
 * There are two variables you **MUST** set manually before trying to use this code.
 * 
 * 1. SERVER_IP: The IP address of the computer running the server application. This will be your
 * own laptop, until the beta beta demo or competition where this is the TA or professor's laptop.
 * In that case, set the IP to 192.168.2.3.
 * 
 * 2. TEAM_NUMBER: your project team number
 * 
 * Note: We System.out.println() instead of LCD printing so that full debug output (e.g. the very
 * long string containing the transmission) can be read on the screen OR a remote console such as
 * the EV3Control program via Bluetooth or WiFi. You can disable printing from the WiFi code via
 * ENABLE_DEBUG_WIFI_PRINT (below).
 * 
 * 
 * This class was taken from the one given on mycourses. The changes made are the main method was converted to a 
 * public method that could be called from elsewhere. Also, it now gathers all the parameters available and sets
 * them to variables which can be accessed.
 * 
 * @author Michael Smith, Tharsan Ponnampalam
 * 
 * @author Lucy
 *
 */
public class WiFiParameters {

  // ** Set these as appropriate for your team and current situation **
  private static final String SERVER_IP = "192.168.2.39";//192.168.2.2
  private static final int TEAM_NUMBER = 6;
  
  public int greenTeam;
  public int redTeam;
  public int greenCorner;	
  public int Green_UR_x;		
  public int Green_LL_x;		
  public int Green_UR_y;		
  public int Green_LL_y;	
  public int redCorner;	
  public int Red_UR_x;		
  public int Red_LL_x;		
  public int Red_UR_y;		
  public int Red_LL_y;	
  public int Island_UR_x;	
  public int Island_LL_x;
  public int Island_UR_y;
  public int Island_LL_y;
  public int TNG_UR_x;
  public int TNG_LL_x;
  public int TNG_UR_y;
  public int TNG_LL_y;
  public int TNR_UR_x;
  public int TNR_LL_x;
  public int TNR_UR_y;
  public int TNR_LL_y;
  public int TG_x;
  public int TG_y;
  public int TR_x;
  public int TR_y;

  // Enable/disable printing of debug info from the WiFi class
  private static final boolean ENABLE_DEBUG_WIFI_PRINT = false;

  @SuppressWarnings("rawtypes")
  public void getParameters() {


    // Initialize WifiConnection class
    WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);

    // Connect to server and get the data, catching any errors that might occur
    try {
      /*
       * getData() will connect to the server and wait until the user/TA presses the "Start" button
       * in the GUI on their laptop with the data filled in. Once it's waiting, you can kill it by
       * pressing the upper left hand corner button (back/escape) on the EV3. getData() will throw
       * exceptions if it can't connect to the server (e.g. wrong IP address, server not running on
       * laptop, not connected to WiFi router, etc.). It will also throw an exception if it connects
       * but receives corrupted data or a message from the server saying something went wrong. For
       * example, if TEAM_NUMBER is set to 1 above but the server expects teams 17 and 5, this robot
       * will receive a message saying an invalid team number was specified and getData() will throw
       * an exception letting you know.
       */
      Map data = conn.getData();
      
      //Get all parameters
      this.redTeam = ((Long) data.get("RedTeam")).intValue();
      this.greenTeam = ((Long) data.get("GreenTeam")).intValue();
      this.TR_x = ((Long) data.get("TR_x")).intValue();
      this.Green_UR_x= ((Long) data.get("Green_UR_x")).intValue();		
      this.Green_LL_x= ((Long) data.get("Green_LL_x")).intValue();		
      this.Green_UR_y= ((Long) data.get("Green_UR_y")).intValue();		
      this.Green_LL_y= ((Long) data.get("Green_LL_y")).intValue();	
      this.redCorner= ((Long) data.get("RedCorner")).intValue();
      this.greenCorner= ((Long) data.get("GreenCorner")).intValue();	
      this.Red_UR_x= ((Long) data.get("Red_UR_x")).intValue();		
      this.Red_LL_x= ((Long) data.get("Red_LL_x")).intValue();		
      this.Red_UR_y= ((Long) data.get("Red_UR_y")).intValue();		
      this.Red_LL_y= ((Long) data.get("Red_LL_y")).intValue();	
      this.Island_UR_x= ((Long) data.get("Island_UR_x")).intValue();	
      this.Island_LL_x= ((Long) data.get("Island_LL_x")).intValue();
      this.Island_UR_y= ((Long) data.get("Island_UR_y")).intValue();
      this.Island_LL_y= ((Long) data.get("Island_LL_y")).intValue();
      
      this.TNG_UR_x= ((Long) data.get("TNG_UR_x")).intValue();
      this.TNG_UR_y= ((Long) data.get("TNG_UR_y")).intValue();
      this.TNG_LL_y= ((Long) data.get("TNG_LL_y")).intValue();
      this.TNG_LL_x =  ((Long) data.get("TNG_LL_x")).intValue();
      this.TNR_UR_x= ((Long) data.get("TNR_UR_x")).intValue();
      this.TNR_LL_x= ((Long) data.get("TNR_LL_x")).intValue();
      this.TNR_UR_y= ((Long) data.get("TNR_UR_y")).intValue();
      this.TNR_LL_y= ((Long) data.get("TNR_LL_y")).intValue();
      this.TG_x= ((Long) data.get("TG_x")).intValue();
      this.TG_y= ((Long) data.get("TG_y")).intValue();
      this.TR_y= ((Long) data.get("TR_y")).intValue();
      

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }

  }
}
