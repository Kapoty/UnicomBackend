package br.net.unicom.backend.model.projection;

import java.time.LocalDateTime;

public interface VendaDataStatusProjection {

    Integer getVendaId();

    LocalDateTime getDataStatus();

}
