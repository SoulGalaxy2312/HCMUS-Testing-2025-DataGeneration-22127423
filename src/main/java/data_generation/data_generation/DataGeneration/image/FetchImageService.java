package data_generation.data_generation.DataGeneration.image;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.*;
import org.springframework.stereotype.Service;

import data_generation.data_generation.DataGeneration.config.EnvConfig;

@Service
public class FetchImageService {

    private final String PLACEHOLDER_IMG = "https://via.placeholder.com/400x300?text=";
    private final Set<String> usedImageUrls = new HashSet<>();
    private final String[] SUFFIXES = {"tool", "equipment", "machine", "device" };
    private final Random random = new Random();

    private final EnvConfig envConfig;
    private final String PIXABAY_KEY;

    public FetchImageService(EnvConfig envConfig) {
        this.envConfig = envConfig;
        this.PIXABAY_KEY = this.envConfig.getPixabayKey();
    } 

    public String[] fetchImage(String tag) {
        String query = tag + " " + getRandomSuffix();
        List<String[]> candidates = fetchPixabayImages(query, 12);

        Collections.shuffle(candidates);

        for (String[] img : candidates) {
            String imgUrl = img[0];
            if (!usedImageUrls.contains(imgUrl)) {
                usedImageUrls.add(imgUrl);
                return img;
            }
        }

        // fallback: use placeholder
        return new String[]{
            PLACEHOLDER_IMG + URLEncoder.encode(tag, StandardCharsets.UTF_8),
            ""
        };
    }

    private List<String[]> fetchPixabayImages(String query, int perPage) {
        List<String[]> result = new ArrayList<>();
        if (PIXABAY_KEY == null || PIXABAY_KEY.isEmpty()) return result;

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String apiUrl = String.format("https://pixabay.com/api/?key=%s&q=%s&image_type=photo&per_page=%d",
                                          PIXABAY_KEY, encodedQuery, perPage);

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) return result;

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) json.append(line);
            in.close();

            JSONObject obj = new JSONObject(json.toString());
            JSONArray hits = obj.optJSONArray("hits");

            if (hits != null) {
                for (int i = 0; i < hits.length(); i++) {
                    JSONObject hit = hits.getJSONObject(i);
                    result.add(new String[]{
                        hit.getString("webformatURL"),
                        hit.getString("pageURL")
                    });
                }
            }

        } catch (Exception e) {
            System.err.println("Pixabay fetch error: " + e.getMessage());
        }

        return result;
    }

    private String getRandomSuffix() {
        return SUFFIXES[random.nextInt(SUFFIXES.length)];
    }
}
