package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.split;

public class CmsSkrivestotte {

    private static final String APPRES_URL = "%s/app/modiabrukerdialog/%s/skrivestotte";

    public static List<Hjelpetekst> hentHjelpetekster(String appresUrl, String locale) {
        try {
            Future<Content> resp = Async.newInstance().execute(Request.Get(String.format(APPRES_URL, appresUrl, locale)));

            Content content = resp.get(10000, TimeUnit.SECONDS);

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(content.asStream());

            NodeList nodeset = document.getElementsByTagName("contentdata");

            List<Hjelpetekst> hjelpetekster = new ArrayList<>();
            for (int i = 0; i < nodeset.getLength(); i++) {
                Node node = nodeset.item(i);
                hjelpetekster.add(new Hjelpetekst(
                        getChildValueByName(node, "key"),
                        getChildValueByName(node, "overskrift"),
                        getChildValueByName(node, "fritekst"),
                        split(getChildValueByName(node, "tags"), " ")));
            }
            return hjelpetekster;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static String getChildValueByName(Node node, String name) {
        Node current = node.getFirstChild();
        while (true) {
            if (name.equals(current.getNodeName())) {
                return current.getTextContent();
            }
            current = current.getNextSibling();
        }
    }

}
