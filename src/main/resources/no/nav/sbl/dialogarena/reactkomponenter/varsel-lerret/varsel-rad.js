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
        const resources = this.props.store.getResources();
        const datoformat = resources.getOrElse('varsel.ledetekst.rad.datoformat', 'DD. MMM, HH.mm');
        const sendIKanal = varsel.meldingListe
            .map((m) => resources.getOrElse('varsel.kanal.' + m.kanal, m.kanal))
            .join(', ');

        let headerClassname = 'varsel-rad-header';
        let pilClassname = 'ekspanderingspil';
        let meldinger = null;

        if (varsel.ekspandert) {
            headerClassname += ' ekspandert';
            pilClassname += ' opp';
            meldinger = varsel.meldingListe.map((melding) => <VarselRadElement melding={melding} store={this.props.store}/>);
        } else {
            pilClassname += ' ned';
        }


        return (
            <li className="varsel-rad">
                <button className={headerClassname} onClick={this.toggleEkspandert} aria-expanded={varsel.ekspandert}>
                    <span className="header-dato">{prettyDate(varsel.mottattTidspunkt, datoformat)}</span>
                    <span className="vekk"> | </span>
                    <span className="header-type">{resources.getOrElse('varsel.varseltype.' + varsel.varselType, 'Ukjent nøkkel: ' + varsel.varselType)}</span>
                    <span className="vekk"> | </span>
                    <span className="header-kanal">
                        {sendIKanal}
                        <i className={pilClassname} aria-hidden="true"/>
                    </span>
                </button>
                <ul className="reset-ul-styling">
                    {meldinger}
                </ul>
            </li>
        );
    }
}

VarselRad.propTypes = {
    'store': React.PropTypes.object.isRequired,
    'toggleEkspandert': React.PropTypes.func.isRequired,
    'varsel': React.PropTypes.object.isRequired
};

export default VarselRad;
