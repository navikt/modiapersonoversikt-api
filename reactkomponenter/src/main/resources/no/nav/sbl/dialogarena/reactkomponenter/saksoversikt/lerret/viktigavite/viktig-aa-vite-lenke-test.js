/* eslint-env mocha */
import './../../../test-config';
import { expect } from 'chai';
import { skalViseViktigAViteSideForTema } from './viktig-aa-vite-lenke';

describe('ViktigAaViteLenke', () => {
    const godkjenteTemakoder = 'DAG,AAP,IND';

    it('Skal vise viktig-aa-vite-lenke', () => {
        const valgtTemakode = 'DAG';

        const skalViseViktigAaViteSideForTema = skalViseViktigAViteSideForTema(godkjenteTemakoder, valgtTemakode);

        expect(skalViseViktigAaViteSideForTema).to.be.eql(true);
    });

    it('Skal ikke vise viktig-aa-vite-lenke', () => {
        const valgtTemakode = 'BID';

        const skalViseViktigAaViteSideForTema = skalViseViktigAViteSideForTema(godkjenteTemakoder, valgtTemakode);

        expect(skalViseViktigAaViteSideForTema).to.be.eql(false);
    });
});
