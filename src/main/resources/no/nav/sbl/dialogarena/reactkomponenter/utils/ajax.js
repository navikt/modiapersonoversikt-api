import http from 'superagent';
import Q from 'q';

class Ajax {

    static toPromise(req) {
        const deferred = Q.defer();

        req.end((err, resp) => {
            if (err) {
                deferred.reject([err, resp]);
            } else {
                deferred.resolve(resp.body);
            }
        });

        return deferred.promise;
    }

    static doRequest(request, requestModifier = req => req) {
        return Ajax.toPromise(requestModifier(request));
    }

    static get(url, requestModifier = (req) => req) {
        return Ajax.doRequest(http.get(url), requestModifier);
    }

    static post(url, data, requestModifier = (req) => req) {
        return Ajax.doRequest(http.post(url).type('application/json').send(data), requestModifier);
    }

    static put(url, requestModifier = (req) => req) {
        return Ajax.doRequest(http.put(url), requestModifier);
    }

    static delete(url, requestModifier = (req) => req) {
        return Ajax.doRequest(http.del(url), requestModifier);
    }
}

export default Ajax;
