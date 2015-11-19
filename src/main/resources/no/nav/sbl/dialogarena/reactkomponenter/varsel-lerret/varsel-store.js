import Store from './../utils/store';
import ResourceMap from './../utils/resource-map';
import { sortBy } from 'lodash';
import Ajax from './../utils/ajax';
import Q from 'q';

class VarselStore extends Store {
    constructor(fnr) {
        super();

        this._sortVarsler = this._sortVarsler.bind(this);
        this._resourcesResolved = this._resourcesResolved.bind(this);

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

        this.state.promise.done(this._resourcesResolved);
    }

    _resourcesResolved([varsler, resources]) {
        this._sortVarsler(varsler);
        this.state.resources = new ResourceMap(resources);

        this.fireUpdate();
    }

    _sortVarsler(varsler) {
        this.state.varsler = sortBy(varsler, 'mottattTidspunkt')
            .reverse()
            .map((varsel, idx) => {
                varsel.ekspandert = false;
                varsel.idx = idx;
                return varsel;
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