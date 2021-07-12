package tmc.client;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class AirDropClient implements ActionListener, ItemListener  {

	private static final Logger _log = Logger.getLogger(AirDropClient.class.getName());
	SystemTray tray = SystemTray.getSystemTray();
	PopupMenu menu;
	MenuItem menuItem;
	private static TrayIcon ti;
	private static Image iIMAGE;
	private static String HOST;
	private static String REMOTE_PORT;
	private static Socket server;
	private static InputStreamReader from_server;
	private static OutputStreamWriter to_server;
	private static Vector aMenuVector = new Vector();
	private static Vector aMenuUrlVector = new Vector();
	public static String CMD_REG = "REG";
    public static String MENU_EXIT = "Exit";
    public static String MENU_DISABLE_NOTIFICATION = "Disable Notification";
    private boolean bDisableNotification = false;
    
	int iFormNum = 0;

	// */
	public AirDropClient() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		menu = new PopupMenu("Menu");
		String[] sPropMenuItem = (ClientGlobal.getProperties(ClientGlobal.MENU_ITEM)).split(",");
		for (int i = 0; i < sPropMenuItem.length; i++) {
			menuItem = new MenuItem(sPropMenuItem[i]);
			menuItem.addActionListener(this);
			menu.add(menuItem);
			aMenuVector.add(sPropMenuItem[i]);
		}

		String[] sMenuItemURL = (ClientGlobal.getProperties(ClientGlobal.MENU_ITEM_URL)).split(",");
		for (int i = 0; i < sMenuItemURL.length; i++) {
			aMenuUrlVector.add(sMenuItemURL[i]);
		}

		CheckboxMenuItem checkItem = new CheckboxMenuItem (MENU_DISABLE_NOTIFICATION);
		checkItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb1Id = e.getStateChange();
                if (cb1Id == ItemEvent.SELECTED){
        			bDisableNotification = true;     
                } else {
                	bDisableNotification = false;
                }
    			System.out.println("bDisableNotification="+bDisableNotification);
            }
        });
		menu.add(checkItem);
		// "Exit" menu item
		menu.addSeparator();
		menuItem = new MenuItem(MENU_EXIT);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		iIMAGE = Toolkit.getDefaultToolkit().getImage(ClientGlobal.getProperties(ClientGlobal.PROP_IMAGE));
		ti = new TrayIcon(iIMAGE, "unpaid coin : 0", menu);
		ti.setImageAutoSize(true);

		try {
			tray.add(ti);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// Returns just the class name -- no package info.
	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");

		return classString.substring(dotIndex + 1);
	}

	
	public void actionPerformed(ActionEvent e) {
		MenuItem source = (MenuItem) (e.getSource());
		String s = source.getLabel();
		//System.out.println("getLabel = "+s+",Action Command = "+ e.getActionCommand());
		if (s.equalsIgnoreCase(MENU_EXIT)) {
			_log.info("Exit menu item selected!");
			System.exit(-1);
			Runtime.getRuntime().halt(0);
		} else if(s.equalsIgnoreCase(MENU_DISABLE_NOTIFICATION) ){
			System.out.println("action command = "+ e.getActionCommand());
		}else {
			for (int i = 0; i < aMenuVector.size(); i++) {
				if (s.equals(aMenuVector.get(i).toString()))
					try {
						String sCmd = ClientGlobal.getProperties(ClientGlobal.EXPLORER_NAME) + " "
								+ aMenuUrlVector.get(i).toString();
						Runtime.getRuntime().exec(sCmd);
					} catch (Exception ex) {
						_log.warning(ex.getMessage());
					}
			}

		}
	}

	public void itemStateChanged(ItemEvent e) {
		MenuItem source = (MenuItem) (e.getSource());
		String s = "Item event detected." + "\n" + "    Event source: " + source.getLabel() + " (an instance of "
				+ getClassName(source) + ")" + "\n" + "    New state: "
				+ ((e.getStateChange() == ItemEvent.SELECTED) ? "selected" : "unselected");

		_log.info(s);

	}

	public void run() {
		getData();
		// ti.setImage(iIMAGE);

	}

	public static void main(String[] args) {
		AirDropClient tT = new AirDropClient();
		HOST = ClientGlobal.getProperties(ClientGlobal.PROP_AIRDROP_SERVER);
		REMOTE_PORT = ClientGlobal.getProperties(ClientGlobal.PROP_AIRDROP_SERVER_PORT);
		int iMaxFailNumber = 10;
		int iFailNumber = 0;
		while (true) {
			if (iFailNumber > iMaxFailNumber) {
				System.exit(-1);
				return;
			}
			try {
				server = new Socket(HOST.trim(), Integer.parseInt(REMOTE_PORT));
                server.setKeepAlive(true);
				from_server = new InputStreamReader(server.getInputStream(), ClientGlobal.CONN_ENCODE);
				to_server = new OutputStreamWriter(server.getOutputStream(), ClientGlobal.CONN_ENCODE);

				iFailNumber = 0;
				tT.reg(ClientGlobal.getProperties(ClientGlobal.PROP_WALLET),
						ClientGlobal.getProperties(ClientGlobal.PROP_WALLET_PUBLIC_KEY));
				tT.run();
				System.out.println("try connect after 30s");
				try {
					Thread.sleep(30000);
				} catch (Exception ee) {

				}
				
			} catch (Exception ex) {
				// ti.setToolTip( "disconnected from server" );
				System.out.println("socket disconnect");
				ex.printStackTrace();
				server = null;
				try {
					Thread.sleep(30000);
				} catch (Exception ee) {

				}
				iFailNumber++;
			}
		}
	}

	private void reg(String sWallet, String sWalletPublicKey) {
		try {
			to_server.write(CMD_REG + sWallet + "," + sWalletPublicKey + "\r\n");
			to_server.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
			_log.warning(ex.getMessage());
		}
	}

	private void getData() {

		try {
			BufferedReader in = new BufferedReader(from_server);
			String userInput = null;
			while ((userInput = in.readLine()) != null) {
				
				if (userInput.length() > 0) {
					if( userInput.indexOf(ClientGlobal.CMD_UNPAID_COIN)!=-1 ) {
						ti.setToolTip("unpaid airdrop : "+userInput.substring(ClientGlobal.CMD_UNPAID_COIN.length()));
					}
					else {
						if( !bDisableNotification)
					  ti.displayMessage("AirDrop", userInput, TrayIcon.MessageType.INFO);
					}
				}
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

}
