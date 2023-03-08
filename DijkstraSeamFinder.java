package seamcarving;

import graphs.Edge;
import graphs.Graph;
import graphs.shortestpaths.DijkstraShortestPathFinder;
import graphs.shortestpaths.ShortestPath;
import graphs.shortestpaths.ShortestPathFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraSeamFinder implements SeamFinder {

    public DijkstraSeamFinder() {
        ShortestPathFinder<Graph<String, Edge<String>>, String, Edge<String>> pathFinder = createPathFinder();
    }

    protected <G extends Graph<V, Edge<V>>, V> ShortestPathFinder<G, V, Edge<V>> createPathFinder() {
        return new DijkstraShortestPathFinder<>();
    }
    private static class AdjacencyListDirectedGraph implements Graph<String, Edge<String>> {
        protected final Map<String, Set<Edge<String>>> adjacencyList;

        public AdjacencyListDirectedGraph(Collection<Edge<String>> edges) {
            this.adjacencyList = new HashMap<>();
            edges.forEach(e -> adjacencyList.computeIfAbsent(e.from(), v -> new HashSet<>()).add(e));
        }

        @Override
        public Set<Edge<String>> outgoingEdgesFrom(String vertex) {
            return Collections.unmodifiableSet(adjacencyList.getOrDefault(vertex, Set.of()));
        }
    }
    private void addRightPix(String start, double[][] energies, Collection<Edge<String>> edges) {
        for (int i = 0; i < 3; i++) {
            String[] xy = start.split(" ");
            int x = Integer.parseInt(xy[0])+1;
            int y = Integer.parseInt(xy[1])+i-1;
            if (y >= 0 && y < energies.length && x < energies[0].length) {
                String next = x + " " + y;
                edges.add(new Edge<>(start, next, energies[y][x]));
            }
        }
    }

    public Graph<String, Edge<String>> makeGraph(double[][] energies) {
        Collection<Edge<String>> edges = new ArrayList<>();
        for (int y = 0; y < energies.length; y++) {
            String begin = "-1 -1";
            String end = "-2 -2";
            String startD = 0 + " " + y;
            String endD = energies[0].length - 1 + " " + y;
            edges.add(new Edge<>(begin, startD, energies[y][0]));
            edges.add(new Edge<>(endD, end, 0));
            for (int x = 0; x < energies[0].length; x++) {
                String now = x + " " + y;
                addRightPix(now, energies, edges);
            }
        }
        return new AdjacencyListDirectedGraph(edges);
    }
    public double[][] rotate(double[][] a) {
        double[][] b = new double[a[0].length][a.length];
        for (int i = 0; i < a.length; i++) {
            int end = a[0].length - 1;
            for (int j = 0; j < a[0].length; j++, end--) {
                b[end][i] = a[i][j];
            }
        }
        return b;
    }
    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        double[][] energiesH = rotate(energies);
        List<Integer> result = findVerticalSeam(energiesH);
        result.replaceAll(integer -> energies[0].length - integer - 1);
        return result;
    }

    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        List<Integer> result = new ArrayList<>();
        ShortestPathFinder<Graph<String, Edge<String>>, String, Edge<String>> spf = createPathFinder();
        Graph<String, Edge<String>> graph = makeGraph(energies);
        String begin = "-1 -1";
        String end = "-2 -2";
        ShortestPath<String, Edge<String>> minSp = spf.findShortestPath(graph, begin, end);
        Collection<Edge<String>> thing = minSp.edges();
        for (Edge<String> temp : thing) {
            result.add(Integer.parseInt(temp.from().split(" ")[1]));
        }
        result.remove(0);
        return result;
    }
}
