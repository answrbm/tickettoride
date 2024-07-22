package ansarbektassov.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Town implements Comparable<Town> {

    private String name;
    private boolean visited;
    private final List<Route> neighbours;
    private Integer distance;
    private Town previousTown;

    public Town(String name) {
        this.name = name;
        this.neighbours = new ArrayList<>();
        this.distance = Integer.MAX_VALUE;
    }

    public void addNeighbour(Route neighbour) {
        neighbours.add(neighbour);
    }

    @Override
    public int compareTo(Town o) {
        return Integer.compare(this.getDistance(),o.getDistance());
    }

}
