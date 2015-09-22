import Store from './../utils/store'

class VarselStore extends Store {
    constructor(fnr) {
        super();
        this.state = {
            varsler: [],
            promise: $.get('/modiabrukerdialog/rest/varsler/' + fnr),
            filtersetup: {
                fraDato: undefined,
                tilDato: undefined,
                type: undefined
            }
        };

        this.state.promise.done((varsler) => {
            this.state.varsler = varsler.map((varsel, idx) => {
                varsel.ekspandert = false;
                varsel.idx = idx;
                return varsel;
            });
            this.fireUpdate();
        })
    }

    toggleEkspandert(idx) {
        this.state.varsler[idx].ekspandert = !this.state.varsler[idx].ekspandert;
        this.fireUpdate();
    }
}

export default VarselStore;