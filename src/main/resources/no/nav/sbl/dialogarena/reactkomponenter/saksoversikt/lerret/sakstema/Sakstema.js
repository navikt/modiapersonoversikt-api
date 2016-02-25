import React, { PropTypes as PT } from 'react';
import { FormattedDate } from 'react-intl';
import { FormattedMessage } from 'react-intl';
import { dokumentMetadataTilJSDate } from './../../utils/dato-utils';

class Sakstema extends React.Component {
    render() {
        const { tema, valgtTema, velgSak, nokkelinfo} = this.props;
        // Sjekk på temakode ettersom 'alletemaet' blir laget på nytt ved rerender.
        const erValgt = tema.temakode === valgtTema.temakode ? 'valgt' : '';
        const id = `sakstemaRadioListe--${tema.temakode}`;
        const sisteOppdatering = nokkelinfo.sisteOppdatering? nokkelinfo.sisteOppdatering : "";
        const statusTekst = nokkelinfo.behandlingsstatus? nokkelinfo.behandlingsstatus : "";
        const datoOppdatert =  <FormattedDate value={sisteOppdatering} />

        return (
            <div className={`saksoversikt-liste-element ${erValgt}`}>
                <input type="radio" id={id} readOnly checked={erValgt} name="sakstemaRadioListe"
                       onClick={() => velgSak(tema)}
                />
                <label htmlFor={id}>
                    <p className="temaliste-label datotekst">{datoOppdatert}</p>
                    <p className="temaliste-label stortekst">{tema.temanavn}</p>
                    <p className="temaliste-label datotekst">{statusTekst}</p>
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
