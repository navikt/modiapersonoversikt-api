import React from 'react';
import PT from 'prop-types';

import { API_BASE_URL } from '../../constants';
import AsyncLoader from './async-loader';
import Ajax from '../../utils/ajax';
import { EkspanderbartpanelBase } from 'nav-frontend-ekspanderbartpanel';
import NavKontorHeader from './components/header';
import DetaljertEnhetsinformasjon from './components/detaljert-enhetsinformasjon';
import IngenEnhet from './components/ingen-enhet';

function NavKontor({ organisasjon, norg2FrontendBaseUrl }) {
    return (
        <EkspanderbartpanelBase
            className="nav-kontor-info"
            ariaTittel={"Brukers NAV-kontor"}
            heading={<NavKontorHeader organisasjon={organisasjon} />}
        >
            <DetaljertEnhetsinformasjon organisasjon={organisasjon} norg2FrontendBaseUrl={norg2FrontendBaseUrl} />
        </EkspanderbartpanelBase>
    );
}
NavKontor.propTypes = {
    organisasjon: PT.object,
    norg2FrontendBaseUrl: PT.string.isRequired
};

class BrukersNavKontor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            organisasjon: undefined
        };
        if (this.props.organisasjonsenhetId) {
            const url = `${API_BASE_URL}/enheter/${this.props.organisasjonsenhetId}`;
            this.promise = Ajax.get(url);
        }
    }
    render() {
        if (!this.props.organisasjonsenhetId) {
            return (
                <IngenEnhet/>
            );
        }
        return (
            <div className="nav-kontor-panel">
                <div className="nav-ikon" />
                <div className="content">
                    <AsyncLoader promises={this.promise} toProp={"organisasjon"} >
                        <NavKontor norg2FrontendBaseUrl={this.props.norg2FrontendBaseUrl} />
                    </AsyncLoader>
                </div>
            </div>
        );
    }
}
BrukersNavKontor.propTypes = {
    organisasjonsenhetId: PT.string.isRequired,
    norg2FrontendBaseUrl: PT.string.isRequired
};

export default BrukersNavKontor;
