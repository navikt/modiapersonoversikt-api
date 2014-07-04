package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Dokument;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.apache.commons.collections15.Transformer;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsType;

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
                    .withInnsendteDokumenter(hentDokumenter(wsSoknad, true))
                    .withManglendeDokumenter(hentDokumenter(wsSoknad, false))
                    .withBehandlingskjedeId(wsSoknad.getBehandlingsKjedeId())
                    .withSkjemanummerRef(wsSoknad.getHovedskjemaKodeverkId())
                    .withBehandlingsType(BehandlingsType.KVITTERING);
        }
    };

    private static Transformer<WSDokumentforventning, Dokument> tilDokument(final String hovedskjemaId) {
        return new Transformer<WSDokumentforventning, Dokument>() {
            @Override
            public Dokument transform(WSDokumentforventning wsDokumentforventning) {
                boolean innsendt = dokumentforventningErMottatt(wsDokumentforventning);
                return new Dokument()
                        .withHovedskjema(wsDokumentforventning.getKodeverkId().equals(hovedskjemaId))
                        .withInnsendt(innsendt)
                        .withKodeverkRef(wsDokumentforventning.getKodeverkId())
                        .withTilleggsTittel(wsDokumentforventning.getTilleggsTittel());
            }
        };
    }

    private static List<Dokument> hentDokumenter(WSSoknad wsSoknad, boolean erInnsendt) {
        List<Dokument> dokumenter = new ArrayList<>();
        List<WSDokumentforventning> wsDokumentforventninger = wsSoknad.getDokumentforventninger().getDokumentforventning();
        for (WSDokumentforventning wsDokumentforventning : wsDokumentforventninger) {
            Dokument dokument = tilDokument(wsSoknad.getHovedskjemaKodeverkId()).transform(wsDokumentforventning);
            if (erInnsendt && dokument.innsendt) {
                dokumenter.add(dokument);
            } else if (!erInnsendt && !dokument.innsendt) {
                dokumenter.add(dokument);
            }
        }
        return dokumenter;
    }

    private static boolean dokumentforventningErMottatt(WSDokumentforventning dokumentforventning) {
        return dokumentforventning.getInnsendingsvalg().equals("INNSENDT") || dokumentforventning.getInnsendingsvalg().equals("LASTET_OPP");
    }

}
