package no.nav.sbl.dialogarena.sak.util;

public class KvitteringstypeUtils {

    public static final String DOKUMENTINNSENDING_KVITTERINGSTYPE = "ae0001";
    public static final String SEND_SOKNAD_KVITTERINGSTYPE = "ae0002";

    public static boolean erKvitteringstype(String type) {
        return SEND_SOKNAD_KVITTERINGSTYPE.equals(type) || DOKUMENTINNSENDING_KVITTERINGSTYPE.equals(type);
    }
}
