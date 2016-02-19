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
        const behandlingerByTema = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/behandlinger-by-tema');
        const journalposter = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/journalposter');
        const tekster = Ajax.get('/modiabrukerdialog/rest/informasjon/tekster');
        const miljovariabler = Ajax.get('/modiabrukerdialog/rest/informasjon/miljovariabler');

        this.state = {
            tekster: {},
            miljovariabler: {},
            behandlingerByTema: {},
            temaer: [],
            journalposter: [],
            promise: Q.all([temaer, behandlingerByTema, journalposter])
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

    _resourcesResolved([temaer, behandlingerByTema, journalposter, tekster, miljovariabler]) {
        this.state.temaer = temaer;
        this.state.behandlingerByTema = Object.keys(behandlingerByTema).reduce((temaMapping, tema)=> {
            const temaNavn = behandlingerByTema[tema][0].sakstema;
            temaMapping[temaNavn] = behandlingerByTema[tema];
            return temaMapping;
        }, {});
        this.state.journalposter = journalposter;
        this.state.tekster = tekster;
        this.state.miljovariabler = miljovariabler;

        this.fireUpdate();
    }
}

export default SaksoversiktStore;
