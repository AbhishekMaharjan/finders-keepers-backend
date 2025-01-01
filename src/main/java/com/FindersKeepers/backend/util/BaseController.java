package com.FindersKeepers.backend.util;



import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    /**
     * Global API Response Instance
     */
    @Autowired
    protected GlobalApiResponse globalApiResponse;

    /**
     * API Success Status
     */
    protected final boolean API_SUCCESS_STATUS = true;

    /**
     * API Error Status
     */
    protected final boolean API_ERROR_STATUS = false;

    /**
     * Message Source Instance
     */
    @Autowired
    protected CustomMessageSource customMessageSource;


    /**
     * Function that sends successful API Response
     *
     * @param message
     * @param data
     * @return
     */
    protected GlobalApiResponse successResponse(String message, Object data) {
        globalApiResponse.setStatus(API_SUCCESS_STATUS);
        globalApiResponse.setMessage(message);
        globalApiResponse.setData(data);
        return globalApiResponse;
    }

    protected GlobalApiResponse successResponse(String message) {
        globalApiResponse.setStatus(API_SUCCESS_STATUS);
        globalApiResponse.setMessage(message);
        globalApiResponse.setData(null);
        return globalApiResponse;
    }

    /**
     * Function that sends error API Response
     *
     * @param message
     * @param errors
     * @return
     */
    protected GlobalApiResponse errorResponse(String message, Object errors) {
        globalApiResponse.setStatus(API_ERROR_STATUS);
        globalApiResponse.setMessage(message);
        globalApiResponse.setData(errors);
        return globalApiResponse;
    }
}
