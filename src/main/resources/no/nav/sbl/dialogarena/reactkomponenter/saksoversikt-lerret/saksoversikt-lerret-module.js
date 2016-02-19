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
        console.log("valgte " + tema);
    }

    render() {

        const dato = '20.10.2016';
        const temaListe = [];
        temaListe.push(<TemaListeKomponent tema="Alle temaer" dato={dato} valgt="true" onClickSakstema={this.velgSak}/>);

        temaListe.push(this.state.sakstema.map((tema) => {
            return <TemaListeKomponent tema={tema.temanavn} temakode={tema.temakode} dato={dato} valgt onClickSakstema={this.velgSak}/>
        }));

        const valgtTema = this.state.sakstema.filter(tema => tema.temakode === tema.temakode)[0];
        const sakstemapage = (typeof valgtTema === 'undefined') ? <div></div> : <Sakstema temakode={valgtTema.temakode}
                                                                                 temanavn={valgtTema.temanavn}
                                                                                 dokumentMetadata={valgtTema.dokumentMetadata}/>;

        return (
            <div className="saksoversikt-lerret">
                <AsyncLoader promises={this.state.promise}>
                    <section className="saksoversikt-liste" >
                        {temaListe}
                    </section>
                    <section>
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
