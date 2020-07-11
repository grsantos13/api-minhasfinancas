package io.github.grsantos13.minhasfinancas.service;


import io.github.grsantos13.minhasfinancas.exception.RegraNegocioException;
import io.github.grsantos13.minhasfinancas.model.entity.Lancamento;
import io.github.grsantos13.minhasfinancas.model.entity.Usuario;
import io.github.grsantos13.minhasfinancas.model.enums.StatusLancamento;
import io.github.grsantos13.minhasfinancas.model.enums.TipoLancamento;
import io.github.grsantos13.minhasfinancas.model.repository.LancamentoRepository;
import io.github.grsantos13.minhasfinancas.model.repository.LancamentoRepositoryTest;
import io.github.grsantos13.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;
    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.salvar(lancamentoASalvar);

        assertThat( lancamento.getId() ).isEqualTo(lancamentoSalvo.getId());
        assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        doThrow( RegraNegocioException.class ).when(service).validar(lancamentoASalvar);

        catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class );
        verify(repository, never()).save(lancamentoASalvar);
    }

    @Test
    public void deveAtualizarUmLancamento() {
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        doNothing().when(service).validar(lancamentoSalvo);

        when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        service.atualizar(lancamentoSalvo);

        verify(repository, times(1)).save(lancamentoSalvo);

    }

    @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        catchThrowableOfType( () -> service.atualizar(lancamento), NullPointerException.class );
        verify(repository, never()).save(lancamento);
    }

    @Test
    public void deveDeletarUmLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        service.deletar(lancamento);

        verify( repository ).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class );

        verify( repository, never() ).delete(lancamento);
    }


    @Test
    public void deveFiltrarLancamentos() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        List<Lancamento> lista = Arrays.asList(lancamento);

        when( repository.findAll(Mockito.any(Example.class)) ).thenReturn(lista);

        List<Lancamento> resultado = service.buscar(lancamento);

        assertThat(resultado)
                .isNotEmpty()
                .hasSize(1)
                .contains(lancamento);

    }

    @Test
    public void deveAtualizarOStatusDeUmLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        StatusLancamento novoStatus = StatusLancamento.REALIZADO;
        Lancamento lancamentoAtualizado = LancamentoRepositoryTest.criarLancamento();
            lancamentoAtualizado.setId(1L);
            lancamentoAtualizado.setStatus(StatusLancamento.REALIZADO);

        doReturn(lancamentoAtualizado).when(service).atualizar(lancamento);

        Lancamento updatedLancamento = service.atualizar(lancamento);

        assertThat(updatedLancamento.getStatus()).isEqualTo(novoStatus);
        verify(service, times(1)).atualizar(lancamento);

    }

    @Test
    public void deveObterUmLancamentoPorID() {
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado =  service.getById(id);

        assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveREtornarVazioQuandoOLancamentoNaoExiste() {
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        when( repository.findById(id) ).thenReturn( Optional.empty() );

        Optional<Lancamento> resultado =  service.getById(id);

        assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void deveLancarErrosAoValidarUmLancamento() {
        Lancamento lancamento = new Lancamento();

        Throwable erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

        lancamento.setDescricao("");

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

        lancamento.setDescricao("Salario");

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

        lancamento.setAno(0);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

        lancamento.setAno(13);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

        lancamento.setMes(1);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");

        lancamento.setAno(202);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");

        lancamento.setAno(2020);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário válido.");

        lancamento.setUsuario(new Usuario());

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário válido.");

        lancamento.getUsuario().setId(1L);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");

        lancamento.setValor(BigDecimal.ZERO);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");

        lancamento.setValor(BigDecimal.valueOf(1));

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");

    }

    @Test
    public void deveObterSaldoPorUsuario() {
        Long idUsuario = 1l;

        when( repository
                .obterSaldoPorTipoLancamentoEUsuario(idUsuario, TipoLancamento.RECEITA, StatusLancamento.REALIZADO))
                .thenReturn(BigDecimal.valueOf(100));

        when( repository
                .obterSaldoPorTipoLancamentoEUsuario(idUsuario, TipoLancamento.DESPESA, StatusLancamento.REALIZADO))
                .thenReturn(BigDecimal.valueOf(50));

        BigDecimal saldo = service.obterSaldoPorUsuario(idUsuario);

        assertThat(saldo).isEqualTo(BigDecimal.valueOf(50));

    }
}
