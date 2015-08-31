var Store = function (state) {
    this.listeners = [];
    this.state = state;
    this.container = undefined;
};

Store.prototype.addListener = function (listener) {
    this.listeners.push(listener);
};

Store.prototype.removeListener = function (listener) {
    var nyeListeners = this.listeners.slice(0);
    var index = nyeListeners.indexOf(listener);
    this.listeners.splice(index, 1);
};

Store.prototype.getState = function () {
    return this.state;
};
Store.prototype.fireUpdate = function(){
    this.listeners.forEach(function(listener){
        listener();
    });
};

Store.prototype.setContainerElement = function(container){
    this.container = container;
};

module.exports = Store;