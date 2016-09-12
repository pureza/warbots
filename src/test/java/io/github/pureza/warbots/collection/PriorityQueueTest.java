package io.github.pureza.warbots.collection;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

public class PriorityQueueTest {

    /*
     * PriorityQueue(E[] elements, Comparator<E> comparator)
     */

    @Test
    public void constructorCreatesEmptyQueue() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Integer[0], null);
        assertThat(queue.isEmpty(), is(true));
    }


    @Test
    public void constructorCreatesQueueFromUnsortedArray() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Integer[] { 8, 2, 3, 6, 0, 1, 5 }, null);
        assertThat(sort(queue), contains(0, 1, 2, 3, 5, 6, 8));
    }


    @Test
    public void constructorWithCustomComparatorSortsWithComparator() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Integer[] { 8, 2, 3, 6, 0, 1, 5 }, Comparator.reverseOrder());
        assertThat(sort(queue), contains(8, 6, 5, 3, 2, 1, 0));
    }


    /*
     * int offer(E element)
     */

    @Test
    public void offerAddsFirstElement() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        assertThat(queue.offer(1), is(0));
    }


    @Test
    public void offerAddsSmallerElement() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.offer(1);
        assertThat(queue.offer(0), is(0));
        assertThat(sort(queue), contains(0, 1));
    }


    @Test
    public void offerAddsBiggerElement() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.offer(1);
        assertThat(queue.offer(2), is(1));
        assertThat(sort(queue), contains(1, 2));
    }


    @Test
    public void offerAddsManyElementsInOrder() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.offer(8);
        queue.offer(2);
        queue.offer(3);
        queue.offer(6);
        queue.offer(0);
        queue.offer(1);
        queue.offer(5);
        assertThat(sort(queue), contains(0, 1, 2, 3, 5, 6, 8));
    }


    @Test
    public void offerExpandsStorageAutomatically() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(1, null);
        queue.offer(8);
        queue.offer(2);
        queue.offer(3);
        queue.offer(6);
        queue.offer(0);
        queue.offer(1);
        queue.offer(5);
        assertThat(sort(queue), contains(0, 1, 2, 3, 5, 6, 8));
    }


    /*
     * E poll()
     */


    @Test
    public void pollEmptyQueueReturnsNull() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        assertThat(queue.poll(), is(nullValue()));
    }


    @Test
    public void pollReturnsSmallestElement() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Integer[] { 7, 4, 6, 0, 1, 3, 4 }, null);
        assertThat(queue.poll(), is(0));
    }


    @Test
    public void pollRemovesSmallestElement() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Integer[] { 7, 4, 6, 0, 1, 3, 4 }, null);
        assertThat(queue.poll(), is(0));
        assertThat(queue.poll(), is(1));
    }


    /*
     * E peek()
     */


    @Test
    public void peekEmptyQueueReturnsNull() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        assertThat(queue.peek(), is(nullValue()));
    }


    @Test
    public void peekReturnsSmallestElement() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Integer[] { 7, 4, 6, 0, 1, 3, 4 }, null);
        assertThat(queue.peek(), is(0));
    }


    @Test
    public void peekDoesNotRemoveSmallestElement() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Integer[] { 7, 4, 6, 0, 1, 3, 4 }, null);
        assertThat(queue.peek(), is(0));
        assertThat(queue.peek(), is(0));
    }


    /*
     * int update(int index, E element)
     */

    @Test
    public void updateMovesElementUp() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(1, null);
        queue.offer(8);
        queue.offer(2);
        queue.offer(3);
        queue.offer(6);
        queue.offer(0);
        queue.offer(1);

        // 9 is the largest element, so it will be added at the end
        assertThat(queue.offer(9), is(6));

        assertThat(queue.update(6, -1), is(0));
        assertThat(sort(queue), contains(-1, 0, 1, 2, 3, 6, 8));
    }


    @Test
    public void updateMovesElementDown() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(1, null);
        queue.offer(8);
        queue.offer(2);
        queue.offer(3);
        queue.offer(6);
        queue.offer(0);
        queue.offer(1);

        assertThat(((Comparable[]) queue.elements)[2], is(1));

        // 1 -> 9
        assertThat(queue.update(1, 9), is(4));
        assertThat(sort(queue), contains(0, 1, 3, 6, 8, 9));
    }


    @Test(expected=IndexOutOfBoundsException.class)
    public void updateFailsIfOutOfBounds() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.update(0, 3);
    }


    /*
     * int size()
     */

    @Test
    public void sizeReturnsZeroForEmptyQueue() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        assertThat(queue.size(), is(0));
    }


    @Test
    public void sizeReturnsNumberOfElementsInQueue() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Integer[] { 7, 4, 6, 0, 1, 3, 4 }, null);
        assertThat(queue.size(), is(7));
    }


    @Test
    public void offerIncreasesSize() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        assertThat(queue.size(), is(0));

        queue.offer(1);
        assertThat(queue.size(), is(1));
    }


    @Test
    public void pollReducesSize() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.offer(1);

        assertThat(queue.size(), is(1));

        queue.poll();
        assertThat(queue.size(), is(0));
    }


    /*
     * boolean isEmpty()
     */

    @Test
    public void isEmptyReturnsTrueIfQueueIsEmpty() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        assertThat(queue.isEmpty(), is(true));
    }


    @Test
    public void isEmptyReturnsFalseIfQueueIsNotEmpty() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.offer(1);
        assertThat(queue.isEmpty(), is(false));
    }


    private List<Integer> sort(PriorityQueue<Integer> queue) {
        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            result.add(queue.poll());
        }

        return result;
    }
}
