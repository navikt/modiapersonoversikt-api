package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders

import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk

data class KodeverkProviders(
    val fellesKodeverk: FellesKodeverk.Provider,
    val sfHenvendelseKodeverk: SfHenvendelseKodeverk.Provider,
    val oppgaveKodeverk: OppgaveKodeverk.Provider
)
