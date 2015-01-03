package bento.tiago.visa;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.joda.time.LocalDate;

public class Preparador {
	public static void preparar(Fatura fatura) {
		prepararComprasParceladas(fatura);
		prepararComprasDolar(fatura);
	}

	private static void prepararComprasParceladas(Fatura fatura) {
		List<Transacao> parcelasFuturas = new ArrayList<Transacao>();
		
		for (Transacao transacao : fatura.getTransacoes()) {
			String desc = transacao.getDescricao();
			int indexParc = desc.indexOf(" PARC ");
			if (indexParc > 0) {
				transacao.setParcelado(true);
				int beginIndexParc = indexParc + 6;
				int endIndexParc = indexParc + 8;
				int parcelaAtual = Integer.parseInt(desc.substring(beginIndexParc, endIndexParc));
				int qtdParcelas = Integer.parseInt(desc.substring(indexParc + 9, indexParc + 11));

				transacao.setParcelaAtual(parcelaAtual);
				transacao.setQtdParcelas(qtdParcelas);

				LocalDate dataParcela = transacao.getData().plusMonths(parcelaAtual - 1);

				transacao.setDataParcela(dataParcela);
				if (parcelaAtual == 1) {
					for (int parcelaProjecao = parcelaAtual + 1; parcelaProjecao <= qtdParcelas; parcelaProjecao++) {
						StringBuilder sb = new StringBuilder(desc);
						String parcelaStr = String.format("%02d", new Object[] {Integer.valueOf(parcelaProjecao)});
						sb.replace(beginIndexParc, endIndexParc, parcelaStr);

						Transacao novaTransacao = clonarTransacao(transacao);
						novaTransacao.setDescricao(sb.toString());

						LocalDate dataParcelaProjecao = novaTransacao.getData()
								.plusMonths(parcelaProjecao - 1);
						novaTransacao.setDataParcela(dataParcelaProjecao);
						
						parcelasFuturas.add(novaTransacao);
					}
				}
			}
		}
		
		parcelasFuturas.forEach(novaTransacao -> fatura.addTransacao(novaTransacao));
	}

	private static void prepararComprasDolar(Fatura fatura) {
		boolean emAberto = fatura.isEmAberto();
		BigDecimal cotacao;
		if (emAberto) {
			cotacao = obterCotacao();
		} else {
			cotacao = fatura.getCotacaoDolar();
		}
		Transacao maiorTransacao = null;
		BigDecimal somaConversao = new BigDecimal(0);
		for (Transacao transacao : fatura.getTransacoes()) {
			if (transacao.isDolar()) {
				BigDecimal valorConvertido = transacao.getValor().multiply(
						cotacao);
				valorConvertido = valorConvertido.setScale(2,
						RoundingMode.HALF_UP);
				transacao.setValorConvertido(valorConvertido);
				if (emAberto) {
					String descricao = transacao.getDescricao()
							+ " [previsao - tx " + cotacao + "]";
					transacao.setDescricao(descricao);
				} else {
					somaConversao = somaConversao.add(valorConvertido);
					if ((maiorTransacao == null)
							|| (valorConvertido.compareTo(maiorTransacao
									.getValorConvertido()) > 0)) {
						maiorTransacao = transacao;
					}
				}
			}
		}
		if (!emAberto) {
			BigDecimal diferenca = fatura.getTotalDolarConvertido().subtract(
					somaConversao);
			BigDecimal novoValorDoMaior = maiorTransacao.getValorConvertido()
					.add(diferenca);
			maiorTransacao.setValorConvertido(novoValorDoMaior);
		}
	}

	private static Transacao clonarTransacao(Transacao transacao) {
		Transacao clone = new Transacao();

		clone.setData(transacao.getData());
		clone.setDataParcela(transacao.getDataParcela());
		clone.setDescricao(transacao.getDescricao());
		clone.setDolar(transacao.isDolar());
		clone.setParcelaAtual(transacao.getParcelaAtual());
		clone.setParcelado(transacao.isParcelado());
		clone.setQtdParcelas(transacao.getQtdParcelas());
		clone.setValor(transacao.getValor());

		return clone;
	}

	private static BigDecimal obterCotacao() {
		String cotacaoStr = "2.5";
		try {
			URL url = new URL(
					"http://finance.yahoo.com/d/quotes.csv?e=.csv&f=c4l1&s=USDBRL=X");
			InputStream resposta = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					resposta));
			String linha = br.readLine();

			StringTokenizer tokenizer = new StringTokenizer(linha, ",");
			tokenizer.nextToken();

			cotacaoStr = tokenizer.nextToken();

			br.close();
			resposta.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BigDecimal(cotacaoStr);
	}
}
