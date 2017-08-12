package com.kingston.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {

	public static boolean isInteger(String input){
		Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);
		return mer.find();
	}

}
