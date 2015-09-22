import React from 'react';
import { prettyDate} from './dato-formatering'

class VarselRad extends React.Component {
    constructor(props) {
        super(props);

        this.toggleEkspandert = this.toggleEkspandert.bind(this);

    }

    toggleEkspandert() {
        this.props.store.toggleEkspandert(this.props.varsel.idx);
    }

    render() {
        const varsel = this.props.varsel;

        const sendIKanal = varsel.meldingListe.map((m) => m.kanal).join(", ");
        let headerClassname = 'varsel-rad-header';
        let meldinger = null;

        if (varsel.ekspandert) {
            headerClassname += ' ekspandert';
            meldinger = varsel.meldingListe.map((melding) => {
                return (
                    <div className="varsel-rad-element">
                        <span className="innhold-kanal">{melding.kanal + ':'}</span>
                        <span className="innhold-melding">{melding.innhold}</span>

                        <p className="innhold-informasjon">{melding.mottakerInformasjon}</p>
                    </div>
                );
            });
        }



        return (
            <div className="varsel-rad">
                <button className={headerClassname} onClick={this.toggleEkspandert}>
                    <span className="header-dato">{prettyDate(varsel.mottattTidspunkt)}</span>
                    <span className="header-type">{varsel.varselType}</span>
                    <span className="header-kanal">{sendIKanal}</span>
                </button>
                {meldinger}
            </div>
        );
    }
}

export default VarselRad;