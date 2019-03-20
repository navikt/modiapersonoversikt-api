package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf

val hentSakerGraphQLQuery = "query {" +
        "  dokumentoversiktBruker(brukerId: {id: \\\"[FNR]\\\", type: FNR}, journalstatuser: [JOURNALFOERT, FERDIGSTILT, EKSPEDERT], foerste: 9999) {" +
        "    journalposter {" +
        "      journalposttype" +
        "      datoOpprettet" +
        "      tittel" +
        "      journalpostId" +
        "      journalstatus" +
        "      dokumenter {" +
        "        tittel" +
        "        dokumentInfoId" +
        "        dokumentvarianter{" +
        "          variantformat" +
        "          saksbehandlerHarTilgang" +
        "        }" +
        "        logiskeVedlegg {" +
        "          tittel" +
        "        }" +
        "      }" +
        "      sak{" +
        "        fagsakId" +
        "        fagsaksystem" +
        "        arkivsaksystem" +
        "        arkivsaksnummer" +
        "      }" +
        "      tema" +
        "      temanavn" +
        "      journalstatus" +
        "      avsenderMottakerNavn" +
        "      avsenderMottakerId" +
        "      bruker{" +
        "        id" +
        "        type" +
        "      }" +
        "      relevanteDatoer{" +
        "        dato" +
        "        datotype" +
        "      }" +
        "    }" +
        "  }" +
        "}"

fun hentSakerJsonQuery(fnr: String): String {
    val medFnr = hentSakerGraphQLQuery.replace("[FNR]", fnr)
    return "{\"query\":\"$medFnr\"}"
}