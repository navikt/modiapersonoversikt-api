import React from 'react';
import AsyncLoader from './../utils/async-loader';
import FilterHeader from './filter-header';
import VarselListe from './varsel-liste';

class VarselLerret extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            filtersetup: {
                fraDato: undefined,
                tilDato: undefined,
                type: undefined
            }
        };
        this.promise = $.get('/modiabrukerdialog/rest/varsler/' + this.props.fnr);
    }

    render() {
        return (
            <div className="varsel-lerret">
                <AsyncLoader promises={this.promise} toProp="varsler">
                    <FilterHeader filterSetup={this.state.filtersetup}/>
                    <VarselListe />
                </AsyncLoader>
            </div>
        );
    }
}

export default VarselLerret;
