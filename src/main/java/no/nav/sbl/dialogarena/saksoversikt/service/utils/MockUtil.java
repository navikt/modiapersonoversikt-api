package no.nav.sbl.dialogarena.saksoversikt.service.utils;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;

public class MockUtil {

    public static final String TILLATMOCK_PROPERTY = "tillatmock";
    public static final String ANTALLPAABEGYNTE_PROPERTY = "antallpaabegynte";
    private static final String DEFUALT_ANTALLPAABEGYNTE = "3";
    private static final String DEFAULT_MOCK_TILATT = "false";
    public static final String ALLOW_MOCK = "true";

    public static boolean mockSetupErTillatt() {
        return valueOf(getProperty(TILLATMOCK_PROPERTY, DEFAULT_MOCK_TILATT));
    }

    public static Integer antallPaabegynte() {
        return Integer.parseInt(getProperty(ANTALLPAABEGYNTE_PROPERTY, DEFUALT_ANTALLPAABEGYNTE));
    }
}
