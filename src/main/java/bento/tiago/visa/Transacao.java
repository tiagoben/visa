package bento.tiago.visa;

import java.math.BigDecimal;
import org.joda.time.LocalDate;

public class Transacao {
	private LocalDate data;
	private String descricao;
	private BigDecimal valor;
	private BigDecimal valorConvertido;
	private boolean dolar;
	private boolean parcelado;
	private int parcelaAtual;
	private int qtdParcelas;
	private LocalDate dataParcela;

	public BigDecimal getValorConvertido() {
		return this.valorConvertido;
	}

	public void setValorConvertido(BigDecimal valorConvertido) {
		this.valorConvertido = valorConvertido;
	}

	public LocalDate getData() {
		return this.data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getValor() {
		return this.valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public boolean isDolar() {
		return this.dolar;
	}

	public void setDolar(boolean dolar) {
		this.dolar = dolar;
	}

	public boolean isParcelado() {
		return this.parcelado;
	}

	public void setParcelado(boolean parcelado) {
		this.parcelado = parcelado;
	}

	public int getParcelaAtual() {
		return this.parcelaAtual;
	}

	public void setParcelaAtual(int parcelaAtual) {
		this.parcelaAtual = parcelaAtual;
	}

	public int getQtdParcelas() {
		return this.qtdParcelas;
	}

	public void setQtdParcelas(int qtdParcelas) {
		this.qtdParcelas = qtdParcelas;
	}

	public LocalDate getDataParcela() {
		return this.dataParcela;
	}

	public void setDataParcela(LocalDate dataParcela) {
		this.dataParcela = dataParcela;
	}
}
