package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.skrivestotte;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.Hjelpetekst;
import org.apache.commons.collections15.Transformer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.lucene.document.Field.Store.YES;
import static org.apache.lucene.index.DirectoryReader.open;
import static org.apache.lucene.queryparser.classic.QueryParser.Operator.AND;
import static org.apache.lucene.util.Version.LUCENE_4_10_2;

public class HjelpetekstIndex {
    public static final String TITTEL = "tittel";
    public static final String TAGS = "tags";
    public static final String INNHOLD = "innhold";

    public static final String[] FIELDS = new String[]{TITTEL, TAGS, INNHOLD};

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private RAMDirectory directory = new RAMDirectory();
    private MultiFieldQueryParser queryParser = new MultiFieldQueryParser(FIELDS, analyzer);

    public HjelpetekstIndex() {
        queryParser.setDefaultOperator(AND);
    }

    public void indekser(List<Hjelpetekst> hjelpetekster) {
        try {
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(LUCENE_4_10_2, analyzer));
            for (Hjelpetekst hjelpetekst : hjelpetekster) {
                writer.addDocument(lagDokument(hjelpetekst));
            }
            writer.commit();
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    public List<Hjelpetekst> sok(String frisok) {
        try {
            IndexSearcher searcher = new IndexSearcher(open(directory));
            TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
            searcher.search(queryParser.parse(trim(frisok) + "*"), collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            return lagHjelpetekster(searcher, hits);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Document lagDokument(Hjelpetekst hjelpetekst) {
        Document document = new Document();
        document.add(new TextField(TITTEL, hjelpetekst.tittel, YES));
        document.add(new TextField(INNHOLD, hjelpetekst.innhold, YES));
        document.add(new TextField(TAGS, join(hjelpetekst.tags, " "), YES));
        return document;
    }

    private static List<Hjelpetekst> lagHjelpetekster(final IndexSearcher searcher, ScoreDoc ... hits) {
        return on(hits).map(new Transformer<ScoreDoc, Hjelpetekst>() {
            @Override
            public Hjelpetekst transform(ScoreDoc hit) {
                try {
                    Document doc = searcher.doc(hit.doc);
                    return new Hjelpetekst(doc.get(TITTEL), doc.get(INNHOLD), split(doc.get(TAGS), " "));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).collect();
    }
}
