package estructura5;
import java.util.*;

public class Estructura5 {

    static class Nodo {
        String nombre;
        Equipo equipo;
        Nodo izquierdo;
        Nodo derecho;
    }

    static class Equipo {
        String codigo;
        String nombre;
        String capitan;
    }

    static class Incidente implements Comparable<Incidente> {
        String codigo;
        String descripcion;
        int prioridad;
        int orden;

        public int compareTo(Incidente otro) {
            if (this.prioridad != otro.prioridad) {
                return Integer.compare(this.prioridad, otro.prioridad);
            }
            return Integer.compare(this.orden, otro.orden);
        }
    }

    static class CambioResultado {
        Nodo nodoModificado;
        Equipo ganadorAnterior;
    }

    static ArrayList<Equipo> equipos = new ArrayList<>();
    static Queue<Equipo> colaCheckin = new LinkedList<>();
    static PriorityQueue<Incidente> incidentes = new PriorityQueue<>();
    static ArrayList<Incidente> atendidos = new ArrayList<>();
    static Stack<CambioResultado> pilaResultados = new Stack<>();
    
    static int contadorIncidentes = 0;

    static Nodo semifinal1 = new Nodo();
    static Nodo semifinal2 = new Nodo();
    static Nodo finalT = new Nodo();

    public static void main(String[] args) {
        semifinal1.nombre = "Semifinal 1";
        semifinal2.nombre = "Semifinal 2";
        finalT.nombre = "Final";
        finalT.izquierdo = semifinal1;
        finalT.derecho = semifinal2;

        Scanner sc = new Scanner(System.in);
        int op;
        do {
            System.out.println("\n=== Campus Clash ===");
            System.out.println("1. Registrar equipo");
            System.out.println("2. Hacer check-in");
            System.out.println("3. Registrar ganador de semifinal");
            System.out.println("4. Registrar ganador de la final");
            System.out.println("5. Deshacer ultimo resultado");
            System.out.println("6. Registrar incidente");
            System.out.println("7. Ver proximo incidente");
            System.out.println("8. Atender incidente");
            System.out.println("9. Ver incidentes atendidos");
            System.out.println("10. Ver cuadro del torneo");
            System.out.println("0. Salir");
            System.out.print("\nOpcion: ");

            try {
                op = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Opcion invalida, ingrese un numero");
                sc.nextLine();
                op = -1;
            }

            switch (op) {
                case 1: registrarEquipo(sc); break;
                case 2: hacerCheckin(sc); break;
                case 3: registrarGanadorSemifinal(sc); break;
                case 4: registrarGanadorFinal(sc); break;
                case 5: deshacerResultado(); break;
                case 6: registrarIncidente(sc); break;
                case 7: verProximoIncidente(); break;
                case 8: atenderIncidente(); break;
                case 9: verIncidentesAtendidos(); break;
                case 10: verCuadro(); break;
                case 0: System.out.println("Fin del torneo"); break;
                default:
                    if (op != -1) System.out.println("Opcion invalida");
                    break;
            }
        } while (op != 0);
    }

    static void registrarEquipo(Scanner sc) {
        if (equipos.size() >= 4) {
            System.out.println("Ya hay 4 equipos registrados");
            return;
        }

        System.out.print("Codigo unico: ");
        String cod = sc.nextLine().trim();
        for (Equipo e : equipos) {
            if (e.codigo.equalsIgnoreCase(cod)) {
                System.out.println("Ya existe un equipo con ese codigo");
                return;
            }
        }
        Equipo e = new Equipo();
        e.codigo = cod;
        System.out.print("Nombre del equipo: ");
        e.nombre = sc.nextLine().trim();
        System.out.print("Nombre del capitan: ");
        e.capitan = sc.nextLine().trim();
        
        equipos.add(e);
        System.out.println("Equipo '" + e.nombre + "' registrado.");
    }

    static void hacerCheckin(Scanner sc) {
        if (semifinal1.izquierdo != null) {
            System.out.println("Las semifinales ya estan formadas");
            return;
        }
        if (equipos.isEmpty()) {
            System.out.println("No hay equipos registrados todavia");
            return;
        }

        System.out.print("Ingrese el codigo del equipo: ");
        String cod = sc.nextLine().trim();
        Equipo encontrado = null;
        for (Equipo e : equipos) {
            if (e.codigo.equalsIgnoreCase(cod)) {
                encontrado = e;
                break;
            }
        }
        if (encontrado == null) {
            System.out.println("Codigo no encontrado");
            return;
        }

        if (colaCheckin.contains(encontrado)) {
            System.out.println("Este equipo ya hizo check-in");
            return;
        }

        colaCheckin.offer(encontrado);
        System.out.println(encontrado.nombre + " hizo check-in (" + colaCheckin.size() + " de 4)");

        if (colaCheckin.size() == 4) {
            semifinal1.izquierdo = new Nodo();
            semifinal1.izquierdo.nombre = "Local SF1";
            semifinal1.izquierdo.equipo = colaCheckin.poll();

            semifinal1.derecho = new Nodo();
            semifinal1.derecho.nombre = "Visitante SF1";
            semifinal1.derecho.equipo = colaCheckin.poll();

            semifinal2.izquierdo = new Nodo();
            semifinal2.izquierdo.nombre = "Local SF2";
            semifinal2.izquierdo.equipo = colaCheckin.poll();

            semifinal2.derecho = new Nodo();
            semifinal2.derecho.nombre = "Visitante SF2";
            semifinal2.derecho.equipo = colaCheckin.poll();

            System.out.println("\nTodas las semifinales ya estan formadas automaticamente");
        }
    }

    static void registrarGanadorSemifinal(Scanner sc) {
        if (semifinal1.izquierdo == null || semifinal2.izquierdo == null) {
            System.out.println("Las semifinales aun no estan completas");
            return;
        }

        System.out.print("Cual semifinal? (1 o 2): ");
        int i;
        try {
            i = sc.nextInt();
            sc.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Ingrese 1 o 2");
            sc.nextLine();
            return;
        }

        if (i != 1 && i != 2) {
            System.out.println("Opcion invalida, ingrese 1 o 2");
            return;
        }

        Nodo semi;
        if (i == 1) {
            semi = semifinal1;
        } else {
            semi = semifinal2;
        }

        if (semi.equipo != null) {
            System.out.println("Esta semifinal ya tiene ganador: " + semi.equipo.nombre);
            return;
        }

        System.out.println("Ganador de " + semi.nombre + ":");
        System.out.println("1. " + semi.izquierdo.equipo.nombre);
        System.out.println("2. " + semi.derecho.equipo.nombre);
        System.out.print("Opcion: ");
        int op;
        try {
            op = sc.nextInt();
            sc.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Opcion invalida");
            sc.nextLine();
            return;
        }

        Equipo ganadorSeleccionado;
        if (op == 1) {
            ganadorSeleccionado = semi.izquierdo.equipo;
        } else if (op == 2) {
            ganadorSeleccionado = semi.derecho.equipo;
        } else {
            System.out.println("Opcion invalida");
            return;
        }

        CambioResultado cambio = new CambioResultado();
        cambio.nodoModificado = semi;
        cambio.ganadorAnterior = semi.equipo;
        pilaResultados.push(cambio);

        semi.equipo = ganadorSeleccionado;
        System.out.println(semi.equipo.nombre + " registrado como ganador de " + semi.nombre);

        if (semifinal1.equipo != null && semifinal2.equipo != null) {
            System.out.println("\nFinal formada: " + semifinal1.equipo.nombre + " vs " + semifinal2.equipo.nombre);
        }
    }

    static void registrarGanadorFinal(Scanner sc) {
        if (semifinal1.equipo == null || semifinal2.equipo == null) {
            System.out.println("La final aun no esta formada");
            return;
        }
        if (finalT.equipo != null) {
            System.out.println("El torneo ya acabo, gano " + finalT.equipo.nombre);
            return;
        }

        System.out.println("Ganador de la Final:");
        System.out.println("1. " + semifinal1.equipo.nombre);
        System.out.println("2. " + semifinal2.equipo.nombre);
        System.out.print("Opcion: ");
        int op;
        try {
            op = sc.nextInt();
            sc.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Opcion invalida");
            sc.nextLine();
            return;
        }

        Equipo ganadorSeleccionado;
        if (op == 1) {
            ganadorSeleccionado = semifinal1.equipo;
        } else if (op == 2) {
            ganadorSeleccionado = semifinal2.equipo;
        } else {
            System.out.println("Opcion invalida");
            return;
        }

        CambioResultado cambio = new CambioResultado();
        cambio.nodoModificado = finalT;
        cambio.ganadorAnterior = finalT.equipo;
        pilaResultados.push(cambio);

        finalT.equipo = ganadorSeleccionado;
        System.out.println(finalT.equipo.nombre + " es el campeon!");
    }

    static void deshacerResultado() {
        if (pilaResultados.isEmpty()) {
            System.out.println("Aun no hay resultados para deshacer");
            return;
        }

        CambioResultado ultimo = pilaResultados.pop();
        Nodo nodo = ultimo.nodoModificado;
        nodo.equipo = ultimo.ganadorAnterior;
        if (nodo == semifinal1 || nodo == semifinal2) {
            finalT.equipo = null; 
        }
        System.out.println("Resultado deshecho en " + nodo.nombre);
    }

    static void registrarIncidente(Scanner sc) {
        System.out.print("Codigo del incidente: ");
        String cod = sc.nextLine().trim();
        System.out.print("Descripcion: ");
        String desc = sc.nextLine().trim();
        System.out.println("Prioridad:");
        System.out.println("1 - Critica");
        System.out.println("2 - Alta");
        System.out.println("3 - Media");
        System.out.println("4 - Baja");
        System.out.print("Ingrese prioridad (1-4): ");
        int prio;
        try {
            prio = sc.nextInt();
            sc.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Prioridad invalida");
            sc.nextLine();
            return;
        }

        if (prio < 1 || prio > 4) {
            System.out.println("Opcion invalida");
            return;
        }

        contadorIncidentes++;
        Incidente inc = new Incidente();
        inc.codigo = cod;
        inc.descripcion = desc;
        inc.prioridad = prio;
        inc.orden = contadorIncidentes;

        incidentes.offer(inc);
        System.out.println("Incidente '" + cod + "' registrado.");
    }

    static void verProximoIncidente() {
        if (incidentes.isEmpty()) {
            System.out.println("No hay incidentes pendientes");
            return;
        }
        Incidente inc = incidentes.peek();
        System.out.println("\nProximo incidente a atender:");
        System.out.println("Codigo: " + inc.codigo);
        System.out.println("Descripcion: " + inc.descripcion);
        System.out.println("Prioridad: " + inc.prioridad);
    }

    static void atenderIncidente() {
        if (incidentes.isEmpty()) {
            System.out.println("No hay incidentes pendientes");
            return;
        }
        Incidente inc = incidentes.poll();
        atendidos.add(inc);
        System.out.println("Incidente atendido: [" + inc.codigo + "] " + inc.descripcion);
    }

    static void verIncidentesAtendidos() {
        if (atendidos.isEmpty()) {
            System.out.println("Aun no se ha atendido ningun incidente");
            return;
        }
        System.out.println("\n=== Incidentes atendidos ===");
        for (int i = 0; i < atendidos.size(); i++) {
            Incidente inc = atendidos.get(i);
            System.out.println((i + 1) + ". [" + inc.codigo + "] " + inc.descripcion + " - Prioridad " + inc.prioridad);
        }
    }

    static void verCuadro() {
        Nodo s1 = finalT.izquierdo;
        Nodo s2 = finalT.derecho;
        
        System.out.println("\n======BRACKETS DEL TORNEO======\n");
        System.out.println("            [" + finalT.nombre + "]");
        System.out.println("           /        \\");
        System.out.println("   [" + s1.nombre + "]  [" + s2.nombre + "]");
        System.out.println("================================");

        if (s1.izquierdo != null) {
            String rival1;
            if (s1.derecho != null) {
                rival1 = s1.derecho.equipo.nombre;
            } else {
                rival1 = "?";
            }
            System.out.println("Semifinal 1: " + s1.izquierdo.equipo.nombre + " vs " + rival1);
            
            String txtG1;
            if (s1.equipo != null) {
                txtG1 = s1.equipo.nombre;
            } else {
                txtG1 = "Pendiente";
            }
            System.out.println("Ganador: " + txtG1);
        } else {
            System.out.println("Semifinal 1: (sin equipos aun)");
        }

        if (s2.izquierdo != null) {
            String rival2;
            if (s2.derecho != null) {
                rival2 = s2.derecho.equipo.nombre;
            } else {
                rival2 = "?";
            }
            System.out.println("Semifinal 2: " + s2.izquierdo.equipo.nombre + " vs " + rival2);
            
            String txtG2;
            if (s2.equipo != null) {
                txtG2 = s2.equipo.nombre;
            } else {
                txtG2 = "Pendiente";
            }
            System.out.println("Ganador: " + txtG2);
        } else {
            System.out.println("Semifinal 2: (sin equipos aun)");
        }

        System.out.println();

        if (s1.equipo != null && s2.equipo != null) {
            System.out.println("Final: " + s1.equipo.nombre + " vs " + s2.equipo.nombre);
            
            String txtCampeon;
            if (finalT.equipo != null) {
                txtCampeon = finalT.equipo.nombre;
            } else {
                txtCampeon = "Pendiente";
            }
            System.out.println("Campeon: " + txtCampeon);
        } else {
            System.out.println("Final: (pendiente de semifinales)");
        }
        System.out.println("================================");
    }
}