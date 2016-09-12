package io.github.pureza.warbots.search;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

public class PathTest {

    /*
     * static <T> Path<T> emptyPath()
     */

    @Test
    public void emptyPathIsEmpty() {
        assertThat(Path.emptyPath().getLocations(), is(empty()));
    }


    @Test(expected=UnsupportedOperationException.class)
    public void cantAppendToEmptyPath() {
        Path.emptyPath().append(1);

    }


    @Test(expected=UnsupportedOperationException.class)
    public void cantPrependToEmptyPath() {
        Path.emptyPath().prepend(1);
    }


    /*
     * Path()
     */

    @Test
    public void defaultConstructorCreatesAnEmptyPath() {
        assertThat(new Path(), Is.is(Path.emptyPath()));
    }


    /*
     * Path(T... locations)
     */

    @Test
    public void varArgConstructorCreatesPathWithLocationsInOrder() {
        assertThat(new Path<>(1, 2, 3).getLocations(), is(asList(1, 2, 3)));
    }


    @Test
    public void varArgsConstructorProducesMutablePath() {
        Path<Integer> path = new Path<>(1, 2, 3);
        path.append(4);
        assertThat(path.getLocations(), contains(1, 2, 3, 4));
    }


    /*
     * Path(LinkedList<T> locations)
     */

    @Test
    public void constructorCreatesPathWithGivenLocations() {
        List<Integer> locations = asList(1, 2, 3);
        assertThat(new Path<>(locations).getLocations(), is(locations));
    }


    @Test
    public void constructorCopiesListWithLocations() {
        List<Integer> locations = new LinkedList<>(asList(1, 2, 3));
        Path<Integer> path = new Path<>(locations);

        locations.clear();
        assertThat(path.getLocations(), contains(1, 2, 3));
    }


    /*
     * void prepend(T location)
     */

    @Test
    public void prependAddsLocationToBeginningOfPath() {
        Path<Integer> path = new Path<>(asList(1, 2));
        path.prepend(0);
        assertThat(path.getLocations(), contains(0, 1, 2));
    }


    /*
     * void append(T location)
     */

    @Test
    public void appendAddsLocationToEndOfPath() {
        Path<Integer> path = new Path<>(asList(1, 2));
        path.append(0);
        assertThat(path.getLocations(), contains(1, 2, 0));
    }


    /*
     * List<T> getLocations()
     */

    @Test
    public void getLocationsReturnsEmptyForTheEmptyPath() {
        assertThat(new Path<>().getLocations(), is(empty()));
    }


    @Test
    public void getLocationsReturnsThePathLocations() {
        List<Integer> locations = asList(1, 2, 3);
        assertThat(new Path<>(locations).getLocations(), is(locations));
    }


    @Test(expected=UnsupportedOperationException.class)
    public void getLocationsReturnsAnUnmodifiableList() {
        List<Integer> locations = asList(1, 2, 3);
        new Path<>(locations).getLocations().add(1);
    }
    
    
    /*
     T getSource()
     */
    
    @Test(expected=NoSuchElementException.class)
    public void getSourceFailsForEmptyPath() {
        new Path<>().getSource();
    }
    
    
    @Test
    public void getSourceReturnsInitialLocation() {
        assertThat(new Path<>(asList(1, 2, 3)).getSource(), is(1));
    }
    
    
    /*
     T getTarget()
     */

    @Test(expected=NoSuchElementException.class)
    public void getTargetFailsForEmptyPath() {
        new Path<>().getTarget();
    }


    @Test
    public void getTargetReturnsFinalLocation() {
        assertThat(new Path<>(asList(1, 2, 3)).getTarget(), is(3));
    }


    /*
     * int size()
     */

    @Test
    public void sizeReturnsZeroForAnEmptyPath() {
        assertThat(new Path<>().size(), is(0));
    }


    @Test
    public void sizeReturnsTheNumberOfLocationsInThePath() {
        assertThat(new Path<>(1, 2, 3).size(), is(3));
    }


    /*
     * T get(int i)
     */

    @Test
    public void getReturnsTheIthLocation() {
        assertThat(new Path<>(1, 2, 3).get(2), is(3));
    }


    @Test(expected=IndexOutOfBoundsException.class)
    public void getFailsWhenIndexIsOutOfBounds() {
        new Path<>().get(0);
    }
}
