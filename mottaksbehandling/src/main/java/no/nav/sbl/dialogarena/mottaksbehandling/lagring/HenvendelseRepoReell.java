package no.nav.sbl.dialogarena.mottaksbehandling.lagring;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.common.integrasjonsutils.RowMapper;
import no.nav.sbl.dialogarena.common.integrasjonsutils.SQL;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.types.Pingable;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;

public class HenvendelseRepoReell implements HenvendelseRepo {

	/*
create table henvendelse (
    behandlingsid varchar(255 char) not null,
    aktor varchar(255 char),
    oppgaveid varchar(255 char),
    sporsmal varchar(255 char),
    svar varchar(255 char),
    traad varchar(255 char),
    tema varchar(255 char),
    sensitiv number(1, 0),
    opprettet timestamp default sysdate not null,
    constraint henvendelse_pk primary key (behandlingsid)
);

--create index HENVENDELSE_AKTOR_I on HENVENDELSE(AKTOR);
*/

	private DataSource ds;

	public HenvendelseRepoReell(DataSource ds) {
		this.ds = ds;
	}

	@Override
	public Optional<Record<SporsmalOgSvar>> hentMedBehandlingsId(String behandlingsId) {
		return optional(SQL.query(ds, mapper, "select * from henvendelse where behandlingsid = ?", behandlingsId));
	}

	@Override
	public Optional<Record<SporsmalOgSvar>> hentMedOppgaveId(String oppgaveId) {
		return optional(SQL.query(ds, mapper, "select * from henvendelse where oppgaveid = ?", oppgaveId));
	}

	@Override
	public Record<SporsmalOgSvar> opprett(Record<SporsmalOgSvar> henvendelse) {
		String behandlingsId = IdGenerator.lagBehandlingsId(SQL.nesteSekvensverdi(ds, "behandlingsid_seq"));
		Record<SporsmalOgSvar> hvMedId = henvendelse.with(SporsmalOgSvar.behandlingsid, behandlingsId);
		String sql = "insert into henvendelse (aktor, oppgaveid, sporsmal, sporsmalbehandlingsid, svar, traad, tema, sensitiv, opprettet, behandlingsid) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		List<Object> args = hvMedId.valuesFor(SporsmalOgSvar.aktor, SporsmalOgSvar.oppgaveid, SporsmalOgSvar.sporsmal, SporsmalOgSvar.sporsmaletsBehandlingsId, SporsmalOgSvar.svar, SporsmalOgSvar.traad, SporsmalOgSvar.tema,
                SporsmalOgSvar.sensitiv, SporsmalOgSvar.opprettet, SporsmalOgSvar.behandlingsid);
		SQL.update(ds, sql, args.toArray());
		return hvMedId;
	}

	@Override
	public void oppdater(Record<SporsmalOgSvar> henvendelse) {
		String sql = "update henvendelse set aktor = ?, oppgaveid = ?, sporsmal = ?, sporsmalbehandlingsid = ?, svar = ?, traad = ?, tema = ?, sensitiv = ?, opprettet = ? where behandlingsid = ?";
		List<Object> args = henvendelse.valuesFor(SporsmalOgSvar.aktor, SporsmalOgSvar.oppgaveid, SporsmalOgSvar.sporsmal, SporsmalOgSvar.sporsmaletsBehandlingsId, SporsmalOgSvar.svar, SporsmalOgSvar.traad, SporsmalOgSvar.tema,
                SporsmalOgSvar.sensitiv, SporsmalOgSvar.opprettet, SporsmalOgSvar.behandlingsid);
		SQL.update(ds, sql, args.toArray());
	}

	private static RowMapper<Record<SporsmalOgSvar>> mapper = new RowMapper<Record<SporsmalOgSvar>>() {
		public Record<SporsmalOgSvar> map(ResultSet rs) throws SQLException {
			return new Record<SporsmalOgSvar>()
					.with(SporsmalOgSvar.aktor, rs.getString("aktor"))
					.with(SporsmalOgSvar.behandlingsid, rs.getString("behandlingsid"))
					.with(SporsmalOgSvar.oppgaveid, rs.getString("oppgaveid"))
					.with(SporsmalOgSvar.sporsmal, rs.getString("sporsmal"))
                    .with(SporsmalOgSvar.sporsmaletsBehandlingsId, rs.getString("sporsmalbehandlingsid"))
					.with(SporsmalOgSvar.svar, rs.getString("svar"))
					.with(SporsmalOgSvar.traad, rs.getString("traad"))
					.with(SporsmalOgSvar.tema, Tema.valueOf(rs.getString("tema")))
					.with(SporsmalOgSvar.sensitiv, SQL.getBoolean(rs, "sensitiv"))
					.with(SporsmalOgSvar.opprettet, SQL.getDateTime(rs, "opprettet"));
		}
	};

    @Override
    public Pingable.Ping ping() {
        try {
            SQL.query(ds, new RowMapper.LongMapper(), "SELECT COUNT(*) FROM HENVENDELSE");
            return Pingable.Ping.lyktes("MOTTAKSBEHANDLING_DATABASE_OK");
        } catch (Exception e) {
            return Pingable.Ping.feilet("MOTTAKSBEHANDLING_DATABASE_ERROR", e);
        }
    }

}