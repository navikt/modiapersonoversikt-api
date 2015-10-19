class Store {

    constructor(state) {
        this.listeners = [];
        this.state = state;
        this.container = undefined;

    }

    addListener(listener) {
        this.listeners.push(listener);
    }

    removeListener(listener) {
        var nyeListeners = this.listeners.slice(0);
        var index = nyeListeners.indexOf(listener);
        this.listeners.splice(index, 1);
    }

    getState() {
        return this.state;
    }

    fireUpdate() {
        this.listeners.forEach(function (listener) {
            listener();
        });
    }

    setContainerElement(container) {
        this.container = container;
    }
}

export default Store;