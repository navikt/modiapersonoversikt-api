/* eslint no-console:0 */
class ResourceMap {
    constructor(resources) {
        this.resources = resources;
    }

    hasResource(key) {
        return this.resources.hasOwnProperty(key);
    }

    get(key) {
        if (!this.hasResource(key)) {
            console.warn('Uthenting av key: %s feilet. Fant ingen mathc.', key);
        }
        return this.resources[key];
    }

    getOrElse(key, defaultValue) {
        return this.hasResource(key) ? this.get(key) : defaultValue;
    }
}

export default ResourceMap;
