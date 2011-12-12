package tu.modgeh.intfiresim;

import org.jfree.data.xy.XYSeries;


public class IntegrationTimeSeriesCreator implements Runnable, SpikeListener, UpdateListener {

	protected Integrator integrator = null;
	private XYSeries spikes = null;
	private XYSeries membranePotential = null;
	protected double simulationTime = 0.0;

	public IntegrationTimeSeriesCreator(Integrator integrator, double simulationTime) {

		this.integrator = integrator;
		this.spikes = new XYSeries(integrator.toString() + " - Spikes");
		this.membranePotential = new XYSeries(integrator.toString() + " - V");
		this.simulationTime = simulationTime;
	}

	@Override
	public void run() {

		integrator.addSpikeListener(this);
		integrator.addUpdateListener(this);

		integrator.runSimulation(simulationTime);
	}

	public Integrator getIntegrator() {
		return integrator;
	}

	public XYSeries getSpikes() {
		return spikes;
	}

	public XYSeries getMembranePotential() {
		return membranePotential;
	}

	@Override
	public void spikeGenerated(SpikeEvent evt) {
		spikes.add(integrator.getTime() - (integrator.getDeltaT() / 2), integrator.getThreasholdPotential());
	}

	@Override
	public void stepFinnished(UpdateEvent evt) {
		membranePotential.add(integrator.getTime(), integrator.getPotential());
	}
}
