package no.nav.sbl.dialogarena.besvare.config;

import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Felles konfigurasjon av jaxws features for web services
 */
public interface JaxWsFeatures {

    List<Feature> jaxwsFeatures();

    @Configuration
    class Integration implements JaxWsFeatures {

        @Override
        @Bean
        public List<Feature> jaxwsFeatures() {
            List<Feature> features = new ArrayList<>();
            features.add(new LoggingFeature());
//            features.add(new WSAddressingFeature());
            return features;
        }
    }

//    @Configuration
//    class Mock implements JaxWsFeatures {
//
//        @Override
//        @Bean
//        public List<Feature> jaxwsFeatures() {
//            List<Feature> features = new ArrayList<>();
//            features.add(new LoggingFeature());
//            return features;
//        }
//    }
}
