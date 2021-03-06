package tu.modgeh.spiketrain;


import java.util.ArrayList;
import java.util.List;

/**
 * TU Berlin
 * Modelle zur Informationsverabeitung im Gehirn
 * WS 11/12
 *
 * Aufgabe: 9. Random-Dot Discrimination Experiment
 * Ausgabe: 10.01.2012
 * Abgabe:  24.01.2012
 *
 * @author Robin Vobruba (robin.vobruba@campus.tu-berlin.de)
 */
public class App {

	public static void main(String[] args) {

//		run91a();
//		run91b();
//		run91c();
//		run91d();
		run91e();
	}

	private static void run91a() {

		List<RateGuesser> rateGuessers = new ArrayList<RateGuesser>();
		for (int d = 0; d <= 10; d++) {
			RateGenerator rateGenerator = new RateGenerator(d);
			RateGuesser rateGuesser = new RateGuesser(rateGenerator);
			rateGenerator.runSimulation();
			rateGuessers.add(rateGuesser);
		}
		GuessesVisualizer guessesVisualizer = new GuessesVisualizer(rateGuessers);
		guessesVisualizer.run();
	}

	private static void run91b() {

		List<RateGuesser> rateGuessers = new ArrayList<RateGuesser>();
		for (int d = 0; d <= 10; d++) {
			RateGenerator rateGenerator = new RateGenerator(d);
			RateGuesser rateGuesser = new RateGuesser(rateGenerator);
			rateGenerator.runSimulation();
			rateGuessers.add(rateGuesser);
		}
		RocVisualizer rocVisualizer = new RocVisualizer(rateGuessers);
		rocVisualizer.run();
	}

	private static void run91c() {

		List<RateGuesser> rateGuessers = new ArrayList<RateGuesser>();
		for (int d = 0; d <= 10; d++) {
			RateGenerator rateGenerator = new RateGenerator(d);
			rateGenerator.setRandomTest(false);
			RateGuesser rateGuesser = new RateGuesser(rateGenerator);
			rateGenerator.runSimulation();
			rateGuessers.add(rateGuesser);
		}
		GuessesVisualizer guessesVisualizer = new GuessesVisualizer(rateGuessers);
		guessesVisualizer.run();
	}

	private static void run91d() {

		List<RateGuesser> rateGuessers = new ArrayList<RateGuesser>();
		for (int d = 0; d <= 10; d++) {
			RateGenerator rateGenerator = new RateGenerator(d);
			rateGenerator.setRandomTest(false);
			RateGuesser rateGuesser = new RateGuesser(rateGenerator);
			rateGenerator.runSimulation();
			rateGuessers.add(rateGuesser);
		}
		GuessesVisualizer guessesVisualizer = new GuessesVisualizer(rateGuessers);
		guessesVisualizer.setPlotAnalytical(true);
		guessesVisualizer.run();
	}

	private static void run91e() {

		List<RateGuesser> rateGuessers = new ArrayList<RateGuesser>();
		for (int d = 0; d <= 10; d++) {
			RateGenerator rateGenerator = new RateGenerator(d);
			rateGenerator.setRandomTest(false);
			RateGuesser rateGuesser = new RateGuesser(rateGenerator);
			rateGenerator.runSimulation();
			rateGuessers.add(rateGuesser);
		}
		GuessesVisualizer guessesVisualizer = new GuessesVisualizer(rateGuessers);
		guessesVisualizer.setPlotRocCurveIntegral(true);
		guessesVisualizer.run();
	}
}
