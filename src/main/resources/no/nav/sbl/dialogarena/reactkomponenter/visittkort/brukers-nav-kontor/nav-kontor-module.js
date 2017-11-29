import React from 'react';
import PT from 'prop-types';

import { API_BASE_URL } from '../../constants';
import AsyncLoader from './async-loader';
import Ajax from '../../utils/ajax';
import { EkspanderbartpanelBase } from 'nav-frontend-ekspanderbartpanel';
import { organisasjonType } from './types';
import NavKontorHeader from './components/header';
import DetaljertEnhetsInformasjon from './components/detaljert-enhets-informasjon';

function NavKontor({ organisasjon }) {
    return (
        <div className="nav-kontor-panel">
            <div className="nav-ikon"></div>
            <EkspanderbartpanelBase
                ariaTittel={"Brukers NAV-kontor"}
                heading={<NavKontorHeader organisasjon={organisasjon} />}
            >
                <DetaljertEnhetsInformasjon organisasjon={organisasjon} />
            </EkspanderbartpanelBase>
        </div>
    );
}
NavKontor.propTypes = organisasjonType;

class BrukersNavKontor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            organisasjon: undefined
        };
        if (this.props.organisasjonsenhetId) {
            const url = `${API_BASE_URL}/organisasjoner/${this.props.organisasjonsenhetId}`;
            this.promise = Ajax.get(url);
        }
    }
    render() {
        if (!this.props.organisasjonsenhetId) {
            return null;
        }
        return (
            <AsyncLoader promises={this.promise} toProp={"organisasjon"} >
                <NavKontor />
            </AsyncLoader>
        );
    }
}
BrukersNavKontor.propTypes = {
    organisasjonsenhetId: PT.string.isRequired
};

export default BrukersNavKontor;
