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

public class LoggingMessagePasswordMasked extends LoggingMessageSecure {

    private static final String WSSE_PASSWORD_BEGIN_TAG = "<wsse:Password";

    private static final String END_OF_TAG = ">";

    private static final String WSSE_PASSWORD_EMPTY_TAG = "<wsse:Password/>";

    private static final String WSSE_PASSWORD_END_TAG = "</wsse:Password>";

    private static final String MASK_CHARACTER = "*";

    public LoggingMessagePasswordMasked(String h, String i) {
        super(h, i);
    }

    @Override
    protected StringBuilder formatPayload(StringBuilder builder) {
        return maskPassword(getPayload());
    }

    private StringBuilder maskPassword(StringBuilder builder) {
        if (builder.indexOf(WSSE_PASSWORD_EMPTY_TAG) != -1) {
            // empty tag, like <wsse:Password/>
            System.err.println("Empty <wsse:Password/> found");
        } else {
            int wssePasswordTagStartPos = builder.indexOf(WSSE_PASSWORD_BEGIN_TAG);
            if (wssePasswordTagStartPos >= 0) {
                int startPassword = builder.indexOf(END_OF_TAG,
                        wssePasswordTagStartPos + WSSE_PASSWORD_BEGIN_TAG.length()) + 1;
                int endPassword = builder.indexOf(WSSE_PASSWORD_END_TAG, startPassword);
                int length = (endPassword - startPassword);

                System.err.println(builder.substring(startPassword - 10, startPassword) + ":=:=:"
                        + builder.substring(startPassword, endPassword) + ":=:=:"
                        + builder.substring(endPassword, endPassword + 10));

                if (length > 0) {
                    String str = "";
                    for (int i = 0; i < length; i++) {
                        str += MASK_CHARACTER;
                    }
                    builder.replace(startPassword, endPassword, str);
                    System.err.println(builder.substring(startPassword - 10, startPassword) + ":=:=:"
                            + builder.substring(startPassword, endPassword) + ":=:=:"
                            + builder.substring(endPassword, endPassword + 10));
                    return builder;
                } else {
                    System.err.println("Empty <wsse:Password></wsse:Password> found");
                }
            } else {
                // no password tag
                System.err.println("No <wsse:Password> found");
            }
        }
        return builder;
    }
    // public static void main(String[] args) {
    // LoggingMessagePasswordMasked me = new LoggingMessagePasswordMasked("::::", "????");
    // StringBuilder payload = new StringBuilder().append("<fdaf><wsse:Password>My Secret</wsse:Password></fdaf>");
    // StringBuilder masked = me.formatPayload(new StringBuilder(payload));
    // System.out.println(payload + " ==> " + masked);
    // payload = new StringBuilder().append("<fdaf><wsse:Password attr=\"dafds\">My Secret</wsse:Password></fdaf>");
    // masked = me.formatPayload(new StringBuilder(payload));
    // System.out.println(payload + " ==> " + masked);
    // payload = new StringBuilder().append("<fdaf><wsse:Password/></fdaf>");
    // masked = me.formatPayload(new StringBuilder(payload));
    // System.out.println(payload + " ==> " + masked);
    // payload = new StringBuilder().append("<fdaf><wsse:Password attr=\"dafds\"/></fdaf>");
    // masked = me.formatPayload(new StringBuilder(payload));
    // System.out.println(payload + " ==> " + masked);
    // }
}
