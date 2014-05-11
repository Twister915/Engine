/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.util;

import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.exceptions.FormatException;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 09/05/2014
 */
public class Utils {

	public static String _(String format, Boolean prefix, Boolean color, String[]... data) {
		return GearzBungee.getInstance().getFormat(format, prefix, color, data);
	}

	public static String _(String format, Boolean prefix, Boolean color) {
		return GearzBungee.getInstance().getFormat(format, prefix, color);
	}

	public static String _(String format, Boolean prefix) {
		return GearzBungee.getInstance().getFormat(format, prefix);
	}

	public static String _(String format, String[] data) {
		return GearzBungee.getInstance().getFormat(format, data);
	}

	public static String _(String format) {
		return GearzBungee.getInstance().getFormat(format);
	}

	public static String replace(String s, String target, String replacement) { //todo test
		StringBuilder sb = null;
		int start = 0;
		for (int i; (i = s.indexOf(target, start)) != -1; ) {
			if (sb == null) sb = new StringBuilder();
			sb.append(s, start, i);
			sb.append(replacement);
			start = i + target.length();
		}
		if (sb == null) return s;
		sb.append(s, start, s.length());
		return sb.toString();
	}

	public static String method2Exception(String exceptionText,  Method method, Object... parameters) { //todo test
		String newExceptionText = method.getName()+"("+Arrays.deepToString(method.getParameterTypes())+")"+" was called with the parameters:  ";

		for(Integer i = 0, l = parameters.length; i < l; i++) {
			newExceptionText += "\n" +"arg"+i+": "+parameters[i].toString();
		}

		return newExceptionText;
	}

	/**
	 * Short Utility Method for
	 * ChatColor.translateAlternateColorCodes('&', <string>)
	 * @param string the string to translate the color codes for
	 * @return the string with color codes in
	 * @see net.md_5.bungee.api.ChatColor#translateAlternateColorCodes(char, String)
	 */
	public static String tc(String string) {
		return tc('&', string);
	}

	/**
	 * Short Utility Method for
	 * ChatColor.translateAlternateColorCodes(<altCode>, <string>)
	 * @param altCode the alternate code to use for replacements of color codes
	 * @param string the the string to translate the color codes for
	 * @return the string with color codes in
	 * @see net.md_5.bungee.api.ChatColor#translateAlternateColorCodes(char, String)
	 */
	public static String tc(char altCode, String string) {
		return ChatColor.translateAlternateColorCodes(altCode, string);
	}

	/**
	 * Turns text into a base component
	 * Useful when sending messages to avoid deprecation
	 * @param text The text to turn into base component
	 * @return The Base Component
	 */
	public static BaseComponent[] text2BaseComponent(String text) {
		return TextComponent.fromLegacyText(text);
	}

	/**
	 * Short named utility method for
	 * text2BaseComponent(String text)
	 * @param text The text to turn into base component
	 * @return The Base Component
	 * @see Utils#text2BaseComponent(String)
	 */
	public static BaseComponent[] t2BC(String text) {
		return text2BaseComponent(text);
	}

	@SneakyThrows(Exception.class)
	public static String replaceFromArray(String property, String[]... replacements) {
		Exception ex = null;
		for (String[] replacement: replacements) {
			if (replacement.length < 2) {
				ex = new FormatException(
						method2Exception(
								"In the replaceFromArray method: one of the arrays was smaller than 2",
								(new Object(){}.getClass().getEnclosingMethod()),
								Arrays.deepToString(replacements)
						)
				);
				continue;
			}
			property = replace(property, replacement[0], replacement[1]);
		}
		if(ex != null) throw ex;
		return property;
	}

}
