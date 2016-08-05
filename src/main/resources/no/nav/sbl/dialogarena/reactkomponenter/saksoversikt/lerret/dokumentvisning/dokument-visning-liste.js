import React from 'react';
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


export default DokumentVisningListe;
