package com.hasunemiku2015.metrofare.lookuptables.datatables;

import com.hasunemiku2015.metrofare.MetroFare;
import de.vogella.algorithms.dijkstra.engine.DijkstraAlgorithm;
import de.vogella.algorithms.dijkstra.model.Edge;
import de.vogella.algorithms.dijkstra.model.Graph;
import de.vogella.algorithms.dijkstra.model.Vertex;

import java.io.*;
import java.util.*;

public class DataTable {
    //Var
    private Graph graph;
    private final String name;
    private String password;

    //FileIO
    protected DataTable(File file) throws IOException {
        //Return if not CSV file
        if (!file.getName().endsWith(".csv")) {
            throw new FileNotFoundException();
        }

        name = file.getName().replace(".csv", "");
        ObjInit();

        try (Scanner sc = new Scanner(file)) {
            password = sc.nextLine().split(",")[1];
            password = password.replace(" ", "");

            //Discard Header Line
            sc.nextLine();
            while (sc.hasNext()) {
                //name,from,to,weight
                String[] data = sc.nextLine().split(",");

                //create Edge
                addEdge(data[0], data[1], data[2], Integer.parseInt(data[3]));
            }
        }
    }

    protected DataTable(String name, String password) throws IOException {
        this.name = name;
        this.password = password;

        File file = new File(MetroFare.PLUGIN.getDataFolder() + "/DataTables", name + ".csv");
        file.createNewFile();
        ObjInit();
    }

    protected void exportToCSV() throws IOException {
        File file = new File(MetroFare.PLUGIN.getDataFolder() + "/DataTables", name + ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write("Password:," + password);
            writer.newLine();
            writer.write("Edge Name,Source Node,Destination Node,Weight");
            writer.newLine();
            for (Edge e : graph.getEdges()) {
                String sb = e.getId() + "," + e.getSource().getName() + ","
                        + e.getDestination().getName() + "," + e.getWeight();
                writer.write(sb);
                writer.newLine();
            }
        }
    }

    //Object methods
    private void ObjInit() {
        List<Vertex> vert = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        graph = new Graph(vert, edges);
    }

    @SuppressWarnings("DuplicatedCode")
    protected void addEdge(String Name, String source, String destination, int weight) {
        if (source.equals(destination)) {
            return;
        }

        List<Vertex> vertexList = graph.getVertexes();
        List<Edge> edgeList = graph.getEdges();


        Vertex v0 = null;
        Vertex v1 = null;

        for (Vertex v : graph.getVertexes()) {
            if (v.getName().equals(source)) {
                v0 = v;
            }

            if (v.getName().equals(destination)) {
                v1 = v;
            }
        }
        if (v0 == null) {
            v0 = new Vertex(source, source);
            vertexList.add(v0);
        }
        if (v1 == null) {
            v1 = new Vertex(destination, destination);
            vertexList.add(v1);
        }

        Edge e = new Edge(Name, v0, v1, weight);
        edgeList.add(e);

        graph = new Graph(vertexList, edgeList);
    }

    protected void addEdge(String source, String destination, int weight) {
        addEdge(UUID.randomUUID().toString(), source, destination, weight);
    }

    protected void removeEdge(String source, String destination) {
        List<Vertex> vertexList = graph.getVertexes();
        List<Edge> edgeList = graph.getEdges();

        for (Edge e : edgeList) {
            if (e.getSource().getName().equals(source) && e.getDestination().getName().equals(destination)) {
                edgeList.remove(e);
                graph = new Graph(vertexList, edgeList);
                return;
            }
        }
    }

    protected boolean checkWrongPassword(String s) {
        return !s.equals(password);
    }

    protected String getPassword() {
        return password;
    }

    public List<Vertex> getVertices() {
        return graph.getVertexes();
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("DuplicatedCode")
    public double ComputeFare(String from, String to) {
        Vertex source = null;
        Vertex target = null;

        for (Vertex v : graph.getVertexes()) {
            if (v.getName().equals(from)) {
                source = v;
            }

            if (v.getName().equals(to)) {
                target = v;
            }
        }

        if (source != null && target != null) {
            DijkstraAlgorithm algo = new DijkstraAlgorithm(graph);
            algo.execute(source);
            int Fare = algo.getShortestDistance(target);
            if (Fare <= 1000000) {
                return Fare / 1000.0;
            }
        }
        return -1;
    }
}
