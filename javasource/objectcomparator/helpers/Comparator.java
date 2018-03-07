package objectcomparator.helpers;

import java.util.Map;

import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IMendixObject;

import objectcomparator.repositories.MendixObjectRepository;

public class Comparator {

	private IMendixObject expected;
	private IMendixObject actual;
	private ILogNode logger;
	private MendixObjectRepository mendixObjectRepository = null;
	private boolean includeAssociatedObjects; 

	public Comparator(IMendixObject expected, IMendixObject actual, ILogNode logger,
			MendixObjectRepository mendixObjectRepository, boolean includeAssociatedObjects) {
		this.expected = expected;
		this.actual = actual;
		this.logger = logger;
		this.mendixObjectRepository = mendixObjectRepository;
		this.includeAssociatedObjects = includeAssociatedObjects;
	}

	public boolean CompareLists() {

		if (expected == null) {
			logger.debug("Expected object is NULL.");
			return false;
		}

		if (actual == null) {
			logger.debug("Actual object is NULL.");
			return false;
		}

		if (objectsAreDifferentTypes()) {
			logger.debug("Objects are not of the same type. Actual object is of type " + actual.getType()
					+ ". Expected object is of type " + expected.getType() + ".");
			return false;
		}

		Map<String, Object> actualFlattenMendixObject = null;
		Map<String, Object> expectedFlattenMendixObject = null;
		
		if(includeAssociatedObjects == true) {
			actualFlattenMendixObject = (new FlattenMendixObject(actual, mendixObjectRepository))
					.getFlattenMendixObject();
			expectedFlattenMendixObject = (new FlattenMendixObject(expected, mendixObjectRepository))
					.getFlattenMendixObject();
		}
		
		if(includeAssociatedObjects == false) {
			actualFlattenMendixObject = (new FlattenMendixObject(actual, mendixObjectRepository))
					.getFlattenMendixObjectWithoutAssociatedObjects();
			expectedFlattenMendixObject = (new FlattenMendixObject(expected, mendixObjectRepository))
					.getFlattenMendixObjectWithoutAssociatedObjects();
		}

		return (new CheckMapDifference(logger, expectedFlattenMendixObject, actualFlattenMendixObject))
				.checkDifference();
	}

	private boolean objectsAreDifferentTypes() {
		return (!this.actual.getType().equals(this.expected.getType()));
	}
}