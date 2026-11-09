import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlertEngine {
    private static final Logger logger = LoggerFactory.getLogger(AlertEngine.class);
    private double cpuLimit;
    private double memoryLimit;
    private MemoryCollector memoryCollector;

    // Guardam se o alerta de cada métrica está "ligado" no momento.
    // Cada métrica tem a sua própria flag, para uma não bloquear a outra.
    private boolean memoryAlertActive = false; // controlam se um alerta já está ativo
    private boolean cpuAlertActive = false; // controlam se um alerta já está ativo

    public AlertEngine(MemoryCollector memoryCollector, double cpuLimit, double memoryLimit) {
        this.memoryCollector = memoryCollector;
        this.cpuLimit = cpuLimit;
        this.memoryLimit = memoryLimit;
    }

    public void checkMemory(){
        double memoryUsedPercent = memoryCollector.getMemoryUsedPercent();
        if(  memoryUsedPercent >= memoryLimit && ! memoryAlertActive){
            logger.warn("Alerta, memoria acima do limite");
            memoryAlertActive = true;
        }
        if (memoryUsedPercent < memoryLimit && memoryAlertActive ){
            logger.info("Memoria abaixo do limite");
            memoryAlertActive = false;
        }
    }

    public void checkCpu(double load){
        // Mesmo molde do checkMemory: só registra quando o estado MUDA.
        if (load >= cpuLimit && !cpuAlertActive) {
            logger.warn("Alerta, CPU acima do limite");
            cpuAlertActive = true;
        }
        if (load < cpuLimit && cpuAlertActive) {
            logger.info("CPU abaixo do limite");
            cpuAlertActive = false;
        }
    }
}
/*
classe feita para mostrar os alertas da cpu e da memoria na tela
memory colector injetado via construtor
logger sendo usado para registrar eventos
 */