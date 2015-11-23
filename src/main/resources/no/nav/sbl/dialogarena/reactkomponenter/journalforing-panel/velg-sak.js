import React from 'react';
import TypeValg from './typevalg';
import SakerListe from './saker-liste';
import AdvarselBoks from './../utils/advarsel-boks';
import { partition, isUndefined } from 'lodash';
import { kvpair } from './../utils/utils-module';

class VelgSak extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            valgtKategori: this.props.temagruppe === 'OVRG' ? 'GEN' : 'FAG'
        };
        this.endreKategori = this.endreKategori.bind(this);
    }

    endreKategori(kategori) {
        this.setState({valgtKategori: kategori});
    }

    render() {
        const saker = this.props.saker || {};

        const advarsler = kvpair(saker)
            .filter(([_, value]) => isUndefined(value))
            .map(([feiletKall, _]) => <AdvarselBoks tekst={'Feil ved uthenting av saker fra ' + feiletKall.toUpperCase()}/>);

        const mergedSaker = kvpair(saker)
            .reduce((acc, [_, value]) => {
                return acc.concat(value || []);
            }, []);

        const kategorier = partition(mergedSaker, sak => sak.sakstype === 'GEN');
        const generelle = kategorier[0];
        const fagsaker = kategorier[1];
        const sakerSomVises = this.state.valgtKategori === 'FAG' ? fagsaker : generelle;

        return (
            <div>
                <TypeValg valgtKategori={this.state.valgtKategori} endreKategori={this.endreKategori}/>
                {advarsler}
                <SakerListe
                    saker={sakerSomVises}
                    temagruppe={this.props.temagruppe}
                    velgSak={this.props.velgSak}
                    temagruppeTemaMapping={this.props.temagruppeTemaMapping}/>
            </div>
        );
    }
}

VelgSak.propTypes = {
    'temagruppe': React.PropTypes.string.isRequired,
    'temagruppeTemaMapping': React.PropTypes.object.isRequired,
    'valgtKategori': React.PropTypes.string.isRequired,
    'saker': React.PropTypes.array.isRequired,
    'velgSak': React.PropTypes.func.isRequired
};

export default VelgSak;
