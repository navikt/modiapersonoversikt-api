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
        const nyeListeners = this.listeners.slice(0);
        const index = nyeListeners.indexOf(listener);
        this.listeners.splice(index, 1);
    }

    getState() {
        return this.state;
    }

    fireUpdate() {
        this.listeners.forEach((listener) =>listener());
    }

    setContainerElement(container) {
        this.container = container;
    }
}

export default Store;
