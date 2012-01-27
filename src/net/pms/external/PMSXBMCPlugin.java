package net.pms.external;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.pms.dlna.DLNAResource;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.xbmc.folders.VideoFolder;

/**
 * 
 * @author Frans
 * 
 */
public class PMSXBMCPlugin implements AdditionalFolderAtRoot {
	private final static int TYPE_VIDEO = 1;

	private VirtualFolder plugRoot;

	private JButton browseSQLiteFileButt;
	private JButton configMySQLButt;
	private JLabel sqLiteLocationLab;
	private JLabel mySqlLab;
	private JLabel sqLiteLab;
	private JFileChooser sqLiteFileChooser;

	public PMSXBMCPlugin() {
		XBMCLog.info("creating root folder");
		plugRoot = new VirtualFolder(name(), null);

		XBMCLog.info("adding video folder");
		plugRoot.addChild(new VideoFolder("Videos", null));

		XBMCLog.info("adding music folder");
		plugRoot.addChild(new VirtualFolder("Music", null));
	}

	@Override
	public String name() {
		return "PMS XBMC Plug";
	}

	@Override
	public void shutdown() {
	}

	@Override
	public DLNAResource getChild() {
		return plugRoot;
	}

	@Override
	public JComponent config() {
		JPanel confPanel = new JPanel();
		confPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;

		c.gridx = 0;
		c.gridy = 0;
		confPanel.add(getMySqlLab(), c);

		c.gridx = 1;
		c.gridy = 0;
		confPanel.add(getConfigMySQLButt(), c);

		c.gridx = 0;
		c.gridy = 1;
		confPanel.add(getSqLiteLab(), c);

		c.gridx = 1;
		c.gridy = 1;
		confPanel.add(getBrowseSQLiteFileButt(), c);

		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 2;
		confPanel.add(getSqLiteLocationLab(), c);

		return confPanel;
	}

	class ConfigActionListener implements ActionListener {

		private int type;

		public ConfigActionListener(int type) {
			this.type = type;
		}

		public ConfigActionListener() {
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == getBrowseSQLiteFileButt()) {

				int option = getSqLiteFileChooser().showOpenDialog(null);

				if (option == JFileChooser.APPROVE_OPTION) {
					try {
						XBMCLog.info("Choosen: " + getSqLiteFileChooser().getSelectedFile().getCanonicalPath().toString());
						if (getSqLiteFileChooser().getSelectedFile().exists()) {
							String location = getSqLiteFileChooser().getSelectedFile().getCanonicalPath().toString();
							if (type == TYPE_VIDEO) {
								XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_VIDEO_SQLITE, location);
								getSqLiteLocationLab().setText(location);
								getSqLiteLocationLab().repaint();
							}
							XBMCLog.info("Using following DB file: " + location);
						} else {
							XBMCLog.info("Choosen file does not exist");
						}
					} catch (IOException e) {
						XBMCLog.error("Error while getting path of choosen file");
					}
				}
			}
		}
	}

	public JButton getBrowseSQLiteFileButt() {
		if (browseSQLiteFileButt == null) {
			browseSQLiteFileButt = new JButton("Browse");
			browseSQLiteFileButt.addActionListener(new ConfigActionListener(TYPE_VIDEO));
		}
		return browseSQLiteFileButt;
	}

	public JButton getConfigMySQLButt() {
		if (configMySQLButt == null) {
			configMySQLButt = new JButton("Configure");
		}
		return configMySQLButt;
	}

	public JLabel getSqLiteLocationLab() {
		if (sqLiteLocationLab == null) {
			sqLiteLocationLab = new JLabel(XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_SQLITE));
		}
		return sqLiteLocationLab;
	}

	public JLabel getMySqlLab() {
		if (mySqlLab == null) {
			mySqlLab = new JLabel("Configure MySQL");
		}
		return mySqlLab;
	}

	public JLabel getSqLiteLab() {
		if (sqLiteLab == null) {
			sqLiteLab = new JLabel("Configure SQLite");
		}
		return sqLiteLab;
	}

	public JFileChooser getSqLiteFileChooser() {
		if (sqLiteFileChooser == null) {
			sqLiteFileChooser = new JFileChooser(new File(XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_SQLITE)));
			sqLiteFileChooser.setDialogTitle("Locate XBMC Database file");
			sqLiteFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XBMC SQLite Database", "db", "DB"));
		}
		return sqLiteFileChooser;
	}

}
