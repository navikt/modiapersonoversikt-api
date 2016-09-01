import React, { PropTypes as PT } from 'react';
import Kontaktinformasjon from './kontaktinformasjon';
import { prettyDate } from './dato-formatering';

class VarselRadElement extends React.Component {
    constructor(props) {
        super(props);

        this.getInnholdInfo = this.getInnholdInfo.bind(this);
    }

    getInnholdInfo(melding) {
        const resources = this.props.store.getResources();

        if (melding.statusKode === 'OK' && melding.utsendingsTidspunkt) {
            const prefix = resources.getOrElse(`varsel.tilbakemelding.${melding.kanal}`, melding.kanal);
            return <p className="innhold-informasjon ok">{prefix}{this.getMottakerInfo(melding)}</p>;
        }
        return undefined;
    }

    getMottakerInfo(melding) {
        if (melding.mottakerInformasjon === null) {
            return '';
        }
        return melding.mottakerInformasjon;
    }

    render() {
        const melding = this.props.melding;
        const innholdInfo = this.getInnholdInfo(melding);
        const resources = this.props.store.getResources();
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
                {innholdInfo}
            </li>
        );
    }
}

VarselRadElement.propTypes = {
    store: PT.object.isRequired,
    melding: PT.object.isRequired
};

export default VarselRadElement;
