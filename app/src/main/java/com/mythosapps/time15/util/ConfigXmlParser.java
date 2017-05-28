package com.mythosapps.time15.util;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import com.mythosapps.time15.types.KindOfDay;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by andreas on 09.02.17.
 */
public class ConfigXmlParser {

    // XML
    // <config>
    //   <task>
    //     <displayString>
    //     <color>
    //     <beginEndType>

    private static final String XML_TASK = "task";
    private static final String XML_NAME = "displayString";
    private static final String XML_COLOR = "color";
    private static final String XML_BEGIN_END = "beginEndType";

    public List<KindOfDay> parse(InputStream fis) {
        Document doc = getDocument(fis);

        List<KindOfDay> result = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagName(XML_TASK);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element e = (Element) nodeList.item(i);
            String displayString = getValue(e, XML_NAME);
            int color = Integer.valueOf(getValue(e, XML_COLOR));
            boolean beginEndType = Boolean.valueOf(getValue(e, XML_BEGIN_END));

            KindOfDay task = new KindOfDay(displayString, color, beginEndType);
            result.add(task);
        }

        return result;
    }

    public Document getDocument(InputStream inputStream) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(inputStream);
            document = db.parse(inputSource);
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        return document;
    }

    public Document getDocumentFromResource(String resourceFileName, Activity activity) {

        AssetManager manager = activity.getAssets();
        InputStream stream;
        Document doc = null;

        try {
            stream = manager.open(resourceFileName);
            doc = getDocument(stream);
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
        } finally {
            manager.close();
        }
        return doc;
    }

    private String getValue(Element item, String name) {
        NodeList nodes = item.getElementsByTagName(name);
        return this.getTextNodeValue(nodes.item(0));
    }

    private final String getTextNodeValue(Node node) {
        Node child;
        if (node != null) {
            if (node.hasChildNodes()) {
                child = node.getFirstChild();
                while (child != null) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        String value = child.getNodeValue();
                        return value.trim();
                    }
                    child = child.getNextSibling();
                }
            }
        }
        return "";
    }
}
