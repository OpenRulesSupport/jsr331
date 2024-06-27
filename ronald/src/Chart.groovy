import groovy.swing.SwingBuilder
import com.thecoderscorner.gfreechart.GFreeChartBuilder

import java.awt.BorderLayout
import java.awt.Color

//import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
//import org.jfree.chart.axis.AxisLocation
//import org.jfree.chart.axis.NumberAxis
//import org.jfree.chart.axis.LogarithmicAxis
//import org.jfree.chart.renderer.xy.StandardXYItemRenderer
//import org.jfree.data.xy.XYSeries
//import org.jfree.data.xy.XYSeriesCollection
//import org.jfree.chart.plot.PlotOrientation
//import org.jfree.chart.plot.XYPlot


        /*
        def dataset = new XYSeriesCollection()

        def addCase = {String desc, Map parameters ->
            def series = new XYSeries(desc)
            println maxC
            for (double x=maxC/100.0; x<maxC; x += maxC/300.0) {
                //Formula execution should probably move elsewhere
                parameters.each() { k, v ->
                    effect.formula.setProperty(k, v)
                }
                effect.formula.setProperty(cpd.abbreviation, x)
                series.add(x, effect.formula())
            }
            dataset.addSeries(series)
        }
        addCase("Wild-type", effect.parameters)
        effect.resistances.each {Resistance res -> 
            addCase(res.mutations.toString(), res.parameters)
        }
        plot.setDomainAxis(0, new LogarithmicAxis("Concentration"))
        plot.setDataset(0, dataset)
        return new ChartPanel(chart)
        */
class Chart {
    static getEffectChart(Effect effect, Compound cpd, Integer maxC) {
        def chart = new GFreeChartBuilder()
        chart.createLine(title: "Pie Chart", legend: true, size: [400,200]) {
            dataset {
                First(20)
                Second(30)
                Third(10)
                Fourth(40)
            }
            antiAlias true
            backgroundPaint Color.WHITE

            plot {
                font name: 'arial', height: 15
                simpleGradient start: new Color(0,0,255), end: new Color(255,255,255)
            }
        }
        return new ChartPanel(chart.theChart)
    }
    static test(Effect effect, Compound cpd, Integer maxC) {
        def swing = new SwingBuilder()
        def frame = swing.frame(title:'Frame', size:[300,300]) {
            borderLayout()
            container(getEffectChart(effect, cpd, maxC))
        }

        frame.pack()
        frame.show()
    }

}
