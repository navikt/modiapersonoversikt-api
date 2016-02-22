import React from 'react';
import { connect, Provider } from 'react-redux';
import { store } from './store';
import { hentTemaer } from './actions';
console.log('store', store);

import Temaliste from './temaliste';

class SaksoversiktWidget extends React.Component {
    componentWillMount() {
        hentTemaer(this.props.fnr);
    }

    render() {
        // Fnr kommer fra wicket
        const { fnr, temaer } = this.props;
        const goToSaksoversikt = () => window.location = `/modiabrukerdialog/person/${fnr}#!saksoversikt`; //Burde vært kall til wicket

        return (
            <Provider store={store}>
                <div onClick={goToSaksoversikt}>
                    <header className="klikkbar-header" role="link">
                        <div className="initial">S</div>
                        <h2 className="widget-header">Saksoversikt</h2>
                    </header>
                    <Temaliste temaer={temaer} fnr={fnr}/>
                </div>
            </Provider>
        );
    }
}

SaksoversiktWidget.propTypes = {
    fnr: React.PropTypes.string.isRequired
};

const mapStateToProps = (state) => ({
    fnr: state.fnr,
    temaer: state.temaer
});

export default connect(mapStateToProps, { hentTemaer })(SaksoversiktWidget);
