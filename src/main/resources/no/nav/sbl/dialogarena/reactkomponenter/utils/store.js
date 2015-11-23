const Store = function Store(state) {
    this.listeners = [];
    this.state = state;
    this.container = undefined;
};

Store.prototype.addListener = function addListener(listener) {
    this.listeners.push(listener);
};

Store.prototype.removeListener = function removeListener(listener) {
    const nyeListeners = this.listeners.slice(0);
    const index = nyeListeners.indexOf(listener);
    this.listeners.splice(index, 1);
};

Store.prototype.getState = function getState() {
    return this.state;
};
Store.prototype.fireUpdate = function fireUpdate() {
    this.listeners.forEach(function cb(listener) {
        listener();
    });
};

Store.prototype.setContainerElement = function setContainerElement(container) {
    this.container = container;
};

module.exports = Store;
