package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import java.util.List;

public interface Mergeable {

    Object doMerge(List<Mergeable> skalMerges);
}
