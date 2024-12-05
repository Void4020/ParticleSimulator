// NOTE: I separtated these two events into different classes for better organization, and so I can make instances of the different collision events.

public class WallCollisionEvent extends Event {
    private Particle _particle;
    private int _wall; // 1: Right, 2: Left, 3: Bottom, 4: Tops

    WallCollisionEvent(double timeOfEvent, double timeOfEventCreated, Particle particle, int wall) {
        super(timeOfEvent, timeOfEventCreated);
        _particle = particle;
        _wall = wall;
    }

    public Particle getParticle() {
        return _particle;
    }

    public int getWall() {
        return _wall;
    }

    /**
     * Returns whether this wall collision event is valid
     */
    public boolean isValid() {
        return _particle.isValid(_timeEventCreated); // ADDED AFTER DEADLINE (changed from _timeOfEvent to _timeEventCreated)
    }
}