package no.nav.sbl.dialogarena.sak.domain;

import no.nav.modig.modia.model.FeedItemVM;

import java.io.Serializable;

public class TemaVM implements FeedItemVM, Serializable {

    @Override
    public String getType() {
        return "sakstema";
    }

    @Override
    public String getId() {
        return "dummyid";
    }
}
