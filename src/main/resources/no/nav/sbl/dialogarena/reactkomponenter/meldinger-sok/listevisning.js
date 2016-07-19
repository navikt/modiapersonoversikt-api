import React, { PropTypes as pt } from 'react';
import ReactDOM from 'react-dom';
import sanitize from 'sanitize-html';
import format from 'string-format';
import AntallMeldinger from './AntallMeldinger';

const erValgtTekst = (traad, valgtTraad) => traad === valgtTraad;

class Listevisning extends React.Component {
    constructor(props) {
        super(props);
        this.tekstChangedProxy = this.tekstChangedProxy.bind(this);
    }

    tekstChangedProxy() {
        this.props.store.traadChanged(this.props.traad, ReactDOM.findDOMNode(this).parentNode);
    }

    render() {
        const { traad, valgtTraad } = this.props;
        const { antallMeldingerIOpprinneligTraad, statusKlasse } = traad;
        const erValgt = erValgtTekst(traad, valgtTraad);
        const cls = erValgt ? 'meldingsforhandsvisning valgt' : 'meldingsforhandsvisning';
        const visningsDato = traad.visningsDato;
        const dato = sanitize(visningsDato, { allowedTags: ['em'] });
        
        const temagruppe = !traad.temagruppe ? '' : `, ${traad.temagruppe}`;
        let meldingsStatus = `${traad.statusTekst}${temagruppe}`;
        meldingsStatus = sanitize(meldingsStatus, { allowedTags: ['em'] });
        const innhold = sanitize(traad.innhold, { allowedTags: ['em'] });

        const statusIkonTekst = format('{0}, {1} {2}',
            traad.statusKlasse.match(/ubesvart$/) ? 'Ubesvart' : 'Besvart',
            antallMeldingerIOpprinneligTraad,
            antallMeldingerIOpprinneligTraad === 1 ? 'melding' : 'meldinger'
        );

        return (
            <div className="sok-element" onClick={this.tekstChangedProxy}>
                <input id={`melding ${traad.key}`} name="tekstListeRadio" type="radio" readOnly checked={erValgt}/>
                <label htmlFor={`melding ${traad.key}`} className={cls}>
                    <div className={`melding-detaljer ${statusKlasse}`}>
                        <div className={`statusIkon ${statusKlasse}`} aria-hidden="true"></div>
                        <AntallMeldinger antall={antallMeldingerIOpprinneligTraad} />
                        <p className="vekk">{statusIkonTekst}</p>
                        <div className="melding-data">
                            <p dangerouslySetInnerHTML={{ __html: dato }}></p>
                            <p className={'meldingstatus'} dangerouslySetInnerHTML={{ __html: meldingsStatus }}></p>
                            <p className="fritekst" dangerouslySetInnerHTML={{ __html: innhold }}></p>
                        </div>
                    </div>
                </label>
            </div>
        );
    }
}

Listevisning.propTypes = {
    traad: pt.shape({
        statusKlasse: pt.string,
        antallMeldingerIOpprinneligTraad: pt.number,
        statusTekst: pt.string.isRequired,
        temagruppe: pt.string,
        innhold: pt.string
    }),
    valgtTraad: pt.object.isRequired
};

export default Listevisning;
