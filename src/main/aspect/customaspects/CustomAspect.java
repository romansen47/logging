package customaspects;
/*
 * Copyright (c) FJA AG. All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;

/**
 * Interface providing elementary xml parsing methods
 */
public interface CustomAspect {

	String getGenericName();

	/**
	 * Logging
	 *
	 * @param logInfo        the message
	 * @param printToConsole whether to use System.out.println or the logger
	 */
	default void log(String logInfo, boolean printToConsole) {
		if (printToConsole) {
			System.out.println(getGenericName() + "-log: " + logInfo);
		} else {
			getLogger().info(getGenericName() + "-log: " + logInfo);
		}
	}

	/**
	 * @return the corresponding logger
	 */
	Logger getLogger();

	/**
	 * Logging using the logger
	 *
	 * @param logInfo the message
	 */
	default void log(String logInfo) {
		log(logInfo, false);
	}

	default String customFullyQualifiedName(JoinPoint jp) {
		String ans = jp.getStaticPart().toShortString().split(Pattern.quote("@"))[0];
		int k = ans.length();
		ans = ans.substring(10, k).split("@")[0];
		k = ans.length();
		if (stringOf(ans).contains(".))")) {
			ans = ans.substring(0, k - 5);
		} else {
			ans = ans.substring(0, k - 3);
		}
		if (jp.toLongString().contains("ipl.customer")) {
			ans = "customer." + ans;
		}
		return ans;
	}

	final String[][] UMLAUT_REPLACEMENTS = { { "Ä", "Ae" }, { "Ü", "Ue" }, { "Ö", "Oe" }, { "ä", "ae" }, { "ü", "ue" },
			{ "ö", "oe" }, { "ß", "ss" } };

	default String replaceUmlaute(String orig) {
		String result = orig;
		for (String[] element : UMLAUT_REPLACEMENTS) {
			result = result.replaceAll(element[0], element[1]);
		}
		return result;
	}

	default String getEntry(final Object o, final List<String> ans) {
		String str = "";
		if (o instanceof Boolean) {
			str = "<boolean>" + stringOf(o) + "</boolean>\r";
		} else {
			if (o instanceof String) {
				str = "<string>" + stringOf(o) + "</string>\r";
			}
		}
		return str;
	}
	 
	default String numberToString(Object o) {
		return "<" + o.getClass().getSimpleName().split(Pattern.quote("@"))[0].split(Pattern.quote("$"))[0] + ">"
				+ stringOf(o) + "</"
				+ o.getClass().getSimpleName().split(Pattern.quote("@"))[0].split(Pattern.quote("$"))[0] + ">";
	}
	
	default void listToString(Object o, List<String> ans) {
		ans.add("<list>");
		for (final Object x : (List<?>) o) {
			final String str = this.getEntry(o, ans);
			if (str.isEmpty()) {
				String s = stringOf(x.getClass()).split("class ")[1].split(Pattern.quote("@"))[0]
						.split(Pattern.quote("$"))[0];
				ans.add("<" + s + ">" + stringOf(x).split(Pattern.quote("@"))[0].split(Pattern.quote("$"))[0] + "</" + s
						+ ">\r");
			}

			else {
				ans.add(str);
			}
		}
		ans.add("</list>");
	}

	
	default String xmlString(final Object o) {
		final List<String> ans = new ArrayList<>();
		if (o != null) {
			simpleToString(o, ans);
		} else {
			ans.add("<null />");
		} 
		String realAns = "";
		for (final String str : ans) {
			realAns += str;
		}
		return realAns;
	}

	default void simpleToString(Object o, List<String> ans) {
		String type = null;
		try {
			type = o.getClass().getSimpleName().split(Pattern.quote("@"))[0].split(Pattern.quote("$"))[0];
		} catch (Exception e) {
			log(e.getMessage());
		}
		if (type == null || type.contentEquals("")) {
			type = stringOf(o.getClass()).split("class ")[1];
		}
		type = type.split(Pattern.quote("$"))[0];
		String value = stringOf(o);
		value = value.split("@")[0];
		if (value.contains("$")) {
			value = "subtype_in_" + value.split(Pattern.quote("$"))[0];
		}
		ans.add("<" + type + ">" + value + "</" + type + ">\r");
	}

	default String doubleArrayToString(double[] o) {
		String ans = "<doubles>\r";
		for (double x : o) {
			ans += "<" + x + " />\r";
		}
		return ans + "</doubles>\r";
	}

	default String stringOf(Object o) {
		String str = o.toString().replace(Pattern.quote("\\$"), "_");
		return str;
	}

	/**
	 * @param a
	 * @param b
	 * @return "sum" of the strings
	 */
	default String addStringsOf(String a, String b) {
		return a + b;
	}
}
