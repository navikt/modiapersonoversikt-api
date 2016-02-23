import React from 'react';
import { wrapWithProvider } from './../utils/redux-utils';
import { store } from './../store';
import { connect } from 'react-redux';
import { hentLerretData, velgTema } from './../actions';
import * as Const from './../konstanter';

import AsyncLoader from './../../utils/async-loader';
import SakstemaPage from './sakstema/SakstemaPage';
import ViktigAVitePage from './viktigavite/ViktigAVitePage';
import DokumentVisningPage from './dokumentvisning/DokumentVisningPage';

const contextRoutes = {
    'sakstema': (props) => <SakstemaPage {...props} />,
    'viktigavite': (props) => <ViktigAVitePage {...props} />,
    'dokumentvisning': (props) => <DokumentVisningPage {...props} />
};

function getContent(valgtside, props) {
    const fn = contextRoutes[valgtside] || contextRoutes['sakstema'];
    return fn(props);
}

function getUrlParameter(wicketurl, urlparameter) {
    try {
        return wicketurl.split(urlparameter + '=')[1].split('&')[0];
    } catch (error) {
        return 'undefined';
    }
}

class SaksoversiktLerret extends React.Component {
    componentWillMount() {
        const temakode = getUrlParameter(this.props.wicketurl);
        this.props.hentLerretData(this.props.fnr);
        this.props.velgTema(temakode);
    }

    render() {
        const valgtside = getUrlParameter(this.props.wicketurl, 'valgtside');
        const content = getContent(valgtside, this.props);

        console.log('this.props', this.props);

        if (this.props.status !== Const.LASTET) {
            return <noscript />;
        }

        return (
            <div className="saksoversikt-lerret">
                    {content}
            </div>
        );
    }
}

SaksoversiktLerret.propTypes = {
    'fnr': React.PropTypes.string.isRequired
};

const mapStateToProps = (state) => {
    console.log('state', state);

    return {
        status: state.lerret.status,
        valgtTema: state.lerret.valgtTema,
    };
};

export default wrapWithProvider(connect(mapStateToProps, { velgTema, hentLerretData })(SaksoversiktLerret), store);
