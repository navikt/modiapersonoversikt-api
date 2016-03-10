import React from 'react';
import { FormattedMessage } from 'react-intl';

const TidligereDokumenter = () => (
    <section className="panel gamle-dokumenter">
        <img className="tidligaredokumentimage" src="/modiabrukerdialog/img/saksoversikt/dokument_stop.png"
             alt="Dokumentet kan ikke vises" aria-hidden="true"/>
        <p className="tidligaredokumentheader"><FormattedMessage
            id={'dokumentinfo.ikke.vise.tidligere.dokumenter.head'}/>
        </p>
    </section>
);

export default TidligereDokumenter;
