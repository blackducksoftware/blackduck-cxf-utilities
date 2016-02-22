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

public enum WsseLoggingLevel {
    /**
     * wsse-stripped & pretty print
     */

    COMPACT_PRETTY("0"),

    /**
     * wsse-stripped, standard print
     */
    COMPACT("1"),

    /**
     * include wsse and standard print, but password masked
     */
    VERBOSE_SECURE("2"),

    /**
     * include all in pretty print & mask password
     */
    VERBOSE_SECURE_PRETTY("3"),

    /**
     * include all in standard print and don't mask the password
     */
    VERBOSE("4")

    ;

    private String externalLevel = null;

    public String getExternalLevel() {
        return externalLevel;
    }

    private WsseLoggingLevel(String externalLevel) {
        this.externalLevel = externalLevel;
    }

    /**
     * Create an Enum object from an external representation, such as an option value for a command line.
     * 
     * @param externalLevel
     *            The external option value
     * @return The WsseLogginLeve enum
     * 
     * @throws RuntimeException
     *             if external level value is undefined.
     */
    public static WsseLoggingLevel createFromExternalLevel(String externalLevel) {
        for (WsseLoggingLevel level : values()) {
            if (externalLevel.equals(level.getExternalLevel())) { return level; }
        }
        throw new RuntimeException("No Value defined for external Level '" + externalLevel + "'");
    }

}
