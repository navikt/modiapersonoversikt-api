import http from 'superagent';
import Q from 'q';

class Ajax {

    static toPromise(req) {
        const deferred = Q.defer();

        req.end((err, resp) => {
            if (err)deferred.reject([err, resp]);
            else deferred.resolve(resp.body);
        });

        return deferred.promise;
    }

    static doRequest(req, requestModifier = req => req) {
        return Ajax.toPromise(requestModifier(req))
    }

    static get(url, requestModifier = (req) => req) {
        return Ajax.doRequest(http.get(url), requestModifier);
    }

    static post(url, data, requestModifier = (req) => req) {

        return this.doRequest(http.post(url).type('application/json').send(data), requestModifier);
    }

    static put(url) {
        return doRequest(http.put(url), requestModifier);
    }

    static delete(url) {
        return doRequest(http.del(url), requestModifier);
    }
}

export default Ajax;