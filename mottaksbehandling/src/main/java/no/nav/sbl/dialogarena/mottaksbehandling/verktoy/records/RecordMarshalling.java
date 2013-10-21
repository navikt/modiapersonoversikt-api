package no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.joda.time.DateTime;

public class RecordMarshalling<T> {
    private static XStream xStream = new XStream();

    static {
        xStream.registerConverter(new JodaTimeConverter());
        xStream.alias("record", Record.class);
        xStream.alias("key", Key.class);
        xStream.alias("dateTime", DateTime.class);
    }

    public static <T> String  marshal(Record<T> record) {
        return xStream.toXML(record);
    }

    @SuppressWarnings("unchecked")
    public static <T> Record<T> unmarshal(String s) {
        return (Record<T>) xStream.fromXML(s);
    }

    public static class JodaTimeConverter implements Converter {
        @Override
        @SuppressWarnings("unchecked")
        public boolean canConvert(final Class type)
        {
            return DateTime.class.isAssignableFrom(type);
        }

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
        {
            writer.setValue(source.toString());
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
        {
            return new DateTime(reader.getValue());
        }
    }
}
