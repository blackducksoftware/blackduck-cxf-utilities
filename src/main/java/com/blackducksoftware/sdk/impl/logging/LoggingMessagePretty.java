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

import org.xml.sax.SAXParseException;

public class LoggingMessagePretty extends LoggingMessageSecure {
    public LoggingMessagePretty(String h, String i) {
        super(h, i);
    }

    @Override
    protected StringBuilder formatPayload(StringBuilder builder) {
    	try {
    		return new StringBuilder(new XmlFormatter().prettyPrint(getPayload().toString()));
    	} catch (RuntimeException e) {
    		// PROTEX-9322:  If we get a parse error while trying to format the payload as XML,
    		// simply return the payload unformatted.
    		if ((e.getCause() != null) && (e.getCause() instanceof SAXParseException)) {
    			return getPayload();
    		} else {
    			throw e;
    		}
    	}
    }

}
