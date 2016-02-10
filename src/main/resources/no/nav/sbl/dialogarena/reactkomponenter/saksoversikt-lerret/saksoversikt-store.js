import Store from './../utils/store';
import ResourceMap from './../utils/resource-map';
import { sortBy } from 'lodash';
import Ajax from './../utils/ajax';

class SaksoversiktStore extends Store {
    constructor(fnr) {
        super();
        this._resourcesResolved = this._resourcesResolved.bind(this);

        this.state = {
            behandlingerByTema: [],
            promise: Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr)
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
        this.state.behandlingerByTema = Object.keys(behandlingerByTema).reduce((temaMapping, tema)=> {
            const temaNavn = behandlingerByTema[tema][0].sakstema;
            temaMapping[temaNavn] = behandlingerByTema[tema];
            return temaMapping;
        }, {});

        this.fireUpdate();
    }
}

export default SaksoversiktStore;
