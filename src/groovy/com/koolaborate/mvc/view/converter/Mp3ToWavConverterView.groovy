package com.koolaborate.mvc.view.converter

import com.koolaborate.mvc.controller.converter.Mp3ToWavConverterController
import java.awt.BorderLayout
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder

class Mp3ToWavConverterView extends JDialog{
	private JLabel currentFile
	private JProgressBar progressBar
	private String language
	
	Mp3ToWavConverterController mp3ToWavConverterController
	
	public Mp3ToWavConverterView(){
		// ultimately removed for spring
		mp3ToWavConverterController = new Mp3ToWavConverterController()
		
		init()
		initLabel()
	}
	
	def void init(){
		setLayout(new BorderLayout())
		String progress = "Progress..."
		if(language.equals("de")) progress = "Fortschritt..."
		getContentPane().setBackground(Color.WHITE)
		setModal(false)
		setTitle(progress)
		setSize(300, 100)
		setResizable(false)
		setLocationRelativeTo(null)
	}
	
	def void initLabel(){
		currentFile = new JLabel("")
		
		// this can be invoked later
		currentFile.setBorder(new EmptyBorder(10, 10, 10, 10))
		add(currentFile, BorderLayout.CENTER)
		progressBar = new JProgressBar(0, mp3ToWavConverterController.getNumberOfFiles())
		add(progressBar, BorderLayout.SOUTH)
		setVisible(true)
	}
	
	
	
}




