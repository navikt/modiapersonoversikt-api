import React from 'react';
import { connect } from 'react-redux';
import Sakstema from './tema';
import { hentWidgetData } from './../actions';
import { take } from 'lodash';
import WicketSender from './../../react-wicket-mixin/wicket-sender';
import nbLocale from 'react-intl/dist/locale-data/nb';
import { IntlProvider, addLocaleData,FormattedMessage } from 'react-intl';
addLocaleData(nbLocale);

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
            <li key={tema.temakode}><Sakstema tema={tema} fnr={fnr} sendToWicket={this.sendToWidget}/></li>
        );

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
        tekster: state.widget.data.tekster
    };
};

export default connect(mapStateToProps, { hentWidgetData })(Temaliste);

