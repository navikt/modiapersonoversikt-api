package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Dokument;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.both;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.HenvendelseType;

public class HenvendelseTransformers {

    public static final Transformer<WSSoknad, Boolean> INNSENDT = wsSoknad -> wsSoknad.getInnsendtDato() != null;

    public static final Transformer<WSSoknad, Kvittering> KVITTERING = new Transformer<WSSoknad, Kvittering>() {

        @Override
        public Kvittering transform(WSSoknad wsSoknad) {
            GenerellBehandling.BehandlingsStatus status = wsSoknad.getInnsendtDato() != null ? GenerellBehandling.BehandlingsStatus.AVSLUTTET : GenerellBehandling.BehandlingsStatus.OPPRETTET;
            return (Kvittering) new Kvittering()
                    .withAvsluttet(INNSENDT.transform(wsSoknad))
                    .withInnsendteDokumenter(hentDokument(wsSoknad, ER_DOKUMENT_INNSENDT))
                    .withManglendeDokumenter(hentDokument(wsSoknad, both(not(ER_DOKUMENT_INNSENDT)).and(not(er_Hovedskjema(wsSoknad.getHovedskjemaKodeverkId())))))
                            .withEttersending(wsSoknad.isEttersending())
                            .withBehandlingskjedeId(wsSoknad.getBehandlingsKjedeId())
                            .withJournalPostId(wsSoknad.getJournalpostId())
                            .withSkjemanummerRef(wsSoknad.getHovedskjemaKodeverkId())
                            .withBehandlingsDato(wsSoknad.getInnsendtDato())
                            .withHenvendelseType(HenvendelseType.valueOf(WSHenvendelseType.valueOf(wsSoknad.getHenvendelseType()).name()))
                            .withBehandlingStatus(status)
                            .withBehandlingsId(wsSoknad.getBehandlingsId());
        }
    };

    private static Transformer<WSDokumentforventning, Dokument> tilDokument(final String hovedskjemaId) {
        return wsDokumentforventning -> {
            boolean innsendt = wsDokumentforventning.getInnsendingsvalg().equals("INNSENDT") || wsDokumentforventning.getInnsendingsvalg().equals("LASTET_OPP");
            return new Dokument()
                    .withHovedskjema(wsDokumentforventning.getKodeverkId().equals(hovedskjemaId))
                    .withInnsendt(innsendt)
                    .withKodeverkRef(wsDokumentforventning.getKodeverkId())
                    .withInnsendingsvalg(wsDokumentforventning.getInnsendingsvalg())
                    .withArkivreferanse(wsDokumentforventning.getArkivreferanse())
                    .withTilleggsTittel(wsDokumentforventning.getTilleggsTittel());
        };
    }

    private static List<Dokument> hentDokument(WSSoknad wsSoknad, Predicate<WSDokumentforventning> betingelse) {
        return on(wsSoknad.getDokumentforventninger().getDokumentforventning())
                .filter(both(betingelse).and(not(ER_KVITTERING)))
                .map(tilDokument(wsSoknad.getHovedskjemaKodeverkId()))
                .collect();
    }

    private static Predicate<WSDokumentforventning> er_Hovedskjema(final String hovedskjemaId) {
        return dokumentforventning -> dokumentforventning.getKodeverkId().equals(hovedskjemaId);
    }

    private static final Predicate<WSDokumentforventning> ER_KVITTERING = dokumentforventning -> dokumentforventning.getKodeverkId().equals("L7");

    private static final Predicate<WSDokumentforventning> ER_DOKUMENT_INNSENDT = dokumentforventning -> asList("INNSENDT", "LASTET_OPP").contains(dokumentforventning.getInnsendingsvalg());
}
