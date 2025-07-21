package data_generation.data_generation.DataGeneration.config;

import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;

@Component
public class EnvConfig {
    
    private final Dotenv dotenv = Dotenv.configure().load();

    public String getPixabayKey() {
        return dotenv.get("PIXABAY_KEY");
    }
}
