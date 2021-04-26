package no.nav.sbl.dialogarena.abac.mapper;

import com.google.gson.reflect.TypeToken;
import no.nav.sbl.dialogarena.abac.Advice;
import no.nav.sbl.dialogarena.abac.AttributeAssignment;
import no.nav.sbl.dialogarena.abac.Response;

import java.lang.reflect.Type;
import java.util.List;


// Denne måtte være i java pga noe reflection-tull
public class JsonMapperTypes {
    public static final Type responseType = new TypeToken<List<Response>>() {
    }.getType();
    public static final Type associatedAdviceType = new TypeToken<List<Advice>>() {
    }.getType();
    public static final Type attributeAssignmentType = new TypeToken<List<AttributeAssignment>>() {
    }.getType();
}
