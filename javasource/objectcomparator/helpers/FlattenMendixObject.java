package objectcomparator.helpers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mendix.core.objectmanagement.member.MendixObjectReference;
import com.mendix.core.objectmanagement.member.MendixObjectReferenceSet;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.IMendixObjectMember;

import objectcomparator.repositories.MendixObjectRepository;

public class FlattenMendixObject {
	private MendixObjectRepository mendixObjectRepository;
	private IMendixObject rootObject;
	private Set<String> uniqueReferenceSet = new HashSet<>();

	public FlattenMendixObject(IMendixObject rootObject, MendixObjectRepository mendixObjectRepository) {
		this.rootObject = rootObject;
		this.mendixObjectRepository = mendixObjectRepository;
	}

	public Map<String, Object> getFlattenMendixObject() {
		return (getFlattenMendixObject(rootObject, true));
	}
	
	public Map<String, Object> getFlattenMendixObjectWithoutAssociatedObjects() {
		return (getFlattenMendixObject(rootObject, false));
	}

	private Map<String, Object> getFlattenMendixObject(IMendixObject iMendixObject, boolean includeAssociatedObjects) {
		Map<String, Object> oneFlatMendixObject = new LinkedHashMap<>();
		oneFlatMendixObject.putAll(mendixObjectRepository.getMembers(iMendixObject));

		CleanObjectMap cleanObjectMap = new CleanObjectMap(mendixObjectRepository, oneFlatMendixObject);

		oneFlatMendixObject = addObjectNameToMemberName(cleanObjectMap.cleanMap(), iMendixObject);

		Iterator<Map.Entry<String, Object>> it = oneFlatMendixObject.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			
			if(includeAssociatedObjects == true) {
				if ((entry.getValue() instanceof MendixObjectReference
						|| entry.getValue() instanceof MendixObjectReferenceSet)
						&& !uniqueReferenceSet.contains(((IMendixObjectMember<?>) entry.getValue()).getName())) {
					uniqueReferenceSet.add(((IMendixObjectMember<?>) entry.getValue()).getName());
					entry.setValue(getAssociatedObjectsAndMembers(iMendixObject, entry));
					continue;
				}

				if ((entry.getValue() instanceof MendixObjectReference
						|| entry.getValue() instanceof MendixObjectReferenceSet)
						&& uniqueReferenceSet.contains(((IMendixObjectMember<?>) entry.getValue()).getName())) {
					it.remove();
				}
			}
			
			if(includeAssociatedObjects == false) {
				if ((entry.getValue() instanceof MendixObjectReference
						|| entry.getValue() instanceof MendixObjectReferenceSet)) {
					it.remove();
				}
			}

		}
		return oneFlatMendixObject;
	}

	private Map<String, Map<?, ?>> getAssociatedObjectsAndMembers(IMendixObject iMendixObject,
			Map.Entry<String, Object> entry) {
		Map<String, Map<?, ?>> associatedObjectsAndMembers = new LinkedHashMap<>();
		List<? extends IMendixObject> associatedObjects = mendixObjectRepository
				.retrieveAssociatedObjects(iMendixObject, entry.getKey());
		for (IMendixObject associatedObject : associatedObjects) {
			associatedObjectsAndMembers.put(associatedObject.getType(), getFlattenMendixObject(associatedObject, true));
		}
		return associatedObjectsAndMembers;
	}
	
	private Map<String, Object> addObjectNameToMemberName(Map<String, Object> mapWithoutObjectName, IMendixObject object) {
		Map<String, Object> mapWithObjectName = new LinkedHashMap<>();
		for (Map.Entry<String, Object> entry : mapWithoutObjectName.entrySet()) {
			if(entry.getValue() instanceof MendixObjectReference || entry.getValue() instanceof MendixObjectReferenceSet) {
				mapWithObjectName.put(entry.getKey(), entry.getValue());
				continue;
			}
			mapWithObjectName.put(object.getType() + "." + entry.getKey(), entry.getValue());
		}
		
		return mapWithObjectName;
	}
}
