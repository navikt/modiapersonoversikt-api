package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning;

import java.util.Optional;


public class MeldingBuilder {

    private HenvendelseVM henvendelseVM;
    private Optional<Melding> eldsteMeldingITraad = Optional.empty();
    private Meldingstype type;
    private String fnr;
    private String navident;
    private String valgtEnhet;

    public Melding build() {
        Melding melding = new Melding()
                .withFnr(fnr)
                .withSkrevetAv(new Person("", "", "navident"))
                .withKanal(henvendelseVM.kanal.name())
                .withType(type)
                .withFritekst(henvendelseVM.getFritekst())
                .withEksternAktor(navident)
                .withTilknyttetEnhet(valgtEnhet)
                .withErTilknyttetAnsatt(henvendelseVM.oppgaveTilknytning == OppgaveTilknytning.SAKSBEHANDLER);
        if (eldsteMeldingITraad.isPresent()) {
            melding
                    .withTraadId(eldsteMeldingITraad.get().id)
                    .withKontorsperretEnhet(eldsteMeldingITraad.get().kontorsperretEnhet)
                    .withTemagruppe(eldsteMeldingITraad.get().temagruppe);
        } else {
            melding.withTemagruppe(henvendelseVM.temagruppe.name());
        }
        return melding;
    }

    public MeldingBuilder withHenvendelseVM(HenvendelseVM henvendelseVM) {
        this.henvendelseVM = henvendelseVM;
        return this;
    }

    public MeldingBuilder withMeldingstype(Meldingstype type) {
        this.type = type;
        return this;
    }

    public MeldingBuilder withEldsteMeldingITraad(Optional<Melding> eldsteMeldingITraad) {
        this.eldsteMeldingITraad = eldsteMeldingITraad;
        return this;
    }

    public MeldingBuilder withFnr(String fnr) {
        this.fnr = fnr;
        return this;
    }

    public MeldingBuilder withNavident(String navident) {
        this.navident = navident;
        return this;
    }

    public MeldingBuilder withValgtEnhet(String valgtEnhet) {
        this.valgtEnhet = valgtEnhet;
        return this;
    }

}
