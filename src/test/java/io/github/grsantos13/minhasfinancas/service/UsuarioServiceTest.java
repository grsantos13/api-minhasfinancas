package io.github.grsantos13.minhasfinancas.service;

import io.github.grsantos13.minhasfinancas.exception.ErroDeAutenticacao;
import io.github.grsantos13.minhasfinancas.exception.RegraNegocioException;
import io.github.grsantos13.minhasfinancas.model.entity.Usuario;
import io.github.grsantos13.minhasfinancas.model.repository.UsuarioRepository;
import io.github.grsantos13.minhasfinancas.model.repository.UsuarioRepositoryTest;
import io.github.grsantos13.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl usuarioService;

    @MockBean
    UsuarioRepository usuarioRepository;

    @Test
    public void deveValidarEmail(){
        Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        assertDoesNotThrow(() -> usuarioService.validarEmail("gustavo@domain.com"));
    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoHouverMesmoEmailCadastrado(){
        Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
        assertThrows(RegraNegocioException.class, () -> usuarioService.validarEmail("gustavo@domain.com"));
    }

    @Test
    public void deveAutenticarComSucesso(){
        String email = "gustavo@domain.com";
        String senha = "123";

        Usuario usuario = UsuarioRepositoryTest.createNewUser();
            usuario.setId(1L);

        Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.autenticar(email, senha);

        assertThat(result).isNotNull();
        assertDoesNotThrow(() -> usuarioService.autenticar(email, senha));
    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastrado(){
        Mockito.when(usuarioRepository.findByEmail("gustavo@domain.com")).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> usuarioService.autenticar("gustavo@domain.com", "123"));

        assertThat(throwable).isInstanceOf(ErroDeAutenticacao.class)
                    .hasMessage("Usuário não encontrado.");

    }

    @Test
    public void deveLancarErroQuandoSenhaEstiverIncorreta(){
        String email = "gustavo@domain.com";
        String senha = "123";
        Usuario usuario = UsuarioRepositoryTest.createNewUser();
            usuario.setId(1L);
        Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        Throwable throwable = catchThrowable(() -> usuarioService.autenticar(email, "321"));

        assertThat(throwable).isInstanceOf(ErroDeAutenticacao.class)
                    .hasMessage("Senha incorreta.");
    }

    @Test
    public void deveSalvarUsuario(){
        Usuario savedUser = UsuarioRepositoryTest.createNewUser();
            savedUser.setId(1L);
        Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(savedUser);

        Usuario result = usuarioService.cadastrarUsuario(UsuarioRepositoryTest.createNewUser());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
    }

    public void deveOcorrerErroAoSalvarUsuarioPorJaHaverEmailCadastrado(){
        Usuario usuario = UsuarioRepositoryTest.createNewUser();
        Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(usuario.getEmail());

        Throwable throwable = catchThrowable(() -> usuarioService.cadastrarUsuario(usuario));

        assertThat(throwable).isInstanceOf(RegraNegocioException.class);
        verify(usuarioRepository, never()).save(usuario);

    }
}
