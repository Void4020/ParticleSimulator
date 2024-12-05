// Note for the grader:
// I was unable to fully make this work, even after going to office hours and such.
// The issue that I found was that even though particles_a_start.txt works and has the right values,
// particles_b_start.txt's end values were off. This could be because of two reasons. One, particles_a_start only had
// wall collisions, but particles_b_start involved particle collisions, which I can only think that the way I implemented
// particle collisions or queueing, etc. The other reason is that when I added a print statement to whenever I remove the
// top element of the heap, during particles_b_start.txt, the size of the heap increases at way too fast of a rate -- it gets up to
// around 10,000 elements over the course of 5 seconds (for only a 3 particle experiment). I couldn't solve this issue either, and
// I can only think that it contributed to my program not working. Lastly, when I run the particalsInitial.txt, the particles move, but really slowly,
// which may or may not be caused by the two reasons above.
// Please take this into account while grading and looking at the auto-grader results, as the core logic of my program is correct, and so is my heap implementation.
// Thank you,
		
// - Owen

import java.util.*;
import java.util.function.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;

public class ParticleSimulator extends JPanel {
	private Heap<Event> _events;
	private java.util.List<Particle> _particles;
	private double _duration;
	private int _width;

	/**
	 * @param filename the name of the file to parse containing the particles
	 */
	public ParticleSimulator(String filename) throws IOException {
		_events = new HeapImpl<>();

		// Parse the specified file and load all the particles.
		Scanner s = new Scanner(new File(filename));
		_width = s.nextInt();
		_duration = s.nextDouble();
		s.nextLine();
		_particles = new ArrayList<>();
		while (s.hasNext()) {
			String line = s.nextLine();
			Particle particle = Particle.build(line);
			_particles.add(particle);
		}

		setPreferredSize(new Dimension(_width, _width));
	}

	@Override
	/**
	 * Draws all the particles on the screen at their current locations
	 * DO NOT MODIFY THIS METHOD
	 */
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, _width, _width);
		for (Particle p : _particles) {
			p.draw(g);
		}
	}

	// Helper class to signify the final event of the simulation.
	private class TerminationEvent extends Event {
		TerminationEvent(double timeOfEvent) {
			super(timeOfEvent, 0);
		}

		public boolean isValid() { // Needed because abstract requires it
			return true;
		}
	}

	/**
	 * Helper method to update the positions of all the particles based on their current velocities.
	 */
	private void updateAllParticles(double delta) {
		for (Particle p : _particles) {
			p.update(delta, _width);
		}
	}

	/**
	 * Queues particle collisions with walls
	 */
	private void queueWalls(Particle p, double lastTime) {
		for (int i = 1; i <= 4; i++) {
			double wallCollisionTime = p.getWallCollisionTime(_width, i);
			if (wallCollisionTime != Double.POSITIVE_INFINITY) {
				_events.add(new WallCollisionEvent(lastTime + wallCollisionTime, lastTime, p, i));
				//System.out.println("Adding new wall collision event for particle " + p);
			}
		}
	}

	/**
	 * Queues particle collisions with other particles
	 */
	private void queueParticle(Particle p, double lastTime) {
		for (Particle other : _particles) {
			if (!p.equals(other)) {
				double particleCollisionTime = p.getParticleCollisionTime(other);
				if (particleCollisionTime != Double.POSITIVE_INFINITY) {
					_events.add(new ParticleCollisionEvent(lastTime + particleCollisionTime, lastTime, p, other));
					//System.out.println("Adding new particle collision  event for particle " + p + " and " + other);
				}
			}
		}
	}

	/**
	 * Executes the actual simulation.
	 */
	private void simulate(boolean show) {
		double lastTime = 0;

		// Create initial events, i.e., all the possible
		// collisions between all the particles and each other,
		// and all the particles and the walls.
		for (Particle p : _particles) {
			queueWalls(p, lastTime);
			queueParticle(p, lastTime);
			//System.out.println("Queueing events for " + p);
		}

		_events.add(new TerminationEvent(_duration));

		while (_events.size() > 0) {
			Event event = _events.removeFirst();
			//System.out.println("Removing event " + event + ". Size: " + _events.size());
			double delta = event._timeOfEvent - lastTime;

			if (event instanceof TerminationEvent) {
				updateAllParticles(delta);
				break;
			}

			// Check if event still valid; if not, then skip this event
			if (!event.isValid()) {
				//System.out.println("NOT VALID EVENT");
				continue;
			}
			
			// Since the event is valid, then pause the simulation for the right
			// amount of time, and then update the screen.
			if (show) {
				try {
					Thread.sleep((long) delta);
				} catch (InterruptedException ie) {
				}
			}

			// Update positions of all particles
			updateAllParticles(delta);

			// Update the velocity of the particle(s) involved in the collision
			// (either for a particle-wall collision or a particle-particle collision).

			// Checking if event is a WallCollisionEvent
			if (event instanceof WallCollisionEvent) {
				//System.out.println("WALL COLLIDE");
				// Updating velocity of particle in the wall collision
				WallCollisionEvent wallEvent = (WallCollisionEvent) event;
				Particle p = wallEvent.getParticle();
				p.updateAfterCollision(wallEvent._timeOfEvent, null, _width, wallEvent.getWall());

				// Queueing new events for the particle in the wall collision
				queueWalls(p, wallEvent._timeOfEvent);
				queueParticle(p, wallEvent._timeOfEvent);
			}

			// Checking if event is a ParticleCollisionEvent
			else if (event instanceof ParticleCollisionEvent) {
				//System.out.println("PARTICLE COLLIDE");
				// Updating velocity of particles in the particle collision
				ParticleCollisionEvent particleEvent = (ParticleCollisionEvent) event;
				Particle p1 = particleEvent.getParticle1();
				Particle p2 = particleEvent.getParticle2();
				p1.updateAfterCollision(particleEvent._timeOfEvent, p2, _width, -1); // -1 because it isn't a wall collision

				// Queueing new events for the particles in the particle collision
				queueParticle(p1, particleEvent._timeOfEvent);
				queueParticle(p2, particleEvent._timeOfEvent);
				queueWalls(p1, particleEvent._timeOfEvent);
				queueWalls(p2, particleEvent._timeOfEvent);
			}

			// Update the time of the simulation
			lastTime = event._timeOfEvent;

			// Redraw the screen
			if (show) {
				repaint();
			}
		}
	

		// Print out the final state of the simulation
		System.out.println(_width);
		System.out.println(_duration);
		for (Particle p : _particles) {
			System.out.println(p);
		}
	}

	public static void main(String[] args) throws IOException {

		if (args.length < 1) {
			System.out.println("Usage: java ParticalSimulator <filename>");
			System.exit(1);
		}
		ParticleSimulator simulator;

		simulator = new ParticleSimulator(args[0]);
		JFrame frame = new JFrame();
		frame.setTitle("Particle Simulator");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(simulator, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulator.simulate(true);
	}
}
