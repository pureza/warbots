package io.github.pureza.warbots.search;

import io.github.pureza.warbots.collection.PriorityQueue;

import java.util.HashMap;
import java.util.Map;


/**
 * Priority queue that supports indexing
 *
 * Indexing allows us to modify the priority of an element, which is useful for
 * graph searching algorithms.
 */
public class IndexedPriorityQueue<V> extends PriorityQueue<AStarSearch.State<V>> {

    /** Keeps track of the index of each vertex in this queue */
    protected Map<V, Integer> indices = new HashMap<>();


    @Override
    public int offer(AStarSearch.State<V> state) {
        int index = super.offer(state);
        indices.put(state.vertex(), index);
        return index;
    }


    @Override
    public AStarSearch.State<V> poll() {
        AStarSearch.State<V> head = super.poll();
        indices.remove(head.vertex());
        return head;
    }


    /**
     * Returns the index of a vertex in this queue, or -1 if it isn't there
     */
    public int indexOf(V vertex) {
        if (!indices.containsKey(vertex)) {
            return -1;
        }

        return indices.get(vertex);
    }


    /**
     * Returns the state at the given index
     */
    public AStarSearch.State<V> get(int index) {
        return (AStarSearch.State<V>) ((Comparable[]) this.elements)[index];
    }


    @Override
    protected void swap(int indexA, int indexB) {    	
        // Swaps the indices
        indices.put(get(indexA).vertex(), indexB);
        indices.put(get(indexB).vertex(), indexA);

        super.swap(indexA, indexB);
    }
}
