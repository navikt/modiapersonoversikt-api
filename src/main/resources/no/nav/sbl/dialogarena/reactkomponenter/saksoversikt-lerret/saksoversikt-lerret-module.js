import React from 'react';
import SaksoversiktStore from './saksoversikt-store';
import AsyncLoader from './../utils/async-loader';
import TemaListeKomponent from './tema-liste-komponent'

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

    render() {
        console.log('render');
        const temaListe = Object.keys(this.state.behandlingerByTema).map((tema)=> {
            return <TemaListeKomponent tema={tema}/>;
        });

        return (
            <div className="saksoversikt-lerret">
                <AsyncLoader promises={this.state.promise}>
                    <div className="saksoversikt-liste">
                        {temaListe}
                    </div>
                </AsyncLoader>
            </div>
        );
    }
}
SaksoversiktLerret.propTypes = {
    'fnr': React.PropTypes.string.isRequired
};

export default SaksoversiktLerret;
