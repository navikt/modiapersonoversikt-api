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
            return <p className="innhold-informasjon ok">{prefix + melding.mottakerInformasjon}</p>;
        }
    }

    render() {
        const melding = this.props.melding;
        const innholdInfo = this.getInnholdInfo(melding);
        const resources = this.props.store.getResources();

        return (
            <li className="varsel-rad-element">
                <p>
                    <span
                        className="innhold-kanal">{resources.getOrElse('varsel.kanal.' + melding.kanal, melding.kanal) + ': '}</span>
                    <span className="innhold-melding">{melding.innhold}</span>
                </p>
                {innholdInfo}
            </li>
        );
    }
}

export default VarselRadElement;