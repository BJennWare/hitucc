package hitucc.actors.messages;

import hitucc.model.EncodedRow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SendEncodedDataBatchMessage implements Serializable {
	private static final long serialVersionUID = 4322411166379563033L;
	private int batchIdentifier;
	private List<EncodedRow> batch;
	private int currentSplit;
	private int splitCount;
}