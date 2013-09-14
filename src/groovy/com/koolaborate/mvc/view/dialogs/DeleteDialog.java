package com.koolaborate.mvc.view.dialogs;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.koolaborate.mvc.view.common.VariableLineBorder;
import com.koolaborate.util.LocaleMessage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/***********************************************************************************
 * DeleteDialog                                                                    *
 ***********************************************************************************
 * A class to show various delete dialogs in Windows(R) Vista(R) style.            *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.2                                                                    *
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
public class DeleteDialog extends JDialog implements ActionListener 
{
	private static final long serialVersionUID = -6068666222344090944L;
	public final static String YES_OPTION = "yes";
	public final static String NO_OPTION  = "no";
	public final static String NO_ACTION  = "dispose";
	
	/** the dialog instance */
	private static DeleteDialog dialog;
	
	/** the selected yes or no option of a confirmation dialog */
	public boolean yesSelected = false;
	
	/** whether the files shall also be deleted from the hard disk */
	public boolean delFilesSelected = false;
	
	/** a label that shows the appropriate icon image */
	private JLabel iconLabel;
	/** a text area for the messages */
	private JTextArea messages;
	/** a text area that shows the title description of the message(s) */
	private JTextArea titleText;
		
	/** the different fonts */
	private static Font titleTextFont   = new Font("Tahoma", Font.PLAIN, 16);
	private static Font messageTextFont = new Font("Tahoma", Font.PLAIN, 11);
	
	/** the colors */
	private static Color background     = Color.WHITE;
	private static Color titleTextColor = new Color(0, 90, 210);
	
	private Icon delIco = new ImageIcon(getClass().getResource("/images/trash.png"));
	
	 /**
     * Constructor.
     * 
     * @param frame the parent frame
     * @param type the dialog type
     */
    private DeleteDialog(JFrame frame) 
    {
		super(frame);
		initGUI();
	}
    
	/**
	 * Shows a confirmation dialog with the options to answer with yes or no.
	 */
	public static DeleteDialog showDeleteAlbumDialog()
	{
		JFrame frame = new JFrame();
		dialog = new DeleteDialog(frame);
		
		// set the title
		dialog.setTitle(LocaleMessage.getInstance().getString("question.delete_album_title")); 
		
		// set the textual description of the message
		dialog.titleText.setText(LocaleMessage.getInstance().getString("question.delete_album_label"));
		
		// set the message
		dialog.messages.setText(LocaleMessage.getInstance().getString("question.delete_album"));
		dialog.messages.setCaretPosition(0);
		
		// show the dialog window
		setSizeAndShowDialog(dialog);
		
		return dialog;
	}
	
	/**
	 * Shows a confirmation dialog with the options to answer with yes or no.
	 */
	public static DeleteDialog showDeleteSongDialog()
	{
		JFrame frame = new JFrame();
    	dialog = new DeleteDialog(frame);
    	
    	// set the title
    	dialog.setTitle(LocaleMessage.getInstance().getString("question.delete_song_title"));
    	
    	// set the textual description of the message
    	dialog.titleText.setText(LocaleMessage.getInstance().getString("question.delete_song_label"));
    	
    	// set the message
    	dialog.messages.setText(LocaleMessage.getInstance().getString("question.delete_song"));
    	dialog.messages.setCaretPosition(0);
    	
    	// show the dialog window
    	setSizeAndShowDialog(dialog);

		return dialog;
	}
    
    /**
     * Initializes the GUI elements.
     */
    private void initGUI() 
    {
		try 
		{
			Container cp = getContentPane();
			
			int maxWidth = 400;
			
			BorderLayout thisLayout = new BorderLayout();
			cp.setLayout(thisLayout);
			
			iconLabel = new JLabel();
			iconLabel.setIcon(delIco);
			iconLabel.setSize(delIco.getIconWidth(), delIco.getIconHeight());
			
			messages = new JTextArea();
			messages.setText("");
			messages.setLineWrap(true);
			messages.setWrapStyleWord(true);
			messages.setFont(messageTextFont);
			messages.setEditable(false);
			messages.setSize(maxWidth - delIco.getIconWidth() - 50, 40);
			
			titleText = new JTextArea(){
				private static final long serialVersionUID = 7523079066946770867L;

				@Override
				protected void paintComponent(Graphics g)
				{
					Graphics2D g2d = (Graphics2D)g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					super.paintComponent(g);
				}
			};
			titleText.setText("");
			titleText.setForeground(titleTextColor);
			titleText.setFont(titleTextFont);
			titleText.setLineWrap(true);
			titleText.setWrapStyleWord(true);
			titleText.setEditable(false);
			titleText.setBorder(new EmptyBorder(0, 0, 0, 0));
			titleText.setSize(maxWidth - delIco.getIconWidth() - 20, delIco.getIconHeight());

			// header panel
			JPanel header = new JPanel();
			header.setBackground(background);
			header.setLayout(new FlowLayout(FlowLayout.LEFT));
			header.add(iconLabel);
			header.add(titleText);
			
			// main panel
			JPanel main = new JPanel();
			main.setLayout(new BorderLayout(10, 10));
			main.setBackground(background);
			messages.setBackground(background);
			messages.setBorder(BorderFactory.createEmptyBorder(10, 50, 0, 10));
			main.add(messages, BorderLayout.CENTER);
			JCheckBox delFilesAlso = new JCheckBox(LocaleMessage.getInstance().getString("question.delete_files")); 
			delFilesAlso.setFont(messageTextFont);
			delFilesAlso.setBorder(BorderFactory.createEmptyBorder(0, 50, 10, 10));
			delFilesAlso.setSelected(false);
			delFilesAlso.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e)
				{
					delFilesSelected = !delFilesSelected;
				}
			});
			main.add(delFilesAlso, BorderLayout.SOUTH);
			
			// button panel
			JPanel buttons = new JPanel();
			buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
			
			// a confirmation message has a yes and a no answer
			JButton yesButt = new JButton();
			yesButt.addActionListener(this);
			yesButt.setText(UIManager.getString("OptionPane.yesButtonText"));
			yesButt.setActionCommand(YES_OPTION);
			buttons.add(yesButt);
			JButton noButt = new JButton();
			noButt.addActionListener(this);
			noButt.setText(UIManager.getString("OptionPane.noButtonText"));
			noButt.setActionCommand(NO_OPTION);
			buttons.add(noButt);
			
			buttons.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true, 
					false, false, false));
			
			// add the panels to the dialog window
			cp.add(header, BorderLayout.NORTH);
			cp.add(main, BorderLayout.CENTER);
			cp.add(buttons, BorderLayout.SOUTH);
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
    
    /**
     * Sets the dialog size correctly.
     */
    private static void setSizeAndShowDialog(DeleteDialog d)
    {
    	d.setIconImage(null);
    	d.setModal(true);
    	d.setAlwaysOnTop(true);
    	d.setMinimumSize(new Dimension(400, 200));
    	d.pack();
    	d.setResizable(false);
    	d.setLocationRelativeTo(null);
    	d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    	d.setVisible(true);
    }

    // A listener for the button(s).
    public void actionPerformed(ActionEvent e) 
    {
    	JButton source = (JButton)e.getSource();
    	String command = source.getActionCommand();
    	
    	DeleteDialog dialog = DeleteDialog.dialog;
    	
    	if(command.equals(YES_OPTION)) dialog.yesSelected = true; 
    	else if(command.equals(NO_OPTION)) dialog.yesSelected = false;
    	
    	// now hide the dialog window
    	dialog.setVisible(false);
    }
}