import React from 'react';
import { connect } from 'react-redux';
import Sakstema from './tema';
import { hentWidgetData } from './../actions';
import { take } from 'lodash';
import WicketSender from './../../react-wicket-mixin/wicket-sender';
import nbLocale from 'react-intl/locale-data/nb';
import { IntlProvider, addLocaleData,FormattedMessage } from 'react-intl';
import * as Const from './../konstanter';
addLocaleData(nbLocale);

const ANTALL_TEMAER = 6;

function widgetSnurrepipp(status) {
    const initial = document.querySelector('.widget-saksoversikt .klikkbar-header .initial');
    if (!initial)return;

    if (status === Const.LASTER) {
        initial.classList.add('loading');
    } else {
        initial.classList.remove('loading');
    }
}

class Temaliste extends React.Component {
    componentWillMount() {
        this.props.hentWidgetData(this.props.fnr);
        this.sendToWidget = WicketSender.bind(this, this.props.wicketurl, this.props.wicketcomponent);
        widgetSnurrepipp(this.props.status)
    }

    componentDidUpdate() {
        widgetSnurrepipp(this.props.status);
    }

    render() {
        const { temaer, fnr, tekster, status } = this.props;

        if (status === Const.LASTER) {
            return <noscript></noscript>;
        }

        const temaliste = status === Const.FEILET ? <li className="feederroritem"><p className="-ikon-feil"><FormattedMessage id="sakswidget.feilmelding" /></p></li>
            :  take(temaer, ANTALL_TEMAER).map((tema) =>
            <li key={tema.temakode}><Sakstema tema={tema} fnr={fnr} sendToWicket={this.sendToWidget}/></li>);

        return (
            <IntlProvider defaultLocale="nb" locale="nb" messages={tekster}>
                <ul>
                    {temaliste}
                    <li><a href="javascript:void(0)" onClick={() => this.sendToWidget('VIS_ALLE_CLICK')}
                           tabIndex="-1"><FormattedMessage id="sakswidget.sefleresaker"/></a></li>
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

