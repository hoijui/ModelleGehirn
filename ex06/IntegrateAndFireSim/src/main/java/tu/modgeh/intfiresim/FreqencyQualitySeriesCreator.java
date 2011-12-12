package tu.modgeh.intfiresim;


import java.util.concurrent.Callable;
import org.jfree.data.xy.XYSeries;

public class FreqencyQualitySeriesCreator extends IntegrationTimeSeriesCreator implements Comparable<FreqencyQualitySeriesCreator>, Callable<XYSeries> {

	private double pulseFrequency;
	private boolean spikeGenerated = false;

	/** N_cd */
	private int numSpikesWanted = 0;
	/** N_fp */
	private int numSpikesUnwanted = 0;
	/** N_fn */
	private int numSpikesMissing = 0;
	/** N_pulse */
	private int numPulses = 0;

	private final double EPSILON = 0.00001;

	public FreqencyQualitySeriesCreator(Integrator integrator, double pulseFrequency, double simulationTime) {
		super(integrator, simulationTime);

		this.pulseFrequency = pulseFrequency;
	}

	public XYSeries call() throws Exception {

		integrator.setReceivePulseInterval(pulseFrequency);
		integrator.addSpikeListener(this);
		run();

		return super.getMembranePotential();
	}

	public double getQuality() {
		return 1.0 - ((double)numSpikesWanted / numPulses) + ((double)numSpikesUnwanted / numPulses);
	}

	@Override
	public int compareTo(FreqencyQualitySeriesCreator other) {
		return (int)(getQuality()*Integer.MAX_VALUE - other.getQuality()*Integer.MAX_VALUE);
	}

	@Override
	public void spikeGenerated(SpikeEvent evt) {
		super.spikeGenerated(evt);

		spikeGenerated = true;
	}

	@Override
	public void stepFinnished(UpdateEvent evt) {
		super.stepFinnished(evt);

		final boolean shouldHaveGeneratedSpike = (integrator.getTime() > 0.0)
				&& ((((integrator.getTime() % integrator.getReceivePulseInterval()) < EPSILON)
				|| (Math.abs((integrator.getTime() % integrator.getReceivePulseInterval()) - integrator.getReceivePulseInterval()) < EPSILON)));

		if (shouldHaveGeneratedSpike) {
			numPulses++;
		}

		if (shouldHaveGeneratedSpike == spikeGenerated) {
			// goody good-good
			if (spikeGenerated) {
//System.out.println(integrator.getTime() + ":\t\tgot wanted spike!");
				numSpikesWanted++;
			}
		} else {
			// baaad
			if (spikeGenerated) {
				numSpikesUnwanted++;
//System.out.println(integrator.getTime() + ":\t\tgot unwanted spike!");
			} else {
//System.out.println(integrator.getTime() + ":\t\tdid not get wanted spike!");
				numSpikesMissing++;
			}
		}

		spikeGenerated = false;
	}
}
