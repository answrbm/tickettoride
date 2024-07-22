package ansarbektassov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Route {

    private Town departure;
    private Town arrival;
    private int segments;
}
