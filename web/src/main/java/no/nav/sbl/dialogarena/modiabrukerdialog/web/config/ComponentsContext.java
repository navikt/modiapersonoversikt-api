package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        no.nav.dialogarena.modiabrukerdialog.example.config.ExampleContext.class
        , no.nav.kjerneinfo.consumer.config.ConsumerConfig.class
        //, no.nav.kjerneinfo.consumer.config.MockConsumerConfig.class
        , no.nav.sykmeldingsperioder.config.SykmeldingsperioderPanelConfig.class
            //        , no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig.class
            //, no.nav.personsok.config.spring.PersonsokConfig.class
        , no.nav.personsok.consumer.config.ConsumerConfig.class
        , no.nav.kjerneinfo.hent.config.spring.HentPersonConfig.class
        , no.nav.kjerneinfo.config.spring.KjerneinfoConfig.class
        //brukerProfil - start
        , no.nav.brukerprofil.config.BrukerprofilPanelConfig.class
        , no.nav.brukerprofil.config.spring.ConsumerConfig.class
        , no.nav.behandlebrukerprofil.config.spring.BehandleBrukerprofilConsumerConfig.class
        , no.nav.behandlebrukerprofil.config.spring.ConsumerConfig.class
//        //brukerProfil - slutt

})
public class ComponentsContext {

}
