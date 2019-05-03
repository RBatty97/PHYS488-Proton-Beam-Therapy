public class BeamGeometry{
  private Cylinder[] cylinders; //initializes an array variable of the object type cylinder, this will contain the amount of cylinders created
  private int volumes; //intializes an integer called volumes, this defines the amount of cylinders created

  public BeamGeometry(int totCylinders){ //constructor for the BeamGeometry class that takes an integer value which is then called totCylinders
    this.cylinders = new Cylinder[totCylinders]; //this assigns the totCylinders int variable to a cylinder object
    this.volumes = 0; //set the volumes to zero, so when BeamGeometry is created, volumes are at zero
  }

  public void addCylinder(double radiusInside, double radiusOutside, double h, Material material, double[] org ){ //addCylinder method which creates the actual cylinders from taken parameters
    cylinders[volumes] = new Cylinder(radiusInside, radiusOutside, h, material, org); //assigns each cylinder from the 0th array position to a new cylinder
    this.volumes++; //increments the volumes for each cylinder created so each would fall into the 0th, 1st ... nth array position
  }

  public double getHeight() {return cylinders[0].getHeight();} //getter for retuning the height value for the first cylinder

  public boolean doELoss(Proton p){ //this is the energy loss from the Bethe-Bloch formula based on a passed in proton object
    for ( int i = volumes - 1; i >= 0 ; i--){ //this loop cycles through the volumes backwards
      if (this.cylinders[i].isInCylinderVolume(p)){ //this if loops asks if the proton is in the volume, and if (and only if) it is then it executes the energy loss
        this.cylinders[i].doELoss(p);
          return true;
      }
    }
    return false;
  }

  public void doMCS(Proton p){ //this works the same as the energyloss above
    for (int i = this.volumes - 1; i >= 0; i--){
      if (this.cylinders[i].isInCylinderVolume(p)){
        this.cylinders[i].doMCS(p);
      }
    }
  }
}

//Stamp of Approval for FinalModel
