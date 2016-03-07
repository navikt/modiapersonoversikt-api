import React from 'react';
import { connect } from 'react-redux';
import Sakstema from './tema';
import { hentWidgetData } from './../actions';
import { take } from 'lodash';
import WicketSender from './../../react-wicket-mixin/wicket-sender';
import nbLocale from 'react-intl/dist/locale-data/nb';
import { IntlProvider, addLocaleData,FormattedMessage } from 'react-intl';
import * as Const from './../konstanter';
addLocaleData(nbLocale);

const LASTER = 'initial loading';
const LASTET = 'initial';
const ANTALL_TEMAER = 6;

function widgetSnurrepipp (status) {
    document.querySelector('.widget-saksoversikt header div').className = status;
}

class Temaliste extends React.Component {
    componentWillMount() {
        this.props.hentWidgetData(this.props.fnr);
        this.sendToWidget = WicketSender.bind(this, this.props.wicketurl, this.props.wicketcomponent);
    }

    render() {

        if (this.props.status !== Const.LASTET) {
            widgetSnurrepipp(LASTER);
            return <div></div>;
        }

        const { temaer, fnr } = this.props;
        const redusertAntallTemaer = take(temaer, ANTALL_TEMAER);
        const temaliste = redusertAntallTemaer.map((tema) =>
            <li key={tema.temakode}><Sakstema tema={tema} fnr={fnr} sendToWicket={this.sendToWidget}/></li>
        );

        widgetSnurrepipp(LASTET);

        return (
            <IntlProvider defaultLocale="nb" locale="nb" messages={this.props.tekster}>
                <ul>
                    {temaliste}
                    <li><a href="javascript:void(0)" onClick={() => this.sendToWidget('VIS_ALLE_CLICK')} tabIndex="-1" ><FormattedMessage id="sakswidget.sefleresaker" /></a></li>
                </ul>
            </IntlProvider>
        );
    }
}


Temaliste.PropTypes = {
    temaer: React.PropTypes.array,
    fnr: React.PropTypes.string
};

const mapStateToProps = (state) => {
    return {
        temaer: state.widget.data.temaer,
        tekster: state.widget.data.tekster,
        status: state.widget.status,
    };
};

export default connect(mapStateToProps, { hentWidgetData })(Temaliste);

