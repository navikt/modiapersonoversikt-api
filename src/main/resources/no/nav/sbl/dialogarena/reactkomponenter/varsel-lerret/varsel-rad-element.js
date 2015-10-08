import React from 'react';

class VarselRadElement extends React.Component {
    constructor(props) {
        super(props);

        this.getInnholdInfo = this.getInnholdInfo.bind(this);
    }

    getInnholdInfo(melding) {
        const resources = this.props.store.getResources();

        if (melding.statusKode === 'OK' && melding.utsendingsTidspunkt) {
            const prefix = resources.getOrElse('varsel.tilbakemelding.' + melding.kanal, melding.kanal);
            return <p className="innhold-informasjon ok">{prefix + this.getMottakerInfo(melding)}</p>;
        }
    }

    getMottakerInfo(melding) {
        if (melding.mottakerInformasjon == null) {
            return '';
        }
        return melding.mottakerInformasjon;
    }

    render() {
        const melding = this.props.melding;
        const innholdInfo = this.getInnholdInfo(melding);
        const resources = this.props.store.getResources();
        const epostEmne = melding.kanal === 'EPOST' && melding.epostemne ?
            <div><span className="innhold-epostemne">{melding.epostemne}</span><br/></div> :
            undefined;

        return (
            <li className="varsel-rad-element">
                <div className="varsel-innhold-container">
                    <span className="innhold-kanal">{resources.getOrElse('varsel.kanal.' + melding.kanal, melding.kanal) + ': '}</span>
                    <div className="innhold-melding">
                        {epostEmne}
                        <span>{melding.innhold}</span>
                    </div>
                </div>
                {innholdInfo}
            </li>
        );
    }
}

export default VarselRadElement;
