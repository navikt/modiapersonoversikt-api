import React, { Component } from 'react';
import PT from 'prop-types';
import Icon from 'nav-frontend-ikoner-assets';

const clsMap = {
    'suksess': { cls: 'alertstripe alertstripe--suksess alertstripe--solid blokk-s', ikon: 'ok-sirkel-fylt'},
    'info': { cls: 'alertstripe alertstripe--solid alertstripe--info blokk-s', ikon: 'info-sirkel-fylt'},
    'advarsel': { cls: 'alertstripe alertstripe--advarsel alertstripe--solid blokk-s', ikon: 'advarsel-trekant-fylt'}
};

class Alertstripe extends Component {
    render() {
        const config = clsMap[this.props.type];

        return (
            <div
                className={config.cls}
                aria-live="assertive"
                aria-atomic="true"
                role="alert"
            >
                <Icon className="alertstripe__ikon" kind={config.ikon} />
                <span className="alertstripe__tekst">
                    {this.props.tekst}
                    {this.props.children}
                </span>
            </div>
        );
    }
}

Alertstripe.propTypes = {
    tekst: PT.string,
    type: PT.oneOf(['suksess', 'advarsel', 'info'])
};
Alertstripe.defaultProps = {
    type: 'suksess'
};

export default Alertstripe;
