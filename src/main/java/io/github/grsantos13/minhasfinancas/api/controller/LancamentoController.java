package io.github.grsantos13.minhasfinancas.api.controller;

import io.github.grsantos13.minhasfinancas.api.dto.AtualizaStatusDTO;
import io.github.grsantos13.minhasfinancas.api.dto.LancamentoDTO;
import io.github.grsantos13.minhasfinancas.exception.RegraNegocioException;
import io.github.grsantos13.minhasfinancas.model.entity.Lancamento;
import io.github.grsantos13.minhasfinancas.model.entity.Usuario;
import io.github.grsantos13.minhasfinancas.model.enums.StatusLancamento;
import io.github.grsantos13.minhasfinancas.model.enums.TipoLancamento;
import io.github.grsantos13.minhasfinancas.service.LancamentoService;
import io.github.grsantos13.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity salvar (@RequestBody LancamentoDTO lancamentoDTO){
        try {
            Lancamento lancamento = converter(lancamentoDTO);
            lancamento = service.salvar(lancamento);
            return new ResponseEntity(lancamento, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity atualizar (@PathVariable Long id, @RequestBody LancamentoDTO lancamentoDTO){
        return service.getById(id)
                    .map(entity -> {
                        try {
                            Lancamento lancamento = converter(lancamentoDTO);
                            lancamento.setId(entity.getId());
                            lancamento = service.atualizar(lancamento);
                            return ResponseEntity.ok(lancamento);
                        }catch (RegraNegocioException e){
                            return ResponseEntity.badRequest().body(e.getMessage());
                        }
                    }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado.", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletar(@PathVariable Long id){
        return service.getById(id)
                    .map(entity -> {
                        service.deletar(entity);
                        return new ResponseEntity(HttpStatus.NO_CONTENT);
                    }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado.", HttpStatus.BAD_REQUEST)) ;
    }

    @GetMapping
    public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao,
                                 @RequestParam(value = "mes", required = false) Integer mes,
                                 @RequestParam(value = "ano", required = false) Integer ano,
                                 @RequestParam("usuario") Long usuarioId){

        Optional<Usuario> usuario = usuarioService.getById(usuarioId);
        if (!usuario.isPresent()){
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta, usuário não encontrado.");
        }

        Lancamento lancamentoFiltro = new Lancamento();
            lancamentoFiltro.setDescricao(descricao);
            lancamentoFiltro.setMes(mes);
            lancamentoFiltro.setAno(ano);
            lancamentoFiltro.setUsuario(usuario.get());

        List<Lancamento> lancamentoList = service.buscar(lancamentoFiltro);

        return ResponseEntity.ok(lancamentoList);
    }

    @PutMapping("/{id}/atualizar-status")
    public ResponseEntity atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusDTO status){
        return service.getById(id)
                .map(entity -> {
                        StatusLancamento statusLancamento = StatusLancamento.valueOf(status.getStatus());
                        if (statusLancamento == null) {
                            return ResponseEntity.badRequest().body("Status não encontrado.");
                        }

                        entity.setStatus(statusLancamento);

                        try {
                            service.atualizar(entity);
                            return ResponseEntity.ok(entity);
                        }catch (RegraNegocioException e){
                            return ResponseEntity.badRequest().body(e.getMessage());
                        }
                }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado.", HttpStatus.BAD_REQUEST));
    }


    private Lancamento converter(LancamentoDTO dto){
        Usuario usuario = usuarioService.getById(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o id informado."));
        Lancamento lancamento = new Lancamento();
            lancamento.setId(dto.getId());
            lancamento.setDescricao(dto.getDescricao());
            lancamento.setAno(dto.getAno());
            lancamento.setMes(dto.getMes());
            lancamento.setUsuario(usuario);
            if (dto.getTipo() != null) {
                lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
            }
            if (dto.getStatus() != null) {
                lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
            }

        return lancamento;
    }
}
