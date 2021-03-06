package hitucc;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import hitucc.actors.PeerDataBouncer;
import hitucc.actors.PeerWorker;
import hitucc.actors.messages.TaskMessage;
import hitucc.model.AlgorithmTimerObject;

import java.io.IOException;

public class HitUCCPeerHostSystem extends HitUCCSystem {

	public static final String PEER_HOST_ROLE = "host";

	public static void start(int workers, String input, boolean greedyTaskDistribution, char csvDelimiter, boolean csvSkipHeader, char csvQuoteCharacter, char csvEscapeCharacter, String output, int dataDuplicationFactor, boolean nullEqualsNull, boolean sortColumnsInPhaseOne, boolean sortNegatively, int maxTreeDepth, HitUCCApp.CreateDiffSetsStrategy createDiffSetsStrategy, HitUCCApp.MinimizeDiffSetsStrategy minimizeDiffSetsStrategy) {
		final Config config = ConfigFactory.parseString("akka.cluster.roles = [" + PEER_HOST_ROLE + "]\n").withFallback(ConfigFactory.load());
		String clusterName = config.getString("clustering.cluster.name");
		final ActorSystem system = createSystem(clusterName, config);

		int port = config.getInt("clustering.port");
		String host = config.getString("clustering.ip");

		System.out.println("#################### START ACTOR SYSTEM ####################\n" +
				"Address: " + host + ":" + port + "\n" +
				"Memory: " + Runtime.getRuntime().maxMemory() + " Bytes\n" +
				"Workers: " + workers + "\n" +
				"#################### ------------------ ####################");

//			system.actorOf(ClusterListener.props(), ClusterListener.DEFAULT_NAME);
//			system.actorOf(MetricsListener.props(), MetricsListener.DEFAULT_NAME);

//		system.actorOf(Reaper.props(), Reaper.DEFAULT_NAME + ":" + port);
		for (int i = 0; i < workers; i++) {
			system.actorOf(PeerWorker.props(createDiffSetsStrategy, minimizeDiffSetsStrategy), PeerWorker.DEFAULT_NAME + i + ":" + port);
		}
		final ActorRef dataBouncer = system.actorOf(PeerDataBouncer.props(workers), PeerDataBouncer.DEFAULT_NAME + ":" + port);

		Cluster.get(system).registerOnMemberUp(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			int systemCount = config.getInt("clustering.systems");
			AlgorithmTimerObject timerObject = new AlgorithmTimerObject(sortColumnsInPhaseOne, sortNegatively, output, input, maxTreeDepth, workers == 1 && Math.max(systemCount, 1) == 1 && dataDuplicationFactor == 1);
			timerObject.setTableReadStartTime();

			String[][] table = null;
			try {
				table = ReadDataTable.readTable("data/" + input, csvDelimiter, csvSkipHeader, csvQuoteCharacter, csvEscapeCharacter);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}

			timerObject.setRegisterStartTime();

			dataBouncer.tell(new TaskMessage(table, table[0].length, greedyTaskDistribution, dataDuplicationFactor, nullEqualsNull, Math.max(systemCount, 1), timerObject.clone()), ActorRef.noSender());
		});
	}
}
