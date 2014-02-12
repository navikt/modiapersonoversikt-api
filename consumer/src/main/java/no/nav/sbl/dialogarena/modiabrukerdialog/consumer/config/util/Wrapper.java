package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util;

public class Wrapper<WRAPPED> {

    public WRAPPED wrappedObject;

    public Wrapper(WRAPPED wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

}
