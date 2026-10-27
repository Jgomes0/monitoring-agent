import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class MetricsRepositoryTest {
    @Test
    void deveSalvarMetricasCorretamente() throws Exception {

        // 1. Criar banco temporário na memória
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite::memory:")) {

            // 2. Criar repositório e tabela
            MetricsRepository repository = new MetricsRepository(connection);
            repository.createTableIfNotExists();

            // 3. Salvar métricas fictícias
            repository.save(25.5, 86.0);

            // 4. Consultar dados salvos
            String sql = "SELECT * FROM metrics";

            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(sql)) {

                // 5. Verificar se existe um registro
                assertTrue(rs.next());

                // 6. Verificar os valores salvos
                assertEquals(25.5, rs.getDouble("cpu_percent"), 0.001);
                assertEquals(86.0, rs.getDouble("memory_percent"), 0.001);

                // 7. Verificar se a data foi registrada
                assertNotNull(rs.getString("recorded_at"));

                // 8. Verificar se existe apenas um registro
                assertFalse(rs.next());
            }
        }
    }

    @Test
    void deveRetornarAMetricaMaisRecente() throws Exception {

        // 1. Criar banco temporário na memória
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite::memory:")) {

            // 2. Criar repositório e tabela
            MetricsRepository repository = new MetricsRepository(connection);
            repository.createTableIfNotExists();

            // 3. Salvar duas métricas: a segunda é a mais recente (id maior)
            repository.save(10.0, 20.0);
            repository.save(30.0, 40.0);

            // 4. Buscar a mais recente
            Optional<Metric> resultado = repository.findLatest();

            // 5. Deve existir um resultado, e ser a segunda métrica salva
            assertTrue(resultado.isPresent());
            assertEquals(30.0, resultado.get().cpuPercent(), 0.001);
            assertEquals(40.0, resultado.get().memoryPercent(), 0.001);
        }
    }

    @Test
    void deveRetornarVazioQuandoNaoHaMetricas() throws Exception {

        // 1. Criar banco temporário na memória
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite::memory:")) {

            // 2. Criar repositório e tabela (sem salvar nada)
            MetricsRepository repository = new MetricsRepository(connection);
            repository.createTableIfNotExists();

            // 3. Com a tabela vazia, não deve haver resultado
            assertTrue(repository.findLatest().isEmpty());
        }
    }
}