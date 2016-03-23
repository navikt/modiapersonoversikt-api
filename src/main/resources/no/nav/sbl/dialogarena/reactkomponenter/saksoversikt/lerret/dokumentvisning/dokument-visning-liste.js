import React from 'react';
import DokumentVisning from './dokument-visning';

function DokumentVisningListe({ dokumenter }) {
    const dokumentElementer = dokumenter.map((dokument) => (
        <DokumentVisning key={dokument.dokumentreferanse} dokument={dokument}/>
    ));

    return (
        <div className="dokumentervisning-liste">
            {dokumentElementer}
        </div>
    );
}

export default DokumentVisningListe;
