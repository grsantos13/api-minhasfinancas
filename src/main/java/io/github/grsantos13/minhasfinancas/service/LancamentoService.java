package io.github.grsantos13.minhasfinancas.service;

import io.github.grsantos13.minhasfinancas.model.entity.Lancamento;
import io.github.grsantos13.minhasfinancas.model.enums.StatusLancamento;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LancamentoService {
    Lancamento salvar(Lancamento lancamento);

    Lancamento atualizar(Lancamento lancamento);

    void deletar(Lancamento lancamento);

    List<Lancamento> buscar(Lancamento filtro);

    void atualizarStatus(Lancamento lancamento, StatusLancamento status);

    void validar(Lancamento lancamento);

    Optional<Lancamento> getById(Long id);

    BigDecimal obterSaldoPorUsuario(Long id);
}
