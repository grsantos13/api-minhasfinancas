package io.github.grsantos13.minhasfinancas.model.repository;

import io.github.grsantos13.minhasfinancas.model.entity.Lancamento;
import io.github.grsantos13.minhasfinancas.model.enums.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query(" select sum(l.valor) from Lancamento l " +
            " join l.usuario u " +
            " where u.id = :idUsuario and l.tipo = :tipo " +
            " group by u")
    BigDecimal obterSaldoPorTipoLancamentoEUsuario(@Param("idUsuario") Long id,
                                                   @Param("tipo") TipoLancamento tipo);
}
