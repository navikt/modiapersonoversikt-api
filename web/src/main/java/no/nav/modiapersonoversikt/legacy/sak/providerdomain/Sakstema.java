package no.nav.modiapersonoversikt.legacy.sak.providerdomain;

import java.util.List;

import static java.util.Collections.emptyList;

public class Sakstema {

    public String temakode;
    public String temanavn;
    public boolean erGruppert;
    public List<Behandlingskjede> behandlingskjeder = emptyList();
    public List<DokumentMetadata> dokumentMetadata = emptyList();
    public List<Sak> tilhorendeSaker = emptyList();
    public List<Integer> feilkoder = emptyList();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sakstema sakstema = (Sakstema) o;
        return temakode.equals(sakstema.temakode);
    }

    @Override
    public int hashCode() {
        return temakode.hashCode();
    }

    public Sakstema withTemakode(final String temakode) {
        this.temakode = temakode;
        return this;
    }

    public Sakstema withTemanavn(final String temanavn) {
        this.temanavn = temanavn;
        return this;
    }

    public Sakstema withBehandlingskjeder(final List<Behandlingskjede> behandlingskjeder) {
        this.behandlingskjeder = behandlingskjeder;
        return this;
    }

    public Sakstema withDokumentMetadata(final List<DokumentMetadata> dokumentMetadata) {
        this.dokumentMetadata = dokumentMetadata;
        return this;
    }

    public Sakstema withTilhorendeSaker(final List<Sak> tilhorendeSaker) {
        this.tilhorendeSaker = tilhorendeSaker;
        return this;
    }

    public Sakstema withFeilkoder(final List<Integer> feilkoder) {
        this.feilkoder = feilkoder;
        return this;
    }

    public boolean erGruppert() {
        return this.erGruppert;
    }

    public Sakstema withErGruppert(final boolean erGruppert) {
        this.erGruppert = erGruppert;
        return this;
    }

    public boolean erIkkeTomtTema() {
        return !behandlingskjeder.isEmpty() || !dokumentMetadata.isEmpty();
    }
}






