package com.koolaborate.mvc.view.mainwindow


class MainWindowTest{
	MainWindow mainWindow

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
