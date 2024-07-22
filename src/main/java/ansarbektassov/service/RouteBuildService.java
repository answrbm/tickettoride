package ansarbektassov.service;

import ansarbektassov.exception.TownNotFoundException;
import ansarbektassov.model.Route;
import ansarbektassov.model.Town;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

@Component
@Slf4j
public class RouteBuildService {

    private final List<Town> towns;
    private final Queue<Town> townQueue;

    {
        log.info("Initializer block started");
        this.towns = new ArrayList<>();
        Town london = new Town("London");
        Town reading = new Town("Reading");
        Town northhampton = new Town("Northhampton");
        Town coventry = new Town("Coventry");
        Town swindon = new Town("Swindon");
        Town bristol = new Town("Bristol");
        Town birgmingham = new Town("Birgmingham");

        london.addNeighbour(new Route(london,northhampton,2));
        london.addNeighbour(new Route(london,reading,1));

        northhampton.addNeighbour(new Route(northhampton,london,2));
        northhampton.addNeighbour(new Route(northhampton,coventry,2));

        reading.addNeighbour(new Route(reading,london,1));
        reading.addNeighbour(new Route(reading,swindon,4));

        coventry.addNeighbour(new Route(coventry,northhampton,2));
        coventry.addNeighbour(new Route(coventry,birgmingham,1));

        swindon.addNeighbour(new Route(swindon,reading,4));
        swindon.addNeighbour(new Route(swindon,bristol,2));
        swindon.addNeighbour(new Route(swindon,birgmingham,4));

        bristol.addNeighbour(new Route(bristol,swindon,2));
        bristol.addNeighbour(new Route(bristol,birgmingham,3));

        birgmingham.addNeighbour(new Route(birgmingham,coventry,1));
        birgmingham.addNeighbour(new Route(birgmingham,swindon,4));
        birgmingham.addNeighbour(new Route(birgmingham,bristol,3));

        towns.add(london);
        towns.add(reading);
        towns.add(northhampton);
        towns.add(coventry);
        towns.add(swindon);
        towns.add(bristol);
        towns.add(birgmingham);
        log.info("Towns are created and added");
    }

    public RouteBuildService() {
        this.townQueue = new PriorityQueue<>();
    }

    /**
     * Computes the shortest segments for the departure town
     * <p>
     *     This method computes the shortest segments from {@code Town departure} to other towns.
     *     Here is applied the dijkstra algorithm. Set initial distances for all towns: 0 for the departure town,
     *     and {@code Integer.MAX_VALUE} for all the other towns. Choose the unvisited town with the shortest distance
     *     from the start to be the current town. So the algorithm will always start with the departure town as
     *     the current. For each of the current towns unvisited neighbor towns, calculate the distance from the
     *     departure and update the distance if the new, calculated, distance is lower. When it's done with the current
     *     town, it's marked as visited. A visited town is not checked again. Then a new cycle to choose a new current
     *     town, and keep repeating these steps until all towns are visited. In the end there are towns with the shortest
     *     path from the source town to every other town.
     * </p>
     * @param departure the departure town is the starting point
     */
    private void computeTravels(Town departure) {
        log.info("computeTravels() called");
        departure.setDistance(0);
        log.debug("depature town: " + departure);
        townQueue.add(departure);

        while(!townQueue.isEmpty()) {
            Town currentTown = townQueue.poll();
            log.debug("current town: " + currentTown);

            for(Route r : currentTown.getNeighbours()) {
                Town endTown = r.getArrival();
                log.debug("neighbour town: " + currentTown);
                int segments = r.getSegments();

                if(!endTown.isVisited()) {
                    if(currentTown.getDistance() + segments < endTown.getDistance()) {
                        endTown.setDistance(currentTown.getDistance() + segments);
                        log.debug("neighbour town new distance: " + (currentTown.getDistance() + segments));

                        if(townQueue.contains(endTown))
                            townQueue.remove(endTown);

                        endTown.setPreviousTown(currentTown);
                        townQueue.add(endTown);
                    }
                }
            }

            currentTown.setVisited(true);
            log.debug("current town set visited: " + currentTown);
        }
    }

    public Integer getShortestTravel(String departure, String arrival) {
        log.info("getShortestTravel() called");
        Town departureTown = findTownByName(departure);
        Town arrivalTown = findTownByName(arrival);

        computeTravels(departureTown);
        return arrivalTown.getDistance();
    }

    public Town findTownByName(String townName) {
        log.info("findTownByName() called");
        return towns.stream().filter(town -> town.getName().equals(townName)).findAny().orElseThrow(() ->
                new TownNotFoundException("Town " + townName + " not found!"));
    }

    public List<Town> getTowns() {
        return towns;
    }
}
