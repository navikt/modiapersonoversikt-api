package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person;

import java.io.Serializable;

public class GeografiskTilknytning implements Serializable {
    private String value;
    private GeografiskTilknytningstyper type;
    private String diskresjonskode;

    public GeografiskTilknytning withDiskresjonskode(String value) {
        this.diskresjonskode =  value;
        return this;
    }

    public GeografiskTilknytning withValue(String value) {
        this.value = value;
        return this;
    }

    public GeografiskTilknytning withType(GeografiskTilknytningstyper type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public GeografiskTilknytningstyper getType() {
        return type;
    }

    public String getDiskresjonskode() {
        return diskresjonskode;
    }
}
