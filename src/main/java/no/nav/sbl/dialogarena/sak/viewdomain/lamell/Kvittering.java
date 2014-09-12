package no.nav.sbl.dialogarena.sak.viewdomain.lamell;

import java.util.List;

public class Kvittering extends GenerellBehandling {

    public List<Dokument> innsendteDokumenter;
    public List<Dokument> manglendeDokumenter;
    public String behandlingsId;
    public String behandlingskjedeId;
    public String skjemanummerRef;
    public boolean ettersending;
    public boolean avsluttet;

    public Kvittering withInnsendteDokumenter(List<Dokument> innsendteDokumenter) {
        this.innsendteDokumenter = innsendteDokumenter;
        return this;
    }

    public Kvittering withManglendeDokumenter(List<Dokument> manglendeDokumenter) {
        this.manglendeDokumenter = manglendeDokumenter;
        return this;
    }

    public Kvittering withBehandlingsId(String behandlingsId) {
        this.behandlingsId = behandlingsId;
        return this;
    }

    public Kvittering withBehandlingskjedeId(String id) {
        behandlingskjedeId = id;
        return this;
    }

    public Kvittering withSkjemanummerRef(String ref) {
        skjemanummerRef = ref;
        return this;
    }

    public Kvittering withEttersending(boolean ettersending) {
        this.ettersending = ettersending;
        return this;
    }

    public Kvittering withAvsluttet(boolean avsluttet) {
        this.avsluttet = avsluttet;
        return this;
    }

}