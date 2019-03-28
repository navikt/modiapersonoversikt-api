package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf

const val dokumentoversiktBrukerGraphQLQuery = "query {" +
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
        "      avsenderMottaker {" +
        "        erLikBruker" +
        "        navn" +
        "      }" +
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

fun dokumentoversiktBrukerJsonQuery(fnr: String): String {
    val medFnr = dokumentoversiktBrukerGraphQLQuery.replace("[FNR]", fnr)
    return "{\"query\":\"$medFnr\"}"
}