package no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk;

import no.nav.modig.core.exception.ApplicationException;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static no.nav.modig.lang.collections.IterUtils.on;

/**
 * Tema kodeverk. Nøstet kodeverk for uthenting fra gsak
 */

public abstract class GsakKodeTema implements Serializable {

    public final String kode;
    public final String tekst;

    protected GsakKodeTema(String kode, String tekst) {
        this.kode = kode;
        this.tekst = tekst;
    }

    public static class Tema extends GsakKodeTema implements Serializable {
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

    public static class OppgaveType extends GsakKodeTema implements Serializable {
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

    public static class Prioritet extends GsakKodeTema implements Serializable {

        public Prioritet(String kode, String tekst) {
            super(kode, tekst);
        }
    }

    public static class Parser extends GsakKodeParser {
        private static final String KODE = "kode";
        private static final String DEKODE = "dekode";

        private static final Transformer<Node, OppgaveType> NODE_OPPGAVE_TYPE_TRANSFORMER = new Transformer<Node, OppgaveType>() {
            @Override
            public OppgaveType transform(Node node) {
                return new OppgaveType(
                        getParentNodeValue(node, KODE),
                        getNodeValue(node, DEKODE),
                        Integer.valueOf(getNodeValue(node, "antallFristDager")));
            }
        };
        private static final Transformer<Node, Prioritet> NODE_TIL_PRIORITET = new Transformer<Node, Prioritet>() {
            @Override
            public Prioritet transform(Node node) {
                return new Prioritet(getParentNodeValue(node, KODE), getParentNodeValue(node, DEKODE));
            }
        };

        public static List<Tema> parse() {
            try (
                    InputStream isFagomrade = GsakKodeTema.class.getResourceAsStream("/xml/fagomrade.xml");
                    InputStream isOppgavetype = GsakKodeTema.class.getResourceAsStream("/xml/oppgaveT.xml");
                    InputStream isPrioritet = GsakKodeTema.class.getResourceAsStream("/xml/prioritetT.xml")
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
                String temaKode = getParentNodeValue(node, KODE);
                String dekode = getNodeValue(node, DEKODE);
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
