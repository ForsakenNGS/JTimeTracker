package TimeTracker.Windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import TimeTracker.TimeTracker;
import TimeTracker.Settings.ConfigMain;
import TimeTracker.Syncronisation.Account;
import TimeTracker.Syncronisation.GoogleTasks;

public class SettingsWindow extends JFrame implements ActionListener, ListSelectionListener, WindowListener {
    
	private static final long serialVersionUID = 1L;
	private JTabbedPane					tabbedPane;
	private JPanel						tabPaneGeneral;
	private JPanel						tabPaneSyncronisation;
	private JPanel						syncPanelLeft;
	private JPanel						syncPanelCenter;
	private JList<Account>				syncListAccount;
	private DefaultListModel<Account>	syncListModelAccount;
	private JButton						syncButtonAdd;
	private JPopupMenu					syncMenuAdd;
	private JMenuItem					syncMenuAddGoogleTasks;
	private ConfigMain					configuration;
	
	public SettingsWindow() {
		super("Settings");
		configuration = new ConfigMain();
		this.initTabGeneral();
		this.initTabSyncronisation();
		tabbedPane = new JTabbedPane();
		// TODO tabbedPane.addTab("General", tabPaneGeneral);
		tabbedPane.addTab("Syncronisation", tabPaneSyncronisation);
		this.add(tabbedPane);
		this.addWindowListener(this);
	}
	
	private void initTabGeneral() {
		tabPaneGeneral = new JPanel();
	}
	
	private void initTabSyncronisation() {
		// Left
		syncListModelAccount = new DefaultListModel<Account>();
		syncListAccount = new JList<Account>(syncListModelAccount);
		syncListAccount.addListSelectionListener(this);
		syncButtonAdd = new JButton("Add new ...");
		syncButtonAdd.addActionListener(this);
		syncMenuAddGoogleTasks = new JMenuItem("Google Tasks");
		syncMenuAddGoogleTasks.addActionListener(this);
		syncMenuAdd = new JPopupMenu();
		syncMenuAdd.setInvoker(syncButtonAdd);
		syncMenuAdd.add(syncMenuAddGoogleTasks);
		syncPanelLeft = new JPanel(new BorderLayout());
		syncPanelLeft.add(syncListAccount, BorderLayout.CENTER);
		syncPanelLeft.add(syncButtonAdd, BorderLayout.SOUTH);
		// Center
		syncPanelCenter = new JPanel(new BorderLayout());
		// Main panel
		tabPaneSyncronisation = new JPanel(new BorderLayout());
		tabPaneSyncronisation.add(syncPanelLeft, BorderLayout.WEST);
		tabPaneSyncronisation.add(syncPanelCenter, BorderLayout.CENTER);
	}

	public ConfigMain getConfiguration() {
		return configuration;
	}
	
	public void setAccounts(Vector<Account> accountList) {
		syncListModelAccount.removeAllElements();
		for (Iterator<Account> accountIt = accountList.iterator(); accountIt.hasNext(); ) {
			syncListModelAccount.addElement(accountIt.next());
		}
	}

	public void setConfiguration(ConfigMain config) {
		configuration = config;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(syncMenuAddGoogleTasks)) {
			try {
				GoogleTasks account = new GoogleTasks();
				TimeTracker.getInstance().addAccount(account);
				syncListModelAccount.addElement(account);
				syncPanelCenter.removeAll();
				syncPanelCenter.add(account.getConfiguration(), BorderLayout.CENTER);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (event.getSource().equals(syncButtonAdd)) {
			syncMenuAdd.show(syncButtonAdd, 10, 10);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		if (event.getSource().equals(syncListAccount)) {
			Account account = syncListAccount.getSelectedValue();
			if (account != null) {
				syncPanelCenter.removeAll();
				syncPanelCenter.add(account.getConfiguration(), BorderLayout.CENTER);				
			}
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// Nothing yet
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// Nothing yet
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		TimeTracker.getInstance(new String[]{}).syncTasks();		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// Nothing yet		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// Nothing yet
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// Nothing yet
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// Nothing yet
	}
	
}
