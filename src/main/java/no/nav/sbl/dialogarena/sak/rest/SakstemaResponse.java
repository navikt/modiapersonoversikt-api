package no.nav.sbl.dialogarena.sak.rest;

import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;

import java.util.List;
import java.util.Set;

public class SakstemaResponse {

    public List<ModiaSakstema> sakstema;
    public Set<Baksystem> feilendeBaksystemer;

    public SakstemaResponse(List<ModiaSakstema> sakstema, Set<Baksystem> feilendeBaksystemer) {
        this.sakstema = sakstema;
        this.feilendeBaksystemer = feilendeBaksystemer;
    }
}
