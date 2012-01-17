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

		// 9.1.a
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
}
