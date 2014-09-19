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
import java.util.List;

public class ArenaService {

    public static final String ARENA_FAGSYSTEMNAVN = "Arena";
    public static final String TEMA_OPPFOLGING = "OPP";

    //TODO: Finn identifikator for oppfølgingssak
    public static final String OPPFOLGINGSSAK_TEMA_IDENTIFIKATOR = "opp";

    @Inject
    private ArbeidOgAktivitet arbeidOgAktivitet;

    public Optional<Sak> hentOppfolgingssak(String fodselsnummer) {
        WSHentSakListeResponse response = arbeidOgAktivitet.hentSakListe(
                new WSHentSakListeRequest().withBruker(new WSBruker().withBruker(fodselsnummer)));
        List<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak> arenaSakListe = response.getSakListe();
        Optional<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak> oppfolgingssak = filtrerUtOppfolgingssak(arenaSakListe);
        return oppfolgingssak.isSome() ? Optional.optional(TIL_SAK.transform(oppfolgingssak.get())) : Optional.<Sak>none();
    }

    private Optional<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak> filtrerUtOppfolgingssak(List<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak> arenaSakListe) {
        for (no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak sak : arenaSakListe) {
            if (sakErOppfolgingssak(sak)) {
                return Optional.optional(sak);
            }
        }
        return Optional.none();
    }

    private boolean sakErOppfolgingssak(no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak sak) {
        return OPPFOLGINGSSAK_TEMA_IDENTIFIKATOR.equalsIgnoreCase(sak.getSakstypeKode().getKode());
    }

    private static final Transformer<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak, Sak> TIL_SAK =
            new Transformer<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak, Sak>() {
                @Override
                public Sak transform(no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak arenaSak) {
                    Sak sak = new Sak();
                    sak.saksId = arenaSak.getSaksId();
                    sak.fagsystem = ARENA_FAGSYSTEMNAVN;
                    sak.sakstype = arenaSak.getSakstypeKode().getKode();
                    sak.tema = TEMA_OPPFOLGING;
                    sak.opprettetDato = new DateTime(arenaSak.getEndringsInfo().getOpprettetDato().toDate());
                    return sak;
                }
            };

}
