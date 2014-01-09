package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import java.util.List;

public interface Mergeable<T> {

    T doMerge(List<Mergeable> skalMerges);
}
