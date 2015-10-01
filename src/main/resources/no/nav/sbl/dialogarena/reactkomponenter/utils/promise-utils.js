class PromiseUtils {

    static atLeastN(n, wrapperPromise) {
        const deferred = $.Deferred();
        var numPromises = Object.keys(wrapperPromise).length;
        if (n < 0 || n > numPromises || typeof n !== "number") {
            const message = "n = " + n + ". n must be a number greater than zero, and less than or equal to the number of promises. ";
            throw new RangeError(message);
        }

        const results = Object.keys(wrapperPromise)
            .reduce((acc, key) => {
                acc[key] = null;
                return acc;
            }, {});

        let NOF_resolved = 0;
        let atLeastNIsOK = false;
        let countOK = 0;

        Object.keys(wrapperPromise).forEach((key) => {
            wrapperPromise[key]
                .success((res) => {
                    results[key] = res;
                    countOK++;
                    atLeastNIsOK = countOK >= n;
                })
                .always(() => {
                    NOF_resolved++;

                    if (NOF_resolved === numPromises) {
                        if (atLeastNIsOK) {
                            deferred.resolve(results);
                        } else {
                            deferred.reject(results);
                        }
                    }
                });

        });

        return deferred.promise();
    }
}

export
default
PromiseUtils;