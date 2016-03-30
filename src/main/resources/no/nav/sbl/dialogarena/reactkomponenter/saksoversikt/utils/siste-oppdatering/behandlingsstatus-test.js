import './../../../test-config';
import { expect } from 'chai';
import React from 'react';
import assign from 'object-assign';
import { finnBehandlingsstatus, finnAntallBehandlingskjeder, underBehandlingEllerNyereEnnGrenseverdi } from './behandlingsstatus';

const datoForGrense = {
    year: new Date().getFullYear()-1,
    monthValue: new Date().getMonth()+1,
    dayOfMonth: new Date().getDate(),
    hour: 10,
    minute: 39,
    second: 55
};

const datoEtterGrense = {
    year: new Date().getFullYear(),
    monthValue: new Date().getMonth()+1,
    dayOfMonth: new Date().getDate(),
    hour: 10,
    minute: 39,
    second: 55
};

const behandlingskjedeEtterGrenseFerdigbehandlet = {
    sistOppdatert: datoEtterGrense,
    status: "FERDIG_BEHANDLET"
};

const behandlingskjedeFoerGrenseFerdigbehandlet = {
    sistOppdatert: datoForGrense,
    status: "FERDIG_BEHANDLET"
};

const behandlingskjedeEtterGrenseUnderBehandling = {
    sistOppdatert: datoEtterGrense,
    status: "UNDER_BEHANDLING"
};

const behandlingskjedeForGrenseUnderBehandling = {
    sistOppdatert: datoForGrense,
    status: "UNDER_BEHANDLING"
};

describe('Behandlingsstatus', () => {

    const antallDagerFerdigBehandletStatusErGyldig = "28";

    it('finnes ikke hvis ingen behandlingskjeder er under behandling eller ferdig behandlet', () => {
        const behandlingskjeder = [];

        const behandlingsstatustekst = finnBehandlingsstatus(behandlingskjeder, antallDagerFerdigBehandletStatusErGyldig);

        expect(behandlingsstatustekst).to.be.null;
    });
});

describe('AntallBehandlingskjeder', () => {

    it('Returnerer 4 behandlingskjeder', () => {
        const behandlingskjeder = [
            behandlingskjedeEtterGrenseFerdigbehandlet,
            behandlingskjedeEtterGrenseUnderBehandling,
            behandlingskjedeEtterGrenseUnderBehandling,
            behandlingskjedeEtterGrenseFerdigbehandlet
        ];

        const antallBehandligsstatuser = finnAntallBehandlingskjeder(behandlingskjeder);

        expect(antallBehandligsstatuser.antallFerdigBehandlet).to.be.eql(2);
        expect(antallBehandligsstatuser.antallUnderBehandling).to.be.eql(2);
    });


    it('Skal returnerer ingen behandlingskjeder under behandling', () => {
        const behandlingskjeder = [
            behandlingskjedeEtterGrenseFerdigbehandlet,
            behandlingskjedeEtterGrenseFerdigbehandlet
        ];

        const antallBehandligsstatuser = finnAntallBehandlingskjeder(behandlingskjeder);

        expect(antallBehandligsstatuser.antallFerdigBehandlet).to.be.eql(2);
        expect(antallBehandligsstatuser.antallUnderBehandling).to.be.eql(0);
    });

    it('Skal returnerer en behandlingskjede som er under behandling', () => {
        const behandlingskjeder = [
            behandlingskjedeEtterGrenseUnderBehandling
        ];

        const antallBehandligsstatuser = finnAntallBehandlingskjeder(behandlingskjeder);

        expect(antallBehandligsstatuser.antallFerdigBehandlet).to.be.eql(0);
        expect(antallBehandligsstatuser.antallUnderBehandling).to.be.eql(1);
    });
});

describe('FiltrerBehandligskjeder', () => {

    it('Returnerer tom liste om ferdigbehandlede behandlingskjeder foer grensen', () => {
       const behandlingskjeder = [
           behandlingskjedeFoerGrenseFerdigbehandlet
       ];

        const filtrertBehandlingskjedeListe = behandlingskjeder.filter(underBehandlingEllerNyereEnnGrenseverdi( 21 ));

        expect(filtrertBehandlingskjedeListe.length).to.be.eql(0);

    });


    it('Returnerer behandlingskjeder under behandling eller etter grenseverdien', () => {
        const behandlingskjeder = [
            behandlingskjedeForGrenseUnderBehandling,
            behandlingskjedeEtterGrenseUnderBehandling,
            behandlingskjedeEtterGrenseFerdigbehandlet
        ];

        const filtrertBehandlingskjedeListe = behandlingskjeder.filter(underBehandlingEllerNyereEnnGrenseverdi( 21 ));

        expect(filtrertBehandlingskjedeListe.length).to.be.eql(3);

    });

});