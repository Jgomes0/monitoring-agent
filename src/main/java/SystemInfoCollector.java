import java.net.InetAddress; // coletar informações da rede como nome da maquina,endereço ip,hostnome (Quero usar a classe InetAddress, que está dentro do pacote java.net)
import java.net.UnknownHostException; // captar exceções

public class SystemInfoCollector {

    public void printBasicInfo() {
        try {
            InetAddress localHost = InetAddress.getLocalHost(); // captar informações da rede local
            String hostname = localHost.getHostName(); // captar o nome da maquina local

            //vem da classe System, que pega propriedades do ambiente onde o Java está rodando.
            String sistemaOperacional = System.getProperty("os.name");
            String versaoSistema = System.getProperty("os.version");
            String arquitetura = System.getProperty("os.arch");
            System.out.println("Servidor: " + hostname);
            System.out.println("Sistema operacional: " + sistemaOperacional);
            System.out.println("Versão: " + versaoSistema);
            System.out.println("Arquitetura: " + arquitetura);

        } catch (UnknownHostException e) { // caso não consiga descobrir o hostname pedido do inetdress
            System.out.println("nao foi possivel descobrir o hostname");
        }
    }



    }


