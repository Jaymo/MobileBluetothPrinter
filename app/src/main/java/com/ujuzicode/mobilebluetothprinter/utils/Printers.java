package com.ujuzicode.mobilebluetothprinter.utils;


import com.ujuzicode.mobilebluetothprinter.printer.PocketPos;

public class Printers {
	
	public Printers(){}
	
	public static byte[] printfont (String content, byte fonttype, byte fontalign, byte linespace, byte language){
		
		if (content != null && content.length() > 0) {
			
			content = content + "\n";
			byte[] temp;
			temp = PocketPos.convertPrintData(content, 0,content.length(), language, fonttype,fontalign,linespace);
			
			return temp;
		}else{
			return null;
		}
	}
	
}