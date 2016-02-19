import React from 'react';
import SaksoversiktStore from './saksoversikt-store';
import AsyncLoader from './../utils/async-loader';
import TemaListeKomponent from './tema-liste-komponent';
import Sakstema from './sakstema/sakstema';

class SaksoversiktLerret extends React.Component {

    constructor(props) {
        super(props);
        this.store = new SaksoversiktStore(this.props.fnr);
        this.state = this.store.getState();
        this.updateState = this.updateState.bind(this);
        this.velgSak = this.velgSak.bind(this);
    }

    componentDidMount() {
        this.store.addListener(this.updateState);
    }

    componentWillUnmount() {
        this.store.removeListener(this.updateState);
    }

    updateState() {
        this.setState(this.store.getState());
    }

    velgSak(tema) {
        this.store.velgTema(tema);
    }

    erValgt(tema) {
        return this.state.valgtTema.temakode === tema.temakode;
    }

    render() {
        const temaListe = [];
        const alleTemaer = {temanavn: 'Alle temaer', temakode: 'alle', sistOppdatertDato: ''};
        temaListe.push(<TemaListeKomponent tema={alleTemaer}
                                           valgt={this.erValgt(alleTemaer)}
                                           onClickSakstema={this.velgSak}/>);
        temaListe.push(this.state.sakstema.sort((a, b) => {
            return !a.sistOppdatertDato ? 1 : new Date(b.sistOppdatertDato) - new Date(a.sistOppdatertDato);
        }).map((tema) => {
            return (<TemaListeKomponent tema={tema} valgt={this.erValgt(tema)}
                                        onClickSakstema={this.velgSak}/>);
        }));

        const valgtTema = this.state.valgtTema;
        const temaErValgt = Object.keys(valgtTema).length > 0;
        const sakstemapage = temaErValgt ? <Sakstema dokumentMetadata={valgtTema.dokumentMetadata}/> : <noscript></noscript>;

        return (
            <div className="saksoversikt-lerret">
                <AsyncLoader promises={this.state.promise}>
                    <section className="saksoversikt-liste">
                        {temaListe}
                    </section>
                    <section className="saksoversikt-innhold">
                        {sakstemapage}
                    </section>
                </AsyncLoader>
            </div>
        );
    }
}
SaksoversiktLerret.propTypes = {
    'fnr': React.PropTypes.string.isRequired
};

export default SaksoversiktLerret;
