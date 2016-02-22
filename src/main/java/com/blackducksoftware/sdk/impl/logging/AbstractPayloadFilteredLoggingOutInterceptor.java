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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * 
 */
public abstract class AbstractPayloadFilteredLoggingOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private int limit = 100 * 1024;

    private PrintWriter writer;

    public AbstractPayloadFilteredLoggingOutInterceptor(String phase) {
        super(phase);
        addBefore(StaxOutInterceptor.class.getName());
    }

    public AbstractPayloadFilteredLoggingOutInterceptor() {
        this(Phase.PRE_STREAM);
    }

    public AbstractPayloadFilteredLoggingOutInterceptor(int lim) {
        this();
        limit = lim;
    }

    public AbstractPayloadFilteredLoggingOutInterceptor(PrintWriter w) {
        this();
        writer = w;
    }

    public void setLimit(int lim) {
        limit = lim;
    }

    public int getLimit() {
        return limit;
    }

    public void handleMessage(Message message) throws Fault {
        final OutputStream os = message.getContent(OutputStream.class);
        if (os == null) { return; }

        if (getLogger().isLoggable(Level.INFO) || (writer != null)) {
            // Write the output while caching it for the log message
            final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
            message.setContent(OutputStream.class, newOut);
            newOut.registerCallback(new LoggingCallback(message, os));
        }
    }

    /**
     * Transform the string before display. The implementation in this class does nothing. Override this method if you
     * want to change the contents of the logged message before it is delivered to the output. For example, you can use
     * this to masking out sensitive information.
     * 
     * @param originalLogString
     *            the raw log message.
     * @return transformed data
     */
    protected String transform(String originalLogString) {
        return originalLogString;
    }

    class LoggingCallback implements CachedOutputStreamCallback {

        private final Message message;

        private final OutputStream origStream;

        public LoggingCallback(final Message msg, final OutputStream os) {
            message = msg;
            origStream = os;
        }

        public void onFlush(CachedOutputStream cos) {

        }

        public void onClose(CachedOutputStream cos) {
            String id = (String) message.getExchange().get(LoggingMessageSecure.ID_KEY);
            if (id == null) {
                id = LoggingMessageSecure.nextId();
                message.getExchange().put(LoggingMessageSecure.ID_KEY, id);
            }
            final LoggingMessageSecure buffer = createNewLoggingMessage(id);

            String encoding = (String) message.get(Message.ENCODING);

            if (encoding != null) {
                buffer.getEncoding().append(encoding);
            }

            String address = (String) message.get(Message.ENDPOINT_ADDRESS);
            if (address != null) {
                buffer.getAddress().append(address);
            }
            String ct = (String) message.get(Message.CONTENT_TYPE);
            if (ct != null) {
                buffer.getContentType().append(ct);
            }
            Object headers = message.get(Message.PROTOCOL_HEADERS);
            if (headers != null) {
                buffer.getHeader().append(headers);
            }

            if (cos.getTempFile() == null) {
                // buffer.append("Outbound Message:\n");
                if (cos.size() > limit) {
                    buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
                }
            } else {
                buffer.getMessage().append("Outbound Message (saved to tmp file):\n");
                buffer.getMessage().append("Filename: " + cos.getTempFile().getAbsolutePath() + "\n");
                if (cos.size() > limit) {
                    buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
                }
            }
            try {
                cos.writeCacheTo(buffer.getPayload(), limit);
            } catch (Exception ex) {
                // ignore
            }

            if (writer != null) {
                writer.println(transform(buffer.toString()));
            } else if (getLogger().isLoggable(Level.INFO)) {
                getLogger().info(transform(buffer.toString()));
            }
            try {
                // empty out the cache
                cos.lockOutputStream();
                cos.resetOut(null, false);
            } catch (Exception ex) {
                // ignore
            }
            message.setContent(OutputStream.class, origStream);
        }
    }

    abstract Logger getLogger();

    abstract LoggingMessageSecure createNewLoggingMessage(String id);

}
