/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvnn;

import ComplexNumber.Complex;

/**
 *
 * @author Varun
 */
public class Pattern {

    public Complex[] complexInputs;
    public Complex[] complexTarget;
    public double[] angularTarget;
    public double[] outputs;

    public Pattern(Complex[] complexin, Complex[] complexout, double[] angularout, double[] out) {
        complexInputs = complexin;
        complexTarget = complexout;
        angularTarget = angularout;
        outputs = out;
    }//Contractor Pattern
}//Class Pattern 
