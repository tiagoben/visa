package bento.tiago.visa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Principal {
	private static StringBuilder resposta = new StringBuilder();

	public void iniciar() {
		Logger.log("Iniciando execução");

		File pasta = abrirArquivo();
		if (pasta != null) {
			File[] arquivos = pasta.listFiles(new FiltroTxt());
			for (File arquivo : arquivos) {
				Logger.log("\nArquivo encontrado: " + arquivo.getAbsolutePath());

				Importador importador = new Importador();
				Fatura fatura = importador.importar(arquivo);
				if (fatura != null) {
					Preparador.preparar(fatura);
					String ofc = CriadorOFC.gerarConteudoOFC(fatura);
					File arquivoSaida = getArquivoSaida(fatura, pasta);
					gravarArquivo(ofc, arquivoSaida);
					resposta.append("Entrada: " + arquivo.getName() + "\n");
					resposta.append("Saída: " + arquivoSaida.getName() + "\n\n");
					Logger.log("Arquivo \"" + arquivoSaida.getName()
							+ "\" gerado");
				} else {
					Logger.log("O arquivo não pôde ser convertido para OFC");
				}
			}
		} else {
			Logger.log("Nenhuma pasta selecionada");
		}
		Logger.log(getResposta());
		exibirResposta();
	}

	private File abrirArquivo() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(1);

		int returnVal = fc.showOpenDialog(null);
		if (returnVal == 0) {
			return fc.getSelectedFile();
		}
		return null;
	}

	private File getArquivoSaida(Fatura fatura, File pasta) {
		int i = 0;
		File arquivo = null;
		do {
			String nome = getNomeArquivo(fatura, i);
			arquivo = new File(pasta.getAbsolutePath() + "/" + nome);
			i++;
		} while (arquivo.isFile());
		return arquivo;
	}

	private String getNomeArquivo(Fatura fatura, int i) {
		String nome;
		if (fatura.isEmAberto()) {
			if (i == 0) {
				nome = "fatura_em_aberto.ofc";
			} else {
				nome = "fatura_em_aberto (" + i + ").ofc";
			}
		} else {
			if (i == 0) {
				nome = "fatura" + fatura.getVencimento().toString("_yyyy_MM")
						+ ".ofc";
			} else {
				nome = "fatura" + fatura.getVencimento().toString("_yyyy_MM (")
						+ i + ").ofc";
			}
		}
		return nome;
	}

	private void gravarArquivo(String conteudo, File arquivo) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo));
			bw.write(conteudo);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exibirResposta() {
		JOptionPane.showMessageDialog(null, getResposta());
	}

	private String getResposta() {
		String message = "Fim da execução:\n";

		String logMsg = resposta.toString();
		if (logMsg.isEmpty()) {
			message = message
					+ "Nenhum arquivo válido encontrado para processamento";
		} else {
			message = message + logMsg;
		}
		return message;
	}
}
