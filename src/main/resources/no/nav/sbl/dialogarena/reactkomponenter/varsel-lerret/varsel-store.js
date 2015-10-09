import Store from './../utils/store';
import ResourceMap from './../utils/resource-map';
import { sortBy } from 'lodash';
import Ajax from './../utils/ajax';
import Q from 'q';

class VarselStore extends Store {
    constructor(fnr) {
        super();

        const varsler = Ajax.get('/modiabrukerdialog/rest/varsler/' + fnr);
        const resources = Ajax.get('/modiabrukerdialog/rest/varsler/' + fnr + '/resources');

        this.state = {
            varsler: [],
            promise: Q.all([varsler, resources]),
            resources: new ResourceMap({}),
            filtersetup: {
                fraDato: undefined,
                tilDato: undefined,
                type: undefined
            }
        };

        this.state.promise.done(([varsler, resources]) => {
            this.state.varsler = sortBy(varsler, 'mottattTidspunkt')
                .reverse()
                .map((varsel, idx) => {
                    varsel.ekspandert = false;
                    varsel.idx = idx;
                    return varsel;
                });
            this.state.resources = new ResourceMap(resources);

            this.fireUpdate();
        });
    }

    getResources() {
        return this.state.resources;
    }

    toggleEkspandert(idx) {
        this.state.varsler[idx].ekspandert = !this.state.varsler[idx].ekspandert;
        this.fireUpdate();
    }
}

export default VarselStore;