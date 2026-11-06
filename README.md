# Monitoring Agent

Projeto pessoal desenvolvido em Java 17, sem frameworks de aplicação como Spring Boot. Utiliza bibliotecas externas para coleta de informações do sistema, persistência, logging e testes automatizados. Desenvolvido com o objetivo de praticar fundamentos de Java, orientação a objetos, injeção de dependência e organização de uma aplicação backend antes de migrar para Spring Boot.

Projeto pessoal, feito para aprender a estruturar uma aplicação backend em Java puro (sem framework) antes de migrar para o Spring.

## O que ele faz

- Mostra o **nome da máquina**, o **sistema operacional**, a versão e a arquitetura.
- Atualiza a cada segundo o **uptime**, o **uso de CPU** e a **memória** (total, usada e livre).
- Gera **alertas** quando a CPU ou a memória passam do limite configurado. O alerta só é registrado quando a situação *muda*: um `WARN` ao ultrapassar o limite e um `INFO` ao voltar ao normal, em vez de repetir a mesma mensagem a cada segundo.
- Grava **uma leitura por segundo** (data/hora, CPU e memória) em um banco SQLite (`monitoring.db`).
- Ao iniciar, mostra a **última leitura salva** da execução anterior.
- Registra os eventos do programa com **logging** (SLF4J + Logback), no console e em arquivo.
- Os **limites de alerta** ficam num arquivo de configuração, sem precisar mexer no código.

## Exemplo de saída

```
2026-01-01 12:00:00 INFO  Main - Monitoring Agent iniciado!
Servidor: MINHA-MAQUINA
Sistema operacional: Windows 11
Versão: 10.0
Arquitetura: amd64
2026-01-01 12:00:01 INFO  Main - Última leitura salva: 2025-12-31T23:59:58 | CPU 11,8% | Memória 62,4%
71h 19min 18s
CPU: 21,4%
Memória total: 16239 MB
Memória usada: 14465 MB
Memória livre: 1774 MB
2026-01-01 12:00:02 WARN  AlertEngine - Alerta, memoria acima do limite
```

## Tecnologias

| Para quê | O quê |
|---|---|
| Linguagem | Java 17 |
| Build e dependências | Maven |
| Leitura de CPU, memória, uptime | [OSHI](https://github.com/oshi/oshi) |
| Banco de dados | SQLite via JDBC (`sqlite-jdbc`) |
| Logging | SLF4J + Logback |
| Testes | JUnit 5 e Mockito |

## Como rodar

Requisitos: **JDK 17 ou superior** e **Maven**.

```bash
git clone https://github.com/Jgomes0/monitoring-agent.git
cd monitoring-agent
mvn test
```

Para executar o monitor, abra o projeto em uma IDE (como o IntelliJ IDEA) e rode a classe `Main`. O arquivo do banco (`monitoring.db`) é criado na pasta de execução, na primeira vez.

> Desenvolvido e testado no Windows 11. O OSHI também suporta Linux e macOS, mas o projeto ainda não foi testado neles.

## Configuração

Os limites de alerta ficam em `src/main/resources/config.properties`:

```properties
cpu.limit=80.0
memory.limit=85.0
```

Se o arquivo ou uma das chaves não existir, o programa usa os valores padrão acima e registra um aviso no log. O formato e o destino dos logs são configurados em `src/main/resources/logback.xml`.

## Estrutura do projeto

```
src/
├── main/
│   ├── java/
│   │   ├── Main.java                 # ponto de entrada: monta as classes e roda o loop
│   │   ├── SystemInfoCollector.java  # nome da máquina e sistema operacional
│   │   ├── CpuCollector.java         # uso de CPU
│   │   ├── MemoryCollector.java      # memória total, usada e livre
│   │   ├── UptimeCollector.java      # tempo ligado
│   │   ├── AlertEngine.java          # decide e registra os alertas
│   │   ├── MetricsRepository.java    # grava e lê as leituras no SQLite
│   │   └── Metric.java               # uma leitura (record)
│   └── resources/
│       ├── config.properties         # limites de alerta
│       └── logback.xml               # configuração dos logs
└── test/java/                        # testes automatizados
```

## Decisões de projeto

- **Injeção de dependência pelo construtor.** Cada classe recebe o que precisa (o `HardwareAbstractionLayer` do OSHI, a conexão com o banco, o `MemoryCollector`), em vez de criar sozinha. O `Main` cria os objetos uma vez e os entrega, o que deixa o código simples de testar.
- **Cada classe com uma responsabilidade.** Os coletores só leem dados, o `AlertEngine` só decide os alertas, e o `MetricsRepository` só fala com o banco.
- **Painel e log separados.** O que o usuário veio ver (CPU, memória, uptime) sai direto no console. Os eventos do programa (início, falhas, alertas) passam pelo logger.
- **Configuração fora do código.** Os limites vêm de um arquivo, com valores padrão caso ele não exista.

## Testes

O projeto tem **17 testes automatizados**, executados com `mvn test`:

- Os coletores são testados com **mocks** (Mockito) no lugar do hardware real, para os resultados serem sempre previsíveis.
- O `AlertEngine` é testado verificando as mensagens de log registradas (nível e texto), inclusive que o alerta não se repete enquanto o limite continua ultrapassado.
- O `MetricsRepository` é testado com um banco SQLite **em memória**, sem criar arquivo.

## Próximos passos

- Gerar um `.jar` executável, para rodar com `java -jar` sem IDE.
- Continuar monitorando mesmo se a gravação no banco falhar.
- Migrar para **Spring Boot** e expor as métricas por uma API REST.
- Permitir editar a configuração fora do `.jar`.

## Autor

[Jgomes0](https://github.com/Jgomes0)
