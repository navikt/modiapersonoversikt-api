import Store from './../utils/store';
import ResourceMap from './../utils/resource-map';
import { sortBy } from 'lodash';
import Ajax from './../utils/ajax';
import Q from 'q';

class SaksoversiktStore extends Store {
    constructor(fnr) {
        super();
        this._resourcesResolved = this._resourcesResolved.bind(this);
        const behandlingerByTema = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr);

        this.state = {
            behandlingerByTema: [],
            promise: behandlingerByTema
        };
        this.state.promise.done(this._resourcesResolved);
    }

    componentDidMount() {
        this.store.addListener(this.updateState);
    }

    componentWillUnmount() {
        this.store.removeListener(this.updateState);
    }

    updateState() {
        this.setState(this.store.getState());
    }

    getResources() {
        return this.state.resources;
    }

    _resourcesResolved(behandlingerByTema) {
        this.state.behandlingerByTema = behandlingerByTema;
        this.fireUpdate();
    }
}

export default SaksoversiktStore;
