package io.github.grsantos13.minhasfinancas.exception;

public class ErroDeAutenticacao extends RuntimeException {
    public ErroDeAutenticacao(String message) {
        super(message);
    }
}
