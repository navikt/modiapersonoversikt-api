package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import no.nav.sbl.util.EnvironmentUtils;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.split;

public class CmsSkrivestotteEnonic implements CmsSkrivestotte {

    private static final String APPRES_URL = "%s/app/modiabrukerdialog/skrivestotte";

    private String appresUrl;

    public CmsSkrivestotteEnonic() {
        appresUrl = System.getProperty("appres.cms.url");
    }

    @Override
    public List<SkrivestotteTekst> hentSkrivestotteTekster() {
        try {
            Future<Content> resp = Async.newInstance().execute(Request.Get(String.format(APPRES_URL, appresUrl)));

            Content content = resp.get(10000, TimeUnit.SECONDS);

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(content.asStream());

            NodeList nodeset = document.getElementsByTagName("contentdata");

            List<SkrivestotteTekst> skrivestotteTekster = new ArrayList<>();
            for (int i = 0; i < nodeset.getLength(); i++) {
                Node node = nodeset.item(i);
                skrivestotteTekster.add(new SkrivestotteTekst(
                        String.valueOf(i),
                        getChildValueByName(node, "overskrift"),
                        lagInnholdMap(getChildrenWithName(node, "innhold")),
                        split(getChildValueByName(node, "tags"), " ")));
            }
            return skrivestotteTekster;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getChildValueByName(Node parent, String name) {
        Node current = parent.getFirstChild();
        while (true) {
            if (name.equals(current.getNodeName())) {
                return current.getTextContent();
            }
            current = current.getNextSibling();
        }
    }

    private static List<Node> getChildrenWithName(Node parent, String name) {
        ArrayList<Node> result = new ArrayList<>();
        NodeList childNodes = parent.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (name.equals(childNodes.item(i).getNodeName())) {
                result.add(childNodes.item(i));
            }
        }
        return result;
    }

    private static Map<String, String> lagInnholdMap(List<Node> nodes) {
        HashMap<String, String> result = new HashMap<>();
        for (Node node : nodes) {
            result.put(getChildValueByName(node, "locale"), getChildValueByName(node, "fritekst"));
        }
        return result;
    }

}
