package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.lang.System.getProperty;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.lucene.document.Field.Store.YES;
import static org.joda.time.DateTime.now;

public class MeldingerSokImpl implements MeldingerSok {

    private static final Logger logger = LoggerFactory.getLogger(MeldingerSokImpl.class);

    public static final String DEFAULT_TIME_TO_LIVE_MINUTES = "10";
    public static final String TIME_TO_LIVE_MINUTES_PROPERTY = "meldingersok.time.to.live.minutes";
    public static final String LUCENE_SPECIAL_CHARS = "[\\\\+\\!\\(\\)\\:\\^\\[\\]\\{\\}\\~\\?\\=\\/\\|\\.\"]+";
    public static final Pattern LUCENE_PATTERN = Pattern.compile(LUCENE_SPECIAL_CHARS);
    public static final String REPLACEMENT_STRING = "";

    private static final String ID = "id";
    private static final String BEHANDLINGS_ID = "behandlingsId";
    private static final String FRITEKST = "fritekst";
    private static final String TEMAGRUPPE = "temagruppe";
    private static final String ARKIVTEMA = "arkivtema";
    private static final String DATO = "dato";
    private static final String NAVIDENT = "navident";
    private static final String STATUSTEKST = "statustekst";
    private static final String IKONTEKST = "ikontekst";
    private static final String LEST_STATUS = "leststatus";
    private static final String KANAL = "kanal";
    private static final String SKREVET_AV_NAVN = "skrevetavnavn";
    private static final String JOURNALFORT_AV_NAVN = "journalfortavnavn";
    private static final String JOURNALFORT_AV_IDENT = "journalfortavident";
    private static final String JOURNALFORT_DATO = "journalfortdato";
    private static final String JOURNALFORT_SAKSID = "journalfortsaksid";
    private static final String FERDIGSTILT_UTEN_SVAR_DATO = "ferdigstiltutensvardato";
    private static final String FEILSENDT_DATO = "feilsendtdato";
    private static final String KONTORSPERRET_DATO = "kontorsperredato";

    private static final String[] FIELDS = new String[]{
            FRITEKST,
            TEMAGRUPPE,
            ARKIVTEMA,
            DATO,
            NAVIDENT,
            STATUSTEKST,
            LEST_STATUS,
            KANAL,
            SKREVET_AV_NAVN,
            JOURNALFORT_AV_NAVN,
            JOURNALFORT_AV_IDENT,
            JOURNALFORT_DATO,
            JOURNALFORT_SAKSID};
    private static final StandardAnalyzer ANALYZER = new StandardAnalyzer();

    private final Integer timeToLiveMinutes;

    protected Map<String, MeldingerCacheEntry> cache = new ConcurrentHashMap<>();

    public MeldingerSokImpl() {
        timeToLiveMinutes = Integer.valueOf(getProperty(TIME_TO_LIVE_MINUTES_PROPERTY, DEFAULT_TIME_TO_LIVE_MINUTES));
    }

    @Override
    public void indekser(String fnr, List<Melding> meldinger) {
        String navIdent = getSubjectHandler().getUid();
        String key = key(fnr, navIdent);

        List<Melding> transformerteMeldinger = meldinger.stream().map((melding) -> {
            melding.visningsDatoTekst = ofNullable(melding.getVisningsDato()).map(DateUtils::toString).orElse("");
            melding.journalfortDatoTekst = ofNullable(melding.journalfortDato).map(DateUtils::toString).orElse("");
            melding.ferdigstiltUtenSvarDatoTekst = ofNullable(melding.ferdigstiltUtenSvarDato).map(DateUtils::toString).orElse("");
            melding.markertSomFeilsendtDatoTekst = ofNullable(melding.markertSomFeilsendtDato).map(DateUtils::toString).orElse("");
            melding.kontorsperretDatoTekst = ofNullable(melding.kontorsperretDato).map(DateUtils::toString).orElse("");
            return melding;
        }).collect(toList());

        MeldingerCacheEntry cacheEntry = new MeldingerCacheEntry(
                transformerteMeldinger,
                indekser(transformerteMeldinger),
                now()
        );
        cache.put(key, cacheEntry);
    }

    @Override
    public List<Traad> sok(final String fnr, String soketekst) throws IkkeIndeksertException {
        try {
            final String navIdent = getSubjectHandler().getUid();
            final String key = key(fnr, navIdent);

            if (!cache.containsKey(key)) {
                throw new IkkeIndeksertException(String.format("Man må kalle %s.indekser før man kan søke", MeldingerSokImpl.class.getName()));
            }
            MeldingerCacheEntry entry = cache.get(key);

            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(entry.directory));
            TopScoreDocCollector collector = TopScoreDocCollector.create(1000);

            Query query = queryParser().parse(query(soketekst));
            searcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new NullFragmenter());

            Map<String, MeldingerSokResultat> resultat = hentResultat(searcher, ANALYZER, highlighter, soketekst, hits);

            return lagTraader(key, resultat);

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static QueryParser queryParser() {
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(FIELDS, ANALYZER);
        queryParser.setDefaultOperator(QueryParser.Operator.AND);
        queryParser.setAllowLeadingWildcard(true);
        return queryParser;
    }

    private List<Traad> lagTraader(String key, Map<String, MeldingerSokResultat> resultat) {
        MeldingerCacheEntry cacheEntry = cache.get(key);
        final Set<String> behandlingsIder = resultat.keySet();
        final List<Melding> opprinneligeMeldinger = cacheEntry.meldinger;
        final Map<String, List<Melding>> opprinneligeTraader = cacheEntry.traader;

        Map<String, List<Melding>> traader = opprinneligeMeldinger.stream()
                .filter(melding -> behandlingsIder.contains(melding.id))
                .map(highlighting(resultat))
                .collect(groupingBy(Melding::getTraadId));

        return traader.entrySet().stream()
                .map(entry -> new Traad(entry.getKey(), opprinneligeTraader.get(entry.getKey()).size(), entry.getValue()))
                .sorted(comparing(Traad::getDato).reversed())
                .collect(toList());
    }

    @Override
    @Scheduled(cron = "1 * * * * *") // Hvert minutt
    public void ryddOppCache() {
        if (!cache.isEmpty()) {
            logger.info("Starter opprydning av cache. Har {} directories", cache.size());
            int count = 0;
            for (Map.Entry<String, MeldingerCacheEntry> entry : cache.entrySet()) {
                if (now().minusMinutes(timeToLiveMinutes).isAfter(entry.getValue().lastIndexed)) {
                    count++;
                    String key = entry.getKey();
                    cache.remove(key);
                }
            }
            logger.info("Fjernet {} directories", count);
        }
    }

    private static String key(String fnr, String navIdent) {
        return fnr + "-" + navIdent;
    }

    private static RAMDirectory indekser(List<Melding> meldinger) {
        try {
            RAMDirectory directory = new RAMDirectory();
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(ANALYZER));

            int i = 0;
            for (Melding melding : meldinger) {
                writer.addDocument(lagDokument(melding, i));
                i++;
            }

            writer.commit();
            return directory;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document lagDokument(Melding melding, int id) {
        Document document = new Document();
        document.add(new StoredField(ID, id));
        document.add(new StoredField(BEHANDLINGS_ID, melding.id));
        document.add(new TextField(FRITEKST, getFritekst(melding), YES));
        document.add(new TextField(TEMAGRUPPE, ofNullable(melding.temagruppeNavn).orElse(""), YES));
        document.add(new TextField(ARKIVTEMA, ofNullable(melding.journalfortTemanavn).orElse(""), YES));
        document.add(new TextField(DATO, ofNullable(melding.visningsDatoTekst).orElse(""), YES));
        document.add(new TextField(NAVIDENT, getNavIdent(melding), YES));
        document.add(new TextField(STATUSTEKST, ofNullable(melding.statusTekst).orElse(""), YES));
        document.add(new TextField(IKONTEKST, ofNullable(melding.statusTekst).orElse(""), YES));
        document.add(new TextField(LEST_STATUS, ofNullable(melding.lestStatus).orElse(""), YES));
        document.add(new TextField(KANAL, ofNullable(melding.kanal).orElse(""), YES));
        document.add(new TextField(SKREVET_AV_NAVN, getForfattere(melding), YES));
        document.add(new TextField(JOURNALFORT_AV_NAVN, ofNullable(melding.journalfortAv.navn).orElse(""), YES));
        document.add(new TextField(JOURNALFORT_AV_IDENT, ofNullable(melding.journalfortAvNavIdent).orElse(""), YES));
        document.add(new TextField(JOURNALFORT_DATO, ofNullable(melding.journalfortDatoTekst).orElse(""), YES));
        document.add(new TextField(JOURNALFORT_SAKSID, ofNullable(melding.journalfortSaksId).orElse(""), YES));
        document.add(new TextField(FERDIGSTILT_UTEN_SVAR_DATO, ofNullable(melding.ferdigstiltUtenSvarDatoTekst).orElse(""), YES));
        document.add(new TextField(FEILSENDT_DATO, ofNullable(melding.markertSomFeilsendtDatoTekst).orElse(""), YES));
        document.add(new TextField(KONTORSPERRET_DATO, ofNullable(melding.kontorsperretDatoTekst).orElse(""), YES));

        return document;
    }

    private static String getNavIdent(Melding melding) {
        return melding.getFriteksterMedEldsteForst().stream()
                .map(Fritekst::getSaksbehandler)
                .filter(Optional::isPresent)
                .map(saksbehandler -> saksbehandler.get().getIdent())
                .collect(joining(", "));
    }

    private static String getForfattere(Melding melding) {
        return melding.getFriteksterMedEldsteForst().stream()
                .map(Fritekst::getSaksbehandler)
                .filter(Optional::isPresent)
                .map(saksbehandler -> saksbehandler.get().navn)
                .collect(joining(", "));
    }

    private static String getFritekst(Melding melding) {
        return String.join("\n\n", melding.getFriteksterMedEldsteForst().stream()
                .map(Fritekst::getFritekst)
                .collect(toList()));
    }

    private static String query(String soketekst) {
        String vasketSoketekst = LUCENE_PATTERN.matcher(soketekst).replaceAll(REPLACEMENT_STRING).trim();
        return isBlank(vasketSoketekst) ? "*:*" : Arrays.stream(vasketSoketekst.split(" "))
                .map((s) -> isBlank(s) ? "" : "*" + s + "*")
                .collect(joining(" "));
    }

    private static Map<String, MeldingerSokResultat> hentResultat(
            final IndexSearcher searcher, final StandardAnalyzer analyzer,
            final Highlighter highlighter, final String soketekst, ScoreDoc... hits) {
        try {
            Map<String, MeldingerSokResultat> resultat = new HashMap<>();
            boolean gjorHighlighting = soketekst.length() > 0;
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                String behandlingsId = searcher.doc(hit.doc).get(BEHANDLINGS_ID);
                String fritekst = hentTekstResultat(FRITEKST, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String temagruppe = hentTekstResultat(TEMAGRUPPE, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String arkivtema = hentTekstResultat(ARKIVTEMA, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String dato = hentTekstResultat(DATO, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String navIdent = hentTekstResultat(NAVIDENT, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String statusTekst = hentTekstResultat(STATUSTEKST, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String ikontekst = hentTekstResultat(IKONTEKST, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String lestStatus = hentTekstResultat(LEST_STATUS, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String kanal = hentTekstResultat(KANAL, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String skrevetAvNavn = hentTekstResultat(SKREVET_AV_NAVN, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String journalfortAvNavn = hentTekstResultat(JOURNALFORT_AV_NAVN, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String journalfortAvIdent = hentTekstResultat(JOURNALFORT_AV_IDENT, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String journalfortDato = hentTekstResultat(JOURNALFORT_DATO, doc, searcher, analyzer, highlighter, gjorHighlighting);
                String journalfortSaksId = hentTekstResultat(JOURNALFORT_SAKSID, doc, searcher, analyzer, highlighter, gjorHighlighting);
                resultat.put(behandlingsId,
                        new MeldingerSokResultat()
                                .withFritekst(fritekst)
                                .withTemagruppe(temagruppe)
                                .withArkivtema(arkivtema)
                                .withDato(dato)
                                .withNavident(navIdent)
                                .withStatustekst(statusTekst)
                                .withIkontekst(ikontekst)
                                .withLestStatus(lestStatus)
                                .withKanal(kanal)
                                .withSkrevetAvNavn(skrevetAvNavn)
                                .withJournalfortAvNavn(journalfortAvNavn)
                                .withJournalfortAvIdent(journalfortAvIdent)
                                .withJournalfortDato(journalfortDato)
                                .withJournalfortSaksId(journalfortSaksId));
            }
            return resultat;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String hentTekstResultat(String felt, Document doc, IndexSearcher searcher, StandardAnalyzer analyser, Highlighter highlighter, boolean medHighlighting) {
        String tekstresultat = doc.get(felt);
        if (!medHighlighting) {
            return tekstresultat;
        }
        return hentHighlightedTekstResultat(felt, doc, searcher, analyser, highlighter);
    }

    private static String hentHighlightedTekstResultat(String felt, Document doc, IndexSearcher searcher, StandardAnalyzer analyzer, Highlighter highlighter) {
        String tekst = doc.get(felt);
        try {
            int id = Integer.parseInt(doc.get(ID));
            TokenStream tokenStreamTittel = TokenSources.getAnyTokenStream(searcher.getIndexReader(), id, felt, analyzer);
            TextFragment[] fragments = highlighter.getBestTextFragments(tokenStreamTittel, tekst, false, 1);
            tekst = fragments[0].toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tekst;
    }

    private static Function<Melding, Melding> highlighting(final Map<String, MeldingerSokResultat> resultat) {
        return (melding) -> {
            MeldingerSokResultat meldingerSokResultat = resultat.get(melding.id);
            melding.withFritekst(new Fritekst(meldingerSokResultat.fritekst, melding.skrevetAv, melding.ferdigstiltDato));
            melding.temagruppeNavn = meldingerSokResultat.temagruppe;
            melding.journalfortTemanavn = meldingerSokResultat.arkivtema;
            melding.visningsDatoTekst = meldingerSokResultat.dato;
            melding.kanal = meldingerSokResultat.kanal;
            melding.statusTekst = meldingerSokResultat.statustekst;
            melding.ikontekst = meldingerSokResultat.ikontekst;
            melding.lestStatus = meldingerSokResultat.lestStatus;
            melding.navIdent = meldingerSokResultat.navIdent;
            melding.skrevetAv = new Person(meldingerSokResultat.skrevetAvNavn, "");
            melding.journalfortAv = new Person(meldingerSokResultat.journalfortAvNavn, "");
            melding.journalfortAvNavIdent = meldingerSokResultat.journalfortAvIdent;
            melding.journalfortDatoTekst = meldingerSokResultat.journalfortDato;
            melding.journalfortSaksId = meldingerSokResultat.journalfortSaksId;
            return melding;
        };
    }


    public static class MeldingerCacheEntry {
        final List<Melding> meldinger;
        final RAMDirectory directory;
        final DateTime lastIndexed;
        final Map<String, List<Melding>> traader;

        public MeldingerCacheEntry(List<Melding> meldinger, RAMDirectory directory, DateTime lastIndexed) {
            this.meldinger = meldinger;
            this.directory = directory;
            this.lastIndexed = lastIndexed;
            this.traader = meldinger.stream().collect(groupingBy(Melding::getTraadId));
        }
    }

}
