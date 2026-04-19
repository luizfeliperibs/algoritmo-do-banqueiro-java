import java.util.Random;

public class Banqueiro {

    static final int NUMBER_OF_CUSTOMERS = 5;
    static final int NUMBER_OF_RESOURCES = 3;

    static final int[] available   = new int[NUMBER_OF_RESOURCES];
    static final int[][] maximum   = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];
    static final int[][] allocation = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];
    static final int[][] need       = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];

    static final Random random = new Random();

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

    public static void main(String[] args) {
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
        System.out.println();

        System.out.println("\n=== Estado inicial ===");
        printState();
    }
}
