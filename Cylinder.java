import java.util.Random; //imports the random utility

class Cylinder {
  Random randy = new Random(); //creates a new random number generator
  private double[] origin = new double [3]; //this creates the origin point of the cylinder in terms of an array consisting of [x,y,z]
  private Material material; //initializes a new Material object called material
  private double radIn; //intializes the inner cylinder radius value, set to zero for solid cylinders
  private double radOut;//intializes the outer radius of the cylinder
  private double height; //initializes height of the cylinder

  public Cylinder(double radiusInside, double radiusOutside, double h, Material mat, double[] org ) { //constructor for the cylinder class
    radIn = radiusInside; //contructor takes in the above variables, which are then assigned to the above intialized ones
    radOut = radiusOutside;
    height = h;
    material = mat;
    origin = org;
    }

    public boolean isInCylinderVolume(Proton p){ //this is the method which checks whether or not the beam is in the cylinder itself - this in essence is what creates the cylinder
      double[] position = p.getLastPosition(); //creates an array variable then is filled with the last (t,x,y,z) coordinates of the proton
      if (position[3] >= origin[2] && position[3] <= (origin[2] + height)){ //IF statement which asks "is the z co/ord of the proton greater or equal to the z of the origin AND is the z co/ord less than the z of the origin", this then executes the IF on line 25.
        if( (Math.sqrt(position[1]*position[1] + position[2]*position[2]) <= radOut) &&//second IF has the conditions of "is sqrt(x^2 + y^2) of the proton position is less or equal to the outer radius value"
        (Math.sqrt(position[1]*position[1] + position[2]*position[2]) >= radIn )){ //AND "is sqrt(x^2 + y^2) greater than or equal to the inner radius"
          return true; //if aforementioned conditions are BOTH met then it's in the cylinder
        }
      }
      return false; // if only ONE is met then return false
    }
    public double getHeight() {return height;} // returns the height value of a cylinder

    public void doELoss(Proton p){ // this is the energy loss method for a passed in variable of object type Proton
      EnergyLoss eLossObject = new EnergyLoss(this.material); // creates an energy loss object which takes in the material parameter earlier initialized
      p.reduceEnergy(eLossObject.getEnergyLoss(p) * p.getLastDistance()); //this calls on the reduce energy method (from Proton) to act on the passed in proton.
    } //this then acts in conjunction with the getEnergyLoss method from the EnergyLoss class and the getLastDistance method from Proton

    public void doMCS(Proton p){
      MCS cylinderScat = new MCS(this.material);
      double theta0 = cylinderScat.getTheta0(p);
      p.applySmallRotation((theta0 * randy.nextGaussian()), (theta0 * randy.nextGaussian()));
    }
  }

  //Stamp of Approval for FinalModel
