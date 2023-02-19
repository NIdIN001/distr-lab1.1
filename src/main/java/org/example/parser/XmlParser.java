package org.example.parser;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.Map;

public interface XmlParser {
    Map<String, Integer> getCountOfTag(InputStream inputStream, String eventName, String tagName) throws XMLStreamException;
}
