import React from 'react';

class VarselRadElement extends React.Component {
    constructor(props) {
        super(props);

        this.getInnholdInfo = this.getInnholdInfo.bind(this);
    }

    getInnholdInfo(melding) {
        if (melding.statusKode === 'OK' && melding.kanal === 'NAV.NO') {
            if (melding.utsendingsTidspunkt) {
                return <p className="innhold-informasjon ok">Sendt til Ditt NAV</p>;
            } else {
                return <p className="innhold-informasjon error">Har ikke mottatt kvittering</p>;
            }
        } else if (melding.statusKode === 'OK' && melding.kanal !== 'NAV.NO') {
            if (melding.utsendingsTidspunkt) {
                const prefix = melding.kanal === 'SMS' ? 'Tlf.: ' : 'Epost: ';
                return <p className="innhold-informasjon ok">{prefix + melding.mottakerInformasjon}</p>;
            } else {
                return <p className="innhold-informasjon error">Har ikke mottatt kvittering</p>;
            }
        } else if (melding.statusKode === '314') {
            return <p className="innhold-informasjon error">Mangler kontaktinformasjon</p>;
        } else if (melding.statusKode === '500') {
            return <p className="innhold-informasjon error">Feil under generering av varsel</p>;
        } else {
            return <p className="innhold-informasjon error">Det skjedde en feil ved utsending av varslet</p>;
        }
    }

    render() {
        const melding = this.props.melding;
        const innholdInfo = this.getInnholdInfo(melding);

        return (
            <li className="varsel-rad-element">
                <p>
                    <span className="innhold-kanal">{melding.kanal + ': '}</span>
                    <span className="innhold-melding">{melding.innhold}</span>
                </p>
                {innholdInfo}
            </li>
        );
    }
}

export default VarselRadElement;