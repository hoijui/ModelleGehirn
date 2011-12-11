package tu.modgeh.intfiresim;


import java.util.concurrent.Callable;
import org.jfree.data.xy.XYSeries;

public class FreqencyCurrentSeriesCreator implements Callable<XYSeries> {

	private Integrator integrator = null;
	private XYSeries currentFrequency = null;
	private double simulationTime;
	private int currents;

	public FreqencyCurrentSeriesCreator(Integrator integrator, double simulationTime, int currents) {

		this.integrator = integrator;
		this.currentFrequency = new XYSeries(integrator.toString());
		this.simulationTime = simulationTime;
		this.currents = currents;

	}

	public XYSeries getFreqencyCurrentSeries() {
		return currentFrequency;
	}

	public XYSeries call() throws Exception {

		// in [nA]
		double current = 0.0;
		final double deltaCurrent = 1.0 / currents;
		for (int i = 0; i < currents; i++) {
//System.out.println("\t\ti: " + current + " nA ...");
			integrator.setCurrent(current * 1E-9);
			IntegrationFreqencyCreator freqCre = new IntegrationFreqencyCreator(integrator, simulationTime);
			currentFrequency.add(current, freqCre.getFrequency());

			current += deltaCurrent;
		}

		return getFreqencyCurrentSeries();
	}
}
