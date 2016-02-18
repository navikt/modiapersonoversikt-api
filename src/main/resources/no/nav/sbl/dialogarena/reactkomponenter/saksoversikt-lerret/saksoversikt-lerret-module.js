import React from 'react';
import SaksoversiktStore from './saksoversikt-store';
import AsyncLoader from './../utils/async-loader';
import TemaListeKomponent from './tema-liste-komponent';

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
        return this.state.valgtTema === tema;
    }

    render() {

        const temaListe = [];
        temaListe.push(<TemaListeKomponent tema="Alle temaer" temakode={"alle"} dato="" valgt={this.erValgt("alle")}
                                           onClickSakstema={this.velgSak}/>);
        temaListe.push(this.state.sakstema.sort(function (a, b) {
            return !a.sistOppdatertDato? 1 :  new Date(b.sistOppdatertDato) - new Date(a.sistOppdatertDato);
        }).map((tema) => {
            return <TemaListeKomponent tema={tema.temanavn} temakode={tema.temakode}
                                       dato={tema.sistOppdatertDato} valgt={this.erValgt(tema.temakode)}
                                       onClickSakstema={this.velgSak}/>
        }));

        return (
            <div className="saksoversikt-lerret">
                <AsyncLoader promises={this.state.promise}>
                    <section className="saksoversikt-liste">
                        {temaListe}
                    </section>
                    <section className="saksoversikt-innhold">
                        <h2>Innhold</h2>
                        {this.state.valgtTema}
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
