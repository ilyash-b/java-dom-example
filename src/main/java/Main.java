import org.apache.commons.collections4.iterators.NodeListIterator;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    private void update(Node node) {
        String category = ((Element) node).getElementsByTagName("category").item(0).getTextContent();
        System.err.println("Category " + category);

        if (!category.equals("fun")) {
            System.err.println("Skipping category " + category);
            return;
        }

        NamedNodeMap attributes = node.getAttributes();

        String oldCode = attributes.getNamedItem("code").getTextContent();
        System.err.println("Old Code " + oldCode);

        String newCode = oldCode.replaceFirst("^1-1000-", "1-2000-");

        if (oldCode.equals(newCode)) {
            return;
        }

        Attr oldCodeAttr = node.getOwnerDocument().createAttribute("prev-code");
        oldCodeAttr.setTextContent(oldCode);
        attributes.setNamedItem(oldCodeAttr);

        System.err.println("New Code " + newCode);
        attributes.getNamedItem("code").setTextContent(newCode);
    }

    void run() throws ParserConfigurationException, SAXException, IOException, TransformerException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("test.xml")) {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = documentBuilder.parse(stream);

            NodeList items = doc.getElementsByTagName("item");
            new NodeListIterator(items).forEachRemaining(this::update);

            // Based on https://docs.oracle.com/javase/tutorial/jaxp/xslt/writingDom.html
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(System.out));
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        new Main().run();
    }
}
