package com.koolaborate.mvc.view.mainwindow

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*

import com.koolaborate.model.Settings;


class MainWindowTest{
	MainWindow mainWindow
	
	@Before
	void setup(){
		Settings settings = new Settings()
		settings.loadSettings()
		
		mainWindow = new MainWindow()
		mainWindow.settings = settings
		mainWindow.initializeGui(settings)
	}
	
	@Test
	void shouldTestInstantiation(){
		assertNotNull mainWindow
	}

	static void main(args){
//		MainWindowTest test = new MainWindowTest()
//		test.mainWindow = new MainWindow()
		
		DoSomething doSomething = [
			act: { i ->
				println 'acted: ' + i
				somethingElse()
				return 'ok'
			}
		] as DoSomething
		
		def something = doSomething.act(4)
		println 'something: ' + something
	}
	
	def static void somethingElse(){
		println 'something else'
	}
		
}

interface DoSomething{
	String act(Integer i)
}
