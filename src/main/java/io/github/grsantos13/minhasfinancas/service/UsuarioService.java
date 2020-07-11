package io.github.grsantos13.minhasfinancas.service;

import io.github.grsantos13.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {

    Usuario autenticar(String email, String senha);

    Usuario cadastrarUsuario(Usuario usuario);

    void validarEmail(String email);
}
