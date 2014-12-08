package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.modig.core.exception.ApplicationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.xpath.XPathConstants.NODESET;

public abstract class GsakKodeParser implements Serializable {

    protected static Document parseDocument(InputStream input) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
        } catch (Exception e) {
            throw new ApplicationException("Kunne ikke laste gsak kodeverk", e);
        }
    }

    protected static String getParentNodeValue(Node item, String id) {
        return getNodeValue(item.getParentNode(), id);
    }

    protected static String getNodeValue(Node item, String id) {
        Node namedItem = item.getAttributes().getNamedItem(id);
        if (namedItem == null) {
            return getParentNodeValue(item, id);
        }
        return namedItem.getNodeValue();
    }

    protected static List<Node> compileAndEvaluate(Document document, String expression) {
        try {
            NodeList nodeset = (NodeList) compileXpath(expression).evaluate(document, NODESET);
            List<Node> result = new ArrayList<>();
            for (int i = 0; i < nodeset.getLength(); i++) {
                result.add(nodeset.item(i));
            }
            return result;
        } catch (Exception ex) {
            throw new ApplicationException("Kunne ikke parse gsak kodeverk", ex);
        }
    }

    protected static XPathExpression compileXpath(String expression) {
        try {
            return XPathFactory.newInstance().newXPath().compile(expression);
        } catch (XPathExpressionException e) {
            throw new ApplicationException("Kunne ikke lage xpath expression", e);
        }
    }

}
