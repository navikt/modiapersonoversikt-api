import React from 'react';
import DokumentVisning from './dokument-visning';

function DokumentVisningListe({ dokumenter }) {

    const hoveddokument = dokumenter.find(dokument => dokument.erHoveddokument);
    const andreDokumenter = dokumenter.filter(dokument => !dokument.erHoveddokument);
    const sortertDokumentliste = [].concat(hoveddokument).concat(andreDokumenter);

    const dokumentElementer = sortertDokumentliste.map((dokument) => (
        <DokumentVisning key={dokument.dokumentreferanse} dokument={dokument}/>
    ));

    return (
        <div className="dokumentervisning-liste">
            {dokumentElementer}
        </div>
    );
}

export default DokumentVisningListe;
