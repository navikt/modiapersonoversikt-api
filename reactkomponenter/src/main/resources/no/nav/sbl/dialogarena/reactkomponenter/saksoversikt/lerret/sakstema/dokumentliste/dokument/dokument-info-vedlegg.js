import React from 'react';
import PT from 'prop-types';
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

    function getVedleggTekst(dokumentVedlegg, index) {
        if (dokumentVedlegg.logiskDokument) {
            return (
                <span className="vedleggtext ikke-lenke"> {dokumentVedlegg.tittel} </span>
            );
        }

        return (
            <a href="#" onClick={_redirect(index + 1)} className="vedleggtext">
                {dokumentVedlegg.tittel}
            </a>
        );
    }

    if (!dokumentinfo.vedlegg || dokumentinfo.vedlegg.length === 0) {
        return <noscript />;
    }
    const vedleggListe = dokumentinfo.vedlegg.map((dokumentVedlegg, index) => (
        <li className="vedlegg-element" key={`vedlegg${index}`}>
            { getVedleggTekst(dokumentVedlegg, index) }
        </li>));
    return (
        <div className="vedleggcontainer">
            <div className="vedleggheader"><FormattedMessage id="dokumentinfo.vedlegg" /></div>
            <ul className="vedleggliste">{vedleggListe}</ul>
        </div>

    );
};

DokumentinfoVedlegg.propTypes = {
    dokumentinfo: PT.object.isRequired,
    visSide: PT.func.isRequired,
    velgJournalpost: PT.func.isRequired
};

export default DokumentinfoVedlegg;
