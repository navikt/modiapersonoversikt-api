//import React from 'react';
//import TypeValg from './typevalg';
//import SakerListe from './sakerliste';
//import { partition } from 'lodash';

var React = require('react');
var TypeValg= require('./typevalg');
var SakerListe = require('./sakerliste');
var partition = require('lodash');

class VelgSak extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            valgtKategori: 'FAG'
        };
        this.endreKategori = this.endreKategori.bind(this);
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.temagruppe === 'OVRG') {
            this.setState({valgtKategori: 'GEN'})
        }
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

//export default VelgSak;
module.exports= VelgSak;

