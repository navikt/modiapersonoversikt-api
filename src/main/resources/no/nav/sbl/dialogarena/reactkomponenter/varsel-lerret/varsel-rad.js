import React from 'react';
import VarselRadElement from './varsel-rad-element';
import { prettyDate } from './dato-formatering';
import { uniq } from 'lodash';

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
        const datoformat = resources.getOrElse('varsel.ledetekst.rad.datoformat', 'DD. MMM YYYY');

        const brukteKanaler = varsel.meldingListe
            .map((m) => resources.getOrElse(`varsel.kanal.${m.kanal}`, m.kanal))
            .sort();

        const unikeKanaler = uniq(brukteKanaler, false).join(', ');

        let headerClassname = 'varsel-rad-header';
        let pilClassname = 'ekspanderingspil';
        let meldinger = null;

        if (varsel.ekspandert) {
            headerClassname += ' ekspandert';
            pilClassname += ' opp';
            meldinger = varsel.meldingListe
                .sort((e1, e2) => e1.utsendingsTidspunkt < e2.utsendingsTidspunkt)
                .map((melding) => <VarselRadElement melding={melding} store={this.props.store} />);
        } else {
            pilClassname += ' ned';
        }

        const varselType = resources.getOrElse(
            `varsel.varseltype.${varsel.varselType}`,
            `Ukjent nøkkel: ${varsel.varselType}`
        );
        const revarsling = varsel.erRevarsling ? ` - ${resources.getOrElse('varsler.revarsel', 'Revarsling')}` : '';

        return (
            <li className="varsel-rad">
                <button className={headerClassname} onClick={this.toggleEkspandert} aria-expanded={varsel.ekspandert}>
                    <span className="header-dato">{prettyDate(varsel.mottattTidspunkt, datoformat)}</span>
                    <span className="vekk"> | </span>
                    <span className="header-type">
                        {`${varselType} ${revarsling}`}
                    </span>
                    <span className="vekk"> | </span>
                    <span className="header-kanal">
                        {unikeKanaler}
                        <i className={pilClassname} aria-hidden="true" />
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
    store: React.PropTypes.object.isRequired,
    varsel: React.PropTypes.object.isRequired
};

export default VarselRad;
