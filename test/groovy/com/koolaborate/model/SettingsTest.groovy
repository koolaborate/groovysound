package com.koolaborate.model

import java.awt.Point;

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

class SettingsTest{
	Settings settings
	
	@Before 
	void setup(){
		settings = new Settings()
	}
	
	@Test
	void shouldLoadSettings(){
		settings = new SettingsStubsForLoad()
		
		assert 0 == settings.mainWindowX
		settings.loadSettings()
		assert 10 == settings.mainWindowX
		// etc
	}
	
	@Test
	void shouldSave(){
		settings.save()
		Properties properties = new Properties()
		properties.load(new FileInputStream(Settings.SETTINGS_PATH))
		assert "0" == properties.get("mainwindow_x")
		assert "-1" == properties.get("mainwindow_y")
		assert Settings.SETTINGS_PATH == properties.get("lastfolder") + File.separator + "settings.ini"
		assert "0.0" == properties.get("balance")
		assert "0.5" == properties.get("volume")
		assert "1.0" ==properties.get("version")
		assert "false" == properties.get("opengl")
		assert "false" == properties.get("checkupdates")
		
		settings.setMainWindowX(10)
		settings.save()
		properties.load(new FileInputStream(Settings.SETTINGS_PATH))
		assert "10" == properties.get("mainwindow_x")
		// etc
	}
	
	@Test
	void shouldSetMainWindowLocation(){
		settings.setMainWindowLocation(null)
		
		Point point = new Point(1, 1)
		assertEquals 0, settings.mainWindowX
		assertEquals new Integer(-1), settings.mainWindowY
		settings.setMainWindowLocation(point)
		assertEquals 1, settings.mainWindowX
		assertEquals new Integer(1), settings.mainWindowY
	}
	
	@Test 
	void shouldTestInstantiation(){
		assertNotNull settings
	}
}


class SettingsStubsForLoad extends Settings{
	@Override
	protected Properties getProperties(){
		Properties properties = new Properties()
		properties.setProperty("mainwindow_x", "10")
		return properties
	}
}



