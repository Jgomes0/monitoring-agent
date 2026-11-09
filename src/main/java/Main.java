import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("Monitoring Agent iniciado!");
        AppConfig config = AppConfig.load("config.properties");
        double cpuLimit = config.getCpuLimit();
        double memoryLimit = config.getMemoryLimit();

        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        UptimeCollector uptimeCollector = new UptimeCollector(os);
        HardwareAbstractionLayer hal = si.getHardware();
        CpuCollector cpuCollector = new CpuCollector(hal);
        SystemInfoCollector collector = new SystemInfoCollector();
        MemoryCollector memoryCollector = new MemoryCollector(hal);
        AlertEngine alertEngine = new AlertEngine(memoryCollector, cpuLimit, memoryLimit); // limites vindos do arquivo
        collector.printBasicInfo();
        // Integração com SQLite
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite:monitoring.db")) {

            MetricsRepository repository = new MetricsRepository(connection);

            repository.createTableIfNotExists();

            // existe alguma leitura salva no banco? Se existir, qual foi a última?
            Optional<Metric> ultima = repository.findLatest();
            if (ultima.isPresent()) {
                Metric metric = ultima.get();
                logger.info(
                        "Última leitura salva: {} | CPU {}% | Memória {}%",
                        metric.recordedAt(),
                        String.format("%.1f", metric.cpuPercent()),
                        String.format("%.1f", metric.memoryPercent())
                );
            } else {
                logger.info("Nenhuma leitura salva ainda");
            }

            // Loop de monitoramento
            while (true) {

                try {
                    uptimeCollector.printUptime();
                    double cpuLoad = cpuCollector.printCpuinfo();

                    memoryCollector.printMemoryInfo();

                    // Verificação dos alertas
                    alertEngine.checkMemory();

                    alertEngine.checkCpu(cpuLoad);

                    // Persistência das métricas
                    repository.save(
                            cpuLoad,
                            memoryCollector.getMemoryUsedPercent()
                    );

                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    logger.info("Monitoramento interrompido");
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        } catch (SQLException e) {
            logger.error("Erro no banco de dados: ", e);
        }

    }
}