package tmc.client;

import java.util.Properties;
import java.util.logging.*;

import java.io.*;

public class ClientGlobal{

  public static final String FILE_ENCODE = "UTF-8";
  public static final String CONN_ENCODE = FILE_ENCODE;
  public static final String CHECK_CYCLE = "CHECK_CYCLE";
  public static final String MENU_ITEM = "MENU_ITEM";
  public static final String MENU_ITEM_URL = "MENU_ITEM_URL";
  public static final String SERVER_CONFIG_FILE = "client.properties";
  public static final String EXPLORER_NAME = "EXPLORER_NAME";
  public static final String PROP_IMAGE = "IMAGE";
  public static final String PROP_WALLET = "WALLET";
  public static final String PROP_WALLET_PUBLIC_KEY="WALLET_PUBLIC_KEY";
  public static final String PROP_AIRDROP_SERVER="AIRDROP_SERVER";
  public static final String PROP_AIRDROP_SERVER_PORT="AIRDROP_SERVER_PORT";
  public static final String PROP_AIRDROP_TRANSFER_COIN_NUMBER="AIRDROP_TRANSFER_COIN_NUMBER";
  public static final String CMD_UNPAID_COIN = "UNPAID_COIN";
  

  private static final Logger _log = Logger.getLogger(ClientGlobal.class.getName());
  private static Properties serverSettings = null;

  public static String getProperties(String sKey){
    if( serverSettings == null )
      loadConfig();
    //_log.info( serverSettings.getProperty(sKey) );
    return serverSettings.getProperty(sKey);
  }

  private static void loadConfig(){
    try{
      serverSettings = new Properties();
//      InputStreamReader ir = new InputStreamReader(ClientGlobal.class.getClassLoader().getResourceAsStream(SERVER_CONFIG_FILE),FILE_ENCODE);
      InputStreamReader ir = new InputStreamReader(new FileInputStream(new File(SERVER_CONFIG_FILE)),FILE_ENCODE);
 	  serverSettings.load(ir);
 	  ir.close();
 	}catch(Exception ex){
 	  _log.warning("read config error"+ex.getMessage());
    }
  }
}