package tu.modgeh.intfiresim;

public class Tester implements SpikeListener, UpdateListener {

	public Tester(double i) {

System.out.println("running sim (i = " + i + "A) ...");
System.out.println("");
		Integrator integrator = new Integrator();
		integrator.addSpikeListener(this);
		integrator.addUpdateListener(this);

		integrator.setCurrent(i);

		integrator.runSimulation(1.0);
System.out.println("");
		System.out.println("done.");
	}

	@Override
	public void spikeGenerated(SpikeEvent evt) {
System.out.println(" spike!");
	}

	@Override
	public void stepFinnished(UpdateEvent evt) {
System.out.print("*");
	}
}
