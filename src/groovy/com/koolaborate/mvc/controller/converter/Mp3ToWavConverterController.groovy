package com.koolaborate.mvc.controller.converter

import com.koolaborate.converter.MP3ToWAVConverter
import com.koolaborate.transaction.TransactionMessage

class Mp3ToWavConverterController{
	MP3ToWAVConverter mp3ToWAVConverter
	
	// maps to init
	def void init(String sourceFolderLocation, String destinationFolderLocation){}
	
	// maps to amountFiles
	def int getNumberOfFiles(){
		return 0
	}
	
	def TransactionMessage convertMp3ToWav(File sourceFile){
		TransactionMessage message = new TransactionMessage()
		
		return message
	}
}
