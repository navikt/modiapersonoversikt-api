import React, { PropTypes as PT } from 'react';
import { FormattedDate } from 'react-intl';
import { FormattedMessage } from 'react-intl';
import { dokumentMetadataTilJSDate } from './../../utils/dato-utils';

class Sakstema extends React.Component {

    componentDidMount() {
        const { tema, valgtTema } = this.props;
        const erValgt = tema.temakode === valgtTema.temakode ? 'valgt' : '';
        if (erValgt && tema.temakode !== 'alle') {
            setTimeout(() => this.refs.radio.focus(), 0);
        }
    }

    render() {
        const { tema, valgtTema, velgSak, nokkelinfo, velgJournalpost} = this.props;
        // Sjekk på temakode ettersom 'alletemaet' blir laget på nytt ved rerender.
        const erValgt = tema.temakode === valgtTema.temakode ? 'valgt' : '';
        const id = `sakstemaRadioListe--${tema.temakode}`;
        const sisteOppdatering = nokkelinfo.sisteOppdatering ? nokkelinfo.sisteOppdatering : "";
        const behandlingsstatus = tema.temakode === 'alle' ? "" : nokkelinfo.behandlingsstatus ? nokkelinfo.behandlingsstatus : "";
        const sisteOppdateringTekst = <FormattedDate day="2-digit" month="2-digit" year="2-digit" value={sisteOppdatering}/>

        return (
            <div className={`saksoversikt-liste-element ${erValgt}`}>
                <input type="radio" id={id} ref="radio" readOnly checked={erValgt} name="sakstemaRadioListe"
                       onClick={() => velgSak(tema)}
                />
                <label htmlFor={id}>
                    <p className="temaliste-label datotekst">{sisteOppdateringTekst}</p>
                    <p className="temaliste-label stortekst">{tema.temanavn}</p>
                    <p className="temaliste-label datotekst">{behandlingsstatus}</p>
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
