class PromiseUtils {

    static atLeastN(n, ...promises) {
        const deferred = $.Deferred();
        if (n < 0 || n > promises.length || typeof n !== "number") {
            const message = "n = " + n + ". n must be a number greater than zero, and less than or equal to the number of promises. ";
            throw new RangeError(message);
        }
        const results = new Array(promises.length);
        let NOF_resolved = 0;
        let atLeastNIsOK = false;
        let countOK = 0;

        promises.forEach((promise, index) => {
            promise
                .success((res) => {
                    results[index] = res;
                    countOK++;
                    atLeastNIsOK = countOK >= n;
                })
                .fail(() => {
                    results[index] = null;
                })
                .always(() => {
                    NOF_resolved++;

                    if (NOF_resolved === promises.length) {
                        if (atLeastNIsOK)deferred.resolve(results);
                        else deferred.reject(results);
                    }
                });
        });


        return deferred.promise();
    }
}

export
default
PromiseUtils;