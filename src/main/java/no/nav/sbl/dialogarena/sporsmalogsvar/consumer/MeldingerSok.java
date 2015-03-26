package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.lang.collections.TransformerUtils;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.containedIn;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.collections.ReduceUtils.join;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding.TRAAD_ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Traad.NYESTE_FORST;
import static org.apache.lucene.document.Field.Store.YES;
import static org.joda.time.DateTime.now;

public class MeldingerSok {

    private static final Logger logger = LoggerFactory.getLogger(MeldingerSok.class);

    public static final String DEFAULT_TIME_TO_LIVE_MINUTES = "10";
    public static final String TIME_TO_LIVE_MINUTES_PROPERTY = "meldingersok.time.to.live.minutes";
    public static final String REPLACEMENT_STRING = "";
    public static final String LUCENE_SPECIAL_CHARS = "[\\\\+\\!\\(\\)\\:\\^\\[\\]\\{\\}\\~\\?\\=\\/\\|\\.]+";
    public static final Pattern LUCENE_PATTERN = Pattern.compile(LUCENE_SPECIAL_CHARS);

    private static final String ID = "id";
    private static final String BEHANDLINGS_ID = "behandlingsId";
    private static final String FRITEKST = "fritekst";
    private static final String TEMAGRUPPE = "temagruppe";
    private static final String ARKIVTEMA = "arkivtema";
    private static final String DATO = "dato";
    private static final String NAVIDENT = "navident";
    private static final String STATUSTEKST = "statustekst";
    private static final String KANAL = "kanal";
    private static final String[] FIELDS = new String[]{FRITEKST, TEMAGRUPPE, ARKIVTEMA, DATO, NAVIDENT, STATUSTEKST, KANAL};
    private static final StandardAnalyzer ANALYZER = new StandardAnalyzer();
    private static final Transformer<DateTime, String> DATO_TIL_STRING = new Transformer<DateTime, String>() {
        @Override
        public String transform(DateTime dateTime) {
            return DateUtils.dateTime(dateTime);
        }
    };

    private final Integer timeToLiveMinutes;

    private MultiFieldQueryParser queryParser = new MultiFieldQueryParser(FIELDS, ANALYZER);

    protected Map<String, List<Melding>> meldingerCache = new ConcurrentHashMap<>();
    protected Map<String, RAMDirectory> directories = new ConcurrentHashMap<>();
    protected Map<String, DateTime> indexingTimestamps = new ConcurrentHashMap<>();

    public MeldingerSok() {
        timeToLiveMinutes = Integer.valueOf(getProperty(TIME_TO_LIVE_MINUTES_PROPERTY, DEFAULT_TIME_TO_LIVE_MINUTES));
        queryParser.setDefaultOperator(QueryParser.Operator.AND);
        queryParser.setAllowLeadingWildcard(true);
    }

    public void indekser(String fnr, List<Melding> meldinger) {
        String navIdent = getSubjectHandler().getUid();
        String key = key(fnr, navIdent);

        List<Melding> transformerteMeldinger = on(meldinger).map(new Transformer<Melding, Melding>() {
            @Override
            public Melding transform(Melding melding) {
                melding.opprettetDatoTekst = optional(melding.opprettetDato).map(DATO_TIL_STRING).getOrElse("");
                return melding;
            }
        }).collect();
        meldingerCache.put(key, transformerteMeldinger);
        directories.put(key, indekser(transformerteMeldinger));
        indexingTimestamps.put(key, now());
    }

    public List<Traad> sok(final String fnr, String soketekst) {
        try {
            final String navIdent = getSubjectHandler().getUid();
            final String key = key(fnr, navIdent);

            if (!directories.containsKey(key)) {
                throw new ServiceUnavailableException(String.format("Man må kalle %s.indekser før man kan søke", MeldingerSok.class.getName()));
            }

            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directories.get(key)));
            TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);

            Query query = queryParser.parse(query(soketekst));
            searcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new NullFragmenter());

            Map<String, MeldingerSokResultat> resultat = hentResultat(searcher, ANALYZER, highlighter, soketekst, hits);

            return lagTraader(key, resultat);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Traad> lagTraader(String key, Map<String, MeldingerSokResultat> resultat) {
        final List<String> ider = on(resultat).map(TransformerUtils.<String>key()).collect();
        final List<Melding> opprinneligeMeldinger = meldingerCache.get(key);

        Map<String, List<Melding>> traader = on(opprinneligeMeldinger)
                .filter(where(Melding.ID, containedIn(ider)))
                .map(highlighting(resultat))
                .reduce(indexBy(TRAAD_ID));

        final Map<String, List<Melding>> opprinneligeTraader = on(opprinneligeMeldinger).reduce(indexBy(TRAAD_ID));
        return on(traader.entrySet()).map(new Transformer<Map.Entry<String, List<Melding>>, Traad>() {
            @Override
            public Traad transform(Map.Entry<String, List<Melding>> entry) {
                int antallMeldingerIOpprinneligTraad = opprinneligeTraader.get(entry.getKey()).size();
                return new Traad(entry.getKey(), antallMeldingerIOpprinneligTraad, entry.getValue());
            }
        }).collect(NYESTE_FORST);
    }

    @Scheduled(cron = "1 * * * * *") // Hvert minutt
    public void ryddOppCache() {
        logger.info("Starter opprydning av cache. Har {} directories", directories.size());
        int count = 0;
        for (Map.Entry<String, DateTime> entry : indexingTimestamps.entrySet()) {
            if (now().minusMinutes(timeToLiveMinutes).isAfter(entry.getValue())) {
                count++;
                String key = entry.getKey();
                indexingTimestamps.remove(key);
                directories.remove(key);
                meldingerCache.remove(key);
            }
        }
        logger.info("Fjernet {} directories", count);
    }

    private static String key(String fnr, String navIdent) {
        return fnr + "-" + navIdent;
    }

    private static RAMDirectory indekser(List<Melding> meldinger) {
        try {
            RAMDirectory directory = new RAMDirectory();
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_4_10_2, ANALYZER));

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
        document.add(new TextField(FRITEKST, optional(melding.fritekst).getOrElse(""), YES));
        document.add(new TextField(TEMAGRUPPE, optional(melding.temagruppeNavn).getOrElse(""), YES));
        document.add(new TextField(ARKIVTEMA, optional(melding.journalfortTemanavn).getOrElse(""), YES));
        document.add(new TextField(DATO, optional(melding.opprettetDatoTekst).getOrElse(""), YES));
        document.add(new TextField(NAVIDENT, optional(melding.navIdent).getOrElse(""), YES));
        document.add(new TextField(STATUSTEKST, optional(melding.statusTekst).getOrElse(""), YES));
        document.add(new TextField(KANAL, optional(melding.kanal).getOrElse(""), YES));

        return document;
    }

    private static String query(String soketekst) {
        String vasketSoketekst = LUCENE_PATTERN.matcher(soketekst).replaceAll(REPLACEMENT_STRING);
        return on(asList(vasketSoketekst.split(" "))).map(new Transformer<String, String>() {

            @Override
            public String transform(String s) {
                return "*" + s + "*";
            }
        }).reduce(join(" "));
    }

    private static Map<String, MeldingerSokResultat> hentResultat(final IndexSearcher searcher, final StandardAnalyzer analyzer, final Highlighter highlighter, final String soketekst, ScoreDoc... hits) {
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
                String kanal = hentTekstResultat(KANAL, doc, searcher, analyzer, highlighter, gjorHighlighting);
                resultat.put(behandlingsId, new MeldingerSokResultat().withFritekst(fritekst).withTemagruppe(temagruppe).withArkivtema(arkivtema)
                        .withDato(dato).withNavident(navIdent).withStatustekst(statusTekst).withKanal(kanal));
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

    private static Transformer<Melding, Melding> highlighting(final Map<String, MeldingerSokResultat> resultat) {
        return new Transformer<Melding, Melding>() {
            @Override
            public Melding transform(Melding melding) {
                MeldingerSokResultat meldingerSokResultat = resultat.get(melding.id);
                melding.fritekst = meldingerSokResultat.fritekst;
                melding.temagruppeNavn = meldingerSokResultat.temagruppe;
                melding.journalfortTemanavn = meldingerSokResultat.arkivtema;
                melding.opprettetDatoTekst = meldingerSokResultat.dato;
                melding.navIdent = meldingerSokResultat.navIdent;
                melding.kanal = meldingerSokResultat.kanal;
                melding.statusTekst = meldingerSokResultat.statustekst;
                return melding;
            }
        };
    }

    private static class MeldingerSokResultat {
        public String fritekst, temagruppe, arkivtema, dato, navIdent, statustekst, kanal;

        public MeldingerSokResultat() {
        }

        public MeldingerSokResultat withFritekst(String fritekst) {
            this.fritekst = fritekst;
            return this;
        }

        public MeldingerSokResultat withTemagruppe(String temagruppe) {
            this.temagruppe = temagruppe;
            return this;
        }

        public MeldingerSokResultat withArkivtema(String arkivtema) {
            this.arkivtema = arkivtema;
            return this;
        }

        public MeldingerSokResultat withDato(String dato) {
            this.dato = dato;
            return this;
        }

        public MeldingerSokResultat withNavident(String navIdent) {
            this.navIdent = navIdent;
            return this;
        }

        public MeldingerSokResultat withStatustekst(String statustekst) {
            this.statustekst = statustekst;
            return this;
        }

        public MeldingerSokResultat withKanal(String kanal) {
            this.kanal = kanal;
            return this;
        }
    }
}
