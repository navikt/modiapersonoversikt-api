import React from 'react';
import PT from 'prop-types';

import SykepengerLamell from 'modiapersonoversikt/build/dist/components/standalone/Sykepenger/SykepengerLamell';
import moment from 'moment';

function konverterIDDatoTilNormalisertDatoFormat(idDato) {
    return moment(idDato, ["DD.MM.YYYY"]).format('YYYY-MM-DD');
}

class NySykepenger extends React.Component {
    render() {
        const normalisertDato = konverterIDDatoTilNormalisertDatoFormat(this.props.sykmeldtFraOgMed);
        return <SykepengerLamell fødselsnummer={this.props.fødselsnummer} sykmeldtFraOgMed={normalisertDato}/>;
    }
}

NySykepenger.propTypes = {
    fødselsnummer: PT.string.isRequired,
    sykmeldtFraOgMed: PT.string.isRequired
};

export default NySykepenger;
