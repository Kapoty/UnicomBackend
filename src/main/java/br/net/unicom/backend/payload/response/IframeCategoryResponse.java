package br.net.unicom.backend.payload.response;

import java.util.List;

import br.net.unicom.backend.model.Iframe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString @AllArgsConstructor
public class IframeCategoryResponse {

    private Integer iframeCategoryId;

    private String titulo;

    private String uri;

    private String icon;

    private String iconFilename;

    private Boolean ativo;

    private Integer empresaId;

    private List<Iframe> iframeList;

}
