package bento.tiago.visa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Importador {
	private int dolarCnt = 0;
	private boolean buscandoDolar = false;

	public Fatura importar(File arquivo) {
		this.dolarCnt = 0;
		this.buscandoDolar = false;
		Fatura fatura = new Fatura();

		BufferedReader br = null;
		Status status = Status.VERIFICAR_VALIDADE;
		boolean emProcessamento = true;
		try {
			br = new BufferedReader(new FileReader(arquivo));
			String linha = null;
			do {
				if (StringUtils.isNotBlank(linha)) {
					switch (status) {
					case ARQUIVO_INVALIDO:
						status = verificarValidade(linha);
						break;
					case BUSCAR_DOLAR:
						return null;
					case BUSCAR_TOTAIS:
						status = buscarVencimento(fatura, linha);
						break;
					case BUSCAR_TOTAL_FATURA:
						status = buscarTotalFatura(fatura, linha);
						break;
					case BUSCAR_TRANSACOES:
						status = buscarTransacoes(fatura, linha);
						break;
					case BUSCAR_VENCIMENTO:
						status = buscarTotais(fatura, linha);
						break;
					case FIM_IMPORTACAO:
						status = buscarDolar(fatura, linha);
						break;
					case VERIFICAR_VALIDADE:
						emProcessamento = false;
					}
				}
				if (!emProcessamento) {
					break;
				}
			} while ((linha = br.readLine()) != null);
		} catch (Exception e) {
			e.printStackTrace();
			if (br != null) {
				try {
					br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fatura;
	}

	private Status verificarValidade(String linha) {
		if (linha.indexOf("SISBB - Sistema de Informa") >= 0) {
			return Status.BUSCAR_VENCIMENTO;
		}
		return Status.ARQUIVO_INVALIDO;
	}

	private Status buscarVencimento(Fatura fatura, String linha) {
		if (linha.indexOf("Vencimento") >= 0) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern("dd.MM.yyyy");
			String dataStr = linha.substring(18, 28);
			LocalDate data = dtf.parseLocalDate(dataStr);
			fatura.setVencimento(data);
			fatura.setEmAberto(false);
			return Status.BUSCAR_TOTAL_FATURA;
		}
		if (linha.indexOf("DEMONSTRATIVO") >= 0) {
			fatura.setEmAberto(true);
			return Status.BUSCAR_TRANSACOES;
		}
		return Status.BUSCAR_VENCIMENTO;
	}

	private Status buscarTotalFatura(Fatura fatura, String linha) {
		if (linha.indexOf("Total da fatura") >= 0) {
			BigDecimal valor = getValor(linha, 21);
			fatura.setTotalFatura(valor);
			return Status.BUSCAR_TRANSACOES;
		}
		return Status.BUSCAR_TOTAL_FATURA;
	}

	private Status buscarTransacoes(Fatura fatura, String linha) {
		if (linha.toLowerCase().indexOf("subtotal") >= 0) {
			return Status.BUSCAR_TOTAIS;
		}
		Transacao transacao = getTransacao(linha);
		if (transacao != null) {
			fatura.addTransacao(transacao);
		}
		return Status.BUSCAR_TRANSACOES;
	}

	private Status buscarTotais(Fatura fatura, String linha) {
		if (linha.indexOf("Total") >= 0) {
			BigDecimal totalReais = getValor(linha, 50, 68);
			BigDecimal totalDolar = getValor(linha, 68);

			fatura.setTotalReal(totalReais);
			fatura.setTotalDolar(totalDolar);
			if (fatura.isEmAberto()) {
				return Status.FIM_IMPORTACAO;
			}
			return Status.BUSCAR_DOLAR;
		}
		return Status.BUSCAR_TOTAIS;
	}

	private Status buscarDolar(Fatura fatura, String linha) {
		if (linha.indexOf("RESUMO EM D") >= 0) {
			this.buscandoDolar = true;
		}
		if (this.buscandoDolar) {
			this.dolarCnt += 1;
		}
		if (this.dolarCnt >= 6) {
			this.buscandoDolar = false;

			StringTokenizer st = new StringTokenizer(linha);
			int i = 0;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (i == 8) {
					BigDecimal cotacao = getValor(token, 0);
					fatura.setCotacaoDolar(cotacao);
				}
				if (i == 10) {
					BigDecimal totalDolarConvertido = getValor(token, 0);
					fatura.setTotalDolarConvertido(totalDolarConvertido);
				}
				i++;
			}
			return Status.FIM_IMPORTACAO;
		}
		return Status.BUSCAR_DOLAR;
	}

	private BigDecimal getValor(String linha, int indexInicio, int indexFim) {
		String valorStr = linha.substring(indexInicio, indexFim).trim();
		valorStr = valorStr.replaceAll("\\.", "").replace(',', '.');
		BigDecimal valor = new BigDecimal(valorStr);

		return valor;
	}

	private BigDecimal getValor(String linha, int indexInicio) {
		String valorStr = linha.substring(indexInicio).trim();
		valorStr = valorStr.replaceAll("\\.", "").replace(',', '.');
		BigDecimal valor = new BigDecimal(valorStr);

		return valor;
	}

	private Transacao getTransacao(String linha) {
		Transacao transacao = new Transacao();
		if (linha.length() < 80) {
			return null;
		}
		String dataStr = linha.substring(0, 8).trim();

		String pattern = "dd/MM/yy";

		boolean dataStrCurta = dataStr.length() < 8;
		if (dataStrCurta) {
			pattern = "dd/MM";
		} else {
			pattern = "dd/MM/yy";
		}
		DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
		try {
			LocalDate data = dtf.parseLocalDate(dataStr);
			if (dataStrCurta) {
				data = setAno(data);
			}
			transacao.setData(data);
		} catch (Exception e) {
			return null;
		}
		String descricao = linha.substring(9, 51).trim()
				.replaceAll("\\s+", " ");
		transacao.setDescricao(descricao);

		BigDecimal valorReal = getValor(linha, 51, 68);
		BigDecimal valorDolar = getValor(linha, 68);
		if (valorDolar.compareTo(BigDecimal.ZERO) > 0) {
			transacao.setValor(valorDolar);
			transacao.setDolar(true);
		} else {
			transacao.setValor(valorReal);
			transacao.setDolar(false);
		}
		return transacao;
	}

	private LocalDate setAno(LocalDate data) {
		LocalDate dataAtual = new LocalDate();
		int mesAtual = dataAtual.getMonthOfYear();
		int anoAtual = dataAtual.getYear();

		LocalDate novaData = data.withYear(anoAtual);
		if (novaData.getMonthOfYear() > mesAtual) {
			novaData = novaData.minusYears(1);
		}
		return novaData;
	}
}
