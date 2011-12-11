package tu.modgeh.intfiresim;


public class IntegrationFreqencyCreator implements SpikeListener {

	private Integrator integrator = null;
	private int spikes = 0;

	public IntegrationFreqencyCreator(Integrator integrator, double simulationTime) {

		this.integrator = integrator;
		this.spikes = 0;

		integrator.addSpikeListener(this);

		integrator.runSimulation(simulationTime);
	}

	public double getFrequency() {
		return spikes / integrator.getTime();
	}

	@Override
	public void spikeGenerated(SpikeEvent evt) {
		spikes++;
	}
}
