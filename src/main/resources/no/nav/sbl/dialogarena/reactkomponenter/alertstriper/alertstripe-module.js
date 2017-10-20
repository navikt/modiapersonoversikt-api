import React, { Component } from 'react';
import Icon from 'nav-frontend-ikoner-assets';

class AlertstripeSuksessSolid extends Component {
    constructor(props) {
        super(props);
        this.state = { tekst: props.tekst };
    }

    render() {
        const { tekst } = this.state;
        return (
            <div
                className="alertstripe alertstripe--suksess alertstripe--solid blokk-s"
                aria-live="assertive"
                aria-atomic="true"
                role="alert"
            >
                <Icon className="ikon" kind="ok-sirkel-fylt" />
                <span className="alertstripe--tekst">
                    {tekst}
                </span>
            </div>
        );
    }
}

AlertstripeSuksessSolid.propTypes = {
    tekst: React.PropTypes.string.isRequired
};

export default AlertstripeSuksessSolid;
