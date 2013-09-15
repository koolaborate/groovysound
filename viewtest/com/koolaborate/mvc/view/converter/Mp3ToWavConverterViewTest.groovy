package com.koolaborate.mvc.view.converter

import javax.swing.JDialog
import javax.swing.JFrame

import com.koolaborate.mvc.view.test.ViewTestHarness

class Mp3ToWavConverterViewTest extends ViewTestHarness{
	Mp3ToWavConverterView converter
	
	static void main(args){
		Mp3ToWavConverterViewTest test = new Mp3ToWavConverterViewTest()
		Mp3ToWavConverterView converter = new Mp3ToWavConverterView()
		test.show(converter)
	}
	
	def void show(Mp3ToWavConverterView converter){
		JFrame frame = frameShell
		frame.setVisible(true)
		
		converter.init()
		JDialog dialog = converter
		dialog.setVisible(true);
		dialog.pack();
		
		// Set location centered for JFrame
		dialog.setLocationRelativeTo(frame)
	}
	
	
}




