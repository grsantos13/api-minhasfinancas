package io.github.grsantos13.minhasfinancas.service;

import io.github.grsantos13.minhasfinancas.model.entity.Usuario;

import java.util.Optional;

public interface UsuarioService {

    Usuario autenticar(String email, String senha);

    Usuario cadastrarUsuario(Usuario usuario);

    Optional<Usuario> getById(Long id);

    void validarEmail(String email);
}
