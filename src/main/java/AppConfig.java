import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    // Valores usados quando o arquivo não existe, a chave falta ou o valor é inválido
    private static final double DEFAULT_CPU_LIMIT = 80.0;
    private static final double DEFAULT_MEMORY_LIMIT = 85.0;

    private final double cpuLimit;
    private final double memoryLimit;

    // Recebe as propriedades já lidas. Fica separado da leitura do arquivo para dar para testar sem arquivo nenhum.
    public AppConfig(Properties properties) {
        this.cpuLimit = readLimit(properties, "cpu.limit", DEFAULT_CPU_LIMIT);
        this.memoryLimit = readLimit(properties, "memory.limit", DEFAULT_MEMORY_LIMIT);
    }

    // Lê o arquivo de configuração (em src/main/resources) e monta o AppConfig
    public static AppConfig load(String fileName) {
        Properties properties = new Properties();
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in != null) {
                properties.load(in); // preenche o mapa chave/valor com o conteúdo do arquivo
            } else {
                logger.warn("{} não encontrado, usando limites padrão", fileName);
            }
        } catch (IOException e) {
            logger.warn("Erro ao ler {}, usando limites padrão", fileName, e);
        }
        return new AppConfig(properties);
    }

    private double readLimit(Properties properties, String key, double defaultValue) {
        String text = properties.getProperty(key);
        if (text == null) {
            return defaultValue; // chave ausente: usa o padrão
        }
        try {
            double value = Double.parseDouble(text.trim());
            // Um limite é uma porcentagem: só vale de 0 a 100. A forma "!(a && b)" também recusa NaN.
            if (!(value >= 0 && value <= 100)) {
                logger.warn("Valor fora da faixa 0-100 para {}: {}. Usando o padrão {}", key, text, defaultValue);
                return defaultValue;
            }
            return value;
        } catch (NumberFormatException e) {
            logger.warn("Valor inválido para {}: '{}'. Usando o padrão {}", key, text, defaultValue);
            return defaultValue;
        }
    }

    public double getCpuLimit() {
        return cpuLimit;
    }

    public double getMemoryLimit() {
        return memoryLimit;
    }
}