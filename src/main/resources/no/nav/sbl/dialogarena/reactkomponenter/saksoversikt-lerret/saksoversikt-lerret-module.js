import React from 'react';
import SaksoversiktStore from './saksoversikt-store';
import AsyncLoader from './../utils/async-loader';
import SakstemaListe from './SakstemaListe';
import formats from './utils/formater/formats';

class SaksoversiktLerret extends React.Component {

    constructor(props) {
        super(props);


        this.store = new SaksoversiktStore(this.props.fnr);
        this.state = this.store.getState();
        this.updateState = this.updateState.bind(this);
        this.velgSak = this.velgSak.bind(this);
        this.wicketUrlBehandler(props.wicketurl);
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

    wicketUrlBehandler(wicketurl) {
        const temakode = wicketurl.split('temakode=')[1];
        this.store.velgTema(temakode);
    }

    velgSak(tema) {
        this.store.velgTema(tema);
    }

    erValgt(tema) {
        return this.state.valgtTema === tema;
    }

    render() {
        const sakstema = this.state.sakstema;
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
                        <SakstemaListe sakstema={sakstema} erValgt={this.erValgt.bind(this)}
                                       velgSak={this.velgSak} store={this.store}/>
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
