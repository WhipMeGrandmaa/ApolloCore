/*
 * Copyright (c) 2020 Lewys Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.whipmegrandma.apollocore.util;

import lombok.Data;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.*;

@Data
public final class ProbabilityCollection implements ConfigSerializable {

	private final NavigableSet<ProbabilitySetElement> collection;
	private final SplittableRandom random = new SplittableRandom();

	private int totalProbability;

	public ProbabilityCollection() {
		this.collection = new TreeSet<>(Comparator.comparingInt(ProbabilitySetElement::getIndex));
		this.totalProbability = 0;
	}

	public int size() {
		return this.collection.size();
	}

	public boolean isEmpty() {
		return this.collection.isEmpty();
	}

	public boolean contains(CompMaterial object) {
		if (object == null) {
			throw new IllegalArgumentException("Cannot check if null object is contained in this collection");
		}

		return this.collection.stream().anyMatch(entry -> entry.getObject().equals(object));
	}

	public Iterator<ProbabilitySetElement> iterator() {
		return this.collection.iterator();
	}

	public void add(CompMaterial object, int probability) {
		if (object == null) {
			throw new IllegalArgumentException("Cannot add null object");
		}

		if (probability <= 0) {
			throw new IllegalArgumentException("Probability must be greater than 0");
		}

		ProbabilitySetElement entry = new ProbabilitySetElement(object, probability);
		entry.setIndex(this.totalProbability + 1);

		this.collection.add(entry);
		this.totalProbability += probability;
	}

	public boolean remove(CompMaterial object) {
		if (object == null) {
			throw new IllegalArgumentException("Cannot remove null object");
		}

		Iterator<ProbabilitySetElement> it = this.iterator();
		boolean removed = false;

		while (it.hasNext()) {
			ProbabilitySetElement entry = it.next();
			if (entry.getObject().equals(object)) {
				this.totalProbability -= entry.getProbability();
				it.remove();
				removed = true;
			}
		}

		if (removed) {
			int previousIndex = 0;
			for (ProbabilitySetElement entry : this.collection) {
				previousIndex = entry.setIndex(previousIndex + 1) + (entry.getProbability() - 1);
			}
		}

		return removed;
	}

	public void clear() {
		this.collection.clear();
		this.totalProbability = 0;
	}

	public CompMaterial get() {
		if (this.isEmpty()) {
			return CompMaterial.AIR;
		}

		ProbabilitySetElement toFind = new ProbabilitySetElement(null, 0);
		toFind.setIndex(this.random.nextInt(1, this.totalProbability + 1));

		return Objects.requireNonNull(this.collection.floor(toFind).getObject());
	}

	public int getTotalProbability() {
		return this.totalProbability;
	}

	public static ProbabilityCollection deserialize(SerializedMap map) {
		ProbabilityCollection set = new ProbabilityCollection();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			CompMaterial material = CompMaterial.fromString(entry.getKey());
			int chance = (int) entry.getValue();

			set.add(material, chance);
		}

		return set;
	}

	@Override
	public SerializedMap serialize() {
		SerializedMap map = new SerializedMap();

		map.put("Mine_Blocks", this.collection);

		return map;
	}

	@Data
	public final static class ProbabilitySetElement implements ConfigSerializable {
		private CompMaterial object;
		private int probability;
		private int index;

		private ProbabilitySetElement() {
		}

		protected ProbabilitySetElement(CompMaterial object, int probability) {
			this.object = object;
			this.probability = probability;
		}

		public CompMaterial getObject() {
			return this.object;
		}

		public int getProbability() {
			return this.probability;
		}

		private int getIndex() {
			return this.index;
		}

		private int setIndex(int index) {
			this.index = index;
			return this.index;
		}

		public static ProbabilitySetElement deserialize(SerializedMap map) {
			ProbabilitySetElement object = new ProbabilitySetElement();

			object.object = map.getMaterial("Material");
			object.probability = map.getInteger("Probability");
			object.index = map.getInteger("Index");
			
			return object;
		}

		@Override
		public SerializedMap serialize() {
			SerializedMap map = new SerializedMap();

			map.put("Material", this.object);
			map.put("Probability", this.probability);
			map.put("Index", this.index);

			return null;
		}
	}
}
