package data_generation.data_generation.DataGeneration.utils;

import org.springframework.stereotype.Component;

@Component
public class SlugUtil {
    public String slugify(String input) {
        return input.toLowerCase()
                    .replaceAll("[^a-z0-9]+", "-")
                    .replaceAll("-+", "-")
                    .replaceAll("^-|-$", "");
    }
}
