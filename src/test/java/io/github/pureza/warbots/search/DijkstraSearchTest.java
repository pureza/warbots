package io.github.pureza.warbots.search;

import io.github.pureza.warbots.collection.Graph;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class DijkstraSearchTest {

    private Graph<String, Double> map;

    @Before
    public void setUp() {
        map = new Graph<>();

        map.add("Aveiro");
        map.add("Braga");
        map.add("Braganca");
        map.add("Beja");
        map.add("Castelo Branco");
        map.add("Coimbra");
        map.add("Evora");
        map.add("Faro");
        map.add("Guarda");
        map.add("Leiria");
        map.add("Lisboa");
        map.add("Portalegre");
        map.add("Porto");
        map.add("Santarem");
        map.add("Setubal");
        map.add("Viana do Castelo");
        map.add("Vila Real");
        map.add("Viseu");

        map.addEdge("Aveiro", "Porto", 68.0);
        map.addEdge("Aveiro", "Viseu", 95.0);
        map.addEdge("Aveiro", "Coimbra", 58.0);
        map.addEdge("Aveiro", "Leiria", 115.0);

        map.addEdge("Braga", "Viana do Castelo", 48.0);
        map.addEdge("Braga", "Vila Real", 106.0);
        map.addEdge("Braga", "Porto", 53.0);

        map.addEdge("Braganca", "Vila Real", 137.0);
        map.addEdge("Braganca", "Guarda", 202.0);

        map.addEdge("Beja", "Evora", 78.0);
        map.addEdge("Beja", "Faro", 152.0);
        map.addEdge("Beja", "Setubal", 142.0);

        map.addEdge("Castelo Branco", "Coimbra", 159.0);
        map.addEdge("Castelo Branco", "Guarda", 106.0);
        map.addEdge("Castelo Branco", "Portalegre", 80.0);
        map.addEdge("Castelo Branco", "Evora", 203.0);

        map.addEdge("Evora", "Lisboa", 150.0);
        map.addEdge("Evora", "Santarem", 117.0);
        map.addEdge("Evora", "Portalegre", 131.0);
        map.addEdge("Evora", "Setubal", 103.0);

        map.addEdge("Coimbra", "Viseu", 96.0);
        map.addEdge("Coimbra", "Leiria", 67.0);

        map.addEdge("Faro", "Setubal", 249.0);
        map.addEdge("Faro", "Lisboa", 299.0);
        map.addEdge("Guarda", "Vila Real", 157.0);
        map.addEdge("Guarda", "Viseu", 85.0);
        map.addEdge("Leiria", "Lisboa", 129.0);
        map.addEdge("Leiria", "Santarem", 70.0);
        map.addEdge("Lisboa", "Santarem", 78.0);
        map.addEdge("Porto", "Viana do Castelo", 71.0);
        map.addEdge("Porto", "Vila Real", 116.0);
        map.addEdge("Porto", "Viseu", 133.0);
        map.addEdge("Vila Real", "Viseu", 110.0);

        Map<String, Integer> distancesToFaro = new HashMap<>();
        distancesToFaro.put("Aveiro", 363);
        distancesToFaro.put("Braga", 454);
        distancesToFaro.put("Braganca", 487);
        distancesToFaro.put("Beja", 99);
        distancesToFaro.put("Castelo Branco", 280);
        distancesToFaro.put("Coimbra", 319);
        distancesToFaro.put("Evora", 157);
        distancesToFaro.put("Faro", 0);
        distancesToFaro.put("Guarda", 352);
        distancesToFaro.put("Leiria", 278);
        distancesToFaro.put("Lisboa", 195);
        distancesToFaro.put("Portalegre", 228);
        distancesToFaro.put("Porto", 418);
        distancesToFaro.put("Santarem", 228);
        distancesToFaro.put("Setubal", 168);
        distancesToFaro.put("Viana do castelo", 473);
        distancesToFaro.put("Vila Real", 429);
        distancesToFaro.put("Viseu", 363);
    }


    /*
     * DijkstraSearch(Graph<V, Double> graph, V source, Predicate<State> terminationCondition)
     */

    @Test(expected=NoSuchElementException.class)
    public void constructorFailsIfSourceVertexDoesNotExist() {
        new DijkstraSearch<String>(map, "Tondela", new FindTargetCondition<String>(map, "Faro"));
    }


    /*
     * Path<V> search()
     */

    @Test
    public void searchFindsSource() throws NoPathFoundException {
        DijkstraSearch<String> searcher = new DijkstraSearch<>(map, "Faro", new FindTargetCondition<>(map, "Faro"));
        assertThat(searcher.search().getLocations(), contains("Faro"));
    }


    @Test
    public void searchFindsNeighbour() throws NoPathFoundException {
        DijkstraSearch<String> searcher = new DijkstraSearch<>(map, "Setubal", new FindTargetCondition<>(map, "Faro"));
        assertThat(searcher.search().getLocations(), contains("Setubal", "Faro"));
    }


    @Test
    public void searchFindsShortestDistanceBetweenCoimbraAndFaro() throws NoPathFoundException {
        DijkstraSearch<String> searcher = new DijkstraSearch<>(map, "Coimbra", new FindTargetCondition<>(map, "Faro"));
        List<String> cities = searcher.search().getLocations();
        assertThat(cities, contains("Coimbra", "Leiria", "Santarem", "Evora", "Beja", "Faro"));
    }


    @Test(expected=NoPathFoundException.class)
    public void searchFailsIfThereIsNoPath() throws NoPathFoundException {
        map.add("Tondela");

        DijkstraSearch<String> searcher = new DijkstraSearch<>(map, "Tondela", new FindTargetCondition<>(map, "Faro"));
        searcher.search();
    }
}
