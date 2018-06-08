package com.ujuzicode.mobilebluetothprinter.utils;

import java.util.Locale;

public class Language {

	public static int GetIndex() {
		String language = Locale.getDefault().getLanguage();
		if (language.endsWith("en"))
			return 0;
		else if (language.endsWith("zh"))
			return 1;
		else
			return 0;
	}
}
