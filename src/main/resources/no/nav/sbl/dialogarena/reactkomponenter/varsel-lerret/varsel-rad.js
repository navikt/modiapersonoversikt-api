import React from 'react';
import VarselRadElement from './varsel-rad-element';
import { prettyDate} from './dato-formatering';

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
        let pilClassname = 'ekspanderingspil';
        let meldinger = null;

        if (varsel.ekspandert) {
            headerClassname += ' ekspandert';
            pilClassname += ' opp';
            meldinger = varsel.meldingListe.map((melding) => <VarselRadElement melding={melding}/>);
        } else {
            pilClassname += ' ned';
        }


        return (
            <li className="varsel-rad">
                <button className={headerClassname} onClick={this.toggleEkspandert} aria-expanded={varsel.ekspandert} >
                    <span className="header-dato">{prettyDate(varsel.mottattTidspunkt)}</span>
                    <span className="vekk"> | </span>
                    <span className="header-type">{varsel.varselType}</span>
                    <span className="vekk"> | </span>
                    <span className="header-kanal">{sendIKanal}</span>
                    <i className={pilClassname} aria-hidden="true" />
                </button>
                <ul className="reset-ul-styling">
                    {meldinger}
                </ul>
            </li>
        );
    }
}

export default VarselRad;