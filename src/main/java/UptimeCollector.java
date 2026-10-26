import oshi.software.os.OperatingSystem;

public class UptimeCollector {
    private final OperatingSystem os;
    public UptimeCollector(OperatingSystem os) {
        this.os = os;
    }
    public void printUptime(){
        long totalSegundos = os.getSystemUptime();
        long horas = totalSegundos / 3600;
        long minutos = (totalSegundos % 3600) / 60;
        long segundos = totalSegundos % 60;

        System.out.println(horas + "h " + minutos + "min " + segundos + "s");
    }
}
