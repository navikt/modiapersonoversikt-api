import React from 'react';
import { prettyDate} from './dato-formatering'

class VarselRad extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const varsel = this.props.varsel;

        const sendIKanal = varsel.meldingListe.map((m) => m.kanal).join(", ");

        const meldinger = varsel.meldingListe.map((melding) => {
            return (
                <div className="varsel-rad-element">
                    <span className="innhold-kanal">{melding.kanal + ':'}</span>
                    <span className="innhold-melding">{melding.innhold}</span>

                    <p className="innhold-informasjon">{melding.mottakerInformasjon}</p>
                </div>
            );
        });

        return (
            <div className="varsel-rad">
                <div className="varsel-rad-header">
                    <span className="header-dato">{prettyDate(varsel.mottattTidspunkt)}</span>
                    <span className="header-type">{varsel.varselType}</span>
                    <span className="header-kanal">{sendIKanal}</span>
                </div>
                {meldinger}
            </div>
        );
    }
}

export default VarselRad;