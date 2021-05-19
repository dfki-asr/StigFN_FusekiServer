package de.dfki.asr.stigFN;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase4;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class diffusion_distance extends FunctionBase4 {
    @Override
    public NodeValue exec(NodeValue distance, NodeValue duration, NodeValue concentration, NodeValue rate) {
        double L=20;
        double nx = 100;
        double T = duration.getDouble();            // can be taken from input of sparql function (duration)
        double nt = T*10;
        double alpha = rate.getDouble();            // taken from sparql function (rate)
        double conc = concentration.getDouble();    // initial concentration taken from sparql function (concentration)
        double dist = distance.getDouble();         //distance from center, taken from sparql function (distance)

        double[] x = new double[(int)nx+1];
        linspace(x, 0, L, L / nx);
        double dx = x[1] - x[0];
        double[] t = new double[(int)nt+1];
        linspace(t, 0, T, T / nt);
        double dt = t[1] - t[0];
        double F = alpha*dt/(dx*dx);
        double fac = 1.0 - 2.0*F;
        double[] init_conc = new double[(int)nx+1];
        boolean req_plot = false;
        for(int i=0; i<init_conc.length; i++)   //calculating the initial function
        {
            if(x[i]>=((L/2)-0.5) && x[i]<=((L/2)+0.5))
                init_conc[i] = conc*Math.sin(2*Math.PI*5*(x[i]-((L/2)-0.5))/L);
            else
                init_conc[i] = 0;
        }

        List<Double> xList = new ArrayList<>();  //converting to lists, because the "plot" python package requires this
        for(double n:x){
//            System.out.println(n);
            xList.add(n);
        }
//        System.out.println("List: "+xList);
        List<Double> y1 = new ArrayList<>();     //converting to lists, because the "plot" python package requires this
        for(double n:init_conc){
//            System.out.println(im+"--->"+n);
            y1.add(n);
        }

        double[] conc_old = init_conc;
        double[] conc_new = new double[(int)nx+1];

        for(int j=0; j<nt; j++)                 //iteratively calculating the diffusion curve
        {
            for (int i=1; i<conc_new.length-1; i++)
            {
                conc_new[i]= round(fac*conc_old[i]+F*(conc_old[i-1]+conc_old[i+1]),8);
            }
            conc_old = conc_new;
        }

        List<Double> y = new ArrayList<>();     //as above, required for the "plot" python package
        for(double n:conc_new){
            y.add(n);
        }

//        System.out.println("Final concentration at distance "+distance+" from point of pheromone deposition after "+T+" seconds is "+conc_new[(int)(((L/2)-dist)*(nx/L))]);
        double output = 0.0;            //diffusion intensity at a given distance after a given duration.

        if(dist<L/2)          //deliberately limiting the spatial domain of diffusion to 10 units (5 on either side of the point where the stigma is deposited)
        {
            output = conc_new[(int) (((L / 2) - dist) * (nx / L))];   //diffusion intensity within "area of effect" i.e 10 distance units
        }

        if(req_plot) {      //option to plot the result if necessary using pyhton "plot" package
            Plot plt = Plot.create();
            plt.plot()
                    .add(xList, y)
                    .label("Diffused profile")
                    .linestyle("-");
            plt.plot()
                    .add(xList, y1)
                    .label("Initial pheromone deposit")
                    .linestyle("-");
            plt.xlabel("space");
            plt.ylabel("intensity");
            plt.xlim(0, 21);
            plt.title("Diffused curve after " + T + " seconds");
            plt.legend();
            try {
                plt.show();
            } catch (IOException | PythonExecutionException e) {
                e.printStackTrace();
            }
        }

        return NodeValue.makeDouble(output);
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private static void linspace(double[] array, double start, double end, double space)
    {
        array[0] = start;
        for(int i=1; i<array.length; i++) {
            array[i] = round(array[i-1]+space,1);
        }
    }
}




