import React from 'react';
import { FormattedMessage } from 'react-intl';

// TODO stateless function
class DokumentinfoVedlegg extends React.Component {

    _redirect(e) {
        e.preventDefault();
        this.props.visSide('dokumentvisning');
        this.props.velgJournalpost(this.props.dokumentinfo);
    }

    render() {
        const { dokumentinfo } = this.props;

        if (!dokumentinfo.vedlegg || dokumentinfo.vedlegg.length === 0) {
            return <noscript />;
        }

        const vedleggListe = dokumentinfo.vedlegg.map(dokumentVedlegg => (
            <li><a href="javascript:void(0);" onClick={this._redirect.bind(this)} className="vedleggtext">{dokumentVedlegg.tittel}</a></li>));

        return (
            <div>
                <div className="vedleggheader"><FormattedMessage id="dokumentinfo.vedlegg"/></div>
                <ul className="vedleggliste">{vedleggListe}</ul>
            </div>
        );
    }
}

DokumentinfoVedlegg.propTypes = {
    dokumentinfo: React.PropTypes.object.isRequired
};

export default DokumentinfoVedlegg;
