import java.awt.*;
import java.util.*;

public class Particle {
	private String _name;
	private double _x, _y;
	private double _vx, _vy;
	private double _radius;
	private double _lastUpdateTime;

	/**
	 * Helper method to parse a string into a Particle.
	 * DO NOT MODIFY THIS METHOD
	 * @param str the string to parse
	 * @return the parsed Particle
	 */
	public static Particle build(String str) {
		String[] tokens = str.split("\\s+");
		double[] nums = Arrays.stream(Arrays.copyOfRange(tokens, 1, tokens.length))
				.mapToDouble(Double::parseDouble)
				.toArray();
		return new Particle(tokens[0], nums[0], nums[1], nums[2], nums[3], nums[4]);
	}

	/**
	 * @name name of the particle (useful for debugging)
	 * @param x x-coordinate of the particle
	 * @param y y-coordinate of the particle
	 * @param vx x-velocity of the particle
	 * @param vy y-velocity of the particle
	 * @param radius radius of the particle
	 */
	Particle(String name, double x, double y, double vx, double vy, double radius) {
		_name = name;
		_x = x;
		_y = y;
		_vx = vx;
		_vy = vy;
		_radius = radius;
	}

	/**
	 * Draws the particle as a filled circle.
	 * DO NOT MODIFY THIS METHOD
	 */
	void draw(Graphics g) {
		g.fillOval((int) (_x - _radius), (int) (_y - _radius), (int) (2 * _radius), (int) (2 * _radius));
	}

	/**
	 * Useful for debugging.
	 */
	public String toString() {
		return (_name.equals("") ? "" : _name + " ") + _x + "  " + _y + " " + _vx + " " + _vy + " " + _radius;
	}

	/**
	 * Updates the position of the particle after an elapsed amount of time, delta, using
	 * the particle's current velocity.
	 * @param delta the elapsed time since the last particle update
	 */
	public void update(double delta, double _width) {
		double newX = _x + delta * _vx;
		double newY = _y + delta * _vy;
		_x = newX;
		_y = newY;

		if (_x < -10 || _x > _width + 10 || _y < -10 || _y > _width + 10) {
			System.out.printf("Particle out of bounds: x=%.2f, y=%.2f, vx=%.2f, vy=%.2f%n", _x, _y, _vx, _vy);
		}
	}

	/**
	 * Updates both this particle's and another particle's velocities after a collision between them.
	 * OR
	 * Updates a particle's collision with a wall
	 * DO NOT CHANGE THE MATH IN THIS METHOD
	 * @param now the current time in the simulation
	 * @param other the particle that this one collided with; if null, it is a wall
	 * @param width the width of the simulation
	 */
	public void updateAfterCollision(double now, Particle other, double width, int wall) {
		// Wall
		if (other == null) {
			//System.out.println("Moving Particle [WALL]");

			if (wall == 1 || wall == 2) { // Right / Left
				_vx *= -1;
				if (_y - _radius == 0 || _y + _radius == width) {
					_vy *= -1;
				}
			}

			if (wall == 3 || wall == 4) { // Bottom / Top
				_vy *= -1;
				if (_x - _radius == 0 || _x + _radius == width) {
					_vx *= -1;
				}
			}

			// Particles
		} else {
			//System.out.println("Moving Particle [PARTICLE]");
			double vxPrime, vyPrime;
			double otherVxPrime, otherVyPrime;
			double common = ((_vx - other._vx) * (_x - other._x) +
					(_vy - other._vy) * (_y - other._y)) /
					(Math.pow(_x - other._x, 2) + Math.pow(_y - other._y, 2));
			vxPrime = _vx - common * (_x - other._x);
			vyPrime = _vy - common * (_y - other._y);
			otherVxPrime = other._vx - common * (other._x - _x);
			otherVyPrime = other._vy - common * (other._y - _y);

			_vx = vxPrime;
			_vy = vyPrime;
			other._vx = otherVxPrime;
			other._vy = otherVyPrime;

			other._lastUpdateTime = now;
		}
		_lastUpdateTime = now;
	}

	/**
	 * Computes and returns the time when (if ever) this particle will collide with another particle,
	 * or infinity if the two particles will never collide given their current velocities.
	 * DO NOT CHANGE THE MATH IN THIS METHOD
	 * @param other the other particle to consider
	 * @return the time with the particles will collide, or infinity if they will never collide
	 */
	public double getParticleCollisionTime(Particle other) {
		// See https://en.wikipedia.org/wiki/Elastic_collision#Two-dimensional_collision_with_two_moving_objects
		double a = _vx - other._vx;
		double b = _x - other._x;
		double c = _vy - other._vy;
		double d = _y - other._y;
		double r = _radius;

		double A = a * a + c * c;
		double B = 2 * (a * b + c * d);
		double C = b * b + d * d - 4 * r * r;

		// Numerically more stable solution to QE.
		// https://people.csail.mit.edu/bkph/articles/Quadratics.pdf
		double t1, t2;
		if (B >= 0) {
			t1 = (-B - Math.sqrt(B * B - 4 * A * C)) / (2 * A);
			t2 = 2 * C / (-B - Math.sqrt(B * B - 4 * A * C));
		} else {
			t1 = 2 * C / (-B + Math.sqrt(B * B - 4 * A * C));
			t2 = (-B + Math.sqrt(B * B - 4 * A * C)) / (2 * A);
		}

		// Require that the collision time be slightly larger than 0 to avoid
		// numerical issues.
		double SMALL = 1e-6;
		double t;
		if (t1 > SMALL && t2 > SMALL) {
			t = Math.min(t1, t2);
		} else if (t1 > SMALL) {
			t = t1;
		} else if (t2 > SMALL) {
			t = t2;
		} else {
			// no collision
			t = Double.POSITIVE_INFINITY;
		}
		return t;
	}

	/**
	 * Computes and returns the time when (if ever) this particle will collide with a wall,
	 * or infinity if it will never collide.
	 */
	public double getWallCollisionTime(double width, int wall) {
		double collisionTime = Double.POSITIVE_INFINITY;

		switch (wall) {
			case 1: // Checking right wall (1)
				if (_vx >= 0) {
					collisionTime = (width - _radius - _x) / _vx;
				}
				break;

			case 2: // Checking left wall (2)
				if (_vx <= 0) {
					collisionTime = (_radius - _x) / _vx;
				}
				break;

			case 3: // Checking bottom wall (3)
				if (_vy >= 0) {
					collisionTime = (width - _radius - _y) / _vy;
				}
				break;

			case 4: // Checking top wall (4)
				if (_vy <= 0) {
					collisionTime = (_radius - _y) / _vy;
				}
				break;
		}
		return collisionTime;
	}

	/**
	 * Checks if the current event's time is valid relative to the _lastUpdateTime
	 */
	public boolean isValid(double eventTime) {
		return eventTime >= _lastUpdateTime;
	}
}
