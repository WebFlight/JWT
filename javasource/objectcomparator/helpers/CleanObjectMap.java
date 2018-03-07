package objectcomparator.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mendix.core.objectmanagement.member.MendixAutoNumber;
import com.mendix.core.objectmanagement.member.MendixHashString;
import com.mendix.core.objectmanagement.member.MendixObjectReference;
import com.mendix.core.objectmanagement.member.MendixObjectReferenceSet;
import com.mendix.systemwideinterfaces.core.IMendixObjectMember;

import objectcomparator.repositories.MendixObjectRepository;

public class CleanObjectMap {
	
	private MendixObjectRepository mendixObjectRepository;
	private Map<String, Object> map;
	
	public CleanObjectMap(MendixObjectRepository mendixObjectRepository, Map<String, Object> map) {
		this.mendixObjectRepository = mendixObjectRepository;
		this.map = map;
	}

	public LinkedHashMap<String, Object> cleanMap() {
		return cleanMap(map);
	}
	
	private LinkedHashMap<String, Object> cleanMap(
			Map<String, Object> map) {
		List<String> defaultKeys = new ArrayList<>();
		defaultKeys.add("changedDate");
		defaultKeys.add("createdDate");
		defaultKeys.add("System.changedBy");
		defaultKeys.add("System.owner");
		defaultKeys.add("CreationDate");

		for (String defaultKey : defaultKeys) {
			if (map.get(defaultKey) != null) {
				map.remove(defaultKey);
			}
		}
		
		LinkedHashMap<String, Object> cleanedMap = new LinkedHashMap<>();

		Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			if (entry.getValue() instanceof MendixHashString) {
				it.remove();
				continue;
			}
			if (entry.getValue() instanceof MendixAutoNumber) {
				it.remove();
				continue;
			}
			if(entry.getValue() instanceof IMendixObjectMember) {
				if (mendixObjectRepository.getValue((IMendixObjectMember<?>) entry.getValue()) == null) {
					it.remove();
					continue;
				}
				if(!(entry.getValue() instanceof MendixObjectReference || entry.getValue() instanceof MendixObjectReferenceSet)) {
					cleanedMap.put(entry.getKey(), mendixObjectRepository.getValue((IMendixObjectMember<?>) entry.getValue()));
					continue;
				}
			}
			cleanedMap.put(entry.getKey(), entry.getValue());
		}
		return cleanedMap;
	}
}