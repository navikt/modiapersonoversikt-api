import React, { PropTypes as PT } from 'react';
import { FormattedDate } from 'react-intl';
import { datoformat } from './../../utils/dato-utils';

class Sakstema extends React.Component {

    componentDidMount() {
        const { tema, valgtTema } = this.props;
        const erValgt = tema.temakode === valgtTema.temakode ? 'valgt' : '';
        if (erValgt && tema.temakode !== 'alle') {
            setTimeout(() => this.refs.radio && this.refs.radio.focus(), 0);
        }
    }

    render() {
        const { tema, valgtTema, nokkelinfo } = this.props;

        // Sjekk på temakode ettersom 'alletemaet' blir laget på nytt ved rerender.
        const erValgt = tema.temakode === valgtTema.temakode ? 'valgt' : '';
        const velgSak = () => this.props.velgSak(tema);
        const id = `sakstemaRadioListe--${tema.temakode}`;
        const sisteOppdatering = nokkelinfo.sisteOppdatering || '';
        const behandlingsstatus = tema.temakode !== 'alle' && nokkelinfo.behandlingsstatus ?
            nokkelinfo.behandlingsstatus : '';
        const sisteOppdateringTekst = <FormattedDate value={sisteOppdatering} {...datoformat.NUMERISK_2_DIGIT} />;
        const harTilgang = tema.harTilgang ? '' : 'tema-ikke-tilgang';
        const skjultIngenTilgangTekst = !tema.harTilgang ? <p className="vekk">Ikke tilgang til sakstema</p> : '';

        return (
            <div className={`saksoversikt-liste-element ${erValgt} ${harTilgang}`}>
                <input type="radio" id={id} ref="radio" readOnly checked={erValgt} name="sakstemaRadioListe"
                  onClick={velgSak}
                />
                <label htmlFor={id}>
                    {skjultIngenTilgangTekst}
                    <p className="datotekst">{sisteOppdateringTekst}</p>
                    <p className="temaliste-label stortekst">{tema.temanavn}</p>
                    {behandlingsstatus}
                </label>
            </div>
        );
    }
}

Sakstema.propTypes = {
    tema: PT.object.isRequired,
    valgtTema: PT.object.isRequired,
    velgSak: PT.func.isRequired,
    nokkelinfo: PT.object.isRequired
};

export default Sakstema;
