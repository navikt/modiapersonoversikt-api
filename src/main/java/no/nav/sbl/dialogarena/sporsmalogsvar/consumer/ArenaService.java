package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSBruker;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.inject.Inject;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalToIgnoreCase;
import static no.nav.modig.lang.collections.PredicateUtils.where;

public class ArenaService {

    public static final String ARENA_FAGSYSTEMKODE = "AO01";
    public static final String BRUKERKODE_PERSON = "PERSON";
    public static final String OPPFOLGINGSSAK_TEMA_IDENTIFIKATOR = "OPP";

    @Inject
    private ArbeidOgAktivitet arbeidOgAktivitet;

    public Optional<Sak> hentOppfolgingssak(String fodselsnummer) {
        WSHentSakListeResponse response = arbeidOgAktivitet.hentSakListe(
                new WSHentSakListeRequest().withBruker(new WSBruker().withBrukertypeKode(BRUKERKODE_PERSON).withBruker(fodselsnummer)));

        return on(response.getSakListe())
                .filter(where(FAGOMRADE_KODE, equalToIgnoreCase(OPPFOLGINGSSAK_TEMA_IDENTIFIKATOR)))
                .head()
                .map(TIL_SAK);
    }

    private static final Transformer<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak, String> FAGOMRADE_KODE = new Transformer<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak, String>() {
        @Override
        public String transform(no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak sak) {
            return sak.getFagomradeKode().getKode();
        }
    };

    private static final Transformer<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak, Sak> TIL_SAK =
            new Transformer<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak, Sak>() {
                @Override
                public Sak transform(no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak arenaSak) {
                    Sak sak = new Sak();
                    sak.saksId = arenaSak.getSaksId();
                    sak.fagsystemKode = ARENA_FAGSYSTEMKODE;
                    sak.sakstype = arenaSak.getSakstypeKode().getKode();
                    sak.tema = arenaSak.getFagomradeKode().getKode();
                    sak.opprettetDato = new DateTime(arenaSak.getEndringsInfo().getOpprettetDato().toDate());
                    return sak;
                }
            };

}
