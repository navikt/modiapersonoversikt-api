import './../../../../../test-config';
import React from 'react';
import { expect } from 'chai';
import { IntlProvider } from 'react-intl';
import DokumentinfoVedlegg from './dokument-info-vedlegg';
import { mount, shallow } from 'enzyme';

describe('DokumentinfoVedlegg', () => {
    const dokumentinfo = {
        vedlegg: [
            { tittel: 'test1' },
            { tittel: 'test2' }
        ]
    };

    function noop() {}

    const props = {
        dokumentinfo,
        visSide: noop,
        velgJournalpost: noop
    };

    it('Gir noscript om vedlegglisten er tom', () => {
        const emptyProps = {
            dokumentinfo: { vedlegg: [] },
            visSide: noop,
            velgJournalpost: noop
        };

        const result = shallow(<DokumentinfoVedlegg {...emptyProps} />);

        expect(result.is('noscript')).to.be.true;
    });
    it('Gir vedleggliste om det finnes vedlegg', () => {
        const result = shallow(<DokumentinfoVedlegg {...props} />);

        expect(result.is('div')).to.be.true;
        expect(result.props().className).to.equal('vedleggcontainer');
    });
    it('Gir riktig antall vedlegg i vedlegglista', () => {
        const element = mount(
            <IntlProvider locale="en" messages={{ 'dokumentinfo.vedlegg': 'vedlegg' }}>
                <DokumentinfoVedlegg {...props} />
            </IntlProvider>
        );

        expect(element.find('.vedleggcontainer').length).to.equal(1);

        expect(element.find('li').length).to.equal(2);

        expect(element.find('.vedlegg-element').length).to.equal(2);
    });
});
