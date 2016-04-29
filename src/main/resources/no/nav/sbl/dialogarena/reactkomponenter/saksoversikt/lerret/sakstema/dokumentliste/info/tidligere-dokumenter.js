import React from 'react';
import { FormattedMessage } from 'react-intl';

const TidligereDokumenter = () => (
    <section className="panel gamle-dokumenter">
        <img className="tidligere-dokumenter-bilde" src="/modiabrukerdialog/img/saksoversikt/file-block.svg"
          alt="Dokumentet kan ikke vises" aria-hidden="true"
        />
        <p className="tidligere-dokumenter-tekst">
            <FormattedMessage
              id={'dokumentinfo.ikke.vise.tidligere.dokumenter.head'}
            />
        </p>
    </section>
);

export default TidligereDokumenter;
