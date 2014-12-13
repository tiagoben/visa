package bento.tiago.visa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;

public class Fatura {
	private LocalDate vencimento;
	private List<Transacao> transacoes = new ArrayList<Transacao>();
	private BigDecimal cotacaoDolar;
	private BigDecimal totalFatura;
	private BigDecimal totalReal;
	private BigDecimal totalDolar;
	private BigDecimal totalDolarConvertido;
	private boolean emAberto;

	public LocalDate getVencimento() {
		return this.vencimento;
	}

	public void setVencimento(LocalDate vencimento) {
		this.vencimento = vencimento;
	}

	public List<Transacao> getTransacoes() {
		return this.transacoes;
	}

	public void setTransacoes(List<Transacao> transacoes) {
		this.transacoes = transacoes;
	}

	public void addTransacao(Transacao transacao) {
		this.transacoes.add(transacao);
	}

	public BigDecimal getCotacaoDolar() {
		return this.cotacaoDolar;
	}

	public void setCotacaoDolar(BigDecimal cotacaoDolar) {
		this.cotacaoDolar = cotacaoDolar;
	}

	public BigDecimal getTotalFatura() {
		return this.totalFatura;
	}

	public void setTotalFatura(BigDecimal totalFatura) {
		this.totalFatura = totalFatura;
	}

	public BigDecimal getTotalReal() {
		return this.totalReal;
	}

	public void setTotalReal(BigDecimal totalReal) {
		this.totalReal = totalReal;
	}

	public BigDecimal getTotalDolarConvertido() {
		return this.totalDolarConvertido;
	}

	public void setTotalDolarConvertido(BigDecimal totalDolarConvertido) {
		this.totalDolarConvertido = totalDolarConvertido;
	}

	public boolean isEmAberto() {
		return this.emAberto;
	}

	public void setEmAberto(boolean emAberto) {
		this.emAberto = emAberto;
	}

	public BigDecimal getTotalDolar() {
		return this.totalDolar;
	}

	public void setTotalDolar(BigDecimal totalDolar) {
		this.totalDolar = totalDolar;
	}
}
