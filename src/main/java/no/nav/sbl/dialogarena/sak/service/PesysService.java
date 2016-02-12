package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.detalj.Sak;

import java.util.List;

public interface PesysService {
    List<Sak> hentSakstemaFraPesys(String uId);
}
