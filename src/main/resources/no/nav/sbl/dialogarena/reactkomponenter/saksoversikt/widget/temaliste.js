import React from 'react';
import { connect } from 'react-redux';
import Sakstema from './tema';
import { hentWidgetData } from './../actions';
import { take } from 'lodash';
import WicketSender from './../../react-wicket-mixin/wicket-sender';
import nbLocale from 'react-intl/locale-data/nb';
import { IntlProvider, addLocaleData, FormattedMessage } from 'react-intl';
import * as Const from './../konstanter';
addLocaleData(nbLocale);

const ANTALL_TEMAER = 6;

function widgetSnurrepipp(status) {
    const initial = document.querySelector('.widget-saksoversikt .klikkbar-header .initial');
    if (!initial) return;

    if (status === Const.LASTER) {
        initial.classList.add('loading');
    } else {
        initial.classList.remove('loading');
    }
}

const feilmelding = (
    <div className="listeelement-kant">
        <p className="-ikon-feil"><FormattedMessage id="sakswidget.feilmelding"/></p>
    </div>
);

class Temaliste extends React.Component {
    componentWillMount() {
        this.props.hentWidgetData(this.props.fnr);
        this.sendToWidget = WicketSender.bind(this, this.props.wicketurl, this.props.wicketcomponent);
        widgetSnurrepipp(this.props.status);
    }

    componentDidUpdate() {
        widgetSnurrepipp(this.props.status);
    }

    render() {
        const { temaer, fnr, tekster, status } = this.props;

        if (status === Const.VOID || status === Const.LASTER) {
            return <noscript/>;
        }

        const temaliste = status === Const.FEILET ? feilmelding :
            take(temaer, ANTALL_TEMAER)
                .map((tema) => (
                    <li key={tema.temakode}>
                        <Sakstema tema={tema} fnr={fnr} sendToWicket={this.sendToWidget}/>
                    </li>
                ));

        const sendToWidget = () => this.sendToWidget('VIS_ALLE_CLICK');

        const flereSaker = (
            <li>
                <a href="#" onClick={sendToWidget} tabIndex="-1">
                    <FormattedMessage id="sakswidget.sefleresaker"/>
                </a>
            </li>
        );

        return (
            <IntlProvider defaultLocale="nb" locale="nb" messages={tekster}>
                <ul>
                    {temaliste}
                    {flereSaker}
                </ul>
            </IntlProvider>
        );
    }
}


Temaliste.propTypes = {
    temaer: React.PropTypes.array,
    fnr: React.PropTypes.string.isRequired,
    wicketurl: React.PropTypes.string.isRequired,
    wicketcomponent: React.PropTypes.string.isRequired,
    status: React.PropTypes.string.isRequired,
    tekster: React.PropTypes.object,
    hentWidgetData: React.PropTypes.func.isRequired
};

const mapStateToProps = (state) => ({
    temaer: state.widget.data.temaer,
    tekster: state.widget.data.tekster,
    status: state.widget.status
});

export default connect(mapStateToProps, { hentWidgetData })(Temaliste);
