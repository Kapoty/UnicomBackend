package br.net.unicom.backend.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.net.unicom.backend.model.enums.RegistroJornadaCorrecaoObservacaoEnum;
import br.net.unicom.backend.model.key.RegistroJornadaCorrecaoKey;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class RegistroJornadaCorrecao {

    @EmbeddedId
    RegistroJornadaCorrecaoKey registroJornadaCorrecaoKey;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    @ToString.Exclude
    Usuario usuario;

    public RegistroJornadaCorrecao(Usuario usuario, LocalDate data) {
        this.usuario = usuario;
        this.registroJornadaCorrecaoKey = new RegistroJornadaCorrecaoKey(usuario.getUsuarioId(), data);
    }

    @Column(name = "contrato_id")
    private Integer contratoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", insertable = false, updatable = false, nullable = true)
    @JsonIgnore
    @ToString.Exclude
    private Contrato contrato;

    private LocalTime jornadaEntrada;

    private LocalTime jornadaIntervaloInicio;

    private LocalTime jornadaIntervaloFim;

    private LocalTime jornadaSaida;

    @NotNull
    @Length(max = 300)
    private String justificativa;

    @NotNull
    private Integer horasTrabalhadas;

    private LocalTime entrada;
    
    private LocalTime saida;

    @NotNull
    private Boolean horaExtraPermitida;

    @Enumerated(EnumType.STRING)
    private RegistroJornadaCorrecaoObservacaoEnum observacao;

    @NotNull
    private Integer ajusteHoraExtra;

    @NotNull
    private Boolean aprovada;

}
