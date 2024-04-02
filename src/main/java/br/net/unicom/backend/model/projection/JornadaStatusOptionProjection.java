package br.net.unicom.backend.model.projection;

public interface JornadaStatusOptionProjection {

    Integer getJornadaStatusId();

    String getNome();
    
    Integer getMaxDuracao();

    Integer getMaxUso();

    Integer getUsos();

    String getCor();

    Integer getUsuarioPodeAtivar();

    Integer getSupervisorPodeAtivar();

}
