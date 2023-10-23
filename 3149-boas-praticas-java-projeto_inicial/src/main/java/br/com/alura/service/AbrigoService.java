package br.com.alura.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class AbrigoService {

    public void listarAbrigo() throws IOException, InterruptedException {

        HttpResponse<String> response = dispararRequest("/abrigos", "GET" ,null);
        String responseBody = response.body();
        JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();
        System.out.println("Abrigos cadastrados:");
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            long id = jsonObject.get("id").getAsLong();
            String nome = jsonObject.get("nome").getAsString();
            System.out.println(id +" - " +nome);
        }
    }

    public void cadastrarAbrigo() throws IOException, InterruptedException {
        System.out.println("Digite o nome do abrigo:");
        String nome = new Scanner(System.in).nextLine();
        System.out.println("Digite o telefone do abrigo:");
        String telefone = new Scanner(System.in).nextLine();
        System.out.println("Digite o email do abrigo:");
        String email = new Scanner(System.in).nextLine();

        JsonObject json = new JsonObject();
        json.addProperty("nome", nome);
        json.addProperty("telefone", telefone);
        json.addProperty("email", email);

        HttpResponse<String> response = dispararRequest("/abrigos", "POST" ,json);

        int statusCode = response.statusCode();
        String responseBody = response.body();
        if (statusCode == 200) {
            System.out.println("Abrigo cadastrado com sucesso!");
            System.out.println(responseBody);
        } else if (statusCode == 400 || statusCode == 500) {
            System.out.println("Erro ao cadastrar o abrigo:");
            System.out.println(responseBody);
        }
    }

    private static HttpResponse<String> dispararRequest(String path, String method, JsonObject json) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String uri = "http://localhost:8080";
        HttpRequest request = null;

        if (method.equals("GET")) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(uri + path))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
        } else if (method.equals("POST")) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(json == null ? "" : json.toString()))
                    .build();
        }

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        if (statusCode == 404) {
            System.out.println("Não encontrado");
        } else if (statusCode == 400 || statusCode == 500) {
            System.out.println("Erro na requisição");
            System.out.println(response);
        }

        return response;
    }
}
