import React from 'react';
import { connect } from 'react-redux';
import Sakstema from './tema';
import { hentWidgetData } from './../actions';
import { take } from 'lodash';
import WicketSender from './../../react-wicket-mixin/wicket-sender';

const ANTALL_TEMAER = 6;

class Temaliste extends React.Component {
    componentWillMount() {
        this.props.hentWidgetData(this.props.fnr);
        this.sendToWidget = WicketSender.bind(this, this.props.wicketurl, this.props.wicketcomponent);
    }
    render() {
        const { temaer, fnr } = this.props;
        const redusertAntallTemaer = take(temaer, ANTALL_TEMAER);
        const temaliste = redusertAntallTemaer.map((tema) =>
            <li><Sakstema tema={tema} fnr={fnr} sendToWicket={this.sendToWidget}/></li>
        );
        const linkSaksoversikt = `/modiabrukerdialog/person/${fnr}#!saksoversikt`;

        return (
            <ul>
                {temaliste}
                <li><a href="#" onClick={() => this.sendToWidget('VIS_ALLE_CLICK')} tabIndex="-1" >Se flere saker</a></li>
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

