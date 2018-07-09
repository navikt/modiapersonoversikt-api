import './../../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import { LASTER, FEILET, LASTET } from './../konstanter';
import sinon from 'sinon';

import { SaksoversiktLerret } from './saksoversikt-lerret-module';
import ViktigAVitePage from './viktigavite/viktig-aa-vite-page';
import SakstemaPage from './sakstema/sakstema-page';

const mockProps = {
    fnr: '',
    brukerNavn: '',
    norgUrl: '',
    gt: '',
    miljovariabler: {}
};

describe('SaksoversiktLerret module', () => {
    const noop = () => undefined;

    it('skal hente lerret data for fnr når den mountes', () => {
        const lerretSpy = sinon.spy();
        shallow(<SaksoversiktLerret {...mockProps}
            hentLerretData={lerretSpy}
            fnr="12345678901"
            status={LASTER}
        />);

        expect(lerretSpy.withArgs('12345678901').calledOnce).to.be.true;
    });

    it('skal rendre snurrepipp når status er laster', () => {
        const element = shallow(<SaksoversiktLerret {...mockProps} status={LASTER} hentLerretData={noop} />);

        expect(element.find('Snurrepipp').length).to.equal(1);
    });

    it('skal vise feilmelding om kallet feilet', () => {
        const element = shallow(<SaksoversiktLerret {...mockProps} status={FEILET} hentLerretData={noop} />);

        expect(element.find('Snurrepipp').length).to.equal(0);
        expect(element.find('.lamell-feilmelding').length).to.equal(1);
    });

    it('skal vise valgt page/side', () => {
        const element = shallow(<SaksoversiktLerret {...mockProps} valgtside="viktigavite" status={LASTET} hentLerretData={noop} />);

        expect(element.find(ViktigAVitePage).length).to.equal(1);
    });

    it('skal vise sakstema by default', () => {
        const element = shallow(<SaksoversiktLerret {...mockProps} status={LASTET} hentLerretData={noop} />);

        expect(element.find(SakstemaPage).length).to.equal(1);
    });
});
