import React from 'react';
import TypeValg from './typevalg';
import SakerListe from './saker-liste';
import AdvarselBoks from './../utils/advarsel-boks';
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
        const feileteAjaxkall = Object.keys(this.props.saker).reduce((acc, key) => {
            if (this.props.saker[key] === null) {
                acc.push(key);
            }
            return acc;
        }, []);

        const advarsler = feileteAjaxkall.map((feiletKall) => <AdvarselBoks
            tekst={'Feil ved uthenting av saker fra ' + feiletKall.toUpperCase()}/>);

        const mergedSaker = Object.keys(this.props.saker).reduce((acc, key) => {
            acc = acc.concat(this.props.saker[key] || []);
            return acc;
        }, []);

        const kategorier = partition(mergedSaker, sak => sak.sakstype === 'GEN');
        const generelle = kategorier[0];
        const fagsaker = kategorier[1];
        const saker = this.state.valgtKategori === 'FAG' ? fagsaker : generelle;


        return (
            <div>
                <TypeValg valgtKategori={this.state.valgtKategori} endreKategori={this.endreKategori}/>
                {advarsler}
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