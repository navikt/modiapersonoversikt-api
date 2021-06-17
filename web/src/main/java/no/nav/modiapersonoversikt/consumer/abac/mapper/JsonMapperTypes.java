package no.nav.modiapersonoversikt.consumer.abac.mapper;

import com.google.gson.reflect.TypeToken;
import no.nav.modiapersonoversikt.consumer.abac.Advice;
import no.nav.modiapersonoversikt.consumer.abac.AttributeAssignment;
import no.nav.modiapersonoversikt.consumer.abac.Response;

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
