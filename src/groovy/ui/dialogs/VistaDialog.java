package ui.dialogs;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import ui.common.VariableLineBorder;
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
import java.util.List;

/***********************************************************************************
 * VistaDialog                                                                     *
 ***********************************************************************************
 * A class to show various dialogs in Windows(R) Vista(R) style.                   *
 *                                                                                 *
 * The following examples show how the dialogs are created and shown:              *
 *                                                                                 *
 * Error dialog:                                                                   *
 * VistaDialog.showDialog("Outlook", "Can't move \"Sent Items\"",                  *
 *    "You don't have permission to access this item.",                            *
 *	  VistaDialog.ERROR_MESSAGE);                                                  *
 *                                                                                 *
 * Warning dialog:                                                                 *
 * VistaDialog.showDialog("Network", "Unable to access remote computer",           *
 *    "The network cable might be unplugged.",                                     *
 *    VistaDialog.WARNING_MESSAGE);                                                *
 *                                                                                 *
 * Information dialog:                                                             *
 * VistaDialog.showDialog("Low Battery", "Change your battery or change to " +     *
 *    "outlet power immediately",                                                  *
 *    "Your computer has a low battery, so you should act immediately to keep " +  *
 *    "from losing your work.",                                                    *
 *    VistaDialog.INFORMATION_MESSAGE);                                            *
 *                                                                                 *
 * Confirmation dialog:                                                            *
 * VistaDialog dialog = VistaDialog.showConfirmationDialog("Format F:",            *
 *    "Are you sure to format this volume?", "Formatting this volume will " +      *
 *    "erase all data on it. Back up any data you want to keep before " +          *
 *    "formatting. Do you want to continue?");                                     *
 * if(dialog.yesSelected == true) System.out.println("Answer was YES!");           *
 * else System.out.println("Answer was NO!");                                      *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.3                                                                    *
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
public class VistaDialog extends JDialog implements ActionListener 
{
	/** constants for the various message types */
	public final static int INFORMATION_MESSAGE  = 0;
	public final static int CONFIRMATION_MESSAGE = 1;
	public final static int WARNING_MESSAGE      = 2;
	public final static int ERROR_MESSAGE        = 3;

	public final static String YES_OPTION = "yes";
	public final static String NO_OPTION  = "no";
	public final static String NO_ACTION  = "dispose";
	
	/** the dialog instance */
	private static VistaDialog dialog;
	
	/** the selected yes or no option of a confirmation dialog */
	public boolean yesSelected = false;
	
	/** a label that shows the appropriate icon image */
	private JLabel iconLabel;
	/** a text area for the messages */
	private JTextArea messages;
	/** a text area that shows the title description of the message(s) */
	private JTextArea titleText;
		
	/** the different fonts */
	private static Font titleTextFont   = new Font("Segoe UI", Font.PLAIN, 15);
	private static Font messageTextFont = new Font("Segoe UI", Font.PLAIN, 12);
	
	/** the colors */
	private static Color background     = Color.WHITE;
	private static Color titleTextColor = new Color(0, 51, 188); // dark blue
	
	/**
	 * Method to show a dialog of the type <code>type</code>.
	 * 
	 * @param title the title that is shown in the top of the window
	 * @param labelText the text that gives a description of the message
	 * @param messageText the message text (can be multiple lines, seperated by "\n")
	 * @param type the type of the dialog, one of the message type constants
	 */
	public static void showDialog(String title, String labelText, String messageText, int type) 
    {
		if(type <= INFORMATION_MESSAGE)  showInformationDialog(title, labelText, messageText);
		else if(type == WARNING_MESSAGE) showWarningDialog(title, labelText, messageText);
		else if(type >= ERROR_MESSAGE)   showErrorDialog(title, labelText, messageText);
    }
	
	/**
	 * Shows a confirmation dialog with the options to answer with yes or no.
	 *
	 * @param title the title that is shown in the top of the window
	 * @param labelText the text that gives a description of the information message
	 * @param questionText the question text (can be multiple lines, seperated by "\n")
	 */
	public static VistaDialog showConfirmationDialog(String title, String labelText, String questionText)
	{
		JFrame frame = new JFrame();
    	dialog = new VistaDialog(frame, CONFIRMATION_MESSAGE);
    	
    	// set the title
    	dialog.setTitle(title);
    	
    	// set the textual description of the message
    	dialog.titleText.setText(labelText);
    	
    	// set the message
    	dialog.messages.setText(questionText);
    	dialog.messages.setCaretPosition(0);
    	
    	// show the dialog window
    	setSizeAndShowDialog(dialog);
				
		return dialog;
	}
	
	/**
	 * Method to show a dialog of the type <code>type</code>.
	 * 
	 * @param title the title that is shown in the top of the window
	 * @param labelText the text that gives a description of the message
	 * @param messages the messages
	 * @param type the type of the dialog, one of the message type constants
	 */
	public static void showDialog(String title, String labelText, List<String> messages, int type) 
    {
		StringBuilder sb = new StringBuilder();
		String newline = "\n";
		// add all messages to one String
		for(String message : messages)
		{
			sb.append(message);
			sb.append(newline);
		}
		// remove final newline
		int newlineLength = newline.length();
		sb.delete(sb.length() - newlineLength, sb.length());
		
		showDialog(title, labelText, sb.toString(), type);
    }
	
	/**
	 * Shows an information message dialog.
	 * 
	 * @param title the title that is shown in the top of the window
	 * @param labelText the text that gives a description of the information message
	 * @param messageText the message text (can be multiple lines, seperated by "\n")
	 */
    private static void showInformationDialog(String title, String labelText, String messageText)
	{
    	JFrame frame = new JFrame();
    	dialog = new VistaDialog(frame, INFORMATION_MESSAGE);
    	
    	// set the title
    	dialog.setTitle(title);
    	
    	// set the textual description of the message
    	dialog.titleText.setText(labelText);
    	
    	// set the message
    	dialog.messages.setText(messageText);
    	dialog.messages.setCaretPosition(0);
    	
    	// show the dialog window
    	setSizeAndShowDialog(dialog);
	}
	
    /**
	 * Shows a warning dialog.
	 * 
	 * @param title the title that is shown in the top of the window
	 * @param labelText the text that gives a description of the information message
	 * @param messageText the message text (can be multiple lines, seperated by "\n")
	 */
    private static void showWarningDialog(String title, String labelText, String messageText)
	{
    	JFrame frame = new JFrame();
    	dialog = new VistaDialog(frame, WARNING_MESSAGE);
    	
    	// set the title
    	dialog.setTitle(title);
    	
    	// set the textual description of the message
    	dialog.titleText.setText(labelText);
    	
    	// set the message
    	dialog.messages.setText(messageText);
    	dialog.messages.setCaretPosition(0);
    	
    	// show the dialog window
    	setSizeAndShowDialog(dialog);
	}
    
    /**
	 * Shows an error message dialog.
	 * 
	 * @param title the title that is shown in the top of the window
	 * @param labelText the text that gives a description of the information message
	 * @param messageText the message text (can be multiple lines, seperated by "\n")
	 */
    private static void showErrorDialog(String title, String labelText, String messageText)
	{
    	JFrame frame = new JFrame();
    	dialog = new VistaDialog(frame, ERROR_MESSAGE);
    	
    	// set the title
    	dialog.setTitle(title);
    	
    	// set the textual description of the message
    	dialog.titleText.setText(labelText);
    	
    	// set the message
    	dialog.messages.setText(messageText);
    	dialog.messages.setCaretPosition(0);
    	
    	// show the dialog window
    	setSizeAndShowDialog(dialog);
	}

    /**
     * Constructor.
     * 
     * @param frame the parent frame
     * @param type the dialog type
     */
    private VistaDialog(JFrame frame, int type) 
    {
		super(frame);
		initGUI(type);
	}
    
    /**
     * Initializes the GUI elements.
     * 
     * @param type the dialog type
     */
    private void initGUI(int type) 
    {
		try 
		{
			Container cp = getContentPane();
			
			int maxWidth = 400;
			
			Icon icon = null;
			
			if(type <= INFORMATION_MESSAGE)       icon = (Icon)UIManager.get("OptionPane.warningIcon");
			else if(type == CONFIRMATION_MESSAGE) icon = (Icon)UIManager.get("OptionPane.questionIcon");
			else if(type == WARNING_MESSAGE)      icon = (Icon)UIManager.get("OptionPane.warningIcon");
			else if(type >= ERROR_MESSAGE)        icon = (Icon)UIManager.get("OptionPane.errorIcon");
			
			int icoWidth = icon.getIconWidth();
			int icoHeight = icon.getIconHeight();
			
			BorderLayout thisLayout = new BorderLayout();
			cp.setLayout(thisLayout);
			iconLabel = new JLabel();
			iconLabel.setIcon(icon);
			iconLabel.setSize(icoWidth, icoHeight);
			messages = new JTextArea();
			messages.setText("");
			messages.setLineWrap(true);
			messages.setWrapStyleWord(true);
			messages.setFont(messageTextFont);
			messages.setEditable(false);
			messages.setSize(maxWidth - icoWidth - 50, 40);
			titleText = new JTextArea(){
				@Override
				protected void paintComponent(Graphics g)
				{
					Graphics2D g2d = (Graphics2D)g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					super.paintComponent(g);
				}
			};
			titleText.setForeground(titleTextColor);
			titleText.setFont(titleTextFont);
			titleText.setLineWrap(true);
			titleText.setWrapStyleWord(true);
			titleText.setEditable(false);
			titleText.setSize(maxWidth - icoWidth - 20, icoHeight);
			titleText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

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
			
			// button panel
			JPanel buttons = new JPanel();
			buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
			
			// the buttons depend on the dialog type...
			if(type <= INFORMATION_MESSAGE)
			{
				// an information message only has an OK button
				JButton okButt = new JButton();
				okButt.addActionListener(this);
				okButt.setText(UIManager.getString("OptionPane.okButtonText"));
				buttons.add(okButt);
			}
			else if(type == CONFIRMATION_MESSAGE)
			{
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
			}
			else if(type == WARNING_MESSAGE)    
			{
				// a warning message only has an OK button
				JButton okButt = new JButton();
				okButt.addActionListener(this);
				okButt.setText(UIManager.getString("InternalFrameTitlePane.closeButtonText"));
				okButt.setActionCommand(NO_ACTION);
				buttons.add(okButt);
			}
			else if(type >= ERROR_MESSAGE) 
			{
				// an error message only has a close button
				JButton closeButt = new JButton();
				closeButt.addActionListener(this);
				closeButt.setText(UIManager.getString("InternalFrameTitlePane.closeButtonText"));
				closeButt.setActionCommand(NO_ACTION);
				buttons.add(closeButt);
			}

			buttons.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true, 
					false, false, false));
			
			// add the panels to the dialog window
			cp.add(header,  BorderLayout.NORTH);
			cp.add(main,    BorderLayout.CENTER);
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
    private static void setSizeAndShowDialog(VistaDialog d)
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
    	
    	VistaDialog dialog = VistaDialog.dialog;
    	
    	if(command.equals(YES_OPTION)) dialog.yesSelected = true; 
    	else if(command.equals(NO_OPTION)) dialog.yesSelected = false;
    	
    	// now hide the dialog window
    	dialog.dispose();
    }
}