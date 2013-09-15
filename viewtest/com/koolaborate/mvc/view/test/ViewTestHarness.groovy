package com.koolaborate.mvc.view.test

import java.awt.BorderLayout
import javax.swing.JFrame;

class ViewTestHarness{

	def JFrame getFrameShell(){
		JFrame frame = new JFrame()
		frame.setTitle("Center a JDialog")
		frame.setSize(400,400)
		frame.setLayout(new BorderLayout())
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
		frame.setLocationRelativeTo(null)
		
		return frame
	}
	
	
	
}










