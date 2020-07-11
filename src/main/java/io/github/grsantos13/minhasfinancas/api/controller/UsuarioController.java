package io.github.grsantos13.minhasfinancas.api.controller;

import io.github.grsantos13.minhasfinancas.api.dto.UsuarioAutenticarDTO;
import io.github.grsantos13.minhasfinancas.api.dto.UsuarioDTO;
import io.github.grsantos13.minhasfinancas.exception.ErroDeAutenticacao;
import io.github.grsantos13.minhasfinancas.exception.RegraNegocioException;
import io.github.grsantos13.minhasfinancas.model.entity.Usuario;
import io.github.grsantos13.minhasfinancas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private UsuarioService service;

    public UsuarioController(UsuarioService service){
        this.service = service;
    }

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
}
