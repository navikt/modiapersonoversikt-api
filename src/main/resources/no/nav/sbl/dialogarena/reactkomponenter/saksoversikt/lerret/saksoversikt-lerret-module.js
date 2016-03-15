import React from 'react';
import { wrapWithProvider } from './../utils/redux-utils';
import { store } from './../store';
import { connect } from 'react-redux';
import { hentLerretDataInit, hentLerretDataSakstema, velgSak, visSide, velgJournalpost } from './../actions';
import * as Const from './../konstanter';

import SakstemaPage from './sakstema/SakstemaPage';
import ViktigAVitePage from './viktigavite/ViktigAVitePage';
import DokumentVisningPage from './dokumentvisning/DokumentVisningPage';
import Snurrepipp from './../../utils/snurrepipp';
import { IntlProvider, addLocaleData, FormattedMessage } from 'react-intl';
import MiljovariablerProvider from './../miljovariabler-provider';
import nbLocale from 'react-intl/dist/locale-data/nb';
addLocaleData(nbLocale);

const contextRoutes = {
    'sakstema': (props) => <SakstemaPage {...props} />,
    'viktigavite': (props) => <ViktigAVitePage {...props} />,
    'dokumentvisning': (props) => <DokumentVisningPage {...props} />
};

function getContent(props) {
    const { valgtside, ...componentProps } = props;
    const fn = contextRoutes[valgtside] || contextRoutes['sakstema'];
    return fn(componentProps);
}

function lagFeilmelding(props) {
    return props.feilendeSystemer && props.feilendeSystemer.length > 0
    && props.valgtside === 'sakstema'?
        (<div className="lamell-feilmelding">
            <FormattedMessage id="sakslamell.feilmelding" />
        </div>): <noscript />;
}

class SaksoversiktLerret extends React.Component {
    componentWillMount() {
        this.props.hentLerretDataInit();
        this.props.hentLerretDataSakstema(this.props.fnr);
    }

    render() {
        if (this.props.status !== Const.LASTET) {
            return <Snurrepipp />;
        }

        const feilmelding = lagFeilmelding(this.props);

        return (
            <MiljovariablerProvider miljovariabler={this.props.miljovariabler}>
                <IntlProvider defaultLocale="nb" locale="nb" messages={this.props.tekster}>
                    <div className="saksoversikt-lerret-container">
                        { feilmelding }
                        { getContent(this.props) }
                    </div>
                </IntlProvider>
            </MiljovariablerProvider>
        );
    }
}

SaksoversiktLerret.propTypes = {
    'fnr': React.PropTypes.string.isRequired,
    'brukerNavn': React.PropTypes.string.isRequired,
    'hentLerretDataInit': React.PropTypes.func,
    'hentLerretDataSakstema': React.PropTypes.func,
    'velgSak': React.PropTypes.func,
    'status': React.PropTypes.string,
    'tekster': React.PropTypes.object
};

const mapStateToProps = (state) => {
    return {
        valgtside: state.lerret.valgtside,
        sakstema: state.lerret.data.sakstema,
        feilendeSystemer: state.lerret.data.feilendeSystemer,
        status: state.lerret.status,
        valgtTema: state.lerret.valgtTema,
        tekster: state.lerret.data.tekster,
        miljovariabler: state.lerret.data.miljovariabler
    };
};

export default wrapWithProvider(connect(mapStateToProps, {
    velgSak,
    visSide,
    velgJournalpost,
    hentLerretDataInit,
    hentLerretDataSakstema
})(SaksoversiktLerret), store);
