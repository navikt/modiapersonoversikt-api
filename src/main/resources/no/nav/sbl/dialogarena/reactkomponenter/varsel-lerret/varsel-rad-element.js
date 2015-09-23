import React from 'react';

class VarselRadElement extends React.Component {
    constructor(props) {
        super(props);

        this.getInnholdInfo = this.getInnholdInfo.bind(this);
    }

    getInnholdInfo(melding) {
        console.log('melding', melding);
        if (melding.statusKode === 'OK') {
            if (melding.mottakerInformasjon) {
                const prefix = melding.kanal === 'SMS' ? 'Tlf.: ' : 'Epost: ';
                return <p className="innhold-informasjon ok">{prefix + melding.mottakerInformasjon}</p>;
            } else {
                return <p className="innhold-informasjon error">Mangler kontaktinformasjon</p>;
            }
        } else if (melding.statusKode === '500') {
            return <p className="innhold-informasjon error">Det skjedde en intern feil</p>;
        } else {
            return <p className="innhold-informasjon error">Det skjedde en feil ved utsending av varslet</p>;
        }
    }

    render() {
        const melding = this.props.melding;
        const innholdInfo = this.getInnholdInfo(melding);

        return (
            <li className="varsel-rad-element">
                <span className="innhold-kanal">{melding.kanal + ': '}</span>
                <span className="innhold-melding">{melding.innhold}</span>
                {innholdInfo}
            </li>
        );
    }
}

export default VarselRadElement;