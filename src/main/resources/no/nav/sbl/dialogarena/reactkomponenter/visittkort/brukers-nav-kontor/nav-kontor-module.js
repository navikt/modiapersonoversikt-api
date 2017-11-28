import React from 'react';
import PT from 'prop-types';

import { API_BASE_URL } from '../../constants';
import AsyncLoader from './async-loader';
import Ajax from '../../utils/ajax';
import { EkspanderbartpanelBase } from 'nav-frontend-ekspanderbartpanel';
import { OrganiasjonType } from './types';
import Apningstider from './components/apningstider';
import NavKontorHeader from './components/header';
import Adresse from './components/adresse';


function NavKontor({ organisasjon }) {
    return (
        <div className="nav-kontor-panel">
            <EkspanderbartpanelBase heading={<NavKontorHeader organisasjon={organisasjon} />}>
                <Adresse organisasjon={organisasjon} />
                <Apningstider organisasjon={organisasjon} />
            </EkspanderbartpanelBase>
        </div>
    );
}

NavKontor.propTypes = OrganiasjonType;

class BrukersNavKontor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            organisasjon: undefined
        };
        const url = `${API_BASE_URL}/organisasjoner/${this.props.organisasjonsenhetId}`;
        this.promise = Ajax.get(url);
    }
    render() {
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
