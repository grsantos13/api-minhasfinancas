package io.github.grsantos13.minhasfinancas.model.repository;

import io.github.grsantos13.minhasfinancas.model.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveVerificarAExistenciaDeUmEmail(){
        Usuario usuario = createNewUser();
        entityManager.persist(usuario);

        boolean exists = usuarioRepository.existsByEmail(usuario.getEmail());

        assertThat(exists).isTrue();
    }

    @Test
    public void deveRetornarFalsoAoVerificarAExistenciaDeUmEmail(){
        boolean exists = usuarioRepository.existsByEmail("gustavo@domain.com");

        assertThat(exists).isFalse();
    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados(){
        Usuario usuario = createNewUser();

        Usuario savedUser = usuarioRepository.save(usuario);

        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void deveBuscarUmUsuarioPorEmail(){
        Usuario user = createNewUser();
        entityManager.persist(user);

        Optional<Usuario> usuario = usuarioRepository.findByEmail(user.getEmail());

        assertThat(usuario).isNotEmpty();

    }

    @Test
    public void deveRetornarVazioAoBuscarUmUsuarioPorEmailQuandoNaoExisteNaBase(){
        Optional<Usuario> usuario = usuarioRepository.findByEmail("gustavo@domain.com");

        assertThat(usuario).isEmpty();

    }

    public static Usuario createNewUser() {
        return Usuario.builder()
                .nome("Gustavo")
                .email("gustavo@domain.com")
                .senha("123")
                .build();
    }
}
