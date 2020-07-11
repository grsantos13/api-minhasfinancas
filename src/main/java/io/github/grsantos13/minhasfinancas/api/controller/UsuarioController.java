package io.github.grsantos13.minhasfinancas.api.controller;

import io.github.grsantos13.minhasfinancas.api.dto.UsuarioAutenticarDTO;
import io.github.grsantos13.minhasfinancas.api.dto.UsuarioDTO;
import io.github.grsantos13.minhasfinancas.exception.ErroDeAutenticacao;
import io.github.grsantos13.minhasfinancas.exception.RegraNegocioException;
import io.github.grsantos13.minhasfinancas.model.entity.Usuario;
import io.github.grsantos13.minhasfinancas.service.LancamentoService;
import io.github.grsantos13.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final LancamentoService lancamentoService;

    @PostMapping
    public ResponseEntity cadastrarUsuario(@RequestBody UsuarioDTO usuario){
        Usuario usuarioEntity = Usuario.builder()
                            .nome(usuario.getNome())
                            .email(usuario.getEmail())
                            .senha(usuario.getSenha())
                            .build();

        try{
            Usuario usuarioCadastrado = service.cadastrarUsuario(usuarioEntity);
            return new ResponseEntity(usuarioCadastrado, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity autenticar(@RequestBody UsuarioAutenticarDTO autenticar){
        try {
            Usuario usuarioAutenticado = service.autenticar(autenticar.getEmail(), autenticar.getSenha());
            return ResponseEntity.ok(usuarioAutenticado);
        }catch (ErroDeAutenticacao e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity consultarSaldo(@PathVariable Long id){
        Optional<Usuario> usuario = service.getById(id);

        if (!usuario.isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }
}
