/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvnn;

import ComplexNumber.Complex;
import Optimization.ABC;
import Optimization.DEvolution;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Varun
 */
public class MVNN {

    public static void main(String args[]) {

        int numberOfSectors = 6;
        double sectorSize = 2 * Math.PI / numberOfSectors;
        double numberOfSectorHalf = Math.floor(numberOfSectors / 2);
        boolean discreate_inputs = false;
        boolean discreate_outputs = true;
        try {
            //************************ DATA INPUT * ************************
            //Start Data reading
            String input_file = "glass_conv.txt";
            FileReader fin = new FileReader(input_file);
            BufferedReader br = new BufferedReader(fin);
            String line;
            int patterns = 0;
            int input_features = 13;
            int output_features = 1;
            while ((line = br.readLine()) != null) {
                patterns++;
            }
            br.close();
            fin.close();
            double[][] dataX = new double[patterns][input_features];
            double[][] dataY = new double[patterns][output_features];

            FileReader fin1 = new FileReader(input_file);
            BufferedReader br1 = new BufferedReader(fin1);
            for (int i = 0; i < patterns; i++) {
                line = br1.readLine();
                String[] tokens = line.split(",");
                //System.out.printf("%d  :  ",i);
                for (int j = 0; j < tokens.length; j++) {
                    double valX = Double.parseDouble(tokens[j]);
                    if (j < input_features) {
                        dataX[i][j] = valX;
                        //System.out.printf(" %1.3f ", valX);
                    } else {
                        dataY[i][j - input_features] = valX;
                        //System.out.printf("  -> %1.1f ", valX);
                    }
                }
                //System.out.println();
            }
            //End data reading

            //* ************************ DATA Transformation *****************
            //START Converining data to complex number
            Pattern[] pat = new Pattern[patterns];
            Complex[][] dataXComplex = new Complex[patterns][input_features];
            Complex[][] dataYComplex = new Complex[patterns][output_features];
            double[][] dataYAngular = new double[patterns][output_features];
            for (int i = 0; i < patterns; i++) {
                //System.out.printf("%d  :  ",i);
                for (int j = 0; j < input_features; j++) {
                    if (discreate_inputs) {//deal with descrete inputs
                        //do nothing
                    } else {//deal with descrete input
                        dataXComplex[i][j] = Complex.real2complex(dataX[i][j], 2);
                    }
                }
                for (int j = 0; j < output_features; j++) {
                    if (discreate_outputs) {//deal with descrete outputs
                        dataYComplex[i][j] = Complex.real2complex((dataY[i][j] + 0.5) * sectorSize);
                        double angle = dataYComplex[i][j].phase();
                        double twopi = 2 * Math.PI;
                        double angleMode2pi = (angle - (Math.floor(angle / twopi) * twopi));
                        dataYAngular[i][j] = angleMode2pi;
                    } else {//deal with contunous outputs
                        dataYComplex[i][j] = Complex.real2complex(dataY[i][j]);
                        dataYAngular[i][j] = dataY[i][j];
                    }
                    //System.out.println(dataYAngular[i][j]);
                }
                //System.out.println();
                pat[i] = new Pattern(dataXComplex[i], dataYComplex[i], dataYAngular[i], dataY[i]);//Setting the patterns
            }//END Converining data to complex number        

            //************************ Network Formation **********************
            int in_node = input_features;
            int hi_node = 10;
            int ot_node = output_features;

            // create weights
            Complex[][] wi = new Complex[in_node + 1][hi_node];
            Complex[][] wo = new Complex[hi_node + 1][ot_node];

            // nodes activation value
            Complex[] ai = new Complex[in_node];
            Complex[] ah = new Complex[hi_node];
            Complex[] ao = new Complex[ot_node];
            double[][] netOut = new double[patterns][ot_node];

            // nodes weighted sum activation value
            Complex[] wsumh = new Complex[hi_node];
            Complex[] wsumo = new Complex[ot_node];
            // nodes error sum activation value
            Complex[] errh = new Complex[hi_node];
            Complex[] erro = new Complex[ot_node];

            System.out.printf("in_node %d,hi_node %d,ot_node %d", in_node, hi_node, ot_node);

//            double[][] aRe = {
//                {0.9020, 0.7021, 0.3775, 0.7350, 0.9541, 0.5428, 0.5401, 0.3111, 0.0712, 0.1820, 0.0930, 0.4635},
//                {0.0093, 0.9150, 0.6427, 0.0014, 0.0304, 0.2085, 0.4550, 0.1273, 0.0086, 0.7271, 0.3541, 0.7804},
//                {0.4367, 0.4366, 0.0492, 0.0496, 0.0911, 0.5940, 0.2411, 0.8414, 0.8572, 0.9636, 0.4889, 0.2203},
//                {0.2262, 0.5368, 0.7621, 0.3476, 0.4612, 0.6393, 0.9173, 0.1616, 0.7156, 0.5777, 0.4333, 0.8842},
//                {0.3931, 0.1790, 0.6333, 0.6240, 0.3279, 0.8030, 0.9995, 0.9810, 0.1270, 0.2322, 0.0236, 0.6074},
//                {0.1108, 0.4075, 0.8841, 0.5481, 0.3690, 0.2083, 0.4409, 0.9562, 0.1240, 0.4708, 0.8569, 0.0434},
//                {0.6916, 0.9790, 0.2833, 0.1338, 0.6853, 0.9095, 0.6109, 0.9000, 0.1934, 0.7544, 0.3463, 0.4186},
//                {0.1557, 0.8190, 0.6249, 0.7386, 0.8051, 0.0672, 0.9508, 0.4976, 0.7551, 0.7424, 0.8311, 0.1565},
//                {0.4573, 0.6181, 0.9322, 0.8351, 0.8954, 0.5825, 0.5827, 0.8549, 0.0349, 0.8854, 0.4077, 0.0364},
//                {0.7461, 0.1548, 0.1439, 0.6060, 0.2545, 0.3242, 0.4018, 0.4064, 0.3862, 0.6098, 0.1669, 0.1881}};
//
//            double[][] aIm = {
//                {0.8103, 0.5570, 0.2630, 0.6806, 0.2337, 0.4564, 0.3846, 0.5386, 0.9917, 0.7552, 0.9805, 0.2348},
//                {0.5286, 0.0514, 0.7569, 0.6020, 0.8572, 0.9883, 0.9295, 0.4095, 0.0003, 0.5409, 0.2077, 0.2193},
//                {0.3258, 0.0959, 0.7475, 0.7485, 0.5433, 0.3381, 0.8323, 0.5526, 0.9575, 0.8928, 0.3565, 0.5464},
//                {0.3467, 0.6228, 0.7966, 0.7459, 0.1255, 0.8224, 0.0252, 0.4144, 0.7314, 0.7814, 0.3673, 0.7449},
//                {0.8923, 0.2426, 0.1296, 0.2251, 0.3500, 0.2871, 0.9275, 0.0513, 0.5927, 0.1629, 0.8384, 0.1676},
//                {0.5022, 0.9993, 0.3554, 0.0471, 0.2137, 0.3978, 0.3337, 0.2296, 0.9361, 0.6832, 0.9621, 0.4380},
//                {0.9403, 0.0058, 0.6103, 0.8011, 0.2330, 0.9325, 0.7633, 0.8264, 0.5735, 0.7926, 0.3290, 0.2235},
//                {0.3124, 0.5845, 0.8299, 0.2905, 0.4026, 0.8621, 0.6147, 0.9912, 0.2037, 0.8272, 0.6759, 0.2489},
//                {0.4758, 0.3991, 0.5994, 0.8005, 0.1051, 0.8214, 0.8411, 0.3545, 0.4301, 0.5722, 0.7008, 0.7425},
//                {0.7579, 0.3891, 0.4293, 0.9563, 0.5730, 0.8497, 0.2763, 0.6223, 0.5884, 0.9635, 0.0859, 0.5005}};
//
//            double[][] bRe = {{0.0946}, {0.3232}, {0.7696}, {0.2341}, {0.7404}, {0.6928}, {0.8241}, {0.8280}, {0.2934}, {0.3094}, {0.5230}, {0.3253}, {0.8318}};
//            double[][] bIm = {{0.5216}, {0.0902}, {0.9047}, {0.8844}, {0.4390}, {0.7817}, {0.1485}, {0.6198}, {0.2606}, {0.4457}, {0.8440}, {0.1962}, {0.3039}};

            double localThreshold = 0.3236;
            Network net = new Network(in_node, hi_node, ot_node, discreate_inputs, discreate_outputs, numberOfSectors, "ErrorRate",localThreshold);
            net.test(pat);

            int population = 50;
            int dimension = ((in_node + 1) * hi_node + (hi_node+1) * ot_node)*2;//multiplied by 2 becuase of re + im
            double[] low = new double[dimension];
            double[] high = new double[dimension];
            int stepCount = 100;
            boolean stepPrint = false;
            boolean stepFinal = false;
            int m_MaxIterations = 1000;
            String algo = "DE";
            double wtsMin = -0.5;
            double wtsMax = 0.5;

            switch (algo) {
            case "ABC": {
                ABC obj = new ABC();
                obj.setDimension(dimension);
                obj.setPopulation(population);
                obj.setMaxIteration(m_MaxIterations);
                obj.setArrays();
                obj.setNetwork(net);
                obj.setTrainingPattern(pat);
                for (int l = 0; l < dimension; l++) {
                    low[l] = wtsMin;
                    high[l] = wtsMax;
                }// end for dimension
                obj.setBounds(low, high);
                obj.setSeed(9967);
                obj.setPrintStep(stepCount);
                obj.setPrintStep(stepPrint);
                obj.setPrintFinal(stepFinal);
                obj.execute();
                break;
            }
            case "BFO": {

                break;
            }
            case "DE": {
                DEvolution obj = new DEvolution();
                obj.setDimension(dimension);
                obj.setPopulation(population);
                obj.setMaxIteration(m_MaxIterations);
                obj.setArrays();
                obj.setNetwork(net);
                obj.setTrainingPattern(pat);
                for (int l = 0; l < dimension; l++) {
                    low[l] = wtsMin;
                    high[l] = wtsMax;
                }// end for dimension
                obj.setBounds(low, high);
                obj.setSeed(897456453);
                obj.setPrintStep(stepCount);
                obj.setPrintStep(stepPrint);
                obj.setPrintFinal(stepFinal);
                obj.execute();
                break;
            }
            case "GA": {
                System.out.println("No code available");
                break;
            }
            case "GWO": {

                break;
            }
            case "PSO": {

                break;
            }
        }

            
        //System.exit(1);
        
            // set them to random vaules
            for (int i = 0; i < in_node + 1; i++) {
                for (int j = 0; j < hi_node; j++) {
                    //wi[i][j] = Complex.randomComplex(1); //Methhod 1
                    double minReVal = Math.random() - 0.5;
                    double maxImgVal = Math.random() - 0.5;
                    //double minReVal = aRe[i][j] - 0.5;
                    //double maxImgVal = aIm[i][j] - 0.5;
                    wi[i][j] = new Complex(minReVal, maxImgVal);
                }
            }
            for (int i = 0; i < hi_node + 1; i++) {
                for (int j = 0; j < ot_node; j++) {
                    //wo[i][j] = Complex.randomComplex(1);
                    double minReVal = Math.random() - 0.5;
                    double maxImgVal = Math.random() - 0.5;
                    //double minReVal = bRe[i][j] - 0.5;
                    //double maxImgVal = bIm[i][j] - 0.5;
                    wo[i][j] = new Complex(minReVal, maxImgVal);
                }
            }
            //************************ Network Initialization *****************
            //Main Training loop
            boolean finishTraining = false;
            int iteration = 0;
            while (!finishTraining) {//for all traiining epocs
                //Training patterns
                for (int i = 0; i < patterns; i++) {//for all patterns
                    //********************** Calculating network outputs*******
                    // input activations
                    //System.out.println();
                    for (int j = 0; j < in_node; j++) {
                        ai[j] = dataXComplex[i][j];
                        //System.out.println("  "+ai[j]);
                    }
                    //System.out.println();

                    //hidden activations
                    for (int j = 0; j < hi_node; j++) {
                        Complex sum = new Complex(0.0, 0.0);
                        for (int k = 1; k < in_node + 1; k++) {
                            sum = sum.plus(ai[k - 1].times(wi[k][j]));
                            //System.out.print("  "+wi[k][j]);
                        }
                        //System.out.println();
                        //System.out.println(wi[0][j]);
                        wsumh[j] = sum.plus(wi[0][j]);//weighted sum bis at the 0-th index
                        double absMultiplicativeFactor = 1 / wsumh[j].abs();
                        ah[j] = wsumh[j].times(absMultiplicativeFactor);//activation 
                        //System.out.println("  "+ah[j]);
                    }
                    //System.out.println();
                    //output activations        
                    for (int j = 0; j < ot_node; j++) {
                        Complex sum = new Complex(0.0, 0.0);
                        for (int k = 1; k < hi_node + 1; k++) {
                            sum = sum.plus(ah[k - 1].times(wo[k][j]));
                            //System.out.print("  "+wo[k][j]);
                        }
                        //System.out.println();
                        //System.out.println(wo[0][j]);
                        wsumo[j] = sum.plus(wo[0][j]);//weighted sum
                        //System.out.println(wsumo[j]);
                        double absMultiplicativeFactor = 1 / wsumo[j].abs();
                        ao[j] = wsumo[j].times(absMultiplicativeFactor);//activation value
                        if (discreate_outputs) {//deal with descrete outputs
                            double angle = (wsumo[j].phase());
                            double twopi = 2 * Math.PI;
                            double angleMode2pi = (angle - (Math.floor(angle / twopi) * twopi));
                            //System.out.println(angleMode2pi);
                            netOut[i][j] = Math.floor(angleMode2pi / sectorSize);//network output for descrete varibles
                            //System.out.println(netOut[i][j]);
                        } else {//contunous
                            double angle = (ao[j].phase());
                            double twopi = 2 * Math.PI;
                            double angleMode2pi = (angle - (Math.floor(angle / twopi) * twopi));
                            netOut[i][j] = angleMode2pi;// network output for Continuouis variable
                        }
                    }
                    //System.out.println();
                }//END of network output calculations for all patterns

                //********************** Calculating network errors *******
                double[][] errors = new double[patterns][ot_node];
                double sse = 0.0;
                //Training patterns
                for (int i = 0; i < patterns; i++) {//for all patterns
                    double current_error = 0.0;
                    for (int j = 0; j < ot_node; j++) {
                        if (discreate_outputs) {//deal with descrete outputs  
                            //System.out.printf("\n %.1f\t%.1f", netOut[i][j], dataY[i][j]);
                            errors[i][j] = Math.abs(netOut[i][j] - dataY[i][j]);
                            //System.out.printf("%.1f\t", errors[i][j]);
                            if (errors[i][j] > numberOfSectorHalf) {
                                errors[i][j] = numberOfSectors - errors[i][j];
                            }
                        } else {//contunous
                            errors[i][j] = Math.abs(netOut[i][j] - dataYAngular[i][j]);
                            if (errors[i][j] > Math.PI) {
                                errors[i][j] = Math.PI - errors[i][j];
                            }
                        }
                        //System.out.printf("%.1f \n", errors[i][j]);
                        current_error = current_error + errors[i][j] * errors[i][j];
                    }
                    sse = sse + current_error;
                }
                double mse = sse / patterns;
                double rmse = Math.sqrt(mse);
                //End error calculation
                if (iteration % 100 == 0) {
                    System.out.printf("\nIteration %5d  RMSE: %.4f", iteration, rmse);
                    break;
                }
                break;
//                if (rmse > 0.05) {
//                    finishTraining = true;
//                } else {
//                    //********************** Updating Weights *****************
//                    for (int i = 0; i < patterns; i++) {//for all patterns
//                        //********************** Calculating network outputs*******
//                        // input activations
//                        for (int j = 0; j < in_node; j++) {
//                            ai[j] = dataXComplex[i][j];
//                        }
//
//                        //hidden activations
//                        for (int j = 0; j < hi_node; j++) {
//                            Complex sum = new Complex(0.0, 0.0);
//                            for (int k = 0; k < in_node; k++) {
//                                sum = sum.plus(ai[k].times(wi[k][j]));
//                                //System.out.println((ai[l].times(wi[l][k])));
//                            }
//                            wsumh[j] = sum;//weighted sum
//                            double absMultiplicativeFactor = 1 / sum.abs();
//                            ah[j] = sum.times(absMultiplicativeFactor);//activation 
//                        }
//
//                        //output activations        
//                        for (int j = 0; j < ot_node; j++) {
//                            Complex sum = new Complex(0.0, 0.0);
//                            for (int k = 0; k < hi_node; k++) {
//                                sum = sum.plus(ah[k].times(wo[k][j]));
//                            }
//                            wsumo[j] = sum;//weighted sum
//                            double absMultiplicativeFactor = 1 / sum.abs();
//                            ao[j] = sum.times(absMultiplicativeFactor);//activation value
//                            if (!discreate_outputs) {//deal with contunous outputs
//                                netOut[i][j] = Math.abs(((ao[j].phase()) % (2 * Math.PI)));// network output for Continuouis variable
//                            } else {
//                                netOut[i][j] = Math.floor(((sum.phase()) % (2 * Math.PI)) / sectorSize);//network output for descrete varibles
//                            }
//                            erro[j] = dataYComplex[i][j].minus(ao[j]);
//                            erro[j] = erro[j].times((1 / hi_node));
//                        }
//                        //network weight sum and output calculation
//                        for (int j = 0; j < hi_node; j++) {
//                            Complex sum = new Complex(0.0, 0.0);
//                            for (int k = 0; k < ot_node; k++) {
//                                Complex temp = wo[j][k];
//                                temp = temp.times(0.5);
//                                temp = temp.times(erro[k]);
//                                temp = temp.times((1 / in_node));
//                                sum = sum.plus(temp);//Notmalize network error
//                            }
//                            errh[j] = sum;
//                        }
//
//                        // inputs
//                        for (int j = 0; j < in_node; j++) {
//                            ai[j] = dataXComplex[i][j];
//                        }
//
//                        //hidden activations
//                        for (int j = 0; j < hi_node; j++) {
//                            double lr = 1 / (wsumh[j].abs());
//                            for (int k = 0; k < in_node; k++) {
//                                Complex temp = ai[k].times(lr).times(errh[j]);
//                                wi[k][j] = wi[k][j].plus(temp);//changing weight
//                            }
//                        }
//
//                        //output activations        
//                        for (int j = 0; j < ot_node; j++) {
//                            double lr = 1 / (wsumo[j].abs());
//                            for (int k = 0; k < hi_node; k++) {
//                                Complex temp = ah[k].times(lr).times(erro[j]);
//                                wo[k][j] = wo[k][j].plus(temp);//changing weight
//                            }
//                        }
//                    }//END of network output calculations for all patterns
//                }
//                iteration++;
            }
            //End
            System.out.println(" Succefully calculated network outputs");
        } catch (IOException | NumberFormatException e) {
            System.out.println(e);
        }
    }//End of main
}//End of class
