/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adr.taskexecutor.test;

import com.adr.taskexecutor.common.Utils;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author adrian
 */
public class XPathTesting {

    public XPathTesting() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


     @Test
     public void hello() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = factory.newDocumentBuilder();

        Document doc = db.parse(new InputSource(Utils.getResourceReader("/com/adr/taskexecutor/samples/sheldon.xml")));

        XPath xpath = XPathFactory.newInstance().newXPath();

        NodeList nodes = (NodeList) xpath.evaluate("/ItemSearchResponse/Items/Item", doc.getDocumentElement(), XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); i++) {
            System.out.println("-->");
            System.out.println(xpath.evaluate("ASIN/text()", nodes.item(i), XPathConstants.STRING));
            System.out.println(xpath.evaluate("ItemAttributes/Author/text()", nodes.item(i), XPathConstants.STRING));
            System.out.println(xpath.evaluate("ItemAttributes/Title/text()", nodes.item(i), XPathConstants.STRING));
        }


     }

}