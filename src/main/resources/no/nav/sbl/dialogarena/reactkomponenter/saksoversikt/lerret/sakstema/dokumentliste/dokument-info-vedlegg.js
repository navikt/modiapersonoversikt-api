import React from 'react';
import { FormattedMessage } from 'react-intl';

class DokumentinfoVedlegg extends React.Component {

    _redirect(e) {
        e.preventDefault();
        this.props.visSide('dokumentvisning');
    }

    render() {
        const vedlegg = this.props.vedlegg;
        if (!vedlegg || vedlegg.length === 0) {
            return <div/>;
        }

        const vedleggListe = vedlegg.map(dokumentVedlegg => (
            <li><a href="javascript:void(0);"
                    onClick={this._redirect.bind(this)} className="vedleggtext">{dokumentVedlegg.tittel}</a></li>));

        return (
            <div>
                <div className="vedleggheader"><FormattedMessage id="dokumentinfo.vedlegg"/></div>
                <ul className="vedleggliste">{vedleggListe}</ul>
            </div>
        );
    }
}

DokumentinfoVedlegg.propTypes = {
    vedlegg: React.PropTypes.array.isRequired
};

export default DokumentinfoVedlegg;
