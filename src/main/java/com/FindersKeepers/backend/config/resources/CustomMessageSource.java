package com.FindersKeepers.backend.config.resources;


import com.FindersKeepers.backend.config.resources.locale.LocaleThreadStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class CustomMessageSource {

    private final MessageSource messageSource;

    public CustomMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public String get(String code) {
        return messageSource.getMessage(code, null, LocaleThreadStorage.getLocale());
    }

    public String get(String code, Object... objects) {
        return messageSource.getMessage(code, objects, LocaleThreadStorage.getLocale());
    }


}
