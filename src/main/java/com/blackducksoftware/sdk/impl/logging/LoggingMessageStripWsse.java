/*
 * Black Duck Software Suite SDK
 * Copyright (C) 2015  Black Duck Software, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.blackducksoftware.sdk.impl.logging;

public class LoggingMessageStripWsse extends LoggingMessageSecure {
	public LoggingMessageStripWsse(String h, String i) {
		super(h, i);
	}

	private static final String WSSE_SECURITY_HEADER_BEGIN_TAG = "<wsse:Security";

	private static final char END_OF_TAG = '>';

	private static final char EMPTY_TAG = '/';

	private static final String WSSE_SECURITY_HEADER_END_TAG = "</wsse:Security>";

	@Override
	protected StringBuilder formatPayload(StringBuilder builder) {
		return filterWsseSecurity(getPayload());
	}

	private StringBuilder filterWsseSecurity(StringBuilder builder) {
		int startWsse = builder.indexOf(WSSE_SECURITY_HEADER_BEGIN_TAG);
		if (startWsse > 0) {
			int startHeader = startWsse
					+ WSSE_SECURITY_HEADER_BEGIN_TAG.length() - 1;
			boolean emptyHeader = false;
			while (++startHeader < builder.length()) {
				if (builder.charAt(startHeader) == END_OF_TAG) {
					if (builder.charAt(startHeader - 1) == EMPTY_TAG) {
						emptyHeader = true;
					}
					break;
				}
			}
//			 System.out.println("Password: " + (emptyHeader ? "<empty>" :builder.substring(startHeader, startHeader + 10)));
			if (!emptyHeader) {
				int endHeader = builder.indexOf(WSSE_SECURITY_HEADER_END_TAG,
						startHeader);
//				 System.out.println("Password: " + builder.substring(startHeader + 1, endHeader));
				return builder.replace(startHeader + 1, endHeader, "...");
			}
		}
		return builder;
	}

//     public static void main(String[] args) {
//    	 LoggingMessageStripWsse me = new LoggingMessageStripWsse("::::", "????");
//     StringBuilder payload = new StringBuilder().append("<fdaf><wsse:Security><wsse:Password>My Secret</wsse:Password></wsse:Security></fdaf>");
//     StringBuilder masked = me.formatPayload(new StringBuilder(payload));
//     System.out.println(payload + " ==> " + masked);
//     payload = new StringBuilder().append("<fdaf><wsse:Security attr=\"jljlkjk\"><wsse:Password attr=\"dafds\">My Secret</wsse:Password></wsse:Security></fdaf>");
//     masked = me.formatPayload(new StringBuilder(payload));
//     System.out.println(payload + " ==> " + masked);
//     payload = new StringBuilder().append("<fdaf><wsse:Security/></fdaf>");
//     masked = me.formatPayload(new StringBuilder(payload));
//     System.out.println(payload + " ==> " + masked);
//     payload = new StringBuilder().append("<fdaf><wsse:Security attr=\"jljlkjk\"/></fdaf>");
//     masked = me.formatPayload(new StringBuilder(payload));
//     System.out.println(payload + " ==> " + masked);
//     }

}
