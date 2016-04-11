import React from 'react';
import { FormattedMessage } from 'react-intl';

const DokumentinfoVedlegg = ({ visSide, velgJournalpost, dokumentinfo }) => {
    function _redirect(index) {
        return (e) => {
            e.preventDefault();
            visSide('dokumentvisning');
            const dokumentMetadata = Object.assign({ valgtIndex: index }, dokumentinfo);
            velgJournalpost(dokumentMetadata);
        };
    }

    if (!dokumentinfo.vedlegg || dokumentinfo.vedlegg.length === 0) {
        return <noscript />;
    }
    const vedleggListe = dokumentinfo.vedlegg.map((dokumentVedlegg, index) => (
        <li className="vedlegg-element" key={`vedlegg${index}`}>
            <a href="#" onClick={_redirect(index + 1)} className="vedleggtext">
                {dokumentVedlegg.tittel}
            </a>
        </li>));
    return (
        <div className="vedleggcontainer">
            <div className="vedleggheader"><FormattedMessage id="dokumentinfo.vedlegg"/></div>
            <ul className="vedleggliste">{vedleggListe}</ul>
        </div>

    );
};


DokumentinfoVedlegg.propTypes = {
    dokumentinfo: React.PropTypes.object.isRequired,
    visSide: React.PropTypes.func.isRequired,
    velgJournalpost: React.PropTypes.func.isRequired
};

export default DokumentinfoVedlegg;
