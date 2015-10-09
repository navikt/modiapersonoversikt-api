import React from 'react';
import AsyncLoader from './../utils/async-loader';
import VarselStore from './varsel-store';
import FilterHeader from './filter-header';
import VarselListe from './varsel-liste';
import Infoboks from './infoboks';

class VarselLerret extends React.Component {
    constructor(props) {
        super(props);
        this.store = new VarselStore(this.props.fnr);
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

    render() {
        const resources = this.store.getResources();
        const ingenMeldingerInfotekst = resources.getOrElse('varsling.lerret.feilmelding.ingenvarsler', 'Det finnes ingen varsler for brukeren');
        const visMeldingsListe = this.state.varsler.length !== 0 ? null : {display: 'none'};
        const visIngenMeldingerInfoboks = this.state.varsler.length !== 0 ? {display: 'none'} : null;

        return (
            <div className="varsel-lerret">
                <AsyncLoader promises={this.state.promise} snurrepipp={{farge: 'hvit'}}>
                    <Infoboks tekst={ingenMeldingerInfotekst} style={visIngenMeldingerInfoboks}/>
                    <FilterHeader filterSetup={this.state.filtersetup} style={visMeldingsListe}/>
                    <VarselListe varsler={this.state.varsler} store={this.store} style={visMeldingsListe}/>
                </AsyncLoader>
            </div>
        );
    }
}

export default VarselLerret;
