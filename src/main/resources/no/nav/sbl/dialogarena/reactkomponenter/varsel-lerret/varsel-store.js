import Store from './../utils/store';
import ResourceMap from './../utils/resource-map';
import { sortBy } from 'lodash';

class VarselStore extends Store {
    constructor(fnr) {
        super();

        const varsler = $.get('/modiabrukerdialog/rest/varsler/' + fnr);
        const resources = $.get('/modiabrukerdialog/rest/varsler/' + fnr + '/resources');

        this.state = {
            varsler: [],
            promise: $.when(varsler, resources),
            resources: new ResourceMap({}),
            filtersetup: {
                fraDato: undefined,
                tilDato: undefined,
                type: undefined
            }
        };

        this.state.promise.done((varslerResponse, resourcesResponse) => {
            const [varsler] = varslerResponse;
            const [resources] = resourcesResponse;

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