import java.util.Arrays;

class HeapImpl<T extends Comparable<? super T>> implements Heap<T> {
	private static final int INITIAL_CAPACITY = 128;
	private T[] _storage;
	private int _numElements;

	@SuppressWarnings("unchecked")
	public HeapImpl() {
		_storage = (T[]) new Comparable[INITIAL_CAPACITY];
		_numElements = 0;
	}

	// THESE METHODS ARE FOR ARRAY TREE TRAVERSALS (how to access parent, left child, and right child from just a 1d array)

	/**
	 * Returns index of parent node
	 */
	private int parent(int i) {
		return (i - 1) / 2;
	}

	/**
	 * Returns index of left child node
	 */
	private int leftChild(int i) {
		return 2 * i + 1;
	}

	/**
	 * Returns index of right child node
	 */
	private int rightChild(int i) {
		return 2 * i + 2;
	}

	/**
	 * Swaps two "nodes"
	 */
	private void swap(int indexOne, int indexTwo) {
		T temp = _storage[indexOne];
		_storage[indexOne] = _storage[indexTwo];
		_storage[indexTwo] = temp;	
	}

	/**
	 * Method to add an element to the heap
	 */
	@SuppressWarnings("unchecked")
	public void add(T data) { 
		if (_numElements == _storage.length) {
			_storage = Arrays.copyOf(_storage, _storage.length * 2); // Resizes the array by two times
		}
		_storage[_numElements] = data;
		_numElements++;
		int currentIndex = _numElements - 1;

		// Bubble Up
		while (currentIndex > 0 && _storage[currentIndex].compareTo(_storage[parent(currentIndex)]) > 0) {
			swap(currentIndex, parent(currentIndex));
			currentIndex = parent(currentIndex);
		}
	}

	/**
	 * Method to remove the top element of the heap
	 */
	public T removeFirst() {

		if (_numElements == 0) {
			return null;
		}

		T firstElement = _storage[0];
		T lastElement = _storage[_numElements - 1];
		_numElements--;

		if (_numElements != 0) {
			_storage[0] = lastElement;
			int currentIndex = 0;

			// Bubble Down
			while (true) {
				int leftIndex = leftChild(currentIndex);
				int rightIndex = rightChild(currentIndex);
				int largestIndex = currentIndex;

				if (leftIndex < _numElements && _storage[leftIndex].compareTo(_storage[largestIndex]) > 0) {
					largestIndex = leftIndex;
				}
				if (rightIndex < _numElements && _storage[rightIndex].compareTo(_storage[largestIndex]) > 0) {
					largestIndex = rightIndex;
				}
				if (largestIndex == currentIndex) {
					break;
				}

				swap(currentIndex, largestIndex);
				currentIndex = largestIndex;
			}
		}
		_storage[_numElements] = null;
		return firstElement;
	}

	/**
	 * Method that returns the size of the heap
	 */
	public int size() {
		return _numElements;
	}
}
