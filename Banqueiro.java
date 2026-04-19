import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class BankersAlgorithm {

    static final int NUMBER_OF_CUSTOMERS = 5;
    static final int NUMBER_OF_RESOURCES = 3;

    static final int[] available   = new int[NUMBER_OF_RESOURCES];
    static final int[][] maximum   = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];
    static final int[][] allocation = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];
    static final int[][] need       = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];

    static final ReentrantLock mutex = new ReentrantLock();
    static final Random random = new Random();

    static volatile boolean running = true;

    
    // Algoritmo de segurança — verifica se o sistema está em estado seguro
    
    static boolean isSafeState() {
        int[] work = available.clone();
        boolean[] finish = new boolean[NUMBER_OF_CUSTOMERS];

        boolean found;
        do {
            found = false;
            for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
                if (!finish[i] && needFitsWork(i, work)) {
                    for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                        work[j] += allocation[i][j];
                    }
                    finish[i] = true;
                    found = true;
                }
            }
        } while (found);

        for (boolean f : finish) {
            if (!f) return false;
        }
        return true;
    }

    static boolean needFitsWork(int customer, int[] work) {
        for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
            if (need[customer][j] > work[j]) return false;
        }
        return true;
    }

    
    // request_resources — retorna 0 se bem-sucedido, -1 caso contrário
    
    static int request_resources(int customer_num, int[] request) {
        mutex.lock();
        try {
            // Passo 1: verifica se a requisição não excede o need do cliente
            for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                if (request[j] > need[customer_num][j]) {
                    System.out.printf("[Cliente %d] NEGADO    — solicitação %s excede need %s%n",
                            customer_num, arrayToString(request), arrayToString(need[customer_num]));
                    return -1;
                }
            }

            // Passo 2: verifica se os recursos estão disponíveis
            for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                if (request[j] > available[j]) {
                    System.out.printf("[Cliente %d] NEGADO    — solicitação %s excede disponível %s%n",
                            customer_num, arrayToString(request), arrayToString(available));
                    return -1;
                }
            }

            // Passo 3: alocação tentativa
            for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                available[j]                -= request[j];
                allocation[customer_num][j]  += request[j];
                need[customer_num][j]         -= request[j];
            }

            // Passo 4: verifica se o estado resultante é seguro
            if (isSafeState()) {
                System.out.printf("[Cliente %d] CONCEDIDO — solicitação %s%n",
                        customer_num, arrayToString(request));
                return 0;
            }

            // Estado inseguro: reverte a alocação tentativa
            for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                available[j]                += request[j];
                allocation[customer_num][j]  -= request[j];
                need[customer_num][j]         += request[j];
            }
            System.out.printf("[Cliente %d] NEGADO    — estado inseguro após alocação %s%n",
                    customer_num, arrayToString(request));
            return -1;

        } finally {
            mutex.unlock();
        }
    }

    
    // release_resources — retorna 0 se bem-sucedido, -1 caso contrário
    
    static int release_resources(int customer_num, int[] release) {
        mutex.lock();
        try {
            // Valida que o cliente não libera mais do que possui alocado
            for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                if (release[j] > allocation[customer_num][j]) {
                    System.out.printf("[Cliente %d] ERRO      — tentativa de liberar mais do que alocado.%n",
                            customer_num);
                    return -1;
                }
            }

            for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                available[j]                += release[j];
                allocation[customer_num][j]  -= release[j];
                need[customer_num][j]         += release[j];
            }

            System.out.printf("[Cliente %d] LIBERADO  — liberação %s%n",
                    customer_num, arrayToString(release));
            return 0;

        } finally {
            mutex.unlock();
        }
    }

    
    // Thread de cliente — loop contínuo de solicitações e liberações
    
    static class CustomerThread implements Runnable {
        private final int id;

        CustomerThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (running) {
                // Gera solicitação aleatória limitada pelo need atual do cliente
                int[] request = buildRandomRequest();
                request_resources(id, request);

                sleep(200 + random.nextInt(500));

                // Libera parte aleatória do que está alocado
                int[] release = buildRandomRelease();
                release_resources(id, release);

                sleep(200 + random.nextInt(500));
            }
        }

        private int[] buildRandomRequest() {
            mutex.lock();
            int[] req = new int[NUMBER_OF_RESOURCES];
            try {
                for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                    req[j] = need[id][j] > 0 ? random.nextInt(need[id][j] + 1) : 0;
                }
            } finally {
                mutex.unlock();
            }
            return req;
        }

        private int[] buildRandomRelease() {
            mutex.lock();
            int[] rel = new int[NUMBER_OF_RESOURCES];
            try {
                for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                    rel[j] = allocation[id][j] > 0 ? random.nextInt(allocation[id][j] + 1) : 0;
                }
            } finally {
                mutex.unlock();
            }
            return rel;
        }

        private void sleep(long ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    
    // Impressão do estado atual do sistema
    
    static void printState() {
        System.out.println("  Disponível : " + arrayToString(available));
        System.out.print("  Alocação   : ");
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
            System.out.print("C" + i + ":" + arrayToString(allocation[i]) + " ");
        System.out.println();
        System.out.print("  Need       : ");
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
            System.out.print("C" + i + ":" + arrayToString(need[i]) + " ");
        System.out.println();
    }

    static String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    
    // main
    
    public static void main(String[] args) throws InterruptedException {
        if (args.length != NUMBER_OF_RESOURCES) {
            System.err.printf("Uso: java BankersAlgorithm <rec1> <rec2> <rec3>%n");
            System.err.printf("Exemplo: java BankersAlgorithm 10 5 7%n");
            System.exit(1);
        }

        // Inicializa recursos disponíveis a partir dos argumentos da linha de comando
        for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
            available[j] = Integer.parseInt(args[j]);
        }

        // Inicializa maximum com valores aleatórios entre 1 e available[j]
        // need começa igual ao maximum pois a alocação inicial é zero
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
            for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                maximum[i][j] = 1 + random.nextInt(available[j]);
                need[i][j]    = maximum[i][j];
            }
        }

        System.out.println("=== Algoritmo do Banqueiro ===");
        System.out.println("Clientes  : " + NUMBER_OF_CUSTOMERS);
        System.out.println("Recursos  : " + NUMBER_OF_RESOURCES);
        System.out.println("Disponível: " + arrayToString(available));
        System.out.print("Maximum   : ");
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
            System.out.print("C" + i + ":" + arrayToString(maximum[i]) + " ");
        System.out.println("\n");

        // Cria e inicia as threads de clientes
        Thread[] threads = new Thread[NUMBER_OF_CUSTOMERS];
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
            threads[i] = new Thread(new CustomerThread(i), "Cliente-" + i);
            threads[i].setDaemon(true);
            threads[i].start();
        }

        // Executa por 10 segundos e encerra graciosamente
        Thread.sleep(10000);
        running = false;

        for (Thread t : threads) {
            t.join(1000);
        }

        System.out.println("\n=== Estado final do sistema ===");
        printState();
        System.out.println("=== Simulação encerrada ===");
    }
}
