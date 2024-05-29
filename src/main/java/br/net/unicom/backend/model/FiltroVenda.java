package br.net.unicom.backend.model;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class FiltroVenda {
   
    @Id
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    @ToString.Exclude
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    VendaTipoProdutoEnum tipoProduto;

    @NotNull
    @Length(max = 50)
    private String pdv;

    private LocalDate safra;

    @Enumerated(EnumType.STRING)
    private FiltroVendaTipoDataEnum tipoData;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    @NotNull
    @Length(max = 200)
    private String statusIdList;

    @NotNull
    @Length(max = 50)
    private String os;

    @NotNull
    @Length(max = 14)
    private String cpf;

    @NotNull
    @Length(max = 200)
    private String nome;

    public FiltroVenda(Usuario usuario) {
        this.setUsuario(usuario);
    }
}
