import React from 'react';
import PT from 'prop-types';
import DokumentVisning from './dokument-visning';

export function hoveddokumentForst(dokumenter) {
    const hoveddokument = dokumenter.find(dokument => dokument.erHoveddokument);
    const andreDokumenter = dokumenter.filter(dokument => !dokument.erHoveddokument);
    return hoveddokument ? [].concat(hoveddokument).concat(andreDokumenter) : dokumenter;
}

function DokumentVisningListe({ dokumenter }) {
    const sortertDokumentliste = hoveddokumentForst(dokumenter);

    const dokumentElementer = sortertDokumentliste.map((dokument) => (
        <DokumentVisning key={dokument.dokumentreferanse} dokument={dokument} />
    ));

    return (
        <div className="dokumentervisning-liste">
            {dokumentElementer}
        </div>
    );
}

let dokumentShape = PT.shape({
    erHoveddokument: PT.bool,
    dokumentreferanse: PT.string.isRequired,
    antallSider: PT.number,
    journalpostId: PT.string,
    pdfUrl: PT.string
});

DokumentVisningListe.propTypes = {
    dokumenter: PT.arrayOf(dokumentShape).isRequired
};

export default DokumentVisningListe;
