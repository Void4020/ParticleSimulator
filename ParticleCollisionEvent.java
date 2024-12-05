// NOTE: I separtated these two events into different classes for better organization, and so I can make instances of the different collision events.


public class ParticleCollisionEvent extends Event {
    private Particle _particle1;
    private Particle _particle2;

    ParticleCollisionEvent(double timeOfEvent, double timeOfEventCreated, Particle p1, Particle p2) {
        super(timeOfEvent, timeOfEventCreated);
        _particle1 = p1;
        _particle2 = p2;
    }

    public Particle getParticle1() {
        return _particle1;
    }

    public Particle getParticle2() {
        return _particle2;
    }

    /**
     * Returns whether this particles collision event is valid
     */
    public boolean isValid() {
        return _particle1.isValid(_timeEventCreated) && _particle2.isValid(_timeEventCreated); // ADDED AFTER DEADLINE (changed from _timeOfEvent to _timeEventCreated)
    }
}
