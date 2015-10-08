import React from 'react';
import AsyncLoader from './../utils/async-loader';
import VarselStore from './varsel-store';
import FilterHeader from './filter-header';
import VarselListe from './varsel-liste';
import Infoboks from './infoboks'

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

        if (this.state.varsler.length == 0) {
            return (
                <div className="varsel-lerret">
                    <Infoboks tekst={resources.getOrElse('varsling.lerret.feilmelding.ingenvarsler', 'Det finnes ingen varsler for brukeren')}/>
                </div>
            )
        }

        return (
            <div className="varsel-lerret">
                <AsyncLoader promises={this.promise}>
                    <FilterHeader filterSetup={this.state.filtersetup}/>
                    <VarselListe varsler={this.state.varsler} store={this.store}/>
                </AsyncLoader>
            </div>
        );
    }
}

export default VarselLerret;
