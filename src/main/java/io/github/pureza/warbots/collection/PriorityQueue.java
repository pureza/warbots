package io.github.pureza.warbots.collection;

import java.util.Comparator;


/**
 * A Priority Queue implemented with a Heap Binary Tree
 *
 * This is a basically simpler java.util.PriorityQueue adapted for our use
 * case.
 */
public class PriorityQueue<E extends Comparable<E>> {

    /** Queue's initial size */
    protected static final int INITIAL_SIZE = 16;

    /** Queue's elements */
    protected E[] elements;

    /** Queue's current size */
    protected int size = 0;

    /**
     * Comparator to use for sorting elements. If none is given, sorts by
     * natural order
     */
    protected Comparator<E> comparator;


    /**
     * Creates an empty queue
     */
    public PriorityQueue() {
        this(INITIAL_SIZE, null);
    }


    /**
     * Creates a new queue from the given array of elements
     *
     * The elements do not need to be sorted beforehand.
     */
    public PriorityQueue(E[] elements, Comparator<E> comparator) {
        this.elements = elements.clone();
        this.size = elements.length;
        this.comparator = comparator;

        // Sorts the elements
        for (int i = size / 2 - 1; i >= 0; i--) {
            moveDown(i);
        }
    }


    /**
     * Creates a queue with the given size and comparator
     */
    public PriorityQueue(int length, Comparator<E> comparator) {
        this.elements = (E[]) new Comparable[length];
        this.comparator = comparator;
    }


    /**
     * Adds an element to the queue
     *
     * Automatically expands the queue if necessary.
     *
     * Returns the index of this new element
     */
    public int offer(E element) {
        // Double the size if necessary
        if (size == elements.length) {
            expand(2 * size);
        }

        elements[size] = element;
        return moveUp(size++);
    }


    /**
     * Removes and returns the lowest element from the queue
     *
     * Returns null if the queue is empty.
     */
    public E poll() {
        if (size == 0) {
            return null;
        }

        E head = elements[0];

        // Places the last element at the top
        swap(0, --size);

        // Move it down until it reaches its new position
        moveDown(0);

        return head;
    }


    /**
     * Returns the lowest element of the queue, without removing it
     */
    public E peek() {
        return elements[0];
    }


    /**
     * Updates the priority of the element at the given index
     *
     * Returns the new index of the element.
     */
    public int update(int index, E element) {
        if (!(index >= 0 && index < size())) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        E old = elements[index];
        elements[index] = element;
        if (compare(element, old) < 0) {
            return moveUp(index);
        } else {
            return moveDown(index);
        }
    }


    /**
     * Returns the number of elements in the queue
     */
    public int size() {
        return size;
    }


    /**
     * Checks if the queue is empty or not
     */
    public boolean isEmpty() {
        return size == 0;
    }


    /**
     * Moves an element towards the top of the heap, until it becomes valid
     * again
     *
     * Returns the index where the element was put
     */
    protected int moveUp(int index) {
        int parentIndex = (index - 1) / 2;
        int currentIndex = index;

        E current = elements[currentIndex];

        // Swap while the parent is greater
        while (compare(current, elements[parentIndex]) < 0 && currentIndex > 0) {
            swap(currentIndex, parentIndex);
            currentIndex = parentIndex;
            parentIndex = (currentIndex - 1) / 2;
        }

        return currentIndex;
    }

    /**
     * Moves an element towards the bottom of the queue until it becomes valid
     * again
     *
     * Returns the index where the element was put.
     */
    protected int moveDown(int index) {
        int currentIndex = index;
        E current = elements[currentIndex];

        int leftChildIndex = 2 * currentIndex + 1;
        int rightChildIndex = 2 * currentIndex + 2;
        int smallerIndex;

        // While we are not at the end of the heap...
        while (leftChildIndex < size) {
            // If I only have one son...
            if (rightChildIndex == size) {
                // If the parent is greater than the son, swap them
                if (compare(current, elements[leftChildIndex]) > 0) {
                    smallerIndex = leftChildIndex;
                    swap(currentIndex, smallerIndex);
                    currentIndex = smallerIndex;
                }

                // Break the cycle, as I'm at the bottom of the tree
                leftChildIndex = size;
            } else {
                // Find the index of the smallest child
                smallerIndex = (compare(elements[leftChildIndex], elements[rightChildIndex]) < 0
                        ? leftChildIndex
                        : rightChildIndex);
                
                // If the parent is greater than the smallest child, swap them
                if (compare(current, elements[smallerIndex]) > 0) {
                    swap(currentIndex, smallerIndex);

                    // Repeat with the next level
                    currentIndex = smallerIndex;
                    leftChildIndex = 2 * currentIndex + 1;
                    rightChildIndex = 2 * currentIndex + 2;
                } else {
                    // Otherwise, the parent is at the right position and we can
                    // break the loop
                    leftChildIndex = size;
                }
            }
        }

        return currentIndex;
    }


    /**
     * Swaps the elements at the two given indices
     */
    protected void swap(int indexA, int indexB) {
        E tmp = elements[indexA];
        elements[indexA] = elements[indexB];
        elements[indexB] = tmp;
    }

    /**
     * Expands the queue to the given size
     */
    protected void expand(int newSize) {
        E[] newContents = (E[]) new Comparable[newSize];
        System.arraycopy(elements, 0, newContents, 0, elements.length);
        elements = newContents;
    }


    /**
     * Compares two elements
     *
     * If the comparator exists, uses it. Otherwise, sorts the elements by
     * natural order
     */
    protected int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }

        return a.compareTo(b);
    }
}

