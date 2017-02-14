package com.mythosapps.time15;

import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.util.ConfigXmlParser;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by andreas on 09.02.17.
 */
public class ConfigXmlParserTest extends TestCase {

    private static final String XML_DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private ConfigXmlParser parser = new ConfigXmlParser();

    public void testLoadConfigOneElement() {

        String config = XML_DECL +
                "<config>\n" +
                "  <task>\n" +
                "    <displayString>Arbeit</displayString>\n" +
                "    <color>12</color>\n" +
                "    <dueMinutes>720</dueMinutes>\n" +
                "    <beginEndType>true</beginEndType>\n" +
                "  </task>\n" +
                "</config>\n";

        List<KindOfDay> list = parser.parse(new ByteArrayInputStream(config.getBytes()));

        assertNotNull(list);
        KindOfDay task = list.get(0);
        assertEquals("Arbeit", task.getDisplayString());
        assertEquals(12, task.getColor());
        assertEquals(720, task.getDueMinutes());
        assertEquals(720, task.getDefaultDue().toMinutes());
        assertEquals(true, task.isBeginEndType());
    }

    public void testLoadConfigOneElementTrimValues() {
        String config = XML_DECL +
                "<config>\n" +
                "  <task>\n" +
                "    <displayString> Arbeit Vollzeit </displayString>\n" +
                "    <color>  12 </color>\n" +
                "    <dueMinutes> 720    </dueMinutes>\n" +
                "    <beginEndType> \n true \n \n</beginEndType>\n" +
                "  </task>\n" +
                "</config>\n";

        List<KindOfDay> list = parser.parse(new ByteArrayInputStream(config.getBytes()));

        assertNotNull(list);
        KindOfDay task = list.get(0);
        assertEquals("Arbeit Vollzeit", task.getDisplayString());
        assertEquals(12, task.getColor());
        assertEquals(720, task.getDueMinutes());
        assertEquals(720, task.getDefaultDue().toMinutes());
        assertEquals(true, task.isBeginEndType());
    }

    public void testLoadConfigMoreElements() {

        String config = XML_DECL +
                "<config>\n" +
                "  <task>\n" +
                "    <displayString>Arbeit</displayString>\n" +
                "    <color>12</color>\n" +
                "    <dueMinutes>720</dueMinutes>\n" +
                "    <beginEndType>true</beginEndType>\n" +
                "  </task>\n" +
                "  <task>\n" +
                "    <displayString>Feiertag</displayString>\n" +
                "    <color>999</color>\n" +
                "    <dueMinutes>560</dueMinutes>\n" +
                "    <beginEndType>false</beginEndType>\n" +
                "  </task>\n" +
                "</config>\n";

        List<KindOfDay> list = parser.parse(new ByteArrayInputStream(config.getBytes()));

        assertNotNull(list);
        KindOfDay task = list.get(0);
        assertEquals("Arbeit", task.getDisplayString());
        assertEquals(12, task.getColor());
        assertEquals(720, task.getDueMinutes());
        assertEquals(720, task.getDefaultDue().toMinutes());
        assertEquals(true, task.isBeginEndType());

        task = list.get(1);
        assertEquals("Feiertag", task.getDisplayString());
        assertEquals(999, task.getColor());
        assertEquals(560, task.getDueMinutes());
        assertEquals(560, task.getDefaultDue().toMinutes());
        assertEquals(false, task.isBeginEndType());
    }
}
