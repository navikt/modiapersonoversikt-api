import Store from './../utils/store';
import ResourceMap from './../utils/resource-map';
import { sortBy } from 'lodash';
import Ajax from './../utils/ajax';
import Q from 'q';


class SaksoversiktStore extends Store {
    constructor(fnr) {
        super();
        this._resourcesResolved = this._resourcesResolved.bind(this);
        const temaer = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/temaer');
        const journalposter = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/journalposter');
        const sakstema = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/sakstema');

        this.state = {
            temaer: [],
            journalposter: [],
            sakstema: [],
            promise: Q.all([temaer, journalposter, sakstema])
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

    _resourcesResolved([temaer, journalposter, sakstema]) {
        this.state.temaer = temaer;
        this.state.journalposter = journalposter;
        this.state.sakstema = sakstema;

        this.fireUpdate();
    }
}

export default SaksoversiktStore;
