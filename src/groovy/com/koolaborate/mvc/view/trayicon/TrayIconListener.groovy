import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.koolaborate.mvc.view.mainwindow.MainWindow;

/**
 * A listener class for the mouse events when selecting an action from the tray
 * icon.
 * 
 * @author Manuel Kaess
 */
public class TrayIconListener implements ActionListener{
	MainWindow mainWindow

	// TODO refactor to state pattern
	@Override
	public void actionPerformed(ActionEvent e){
		String action = e.getActionCommand()

		if(action.equals("exit")) {
			mainWindow.exit()
		} else if(action.equals("next")) {
			mainWindow.getPlayerPanel().fadeOut()
			mainWindow.getPlayerPanel().playNextSong()
		} else if(action.equals("previous")) {
			mainWindow.getPlayerPanel().fadeOut()
			mainWindow.getPlayerPanel().playPreviousSong()
		} else if(action.equals("play")) {
			mainWindow.getPlayerPanel().playSong()
		} else if(action.equals("pause")) {
			mainWindow.getPlayerPanel().pauseSong()
		} else if(action.equals("stop")) {
			mainWindow.getPlayerPanel().fadeOut()
			mainWindow.getPlayerPanel().stopSong()
		}
	}
}
