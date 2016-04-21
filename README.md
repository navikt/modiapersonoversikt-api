# Modia utbetalinger #

* StartJettyUtbetalinger
    * utbetal.endpoint.mock=MOCK
        * Går mot lokal mock i applikasjonen (WSUtbetalingTestData)
    *utbetal.endpoint.mock=REAL
        * Går mot tjenesten i U som er definert opp ved _utbetalingendpoint.url_ propertien
        * For å spørre på et gitt fødsesnummer:
            * ?fnr=<fnr> i URL