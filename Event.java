/**
 * Represents a collision between a particle and another particle, or a particle and a wall.
 */
public abstract class Event implements Comparable<Event> {
	double _timeOfEvent;
	double _timeEventCreated;

	/**
	 * @param timeOfEvent the time when the collision will take place
	 * @param timeEventCreated the time when the event was first instantiated and added to the queue
	 */
	public Event (double timeOfEvent, double timeEventCreated) {
		_timeOfEvent = timeOfEvent;
		_timeEventCreated = timeEventCreated;
	}

	@Override
	/**
	 * Compares two Events based on their event times. Since you are implementing a maximum heap,
	 * this method assumes that the event with the smaller event time should receive higher priority.
	 */
	public int compareTo(Event e) {
		if (_timeOfEvent < e._timeOfEvent) {
			return +1;
		} else if (_timeOfEvent == e._timeOfEvent) {
			return 0;
		} else {
			return -1;
		}
	}
	
	/**
	 * Checks whether the current event is a valid event or not based on prior collisions 
	 * Subclasses WallCollisionEvent and ParticleCollisionEvent have their own implementations of this
	 */
	public abstract boolean isValid();
}
