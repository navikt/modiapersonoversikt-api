import React from 'react';
import { FormattedMessage } from 'react-intl';

const DokumentinfoVedlegg = ({ visSide, velgJournalpost, dokumentinfo }) => {
    function _redirect(e) {
        e.preventDefault();
        visSide('dokumentvisning');
        velgJournalpost(dokumentinfo);
    }

    if (!dokumentinfo.vedlegg || dokumentinfo.vedlegg.length === 0) {
        return <noscript />;
    }
    let i = 0;
    const vedleggListe = dokumentinfo.vedlegg.map((dokumentVedlegg) => (
        <li className="vedlegg-element" key={`vedlegg${++i}`}>
            <a href="#" onClick={_redirect} className="vedleggtext">
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
