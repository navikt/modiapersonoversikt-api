import React from 'react';
import PT from 'prop-types';
import Kontaktinformasjon from './kontaktinformasjon';
import { prettyDate } from './dato-formatering';

function VarselRadElement(props) {
    const melding = props.melding;
    const resources = props.store.getResources();
    const epostEmne = melding.kanal === 'EPOST' && melding.epostemne ?
        <div><span className="innhold-epostemne">{melding.epostemne}</span><br /></div> :
        undefined;

    const kanal = `${resources.getOrElse(`varsel.kanal.${melding.kanal}`, melding.kanal)}: `;
    const varselDato = melding.utsendingsTidspunkt ? prettyDate(melding.utsendingsTidspunkt) : 'ikke sendt';
    return (
        <li className="varsel-rad-element">
            <div className="varsel-innhold-container">
                <span className="innhold-kanal">{kanal}</span>
                <div className="innhold-melding">
                    {epostEmne}
                    <span>{melding.innhold}</span>
                </div>
                <p>
                    <span className="varsel-dato">{varselDato}</span>
                    <Kontaktinformasjon
                        kanal={melding.kanal}
                        mottakerInformasjon={melding.mottakerInformasjon}
                        resources={resources}
                    />
                </p>
            </div>
        </li>
    );
}

VarselRadElement.propTypes = {
    store: PT.object.isRequired,
    melding: PT.object.isRequired
};

export default VarselRadElement;
