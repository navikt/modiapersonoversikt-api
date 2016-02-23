import React, { PropTypes as PT } from 'react';
import { FormattedDate } from 'react-intl';
import { dokumentMetadataTilJSDate } from './../../utils/dato-utils';

function hentDatostreng(dokumentmetadata) {
    //Hack enn så lenge. Bør bruke FormattedDate.
    if (!dokumentmetadata || dokumentmetadata.length === 0) {
        return "";
    } else {
        const dato = dokumentmetadata[0].dato;
        const day = dato.dayOfMonth;
        const year = dato.year;
        const month = dato.monthValue;
        return day + "." + month + "." + year;
    }
}

class Sakstema extends React.Component {
    render() {
        const { tema, dokumentmetadata, valgtTema } = this.props;
        const erValgt = tema === valgtTema ? 'valgt' : '';
        const datostreng = hentDatostreng(dokumentmetadata);

        return (
            <li>
                <a className={`saksoversikt-liste-element ${erValgt}`}
                   href="javascript:void(0)"
                   onClick={() => this.props.velgSak(tema.temakode)}
                >
                    <label className="temaliste-label datotekst">{datostreng}</label>
                    <label className="temaliste-label stortekst">{tema.temanavn}</label>
                </a>
            </li>
        );
    }
}

Sakstema.propTypes = {
    tema: PT.object.isRequired,
    valgtTema: PT.object.isRequired,
    velgSak: PT.func.isRequired
};

export default Sakstema;
