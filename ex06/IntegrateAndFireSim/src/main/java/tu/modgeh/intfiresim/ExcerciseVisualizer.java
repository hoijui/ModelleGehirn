package tu.modgeh.intfiresim;


import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ExcerciseVisualizer {

	public ExcerciseVisualizer() {
	}

	public static class CrossShape extends Path2D.Double {

		public CrossShape(double crossRadius) {

			moveTo( crossRadius,  crossRadius);
			lineTo(-crossRadius, -crossRadius);
			moveTo(-crossRadius,  crossRadius);
			lineTo( crossRadius, -crossRadius);
		}
		public CrossShape() {
			this(3.0);
		}
	}

	public static class SpikeShape extends Path2D.Double {

		private SpikeShape(double height, double offset) {

			moveTo(0.0, -offset);
			lineTo(0.0, -offset -height);
		}
		public SpikeShape(double height) {
			this(height, 0.0);
		}
		public SpikeShape() {
			this(100.0);
		}
	}

	private static XYItemRenderer createRenderer() {

		StandardXYItemRenderer renderer = null;

		renderer = new StandardXYItemRenderer();

		renderer.setDrawSeriesLineAsPath(false);
		renderer.setPlotLines(false);
		renderer.setBaseItemLabelsVisible(false);
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(false);

		Shape spikeShape = new SpikeShape();
		Shape defaultShape = renderer.getBaseShape();
		for (int i = 0; i < 100; i++) {
			renderer.setSeriesVisible(i, Boolean.TRUE);
			renderer.setSeriesShape(i, spikeShape);
			renderer.setLegendShape(i, defaultShape);
		}

		return renderer;
	}

	private static ValueAxis createDomainAxis() {

		NumberAxis axis = null;

		axis = new NumberAxis();
		axis.setLowerBound(-0.05);
		axis.setUpperBound(1.05);

		return axis;
	}

	private static ValueAxis createRangeAxis(double maxValue) {

		NumberAxis axis = null;

		axis = new NumberAxis();
		axis.setLowerBound(-0.2);
		axis.setUpperBound(maxValue);

		return axis;
	}

	public static ChartPanel createCanvas(List<Integrator> integrators) {

		ChartPanel panel = null;

		XYSeriesCollection dataCollection = new XYSeriesCollection();

		double value = 0.0;
		for (Integrator integrator : integrators) {
			IntegrationTimeSeriesCreator dataGenerator = new IntegrationTimeSeriesCreator(integrator, value);
			XYSeries data = dataGenerator.getData();
			dataCollection.addSeries(data);

			value += 1.0;
		}

		ValueAxis rangeAxis = createRangeAxis(value);
		ValueAxis domainAxis = createDomainAxis();

		XYPlot plot = new XYPlot(dataCollection, domainAxis, rangeAxis, createRenderer());

		plot.setDomainCrosshairVisible(true);

		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		JFreeChart chart = new JFreeChart(plot);
		panel = new ChartPanel(chart);

		return panel;
	}

	private static class IntegrationTimeSeriesCreator implements SpikeListener, UpdateListener {

		private XYSeries data = null;
		private Integrator integrator = null;
		private double value = 0.0;

		private IntegrationTimeSeriesCreator(Integrator integrator, double value) {

			this.integrator = integrator;
			this.data = new XYSeries(integrator.toString());
			this.value = value;

			integrator.addSpikeListener(this);
			integrator.addUpdateListener(this);

			integrator.runSimulation(1.0);
		}

		public XYSeries getData() {
			return data;
		}

		@Override
		public void spikeGenerated(SpikeEvent evt) {
			data.add(integrator.getTime(), value);
		}

		@Override
		public void stepFinnished(UpdateEvent evt) {
		}
	}

    public void run() {

		final JFrame mainFrame = new JFrame("6.a) Spike-rates on different noise-currents");
		mainFrame.setSize(900, 500);

		List<Integrator> integrators = new ArrayList<Integrator>();

		Integrator integrator_5nA = new Integrator();
		integrator_5nA.setCurrent(0.5E-9);
		integrators.add(integrator_5nA);

		Integrator integrator_7nA = new Integrator();
		integrator_7nA.setCurrent(0.7E-9);
		integrators.add(integrator_7nA);

		Integrator integrator_9nA = new Integrator();
		integrator_9nA.setCurrent(0.9E-9);
		integrators.add(integrator_9nA);

		ChartPanel canvas = createCanvas(integrators);
		canvas.setBorder(new EmptyBorder(10, 10, 10, 10));

		mainFrame.getContentPane().add(canvas);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mainFrame.setVisible(true);
            }
        });
    }
}
