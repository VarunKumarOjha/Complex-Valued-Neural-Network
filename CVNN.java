/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvnn;

import ComplexNumber.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Varun
 */
public class CVNN {

    public static void main(String args[]) {
        try {
            //Data reading
            FileReader fin = new FileReader("Diabetes1.txt");//readt output Train to make ensemble
            BufferedReader br = new BufferedReader(fin);
            String line;
            int length = 0;
            while ((line = br.readLine()) != null) {
                length++;
            }
            br.close();
            fin.close();
            double[][] dataX = new double[length][8];
            double[][] dataY = new double[length][2];

            FileReader fin1 = new FileReader("Diabetes1.txt");//readt output Train to make ensemble
            BufferedReader br1 = new BufferedReader(fin1);
            for (int i = 0; i < length; i++) {
                line = br1.readLine();
                String[] tokens = line.split(" ");
                //System.out.printf("%d  :  ",i);
                for (int j = 0; j < tokens.length; j++) {
                    double valX = Double.parseDouble(tokens[j]);
                    if (j < 8) {
                        dataX[i][j] = valX;
                        //System.out.printf(" %1.3f ", valX);
                    } else {
                        dataY[i][j - 8] = valX;
                        //System.out.printf(" %1.1f ", valX);
                    }
                }
                //System.out.println();
            }
            //Converting data into complex
            Complex[][] dataXC = new Complex[length][8];
            for (int i = 0; i < length; i++) {
                //System.out.printf("%d  :  ",i);
                for (int j = 0; j < 8; j++) {
                    dataXC[i][j] = Complex.real2complex(dataX[i][j],1);
                    //System.out.print("\t  "+dataXC[i][j]);
                }
                //System.out.println();
            }

            //Network weights
             double[] arnd = {0.2760,0.6797,0.6551,0.1626,0.1190,0.4984,0.9597,0.3404};
             double[] brnd = {0.5853,0.2238,0.7513,0.2551,0.5060,0.6991,0.8909,0.9593};
             
            Complex[] wih1 = new Complex[8];
            Complex[] wih2 = new Complex[8];

            wih1 = Complex.randomComplex(8,2);
            wih2 = Complex.randomComplex(8,2);
            for(int i=0;i < 8; i++){
                //wih1[i] = wih1[i].times(0.5);
                //wih1[i] = Complex.randomComplex(arnd[i]).times(0.5);
                wih1[i] = Complex.randomComplex(1).times(0.5);
                //System.out.println(wih1[i]);
            }
            //System.out.println();
            for(int i=0;i < 8; i++){
               //wih2[i] = Complex.randomComplex(brnd[i]).times(0.5);
               wih2[i] = Complex.randomComplex(1).times(0.5);
               //System.out.println(wih2[i]);
            }
            //System.out.println();
            
            //Network bias
            double  crand =  0.5472;
            double  drand =  0.1386;
            //Complex Pih1 = Complex.randomComplex(crand).times(0.5);
            //Complex Pih2 = Complex.randomComplex(drand).times(0.5);
            Complex Pih1 = Complex.randomComplex(1).times(0.5);
            Complex Pih2 = Complex.randomComplex(1).times(0.5);
            //System.out.println(Pih1);
            //System.out.println(Pih2);
            //System.out.println();

            //Training variables
            int epochs = 1000;
            double Err = 0.0;
            double blr = 0.1;
            //Main Training loop
            for (int i = 0; i < epochs; i++) {//for all traiining epocs
                
                double E_Total = 0.0;//total error for current iteration
                double E1t = 0.0;//error for out node 1 at current iteration
                double E2t = 0.0;//error for out node 2 at current iteration
                
                for (int j = 0; j < 384; j++) {//for all patterns
                    Complex z1 = new Complex(0, 0);
                    Complex z2 = new Complex(0, 0);
                    //for all attributes in the j-th pattern
                    //System.out.print("pattern i = "+i+":");
                    for (int k = 0; k < 8; k++) {
                        //System.out.print(" "+dataXC[j][k]);
                        Complex a = dataXC[j][k].times(wih1[k]);
                        Complex b = dataXC[j][k].times(wih2[k]);
                        if (k == 0) {
                            z1 = a;
                            z2 = b;
                        } else {
                            z1 = z1.plus(a);
                            z2 = z2.plus(b);
                        }
                    }
                    z1 = z1.plus(Pih1);
                    z2 = z2.plus(Pih2);
                    
                    //System.out.println("Outputs for the pattern i = "+i+" are:");
                    //System.out.println(z1);
                    //System.out.println(z2);
                    
                    //System.exit(1);

                    double u1 = z1.re();
                    double v1 = z1.im();
                    double u2 = z2.re();
                    double v2 = z2.im();

                    
                    double fu1 = 1 / (1 + Math.exp(-u1));
                    double fv1 = 1 / (1 + Math.exp(-v1));
                    double fu2 = 1 / (1 + Math.exp(-u2));
                    double fv2 = 1 / (1 + Math.exp(-v2));
                    

                    double t11 = 1 / (1 + Math.exp(-u1));
                    double f11 = t11 * (1 - t11);
                    double t21 = 1 / (1 + Math.exp(-v1));
                    double f21 = t21 * (1 - t21);
                   

                    double t12 = 1 / (1 + Math.exp(-u2));
                    double f12 = t12 * (1 - t12);
                    double t22 = 1 / (1 + Math.exp(-v2));
                    double f22 = t22 * (1 - t22);
                    
                    
                    double y1 = Math.sqrt(fu1 * fu1 + fv1 * fv1);
                    double y2 = Math.sqrt(fu2 * fu2 + fv2 * fv2);
                    
                    double error1 = dataY[j][0] - y1;
                    double E1 = error1 * error1;
                    double error2 = dataY[j][1] - y2;
                    double E2 = error2 * error2;

                    double r1re = blr * (error1) * ((t11 * f11) / y1);
                    double r1im = blr * (error1) * ((t21 * f21) / y1);
                    double r2re = blr * (error2) * ((t12 * f12) / y2);
                    double r2im = blr * (error2) * ((t22 * f22) / y2);
                    Complex r1 = new Complex(r1re, r1im);
                    Complex r2 = new Complex(r2re, r2im);
                   

                    //System.out.println("Updated phase");    
                    Pih1 = Pih1.plus(r1);
                    Pih2 = Pih2.plus(r2);
                    
                    //System.out.println(Pih1);    
                    //System.out.println(Pih2);       
                    
                    //System.out.println("Outputs for the pattern i = "+i+" are:");
                    for (int k = 0; k < 8; k++) {
                        wih1[k] = wih1[k].plus(dataXC[j][k].times(r1));
                        //System.out.println(wih1[k]);                      
                    }
                    //System.out.println(" ");          
                    for (int k = 0; k < 8; k++) {
                        wih2[k] = wih2[k].plus(dataXC[j][k].times(r2));
                        //System.out.println(wih2[k]);                        
                    }
                    //System.exit(1);
                    
                    //error at current    
                    E_Total = E_Total + E1 + E2;
                }//for training patters
                Err = E_Total / (2 * 384);
                if (i % 100 == 0) {
                    System.out.println("" + Err);
                }
            }//for epochs
            System.out.println("" + Err);
        } catch (IOException | NumberFormatException e) {
            System.out.println(e);
        }
    }

}
