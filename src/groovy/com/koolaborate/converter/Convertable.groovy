package com.koolaborate.converter

interface Convertable{
	def void init(String srcDir, String dstDir)
	def void convert()
}
