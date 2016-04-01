import React from 'react';
import { FormattedMessage } from 'react-intl';
import { javaLocalDateTimeToJSDate } from './../dato-utils';

const finnTekst = (antallUnderBehandling, antallFerdigBehandlet) => {
    const tallSomTekstUnderBehandling =
        <FormattedMessage id="behandlingsstatus.telling" values={{ antall: antallUnderBehandling }}/>;
    const tallSomTekstFerdigBehandlet =
        <FormattedMessage id="behandlingsstatus.telling" values={{ antall: antallFerdigBehandlet }}/>;
    const values = {
        antallSoknaderUnderBehandling: tallSomTekstUnderBehandling,
        antallSoknaderFerdigBehandlet: tallSomTekstFerdigBehandlet
    };
    let key;
    if (antallUnderBehandling > 0 && antallFerdigBehandlet > 0) {
        return (
            <div className="behandlingsstatus-tekst">
                <p className="temaliste-label">
                    <FormattedMessage id={'behandlingsstatus.underbehandling'} values={values}/>
                </p>
                <p className="temaliste-label">
                    <FormattedMessage id={'behandlingsstatus.ferdigbehandlet'} values={values}/>
                </p>
            </div>);
    } else if (antallUnderBehandling > 0) {
        key = 'behandlingsstatus.underbehandling';
    } else if (antallFerdigBehandlet > 0) {
        key = 'behandlingsstatus.ferdigbehandlet';
    }

    return key ?
        <div className="behandlingsstatus-tekst">
            <p className="temaliste-label">
                <FormattedMessage id={key} values={values}/>
            </p>
        </div> : null;
};

const datoNyereEnnAntallDager = (date, antallDager) => {
    const grense = new Date();
    grense.setDate(grense.getDate() - antallDager);
    return date >= grense;
};

export const underBehandlingEllerNyereEnnGrenseverdi = (antallDager) => (behandlingskjede) =>
behandlingskjede.status === 'UNDER_BEHANDLING' ||
datoNyereEnnAntallDager(new Date(javaLocalDateTimeToJSDate(behandlingskjede.sistOppdatert)), antallDager);


export const finnAntallBehandlingskjeder = (gyldigeBehandlingskjeder) => {
    const antallUnderBehandling = gyldigeBehandlingskjeder.filter(kjede => kjede.status === 'UNDER_BEHANDLING').length;
    const antallFerdigBehandlet = gyldigeBehandlingskjeder.filter(kjede => kjede.status === 'FERDIG_BEHANDLET').length;

    return {
        antallUnderBehandling,
        antallFerdigBehandlet
    };
};

export const finnBehandlingsstatus = (behandlingskjeder, antallDagerFerdigBehandletStatusErGyldig) => {
    const gyldigeBehandlingskjeder = behandlingskjeder
        .filter(underBehandlingEllerNyereEnnGrenseverdi(antallDagerFerdigBehandletStatusErGyldig));

    const { antallUnderBehandling, antallFerdigBehandlet } = finnAntallBehandlingskjeder(gyldigeBehandlingskjeder);

    return finnTekst(antallUnderBehandling, antallFerdigBehandlet);
};
