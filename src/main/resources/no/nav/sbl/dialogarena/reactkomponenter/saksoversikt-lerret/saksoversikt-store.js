import Store from './../utils/store';
import ResourceMap from './../utils/resource-map';
import { sortBy } from 'lodash';
import Ajax from './../utils/ajax';
import Q from 'q';

class SaksoversiktStore extends Store {
    constructor(fnr) {
        super();
        this._resourcesResolved = this._resourcesResolved.bind(this);
        this.state.promise.done(this._resourcesResolved);
    }

    getResources() {
        return this.state.resources;
    }
}

export default SaksoversiktStore;