package no.nav.modiapersonoversikt.service.pensjonsak;

import no.nav.modiapersonoversikt.service.saker.Sak;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.informasjon.WSSakSammendrag;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeRequest;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.service.saker.Sak.FAGSYSTEMKODE_PSAK;

public class PsakServiceImpl implements PsakService {

    private final PensjonSakV1 pensjonSakV1;

    public PsakServiceImpl(PensjonSakV1 pensjonSakV1) {
        this.pensjonSakV1 = pensjonSakV1;
    }

    @Override
    public List<Sak> hentSakerFor(String fnr) {
        try {
            List<WSSakSammendrag> sakSammendragListe =
                    pensjonSakV1.hentSakSammendragListe(new WSHentSakSammendragListeRequest().withPersonident(fnr))
                            .getSakSammendragListe();

            return sakSammendragListe.stream()
                    .map(TIL_SAK)
                    .collect(toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Function<WSSakSammendrag, Sak> TIL_SAK = wsSakSammendrag -> {
        Sak sak = new Sak();
        sak.fagsystemSaksId = wsSakSammendrag.getSakId();
        sak.temaKode = wsSakSammendrag.getArkivtema().getValue();
        sak.temaNavn = wsSakSammendrag.getArkivtema().getValue();
        sak.fagsystemKode = FAGSYSTEMKODE_PSAK;
        sak.saksId = wsSakSammendrag.getSakId();
        sak.finnesIPsak = true;
        sak.opprettetDato = opprettetDato(wsSakSammendrag.getSaksperiode());
        return sak;
    };

    private static DateTime opprettetDato(WSPeriode wsPeriode) {
        return ofNullable(wsPeriode).map(wsPeriode1 -> {
            LocalDate fom = wsPeriode1.getFom();
            return fom == null ? null : fom.toDateTimeAtStartOfDay();
        }).orElse(null);
    }
}
