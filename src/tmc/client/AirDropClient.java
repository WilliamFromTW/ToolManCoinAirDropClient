package tmc.client;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class AirDropClient implements ActionListener, ItemListener {

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
	private static String sUid;
	private static String sName;
	private static int iCurrentMin = 0;
	private static int iPrevious = 0;
	private static Vector aMenuVector = new Vector();
	private static Vector aMenuUrlVector = new Vector();
	public static String CMD_REG = "REG";

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

		// "Exit" menu item
		menu.addSeparator();
		menuItem = new MenuItem("Exit");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		iIMAGE = Toolkit.getDefaultToolkit().getImage(ClientGlobal.getProperties(ClientGlobal.PROP_IMAGE));
		ti = new TrayIcon(iIMAGE, "0", menu);
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
		if (s.equalsIgnoreCase("Exit")) {
			_log.info("Exit menu item selected!");
			// System.exit(0);
		} else {
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
					ti.displayMessage("AirDrop Info", userInput, TrayIcon.MessageType.INFO);
				}
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	private void getData2() {
		int bytes_read = 0;
		StringBuffer aReturnData = new StringBuffer();
		try {

			while (true) {

				bytes_read = from_server.read();
				if (bytes_read == -1)
					break;
				// System.out.println(bytes_read);
				if (bytes_read == '\n')
					;
				else if ((bytes_read != '\r')) {
					aReturnData.append((char) bytes_read);
				} else {
					if (aReturnData.toString().length() > 0) {
						ti.displayMessage("Coin Info", aReturnData.toString(), TrayIcon.MessageType.INFO);
						aReturnData.delete(0, aReturnData.length());
					}
				}
			}
		} catch (IOException ee) {
			ee.printStackTrace();
		}

	}
}
