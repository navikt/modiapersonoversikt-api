package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders

data class KodeverkProviders(
    val fellesKodeverk: FellesKodeverkProvider,
    val sfHenvendelseKodeverk: SfHenvendelseKodeverkProvider,
    val oppgaveKodeverk: OppgaveKodeverkProvider
)
