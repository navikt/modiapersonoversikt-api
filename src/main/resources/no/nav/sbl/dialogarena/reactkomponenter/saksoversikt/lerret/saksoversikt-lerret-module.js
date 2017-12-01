import React from 'react';
import PT from 'prop-types';
import { wrapWithProvider } from './../utils/redux-utils';
import { store } from './../store';
import { connect } from 'react-redux';
import { hentLerretData, velgSak, purgeScrollId,
    visSide, velgJournalpost, velgFiltreringAvsender } from './../actions';
import * as Const from './../konstanter';

import SakstemaPage from './sakstema/sakstema-page';
import ViktigAVitePage from './viktigavite/viktig-aa-vite-page';
import DokumentVisningPage from './dokumentvisning/dokument-visning-page';
import Snurrepipp from './../../utils/snurrepipp';
import { IntlProvider, addLocaleData, FormattedMessage } from 'react-intl';
import MiljovariablerProvider from './../miljovariabler-provider';
import nbLocale from 'react-intl/locale-data/nb';

addLocaleData(nbLocale);

const contextRoutes = {
    sakstema: (props) => <SakstemaPage {...props} />,
    viktigavite: (props) => <ViktigAVitePage {...props} />,
    dokumentvisning: (props) => <DokumentVisningPage {...props} />
};

function getContent(props) {
    const { valgtside, ...componentProps } = props;
    const fn = contextRoutes[valgtside] || contextRoutes.sakstema;
    return fn(componentProps);
}

function skalViseFeilmeldingOmBaksystem(props) {
    return props.feilendeSystemer && props.feilendeSystemer.length > 0
        && props.valgtside === 'sakstema';
}

function harInternfeil(props) {
    return props.status === Const.FEILET;
}

function lagFeilmelding(props) {
    let enonicFeilmeldingKey = '';

    if (harInternfeil(props)) {
        enonicFeilmeldingKey = 'sakslamell.tekniskfeilmelding';
    } else if (skalViseFeilmeldingOmBaksystem(props)) {
        enonicFeilmeldingKey = 'sakslamell.feilmelding';
    }

    if (enonicFeilmeldingKey !== '') {
        return (
            <div className="lamell-feilmelding" role="alert" aria-atomic="true">
                <FormattedMessage id={`${enonicFeilmeldingKey}`} />
            </div>);
    }

    return <noscript />;
}

export class SaksoversiktLerret extends React.Component {
    componentWillMount() {
        this.props.hentLerretData(this.props.fnr);
    }

    render() {
        if (this.props.status === Const.VOID || this.props.status === Const.LASTER) {
            return (
                <div className="saksoversikt-snurrepipp">
                    <Snurrepipp farge="hvit" />
                </div>
            );
        }

        const feilmelding = lagFeilmelding(this.props);
        const feilmeldingKlasse = skalViseFeilmeldingOmBaksystem(this.props) ? 'har-feilmelding' : '';

        return (
            <MiljovariablerProvider miljovariabler={this.props.miljovariabler}>
                <IntlProvider defaultLocale="nb" locale="nb" messages={this.props.tekster}>
                    <div className={`saksoversikt-lerret-container ${feilmeldingKlasse}`}>
                        { feilmelding }
                        { getContent(this.props) }
                    </div>
                </IntlProvider>
            </MiljovariablerProvider>
        );
    }
}

SaksoversiktLerret.propTypes = {
    fnr: PT.string.isRequired,
    gt: PT.string.isRequired,
    diskresjonskode: PT.string,
    norgUrl: PT.string.isRequired,
    brukerNavn: PT.string.isRequired,
    hentLerretData: PT.func,
    velgSak: PT.func,
    status: PT.string,
    tekster: PT.object,
    miljovariabler: PT.object
};

const mapStateToProps = (state) => (
    {
        valgtside: state.lerret.valgtside,
        sakstema: state.lerret.data.sakstema,
        feilendeSystemer: state.lerret.data.feilendeSystemer,
        status: state.lerret.status,
        valgtTema: state.lerret.valgtTema,
        tekster: state.lerret.data.tekster,
        miljovariabler: state.lerret.data.miljovariabler,
        filtreringsvalg: state.lerret.filtreringsvalg,
        scrollToDokumentId: state.lerret.scrollToDokumentId
    }
);

export default wrapWithProvider(connect(mapStateToProps, {
    velgSak,
    visSide,
    purgeScrollId,
    velgJournalpost,
    velgFiltreringAvsender,
    hentLerretData
})(SaksoversiktLerret), store);
