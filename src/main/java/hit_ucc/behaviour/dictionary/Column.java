package hit_ucc.behaviour.dictionary;

public class Column implements IColumn {
	private int[] data;

	public Column(int size) {
		data = new int[size];
	}

	@Override
	public int getValue(int index) {
		return data[index];
	}

	@Override
	public void setValue(int index, int value) {
		data[index] = value;
	}

	@Override
	public int size() {
		return data.length;
	}
}
