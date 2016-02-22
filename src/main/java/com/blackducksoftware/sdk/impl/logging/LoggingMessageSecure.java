package com.blackducksoftware.sdk.impl.logging;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * Modifications * Copyright (C) 2009, 2010 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LoggingMessageSecure {
    public static final String ID_KEY = LoggingMessageSecure.class.getName() + ".ID";

    private static final AtomicInteger ID = new AtomicInteger();

    private final String logHeading;

    private final StringBuilder address = new StringBuilder();

    private final StringBuilder contentType = new StringBuilder();

    private final StringBuilder encoding = new StringBuilder();

    private final StringBuilder header = new StringBuilder();

    private final StringBuilder message = new StringBuilder();

    private final StringBuilder payload = new StringBuilder();

    private final String id;

    public LoggingMessageSecure(String h, String i) {
        logHeading = h;
        id = i;
    }

    public static String nextId() {
        return Integer.toString(ID.incrementAndGet());
    }

    public StringBuilder getAddress() {
        return address;
    }

    public StringBuilder getEncoding() {
        return encoding;
    }

    public StringBuilder getHeader() {
        return header;
    }

    public StringBuilder getContentType() {
        return contentType;
    }

    public StringBuilder getMessage() {
        return message;
    }

    public StringBuilder getPayload() {
        return payload;
    }

    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(logHeading).append(" ID: ").append(id);
        if (address.length() > 0) {
            buffer.append(logHeading).append(" Address: ");
            buffer.append(address);
        }
        buffer.append(logHeading).append(" Encoding: ");
        buffer.append(encoding);
        buffer.append(logHeading).append(" Content-Type: ");
        buffer.append(contentType);
        buffer.append(logHeading).append(" Headers:  ");
        buffer.append(header);
        if (message.length() > 0) {
            buffer.append("\nMessages: ");
            buffer.append(message);
        }
        buffer.append("\n");
        buffer.append(logHeading).append("Payload: ");

        buffer.append(formatPayload(payload));

        return buffer.toString();
    }

    abstract StringBuilder formatPayload(StringBuilder payload);

}
