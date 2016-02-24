import React from 'react';
import SaksoversiktStore from './saksoversikt-store';
import AsyncLoader from './../utils/async-loader';
import SakstemaPage from './sakstema/SakstemaPage';
import ViktigAVitePage from './viktigavite/ViktigAVitePage';
import DokumentVisningPage from './dokumentvisning/DokumentVisningPage';
import formats from './utils/formater/formats';

class SaksoversiktLerret extends React.Component {

    constructor(props) {
        super(props);
        this.store = new SaksoversiktStore(this.props.fnr, this.props.brukerNavn);
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
        const temakode = getUrlParameter(wicketurl, 'temakode');
        this.store.velgTema(temakode);
    }

    velgSak(tema) {
        this.store.velgTema(tema);
    }

    erValgt(tema) {
        return this.state.valgtTema === tema;
    }

    render() {
        const valgtside = getUrlParameter(this.props.wicketurl, 'valgtside');
        const content = getContent(valgtside, this);

        return (
            <div className="saksoversikt-lerret">
                <AsyncLoader promises={this.state.promise}>
                    {content}
                </AsyncLoader>
            </div>
        );
    }
}

function getUrlParameter(wicketurl, urlparameter) {
    try {
        return wicketurl.split(urlparameter + '=')[1].split('&')[0];
    } catch (error) {
        return 'undefined';
    }
}

function getContent(valgtside, that) {
    if (valgtside === 'sakstema') {
        return <SakstemaPage tekster={that.state.tekster} store={that.store} erValgt={that.erValgt.bind(that)} velgSak={that.velgSak} />;
    } else if (valgtside === 'viktigavite') {
        return <ViktigAVitePage />
    }  else if (valgtside === 'dokumentvisning') {
        return <DokumentVisningPage />
    } else {
        return <SakstemaPage tekster={that.state.tekster} store={that.store} erValgt={that.erValgt.bind(that)} velgSak={that.velgSak} />;
    }
}

SaksoversiktLerret.propTypes = {
    'fnr': React.PropTypes.string.isRequired
};

export default SaksoversiktLerret;
