package info.sarihh.antiinferencehub;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class JDBCUtil {

    public static final String serialize(Document doc) throws IOException {
        StringWriter writer = new StringWriter();
        OutputFormat format = new OutputFormat();
        format.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(writer, format);
        serializer.serialize(doc);
        return writer.getBuffer().toString();
    }

    public static final Document toDoc(ResultSet rs)
            throws
            SQLException,
            FactoryConfigurationError,
            ParserConfigurationException,
            SAXException,
            IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String xml = toXML(rs);
        StringReader reader = new StringReader(xml);
        InputSource source = new InputSource(reader);
        return builder.parse(source);
    }

    public static final Document toDocument(ResultSet rs)
            throws ParserConfigurationException, SQLException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element results = doc.createElement("Results");
        doc.appendChild(results);
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        while (rs.next()) {
            Element row = doc.createElement("Row");
            results.appendChild(row);
            for (int i = 1; i <= colCount; i++) {
                String columnName = rsmd.getColumnName(i);
                Object value = rs.getObject(i);
                Element node = doc.createElement(columnName);
                node.appendChild(doc.createTextNode(value.toString()));
                row.appendChild(node);
            }
        }
        return doc;
    }

    public static final String toXML(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        StringBuffer xml = new StringBuffer();
        xml.append("<Results>");
        while (rs.next()) {
            xml.append("<Row>");
            for (int i = 1; i <= colCount; i++) {
                String columnName = rsmd.getColumnName(i);
                Object value = rs.getObject(i);
                xml.append("<" + columnName + ">");
                if (value != null) {
                    xml.append(value.toString().trim());
                }
                xml.append("</" + columnName + ">");
            }
            xml.append("</Row>");
        }
        xml.append("</Results>");
        return xml.toString();
    }
}
