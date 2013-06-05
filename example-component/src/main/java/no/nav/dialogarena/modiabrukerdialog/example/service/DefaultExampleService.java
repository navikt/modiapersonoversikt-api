package no.nav.dialogarena.modiabrukerdialog.example.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;


public class DefaultExampleService implements ExampleService {

    @Value("${content.label:Content}")
    private String contentLabel;

    @Override
    public String getContent() {
        return new StringBuilder(contentLabel).append(" ").append(new Date().toString()).toString();
    }

    public void setContentLabel(String contentLabel) {
        this.contentLabel = contentLabel;
    }

}
