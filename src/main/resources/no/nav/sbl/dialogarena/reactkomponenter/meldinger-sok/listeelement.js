import React from 'react';
import PT from 'prop-types';
import sanitize from 'sanitize-html';
import format from 'string-format';
import AntallMeldinger from './AntallMeldinger';
import { Checkbox } from 'nav-frontend-skjema';

function ListeElement(props) {
    const { antallMeldingerIOpprinneligTraad, statusKlasse } = props.traad;
    const cls = props.erValgt ? 'meldingsforhandsvisning valgt' : 'meldingsforhandsvisning';
    const dato = sanitize(props.traad.visningsDato, { allowedTags: ['em'] });
    const temagruppe = props.traad.temagruppe ? `, ${props.traad.temagruppe}` : '';
    const meldingsStatus = sanitize(`${props.traad.statusTekst}${temagruppe}`, { allowedTags: ['em'] });
    const innhold = sanitize(props.traad.innhold, { allowedTags: ['em'] });
    const statusIkonTekst = format('{0}, {1} {2}',
        props.traad.ikontekst,
        antallMeldingerIOpprinneligTraad,
        antallMeldingerIOpprinneligTraad === 1 ? 'melding' : 'meldinger'
    );
    const checkBox = props.visCheckBox ? <Checkbox className="checkbox" label="Besvar denne oppgaven" id={props.traad.traadId} /> : '';

    return (
        <div className="sok-element">
            <input
                id={`melding-${props.traad.key}`}
                name="tekstListeRadio"
                type="radio"
                readOnly
                checked={props.erValgt}
                onClick={() => props.onClick()}
            />
            <label htmlFor={`melding-${props.traad.key}`} className={cls}>
                <div className={`melding-detaljer ${statusKlasse}`}>
                    <div className="melding-detaljer-venstre">
                        <div className={`statusIkon ${statusKlasse}`} aria-hidden="true">
                            <AntallMeldinger antall={antallMeldingerIOpprinneligTraad}/>
                            <p className="vekk">{statusIkonTekst}</p>
                        </div>
                    </div>
                    <div className="melding-data">
                        <p className="opprettet" dangerouslySetInnerHTML={{__html: dato}}></p>
                        <p className="meldingstatus" dangerouslySetInnerHTML={{__html: meldingsStatus}}></p>
                        <p className="fritekst" dangerouslySetInnerHTML={{__html: innhold}}></p>
                    </div>
                </div>
            </label>
            {checkBox}
        </div>
    );
}

ListeElement.propTypes = {
    traad: PT.shape({
        statusKlasse: PT.string,
        antallMeldingerIOpprinneligTraad: PT.number,
        statusTekst: PT.string.isRequired,
        ikontekst: PT.string.isRequired,
        temagruppe: PT.string,
        erMonolog: PT.bool,
        innhold: PT.string
    }),
    visCheckBox: PT.bool.isRequired,
    onClick: PT.func.isRequired,
    erValgt: PT.bool.isRequired
};

export default ListeElement;
