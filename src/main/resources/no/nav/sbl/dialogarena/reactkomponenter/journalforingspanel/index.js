import React from 'react';
import TypeValg from './typevalg';
import SakerListe from './sakerliste';
import LukkKnapp from './lukkknapp';
import { partition } from 'lodash';

class JournalforingsPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            valgtKategori: 'FAG'
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
            <div className="journalforings-panel">
                <h2 className="header">Journalføring</h2>
                <TypeValg valgtKategori={this.state.valgtKategori} endreKategori={this.endreKategori}/>

                <SakerListe saker={saker}></SakerListe>
                <LukkKnapp></LukkKnapp>
            </div>
        );
    }
}

export default JournalforingsPanel;

