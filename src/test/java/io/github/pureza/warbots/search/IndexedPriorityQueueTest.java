package io.github.pureza.warbots.search;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class IndexedPriorityQueueTest {

    /*
     * int indexOf(V vertex)
     */

    @Test
    public void indexOfReturnsNegativeNumberIfQueueIsEmpty() {
        IndexedPriorityQueue<Integer> queue = new IndexedPriorityQueue<>();
        assertThat(queue.indexOf(1), is(-1));
    }


    @Test
    public void indexOfReturnsNegativeNumberIfElementDoesNotExist() {
        IndexedPriorityQueue<Integer> queue = new IndexedPriorityQueue<>();
        queue.offer(new AStarSearch.State<>(0, null, 0, 0));
        assertThat(queue.indexOf(1), is(-1));
    }


    @Test
    public void indexOfReturnsElementIndex() {
        IndexedPriorityQueue<Integer> queue = new IndexedPriorityQueue<>();
        queue.offer(new AStarSearch.State<>(3, null, 0, 0));
        queue.offer(new AStarSearch.State<>(5, null, 0, 0));
        queue.offer(new AStarSearch.State<>(1, null, 0, 0));
        queue.offer(new AStarSearch.State<>(7, null, 0, 0));
        queue.offer(new AStarSearch.State<>(2, null, 0, 0));
        assertThat(queue.get(queue.indexOf(1)).vertex(), is(1));
        assertThat(queue.get(queue.indexOf(2)).vertex(), is(2));
        assertThat(queue.get(queue.indexOf(3)).vertex(), is(3));
        assertThat(queue.get(queue.indexOf(5)).vertex(), is(5));
        assertThat(queue.get(queue.indexOf(7)).vertex(), is(7));
    }


    @Test
    public void indexOfWorksAfterMultipleChanges() {
        IndexedPriorityQueue<Integer> queue = new IndexedPriorityQueue<>();
        queue.offer(new AStarSearch.State<>(3, null, 0, 3));
        queue.offer(new AStarSearch.State<>(5, null, 0, 5));
        queue.offer(new AStarSearch.State<>(1, null, 0, 1));
        queue.offer(new AStarSearch.State<>(7, null, 0, 7));
        queue.poll();
        queue.offer(new AStarSearch.State<>(2, null, 0, 2));
        queue.poll();
        queue.offer(new AStarSearch.State<>(4, null, 0, 4));
        queue.offer(new AStarSearch.State<>(6, null, 0, 6));
        queue.poll();

        assertThat(queue.get(queue.indexOf(4)).vertex(), is(4));
        assertThat(queue.get(queue.indexOf(5)).vertex(), is(5));
        assertThat(queue.get(queue.indexOf(6)).vertex(), is(6));
        assertThat(queue.get(queue.indexOf(7)).vertex(), is(7));
    }
}
