package tu.modgeh.spiketrain;


import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class InfoPerTbVisualizer {

	public InfoPerTbVisualizer() {
	}

	private static XYItemRenderer createRenderer() {

		DefaultXYItemRenderer renderer = null;

		renderer = new DefaultXYItemRenderer();

		renderer.setDrawSeriesLineAsPath(false);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseLinesVisible(true);
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(true);

		return renderer;
	}

	private static ValueAxis createDomainAxis() {

		NumberAxis axis = null;

		axis = new NumberAxis("1/T_b (sub-sequence length)");
//		axis.setAutoRange(true);
		axis.setLowerBound(0.0);
		axis.setUpperBound(1.0);

		return axis;
	}

	private static ValueAxis createRangeAxis() {

		NumberAxis axis = null;

		axis = new NumberAxis("H (Information-rate)");
		axis.setAutoRange(true);

		return axis;
	}

	public static ChartPanel createCanvas() {

		ChartPanel panel = null;

		final XYSeriesCollection currentFreqsSeries = new XYSeriesCollection();

		final int tau = 1;
		final int f = 0;
		final int tbStart = 1;
		final int tbEnd = 20;

		SpikeTrain spikeTrain = new SpikeTrain(tau);

System.out.println("calculating ...");
		XYSeries hApprox = new XYSeries("H approx (tau = " + tau + "ms, f = " + f + "s^-1)");
		for (int tb = tbStart*tau; tb <= tbEnd*tau; tb += tau) {
			final double H = spikeTrain.calcH(tb, f);
			hApprox.add(1.0/tb, H);
		}
		currentFreqsSeries.addSeries(hApprox);

		XYDataItem last = (XYDataItem) hApprox.getItems().get(hApprox.getItemCount() - 1);
		XYDataItem secondLast = (XYDataItem) hApprox.getItems().get(hApprox.getItemCount() - 2);
		double H_real = last.getYValue() - (last.getYValue() - secondLast.getYValue()) / (last.getXValue() - secondLast.getXValue()) * last.getXValue();
		XYSeries hReal = new XYSeries("H real (" + H_real + ")");
		hReal.add(0.0, H_real);
		hReal.add(last);
		currentFreqsSeries.addSeries(hReal);

		ValueAxis rangeAxis = createRangeAxis();
		ValueAxis domainAxis = createDomainAxis();

		XYPlot plot = new XYPlot(currentFreqsSeries, domainAxis, rangeAxis, createRenderer());

		plot.setDomainCrosshairVisible(true);

		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		JFreeChart chart = new JFreeChart(plot);
		panel = new ChartPanel(chart);

		return panel;
	}

    public void run() {

		final JFrame mainFrame = new JFrame("8.2/8.3 Spike train");
		mainFrame.setSize(800, 500);

		ChartPanel canvas = createCanvas();
		canvas.setBorder(new EmptyBorder(10, 10, 10, 10));

		mainFrame.getContentPane().add(canvas);

		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainFrame.setVisible(true);
			}
		});
	}
}
