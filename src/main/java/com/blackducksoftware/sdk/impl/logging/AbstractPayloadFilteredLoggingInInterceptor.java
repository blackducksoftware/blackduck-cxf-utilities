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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * A simple logging handler which outputs the bytes of the message to the Logger.
 */
public abstract class AbstractPayloadFilteredLoggingInInterceptor extends AbstractPhaseInterceptor<Message> {

    private int limit = 1000 * 1024;

    private PrintWriter writer;

    public AbstractPayloadFilteredLoggingInInterceptor() {
        super(Phase.RECEIVE);
    }

    public AbstractPayloadFilteredLoggingInInterceptor(String phase) {
        super(phase);
    }

    public AbstractPayloadFilteredLoggingInInterceptor(int lim) {
        this();
        limit = lim;
    }

    public AbstractPayloadFilteredLoggingInInterceptor(PrintWriter w) {
        this();
        writer = w;
    }

    public void setPrintWriter(PrintWriter w) {
        writer = w;
    }

    public PrintWriter getPrintWriter() {
        return writer;

    }

    public void setLimit(int lim) {
        limit = lim;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        if ((writer != null) || getLogger().isLoggable(Level.INFO)) {
            logging(message);
        }

    }

    private void logging(Message message) throws Fault {
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
        String ct = (String) message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        Object headers = message.get(Message.PROTOCOL_HEADERS);

        if (headers != null) {
            buffer.getHeader().append(headers);
        }
        String uri = (String) message.get(Message.ENDPOINT_ADDRESS);
        if (uri != null) {
            buffer.getAddress().append(uri);
        }

        InputStream is = message.getContent(InputStream.class);
        if (is != null) {
            CachedOutputStream bos = new CachedOutputStream();
            try {
                IOUtils.copy(is, bos);

                bos.flush();
                is.close();

                message.setContent(InputStream.class, bos.getInputStream());
                if (bos.getTempFile() != null) {
                    // large thing on disk...
                    buffer.getMessage().append("\nMessage (saved to tmp file):\n");
                    buffer.getMessage().append("Filename: " + bos.getTempFile().getAbsolutePath() + "\n");
                }
                if (bos.size() > limit) {
                    buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
                }
                bos.writeCacheTo(buffer.getPayload(), limit);

                bos.close();
            } catch (IOException e) {
                throw new Fault(e);
            }
        }

        if (writer != null) {
            writer.println(buffer.toString());
        } else if (getLogger().isLoggable(Level.INFO)) {
            getLogger().info(buffer.toString());
        }
    }

    abstract Logger getLogger();

    abstract LoggingMessageSecure createNewLoggingMessage(String id);

}
