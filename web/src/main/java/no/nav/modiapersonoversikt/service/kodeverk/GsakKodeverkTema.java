package no.nav.modiapersonoversikt.service.kodeverk;

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import no.nav.modiapersonoversikt.legacy.api.domain.saker.GsakKodeTema;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class GsakKodeverkTema implements Serializable {

    public static class Parser extends GsakKodeParser {
        private static final String KODE = "kode";
        private static final String DEKODE = "dekode";
        private static final String ER_GYLDIG = "erGyldig";
        private static final String DATO_TOM = "datoTom";
        private static final List<String> GODKJENTE_OPPGAVETYPER = asList("KONT_BRUK", "VURD_HENV", "VUR_KONS_YTE");

        private static GsakKodeTema.OppgaveType parseOppgavetype(Node node) {
            return new GsakKodeTema.OppgaveType(
                    getParentNodeValue(node, KODE),
                    getNodeValue(node, DEKODE),
                    Integer.valueOf(getNodeValue(node, "antallFristDager")));
        }

        private static GsakKodeTema.Prioritet parsePrioritet(Node node) {
            return new GsakKodeTema.Prioritet(getParentNodeValue(node, KODE), getNodeValue(node, DEKODE));
        }

        private static GsakKodeTema.Underkategori parseUnderkategori(Node node) {
                boolean erGyldig = attribute(node, ER_GYLDIG)
                        .map(Node::getNodeValue)
                        .map(Boolean::valueOf)
                        .orElse(true);
                LocalDate datoTom = attribute(node, DATO_TOM)
                        .map(Node::getNodeValue)
                        .map(LocalDate::parse)
                        .orElse(null);

                return new GsakKodeTema.Underkategori(getParentNodeValue(node, KODE), getParentNodeValue(node, DEKODE))
                        .withErGyldig(erGyldig)
                        .withDatoTom(datoTom);
        }

        public static List<GsakKodeTema.Tema> parse() {
            try (
                    InputStream isFagomrade = GsakKodeTema.class.getResourceAsStream("/xml/fagomrade.xml");
                    InputStream isOppgavetype = GsakKodeTema.class.getResourceAsStream("/xml/oppgaveT.xml");
                    InputStream isPrioritet = GsakKodeTema.class.getResourceAsStream("/xml/prioritetT.xml");
                    InputStream isUnderkategori = GsakKodeTema.class.getResourceAsStream("/xml/underkategori.xml")
            ) {
                Document gsakKoder = parseDocument(isFagomrade);
                List<Node> temaNodes = compileAndEvaluate(gsakKoder, "//fagomradeListe/fagomrade/gosys[@person='true' and not(@erGyldig = 'false')]");
                Function<Node, GsakKodeTema.Tema> temaTransformer = new NodeTemaTransformer(isOppgavetype, isPrioritet, isUnderkategori);

                return temaNodes.stream()
                        .map(temaTransformer)
                        .sorted(comparing(tema -> tema.tekst))
                        .collect(toList());

            } catch (Exception e) {
                throw new ApplicationException("Kunne ikke laste inn gsak kodeverk", e);
            }
        }

        private static final class NodeTemaTransformer implements Function<Node, GsakKodeTema.Tema> {

            private static Predicate<GsakKodeTema.OppgaveType> godkjenteKoder(final String fagomrade) {
                return oppgaveType -> GODKJENTE_OPPGAVETYPER.contains(oppgaveType.kode.replaceAll("_" + fagomrade + "$", ""));
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
            public GsakKodeTema.Tema apply(Node node) {
                String temaKode = getParentNodeValue(node, KODE);
                String dekode = getNodeValue(node, DEKODE);
                List<Node> oppgaveNoder = compileAndEvaluate(oppgaveDokument, "//oppgaveTListe/oppgaveT[@fagomrade='" + temaKode + "']/gosys[@person='true' and not(erGyldig='false')]");
                List<Node> prioritetNoder = compileAndEvaluate(prioritetDokument, "//prioritetTListe/prioritetT[@fagomrade='" + temaKode + "']/gosys");
                List<Node> underkategoriNoder = compileAndEvaluate(underkategoriDokument, "//underkategoriListe/underkategori[@fagomrade='" + temaKode + "' and not(@erGyldig = 'false')]/gosys");

                List<GsakKodeTema.Underkategori> underkategorier = underkategoriNoder.stream()
                        .map(Parser::parseUnderkategori)
                        .filter(GsakKodeTema.Underkategori::erGyldig)
                        .sorted(comparing(underkategori -> underkategori.tekst))
                        .collect(toList());
                List<GsakKodeTema.OppgaveType> oppgavetyper = oppgaveNoder.stream()
                        .map(Parser::parseOppgavetype)
                        .filter(godkjenteKoder(temaKode))
                        .collect(toList());
                List<GsakKodeTema.Prioritet> prioriteter = prioritetNoder.stream()
                        .map(Parser::parsePrioritet)
                        .collect(toList());

                return new GsakKodeTema.Tema(
                        temaKode,
                        dekode,
                        oppgavetyper,
                        prioriteter,
                        underkategorier
                );
            }
        }

    }

}
