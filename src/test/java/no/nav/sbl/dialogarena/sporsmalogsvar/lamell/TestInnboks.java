package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

public class TestInnboks extends Innboks {
    public TestInnboks(String id, String fnr) {
        super(id, fnr);
    }

    @Override
    protected boolean redirectHvisHenvendelsePageParam() {
        flyttParamFraURLTilSession();
        setValgtTraadBasertPaaTraadIdSessionParameter();
        return false;
    }
}
