package io.github.grsantos13.minhasfinancas.service.impl;

import io.github.grsantos13.minhasfinancas.exception.ErroDeAutenticacao;
import io.github.grsantos13.minhasfinancas.exception.RegraNegocioException;
import io.github.grsantos13.minhasfinancas.model.entity.Usuario;
import io.github.grsantos13.minhasfinancas.model.repository.UsuarioRepository;
import io.github.grsantos13.minhasfinancas.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new ErroDeAutenticacao("Usuário não encontrado."));

        if (!usuario.getSenha().equals(senha)){
            throw new ErroDeAutenticacao("Senha incorreta.");
        }

        return usuario;
    }

    @Override
    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        if (usuarioRepository.existsByEmail(email)){
            throw new RegraNegocioException("Já existe um usuário cadastrado com esse e-mail.");
        }
    }

    @Override
    public Optional<Usuario> getById(Long id) {
        return usuarioRepository.findById(id);
    }
}
