import React from 'react';
import { connect } from 'react-redux';
import Sakstema from './tema';
import { hentTemaer } from './../actions';
import { take } from 'lodash';

const ANTALL_TEMAER = 6;

class Temaliste extends React.Component {
    componentWillMount() {
        this.props.hentTemaer(this.props.fnr);
    }

    render() {
        const { temaer, fnr } = this.props;
        const redusertAntallTemaer = take(temaer, ANTALL_TEMAER);
        const temaliste = redusertAntallTemaer.map(tema => <li><Sakstema tema={tema} fnr={fnr}/></li>);

        return (
            <ul>
                {temaliste}
                <li><a href="#">Se flere saker</a></li>
            </ul>
        );
    }
}


Temaliste.PropTypes = {
    temaer: React.PropTypes.array,
    fnr: React.PropTypes.string
};

const mapStateToProps = (state) => {
    return ({
        temaer: state.temaer.data
    })
};

export default connect(mapStateToProps, { hentTemaer })(Temaliste);

