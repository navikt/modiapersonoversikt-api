package no.nav.sbl.dialogarena.modiabrukerdialog.consumer;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.informasjon.WSSakSammendrag;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeRequest;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

import static java.util.Optional.ofNullable;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.FAGSYSTEMKODE_PSAK;

public class PsakServiceImpl implements PsakService {

    private final PensjonSakV1 pensjonSakV1;

    public PsakServiceImpl(PensjonSakV1 pensjonSakV1) {
        this.pensjonSakV1 = pensjonSakV1;
    }

    @Override
    public List<Sak> hentSakerFor(String fnr) {
        try {
            List<WSSakSammendrag> sakSammendragListe = pensjonSakV1.hentSakSammendragListe(new WSHentSakSammendragListeRequest().withPersonident(fnr)).getSakSammendragListe();
            return on(sakSammendragListe).map(TIL_SAK).collect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Transformer<WSSakSammendrag, Sak> TIL_SAK = wsSakSammendrag -> {
        Sak sak = new Sak();
        sak.fagsystemSaksId = ofNullable(wsSakSammendrag.getSakId());
        sak.temaKode = wsSakSammendrag.getArkivtema().getValue();
        sak.temaNavn = wsSakSammendrag.getArkivtema().getValue();
        sak.fagsystemKode = FAGSYSTEMKODE_PSAK;
        sak.saksId = ofNullable(wsSakSammendrag.getSakId());
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
