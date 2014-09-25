package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Dokument;
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

    public static final Transformer<WSSoknad, Boolean> INNSENDT = new Transformer<WSSoknad, Boolean>() {
        @Override
        public Boolean transform(WSSoknad wsSoknad) {
            return wsSoknad.getInnsendtDato() != null;
        }
    };

    public static final Transformer<WSSoknad, Kvittering> KVITTERING = new Transformer<WSSoknad, Kvittering>() {

        @Override
        public Kvittering transform(WSSoknad wsSoknad) {
            return (Kvittering) new Kvittering()
                    .withBehandlingsId(wsSoknad.getBehandlingsId())
                    .withAvsluttet(INNSENDT.transform(wsSoknad))
                    .withInnsendteDokumenter(hentDokument(wsSoknad, ER_DOKUMENT_INNSENDT))
                    .withManglendeDokumenter(hentDokument(wsSoknad, not(ER_DOKUMENT_INNSENDT)))
                    .withEttersending(wsSoknad.isEttersending())
                    .withBehandlingskjedeId(wsSoknad.getBehandlingsKjedeId())
                    .withSkjemanummerRef(wsSoknad.getHovedskjemaKodeverkId())
                    .withBehandlingsDato(wsSoknad.getInnsendtDato())
                    .withHenvendelseType(HenvendelseType.valueOf(WSHenvendelseType.valueOf(wsSoknad.getHenvendelseType()).name()));
        }
    };

    private static Transformer<WSDokumentforventning, Dokument> tilDokument(final String hovedskjemaId) {
        return new Transformer<WSDokumentforventning, Dokument>() {
            @Override
            public Dokument transform(WSDokumentforventning wsDokumentforventning) {
                boolean innsendt = wsDokumentforventning.getInnsendingsvalg().equals("INNSENDT") || wsDokumentforventning.getInnsendingsvalg().equals("LASTET_OPP");
                return new Dokument()
                        .withHovedskjema(wsDokumentforventning.getKodeverkId().equals(hovedskjemaId))
                        .withInnsendt(innsendt)
                        .withKodeverkRef(wsDokumentforventning.getKodeverkId())
                        .withInnsendingsvalg(wsDokumentforventning.getInnsendingsvalg())
                        .withTilleggsTittel(wsDokumentforventning.getTilleggsTittel());
            }
        };
    }

    private static List<Dokument> hentDokument(WSSoknad wsSoknad, Predicate<WSDokumentforventning> erInnsendtPredikat) {
        return on(wsSoknad.getDokumentforventninger().getDokumentforventning())
                .filter(both(erInnsendtPredikat).and(not(er_Hovedskjema(wsSoknad.getHovedskjemaKodeverkId()))).and(not(ER_KVITTERING)))
                .map(tilDokument(wsSoknad.getHovedskjemaKodeverkId()))
                .collect();
    }

    private static Predicate<WSDokumentforventning> er_Hovedskjema(final String hovedskjemaId) {
        return new Predicate<WSDokumentforventning>() {
            @Override
            public boolean evaluate(WSDokumentforventning dokumentforventning) {
                return dokumentforventning.getKodeverkId().equals(hovedskjemaId);
            }
        };
    }

    private static final Predicate<WSDokumentforventning> ER_KVITTERING = new Predicate<WSDokumentforventning>() {
        @Override
        public boolean evaluate(WSDokumentforventning dokumentforventning) {
            return dokumentforventning.getKodeverkId().equals("L7");
        }
    };

    private static final Predicate<WSDokumentforventning> ER_DOKUMENT_INNSENDT = new Predicate<WSDokumentforventning>() {
        @Override
        public boolean evaluate(WSDokumentforventning dokumentforventning) {
            return asList("INNSENDT", "LASTET_OPP").contains(dokumentforventning.getInnsendingsvalg());
        }
    };
}
