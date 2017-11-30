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
        <EkspanderbartpanelBase
            className="nav-kontor-info"
            ariaTittel={"Brukers NAV-kontor"}
            heading={<NavKontorHeader organisasjon={organisasjon} />}
        >
            <DetaljertEnhetsinformasjon organisasjon={organisasjon} baseUrlAppAdeo={baseUrlAppAdeo} />
        </EkspanderbartpanelBase>
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
            const url = `${API_BASE_URL}/enheter/${this.props.organisasjonsenhetId}`;
            this.promise = Ajax.get(url);
        }
    }
    render() {
        if (!this.props.organisasjonsenhetId) {
            return null;
        }
        return (
            <div className="nav-kontor-panel">
                <div className="nav-ikon" />
                <AsyncLoader promises={this.promise} toProp={"organisasjon"} >
                    <NavKontor baseUrlAppAdeo={this.props.baseUrlAppAdeo} />
                </AsyncLoader>
            </div>
        );
    }
}
BrukersNavKontor.propTypes = {
    organisasjonsenhetId: PT.string.isRequired,
    baseUrlAppAdeo: PT.string.isRequired
};

export default BrukersNavKontor;
