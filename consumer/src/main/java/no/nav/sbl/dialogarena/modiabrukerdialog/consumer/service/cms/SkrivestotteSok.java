package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteTekst.LOCALE_DEFAULT;
import static org.apache.commons.lang3.StringUtils.*;

public class SkrivestotteSok {
    public static final String ID = "id";
    public static final String INNHOLD = "innhold";
    public static final String TITTEL = "tittel";
    public static final String TAGS = "tags";
    public static final String TAGS_FILTER = "tags-filter";

    public static final String LUCENE_SPECIAL_CHARS = "[\\\\+\\!\\(\\)\\:\\^\\[\\]\\{\\}\\~\\?\\=\\/\\|\\.\"]+";
    public static final Pattern LUCENE_PATTERN = Pattern.compile(LUCENE_SPECIAL_CHARS);
    public static final String REPLACEMENT_STRING = "";

    public static final String HIGHLIGHTED_BEGIN = "<em>";
    public static final String HIGHLIGHTED_END = "</em>";

    private static final String[] FIELDS = new String[]{TITTEL, TAGS, INNHOLD};

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private RAMDirectory directory;

    public void indekser(List<SkrivestotteTekst> skrivestotteTekster) {
        try {
            directory = new RAMDirectory();
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));
            int i = 0;
            for (SkrivestotteTekst skrivestotteTekst : skrivestotteTekster) {
                if (skrivestotteTekst.isValid()) {
                    writer.addDocument(lagDokument(skrivestotteTekst, i));
                    i++;
                }
            }
            writer.commit();
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    private static Document lagDokument(SkrivestotteTekst skrivestotteTekst, int id) {
        Document document = new Document();
        document.add(new StoredField(ID, id));
        document.add(new TextField(TITTEL, skrivestotteTekst.tittel, Store.YES));
        document.add(new TextField(INNHOLD, skrivestotteTekst.getDefaultLocaleInnhold().get(), Store.YES));
        document.add(new TextField(TAGS, join(skrivestotteTekst.tags, " "), Store.YES));

        for (Map.Entry<String, String> localeSkrivestotteTekst : skrivestotteTekst.innhold.entrySet()) {
            document.add(new StoredField(INNHOLD + "_" + localeSkrivestotteTekst.getKey(), localeSkrivestotteTekst.getValue()));
        }
        for (String tag : skrivestotteTekst.tags) {
            document.add(new StringField(TAGS_FILTER, tag.toLowerCase(), Store.NO));
        }
        return document;
    }

    public List<SkrivestotteTekst> sok(String frisok, List<String> tags) {
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
            TopScoreDocCollector collector = TopScoreDocCollector.create(1000);

            Query query = queryParser().parse(query(frisok, tags));
            searcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter(HIGHLIGHTED_BEGIN, HIGHLIGHTED_END);
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new NullFragmenter());

            return lagSkrivestotteTekster(searcher, analyzer, highlighter, hits);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<SkrivestotteTekst> sok(String frisok, String... tags) {
        return sok(frisok, asList(tags));
    }

    private QueryParser queryParser() {
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(FIELDS, analyzer);
        queryParser.setDefaultOperator(QueryParser.Operator.AND);
        queryParser.setAllowLeadingWildcard(true);
        return queryParser;
    }

    private static String query(String frisok, List<String> tags) {
        String vasketFrisok = LUCENE_PATTERN.matcher(frisok).replaceAll(REPLACEMENT_STRING).trim();
        String frisokQuery = isBlank(vasketFrisok) ? "*:*" : "*" + vasketFrisok + "*";
        String tagsQuery = tags.stream().map(tag -> TAGS_FILTER + ":" + tag).collect(joining(" AND "));
        return frisokQuery + (isBlank(tagsQuery) ? "" : (" AND " + tagsQuery));
    }

    private static List<SkrivestotteTekst> lagSkrivestotteTekster(final IndexSearcher searcher, final StandardAnalyzer analyzer, final Highlighter highlighter, ScoreDoc... hits) {
        return Arrays.stream(hits).map(hit -> {
            try {
                Document doc = searcher.doc(hit.doc);
                String tittel = getHighlightedTekst(TITTEL, doc, searcher, analyzer, highlighter);
                HashMap<String, String> innhold = new HashMap<>();
                for (IndexableField field : doc) {
                    if (field.name().startsWith(INNHOLD + "_")) {
                        if (field.name().equals(INNHOLD + "_" + LOCALE_DEFAULT)) {
                            innhold.put(LOCALE_DEFAULT, getHighlightedTekst(INNHOLD, doc, searcher, analyzer, highlighter));
                        } else {
                            innhold.put(field.name().replace(INNHOLD + "_", ""), field.stringValue());
                        }
                    }
                }
                return new SkrivestotteTekst(doc.get(ID), tittel, innhold, split(doc.get(TAGS), " "));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    private static String getHighlightedTekst(String felt, Document doc, IndexSearcher searcher, StandardAnalyzer analyzer, Highlighter highlighter) {
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
}
