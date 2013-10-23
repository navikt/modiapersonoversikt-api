package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene;

import java.util.List;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import org.apache.wicket.model.LoadableDetachableModel;
import org.joda.time.DateTime;

import static java.util.Arrays.asList;

public class JournalforModell extends LoadableDetachableModel<Journalforing> {

    private Traad traad;
    private List<Sak> saker;

    public JournalforModell(Traad traad) {
        this.traad = traad;
    }

    @Override
    protected Journalforing load() {
        //TODO: Hent faktiske saker fra tjenesten
        saker = asList(
                new Sak("1", "Generell", "GSAK", "BID", DateTime.now().minusDays(2)),
                new Sak("2", "Generell", "GSAK", "BID", DateTime.now().minusHours(5)),
                new Sak("3", "Generell", "PSYS", "PEN", DateTime.now().minusMonths(1)),
                new Sak("4", "Generell", "GSAK", "SER", DateTime.now().minusWeeks(4)),
                new Sak("5", "Generell", "PSYS", "UFO", DateTime.now().minusDays(2)),
                new Sak("6", "Generell", "GSAK", "VEN", DateTime.now().minusWeeks(2)));

        return new Journalforing(traad, saker);
    }

    public void nullstill() {
        setObject(new Journalforing(traad, saker));
    }

}
