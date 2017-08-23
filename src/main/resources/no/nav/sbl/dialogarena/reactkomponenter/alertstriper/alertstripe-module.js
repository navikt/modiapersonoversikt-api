import React, { Component } from 'react';
import Icon from 'nav-frontend-ikoner-assets';

class AlertstripeSuksessSolid extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        const { tekst } = this.props;
        return (
            <div className="alertstripe alertstripe--suksess alertstripe--solid blokk-s">
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