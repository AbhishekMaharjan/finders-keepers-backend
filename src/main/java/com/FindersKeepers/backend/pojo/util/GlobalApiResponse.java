package com.FindersKeepers.backend.pojo.util;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
@Scope("prototype")
public class GlobalApiResponse implements Serializable {
    private boolean status;
    private String message;
    private Object data;
    private List<String> error;
}
