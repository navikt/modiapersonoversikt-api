import React from 'react';
import PT from 'prop-types';

import { API_BASE_URL } from '../../constants';
import AsyncLoader from './async-loader';
import Ajax from '../../utils/ajax';
import { EkspanderbartpanelBase } from 'nav-frontend-ekspanderbartpanel';
import NavKontorHeader from './components/header';
import DetaljertEnhetsinformasjon from './components/detaljert-enhetsinformasjon';

function NavKontor({ organisasjon, baseUrlAppAdeo }) {
    return (
        <div className="nav-kontor-panel">
            <div className="nav-ikon" />
            <EkspanderbartpanelBase
                ariaTittel={"Brukers NAV-kontor"}
                heading={<NavKontorHeader organisasjon={organisasjon} />}
            >
                <DetaljertEnhetsinformasjon organisasjon={organisasjon} baseUrlAppAdeo={baseUrlAppAdeo} />
            </EkspanderbartpanelBase>
        </div>
    );
}
NavKontor.propTypes = {
    organisasjon: PT.object,
    baseUrlAppAdeo: PT.string.isRequired
};

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
                <NavKontor baseUrlAppAdeo={this.props.baseUrlAppAdeo} />
            </AsyncLoader>
        );
    }
}
BrukersNavKontor.propTypes = {
    organisasjonsenhetId: PT.string.isRequired,
    baseUrlAppAdeo: PT.string.isRequired
};

export default BrukersNavKontor;
