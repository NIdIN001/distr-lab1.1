package org.example.parser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.*;

public class StaxXmlParser implements XmlParser {

    private static final XMLInputFactory factory = XMLInputFactory.newFactory();

    @Override
    public Map<String, Integer> getCountOfTag(InputStream inputStream, String nodeName, String tagName) throws XMLStreamException {
        Map<String, Integer> tagCountMap = new HashMap<>();

        XMLEventReader reader = createReader(inputStream);

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            if (isEventIsStartElementAndNameEquals(event, nodeName)) {
                var user = event.asStartElement().getAttributeByName(new QName(tagName));
                tagCountMap.merge(user.getValue(), 1, Integer::sum);
            }
        }

        return tagCountMap;
    }

    private static boolean isEventIsStartElementAndNameEquals(XMLEvent event, String name) {
        return event instanceof StartElement startElement && startElement.getName().toString().equals(name);
    }

    private static XMLEventReader createReader(InputStream inputStream) throws XMLStreamException {
        return factory.createXMLEventReader(inputStream);
    }
}
