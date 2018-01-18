import React from 'react';
import PT from 'prop-types';
import sanitize from 'sanitize-html';
import format from 'string-format';
import AntallMeldinger from './AntallMeldinger';
import { Checkbox } from 'nav-frontend-skjema';

const erValgtTekst = (traad, valgtTraad) => traad === valgtTraad;

class Listevisning extends React.Component {
    constructor(props) {
        super(props);
        this.tekstChangedProxy = this.tekstChangedProxy.bind(this);
    }

    tekstChangedProxy() {
        this.props.store.traadChanged(this.props.traad, this.node.parentNode);
    }

    shouldComponentUpdate({ valgtTraad }) {
        const dagensState = erValgtTekst(this.props.traad, this.props.valgtTraad);
        const nesteState = erValgtTekst(this.props.traad, valgtTraad);

        return dagensState !== nesteState;
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
            traad.ikontekst,
            antallMeldingerIOpprinneligTraad,
            antallMeldingerIOpprinneligTraad === 1 ? 'melding' : 'meldinger'
        );
        const checkBox = this.props.visCheckBox
            ? <Checkbox className="checkbox" label="" id={traad.traadId} /> : '';
        return (
            <div className="sok-element" ref={node => this.node = node}>
                <input id={`melding ${traad.key}`} name="tekstListeRadio" type="radio" readOnly checked={erValgt} onClick={this.tekstChangedProxy} />
                <label htmlFor={`melding ${traad.key}`} className={cls}>
                    <div className={`melding-detaljer ${statusKlasse}`}>
                        <div className="melding-detaljer-venstre">
                            <div className={`statusIkon ${statusKlasse}`} aria-hidden="true">
                                <AntallMeldinger antall={antallMeldingerIOpprinneligTraad} />
                                <p className="vekk">{statusIkonTekst}</p>
                            </div>
                            {checkBox}
                        </div>
                        <div className="melding-data">
                            <p className="opprettet">{dato}</p>
                            <p className="meldingstatus">{meldingsStatus}</p>
                            <p className="fritekst">{innhold}</p>
                        </div>
                    </div>
                </label>
            </div>
        );
    }
}

Listevisning.propTypes = {
    traad: PT.shape({
        statusKlasse: PT.string,
        antallMeldingerIOpprinneligTraad: PT.number,
        statusTekst: PT.string.isRequired,
        ikontekst: PT.string.isRequired,
        temagruppe: PT.string,
        erMonolog: PT.bool,
        innhold: PT.string
    }),
    valgtTraad: PT.object.isRequired,
    visCheckBox: PT.bool.isRequired,
    store: PT.object.isRequired
};

export default Listevisning;
