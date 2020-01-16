package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.*
import org.joda.time.DateTime
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

class SakBuilder {
    private var saksId: String? = null
    private var behandlingskjede: Array<out Behandlingskjede>? = null
    private var sakstema: Sakstemaer? = null

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
            builder.behandlingskjede?.also { behandlingskjede.addAll(it) }
        }
    }

    companion object {
        @JvmStatic
        fun create(): SakBuilder = SakBuilder()
    }
}

class BehandlingskjedeBuilder {
    private var behandlingskjedeId: String? = null
    private var behandlingskjedetype: Behandlingskjedetyper? = null
    private var behandlingstema: Behandlingstemaer? = null
    private var start: DateTime? = null
    private var slutt: DateTime? = null
    private var sisteBehandlingREF: String? = null
    private var sisteBehandlingstype: Behandlingstyper? = null
    private var behandlingsListeRef: String? = null
    private var sisteBehandlingsoppdatering: DateTime? = null
    private var sisteBehandlingsstatus: Behandlingsstatuser? = null
    private var sisteBehandlingAvslutningsstatus: Avslutningsstatuser? = null


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

private fun toXMLCalendar(date: DateTime?):XMLGregorianCalendar? {
    return date
            ?.let {
                DatatypeFactory.newInstance().newXMLGregorianCalendar(it.toString())
            }
}