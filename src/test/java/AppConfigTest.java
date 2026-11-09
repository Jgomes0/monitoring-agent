
import org.junit.jupiter.api.Test;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppConfigTest {

    @Test
    void deveLerLimitesValidos() {
        // ARRANGE
        Properties properties = new Properties();
        properties.setProperty("cpu.limit", "70.0");
        properties.setProperty("memory.limit", "90.0");

        // ACT
        AppConfig config = new AppConfig(properties);

        // ASSERT
        assertEquals(70.0, config.getCpuLimit(), 0.001);
        assertEquals(90.0, config.getMemoryLimit(), 0.001);
    }

    @Test
    void deveUsarPadraoQuandoAsChavesNaoExistem() {
        // ARRANGE: propriedades vazias, como um arquivo sem as chaves
        Properties properties = new Properties();

        // ACT
        AppConfig config = new AppConfig(properties);

        // ASSERT: padrões 80.0 (CPU) e 85.0 (memória)
        assertEquals(80.0, config.getCpuLimit(), 0.001);
        assertEquals(85.0, config.getMemoryLimit(), 0.001);
    }

    @Test
    void deveUsarPadraoQuandoOValorNaoEUmNumero() {
        // ARRANGE: o caso que derrubava o programa (cpu.limit=abc)
        Properties properties = new Properties();
        properties.setProperty("cpu.limit", "abc");
        properties.setProperty("memory.limit", "");

        // ACT: não pode lançar exceção
        AppConfig config = new AppConfig(properties);

        // ASSERT
        assertEquals(80.0, config.getCpuLimit(), 0.001);
        assertEquals(85.0, config.getMemoryLimit(), 0.001);
    }

    @Test
    void deveUsarPadraoQuandoOValorEstaForaDaFaixa() {
        // ARRANGE: uma porcentagem não pode ser 150 nem negativa
        Properties properties = new Properties();
        properties.setProperty("cpu.limit", "150");
        properties.setProperty("memory.limit", "-5");

        // ACT
        AppConfig config = new AppConfig(properties);

        // ASSERT
        assertEquals(80.0, config.getCpuLimit(), 0.001);
        assertEquals(85.0, config.getMemoryLimit(), 0.001);
    }

    @Test
    void deveAceitarEspacosAoRedorDoNumero() {
        // ARRANGE
        Properties properties = new Properties();
        properties.setProperty("cpu.limit", "  75.5 ");

        // ACT
        AppConfig config = new AppConfig(properties);

        // ASSERT
        assertEquals(75.5, config.getCpuLimit(), 0.001);
    }

    @Test
    void deveUsarPadraoQuandoOArquivoNaoExiste() {
        // ACT: nome de arquivo que não existe no classpath
        AppConfig config = AppConfig.load("arquivo-que-nao-existe.properties");

        // ASSERT
        assertEquals(80.0, config.getCpuLimit(), 0.001);
        assertEquals(85.0, config.getMemoryLimit(), 0.001);
    }
}