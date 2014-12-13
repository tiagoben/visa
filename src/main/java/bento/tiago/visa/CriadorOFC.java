package bento.tiago.visa;

import java.math.BigDecimal;
import org.joda.time.LocalDate;

public class CriadorOFC {
	public static String gerarConteudoOFC(Fatura fatura) {
		StringBuilder sb = new StringBuilder();
		sb.append("<OFC>");
		sb.append("\n");
		sb.append("<ACCTSTMT>");
		sb.append("\n");
		sb.append("<STMTRS>");
		sb.append("\n");
		for (Transacao transacao : fatura.getTransacoes()) {
			String trntype = getTrntype(transacao);
			String dtposted = getDtposted(transacao);
			String trnamt = getTrnamt(transacao);
			String memo = getMemo(transacao);

			sb.append("<STMTTRN>");
			sb.append("\n");

			sb.append("<TRNTYPE>" + trntype + "</TRNTYPE>");
			sb.append("\n");
			sb.append("<DTPOSTED>" + dtposted + "</DTPOSTED>");
			sb.append("\n");
			sb.append("<TRNAMT>" + trnamt + "</TRNAMT>");
			sb.append("\n");
			sb.append("<MEMO>" + memo + "</MEMO>");
			sb.append("\n");

			sb.append("</STMTTRN>");
			sb.append("\n");
		}
		sb.append("</STMTRS>");
		sb.append("\n");
		sb.append("</ACCTSTMT>");
		sb.append("\n");
		sb.append("</OFC>");
		return sb.toString();
	}

	private static String getTrntype(Transacao transacao) {
		if (transacao.getValor().compareTo(BigDecimal.ZERO) < 0) {
			return "0";
		}
		return "1";
	}

	private static String getDtposted(Transacao transacao) {
		LocalDate data = transacao.getData();
		if (transacao.isParcelado()) {
			data = transacao.getDataParcela();
		}
		return data.toString("yyyyMMdd");
	}

	private static String getTrnamt(Transacao transacao) {
		BigDecimal valor = transacao.getValor().negate();
		if (transacao.isDolar()) {
			valor = transacao.getValorConvertido().negate();
		}
		return valor.toString();
	}

	private static String getMemo(Transacao transacao) {
		return transacao.getDescricao();
	}
}
