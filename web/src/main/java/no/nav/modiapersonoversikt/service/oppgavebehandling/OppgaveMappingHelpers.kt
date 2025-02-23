package no.nav.modiapersonoversikt.service.oppgavebehandling

import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.*

object OppgaveMappingHelpers {
    fun requiredOppgaveId(value: Long?): Long = requireNotNull(value)

    fun <T : Any> required(value: T?): T = requireNotNull(value)

    fun convertEnumToPutReq(value: OppgaveJsonDTO.Status): PutOppgaveRequestJsonDTO.Status = convertEnum(value)

    fun convertEnumToPutReq(value: OppgaveJsonDTO.Prioritet): PutOppgaveRequestJsonDTO.Prioritet = convertEnum(value)

    fun convertEnumToPutResp(value: OppgaveJsonDTO.Status): PutOppgaveResponseJsonDTO.Status = convertEnum(value)

    fun convertEnumToPutResp(value: PutOppgaveRequestJsonDTO.Status): PutOppgaveResponseJsonDTO.Status = convertEnum(value)

    fun convertEnumToPutResp(value: OppgaveJsonDTO.Prioritet): PutOppgaveResponseJsonDTO.Prioritet = convertEnum(value)

    fun convertEnumToPutResp(value: PutOppgaveRequestJsonDTO.Prioritet): PutOppgaveResponseJsonDTO.Prioritet = convertEnum(value)

    fun convertEnumToPostResp(value: OppgaveJsonDTO.Status): PostOppgaveResponseJsonDTO.Status = convertEnum(value)

    fun convertEnumToPostResp(value: OppgaveJsonDTO.Prioritet): PostOppgaveResponseJsonDTO.Prioritet = convertEnum(value)

    fun convertEnumToGetResp(value: OppgaveJsonDTO.Status): GetOppgaveResponseJsonDTO.Status = convertEnum(value)

    fun convertEnumToGetResp(value: OppgaveJsonDTO.Prioritet): GetOppgaveResponseJsonDTO.Prioritet = convertEnum(value)

    fun convertEnumToJson(value: GetOppgaveResponseJsonDTO.Status): OppgaveJsonDTO.Status = convertEnum(value)

    fun convertEnumToJson(value: GetOppgaveResponseJsonDTO.Prioritet): OppgaveJsonDTO.Prioritet = convertEnum(value)

    fun convertEnumToJson(value: PutOppgaveResponseJsonDTO.Status): OppgaveJsonDTO.Status = convertEnum(value)

    fun convertEnumToJson(value: PutOppgaveResponseJsonDTO.Prioritet): OppgaveJsonDTO.Prioritet = convertEnum(value)

    private inline fun <S : Enum<S>, reified T : Enum<T>> convertEnum(value: S): T {
        val allowValues: Array<T> = T::class.java.enumConstants
        return allowValues
            .find { it.name == value.name }
            ?: throw IllegalStateException("Fant ikke gyldig enum verdi")
    }
}
