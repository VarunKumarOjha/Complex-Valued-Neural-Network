/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvnn;

import ComplexNumber.*;

/**
 *
 * @author Varun
 */
public class Network {

    int ni;//neuron at input layer
    int nh;//neuron at hidden layer
    int no;//neuron at output layer  

    Complex[] ai;
    Complex[] ah;
    Complex[] ao;

    Complex[] wsumh;
    Complex[] wsumo;

    Complex[][] wi;
    Complex[][] wo;

    Complex[][] ci;
    Complex[][] co;

    private boolean discreate_outputs;
    private double sectorSize;
    private int numberOfSectors;
    private double numberOfSectorHalf;
    private boolean discreate_inputs;
    private final String error_type;
    private final double localThreshold;

    public Network(int ini, int inh, int ino, boolean dis_in, boolean dis_out, int secNo, String er_type,double localTh) {
        //# number of input, hidden, and output nodes
        ni = ini; // +1 for bias node
        nh = inh;
        no = ino;

        ai = new Complex[ni];
        ah = new Complex[nh];
        ao = new Complex[no];

        wsumh = new Complex[nh];
        wsumo = new Complex[no];

        //# init activations for nodes
        for (int i = 0; i < ni; i++) {
            ai[i] = new Complex(1.0, 1.0);
        }
        for (int i = 0; i < nh; i++) {
            ah[i] = new Complex(1.0, 1.0);
        }
        for (int i = 0; i < no; i++) {
            ao[i] = new Complex(1.0, 1.0);
        }

        // create weights
        wi = new Complex[ni + 1][nh];
        wo = new Complex[nh + 1][no];

        // set them to random vaules
        for (int i = 0; i < ni + 1; i++) {//number of inpout node +1 bias
            for (int j = 0; j < nh; j++) {
                //wi[i][j] = Complex.randomComplex(1); //Methhod 1
                double minReVal = Math.random() - 0.5;
                double maxImgVal = Math.random() - 0.5;
                //double minReVal = aRe[i][j] - 0.5;
                //double maxImgVal = aIm[i][j] - 0.5;
                wi[i][j] = new Complex(minReVal, maxImgVal);
            }
        }
        for (int i = 0; i < nh + 1; i++) {//number of hidden node +1 bias
            for (int j = 0; j < no; j++) {
                //wo[i][j] = Complex.randomComplex(1);//Mathod 2
                double minReVal = Math.random() - 0.5;
                double maxImgVal = Math.random() - 0.5;
                //double minReVal = bRe[i][j] - 0.5;
                //double maxImgVal = bIm[i][j] - 0.5;
                wo[i][j] = new Complex(minReVal, maxImgVal);
            }
        }
        // last change in weights for momentum   
        ci = new Complex[ni + 1][nh];
        co = new Complex[nh + 1][no];

        numberOfSectors = secNo;
        sectorSize = 2 * Math.PI / numberOfSectors;
        numberOfSectorHalf = Math.floor(numberOfSectors / 2);
        discreate_inputs = dis_in;
        discreate_outputs = dis_out;
        error_type = er_type;
        localThreshold = localTh;
    }//end constructor

    public double test(Pattern[] patterns) {
        System.out.println();
        double error = 0.0;
        int countZeros = 0;
        
        for (int p = 0; p < patterns.length; p++) {
            double current_error = 0.0;
            Complex[] networkOutSums = update(patterns[p]);//receive ouput nodes weighted sums
            for (int j = 0; j < networkOutSums.length; j++) {//for all node at output layer
                double node_error = 0.0;
                if (discreate_outputs) {//deal with descrete outputs
                    double angle = networkOutSums[j].phase();
                    double twopi = 2 * Math.PI;
                    double angleMode2pi = (angle - (Math.floor(angle / twopi) * twopi));
                    double node_out = Math.floor(angleMode2pi / sectorSize);//network output for descrete varibles
                    node_error = Math.abs(node_out - patterns[p].outputs[j]);
                    //System.out.printf("%.1f\t", node_error);
                    if (node_error > numberOfSectorHalf) {
                        node_error = numberOfSectors - node_error;
                    }
                } else {
                    double angle = (activation(networkOutSums[j]).phase());
                    double twopi = 2 * Math.PI;
                    double angleMode2pi = (angle - (Math.floor(angle / twopi) * twopi));
                    double node_out = angleMode2pi;// network output for Continuouis variable
                    node_error = Math.abs(node_out - patterns[p].angularTarget[j]);
                    if (node_error > Math.PI) {
                        node_error = Math.PI - node_error;
                    }
                }
                if(error_type.equalsIgnoreCase("ErrorRate")){
                    if(node_error > localThreshold){
                        node_error = 1;
                    }else{
                        node_error = 0;
                    }
                    if(node_error == 0){
                         countZeros++;
                    }
                }
                //System.out.printf("%.1f\n", node_error);
                current_error = current_error + node_error * node_error;
            }//go to next node	
            error = error + current_error;
        }
        error = error / patterns.length;
        error = Math.sqrt(error);
        if(error_type.equalsIgnoreCase("ErrorRate")){
           error = (1.0 - (countZeros+0.0)/patterns.length);
           System.out.println("Total correct classification " + countZeros+ " out of "+patterns.length +" : "+error);
        }
        System.out.println("Network error: " + error );
        return error;
    }

    public double train(Pattern[] patterns) {
        //System.out.println();
        double error = 0.0;
        int countZeros = 0;
        for (int p = 0; p < patterns.length; p++) {
            double current_error = 0.0;
            Complex[] networkOutSums = update(patterns[p]);//receive ouput nodes weighted sums
            for (int j = 0; j < networkOutSums.length; j++) {//for all node at output layer
                double node_error = 0.0;
                if (discreate_outputs) {//deal with descrete outputs
                    double angle = networkOutSums[j].phase();
                    double twopi = 2 * Math.PI;
                    double angleMode2pi = (angle - (Math.floor(angle / twopi) * twopi));
                    double node_out = Math.floor(angleMode2pi / sectorSize);//network output for descrete varibles
                    node_error = Math.abs(node_out - patterns[p].outputs[j]);
                    //System.out.printf("%.1f\t", node_error);
                    if (node_error > numberOfSectorHalf) {
                        node_error = numberOfSectors - node_error;
                    }
                } else {
                    double angle = (activation(networkOutSums[j]).phase());
                    double twopi = 2 * Math.PI;
                    double angleMode2pi = (angle - (Math.floor(angle / twopi) * twopi));
                    double node_out = angleMode2pi;// network output for Continuouis variable
                    node_error = Math.abs(node_out - patterns[p].angularTarget[j]);
                    if (node_error > Math.PI) {
                        node_error = Math.PI - node_error;
                    }
                }
                if(error_type.equalsIgnoreCase("ErrorRate")){
                    if(node_error > localThreshold){
                        node_error = 1;
                    }else{
                        node_error = 0;
                    }
                    if(node_error == 0){
                         countZeros++;
                    }
                }
                //System.out.printf("%.1f\n", node_error);
                current_error = current_error + node_error * node_error;
            }//go to next node	
            error = error + current_error;
        }
        error = error / patterns.length;
        error = Math.sqrt(error);
        if(error_type.equalsIgnoreCase("ErrorRate")){
           error = (1.0 - (countZeros+0.0)/patterns.length);
           //System.out.println("Total correct classification " + countZeros+ " out of "+patterns.length +" : "+error);
        }
        //System.out.println("This error: " + error + " = ");
        return error;
    }

    //(2)---->Activation of neurons	
    public Complex[] update(Pattern pat) {
        // input activations
        for (int i = 0; i < ni; i++) {
            ai[i] = pat.complexInputs[i];
        }
        //hidden activations
        for (int j = 0; j < nh; j++) {
            Complex sum = new Complex(0.0, 0.0);
            for (int i = 1; i < ni + 1; i++) {
                sum = sum.plus(ai[i - 1].times(wi[i][j]));
            }
            wsumh[j] = sum.plus(wi[0][j]);//weighted sum bis at the 0-th index
            ah[j] = activation(wsumh[j]);//---->(3)
        }
        //output activations        
        for (int j = 0; j < no; j++) {
            Complex sum = new Complex(0.0, 0.0);
            for (int i = 1; i < nh + 1; i++) {
                sum = sum.plus(ah[i - 1].times(wo[i][j]));
            }
            wsumo[j] = sum.plus(wo[0][j]);//weighted sum
            ao[j] = activation(wsumo[j]);//---->(3)
        }
        return wsumo;//return weigthed sum as network output
    }//end update weights

    //(3)---->our sigmoid function, tanh is a little nicer than the standard 1/(1+e^-x)
    public Complex activation(Complex sum) {
        double absMultiplicativeFactor = 1.0 / sum.abs();
        return sum.times(absMultiplicativeFactor);
    }//activation

    //(1)---------> Training Main loop
    public double train(Pattern[] patterns, int iterations) {
        System.out.println();
        double error = 0.0;
        for (int i = 0; i < iterations; i++) {
            error = 0.0;
            for (Pattern pattern : patterns) {
                update(pattern); //----->(2)
                error += backPropagate(pattern); //---->(4)
            }
            error = error / patterns.length;
            error = Math.sqrt(error);//RMSE
            if (i % 100 == 0) {
                System.out.printf(" %.3f\n", error);
            }
            if (error < 0.004) {
                break;
            }
        }
        //System.out.printf("\nSTOP at iteration[%d] with Mean Square error: %1.3f \n",i,error);
        return error;
    }//training

    //(4)---->Back-Propagation 
    public double backPropagate(Pattern pat) {
        double current_error = 0.0;

        //calculate error terms for output
        Complex[] output_node_errors = new Complex[no];
        for (int j = 0; j < no; j++) {
            output_node_errors[j] = pat.complexTarget[j].minus(ao[j]);
            output_node_errors[j] = output_node_errors[j].times((1.0 / (nh + 1)));
            //System.out.println((nh+1)+":"+pat.complexTarget[i]+": "+activation(ao[i])+" : "+ output_node_errors[i]);
        }

        //calculate error terms for hidden
        Complex[] temp = new Complex[nh + 1];
        for (int j = 0; j < nh + 1; j++) {
            for (int k = 0; k < no; k++) {
                //System.out.println(wo[j][k]);
                temp[j] = wo[j][k];
                temp[j] = temp[j].reciprocal();
            }
        }
        Complex[] hidden_node_errors = new Complex[nh];
        for (int j = 1; j < nh + 1; j++) {
            for (int k = 0; k < no; k++) {
                hidden_node_errors[j - 1] = (temp[j].times(output_node_errors[k])).times(1.0 / (ni + 1));
                //System.out.println(temp[j]);
            }
        }

        // update input weights 
        //determining learning rate
        double[] lr = new double[nh];
        for (int i = 0; i < nh; i++) {
            lr[i] = 1.0 / (wsumh[i].abs());
            //System.out.println(lr[i]);
        }
        //updating weights
        for (int i = 0; i < ni + 1; i++) {
            for (int j = 0; j < nh; j++) {
                Complex change = new Complex(0.0, 0.0);
                if (i == 0) {
                    change = hidden_node_errors[j].times(lr[j]);
                } else {
                    change = ai[i - 1].conjugate().times(lr[j]).times(hidden_node_errors[j]);
                }
                wi[i][j] = wi[i][j].plus(change);
                //System.out.print("\t" + wi[i][j]);
                //System.out.print("\t" + wi[i][j]);
            }
            //System.out.println();
        }

        //update output weights
        //determining hidden inputs activation
        for (int j = 0; j < nh; j++) {
            Complex sum = new Complex(0.0, 0.0);
            for (int i = 1; i < ni + 1; i++) {
                sum = sum.plus(ai[i - 1].times(wi[i][j]));
            }
            wsumh[j] = sum.plus(wi[0][j]);//weighted sum bis at the 0-th index
            ah[j] = activation(wsumh[j]);//---->(3)
        }

        //updating weights
        for (int i = 0; i < nh + 1; i++) {
            for (int j = 0; j < no; j++) {
                Complex change = new Complex(0.0, 0.0);
                if (i == 0) {
                    change = output_node_errors[j];
                } else {
                    change = ah[i - 1].conjugate().times(output_node_errors[j]);
                    //System.out.print("\t" + ah[i - 1].conjugate());
                }
                wo[i][j] = wo[i][j].plus(change);
                //System.out.print("\t" + wo[i][j]);
            }
            //System.out.println();
        }

        //output activations        
        for (int j = 0; j < no; j++) {
            Complex sum = new Complex(0.0, 0.0);
            for (int i = 1; i < nh + 1; i++) {
                sum = sum.plus(ah[i - 1].times(wo[i][j]));
            }
            wsumo[j] = sum.plus(wo[0][j]);//weighted sum
            ao[j] = activation(wsumo[j]);//---->(3)
        }

        for (int j = 0; j < no; j++) {
            double node_error = 0.0;
            if (discreate_outputs) {//deal with descrete outputs
                double angle = wsumo[j].phase();
                double twopi = 2 * Math.PI;
                double angleMode2pi = (angle - (Math.floor(angle / twopi) * twopi));
                double node_out = Math.floor(angleMode2pi / sectorSize);//network output for descrete varibles
                node_error = Math.abs(node_out - pat.outputs[j]);
                //System.out.printf("%.1f\t", node_error);
                if (node_error > numberOfSectorHalf) {
                    node_error = numberOfSectors - node_error;
                }
            } else {
                double angle = (activation(wsumo[j]).phase());
                double twopi = 2 * Math.PI;
                double angleMode2pi = (angle - (Math.floor(angle / twopi) * twopi));
                double node_out = angleMode2pi;// network output for Continuouis variable
                node_error = Math.abs(node_out - pat.angularTarget[j]);
                if (node_error > Math.PI) {
                    node_error = Math.PI - node_error;
                }
            }
            current_error = current_error + node_error * node_error;
        }

        return current_error;
    }//end backproagation

    public void UpdateNetworkWeights(double mhweights[]) {
        int Length = mhweights.length;
        Complex[] weights = new Complex[Length/2];
        int k = 0;//k is index for dimension
        for(int i =0; i < mhweights.length; ){
            double re = mhweights[i++];
            double im = mhweights[i++];
            weights[k++] = new Complex(re,im); 
        }
        
        //reset index k
        k = 0;
        for (int i = 0; i < ni + 1; i++) {
            //System.out.print("["); 
            for (int j = 0; j < nh; j++) {
                if (k <= Length) {
                    wi[i][j] = weights[k];
                    k++;
                    //System.out.print("\t" + wi[i][j]);
                }
            }
            //System.out.println("]");
        }

        // System.out.println("\nSynaptic Weight Matrix Hidden-Output layer\n");
        for (int i = 0; i < nh + 1; i++) {
            //System.out.print("["); 
            for (int j = 0; j < no; j++) {
                if (k <= Length) {
                    wo[i][j] = weights[k];
                    k++;
                    //System.out.printf("%.3f",wo[i][j]);
                }
            }
            //System.out.println("]");
        }
    }//the weight vector matrix mapping is done  
}
