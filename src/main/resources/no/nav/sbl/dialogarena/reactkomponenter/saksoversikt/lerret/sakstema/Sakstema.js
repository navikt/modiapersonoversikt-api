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
        const { tema, valgtTema, velgSak } = this.props;
        // Sjekk på temakode ettersom 'alletemaet' blir laget på nytt ved rerender.
        const erValgt = tema.temakode === valgtTema.temakode ? 'valgt' : '';
        const datostreng = hentDatostreng(tema.dokumentmetadata);
        const id = `sakstemaRadioListe--${tema.temakode}`;

        return (
            <div className={`saksoversikt-liste-element ${erValgt}`}>
                <input type="radio" id={id} readOnly checked={erValgt} name="sakstemaRadioListe"
                       onClick={() => velgSak(tema)}
                />
                <label htmlFor={id}>
                    <p className="temaliste-label datotekst">{datostreng}</p>
                    <p className="temaliste-label stortekst">{tema.temanavn}</p>
                </label>
            </div>
        );
    }
}

Sakstema.propTypes = {
    tema: PT.object.isRequired,
    valgtTema: PT.object.isRequired,
    velgSak: PT.func.isRequired
};

export default Sakstema;
