import React from 'react';
import { FormattedMessage } from 'react-intl';

const TidligereDokumenter = () => (
		<section className="panel gamle-dokumenter">
			<img src="/saksoversikt/img/dokument_stop.png" alt="Dokumentet kan ikke vises"/>
			<h1 className="typo-element netto"><FormattedMessage id={'dokumentinfo.ikke.vise.tidligere.dokumenter.head'}/></h1>
			<p className="typo-infotekst"><FormattedMessage id={'dokumentinfo.ikke.vise.tidligere.dokumenter'} /></p>
		</section>
	);

export default TidligereDokumenter;
