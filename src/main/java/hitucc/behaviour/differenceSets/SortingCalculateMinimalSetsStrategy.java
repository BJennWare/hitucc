package hitucc.behaviour.differenceSets;

import hitucc.model.SerializableBitSet;

import java.util.*;

public class SortingCalculateMinimalSetsStrategy implements ICalculateMinimalSetsStrategy {
	@Override
	public SerializableBitSet[] calculateMinimalDifferenceSets(Iterable<SerializableBitSet> uniqueSets) {
		List<SerializableBitSet> foundMinimalSets = new ArrayList<>();

		List<SerializableBitSet> sortedList = new ArrayList<>();
		for (SerializableBitSet i : uniqueSets) {
			sortedList.add(i);
		}
		sortedList.sort(Comparator.comparingInt(SerializableBitSet::cardinality));

		for (SerializableBitSet set : sortedList) {
			DifferenceSetDetector.insertMinimalDifferenceSets(foundMinimalSets, set);
		}

		SerializableBitSet[] result = new SerializableBitSet[foundMinimalSets.size()];
		return foundMinimalSets.toArray(result);
	}

	@Override
	public SerializableBitSet[] calculateMinimalDifferenceSets(DifferenceSetDetector differenceSetDetector, Iterable<SerializableBitSet> uniqueSets, SerializableBitSet[] oldMinimalSets) {
		return differenceSetDetector.mergeMinimalDifferenceSets(oldMinimalSets, calculateMinimalDifferenceSets(uniqueSets));
	}
}
