import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class MetricsRepository {
    private final Connection connection;

    public MetricsRepository(Connection connection) {
        this.connection = connection;
    }

    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS metrics (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    recorded_at TEXT NOT NULL,
                    cpu_percent REAL NOT NULL,
                    memory_percent REAL NOT NULL
                )
                """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    public void save(double cpuPercent, double memoryPercent) throws SQLException {
        String sql = "INSERT INTO metrics (recorded_at, cpu_percent, memory_percent) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, LocalDateTime.now().toString());
            statement.setDouble(2, cpuPercent);
            statement.setDouble(3, memoryPercent);
            statement.executeUpdate();
        }

    }

    public Optional<Metric> findLatest() throws SQLException {
        String sql = "SELECT * FROM metrics ORDER BY id DESC LIMIT 1;";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            if (rs.next()) { // O cursor tenta avançar para a primeira linha. Se existir uma linha, retorna true e entra no if.
                int id = rs.getInt("id");
                String recordedAt = rs.getString("recorded_at");
                double cpuPercent = rs.getDouble("cpu_percent");
                double memoryPercent = rs.getDouble("memory_percent");
                return Optional.of(new Metric(id, recordedAt, cpuPercent, memoryPercent)); // monta o objeto com os valores lidos
            }
            return Optional.empty(); // tabela vazia: nenhuma linha para devolver
        }
    }
}