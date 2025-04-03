package com.tradesim;
import okhttp3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class MarketDataService {
    private static final String API_KEY = "cvmrdh9r01ql90pvq0ngcvmrdh9r01ql90pvq0o0";
    private static final String BASE_URL = "https://finnhub.io/api/v1";
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public MarketDataService() {
        client = new OkHttpClient();
        mapper = new ObjectMapper();
    }

    public double getCurrentPrice(String symbol) throws IOException {
        String url = String.format("%s/quote?symbol=%s&token=%s", BASE_URL, symbol, API_KEY);

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            JsonNode json = mapper.readTree(response.body().string());
            return json.get("c").asDouble();  // "c" = current price
        }
    }
}
