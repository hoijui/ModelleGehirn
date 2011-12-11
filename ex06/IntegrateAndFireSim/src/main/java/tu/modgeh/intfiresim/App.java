package tu.modgeh.intfiresim;

/**
 * TU Berlin
 * Modelle zur Informationsverabeitung im Gehirn
 * WS 11/12
 *
 * Aufgabe: 6. Stochastisches Integrate-and-fire Neuron
 * Ausgabe: 06.12.2011
 * Abgabe:  13.12.2011
 *
 * @author Robin Vobruba (robin.vobruba@campus.tu-berlin.de)
 */
public class App {

	public static void main(String[] args) {

		double i = 0.5E-9;
		if (args.length > 0) {
			i = Double.parseDouble(args[0]);
		}

//		new Tester(i);

		// 6. a)
		new ExcerciseVisualizer().run();

		// 6. b)
		new FrequencyCurrentVisualizer().run();
	}
}
