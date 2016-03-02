import React from 'react';
import { FormattedMessage } from 'react-intl';

// TODO stateless function
class DokumentinfoVedlegg extends React.Component {
    render() {
        const { vedlegg } = this.props;

        if (!vedlegg || vedlegg.length === 0) {
            return <noscript />;
        }

        const vedleggListe = vedlegg.map((dokumentVedlegg) => (
            <li ><a href="javascript:void(0)" className="vedleggtext">{dokumentVedlegg.tittel}</a></li>)
        );

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
