package com.tradesim;

import okhttp3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MarketDataService {
    private static final String API_KEY = System.getenv("FINNHUB_API_KEY");
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
            return json.get("c").asDouble();
        }
    }
}
