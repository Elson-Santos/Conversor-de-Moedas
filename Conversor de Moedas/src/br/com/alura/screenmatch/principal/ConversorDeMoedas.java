package br.com.alura.screenmatch.principal;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;

// Classe que representa a taxa de câmbio
class TaxaCambio {
    @SerializedName("result")
    private String result;

    @SerializedName("base_code")
    private String baseCode;

    @SerializedName("conversion_rates")
    private Map<String, Double> conversionRates;

    public Map<String, Double> getConversionRates() {
        return conversionRates;
    }
}

public class ConversorDeMoedas {
    public static void main(String[] args) {
        try (Scanner leitura = new Scanner(System.in)) {
            boolean continuar = true;

            while (continuar) {
                System.out.println("Deseja fazer uma conversão de moedas? (sim/não)");
                String resposta = leitura.nextLine();

                if (resposta.equalsIgnoreCase("sim")) {
                    // Menu com 5 opções de moedas
                    String menu = "Escolha uma moeda de destino para converter:\n" +
                            "1. OMR (Rial de Omã)\n" +
                            "2. EUR (Euro)\n" +
                            "3. USD (Dólar americano)\n" +
                            "4. CHF (Franco Suíço)\n" +
                            "5. KWD (Dinar do Kuwait)\n" +
                            "6. ZAR (Rand)\n" +
                            "Digite o número da moeda desejada: ";

                    System.out.println(menu);
                    int opcao = leitura.nextInt();
                    leitura.nextLine(); // Consumir a quebra de linha

                    String moedaDestino = "";
                    switch (opcao) {
                        case 1:
                            moedaDestino = "OMR";
                            break;
                        case 2:
                            moedaDestino = "EUR";
                            break;
                        case 3:
                            moedaDestino = "USD";
                            break;
                        case 4:
                            moedaDestino = "CHF";
                            break;
                        case 5:
                            moedaDestino = "KWD";
                            break;
                        case 6:
                            moedaDestino = "ZAR";
                            break;
                        default:
                            System.out.println("Opção inválida!");
                            continue; // Volta para o início do loop se a opção for inválida
                    }

                    System.out.println("Digite o valor a ser convertido: ");
                    double valor = leitura.nextDouble();
                    leitura.nextLine(); // Consumir a quebra de linha após o valor

                    try {
                        double taxa = obterTaxaDeCambio(moedaDestino);
                        double valorConvertido = valor * taxa;
                        System.out.println(valor + " BRL é equivalente a " + valorConvertido + " " + moedaDestino);
                    } catch (IOException | InterruptedException e) {
                        System.out.println("Erro na conversão de moedas: " + e.getMessage());
                    }
                } else {
                    continuar = false; // Sai do loop se a resposta for diferente de "sim"
                }

                // Pergunta se deseja realizar outra conversão
                if (continuar) {
                    System.out.println("Deseja realizar outra conversão? (sim/não)");
                    String novaConversao = leitura.nextLine();
                    if (!novaConversao.equalsIgnoreCase("sim")) {
                        continuar = false;
                    }
                }
            }

            System.out.println("Encerrando o conversor de moedas.");
        }
    }

    public static double obterTaxaDeCambio(String moedaDestino) throws IOException, InterruptedException {
        String endereco = "https://v6.exchangerate-api.com/v6/3467ffd4b0a125c6dc0d516a/latest/BRL";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endereco))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verificando o código de resposta
        if (response.statusCode() != 200) {
            throw new IOException("Erro ao obter taxa de câmbio: " + response.statusCode());
        }

        // Fazendo o parsing do JSON
        Gson gson = new Gson();
        TaxaCambio taxaCambio = gson.fromJson(response.body(), TaxaCambio.class);

        // Verificando se as taxas foram carregadas corretamente
        if (taxaCambio.getConversionRates() == null) {
            throw new IOException("As taxas de câmbio não foram encontradas na resposta.");
        }

        // Retorna a taxa para a moeda desejada
        Double taxa = taxaCambio.getConversionRates().get(moedaDestino);
        if (taxa == null) {
            throw new IOException("Moeda de destino não encontrada.");
        }

        return taxa;
    }
}
