import Q from 'q';

class PromiseUtils {

    static atLeastN(n, wrapperPromise) {
        const deferred = $.Deferred();
        var numPromises = Object.keys(wrapperPromise).length;
        if (n < 0 || n > numPromises || typeof n !== "number") {
            const message = "n = " + n + ". n must be a number greater than zero, and less than or equal to the number of promises. ";
            throw new RangeError(message);
        }

        const promiseArray = Object.keys(wrapperPromise).sort().reduce((acc, key)=> {
            acc.push(wrapperPromise[key]);
            return acc;
        }, []);

        Q.allSettled(promiseArray)
                        .then(function (settledPromises) {
                            console.log('settled', settledPromises);

                            const results = Object.keys(wrapperPromise).reduce((acc, key, idx) => {
                                acc[key] = settledPromises[idx];
                                return acc;
                            }, {});
                            console.log('results', results);

                            const success = Object.keys(wrapperPromise).reduce((acc, key)=> {
                                if (wrapperPromise[key]) {
                                    return acc + 1;
                                } else {
                                    return acc;
                                }
                            }, 0);
                            console.log('success', success);


                            if (success >= n) {
                                deferred.resolve(results);
                            } else {
                                deferred.reject(results);
                }
            });

        return deferred.promise();
    }
}

export
default
PromiseUtils;