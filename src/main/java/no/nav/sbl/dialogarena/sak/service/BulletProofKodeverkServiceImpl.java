package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.sak.service.interfaces.BulletProofKodeverkService;
import org.slf4j.Logger;

import javax.inject.Inject;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

public class BulletProofKodeverkServiceImpl implements BulletProofKodeverkService {

    private static final Logger LOG = getLogger(BulletProofKodeverkServiceImpl.class);

    @Inject
    private KodeverkClient kodeverkClient;

    @Inject
    private Kodeverk lokaltKodeverk;

    @Override
    public String getSkjematittelForSkjemanummer(String vedleggsIdOrSkjemaId) {
        try {
            String tittel = lokaltKodeverk.getTittel(vedleggsIdOrSkjemaId);
            if (tittel == null) {
                throw new RuntimeException();
            }
            return tittel;
        } catch (Exception e) {
            LOG.warn("Fant ikke kodeverkid '"+vedleggsIdOrSkjemaId+"'. Bruker generisk tittel.", e);
            return hentUkjentKodeverkverdi(vedleggsIdOrSkjemaId);
        }
    }

    @Override
    public boolean isEgendefinert(String vedleggsIdOrskjemaId) {
        return lokaltKodeverk.isEgendefinert(vedleggsIdOrskjemaId);
    }

    @Override
    public String getTemanavnForTemakode(String temakode, String kodeverk) {
        try {
            return kodeverkClient.hentFoersteTermnavnForKode(temakode, kodeverk);
        } catch(Exception e) {
            LOG.warn("Fant ikke kodeverkid '" + temakode + "'. Bruker generisk tittel.", e);
            return hentUkjentKodeverkverdi(temakode);
        }
    }

    private String hentUkjentKodeverkverdi(String kode) {
        return format("[Fant ikke \"%s\" i kodeverk]", kode);
    }

}
