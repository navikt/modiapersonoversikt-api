import React from 'react';
import { connect } from 'react-redux';
import Sakstema from './tema';
import { hentWidgetData } from './../actions';
import { take } from 'lodash';

const ANTALL_TEMAER = 6;

class Temaliste extends React.Component {
    componentWillMount() {
        this.props.hentWidgetData(this.props.fnr);
    }

    render() {
        const { temaer, fnr } = this.props;
        const redusertAntallTemaer = take(temaer, ANTALL_TEMAER);
        const temaliste = redusertAntallTemaer.map(tema => <li key={tema.temakode}><Sakstema tema={tema} fnr={fnr}/></li>);
        const linkSaksoversikt = `/modiabrukerdialog/person/${fnr}#!saksoversikt`;
        return (
            <ul>
                {temaliste}
                <li><a href={linkSaksoversikt}>Se flere saker</a></li>
            </ul>
        );
    }
}


Temaliste.PropTypes = {
    temaer: React.PropTypes.array,
    fnr: React.PropTypes.string
};

const mapStateToProps = (state) => {
    return {
        temaer: state.widget.data
    };
};

export default connect(mapStateToProps, { hentWidgetData })(Temaliste);

