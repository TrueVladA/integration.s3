package ru.bpmcons.client.ecm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class EcmFileResponse {
    private String success;
    private String error;
    private EcmFileDto file;
}
