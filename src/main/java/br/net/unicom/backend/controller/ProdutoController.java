package br.net.unicom.backend.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Produto;
import br.net.unicom.backend.payload.request.ProdutoPatchRequest;
import br.net.unicom.backend.payload.request.ProdutoPostRequest;
import br.net.unicom.backend.repository.ProdutoRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/produto",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class ProdutoController {

    @Autowired
    ProdutoRepository produtoRepository;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @GetMapping("/{produtoId}")
    public ResponseEntity<Produto> getProdutoByProdutoId(@Valid @PathVariable("produtoId") Integer produtoId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.of(produtoRepository.findByProdutoIdAndEmpresaId(produtoId, userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PatchMapping("/{produtoId}")
    @Transactional
    public ResponseEntity<Void> patchByProdutoId(@Valid @PathVariable("produtoId") Integer produtoId, @Valid @RequestBody ProdutoPatchRequest produtoPatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Produto produto = produtoRepository.findByProdutoIdAndEmpresaId(produtoId, userDetails.getEmpresaId()).get();

        modelMapper.map(produtoPatchRequest, produto);

        produtoRepository.saveAndFlush(produto);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<Produto> postProduto(@Valid @RequestBody ProdutoPostRequest produtoPostRequest) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Produto produto = modelMapper.map(produtoPostRequest, Produto.class);

        produto.setEmpresaId(userDetails.getEmpresaId());
        
        produtoRepository.saveAndFlush(produto);

        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @DeleteMapping("/{produtoId}")
    @Transactional
    public ResponseEntity<Void> deleteProduto(@Valid @PathVariable("produtoId") Integer produtoId) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Produto produto = produtoRepository.findByProdutoIdAndEmpresaId(produtoId, userDetails.getEmpresaId()).get();

        produtoRepository.delete(produto);

        return ResponseEntity.noContent().build();
    }

}
