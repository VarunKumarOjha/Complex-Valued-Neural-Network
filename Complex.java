package ComplexNumber;

/******************************************************************************
 *  Compilation:  javac Complex.java
 *  Execution:    java Complex
 *
 *  Data type for complex numbers.
 *
 *  The data type is "immutable" so once you create and initialize
 *  a Complex object, you cannot change it. The "final" keyword
 *  when declaring re and im enforces this rule, making it a
 *  compile-time error to change the .re or .im fields after
 *  they've been initialized.
 *
 *  % java Complex
 *  a            = 5.0 + 6.0i
 *  b            = -3.0 + 4.0i
 *  Re(a)        = 5.0
 *  Im(a)        = 6.0
 *  b + a        = 2.0 + 10.0i
 *  a - b        = 8.0 + 2.0i
 *  a * b        = -39.0 + 2.0i
 *  b * a        = -39.0 + 2.0i
 *  a / b        = 0.36 - 1.52i
 *  (a / b) * b  = 5.0 + 6.0i
 *  conj(a)      = 5.0 - 6.0i
 *  |a|          = 7.810249675906654
 *  tan(a)       = -6.685231390246571E-6 + 1.0000103108981198i
 *
 ******************************************************************************/
public class Complex {


    private final double re;   // the real part
    private final double im;   // the imaginary part

    // create a new object with the given real and imaginary parts
    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    // return a string representation of the invoking Complex object
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    
    /** 
     * @return  return abs/modulus/magnitude */
    public double abs()   { return Math.hypot(re, im); }  // Math.sqrt(re*re + im*im)
    /** 
     * @return  return angle/phase/argument */
    public double phase() { return Math.atan2(im, re); }  // between -pi and pi

    /** 
     * @param b is a complex number
     * @return  return a new Complex object whose value is (this + b) */
    public Complex plus(Complex b) {
        Complex a = this;             // invoking object
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return new Complex(real, imag);
    }

    /** 
     * @param b is a complex number
     * @return  return a new Complex object whose value is (this - b) */
    public Complex minus(Complex b) {
        Complex a = this;
        double real = a.re - b.re;
        double imag = a.im - b.im;
        return new Complex(real, imag);
    }

    /** returns a complex number 
     * @param b is a complex number
     * @return a new Complex object whose value is (this * b)  */
    public Complex times(Complex b) {
        Complex a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    // scalar multiplication
    // return a new object whose value is (this * alpha)
    public Complex times(double alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    // return a new Complex object whose value is the conjugate of this
    public Complex conjugate() {  return new Complex(re, -im); }

    // return a new Complex object whose value is the reciprocal of this
    public Complex reciprocal() {
        double scale = re*re + im*im;
        return new Complex(re / scale, -im / scale);
    }

    // return the real or imaginary part
    public double re() { return re; }
    public double im() { return im; }

    /** 
     * @param b is a complex number
     * @return return a / b */
    public Complex divides(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    // return a new Complex object whose value is the complex exponential of this
    public Complex exp() {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    // return a new Complex object whose value is the complex sine of this
    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex cosine of this
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex tangent of this
    public Complex tan() {
        return sin().divides(cos());
    }
    

    /** 
     * a static version of plus
     * @param a a complex number
     * @param b a complex number
     * @return a + b
     */
    public static Complex plus(Complex a, Complex b) {
        double real = a.re + b.re;
        double imag = a.im + b.im;
        Complex sum = new Complex(real, imag);
        return sum;
    }

    /**
     * a static version of real to complex conversion with with k*pi
     * @param d is a real number 
     * @return converts d into complex number without PI
     */
    public static Complex real2complex(double d) { 
        double[] xc = new double[2];//index 0 has real part and index 1 has imaginary part
        xc[0] = Math.cos((d-0));
        xc[1] = Math.sin((d-0));
        return new Complex(xc[0], xc[1]); //Equvalent formulan in MATLAB is: exp(k*pi*d*1i)
    }
    
    /**
     * a static version of real to complex conversion with with k*pi
     * @param d is a real number
     * @param k is a multiplicative factor of PI 
     * @return converts d into complex number
     */
    public static Complex real2complex(double d,double k) { 
        double[] xc = new double[2];//index 0 has real part and index 1 has imaginary part
        xc[0] = Math.cos(k*Math.PI*(d-0));
        xc[1] = Math.sin(k*Math.PI*(d-0));
        return new Complex(xc[0], xc[1]); //Equvalent formulan in MATLAB is: exp(k*pi*d*1i)
    }
    
    /**
     * a static version of a random complex number generator
     * @param N size of the random vector
     * @param k is a multiplicative factor of PI 
     * @return a vector of random complex numbers
     */
    public static Complex[] randomComplex(int N, double k) {
        Complex[] a = new Complex[N];
        for(int i = 0; i < N; i ++){
            double d = Math.random()*k*Math.PI;
            double[] xc = new double[2];//index 0 has real part and index 1 has imaginary part
            xc[0] = Math.cos(d);
            xc[1] = Math.sin(d);
            a[i] = new Complex(xc[0], xc[1]);
        }
        return a;
    }
    
 
    /**
     * a static version of a random complex number generator with k*pi
     * @param k is a multiplicative factor of PI 
     * @return a random complex number
     */
    public static Complex randomComplex(double k) {
        Complex a;
            double d = Math.random()*k*Math.PI;
            double[] xc = new double[2];//index 0 has real part and index 1 has imaginary part
            xc[0] = Math.cos(d);
            xc[1] = Math.sin(d);
            a = new Complex(xc[0], xc[1]);
        return a;
    }



    // sample client for testing
    public static void main(String[] args) {
        //Complex[] xc = randomComplex(3);//generate a random complex number
        //Complex a = xc[2];
        //a = real2complex(0.1765);
        //Complex a = new Complex(xc[0], xc[1]);
        //Complex a = new Complex(5.0, 6.0);
        Complex a = new Complex(0.273614333410591, -1.26931162005251);
        Complex b = new Complex(-3.0, 4.0);

        System.out.println("a            = " + a);
        System.out.println("b            = " + b);
        System.out.println("Re(a)        = " + a.re());
        System.out.println("Im(a)        = " + a.im());
        System.out.println("b + a        = " + b.plus(a));
        System.out.println("a - b        = " + a.minus(b));
        System.out.println("a * b        = " + a.times(b));
        System.out.println("b * a        = " + b.times(a));
        System.out.println("a / b        = " + a.divides(b));
        System.out.println("(a / b) * b  = " + a.divides(b).times(b));
        System.out.println("conj(a)      = " + a.conjugate());
        System.out.println("|a|          = " + a.abs());
        System.out.println("tan(a)       = " + a.tan());
        System.out.println("abc(a)       = " + a.abs());
        System.out.println("pahse(a)     = " + a.phase());
        
        Complex a1 = real2complex(1.0,2);
        Complex a2 = new Complex( -0.1768, -0.4098);//Complex(-0.4054, 0.0216);
         
        System.out.println("recprocal(d)     = " + a1.divides(a2));
        System.out.println("recprocal(d)     = " + a2.reciprocal());
        System.out.println(" ");
          
        Complex sum = new Complex(0.0,0.0);
        sum = sum.plus(b);
        System.out.println("Sum is ?            "+ sum);//yes it is equivlant
        
        Complex d   = real2complex(0.7,1);
        Complex d1  = real2complex(0.7,2);
        System.out.println();
        System.out.println("0.7 to Complex  pi         = " + d);
        System.out.println("0.7 to Complex  2pi        = " + d1);
        
        System.out.println();
        Complex c  = new Complex(0,0.07);
        System.out.println("c                          = " + c);
        c = c.exp();
        System.out.println("exp(1i * 2*pi*0.0971)      = " + c);
        double red = 0.5*c.re();
        double imd = 0.5*c.im();
        System.out.println("0.5*exp(1i * 2*pi*0.1765)  = " +new  Complex(red,imd));
        
        System.out.println();
        Complex test = real2complex(0.75);
        System.out.println("Matlab equivalant test without PI?            "+ test);//yes it is equivlant
        //test = test.times(0.5);
        System.out.println("Matlab equivalant test times 0.5 ? "+ test);//yes it is equivlant
        
        System.out.println();
        Complex test1 = real2complex(0.5,2);
        System.out.println("Matlab equivalant test with  2PI?            "+ test1);//yes it is equivlant
        //test = test1.times(0.5);
        System.out.println("Matlab equivalant test times 0.5 ? "+ test1);//yes it is equivlant
        
        
         //mod(angle(c), pi2);
        double test2  = (test.phase())%(2*Math.PI);
        System.out.println("Matlab equivalant test mod(angle(c), pi2); ? "+ test2);//yes it is equivlant
        
        Complex absA = new Complex(0.5,0.6);
        double absAval = 1.0/absA.abs();
        System.out.println("Matlab equivalant test a / abs(a) ; ? "+ absA);//yes it is equivlant
        Complex absB = absA.times((absAval));
        System.out.println("Matlab equivalant test a / abs(a) ; ? "+ absB);//yes it is equivlant
        absB = absB.times(1.0/absAval);
        System.out.println("Matlab equivalant test a / abs(a) ; ? "+ absB);//yes it is equivlant
    }

}
