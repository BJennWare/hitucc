package hitucc;

import com.beust.jcommander.*;
import com.opencsv.CSVParser;

import java.util.Arrays;

public class HitUCCApp {
	public static void main(String[] args) {

		PeerCommand peerCommand = new PeerCommand();
		PeerHostCommand peerHostCommand = new PeerHostCommand();
		JCommander jCommander = JCommander.newBuilder()
				.addCommand(HitUCCPeerHostSystem.PEER_HOST_ROLE, peerHostCommand)
				.addCommand(HitUCCPeerSystem.PEER_ROLE, peerCommand)
				.build();

		try {
			jCommander.parse(args);

			if (jCommander.getParsedCommand() == null) {
				throw new ParameterException("No command given.");
			}

			switch (jCommander.getParsedCommand()) {
				case HitUCCPeerHostSystem.PEER_HOST_ROLE:
					HitUCCPeerHostSystem.start(
							peerHostCommand.workers,
							peerHostCommand.input,
							peerHostCommand.greedyTaskDistribution,
							peerHostCommand.csvDelimiter.charAt(0),
							peerHostCommand.csvSkipHeader,
							peerHostCommand.csvQuoteCharacter,
							peerHostCommand.csvEscapeCharacter,
							peerHostCommand.output,
							peerHostCommand.dataDuplicationFactor,
							peerHostCommand.nullEqualsNull,
							peerHostCommand.sortColumnsInPhaseOne,
							peerHostCommand.sortColumnsNegatively,
							peerHostCommand.maxLocalTreeDepth,
							peerHostCommand.createDiffSets,
							peerHostCommand.minimizeDiffSets);
					break;
				case HitUCCPeerSystem.PEER_ROLE:
					HitUCCPeerSystem.start(peerCommand.workers,peerCommand.createDiffSets, peerCommand.minimizeDiffSets);
					break;
				default:
					throw new AssertionError();
			}

		} catch (ParameterException e) {
			System.out.printf("Could not parse args: %s\n", e.getMessage());
			if (jCommander.getParsedCommand() == null) {
				jCommander.usage();
			} else {
				jCommander.usage(jCommander.getParsedCommand());
			}
			System.exit(1);
		}
	}

	public enum CreateDiffSetsStrategy {
		HASH,
		LIST,
		TRIE,
		PATRICIA,
		NAIVE;

		// converter that will be used later
		public static CreateDiffSetsStrategy fromString(String code) {

			for(CreateDiffSetsStrategy output : CreateDiffSetsStrategy.values()) {
				if(output.toString().equalsIgnoreCase(code)) {
					return output;
				}
			}

			return null;
		}
	}

	public static class CreateDiffSetsStrategyConverter implements IStringConverter<CreateDiffSetsStrategy> {

		@Override
		public CreateDiffSetsStrategy convert(String value) {
			CreateDiffSetsStrategy convertedValue = CreateDiffSetsStrategy.fromString(value);

			if(convertedValue == null) {
				throw new ParameterException("Value " + value + "can not be converted to CreateDiffSetsStrategy. " +
						"Available values are: " + Arrays.toString(CreateDiffSetsStrategy.values()));
			}
			return convertedValue;
		}
	}

	public static class MinimizeDiffSetsStrategyConverter implements IStringConverter<MinimizeDiffSetsStrategy> {

		@Override
		public MinimizeDiffSetsStrategy convert(String value) {
			MinimizeDiffSetsStrategy convertedValue = MinimizeDiffSetsStrategy.fromString(value);

			if(convertedValue == null) {
				throw new ParameterException("Value " + value + "can not be converted to MinimizeDiffSetsStrategy. " +
						"Available values are: " + Arrays.toString(MinimizeDiffSetsStrategy.values()));
			}
			return convertedValue;
		}
	}

	public enum MinimizeDiffSetsStrategy {
		BUCKETING,
		SORT,
		NAIVE;

		// converter that will be used later
		public static MinimizeDiffSetsStrategy fromString(String code) {

			for(MinimizeDiffSetsStrategy output : MinimizeDiffSetsStrategy.values()) {
				if(output.toString().equalsIgnoreCase(code)) {
					return output;
				}
			}

			return null;
		}
	}

	static class PeerCommand {
		public static final int DEFAULT_WORKERS = Runtime.getRuntime().availableProcessors() - 2;
		public static final String DEFAULT_OUTPUT_FILE = "test-results.json";
		public static final int DEFAULT_DATA_DUPLICATION_FACTOR = 0;
		public static final int DEFAULT_MAX_LOCAL_TREE_DEPTH = 1000;
		public static final boolean DEFAULT_GREEDY_TASK_DISTRIBUTION = false;
		public static final boolean DEFAULT_NULL_EQUALS_EQUALS = false;
		public static final boolean DEFAULT_CSV_SKIP_HEADER = false;

		@Parameter(names = {"-w", "--workers"}, description = "number of workers to start locally", required = false)
		int workers = DEFAULT_WORKERS;

		@Parameter(names = {"--createDiffSets"},
				description = "Create Difference Sets Strategy",
				required = false,
				converter = CreateDiffSetsStrategyConverter.class)
		CreateDiffSetsStrategy createDiffSets = CreateDiffSetsStrategy.HASH;

		@Parameter(names = {"--minimizeDiffSets"},
				description = "Minimize Difference Sets Strategy",
				required = false,
				converter = MinimizeDiffSetsStrategyConverter.class)
		MinimizeDiffSetsStrategy minimizeDiffSets = MinimizeDiffSetsStrategy.BUCKETING;

//		@Parameter(names = {"-bh", "--bind-host"}, description = "this machine's host name or IP to bind against")
//		String bindHost = "0.0.0.0";
//		@Parameter(names = {"-bp", "--bind-port"}, description = "port to bind against", required = false)
//		int bindPort = -1;
	}

	@Parameters(commandDescription = "start a peer to peer host actor system")
	static class PeerHostCommand extends PeerCommand {

		@Parameter(names = {"-greedy", "--greedyTaskDistribution"},
				description = "Set to true if you want to redistribute the subtasks for better network optimizations",
				required = false)
		boolean greedyTaskDistribution = DEFAULT_GREEDY_TASK_DISTRIBUTION;

		@Parameter(names = {"-ddf", "--dataDuplicationFactor"},
				description = "Describes how often the data should be duplicated and send to other nodes in the network. Determines the batch count.",
				required = false)
		int dataDuplicationFactor = DEFAULT_DATA_DUPLICATION_FACTOR;

		@Parameter(names = {"-nen", "--nullEqualsNull"},
				description = "If null should equals null",
				required = false)
		boolean nullEqualsNull = DEFAULT_NULL_EQUALS_EQUALS;

		@Parameter(names = {"-i", "--input"},
				description = "Input csv file",
				required = true)
		String input;

		@Parameter(names = {"-csv_d", "--csvDelimiter"},
				description = "Delimiter of the csv file. Default is ','",
				required = false)
		String csvDelimiter = ",";

		@Parameter(names = {"-csv_sh", "--csvSkipHeader"},
				description = "Whether to skip the header of the csv or not. Default is 'true'",
				required = false)
		boolean csvSkipHeader = DEFAULT_CSV_SKIP_HEADER;

		@Parameter(names = {"-csv_quote", "--csvQuoteCharacter"},
				description = "Quote character",
				required = false)
		char csvQuoteCharacter = CSVParser.DEFAULT_QUOTE_CHARACTER;

		@Parameter(names = {"-csv_escape", "--csvEscapeCharacter"},
				description = "Quote character",
				required = false)
		char csvEscapeCharacter = CSVParser.DEFAULT_ESCAPE_CHARACTER;

		@Parameter(names = {"-o", "--o"},
				description = "Output file with all accumulated UCCs",
				required = false)
		String output = DEFAULT_OUTPUT_FILE;

		@Parameter(names = {"-sortColumnsInPhaseOne"},
				required = false)
		boolean sortColumnsInPhaseOne = false;

		@Parameter(names = {"-sortColumnsNegatively"},
				required = false)
		boolean sortColumnsNegatively = false;

		@Parameter(names = {"-maxLocalTreeDepth"},
				required = false)
		int maxLocalTreeDepth = DEFAULT_MAX_LOCAL_TREE_DEPTH;
	}
}
