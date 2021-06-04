package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;

import java.util.Map;

import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.MELDINGSTYPE_MAP;
import static org.joda.time.DateTime.now;

public class HenvendelseUtils {

    public static final String KONTAKT_NAV_SAKSTEMA = "KNA";

    public static XMLHenvendelse createXMLHenvendelseMedMeldingTilBruker(Melding henvendelse, XMLHenvendelseType type) {
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withFnr(henvendelse.fnrBruker)
                .withOpprettetDato(now())
                .withAvsluttetDato(now())
                .withTema(KONTAKT_NAV_SAKSTEMA)
                .withBehandlingskjedeId(henvendelse.traadId)
                .withKontorsperreEnhet(henvendelse.kontorsperretEnhet)
                .withEksternAktor(henvendelse.eksternAktor)
                .withTilknyttetEnhet(henvendelse.tilknyttetEnhet)
                .withErTilknyttetAnsatt(henvendelse.erTilknyttetAnsatt)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withTemagruppe(henvendelse.temagruppe)
                                .withKanal(henvendelse.kanal)
                                .withFritekst(henvendelse.getFritekst())
                                .withNavident(henvendelse.navIdent)
                ))
                .withFerdigstiltUtenSvar(henvendelse.erFerdigstiltUtenSvar);
    }


    public static XMLHenvendelseType getXMLHenvendelseTypeBasertPaaMeldingstype(Meldingstype type) {
        for (Map.Entry<XMLHenvendelseType, Meldingstype> entry : MELDINGSTYPE_MAP.entrySet()) {
            if (entry.getValue().name().equals(type.name())) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Det finnes ingen XMLHenvendelseType som korresponderer til Meldingstypen " + type.name());
    }

}
