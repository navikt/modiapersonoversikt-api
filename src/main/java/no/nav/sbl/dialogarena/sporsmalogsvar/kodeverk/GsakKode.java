package no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk;

import no.nav.modig.core.exception.ApplicationException;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.builder.ToStringBuilder;
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

import static java.util.Collections.unmodifiableList;
import static javax.xml.xpath.XPathConstants.NODESET;
import static no.nav.modig.lang.collections.IterUtils.on;

/**
 * Tema kodeverk. Nøstet kodeverk for uthenting fra gsak
 */

public abstract class GsakKode implements Serializable {

    public final String kode;
    public final String tekst;

    protected GsakKode(String kode, String tekst) {
        this.kode = kode;
        this.tekst = tekst;
    }

    public static class Tema extends GsakKode implements Serializable {
        public final List<OppgaveType> oppgaveTyper;
        public final List<Prioritet> prioriteter;

        public Tema(String kode, String tekst, List<OppgaveType> oppgaveTyper, List<Prioritet> prioritets) {
            super(kode, tekst);
            this.oppgaveTyper = unmodifiableList(oppgaveTyper);
            this.prioriteter = unmodifiableList(prioritets);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("kode", kode)
                    .append("tekst", tekst)
                    .append("oppgaveTyper", oppgaveTyper)
                    .toString();
        }
    }

    public static class OppgaveType extends GsakKode implements Serializable {
        public final Integer dagerFrist;

        public OppgaveType(String kode, String tekst, Integer dagerFrist) {
            super(kode, tekst);
            this.dagerFrist = dagerFrist;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("kode", kode)
                    .append("tekst", tekst)
                    .append("dagerFrist", dagerFrist)
                    .toString();
        }
    }

    public static class Prioritet extends GsakKode implements Serializable {

        public Prioritet(String kode, String tekst) {
            super(kode, tekst);
        }
    }

    public static class Parser {
        private static final String GSAK_KODE = "kode";
        private static final String GSAK_DEKODE = "dekode";
        private static final Transformer<Node, OppgaveType> NODE_OPPGAVE_TYPE_TRANSFORMER = new Transformer<Node, OppgaveType>() {
            @Override
            public OppgaveType transform(Node node) {
                return new OppgaveType(
                        getParentNodeValue(node, GSAK_KODE),
                        getNodeValue(node, GSAK_DEKODE),
                        Integer.valueOf(getNodeValue(node, "antallFristDager")));
            }
        };
        private static final Transformer<Node, Prioritet> NODE_TIL_PRIORITET = new Transformer<Node, Prioritet>() {
            @Override
            public Prioritet transform(Node node) {
                return new Prioritet(getParentNodeValue(node, GSAK_KODE), getParentNodeValue(node, GSAK_DEKODE));
            }
        };

        private static Document parseDocument(InputStream isFagomrade) {
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(isFagomrade);
            } catch (Exception e) {
                throw new ApplicationException("Kunne ikke laste gsak kodeverk", e);
            }
        }

        private static String getParentNodeValue(Node item, String id) {
            return getNodeValue(item.getParentNode(), id);
        }

        private static String getNodeValue(Node item, String id) {
            Node namedItem = item.getAttributes().getNamedItem(id);
            if(namedItem == null){
                return getParentNodeValue(item, id);
            }
            return namedItem.getNodeValue();
        }

        private static List<Node> compileAndEvaluate(Document document, String expression) {
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

        private static XPathExpression compileXpath(String expression) {
            try {
                return XPathFactory.newInstance().newXPath().compile(expression);
            } catch (XPathExpressionException e) {
                throw new ApplicationException("Kunne ikke lage xpath expression", e);
            }
        }

        public static List<Tema> parse() {
            try (
                    InputStream isFagomrade = GsakKode.class.getResourceAsStream("/xml/fagomrade.xml");
                    InputStream isOppgavetype = GsakKode.class.getResourceAsStream("/xml/oppgaveT.xml");
                    InputStream isPrioritet = GsakKode.class.getResourceAsStream("/xml/prioritetT.xml")
            ) {
                Document gsakKoder = parseDocument(isFagomrade);
                List<Node> temaNodes = compileAndEvaluate(gsakKoder, "//fagomradeListe/fagomrade/gosys[@person='true' and not(@erGyldig = 'false')]");
                return on(temaNodes).map(new NodeTemaTransformer(isOppgavetype, isPrioritet)).collect();
            } catch (Exception e) {
                throw new ApplicationException("Kunne ikke laste inn gsak kodeverk", e);
            }
        }


        private static final class NodeTemaTransformer implements Transformer<Node, Tema> {
            private final Document oppgaveDokument;
            private final Document prioritetDokument;

            public NodeTemaTransformer(InputStream isOppgavetype, InputStream isPrioritet) {
                oppgaveDokument = parseDocument(isOppgavetype);
                prioritetDokument = parseDocument(isPrioritet);
            }

            @Override
            public Tema transform(Node node) {
                String temaKode = getParentNodeValue(node, GSAK_KODE);
                String dekode = getNodeValue(node, GSAK_DEKODE);
                List<Node> oppgaveNoder = compileAndEvaluate(oppgaveDokument, "//oppgaveTListe/oppgaveT[@fagomrade='" + temaKode + "']/gosys[@person='true' and not(erGyldig='false')]");
                List<Node> prioritetNoder = compileAndEvaluate(prioritetDokument, "//prioritetTListe/prioritetT[@fagomrade='" + temaKode + "']/gosys");
                return new Tema(temaKode,
                        dekode,
                        on(oppgaveNoder).map(NODE_OPPGAVE_TYPE_TRANSFORMER).collect(),
                        on(prioritetNoder).map(NODE_TIL_PRIORITET).collect());
            }
        }
    }


}

