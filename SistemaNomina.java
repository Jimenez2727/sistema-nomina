import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

// Clase base Empleado
abstract class Empleado {
    protected String nombre;
    protected double salario;
    protected int horasTrabajadas;

    public Empleado(String nombre, double salario) {
        this.nombre = nombre;
        this.salario = salario;
        this.horasTrabajadas = 0; // Inicialmente 0 horas trabajadas
    }

    public String getNombre() {
        return nombre;
    }

    public double getSalario() {
        return salario;
    }

    public int getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void registrarHoras(int horas) throws HorasInvalidasException {
        if (horas < 0) {
            throw new HorasInvalidasException("Las horas trabajadas no pueden ser negativas.");
        }
        this.horasTrabajadas += horas;
    }

    public abstract double calcularSalario();
}

// Clase EmpleadoFijo que hereda de Empleado
class EmpleadoFijo extends Empleado {
    public EmpleadoFijo(String nombre, double salario) {
        super(nombre, salario);
    }

    @Override
    public double calcularSalario() {
        return salario; // El salario es fijo
    }
}

// Clase EmpleadoTemporal que hereda de Empleado
class EmpleadoTemporal extends Empleado {
    private double pagoPorHora;

    public EmpleadoTemporal(String nombre, double pagoPorHora) {
        super(nombre, 0); // El salario será calculado en base a horas trabajadas
        this.pagoPorHora = pagoPorHora;
    }

    @Override
    public double calcularSalario() {
        return horasTrabajadas * pagoPorHora;
    }
}

// Excepción personalizada HorasInvalidasException
class HorasInvalidasException extends Exception {
    public HorasInvalidasException(String mensaje) {
        super(mensaje);
    }
}

// Excepción personalizada SalarioInvalido
class SalarioInvalido extends Exception {
    public SalarioInvalido(String mensaje) {
        super(mensaje);
    }
}

// Clase principal SistemaNomina (sin public)
class SistemaNomina {
    private static ArrayList<Empleado> empleados = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n--- Sistema de Nómina ---");
            System.out.println("1. Registrar Empleado");
            System.out.println("2. Registrar Horas Trabajadas");
            System.out.println("3. Consultar Salario");
            System.out.println("4. Guardar Empleados en Archivo");
            System.out.println("5. Cargar Empleados desde Archivo");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    registrarEmpleado(scanner);
                    break;
                case 2:
                    registrarHoras(scanner);
                    break;
                case 3:
                    consultarSalario(scanner);
                    break;
                case 4:
                    guardarEmpleadosEnArchivo();
                    break;
                case 5:
                    cargarEmpleadosDesdeArchivo();
                    break;
                case 6:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }
    }

    private static void registrarEmpleado(Scanner scanner) {
        System.out.print("Nombre del empleado: ");
        String nombre = scanner.next();
        System.out.print("Tipo de empleado (1 = Fijo, 2 = Temporal): ");
        int tipo = scanner.nextInt();

        try {
            if (tipo == 1) {
                System.out.print("Salario fijo: ");
                double salario = scanner.nextDouble();
                empleados.add(new EmpleadoFijo(nombre, salario));
            } else if (tipo == 2) {
                System.out.print("Pago por hora: ");
                double pagoPorHora = scanner.nextDouble();
                empleados.add(new EmpleadoTemporal(nombre, pagoPorHora));
            } else {
                System.out.println("Tipo de empleado no válido.");
            }
        } catch (SalarioInvalido e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registrarHoras(Scanner scanner) {
        System.out.print("Nombre del empleado: ");
        String nombre = scanner.next();
        Empleado empleado = buscarEmpleado(nombre);

        if (empleado != null) {
            try {
                System.out.print("Horas trabajadas: ");
                int horas = scanner.nextInt();
                empleado.registrarHoras(horas);
            } catch (HorasInvalidasException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Empleado no encontrado.");
        }
    }

    private static void consultarSalario(Scanner scanner) {
        System.out.print("Nombre del empleado: ");
        String nombre = scanner.next();
        Empleado empleado = buscarEmpleado(nombre);

        if (empleado != null) {
            System.out.println("Salario: " + empleado.calcularSalario());
        } else {
            System.out.println("Empleado no encontrado.");
        }
    }

    private static Empleado buscarEmpleado(String nombre) {
        for (Empleado empleado : empleados) {
            if (empleado.getNombre().equalsIgnoreCase(nombre)) {
                return empleado;
            }
        }
        return null;
    }

    private static void guardarEmpleadosEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("empleados.txt"))) {
            for (Empleado empleado : empleados) {
                writer.println(empleado.getNombre() + "," + empleado.getSalario() + "," + empleado.getHorasTrabajadas());
            }
            System.out.println("Empleados guardados en empleados.txt.");
        } catch (IOException e) {
            System.out.println("Error al guardar empleados: " + e.getMessage());
        }
    }

    private static void cargarEmpleadosDesdeArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader("empleados.txt"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                String nombre = datos[0];
                double salario = Double.parseDouble(datos[1]);
                int horas = Integer.parseInt(datos[2]);
                Empleado empleado = new EmpleadoFijo(nombre, salario); // Para simplificar, cargamos como EmpleadoFijo
                empleado.registrarHoras(horas);
                empleados.add(empleado);
            }
            System.out.println("Empleados cargados desde empleados.txt.");
        } catch (IOException | HorasInvalidasException e) {
            System.out.println("Error al cargar empleados: " + e.getMessage());
        }
    }
}
