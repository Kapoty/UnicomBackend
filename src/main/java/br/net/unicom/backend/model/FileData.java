package br.net.unicom.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class FileData {

    private String filename;
    private String url;
    private Long size;

}