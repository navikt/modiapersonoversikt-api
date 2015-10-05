import http from 'superagent';
import Q from 'q';

function toPromise(req) {
    const deferred = Q.defer();

    req.end((err, resp) => {
        if (err)deferred.reject([err, resp]);
        else deferred.resolve(resp.body);
    });

    return deferred.promise;
}

function doRequest(req, requestModifier = req => req) {
    return toPromise(requestModifier(req))
}

class Ajax {
    static get(url, requestModifier = (req) => req) {
        return doRequest(http.get(url), requestModifier);
    }

    static post(url, contentType, data, requestModifier = (req) => req) {

        return doRequest(http.post(url).type(contentType).send(data), requestModifier);
    }

    static put(url) {
        return doRequest(http.put(url), requestModifier);
    }

    static delete(url) {
        return doRequest(http.del(url), requestModifier);
    }
}

export default Ajax;