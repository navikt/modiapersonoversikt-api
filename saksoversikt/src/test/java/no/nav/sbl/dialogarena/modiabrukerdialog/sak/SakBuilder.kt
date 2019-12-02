package no.nav.sbl.dialogarena.modiabrukerdialog.sak

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.*
import org.joda.time.DateTime
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

class SakBuilder {
    private lateinit var behandlingskjede: Array<out Behandlingskjede>
    private lateinit var saksId: String
    private lateinit var sakstema: Sakstemaer

    fun withSaksId(saksId: String): SakBuilder {
        this.saksId = saksId
        return this
    }

    fun withSakstema(sakstemaer: String): SakBuilder {
        this.sakstema = Sakstemaer().apply { value = sakstemaer }
        return this
    }

    fun withOpprettet(date: DateTime): SakBuilder {
        return this
    }

    fun withBehandlingskjede(vararg behandlingskjede: Behandlingskjede): SakBuilder {
        this.behandlingskjede = behandlingskjede
        return this
    }

    fun build(): Sak {
        val builder = this
        return Sak().apply {
            saksId = builder.saksId
            sakstema = builder.sakstema
            behandlingskjede.addAll(builder.behandlingskjede)
        }
    }

    companion object {
        @JvmStatic
        fun create(): SakBuilder = SakBuilder()
    }
}

class BehandlingskjedeBuilder {
    private lateinit var behandlingskjedeId: String
    private lateinit var behandlingskjedetype: Behandlingskjedetyper
    private lateinit var behandlingstema: Behandlingstemaer
    private lateinit var start: DateTime
    private lateinit var slutt: DateTime
    private lateinit var sisteBehandlingREF: String
    private lateinit var sisteBehandlingstype: Behandlingstyper
    private lateinit var behandlingsListeRef: String
    private lateinit var sisteBehandlingsoppdatering: DateTime
    private lateinit var sisteBehandlingsstatus: Behandlingsstatuser
    private lateinit var sisteBehandlingAvslutningsstatus: Avslutningsstatuser


    fun withBehandlingskjedeId(behandlingskjedeId: String): BehandlingskjedeBuilder {
        this.behandlingskjedeId = behandlingskjedeId
        return this
    }

    fun withBehandlingskjedetype(behandlingskjedetype: String): BehandlingskjedeBuilder {
        this.behandlingskjedetype = Behandlingskjedetyper().apply { value = behandlingskjedetype }
        return this
    }

    fun withBehandlingstema(behandlingstemaer: String): BehandlingskjedeBuilder {
        this.behandlingstema = Behandlingstemaer().apply { value = behandlingstemaer }
        return this
    }

    fun withStart(date: DateTime): BehandlingskjedeBuilder {
        this.start = date
        return this
    }

    fun withSlutt(date: DateTime): BehandlingskjedeBuilder {
        this.slutt = date
        return this
    }

    fun withSisteBehandlingREF(behandlingREF: String): BehandlingskjedeBuilder {
        this.sisteBehandlingREF = behandlingREF
        return this
    }

    fun withSisteBehandlingstype(behandlingstype: String): BehandlingskjedeBuilder {
        this.sisteBehandlingstype = Behandlingstyper().apply { value = behandlingstype }
        return this
    }

    fun withBehandlingsListeRef(behandlingsListeRef: String): BehandlingskjedeBuilder {
        this.behandlingsListeRef = behandlingsListeRef
        return this
    }

    fun withSisteBehandlingsoppdatering(date: DateTime): BehandlingskjedeBuilder {
        this.sisteBehandlingsoppdatering = date
        return this
    }
    fun withSisteBehandlingsstatus(sisteBehandlingsstatus: String): BehandlingskjedeBuilder {
        this.sisteBehandlingsstatus = Behandlingsstatuser().apply { value = sisteBehandlingsstatus }
        return this
    }

    fun withSisteBehandlingAvslutningsstatus(sisteBehandlingAvslutningsstatus: String): BehandlingskjedeBuilder {
        this.sisteBehandlingAvslutningsstatus = Avslutningsstatuser().apply { value = sisteBehandlingAvslutningsstatus }
        return this
    }


    fun build(): Behandlingskjede {
        val builder = this
        return Behandlingskjede()
                .apply {
                    behandlingskjedeId = builder.behandlingskjedeId
                    behandlingskjedetype = builder.behandlingskjedetype
                    behandlingstema = builder.behandlingstema
                    start = toXMLCalendar(builder.start)
                    slutt = toXMLCalendar(builder.slutt)
                    sisteBehandlingREF = builder.sisteBehandlingREF
                    sisteBehandlingstype = builder.sisteBehandlingstype
                    behandlingsListeRef.add(builder.behandlingsListeRef)
                    sisteBehandlingsoppdatering = toXMLCalendar(builder.sisteBehandlingsoppdatering)
                    sisteBehandlingsstatus = builder.sisteBehandlingsstatus
                    sisteBehandlingAvslutningsstatus = builder.sisteBehandlingAvslutningsstatus
                }
    }


    companion object {
        @JvmStatic
        fun create(): BehandlingskjedeBuilder = BehandlingskjedeBuilder()
    }
}

private fun toXMLCalendar(date: DateTime):XMLGregorianCalendar {
    return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.toString())
}