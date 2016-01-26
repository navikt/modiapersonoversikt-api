package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;

public class GsakKodeverkTema implements Serializable {

    public static class Parser extends GsakKodeParser {
        private static final String KODE = "kode";
        private static final String DEKODE = "dekode";
        private static final List<String> GODKJENTE_OPPGAVETYPER = asList("KONT_BRUK", "VURD_HENV", "VUR_KONS_YTE");

        private static final Transformer<Node, GsakKodeTema.OppgaveType> NODE_OPPGAVE_TYPE_TRANSFORMER = new Transformer<Node, GsakKodeTema.OppgaveType>() {
            @Override
            public GsakKodeTema.OppgaveType transform(Node node) {
                return new GsakKodeTema.OppgaveType(
                        getParentNodeValue(node, KODE),
                        getNodeValue(node, DEKODE),
                        Integer.valueOf(getNodeValue(node, "antallFristDager")));
            }
        };
        private static final Transformer<Node, GsakKodeTema.Prioritet> NODE_TIL_PRIORITET = new Transformer<Node, GsakKodeTema.Prioritet>() {
            @Override
            public GsakKodeTema.Prioritet transform(Node node) {
                return new GsakKodeTema.Prioritet(getParentNodeValue(node, KODE), getNodeValue(node, DEKODE));
            }
        };
        private static final Transformer<Node, GsakKodeTema.Underkategori> NODE_TIL_UNDERKATEGORI = new Transformer<Node, GsakKodeTema.Underkategori>() {
            @Override
            public GsakKodeTema.Underkategori transform(Node node) {
                return new GsakKodeTema.Underkategori(getParentNodeValue(node, KODE), getParentNodeValue(node, DEKODE));
            }
        };

        public static List<GsakKodeTema.Tema> parse() {
            try (
                    InputStream isFagomrade = GsakKodeTema.class.getResourceAsStream("/xml/fagomrade.xml");
                    InputStream isOppgavetype = GsakKodeTema.class.getResourceAsStream("/xml/oppgaveT.xml");
                    InputStream isPrioritet = GsakKodeTema.class.getResourceAsStream("/xml/prioritetT.xml");
                    InputStream isUnderkategori = GsakKodeTema.class.getResourceAsStream("/xml/underkategori.xml")
            ) {
                Document gsakKoder = parseDocument(isFagomrade);
                List<Node> temaNodes = compileAndEvaluate(gsakKoder, "//fagomradeListe/fagomrade/gosys[@person='true' and not(@erGyldig = 'false')]");
                return on(temaNodes).map(new NodeTemaTransformer(isOppgavetype, isPrioritet, isUnderkategori))
                        .collect(compareWith(GsakKodeTema.TEKST));
            } catch (Exception e) {
                throw new ApplicationException("Kunne ikke laste inn gsak kodeverk", e);
            }
        }

        private static final class NodeTemaTransformer implements Transformer<Node, GsakKodeTema.Tema> {

            private static Predicate<? super GsakKodeTema.OppgaveType> godkjenteKoder(final String fagomrade) {
                return new Predicate<GsakKodeTema.OppgaveType>() {
                    @Override
                    public boolean evaluate(GsakKodeTema.OppgaveType oppgaveType) {
                        return GODKJENTE_OPPGAVETYPER.contains(oppgaveType.kode.replaceAll("_" + fagomrade + "$", ""));
                    }
                };
            }

            private final Document oppgaveDokument;
            private final Document prioritetDokument;
            private final Document underkategoriDokument;

            public NodeTemaTransformer(InputStream isOppgavetype, InputStream isPrioritet, InputStream isUnderkategori) {
                oppgaveDokument = parseDocument(isOppgavetype);
                prioritetDokument = parseDocument(isPrioritet);
                underkategoriDokument = parseDocument(isUnderkategori);
            }

            @Override
            public GsakKodeTema.Tema transform(Node node) {
                String temaKode = getParentNodeValue(node, KODE);
                String dekode = getNodeValue(node, DEKODE);
                List<Node> oppgaveNoder = compileAndEvaluate(oppgaveDokument, "//oppgaveTListe/oppgaveT[@fagomrade='" + temaKode + "']/gosys[@person='true' and not(erGyldig='false')]");
                List<Node> prioritetNoder = compileAndEvaluate(prioritetDokument, "//prioritetTListe/prioritetT[@fagomrade='" + temaKode + "']/gosys");
                List<Node> underkategoriNoder = compileAndEvaluate(underkategoriDokument, "//underkategoriListe/underkategori[@fagomrade='" + temaKode + "' and not(@erGyldig = 'false')]/gosys[not(@erGyldig='false')]");

                List<GsakKodeTema.Underkategori> underkategoriList = on(underkategoriNoder).map(NODE_TIL_UNDERKATEGORI).collect(compareWith(new Transformer<GsakKodeTema.Underkategori, String>() {
                    @Override
                    public String transform(GsakKodeTema.Underkategori underkategori) {
                        return underkategori.tekst;
                    }
                }));

                return new GsakKodeTema.Tema(
                        temaKode,
                        dekode,
                        on(oppgaveNoder).map(NODE_OPPGAVE_TYPE_TRANSFORMER).filter(godkjenteKoder(temaKode)).collect(),
                        on(prioritetNoder).map(NODE_TIL_PRIORITET).collect(),
                        underkategoriList
                );
            }
        }

    }

}
