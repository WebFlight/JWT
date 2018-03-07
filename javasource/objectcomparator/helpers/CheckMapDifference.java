package objectcomparator.helpers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.mendix.logging.ILogNode;

public class CheckMapDifference {

	private boolean assertStatus;
	private ILogNode logger;
	private Map<String, Object> expected;
	private Map<String, Object> actual;

	public CheckMapDifference(ILogNode logger, Map<String, Object> expected, Map<String, Object> actual) {
		this.assertStatus = true;
		this.logger = logger;
		this.expected = expected;
		this.actual = actual;
	}

	public boolean checkDifference() {
		MapDifference<String, Object> mapDifference = Maps.difference(expected, actual);
		return checkDifference(mapDifference);
	}

	private boolean checkDifference(MapDifference<String, Object> mapDifference) {
		printDifferencesOnBothSides(mapDifference);
		printDifferencesOnSingleSide(mapDifference);
		return assertStatus;
	}

	private Map<String, ValueDifference<Object>> getDifferencesNestedMap(
			Map<String, ValueDifference<Object>> nestedMap) {
		boolean isNestedMap = true;
		Map<String, ValueDifference<Object>> oldMap = new LinkedHashMap<>();
		oldMap.putAll(nestedMap);
		while (isNestedMap) {
			isNestedMap = false;
			Map<String, ValueDifference<Object>> newMap = new LinkedHashMap<>();
			for (Map.Entry<String, ? extends ValueDifference<Object>> entry : oldMap.entrySet()) {
				if (entry.getValue().leftValue() instanceof LinkedHashMap<?, ?>
						|| entry.getValue().rightValue() instanceof LinkedHashMap<?, ?>) {
					isNestedMap = true;
					
					@SuppressWarnings("unchecked")
					Map<String, Object> expectedNestedMap = (Map<String, Object>) entry.getValue().leftValue();
					@SuppressWarnings("unchecked")
					Map<String, Object> actualNestedMap = (Map<String, Object>) entry.getValue().rightValue();
					
					MapDifference<String, Object> mapDifference = Maps.difference(expectedNestedMap, actualNestedMap);
					newMap.putAll(mapDifference.entriesDiffering());

					printDifferencesOnSingleSide(mapDifference);
					
					continue;
				}
				newMap.put(entry.getKey(), entry.getValue());
			}
			oldMap.clear();
			oldMap.putAll(newMap);
		}
		return oldMap;
	}
	
	private void printDifferencesOnBothSides(MapDifference<String, Object> mapDifference) {
		if (!mapDifference.entriesDiffering().isEmpty()) {
			this.assertStatus = false;
			Map<String, ValueDifference<Object>> differences = getDifferencesNestedMap(
					mapDifference.entriesDiffering());
			for (Map.Entry<String, ? extends ValueDifference<Object>> entry : differences.entrySet()) {
				logger.debug(entry.getKey() + " expected value is '"
						+ entry.getValue().leftValue().getClass().getSimpleName() + " => "
						+ entry.getValue().leftValue() + "' where actual value is '"
						+ entry.getValue().leftValue().getClass().getSimpleName() + " => "
						+ entry.getValue().rightValue() + "'");
			}
		}
	}
	
	private void printDifferencesOnSingleSide(MapDifference<String, Object> mapDifference) {
		if (!mapDifference.entriesOnlyOnLeft().isEmpty()) {
			this.assertStatus = false;
			Map<String, Object> existsInExpected = mapDifference.entriesOnlyOnLeft();
			for (Map.Entry<String, Object> entry : existsInExpected.entrySet()) {
				logger.debug(entry.getKey() + " => " + (entry.getValue() instanceof LinkedHashMap<?, ?> ? "association"
						: "=> '" + entry.getValue() + "'") + " is not set in actual object.");
			}
		}
		if (!mapDifference.entriesOnlyOnRight().isEmpty()) {
			this.assertStatus = false;
			Map<String, Object> existsInExpected = mapDifference.entriesOnlyOnRight();
			for (Map.Entry<String, Object> entry : existsInExpected.entrySet()) {
				logger.debug(entry.getKey() + " " + (entry.getValue() instanceof LinkedHashMap<?, ?> ? "association"
						: "=> '" + entry.getValue() + "'") + " is not set in expected object.");
			}
		}
	}
}
