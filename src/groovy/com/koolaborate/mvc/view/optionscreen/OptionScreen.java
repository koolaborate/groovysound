package com.koolaborate.mvc.view.optionscreen;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.koolaborate.model.Settings;
import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.util.LocaleMessage;

import plug.engine.ui.swing.firefoxstyle.UpdateDialog;

/***********************************************************************************
 * OptionScreen                                                                    *
 ***********************************************************************************
 * An option window to enable the user to specify application wide settings.       *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.01                                                                   *
 ***********************************************************************************
 * This file is part of VibrantPlayer.                                             *
 *                                                                                 *
 *  VibrantPlayer is free software: you can redistribute it and/or modify          *
 *  it under the terms of the Lesser GNU General Public License as published by    *
 *  the Free Software Foundation, either version 3 of the License, or              *
 *  (at your option) any later version.                                            *
 *                                                                                 *
 *  VibrantPlayer is distributed in the hope that it will be useful,               *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of                 *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Lesser            *
 *  GNU General Public License for more details.                                   *
 *                                                                                 *
 *  You should have received a copy of the Lesser GNU General Public License       *
 *  along with VibrantPlayer. If not, see <http://www.gnu.org/licenses/>.          *
 ***********************************************************************************/
public class OptionScreen extends JPanel
{
	private Settings s;
	private JButton searchNow;
	
	/**
	 * Constructor.
	 * 
	 * @param window reference to the main window
	 */
	public OptionScreen(final MainWindow window)
	{
		this.s = window.getSettings();
		
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		TitlePanel title1 = new TitlePanel(LocaleMessage.getString("options.graphics"), new ImageIcon(getClass().getResource("/images/settings_graphics.png")));
		add(title1);
		
		JCheckBox useGraphixAcc = new JCheckBox(LocaleMessage.getString("options.enable_d3d"));
		useGraphixAcc.setSelected(s.isHardwareAccellerated());
		useGraphixAcc.setOpaque(false);
		useGraphixAcc.setBorder(new EmptyBorder(0, 68, 0, 0));
		useGraphixAcc.setAlignmentX(Component.LEFT_ALIGNMENT);
		useGraphixAcc.setAlignmentY(Component.TOP_ALIGNMENT);
		useGraphixAcc.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				JCheckBox source = (JCheckBox) e.getSource();
				window.getSettings().setHardwareAccellerated(source.isSelected());
			}
		});
		add(useGraphixAcc);
		
		TitlePanel title2 = new TitlePanel(LocaleMessage.getString("options.sound"), new ImageIcon(getClass().getResource("/images/settings_sound.png")));
		add(title2);
		
		JPanel sound = new JPanel(new GridBagLayout());
		sound.setOpaque(false);
		sound.setAlignmentX(Component.LEFT_ALIGNMENT);
		sound.setAlignmentY(Component.TOP_ALIGNMENT);
		sound.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 68, 0, 0);
		
		JLabel loudness = new JLabel(LocaleMessage.getString("options.volume") + ":");
		sound.add(loudness, gbc);
		
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.gridx = 1;
		
		JLabel softLabel = new JLabel(new ImageIcon(getClass().getResource("/images/soft.png")));
		sound.add(softLabel, gbc);
		
		gbc.gridx = 2;
		
		JSlider loudnessSlider = new JSlider(0, 10);
		loudnessSlider.setOpaque(false);
		loudnessSlider.setPreferredSize(new Dimension(100, 20));
		loudnessSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();
				float newVol = source.getValue() / 10.0f;
				window.getPlayerPanel().setVolumeAndUpdateSlider(newVol);
			}
		});
		loudnessSlider.setValue((int)(s.getVolume() * 10));
		sound.add(loudnessSlider, gbc);
		
		gbc.weightx = 1.0f;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 3;
		
		JLabel loudLabel = new JLabel(new ImageIcon(getClass().getResource("/images/loud.png")));
		loudLabel.setHorizontalAlignment(SwingConstants.LEFT);
		sound.add(loudLabel, gbc);
		
		gbc.weightx = 0.0f;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 68, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		
		JLabel balance = new JLabel(LocaleMessage.getString("options.balance") + ":");
		sound.add(balance, gbc);
		
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.gridx = 1;
		
		JLabel leftLabel = new JLabel(new ImageIcon(getClass().getResource("/images/left.png")));
		sound.add(leftLabel, gbc);
		
		gbc.gridx = 2;
		
		JSlider balanceSlider = new JSlider(-10, 10);
		balanceSlider.setOpaque(false);
		balanceSlider.setPreferredSize(new Dimension(100, 20));
		balanceSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();
				float newPan = source.getValue() / 10.0f;
				window.getPlayerPanel().setPlayerBalance(newPan);
			}
		});
		balanceSlider.setValue((int)(s.getBalance() * 10));
		sound.add(balanceSlider, gbc);
		
		gbc.weightx = 1.0f;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 3;
		
		JLabel rightLabel = new JLabel(new ImageIcon(getClass().getResource("/images/right.png")));
		rightLabel.setHorizontalAlignment(SwingConstants.LEFT);
		sound.add(rightLabel, gbc);
		
		add(sound);
		
		TitlePanel title3 = new TitlePanel(LocaleMessage.getString("options.plugins"), new ImageIcon(getClass().getResource("/images/settings_plugins.png")));
		add(title3);
		
		JPanel plugins = new JPanel(new GridBagLayout());
		plugins.setOpaque(false);
		plugins.setAlignmentX(Component.LEFT_ALIGNMENT);
		plugins.setAlignmentY(Component.TOP_ALIGNMENT);
		plugins.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
		GridBagConstraints gbc2 = new GridBagConstraints();

		gbc2.gridx = 0;
		gbc2.gridy = 0;
		gbc2.weightx = 1.0f;
		gbc2.gridwidth = 3;
		gbc2.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		gbc2.insets = new Insets(0, 68, 0, 0);
		
		// show installed themes and plugins
		LinkLabel showPlugins = new LinkLabel(LocaleMessage.getString("options.show_plugins"));
		showPlugins.setActionThread(new Thread(new Runnable(){
			public void run()
			{
				PluginAndThemeBrowser.showDialog(window);
			}
		}));
		plugins.add(showPlugins, gbc2);
		
		gbc2.gridy = 1;
		
		LinkLabel findPlugins = new LinkLabel(LocaleMessage.getString("options.search_plugins"));
		findPlugins.setActionThread(new Thread(new Runnable(){
			public void run()
			{
				FindPluginBrowser.showDialog();
				
				//TODO just 4 now: this way you can get a hook to the main plugin
//				List<Pluggable> allPluggables = PlugEngine.getInstance().getAllPluggables();
//				for(Pluggable plugin : allPluggables)
//				{
//					if(plugin instanceof MainPlugin)
//					{
//						MainPlugin m = (MainPlugin) plugin;
//						System.out.println(m.getName());
//						System.out.println(m.getVersion());
//						m.getMainwindow().setBounds(m.getMainwindow().getBounds().x - 50, 
//								m.getMainwindow().getBounds().y + 50,
//								m.getMainwindow().getBounds().width,
//								m.getMainwindow().getBounds().height);
//						break;
//					}
//				}
			}
		}));
		plugins.add(findPlugins, gbc2);
		
		gbc2.gridwidth = 1;
		gbc2.weightx = 0.0f;
		gbc2.weighty = 1.0f;
		gbc2.fill = GridBagConstraints.VERTICAL;
		gbc2.gridy = 2;
		gbc2.insets = new Insets(4, 68, 0, 0);
		
		// check for updates during application startup?
		JCheckBox atStart = new JCheckBox(LocaleMessage.getString("options.autoupdate"));
		atStart.setSelected(s.isCheckForUpdatesAtStart());
		atStart.setOpaque(false);
		atStart.setVerticalAlignment(SwingConstants.TOP);
		atStart.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				JCheckBox src = (JCheckBox) e.getSource();
				s.setCheckForUpdatesAtStart(src.isSelected());
				searchNow.setEnabled(!src.isSelected());
			}
		});
		plugins.add(atStart, gbc2);
		
		gbc2.insets = new Insets(0, 4, 0, 0);
		gbc2.gridx = 1;
		gbc2.weighty = 0.0f;
		gbc2.fill = GridBagConstraints.NONE;
		
		// search for updates for the plugins
		searchNow = new JButton(LocaleMessage.getString("options.updatenow"));
		searchNow.setToolTipText(LocaleMessage.getString("options.updatenow_tooltip"));
		searchNow.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				UpdateDialog.showDialog();
			}
		});
		searchNow.setVerticalAlignment(SwingConstants.TOP);
		searchNow.setEnabled(!atStart.isSelected());
		plugins.add(searchNow, gbc2);
		
		add(plugins);
		
		revalidate();
	}
}