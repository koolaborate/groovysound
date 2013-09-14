package com.koolaborate.converter

import java.awt.BorderLayout;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JLabel
import javax.swing.JProgressBar
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder

import com.koolaborate.mvc.view.dialogs.VistaDialog;

class Mp3ToWavConverterThread implements Runnable{
	Convertable converter
	
	def void setConverter(Convertable converter){
		this.converter = converter
	}
	
	@Override
	public void run() {
		MP3ToWAVConverter mp3ToWavConverter = (MP3ToWAVConverter) converter
		
		// show a status bar dialog
		SwingUtilities.invokeLater(new Runnable(){
					public void run()
					{
						mp3ToWavConverter.currentFile = new JLabel("");
						mp3ToWavConverter.currentFile.setBorder(new EmptyBorder(10, 10, 10, 10));
						mp3ToWavConverter.dialog.add(mp3ToWavConverter.currentFile, BorderLayout.CENTER);

						mp3ToWavConverter.progressBar = new JProgressBar(0, mp3ToWavConverter.amountFiles);
						mp3ToWavConverter.dialog.add(mp3ToWavConverter.progressBar, BorderLayout.SOUTH);
						mp3ToWavConverter.dialog.setVisible(true);
					}
				});

		// collect all errors
		List<String> errors = new ArrayList<String>();
		mp3ToWavConverter.convertedFiles = 0;

		String dstFolderPath = mp3ToWavConverter.destinationFolder.getAbsolutePath();
		if(!dstFolderPath.endsWith(File.separator)) dstFolderPath += File.separator;

		// convert all the files in the folder
		for(File mp3 : mp3ToWavConverter.mp3s)
		{
			String mp3Path = mp3.getAbsolutePath();
			final String mp3Name = mp3.getName();
			String wavFileName = dstFolderPath + mp3Name.substring(0, mp3Name.length()-".mp3".length()); // cut the '.mp3' file extension
			wavFileName += ".wav";
			SwingUtilities.invokeLater(new Runnable(){
						public void run()
						{
							mp3ToWavConverter.currentFile.setText(mp3Name);
							mp3ToWavConverter.currentFile.repaint();
						}
					});

			try
			{
				System.out.println("--- Start converting " + mp3Path + " ---");
				File outFile = new File(wavFileName);
				AudioFileFormat aff = AudioSystem.getAudioFileFormat(mp3);
				System.out.println("Audio Type : " + aff.getType());
				AudioInputStream instream = AudioSystem.getAudioInputStream(mp3);
				AudioInputStream din = null;
				if(instream != null)
				{
					AudioFormat baseFormat = instream.getFormat();
					System.out.println("Source Format : " + baseFormat.toString());
					AudioFormat decodedFormat = new AudioFormat(
							AudioFormat.Encoding.PCM_SIGNED,
							baseFormat.getSampleRate(),
							16,
							baseFormat.getChannels(),
							baseFormat.getChannels() * 2,
							baseFormat.getSampleRate(),
							false);
					System.out.println("Target Format : " + decodedFormat.toString());
					din = AudioSystem.getAudioInputStream(decodedFormat, instream);

					AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
					AudioSystem.write(din, targetType, outFile);

					instream.close();
					System.out.println("--- Stop converting" + mp3Path + " ---");
				}
			}
			catch (Exception e)
			{
				String err = "Error converting '{mp3Path}'.\nProblem: ";
				if(mp3ToWavConverter.language.equals("de"))
				{
					err = "Fehler beim Konvertieren von '{mp3Path}'.\nProblembeschreibung: ";
				}
				err = err.replace("{mp3Path}", mp3Path);
				errors.add(err + e.getMessage());
			}

			mp3ToWavConverter.convertedFiles++;
			SwingUtilities.invokeLater(new Runnable(){
						public void run()
						{
							mp3ToWavConverter.progressBar.setValue(mp3ToWavConverter.convertedFiles);
							mp3ToWavConverter.progressBar.repaint();
						}
					});
		}

		mp3ToWavConverter.dialog.dispose();

		// if there was an error
		if(errors.size() > 0)
		{
			String errTitle = "Errors occured";
			String error = "Errors occured trying to convert";
			if(mp3ToWavConverter.language.equals("de"))
			{
				errTitle = "Fehler aufgetreten";
				error = "Bei der Umwandlung traten Fehler auf";
			}
			VistaDialog.showDialog(errTitle, error, errors, VistaDialog.ERROR_MESSAGE);
		}
		else
		{
			String succTitle = "Conversion complete";
			String success = "Conversion successfully completed";
			String message = "The conversion has been successfully completed. The WAV files can be found under '{dstDir}' and burned onto an audio CD.";
			if(mp3ToWavConverter.language.equals("de"))
			{
				succTitle = "Konvertierung erfolgreich";
				success = "Die Konvertierung wurde erfolgreich abgeschlossen";
				message = "Die Konvertierung wurde erfolgreich abgeschlossen. Die WAV-Dateien befinden sich im Ordner '{dstDir}' und können nun auf eine Audio-CD gebrannt werden.";
			}
			message = message.replace("{dstDir}", mp3ToWavConverter.destinationFolder.getAbsolutePath());
			VistaDialog.showDialog(succTitle, success, message, VistaDialog.INFORMATION_MESSAGE);
		}
	}


}
