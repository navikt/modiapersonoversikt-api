import React from 'react';
import TypeValg from './typevalg';
import SakerListe from './sakerliste';
import { partition } from 'lodash';

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
        const kategorier = partition(this.props.saker, sak => sak.sakstype === 'GEN');
        const generelle = kategorier[0];
        const fagsaker = kategorier[1];

        const saker = this.state.valgtKategori === 'FAG' ? fagsaker : generelle;

        return (
            <div>
                <TypeValg valgtKategori={this.state.valgtKategori} endreKategori={this.endreKategori}/>
                <SakerListe
                    saker={saker}
                    temagruppe={this.props.temagruppe}
                    velgSak={this.props.velgSak}
                    temagruppeTemaMapping={this.props.temagruppeTemaMapping}/>
            </div>
        );
    }
}

export default VelgSak;