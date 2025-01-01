package com.FindersKeepers.backend.config.resources.locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class LanguageConfig {
    protected static final List<Locale> LOCALES = List.of(
            new Locale("en") //ENGLISH
    );

    private LanguageConfig() {

    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource bundleMessageSource = new ReloadableResourceBundleMessageSource();
        bundleMessageSource.setBasename("classpath:messages");
        bundleMessageSource.setDefaultEncoding("UTF-8");
        bundleMessageSource.setCacheSeconds(3600); // Set to -1 to disable caching
        return bundleMessageSource;
    }

    public Locale resolveLocale(String language) {
        if (language == null || language.isEmpty())
            return Locale.forLanguageTag("en");
        Locale locale = Locale.forLanguageTag(language);
        if (LanguageConfig.LOCALES.contains(locale))
            return locale;
        return Locale.forLanguageTag("en");
    }
}
