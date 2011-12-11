package tu.modgeh.intfiresim;

import org.jfree.data.xy.XYSeries;


public class IntegrationTimeSeriesCreator implements SpikeListener, UpdateListener {

	private Integrator integrator = null;
	private XYSeries spikes = null;
	private XYSeries membranePotential = null;

	public IntegrationTimeSeriesCreator(Integrator integrator, double simulationTime) {

		this.integrator = integrator;
		this.spikes = new XYSeries(integrator.toString() + " - Spikes");
		this.membranePotential = new XYSeries(integrator.toString() + " - V");

		integrator.addSpikeListener(this);
		integrator.addUpdateListener(this);

		integrator.runSimulation(simulationTime);
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
