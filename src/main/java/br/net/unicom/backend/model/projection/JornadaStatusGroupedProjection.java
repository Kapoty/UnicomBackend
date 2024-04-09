package br.net.unicom.backend.model.projection;

public interface JornadaStatusGroupedProjection {

    Integer getJornadaStatusId();

    String getNome();
    
    Integer getMaxDuracao();

    Integer getDuracao();

    Integer getMaxUso();

    Integer getUsos();

    String getCor();

    Boolean getHoraTrabalhada();
}
