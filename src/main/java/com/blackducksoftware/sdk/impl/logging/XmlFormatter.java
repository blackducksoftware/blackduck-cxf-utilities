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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Pretty-prints xml, supplied as a string.
 * <p/>
 * eg. <code>
 * String formattedXml = new XmlFormatter().format("<tag><nested>hello</nested></tag>");
 * </code>
 */
public class XmlFormatter {

    public XmlFormatter() {
    }

    private Document parseXmlString(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Pretty-prints String containing XML using DOM Load and Save's LSSerializer. This method requires JRE 1.6+
     * 
     * Uses UTF-8 encoding
     * 
     * @param unformattedXml
     *            The String with the XML
     * @return The String containing the pretty printed version of the XML
     */
    public String prettyPrint(String unformattedXml) {
        final Document document = parseXmlString(unformattedXml);
        DOMImplementation domImplementation = document.getImplementation();
        DOMImplementationLS domImplementationLS;
        LSSerializer lsSerializer;
        // Verify the pretty print is supported - requires JRE 1.6+
        if (domImplementation.hasFeature("LS", "3.0") && domImplementation.hasFeature("Core", "2.0")) {
            domImplementationLS = (DOMImplementationLS) domImplementation.getFeature("LS", "3.0");
            lsSerializer = domImplementationLS.createLSSerializer();
            DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
            if (domConfiguration.canSetParameter("format-pretty-print", Boolean.TRUE)) {
                lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
            } else {
                throw new RuntimeException(
                        "DOMConfiguration 'format-pretty-print' parameter can't be set. This method of pretty print requires JRE 1.6+");
            }
        } else {
            throw new RuntimeException(
                    "DOM 3.0 LS or DOM 2.0 Core feature(s) not supported. This method of pretty print requires JRE 1.6+");
        }
        LSOutput lsOutput = domImplementationLS.createLSOutput();
        lsOutput.setEncoding("UTF-8");
        StringWriter stringWriter = new StringWriter();
        lsOutput.setCharacterStream(stringWriter);
        lsSerializer.write(document, lsOutput);
        return stringWriter.toString();
    }

}
