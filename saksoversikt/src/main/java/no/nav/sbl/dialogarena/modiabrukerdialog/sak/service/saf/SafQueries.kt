package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf

val dokumentoversiktBrukerGraphQLQuery = """
    query {
          dokumentoversiktBruker(brukerId: {id: \"[FNR]\", type: FNR}, journalstatuser: [JOURNALFOERT, FERDIGSTILT, EKSPEDERT], fraDato: \"2016-06-04\", foerste: 9999) {
            journalposter {
              journalposttype
              datoOpprettet
              tittel
              journalpostId
              journalstatus
              dokumenter {
                tittel
                dokumentInfoId
                dokumentvarianter{
                  saksbehandlerHarTilgang
                  variantformat
                  skjerming
                }
                logiskeVedlegg {
                  tittel
                }
              }
              sak{
                fagsakId
                fagsaksystem
                arkivsaksystem
                arkivsaksnummer
              }
              tema
              temanavn
              journalstatus
              avsenderMottaker {
                erLikBruker
                navn
              }
              bruker{
                id
                type
              }
              relevanteDatoer{
                dato
                datotype
              }
            }
          }
        }
""".trimIndent()

fun dokumentoversiktBrukerJsonQuery(fnr: String): String {
    val medFnrOgFjernLinebreak = dokumentoversiktBrukerGraphQLQuery
            .replace("[FNR]", fnr)
            .replace("\n", "")
    return "{\"query\":\"$medFnrOgFjernLinebreak\"}"
}
